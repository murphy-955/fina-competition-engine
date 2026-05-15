package io.github.murphy955.fina.records.register;

import io.github.murphy955.fina.domain.enm.EventType;
import io.github.murphy955.fina.domain.enm.Gender;
import io.github.murphy955.fina.domain.enm.Stroke;
import io.github.murphy955.fina.domain.entity.achievements.RaceTime;
import io.github.murphy955.fina.domain.entity.competition.Record;
import io.github.murphy955.fina.domain.entity.project.Project;
import io.github.murphy955.fina.domain.util.RaceKeyUtil;
import io.github.murphy955.fina.domain.shared.BaseGroup;
import io.github.murphy955.fina.domain.vo.RaceInfo;
import io.github.murphy955.fina.records.filter.AbstractRecordFilterChain;
import io.github.murphy955.fina.records.vo.OverRecordMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 纪录引擎。
 * <p>
 * 管理多级纪录责任链，接收运动员成绩后与各级纪录线比对，判断是否破纪录。
 * </p>
 *
 * @param <G> 组别类型，须为枚举且实现 {@link BaseGroup}
 * @author : 李泽聿
 * @since : 2026:05:12 15:49
 */
public class RecordEngine<G extends Enum<G> & BaseGroup> {

    private final List<AbstractRecordFilterChain<G>> recordFilterChain = new ArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public RecordEngine() {
    }

    public void register(AbstractRecordFilterChain<G> recordFilterChain) {
        try {
            lock.writeLock().lock();
            this.recordFilterChain.add(recordFilterChain);
            // priority 越大越靠近头部
            this.recordFilterChain.sort((o1, o2) -> o2.getPriority() - o1.getPriority());
        } finally {
            lock.writeLock().unlock();
        }
    }

    // ==================== evaluate 重载 ====================

    /**
     * 评估给定项目成绩是否打破已注册纪录。
     *
     * @param project 比赛单项（含运动员、成绩、项目维度）
     * @return true 如果至少打破一级纪录
     */
    public boolean evaluate(Project<G> project) {
        return evaluate(project.getResult().getTotalTime(), project.getKey());
    }

    /**
     * 评估给定成绩是否打破已注册纪录（逐参方式）。
     *
     * @param athleteTime 运动员成绩
     * @param gender      性别
     * @param group       用户自定义的分组
     * @param distance    项目长度
     * @param event       项目类型（个人/团体）
     * @param stroke      泳姿
     * @return true 如果至少打破一级纪录
     */
    public boolean evaluate(RaceTime athleteTime, Gender gender, G group, String distance, EventType event, Stroke stroke) {
        return evaluate(athleteTime, RaceKeyUtil.buildKey(gender, group, distance, event, stroke));
    }

    /**
     * 评估给定成绩是否打破已注册纪录（RaceInfo 方式）。
     *
     * @param athleteTime 运动员成绩
     * @param raceInfo    项目维度信息
     * @return true 如果至少打破一级纪录
     */
    public boolean evaluate(RaceTime athleteTime, RaceInfo<G> raceInfo) {
        return evaluate(athleteTime, RaceKeyUtil.buildKey(raceInfo));
    }

    /**
     * 核心评估逻辑。
     *
     * <p>TODO: 支持 Filter 级 {@code BreakPolicy} 与 {@code EvaluationInterceptor} 自定义策略。
     * 当前为硬编码短路：一旦某个 filter 判定破纪录即 {@code return true}。
     * 后续应改为：
     * <pre>
     * for (filter : recordFilterChain) {
     *     if (破了该级 && filter.getBreakPolicy() == BREAK_ON_RECORD) return true;
     *     if (破了该级 && filter.getBreakPolicy() == CONTINUE)        continue;
     *     // 进阶：injector.shouldContinue(filter, athleteTime, recordTime)
     * }
     * </pre>
     * 注意：{@code getOverRecordMap} 应保持独立，始终返回全部比对结果，不受 BreakPolicy 影响。
     *
     * @param athleteTime 运动员成绩
     * @param key         项目唯一 key
     * @return true 如果至少打破一级纪录
     */
    protected boolean evaluate(RaceTime athleteTime, String key) {
        for (AbstractRecordFilterChain<G> filter : recordFilterChain) {
            RaceTime recordTime = readRecord(key, filter);
            if (recordTime == null) {
                continue; // 该项目在此 filter 中无纪录，跳过
            }
            if (athleteTime.compareTo(recordTime) < 0) {
                return true;
            }
        }
        return false;
    }

    private RaceTime readRecord(String key, AbstractRecordFilterChain<G> filter) {
        Record<G> record;
        try {
            lock.readLock().lock();
            record = filter.getRecordMap().get(key);
        } finally {
            lock.readLock().unlock();
        }
        return record == null ? null : record.getRaceTime();
    }

    // ==================== getOverRecordMap 重载 ====================

    /**
     * 1. Project 入口
     * 从 Project 中提取 RaceTime 与 RaceInfo，直接透传结构化对象。
     */
    public Map<String, OverRecordMap<G>> getOverRecordMap(Project<G> project) {
        return innerGetOverRecordMap(
                project.getResult().getTotalTime(),
                project.getRaceInfo()
        );
    }

    /**
     * 2. 逐参入口
     * 将离散参数装箱为 RaceInfo 后委托，全程不出现 String key 的提前编码。
     */
    public Map<String, OverRecordMap<G>> getOverRecordMap(
            RaceTime athleteTime,
            Gender gender,
            G group,
            String distance,
            EventType event,
            Stroke stroke) {
        return innerGetOverRecordMap(
                athleteTime,
                new RaceInfo<>(gender, group, distance, event, stroke)
        );
    }

    /**
     * 3. RaceInfo 公开入口
     * 外部已持有 RaceInfo 时的零转换透传。
     */
    public Map<String, OverRecordMap<G>> getOverRecordMap(
            RaceTime athleteTime,
            RaceInfo<G> raceInfo) {
        return innerGetOverRecordMap(athleteTime, raceInfo);
    }

    /**
     * 4. 核心逻辑（protected，与 evaluate 的 protected 核心方法作用域对齐）
     *
     * <p>RaceInfo 作为结构化对象单向流入核心链路；String key 仅在查询
     * {@code filter.getRecordMap()} 时做唯一一次 {@code buildKey}，且只出不进，
     * 彻底消除无意义的 decode 回环。</p>
     */
    protected Map<String, OverRecordMap<G>> innerGetOverRecordMap(
            RaceTime athleteTime,
            RaceInfo<G> raceInfo) {

        Map<String, OverRecordMap<G>> res = new HashMap<>();
        // 循环外仅做一次 buildKey，避免重复编码
        String key = RaceKeyUtil.buildKey(raceInfo);

        for (AbstractRecordFilterChain<G> filter : recordFilterChain) {
            String recordLevel = filter.getRecordLevel();

            RaceTime recordTime = readRecord(key, filter);
            if (recordTime == null) {
                continue; // 该项目在此 filter 中无纪录，不生成比对结果
            }
            // 结果 key 直接由 RaceInfo 现场组装，不再依赖预编码字符串的反向解析
            String mapKey = recordLevel + "-" + key;

            res.put(mapKey, new OverRecordMap<>(
                    raceInfo, recordTime, athleteTime, recordLevel));
        }
        return res;
    }

    /**
     * 更新责任链中的纪录线。<br>
     * <strong>
     *     注意： <br>
     *     此方法不保证数据库、缓存一致性。仅更新当前进程内的记录。<br>
     *     落库、更新缓存、一致性问题由调用者自行决断。
     * </strong>
     * <p>
     * 当运动员成绩打破某级纪录后，调用此方法将对应 Filter 中的旧纪录替换为新成绩。
     * 仅更新 {@code overRecordMap} 中 {@link OverRecordMap#isOverRecord()} 为 {@code true} 的条目。
     * </p>
     *
     * @param overRecordMap 各级纪录比对结果，key 格式为 {@code recordLevel-projectKey}
     */
    public void changeFilterChain(Map<String, OverRecordMap<G>> overRecordMap) {
        for (Map.Entry<String, OverRecordMap<G>> entry : overRecordMap.entrySet()) {
            String mapKey = entry.getKey();
            OverRecordMap<G> overRecord = entry.getValue();

            if (!overRecord.isOverRecord()) {
                continue;
            }

            // mapKey 格式: recordLevel-projectKey，projectKey 本身含多个 "-"
            String[] parts = mapKey.split("-", 2);
            if (parts.length != 2) {
                continue;
            }
            String recordLevel = parts[0];
            String key = parts[1];
            RaceTime newRecordTime = overRecord.getNewRecordTime();

            for (AbstractRecordFilterChain<G> filter : recordFilterChain) {
                if (filter.getRecordLevel().equals(recordLevel) && filter.getRecordMap().containsKey(key)) {
                    // 创建新 Record 替换旧纪录（Record 不可变，必须新建实例）
                    Record<G> newRecord = new Record<>(
                            overRecord.getRaceInfo(),
                            newRecordTime
                    );
                    try {
                        lock.writeLock().lock();
                        filter.getRecordMap().put(key, newRecord);
                    } finally {
                        lock.writeLock().unlock();
                    }
                    break; // 找到对应 filter 后无需继续遍历
                }
            }
        }
    }
}
