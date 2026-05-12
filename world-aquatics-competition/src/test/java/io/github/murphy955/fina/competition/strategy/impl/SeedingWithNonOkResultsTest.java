package io.github.murphy955.fina.competition.strategy.impl;

import io.github.murphy955.fina.competition.strategy.SeedingStrategy;
import io.github.murphy955.fina.domain.enm.RaceResultCode;
import io.github.murphy955.fina.domain.entity.achievements.RaceTime;
import io.github.murphy955.fina.domain.entity.athlete.Athlete;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 编排整体功能测试 —— 运动员成绩不为 OK 的场景
 * <p>
 * 验证预赛、半决赛、决赛编排时，结果码非 {@code OK} 的运动员排序与分组行为：
 * <ul>
 *     <li>只有 {@code OK} 参与正常成绩排名与分组</li>
 *     <li>非 {@code OK} 排至所有 {@code OK} 运动员之后</li>
 *     <li>非 {@code OK} 之间按 DQ → DNS → DNF → SCR → DSQ 顺序排列</li>
 * </ul>
 *
 * @author 李泽聿
 * @since 2026/05/10
 */
@DisplayName("编排功能测试 —— 非 OK 成绩场景")
class SeedingWithNonOkResultsTest {

	private Map<String, List<Athlete>> entries;

	@BeforeEach
	void setUp() {
		entries = new HashMap<>();
	}

	// ==================== 预赛编排 ====================

	@Test
	@DisplayName("预赛: 混合 OK 与非 OK，OK 在前并正常分组，非 OK 排在最后")
	void heatsMixedOkAndNonOk() {
		List<Athlete> athletes = Arrays.asList(
				new Athlete("A_DQ", RaceTime.parse("55.00"), RaceResultCode.DQ),
				new Athlete("B_OK", RaceTime.parse("52.00"), RaceResultCode.OK),
				new Athlete("C_DNS", RaceTime.parse("0.00"), RaceResultCode.DNS),
				new Athlete("D_OK", RaceTime.parse("51.00"), RaceResultCode.OK),
				new Athlete("E_DNF", RaceTime.parse("0.00"), RaceResultCode.DNF),
				new Athlete("F_OK", RaceTime.parse("53.00"), RaceResultCode.OK),
				new Athlete("G_SCR", RaceTime.parse("0.00"), RaceResultCode.SCR),
				new Athlete("H_OK", RaceTime.parse("54.00"), RaceResultCode.OK),
				new Athlete("I_DSQ", RaceTime.parse("0.00"), RaceResultCode.DSQ),
				new Athlete("J_OK", RaceTime.parse("50.00"), RaceResultCode.OK)
		);
		entries.put("heat", new ArrayList<>(athletes));

		SeedingStrategy strategy = new HeatsSeedingStrategy();
		strategy.generateSeeding(entries, 8);

		List<Athlete> sorted = entries.get("heat");

		// 前 5 名必须是 OK，按成绩排序
		assertEquals("J_OK", sorted.get(0).getName()); // 50.00
		assertEquals("D_OK", sorted.get(1).getName()); // 51.00
		assertEquals("B_OK", sorted.get(2).getName()); // 52.00
		assertEquals("F_OK", sorted.get(3).getName()); // 53.00
		assertEquals("H_OK", sorted.get(4).getName()); // 54.00

		// 后 5 名是非 OK，按 DQ → DNS → DNF → SCR → DSQ
		assertEquals(RaceResultCode.DQ, sorted.get(5).getResultCode());
		assertEquals(RaceResultCode.DNS, sorted.get(6).getResultCode());
		assertEquals(RaceResultCode.DNF, sorted.get(7).getResultCode());
		assertEquals(RaceResultCode.SCR, sorted.get(8).getResultCode());
		assertEquals(RaceResultCode.DSQ, sorted.get(9).getResultCode());
	}

	@Test
	@DisplayName("预赛: 全部非 OK 时，仍按规则排序并分到同一组")
	void heatsAllNonOk() {
		List<Athlete> athletes = Arrays.asList(
				new Athlete("A_DSQ", RaceTime.parse("0.00"), RaceResultCode.DSQ),
				new Athlete("B_DNF", RaceTime.parse("0.00"), RaceResultCode.DNF),
				new Athlete("C_DNS", RaceTime.parse("0.00"), RaceResultCode.DNS),
				new Athlete("D_SCR", RaceTime.parse("0.00"), RaceResultCode.SCR),
				new Athlete("E_DQ", RaceTime.parse("0.00"), RaceResultCode.DQ),
				new Athlete("F_DNF", RaceTime.parse("0.00"), RaceResultCode.DNF)
		);
		entries.put("heat", new ArrayList<>(athletes));

		SeedingStrategy strategy = new HeatsSeedingStrategy();
		strategy.generateSeeding(entries, 8);

		List<Athlete> sorted = entries.get("heat");

		// 全部非 OK，按 DQ → DNS → DNF → SCR → DSQ
		assertEquals(RaceResultCode.DQ, sorted.get(0).getResultCode());
		assertEquals(RaceResultCode.DNS, sorted.get(1).getResultCode());
		assertEquals(RaceResultCode.DNF, sorted.get(2).getResultCode());
		assertEquals(RaceResultCode.DNF, sorted.get(3).getResultCode());
		assertEquals(RaceResultCode.SCR, sorted.get(4).getResultCode());
		assertEquals(RaceResultCode.DSQ, sorted.get(5).getResultCode());

		// 6 人 ≤ 8 泳道，应在同一组
		assertEquals(1, sorted.get(0).getGroup());
	}

	@Test
	@DisplayName("预赛: 长距离项目混合 OK 与非 OK")
	void heatsLongDistanceMixed() {
		List<Athlete> athletes = Arrays.asList(
				new Athlete("A_OK", RaceTime.parse("4:15.00"), RaceResultCode.OK),
				new Athlete("B_OK", RaceTime.parse("4:10.00"), RaceResultCode.OK),
				new Athlete("C_DQ", RaceTime.parse("4:05.00"), RaceResultCode.DQ),
				new Athlete("D_OK", RaceTime.parse("4:20.00"), RaceResultCode.OK),
				new Athlete("E_DNS", RaceTime.parse("0.00"), RaceResultCode.DNS),
				new Athlete("F_OK", RaceTime.parse("4:12.00"), RaceResultCode.OK),
				new Athlete("G_OK", RaceTime.parse("4:08.00"), RaceResultCode.OK),
				new Athlete("H_DNF", RaceTime.parse("0.00"), RaceResultCode.DNF),
				new Athlete("I_OK", RaceTime.parse("4:18.00"), RaceResultCode.OK),
				new Athlete("J_OK", RaceTime.parse("4:22.00"), RaceResultCode.OK)
		);
		entries.put("heat", new ArrayList<>(athletes));

		SeedingStrategy strategy = new HeatsSeedingStrategy(true); // 长距离
		strategy.generateSeeding(entries, 8);

		List<Athlete> sorted = entries.get("heat");

		// OK 运动员成绩顺序: 4:08, 4:10, 4:12, 4:15, 4:18, 4:20, 4:22
		assertEquals("G_OK", sorted.get(0).getName());
		assertEquals("B_OK", sorted.get(1).getName());
		assertEquals("F_OK", sorted.get(2).getName());
		assertEquals("A_OK", sorted.get(3).getName());
		assertEquals("I_OK", sorted.get(4).getName());
		assertEquals("D_OK", sorted.get(5).getName());
		assertEquals("J_OK", sorted.get(6).getName());

		// 非 OK 排在后面
		assertEquals(RaceResultCode.DQ, sorted.get(7).getResultCode());
		assertEquals(RaceResultCode.DNS, sorted.get(8).getResultCode());
		assertEquals(RaceResultCode.DNF, sorted.get(9).getResultCode());
	}

	// ==================== 半决赛编排 ====================

	@Test
	@DisplayName("半决赛: 混合 OK 与非 OK，OK 进入两组交替分配，非 OK 排在最后")
	void semiFinalsMixedOkAndNonOk() {
		List<Athlete> athletes = Arrays.asList(
				new Athlete("A_OK", RaceTime.parse("52.00"), RaceResultCode.OK),
				new Athlete("B_OK", RaceTime.parse("51.00"), RaceResultCode.OK),
				new Athlete("C_DQ", RaceTime.parse("50.00"), RaceResultCode.DQ),
				new Athlete("D_OK", RaceTime.parse("53.00"), RaceResultCode.OK),
				new Athlete("E_OK", RaceTime.parse("54.00"), RaceResultCode.OK),
				new Athlete("F_DNS", RaceTime.parse("0.00"), RaceResultCode.DNS),
				new Athlete("G_OK", RaceTime.parse("55.00"), RaceResultCode.OK),
				new Athlete("H_DNF", RaceTime.parse("0.00"), RaceResultCode.DNF)
		);
		entries.put("semi", new ArrayList<>(athletes));

		SeedingStrategy strategy = new SemiFinalsSeedingStrategy();
		strategy.generateSeeding(entries, 8);

		List<Athlete> sorted = entries.get("semi");

		// 前 5 名是 OK，按成绩排序
		assertEquals("B_OK", sorted.get(0).getName()); // 51.00
		assertEquals("A_OK", sorted.get(1).getName()); // 52.00
		assertEquals("D_OK", sorted.get(2).getName()); // 53.00
		assertEquals("E_OK", sorted.get(3).getName()); // 54.00
		assertEquals("G_OK", sorted.get(4).getName()); // 55.00

		// 后 3 名是非 OK
		assertEquals(RaceResultCode.DQ, sorted.get(5).getResultCode());
		assertEquals(RaceResultCode.DNS, sorted.get(6).getResultCode());
		assertEquals(RaceResultCode.DNF, sorted.get(7).getResultCode());

		// OK 运动员的分组：最快→第2组，次快→第1组，交替
		Athlete fastest = findByName(sorted, "B_OK");
		Athlete second = findByName(sorted, "A_OK");
		assertEquals(2, fastest.getGroup(), "最快应进入半决赛第2组");
		assertEquals(1, second.getGroup(), "次快应进入半决赛第1组");
	}

	@Test
	@DisplayName("半决赛: 全部非 OK 时，仍分两组并按规则排序")
	void semiFinalsAllNonOk() {
		List<Athlete> athletes = Arrays.asList(
				new Athlete("A_DQ", RaceTime.parse("0.00"), RaceResultCode.DQ),
				new Athlete("B_DSQ", RaceTime.parse("0.00"), RaceResultCode.DSQ),
				new Athlete("C_DNS", RaceTime.parse("0.00"), RaceResultCode.DNS),
				new Athlete("D_SCR", RaceTime.parse("0.00"), RaceResultCode.SCR),
				new Athlete("E_DNF", RaceTime.parse("0.00"), RaceResultCode.DNF),
				new Athlete("F_DQ", RaceTime.parse("0.00"), RaceResultCode.DQ)
		);
		entries.put("semi", new ArrayList<>(athletes));

		SeedingStrategy strategy = new SemiFinalsSeedingStrategy();
		strategy.generateSeeding(entries, 8);

		List<Athlete> sorted = entries.get("semi");

		// 排序验证: DQ → DQ → DNS → DNF → SCR → DSQ
		assertEquals(RaceResultCode.DQ, sorted.get(0).getResultCode());
		assertEquals(RaceResultCode.DQ, sorted.get(1).getResultCode());
		assertEquals(RaceResultCode.DNS, sorted.get(2).getResultCode());
		assertEquals(RaceResultCode.DNF, sorted.get(3).getResultCode());
		assertEquals(RaceResultCode.SCR, sorted.get(4).getResultCode());
		assertEquals(RaceResultCode.DSQ, sorted.get(5).getResultCode());

		// 分组验证: 交替分配，最快(DQ-A)→第2组
		assertEquals(2, sorted.get(0).getGroup());
		assertEquals(1, sorted.get(1).getGroup());
	}

	// ==================== 决赛编排 ====================

	@Test
	@DisplayName("决赛: 混合 OK 与非 OK，OK 在前分配泳道，非 OK 排在最后")
	void finalsMixedOkAndNonOk() {
		List<Athlete> athletes = Arrays.asList(
				new Athlete("A_OK", RaceTime.parse("52.00"), RaceResultCode.OK),
				new Athlete("B_OK", RaceTime.parse("51.00"), RaceResultCode.OK),
				new Athlete("C_DQ", RaceTime.parse("50.00"), RaceResultCode.DQ),
				new Athlete("D_OK", RaceTime.parse("53.00"), RaceResultCode.OK),
				new Athlete("E_DNS", RaceTime.parse("0.00"), RaceResultCode.DNS),
				new Athlete("F_OK", RaceTime.parse("54.00"), RaceResultCode.OK),
				new Athlete("G_DNF", RaceTime.parse("0.00"), RaceResultCode.DNF),
				new Athlete("H_OK", RaceTime.parse("55.00"), RaceResultCode.OK)
		);
		entries.put("final", new ArrayList<>(athletes));

		SeedingStrategy strategy = new FinalsSeedingStrategy();
		strategy.generateSeeding(entries, 8);

		List<Athlete> sorted = entries.get("final");

		// 全部在同一组（决赛）
		assertTrue(sorted.stream().allMatch(a -> a.getGroup() == 1));

		// OK 在前，按成绩排序
		assertEquals("B_OK", sorted.get(0).getName()); // 51.00 → 第4道
		assertEquals("A_OK", sorted.get(1).getName()); // 52.00 → 第5道
		assertEquals("D_OK", sorted.get(2).getName()); // 53.00 → 第3道
		assertEquals("F_OK", sorted.get(3).getName()); // 54.00 → 第6道
		assertEquals("H_OK", sorted.get(4).getName()); // 55.00 → 第2道

		// 非 OK 排在最后
		assertEquals(RaceResultCode.DQ, sorted.get(5).getResultCode());
		assertEquals(RaceResultCode.DNS, sorted.get(6).getResultCode());
		assertEquals(RaceResultCode.DNF, sorted.get(7).getResultCode());

		// 泳道验证: 8泳道池，最快→第4道
		assertEquals(4, findByName(sorted, "B_OK").getSwimLane());
		assertEquals(5, findByName(sorted, "A_OK").getSwimLane());
		assertEquals(3, findByName(sorted, "D_OK").getSwimLane());
	}

	@Test
	@DisplayName("决赛: 第一名 OK，其余全部非 OK，冠军有效其余无排名")
	void finalsOnlyChampionOk() {
		List<Athlete> athletes = Arrays.asList(
				new Athlete("Champion", RaceTime.parse("50.00"), RaceResultCode.OK),
				new Athlete("DQ1", RaceTime.parse("51.00"), RaceResultCode.DQ),
				new Athlete("DNS1", RaceTime.parse("0.00"), RaceResultCode.DNS),
				new Athlete("DNF1", RaceTime.parse("0.00"), RaceResultCode.DNF),
				new Athlete("SCR1", RaceTime.parse("0.00"), RaceResultCode.SCR),
				new Athlete("DSQ1", RaceTime.parse("0.00"), RaceResultCode.DSQ),
				new Athlete("DQ2", RaceTime.parse("52.00"), RaceResultCode.DQ),
				new Athlete("DNS2", RaceTime.parse("0.00"), RaceResultCode.DNS)
		);
		entries.put("final", new ArrayList<>(athletes));

		SeedingStrategy strategy = new FinalsSeedingStrategy();
		strategy.generateSeeding(entries, 8);

		List<Athlete> sorted = entries.get("final");

		// 冠军在前
		assertEquals("Champion", sorted.get(0).getName());
		assertEquals(RaceResultCode.OK, sorted.get(0).getResultCode());
		assertEquals(4, sorted.get(0).getSwimLane()); // 最快→第4道

		// 其余全部非 OK
		for (int i = 1; i < sorted.size(); i++) {
			assertFalse(sorted.get(i).getResultCode().isQualified(),
					"索引 " + i + " 应为非 OK");
		}

		// 非 OK 顺序: DQ → DQ → DNS → DNS → DNF → SCR → DSQ
		assertEquals(RaceResultCode.DQ, sorted.get(1).getResultCode());
		assertEquals(RaceResultCode.DQ, sorted.get(2).getResultCode());
		assertEquals(RaceResultCode.DNS, sorted.get(3).getResultCode());
		assertEquals(RaceResultCode.DNS, sorted.get(4).getResultCode());
		assertEquals(RaceResultCode.DNF, sorted.get(5).getResultCode());
		assertEquals(RaceResultCode.SCR, sorted.get(6).getResultCode());
		assertEquals(RaceResultCode.DSQ, sorted.get(7).getResultCode());
	}

	// ==================== 晋级场景 ====================

	@Test
	@DisplayName("晋级: 预赛结果决定半决赛名单，非 OK 运动员不应进入半决赛编排池")
	void nonOkAthletesShouldNotAdvanceToSemiFinals() {
		// 模拟预赛结果
		List<Athlete> prelimResults = Arrays.asList(
				new Athlete("A", RaceTime.parse("51.00"), RaceResultCode.OK),
				new Athlete("B", RaceTime.parse("52.00"), RaceResultCode.OK),
				new Athlete("C", RaceTime.parse("50.00"), RaceResultCode.DQ),  // 取消资格，不应晋级
				new Athlete("D", RaceTime.parse("53.00"), RaceResultCode.OK),
				new Athlete("E", RaceTime.parse("0.00"), RaceResultCode.DNS),   // 未出发，不应晋级
				new Athlete("F", RaceTime.parse("54.00"), RaceResultCode.OK),
				new Athlete("G", RaceTime.parse("55.00"), RaceResultCode.OK),
				new Athlete("H", RaceTime.parse("0.00"), RaceResultCode.DNF)    // 未完成，不应晋级
		);
		entries.put("prelim", new ArrayList<>(prelimResults));

		// 预赛编排
		SeedingStrategy heats = new HeatsSeedingStrategy();
		heats.generateSeeding(entries, 8);

		// 模拟晋级: 只选取 OK 运动员进入半决赛
		List<Athlete> qualified = prelimResults.stream()
				.filter(a -> a.getResultCode().isQualified())
				.toList();

		assertEquals(5, qualified.size(), "应有 5 名 OK 运动员晋级");
		assertTrue(qualified.stream().allMatch(a -> a.getResultCode() == RaceResultCode.OK));

		// 半决赛编排（仅晋级运动员）
		Map<String, List<Athlete>> semiEntries = new HashMap<>();
		semiEntries.put("semi", new ArrayList<>(qualified));

		SeedingStrategy semi = new SemiFinalsSeedingStrategy();
		semi.generateSeeding(semiEntries, 8);

		List<Athlete> semiSorted = semiEntries.get("semi");
		// 5 人分两组: 最快→第2组，次快→第1组
		assertEquals("A", semiSorted.get(0).getName());
		assertEquals("B", semiSorted.get(1).getName());
		assertEquals("D", semiSorted.get(2).getName());
		assertEquals("F", semiSorted.get(3).getName());
		assertEquals("G", semiSorted.get(4).getName());
	}

	// ==================== 辅助方法 ====================

	private Athlete findByName(List<Athlete> athletes, String name) {
		return athletes.stream()
				.filter(a -> a.getName().equals(name))
				.findFirst()
				.orElseThrow(() -> new AssertionError("找不到运动员: " + name));
	}
}
