package io.github.murphy955.fina.domain.entity.project;

import io.github.murphy955.fina.domain.enm.EventType;
import io.github.murphy955.fina.domain.enm.Gender;
import io.github.murphy955.fina.domain.enm.Stroke;
import io.github.murphy955.fina.domain.entity.achievements.Result;
import io.github.murphy955.fina.domain.entity.athlete.Athlete;
import io.github.murphy955.fina.domain.service.RaceKeyRegister;
import io.github.murphy955.fina.domain.shared.BaseGroup;

import java.io.Serial;
import java.io.Serializable;

/**
 * 比赛单项
 *
 * @author : 李泽聿
 * @since : 2026:05:13 14:22
 */
public class Project<G extends Enum<G> & BaseGroup> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Athlete athlete;

    private Result result;

    private String key;

    public Project(Athlete athlete, Result result, Gender gender, G group, String distance, EventType event, Stroke stroke) {
        this.athlete = athlete;
        this.result = result;
        this.key = new RaceKeyRegister<G>().buildKey(gender, group, distance, event, stroke);
    }

    public Project() {
    }

    public Athlete getAthlete() {
        return athlete;
    }

    public Result getResult() {
        return result;
    }

    public String getKey() {
        return key;
    }
}
