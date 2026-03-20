# 角色管理与模板增强功能 - 技术实现方案

## 1. 方案概述

本文档详细描述角色管理、管理员删除权限、带数据模板下载三大功能的技术实现方案。

### 1.1 技术栈
- Java 17
- Spring Boot 3.2
- MyBatis-Plus
- MySQL 8.0
- Apache POI (Excel处理)
- Jakarta Validation (参数校验)

### 1.2 开发规范遵循
- 遵循阿里巴巴Java开发手册
- Controller只做参数校验和结果封装
- Service层接口+实现分离
- 业务异常使用 `BizException(ErrorCode)`
- 日志格式：`log.info("[模块] 操作, param={}", param)`

---

## 2. 功能模块详细设计

### 2.1 角色管理模块

#### 2.1.1 需求描述
允许用户给自己分配普通用户或管理员角色。

#### 2.1.2 时序图

```
┌─────┐    ┌──────────┐    ┌──────────┐    ┌──────┐    ┌─────────┐
│前端 │    │Controller│    │ Service  │    │Mapper│    │  数据库  │
└──┬──┘    └────┬─────┘    └────┬─────┘    └──┬───┘    └────┬────┘
   │            │               │            │             │
   │ GET /api/user/role          │            │             │
   │───────────>│               │            │             │
   │            │               │            │             │
   │            │ getUserRole() │            │             │
   │            │──────────────>│            │             │
   │            │               │ selectById()│             │
   │            │               │───────────>│             │
   │            │               │            │             │
   │            │               │            │<───────────│
   │            │               │<───────────│             │
   │            │<──────────────│            │             │
   │<───────────│               │            │             │
   │            │               │            │             │
   │ POST /api/user/role        │            │             │
   │{role:"ADMIN"}              │            │             │
   │───────────>│               │            │             │
   │            │ updateRole()  │            │             │
   │            │──────────────>│            │             │
   │            │               │ updateById()│            │
   │            │               │───────────>│             │
   │            │               │            │             │
   │            │               │            │<───────────│
   │            │<──────────────│            │             │
   │<───────────│               │            │             │
```

#### 2.1.3 接口定义

**获取当前角色**
```java
/**
 * 获取当前用户角色
 * @return 角色信息
 */
@GetMapping("/role")
public Result<RoleResponse> getCurrentRole() {
    Long userId = UserContext.getUserId();
    RoleResponse response = userService.getCurrentRole(userId);
    return Result.success(response);
}
```

**更新角色**
```java
/**
 * 更新用户角色
 * @param request 角色更新请求
 * @return 空
 */
@PostMapping("/role")
public Result<Void> updateRole(@Valid @RequestBody RoleUpdateRequest request) {
    Long userId = UserContext.getUserId();
    UserRole role = UserRole.valueOf(request.getRole());
    userService.updateRole(userId, role);
    return Result.success();
}
```

#### 2.1.4 数据验证

| 字段 | 校验规则 | 错误码 |
|------|----------|--------|
| role | 非空，必须是USER或ADMIN | 1001 |

#### 2.1.5 实现要点

1. 角色更新后，Token中存储的角色信息不会立即更新
2. 下次请求时需要重新登录或Token刷新才能生效
3. 更新操作需要记录操作日志

---

### 2.2 管理员删除权限模块

#### 2.2.1 需求描述
管理员可以删除任何用户的文件，普通用户只能删除自己上传的文件。

#### 2.2.2 业务规则

```java
// 删除权限判断逻辑
if (currentUser.getRole() == UserRole.ADMIN) {
    // 管理员可以删除任何文件
    allowDelete();
} else if (fileRecord.getUserId().equals(currentUserId)) {
    // 普通用户只能删除自己的文件
    allowDelete();
} else {
    throw new BizException(ErrorCode.PERMISSION_DENIED);
}
```

#### 2.2.3 时序图

```
┌─────┐    ┌──────────┐    ┌──────────┐    ┌──────┐    ┌─────────┐
│前端 │    │Controller│    │ Service  │    │Mapper│    │  数据库  │
└──┬──┘    └────┬─────┘    └────┬─────┘    └──┬───┘    └────┬────┘
   │            │               │            │             │
   │ DELETE /api/file/{id}      │            │             │
   │───────────>│               │            │             │
   │            │               │            │             │
   │            │ deleteFile()  │            │             │
   │            │──────────────>│            │             │
   │            │               │ selectById()│            │
   │            │               │───────────>│             │
   │            │               │            │             │
   │            │               │ 获取文件和用户信息        │
   │            │               │            │<───────────│
   │            │               │<───────────│             │
   │            │               │            │             │
   │            │               │ 权限判断    │             │
   │            │               │ (admin或owner)           │
   │            │               │            │             │
   │            │               │ updateById(status=0)     │
   │            │               │───────────>│             │
   │            │               │            │             │
   │            │               │ delete(DataDetail)       │
   │            │               │───────────>│             │
   │            │               │            │<───────────│
   │            │<──────────────│            │             │
   │<───────────│               │            │             │
```

#### 2.2.4 修改点

**FileServiceImpl.java 修改前**
```java
@Override
@Transactional(rollbackFor = Exception.class)
public void deleteFile(Long userId, Long fileId) {
    FileRecord fileRecord = fileRecordMapper.selectById(fileId);
    if (fileRecord == null || !fileRecord.getUserId().equals(userId)) {
        throw new BizException(ErrorCode.FILE_NOT_FOUND);
    }
    // 删除逻辑...
}
```

**FileServiceImpl.java 修改后**
```java
@Override
@Transactional(rollbackFor = Exception.class)
public void deleteFile(Long userId, Long fileId) {
    FileRecord fileRecord = fileRecordMapper.selectById(fileId);
    if (fileRecord == null) {
        throw new BizException(ErrorCode.FILE_NOT_FOUND);
    }

    // 获取当前用户信息
    User currentUser = userMapper.selectById(userId);
    if (currentUser == null) {
        throw new BizException(ErrorCode.USER_NOT_FOUND);
    }

    // 权限校验：管理员或文件所有者可以删除
    if (currentUser.getRole() != UserRole.ADMIN
        && !fileRecord.getUserId().equals(userId)) {
        throw new BizException(ErrorCode.PERMISSION_DENIED);
    }

    // 软删除文件记录
    fileRecord.setStatus(0);
    fileRecordMapper.updateById(fileRecord);

    // 删除数据明细
    LambdaQueryWrapper<DataDetail> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(DataDetail::getFileId, fileId);
    dataDetailMapper.delete(wrapper);

    log.info("[文件删除] 文件删除成功, fileId={}, operatorId={}, operatorRole={}",
        fileId, userId, currentUser.getRole());
}
```

#### 2.2.5 错误处理

| 场景 | 错误码 | 错误信息 |
|------|--------|----------|
| 文件不存在 | 3004 | 文件不存在 |
| 无权限删除 | 1004 | 无权限 |

---

### 2.3 带数据模板下载模块

#### 2.3.1 需求描述
支持下载带随机数据的Excel/CSV模板，用户可指定数据条数（1-1000000）。

#### 2.3.2 架构设计

```
┌─────────────────────────────────────────────────────────────┐
│                      TemplateController                      │
│  downloadExcelTemplateWithData(count)                       │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      TemplateService                         │
│  - generateExcelTemplateWithData(userId, count)             │
│  - generateCsvTemplateWithData(userId, count)               │
└───────────────────────────┬─────────────────────────────────┘
                            │
            ┌───────────────┼───────────────┐
            ▼               ▼               ▼
┌──────────────────┐ ┌──────────────┐ ┌──────────────────┐
│ FieldConfigService│ │MockDataGenerator│ │ ExcelUtil/CsvUtil │
│ 获取用户字段配置  │ │  生成随机数据  │ │  生成文件        │
└──────────────────┘ └──────────────┘ └──────────────────┘
```

#### 2.3.3 数据生成流程

```
1. 参数校验
   └── count: 1-1000000

2. 获取用户字段配置
   └── 固定字段: 序号、身份证、手机号
   └── 自定义字段: 从 t_field_config 获取

3. 生成表头
   └── 按字段配置顺序排列

4. 生成数据行
   for i = 1 to count:
       ├── 序号: i
       ├── 身份证: MockDataGenerator.generateIdCard()
       ├── 手机号: MockDataGenerator.generatePhone()
       └── 自定义字段: 按类型生成
           ├── TEXT: generateText()
           ├── NUMBER: generateNumber()
           └── DATE: generateDate()

5. 生成文件
   ├── Excel: 使用 Apache POI
   └── CSV: 使用 CsvUtil

6. 返回文件流
```

#### 2.3.4 随机数据生成算法

**身份证号生成**
```java
/**
 * 18位身份证号生成算法
 * 1. 前6位：随机行政区代码
 * 2. 中间8位：出生日期
 * 3. 后3位：顺序码
 * 4. 最后1位：校验码
 */
public static String generateIdCard() {
    // 行政区代码（部分常用）
    String[] areaCodes = {"110101", "110102", "310101", "310104", "440103"};

    // 随机出生日期（1970-2005年）
    LocalDate startDate = LocalDate.of(1970, 1, 1);
    LocalDate endDate = LocalDate.of(2005, 12, 31);
    long days = ChronoUnit.DAYS.between(startDate, endDate);
    LocalDate randomDate = startDate.plusDays(random.nextInt((int) days));

    // 顺序码
    int seqCode = random.nextInt(1000);

    // 组装前17位
    String id17 = areaCodes[random.nextInt(areaCodes.length)] +
        randomDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
        String.format("%03d", seqCode);

    // 计算校验码
    char checkCode = calculateCheckCode(id17);

    return id17 + checkCode;
}

private static char calculateCheckCode(String id17) {
    int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    char[] checkChars = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    int sum = 0;
    for (int i = 0; i < 17; i++) {
        sum += (id17.charAt(i) - '0') * weights[i];
    }
    return checkChars[sum % 11];
}
```

**手机号生成**
```java
public static String generatePhone() {
    // 第1位固定为1
    // 第2位：3-9随机
    // 第3-11位：随机数字
    StringBuilder phone = new StringBuilder("1");
    phone.append(random.nextInt(7) + 3); // 3-9
    for (int i = 0; i < 9; i++) {
        phone.append(random.nextInt(10));
    }
    return phone.toString();
}
```

**中文文本生成**
```java
private static final String[] CHINESE_CHARS = {
    "张王李赵刘陈杨黄周吴徐孙胡朱高林何郭马罗梁宋郑谢韩唐冯于董萧程曹袁邓许傅沈曾彭吕苏卢蒋蔡贾丁魏薛叶阎余潘杜戴夏钟汪田任姜范方石姚谭廖邹熊金陆郝孔白崔康毛邱秦江史顾侯邵孟龙万段漕钱汤尹黎易常武乔贺赖龚文"];

public static String generateText(int maxLength) {
    int length = random.nextInt(maxLength) + 1;
    StringBuilder text = new StringBuilder();
    for (int i = 0; i < length; i++) {
        text.append(CHINESE_CHARS[random.nextInt(CHINESE_CHARS.length)]);
    }
    return text.toString();
}
```

**日期生成**
```java
public static String generateDate() {
    LocalDate today = LocalDate.now();
    int daysOffset = random.nextInt(365) - 182; // 前/后半年内
    LocalDate randomDate = today.plusDays(daysOffset);
    return randomDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
}
```

#### 2.3.5 Excel生成实现

```java
@Override
public byte[] generateExcelTemplateWithData(Long userId, int count) {
    log.info("[模板生成] 生成带数据Excel模板, userId={}, count={}", userId, count);

    // 1. 获取字段配置
    List<FieldConfigItem> fieldConfigs = fieldConfigService.getFieldConfig(userId);

    // 2. 创建工作簿
    try (Workbook workbook = new SXSSFWorkbook(100)) {
        Sheet sheet = workbook.createSheet("数据");

        // 3. 创建表头
        Row headerRow = sheet.createRow(0);
        List<String> headers = new ArrayList<>();
        headers.addAll(Arrays.asList("序号", "身份证号", "手机号"));
        headers.addAll(fieldConfigs.stream()
            .map(FieldConfigItem::getFieldName)
            .toList());

        for (int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            // 设置为文本格式
            CellStyle style = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            style.setDataFormat(
                createHelper.createDataFormat().getFormat("@"));
            cell.setCellStyle(style);
        }

        // 4. 生成数据行
        for (int rowIdx = 0; rowIdx < count; rowIdx++) {
            Row dataRow = sheet.createRow(rowIdx + 1);

            // 固定字段
            setCellValueAsString(dataRow.createCell(0), String.valueOf(rowIdx + 1));
            setCellValueAsString(dataRow.createCell(1), MockDataGenerator.generateIdCard());
            setCellValueAsString(dataRow.createCell(2), MockDataGenerator.generatePhone());

            // 自定义字段
            for (int i = 0; i < fieldConfigs.size(); i++) {
                FieldConfigItem config = fieldConfigs.get(i);
                String value = generateFieldValue(config);
                setCellValueAsString(dataRow.createCell(3 + i), value);
            }

            // 每1000行打印日志
            if ((rowIdx + 1) % 1000 == 0) {
                log.info("[模板生成] 已生成 {} 行数据", rowIdx + 1);
            }
        }

        // 5. 写入字节数组
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            workbook.write(bos);
            log.info("[模板生成] Excel模板生成完成, totalRows={}", count);
            return bos.toByteArray();
        }
    } catch (Exception e) {
        log.error("[模板生成] Excel模板生成失败", e);
        throw new BizException(ErrorCode.SYSTEM_ERROR);
    }
}

private void setCellValueAsString(Cell cell, String value) {
    cell.setCellValue(value);
    CellStyle style = cell.getSheet().getWorkbook().createCellStyle();
    style.setDataFormat(
        cell.getSheet().getWorkbook().getCreationHelper()
            .createDataFormat().getFormat("@"));
    cell.setCellStyle(style);
}

private String generateFieldValue(FieldConfigItem config) {
    return switch (config.getFieldType()) {
        case "TEXT" -> MockDataGenerator.generateText(64);
        case "NUMBER" -> MockDataGenerator.generateNumber();
        case "DATE" -> MockDataGenerator.generateDate();
        default -> "";
    };
}
```

#### 2.3.6 CSV生成实现

```java
@Override
public byte[] generateCsvTemplateWithData(Long userId, int count) {
    log.info("[模板生成] 生成带数据CSV模板, userId={}, count={}", userId, count);

    // 1. 获取字段配置
    List<FieldConfigItem> fieldConfigs = fieldConfigService.getFieldConfig(userId);

    // 2. 构建表头
    List<String> headers = new ArrayList<>();
    headers.addAll(Arrays.asList("序号", "身份证号", "手机号"));
    headers.addAll(fieldConfigs.stream()
        .map(FieldConfigItem::getFieldName)
        .toList());

    // 3. 生成CSV内容
    StringBuilder csv = new StringBuilder();
    csv.append(String.join(",", headers)).append("\n");

    for (int i = 0; i < count; i++) {
        List<String> values = new ArrayList<>();
        values.add(String.valueOf(i + 1));
        values.add(MockDataGenerator.generateIdCard());
        values.add(MockDataGenerator.generatePhone());

        for (FieldConfigItem config : fieldConfigs) {
            values.add(generateFieldValue(config));
        }

        csv.append(String.join(",", values)).append("\n");

        if ((i + 1) % 1000 == 0) {
            log.info("[模板生成] 已生成 {} 行数据", i + 1);
        }
    }

    log.info("[模板生成] CSV模板生成完成, totalRows={}", count);
    return csv.toString().getBytes(StandardCharsets.UTF_8);
}
```

#### 2.3.7 接口定义

```java
@Operation(summary = "下载带数据的Excel模板")
@GetMapping(value = "/download/excelWithData",
    produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
public ResponseEntity<byte[]> downloadExcelTemplateWithData(
    @RequestParam(defaultValue = "10") @Min(1) @Max(1000000) int count) {

    Long userId = UserContext.getUserId();
    byte[] excelBytes = templateService.generateExcelTemplateWithData(userId, count);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentDispositionFormData("attachment",
        "data_template_with_data.xlsx");
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

    return ResponseEntity.ok()
        .headers(headers)
        .body(excelBytes);
}
```

---

## 3. MockDataGenerator 工具类设计

### 3.1 类结构

```java
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
    private static final String[] COMMON_CHARS = {
        "的一是在不了有和人这中大为上个国我以要他时来用们生到作地于出就分对成会可主发年动同工也能下过子说产种面而方后多定行学法所民得经十三之进着等部度家电力里如水化高自二理起小物现实加量都两体制机当使点从业本去把性好应开它合还因由其些然前外天政四日那社义事平形相全表间样与关各重新线内数正心反你明看原又么利比或但质气第向道命此变条只没结解问意建月公无系军很情者最立代想已通并提直题党程展五果料象员革位入常文总次品式活设及管特件长求老头基资边流路级少图山统接知较将组见计别她手角期根论运农指几九区强放决西被干做必战先回则任取据处队南给色光门即保治北造百规热领七海口东导器压志世金增争济阶油思术极交受联什认六共权收证改清己美再采转更单风切打白教速花带安场身车例真务具万每目至达走积示议声报斗完类八离华名确才科张信马节话米整空元况今集温传土许步群广石记需段研界拉林律叫且究观越织装影算低持音众书布复容儿须际商非验连断深难近矿千周委素技备半办青省列习响约支般史感劳便团往酸历市克何除消构府称太准精值号率族维划选标写存候毛亲快效斯院查江型眼王按格养易置派层片始却专状育厂京识适属圆包火住调满县局照参红细引听该铁价严";

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
     */
    public static String generateDate() {
        LocalDate today = LocalDate.now();
        int offset = random.nextInt(365) - 182;
        LocalDate randomDate = today.plusDays(offset);
        return randomDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 生成随机中文文本
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
     * @return 随机数字（1-99999）
     */
    public static String generateNumber() {
        return String.valueOf(random.nextInt(99999) + 1);
    }
}
```

### 3.2 单元测试

```java
package com.traespace.filemanager.util;

import org.junit.jupiter.api.Test;
import com.traespace.filemanager.util.ValidationUtil;

import static org.junit.jupiter.api.Assertions.*;

class MockDataGeneratorTest {

    @Test
    void testGenerateIdCard() {
        for (int i = 0; i < 1000; i++) {
            String idCard = MockDataGenerator.generateIdCard();
            assertEquals(18, idCard.length());
            assertTrue(ValidationUtil.isValidIdCard(idCard),
                "生成的身份证号不符合格式: " + idCard);
        }
    }

    @Test
    void testGeneratePhone() {
        for (int i = 0; i < 1000; i++) {
            String phone = MockDataGenerator.generatePhone();
            assertEquals(11, phone.length());
            assertTrue(ValidationUtil.isValidPhone(phone),
                "生成的手机号不符合格式: " + phone);
        }
    }

    @Test
    void testGenerateDate() {
        for (int i = 0; i < 1000; i++) {
            String date = MockDataGenerator.generateDate();
            assertTrue(ValidationUtil.isValidDate(date),
                "生成的日期不符合格式: " + date);
        }
    }

    @Test
    void testGenerateText() {
        String text = MockDataGenerator.generateText(64);
        assertTrue(text.length() > 0);
        assertTrue(text.length() <= 64);
    }

    @Test
    void testGenerateNumber() {
        String number = MockDataGenerator.generateNumber();
        assertTrue(number.matches("\\d+"));
    }
}
```

---

## 4. 实现步骤

### 4.1 开发任务清单

| ID | 任务 | 优先级 | 估时 |
|----|------|--------|------|
| 1 | 创建 RoleUpdateRequest DTO | P0 | 0.5h |
| 2 | 创建 RoleResponse DTO | P0 | 0.5h |
| 3 | 创建 MockDataGenerator 工具类 | P0 | 2h |
| 4 | 编写 MockDataGenerator 单元测试 | P0 | 1h |
| 5 | 扩展 UserService 接口 | P0 | 0.5h |
| 6 | 实现 UserServiceImpl 角色方法 | P0 | 1h |
| 7 | 扩展 UserController 控制器 | P0 | 0.5h |
| 8 | 编写角色功能单元测试 | P0 | 1h |
| 9 | 修改 FileServiceImpl 删除权限 | P0 | 1h |
| 10 | 编写删除权限测试 | P0 | 1h |
| 11 | 扩展 TemplateService 接口 | P0 | 0.5h |
| 12 | 实现 TemplateServiceImpl 带数据模板 | P0 | 3h |
| 13 | 扩展 TemplateController 控制器 | P0 | 0.5h |
| 14 | 编写带数据模板测试 | P0 | 2h |
| 15 | 集成测试 | P1 | 2h |

### 4.2 实现顺序

```
第一阶段：基础工具
├── MockDataGenerator.java + 单元测试
└── DTO类

第二阶段：角色管理
├── UserService 扩展
├── UserController 扩展
└── 单元测试

第三阶段：删除权限
├── FileServiceImpl 修改
└── 单元测试

第四阶段：带数据模板
├── TemplateService 扩展
├── TemplateServiceImpl 实现
├── TemplateController 扩展
└── 单元测试

第五阶段：集成测试
└── 端到端测试
```

---

## 5. 性能考虑

### 5.1 大数据量生成优化

当数据量较大时（如100万条），需要考虑以下优化：

1. **使用 SXSSFWorkbook**：流式写入Excel，避免内存溢出
2. **批量处理**：每1000行处理一次，释放内存
3. **进度日志**：每1000行打印进度日志
4. **超时控制**：设置合理的请求超时时间

### 5.2 资源限制

| 资源 | 限制 | 说明 |
|------|------|------|
| 数据条数 | 1-1000000 | 前端+后端双重校验 |
| 单次请求超时 | 5分钟 | Nginx + Spring Boot |
| 内存使用 | 512MB | SXSSFWorkbook窗口大小 |

---

## 6. 安全考虑

### 6.1 输入校验

- count参数使用 `@Min(1) @Max(1000000)` 校验
- 角色参数使用正则表达式校验：`^(USER|ADMIN)$`

### 6.2 权限控制

- 角色更新：仅限用户修改自己的角色
- 文件删除：管理员或文件所有者

### 6.3 操作日志

所有重要操作都记录日志：
- 角色变更
- 文件删除
- 大数据量模板下载

---

## 7. 验收标准

### 7.1 功能验收

| 功能 | 验收点 |
|------|--------|
| 角色管理 | 可以成功切换角色，下次请求生效 |
| 删除权限 | 管理员可删除任何文件，普通用户只能删除自己的文件 |
| 带数据模板 | 可生成1-1000000条数据，数据格式正确 |

### 7.2 性能验收

| 场景 | 指标 |
|------|------|
| 1000条数据 | < 2秒 |
| 10000条数据 | < 15秒 |
| 100000条数据 | < 2分钟 |
| 1000000条数据 | < 15分钟 |

### 7.3 代码质量验收

- 单元测试覆盖率 >= 80%
- 通过 checkstyle 检查
- 无 SonarQube 严重问题
