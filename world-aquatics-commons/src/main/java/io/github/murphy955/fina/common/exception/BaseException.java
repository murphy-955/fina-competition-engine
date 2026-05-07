package io.github.murphy955.fina.common.exception;

import java.util.Collections;
import java.util.Map;

/**
 * 所有自定义异常的根类。
 * <p>
 * 通过 {@code messageKey} 与 {@code params} 支持国际化消息渲染，
 * 具体文本翻译由 {@code MessageSource} 在上层完成。
 *
 * @author 李泽聿
 * @since 2026/05/07
 */
public abstract class BaseException extends RuntimeException {

	/**
	 * 国际化消息键，如 {@code exception.validation.invalidResult}
	 */
	private final String messageKey;

	/**
	 * 消息参数，用于填充国际化模板中的占位符
	 */
	private final Map<String, Object> params;

	/**
	 * 引发异常的原始值（可选，用于日志和调试）
	 */
	private final Object offendingValue;

	/**
	 * 领域上下文信息，如实体ID、操作类型等（可选）
	 */
	private final Map<String, Object> context;

	protected BaseException(String messageKey) {
		this(messageKey, null, null, null, null);
	}

	protected BaseException(String messageKey, String detailMessage) {
		this(messageKey, detailMessage, null, null, null);
	}

	protected BaseException(String messageKey, String detailMessage, Throwable cause) {
		this(messageKey, detailMessage, cause, null, null);
	}

	protected BaseException(String messageKey, String detailMessage, Throwable cause,
							Object offendingValue, Map<String, Object> params) {
		super(detailMessage, cause);
		this.messageKey = messageKey;
		this.offendingValue = offendingValue;
		this.params = params == null ? Collections.emptyMap() : Collections.unmodifiableMap(params);
		this.context = Collections.emptyMap();
	}

	protected BaseException(String messageKey, String detailMessage, Throwable cause,
							Object offendingValue, Map<String, Object> params,
							Map<String, Object> context) {
		super(detailMessage, cause);
		this.messageKey = messageKey;
		this.offendingValue = offendingValue;
		this.params = params == null ? Collections.emptyMap() : Collections.unmodifiableMap(params);
		this.context = context == null ? Collections.emptyMap() : Collections.unmodifiableMap(context);
	}

	public String getMessageKey() {
		return messageKey;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public Object getOffendingValue() {
		return offendingValue;
	}

	public Map<String, Object> getContext() {
		return context;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" +
				"messageKey='" + messageKey + '\'' +
				", params=" + params +
				", offendingValue=" + offendingValue +
				", context=" + context +
				'}';
	}
}
