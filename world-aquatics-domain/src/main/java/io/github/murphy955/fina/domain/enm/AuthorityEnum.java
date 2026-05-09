package io.github.murphy955.fina.domain.enm;

/**
 * 官员/技术官员权限位枚举<br>
 * <p>基于 World Aquatics Competition Regulations (2026-02-18) 中技术官员职责制定。</p>
 * <table>
 *     <tr>
 *         <th>权限位</th>
 *         <th>描述</th>
 *     </tr>
 *     <tr>
 *         <td>0b1</td>
 *         <td>只能提交1道的成绩</td>
 *     </tr>
 *     <tr>
 *         <td>0b10</td>
 *         <td>可以修改1~8道的成绩</td>
 *     </tr>
 *     <tr>
 *         <td>0b100</td>
 *         <td>只能提交1道的出发违规行为</td>
 *     </tr>
 *     <tr>
 *         <td>0b1000</td>
 *         <td>可以修改1~8道的出发违规行为</td>
 *     </tr>
 *     <tr>
 *         <td>0b10000</td>
 *         <td>只能提交1道的到边违规行为</td>
 *     </tr>
 *     <tr>
 *         <td>0b100000</td>
 *         <td>可以修改1~8道的到边违规行为</td>
 *     </tr>
 *     <tr>
 *         <td>0b1000000</td>
 *         <td>只能提交1道的转身违规行为</td>
 *     </tr>
 *     <tr>
 *         <td>0b10000000</td>
 *         <td>可以提交1~8道的转身违规行为</td>
 *     </tr>
 *     <tr>
 *         <td>0b100000000</td>
 *         <td>可以修改1~8道的转身违规行为</td>
 *     </tr>
 *     <tr>
 *         <td>0b1000000000</td>
 *         <td>可以提交1~8道的游尽违规行为</td>
 *     </tr>
 *     <tr>
 *         <td>0b10000000000</td>
 *         <td>可以修改1~8道的游尽违规行为</td>
 *     </tr>
 * </table>
 *
 * @author : 李泽聿
 * @since : 2026:05:09
 */
public enum AuthorityEnum {

    /**
     * 只能提交1道的成绩
     */
    INSERT_ACHIEVEMENTS_ONLY_1_LANE("只能提交1道的成绩", 0b1),

    /**
     * 可以修改1~8道的成绩
     */
    UPDATE_ALL_LANE_ACHIEVEMENTS("可以修改1~8道的成绩", 0b10),

    /**
     * 只能提交1道的出发违规行为
     */
    INSERT_DEPARTURE_FOUL_ONLY_1_LANE("只能提交1道的出发违规行为", 0b100),

    /**
     * 可以修改1~8道的出发违规行为
     */
    UPDATE_ALL_DEPARTURE_FOUL("可以修改1~8道的出发违规行为", 0b1000),

    /**
     * 只能提交1道的到边违规行为
     */
    INSERT_ARRIVAL_FOUL_ONLY_1_LANE("只能提交1道的到边违规行为", 0b10000),

    /**
     * 可以修改1~8道的到边违规行为
     */
    UPDATE_ALL_ARRIVAL_FOUL("可以修改1~8道的到边违规行为", 0b100000),

    /**
     * 只能提交1道的转身违规行为
     */
    INSERT_TURN_FOUL_ONLY_1_LANE("只能提交1道的转身违规行为", 0b1000000),

    /**
     * 可以提交1~8道的转身违规行为
     */
    INSERT_TURN_FOUL_ALL_LANES("可以提交1~8道的转身违规行为", 0b10000000),

    /**
     * 可以修改1~8道的转身违规行为
     */
    UPDATE_ALL_TURN_FOUL("可以修改1~8道的转身违规行为", 0b100000000),

    /**
     * 可以提交1~8道的游尽违规行为
     */
    INSERT_ALL_SWIM_IN_FOUL("可以提交1~8道的游尽违规行为", 0b1000000000),

    /**
     * 可以修改1~8道的游尽违规行为
     */
    UPDATE_ALL_SWIM_IN_FOUL("可以修改1~8道的游尽违规行为", 0b10000000000);

    private final String description;
    private final int permissions;

    AuthorityEnum(String description, int permissions) {
        this.description = description;
        this.permissions = permissions;
    }

    public String getDescription() {
        return description;
    }

    public int getPermissions() {
        return permissions;
    }
}
