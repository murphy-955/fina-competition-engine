package io.github.murphy955.fina.competition.strategy;

import io.github.murphy955.fina.competition.strategy.impl.FinalsSeedingStrategy;
import io.github.murphy955.fina.competition.strategy.impl.HeatsSeedingStrategy;
import io.github.murphy955.fina.competition.strategy.impl.SemiFinalsSeedingStrategy;
import io.github.murphy955.fina.domain.entity.athlete.Athlete;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 编排策略接口。
 * <p>
 * 定义将运动员按成绩排序并分配到各组（Heat）和泳道（Lane）的标准契约。
 * 实现类须遵循 World Aquatics Competition Regulations（2026-02-18）的分组与道次分配规则。
 * </p>
 * <p>
 * <strong>排序保证：</strong>默认排序规则下，仅对 {@code resultCode == OK} 的运动员按成绩排序；
 * {@code resultCode != OK} 的运动员（如 DQ、DNS、DNF 等）统一置于队尾，不参与成绩排序。
 * </p>
 *
 * @author : 李泽聿
 * @since : 2026:05:07 15:40
 * @see AbstractSeedingStrategy
 * @see HeatsSeedingStrategy
 * @see SemiFinalsSeedingStrategy
 * @see FinalsSeedingStrategy
 */
public interface SeedingStrategy {

    /**
     * 执行编排。
     * <p>
     * 使用默认排序规则（OK 运动员按成绩排序，非 OK 置队尾）和默认泳道分配规则。
     * </p>
     * <p>
     * <strong>注意：</strong>{@code Map<String, List<Athlete>>} 中的 {@code List<Athlete>} 会被
     * <strong>原地排序</strong>。如果业务需要保留原始分组顺序，请提前拷贝一份。
     * </p>
     *
     * @param athletes  按项目 Key 分组的运动员列表
     * @param laneCount 泳道数量
     * @author 李泽聿
     * @since 2026-05-07 15:49
     */
    void generateSeeding(Map<String, List<Athlete>> athletes, int laneCount);

    /**
     * 执行编排（自定义排序与泳道分配）。
     * <p>
     * 允许调用方传入自定义排序规则和泳道分配规则。若传入 {@code null}，则使用默认实现。
     * </p>
     * <p>
     * <strong>注意：</strong>{@code Map<String, List<Athlete>>} 中的 {@code List<Athlete>} 会被
     * <strong>原地排序</strong>。如果业务需要保留原始分组顺序，请提前拷贝一份。
     * </p>
     *
     * @param athletes  按项目 Key 分组的运动员列表
     * @param laneCount 泳道数量
     * @param sortRule  用户自定义的运动员排序规则。允许为 {@code null}，此时使用默认规则：
     *                  仅对 {@code resultCode == OK} 的运动员按成绩排序，非 OK 运动员置队尾。
     * @param laneRule  用户自定义的泳道分配规则。允许为 {@code null}，此时使用
     *                  {@link WorldAquaticsLaneAllocator}。
     * @author 李泽聿
     * @since 2026-05-07 16:47
     */
    void generateSeeding(Map<String, List<Athlete>> athletes, int laneCount,
                         Comparator<? super Athlete> sortRule, LaneAllocator laneRule);
}
