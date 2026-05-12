package io.github.murphy955.fina.competition.strategy;

import io.github.murphy955.fina.domain.entity.athlete.Athlete;

import java.util.List;

/**
 * 泳道分配策略接口。
 * <p>
 * 定义将已按成绩排序的组内运动员分配到具体泳道的算法。
 * 引擎内置 {@link WorldAquaticsLaneAllocator} 实现世界泳联标准道次分配规则，
 * 用户可通过实现此接口自定义道次分配逻辑（如随机分配、按报名顺序分配等）。
 * </p>
 *
 * @author : 李泽聿
 * @since : 2026:05:07 16:46
 * @see WorldAquaticsLaneAllocator
 */
@FunctionalInterface
public interface LaneAllocator {

    /**
     * 将已排序的运动员分配到泳道。
     * <p>
     * 传入的 {@code sortedAthletes} 已按编排策略排序（通常成绩最好的排在索引 0），
     * 实现类须将每位运动员的泳道号写入 {@link Athlete#setSwimLane(int)}。
     * </p>
     *
     * @param sortedAthletes 已按成绩排序的组内运动员列表（OK 运动员在前且按成绩排列，非 OK 运动员在尾）
     * @param laneCount      泳池泳道总数
     */
    void apply(List<Athlete> sortedAthletes, int laneCount);
}
