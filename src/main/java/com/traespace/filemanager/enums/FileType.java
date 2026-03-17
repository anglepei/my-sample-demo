package com.traespace.filemanager.enums;

/**
 * 文件类型枚举
 *
 * @author Traespace
 * @since 2024-03-17
 */
public enum FileType {

    /**
     * Excel 2007+ 格式
     */
    XLSX("Excel Workbook", ".xlsx"),

    /**
     * Excel 97-2003 格式
     */
    XLS("Excel Legacy", ".xls"),

    /**
     * CSV 格式
     */
    CSV("Comma Separated Values", ".csv");

    /**
     * 文件类型描述
     */
    private final String description;

    /**
     * 文件扩展名
     */
    private final String extension;

    FileType(String description, String extension) {
        this.description = description;
        this.extension = extension;
    }

    public String getDescription() {
        return description;
    }

    public String getExtension() {
        return extension;
    }
}
