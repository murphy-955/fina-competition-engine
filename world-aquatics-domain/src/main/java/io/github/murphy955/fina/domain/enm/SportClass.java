package io.github.murphy955.fina.domain.enm;

/**
 * 残奥游泳分级
 * <p>
 * 基于 World Aquatics Paralympic Swimming Rules：
 * <ul>
 *     <li><strong>S</strong> — 自由泳、仰泳、蝶泳分级</li>
 *     <li><strong>SB</strong> — 蛙泳分级</li>
 *     <li><strong>SM</strong> — 混合泳分级</li>
 * </ul>
 * 具体级别编号（如 S6, SB7, SM8）由用户在比赛配置时注入，引擎不内置具体级别含义。
 *
 * @author : 李泽聿
 * @since : 2026:05:12 16:30
 */
public enum SportClass {

    /**
     * 自由泳 / 仰泳 / 蝶泳分级
     */
    S("S", "自由泳/仰泳/蝶泳分级"),

    /**
     * 蛙泳分级
     */
    SB("SB", "蛙泳分级"),

    /**
     * 混合泳分级
     */
    SM("SM", "混合泳分级");

    private final String code;
    private final String description;

    SportClass(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
