package io.github.murphy955.fina.competition.service;

import io.github.murphy955.fina.competition.strategy.SeedingStrategy;
import io.github.murphy955.fina.competition.strategy.impl.HeatsSeedingStrategy;
import io.github.murphy955.fina.domain.enm.EventTypeEnum;
import io.github.murphy955.fina.domain.enm.GenderEnum;
import io.github.murphy955.fina.domain.enm.StrokeEnum;
import io.github.murphy955.fina.domain.entity.athlete.Athlete;
import io.github.murphy955.fina.domain.entity.achievements.RaceTime;
import io.github.murphy955.fina.domain.shared.BaseGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 编排功能整体集成测试。
 * <p>验证从 {@link RaceKeyRegister#buildKey} 生成 key，到将运动员分组存入 Map，
 * 最后交由 {@link SeedingStrategy} 完成预赛编排的完整流程。</p>
 *
 * @author 李泽聿
 * @since 2026/05/08
 */
class CompetitionSeedingIntegrationTest {

	/**
	 * 测试用年龄分组枚举
	 */
	enum TestAgeGroup implements BaseGroup {
		U18("U18", "18岁以下"),
		U20("U20", "20岁以下"),
		SENIOR("SENIOR", "成年组");

		private final String code;
		private final String description;

		TestAgeGroup(String code, String description) {
			this.code = code;
			this.description = description;
		}

		@Override
		public String getName() {
			return code;
		}

		public String getDescription() {
			return description;
		}
	}

	private RaceKeyRegister<TestAgeGroup> raceKeyRegister;
	private SeedingStrategy seedingStrategy;

	@BeforeEach
	void setUp() {
		raceKeyRegister = new RaceKeyRegister<>();
		seedingStrategy = new HeatsSeedingStrategy();
	}

	@Test
	@DisplayName("整体流程：多项目运动员报名→生成Key→分组编排")
	void fullSeedingWorkflow() {
		// ========== 1. 定义比赛项目 ==========
		String men100FreeKey = raceKeyRegister.buildKey(
				GenderEnum.MALE,
				TestAgeGroup.U18,
				"100",
				EventTypeEnum.INDIVIDUAL,
				StrokeEnum.FREESTYLE
		);
		String women100BreastKey = raceKeyRegister.buildKey(
				GenderEnum.FEMALE,
				TestAgeGroup.U18,
				"100",
				EventTypeEnum.INDIVIDUAL,
				StrokeEnum.BREASTSTROKE
		);
		String men200FreeKey = raceKeyRegister.buildKey(
				GenderEnum.MALE,
				TestAgeGroup.U18,
				"200",
				EventTypeEnum.INDIVIDUAL,
				StrokeEnum.FREESTYLE
		);

		// ========== 2. 准备运动员数据 ==========
		Map<String, List<Athlete>> entries = new HashMap<>();

		// 男子100米自由泳：16人（应分2组）
		List<Athlete> men100FreeAthletes = createAthletes(men100FreeKey, 16, 50.0);
		entries.put(men100FreeKey, men100FreeAthletes);

		// 女子100米蛙泳：5人（应分1组，直接决赛）
		List<Athlete> women100BreastAthletes = createAthletes(women100BreastKey, 5, 70.0);
		entries.put(women100BreastKey, women100BreastAthletes);

		// 男子200米自由泳：20人（应分3组）
		List<Athlete> men200FreeAthletes = createAthletes(men200FreeKey, 20, 120.0);
		entries.put(men200FreeKey, men200FreeAthletes);

		// ========== 3. 执行预赛编排 ==========
		seedingStrategy.generateSeeding(entries, 8);

		// ========== 4. 验证男子100米自由泳（16人，2组，快慢交替）==========
		verifyTwoHeats(men100FreeAthletes, men100FreeKey);

		// ========== 5. 验证女子100米蛙泳（5人，1组，直接决赛）==========
		verifySingleHeat(women100BreastAthletes, women100BreastKey);

		// ========== 6. 验证男子200米自由泳（20人，3组，循环分配）==========
		verifyThreeHeats(men200FreeAthletes, men200FreeKey);
	}

	@Test
	@DisplayName("整体流程：长距离项目使用最后2组规则编排")
	void longDistanceSeedingWorkflow() {
		String men400FreeKey = raceKeyRegister.buildKey(
				GenderEnum.MALE,
				TestAgeGroup.SENIOR,
				"400",
				EventTypeEnum.INDIVIDUAL,
				StrokeEnum.FREESTYLE
		);

		Map<String, List<Athlete>> entries = new HashMap<>();
		// 400米自由泳：20人，长距离，应分3组，最后2组按2组规则
		List<Athlete> athletes = createAthletes(men400FreeKey, 20, 240.0);
		entries.put(men400FreeKey, athletes);

		SeedingStrategy longDistanceStrategy = new HeatsSeedingStrategy(true);
		longDistanceStrategy.generateSeeding(entries, 8);

		// 验证最后2组（group 2 和 3）按2组规则：
		// group 3 应包含 athletes 0,2,4,6,8,10,12,14
		// group 2 应包含 athletes 1,3,5,7,9,11,13,15
		// group 1 应包含 athletes 16,17,18,19
		for (int i = 0; i < 20; i++) {
			Athlete athlete = findByName(athletes, men400FreeKey + "-Athlete" + i);
			int expectedGroup;
			if (i >= 16) {
				expectedGroup = 1;
			} else {
				expectedGroup = (i % 2 == 0) ? 3 : 2;
			}
			assertEquals(expectedGroup, athlete.getGroup(),
					men400FreeKey + " Athlete" + i + " should be in group " + expectedGroup);
		}
	}

	// ==================== 验证辅助方法 ====================

	private void verifyTwoHeats(List<Athlete> athletes, String keyPrefix) {
		// 2组规则：最快→第2组，次快→第1组，交替
		for (int i = 0; i < 16; i++) {
			Athlete athlete = findByName(athletes, keyPrefix + "-Athlete" + i);
			int expectedGroup = (i % 2 == 0) ? 2 : 1;
			assertEquals(expectedGroup, athlete.getGroup(),
					keyPrefix + " Athlete" + i + " should be in group " + expectedGroup);
		}

		// 验证泳道：每组内最快者应在第4道
		Athlete fastestInGroup2 = findByName(athletes, keyPrefix + "-Athlete0");
		assertEquals(2, fastestInGroup2.getGroup());
		assertEquals(4, fastestInGroup2.getSwimLane());

		Athlete fastestInGroup1 = findByName(athletes, keyPrefix + "-Athlete1");
		assertEquals(1, fastestInGroup1.getGroup());
		assertEquals(4, fastestInGroup1.getSwimLane());
	}

	private void verifySingleHeat(List<Athlete> athletes, String keyPrefix) {
		// 5人1组，全部在 group 1
		for (int i = 0; i < 5; i++) {
			Athlete athlete = findByName(athletes, keyPrefix + "-Athlete" + i);
			assertEquals(1, athlete.getGroup(),
					keyPrefix + " Athlete" + i + " should be in group 1 (direct final)");
		}

		// 泳道：1→4, 2→5, 3→3, 4→6, 5→2
		assertEquals(4, findByName(athletes, keyPrefix + "-Athlete0").getSwimLane());
		assertEquals(5, findByName(athletes, keyPrefix + "-Athlete1").getSwimLane());
		assertEquals(3, findByName(athletes, keyPrefix + "-Athlete2").getSwimLane());
		assertEquals(6, findByName(athletes, keyPrefix + "-Athlete3").getSwimLane());
		assertEquals(2, findByName(athletes, keyPrefix + "-Athlete4").getSwimLane());
	}

	private void verifyThreeHeats(List<Athlete> athletes, String keyPrefix) {
		// 3组规则：最快→第3组，次快→第2组，第三快→第1组，循环
		for (int i = 0; i < 20; i++) {
			Athlete athlete = findByName(athletes, keyPrefix + "-Athlete" + i);
			int expectedGroup = 3 - (i % 3);
			assertEquals(expectedGroup, athlete.getGroup(),
					keyPrefix + " Athlete" + i + " should be in group " + expectedGroup);
		}

		// 验证每组最快者都在第4道
		assertEquals(4, findByName(athletes, keyPrefix + "-Athlete0").getSwimLane()); // group 3 最快
		assertEquals(4, findByName(athletes, keyPrefix + "-Athlete1").getSwimLane()); // group 2 最快
		assertEquals(4, findByName(athletes, keyPrefix + "-Athlete2").getSwimLane()); // group 1 最快
	}

	// ==================== 工厂方法 ====================

	/**
	 * 创建 N 名运动员，成绩递增。
	 *
	 * @param keyPrefix  key前缀，用于生成运动员名称
	 * @param count      人数
	 * @param baseTime   基础成绩（秒）
	 */
	private List<Athlete> createAthletes(String keyPrefix, int count, double baseTime) {
		List<Athlete> list = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			String name = keyPrefix + "-Athlete" + i;
			// 成绩从 baseTime 开始，每人慢 1 秒
			String timeStr = String.format("%.2f", baseTime + i);
			list.add(new Athlete(name, RaceTime.parse(timeStr)));
		}
		return list;
	}

	private Athlete findByName(List<Athlete> athletes, String name) {
		return athletes.stream()
				.filter(a -> a.getName().equals(name))
				.findFirst()
				.orElseThrow(() -> new AssertionError("Athlete not found: " + name));
	}
}
