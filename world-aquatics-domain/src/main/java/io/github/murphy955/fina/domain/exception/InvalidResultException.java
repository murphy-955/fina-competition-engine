package io.github.murphy955.fina.domain.exception;

import io.github.murphy955.fina.common.exception.ValidationException;

import java.util.Map;

/**
 * 传入的比赛成绩有误。
 * <p>
 * 当上传/录入的比赛成绩不符合规范时抛出，例如：
 * <ul>
 *     <li>成绩格式非法（如无法解析的时间字符串）</li>
 *     <li>成绩为负数或零</li>
 *     <li>成绩超出该项目合理范围</li>
 *     <li>成绩精度不符合要求（非百分之一秒）</li>
 *     <li>成绩与计时系统类型不匹配</li>
 * </ul>
 *
 * @author 李泽聿
 * @since 2026/05/07
 */
public class InvalidResultException extends ValidationException {

	/**
	 * 默认消息键
	 */
	public static final String DEFAULT_MESSAGE_KEY = "exception.domain.invalidResult";

	public InvalidResultException() {
		super(DEFAULT_MESSAGE_KEY);
	}

	public InvalidResultException(String detailMessage) {
		super(DEFAULT_MESSAGE_KEY, detailMessage);
	}

	public InvalidResultException(String detailMessage, Throwable cause) {
		super(DEFAULT_MESSAGE_KEY, detailMessage, cause);
	}

	public InvalidResultException(String detailMessage, Object offendingValue, Map<String, Object> params) {
		super(DEFAULT_MESSAGE_KEY, detailMessage, offendingValue, params);
	}

	public InvalidResultException(String detailMessage, Throwable cause,
								  Object offendingValue, Map<String, Object> params) {
		super(DEFAULT_MESSAGE_KEY, detailMessage, cause, offendingValue, params);
	}
}
