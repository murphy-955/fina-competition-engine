package io.github.murphy955.fina.competition.strategy.impl;

import io.github.murphy955.fina.competition.strategy.AbstractSeedingStrategy;
import io.github.murphy955.fina.domain.entity.athlete.Athlete;

import java.util.ArrayList;
import java.util.List;

/**
 * 预赛编排策略。
 * <p>根据 World Aquatics Competition Regulations 预赛分组规则：</p>
 * <ul>
 *     <li>1组（≤泳道数）：直接作为决赛</li>
 *     <li>2组：最快→第2组，次快→第1组，交替分配</li>
 *     <li>3组（400/800/1500米除外）：最快→第3组，次快→第2组，第三快→第1组，循环分配</li>
 *     <li>4组及以上（400/800/1500米除外）：最后3组按3组规则，前面的组依次填充</li>
 *     <li>400/800/1500米：最后2组按2组规则，前面的组依次填充</li>
 * </ul>
 *
 * @author : 李泽聿
 * @since : 2026:05:08 15:00
 */
public class HeatsSeedingStrategy extends AbstractSeedingStrategy {

    private final boolean longDistance;

    public HeatsSeedingStrategy() {
        this(false);
    }

    /**
     * @param longDistance 是否为400m/800m/1500m长距离项目
     */
    public HeatsSeedingStrategy(boolean longDistance) {
        this.longDistance = longDistance;
    }

    @Override
    protected List<List<Athlete>> distributeIntoGroups(List<Athlete> sorted, int laneCount) {
        int totalAthletes = sorted.size();
        int totalGroups = (totalAthletes + laneCount - 1) / laneCount;

        List<List<Athlete>> groups = new ArrayList<>();
        for (int i = 0; i < totalGroups; i++) {
            groups.add(new ArrayList<>());
        }

        if (totalGroups == 1) {
            groups.get(0).addAll(sorted);
        } else if (totalGroups == 2) {
            distributeTwoGroups(sorted, groups);
        } else if (totalGroups == 3 && !longDistance) {
            distributeThreeGroups(sorted, groups);
        } else {
            if (longDistance) {
                distributeLongDistance(sorted, groups, laneCount, totalGroups);
            } else {
                distributeFourOrMoreGroups(sorted, groups, laneCount, totalGroups);
            }
        }

        return groups;
    }

    private void distributeTwoGroups(List<Athlete> sorted, List<List<Athlete>> groups) {
        for (int i = 0; i < sorted.size(); i++) {
            int groupIndex = (i % 2 == 0) ? 1 : 0; // 最快→第2组
            groups.get(groupIndex).add(sorted.get(i));
        }
    }

    private void distributeThreeGroups(List<Athlete> sorted, List<List<Athlete>> groups) {
        for (int i = 0; i < sorted.size(); i++) {
            int groupIndex = 2 - (i % 3); // 0→2, 1→1, 2→0
            groups.get(groupIndex).add(sorted.get(i));
        }
    }

    private void distributeFourOrMoreGroups(List<Athlete> sorted, List<List<Athlete>> groups,
                                             int laneCount, int totalGroups) {
        int athleteIndex = 0;

        // 最后3组按3组规则循环分配
        for (int i = 0; athleteIndex < sorted.size() && i < laneCount * 3; i++) {
            int groupIndex = (totalGroups - 3) + (2 - (i % 3));
            groups.get(groupIndex).add(sorted.get(athleteIndex++));
        }

        // 前面的组从倒数第4组往前，每组按顺序填满
        for (int g = totalGroups - 4; g >= 0 && athleteIndex < sorted.size(); g--) {
            while (groups.get(g).size() < laneCount && athleteIndex < sorted.size()) {
                groups.get(g).add(sorted.get(athleteIndex++));
            }
        }
    }

    private void distributeLongDistance(List<Athlete> sorted, List<List<Athlete>> groups,
                                         int laneCount, int totalGroups) {
        int athleteIndex = 0;

        // 最后2组按2组规则循环分配
        for (int i = 0; athleteIndex < sorted.size() && i < laneCount * 2; i++) {
            int groupIndex = (totalGroups - 2) + ((i % 2 == 0) ? 1 : 0);
            groups.get(groupIndex).add(sorted.get(athleteIndex++));
        }

        // 前面的组从倒数第3组往前，每组按顺序填满
        for (int g = totalGroups - 3; g >= 0 && athleteIndex < sorted.size(); g--) {
            while (groups.get(g).size() < laneCount && athleteIndex < sorted.size()) {
                groups.get(g).add(sorted.get(athleteIndex++));
            }
        }
    }
}
