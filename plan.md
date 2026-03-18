# 技术实现方案

## 1. 包结构设计

[详见上文包结构内容...]

---

## 2. 技术选型说明

### 2.1 核心技术栈

| 技术 | 版本 | 用途 | 说明 |
|------|------|------|------|
| Java | 17 | 开发语言 | LTS版本，长期支持 |
| Spring Boot | 3.2 | 应用框架 | 简化配置，快速开发 |
| Spring Security | 6.x | 安全框架 | 认证授权 |
| MyBatis-Plus | 3.5+ | ORM框架 | 简化CRUD操作 |
| MySQL | 8.0 | 数据库 | 支持JSON类型 |
| Redis | 7.x | 缓存 | 会话管理、缓存 |
| Apache POI | 5.2+ | Excel处理 | .xls/.xlsx读写 |
| Hutool | 5.8+ | 工具库 | 简化常用操作 |
| JWT | - | Token生成 | 无状态认证 |

### 2.2 依赖管理

```xml
<!-- 核心依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- 数据库 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.5</version>
</dependency>
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>

<!-- 文件处理 -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>

<!-- 工具类 -->
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>5.8.24</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>

<!-- 文档注解 -->
<dependency>
    <groupId>io.swagger.core.v3</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

---

## 3. 核心技术实现方案

### 3.1 认证授权方案

#### 3.1.1 JWT + Redis 会话管理

```
登录流程：
┌─────────┐      ┌─────────┐      ┌─────────┐
│  用户   │ ──→ │ 后端    │ ──→ │ Redis   │
│         │      │ 验证密码 │      │ 存储Token│
└─────────┘      └─────────┘      └─────────┘
                      ↓
                生成JWT Token
                      ↓
              ┌─────────────┐
              │ 返回给用户   │
              └─────────────┘

请求拦截流程：
┌─────────┐      ┌──────────┐      ┌─────────┐
│ 用户请求 │ ──→ │ 拦截器   │ ──→ │ Redis   │
│+Token   │      │ 验证Token │      │ 验证存在 │
└─────────┘      └──────────┘      └─────────┘
                      ↓
                设置用户上下文
```

**实现要点：**

1. **JWT Token 结构**
   ```java
   - Header: {"alg": "HS256"}
   - Payload: {"userId": "123", "username": "admin", "role": "ADMIN", "exp": "..."}
   - Signature: HMACSHA256(secret, header + payload)
   ```

2. **Redis 存储结构**
   ```
   Key: auth:token:{userId}
   Value: {token}
   TTL: 2小时（与JWT过期时间一致）
   ```

3. **会话超时处理**
   - JWT过期：返回401，前端跳转登录页
   - Redis过期：即使用户持有有效JWT也需重新登录

#### 3.1.2 权限控制实现

```java
/**
 * 自定义权限注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    UserRole[] value();
}

/**
 * 拦截器实现权限校验
 */
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) {
        // 1. 验证Token
        String token = request.getHeader("Authorization");
        if (StringUtils.isEmpty(token)) {
            throw new UnauthorizedException(ErrorCode.TOKEN_MISSING);
        }

        // 2. 验证Redis中是否存在
        String userId = JwtUtil.getUserId(token);
        if (!RedisUtil.exists("auth:token:" + userId)) {
            throw new UnauthorizedException(ErrorCode.TOKEN_EXPIRED);
        }

        // 3. 验证权限
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            RequireRole requireRole = hm.getMethodAnnotation(RequireRole.class);
            if (requireRole != null) {
                UserRole userRole = JwtUtil.getRole(token);
                if (!ArrayUtil.contains(requireRole.value(), userRole)) {
                    throw new UnauthorizedException(ErrorCode.PERMISSION_DENIED);
                }
            }
        }

        // 4. 设置用户上下文
        UserContext.setUserId(userId);
        UserContext.setRole(JwtUtil.getRole(token));
        return true;
    }
}
```

### 3.2 数据校验方案

#### 3.2.1 校验器实现

```java
/**
 * 校验工具类
 */
public class ValidationUtil {

    /**
     * 身份证校验（18位 + 校验码）
     */
    public static boolean isValidIdCard(String idCard) {
        if (StringUtils.isEmpty(idCard) || idCard.length() != 18) {
            return false;
        }
        // 校验格式
        if (!idCard.matches("^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$")) {
            return false;
        }
        // 校验码验证
        char[] chars = idCard.toCharArray();
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] checkCodes = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (chars[i] - '0') * weights[i];
        }
        return chars[17] == checkCodes[sum % 11];
    }

    /**
     * 手机号校验（11位 + 1开头）
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^1\\d{10}$");
    }

    /**
     * 日期校验（YYYY-MM-DD）
     */
    public static boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```

#### 3.2.2 文件校验流程

```java
/**
 * 文件校验服务
 */
@Service
@RequiredArgsConstructor
public class FileValidationService {

    private final FieldConfigService fieldConfigService;

    /**
     * 校验文件数据
     * @return 校验成功返回null，失败返回错误信息
     */
    public ValidationResult validateFile(MultipartFile file, Long userId) {
        // 1. 文件格式校验
        String filename = file.getOriginalFilename();
        if (!isValidFileType(filename)) {
            return ValidationResult.error("文件格式不支持，仅支持Excel和CSV");
        }

        // 2. 文件大小校验（200MB）
        if (file.getSize() > 200 * 1024 * 1024) {
            return ValidationResult.error("文件大小不能超过200MB");
        }

        // 3. 解析文件
        List<Map<String, String>> dataList = parseFile(file);

        // 4. 获取用户字段配置
        FieldConfigResponse config = fieldConfigService.getFieldConfig(userId);

        // 5. 表头校验
        if (!validateHeaders(dataList.get(0), config)) {
            return ValidationResult.error("表头与模板不匹配");
        }

        // 6. 数据校验
        Set<String> idCardSet = new HashSet<>();
        for (int i = 1; i < dataList.size(); i++) {
            Map<String, String> row = dataList.get(i);

            // 跳过空行
            if (isEmptyRow(row)) {
                continue;
            }

            // 固定字段校验
            if (StringUtils.isEmpty(row.get("序号"))) {
                return ValidationResult.error("第" + i + "行序号不能为空");
            }

            String idCard = row.get("身份证");
            if (!ValidationUtil.isValidIdCard(idCard)) {
                return ValidationResult.error("第" + i + "行身份证格式错误");
            }

            // 重复校验
            if (idCardSet.contains(idCard)) {
                return ValidationResult.error("第" + i + "行与第" +
                    findDuplicateRow(idCard, dataList, i) + "行身份证重复");
            }
            idCardSet.add(idCard);

            String phone = row.get("手机号");
            if (!ValidationUtil.isValidPhone(phone)) {
                return ValidationResult.error("第" + i + "行手机号格式错误");
            }

            // 自定义字段校验
            for (FieldConfigItem field : config.getFields()) {
                String value = row.get(field.getName());
                if (field.getRequired() && StringUtils.isEmpty(value)) {
                    return ValidationResult.error("第" + i + "行" + field.getName() + "不能为空");
                }
                if (!validateFieldType(value, field.getType())) {
                    return ValidationResult.error("第" + i + "行" + field.getName() + "格式错误");
                }
            }
        }

        return ValidationResult.success();
    }
}
```

### 3.3 文件处理方案

#### 3.3.1 Excel处理（Apache POI）

```java
/**
 * Excel处理工具
 */
public class ExcelUtil {

    /**
     * 读取Excel文件
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
        }

        return result;
    }

    /**
     * 生成Excel文件
     */
    public static byte[] generateExcel(List<Map<String, String>> data,
                                      List<String> headers) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("数据");

            // 写入表头
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                headerRow.createCell(i).setCellValue(headers.get(i));
            }

            // 写入数据
            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Map<String, String> rowData = data.get(i);
                int col = 0;
                for (String value : rowData.values()) {
                    row.createCell(col++).setCellValue(value);
                }
            }

            // 输出为字节数组
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new BizException(ErrorCode.FILE_GENERATE_FAILED);
        }
    }
}
```

#### 3.3.2 CSV处理

```java
/**
 * CSV处理工具
 */
public class CsvUtil {

    /**
     * 读取CSV文件
     */
    public static List<Map<String, String>> readCsv(MultipartFile file) {
        List<Map<String, String>> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            // 读取表头
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return result;
            }
            String[] headers = headerLine.split(",");

            // 读取数据
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                Map<String, String> rowData = new LinkedHashMap<>();
                for (int i = 0; i < headers.length && i < values.length; i++) {
                    rowData.put(headers[i].trim(), values[i].trim());
                }
                result.add(rowData);
            }
        } catch (IOException e) {
            throw new BizException(ErrorCode.FILE_READ_FAILED);
        }

        return result;
    }
}
```

### 3.4 JSON字段处理方案

#### 3.4.1 Entity定义（MyBatis-Plus处理JSON）

```java
/**
 * 数据明细实体
 */
@Data
@TableName("t_data_detail")
public class DataDetail {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long fileId;
    private String seqNo;
    private String idCard;
    private String phone;

    /**
     * JSON字段 - 使用MyBatis-Plus的TypeHandler处理
     */
    @TableField(typeHandler = JsonTypeHandler.class)
    private Map<String, String> customFields;

    private Integer rowNum;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

/**
 * JSON类型处理器
 */
public class JsonTypeHandler extends BaseTypeHandler<Map<String, String>> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
                                   Map<String, String> parameter,
                                   JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSONUtil.toJsonStr(parameter));
    }

    @Override
    public Map<String, String> getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        String json = rs.getString(columnName);
        return parseJson(json);
    }

    private Map<String, String> parseJson(String json) {
        if (StringUtils.isEmpty(json)) {
            return new HashMap<>();
        }
        return JSONUtil.toBean(json, new TypeReference<Map<String, String>>() {});
    }
}
```

#### 3.4.2 JSON查询

```java
/**
 * 数据明细Mapper（处理JSON字段查询）
 */
@Mapper
public interface DataDetailMapper extends BaseMapper<DataDetail> {

    /**
     * 根据文件ID查询数据详情
     */
    @Select("SELECT id, file_id, seq_no, id_card, phone, " +
            "custom_fields, row_num, create_time " +
            "FROM t_data_detail " +
            "WHERE file_id = #{fileId} " +
            "ORDER BY row_num " +
            "LIMIT #{offset}, #{size}")
    List<DataDetail> selectByFileId(@Param("fileId") Long fileId,
                                    @Param("offset") Integer offset,
                                    @Param("size") Integer size);

    /**
     * 统计文件数据条数
     */
    @Select("SELECT COUNT(*) FROM t_data_detail WHERE file_id = #{fileId}")
    Integer countByFileId(@Param("fileId") Long fileId);

    /**
     * 检查身份证重复（同一文件内）
     */
    @Select("SELECT COUNT(*) FROM t_data_detail " +
            "WHERE file_id = #{fileId} AND id_card = #{idCard}")
    Integer countByIdCard(@Param("fileId") Long fileId,
                         @Param("idCard") String idCard);
}
```

### 3.5 模板生成方案

```java
/**
 * 模板服务实现
 */
@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final FieldConfigService fieldConfigService;

    /**
     * 生成Excel模板
     */
    @Override
    public byte[] generateExcelTemplate(Long userId) {
        // 获取用户字段配置
        FieldConfigResponse config = fieldConfigService.getFieldConfig(userId);

        // 构建表头（固定字段 + 自定义字段）
        List<String> headers = new ArrayList<>();
        headers.addAll(Arrays.asList("序号", "身份证", "手机号"));
        for (FieldConfigItem field : config.getFields()) {
            headers.add(field.getName());
        }

        // 生成空Excel
        List<Map<String, String>> emptyData = new ArrayList<>();
        return ExcelUtil.generateExcel(emptyData, headers);
    }

    /**
     * 生成CSV模板
     */
    @Override
    public byte[] generateCsvTemplate(Long userId) {
        FieldConfigResponse config = fieldConfigService.getFieldConfig(userId);

        StringBuilder sb = new StringBuilder();
        sb.append("序号,身份证,手机号");
        for (FieldConfigItem field : config.getFields()) {
            sb.append(",").append(field.getName());
        }
        sb.append("\n");

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
```

### 3.6 操作日志方案

```java
/**
 * 操作日志切面（AOP实现）
 */
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;

    /**
     * 定义切点：标注了@Log注解的方法
     */
    @Pointcut("@annotation(com.traespace.filemanager.annotation.Log)")
    public void logPointcut() {}

    /**
     * 环绕通知：记录操作日志
     */
    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        Object result = point.proceed();
        long costTime = System.currentTimeMillis() - beginTime;

        // 获取注解信息
        MethodSignature signature = (MethodSignature) point.getSignature();
        Log logAnnotation = signature.getMethod().getAnnotation(Log.class);

        // 构建日志记录
        OperationLog operationLog = OperationLog.builder()
                .userId(UserContext.getUserId())
                .operationType(logAnnotation.operation().name())
                .targetId(extractTargetId(point.getArgs()))
                .description(logAnnotation.value())
                .costTime(costTime)
                .createTime(LocalDateTime.now())
                .build();

        operationLogService.save(operationLog);
        return result;
    }
}

/**
 * 日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    String value();              // 操作描述
    OperationType operation();   // 操作类型
}

/**
 * 使用示例
 */
@PostMapping("/upload")
@Log(value = "上传文件", operation = OperationType.UPLOAD)
public Result<Void> upload(@RequestParam("file") MultipartFile file) {
    fileService.upload(file);
    return Result.success();
}
```

### 3.7 定时任务方案（数据清理）

```java
/**
 * 定时任务配置
 */
@Configuration
@EnableScheduling
public class ScheduleConfig {

    /**
     * 每天凌晨2点清理一个月前的数据
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredData() {
        log.info("[Schedule] 开始清理过期数据");

        LocalDateTime expireTime = LocalDateTime.now().minusMonths(1);

        // 删除过期数据明细
        dataDetailMapper.deleteByCreateTimeBefore(expireTime);

        // 删除过期文件记录
        fileRecordMapper.deleteByCreateTimeBefore(expireTime);

        // 删除过期操作日志
        operationLogMapper.deleteByCreateTimeBefore(expireTime.minusYears(1));

        log.info("[Schedule] 过期数据清理完成");
    }
}
```

---

## 4. 接口实现清单

### 4.1 用户认证模块

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 用户注册 | POST | /api/auth/register | - |
| 用户登录 | POST | /api/auth/login | 返回JWT Token |
| 用户登出 | POST | /api/auth/logout | 清除Redis中的Token |
| 创建用户 | POST | /api/user/create | 仅管理员 |

### 4.2 字段配置模块

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取配置 | GET | /api/field/config | 获取当前用户的字段配置 |
| 保存配置 | POST | /api/field/config | 保存/更新字段配置 |
| 获取字段类型 | GET | /api/field/types | 返回TEXT/NUMBER/DATE |

### 4.3 模板管理模块

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 下载Excel模板 | GET | /api/template/excel | 返回.xlsx文件 |
| 下载CSV模板 | GET | /api/template/csv | 返回.csv文件 |

### 4.4 文件管理模块

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 上传文件 | POST | /api/file/upload | multipart/form-data |
| 文件列表 | GET | /api/file/list | 分页查询 |
| 文件详情 | GET | /api/file/detail | 分页展示数据 |
| 下载文件 | GET | /api/file/download | 返回Excel/CSV |
| 删除文件 | DELETE | /api/file/delete | 仅管理员 |

### 4.5 统计与日志模块

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取统计 | GET | /api/statistics/summary | 文件数、数据条数 |
| 操作日志 | GET | /api/log/list | 分页查询 |

---

## 5. 数据库设计

### 5.1 建表SQL

```sql
-- 用户表
CREATE TABLE t_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    username VARCHAR(64) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(128) NOT NULL COMMENT '密码（加密）',
    role VARCHAR(16) NOT NULL DEFAULT 'USER' COMMENT '角色：USER/ADMIN',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（1:正常 0:禁用）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 字段配置表
CREATE TABLE t_field_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    field_name VARCHAR(64) NOT NULL COMMENT '字段名称',
    field_type VARCHAR(16) NOT NULL COMMENT '字段类型：TEXT/NUMBER/DATE',
    required TINYINT NOT NULL DEFAULT 0 COMMENT '是否必填：0否1是',
    sort_order INT NOT NULL COMMENT '排序顺序',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字段配置表';

-- 文件记录表
CREATE TABLE t_file_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名（系统生成）',
    original_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    user_id BIGINT NOT NULL COMMENT '上传人ID',
    row_count INT NOT NULL COMMENT '数据条数',
    field_config_snapshot JSON NOT NULL COMMENT '字段配置快照',
    upload_time DATETIME NOT NULL COMMENT '上传时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_upload_time (upload_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件记录表';

-- 数据明细表
CREATE TABLE t_data_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    file_id BIGINT NOT NULL COMMENT '文件ID',
    seq_no VARCHAR(32) NOT NULL COMMENT '序号',
    id_card VARCHAR(32) NOT NULL COMMENT '身份证号',
    phone VARCHAR(16) NOT NULL COMMENT '手机号',
    custom_fields JSON NOT NULL COMMENT '自定义字段JSON',
    row_num INT NOT NULL COMMENT '原文件行号',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_file_id (file_id),
    INDEX idx_id_card (id_card)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据明细表';

-- 操作日志表
CREATE TABLE t_operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    username VARCHAR(64) NOT NULL COMMENT '用户名',
    operation_type VARCHAR(32) NOT NULL COMMENT '操作类型：UPLOAD/DELETE等',
    target_id BIGINT COMMENT '目标ID（文件ID）',
    description VARCHAR(500) COMMENT '操作详情',
    request_ip VARCHAR(64) COMMENT '请求IP',
    cost_time BIGINT COMMENT '耗时（毫秒）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';
```

---

## 6. 异常处理方案

### 6.1 统一异常处理

```java
/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(BizException.class)
    public Result<Void> handleBizException(BizException e) {
        log.error("[业务异常] {}", e.getMessage());
        return Result.error(e.getErrorCode(), e.getMessage());
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .findFirst()
                .orElse("参数校验失败");
        return Result.error(ErrorCode.INVALID_PARAM, message);
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("[系统异常]", e);
        return Result.error(ErrorCode.SYSTEM_ERROR, "系统内部错误");
    }
}
```

### 6.2 错误码定义

```java
/**
 * 错误码枚举
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 通用错误 1xxx
    SUCCESS(0, "success"),
    INVALID_PARAM(1001, "参数错误"),
    TOKEN_MISSING(1002, "Token缺失"),
    TOKEN_EXPIRED(1003, "Token已过期"),
    PERMISSION_DENIED(1004, "无权限"),

    // 用户相关 2xxx
    USER_EXISTS(2001, "用户名已存在"),
    USER_NOT_FOUND(2002, "用户不存在"),
    PASSWORD_ERROR(2003, "密码错误"),

    // 文件相关 3xxx
    FILE_FORMAT_ERROR(3001, "文件格式不支持"),
    FILE_SIZE_EXCEED(3002, "文件大小超限"),
    FILE_HEADER_MISMATCH(3003, "表头与模板不匹配"),

    // 数据校验 4xxx
    SEQ_NO_EMPTY(4001, "序号不能为空"),
    ID_CARD_ERROR(4002, "身份证格式错误"),
    PHONE_ERROR(4003, "手机号格式错误"),
    FIELD_REQUIRED(4004, "字段不能为空"),
    FIELD_TYPE_ERROR(4005, "字段格式错误"),
    ID_CARD_DUPLICATE(4006, "身份证号重复"),

    // 系统错误 5xxx
    SYSTEM_ERROR(5001, "系统内部错误"),
    DB_ERROR(5002, "数据库错误");

    private final int code;
    private final String message;
}
```

---

## 7. 部署方案

### 7.1 应用配置

```yaml
# application.yml
spring:
  application:
    name: file-manager
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/file_manager?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: ${DB_PASSWORD}
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      database: 0
      timeout: 3000ms

mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  type-aliases-package: com.traespace.filemanager.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

# JWT配置
jwt:
  secret: ${JWT_SECRET:your-secret-key-change-in-production}
  expire-time: 7200000  # 2小时（毫秒）

# 文件上传配置
spring.servlet.multipart:
  max-file-size: 200MB
  max-request-size: 200MB
```

### 7.2 部署架构

```
┌─────────────────────────────────────────────────────────────┐
│                         Nginx                               │
│                    (反向代理 + 静态资源)                     │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                    Spring Boot App                          │
│                    (多实例部署，可扩展)                      │
└─────────────────────────────────────────────────────────────┘
           ↓                               ↓
┌─────────────────────┐       ┌─────────────────────┐
│      MySQL 8.0      │       │      Redis 7.x      │
│   (主从复制/读写分离) │       │    (哨兵/集群)       │
└─────────────────────┘       └─────────────────────┘
```

---

## 8. 开发计划

### 8.1 开发阶段

| 阶段 | 任务 | 工作量 |
|------|------|--------|
| 第一阶段 | 项目搭建、数据库设计、基础框架 | 2天 |
| 第二阶段 | 用户认证、字段配置、模板管理 | 3天 |
| 第三阶段 | 文件上传、数据校验、数据存储 | 4天 |
| 第四阶段 | 文件列表、详情预览、文件下载 | 2天 |
| 第五阶段 | 操作日志、数据统计、定时任务 | 2天 |
| 第六阶段 | 测试、优化、部署 | 3天 |

### 8.2 关键里程碑

1. **Week 1**: 完成基础框架和用户认证
2. **Week 2**: 完成核心业务功能（上传、校验）
3. **Week 3**: 完成辅助功能和测试

---

## 9. 附录

### 9.1 包结构图

```
filemanager/
├── src/main/java/com/traespace/filemanager/
│   ├── controller/
│   ├── service/
│   │   └── impl/
│   ├── mapper/
│   ├── entity/
│   ├── dto/
│   │   ├── request/
│   │   └── response/
│   ├── vo/
│   ├── config/
│   ├── util/
│   ├── exception/
│   ├── enums/
│   ├── annotation/
│   └── FileManagerApplication.java
├── src/main/resources/
│   ├── mapper/
│   ├── application.yml
│   └── application-prod.yml
└── src/test/java/
```

### 9.2 技术要点总结

| 要点 | 实现方案 |
|------|----------|
| 认证 | JWT + Redis |
| 权限 | 自定义注解 + 拦截器 |
| 数据校验 | 工具类 + 校验流程 |
| JSON处理 | MyBatis-Plus TypeHandler |
| 文件处理 | Apache POI |
| 日志记录 | AOP切面 |
| 定时任务 | Spring @Scheduled |
| 异常处理 | @RestControllerAdvice |


```
com.traespace.filemanager
├── controller          # 控制器层 - 参数校验和结果封装
├── service             # 业务逻辑层 - 接口+实现分离
│   └── impl           # 业务实现类
├── mapper              # 数据访问层 - MyBatis Mapper
├── entity              # 数据库实体
├── dto                 # 数据传输对象
│   ├── request        # 请求DTO
│   └── response       # 响应DTO
├── vo                  # 视图对象 - 前端展示
├── config              # 配置类
├── util                # 工具类
├── exception           # 异常处理
├── enums               # 枚举类
└── FileManagerApplication.java
```

---

## 2. 详细包结构

### 2.1 controller - 控制器层

```
controller
├── auth
│   └── AuthController.java              # 用户登录、登出、注册
├── user
│   └── UserController.java              # 用户管理（管理员创建用户）
├── field
│   └── FieldConfigController.java       # 字段配置CRUD
├── template
│   └── TemplateController.java          # 模板下载
├── file
│   └── FileController.java              # 文件上传、列表、详情、下载、删除
├── log
│   └── OperationLogController.java      # 操作日志查询
└── statistics
    └── StatisticsController.java        # 数据统计
```

### 2.2 service - 业务逻辑层

```
service
├── auth
│   ├── AuthService.java                 # 认证服务接口
│   └── impl
│       └── AuthServiceImpl.java         # 认证服务实现
├── user
│   ├── UserService.java                 # 用户服务接口
│   └── impl
│       └── UserServiceImpl.java         # 用户服务实现
├── field
│   ├── FieldConfigService.java          # 字段配置服务接口
│   └── impl
│       └── FieldConfigServiceImpl.java  # 字段配置服务实现
├── template
│   ├── TemplateService.java             # 模板服务接口
│   └── impl
│       └── TemplateServiceImpl.java     # 模板服务实现
├── file
│   ├── FileService.java                 # 文件服务接口
│   └── impl
│       └── FileServiceImpl.java         # 文件服务实现
├── log
│   ├── OperationLogService.java         # 日志服务接口
│   └── impl
│       └── OperationLogServiceImpl.java # 日志服务实现
└── statistics
    ├── StatisticsService.java           # 统计服务接口
    └── impl
        └── StatisticsServiceImpl.java   # 统计服务实现
```

### 2.3 mapper - 数据访问层

```
mapper
├── UserMapper.java                      # 用户Mapper
├── FieldConfigMapper.java               # 字段配置Mapper
├── FileRecordMapper.java                # 文件记录Mapper
├── DataDetailMapper.java                # 数据明细Mapper
└── OperationLogMapper.java              # 操作日志Mapper
```

### 2.4 entity - 数据库实体

```
entity
├── User.java                            # 用户实体
├── FieldConfig.java                     # 字段配置实体
├── FileRecord.java                      # 文件记录实体
├── DataDetail.java                      # 数据明细实体
└── OperationLog.java                    # 操作日志实体
```

### 2.5 dto - 数据传输对象

```
dto
├── request
│   ├── auth
│   │   ├── RegisterRequest.java        # 注册请求
│   │   └── LoginRequest.java           # 登录请求
│   ├── user
│   │   └── CreateUserRequest.java      # 创建用户请求（管理员）
│   ├── field
│   │   ├── FieldConfigRequest.java     # 字段配置请求
│   │   └── FieldConfigItem.java        # 字段配置项
│   ├── file
│   │   └── FileUploadRequest.java      # 文件上传封装（MultipartFile）
│   └── common
│       ├── BasePageRequest.java        # 分页请求基类
│       └── IdRequest.java              # ID请求
└── response
    ├── auth
    │   └── LoginResponse.java          # 登录响应（含token）
    ├── field
    │   └── FieldConfigResponse.java    # 字段配置响应
    ├── file
    │   ├── FileListResponse.java       # 文件列表响应
    │   ├── FileDetailResponse.java     # 文件详情响应
    │   └── UploadProgressResponse.java # 上传进度响应
    ├── statistics
    │   └── StatisticsResponse.java     # 统计数据响应
    └── common
        ├── BasePageResponse.java       # 分页响应基类
        └── Result.java                 # 统一响应结果
```

### 2.6 vo - 视图对象

```
vo
├── file
│   ├── FileListItemVO.java             # 文件列表项VO
│   ├── DataDetailItemVO.java           # 数据详情项VO
│   └── FieldConfigSnapshotVO.java      # 字段配置快照VO
├── log
│   └── OperationLogItemVO.java         # 操作日志项VO
└── user
    └── UserVO.java                     # 用户VO
```

### 2.7 config - 配置类

```
config
├── MyBatisPlusConfig.java              # MyBatis-Plus配置
├── RedisConfig.java                    # Redis配置
├── WebConfig.java                      # Web配置（CORS、拦截器）
├── SecurityConfig.java                 # 安全配置（密码加密）
└── JacksonConfig.java                  # JSON配置（日期格式、空值处理）
```

### 2.8 util - 工具类

```
util
├── JwtUtil.java                        # JWT工具类
├── PasswordUtil.java                   # 密码加密工具
├── DateUtil.java                       # 日期工具（使用LocalDateTime）
├── FileUtil.java                       # 文件工具
├── ExcelUtil.java                      # Excel处理工具
├── CsvUtil.java                        # CSV处理工具
├── ValidationUtil.java                 # 校验工具
│   ├── IdCardValidator.java           # 身份证校验
│   ├── PhoneValidator.java            # 手机号校验
│   ├── DateValidator.java             # 日期校验
│   └── FieldValidator.java            # 字段类型校验
└── JsonUtil.java                       # JSON工具
```

### 2.9 exception - 异常处理

```
exception
├── BizException.java                   # 业务异常基类
├── UnauthorizedException.java          # 未授权异常
├── BadRequestException.java            # 请求参数异常
└── GlobalExceptionHandler.java         # 全局异常处理器
```

### 2.10 enums - 枚举类

```
enums
├── ErrorCode.java                      # 错误码枚举
├── UserRole.java                       # 用户角色枚举
├── FieldType.java                      # 字段类型枚举
├── FileType.java                       # 文件类型枚举
├── OperationType.java                  # 操作类型枚举
└── FileStatus.java                     # 文件状态枚举
```

---

## 3. 核心类职责说明

### 3.1 Controller层职责

- **只做参数校验**（使用 `@Valid` 和自定义校验）
- **只做结果封装**（调用Service后将结果封装为统一响应格式）
- **不包含业务逻辑**

### 3.2 Service层职责

- **所有业务逻辑**：数据处理、校验、事务控制
- **接口+实现分离**：定义接口，impl包下实现
- **异常处理**：使用 `BizException(ErrorCode)` 抛出业务异常
- **日志记录**：`log.info("[模块] 操作, param={}", param)`

### 3.3 Mapper层职责

- **只做数据访问**：CRUD操作
- **使用MyBatis-Plus**：继承 `BaseMapper<T>`
- **不包含业务逻辑**

---

## 4. 代码示例

### 4.1 Controller示例

```java
@RestController
@RequestMapping("/api/field")
@RequiredArgsConstructor
public class FieldConfigController {

    private final FieldConfigService fieldConfigService;

    /**
     * 获取字段配置
     */
    @GetMapping("/config")
    public Result<FieldConfigResponse> getFieldConfig() {
        FieldConfigResponse response = fieldConfigService.getFieldConfig();
        return Result.success(response);
    }

    /**
     * 保存字段配置
     */
    @PostMapping("/config")
    public Result<Void> saveFieldConfig(@Valid @RequestBody FieldConfigRequest request) {
        fieldConfigService.saveFieldConfig(request);
        return Result.success();
    }
}
```

### 4.2 Service接口示例

```java
public interface FieldConfigService {
    /**
     * 获取字段配置
     */
    FieldConfigResponse getFieldConfig();

    /**
     * 保存字段配置
     */
    void saveFieldConfig(FieldConfigRequest request);
}
```

### 4.3 Service实现示例

```java
@Service
@RequiredArgsConstructor
public class FieldConfigServiceImpl implements FieldConfigService {

    private final FieldConfigMapper fieldConfigMapper;
    private static final Logger log = LoggerFactory.getLogger(FieldConfigServiceImpl.class);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveFieldConfig(FieldConfigRequest request) {
        log.info("[FieldConfig] 保存字段配置, request={}", request);

        // 校验字段数量
        if (request.getFields().size() > 10) {
            throw new BizException(ErrorCode.FIELD_COUNT_EXCEED);
        }

        // 业务逻辑处理...
    }
}
```

### 4.4 Entity示例（使用MyBatis-Plus注解）

```java
@Data
@TableName("t_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("role")
    private UserRole role;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
```

---

## 5. 分层调用规范

```
┌─────────────────────────────────────────────────────────────┐
│                         Controller                          │
│  - 参数校验 (@Valid)                                        │
│  - 调用Service                                              │
│  - 结果封装 (Result.success())                              │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                          Service                            │
│  - 业务逻辑处理                                             │
│  - 数据校验                                                 │
│  - 事务控制 (@Transactional)                                │
│  - 日志记录 (log.info)                                      │
│  - 异常抛出 (throw new BizException)                        │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                           Mapper                            │
│  - CRUD操作 (MyBatis-Plus)                                  │
│  - 自定义SQL (复杂查询)                                      │
└─────────────────────────────────────────────────────────────┘
```

---

## 6. 包结构依赖规则

| 层级 | 可调用 | 不可调用 |
|------|--------|----------|
| Controller | Service、DTO、VO | Mapper、Entity |
| Service | Mapper、Entity、Util、Enum | Controller |
| Mapper | Entity | Service、Controller |
| Entity | 无依赖 | 业务逻辑层 |
| DTO | 无依赖 | 业务逻辑层 |
| Util | 无依赖 | 业务逻辑层 |
| Enum | 无依赖 | 业务逻辑层 |
| Config | 无依赖 | 业务逻辑层 |
