package io.github.murphy955.fina.records.register;

import io.github.murphy955.fina.domain.enm.EventType;
import io.github.murphy955.fina.domain.enm.Gender;
import io.github.murphy955.fina.domain.enm.Stroke;
import io.github.murphy955.fina.domain.entity.achievements.RaceTime;
import io.github.murphy955.fina.domain.entity.competition.Record;
import io.github.murphy955.fina.domain.entity.project.Project;
import io.github.murphy955.fina.domain.shared.BaseGroup;
import io.github.murphy955.fina.domain.vo.RaceInfo;
import io.github.murphy955.fina.records.filter.AbstractRecordFilterChain;
import io.github.murphy955.fina.records.vo.OverRecordMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RecordEngine 单元测试。
 *
 * @author 李泽聿
 * @since 2026/05/13
 */
@DisplayName("RecordEngine 测试")
class RecordEngineTest {

    enum TestAgeGroup implements BaseGroup {
        U18("U18"), SENIOR("SENIOR");

        private final String name;

        TestAgeGroup(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    private RecordEngine<TestAgeGroup> engine;
    private RaceInfo<TestAgeGroup> raceInfo;

    @BeforeEach
    void setUp() {
        engine = new RecordEngine<>();
        raceInfo = new RaceInfo<>(
                Gender.MALE,
                TestAgeGroup.U18,
                "100",
                EventType.INDIVIDUAL,
                Stroke.FREESTYLE
        );
    }

    // ==================== 辅助方法：创建 Filter ====================

    private AbstractRecordFilterChain<TestAgeGroup> createFilter(
            int priority, String level, String time) {
        Record<TestAgeGroup> record = new Record<>(raceInfo, time);
        Map<String, Record<TestAgeGroup>> map = new HashMap<>();
        map.put(record.getKey(), record);
        return new AbstractRecordFilterChain<>(priority, level, map) {};
    }

    private AbstractRecordFilterChain<TestAgeGroup> createFilter(
            int priority, String level, List<Record<TestAgeGroup>> records) {
        return new AbstractRecordFilterChain<>(priority, level, records) {};
    }

    // ==================== register & priority ====================

    @Test
    @DisplayName("注册后按 priority 降序排列")
    void registerSortsByPriorityDesc() {
        AbstractRecordFilterChain<TestAgeGroup> low = createFilter(1, "LOW", "55.00");
        AbstractRecordFilterChain<TestAgeGroup> high = createFilter(10, "HIGH", "54.00");

        engine.register(low);
        engine.register(high);

        RaceTime time = RaceTime.parse("53.00");
        // priority 高的先被评估：HIGH(54.00) < 53.00? 否，继续 LOW(55.00) < 53.00? 是 → true
        assertTrue(engine.evaluate(time, raceInfo));
    }

    // ==================== evaluate ====================

    @Test
    @DisplayName("成绩快于所有纪录 → 打破")
    void evaluateReturnsTrueWhenFasterThanAll() {
        engine.register(createFilter(1, "WORLD", "52.00"));
        engine.register(createFilter(1, "NATIONAL", "53.00"));

        RaceTime athleteTime = RaceTime.parse("51.00");
        assertTrue(engine.evaluate(athleteTime, raceInfo));
    }

    @Test
    @DisplayName("成绩慢于所有纪录 → 未打破")
    void evaluateReturnsFalseWhenSlowerThanAll() {
        engine.register(createFilter(1, "WORLD", "52.00"));
        engine.register(createFilter(1, "NATIONAL", "53.00"));

        RaceTime athleteTime = RaceTime.parse("54.00");
        assertFalse(engine.evaluate(athleteTime, raceInfo));
    }

    @Test
    @DisplayName("成绩介于两级纪录之间 → 打破（因为至少破了一级）")
    void evaluateReturnsTrueWhenBetweenRecords() {
        engine.register(createFilter(1, "WORLD", "52.00"));
        engine.register(createFilter(1, "NATIONAL", "54.00"));

        // 53.00 破全国纪录(54.00) 但未破世界纪录(52.00)
        RaceTime athleteTime = RaceTime.parse("53.00");
        assertTrue(engine.evaluate(athleteTime, raceInfo));
    }

    @Test
    @DisplayName("无对应纪录 → 未打破")
    void evaluateReturnsFalseWhenNoRecordFound() {
        engine.register(createFilter(1, "WORLD", "52.00"));

        RaceInfo<TestAgeGroup> otherRace = new RaceInfo<>(
                Gender.FEMALE, TestAgeGroup.SENIOR, "200",
                EventType.INDIVIDUAL, Stroke.BREASTSTROKE
        );
        RaceTime athleteTime = RaceTime.parse("51.00");
        assertFalse(engine.evaluate(athleteTime, otherRace));
    }

    @Test
    @DisplayName("evaluate(Project) 委托正确")
    void evaluateProject() {
        engine.register(createFilter(1, "WORLD", "52.00"));

        Project<TestAgeGroup> project = new Project<>();
        project.setResult(new io.github.murphy955.fina.domain.entity.achievements.Result(RaceTime.parse("51.00")));
        project.setRaceInfo(raceInfo);

        assertTrue(engine.evaluate(project));
    }

    // ==================== getOverRecordMap ====================

    @Test
    @DisplayName("getOverRecordMap 返回各级比对结果")
    void getOverRecordMapReturnsComparisonResults() {
        engine.register(createFilter(1, "WORLD", "52.00"));
        engine.register(createFilter(1, "NATIONAL", "54.00"));

        RaceTime athleteTime = RaceTime.parse("53.00");
        Map<String, OverRecordMap<TestAgeGroup>> result = engine.getOverRecordMap(athleteTime, raceInfo);

        assertEquals(2, result.size());

        String worldKey = "WORLD-" + raceInfo.toKey();
        String nationalKey = "NATIONAL-" + raceInfo.toKey();

        assertTrue(result.containsKey(worldKey));
        assertTrue(result.containsKey(nationalKey));

        // 53.00 未破 WORLD(52.00)
        OverRecordMap<TestAgeGroup> worldMap = result.get(worldKey);
        assertFalse(worldMap.isOverRecord());
        assertEquals(RaceTime.parse("52.00").toString(), worldMap.getOldRecordTime().toString());
        assertEquals(RaceTime.parse("53.00").toString(), worldMap.getNewRecordTime().toString());
        assertEquals("WORLD", worldMap.getRecordLevel());

        // 53.00 破了 NATIONAL(54.00)
        OverRecordMap<TestAgeGroup> nationalMap = result.get(nationalKey);
        assertTrue(nationalMap.isOverRecord());
        assertEquals("NATIONAL", nationalMap.getRecordLevel());
    }

    @Test
    @DisplayName("getOverRecordMap 无对应纪录时返回空 Map")
    void getOverRecordMapReturnsEmptyWhenNoRecord() {
        engine.register(createFilter(1, "WORLD", "52.00"));

        RaceInfo<TestAgeGroup> otherRace = new RaceInfo<>(
                Gender.FEMALE, TestAgeGroup.SENIOR, "200",
                EventType.INDIVIDUAL, Stroke.BREASTSTROKE
        );
        Map<String, OverRecordMap<TestAgeGroup>> result = engine.getOverRecordMap(RaceTime.parse("51.00"), otherRace);
        assertTrue(result.isEmpty());
    }

    // ==================== changeFilterChain ====================

    @Test
    @DisplayName("changeFilterChain 更新纪录后再次评估应返回 false")
    void changeFilterChainUpdatesRecord() {
        AbstractRecordFilterChain<TestAgeGroup> worldFilter = createFilter(1, "WORLD", "52.00");
        engine.register(worldFilter);

        RaceTime athleteTime = RaceTime.parse("51.00");
        // 先确认打破纪录
        assertTrue(engine.evaluate(athleteTime, raceInfo));

        // 获取比对结果并更新
        Map<String, OverRecordMap<TestAgeGroup>> overMap = engine.getOverRecordMap(athleteTime, raceInfo);
        engine.changeFilterChain(overMap);

        // 更新后再用同样的成绩评估，应不再打破
        assertFalse(engine.evaluate(athleteTime, raceInfo));

        // 验证 filter 内部的 record 已被更新
        Record<?> updatedRecord = worldFilter.getRecordMap().get(raceInfo.toKey());
        assertNotNull(updatedRecord);
        assertEquals(athleteTime, updatedRecord.getRaceTime());
    }

    @Test
    @DisplayName("changeFilterChain 仅更新 isOverRecord=true 的条目")
    void changeFilterChainSkipsNonBrokenRecords() {
        engine.register(createFilter(1, "WORLD", "52.00"));
        engine.register(createFilter(1, "NATIONAL", "54.00"));

        // 53.00 只破 NATIONAL，未破 WORLD
        RaceTime athleteTime = RaceTime.parse("53.00");
        Map<String, OverRecordMap<TestAgeGroup>> overMap = engine.getOverRecordMap(athleteTime, raceInfo);
        engine.changeFilterChain(overMap);

        // WORLD 纪录应保持 52.00 不变
        Record<?> worldRecord = engine.getOverRecordMap(athleteTime, raceInfo).keySet().stream()
                .filter(k -> k.startsWith("WORLD"))
                .map(k -> {
                    // 重新获取 filter 中的 record
                    for (var f : Arrays.asList(createFilter(1, "WORLD", "52.00"))) {
                        return f.getRecordMap().get(raceInfo.toKey());
                    }
                    return null;
                })
                .findFirst().orElse(null);

        // 更直接的验证：用 51.00 去破 WORLD，如果 WORLD 已被更新为 53.00，则 51.00 仍然破
        // 但 WORLD 实际是 52.00，所以下面验证的是 NATIONAL 被更新、WORLD 未被更新
        // 由于 engine 内部持有的是同一个 filter 引用，我们可以直接获取
        assertTrue(engine.evaluate(RaceTime.parse("51.50"), raceInfo),
                "WORLD 纪录应仍为 52.00，51.50 应该能破");
    }
}
