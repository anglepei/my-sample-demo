package com.traespace.filemanager.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Excel处理工具
 *
 * @author Traespace
 * @since 2024-03-17
 */
public class ExcelUtil {

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
     *
     * @param data    数据列表
     * @param headers 表头列表
     * @return Excel文件的字节数组
     */
    public static byte[] generateExcel(List<Map<String, String>> data, List<String> headers) {
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
                int col = 0;
                for (String value : rowData.values()) {
                    Cell cell = row.createCell(col++);
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
                return String.valueOf(cell.getNumericCellValue());
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
