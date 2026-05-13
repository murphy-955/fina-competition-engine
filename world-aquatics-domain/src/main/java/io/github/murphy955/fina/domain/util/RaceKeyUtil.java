package io.github.murphy955.fina.domain.util;

import io.github.murphy955.fina.common.exception.ValidationException;
import io.github.murphy955.fina.domain.enm.EventType;
import io.github.murphy955.fina.domain.enm.Gender;
import io.github.murphy955.fina.domain.enm.Stroke;
import io.github.murphy955.fina.domain.shared.BaseGroup;
import io.github.murphy955.fina.domain.vo.RaceInfo;

import java.util.Objects;

/**
 * 比赛项目 Key 工具类。
 * <p>
 * 提供 {@link RaceInfo} 与字符串 key 之间的双向转换：
 * </p>
 * <ul>
 *     <li>{@code buildKey} — 将维度信息编码为 {@code gender-group-distance-eventType-stroke}</li>
 *     <li>{@code decode} — 将合法 key 还原为 {@link RaceInfo}</li>
 * </ul>
 *
 * @author : 李泽聿
 * @since : 2026:05:08 10:47
 */
public class RaceKeyUtil {

    private static final String SEPARATOR = "-";
    private static final int KEY_PART_COUNT = 5;

    private RaceKeyUtil() {
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
        return String.join(SEPARATOR, gender.name(), group.getName(), distance, event.name(), stroke.name());
    }

    /**
     * 将合法 key 解码为 {@link RaceInfo}。
     * <p>
     * key 格式须为 {@code gender-group-distance-eventType-stroke}，
     * 其中 {@code group} 部分与 {@link BaseGroup#getName()} 匹配，
     * 而非枚举的 {@code name()}。
     * </p>
     *
     * @param key        项目 key
     * @param groupClass 用户自定义分组枚举的 Class 对象，用于反序列化 group 部分
     * @return 解析后的 {@link RaceInfo}
     * @throws ValidationException 如果 key 格式非法、某部分无法解析、或 group 找不到匹配项
     */
    public static <G extends Enum<G> & BaseGroup> RaceInfo<G> decode(String key, Class<G> groupClass) {
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(groupClass, "groupClass must not be null");

        String[] parts = key.split(SEPARATOR, -1);
        if (parts.length != KEY_PART_COUNT) {
            throw new ValidationException(
                    "Invalid key format: '" + key + "'. Expected " + KEY_PART_COUNT + " parts separated by '-', got " + parts.length
            );
        }

        Gender gender = parseEnum(Gender.class, parts[0], "gender");
        G group = parseGroup(groupClass, parts[1]);
        String distance = parts[2];
        EventType eventType = parseEnum(EventType.class, parts[3], "eventType");
        Stroke stroke = parseEnum(Stroke.class, parts[4], "stroke");

        return new RaceInfo<>(gender, group, distance, eventType, stroke);
    }

    private static <E extends Enum<E>> E parseEnum(Class<E> enumClass, String name, String fieldName) {
        try {
            return Enum.valueOf(enumClass, name);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    "Invalid " + fieldName + " in key: '" + name + "'. Expected one of " + java.util.Arrays.toString(enumClass.getEnumConstants())
            );
        }
    }

    private static <G extends Enum<G> & BaseGroup> G parseGroup(Class<G> groupClass, String groupName) {
        for (G constant : groupClass.getEnumConstants()) {
            if (constant.getName().equals(groupName)) {
                return constant;
            }
        }
        throw new ValidationException(
                "Invalid group in key: '" + groupName + "'. No matching getName() found in " + groupClass.getName()
        );
    }
}
