package io.github.murphy955.fina.domain.enm;

/**
 * 泳池长度枚举
 *
 * @author : 李泽聿
 * @since : 2026/05/07 14:11
 */
public enum CourseTypeEnum {

    /**
     * 长池（50米）
     */
    LONG_COURSE(50),

    /**
     * 短池（25米）
     */
    SHORT_COURSE(25);

    /**
     * 泳池长度（米）
     */
    private final int lengthInMeters;

    CourseTypeEnum(int lengthInMeters) {
        this.lengthInMeters = lengthInMeters;
    }

    public int getLengthInMeters() {
        return lengthInMeters;
    }
}
