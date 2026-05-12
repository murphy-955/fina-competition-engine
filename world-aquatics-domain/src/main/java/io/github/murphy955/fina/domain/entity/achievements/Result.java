package io.github.murphy955.fina.domain.entity.achievements;

import io.github.murphy955.fina.domain.enm.TimingSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 比赛成绩结果实体
 * <p>
 * 封装运动员在单场比赛中的完整成绩数据，包括：
 * <ul>
 *     <li>{@code totalTime} — 总成绩（完赛时间）</li>
 *     <li>{@code splits} — 分段成绩列表（按距离升序排列）</li>
 *     <li>{@code reactionTime} — 起跳反应时间（可选）</li>
 * </ul>
 *
 * @author : 李泽聿
 * @since : 2026:05:12 16:05
 */
public class Result {

    /**
     * 总成绩（完赛时间）
     */
    private RaceTime totalTime;

    /**
     * 分段成绩列表
     * <p>按距离标记点升序排列，如 50m、100m、150m...</p>
     */
    private List<SplitTime> splits;

    /**
     * 起跳反应时间（可选）
     * <p>出发台上起跳反应时间，部分比赛会记录此数据</p>
     */
    private RaceTime reactionTime;

    /**
     * 计时系统来源
     * <p>电子计时 / 半自动计时 / 手动计时</p>
     */
    private TimingSystem timingSystem;

    public Result() {
        this.splits = new ArrayList<>();
    }

    public Result(RaceTime totalTime) {
        this.totalTime = Objects.requireNonNull(totalTime, "totalTime must not be null");
        this.splits = new ArrayList<>();
    }

    public Result(RaceTime totalTime, List<SplitTime> splits) {
        this.totalTime = Objects.requireNonNull(totalTime, "totalTime must not be null");
        this.splits = splits == null ? new ArrayList<>() : new ArrayList<>(splits);
    }

    public RaceTime getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(RaceTime totalTime) {
        this.totalTime = totalTime;
    }

    public List<SplitTime> getSplits() {
        return Collections.unmodifiableList(splits);
    }

    public void setSplits(List<SplitTime> splits) {
        this.splits = splits == null ? new ArrayList<>() : new ArrayList<>(splits);
    }

    /**
     * 添加一个分段成绩
     *
     * @param split 分段成绩
     */
    public void addSplit(SplitTime split) {
        Objects.requireNonNull(split, "split must not be null");
        this.splits.add(split);
    }

    public Optional<RaceTime> getReactionTime() {
        return Optional.ofNullable(reactionTime);
    }

    public void setReactionTime(RaceTime reactionTime) {
        this.reactionTime = reactionTime;
    }

    public TimingSystem getTimingSystem() {
        return timingSystem;
    }

    public void setTimingSystem(TimingSystem timingSystem) {
        this.timingSystem = timingSystem;
    }

    /**
     * 获取指定距离标记点的分段数据
     *
     * @param distance 距离标记点（米）
     * @return 对应的分段成绩，不存在则返回 {@link Optional#empty()}
     */
    public Optional<SplitTime> getSplitAt(int distance) {
        return splits.stream()
                .filter(s -> s.getDistance() == distance)
                .findFirst();
    }

    /**
     * 判断是否存在分段成绩
     *
     * @return true 如果存在至少一个分段成绩
     */
    public boolean hasSplits() {
        return !splits.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Result)) {
            return false;
        }
        Result other = (Result) obj;
        return Objects.equals(this.totalTime, other.totalTime)
                && Objects.equals(this.splits, other.splits)
                && Objects.equals(this.reactionTime, other.reactionTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalTime, splits, reactionTime);
    }

    @Override
    public String toString() {
        return "Result{" +
                "totalTime=" + totalTime +
                ", splits=" + splits +
                ", reactionTime=" + reactionTime +
                ", timingSystem=" + timingSystem +
                '}';
    }
}
