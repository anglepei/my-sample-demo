package com.traespace.filemanager.enums;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * FieldType枚举测试
 */
class FieldTypeTest {

    @Test
    void testFieldTypeValues() {
        assertThat(FieldType.TEXT.name()).isEqualTo("TEXT");
        assertThat(FieldType.NUMBER.name()).isEqualTo("NUMBER");
        assertThat(FieldType.DATE.name()).isEqualTo("DATE");
    }

    @Test
    void testFieldTypeCount() {
        assertThat(FieldType.values()).hasSize(3);
    }
}
