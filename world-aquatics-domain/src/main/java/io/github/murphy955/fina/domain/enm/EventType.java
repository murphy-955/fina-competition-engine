package io.github.murphy955.fina.domain.enm;

/**
 * 项目类型枚举
 *
 * @author : 李泽聿
 * @since : 2026/05/07 14:11
 */
public enum EventType {

    /**
     * 个人项目
     */
    INDIVIDUAL("个人项目"),

    /**
     * 接力项目
     */
    RELAY("接力项目");

    private final String description;

    EventType( String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
