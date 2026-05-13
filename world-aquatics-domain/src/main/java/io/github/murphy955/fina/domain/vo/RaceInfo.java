package io.github.murphy955.fina.domain.vo;

import io.github.murphy955.fina.domain.enm.EventType;
import io.github.murphy955.fina.domain.enm.Gender;
import io.github.murphy955.fina.domain.enm.Stroke;
import io.github.murphy955.fina.domain.shared.BaseGroup;

import java.io.Serializable;
import java.util.Objects;

/**
 * 比赛项目维度值对象。
 * <p>
 * 封装定位一个具体比赛项目所需的全部维度：性别、组别、距离、项目类型、泳姿。
 * 用于生成项目唯一 key、纪录查询、编排分组等场景，避免在方法签名中反复罗列五个参数。
 * </p>
 * <p>
 * Key 格式：{@code gender-group-distance-eventType-stroke}<br>
 * 示例：{@code MALE-U18-100-INDIVIDUAL-FREESTYLE}
 * </p>
 *
 * @param gender    性别
 * @param group     用户自定义分组
 * @param distance  项目距离（如 "100", "200", "4*100"）
 * @param eventType 项目类型（个人/接力）
 * @param stroke    泳姿
 * @param <G>       分组类型，须为枚举且实现 {@link BaseGroup}
 * @author : 李泽聿
 * @since : 2026:05:13 15:00
 */
public record RaceInfo<G extends Enum<G> & BaseGroup>(
        Gender gender,
        G group,
        String distance,
        EventType eventType,
        Stroke stroke
) implements Serializable {

    private static final long serialVersionUID = 1L;

    public RaceInfo {
        Objects.requireNonNull(gender, "gender must not be null");
        Objects.requireNonNull(group, "group must not be null");
        Objects.requireNonNull(distance, "distance must not be null");
        Objects.requireNonNull(eventType, "eventType must not be null");
        Objects.requireNonNull(stroke, "stroke must not be null");
        if (distance.isBlank()) {
            throw new IllegalArgumentException("distance must not be blank");
        }
    }

    /**
     * 生成项目唯一 key。
     *
     * @return 格式为 {@code gender-group-distance-eventType-stroke} 的字符串
     */
    public String toKey() {
        return String.join("-", gender.name(), group.getName(), distance, eventType.name(), stroke.name());
    }

    @Override
    public String toString() {
        return toKey();
    }
}
