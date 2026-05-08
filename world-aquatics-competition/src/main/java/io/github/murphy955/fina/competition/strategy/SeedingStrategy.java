package io.github.murphy955.fina.competition.strategy;

import io.github.murphy955.fina.domain.entity.athlete.Athlete;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author : 李泽聿
 * @since : 2026:05:07 15:40
 */
public interface SeedingStrategy {
    /**
     * 注意{@code Map<String, List<Athlete>>}中的{@code List<Athlete>}会被<strong>原地排序</strong><br>
     * 如果业务需要保留分组，请提前拷贝一份。
     *
     * @param athletes  运动员列表
     * @param laneCount 泳道数量
     * @author 李泽聿
     * @since 2026-05-07 15:49
     */
    void generateSeeding(Map<String, List<Athlete>> athletes, int laneCount);

    /**
     * 注意{@code Map<String, List<Athlete>>}中的{@code List<Athlete>}会被<strong>原地排序</strong><br>
     * 如果业务需要保留分组，请提前拷贝一份。
     *
     * @param athletes  运动员列表
     * @param laneCount 泳道数量
     * @param sortRule  用户自定义的对{@link Athlete}(主要是成绩)的排序规则，允许为null。如果为null，则使用默认的排序规则。
     * @param laneRule  用户自定义的泳道分配规则。允许为null。如果为null，则使用默认的泳道分配规则。
     * @author 李泽聿
     * @since 2026-05-07 16:47
     */
    void generateSeeding(Map<String, List<Athlete>> athletes, int laneCount, Comparator<? super Athlete> sortRule, LaneAllocator laneRule);
}