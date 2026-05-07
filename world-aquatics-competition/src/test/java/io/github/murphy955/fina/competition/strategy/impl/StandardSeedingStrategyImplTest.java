package io.github.murphy955.fina.competition.strategy.impl;

import io.github.murphy955.fina.competition.strategy.LaneAllocator;
import io.github.murphy955.fina.domain.entity.athlete.Athlete;
import io.github.murphy955.fina.domain.entity.achievements.RaceTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StandardSeedingStrategyImpl 单元测试
 *
 * @author 李泽聿
 * @since 2026/05/07
 */
class StandardSeedingStrategyImplTest {

	private StandardSeedingStrategyImpl strategy;

	@BeforeEach
	void setUp() {
		strategy = new StandardSeedingStrategyImpl();
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
		// List.of 返回不可变列表，需要转为可变列表
		List<Athlete> mutable = new ArrayList<>(athletes);

		strategy.generateSeeding(mutable, 8);

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

		strategy.generateSeeding(mutable, 8, Comparator.comparing(Athlete::getName), null);

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

		strategy.generateSeeding(mutable, 8, null, null);

		assertEquals("Fast", mutable.get(0).getName());
		assertEquals("Slow", mutable.get(1).getName());
	}

	// ==================== 8 泳道道次分配 ====================

	@Test
	@DisplayName("DefaultLaneAllocator: 8 泳道应按 SW 3.1.2 分配道次")
	void eightLaneAllocation() {
		List<Athlete> athletes = createAthletes(8);
		strategy.generateSeeding(athletes, 8);

		// 排名1→第4道, 2→5, 3→3, 4→6, 5→2, 6→7, 7→1, 8→8
		assertLaneAndGroup(athletes.get(0), 4, 1); // 第1名
		assertLaneAndGroup(athletes.get(1), 5, 1); // 第2名
		assertLaneAndGroup(athletes.get(2), 3, 1); // 第3名
		assertLaneAndGroup(athletes.get(3), 6, 1); // 第4名
		assertLaneAndGroup(athletes.get(4), 2, 1); // 第5名
		assertLaneAndGroup(athletes.get(5), 7, 1); // 第6名
		assertLaneAndGroup(athletes.get(6), 1, 1); // 第7名
		assertLaneAndGroup(athletes.get(7), 8, 1); // 第8名
	}

	// ==================== 6 泳道道次分配 ====================

	@Test
	@DisplayName("DefaultLaneAllocator: 6 泳道应按 SW 3.1.2 分配道次")
	void sixLaneAllocation() {
		List<Athlete> athletes = createAthletes(6);
		strategy.generateSeeding(athletes, 6);

		// 排名1→第3道, 2→4, 3→2, 4→5, 5→1, 6→6
		assertLaneAndGroup(athletes.get(0), 3, 1);
		assertLaneAndGroup(athletes.get(1), 4, 1);
		assertLaneAndGroup(athletes.get(2), 2, 1);
		assertLaneAndGroup(athletes.get(3), 5, 1);
		assertLaneAndGroup(athletes.get(4), 1, 1);
		assertLaneAndGroup(athletes.get(5), 6, 1);
	}

	// ==================== 10 泳道道次分配 ====================

	@Test
	@DisplayName("DefaultLaneAllocator: 10 泳道应按 SW 3.1.2 分配道次")
	void tenLaneAllocation() {
		List<Athlete> athletes = createAthletes(10);
		strategy.generateSeeding(athletes, 10);

		// 10 道池特殊处理：center = 4（泳道编号 0~9）
		// 排名1→第4道, 2→5, 3→3, 4→6, 5→2, 6→7, 7→1, 8→8, 9→0, 10→9
		assertLaneAndGroup(athletes.get(0), 4, 1);
		assertLaneAndGroup(athletes.get(1), 5, 1);
		assertLaneAndGroup(athletes.get(2), 3, 1);
		assertLaneAndGroup(athletes.get(3), 6, 1);
		assertLaneAndGroup(athletes.get(4), 2, 1);
		assertLaneAndGroup(athletes.get(5), 7, 1);
		assertLaneAndGroup(athletes.get(6), 1, 1);
		assertLaneAndGroup(athletes.get(7), 8, 1);
		assertLaneAndGroup(athletes.get(8), 0, 1);
		assertLaneAndGroup(athletes.get(9), 9, 1);
	}

	// ==================== 多组分配 ====================

	@Test
	@DisplayName("DefaultLaneAllocator: 16 人 8 泳道应分为 2 组")
	void multiGroupAllocation() {
		List<Athlete> athletes = createAthletes(16);
		strategy.generateSeeding(athletes, 8);

		// 第1组：运动员 0-7
		for (int i = 0; i < 8; i++) {
			assertEquals(1, athletes.get(i).getGroup(), "Athlete " + i + " should be in group 1");
		}
		// 第2组：运动员 8-15
		for (int i = 8; i < 16; i++) {
			assertEquals(2, athletes.get(i).getGroup(), "Athlete " + i + " should be in group 2");
		}

		// 第2组的道次分配应与第1组相同
		assertEquals(4, athletes.get(8).getSwimLane());
		assertEquals(5, athletes.get(9).getSwimLane());
	}

	// ==================== 运动员不足一组 ====================

	@Test
	@DisplayName("DefaultLaneAllocator: 5 人 8 泳道应只分 1 组，不越界")
	void lessThanOneGroup() {
		List<Athlete> athletes = createAthletes(5);
		strategy.generateSeeding(athletes, 8);

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

		strategy.generateSeeding(athletes, 8, null, customAllocator);

		assertTrue(called[0]);
		for (Athlete a : athletes) {
			assertEquals(99, a.getSwimLane());
			assertEquals(99, a.getGroup());
		}
	}

	@Test
	@DisplayName("generateSeeding: null LaneAllocator 应使用默认分配器")
	void nullLaneRuleUsesDefault() {
		List<Athlete> athletes = createAthletes(8);
		strategy.generateSeeding(athletes, 8, null, null);

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
			// 成绩从 50.00 开始，每人慢 1 秒
			String timeStr = String.format("%d.00", 50 + i);
			list.add(new Athlete("Athlete" + i, RaceTime.parse(timeStr)));
		}
		return list;
	}

	private void assertLaneAndGroup(Athlete athlete, int expectedLane, int expectedGroup) {
		assertEquals(expectedLane, athlete.getSwimLane(),
				"Athlete " + athlete.getName() + " swim lane mismatch");
		assertEquals(expectedGroup, athlete.getGroup(),
				"Athlete " + athlete.getName() + " group mismatch");
	}
}
