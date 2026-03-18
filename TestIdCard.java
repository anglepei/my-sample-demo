public class TestIdCard {
    public static void main(String[] args) {
        String idCard = "440103198503221238";
        System.out.println("验证身份证号: " + idCard);
        System.out.println("长度: " + idCard.length());
        System.out.println("格式匹配: " + idCard.matches("^[1-9]\\\\d{5}(18|19|20)\\\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\\\d|3[01])\\\\d{3}[\\\\dXx]$"));
        
        // 计算校验码
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] checkCodes = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            int digit = idCard.charAt(i) - '0';
            sum += digit * weights[i];
            System.out.println("位置" + i + ": " + digit + " × " + weights[i] + " = " + (digit * weights[i]));
        }
        System.out.println("总和: " + sum);
        System.out.println("取模: " + (sum % 11));
        System.out.println("期望校验码: " + checkCodes[sum % 11]);
        System.out.println("实际校验码: " + idCard.charAt(17));
        System.out.println("校验结果: " + (checkCodes[sum % 11] == idCard.charAt(17)));
    }
}
