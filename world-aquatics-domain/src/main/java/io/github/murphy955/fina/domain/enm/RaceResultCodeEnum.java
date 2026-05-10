package io.github.murphy955.fina.domain.enm;

/**
 * 比赛结果代码枚举
 * <p>
 * 基于 World Aquatics Competition Regulations (2026-02-18) 及国际游泳比赛通用标准制定。
 * 涵盖运动员在比赛各阶段可能出现的全部结果状态代码。
 * </p>
 *
 * @author : 李泽聿
 * @since : 2026:05:09
 */
public enum RaceResultCodeEnum {

    /**
     * 取消资格（Disqualification）
     * <p>运动员因违反技术规则（如出发抢跳、泳姿违规、转身违规、装备违规、行为不当等）
     * 被裁判员取消比赛成绩，成绩记录为 DQ。</p>
     */
    DQ("DQ", "Disqualification", "取消资格",
            "因违反技术规则或体育道德被取消比赛成绩"),

    /**
     * 未出发（Did Not Start）
     * <p>运动员已报名但未实际参加比赛出发，包括：</p>
     * <ul>
     *     <li>检录未到（No Show）</li>
     *     <li>赛前弃权（Withdrawal）</li>
     *     <li>发令员点名后未上出发台</li>
     * </ul>
     */
    DNS("DNS", "Did Not Start", "未出发",
            "已报名但未实际参加出发"),

    /**
     * 未完成（Did Not Finish）
     * <p>运动员已出发但在比赛过程中中途退出或未能完成规定距离，包括：</p>
     * <ul>
     *     <li>比赛中途退赛</li>
     *     <li>因身体原因无法继续</li>
     *     <li>未游完全程即停止</li>
     * </ul>
     */
    DNF("DNF", "Did Not Finish", "未完成",
            "已出发但未完成规定比赛距离"),

    /**
     * 赛前退出（Scratch）
     * <p>运动员在赛前正式程序中申请退出该场比赛，与 DNS 的区别在于 SCR 是在赛前技术会议或
     * 报名截止前正式提交的退出，成绩栏留空或标注 SCR。</p>
     */
    SCR("SCR", "Scratch", "赛前退出",
            "在赛前正式程序中退出比赛"),

    /**
     * 取消比赛资格（Disqualified — 正式书面用语）
     * <p>与 DQ 含义相同，DSQ 多见于正式成绩公告、技术报告及官方记录文件。
     * 在实际成绩处理中与 DQ 等价。</p>
     */
    DSQ("DSQ", "Disqualified", "取消资格（正式）",
            "DQ 的正式书面表述，实际成绩处理中等价于 DQ");

    private final String code;
    private final String englishName;
    private final String chineseName;
    private final String description;

    RaceResultCodeEnum(String code, String englishName, String chineseName, String description) {
        this.code = code;
        this.englishName = englishName;
        this.chineseName = chineseName;
        this.description = description;
    }

    /**
     * 获取标准三字母代码（如 DQ、DNS、DNF）
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取英文全称
     */
    public String getEnglishName() {
        return englishName;
    }

    /**
     * 获取中文名称
     */
    public String getChineseName() {
        return chineseName;
    }

    /**
     * 获取详细说明
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据代码字符串查找对应的枚举值
     *
     * @param code 结果代码，如 "DQ"、"dns"
     * @return 对应的 RaceResultCodeEnum，若找不到则返回 null
     */
    public static RaceResultCodeEnum fromCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        String upper = code.trim().toUpperCase();
        for (RaceResultCodeEnum value : values()) {
            if (value.code.equals(upper)) {
                return value;
            }
        }
        return null;
    }
}
