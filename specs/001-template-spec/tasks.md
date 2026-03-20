# 角色管理与模板增强功能 - 任务列表

> 本文档遵循 **TDD（测试驱动开发）原则**：所有测试任务先于实现任务执行。
>
> **图例说明：**
> - `[P]` = 可并行执行（无依赖关系）
> - `依赖: N` = 依赖任务ID为N的任务完成后才能执行
> - `状态: []` = 待执行，`[进行中]`，`[已完成]`

---

## 任务统计

| 阶段 | 测试任务 | 实现任务 | 小计 |
|------|----------|----------|------|
| 阶段一：MockDataGenerator工具 | 1 | 1 | 2 |
| 阶段二：角色管理DTO | 2 | 2 | 4 |
| 阶段三：角色管理Service | 1 | 2 | 3 |
| 阶段四：角色管理Controller | 1 | 1 | 2 |
| 阶段五：删除权限 | 1 | 1 | 2 |
| 阶段六：带数据模板Service | 2 | 2 | 4 |
| 阶段七：带数据模板Controller | 1 | 1 | 2 |
| 阶段八：集成测试 | 1 | 0 | 1 |
| **总计** | **10** | **10** | **20** |

---

## 阶段一：MockDataGenerator 工具类

### 测试任务

#### 001 [P] 创建 MockDataGeneratorTest.java
**文件路径:** `src/test/java/com/traespace/filemanager/util/MockDataGeneratorTest.java`

**任务描述:** 创建 MockDataGenerator 的单元测试类，包含所有公共方法的测试用例。

**验收标准:**
- 测试类使用 JUnit 5
- 包含以下测试方法：
  - `testGenerateIdCard()`: 循环1000次验证身份证格式（18位+通过ValidationUtil校验）
  - `testGeneratePhone()`: 循环1000次验证手机号格式（11位+通过ValidationUtil校验）
  - `testGenerateDate()`: 循环1000次验证日期格式（通过ValidationUtil.isValidDate校验）
  - `testGenerateText()`: 验证生成的文本长度在1-64之间
  - `testGenerateNumber()`: 验证生成的数字是1-99999之间的数字字符串
- 使用 `@Test` 注解标记测试方法
- 使用 `assertEquals`、`assertTrue` 等断言
- 测试失败时提供有意义的错误信息

**状态:** ✅ [已完成]

---

### 实现任务

#### 002 依赖: 001 | 创建 MockDataGenerator.java
**文件路径:** `src/main/java/com/traespace/filemanager/util/MockDataGenerator.java`

**任务描述:** 创建随机数据生成工具类，提供生成符合格式要求的随机数据方法。

**验收标准:**
- 类使用 `@NoArgsConstructor(access = AccessLevel.PRIVATE)` 注解，私有构造函数
- 包含私有静态常量：
  - `COMMON_CHARS`: 常用汉字数组（约200个）
  - `AREA_CODES`: 身份证区划代码数组（北京、上海、广州、深圳）
  - `SURNAMES`: 常见姓氏数组（20个）
  - `NAME_CHARS`: 常用名字用字数组（30个）
  - `random`: Random 实例
  - `log`: Logger 实例
- 包含公共静态方法：
  - `generateIdCard()`: 返回18位身份证号（含正确校验码）
  - `generatePhone()`: 返回11位手机号（1开头，第2位3-9）
  - `generateDate()`: 返回YYYY-MM-DD格式日期（最近365天内）
  - `generateText(int maxLength)`: 返回1-maxLength长度的随机中文文本
  - `generateNumber()`: 返回1-99999的随机数字字符串
- 包含私有方法：
  - `calculateIdCardCheckCode(String id17)`: 计算身份证校验码
- 符合阿里巴巴Java开发手册规范
- 添加完整的 Javadoc 注释

**状态:** ✅ [已完成]

---

## 阶段二：角色管理 DTO

### 测试任务

#### 003 [P] 创建 RoleUpdateRequestTest.java
**文件路径:** `src/test/java/com/traespace/filemanager/dto/request/user/RoleUpdateRequestTest.java`

**任务描述:** 创建角色更新请求DTO的测试类。

**验收标准:**
- 测试 `@NotBlank` 校验：role为空时校验失败
- 测试 `@Pattern` 校验：role为"INVALID"时校验失败
- 测试有效值：role为"USER"或"ADMIN"时校验通过
- 测试getter/setter方法正常工作

**状态:** ✅ [已完成]

#### 004 [P] 创建 RoleResponseTest.java
**文件路径:** `src/test/java/com/traespace/filemanager/dto/response/user/RoleResponseTest.java`

**任务描述:** 创建角色响应DTO的测试类。

**验收标准:**
- 测试默认构造函数创建对象
- 测试setter/getter方法
- 测试UserRole枚举值与描述的对应关系

**状态:** ✅ [已完成]

---

### 实现任务

#### 005 依赖: 003, 004 | 创建 RoleUpdateRequest.java
**文件路径:** `src/main/java/com/traespace/filemanager/dto/request/user/RoleUpdateRequest.java`

**任务描述:** 创建角色更新请求DTO。

**验收标准:**
- 包含 `role` 字段（String类型）
- 使用 `@NotBlank(message = "角色不能为空")` 注解
- 使用 `@Pattern(regexp = "^(USER|ADMIN)$", message = "角色必须是USER或ADMIN")` 注解
- 提供getter/setter方法
- 添加 Javadoc 注释

**状态:** [] [待执行]

#### 006 依赖: 003, 004 | 创建 RoleResponse.java
**文件路径:** `src/main/java/com/traespace/filemanager/dto/response/user/RoleResponse.java`

**任务描述:** 创建角色响应DTO。

**验收标准:**
- 包含 `role` 字段（UserRole枚举类型）
- 包含 `roleDesc` 字段（String类型）
- 提供getter/setter方法
- 添加 Javadoc 注释

**状态:** [] [待执行]

---

## 阶段三：角色管理 Service

### 测试任务

#### 007 依赖: 005, 006 | 扩展 UserServiceTest.java（角色相关测试）
**文件路径:** `src/test/java/com/traespace/filemanager/service/user/UserServiceTest.java`

**任务描述:** 在现有UserServiceTest中添加角色相关测试用例。

**验收标准:**
- 添加 `testGetCurrentRole()` 测试方法：
  - Mock UserMapper返回USER角色
  - 调用 `getCurrentRole()`
  - 验证返回的RoleResponse中role为USER，roleDesc为"普通用户"
- 添加 `testUpdateRole()` 测试方法：
  - Mock UserMapper.selectById()返回现有用户
  - Mock UserMapper.updateById()
  - 调用 `updateRole(userId, ADMIN)`
  - 验证userMapper.updateById()被调用
  - 验证更新后的角色为ADMIN
- 添加 `testUpdateRole_UserNotFound()` 测试方法：
  - Mock UserMapper.selectById()返回null
  - 调用 `updateRole()` 应抛出 USER_NOT_FOUND 异常
- 使用 `@ExtendWith(MockitoExtension.class)` 和 `@Mock` 注解

**状态:** ✅ [已完成]

---

### 实现任务

#### 008 依赖: 007 | 修改 UserService.java
**文件路径:** `src/main/java/com/traespace/filemanager/service/user/UserService.java`

**任务描述:** 在UserService接口中添加角色相关方法。

**验收标准:**
- 添加方法签名：`RoleResponse getCurrentRole(Long userId);`
- 添加方法签名：`void updateRole(Long userId, UserRole role);`
- 添加完整的 Javadoc 注释，包含 `@param` 和 `@return/@throws`

**状态:** [] [待执行]

#### 009 依赖: 008 | 修改 UserServiceImpl.java
**文件路径:** `src/main/java/com/traespace/filemanager/service/impl/UserServiceImpl.java`

**任务描述:** 实现角色相关方法。

**验收标准:**
- 实现 `getCurrentRole()` 方法：
  - 调用 userMapper.selectById(userId) 获取用户
  - 如果用户不存在，抛出 USER_NOT_FOUND 异常
  - 创建 RoleResponse 对象，设置 role 和 roleDesc
  - 记录日志：`log.info("[用户] 获取当前用户角色, userId={}, role={}", userId, role)`
  - 返回 RoleResponse
- 实现 `updateRole()` 方法：
  - 调用 userMapper.selectById(userId) 验证用户存在
  - 如果用户不存在，抛出 USER_NOT_FOUND 异常
  - 更新用户角色：`user.setRole(role)`
  - 调用 userMapper.updateById(user)
  - 记录日志：`log.info("[用户] 更新用户角色, userId={}, newRole={}", userId, role)`
- 符合阿里巴巴Java开发手册规范

**状态:** ✅ [已完成]

---

## 阶段四：角色管理 Controller

### 测试任务

#### 010 依赖: 005, 006 | 扩展 UserControllerTest.java（角色相关测试）
**文件路径:** `src/test/java/com/traespace/filemanager/controller/user/UserControllerTest.java`

**任务描述:** 在现有UserControllerTest中添加角色相关测试用例。

**验收标准:**
- 添加 `testGetCurrentRole()` 测试方法：
  - Mock UserContext.getUserId() 返回测试用户ID
  - Mock userService.getCurrentRole() 返回 RoleResponse
  - 调用 GET /api/user/role
  - 验证返回状态码200，data中role和roleDesc正确
- 添加 `testUpdateRole()` 测试方法：
  - Mock UserContext.getUserId() 返回测试用户ID
  - Mock userService.updateRole()
  - 调用 POST /api/user/role，请求体 `{"role":"ADMIN"}`
  - 验证返回状态码200
- 添加 `testUpdateRole_InvalidRole()` 测试方法：
  - 调用 POST /api/user/role，请求体 `{"role":"INVALID"}`
  - 验证返回参数错误（400或对应的业务错误码）

**状态:** ✅ [已完成]

---

### 实现任务

#### 011 依赖: 010 | 修改 UserController.java
**文件路径:** `src/main/java/com/traespace/filemanager/controller/user/UserController.java`

**任务描述:** 在UserController中添加角色相关接口。

**验收标准:**
- 添加 `getCurrentRole()` 方法：
  - 使用 `@GetMapping("/role")` 注解
  - 使用 `@Operation(summary = "获取当前用户角色")` 注解
  - 从 UserContext 获取当前用户ID
  - 调用 userService.getCurrentRole(userId)
  - 返回 `Result.success(response)`
- 添加 `updateRole()` 方法：
  - 使用 `@PostMapping("/role")` 注解
  - 使用 `@Operation(summary = "更新用户角色")` 注解
  - 使用 `@Valid @RequestBody RoleUpdateRequest` 参数
  - 从 UserContext 获取当前用户ID
  - 将 request.getRole() 转换为 UserRole 枚举
  - 调用 userService.updateRole(userId, role)
  - 返回 `Result.success()`
- Controller层只做参数校验和结果封装，不包含业务逻辑

**状态:** ✅ [已完成]

---

## 阶段五：删除权限

### 测试任务

#### 012 [P] 创建 FileServiceDeleteTest.java
**文件路径:** `src/test/java/com/traespace/filemanager/service/file/FileServiceDeleteTest.java`

**任务描述:** 创建文件删除权限的专门测试类。

**验收标准:**
- 添加 `testDeleteFile_AsOwner()` 测试方法：
  - 准备：普通用户，文件所有者是当前用户
  - 执行：调用 deleteFile()
  - 验证：删除成功，fileRecord.status设为0，dataDetail被删除
- 添加 `testDeleteFile_AsAdmin_OwnerFile()` 测试方法：
  - 准备：管理员用户，文件所有者是其他用户
  - 执行：调用 deleteFile()
  - 验证：删除成功
- 添加 `testDeleteFile_AsUser_OwnerFile()` 测试方法：
  - 准备：普通用户，文件所有者是其他用户
  - 执行：调用 deleteFile()
  - 验证：抛出 PERMISSION_DENIED 异常
- 添加 `testDeleteFile_FileNotFound()` 测试方法：
  - 准备：fileRecordMapper.selectById() 返回 null
  - 执行：调用 deleteFile()
  - 验证：抛出 FILE_NOT_FOUND 异常
- 使用 Mock 模拟 Mapper 行为

**状态:** ✅ [已完成]

---

### 实现任务

#### 013 依赖: 012 | 修改 FileServiceImpl.java（deleteFile方法）
**文件路径:** `src/main/java/com/traespace/filemanager/service/impl/FileServiceImpl.java`

**任务描述:** 修改deleteFile方法，支持管理员删除任何用户的文件。

**修改前逻辑:**
```java
if (fileRecord == null || !fileRecord.getUserId().equals(userId)) {
    throw new BizException(ErrorCode.FILE_NOT_FOUND);
}
```

**修改后逻辑:**
```java
// 1. 检查文件是否存在
if (fileRecord == null) {
    throw new BizException(ErrorCode.FILE_NOT_FOUND);
}

// 2. 获取当前用户信息（包含角色）
User currentUser = userMapper.selectById(userId);
if (currentUser == null) {
    throw new BizException(ErrorCode.USER_NOT_FOUND);
}

// 3. 权限校验：管理员或文件所有者可以删除
if (currentUser.getRole() != UserRole.ADMIN
    && !fileRecord.getUserId().equals(userId)) {
    throw new BizException(ErrorCode.PERMISSION_DENIED);
}

// 4. 执行删除逻辑（保持不变）
fileRecord.setStatus(0);
fileRecordMapper.updateById(fileRecord);
// ... 删除 dataDetail

// 5. 记录日志（增强）
log.info("[文件删除] 文件删除成功, fileId={}, operatorId={}, operatorRole={}",
    fileId, userId, currentUser.getRole());
```

**验收标准:**
- 修改 deleteFile 方法的权限判断逻辑
- 注入 UserMapper 依赖（如果尚未注入）
- 管理员可以删除任何文件
- 普通用户只能删除自己的文件
- 权限不足时抛出 PERMISSION_DENIED 异常
- 日志中记录操作者角色信息

**状态:** ✅ [已完成]

---

## 阶段六：带数据模板 Service

### 测试任务

#### 014 依赖: 002 | 扩展 TemplateServiceTest.java（带数据模板测试）
**文件路径:** `src/test/java/com/traespace/filemanager/service/template/TemplateServiceTest.java`

**任务描述:** 在现有TemplateServiceTest中添加带数据模板测试用例。

**验收标准:**
- 添加 `testGenerateExcelTemplateWithData_SmallCount()` 测试方法：
  - Mock FieldConfigService 返回空配置列表
  - 调用 generateExcelTemplateWithData(userId, 10)
  - 验证返回的字节数组不为空
  - 验证生成的Excel包含表头和10行数据
- 添加 `testGenerateExcelTemplateWithData_WithCustomFields()` 测试方法：
  - Mock FieldConfigService 返回包含TEXT/NUMBER/DATE字段的配置
  - 调用生成方法
  - 验证自定义字段数据生成正确
- 添加 `testGenerateCsvTemplateWithData_SmallCount()` 测试方法：
  - Mock FieldConfigService 返回空配置列表
  - 调用 generateCsvTemplateWithData(userId, 10)
  - 验证返回的字节数组转换为字符串后包含正确内容
- 添加 `testGenerateCsvTemplateWithData_WithCustomFields()` 测试方法：
  - Mock FieldConfigService 返回字段配置
  - 验证CSV格式正确，自定义字段数据生成正确
- 添加 `testGenerateExcelTemplateWithData_LargeCount()` 测试方法：
  - 使用小数据量（如100条）测试大数据逻辑
  - 验证数据行数正确

**状态:** [已完成]

#### 015 依赖: 014 | 修改 TemplateServiceTest.java（边界条件测试）
**文件路径:** `src/test/java/com/traespace/filemanager/service/template/TemplateServiceTest.java`

**任务描述:** 添加边界条件和异常测试。

**验收标准:**
- 添加 `testGenerateExcelTemplateWithData_CountEqualsOne()` 测试方法：
  - 调用 count=1
  - 验证生成1行数据
- 添加 `testGenerateCsvTemplateWithData_CountEqualsMax()` 测试方法：
  - 使用小数据量模拟最大值测试
  - 验证数据生成
- Mock数据生成验证：
  - 验证调用了 MockDataGenerator 的方法
  - 验证生成的身份证号、手机号格式正确

**状态:** ✅ [已完成]

---

### 实现任务

#### 016 依赖: 014, 015 | 修改 TemplateService.java
**文件路径:** `src/main/java/com/traespace/filemanager/service/template/TemplateService.java`

**任务描述:** 在TemplateService接口中添加带数据模板方法。

**验收标准:**
- 添加方法签名：
  ```java
  byte[] generateExcelTemplateWithData(Long userId, int count);
  byte[] generateCsvTemplateWithData(Long userId, int count);
  ```
- 添加完整的 Javadoc 注释

**状态:** [已完成]

#### 017 依赖: 016 | 修改 TemplateServiceImpl.java（实现带数据Excel模板）
**文件路径:** `src/main/java/com/traespace/filemanager/service/impl/TemplateServiceImpl.java`

**任务描述:** 实现generateExcelTemplateWithData方法。

**验收标准:**
- 使用 `SXSSFWorkbook(100)` 创建流式工作簿（窗口大小100行）
- 获取用户字段配置：`fieldConfigService.getFieldConfig(userId)`
- 创建表头：序号、身份证号、手机号 + 自定义字段
- 设置表头单元格为文本格式
- 循环生成数据行：
  - 序号：递增数字（1, 2, 3...）
  - 身份证：调用 `MockDataGenerator.generateIdCard()`
  - 手机号：调用 `MockDataGenerator.generatePhone()`
  - 自定义字段：根据类型调用对应生成方法
    - TEXT → `generateText(64)`
    - NUMBER → `generateNumber()`
    - DATE → `generateDate()`
  - 每1000行打印进度日志
- 写入ByteArrayOutputStream并返回字节数组
- 异常处理：捕获异常并抛出 SYSTEM_ERROR
- 日志记录：开始、每1000行、完成

**状态:** [已完成]

#### 018 依赖: 016 | 修改 TemplateServiceImpl.java（实现带数据CSV模板）
**文件路径:** `src/main/java/com/traespace/filemanager/service/impl/TemplateServiceImpl.java`

**任务描述:** 实现generateCsvTemplateWithData方法。

**验收标准:**
- 获取用户字段配置
- 构建表头行：序号、身份证号、手机号 + 自定义字段
- 使用StringBuilder构建CSV内容
- 循环生成数据行：
  - 数据生成逻辑与Excel相同
  - 使用String.join连接字段值
  - 每行末尾添加换行符
  - 每1000行打印进度日志
- 转换为UTF-8字节数组返回
- 日志记录：开始、每1000行、完成

**状态:** ✅ [已完成]

---

## 阶段七：带数据模板 Controller

### 测试任务

#### 019 依赖: 016 | 扩展 TemplateControllerTest.java（带数据模板测试）
**文件路径:** `src/test/java/com/traespace/filemanager/controller/template/TemplateControllerTest.java`

**任务描述:** 在现有TemplateControllerTest中添加带数据模板接口测试。

**验收标准:**
- 添加 `testDownloadExcelTemplateWithData()` 测试方法：
  - Mock UserContext.getUserId()
  - Mock templateService.generateExcelTemplateWithData() 返回测试字节数组
  - 调用 GET /api/template/download/excelWithData?count=10
  - 验证返回状态码200
  - 验证Content-Disposition为"attachment; filename=data_template_with_data.xlsx"
- 添加 `testDownloadCsvTemplateWithData()` 测试方法：
  - Mock templateService.generateCsvTemplateWithData() 返回测试字节数组
  - 调用 GET /api/template/download/csvWithData?count=10
  - 验证返回状态码200
  - 验证Content-Disposition为"attachment; filename=data_template_with_data.csv"
- 添加 `testDownloadExcelTemplateWithData_InvalidCount()` 测试方法：
  - 调用 GET /api/template/download/excelWithData?count=0
  - 验证返回参数错误

**状态:** ✅ [已完成]

---

### 实现任务

#### 020 依赖: 019 | 修改 TemplateController.java
**文件路径:** `src/main/java/com/traespace/filemanager/controller/template/TemplateController.java`

**任务描述:** 在TemplateController中添加带数据模板下载接口。

**验收标准:**
- 添加 `downloadExcelTemplateWithData()` 方法：
  - 使用 `@GetMapping(value = "/download/excelWithData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)` 注解
  - 使用 `@Operation(summary = "下载带数据的Excel模板")` 注解
  - 参数：`@RequestParam(defaultValue = "10") @Min(1) @Max(1000000) int count`
  - 从 UserContext 获取当前用户ID
  - 调用 templateService.generateExcelTemplateWithData(userId, count)
  - 设置响应头：
    - Content-Disposition: "attachment; filename=data_template_with_data.xlsx"
    - Content-Type: APPLICATION_OCTET_STREAM
  - 返回 ResponseEntity<byte[]>
- 添加 `downloadCsvTemplateWithData()` 方法：
  - 使用 `@GetMapping(value = "/download/csvWithData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)` 注解
  - 使用 `@Operation(summary = "下载带数据的CSV模板")` 注解
  - 参数与Excel接口相同
  - 文件名为 "data_template_with_data.csv"
  - 其他逻辑与Excel接口相同
- Controller层只做参数校验和结果封装

**状态:** ✅ [已完成]

---

## 阶段八：集成测试

### 测试任务

#### 021 依赖: 全部实现任务 | 创建 IntegrationTest.java
**文件路径:** `src/test/java/com/traespace/filemanager/integration/RoleAndTemplateIntegrationTest.java`

**任务描述:** 创建端到端集成测试，验证完整功能流程。

**验收标准:**
- 测试角色管理完整流程：
  - 获取当前角色 → 更新为ADMIN → 再次获取验证变更
- 测试删除权限完整流程：
  - 创建两个用户（USER和ADMIN）
  - USER上传文件
  - USER删除自己的文件（成功）
  - USER删除ADMIN的文件（失败）
  - ADMIN删除USER的文件（成功）
- 测试带数据模板完整流程：
  - 配置自定义字段
  - 下载10条数据的Excel模板
  - 验证文件内容格式正确
  - 下载10条数据的CSV模板
  - 验证文件内容格式正确
- 使用 `@SpringBootTest` 注解
- 测试后清理数据

**状态:** ✅ [已完成]

---

## 任务执行检查清单

### 每个任务执行后的检查项

- [ ] 代码符合阿里巴巴Java开发手册
- [ ] 添加了必要的 Javadoc 注释
- [ ] 日志格式符合规范：`log.info("[模块] 操作, param={}", param)`
- [ ] 使用 `BizException(ErrorCode)` 抛出业务异常
- [ ] Controller 层只做参数校验和结果封装
- [ ] 单元测试覆盖了正常场景和异常场景
- [ ] 测试可以独立运行
- [ ] 测试执行后没有副作用（或已清理）

### 整体验收标准

#### 功能验收
- [ ] 用户可以成功切换角色
- [ ] 管理员可以删除任何用户的文件
- [ ] 普通用户只能删除自己的文件
- [ ] 带数据模板可以生成1-1000000条数据
- [ ] 所有生成的数据符合格式校验规则

#### 性能验收
- [ ] 1000条数据生成 < 2秒
- [ ] 10000条数据生成 < 15秒

#### 代码质量验收
- [ ] 单元测试覆盖率 >= 80%
- [ ] 通过 `mvn test` 执行所有测试
- [ ] 通过 `mvn checkstyle:check` 检查

---

## 附录：依赖关系图

```
阶段一：MockDataGenerator工具
├── 001 [P] MockDataGeneratorTest.java
└── 002 依赖:001 → MockDataGenerator.java

阶段二：角色管理DTO
├── 003 [P] ────────────────────┐
├── 004 [P] ────────────────────┤
├── 005 依赖:003,004 ────────────┤→ RoleUpdateRequest.java
└── 006 依赖:003,004 ────────────┘→ RoleResponse.java

阶段三：角色管理Service
├── 007 依赖:005,006 → UserServiceTest.java (扩展)
├── 008 依赖:007 → UserService.java (修改)
└── 009 依赖:008 → UserServiceImpl.java (修改)

阶段四：角色管理Controller
├── 010 依赖:005,006 → UserControllerTest.java (扩展)
└── 011 依赖:010 → UserController.java (修改)

阶段五：删除权限
├── 012 [P] → FileServiceDeleteTest.java
└── 013 依赖:012 → FileServiceImpl.java (修改)

阶段六：带数据模板Service
├── 014 依赖:002 → TemplateServiceTest.java (扩展)
├── 015 依赖:014 → TemplateServiceTest.java (扩展)
├── 016 依赖:014,015 → TemplateService.java (修改)
├── 017 依赖:016 → TemplateServiceImpl.java (修改-Excel)
└── 018 依赖:016 → TemplateServiceImpl.java (修改-CSV)

阶段七：带数据模板Controller
├── 019 依赖:016 → TemplateControllerTest.java (扩展)
└── 020 依赖:019 → TemplateController.java (修改)

阶段八：集成测试
└── 021 依赖:全部 → RoleAndTemplateIntegrationTest.java
```

---

## 开发建议

### 并行执行策略

1. **第一批并行任务（无依赖）:** 001, 003, 004, 012
2. **第二批并行任务（依赖第一批）:** 002, 005, 006, 013
3. **第三批任务:** 007 → 008 → 009
4. **第四批任务:** 010 → 011
5. **第五批任务:** 014 → 015 → 016 → 017 → 018 → 019 → 020
6. **最后任务:** 021（集成测试）

### TDD 执行铁律

1. **必须先运行测试:** 每个实现任务执行前，先运行对应的测试，确保测试失败（红色）
2. **编写实现代码:** 编写最少量的代码使测试通过（绿色）
3. **重构:** 在测试保护下重构代码
4. **再测试:** 确保所有测试仍然通过

### 预估工作量

| 阶段 | 预估时间 |
|------|----------|
| 阶段一 | 3h |
| 阶段二 | 2h |
| 阶段三 | 2h |
| 阶段四 | 1.5h |
| 阶段五 | 2h |
| 阶段六 | 6h |
| 阶段七 | 1.5h |
| 阶段八 | 2h |
| **总计** | **20h** |
