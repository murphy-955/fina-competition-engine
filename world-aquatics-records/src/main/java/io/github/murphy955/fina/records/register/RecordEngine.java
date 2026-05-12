package io.github.murphy955.fina.records.register;

import io.github.murphy955.fina.domain.entity.achievements.RaceTime;
import io.github.murphy955.fina.domain.entity.achievements.Result;
import io.github.murphy955.fina.domain.entity.athlete.Athlete;
import io.github.murphy955.fina.domain.shared.BaseGroup;
import io.github.murphy955.fina.records.filter.AbstractRecordFilterChain;

import java.util.List;

/**
 *
 *
 * @author : 李泽聿
 * @since : 2026:05:12 15:49
 */
public class RecordEngine {
    private List<AbstractRecordFilterChain<? extends Enum<? extends BaseGroup>>> recordFilterChain = new java.util.ArrayList<>();

    public void register(AbstractRecordFilterChain<? extends Enum<? extends BaseGroup>> recordFilterChain) {
        this.recordFilterChain.add(recordFilterChain);
    }

    public boolean evaluate(Result result) {
        for (AbstractRecordFilterChain<? extends Enum<? extends BaseGroup>> filter : recordFilterChain) {
            // TODO: (李泽聿 ,2026-05-12 17:33 ,[2026-05-17]) 具体的责任链filter业务待实现
        }
        return false;
    }
}
