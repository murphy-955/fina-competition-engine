package io.github.murphy955.fina.common.exception;

import java.util.Map;

/**
 * 领域规则异常。
 * <p>
 * 当操作违反业务规则（如非法状态转换、组合冲突、规则违背等）时抛出，
 * 通常发生在实体方法或领域服务中。
 *
 * @author 李泽聿
 * @since 2026/05/07
 */
public class DomainException extends BaseException {

	public DomainException(String messageKey) {
		super(messageKey);
	}

	public DomainException(String messageKey, String detailMessage) {
		super(messageKey, detailMessage);
	}

	public DomainException(String messageKey, String detailMessage, Throwable cause) {
		super(messageKey, detailMessage, cause);
	}

	public DomainException(String messageKey, String detailMessage,
						   Object offendingValue, Map<String, Object> params) {
		super(messageKey, detailMessage, null, offendingValue, params);
	}

	public DomainException(String messageKey, String detailMessage, Throwable cause,
						   Object offendingValue, Map<String, Object> params) {
		super(messageKey, detailMessage, cause, offendingValue, params);
	}
}
