package io.github.murphy955.fina.common.exception;

import java.util.Map;

/**
 * 参数校验异常。
 * <p>
 * 当传入的参数违反基本约束（如非法格式、越界、空值等）时抛出，
 * 通常发生在值对象构造或方法入口的合法性检查阶段。
 *
 * @author 李泽聿
 * @since 2026/05/07
 */
public class ValidationException extends BaseException {

	public ValidationException(String messageKey) {
		super(messageKey);
	}

	public ValidationException(String messageKey, String detailMessage) {
		super(messageKey, detailMessage);
	}

	public ValidationException(String messageKey, String detailMessage, Throwable cause) {
		super(messageKey, detailMessage, cause);
	}

	public ValidationException(String messageKey, String detailMessage,
							   Object offendingValue, Map<String, Object> params) {
		super(messageKey, detailMessage, null, offendingValue, params);
	}

	public ValidationException(String messageKey, String detailMessage, Throwable cause,
							   Object offendingValue, Map<String, Object> params) {
		super(messageKey, detailMessage, cause, offendingValue, params);
	}
}
