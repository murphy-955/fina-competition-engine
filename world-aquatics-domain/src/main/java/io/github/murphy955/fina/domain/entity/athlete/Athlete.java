package io.github.murphy955.fina.domain.entity.athlete;

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

    public void setSwimLane(int swimLane) {
        this.swimLane = swimLane;
    }

    public Athlete(String name, RaceTime raceTime) {
        this.name = name;
        this.raceTime = raceTime;
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
        return swimLane == athlete.swimLane &&
                group == athlete.group &&
                java.util.Objects.equals(name, athlete.name) &&
                java.util.Objects.equals(raceTime, athlete.raceTime);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, raceTime, swimLane, group);
    }

    @Override
    public String toString() {
        return "Athlete{" +
                "name='" + name + '\'' +
                ", raceTime=" + raceTime +
                ", swimLane=" + swimLane +
                ", group=" + group +
                '}';
    }
}
