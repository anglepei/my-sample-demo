package com.traespace.filemanager.service.template;

import com.traespace.filemanager.dto.request.field.FieldConfigItem;

/**
 * 模板服务接口
 *
 * @author Traespace
 * @since 2024-03-17
 */
public interface TemplateService {

    /**
     * 生成Excel模板
     * 包含固定字段（序号、身份证号、手机号）和用户自定义字段
     *
     * @param userId 用户ID
     * @return Excel文件的字节数组
     */
    byte[] generateExcelTemplate(Long userId);

    /**
     * 生成CSV模板
     * 包含固定字段（序号、身份证号、手机号）和用户自定义字段
     *
     * @param userId 用户ID
     * @return CSV文件的字节数组
     */
    byte[] generateCsvTemplate(Long userId);

    /**
     * 生成带数据的Excel模板
     * 包含固定字段和随机生成的测试数据
     *
     * @param userId 用户ID
     * @param count 数据条数
     * @return Excel文件的字节数组
     */
    byte[] generateExcelTemplateWithData(Long userId, int count);

    /**
     * 生成带数据的CSV模板
     * 包含固定字段和随机生成的测试数据
     *
     * @param userId 用户ID
     * @param count 数据条数
     * @return CSV文件的字节数组
     */
    byte[] generateCsvTemplateWithData(Long userId, int count);
}
