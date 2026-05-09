package io.github.murphy955.fina.competition.strategy.impl;

import io.github.murphy955.fina.competition.strategy.AbstractSeedingStrategy;
import io.github.murphy955.fina.domain.entity.athlete.Athlete;

import java.util.ArrayList;
import java.util.List;

/**
 * 报名成绩编排策略。
 * <p>根据运动员报名成绩进行标准蛇形分组：</p>
 * <ul>
 *     <li>运动员按报名成绩从快到慢排序（{@code raceTime} 为空者排在后面）</li>
 *     <li>最快的运动员分配到最后一组，次快的分配到倒数第二组，依次循环</li>
 *     <li>每组人数不超过泳道数</li>
 * </ul>
 * <p>该策略支持用户自定义 {@code sortRule} 和 {@code laneRule}，
 * 通过 {@link io.github.murphy955.fina.competition.strategy.SeedingStrategy#generateSeeding(
 *      java.util.Map, int, java.util.Comparator,
 *      io.github.murphy955.fina.competition.strategy.LaneAllocator)} 传入即可。</p>
 *
 * @author : 李泽聿
 * @since : 2026:05:09
 */
public class EntryTimeSeedingStrategy extends AbstractSeedingStrategy {

    @Override
    protected void validateAthletes(List<Athlete> athletes) {
        // 报名成绩编排允许 raceTime 为空，不做校验
    }

    @Override
    protected List<List<Athlete>> distributeIntoGroups(List<Athlete> sorted, int laneCount) {
        int totalAthletes = sorted.size();
        int totalGroups = (totalAthletes + laneCount - 1) / laneCount;

        List<List<Athlete>> groups = new ArrayList<>();
        for (int i = 0; i < totalGroups; i++) {
            groups.add(new ArrayList<>());
        }

        // 蛇形分配：最快→最后一组，次快→倒数第二组，循环
        for (int i = 0; i < sorted.size(); i++) {
            int groupIndex = (totalGroups - 1) - (i % totalGroups);
            groups.get(groupIndex).add(sorted.get(i));
        }

        return groups;
    }
}
