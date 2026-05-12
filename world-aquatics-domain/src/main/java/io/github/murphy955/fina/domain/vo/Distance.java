package io.github.murphy955.fina.domain.vo;

import java.io.Serializable;

/**
 * 距离值对象
 * <p>
 * 表示游泳比赛项目的距离，单位：米。
 * 支持标准距离（50m ~ 1500m）及用户自定义距离。
 *
 * @param meters 距离（米），必须为正数
 * @author : 李泽聿
 * @since : 2026:05:12 16:40
 */
public record Distance(int meters) implements Serializable {

    private static final long serialVersionUID = 1L;

    public Distance {
        if (meters <= 0) {
            throw new IllegalArgumentException("Distance must be positive, got: " + meters);
        }
    }

    /**
     * 判断是否为标准个人项目距离
     *
     * @return true 如果距离为 50, 100, 200, 400, 800, 1500 之一
     */
    public boolean isStandardIndividualDistance() {
        return meters == 50 || meters == 100 || meters == 200
                || meters == 400 || meters == 800 || meters == 1500;
    }

    /**
     * 判断是否为标准接力距离
     *
     * @return true 如果距离为 100 (4×25) 或 200 (4×50) 等常见接力距离
     */
    public boolean isStandardRelayDistance() {
        return meters == 100 || meters == 200;
    }

    @Override
    public String toString() {
        return meters + "m";
    }
}
