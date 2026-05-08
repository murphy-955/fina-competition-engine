package io.github.murphy955.fina.competition.service;

import io.github.murphy955.fina.common.exception.ValidationException;
import io.github.murphy955.fina.domain.enm.EventTypeEnum;
import io.github.murphy955.fina.domain.enm.GenderEnum;
import io.github.murphy955.fina.domain.enm.StrokeEnum;
import io.github.murphy955.fina.domain.shared.BaseGroup;


/**
 * 自动生成{@code SeedingStrategy}的key
 *
 * @author : 李泽聿
 * @since : 2026:05:08 10:47
 */
public class RaceKeyRegister<G extends Enum<G> & BaseGroup> {

    public RaceKeyRegister() {
    }

    /**
     * 对于<strong>项目</strong>，生成的key为：{@code gender-group-distance-event-stroke}<br>
     * 举个栗子：<br>
     * <ul>
     *     <li>{@code MALE-U18-100-BREASTSTROKE-INDIVIDUAL}对应<strong>男子U18组100米个人蛙泳</strong></li>
     *     <li>{@code MALE-U18-4*100-BREASTSTROKE-FREESTYLE}对应<strong>男子U18组4*100米自由泳接力</strong></li>
     * </ul>
     *
     * @param gender   性别
     * @param group    用户自定义的分组
     * @param distance 项目长度
     * @param event    项目类型（个人/团体）
     * @param stroke   泳姿
     * @return String
     * @author 李泽聿
     * @since 2026-05-08 10:57
     */
    public String buildKey(GenderEnum gender, G group, String distance, EventTypeEnum event, StrokeEnum stroke) {
        if (gender == null || group == null || distance == null || event == null || stroke == null) {
            throw new ValidationException("所有传入的参数不能为空");
        }
        return String.join("-", gender.name(), group.getName(), distance, event.name(), stroke.name());
    }
}
