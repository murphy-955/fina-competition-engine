package io.github.murphy955.fina.domain.vo;

import java.io.Serializable;

/**
 * 泳池配置值对象
 * <p>
 * 描述比赛场地的泳池参数：长度与泳道数。
 *
 * @param lengthMeters 泳池长度（米），如 50 或 25
 * @param laneCount    泳道数，如 6、8、10
 * @author : 李泽聿
 * @since : 2026:05:12 16:42
 */
public record PoolConfiguration(int lengthMeters, int laneCount) implements Serializable {

    private static final long serialVersionUID = 1L;

    public PoolConfiguration {
        if (lengthMeters <= 0) {
            throw new IllegalArgumentException("Pool length must be positive, got: " + lengthMeters);
        }
        if (laneCount <= 0) {
            throw new IllegalArgumentException("Lane count must be positive, got: " + laneCount);
        }
    }

    /**
     * 默认 50m 长池，8 泳道
     */
    public static PoolConfiguration defaultPool() {
        return new PoolConfiguration(50, 8);
    }

    /**
     * 判断是否为长池（50m）
     */
    public boolean isLongCourse() {
        return lengthMeters == 50;
    }

    /**
     * 判断是否为短池（25m）
     */
    public boolean isShortCourse() {
        return lengthMeters == 25;
    }

    @Override
    public String toString() {
        return lengthMeters + "m pool, " + laneCount + " lanes";
    }
}
