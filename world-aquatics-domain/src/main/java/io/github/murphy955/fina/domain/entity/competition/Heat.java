package io.github.murphy955.fina.domain.entity.competition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 预赛 / 半决赛 / 决赛组实体
 * <p>
 * 描述同一项目内某一组次（Heat）的参赛名单与编排信息。
 *
 * @author : 李泽聿
 * @since : 2026:05:12 16:55
 */
public class Heat implements Serializable {

    private static final long serialVersionUID = 1L;

    private int heatNumber;

    private SwimmingEvent event;

    private List<Entry> entries = new ArrayList<>();

    public Heat() {
    }

    public Heat(int heatNumber, SwimmingEvent event) {
        if (heatNumber <= 0) {
            throw new IllegalArgumentException("heatNumber must be positive");
        }
        this.heatNumber = heatNumber;
        this.event = Objects.requireNonNull(event, "event must not be null");
    }

    public int getHeatNumber() {
        return heatNumber;
    }

    public void setHeatNumber(int heatNumber) {
        this.heatNumber = heatNumber;
    }

    public SwimmingEvent getEvent() {
        return event;
    }

    public void setEvent(SwimmingEvent event) {
        this.event = event;
    }

    public List<Entry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries == null ? new ArrayList<>() : new ArrayList<>(entries);
    }

    public void addEntry(Entry entry) {
        Objects.requireNonNull(entry, "entry must not be null");
        this.entries.add(entry);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Heat)) return false;
        Heat other = (Heat) obj;
        return heatNumber == other.heatNumber
                && Objects.equals(event, other.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(heatNumber, event);
    }

    @Override
    public String toString() {
        return "Heat{" +
                "heatNumber=" + heatNumber +
                ", event=" + event +
                ", entries=" + entries +
                '}';
    }
}
