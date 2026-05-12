package io.github.murphy955.fina.domain.entity.competition;

import io.github.murphy955.fina.domain.enm.EventType;
import io.github.murphy955.fina.domain.enm.Gender;
import io.github.murphy955.fina.domain.enm.SportClass;
import io.github.murphy955.fina.domain.enm.Stroke;
import io.github.murphy955.fina.domain.vo.Distance;

import java.io.Serializable;
import java.util.Objects;

/**
 * 游泳项目实体
 * <p>
 * 组合泳姿、距离、性别、项目类型及残奥分级（如适用）定义一场比赛项目。
 *
 * @author : 李泽聿
 * @since : 2026:05:12 16:45
 */
public class SwimmingEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private Stroke stroke;

    private Distance distance;

    private Gender gender;

    private EventType eventType;

    private SportClass sportClass;

    public SwimmingEvent() {
    }

    public SwimmingEvent(Stroke stroke, Distance distance, Gender gender,
                         EventType eventType, SportClass sportClass) {
        this.stroke = Objects.requireNonNull(stroke, "stroke must not be null");
        this.distance = Objects.requireNonNull(distance, "distance must not be null");
        this.gender = Objects.requireNonNull(gender, "gender must not be null");
        this.eventType = Objects.requireNonNull(eventType, "eventType must not be null");
        this.sportClass = sportClass;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Stroke getStroke() {
        return stroke;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public SportClass getSportClass() {
        return sportClass;
    }

    public void setSportClass(SportClass sportClass) {
        this.sportClass = sportClass;
    }

    /**
     * 判断是否为接力项目
     */
    public boolean isRelay() {
        return eventType == EventType.RELAY;
    }

    /**
     * 判断是否为残奥项目
     */
    public boolean isParalympic() {
        return sportClass != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SwimmingEvent)) return false;
        SwimmingEvent other = (SwimmingEvent) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SwimmingEvent{" +
                "id='" + id + '\'' +
                ", stroke=" + stroke +
                ", distance=" + distance +
                ", gender=" + gender +
                ", eventType=" + eventType +
                ", sportClass=" + sportClass +
                '}';
    }
}
