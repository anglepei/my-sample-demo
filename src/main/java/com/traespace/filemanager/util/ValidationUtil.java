package com.traespace.filemanager.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 校验工具类
 *
 * @author Traespace
 * @since 2024-03-17
 */
public class ValidationUtil {

    /**
     * 身份证号校验（仅校验位数：18位）
     *
     * @param idCard 身份证号
     * @return 是否有效
     */
    public static boolean isValidIdCard(String idCard) {
        // 仅校验是否为18位数字或结尾是X
        if (StringUtils.isEmpty(idCard)) {
            return false;
        }
        return idCard.matches("^\\d{17}[\\dXx]$");
    }

    /**
     * 手机号校验（仅校验位数：11位）
     *
     * @param phone 手机号
     * @return 是否有效
     */
    public static boolean isValidPhone(String phone) {
        // 仅校验是否为11位数字
        return phone != null && phone.matches("^\\d{11}$");
    }

    /**
     * 日期校验（YYYY-MM-DD）
     *
     * @param dateStr 日期字符串
     * @return 是否有效
     */
    public static boolean isValidDate(String dateStr) {
        if (StringUtils.isEmpty(dateStr)) {
            return false;
        }
        try {
            java.time.format.DateTimeFormatter formatter =
                java.time.format.DateTimeFormatter.ofPattern("uuuu-MM-dd")
                    .withResolverStyle(java.time.format.ResolverStyle.STRICT);
            java.time.LocalDate.parse(dateStr, formatter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
