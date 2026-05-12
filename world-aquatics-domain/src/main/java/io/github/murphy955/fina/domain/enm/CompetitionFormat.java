package io.github.murphy955.fina.domain.enm;

/**
 * 赛制模式
 * <p>
 * 由用户在选择比赛时指定：
 * <ul>
 *     <li>{@code PRELIMINARY_SEMI_FINAL} — 预赛 → 半决赛 → 决赛（默认）</li>
 *     <li>{@code PRELIMINARY_FINAL} — 预赛 → 决赛（无半决赛）</li>
 *     <li>{@code DIRECT_FINAL} — 直接决赛</li>
 * </ul>
 *
 * @author : 李泽聿
 * @since : 2026:05:12 16:35
 */
public enum CompetitionFormat {

    /**
     * 预赛 → 半决赛 → 决赛
     */
    PRELIMINARY_SEMI_FINAL("预赛 → 半决赛 → 决赛"),

    /**
     * 预赛 → 决赛
     */
    PRELIMINARY_FINAL("预赛 → 决赛"),

    /**
     * 直接决赛
     */
    DIRECT_FINAL("直接决赛");

    private final String description;

    CompetitionFormat(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
