package io.github.murphy955.fina.domain.enm;

/**
 * 计时系统类型
 * <p>
 * 引擎须同时支持三种计时系统，允许同一场比赛混合使用。
 *
 * @author : 李泽聿
 * @since : 2026:05:12 16:32
 */
public enum TimingSystem {

    /**
     * 全自动电子计时
     * <p>精度 0.01 秒，作为官方成绩优先采用</p>
     */
    ELECTRONIC("电子计时", "全自动电子计时，精度 0.01 秒"),

    /**
     * 半自动计时
     * <p>人工按按钮 + 电子触板，精度 0.01 秒</p>
     */
    SEMI_AUTOMATIC("半自动计时", "人工按钮 + 电子触板"),

    /**
     * 纯手动计时
     * <p>人工秒表，通常作为备份</p>
     */
    MANUAL("手动计时", "人工秒表");

    private final String name;
    private final String description;

    TimingSystem(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
