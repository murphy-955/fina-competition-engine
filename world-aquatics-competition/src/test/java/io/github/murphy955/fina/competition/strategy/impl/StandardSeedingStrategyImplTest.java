package io.github.murphy955.fina.competition.strategy.impl;

import io.github.murphy955.fina.competition.strategy.LaneAllocator;
import io.github.murphy955.fina.domain.entity.athlete.Athlete;
import io.github.murphy955.fina.domain.entity.achievements.RaceTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StandardSeedingStrategyImpl 单元测试（已委托给 HeatsSeedingStrategy）。
 *
 * @author 李泽聿
 * @since 2026/05/07
 */
class StandardSeedingStrategyImplTest {

	private HeatsSeedingStrategy strategy;

	@BeforeEach
	void setUp() {
		strategy = new HeatsSeedingStrategy();
	}

	// ==================== 排序测试 ====================

	@Test
	@DisplayName("generateSeeding: 默认应按成绩从小到大排序")
	void defaultSortingByRaceTime() {
		List<Athlete> athletes = List.of(
				new Athlete("Slow", RaceTime.parse("2:00.00")),
				new Athlete("Fast", RaceTime.parse("1:00.00")),
				new Athlete("Mid", RaceTime.parse("1:30.00"))
		);
		List<Athlete> mutable = new ArrayList<>(athletes);

		strategy.generateSeeding(wrap(mutable), 8);

		assertEquals("Fast", mutable.get(0).getName());
		assertEquals("Mid", mutable.get(1).getName());
		assertEquals("Slow", mutable.get(2).getName());
	}

	@Test
	@DisplayName("generateSeeding: 自定义比较器应按名称排序")
	void customSortingByName() {
		List<Athlete> athletes = List.of(
				new Athlete("Charlie", RaceTime.parse("1:00.00")),
				new Athlete("Alice", RaceTime.parse("2:00.00")),
				new Athlete("Bob", RaceTime.parse("1:30.00"))
		);
		List<Athlete> mutable = new ArrayList<>(athletes);

		strategy.generateSeeding(wrap(mutable), 8, Comparator.comparing(Athlete::getName), null);

		assertEquals("Alice", mutable.get(0).getName());
		assertEquals("Bob", mutable.get(1).getName());
		assertEquals("Charlie", mutable.get(2).getName());
	}

	@Test
	@DisplayName("generateSeeding: null 比较器应使用默认成绩排序")
	void nullSortRuleUsesDefault() {
		List<Athlete> athletes = List.of(
				new Athlete("Slow", RaceTime.parse("2:00.00")),
				new Athlete("Fast", RaceTime.parse("1:00.00"))
		);
		List<Athlete> mutable = new ArrayList<>(athletes);

		strategy.generateSeeding(wrap(mutable), 8, null, null);

		assertEquals("Fast", mutable.get(0).getName());
		assertEquals("Slow", mutable.get(1).getName());
	}

	// ==================== 8 泳道道次分配 ====================

	@Test
	@DisplayName("WorldAquaticsLaneAllocator: 8 泳道应按 SW 3.2.5.1 分配道次")
	void eightLaneAllocation() {
		List<Athlete> athletes = createAthletes(8);
		strategy.generateSeeding(wrap(athletes), 8);

		// 排名1→第4道, 2→5, 3→3, 4→6, 5→2, 6→7, 7→1, 8→8
		assertLane(athletes.get(0), 4); // 第1名
		assertLane(athletes.get(1), 5); // 第2名
		assertLane(athletes.get(2), 3); // 第3名
		assertLane(athletes.get(3), 6); // 第4名
		assertLane(athletes.get(4), 2); // 第5名
		assertLane(athletes.get(5), 7); // 第6名
		assertLane(athletes.get(6), 1); // 第7名
		assertLane(athletes.get(7), 8); // 第8名
	}

	// ==================== 6 泳道道次分配 ====================

	@Test
	@DisplayName("WorldAquaticsLaneAllocator: 6 泳道应按 SW 3.2.5.1 分配道次")
	void sixLaneAllocation() {
		List<Athlete> athletes = createAthletes(6);
		strategy.generateSeeding(wrap(athletes), 6);

		// 排名1→第3道, 2→4, 3→2, 4→5, 5→1, 6→6
		assertLane(athletes.get(0), 3);
		assertLane(athletes.get(1), 4);
		assertLane(athletes.get(2), 2);
		assertLane(athletes.get(3), 5);
		assertLane(athletes.get(4), 1);
		assertLane(athletes.get(5), 6);
	}

	// ==================== 10 泳道道次分配 ====================

	@Test
	@DisplayName("WorldAquaticsLaneAllocator: 10 泳道应按 SW 3.2.5.1 分配道次")
	void tenLaneAllocation() {
		List<Athlete> athletes = createAthletes(10);
		strategy.generateSeeding(wrap(athletes), 10);

		// 10道池：排名1→第4道, 2→5, 3→3, 4→6, 5→2, 6→7, 7→1, 8→8, 9→0, 10→9
		assertLane(athletes.get(0), 4);
		assertLane(athletes.get(1), 5);
		assertLane(athletes.get(2), 3);
		assertLane(athletes.get(3), 6);
		assertLane(athletes.get(4), 2);
		assertLane(athletes.get(5), 7);
		assertLane(athletes.get(6), 1);
		assertLane(athletes.get(7), 8);
		assertLane(athletes.get(8), 0);
		assertLane(athletes.get(9), 9);
	}

	// ==================== 多组分配（2组快慢交替）====================

	@Test
	@DisplayName("Heats: 16 人 8 泳道 2 组应按快慢交替分配")
	void multiGroupAllocationTwoHeats() {
		List<Athlete> athletes = createAthletes(16);
		strategy.generateSeeding(wrap(athletes), 8);

		// 世界泳联 2 组规则：最快→第2组，次快→第1组，交替
		// Athlete0(最快)→group2, Athlete1→group1, Athlete2→group2, Athlete3→group1...
		for (int i = 0; i < 16; i++) {
			Athlete athlete = findAthleteByName(athletes, "Athlete" + i);
			int expectedGroup = (i % 2 == 0) ? 2 : 1;
			assertEquals(expectedGroup, athlete.getGroup(),
					"Athlete" + i + " should be in group " + expectedGroup);
		}

		// 验证泳道：group 2 的最快者(Athlete0)应得第4道
		Athlete fastestInGroup2 = findAthleteByName(athletes, "Athlete0");
		assertEquals(4, fastestInGroup2.getSwimLane());

		// group 1 的最快者(Athlete1)应得第4道
		Athlete fastestInGroup1 = findAthleteByName(athletes, "Athlete1");
		assertEquals(4, fastestInGroup1.getSwimLane());
	}

	// ==================== 多组分配（3组循环）====================

	@Test
	@DisplayName("Heats: 24 人 8 泳道 3 组应按循环规则分配")
	void multiGroupAllocationThreeHeats() {
		HeatsSeedingStrategy heatsStrategy = new HeatsSeedingStrategy();
		List<Athlete> athletes = createAthletes(24);
		heatsStrategy.generateSeeding(wrap(athletes), 8);

		// 3组规则：最快→第3组，次快→第2组，第三快→第1组，循环
		// i=0→group3, i=1→group2, i=2→group1, i=3→group3, i=4→group2, i=5→group1...
		for (int i = 0; i < 24; i++) {
			Athlete athlete = findAthleteByName(athletes, "Athlete" + i);
			int expectedGroup = 3 - (i % 3);
			assertEquals(expectedGroup, athlete.getGroup(),
					"Athlete" + i + " should be in group " + expectedGroup);
		}
	}

	// ==================== 运动员不足一组 ====================

	@Test
	@DisplayName("Heats: 5 人 8 泳道应只分 1 组，不越界")
	void lessThanOneGroup() {
		List<Athlete> athletes = createAthletes(5);
		strategy.generateSeeding(wrap(athletes), 8);

		// 5 人都应在第1组
		for (int i = 0; i < 5; i++) {
			assertEquals(1, athletes.get(i).getGroup());
		}
		// 道次按排名分配：1→4, 2→5, 3→3, 4→6, 5→2
		assertEquals(4, athletes.get(0).getSwimLane());
		assertEquals(5, athletes.get(1).getSwimLane());
		assertEquals(3, athletes.get(2).getSwimLane());
		assertEquals(6, athletes.get(3).getSwimLane());
		assertEquals(2, athletes.get(4).getSwimLane());
	}

	// ==================== 自定义 LaneAllocator ====================

	@Test
	@DisplayName("generateSeeding: 应调用自定义 LaneAllocator")
	void customLaneAllocatorIsCalled() {
		List<Athlete> athletes = createAthletes(4);
		boolean[] called = {false};

		LaneAllocator customAllocator = (sorted, lanes) -> {
			called[0] = true;
			for (Athlete a : sorted) {
				a.setSwimLane(99);
				a.setGroup(99);
			}
		};

		strategy.generateSeeding(wrap(athletes), 8, null, customAllocator);

		assertTrue(called[0]);
		for (Athlete a : athletes) {
			assertEquals(99, a.getSwimLane());
			// LaneAllocator 只负责泳道分配，组号由策略决定
		}
	}

	@Test
	@DisplayName("generateSeeding: null LaneAllocator 应使用默认分配器")
	void nullLaneRuleUsesDefault() {
		List<Athlete> athletes = createAthletes(8);
		strategy.generateSeeding(wrap(athletes), 8, null, null);

		assertEquals(4, athletes.get(0).getSwimLane());
		assertEquals(1, athletes.get(0).getGroup());
	}

	// ==================== getDefaultComparator ====================

	@Test
	@DisplayName("getDefaultComparator: 成绩小的应排在前面")
	void defaultComparatorOrdersByRaceTime() {
		Comparator<? super Athlete> comparator = strategy.getDefaultComparator();
		Athlete fast = new Athlete("Fast", RaceTime.parse("1:00.00"));
		Athlete slow = new Athlete("Slow", RaceTime.parse("2:00.00"));

		assertTrue(comparator.compare(fast, slow) < 0);
		assertTrue(comparator.compare(slow, fast) > 0);
		assertEquals(0, comparator.compare(fast, fast));
	}

	// ==================== 辅助方法 ====================

	/**
	 * 创建 N 名运动员，成绩递增（第0名最快）
	 */
	private List<Athlete> createAthletes(int count) {
		List<Athlete> list = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			String timeStr = String.format("%d.00", 50 + i);
			list.add(new Athlete("Athlete" + i, RaceTime.parse(timeStr)));
		}
		return list;
	}

	private void assertLane(Athlete athlete, int expectedLane) {
		assertEquals(expectedLane, athlete.getSwimLane(),
				"Athlete " + athlete.getName() + " swim lane mismatch");
	}

	private Map<String, List<Athlete>> wrap(List<Athlete> athletes) {
		Map<String, List<Athlete>> map = new HashMap<>();
		map.put("test", athletes);
		return map;
	}

	private Athlete findAthleteByName(List<Athlete> athletes, String name) {
		return athletes.stream()
				.filter(a -> a.getName().equals(name))
				.findFirst()
				.orElseThrow(() -> new AssertionError("Athlete not found: " + name));
	}
}
