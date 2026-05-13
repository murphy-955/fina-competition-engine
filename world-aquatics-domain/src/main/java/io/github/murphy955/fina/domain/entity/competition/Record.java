package io.github.murphy955.fina.domain.entity.competition;

import io.github.murphy955.fina.domain.enm.EventType;
import io.github.murphy955.fina.domain.enm.Gender;
import io.github.murphy955.fina.domain.enm.Stroke;
import io.github.murphy955.fina.domain.entity.achievements.RaceTime;
import io.github.murphy955.fina.domain.service.RaceKeyBuilder;
import io.github.murphy955.fina.domain.shared.BaseGroup;

/**
 *
 *
 * @author : 李泽聿
 * @since : 2026:05:12 14:56
 */
public class Record<G extends Enum<G> & BaseGroup> {
    protected String key;

    private Gender gender;

    private G group;

    private String distance;

    private EventType event;

    private Stroke stroke;

    private RaceTime raceTime;


    public Record(Gender gender, String raceTime, G group, String distance, EventType event, Stroke stroke) {
        this.raceTime = RaceTime.parse(raceTime);
        this.key = RaceKeyBuilder.buildKey(gender, group, distance, event, stroke);
    }


    public RaceTime getRaceTime() {
        return raceTime;
    }

    public String getKey() {
        return key;
    }
}
