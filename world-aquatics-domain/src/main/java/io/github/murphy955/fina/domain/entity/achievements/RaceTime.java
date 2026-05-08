package io.github.murphy955.fina.domain.entity.achievements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 总成绩
 *
 * @author : 李泽聿
 * @since : 2026:05:07 15:01
 */
public class RaceTime implements Comparable<RaceTime> {
    private final long totalHundredths;

    private static final long MAX_HUNDREDTHS = 99 * 6000L + 59 * 100L + 99;
    // 格式1: 1:03.79（必须有冒号，秒部分严格 00-59）
    private static final Pattern PATTERN_COLON = Pattern.compile("^(\\d+):([0-5]\\d)\\.([0-9]{2})$");
    // 格式2: 1-03-79（必须有连字符，秒部分严格 00-59）
    private static final Pattern PATTERN_MINUS = Pattern.compile("^(\\d+)-([0-5]\\d)-([0-9]{2})$");
    // 格式3: 63.79（纯秒数，可 >=60）
    private static final Pattern PATTERN_SECONDS_ONLY = Pattern.compile("^(\\d+)\\.([0-9]{2})$");

    private RaceTime(long totalHundredths) {
        if (totalHundredths < 0) {
            throw new IllegalArgumentException("Race time cannot be negative");
        }
        if (totalHundredths > MAX_HUNDREDTHS) {
            throw new IllegalArgumentException(
                    String.format("Time %d hundredths exceeds maximum allowed 99:59.99", totalHundredths));
        }
        this.totalHundredths = totalHundredths;
    }

    @Override
    public int compareTo(RaceTime o) {
        return Long.compare(this.totalHundredths, o.totalHundredths);
    }

    /**
     * 传入时间字符串仅支持以下格式
     * <ul>
     *     <li>1:03.79（必须有冒号，秒部分严格 00-59，毫秒部分严格00-99）</li>
     *     <li>1-03-79（必须有连字符，秒部分严格 00-59，毫秒部分严格00-99）</li>
     *     <li>63.79（纯秒数，可 >=60，毫秒部分严格00-99）</li>
     * </ul>
     *
     * @param timeStr 未处理的时间字符串
     * @return io.github.murphy955.fina.domain.entity.achievements.RaceTime
     * @author 李泽聿
     * @since 2026-05-07 15:30
     */
    public static RaceTime parse(String timeStr) {
        if (timeStr == null || timeStr.isBlank()) {
            throw new IllegalArgumentException("Time string must not be blank");
        }
        String s = timeStr.trim();

        // 尝试 1:03.79
        Matcher m1 = PATTERN_COLON.matcher(s);
        if (m1.matches()) {
            int minutes = Integer.parseInt(m1.group(1));
            int seconds = Integer.parseInt(m1.group(2));
            int hundredths = Integer.parseInt(m1.group(3));
            return fromParts(minutes, seconds, hundredths);
        }

        // 尝试 1-03-79
        Matcher m2 = PATTERN_MINUS.matcher(s);
        if (m2.matches()) {
            int minutes = Integer.parseInt(m2.group(1));
            int seconds = Integer.parseInt(m2.group(2));
            int hundredths = Integer.parseInt(m2.group(3));
            return fromParts(minutes, seconds, hundredths);
        }

        // 尝试 63.79
        Matcher m3 = PATTERN_SECONDS_ONLY.matcher(s);
        if (m3.matches()) {
            long totalSeconds = Long.parseLong(m3.group(1));
            int hundredths = Integer.parseInt(m3.group(2));
            long total = totalSeconds * 100L + hundredths;
            return new RaceTime(total);
        }

        throw new IllegalArgumentException(
                "Invalid format: '" + timeStr + "'. Expected: 1:03.79, 1-03-79, or 63.79");
    }

    /**
     *
     *
     * @param minutes    分钟
     * @param seconds    秒
     * @param hundredths 百分位
     * @return io.github.murphy955.fina.domain.entity.achievements.RaceTime
     * @author 李泽聿
     * @since 2026-05-07 15:28
     */
    private static RaceTime fromParts(int minutes, int seconds, int hundredths) {
        if (seconds < 0 || seconds >= 60) {
            throw new IllegalArgumentException("Seconds must be in 00-59");
        }
        if (hundredths < 0 || hundredths >= 100) {
            throw new IllegalArgumentException("Hundredths must be in 00-99");
        }
        long total = minutes * 6000L + seconds * 100L + hundredths;
        return new RaceTime(total);
    }

    /**
     * 统一按招{@code 1:03.79}格式化输出
     *
     * @return java.lang.String
     * @author 李泽聿
     * @since 2026-05-07 15:34
     */
    public String toFinFormat() {
        long mins = totalHundredths / 6000;
        long secs = (totalHundredths % 6000) / 100;
        long hs = totalHundredths % 100;
        if (mins > 0) {
            return String.format("%d:%02d.%02d", mins, secs, hs);
        }
        return String.format("%02d.%02d", secs, hs);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(totalHundredths);
    }

    @Override
    public String toString() {
        return toFinFormat();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof RaceTime)) {
            return false;
        }
        return this.totalHundredths == ((RaceTime) obj).totalHundredths;
    }
}
