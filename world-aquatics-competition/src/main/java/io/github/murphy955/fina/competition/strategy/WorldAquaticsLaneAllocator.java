package io.github.murphy955.fina.competition.strategy;

import io.github.murphy955.fina.domain.entity.athlete.Athlete;

import java.util.List;

/**
 * 世界泳联标准泳道分配器。
 * <p>根据 World Aquatics Competition Regulations（2026-02-18）Article 3.2.5.1 分配泳道：</p>
 * <ul>
 *     <li>面对出发端时，第1道在右侧（10泳道池为第0道）</li>
 *     <li>6泳道池：最快→第3道，次快→第4道，然后左右交替</li>
 *     <li>8泳道池：最快→第4道，次快→第5道，然后左右交替</li>
 *     <li>10泳道池：最快→第4道，次快→第5道，然后左右交替</li>
 *     <li>奇数泳道池：最快→中间道，次快→中间道左侧，然后左右交替</li>
 * </ul>
 *
 * @author : 李泽聿
 * @since : 2026:05:08 15:00
 */
public class WorldAquaticsLaneAllocator implements LaneAllocator {

    @Override
    public void apply(List<Athlete> sortedAthletes, int laneCount) {
        int center = computeCenter(laneCount);

        for (int i = 0; i < sortedAthletes.size(); i++) {
            int rank = i + 1; // 1-based rank within the group
            int lane = computeLane(rank, center);
            sortedAthletes.get(i).setSwimLane(lane);
        }
    }

    /**
     * 计算泳道中位数（最快运动员所在泳道）。
     *
     * @param laneCount 泳道数
     * @return int 中位数泳道号
     */
    private int computeCenter(int laneCount) {
        if (laneCount == 10) {
            return 4; // 10泳道池：最快在4道
        }
        // 向上取整：-Math.floorDiv(-a, b)
        return -Math.floorDiv(-laneCount, 2);
    }

    /**
     * 按中心对称规则计算泳道号。
     *
     * @param rank   组内排名（1为最快）
     * @param center 中位数泳道
     * @return int 泳道号
     */
    private int computeLane(int rank, int center) {
        if (rank % 2 == 0) {
            // 偶数排名：中位数右侧
            return center + rank / 2;
        } else {
            // 奇数排名：中位数左侧（含中位数本身）
            return center - rank / 2;
        }
    }
}
