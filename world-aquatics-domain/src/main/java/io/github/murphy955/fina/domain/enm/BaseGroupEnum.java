package io.github.murphy955.fina.domain.enm;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分组枚举 - 支持运行时扩展
 * <p>
 * 用户可以通过 {@link #register(String, String)} 方法注册新的分组类型
 *
 * @author : 李泽聿
 * @since : 2026:05:08 10:31
 */
public class BaseGroupEnum {

    private static final Map<String, BaseGroupEnum> REGISTRY = new ConcurrentHashMap<>();

    private final String code;
    private final String description;

    private BaseGroupEnum(String code, String description) {
        this.code = code;
        this.description = description;
        REGISTRY.put(code, this);
    }

    /**
     * 注册新的分组类型
     *
     * @param code        分组代码（唯一标识）
     * @param description 分组描述
     * @return 注册的分组实例
     * @throws IllegalArgumentException 如果代码已存在
     */
    public static BaseGroupEnum register(String code, String description) {
        if (REGISTRY.containsKey(code)) {
            throw new IllegalArgumentException("分组代码已存在: " + code);
        }
        return new BaseGroupEnum(code, description);
    }

    /**
     * 根据代码获取分组
     *
     * @param code 分组代码
     * @return 对应的分组实例，不存在则返回 null
     */
    public static BaseGroupEnum fromCode(String code) {
        return REGISTRY.get(code);
    }

    /**
     * 获取所有已注册的分组（不可变视图）
     *
     * @return 所有分组的映射
     */
    public static Map<String, BaseGroupEnum> getAllGroups() {
        return Collections.unmodifiableMap(REGISTRY);
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "BaseGroupEnum{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseGroupEnum that = (BaseGroupEnum) o;
        return code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}
