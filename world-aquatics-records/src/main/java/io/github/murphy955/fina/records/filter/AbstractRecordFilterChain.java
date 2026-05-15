package io.github.murphy955.fina.records.filter;

import io.github.murphy955.fina.domain.shared.BaseGroup;
import io.github.murphy955.fina.domain.entity.competition.Record;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 记录处理抽象责任链。
 *
 * <p>TODO: 引入 {@code BreakPolicy} 枚举，让用户自定义“该级别破纪录后是否阻断后续遍历”。
 * <ul>
 *   <li>{@code BREAK_ON_RECORD} — 该 filter 判定破纪录后，停止检查更低优先级的 filter。</li>
 *   <li>{@code CONTINUE} — 该 filter 判定破纪录后，继续检查更低优先级的 filter（默认行为）。</li>
 * </ul>
 * 进阶：如需按时间差等条件动态决策，可再叠加 {@code EvaluationInterceptor} 策略接口。
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
