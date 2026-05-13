package io.github.murphy955.fina.domain.service;

import io.github.murphy955.fina.common.exception.ValidationException;
import io.github.murphy955.fina.domain.enm.EventType;
import io.github.murphy955.fina.domain.enm.Gender;
import io.github.murphy955.fina.domain.enm.Stroke;
import io.github.murphy955.fina.domain.shared.BaseGroup;
import io.github.murphy955.fina.domain.vo.RaceInfo;

/**
 * 比赛项目 Key 生成器。
 * <p>
 * 用于将性别、组别、距离、项目类型、泳姿等维度编码为唯一字符串 key，
 * 便于在编排、成绩处理、纪录校验等场景中将运动员按项目归类。
 * </p>
 *
 * @author : 李泽聿
 * @since : 2026:05:08 10:47
 */
public class RaceKeyBuilder {

    private RaceKeyBuilder() {
        // 工具类，禁止实例化
    }

    /**
     * 根据 {@link RaceInfo} 生成项目唯一 key。
     * <p>推荐方式，避免在调用处罗列五个参数。</p>
     *
     * @param info 比赛项目维度信息
     * @return 格式为 {@code gender-group-distance-eventType-stroke} 的字符串
     */
    public static <G extends Enum<G> & BaseGroup> String buildKey(RaceInfo<G> info) {
        return info.toKey();
    }

    /**
     * 逐参生成项目唯一 key。
     * <p>当你手头只有零散维度、不便组装成 {@link RaceInfo} 时使用。</p>
     *
     * @param gender   性别
     * @param group    用户自定义的分组（须实现 {@link BaseGroup}）
     * @param distance 项目长度
     * @param event    项目类型（个人/接力）
     * @param stroke   泳姿
     * @return 格式为 {@code gender-group-distance-event-stroke} 的字符串
     * @throws ValidationException 如果任一参数为 null
     */
    public static <G extends Enum<G> & BaseGroup> String buildKey(
            Gender gender, G group, String distance, EventType event, Stroke stroke) {
        if (gender == null || group == null || distance == null || event == null || stroke == null) {
            throw new ValidationException("all passed in parameters cannot be empty");
        }
        return String.join("-", gender.name(), group.getName(), distance, event.name(), stroke.name());
    }
}
