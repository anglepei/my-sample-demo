package com.traespace.filemanager.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MockDataGenerator随机数据生成工具测试
 */
class MockDataGeneratorTest {

    @Test
    void testGenerateIdCard() {
        // 测试生成身份证号：循环1000次验证格式
        for (int i = 0; i < 1000; i++) {
            String idCard = MockDataGenerator.generateIdCard();

            // 验证长度为18位
            assertThat(idCard)
                .as("生成的身份证号长度应为18位，实际: %s", idCard)
                .hasSize(18);

            // 验证通过ValidationUtil校验
            assertThat(ValidationUtil.isValidIdCard(idCard))
                .as("生成的身份证号不符合格式: %s", idCard)
                .isTrue();
        }
    }

    @Test
    void testGeneratePhone() {
        // 测试生成手机号：循环1000次验证格式
        for (int i = 0; i < 1000; i++) {
            String phone = MockDataGenerator.generatePhone();

            // 验证长度为11位
            assertThat(phone)
                .as("生成的手机号长度应为11位，实际: %s", phone)
                .hasSize(11);

            // 验证第一位为1
            assertThat(phone.charAt(0))
                .as("手机号第一位应为1，实际: %s", phone)
                .isEqualTo('1');

            // 验证通过ValidationUtil校验
            assertThat(ValidationUtil.isValidPhone(phone))
                .as("生成的手机号不符合格式: %s", phone)
                .isTrue();
        }
    }

    @Test
    void testGenerateDate() {
        // 测试生成日期：循环1000次验证格式
        for (int i = 0; i < 1000; i++) {
            String date = MockDataGenerator.generateDate();

            // 验证通过ValidationUtil.isValidDate校验
            assertThat(ValidationUtil.isValidDate(date))
                .as("生成的日期不符合格式: %s", date)
                .isTrue();
        }
    }

    @Test
    void testGenerateText() {
        // 测试生成中文文本
        for (int i = 0; i < 100; i++) {
            String text = MockDataGenerator.generateText(64);

            // 验证文本长度在1-64之间
            assertThat(text.length())
                .as("生成的文本长度应在1-64之间，实际: %d", text.length())
                .isGreaterThan(0)
                .isLessThanOrEqualTo(64);

            // 验证是中文字符
            assertThat(text)
                .as("生成的文本应包含中文字符: %s", text)
                .matches("^[\\u4e00-\\u9fa5]+$");
        }
    }

    @Test
    void testGenerateTextWithMaxLength() {
        // 测试不同最大长度
        String text1 = MockDataGenerator.generateText(10);
        assertThat(text1.length()).isGreaterThan(0).isLessThanOrEqualTo(10);

        String text2 = MockDataGenerator.generateText(1);
        assertThat(text2.length()).isEqualTo(1);

        String text3 = MockDataGenerator.generateText(100);
        assertThat(text3.length()).isGreaterThan(0).isLessThanOrEqualTo(100);
    }

    @Test
    void testGenerateNumber() {
        // 测试生成数字字符串
        for (int i = 0; i < 100; i++) {
            String number = MockDataGenerator.generateNumber();

            // 验证是纯数字
            assertThat(number)
                .as("生成的数字应为纯数字，实际: %s", number)
                .matches("^\\d+$");

            // 验证在1-99999范围内
            int value = Integer.parseInt(number);
            assertThat(value)
                .as("生成的数字应在1-99999之间，实际: %d", value)
                .isGreaterThan(0)
                .isLessThanOrEqualTo(99999);
        }
    }
}
