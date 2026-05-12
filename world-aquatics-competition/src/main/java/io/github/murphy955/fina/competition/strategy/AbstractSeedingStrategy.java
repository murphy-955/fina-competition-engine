package io.github.murphy955.fina.competition.strategy;

import io.github.murphy955.fina.common.exception.ValidationException;
import io.github.murphy955.fina.domain.entity.athlete.Athlete;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 编排策略抽象基类。
 * <p>
 * 封装了编排流程的公共逻辑：验证 → 排序 → 分组分配 → 泳道分配。
 * 子类只需实现 {@link #distributeIntoGroups(List, int)} 定义不同的分组策略。
 * </p>
 * <p>
 * <strong>默认排序规则：</strong>
 * <ul>
 *     <li>{@code resultCode == OK} 的运动员按 {@code raceTime} 从快到慢排序</li>
 *     <li>{@code resultCode != OK} 的运动员（DQ、DNS、DNF、SCR 等）统一置于队尾，
 *         内部保持原始顺序，不参与成绩排序</li>
 * </ul>
 * 该规则确保无效成绩不会干扰正常编排，且非 OK 运动员之间的相对顺序与报名顺序一致。
 * </p>
 *
 * @author : 李泽聿
 * @since : 2026:05:08 15:00
 * @see SeedingStrategy
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
     * 将已排序的运动员分配到各个小组（Heat）。
     * <p>传入的 {@code sorted} 列表已按默认规则排序：OK 运动员在前且按成绩排列，
     * 非 OK 运动员在队尾保持原始顺序。</p>
     *
     * @param sorted    按成绩从快到慢排序的运动员列表（OK 在前，非 OK 在尾）
     * @param laneCount 泳道数
     * @return 小组列表，索引 0 为第 1 组
     */
    protected abstract List<List<Athlete>> distributeIntoGroups(List<Athlete> sorted, int laneCount);

    /**
     * 获取默认编排排序比较器。
     * <p>
     * 排序逻辑分三层：
     * </p>
     * <ol>
     *     <li><strong>资格分离：</strong>{@code resultCode == OK} 的运动员始终排在
     *         {@code resultCode != OK} 的运动员之前</li>
     *     <li><strong>有效成绩排序：</strong>均为 OK 时，按 {@code raceTime} 从小到大排序
     *         （时间越短越快）。{@code raceTime} 为空的排在有成绩的后面</li>
     *     <li><strong>无效成绩保持原序：</strong>均为非 OK 时，比较器返回 0，
     *         利用 {@link List#sort} 的稳定性（TimSort）保持原始报名顺序</li>
     * </ol>
     *
     * @return 默认编排排序比较器
     */
    public Comparator<? super Athlete> getDefaultComparator() {
        return (a1, a2) -> {
            boolean a1Ok = a1.getResultCode() != null && a1.getResultCode().isQualified();
            boolean a2Ok = a2.getResultCode() != null && a2.getResultCode().isQualified();

            // 1. 资格分离：OK 在前，非 OK 置队尾
            if (a1Ok && !a2Ok) {
                return -1;
            }
            if (!a1Ok && a2Ok) {
                return 1;
            }

            // 2. 均为非 OK：按结果码 sortOrder 排序（DQ → DNS → DNF → SCR → DSQ）
            int compare = Integer.compare(a1.hashCode(), a2.hashCode());
            if (!a1Ok) {
                int sortOrder1 = a1.getResultCode() != null ? a1.getResultCode().getSortOrder() : 0;
                int sortOrder2 = a2.getResultCode() != null ? a2.getResultCode().getSortOrder() : 0;
                int sortCompare = Integer.compare(sortOrder1, sortOrder2);
                if (sortCompare != 0) {
                    return sortCompare;
                }
                // 同 sortOrder 时按 hashCode 排序以保持确定性
                return compare;
            }

            // 3. 均为 OK：按成绩排序（越小越快）
            boolean a1HasTime = a1.getRaceTime() != null;
            boolean a2HasTime = a2.getRaceTime() != null;

            if (a1HasTime && a2HasTime) {
                return a1.getRaceTime().compareTo(a2.getRaceTime());
            } else if (a1HasTime) {
                return -1;
            } else if (a2HasTime) {
                return 1;
            } else {
                // 成绩均为空时，按 hashCode 排序以保持确定性
                return compare;
            }
        };
    }
}
