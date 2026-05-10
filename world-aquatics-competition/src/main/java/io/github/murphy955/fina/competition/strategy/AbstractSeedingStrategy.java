package io.github.murphy955.fina.competition.strategy;

import io.github.murphy955.fina.common.exception.ValidationException;
import io.github.murphy955.fina.domain.enm.RaceResultCodeEnum;
import io.github.murphy955.fina.domain.entity.athlete.Athlete;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 编排策略抽象基类。
 * <p>封装了公共逻辑：排序、分组分配、泳道分配。</p>
 *
 * @author : 李泽聿
 * @since : 2026:05:08 15:00
 */
public abstract class AbstractSeedingStrategy implements SeedingStrategy {

    private final LaneAllocator defaultLaneAllocator = new WorldAquaticsLaneAllocator();

    @Override
    public void generateSeeding(Map<String, List<Athlete>> athletes, int laneCount) {
        generateSeeding(athletes, laneCount, null, null);
    }

    @Override
    public void generateSeeding(Map<String, List<Athlete>> athletes, int laneCount,
                                Comparator<? super Athlete> sortRule, LaneAllocator laneRule) {
        Comparator<? super Athlete> actualSortRule = sortRule != null ? sortRule : getDefaultComparator();
        LaneAllocator actualLaneRule = laneRule != null ? laneRule : defaultLaneAllocator;

        for (List<Athlete> athleteList : athletes.values()) {
            validateAthletes(athleteList);
            athleteList.sort(actualSortRule);
            List<List<Athlete>> groups = distributeIntoGroups(new ArrayList<>(athleteList), laneCount);

            for (int groupIndex = 0; groupIndex < groups.size(); groupIndex++) {
                List<Athlete> group = groups.get(groupIndex);
                group.sort(actualSortRule);
                actualLaneRule.apply(group, laneCount);
                for (Athlete athlete : group) {
                    athlete.setGroup(groupIndex + 1);
                }
            }
        }
    }

    /**
     * 验证运动员列表是否满足编排前置条件。
     * <p>默认实现要求所有运动员的 {@code raceTime} 均不为空，否则抛出
     * {@link ValidationException}。子类可覆盖此方法以放宽或收紧约束。</p>
     *
     * @param athletes 待验证的运动员列表
     */
    protected void validateAthletes(List<Athlete> athletes) {
        for (Athlete athlete : athletes) {
            if (athlete.getRaceTime() == null) {
                throw new ValidationException(
                        "athlete.raceTime must not be null",
                        "Athlete '" + athlete.getName() + "' has no raceTime. " +
                                "Seeding requires a valid race time for all athletes."
                );
            }
        }
    }

    /**
     * 将已排序的运动员分配到各个小组。
     *
     * @param sorted    按成绩从快到慢排序的运动员列表
     * @param laneCount 泳道数
     * @return 小组列表，索引0为第1组
     */
    protected abstract List<List<Athlete>> distributeIntoGroups(List<Athlete> sorted, int laneCount);

    /**
     * 默认排序规则：
     * <ul>
     *     <li>优先按 {@link RaceResultCodeEnum#getSortOrder()} 排序，{@code OK} 始终在前</li>
     *     <li>结果码相同的运动员按 {@code raceTime} 排序（成绩越小越快）</li>
     *     <li>{@code raceTime} 为空的运动员排在后面，按 {@link Athlete#hashCode()} 排序</li>
     * </ul>
     *
     * @return java.util.Comparator<? super io.github.murphy955.fina.domain.entity.athlete.Athlete>
     */
    public Comparator<? super Athlete> getDefaultComparator() {
        return (a1, a2) -> {
            // 1. 先按结果码排序：OK 在前，非 OK 按 sortOrder 升序排在后面
            int sortOrder1 = a1.getResultCode() != null ? a1.getResultCode().getSortOrder() : 0;
            int sortOrder2 = a2.getResultCode() != null ? a2.getResultCode().getSortOrder() : 0;
            int resultCodeCompare = Integer.compare(sortOrder1, sortOrder2);
            if (resultCodeCompare != 0) {
                return resultCodeCompare;
            }

            // 2. 结果码相同，按成绩排序
            boolean a1HasTime = a1.getRaceTime() != null;
            boolean a2HasTime = a2.getRaceTime() != null;

            if (a1HasTime && a2HasTime) {
                return a1.getRaceTime().compareTo(a2.getRaceTime());
            } else if (a1HasTime) {
                return -1;
            } else if (a2HasTime) {
                return 1;
            } else {
                return Integer.compare(a1.hashCode(), a2.hashCode());
            }
        };
    }
}
