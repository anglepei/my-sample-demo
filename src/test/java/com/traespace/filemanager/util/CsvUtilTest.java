package com.traespace.filemanager.util;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * CsvUtil测试
 */
class CsvUtilTest {

    @Test
    void testReadCsv() {
        // 测试读取CSV
        String csvContent = "姓名,年龄,地址\n张三,25,北京\n李四,30,上海";
        MockMultipartFile file = new MockMultipartFile(
                "test.csv",
                "test.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        List<Map<String, String>> result = CsvUtil.readCsv(file);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).get("姓名")).isEqualTo("张三");
        assertThat(result.get(0).get("年龄")).isEqualTo("25");
        assertThat(result.get(1).get("姓名")).isEqualTo("李四");
    }

    @Test
    void testGenerateCsv() {
        // 测试生成CSV
        List<Map<String, String>> data = List.of(
                Map.of("姓名", "张三", "年龄", "25"),
                Map.of("姓名", "李四", "年龄", "30")
        );
        List<String> headers = List.of("姓名", "年龄");

        byte[] csvBytes = CsvUtil.generateCsv(data, headers);

        assertThat(csvBytes).isNotNull();
        assertThat(csvBytes.length).isGreaterThan(0);

        String csvContent = new String(csvBytes, StandardCharsets.UTF_8);
        assertThat(csvContent).contains("姓名,年龄");
        assertThat(csvContent).contains("张三,25");
        assertThat(csvContent).contains("李四,30");
    }

    @Test
    void testReadCsvWithEmptyLines() {
        // 测试读取包含空行的CSV
        String csvContent = "姓名,年龄\n张三,25\n\n李四,30";
        MockMultipartFile file = new MockMultipartFile(
                "test.csv",
                "test.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        List<Map<String, String>> result = CsvUtil.readCsv(file);

        assertThat(result).hasSize(2);
    }
}
