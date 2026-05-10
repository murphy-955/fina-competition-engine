package io.github.murphy955.fina.domain.enm;

import java.util.Comparator;

/**
 * 比赛结果代码枚举
 * <p>
 * 基于 World Aquatics Competition Regulations (2026-02-18) 及国际游泳比赛通用标准制定。
 * 涵盖运动员在比赛各阶段可能出现的全部结果状态代码。
 * </p>
 * <p>
 * <strong>编排排序规则：</strong>
 * </p>
 * <ul>
 *     <li>只有 {@code OK}（成绩有效）的运动员参与正常成绩排名编排</li>
 *     <li>结果码不为 {@code OK} 的运动员按 {@link #sortOrder} 排至所有 {@code OK} 运动员之后</li>
 *     <li>非 {@code OK} 之间按 {@link #sortOrder} 默认升序排列</li>
 * </ul>
 *
 * @author : 李泽聿
 * @since : 2026:05:09
 */
public enum RaceResultCodeEnum {

    /**
     * 成绩有效（Valid Result）
     * <p>运动员正常完成比赛，成绩经裁判确认有效，成绩栏记录实际比赛时间。
     * 在预赛、半决赛、决赛编排时，只有 {@code OK} 的运动员才会进入正常的成绩排名编排。</p>
     */
    OK("OK", "Valid Result", "成绩有效",
            "正常完成比赛，成绩有效", 0),

    /**
     * 取消资格（Disqualification）
     * <p>运动员因违反技术规则（如出发抢跳、泳姿违规、转身违规、装备违规、行为不当等）
     * 被裁判员取消比赛成绩，成绩记录为 DQ。编排时排在所有 {@code OK} 之后。</p>
     */
    DQ("DQ", "Disqualification", "取消资格",
            "因违反技术规则或体育道德被取消比赛成绩", 1),

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
            "已报名但未实际参加出发", 2),

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
            "已出发但未完成规定比赛距离", 3),

    /**
     * 赛前退出（Scratch）
     * <p>运动员在赛前正式程序中申请退出该场比赛，与 DNS 的区别在于 SCR 是在赛前技术会议或
     * 报名截止前正式提交的退出，成绩栏留空或标注 SCR。</p>
     */
    SCR("SCR", "Scratch", "赛前退出",
            "在赛前正式程序中退出比赛", 4),

    /**
     * 取消比赛资格（Disqualified — 正式书面用语）
     * <p>与 DQ 含义相同，DSQ 多见于正式成绩公告、技术报告及官方记录文件。
     * 在实际成绩处理中与 DQ 等价。</p>
     */
    DSQ("DSQ", "Disqualified", "取消资格（正式）",
            "DQ 的正式书面表述，实际成绩处理中等价于 DQ", 5);

    private final String code;
    private final String englishName;
    private final String chineseName;
    private final String description;

    /**
     * 编排排序序号（越小越靠前）
     * <p>{@code OK = 0} 始终排在最前，非 {@code OK} 按序号升序排在 {@code OK} 之后。</p>
     */
    private final int sortOrder;

    RaceResultCodeEnum(String code, String englishName, String chineseName,
                       String description, int sortOrder) {
        this.code = code;
        this.englishName = englishName;
        this.chineseName = chineseName;
        this.description = description;
        this.sortOrder = sortOrder;
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
     * 获取编排排序序号（越小越靠前）
     */
    public int getSortOrder() {
        return sortOrder;
    }

    /**
     * 判断该结果码是否为成绩有效（OK）
     * <p>在预赛、半决赛、决赛编排时，只有 {@code OK} 的运动员才会进入正常的成绩排名编排。</p>
     *
     * @return true 表示成绩有效，可参与正常排名
     */
    public boolean isQualified() {
        return this == OK;
    }

    /**
     * 判断该结果码是否为取消资格类（DQ 或 DSQ）
     *
     * @return true 表示因犯规被取消资格
     */
    public boolean isDisqualified() {
        return this == DQ || this == DSQ;
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

    /**
     * 编排排序比较器
     * <p>按 {@link #sortOrder} 升序排列，{@code OK} 始终排在最前。</p>
     */
    public static final Comparator<RaceResultCodeEnum> SEEDING_COMPARATOR =
            Comparator.comparingInt(RaceResultCodeEnum::getSortOrder);
}
