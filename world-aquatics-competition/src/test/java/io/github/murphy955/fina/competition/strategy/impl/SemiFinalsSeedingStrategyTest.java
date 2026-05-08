package io.github.murphy955.fina.competition.strategy.impl;

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
 * SemiFinalsSeedingStrategy 单元测试
 *
 * @author 李泽聿
 * @since 2026/05/08
 */
class SemiFinalsSeedingStrategyTest {

	private SemiFinalsSeedingStrategy strategy;

	@BeforeEach
	void setUp() {
		strategy = new SemiFinalsSeedingStrategy();
	}

	@Test
	@DisplayName("半决赛: 16人应分为2组，最快→第2场半决赛")
	void semiFinalsTwoGroups() {
		List<Athlete> athletes = createAthletes(16);
		strategy.generateSeeding(wrap(athletes), 8);

		// 世界泳联半决赛规则：最快→第2场，次快→第1场，交替
		for (int i = 0; i < 16; i++) {
			Athlete athlete = findAthleteByName(athletes, "Athlete" + i);
			int expectedGroup = (i % 2 == 0) ? 2 : 1;
			assertEquals(expectedGroup, athlete.getGroup(),
					"Athlete" + i + " should be in semi-final " + expectedGroup);
		}
	}

	@Test
	@DisplayName("半决赛: 组内泳道应按成绩分配")
	void semiFinalsLaneAssignment() {
		List<Athlete> athletes = createAthletes(16);
		strategy.generateSeeding(wrap(athletes), 8);

		// Semi-final 2 的最快者是 Athlete0，应得第4道
		Athlete fastestInSf2 = findAthleteByName(athletes, "Athlete0");
		assertEquals(2, fastestInSf2.getGroup());
		assertEquals(4, fastestInSf2.getSwimLane());

		// Semi-final 1 的最快者是 Athlete1，应得第4道
		Athlete fastestInSf1 = findAthleteByName(athletes, "Athlete1");
		assertEquals(1, fastestInSf1.getGroup());
		assertEquals(4, fastestInSf1.getSwimLane());
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
