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
 * RaceResultCode 编排排序规则单元测试
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
@DisplayName("RaceResultCode 编排排序规则")
class RaceResultCodeSeedingTest {

	// ==================== 基本属性验证 ====================

	@Test
	@DisplayName("OK 的 sortOrder 应为 0，非 OK 的 sortOrder 应大于 0")
	void sortOrderValues() {
		assertEquals(0, RaceResultCode.OK.getSortOrder());
		assertTrue(RaceResultCode.DQ.getSortOrder() > 0);
		assertTrue(RaceResultCode.DNS.getSortOrder() > 0);
		assertTrue(RaceResultCode.DNF.getSortOrder() > 0);
		assertTrue(RaceResultCode.SCR.getSortOrder() > 0);
		assertTrue(RaceResultCode.DSQ.getSortOrder() > 0);
	}

	@Test
	@DisplayName("非 OK 之间的 sortOrder 应按 DQ < DNS < DNF < SCR < DSQ 排列")
	void nonOkSortOrderSequence() {
		assertTrue(RaceResultCode.DQ.getSortOrder() < RaceResultCode.DNS.getSortOrder());
		assertTrue(RaceResultCode.DNS.getSortOrder() < RaceResultCode.DNF.getSortOrder());
		assertTrue(RaceResultCode.DNF.getSortOrder() < RaceResultCode.SCR.getSortOrder());
		assertTrue(RaceResultCode.SCR.getSortOrder() < RaceResultCode.DSQ.getSortOrder());
	}

	// ==================== isQualified ====================

	@Test
	@DisplayName("isQualified: 只有 OK 返回 true")
	void isQualifiedOnlyOk() {
		assertTrue(RaceResultCode.OK.isQualified());
		assertFalse(RaceResultCode.DQ.isQualified());
		assertFalse(RaceResultCode.DNS.isQualified());
		assertFalse(RaceResultCode.DNF.isQualified());
		assertFalse(RaceResultCode.SCR.isQualified());
		assertFalse(RaceResultCode.DSQ.isQualified());
	}

	@ParameterizedTest
	@EnumSource(value = RaceResultCode.class, names = {"OK"}, mode = EnumSource.Mode.EXCLUDE)
	@DisplayName("isQualified: 所有非 OK 结果码均应返回 false")
	void isQualifiedNonOkAllFalse(RaceResultCode code) {
		assertFalse(code.isQualified(),
				code + " 不应被判定为可参与正常编排");
	}

	// ==================== isDisqualified ====================

	@Test
	@DisplayName("isDisqualified: 只有 DQ 和 DSQ 返回 true")
	void isDisqualifiedOnlyDqAndDsq() {
		assertTrue(RaceResultCode.DQ.isDisqualified());
		assertTrue(RaceResultCode.DSQ.isDisqualified());
		assertFalse(RaceResultCode.OK.isDisqualified());
		assertFalse(RaceResultCode.DNS.isDisqualified());
		assertFalse(RaceResultCode.DNF.isDisqualified());
		assertFalse(RaceResultCode.SCR.isDisqualified());
	}

	// ==================== SEEDING_COMPARATOR ====================

	@Test
	@DisplayName("SEEDING_COMPARATOR: OK 应小于任何非 OK")
	void seedingComparatorOkLessThanNonOk() {
		Comparator<RaceResultCode> cmp = RaceResultCode.SEEDING_COMPARATOR;
		assertTrue(cmp.compare(RaceResultCode.OK, RaceResultCode.DQ) < 0);
		assertTrue(cmp.compare(RaceResultCode.OK, RaceResultCode.DNS) < 0);
		assertTrue(cmp.compare(RaceResultCode.OK, RaceResultCode.DNF) < 0);
		assertTrue(cmp.compare(RaceResultCode.OK, RaceResultCode.SCR) < 0);
		assertTrue(cmp.compare(RaceResultCode.OK, RaceResultCode.DSQ) < 0);
	}

	@Test
	@DisplayName("SEEDING_COMPARATOR: 非 OK 之间应按 sortOrder 升序排列")
	void seedingComparatorNonOkOrder() {
		Comparator<RaceResultCode> cmp = RaceResultCode.SEEDING_COMPARATOR;
		assertTrue(cmp.compare(RaceResultCode.DQ, RaceResultCode.DNS) < 0);
		assertTrue(cmp.compare(RaceResultCode.DNS, RaceResultCode.DNF) < 0);
		assertTrue(cmp.compare(RaceResultCode.DNF, RaceResultCode.SCR) < 0);
		assertTrue(cmp.compare(RaceResultCode.SCR, RaceResultCode.DSQ) < 0);
	}

	@Test
	@DisplayName("SEEDING_COMPARATOR: 相同结果码应相等")
	void seedingComparatorEquality() {
		Comparator<RaceResultCode> cmp = RaceResultCode.SEEDING_COMPARATOR;
		assertEquals(0, cmp.compare(RaceResultCode.DQ, RaceResultCode.DQ));
		assertEquals(0, cmp.compare(RaceResultCode.OK, RaceResultCode.OK));
	}

	// ==================== 枚举自然顺序 ====================

	@Test
	@DisplayName("枚举自然顺序: values() 数组应保证 OK 在索引 0，其余按 sortOrder 递增")
	void enumNaturalOrder() {
		RaceResultCode[] values = RaceResultCode.values();
		assertEquals(RaceResultCode.OK, values[0]);
		assertEquals(RaceResultCode.DQ, values[1]);
		assertEquals(RaceResultCode.DNS, values[2]);
		assertEquals(RaceResultCode.DNF, values[3]);
		assertEquals(RaceResultCode.SCR, values[4]);
		assertEquals(RaceResultCode.DSQ, values[5]);
	}

	// ==================== 编排场景模拟 ====================

	@Test
	@DisplayName("编排场景: 全部 OK 时，排序后顺序不变且均 qualified")
	void seedingAllOk() {
		List<RaceResultCode> input = Arrays.asList(
				RaceResultCode.OK, RaceResultCode.OK, RaceResultCode.OK
		);
		List<RaceResultCode> sorted = input.stream()
				.sorted(RaceResultCode.SEEDING_COMPARATOR)
				.toList();

		assertEquals(3, sorted.size());
		assertTrue(sorted.stream().allMatch(RaceResultCode::isQualified));
	}

	@Test
	@DisplayName("编排场景: 全部非 OK 时，应按 DQ -> DNS -> DNF -> SCR -> DSQ 排序")
	void seedingAllNonOk() {
		List<RaceResultCode> input = Arrays.asList(
				RaceResultCode.DSQ, RaceResultCode.DNS,
				RaceResultCode.DQ, RaceResultCode.SCR,
				RaceResultCode.DNF
		);
		List<RaceResultCode> sorted = input.stream()
				.sorted(RaceResultCode.SEEDING_COMPARATOR)
				.toList();

		assertEquals(
				Arrays.asList(
						RaceResultCode.DQ, RaceResultCode.DNS,
						RaceResultCode.DNF, RaceResultCode.SCR,
						RaceResultCode.DSQ
				),
				sorted
		);
		assertTrue(sorted.stream().noneMatch(RaceResultCode::isQualified));
	}

	@Test
	@DisplayName("编排场景: 混合 OK 与非 OK 时，OK 全部在前，非 OK 按序在后")
	void seedingMixedOkAndNonOk() {
		List<RaceResultCode> input = Arrays.asList(
				RaceResultCode.DQ,
				RaceResultCode.OK,
				RaceResultCode.DNF,
				RaceResultCode.OK,
				RaceResultCode.DNS,
				RaceResultCode.OK,
				RaceResultCode.DSQ,
				RaceResultCode.SCR
		);
		List<RaceResultCode> sorted = input.stream()
				.sorted(RaceResultCode.SEEDING_COMPARATOR)
				.toList();

		// 前 3 个必须是 OK
		assertEquals(RaceResultCode.OK, sorted.get(0));
		assertEquals(RaceResultCode.OK, sorted.get(1));
		assertEquals(RaceResultCode.OK, sorted.get(2));

		// 后 5 个必须是 DQ, DNS, DNF, SCR, DSQ
		assertEquals(RaceResultCode.DQ, sorted.get(3));
		assertEquals(RaceResultCode.DNS, sorted.get(4));
		assertEquals(RaceResultCode.DNF, sorted.get(5));
		assertEquals(RaceResultCode.SCR, sorted.get(6));
		assertEquals(RaceResultCode.DSQ, sorted.get(7));
	}

	@Test
	@DisplayName("编排场景: 预赛成绩混合后，只有 OK 运动员进入下一轮编排池")
	void prelimSeedingOnlyOkAdvance() {
		// 模拟 8 名运动员的预赛成绩结果
		List<MockRaceResult> results = Arrays.asList(
				new MockRaceResult("A1", RaceResultCode.OK, 58_50),   // 0:58.50
				new MockRaceResult("A2", RaceResultCode.DQ, 0),       // 取消资格
				new MockRaceResult("A3", RaceResultCode.OK, 57_10),   // 0:57.10
				new MockRaceResult("A4", RaceResultCode.DNS, 0),      // 未出发
				new MockRaceResult("A5", RaceResultCode.OK, 59_00),   // 0:59.00
				new MockRaceResult("A6", RaceResultCode.DNF, 0),      // 未完成
				new MockRaceResult("A7", RaceResultCode.OK, 56_80),   // 0:56.80
				new MockRaceResult("A8", RaceResultCode.SCR, 0)       // 赛前退出
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
		assertEquals(RaceResultCode.DQ, sorted.get(4).getResultCode());
		assertEquals(RaceResultCode.DNS, sorted.get(5).getResultCode());
		assertEquals(RaceResultCode.DNF, sorted.get(6).getResultCode());
		assertEquals(RaceResultCode.SCR, sorted.get(7).getResultCode());

		// 只有 4 人进入下一轮编排池
		assertEquals(4, qualified.size());
		assertTrue(qualified.stream().allMatch(r -> r.getResultCode() == RaceResultCode.OK));
	}

	@Test
	@DisplayName("编排场景: 半决赛成绩全部为 DQ/DNS/DNF/SCR/DSQ 时，无人进入决赛编排池")
	void semiFinalAllNonOkNoOneAdvances() {
		List<RaceResultCode> semiResults = Arrays.asList(
				RaceResultCode.DQ,
				RaceResultCode.DNS,
				RaceResultCode.DNF,
				RaceResultCode.SCR,
				RaceResultCode.DSQ,
				RaceResultCode.DQ,
				RaceResultCode.DNS,
				RaceResultCode.DNF
		);

		long qualifiedCount = semiResults.stream()
				.filter(RaceResultCode::isQualified)
				.count();

		assertEquals(0, qualifiedCount);

		// 排序验证：全部非 OK，按 sortOrder 排列
		List<RaceResultCode> sorted = semiResults.stream()
				.sorted(RaceResultCode.SEEDING_COMPARATOR)
				.toList();

		assertEquals(RaceResultCode.DQ, sorted.get(0));
		assertEquals(RaceResultCode.DQ, sorted.get(1));
		assertEquals(RaceResultCode.DNS, sorted.get(2));
		assertEquals(RaceResultCode.DNS, sorted.get(3));
		assertEquals(RaceResultCode.DNF, sorted.get(4));
		assertEquals(RaceResultCode.DNF, sorted.get(5));
		assertEquals(RaceResultCode.SCR, sorted.get(6));
		assertEquals(RaceResultCode.DSQ, sorted.get(7));
	}

	@Test
	@DisplayName("编排场景: 决赛成绩中第一名 OK，其余非 OK，冠军有效但无后续排名")
	void finalOnlyOneOk() {
		List<MockRaceResult> finals = Arrays.asList(
				new MockRaceResult("F1", RaceResultCode.OK, 55_00),
				new MockRaceResult("F2", RaceResultCode.DQ, 0),
				new MockRaceResult("F3", RaceResultCode.DNS, 0),
				new MockRaceResult("F4", RaceResultCode.DNF, 0),
				new MockRaceResult("F5", RaceResultCode.DSQ, 0),
				new MockRaceResult("F6", RaceResultCode.SCR, 0),
				new MockRaceResult("F7", RaceResultCode.DQ, 0),
				new MockRaceResult("F8", RaceResultCode.DNS, 0)
		);

		List<MockRaceResult> ranked = finals.stream()
				.sorted(Comparator
						.comparing((MockRaceResult r) -> r.getResultCode().getSortOrder())
						.thenComparing(MockRaceResult::getHundredths))
				.toList();

		// 冠军是 F1
		assertEquals("F1", ranked.get(0).getAthleteId());
		assertEquals(RaceResultCode.OK, ranked.get(0).getResultCode());

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
	void fromCodeCaseInsensitive(String input, RaceResultCode expected) {
		assertEquals(expected, RaceResultCode.fromCode(input));
	}

	@Test
	@DisplayName("fromCode: 非法代码应返回 null")
	void fromCodeInvalidReturnsNull() {
		assertNull(RaceResultCode.fromCode("XYZ"));
		assertNull(RaceResultCode.fromCode(""));
		assertNull(RaceResultCode.fromCode(" "));
		assertNull(RaceResultCode.fromCode(null));
	}

	// ==================== 辅助 Mock 类 ====================

	/**
	 * 模拟比赛成绩记录，用于编排场景测试
	 */
	private static class MockRaceResult {
		private final String athleteId;
		private final RaceResultCode resultCode;
		private final int hundredths;

		MockRaceResult(String athleteId, RaceResultCode resultCode, int hundredths) {
			this.athleteId = athleteId;
			this.resultCode = resultCode;
			this.hundredths = hundredths;
		}

		String getAthleteId() {
			return athleteId;
		}

		RaceResultCode getResultCode() {
			return resultCode;
		}

		int getHundredths() {
			return hundredths;
		}
	}
}
