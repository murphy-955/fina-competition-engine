package io.github.murphy955.fina.competition.strategy.impl;

import io.github.murphy955.fina.competition.strategy.AbstractSeedingStrategy;
import io.github.murphy955.fina.domain.entity.athlete.Athlete;

import java.util.ArrayList;
import java.util.List;

/**
 * 半决赛编排策略。
 * <p>根据 World Aquatics Competition Regulations Article 3.3.1：</p>
 * <ul>
 *     <li>固定分为2组（Semi-final 1 和 Semi-final 2）</li>
 *     <li>按预赛成绩，最快→第2场半决赛，次快→第1场半决赛，然后快慢交替</li>
 * </ul>
 *
 * @author : 李泽聿
 * @since : 2026:05:08 15:00
 */
public class SemiFinalsSeedingStrategy extends AbstractSeedingStrategy {

    @Override
    protected List<List<Athlete>> distributeIntoGroups(List<Athlete> sorted, int laneCount) {
        List<List<Athlete>> groups = new ArrayList<>();
        groups.add(new ArrayList<>()); // Semi-final 1
        groups.add(new ArrayList<>()); // Semi-final 2

        for (int i = 0; i < sorted.size(); i++) {
            int groupIndex = (i % 2 == 0) ? 1 : 0; // 最快→第2组
            groups.get(groupIndex).add(sorted.get(i));
        }

        return groups;
    }
}
