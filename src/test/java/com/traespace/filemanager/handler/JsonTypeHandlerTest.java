package com.traespace.filemanager.handler;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

/**
 * JsonTypeHandler JSON类型处理器测试
 */
class JsonTypeHandlerTest {

    @Test
    void testParseJson() {
        // 测试解析JSON字符串
        String json = "{\"姓名\":\"张三\",\"地址\":\"北京市朝阳区\"}";
        Map<String, String> result = JsonTypeHandler.parseJson(json);

        assertThat(result).hasSize(2);
        assertThat(result.get("姓名")).isEqualTo("张三");
        assertThat(result.get("地址")).isEqualTo("北京市朝阳区");
    }

    @Test
    void testParseJsonEmpty() {
        // 测试空字符串
        Map<String, String> result = JsonTypeHandler.parseJson("");
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        // 测试null
        result = JsonTypeHandler.parseJson(null);
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    void testToJsonString() {
        // 测试转换为JSON字符串
        Map<String, String> map = new HashMap<>();
        map.put("name", "张三");
        map.put("age", "25");

        String json = JsonTypeHandler.toJsonString(map);
        assertThat(json).contains("\"name\":\"张三\"");
        assertThat(json).contains("\"age\":\"25\"");
    }

    @Test
    void testToJsonStringEmpty() {
        // 测试空Map
        Map<String, String> map = new HashMap<>();
        String json = JsonTypeHandler.toJsonString(map);
        assertThat(json).isEqualTo("{}");
    }
}
