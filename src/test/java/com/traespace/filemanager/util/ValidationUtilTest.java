package com.traespace.filemanager.util;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * ValidationUtil校验工具测试
 */
class ValidationUtilTest {

    @Test
    void testValidIdCard() {
        // 测试有效的身份证号（使用正确的校验码）
        // 110101199001011237 - 校验码为7
        assertThat(ValidationUtil.isValidIdCard("110101199001011237")).isTrue();
        // 11010519491231002X - 这是有效的带X的身份证
        assertThat(ValidationUtil.isValidIdCard("11010519491231002X")).isTrue();
    }

    @Test
    void testInvalidIdCard() {
        // 测试无效的身份证号
        assertThat(ValidationUtil.isValidIdCard(null)).isFalse();
        assertThat(ValidationUtil.isValidIdCard("")).isFalse();
        assertThat(ValidationUtil.isValidIdCard("12345678901234567")).isFalse();
        assertThat(ValidationUtil.isValidIdCard("11010119900101123")).isFalse(); // 17位
        assertThat(ValidationUtil.isValidIdCard("1101011990010112345")).isFalse(); // 19位
    }

    @Test
    void testValidPhone() {
        // 测试有效的手机号
        assertThat(ValidationUtil.isValidPhone("13800138000")).isTrue();
        assertThat(ValidationUtil.isValidPhone("15012345678")).isTrue();
        assertThat(ValidationUtil.isValidPhone("18888888888")).isTrue();
    }

    @Test
    void testInvalidPhone() {
        // 测试无效的手机号
        assertThat(ValidationUtil.isValidPhone(null)).isFalse();
        assertThat(ValidationUtil.isValidPhone("")).isFalse();
        assertThat(ValidationUtil.isValidPhone("12345678901")).isFalse(); // 非1开头
        assertThat(ValidationUtil.isValidPhone("1380013800")).isFalse(); // 10位
        assertThat(ValidationUtil.isValidPhone("138001380001")).isFalse(); // 12位
    }

    @Test
    void testValidDate() {
        // 测试有效的日期
        assertThat(ValidationUtil.isValidDate("2024-01-01")).isTrue();
        assertThat(ValidationUtil.isValidDate("2000-02-29")).isTrue(); // 闰年
        assertThat(ValidationUtil.isValidDate("2024-12-31")).isTrue();
    }

    @Test
    void testInvalidDate() {
        // 测试无效的日期
        assertThat(ValidationUtil.isValidDate(null)).isFalse();
        assertThat(ValidationUtil.isValidDate("")).isFalse();
        assertThat(ValidationUtil.isValidDate("2024/01/01")).isFalse(); // 格式错误
        assertThat(ValidationUtil.isValidDate("2024-13-01")).isFalse(); // 无效月份
        assertThat(ValidationUtil.isValidDate("2024-02-30")).isFalse(); // 无效日期
        assertThat(ValidationUtil.isValidDate("2024-1-1")).isFalse(); // 格式错误
    }
}
