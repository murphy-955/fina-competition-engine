package io.github.murphy955.fina.domain.entity.athlete;

import io.github.murphy955.fina.domain.enm.RaceResultCodeEnum;
import io.github.murphy955.fina.domain.entity.achievements.RaceTime;

/**
 * 运动员实体类
 *
 * @author : 李泽聿
 * @since : 2026:05:07 14:33
 */
public class Athlete {
    /**
     * 运动员名称
     */
    private String name;

    /**
     * 运动员成绩
     */
    private RaceTime raceTime;

    /**
     * 比赛结果代码（DQ / DNS / DNF / SCR / OK 等）
     * <p>在编排（seeding）时用于区分成绩有效与无效运动员：</p>
     * <ul>
     *     <li>{@code OK} — 参与正常成绩排名</li>
     *     <li>非 {@code OK} — 排至所有 OK 运动员之后</li>
     * </ul>
     */
    private RaceResultCodeEnum resultCode;

    /**
     * 泳道
     */
    private int swimLane;

    /**
     * 小组数目
     */
    private int group;


    public String getName() {
        return name;
    }

    public RaceTime getRaceTime() {
        return raceTime;
    }

    public RaceResultCodeEnum getResultCode() {
        return resultCode;
    }

    public int getSwimLane() {
        return swimLane;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public void setRaceTime(RaceTime raceTime) {
        this.raceTime = raceTime;
    }

    public void setResultCode(RaceResultCodeEnum resultCode) {
        this.resultCode = resultCode;
    }

    public void setSwimLane(int swimLane) {
        this.swimLane = swimLane;
    }

    /**
     * 构造运动员（强制指定结果码）
     *
     * @param name       运动员名称
     * @param raceTime   成绩（可为 null）
     * @param resultCode 比赛结果代码
     */
    public Athlete(String name, RaceTime raceTime, RaceResultCodeEnum resultCode) {
        this.name = name;
        this.raceTime = raceTime;
        this.resultCode = resultCode;
    }

    /**
     * @deprecated 请使用 {@link #Athlete(String, RaceTime, RaceResultCodeEnum)} 显式传入结果码，
     * 以便编排系统正确识别成绩有效/无效状态。
     */
    @Deprecated
    public Athlete(String name, RaceTime raceTime) {
        this(name, raceTime, RaceResultCodeEnum.OK);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Athlete athlete = (Athlete) obj;
        return java.util.Objects.equals(name, athlete.name) &&
                java.util.Objects.equals(raceTime, athlete.raceTime);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, raceTime);
    }

    @Override
    public String toString() {
        return "Athlete{" +
                "name='" + name + '\'' +
                ", raceTime=" + raceTime +
                ", resultCode=" + resultCode +
                ", swimLane=" + swimLane +
                ", group=" + group +
                '}';
    }
}
