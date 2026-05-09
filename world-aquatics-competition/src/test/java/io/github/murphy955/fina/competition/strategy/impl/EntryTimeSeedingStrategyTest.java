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
 * EntryTimeSeedingStrategy 单元测试
 *
 * @author 李泽聿
 * @since 2026/05/09
 */
class EntryTimeSeedingStrategyTest {

    private EntryTimeSeedingStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new EntryTimeSeedingStrategy();
    }

    // ==================== 基本分组测试 ====================

    @Test
    @DisplayName("报名编排: 16人8泳道应分2组，最快→第2组")
    void twoGroupsSnakeDistribution() {
        List<Athlete> athletes = createAthletes(16);
        strategy.generateSeeding(wrap(athletes), 8);

        // 蛇形分配：i=0→group2, i=1→group1, i=2→group2, i=3→group1...
        for (int i = 0; i < 16; i++) {
            Athlete athlete = findAthleteByName(athletes, "Athlete" + i);
            int expectedGroup = (i % 2 == 0) ? 2 : 1;
            assertEquals(expectedGroup, athlete.getGroup(),
                    "Athlete" + i + " should be in group " + expectedGroup);
        }
    }

    @Test
    @DisplayName("报名编排: 24人8泳道应分3组，最快→第3组")
    void threeGroupsSnakeDistribution() {
        List<Athlete> athletes = createAthletes(24);
        strategy.generateSeeding(wrap(athletes), 8);

        // 蛇形分配：i=0→group3, i=1→group2, i=2→group1, i=3→group3...
        for (int i = 0; i < 24; i++) {
            Athlete athlete = findAthleteByName(athletes, "Athlete" + i);
            int expectedGroup = 3 - (i % 3);
            assertEquals(expectedGroup, athlete.getGroup(),
                    "Athlete" + i + " should be in group " + expectedGroup);
        }
    }

    @Test
    @DisplayName("报名编排: 32人8泳道应分4组，蛇形循环")
    void fourGroupsSnakeDistribution() {
        List<Athlete> athletes = createAthletes(32);
        strategy.generateSeeding(wrap(athletes), 8);

        // 4组蛇形：i=0→group4, i=1→group3, i=2→group2, i=3→group1, i=4→group4...
        for (int i = 0; i < 32; i++) {
            Athlete athlete = findAthleteByName(athletes, "Athlete" + i);
            int expectedGroup = 4 - (i % 4);
            assertEquals(expectedGroup, athlete.getGroup(),
                    "Athlete" + i + " should be in group " + expectedGroup);
        }
    }

    @Test
    @DisplayName("报名编排: 5人8泳道应只分1组")
    void lessThanOneGroup() {
        List<Athlete> athletes = createAthletes(5);
        strategy.generateSeeding(wrap(athletes), 8);

        for (int i = 0; i < 5; i++) {
            assertEquals(1, athletes.get(i).getGroup());
        }
    }

    // ==================== 泳道分配测试 ====================

    @Test
    @DisplayName("报名编排: 组内泳道应按成绩中心对称分配")
    void laneAssignmentWithinGroup() {
        List<Athlete> athletes = createAthletes(16);
        strategy.generateSeeding(wrap(athletes), 8);

        // 第2组的最快者是 Athlete0，应得第4道
        Athlete fastestInGroup2 = findAthleteByName(athletes, "Athlete0");
        assertEquals(2, fastestInGroup2.getGroup());
        assertEquals(4, fastestInGroup2.getSwimLane());

        // 第1组的最快者是 Athlete1，应得第4道
        Athlete fastestInGroup1 = findAthleteByName(athletes, "Athlete1");
        assertEquals(1, fastestInGroup1.getGroup());
        assertEquals(4, fastestInGroup1.getSwimLane());
    }

    // ==================== 自定义排序规则测试 ====================

    @Test
    @DisplayName("报名编排: 自定义sortRule应按名称排序")
    void customSortRule() {
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

    // ==================== 自定义泳道规则测试 ====================

    @Test
    @DisplayName("报名编排: 自定义laneRule应被调用")
    void customLaneRule() {
        List<Athlete> athletes = createAthletes(4);
        boolean[] called = {false};

        LaneAllocator customAllocator = (sorted, lanes) -> {
            called[0] = true;
            for (Athlete a : sorted) {
                a.setSwimLane(99);
            }
        };

        strategy.generateSeeding(wrap(athletes), 8, null, customAllocator);

        assertTrue(called[0]);
        for (Athlete a : athletes) {
            assertEquals(99, a.getSwimLane());
        }
    }

    // ==================== 混合有成绩/无成绩测试 ====================

    @Test
    @DisplayName("报名编排: 有成绩优先，无成绩按hashCode排后")
    void mixedNullAndNonNullRaceTime() {
        List<Athlete> athletes = new ArrayList<>();
        athletes.add(new Athlete("NoTime1", null));
        athletes.add(new Athlete("Fast", RaceTime.parse("1:00.00")));
        athletes.add(new Athlete("NoTime2", null));
        athletes.add(new Athlete("Slow", RaceTime.parse("2:00.00")));

        strategy.generateSeeding(wrap(athletes), 8);

        // 有成绩的在前，按成绩排序
        assertEquals("Fast", athletes.get(0).getName());
        assertEquals("Slow", athletes.get(1).getName());
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
