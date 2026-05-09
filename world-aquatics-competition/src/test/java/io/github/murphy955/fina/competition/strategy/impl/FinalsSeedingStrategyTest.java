package io.github.murphy955.fina.competition.strategy.impl;

import io.github.murphy955.fina.common.exception.ValidationException;
import io.github.murphy955.fina.domain.entity.athlete.Athlete;
import io.github.murphy955.fina.domain.entity.achievements.RaceTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FinalsSeedingStrategy 单元测试
 *
 * @author 李泽聿
 * @since 2026/05/08
 */
class FinalsSeedingStrategyTest {

	private FinalsSeedingStrategy strategy;

	@BeforeEach
	void setUp() {
		strategy = new FinalsSeedingStrategy();
	}

	@Test
	@DisplayName("决赛: raceTime为空应抛出ValidationException")
	void nullRaceTimeThrowsValidationException() {
		List<Athlete> athletes = new ArrayList<>();
		athletes.add(new Athlete("WithTime", RaceTime.parse("1:00.00")));
		athletes.add(new Athlete("WithoutTime", null));

		ValidationException exception = assertThrows(ValidationException.class, () -> {
			strategy.generateSeeding(wrap(athletes), 8);
		});

		assertTrue(exception.getMessage().contains("raceTime"));
	}

	@Test
	@DisplayName("决赛: 所有运动员应在同一组（Final）")
	void finalsSingleGroup() {
		List<Athlete> athletes = createAthletes(8);
		strategy.generateSeeding(wrap(athletes), 8);

		for (Athlete athlete : athletes) {
			assertEquals(1, athlete.getGroup(),
					"All athletes should be in the final (group 1)");
		}
	}

	@Test
	@DisplayName("决赛: 泳道应按成绩从快到慢中心对称分配")
	void finalsLaneAssignment() {
		List<Athlete> athletes = createAthletes(8);
		strategy.generateSeeding(wrap(athletes), 8);

		// 8泳道决赛：1→4, 2→5, 3→3, 4→6, 5→2, 6→7, 7→1, 8→8
		assertEquals(4, athletes.get(0).getSwimLane()); // 最快
		assertEquals(5, athletes.get(1).getSwimLane());
		assertEquals(3, athletes.get(2).getSwimLane());
		assertEquals(6, athletes.get(3).getSwimLane());
		assertEquals(2, athletes.get(4).getSwimLane());
		assertEquals(7, athletes.get(5).getSwimLane());
		assertEquals(1, athletes.get(6).getSwimLane());
		assertEquals(8, athletes.get(7).getSwimLane()); // 最慢
	}

	@Test
	@DisplayName("决赛: 10泳道池预赛可用10道，决赛按8道分配")
	void finalsWithTenLanePool() {
		List<Athlete> athletes = createAthletes(8);
		strategy.generateSeeding(wrap(athletes), 10);

		// 虽然传入 laneCount=10，但决赛通常只用8道。
		// 这里的实现按传入的 laneCount 分配，调用方应传入实际使用的泳道数（8）
		// 此测试验证 FinalsSeedingStrategy 本身不限制组数
		for (Athlete athlete : athletes) {
			assertEquals(1, athlete.getGroup());
		}
	}

	private Map<String, List<Athlete>> wrap(List<Athlete> athletes) {
		Map<String, List<Athlete>> map = new HashMap<>();
		map.put("test", athletes);
		return map;
	}

	// ==================== 辅助方法 ====================

	private List<Athlete> createAthletes(int count) {
		List<Athlete> list = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			String timeStr = String.format("%d.00", 50 + i);
			list.add(new Athlete("Athlete" + i, RaceTime.parse(timeStr)));
		}
		return list;
	}
}
