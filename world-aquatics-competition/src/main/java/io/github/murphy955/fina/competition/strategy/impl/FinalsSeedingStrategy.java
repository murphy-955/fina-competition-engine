package io.github.murphy955.fina.competition.strategy.impl;

import io.github.murphy955.fina.competition.strategy.AbstractSeedingStrategy;
import io.github.murphy955.fina.domain.entity.athlete.Athlete;

import java.util.ArrayList;
import java.util.List;

/**
 * 决赛编排策略。
 * <p>根据 World Aquatics Competition Regulations Article 3.4：</p>
 * <ul>
 *     <li>不分组，所有运动员在同一决赛</li>
 *     <li>按半决赛成绩（或报名成绩）分配泳道</li>
 * </ul>
 *
 * @author : 李泽聿
 * @since : 2026:05:08 15:00
 */
public class FinalsSeedingStrategy extends AbstractSeedingStrategy {

    @Override
    protected List<List<Athlete>> distributeIntoGroups(List<Athlete> sorted, int laneCount) {
        List<List<Athlete>> groups = new ArrayList<>();
        groups.add(new ArrayList<>(sorted)); // 全部放入第1组（Final）
        return groups;
    }
}
