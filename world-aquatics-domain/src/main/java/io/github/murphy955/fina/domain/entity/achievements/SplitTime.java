package io.github.murphy955.fina.domain.entity.achievements;

import java.util.Objects;

/**
 * 分段成绩
 * <p>
 * 用于记录游泳比赛中某一距离标记点的时间数据，包含：
 * <ul>
 *     <li>{@code distance} — 距离标记点（如 50m、100m、150m）</li>
 *     <li>{@code splitTime} — 该分段用时（从上一个标记点到当前点的用时）</li>
 *     <li>{@code cumulativeTime} — 累计用时（从起跳到当前点的总用时）</li>
 * </ul>
 *
 * @author : 李泽聿
 * @since : 2026:05:12 16:00
 */
public class SplitTime {

    /**
     * 距离标记点（单位：米）
     * <p>例如：50 表示 50 米点，100 表示 100 米点</p>
     */
    private final int distance;

    /**
     * 该分段用时（差分时间）
     * <p>从上一个距离标记点到当前距离标记点的用时</p>
     */
    private final RaceTime splitTime;

    /**
     * 累计用时
     * <p>从起跳开始到当前距离标记点的总用时</p>
     */
    private final RaceTime cumulativeTime;

    /**
     * 构造分段成绩
     *
     * @param distance      距离标记点（米）
     * @param splitTime     该分段用时
     * @param cumulativeTime 累计用时
     */
    public SplitTime(int distance, RaceTime splitTime, RaceTime cumulativeTime) {
        if (distance <= 0) {
            throw new IllegalArgumentException("Distance must be positive");
        }
        this.distance = distance;
        this.splitTime = Objects.requireNonNull(splitTime, "splitTime must not be null");
        this.cumulativeTime = Objects.requireNonNull(cumulativeTime, "cumulativeTime must not be null");
    }

    /**
     * 构造分段成绩（仅累计用时）
     * <p>适用于只有累计时间数据的场景，{@code splitTime} 将设为 {@code null}</p>
     *
     * @param distance       距离标记点（米）
     * @param cumulativeTime 累计用时
     */
    public SplitTime(int distance, RaceTime cumulativeTime) {
        if (distance <= 0) {
            throw new IllegalArgumentException("Distance must be positive");
        }
        this.distance = distance;
        this.splitTime = null;
        this.cumulativeTime = Objects.requireNonNull(cumulativeTime, "cumulativeTime must not be null");
    }

    public int getDistance() {
        return distance;
    }

    public RaceTime getSplitTime() {
        return splitTime;
    }

    public RaceTime getCumulativeTime() {
        return cumulativeTime;
    }

    /**
     * 判断该分段是否包含差分时间
     *
     * @return true 如果 {@code splitTime} 不为 null
     */
    public boolean hasSplitTime() {
        return splitTime != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SplitTime)) {
            return false;
        }
        SplitTime other = (SplitTime) obj;
        return this.distance == other.distance
                && Objects.equals(this.splitTime, other.splitTime)
                && Objects.equals(this.cumulativeTime, other.cumulativeTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance, splitTime, cumulativeTime);
    }

    @Override
    public String toString() {
        return "SplitTime{" +
                "distance=" + distance +
                ", splitTime=" + splitTime +
                ", cumulativeTime=" + cumulativeTime +
                '}';
    }
}
