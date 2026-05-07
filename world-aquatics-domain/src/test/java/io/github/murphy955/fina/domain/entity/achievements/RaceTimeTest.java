package io.github.murphy955.fina.domain.entity.achievements;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RaceTime 单元测试
 *
 * @author 李泽聿
 * @since 2026/05/07
 */
class RaceTimeTest {

	// ==================== parse: 合法格式 ====================

	@ParameterizedTest
	@CsvSource({
			"1:03.79, 6379",
			"0:00.01, 1",
			"99:59.99, 599999",
			"10:30.50, 63050",
			"0:45.00, 4500"
	})
	@DisplayName("parse: 冒号格式合法输入应正确解析")
	void parseColonFormat(String input, long expectedHundredths) {
		RaceTime rt = RaceTime.parse(input);
		assertEquals(expectedHundredths, extractTotalHundredths(rt));
	}

	@ParameterizedTest
	@CsvSource({
			"1-03-79, 6379",
			"0-00-01, 1",
			"99-59-99, 599999",
			"10-30-50, 63050"
	})
	@DisplayName("parse: 连字符格式合法输入应正确解析")
	void parseMinusFormat(String input, long expectedHundredths) {
		RaceTime rt = RaceTime.parse(input);
		assertEquals(expectedHundredths, extractTotalHundredths(rt));
	}

	@ParameterizedTest
	@CsvSource({
			"63.79, 6379",
			"0.01, 1",
			"5999.99, 599999",
			"45.00, 4500",
			"100.50, 10050"
	})
	@DisplayName("parse: 纯秒格式合法输入应正确解析")
	void parseSecondsOnlyFormat(String input, long expectedHundredths) {
		RaceTime rt = RaceTime.parse(input);
		assertEquals(expectedHundredths, extractTotalHundredths(rt));
	}

	// ==================== parse: 非法输入 ====================

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {"  ", "\t"})
	@DisplayName("parse: null 或 blank 输入应抛出 IllegalArgumentException")
	void parseBlankInput(String input) {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> RaceTime.parse(input));
		assertTrue(ex.getMessage().contains("blank"));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"1:03:79",
			"1.03.79",
			"abc",
			"1:3.79",
			"1:03.7",
			"1:03.",
			":03.79",
			"1:-03.79",
			"1:03.79.00",
			"01:03",
			"63.7",
			"63.",
			".79"
	})
	@DisplayName("parse: 非法格式应抛出 IllegalArgumentException")
	void parseInvalidFormat(String input) {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> RaceTime.parse(input));
		assertTrue(ex.getMessage().contains("Invalid format"));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"1:60.00",
			"1:99.00"
	})
	@DisplayName("parse: 秒部分超过 59 应抛出 IllegalArgumentException")
	void parseSecondsOutOfRange(String input) {
		assertThrows(IllegalArgumentException.class, () -> RaceTime.parse(input));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"1:03.100",
			"1:03.999"
	})
	@DisplayName("parse: 百分位超过 99 应抛出 IllegalArgumentException")
	void parseHundredthsOutOfRange(String input) {
		assertThrows(IllegalArgumentException.class, () -> RaceTime.parse(input));
	}

	// ==================== 边界值 ====================

	@Test
	@DisplayName("parse: 零时间 0.00 应正确解析")
	void parseZeroTime() {
		RaceTime rt = RaceTime.parse("0.00");
		assertNotNull(rt);
		assertEquals("00.00", rt.toFinFormat());
	}

	@Test
	@DisplayName("parse: 最大允许时间 99:59.99 应正确解析")
	void parseMaxTime() {
		RaceTime rt = RaceTime.parse("99:59.99");
		assertNotNull(rt);
		assertEquals("99:59.99", rt.toFinFormat());
	}

	@Test
	@DisplayName("parse: 超过最大时间应抛出 IllegalArgumentException")
	void parseExceedsMaxTime() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> RaceTime.parse("100:00.00"));
		assertTrue(ex.getMessage().contains("exceeds maximum"));
	}

	@Test
	@DisplayName("parse: 负数纯秒格式应抛出 IllegalArgumentException")
	void parseNegativeSeconds() {
		assertThrows(IllegalArgumentException.class, () -> RaceTime.parse("-1.00"));
	}

	// ==================== toFinFormat ====================

	@ParameterizedTest
	@CsvSource({
			"1:03.79, 1:03.79",
			"0:45.00, 45.00",
			"0:00.01, 00.01",
			"10:05.09, 10:05.09",
			"99:59.99, 99:59.99"
	})
	@DisplayName("toFinFormat: 应按 FINA 格式正确输出")
	void toFinFormat(String input, String expected) {
		RaceTime rt = RaceTime.parse(input);
		assertEquals(expected, rt.toFinFormat());
	}

	// ==================== compareTo ====================

	@Test
	@DisplayName("compareTo: 较小时间应小于较大时间")
	void compareToLessThan() {
		RaceTime a = RaceTime.parse("1:03.79");
		RaceTime b = RaceTime.parse("1:03.80");
		assertTrue(a.compareTo(b) < 0);
	}

	@Test
	@DisplayName("compareTo: 较大时间应大于较小时间")
	void compareToGreaterThan() {
		RaceTime a = RaceTime.parse("1:03.80");
		RaceTime b = RaceTime.parse("1:03.79");
		assertTrue(a.compareTo(b) > 0);
	}

	@Test
	@DisplayName("compareTo: 相等时间应返回 0")
	void compareToEqual() {
		RaceTime a = RaceTime.parse("1:03.79");
		RaceTime b = RaceTime.parse("1-03-79");
		assertEquals(0, a.compareTo(b));
	}

	// ==================== equals / hashCode ====================

	@Test
	@DisplayName("equals: 相同总百分之一秒的两个实例应相等")
	void equalsSameValue() {
		RaceTime a = RaceTime.parse("1:03.79");
		RaceTime b = RaceTime.parse("1-03-79");
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
	}

	@Test
	@DisplayName("equals: 不同总百分之一秒的两个实例不应相等")
	void equalsDifferentValue() {
		RaceTime a = RaceTime.parse("1:03.79");
		RaceTime b = RaceTime.parse("1:03.80");
		assertNotEquals(a, b);
	}

	@Test
	@DisplayName("equals: 同一实例应等于自身")
	void equalsSameInstance() {
		RaceTime a = RaceTime.parse("1:03.79");
		assertEquals(a, a);
	}

	@Test
	@DisplayName("equals: 非 RaceTime 实例不应相等")
	void equalsNullAndOtherType() {
		RaceTime a = RaceTime.parse("1:03.79");
		assertNotEquals(a, null);
		assertNotEquals(a, "1:03.79");
	}

	// ==================== toString ====================

	@Test
	@DisplayName("toString: 应返回 toFinFormat 的结果")
	void toStringReturnsFinFormat() {
		RaceTime rt = RaceTime.parse("1:03.79");
		assertEquals(rt.toFinFormat(), rt.toString());
	}

	// ==================== 辅助方法 ====================

	/**
	 * 通过反射提取私有字段 totalHundredths，用于验证解析是否正确。
	 */
	private long extractTotalHundredths(RaceTime rt) {
		try {
			java.lang.reflect.Field field = RaceTime.class.getDeclaredField("totalHundredths");
			field.setAccessible(true);
			return (long) field.get(rt);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
