package io.github.murphy955.fina.domain.entity.competition;

import io.github.murphy955.fina.domain.enm.TimingSystem;
import io.github.murphy955.fina.domain.entity.achievements.RaceTime;
import io.github.murphy955.fina.domain.entity.athlete.Athlete;

import java.io.Serializable;
import java.util.Objects;

/**
 * 报名实体
 * <p>
 * 描述运动员（或接力队）对某一项目的报名信息，包含报名成绩、成绩来源及编排后的道次/组次。
 *
 * @author : 李泽聿
 * @since : 2026:05:12 17:00
 */
public class Entry implements Serializable {

    private static final long serialVersionUID = 1L;

    private Athlete athlete;

    private SwimmingEvent event;

    private RaceTime entryTime;

    private TimingSystem entryTimeSource;

    private int swimLane;

    private int heatNumber;

    public Entry() {
    }

    public Entry(Athlete athlete, SwimmingEvent event, RaceTime entryTime) {
        this.athlete = Objects.requireNonNull(athlete, "athlete must not be null");
        this.event = Objects.requireNonNull(event, "event must not be null");
        this.entryTime = entryTime;
    }

    public Athlete getAthlete() {
        return athlete;
    }

    public void setAthlete(Athlete athlete) {
        this.athlete = athlete;
    }

    public SwimmingEvent getEvent() {
        return event;
    }

    public void setEvent(SwimmingEvent event) {
        this.event = event;
    }

    public RaceTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(RaceTime entryTime) {
        this.entryTime = entryTime;
    }

    public TimingSystem getEntryTimeSource() {
        return entryTimeSource;
    }

    public void setEntryTimeSource(TimingSystem entryTimeSource) {
        this.entryTimeSource = entryTimeSource;
    }

    public int getSwimLane() {
        return swimLane;
    }

    public void setSwimLane(int swimLane) {
        this.swimLane = swimLane;
    }

    public int getHeatNumber() {
        return heatNumber;
    }

    public void setHeatNumber(int heatNumber) {
        this.heatNumber = heatNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Entry)) return false;
        Entry other = (Entry) obj;
        return Objects.equals(athlete, other.athlete)
                && Objects.equals(event, other.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(athlete, event);
    }

    @Override
    public String toString() {
        return "Entry{" +
                "athlete=" + athlete +
                ", event=" + event +
                ", entryTime=" + entryTime +
                ", entryTimeSource=" + entryTimeSource +
                ", swimLane=" + swimLane +
                ", heatNumber=" + heatNumber +
                '}';
    }
}
