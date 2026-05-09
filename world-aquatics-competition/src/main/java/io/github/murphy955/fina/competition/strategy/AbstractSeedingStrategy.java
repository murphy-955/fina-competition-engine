package io.github.murphy955.fina.competition.strategy;

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
     *     <li>优先安排 {@code raceTime} 不为空的运动员（成绩越小越快）</li>
     *     <li>{@code raceTime} 为空的运动员排在后面，按 {@link Athlete#hashCode()} 排序</li>
     * </ul>
     *
     * @return java.util.Comparator<? super io.github.murphy955.fina.domain.entity.athlete.Athlete>
     */
    public Comparator<? super Athlete> getDefaultComparator() {
        return (a1, a2) -> {
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
