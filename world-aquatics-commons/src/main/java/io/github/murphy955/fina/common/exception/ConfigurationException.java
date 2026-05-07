package io.github.murphy955.fina.common.exception;

import java.util.Map;

/**
 * 配置异常。
 * <p>
 * 当外部配置缺失、格式错误或值不合法时抛出，
 * 通常发生在启动阶段或配置解析阶段。
 *
 * @author 李泽聿
 * @since 2026/05/07
 */
public class ConfigurationException extends BaseException {

	public ConfigurationException(String messageKey) {
		super(messageKey);
	}

	public ConfigurationException(String messageKey, String detailMessage) {
		super(messageKey, detailMessage);
	}

	public ConfigurationException(String messageKey, String detailMessage, Throwable cause) {
		super(messageKey, detailMessage, cause);
	}

	public ConfigurationException(String messageKey, String detailMessage,
								  Object offendingValue, Map<String, Object> params) {
		super(messageKey, detailMessage, null, offendingValue, params);
	}

	public ConfigurationException(String messageKey, String detailMessage, Throwable cause,
								  Object offendingValue, Map<String, Object> params) {
		super(messageKey, detailMessage, cause, offendingValue, params);
	}
}
