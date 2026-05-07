package io.github.murphy955.fina.competition.strategy.impl;

import io.github.murphy955.fina.competition.strategy.LaneAllocator;
import io.github.murphy955.fina.competition.strategy.SeedingStrategy;
import io.github.murphy955.fina.domain.entity.athlete.Athlete;

import java.util.Comparator;
import java.util.List;

/**
 * 标准编排道次实现
 * 具体规则：
 * <ul>
 *     <li>1.优先根据成绩排序，</li>
 * </ul>
 *
 * @author : 李泽聿
 * @since : 2026:05:07 15:44
 */
public class StandardSeedingStrategyImpl implements SeedingStrategy {
    /**
     * @param athletes      运动员列表
     * @param numberOfLanes 泳道数
     * @author 李泽聿
     * @since 2026-05-07 15:58
     */
    @Override
    public void generateSeeding(List<Athlete> athletes, int numberOfLanes) {
        generateSeeding(athletes, numberOfLanes, getDefaultComparator(), new DefaultLaneAllocator());
    }

    @Override
    public void generateSeeding(List<Athlete> athletes, int laneCount, Comparator<? super Athlete> sortRule, LaneAllocator laneRule) {
        Comparator<? super Athlete> actualSortRule = sortRule != null ? sortRule : getDefaultComparator();
        athletes.sort(actualSortRule);

        LaneAllocator actualLaneRule = laneRule != null ? laneRule : new DefaultLaneAllocator();
        actualLaneRule.apply(athletes, laneCount);
    }

    /**
     * 默认排序规则。
     * <ul>
     *     <li>1.优先根据成绩排序。{@link Athlete}的{@code raceTime}字段越小，越靠近列表头部</li>
     *     <li>2.如果成绩相同按照运动员在列表中的顺序排序</li>
     * </ul>
     *
     * @return java.util.Comparator<? super io.github.murphy955.fina.domain.entity.athlete.Athlete>
     * @author 李泽聿
     * @since 2026-05-07 16:50
     */
    Comparator<? super Athlete> getDefaultComparator() {
        return Comparator.comparing(Athlete::getRaceTime);
    }

    static class DefaultLaneAllocator implements LaneAllocator {

        /**
         * @param sortedAthletes 已按种子顺序排列的运动员（索引0为头号种子）
         * @param laneCount      泳道数
         * @author 李泽聿
         * @since 2026-05-07 16:58
         */
        @Override
        public void apply(List<Athlete> sortedAthletes, int laneCount) {
            int size = sortedAthletes.size();
            int totalGroups = (size + laneCount - 1) / laneCount;

            for (int i = 0; i < totalGroups; i++) {
                for (int j = 1; j <= laneCount; j++) {
                    int index = i * laneCount + j - 1;
                    if (index >= size) {
                        break;
                    }
                    sortedAthletes.get(index).setGroup(i + 1);
                    int center = laneCount == 10 ? 4 : -Math.floorDiv(-laneCount, 2);
                    int swimLane = getSwimLane(j, center);
                    sortedAthletes.get(index).setSwimLane(swimLane);
                }
            }
        }

        /**
         * 按如下规则分配泳道：
         * <ul>
         *     <li>1.如果当前小组的序号为偶数，则将当前小组的泳道数设置为中位数的右侧</li>
         *     <li>2.如果当前小组的序数为奇数，则将当前小组的泳道数设置为中位数的左侧</li>
         *     <li>3.中位数：中位数 = 泳道数 / 2 向上取整</li>
         *  </ul>
         *
         * @param lane   当期小组的名次
         * @param center 泳道中位数（向上取整）
         * @return int
         * @author 李泽聿
         * @since 2026-05-07 17:04
         */
        private int getSwimLane(int lane, int center) {
            int res;
            // 偶数，应当在中位数的右侧
            if (lane % 2 == 0) {
                res = center + lane / 2;
            } else {
                res = center - lane / 2;
            }
            return res;
        }
    }
}
