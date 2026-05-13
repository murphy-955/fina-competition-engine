package io.github.murphy955.fina.records.register;

import io.github.murphy955.fina.domain.enm.EventType;
import io.github.murphy955.fina.domain.enm.Gender;
import io.github.murphy955.fina.domain.enm.Stroke;
import io.github.murphy955.fina.domain.entity.achievements.RaceTime;
import io.github.murphy955.fina.domain.entity.competition.Record;
import io.github.murphy955.fina.domain.entity.project.Project;
import io.github.murphy955.fina.domain.service.RaceKeyBuilder;
import io.github.murphy955.fina.domain.shared.BaseGroup;
import io.github.murphy955.fina.records.filter.AbstractRecordFilterChain;

import java.util.List;

/**
 *
 *
 * @author : 李泽聿
 * @since : 2026:05:12 15:49
 */
public class RecordEngine<G extends Enum<G> & BaseGroup> {
    private List<AbstractRecordFilterChain<? extends Enum<? extends BaseGroup>>> recordFilterChain = new java.util.ArrayList<>();

    public void register(AbstractRecordFilterChain<? extends Enum<? extends BaseGroup>> recordFilterChain) {
        this.recordFilterChain.add(recordFilterChain);
        // getPriority的值越大，应当越靠近列表头部
        this.recordFilterChain.sort((o1, o2) ->
                o2.getPriority() - o1.getPriority());
    }

    /**
     * @param result {@link Project}
     * @return boolean 如果破纪录则返回true
     * @author 李泽聿
     * @see Project
     * @since 2026-05-13 14:44
     */
    public boolean evaluate(Project<G> result) {
        return evaluate(result.getResult().getTotalTime(),
                result.getKey());
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
     * @param athleteTime {@link RaceTime}
     * @param key         键
     * @return boolean 如果破纪录则返回true
     * @author 李泽聿
     * @since 2026-05-13 14:44
     */
    public boolean evaluate(RaceTime athleteTime, String key) {
        for (AbstractRecordFilterChain<? extends Enum<? extends BaseGroup>> filter : recordFilterChain) {
            // TODO: (李泽聿 ,2026-05-12 17:33 ,[2026-05-17]) 具体的责任链filter业务待实现
            Record<? extends Enum<? extends BaseGroup>> record = filter.getRecordMap().get(key);
            RaceTime recordTime = record.getRaceTime();
            // athleteTime < recordTime认为破纪录了
            if (athleteTime.compareTo(recordTime) < 0) {
                return true;
            }
        }
        return false;
    }
}
