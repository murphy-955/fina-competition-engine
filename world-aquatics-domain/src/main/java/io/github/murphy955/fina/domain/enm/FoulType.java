package io.github.murphy955.fina.domain.enm;

/**
 * 犯规类型枚举
 * <p>
 * 基于 World Aquatics Competition Regulations (2026-02-18) 和
 * Swimming Rule Quick Reference Infraction Sheet (2026-03-13) 制定。
 * </p>
 * <p>
 * 本枚举按比赛结果代码（DQ / DNS / DNF / SCR）对全部犯规原因进行了顶层分类：
 * </p>
 * <ul>
 *     <li><strong>DQ</strong>（取消资格）— 因违反技术规则或体育道德被取消成绩，占比最大</li>
 *     <li><strong>DNS</strong>（未出发）— 已报名但未实际参加出发</li>
 *     <li><strong>DNF</strong>（未完成）— 已出发但未完成规定距离</li>
 *     <li><strong>SCR</strong>（赛前退出）— 在赛前正式程序中退出比赛</li>
 * </ul>
 *
 * @author : 李泽聿
 * @since : 2026:05:09
 */
public enum FoulType {

    /* ==================== DQ（取消资格）— 出发犯规 ==================== */

    /**
     * 出发抢跳：在出发信号发出前离开出发台或水中出发位置
     */
    FALSE_START("出发抢跳",
            OfficialPositionEnum.TIMER.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 出发姿势违规：未按规则要求采取正确的出发姿势（如仰泳未抓住握手器等）
     * <ul>
     *     <li>仰泳出发：至少有一个脚趾在出发板上</li>
     *     <li>双脚脚趾不能超过水面</li>
     *     <li>至少有一只脚位于出发台前缘</li>
     * </ul>
     */
    ILLEGAL_START_POSITION("出发姿势违规",
            OfficialPositionEnum.TIMER.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 在出发信号发出前启动
     * <p>2026规则 Article 4.4: 如信号已发出则比赛继续，完成后取消资格；
     * 如信号未发出则召回重新出发</p>
     */
    START_BEFORE_SIGNAL("在出发信号发出前启动",
            OfficialPositionEnum.TIMER.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /* ==================== DQ（取消资格）— 游尽犯规（自由泳） ==================== */

    /**
     * 入水超过15米
     * <p><strong>注: 蛙泳不适用</strong></p>
     * <p>2026规则 Article 5.3, 6.3, 8.6: 15米线前头必须露出水面</p>
     */
    MORE_THAN_15_METERS_INTO_THE_WATER("出发或转身后15米线前头未露出水面",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 自由泳游尽违规：身体完全潜入水中
     * <p>2026规则 Article 5.4: 游进过程中完全潜入水中（到达终点前5米除外）</p>
     */
    FREESTYLE_SWIM_IN_VIOLATION_BODY_TOTALLY_SUBMERGED("自由泳游进过程中完全潜入水中",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 自由泳转身或到达终点时未触壁
     * <p>2026规则 Article 5.2</p>
     */
    FREESTYLE_DID_NOT_TOUCH_WALL("自由泳转身或到达终点时未触壁",
            OfficialPositionEnum.TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /* ==================== DQ（取消资格）— 游尽犯规（仰泳） ==================== */

    /**
     * 仰泳游尽过程中，违反身体仰卧原则
     * <p><strong>注: 对以下情况不适用</strong></p>
     * <ul>
     *     <li>转身过程中，可以转过垂直面至俯卧姿势</li>
     * </ul>
     * <p>2026规则 Article 6.2: 除转身外必须保持仰卧姿势</p>
     */
    BACKSTROKE_SWIM_IN_VIOLATION_SUPINE_POSITION("仰泳游进过程中离开仰卧姿势",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 仰泳游尽过程中，身体完全没入水中
     * <p><strong>注: 对以下情况不适用</strong></p>
     * <ul>
     *     <li>除了到边的前5米</li>
     * </ul>
     * <p>2026规则 Article 6.5</p>
     */
    BACKSTROKE_SWIM_IN_VIOLATION_BODY_TOTALLY_UNDER_WATER("仰泳游进过程中完全潜入水中",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 仰泳转身蹬离池壁后未保持仰卧姿势
     * <p>2026规则 Article 6.4</p>
     */
    BACKSTROKE_TURN_NOT_ON_BACK_AFTER_LEAVE_WALL("仰泳转身蹬离池壁后未保持仰卧姿势",
            OfficialPositionEnum.TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 仰泳转身时没有立即开始转身动作
     * <p>2026规则 Article 6.4</p>
     */
    BACKSTROKE_TURN_NOT_INITIATED_IMMEDIATELY("仰泳转身时没有立即开始转身动作",
            OfficialPositionEnum.TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 仰泳到达终点时未保持仰卧姿势
     * <p>2026规则 Article 6.5</p>
     */
    BACKSTROKE_FINISH_NOT_ON_BACK("仰泳到达终点时未保持仰卧姿势",
            OfficialPositionEnum.CHIEF_REFEREE.getPermissions()
                    | OfficialPositionEnum.TIMER.getPermissions(),
            RaceResultCode.DQ),

    /* ==================== DQ（取消资格）— 游尽犯规（蛙泳） ==================== */

    /**
     * 蛙泳长划手中向后划水超过髋线
     * <p>2026规则 Article 7.4: 除出发和转身后第一次划水外，手不得划回超过髋线</p>
     */
    BREASTSTROKE_LONG_SWIM_BACK_TO_SHOULDER("蛙泳长划手中向后划水超过髋线",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蛙泳出发或转身后第一次蹬腿前做超过一次的蝶泳腿
     * <p>2026规则 Article 7.1: 只允许一次蝶泳腿</p>
     */
    BREASTSTROKE_LONG_SWIM_BITE_LEGS_OVER_1_TIME("蛙泳出发或转身后第一次蹬腿前做超过一次的蝶泳腿",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蛙泳出发或转身后，在第二次划臂两手划至最宽点开始向内划水前，头未露出水面
     * <p>2026规则 Article 7.1</p>
     */
    BREASTSTROKE_LONG_SWIM_HEAD_EXPOSED("蛙泳出发或转身后，第二次划臂前头未露出水面",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蛙泳游进时手未从胸前向前推出
     * <p>2026规则 Article 7.4: 双手必须从胸前一起向前推出</p>
     */
    BREASTSTROKE_HANDS_NOT_PUSHED_FROM_BREAST("蛙泳游进时手未从胸前向前推出",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蛙泳游进时肘部未保持在水下
     * <p>2026规则 Article 7.4: 除转身前最后一次划水、转身中、到达终点前最后一次划水外，
     * 肘部必须在水下</p>
     */
    BREASTSTROKE_ELBOWS_NOT_UNDER_WATER("蛙泳游进时肘部未保持在水下",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蛙泳游进时手划回超过髋线
     * <p>2026规则 Article 7.4: 除出发和转身后第一次划水外</p>
     */
    BREASTSTROKE_HANDS_BEYOND_HIP_LINE("蛙泳游进时手划回超过髋线",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蛙泳游进时头未在每个动作周期中露出水面
     * <p>2026规则 Article 7.5</p>
     */
    BREASTSTROKE_HEAD_NOT_EXPOSED_EACH_CYCLE("蛙泳游进时头未在每个动作周期中露出水面",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蛙泳蹬腿时脚未向外翻转
     * <p>2026规则 Article 7.6: 脚的蹬水部分必须向外</p>
     */
    BREASTSTROKE_FEET_NOT_TURNED_OUT("蛙泳蹬腿时脚未向外翻转",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蛙泳游进中做向下海豚式打腿
     * <p>2026规则 Article 7.6: 除7.1允许的一次外，不得做向下海豚式打腿</p>
     */
    BREASTSTROKE_DOWNWARD_BUTTERFLY_KICK("蛙泳游进中做向下海豚式打腿",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蛙泳身体必须俯卧
     * <p>2026规则 Article 7.2: 除转身触壁后外，身体必须保持俯卧</p>
     */
    BREASTSTROKE_SWIM_IN_VIOLATION_BODY_MUST_SUPINE("蛙泳身体未保持俯卧",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蛙泳动作周期没有按一次手一次腿的顺序进行
     * <p>2026规则 Article 7.3: 动作周期必须是一次划臂和一次蹬腿的顺序</p>
     */
    BREASTSTROKE_SWIM_IN_VIOLATION_ACTION_CYCLE_NOT_ONE_HAND_ONE_LEG("蛙泳动作周期没有按一次手一次腿的顺序进行",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蛙泳两臂动作不同时
     * <p>2026规则 Article 7.3: 两臂动作必须同时，不得交替</p>
     */
    BREASTSTROKE_SWIM_IN_VIOLATION_BOTH_ARMS_SHOULDER_ACTION_DIFFERENT("蛙泳两臂动作不同时",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蛙泳腿部动作不是同时进行的
     * <p>2026规则 Article 7.5: 两腿动作必须同时，不得交替</p>
     */
    BREASTSTROKE_SWIM_IN_VIOLATION_LEGS_NOT_SAME_LEVEL("蛙泳腿部动作不是同时进行的",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蛙泳腿两腿交替打水
     * <p>2026规则 Article 7.5</p>
     */
    BREASTSTROKE_SWIM_IN_VIOLATION_LEGS_SWITCH_POSITION("蛙泳腿两腿交替打水",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蛙泳肘部超出水面
     * <p>注：转身前最后一次划水、转身中、终点前最后一次划水除外</p>
     */
    BREASTSTROKE_SWIM_IN_VIOLATION_ELBOW_OUT_OF_WATER("蛙泳肘部超出水面",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蛙泳两臂动作不在同一水平面
     * <p>2026规则: 两手必须同时从胸前向前推出</p>
     */
    BREASTSTROKE_SWIM_IN_VIOLATION_BOTH_ARMS_SHOULDER_ACTION_NOT_SAME_LEVEL("蛙泳两臂动作不在同一水平面",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蛙泳手两手没有从胸前伸出
     */
    BREASTSTROKE_SWIM_IN_VIOLATION_HANDS_NOT_EXTENDED_FROM_CHEST("蛙泳手两手没有从胸前伸出",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蛙泳一次腿部动作完成后，腿部出现上扬或下压现象
     */
    BREASTSTROKE_SWIM_IN_VIOLATION_RISE_OR_DEPRESSION_AFTER_ONE_LEG("蛙泳一次腿部动作完成后，腿部出现上扬或下压现象",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 在蛙泳蹬腿过程中，两脚必须做外翻动作
     */
    BREASTSTROKE_SWIM_IN_VIOLATION_LEGS_NOT_EXTENDED_FROM_WATER("在蛙泳蹬腿过程中，两脚必须做外翻动作",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /* ==================== DQ（取消资格）— 游尽犯规（蝶泳） ==================== */

    /**
     * 蝶泳两手臂未同时前摆，或手臂未同时向后划水
     * <p>2026规则 Article 8.3</p>
     */
    BUTTERFLY_SWIM_IN_VIOLATION_BOTH_ARMS_SHOULDER_NOT_BOTH_SHOULDER_ACTION("蝶泳两手臂未同时前摆，或手臂未同时向后划水",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蝶泳双臂未经空中前摆
     * <p>2026规则 Article 8.3: 两臂必须同时在水面上向前摆动</p>
     */
    BUTTERFLY_SWIM_IN_VIOLATION_BOTH_ARMS_SHOULDER_NOT_SHOULDER_ACTION("蝶泳双臂未经空中前摆",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蝶泳游进过程中出现仰卧姿势
     * <p>2026规则 Article 8.2: 除转身触壁后外，不得转成仰卧</p>
     */
    BUTTERFLY_SWIM_IN_VIOLATION_SUPINE_POSITION("蝶泳游进过程中出现仰卧姿势",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蝶泳腿两腿动作不同时
     * <p>2026规则 Article 8.4</p>
     */
    BUTTERFLY_SWIM_IN_VIOLATION_LEGS_ACTION_DIFFERENT("蝶泳腿两腿动作不同时",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蝶泳腿两腿交替打水
     * <p>2026规则 Article 8.4: 两腿上下动作必须同时，不得交替</p>
     */
    BUTTERFLY_SWIM_IN_VIOLATION_LEGS_SWITCH_SWIM("蝶泳腿两腿交替打水",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蝶泳腿动作中出现蛙泳腿动作
     * <p>2026规则 Article 8.4: 不允许蛙泳蹬腿动作</p>
     */
    BUTTERFLY_SWIM_IN_VIOLATION_LEGS_BUTTERFLY_ACTION("蝶泳腿动作中出现蛙泳腿动作",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蝶泳游进过程中，身体完全没入水中
     * <p>2026规则 Article 8.6: 除转身和到达终点前5米外，身体必须露出水面</p>
     */
    BUTTERFLY_SWIM_IN_VIOLATION_BODY_TOTALLY_UNDER_WATER("蝶泳游进过程中完全潜入水中",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蝶泳出发或转身后在水下做超过一次划臂
     * <p>2026规则 Article 8.6: 只允许一次划臂在水下</p>
     */
    BUTTERFLY_MORE_THAN_ONE_ARM_PULL_UNDERWATER("蝶泳出发或转身后在水下做超过一次划臂",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /* ==================== DQ（取消资格）— 混合泳游尽犯规 ==================== */

    /**
     * 未按蝶泳、仰泳、蛙泳、自由泳顺序游进
     * <p>2026规则 Article 9.1: 个人混合泳顺序</p>
     */
    INDIVIDUAL_MEDLEY_SWIM_IN_VIOLATION_NOT_FOLLOW_ORDER("未按蝶泳、仰泳、蛙泳、自由泳顺序游进",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 混合泳的自由泳段采用蝶泳或仰泳或蛙泳技术
     * <p>2026规则 Article 5.1, 9.3: 自由泳段不得采用其他三种泳姿</p>
     */
    INDIVIDUAL_MEDLEY_SWIM_IN_VIOLATION_MIXED_TECHNIQUE("混合泳自由泳段采用蝶泳、仰泳或蛙泳技术",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 混合泳接力没有按照仰、蛙、蝶、自的顺序游进
     * <p>2026规则 Article 9.2</p>
     */
    MEDLEY_RELAY_SWIM_IN_VIOLATION_NOT_FOLLOW_ORDER("混合泳接力没有按照仰、蛙、蝶、自的顺序游进",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 混合泳蛙泳转自由泳段转身前未恢复俯卧姿势就做打腿或划水动作
     * <p>2026规则 Article 9.3: 必须在任何踢腿或划水前恢复俯卧姿势</p>
     */
    MEDLEY_FREESTYLE_KICK_BEFORE_RETURN_PRONE("混合泳自由泳段转身前未恢复俯卧姿势就做打腿或划水动作",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /* ==================== DQ（取消资格）— 转身犯规 ==================== */

    /**
     * 自由泳转身时，身体任何部分未接触池壁
     * <p>2026规则 Article 10.2.3</p>
     */
    FREESTYLE_TURNING_VIOLATION_BODY_NOT_TOUCH_POOL("自由泳转身时，身体任何部分未接触池壁",
            OfficialPositionEnum.TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 自由泳转身后，头没有在15米内露出水面
     * <p>2026规则 Article 5.3</p>
     */
    FREESTYLE_TURNING_VIOLATION_HEAD_NOT_LEAVE_WATER("自由泳转身后，头没有在15米内露出水面",
            OfficialPositionEnum.TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 仰泳转身时，身体转为俯卧后做多次连续的单臂划水或双臂同时划水动作
     * <p>2026规则 Article 6.4: 只允许立即连续的单臂划水或双臂同时划水</p>
     */
    BACKSTROKE_TURNING_VIOLATION_DOUBLE_ARM_SWIM("仰泳转身时，身体转为俯卧后做多次连续的单臂划水或双臂同时划水动作",
            OfficialPositionEnum.TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 仰泳蹬离池壁后，呈俯卧姿势
     * <p>2026规则 Article 6.4</p>
     */
    BACKSTROKE_TURNING_VIOLATION_SUPINE_POSITION("仰泳蹬离池壁后，呈俯卧姿势",
            OfficialPositionEnum.TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 仰泳转身时，身体任意部位未接触池壁
     * <p>2026规则 Article 6.4</p>
     */
    BACKSTROKE_TURNING_VIOLATION_BODY_NOT_TOUCH_POOL("仰泳转身时，身体任意部位未接触池壁",
            OfficialPositionEnum.TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蛙泳转身时，两手未同时触壁
     * <p>2026规则 Article 7.7: 必须双手分开同时触壁</p>
     */
    BREASTSTROKE_TURNING_VIOLATION_BOTH_HANDS_NOT_TOUCH_POOL("蛙泳转身时，两手未同时触壁",
            OfficialPositionEnum.TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蛙泳转身时，身体任意部分未接触池壁
     * <p>2026规则 Article 7.7</p>
     */
    BREASTSTROKE_TURNING_VIOLATION_BODY_NOT_TOUCH_POOL("蛙泳转身时，身体任意部分未接触池壁",
            OfficialPositionEnum.TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蛙泳转身时，身体出现仰卧姿势
     * <p>2026规则 Article 7.2: 除转身触壁后外，身体必须俯卧</p>
     */
    BREASTSTROKE_TURNING_VIOLATION_SUPINE_POSITION("蛙泳转身时，身体出现仰卧姿势",
            OfficialPositionEnum.TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蛙泳转身后，第一次蛙泳蹬腿前，做超过一次的蝶泳腿
     * <p>2026规则 Article 7.1</p>
     */
    BREASTSTROKE_TURNING_VIOLATION_DOUBLE_LEG_SWIM("蛙泳转身后，第一次蛙泳蹬腿前，做超过一次的蝶泳腿",
            OfficialPositionEnum.TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions()
                    | OfficialPositionEnum.STROKE_INSPECTOR.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蛙泳转身后，在第2次划臂两手划至最宽点开始向内划水前，头没有露出水面
     * <p>2026规则 Article 7.1</p>
     */
    BREASTSTROKE_TURNING_VIOLATION_HEAD_NOT_EXPOSED_WATER("蛙泳转身后，在第2次划臂两手划至最宽点开始向内划水前，头没有露出水面",
            OfficialPositionEnum.TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions()
                    | OfficialPositionEnum.STROKE_INSPECTOR.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蝶泳转身时，单手转身
     * <p>2026规则 Article 8.5: 必须双手同时触壁</p>
     */
    BUTTERFLY_TURNING_VIOLATION_SINGLE_ARM_SWIM("蝶泳转身时，单手转身",
            OfficialPositionEnum.TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蝶泳转身时，仰卧姿势
     * <p>2026规则 Article 8.2</p>
     */
    BUTTERFLY_TURNING_VIOLATION_SUPINE_POSITION("蝶泳转身时，仰卧姿势",
            OfficialPositionEnum.TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蝶泳转身后，头没有在15米标志线之前露出水面
     * <p>2026规则 Article 8.6</p>
     */
    BUTTERFLY_TURNING_VIOLATION_HEAD_NOT_EXPOSED_WATER("蝶泳转身后，头没有在15米标志线之前露出水面",
            OfficialPositionEnum.TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions()
                    | OfficialPositionEnum.STROKE_INSPECTOR.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蝶泳转身后，在水下划手超过一次
     * <p>2026规则 Article 8.6: 只允许一次划臂</p>
     */
    BUTTERFLY_TURNING_VIOLATION_SWIM_HAND_UNDER_WATER("蝶泳转身后，在水下划手超过一次",
            OfficialPositionEnum.TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions()
                    | OfficialPositionEnum.STROKE_INSPECTOR.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蝶泳转身时，身体任意部分未触壁
     * <p>2026规则 Article 8.5</p>
     */
    BUTTERFLY_TURNING_VIOLATION_BODY_NOT_TOUCH_POOL("蝶泳转身时，身体任意部分未触壁",
            OfficialPositionEnum.TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 混合泳转身时，身体任意部分未触壁
     * <p>2026规则 Article 10.2.3</p>
     */
    MEDLEY_TURNING_VIOLATION_BODY_NOT_TOUCH_POOL("混合泳转身时，身体任意部分未触壁",
            OfficialPositionEnum.TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 混合泳转身时，头没有在15米标志线之前露出水面（除了蛙泳）
     * <p>2026规则 Article 5.3, 6.3, 8.6</p>
     */
    MEDLEY_TURNING_VIOLATION_HEAD_NOT_EXPOSED_WATER("混合泳转身时，头没有在15米标志线之前露出水面（除了蛙泳）",
            OfficialPositionEnum.TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions()
                    | OfficialPositionEnum.STROKE_INSPECTOR.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 混合泳在仰泳转蛙泳时，触壁前采用了俯卧姿势
     * <p>2026规则 Article 6.2: 除转身外必须保持仰卧</p>
     */
    MEDLEY_TURNING_VIOLATION_SUPINE_POSITION("混合泳在仰泳转蛙泳时，触壁前采用了俯卧姿势",
            OfficialPositionEnum.TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蝶泳转仰泳和蛙泳转自由泳时，单手接触池壁
     * <p>2026规则 Article 8.5, 7.7: 必须双手同时触壁</p>
     */
    MEDLEY_TURNING_VIOLATION_SINGLE_ARM_SWIM("蝶泳转仰泳和蛙泳转自由泳时，单手接触池壁",
            OfficialPositionEnum.TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蝶泳转仰泳和蛙泳转自由泳时，采用了自由泳滚翻
     * <p>2026规则: 蝶泳和蛙泳必须使用双手触壁转身</p>
     */
    MEDLEY_TURNING_VIOLATION_FREESTYLE_ROLL_FLIP("蝶泳转仰泳和蛙泳转自由泳时，采用了自由泳滚翻",
            OfficialPositionEnum.TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /* ==================== DQ（取消资格）— 到边犯规 ==================== */

    /**
     * 到达终点未触壁：到达终点时未用身体任何部分触及池壁
     * <p>2026规则 Article 10.2.3</p>
     */
    FINISH_NO_TOUCH("到达终点未触壁",
            AuthorityEnum.INSERT_ARRIVAL_FOUL_ONLY_1_LANE.getPermissions()
                    | AuthorityEnum.UPDATE_ALL_ARRIVAL_FOUL.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 蝶泳、蛙泳以单手到边
     * <p>2026规则 Article 7.7, 8.5: 必须双手同时触壁</p>
     */
    FINISH_SINGLE_ARM_SWIM("蝶泳、蛙泳以单手到边",
            AuthorityEnum.INSERT_ARRIVAL_FOUL_ONLY_1_LANE.getPermissions()
                    | AuthorityEnum.UPDATE_ALL_ARRIVAL_FOUL.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 仰泳到达终点线前，改变仰卧姿势
     * <p>2026规则 Article 6.5: 到达终点时必须保持仰卧</p>
     */
    FINISH_SUPINE_POSITION("仰泳到达终点线前，改变仰卧姿势",
            AuthorityEnum.INSERT_ARRIVAL_FOUL_ONLY_1_LANE.getPermissions()
                    | AuthorityEnum.UPDATE_ALL_ARRIVAL_FOUL.getPermissions(),
            RaceResultCode.DQ),

    /* ==================== DQ（取消资格）— 接力犯规 ==================== */

    /**
     * 接力交接违规：前一名运动员尚未触壁后一名运动员即离开出发台
     * <p>2026规则 Article 10.4.5</p>
     */
    RELAY_EARLY_TAKEOFF("接力交接违规",
            AuthorityEnum.INSERT_ACHIEVEMENTS_ONLY_1_LANE.getPermissions()
                    | AuthorityEnum.INSERT_DEPARTURE_FOUL_ONLY_1_LANE.getPermissions()
                    | AuthorityEnum.INSERT_ARRIVAL_FOUL_ONLY_1_LANE.getPermissions()
                    | AuthorityEnum.UPDATE_ALL_DEPARTURE_FOUL.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 接力队员违规入水：非该棒运动员在比赛未结束时进入水中
     * <p>2026规则 Article 10.4.6</p>
     */
    RELAY_ILLEGAL_ENTRY("接力队员违规入水",
            AuthorityEnum.INSERT_ACHIEVEMENTS_ONLY_1_LANE.getPermissions()
                    | AuthorityEnum.INSERT_DEPARTURE_FOUL_ONLY_1_LANE.getPermissions()
                    | AuthorityEnum.INSERT_ARRIVAL_FOUL_ONLY_1_LANE.getPermissions()
                    | AuthorityEnum.UPDATE_ALL_DEPARTURE_FOUL.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 接力顺序错误：未按报名顺序游接力棒次
     * <p>2026规则 Article 10.4.2</p>
     */
    RELAY_ORDER_VIOLATION("接力顺序错误",
            AuthorityEnum.INSERT_ACHIEVEMENTS_ONLY_1_LANE.getPermissions()
                    | AuthorityEnum.INSERT_DEPARTURE_FOUL_ONLY_1_LANE.getPermissions()
                    | AuthorityEnum.INSERT_ARRIVAL_FOUL_ONLY_1_LANE.getPermissions()
                    | AuthorityEnum.UPDATE_ALL_DEPARTURE_FOUL.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 接力队未按报名名单顺序游进
     * <p>2026规则 Article 10.4.2</p>
     */
    RELAY_NOT_IN_ORDER_LISTED("接力队未按报名名单顺序游进",
            OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 接力交接未从出发台开始
     * <p>2026规则 Article 10.4.4</p>
     */
    RELAY_EXCHANGE_NOT_FROM_PLATFORM("接力交接未从出发台开始",
            OfficialPositionEnum.STARTER.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions()
                    | OfficialPositionEnum.TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_TURN_INSPECTOR.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 接力队员在完成比赛前重新入水
     * <p>2026规则 Article 10.4.6</p>
     */
    RELAY_REENTERED_WATER_BEFORE_FINISH("接力队员在完成比赛前重新入水",
            OfficialPositionEnum.STARTER.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions()
                    | OfficialPositionEnum.TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_TURN_INSPECTOR.getPermissions(),
            RaceResultCode.DQ),

    /* ==================== DQ（取消资格）— 通用行为/装备犯规 ==================== */

    /**
     * 游出泳道：在比赛过程中游入其他泳道
     * <p>2026规则 Article 10.2.2</p>
     */
    LANE_DEVIATION("游出泳道",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 干扰其他运动员：阻碍、干扰其他运动员正常比赛
     * <p>2026规则 Article 10.2.6</p>
     */
    INTERFERENCE("干扰其他运动员",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 拉拽分道线：比赛中拉拽分道线
     * <p>2026规则 Article 10.2.5</p>
     */
    LANE_ROPE_PULLING("拉拽分道线",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 池底行走：在自由泳或混合泳自由泳段比赛中行走（站立允许）
     * <p>2026规则 Article 10.2.4: 除自由泳外，站立或行走于池底均属违规</p>
     */
    WALKING_ON_BOTTOM("池底行走",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 在非自由泳比赛（或混合泳非自由泳段）站立于池底
     * <p>2026规则 Article 10.2.4</p>
     */
    STANDING_ON_BOTTOM("在非自由泳比赛中站立于池底",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 完成比赛后未尽快离开泳池
     * <p>2026规则 Article 10.2.8</p>
     */
    FAILED_LEAVE_POOL_SOON_AFTER_FINISH("完成比赛后未尽快离开泳池",
            OfficialPositionEnum.STARTER.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions()
                    | OfficialPositionEnum.TURN_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_TURN_INSPECTOR.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 使用配速设备或配速计划
     * <p>2026规则 Article 10.2.9</p>
     */
    PACE_MAKING_DEVICE_OR_PLAN("使用配速设备或配速计划",
            OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 未报名运动员进入水中
     * <p>2026规则 Article 10.2.7</p>
     */
    UNAUTHORIZED_ENTRY_DURING_RACE("未报名运动员在比赛进行中进入水中",
            OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 泳装违规：泳装不符合规定（如材质、覆盖范围等）
     * <p>2026规则 Article 14 (原SW6)</p>
     */
    ILLEGAL_SWIMWEAR("泳装违规",
            AuthorityEnum.UPDATE_ALL_LANE_ACHIEVEMENTS.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 佩戴违规设备：使用禁止的穿戴设备（如脚蹼、手蹼等）
     * <p>2026规则 Article 14 (原SW10.7)</p>
     */
    ILLEGAL_EQUIPMENT("佩戴违规设备",
            AuthorityEnum.UPDATE_ALL_LANE_ACHIEVEMENTS.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 广告违规：泳装或身体上的广告不符合规定
     */
    ADVERTISING_VIOLATION("广告违规",
            AuthorityEnum.UPDATE_ALL_LANE_ACHIEVEMENTS.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 延误比赛：故意延误出发或比赛进程
     * <p>2026规则 Article 4.4.2, 10.2.9</p>
     */
    DELAY_OF_GAME("延误比赛",
            AuthorityEnum.UPDATE_ALL_LANE_ACHIEVEMENTS.getPermissions(),
            RaceResultCode.DQ),

    /**
     * 不服从裁判指令：故意不服从裁判员的合法指令
     * <p>2026规则 Article 4.4</p>
     */
    DISOBEDIENCE("不服从裁判指令", 0b0,
            RaceResultCode.DQ),

    /**
     * 不当行为：比赛中的其他不当行为或违反体育道德行为
     */
    MISCONDUCT("不当行为", 0b0,
            RaceResultCode.DQ),

    /**
     * 其他犯规（导致取消资格）
     */
    OTHER_FOUL("其他犯规", 0b0,
            RaceResultCode.DQ),

    /* ==================== DNS（未出发） ==================== */

    /**
     * 检录未到：检录时未按时到达
     */
    CALL_ROOM_ABSENCE("检录未到", 0b0,
            RaceResultCode.DNS),

    /**
     * 弃权
     */
    ABSTAIN("弃权",
            OfficialPositionEnum.CHIEF_REFEREE.getPermissions()
                    | OfficialPositionEnum.TIMER.getPermissions(),
            RaceResultCode.DNS),

    /* ==================== DNF（未完成） ==================== */

    /**
     * 未完成全程：未按规定距离完成比赛
     * <p>2026规则 Article 10.2.1</p>
     */
    INCOMPLETE_DISTANCE("未完成全程",
            OfficialPositionEnum.STROKE_INSPECTOR.getPermissions()
                    | OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.DNF),

    /* ==================== 无犯规且成绩有效 ==================== */
    OK("无犯规且成绩有效",
            OfficialPositionEnum.TIMER.getPermissions()
                    |OfficialPositionEnum.CHIEF_REFEREE.getPermissions(),
            RaceResultCode.OK);

    private final String description;
    private final int permissions;
    private final RaceResultCode resultCode;

    FoulType(String description, int permissions, RaceResultCode resultCode) {
        this.description = description;
        this.permissions = permissions;
        this.resultCode = resultCode;
    }

    public String getDescription() {
        return description;
    }

    public int getPermissions() {
        return permissions;
    }

    /**
     * 获取该犯规对应的比赛结果代码
     *
     * @return DQ / DNS / DNF / SCR 等结果代码枚举
     */
    public RaceResultCode getResultCode() {
        return resultCode;
    }

    /**
     * 判断该犯规是否属于取消资格（DQ）
     */
    public boolean isDisqualification() {
        return resultCode == RaceResultCode.DQ || resultCode == RaceResultCode.DSQ;
    }

    /**
     * 判断该犯规是否属于未出发（DNS）
     */
    public boolean isDidNotStart() {
        return resultCode == RaceResultCode.DNS;
    }

    /**
     * 判断该犯规是否属于未完成（DNF）
     */
    public boolean isDidNotFinish() {
        return resultCode == RaceResultCode.DNF;
    }

    /**
     * 判断指定技术官员是否有权限上报/处理本犯规
     * <p>基于 {@link AuthorityEnum} 位掩码进行按位与运算：</p>
     * <pre>(this.permissions &amp; position.getPermissions()) != 0</pre>
     *
     * @param position 技术官员职位
     * @return true 表示该职位至少拥有一项处理此犯规所需的权限
     */
    public boolean canBeReportedBy(OfficialPositionEnum position) {
        return (this.permissions & position.getPermissions()) != 0;
    }

    /**
     * 判断本犯规是否需要指定权限位中的任意一项即可处理
     *
     * @param permissionMask 权限位掩码
     * @return true 表示该权限掩码与本犯规所需权限有交集
     */
    public boolean requiresAnyPermission(int permissionMask) {
        return (this.permissions & permissionMask) != 0;
    }

    /**
     * 判断本犯规是否需要指定权限位中的全部权限才能处理
     *
     * @param permissionMask 权限位掩码
     * @return true 表示本犯规所需权限包含该权限掩码的全部位
     */
    public boolean requiresAllPermissions(int permissionMask) {
        return (this.permissions & permissionMask) == permissionMask;
    }
}
