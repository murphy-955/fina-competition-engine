package io.github.murphy955.fina.domain.entity.project;

import io.github.murphy955.fina.domain.entity.achievements.Result;
import io.github.murphy955.fina.domain.entity.athlete.Athlete;
import io.github.murphy955.fina.domain.shared.BaseGroup;
import io.github.murphy955.fina.domain.vo.RaceInfo;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * 比赛单项（Project）
 * <p>
 * 描述运动员在一次具体项目中的参赛信息，包含运动员、成绩及项目维度。
 * 项目维度由 {@link RaceInfo} 统一封装。
 * </p>
 *
 * @param <G> 组别类型，须为枚举且实现 {@link BaseGroup}
 * @author : 李泽聿
 * @since : 2026:05:13 14:22
 */
public class Project<G extends Enum<G> & BaseGroup> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Athlete athlete;

    private Result result;

    private RaceInfo<G> raceInfo;

    public Project(Athlete athlete, Result result, RaceInfo<G> raceInfo) {
        this.athlete = athlete;
        this.result = result;
        this.raceInfo = Objects.requireNonNull(raceInfo, "raceInfo must not be null");
    }

    public Project() {
    }

    public Athlete getAthlete() {
        return athlete;
    }

    public void setAthlete(Athlete athlete) {
        this.athlete = athlete;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public RaceInfo<G> getRaceInfo() {
        return raceInfo;
    }

    public void setRaceInfo(RaceInfo<G> raceInfo) {
        this.raceInfo = raceInfo;
    }

    /**
     * 获取项目唯一 key。
     *
     * @return 格式为 {@code gender-group-distance-eventType-stroke}
     */
    public String getKey() {
        return raceInfo != null ? raceInfo.toKey() : null;
    }
}
