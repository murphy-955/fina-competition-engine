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

    private final Class<G> groupClass;
    private final List<AbstractRecordFilterChain<? extends Enum<? extends BaseGroup>>> recordFilterChain = new ArrayList<>();

    public RecordEngine(Class<G> groupClass) {
        this.groupClass = groupClass;
    }

    public void register(AbstractRecordFilterChain<? extends Enum<? extends BaseGroup>> recordFilterChain) {
        this.recordFilterChain.add(recordFilterChain);
        // priority 越大越靠近头部
        this.recordFilterChain.sort((o1, o2) -> o2.getPriority() - o1.getPriority());
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
     * @param athleteTime 运动员成绩
     * @param key         项目唯一 key
     * @return true 如果至少打破一级纪录
     */
    protected boolean evaluate(RaceTime athleteTime, String key) {
        for (AbstractRecordFilterChain<? extends Enum<? extends BaseGroup>> filter : recordFilterChain) {
            // TODO: (李泽聿 ,2026-05-12 17:33 ,[2026-05-17]) 责任链 filter 业务待实现
            Record<? extends Enum<? extends BaseGroup>> record = filter.getRecordMap().get(key);
            if (record == null) {
                continue;
            }
            RaceTime recordTime = record.getRaceTime();
            if (athleteTime.compareTo(recordTime) < 0) {
                return true;
            }
        }
        return false;
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

        for (AbstractRecordFilterChain<? extends Enum<? extends BaseGroup>> filter : recordFilterChain) {
            String recordLevel = filter.getRecordLevel();

            Record<? extends Enum<? extends BaseGroup>> record = filter.getRecordMap().get(key);
            if (record == null) {
                continue;
            }

            RaceTime recordTime = record.getRaceTime();
            // 结果 key 直接由 RaceInfo 现场组装，不再依赖预编码字符串的反向解析
            String mapKey = recordLevel + "-" + key;

            res.put(mapKey, new OverRecordMap<>(
                    raceInfo, recordTime, athleteTime, recordLevel));
        }
        return res;
    }
}
