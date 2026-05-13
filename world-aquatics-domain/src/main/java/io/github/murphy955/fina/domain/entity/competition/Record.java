package io.github.murphy955.fina.domain.entity.competition;

import io.github.murphy955.fina.domain.entity.achievements.RaceTime;
import io.github.murphy955.fina.domain.shared.BaseGroup;
import io.github.murphy955.fina.domain.vo.RaceInfo;

import java.util.Objects;

/**
 * 纪录线实体
 * <p>
 * 表示某一级别（世界/全国/赛会等）在特定项目上的当前最好成绩。
 * 项目维度由 {@link RaceInfo} 统一封装，避免零散字段。
 * </p>
 *
 * @param <G> 组别类型，必须是枚举且实现 {@link BaseGroup}
 * @author : 李泽聿
 * @since : 2026:05:12 14:56
 */
public class Record<G extends Enum<G> & BaseGroup> {

    private final RaceInfo<G> raceInfo;
    private final RaceTime raceTime;

    public Record(RaceInfo<G> raceInfo, String raceTime) {
        this.raceInfo = Objects.requireNonNull(raceInfo, "raceInfo must not be null");
        this.raceTime = RaceTime.parse(raceTime);
    }

    public RaceInfo<G> getRaceInfo() {
        return raceInfo;
    }

    public RaceTime getRaceTime() {
        return raceTime;
    }

    /**
     * 获取项目唯一 key。
     *
     * @return 格式为 {@code gender-group-distance-eventType-stroke}
     */
    public String getKey() {
        return raceInfo.toKey();
    }
}
