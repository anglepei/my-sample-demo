package com.traespace.filemanager.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

/**
 * JsonUtil测试
 */
class JsonUtilTest {

    @Test
    void testToJson() {
        // 测试对象转JSON
        Map<String, Object> data = Map.of("name", "张三", "age", 25);

        String json = JsonUtil.toJson(data);

        assertThat(json).isNotNull();
        assertThat(json).contains("\"name\"");
        assertThat(json).contains("\"张三\"");
        assertThat(json).contains("\"age\"");
        assertThat(json).contains("25");
    }

    @Test
    void testFromJson() {
        // 测试JSON转对象
        String json = "{\"name\":\"张三\",\"age\":25}";

        Map<String, Object> result = JsonUtil.fromJson(json, new TypeReference<Map<String, Object>>() {});

        assertThat(result).isNotNull();
        assertThat(result.get("name")).isEqualTo("张三");
        assertThat(result.get("age")).isEqualTo(25);
    }

    @Test
    void testToJsonList() {
        // 测试List转JSON
        List<Map<String, String>> data = List.of(
                Map.of("name", "张三"),
                Map.of("name", "李四")
        );

        String json = JsonUtil.toJson(data);

        assertThat(json).isNotNull();
        assertThat(json).contains("[");
        assertThat(json).contains("]");
    }

    @Test
    void testFromJsonToList() {
        // 测试JSON转List
        String json = "[{\"name\":\"张三\"},{\"name\":\"李四\"}]";

        List<Map<String, String>> result = JsonUtil.fromJson(json, new TypeReference<List<Map<String, String>>>() {});

        assertThat(result).hasSize(2);
        assertThat(result.get(0).get("name")).isEqualTo("张三");
        assertThat(result.get(1).get("name")).isEqualTo("李四");
    }
}
