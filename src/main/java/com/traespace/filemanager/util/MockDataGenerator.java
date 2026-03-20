package com.traespace.filemanager.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * 随机数据生成工具类
 * 用于生成带数据模板的测试数据
 *
 * @author Traespace
 * @since 2024-03-18
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MockDataGenerator {

    private static final Logger log = LoggerFactory.getLogger(MockDataGenerator.class);
    private static final java.util.Random random = new java.util.Random();

    // 常用汉字
    private static final char[] COMMON_CHARS =
        "的一是在不了有和人这中大为上个国我以要他时来用们生到作地于出就分对成会可主发年动同工也能下过子说产种面而方后多定行学法所民得经十三之进着等部度家电力里如水化高自二理起小物现实加量都两体制机当使点从业本去把性好应开它合还因由其些然前外天政四日那社义事平形相全表间样与关各重新线内数正心反你明看原又么利比或但质气第向道命此变条只没结解问意建月公无系军很情者最立代想已通并提直题党程展五果料象员革位入常文总次品式活设及管特件长求老头基资边流路级少图山统接知较将组见计别她手角期根论运农指几九区强放决西被干做必战先回则任取据处队南给色光门即保治北造百规热领七海口东导器压志世金增争济阶油思术极交受联什认六共权收证改清己美再采转更单风切打白教速花带安场身车例真务具万每目至达走积示议声报斗完类八离华名确才科张信马节话米整空元况今集温传土许步群广石记需段研界拉林律叫且究观越织装影算低持音众书布复容儿须际商非验连断深难近矿千周委素技备半办青省列习响约支般史感劳便团往酸历市克何除消构府称太准精值号率族维划选标写存候毛亲快效斯院查江型眼王按格养易置派层片始却专状育厂京识适属圆包火住调满县局照参红细引听该铁价严".toCharArray();

    // 身份证号区划代码（部分）
    private static final String[] AREA_CODES = {
        "110101", "110102", "110105", "110106", "110107",  // 北京
        "310101", "310104", "310105", "310106", "310115",  // 上海
        "440103", "440104", "440105", "440106", "440111",  // 广州
        "440305", "440306", "440307", "440308"             // 深圳
    };

    // 姓氏
    private static final String[] SURNAMES = {
        "王", "李", "张", "刘", "陈", "杨", "黄", "赵", "周", "吴",
        "徐", "孙", "胡", "朱", "高", "林", "何", "郭", "马", "罗"
    };

    // 名字用字
    private static final String[] NAME_CHARS = {
        "伟", "芳", "娜", "敏", "静", "丽", "强", "磊", "军", "洋",
        "勇", "艳", "杰", "涛", "明", "超", "秀", "霞", "平", "刚",
        "桂", "辉", "红", "建", "文", "玲", "道", "国", "华", "金"
    };

    /**
     * 生成随机身份证号（18位）
     * 格式：6位区划 + 8位生日 + 3位顺序码 + 1位校验码
     *
     * @return 18位身份证号
     */
    public static String generateIdCard() {
        String areaCode = AREA_CODES[random.nextInt(AREA_CODES.length)];

        // 生成1970-2005年之间的随机日期
        LocalDate start = LocalDate.of(1970, 1, 1);
        LocalDate end = LocalDate.of(2005, 12, 31);
        long days = ChronoUnit.DAYS.between(start, end);
        LocalDate birthDate = start.plusDays(random.nextInt((int) days));
        String birthStr = birthDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String seqCode = String.format("%03d", random.nextInt(1000));

        String id17 = areaCode + birthStr + seqCode;
        char checkCode = calculateIdCardCheckCode(id17);

        return id17 + checkCode;
    }

    /**
     * 计算身份证校验码
     *
     * @param id17 身份证号前17位
     * @return 校验码
     */
    private static char calculateIdCardCheckCode(String id17) {
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] checkChars = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (id17.charAt(i) - '0') * weights[i];
        }
        return checkChars[sum % 11];
    }

    /**
     * 生成随机手机号（11位，1开头）
     *
     * @return 11位手机号
     */
    public static String generatePhone() {
        StringBuilder phone = new StringBuilder("1");
        phone.append(random.nextInt(7) + 3); // 3-9
        for (int i = 0; i < 9; i++) {
            phone.append(random.nextInt(10));
        }
        return phone.toString();
    }

    /**
     * 生成随机日期（YYYY-MM-DD格式）
     * 范围：最近365天内
     *
     * @return 日期字符串
     */
    public static String generateDate() {
        LocalDate today = LocalDate.now();
        int offset = random.nextInt(365) - 182;
        LocalDate randomDate = today.plusDays(offset);
        return randomDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 生成随机中文文本
     *
     * @param maxLength 最大长度
     * @return 随机文本
     */
    public static String generateText(int maxLength) {
        if (maxLength <= 0) {
            return "";
        }
        int length = random.nextInt(Math.min(maxLength, 10)) + 1;
        StringBuilder text = new StringBuilder();

        // 随机决定是否生成姓名（30%概率）
        if (random.nextInt(10) < 3) {
            text.append(SURNAMES[random.nextInt(SURNAMES.length)]);
            if (length > 1 && random.nextBoolean()) {
                text.append(NAME_CHARS[random.nextInt(NAME_CHARS.length)]);
            }
            if (length > 2 && random.nextBoolean()) {
                text.append(NAME_CHARS[random.nextInt(NAME_CHARS.length)]);
            }
        } else {
            for (int i = 0; i < length; i++) {
                text.append(COMMON_CHARS[random.nextInt(COMMON_CHARS.length)]);
            }
        }

        return text.toString();
    }

    /**
     * 生成随机数字字符串
     *
     * @return 随机数字（1-99999）
     */
    public static String generateNumber() {
        return String.valueOf(random.nextInt(99999) + 1);
    }
}
