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
     * 身份证号校验（18位 + 校验码）
     *
     * @param idCard 身份证号
     * @return 是否有效
     */
    public static boolean isValidIdCard(String idCard) {
        if (StringUtils.isEmpty(idCard) || idCard.length() != 18) {
            return false;
        }

        // 校验格式
        if (!idCard.matches("^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$")) {
            return false;
        }

        // 校验码验证（大小写不敏感）
        char[] chars = idCard.toCharArray();
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] checkCodes = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (chars[i] - '0') * weights[i];
        }
        char expectedCheck = checkCodes[sum % 11];
        char actualCheck = Character.toUpperCase(chars[17]);
        return actualCheck == expectedCheck;
    }

    /**
     * 手机号校验（11位 + 1开头 + 第二位3-9）
     *
     * @param phone 手机号
     * @return 是否有效
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^1[3-9]\\d{9}$");
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
