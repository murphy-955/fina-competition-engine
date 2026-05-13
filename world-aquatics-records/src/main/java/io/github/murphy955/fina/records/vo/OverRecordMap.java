package io.github.murphy955.fina.records.vo;

import io.github.murphy955.fina.domain.entity.achievements.RaceTime;
import io.github.murphy955.fina.domain.shared.BaseGroup;
import io.github.murphy955.fina.domain.vo.RaceInfo;

/**
 * 破纪录的映射
 *
 * @author : 李泽聿
 * @since : 2026:05:13 15:20
 */
public class OverRecordMap<G extends Enum<G> & BaseGroup> {
    private RaceInfo<G> raceInfo;

    private final RaceTime oldRecordTime;

    private final RaceTime newRecordTime;

    private final boolean isOverRecord;

    private final String recordLevel;

    public OverRecordMap(RaceInfo<G> raceInfo, RaceTime oldRecordTime, RaceTime newRecordTime,String recordLevel) {
        this.raceInfo = raceInfo;
        this.oldRecordTime = oldRecordTime;
        this.newRecordTime = newRecordTime;
        this.isOverRecord = newRecordTime.compareTo(oldRecordTime) < 0;
        this.recordLevel = recordLevel;
    }

    public RaceTime getOldRecordTime() {
        return oldRecordTime;
    }

    public RaceTime getNewRecordTime() {
        return newRecordTime;
    }

    public boolean isOverRecord() {
        return isOverRecord;
    }

    public String getRecordLevel() {
        return recordLevel;
    }
}
