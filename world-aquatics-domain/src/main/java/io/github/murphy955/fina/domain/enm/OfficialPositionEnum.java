package io.github.murphy955.fina.domain.enm;

/**
 * 技术官员职位枚举<br>
 * <p>基于 World Aquatics Competition Regulations (2026-02-18) 中技术官员职位与职责制定。</p>
 * <table>
 *     <tr>
 *         <th>职位</th>
 *         <th>权限</th>
 *     </tr>
 *     <tr>
 *         <td>执行总裁判/主裁判</td>
 *         <td>更新任意道次的成绩、到边、转身、游尽犯规</td>
 *     </tr>
 *     <tr>
 *         <td>发令员</td>
 *         <td>无</td>
 *     </tr>
 *     <tr>
 *         <td>计时员</td>
 *         <td>仅能插入1~8道的某一道的成绩、到边、出发犯规</td>
 *     </tr>
 *     <tr>
 *         <td>游尽技术检查员</td>
 *         <td>插入所有道次的游尽犯规</td>
 *     </tr>
 *     <tr>
 *         <td>转身检查员</td>
 *         <td>仅能插入一道的转身犯规</td>
 *     </tr>
 *     <tr>
 *         <td>转身检查长</td>
 *         <td>插入所有道次转身犯规</td>
 *     </tr>
 *     <tr>
 *         <td>其他</td>
 *         <td>无</td>
 *     </tr>
 * </table>
 *
 * @author : 李泽聿
 * @since : 2026:05:09
 */
public enum OfficialPositionEnum {

    /**
     * 执行总裁判/主裁判
     */
    CHIEF_REFEREE("执行总裁判",
            AuthorityEnum.UPDATE_ALL_LANE_ACHIEVEMENTS.getPermissions()
                    | AuthorityEnum.UPDATE_ALL_DEPARTURE_FOUL.getPermissions()
                    | AuthorityEnum.UPDATE_ALL_TURN_FOUL.getPermissions()
                    | AuthorityEnum.UPDATE_ALL_SWIM_IN_FOUL.getPermissions()
                    | AuthorityEnum.UPDATE_ALL_ARRIVAL_FOUL.getPermissions()),

    /**
     * 发令员
     */
    STARTER("发令员", 0b0),

    /**
     * 计时员
     */
    TIMER("计时员",
            AuthorityEnum.INSERT_ACHIEVEMENTS_ONLY_1_LANE.getPermissions()
                    | AuthorityEnum.INSERT_DEPARTURE_FOUL_ONLY_1_LANE.getPermissions()
                    | AuthorityEnum.INSERT_ARRIVAL_FOUL_ONLY_1_LANE.getPermissions()),

    /**
     * 游尽技术检查员（游泳动作检查员）
     */
    STROKE_INSPECTOR("游尽技术检查员",
            AuthorityEnum.INSERT_ALL_SWIM_IN_FOUL.getPermissions()),

    /**
     * 转身检查员
     */
    TURN_INSPECTOR("转身检查员",
            AuthorityEnum.INSERT_TURN_FOUL_ONLY_1_LANE.getPermissions()),

    /**
     * 转身检查长
     */
    CHIEF_TURN_INSPECTOR("转身检查长",
            AuthorityEnum.INSERT_TURN_FOUL_ALL_LANES.getPermissions()),

    /**
     * 其他
     */
    OTHER("其他", 0b0);

    private final String name;
    private final int permissions;

    OfficialPositionEnum(String name, int permissions) {
        this.name = name;
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public int getPermissions() {
        return permissions;
    }

    /**
     * 判断该技术官员是否具备上报指定犯规类型的权限
     * <p>基于 {@link AuthorityEnum} 位掩码进行按位与运算：</p>
     * <pre>(this.permissions &amp; foul.getPermissions()) != 0</pre>
     *
     * @param foul 待判断的犯规类型
     * @return true 表示该职位至少拥有一项处理此犯规所需的权限
     */
    public boolean hasPermission(FoulType foul) {
        return (this.permissions & foul.getPermissions()) != 0;
    }

    /**
     * 判断该技术官员是否具备指定的单项权限
     *
     * @param authority 权限枚举
     * @return true 表示拥有该权限
     */
    public boolean hasPermission(AuthorityEnum authority) {
        return (this.permissions & authority.getPermissions()) != 0;
    }

    /**
     * 判断该技术官员是否具备指定权限掩码中的任意一项权限
     *
     * @param permissionMask 权限位掩码
     * @return true 表示至少拥有一项匹配权限
     */
    public boolean hasAnyPermission(int permissionMask) {
        return (this.permissions & permissionMask) != 0;
    }

    /**
     * 判断该技术官员是否具备指定权限掩码中的全部权限
     *
     * @param permissionMask 权限位掩码
     * @return true 表示拥有全部匹配权限
     */
    public boolean hasAllPermissions(int permissionMask) {
        return (this.permissions & permissionMask) == permissionMask;
    }
}
