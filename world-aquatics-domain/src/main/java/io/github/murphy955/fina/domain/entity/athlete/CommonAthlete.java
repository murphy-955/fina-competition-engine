package io.github.murphy955.fina.domain.entity.athlete;

import io.github.murphy955.fina.domain.entity.achievements.RaceTime;

/**
 * 普通运动员
 *
 * @author : 李泽聿
 * @since : 2026:05:07 14:52
 */
public class CommonAthlete extends Athlete{
    public CommonAthlete(String name, RaceTime raceTime) {
        super(name, raceTime);
    }
}
