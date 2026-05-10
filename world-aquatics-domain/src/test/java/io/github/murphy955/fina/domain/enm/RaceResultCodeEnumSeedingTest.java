package io.github.murphy955.fina.domain.enm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RaceResultCodeEnum 编排排序规则单元测试
 * <p>
 * 验证预赛、半决赛、决赛编排时，成绩不为 OK 的运动员排序行为：
 * <ul>
 *     <li>只有 OK 参与正常成绩排名编排</li>
 *     <li>非 OK 排至所有 OK 之后</li>
 *     <li>非 OK 之间按默认 sortOrder 升序排列</li>
 * </ul>
 *
 * @author 李泽聿
 * @since 2026/05/10
 */
@DisplayName("RaceResultCodeEnum 编排排序规则")
class RaceResultCodeEnumSeedingTest {

	// ==================== 基本属性验证 ====================

	@Test
	@DisplayName("OK 的 sortOrder 应为 0，非 OK 的 sortOrder 应大于 0")
	void sortOrderValues() {
		assertEquals(0, RaceResultCodeEnum.OK.getSortOrder());
		assertTrue(RaceResultCodeEnum.DQ.getSortOrder() > 0);
		assertTrue(RaceResultCodeEnum.DNS.getSortOrder() > 0);
		assertTrue(RaceResultCodeEnum.DNF.getSortOrder() > 0);
		assertTrue(RaceResultCodeEnum.SCR.getSortOrder() > 0);
		assertTrue(RaceResultCodeEnum.DSQ.getSortOrder() > 0);
	}

	@Test
	@DisplayName("非 OK 之间的 sortOrder 应按 DQ < DNS < DNF < SCR < DSQ 排列")
	void nonOkSortOrderSequence() {
		assertTrue(RaceResultCodeEnum.DQ.getSortOrder() < RaceResultCodeEnum.DNS.getSortOrder());
		assertTrue(RaceResultCodeEnum.DNS.getSortOrder() < RaceResultCodeEnum.DNF.getSortOrder());
		assertTrue(RaceResultCodeEnum.DNF.getSortOrder() < RaceResultCodeEnum.SCR.getSortOrder());
		assertTrue(RaceResultCodeEnum.SCR.getSortOrder() < RaceResultCodeEnum.DSQ.getSortOrder());
	}

	// ==================== isQualified ====================

	@Test
	@DisplayName("isQualified: 只有 OK 返回 true")
	void isQualifiedOnlyOk() {
		assertTrue(RaceResultCodeEnum.OK.isQualified());
		assertFalse(RaceResultCodeEnum.DQ.isQualified());
		assertFalse(RaceResultCodeEnum.DNS.isQualified());
		assertFalse(RaceResultCodeEnum.DNF.isQualified());
		assertFalse(RaceResultCodeEnum.SCR.isQualified());
		assertFalse(RaceResultCodeEnum.DSQ.isQualified());
	}

	@ParameterizedTest
	@EnumSource(value = RaceResultCodeEnum.class, names = {"OK"}, mode = EnumSource.Mode.EXCLUDE)
	@DisplayName("isQualified: 所有非 OK 结果码均应返回 false")
	void isQualifiedNonOkAllFalse(RaceResultCodeEnum code) {
		assertFalse(code.isQualified(),
				code + " 不应被判定为可参与正常编排");
	}

	// ==================== isDisqualified ====================

	@Test
	@DisplayName("isDisqualified: 只有 DQ 和 DSQ 返回 true")
	void isDisqualifiedOnlyDqAndDsq() {
		assertTrue(RaceResultCodeEnum.DQ.isDisqualified());
		assertTrue(RaceResultCodeEnum.DSQ.isDisqualified());
		assertFalse(RaceResultCodeEnum.OK.isDisqualified());
		assertFalse(RaceResultCodeEnum.DNS.isDisqualified());
		assertFalse(RaceResultCodeEnum.DNF.isDisqualified());
		assertFalse(RaceResultCodeEnum.SCR.isDisqualified());
	}

	// ==================== SEEDING_COMPARATOR ====================

	@Test
	@DisplayName("SEEDING_COMPARATOR: OK 应小于任何非 OK")
	void seedingComparatorOkLessThanNonOk() {
		Comparator<RaceResultCodeEnum> cmp = RaceResultCodeEnum.SEEDING_COMPARATOR;
		assertTrue(cmp.compare(RaceResultCodeEnum.OK, RaceResultCodeEnum.DQ) < 0);
		assertTrue(cmp.compare(RaceResultCodeEnum.OK, RaceResultCodeEnum.DNS) < 0);
		assertTrue(cmp.compare(RaceResultCodeEnum.OK, RaceResultCodeEnum.DNF) < 0);
		assertTrue(cmp.compare(RaceResultCodeEnum.OK, RaceResultCodeEnum.SCR) < 0);
		assertTrue(cmp.compare(RaceResultCodeEnum.OK, RaceResultCodeEnum.DSQ) < 0);
	}

	@Test
	@DisplayName("SEEDING_COMPARATOR: 非 OK 之间应按 sortOrder 升序排列")
	void seedingComparatorNonOkOrder() {
		Comparator<RaceResultCodeEnum> cmp = RaceResultCodeEnum.SEEDING_COMPARATOR;
		assertTrue(cmp.compare(RaceResultCodeEnum.DQ, RaceResultCodeEnum.DNS) < 0);
		assertTrue(cmp.compare(RaceResultCodeEnum.DNS, RaceResultCodeEnum.DNF) < 0);
		assertTrue(cmp.compare(RaceResultCodeEnum.DNF, RaceResultCodeEnum.SCR) < 0);
		assertTrue(cmp.compare(RaceResultCodeEnum.SCR, RaceResultCodeEnum.DSQ) < 0);
	}

	@Test
	@DisplayName("SEEDING_COMPARATOR: 相同结果码应相等")
	void seedingComparatorEquality() {
		Comparator<RaceResultCodeEnum> cmp = RaceResultCodeEnum.SEEDING_COMPARATOR;
		assertEquals(0, cmp.compare(RaceResultCodeEnum.DQ, RaceResultCodeEnum.DQ));
		assertEquals(0, cmp.compare(RaceResultCodeEnum.OK, RaceResultCodeEnum.OK));
	}

	// ==================== 枚举自然顺序 ====================

	@Test
	@DisplayName("枚举自然顺序: values() 数组应保证 OK 在索引 0，其余按 sortOrder 递增")
	void enumNaturalOrder() {
		RaceResultCodeEnum[] values = RaceResultCodeEnum.values();
		assertEquals(RaceResultCodeEnum.OK, values[0]);
		assertEquals(RaceResultCodeEnum.DQ, values[1]);
		assertEquals(RaceResultCodeEnum.DNS, values[2]);
		assertEquals(RaceResultCodeEnum.DNF, values[3]);
		assertEquals(RaceResultCodeEnum.SCR, values[4]);
		assertEquals(RaceResultCodeEnum.DSQ, values[5]);
	}

	// ==================== 编排场景模拟 ====================

	@Test
	@DisplayName("编排场景: 全部 OK 时，排序后顺序不变且均 qualified")
	void seedingAllOk() {
		List<RaceResultCodeEnum> input = Arrays.asList(
				RaceResultCodeEnum.OK, RaceResultCodeEnum.OK, RaceResultCodeEnum.OK
		);
		List<RaceResultCodeEnum> sorted = input.stream()
				.sorted(RaceResultCodeEnum.SEEDING_COMPARATOR)
				.toList();

		assertEquals(3, sorted.size());
		assertTrue(sorted.stream().allMatch(RaceResultCodeEnum::isQualified));
	}

	@Test
	@DisplayName("编排场景: 全部非 OK 时，应按 DQ -> DNS -> DNF -> SCR -> DSQ 排序")
	void seedingAllNonOk() {
		List<RaceResultCodeEnum> input = Arrays.asList(
				RaceResultCodeEnum.DSQ, RaceResultCodeEnum.DNS,
				RaceResultCodeEnum.DQ, RaceResultCodeEnum.SCR,
				RaceResultCodeEnum.DNF
		);
		List<RaceResultCodeEnum> sorted = input.stream()
				.sorted(RaceResultCodeEnum.SEEDING_COMPARATOR)
				.toList();

		assertEquals(
				Arrays.asList(
						RaceResultCodeEnum.DQ, RaceResultCodeEnum.DNS,
						RaceResultCodeEnum.DNF, RaceResultCodeEnum.SCR,
						RaceResultCodeEnum.DSQ
				),
				sorted
		);
		assertTrue(sorted.stream().noneMatch(RaceResultCodeEnum::isQualified));
	}

	@Test
	@DisplayName("编排场景: 混合 OK 与非 OK 时，OK 全部在前，非 OK 按序在后")
	void seedingMixedOkAndNonOk() {
		List<RaceResultCodeEnum> input = Arrays.asList(
				RaceResultCodeEnum.DQ,
				RaceResultCodeEnum.OK,
				RaceResultCodeEnum.DNF,
				RaceResultCodeEnum.OK,
				RaceResultCodeEnum.DNS,
				RaceResultCodeEnum.OK,
				RaceResultCodeEnum.DSQ,
				RaceResultCodeEnum.SCR
		);
		List<RaceResultCodeEnum> sorted = input.stream()
				.sorted(RaceResultCodeEnum.SEEDING_COMPARATOR)
				.toList();

		// 前 3 个必须是 OK
		assertEquals(RaceResultCodeEnum.OK, sorted.get(0));
		assertEquals(RaceResultCodeEnum.OK, sorted.get(1));
		assertEquals(RaceResultCodeEnum.OK, sorted.get(2));

		// 后 5 个必须是 DQ, DNS, DNF, SCR, DSQ
		assertEquals(RaceResultCodeEnum.DQ, sorted.get(3));
		assertEquals(RaceResultCodeEnum.DNS, sorted.get(4));
		assertEquals(RaceResultCodeEnum.DNF, sorted.get(5));
		assertEquals(RaceResultCodeEnum.SCR, sorted.get(6));
		assertEquals(RaceResultCodeEnum.DSQ, sorted.get(7));
	}

	@Test
	@DisplayName("编排场景: 预赛成绩混合后，只有 OK 运动员进入下一轮编排池")
	void prelimSeedingOnlyOkAdvance() {
		// 模拟 8 名运动员的预赛成绩结果
		List<MockRaceResult> results = Arrays.asList(
				new MockRaceResult("A1", RaceResultCodeEnum.OK, 58_50),   // 0:58.50
				new MockRaceResult("A2", RaceResultCodeEnum.DQ, 0),       // 取消资格
				new MockRaceResult("A3", RaceResultCodeEnum.OK, 57_10),   // 0:57.10
				new MockRaceResult("A4", RaceResultCodeEnum.DNS, 0),      // 未出发
				new MockRaceResult("A5", RaceResultCodeEnum.OK, 59_00),   // 0:59.00
				new MockRaceResult("A6", RaceResultCodeEnum.DNF, 0),      // 未完成
				new MockRaceResult("A7", RaceResultCodeEnum.OK, 56_80),   // 0:56.80
				new MockRaceResult("A8", RaceResultCodeEnum.SCR, 0)       // 赛前退出
		);

		// 按编排规则排序：先按结果码分组，OK 在前；OK 内部按成绩升序
		List<MockRaceResult> sorted = results.stream()
				.sorted(Comparator
						.comparing((MockRaceResult r) -> r.getResultCode().getSortOrder())
						.thenComparing(MockRaceResult::getHundredths))
				.toList();

		// 提取qualified的运动员
		List<MockRaceResult> qualified = sorted.stream()
				.filter(r -> r.getResultCode().isQualified())
				.toList();

		// 验证排序结果
		assertEquals("A7", sorted.get(0).getAthleteId()); // 0:56.80 OK
		assertEquals("A3", sorted.get(1).getAthleteId()); // 0:57.10 OK
		assertEquals("A1", sorted.get(2).getAthleteId()); // 0:58.50 OK
		assertEquals("A5", sorted.get(3).getAthleteId()); // 0:59.00 OK
		assertEquals(RaceResultCodeEnum.DQ, sorted.get(4).getResultCode());
		assertEquals(RaceResultCodeEnum.DNS, sorted.get(5).getResultCode());
		assertEquals(RaceResultCodeEnum.DNF, sorted.get(6).getResultCode());
		assertEquals(RaceResultCodeEnum.SCR, sorted.get(7).getResultCode());

		// 只有 4 人进入下一轮编排池
		assertEquals(4, qualified.size());
		assertTrue(qualified.stream().allMatch(r -> r.getResultCode() == RaceResultCodeEnum.OK));
	}

	@Test
	@DisplayName("编排场景: 半决赛成绩全部为 DQ/DNS/DNF/SCR/DSQ 时，无人进入决赛编排池")
	void semiFinalAllNonOkNoOneAdvances() {
		List<RaceResultCodeEnum> semiResults = Arrays.asList(
				RaceResultCodeEnum.DQ,
				RaceResultCodeEnum.DNS,
				RaceResultCodeEnum.DNF,
				RaceResultCodeEnum.SCR,
				RaceResultCodeEnum.DSQ,
				RaceResultCodeEnum.DQ,
				RaceResultCodeEnum.DNS,
				RaceResultCodeEnum.DNF
		);

		long qualifiedCount = semiResults.stream()
				.filter(RaceResultCodeEnum::isQualified)
				.count();

		assertEquals(0, qualifiedCount);

		// 排序验证：全部非 OK，按 sortOrder 排列
		List<RaceResultCodeEnum> sorted = semiResults.stream()
				.sorted(RaceResultCodeEnum.SEEDING_COMPARATOR)
				.toList();

		assertEquals(RaceResultCodeEnum.DQ, sorted.get(0));
		assertEquals(RaceResultCodeEnum.DQ, sorted.get(1));
		assertEquals(RaceResultCodeEnum.DNS, sorted.get(2));
		assertEquals(RaceResultCodeEnum.DNS, sorted.get(3));
		assertEquals(RaceResultCodeEnum.DNF, sorted.get(4));
		assertEquals(RaceResultCodeEnum.DNF, sorted.get(5));
		assertEquals(RaceResultCodeEnum.SCR, sorted.get(6));
		assertEquals(RaceResultCodeEnum.DSQ, sorted.get(7));
	}

	@Test
	@DisplayName("编排场景: 决赛成绩中第一名 OK，其余非 OK，冠军有效但无后续排名")
	void finalOnlyOneOk() {
		List<MockRaceResult> finals = Arrays.asList(
				new MockRaceResult("F1", RaceResultCodeEnum.OK, 55_00),
				new MockRaceResult("F2", RaceResultCodeEnum.DQ, 0),
				new MockRaceResult("F3", RaceResultCodeEnum.DNS, 0),
				new MockRaceResult("F4", RaceResultCodeEnum.DNF, 0),
				new MockRaceResult("F5", RaceResultCodeEnum.DSQ, 0),
				new MockRaceResult("F6", RaceResultCodeEnum.SCR, 0),
				new MockRaceResult("F7", RaceResultCodeEnum.DQ, 0),
				new MockRaceResult("F8", RaceResultCodeEnum.DNS, 0)
		);

		List<MockRaceResult> ranked = finals.stream()
				.sorted(Comparator
						.comparing((MockRaceResult r) -> r.getResultCode().getSortOrder())
						.thenComparing(MockRaceResult::getHundredths))
				.toList();

		// 冠军是 F1
		assertEquals("F1", ranked.get(0).getAthleteId());
		assertEquals(RaceResultCodeEnum.OK, ranked.get(0).getResultCode());

		// 其余全部是非 OK，排在后面
		List<MockRaceResult> nonRanked = ranked.subList(1, ranked.size());
		assertTrue(nonRanked.stream().noneMatch(r -> r.getResultCode().isQualified()));
	}

	// ==================== fromCode 边界 ====================

	@ParameterizedTest
	@CsvSource({
			"OK, OK",
			"ok, OK",
			"Ok, OK",
			"DQ, DQ",
			"dq, DQ",
			"DNS, DNS",
			"DNF, DNF",
			"SCR, SCR",
			"DSQ, DSQ"
	})
	@DisplayName("fromCode: 大小写不敏感应正确解析")
	void fromCodeCaseInsensitive(String input, RaceResultCodeEnum expected) {
		assertEquals(expected, RaceResultCodeEnum.fromCode(input));
	}

	@Test
	@DisplayName("fromCode: 非法代码应返回 null")
	void fromCodeInvalidReturnsNull() {
		assertNull(RaceResultCodeEnum.fromCode("XYZ"));
		assertNull(RaceResultCodeEnum.fromCode(""));
		assertNull(RaceResultCodeEnum.fromCode(" "));
		assertNull(RaceResultCodeEnum.fromCode(null));
	}

	// ==================== 辅助 Mock 类 ====================

	/**
	 * 模拟比赛成绩记录，用于编排场景测试
	 */
	private static class MockRaceResult {
		private final String athleteId;
		private final RaceResultCodeEnum resultCode;
		private final int hundredths;

		MockRaceResult(String athleteId, RaceResultCodeEnum resultCode, int hundredths) {
			this.athleteId = athleteId;
			this.resultCode = resultCode;
			this.hundredths = hundredths;
		}

		String getAthleteId() {
			return athleteId;
		}

		RaceResultCodeEnum getResultCode() {
			return resultCode;
		}

		int getHundredths() {
			return hundredths;
		}
	}
}
