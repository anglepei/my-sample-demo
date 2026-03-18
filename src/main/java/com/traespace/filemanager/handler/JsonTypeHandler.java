package com.traespace.filemanager.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * JSON类型处理器
 * 用于MyBatis-Plus处理JSON字段与Map<String, String>的转换
 *
 * @author Traespace
 * @since 2024-03-17
 */
public class JsonTypeHandler extends BaseTypeHandler<Map<String, String>> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .disable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
                                   Map<String, String> parameter,
                                   JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJsonString(parameter));
    }

    @Override
    public Map<String, String> getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        return parseJson(rs.getString(columnName));
    }

    @Override
    public Map<String, String> getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        return parseJson(rs.getString(columnIndex));
    }

    @Override
    public Map<String, String> getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        return parseJson(cs.getString(columnIndex));
    }

    /**
     * 解析JSON字符串为Map（使用LinkedHashMap保持顺序）
     */
    public static Map<String, String> parseJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new LinkedHashMap<>();
        }
        try {
            return OBJECT_MAPPER.readValue(json,
                new TypeReference<LinkedHashMap<String, String>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON解析失败: " + json, e);
        }
    }

    /**
     * 将Map转换为JSON字符串
     */
    public static String toJsonString(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON序列化失败: " + map, e);
        }
    }

    /**
     * 将List转换为JSON数组字符串（用于字段配置快照，保持顺序）
     */
    public static String toJsonArray(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON数组序列化失败: " + list, e);
        }
    }

    /**
     * 解析JSON数组字符串为List（用于字段配置快照，保持顺序）
     */
    public static List<String> parseJsonArray(String json) {
        if (json == null || json.trim().isEmpty() || "[]".equals(json.trim())) {
            return new ArrayList<>();
        }
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON数组解析失败: " + json, e);
        }
    }
}
