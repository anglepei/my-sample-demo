package com.traespace.filemanager.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Excel处理工具
 *
 * @author Traespace
 * @since 2024-03-17
 */
public class ExcelUtil {

    /**
     * 默认行数阈值：超过此行数使用流式写入
     */
    private static final int ROW_THRESHOLD = 10000;

    /**
     * 流式写入时内存中保留的行数
     */
    private static final int FLUSH_WINDOW_SIZE = 1000;

    static {
        // 设置 POI 读取大文件的最大字节数限制（设置为约 2GB）
        // 解决 "maximum length for this record type is 100,000,000" 错误
        // 注意：Integer.MAX_VALUE = 2147483647 bytes ≈ 2GB
        IOUtils.setByteArrayMaxOverride(Integer.MAX_VALUE);
    }

    /**
     * 读取Excel文件
     *
     * @param file 上传的文件
     * @return 数据列表，每个Map代表一行，key为表头，value为单元格值
     */
    public static List<Map<String, String>> readExcel(MultipartFile file) {
        List<Map<String, String>> result = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            // 只读取第一个Sheet
            Sheet sheet = workbook.getSheetAt(0);

            // 读取表头
            Row headerRow = sheet.getRow(0);
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(getCellValue(cell));
            }

            // 读取数据
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Map<String, String> rowData = new LinkedHashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j);
                    String value = cell == null ? "" : getCellValue(cell).trim();
                    rowData.put(headers.get(j), value);
                }
                result.add(rowData);
            }
        } catch (IOException e) {
            throw new RuntimeException("Excel文件读取失败", e);
        }

        return result;
    }

    /**
     * 生成Excel文件（所有单元格均为文本格式）
     * 根据数据量自动选择：小数据量使用XSSFWorkbook，大数据量使用SXSSFWorkbook流式写入
     *
     * @param data    数据列表
     * @param headers 表头列表
     * @return Excel文件的字节数组
     */
    public static byte[] generateExcel(List<Map<String, String>> data, List<String> headers) {
        // 根据数据量选择写入方式
        if (data.size() > ROW_THRESHOLD) {
            return generateExcelLarge(data, headers);
        } else {
            return generateExcelNormal(data, headers);
        }
    }

    /**
     * 生成Excel文件（正常模式 - XSSFWorkbook）
     * 适用于小数据量（<=10000行）
     */
    private static byte[] generateExcelNormal(List<Map<String, String>> data, List<String> headers) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("数据");

            // 创建文本格式的单元格样式
            CellStyle textStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            textStyle.setDataFormat(format.getFormat("@")); // @ 表示文本格式

            // 设置列宽（文本格式需要更宽的列）
            for (int i = 0; i < headers.size(); i++) {
                sheet.setColumnWidth(i, 20 * 256); // 20个字符宽度
            }

            // 写入表头
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(textStyle);
            }

            // 写入数据
            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Map<String, String> rowData = data.get(i);

                // 按表头顺序写入数据，确保列对齐
                for (int j = 0; j < headers.size(); j++) {
                    String headerName = headers.get(j);
                    String value = rowData.getOrDefault(headerName, "");

                    Cell cell = row.createCell(j);
                    cell.setCellStyle(textStyle);
                    // 确保值作为字符串存储
                    cell.setCellValue(value != null ? value : "");
                }
            }

            // 输出为字节数组
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Excel文件生成失败", e);
        }
    }

    /**
     * 生成Excel文件（大数据模式 - SXSSFWorkbook流式写入）
     * 适用于大数据量（>10000行），避免内存溢出
     */
    private static byte[] generateExcelLarge(List<Map<String, String>> data, List<String> headers) {
        // 使用SXSSFWorkbook进行流式写入
        // FLUSH_WINDOW_SIZE表示内存中保留的行数，超过此数量会写入临时文件
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(FLUSH_WINDOW_SIZE)) {
            workbook.setCompressTempFiles(true); // 压缩临时文件

            Sheet sheet = workbook.createSheet("数据");

            // 创建文本格式的单元格样式
            CellStyle textStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            textStyle.setDataFormat(format.getFormat("@")); // @ 表示文本格式

            // 设置列宽
            for (int i = 0; i < headers.size(); i++) {
                sheet.setColumnWidth(i, 20 * 256);
            }

            // 写入表头
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(textStyle);
            }

            // 写入数据（流式写入）
            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Map<String, String> rowData = data.get(i);

                // 按表头顺序写入数据
                for (int j = 0; j < headers.size(); j++) {
                    String headerName = headers.get(j);
                    String value = rowData.getOrDefault(headerName, "");

                    Cell cell = row.createCell(j);
                    cell.setCellStyle(textStyle);
                    cell.setCellValue(value != null ? value : "");
                }

                // 每10000行打印日志并触发GC建议
                if ((i + 1) % 10000 == 0) {
                    System.out.println("[Excel生成] 已处理 " + (i + 1) + " 行数据...");
                }
            }

            // 输出为字节数组
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            workbook.dispose(); // 清理临时文件
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Excel文件生成失败", e);
        }
    }

    /**
     * 获取单元格值
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                // 使用 DecimalFormat 避免大数字被转换为科学计数法
                DecimalFormat df = new DecimalFormat("#");
                return df.format(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }
}
