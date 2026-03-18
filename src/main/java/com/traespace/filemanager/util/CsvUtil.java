package com.traespace.filemanager.util;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
            log.info("[CSV解析] 表头行: [{}]", headerLine);
            if (headerLine == null) {
                return result;
            }

            String[] headers = parseCsvLine(headerLine);
            log.info("[CSV解析] 解析后表头数量: {}", headers.length);

            // 读取数据
            String line;
            int lineNum = 1;
            while ((line = reader.readLine()) != null) {
                log.info("[CSV解析] 第{}行原始数据: [{}]", lineNum, line);
                if (line.trim().isEmpty()) {
                    log.info("[CSV解析] 第{}行为空，跳过", lineNum);
                    lineNum++;
                    continue;
                }

                String[] values = parseCsvLine(line);
                log.info("[CSV解析] 第{}行解析后字段数: {}", lineNum, values.length);
                Map<String, String> rowData = new LinkedHashMap<>();
                for (int i = 0; i < headers.length && i < values.length; i++) {
                    rowData.put(headers[i].trim(), values[i].trim());
                }
                result.add(rowData);
                log.info("[CSV解析] 第{}行数据: {}", lineNum, rowData);
                lineNum++;
            }
            log.info("[CSV解析] 总共解析出{}条数据行", result.size());
        } catch (IOException e) {
            log.error("[CSV解析] 读取失败", e);
            throw new RuntimeException("CSV文件读取失败", e);
        }

        return result;
    }

    /**
     * 生成CSV文件（所有字段均用引号包裹，确保长数字不被转换格式）
     *
     * @param data    数据列表
     * @param headers 表头列表
     * @return CSV文件的字节数组
     */
    public static byte[] generateCsv(List<Map<String, String>> data, List<String> headers) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // 写入表头（所有字段名用引号包裹）
            List<String> quotedHeaders = new ArrayList<>();
            for (String header : headers) {
                quotedHeaders.add("\"" + escapeCsvValue(header) + "\"");
            }
            out.write(String.join(DEFAULT_SEPARATOR, quotedHeaders).getBytes(StandardCharsets.UTF_8));
            out.write("\n".getBytes(StandardCharsets.UTF_8));

            // 写入数据（所有值用引号包裹，强制文本格式）
            for (Map<String, String> rowData : data) {
                List<String> values = new ArrayList<>();
                for (String header : headers) {
                    String value = rowData.getOrDefault(header, "");
                    // 始终用引号包裹，确保长数字作为文本处理
                    values.add("\"" + escapeCsvValue(value) + "\"");
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
     * 转义CSV值中的特殊字符
     */
    private static String escapeCsvValue(String value) {
        if (value == null) {
            return "";
        }
        // 将双引号转义为两个双引号
        return value.replace("\"", "\"\"");
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
