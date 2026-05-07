package io.github.murphy955.fina.competition.strategy;

import io.github.murphy955.fina.domain.entity.athlete.Athlete;

import java.util.List;

/**
 *
 *
 * @author : 李泽聿
 * @since : 2026:05:07 16:46
 */
@FunctionalInterface
public interface LaneAllocator {
    void apply(List<Athlete> sortedAthletes, int laneCount);
}
