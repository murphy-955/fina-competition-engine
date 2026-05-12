package io.github.murphy955.fina.domain.entity.competition;

import io.github.murphy955.fina.domain.enm.CompetitionFormat;
import io.github.murphy955.fina.domain.vo.PoolConfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 赛事实体
 * <p>
 * 描述一场游泳比赛的整体信息，包含赛程、泳池配置及赛制模式。
 *
 * @author : 李泽聿
 * @since : 2026:05:12 16:50
 */
public class Meet implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private PoolConfiguration pool;

    private CompetitionFormat format;

    private List<SwimmingEvent> events = new ArrayList<>();

    public Meet() {
    }

    public Meet(String name, PoolConfiguration pool, CompetitionFormat format) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.pool = Objects.requireNonNull(pool, "pool must not be null");
        this.format = Objects.requireNonNull(format, "format must not be null");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PoolConfiguration getPool() {
        return pool;
    }

    public void setPool(PoolConfiguration pool) {
        this.pool = pool;
    }

    public CompetitionFormat getFormat() {
        return format;
    }

    public void setFormat(CompetitionFormat format) {
        this.format = format;
    }

    public List<SwimmingEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public void setEvents(List<SwimmingEvent> events) {
        this.events = events == null ? new ArrayList<>() : new ArrayList<>(events);
    }

    public void addEvent(SwimmingEvent event) {
        Objects.requireNonNull(event, "event must not be null");
        this.events.add(event);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Meet)) return false;
        Meet other = (Meet) obj;
        return Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Meet{" +
                "name='" + name + '\'' +
                ", pool=" + pool +
                ", format=" + format +
                ", events=" + events +
                '}';
    }
}
