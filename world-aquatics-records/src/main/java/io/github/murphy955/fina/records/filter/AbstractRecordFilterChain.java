package io.github.murphy955.fina.records.filter;

import io.github.murphy955.fina.domain.shared.BaseGroup;
import io.github.murphy955.fina.domain.entity.competition.Record;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 记录处理抽象责任链
 *
 * @author : 李泽聿
 * @since : 2026:05:12 14:48
 */
public abstract class AbstractRecordFilterChain<G extends Enum<G> & BaseGroup> {
    protected int priority;

    private String recordLevel;

    private Map<String,Record<G>> recordMap;

    public AbstractRecordFilterChain(int priority, String recordLevel, Map<String,Record<G>> recordMap) {
        this.priority = priority;
        this.recordLevel = recordLevel;
        this.recordMap = recordMap;
    }

    public AbstractRecordFilterChain(int priority, String recordLevel,List<Record<G>> recordList){
        Map<String,Record<G>> recordMap = recordList.stream()
                .collect(Collectors.toMap(Record::getKey, record -> record));
        this.priority = priority;
        this.recordLevel = recordLevel;
        this.recordMap = recordMap;
    }

    public int getPriority() {
        return priority;
    }

    public String getRecordLevel() {
        return recordLevel;
    }

    public Map<String,Record<G>> getRecordMap(){
        return recordMap;
    }
}
