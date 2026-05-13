package io.github.murphy955.fina.records.register;

import io.github.murphy955.fina.domain.enm.EventType;
import io.github.murphy955.fina.domain.enm.Gender;
import io.github.murphy955.fina.domain.enm.Stroke;
import io.github.murphy955.fina.domain.entity.achievements.RaceTime;
import io.github.murphy955.fina.domain.entity.competition.Record;
import io.github.murphy955.fina.domain.entity.project.Project;
import io.github.murphy955.fina.domain.service.RaceKeyBuilder;
import io.github.murphy955.fina.domain.shared.BaseGroup;
import io.github.murphy955.fina.domain.vo.RaceInfo;
import io.github.murphy955.fina.records.filter.AbstractRecordFilterChain;

import java.util.ArrayList;
import java.util.List;

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

    private final List<AbstractRecordFilterChain<? extends Enum<? extends BaseGroup>>> recordFilterChain = new ArrayList<>();

    public void register(AbstractRecordFilterChain<? extends Enum<? extends BaseGroup>> recordFilterChain) {
        this.recordFilterChain.add(recordFilterChain);
        // priority 越大越靠近头部
        this.recordFilterChain.sort((o1, o2) -> o2.getPriority() - o1.getPriority());
    }

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
     * @param athleteTime {@link RaceTime}
     * @param gender      性别
     * @param group       用户自定义的分组
     * @param distance    项目长度
     * @param event       项目类型（个人/团体）
     * @param stroke      泳姿
     * @return boolean 如果破纪录则返回true
     * @author 李泽聿
     * @since 2026-05-13 14:44
     */
    public boolean evaluate(RaceTime athleteTime, Gender gender, G group, String distance, EventType event, Stroke stroke) {
        return evaluate(athleteTime, RaceKeyBuilder.buildKey(gender, group, distance, event, stroke));
    }

    /**
     * @param athleteTime 运动员成绩
     * @param raceInfo 项目信息
     * @return boolean
     * @author 李泽聿
     * @since 2026-05-13 16:30
     */
    public boolean evaluate(RaceTime athleteTime, RaceInfo<G> raceInfo) {
        return evaluate(athleteTime, RaceKeyBuilder.buildKey(raceInfo));
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
}
