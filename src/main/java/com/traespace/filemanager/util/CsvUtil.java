package com.traespace.filemanager.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * CSV处理工具
 *
 * @author Traespace
 * @since 2024-03-17
 */
public class CsvUtil {

    private static final String DEFAULT_SEPARATOR = ",";

    /**
     * 读取CSV文件
     *
     * @param file 上传的文件
     * @return 数据列表，每个Map代表一行，key为表头，value为单元格值
     */
    public static List<Map<String, String>> readCsv(MultipartFile file) {
        List<Map<String, String>> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            // 读取表头
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return result;
            }

            String[] headers = parseCsvLine(headerLine);

            // 读取数据
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] values = parseCsvLine(line);
                Map<String, String> rowData = new LinkedHashMap<>();
                for (int i = 0; i < headers.length && i < values.length; i++) {
                    rowData.put(headers[i].trim(), values[i].trim());
                }
                result.add(rowData);
            }
        } catch (IOException e) {
            throw new RuntimeException("CSV文件读取失败", e);
        }

        return result;
    }

    /**
     * 生成CSV文件
     *
     * @param data    数据列表
     * @param headers 表头列表
     * @return CSV文件的字节数组
     */
    public static byte[] generateCsv(List<Map<String, String>> data, List<String> headers) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // 写入表头
            out.write(String.join(DEFAULT_SEPARATOR, headers).getBytes(StandardCharsets.UTF_8));
            out.write("\n".getBytes(StandardCharsets.UTF_8));

            // 写入数据
            for (Map<String, String> rowData : data) {
                List<String> values = new ArrayList<>();
                for (String header : headers) {
                    String value = rowData.getOrDefault(header, "");
                    // 处理包含逗号的值，用引号包裹
                    if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
                        value = "\"" + value.replace("\"", "\"\"") + "\"";
                    }
                    values.add(value);
                }
                out.write(String.join(DEFAULT_SEPARATOR, values).getBytes(StandardCharsets.UTF_8));
                out.write("\n".getBytes(StandardCharsets.UTF_8));
            }

            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("CSV文件生成失败", e);
        }
    }

    /**
     * 解析CSV行，处理引号包裹的字段
     */
    private static String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // 处理双引号转义
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());

        return result.toArray(new String[0]);
    }
}
