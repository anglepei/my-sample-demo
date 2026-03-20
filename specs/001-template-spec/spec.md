# 角色管理与模板增强功能规格文档

## 1. 需求概述

本次更新包含三个主要功能：
1. 角色配置页面 - 支持用户分配普通用户或管理员角色
2. 管理员删除权限 - 管理员可删除任何用户的文件
3. 带数据模板下载 - 支持下载带随机数据的Excel/CSV模板

---

## 2. 功能需求详情

### 2.1 角色配置页面

#### 2.1.1 功能描述
允许用户给自己分配角色（普通用户/管理员），用于测试和演示目的。

#### 2.1.2 页面元素

| 元素 | 类型 | 说明 |
|------|------|------|
| 角色选择 | 单选框 | 普通用户 / 管理员 |
| 保存按钮 | 按钮 | 保存角色配置 |
| 当前角色提示 | 文本 | 显示当前用户角色 |

#### 2.1.3 权限说明

| 角色 | 权限 |
|------|------|
| 普通用户 | 上传文件、下载文件、查看详情、查看列表、删除自己的文件 |
| 管理员 | 普通用户权限 + 删除任何用户的文件 |

#### 2.1.4 接口设计

**获取当前用户角色**
```
GET /api/user/role
Response: {
  "code": 0,
  "data": {
    "role": "USER",  // USER or ADMIN
    "roleDesc": "普通用户"
  }
}
```

**更新用户角色**
```
POST /api/user/role
Request: {
  "role": "ADMIN"  // USER or ADMIN
}
Response: {
  "code": 0,
  "message": "角色更新成功"
}
```

---

### 2.2 管理员删除文件权限

#### 2.2.1 功能描述
管理员可以删除任何用户的文件，而普通用户只能删除自己上传的文件。

#### 2.2.2 业务规则

| 场景 | 行为 |
|------|------|
| 普通用户删除自己的文件 | 允许 |
| 普通用户删除他人的文件 | 拒绝，返回错误码 1004 (PERMISSION_DENIED) |
| 管理员删除任何文件 | 允许 |
| 文件不存在 | 返回错误码 3004 (FILE_NOT_FOUND) |

#### 2.2.3 接口变更

**删除文件接口（已有，需修改）**
```
DELETE /api/file/{fileId}

权限控制逻辑：
- 验证文件是否存在
- 获取当前用户角色
- 如果是管理员，允许删除
- 如果是普通用户且文件所有者是自己，允许删除
- 否则返回 PERMISSION_DENIED

错误响应示例：
{
  "code": 1004,
  "message": "无权限"
}
```

---

### 2.3 带数据模板下载

#### 2.3.1 功能描述
在现有模板下载基础上，新增两个选项：
- 下载带数据的Excel模板
- 下载带数据的CSV模板

用户可指定生成数据的条数，系统自动生成符合格式要求的随机数据。

#### 2.3.2 数据生成规则

| 固定字段 | 生成规则 |
|----------|----------|
| 序号 | 递增数字（1, 2, 3...） |
| 身份证号 | 随机18位身份证号（符合格式校验） |
| 手机号 | 随机11位手机号（1开头，符合格式校验） |

| 自定义字段类型 | 生成规则 |
|----------------|----------|
| TEXT | 随机中文文本（1-64字符） |
| NUMBER | 随机整数（1-99999） |
| DATE | 随机日期（最近1年内，YYYY-MM-DD格式） |

#### 2.3.3 界面设计

**模板下载区域**

| 选项 | 说明 |
|------|------|
| 下载Excel模板 | 下载空Excel模板（已有） |
| 下载CSV模板 | 下载空CSV模板（已有） |
| 下载带数据的Excel模板 | 新增，弹窗输入数据条数 |
| 下载带数据的CSV模板 | 新增，弹窗输入数据条数 |

**数据条数输入弹窗**

| 元素 | 类型 | 说明 |
|------|------|------|
| 数据条数 | 输入框 | 范围：1-1000000 |
| 确认按钮 | 按钮 | 生成并下载模板 |
| 取消按钮 | 按钮 | 关闭弹窗 |

#### 2.3.4 接口设计

**下载带数据的Excel模板**
```
GET /api/template/download/excelWithData
Query Params:
  - count: 数据条数 (1-1000000)

Response: application/octet-stream
  文件名: data_template_with_data.xlsx
```

**下载带数据的CSV模板**
```
GET /api/template/download/csvWithData
Query Params:
  - count: 数据条数 (1-1000000)

Response: application/octet-stream
  文件名: data_template_with_data.csv
```

**错误处理**
```
参数校验失败：
{
  "code": 1001,
  "message": "参数错误"
}

数据条数超出范围：
{
  "code": 1001,
  "message": "数据条数必须在1-1000000之间"
}
```

---

## 3. 数据模型变更

### 3.1 无需变更的表结构

本次需求不涉及数据库表结构变更，使用现有表结构：
- `t_user` 表已包含 `role` 字段
- 现有文件记录表、数据明细表无需修改

---

## 4. 业务流程

### 4.1 角色配置流程

```
1. 用户进入角色配置页面
2. 查看当前角色
3. 选择新角色（普通用户/管理员）
4. 点击保存
5. 系统更新用户角色
6. 提示"角色更新成功"
7. 下次请求生效（需重新登录或Token刷新）
```

### 4.2 管理员删除文件流程

```
1. 管理员在文件列表选择要删除的文件
2. 点击删除按钮
3. 前端确认提示
4. 调用删除接口
5. 后端校验：
   a. 文件是否存在
   b. 当前用户是否为管理员
6. 校验通过，执行删除
7. 记录操作日志
8. 返回成功
```

### 4.3 带数据模板下载流程

```
1. 用户点击"下载带数据的Excel/CSV模板"
2. 弹出输入框，提示输入数据条数
3. 用户输入条数（1-1000000）
4. 点击确认
5. 后端校验参数
6. 获取用户字段配置
7. 生成随机数据：
   - 序号：1, 2, 3...
   - 身份证：随机18位
   - 手机号：随机11位
   - 自定义字段：按类型生成
8. 生成Excel/CSV文件
9. 返回文件流
10. 浏览器下载文件
```

---

## 5. 随机数据生成规范

### 5.1 身份证号生成规则

```java
// 18位身份证号生成
- 前6位：随机行政区代码（如110101）
- 中间8位：出生日期（YYYYMMDD）
- 后3位：顺序码（随机数字）
- 最后1位：校验码（根据前17位计算，0-9或X）
```

### 5.2 手机号生成规则

```java
// 11位手机号生成
- 第1位：固定为1
- 第2位：随机 3-9
- 第3-11位：随机数字
```

### 5.3 中文文本生成规则

```java
// TEXT类型字段
- 随机生成1-64个中文字符
- 可使用常见姓氏、地名、词语组合
```

### 5.4 日期生成规则

```java
// DATE类型字段
- 随机生成最近365天内的日期
- 格式：YYYY-MM-DD
```

---

## 6. 技术实现要点

### 6.1 角色权限控制

使用现有的 `@RequireRole` 注解：

```java
// 删除文件接口需要管理员或文件所有者
@DeleteMapping("/{fileId}")
public Result<Void> deleteFile(@PathVariable Long fileId) {
    // 在Service层判断：
    // 1. 管理员可以删除任何文件
    // 2. 普通用户只能删除自己的文件
}
```

### 6.2 随机数据生成

创建工具类 `MockDataGenerator`：

```java
public class MockDataGenerator {

    // 生成随机身份证号
    public static String generateIdCard();

    // 生成随机手机号
    public static String generatePhone();

    // 生成随机日期
    public static String generateDate();

    // 生成随机文本
    public static String generateText(int maxLength);

    // 生成随机数字
    public static String generateNumber();
}
```

### 6.3 模板生成扩展

扩展 `TemplateService` 接口：

```java
public interface TemplateService {

    // 现有方法
    byte[] generateExcelTemplate(Long userId);
    byte[] generateCsvTemplate(Long userId);

    // 新增方法
    byte[] generateExcelTemplateWithData(Long userId, int count);
    byte[] generateCsvTemplateWithData(Long userId, int count);
}
```

---

## 7. 错误码定义

| 错误码 | 描述 | 使用场景 |
|--------|------|----------|
| 1001 | 参数错误 | 数据条数不在1-1000000范围 |
| 1004 | 无权限 | 普通用户删除他人文件 |
| 3004 | 文件不存在 | 删除的文件ID不存在 |

---

## 8. 测试用例

### 8.1 角色配置测试

| 用例 | 操作 | 预期结果 |
|------|------|----------|
| 查看当前角色 | GET /api/user/role | 返回当前用户角色 |
| 更新为管理员 | POST /api/user/role {role:"ADMIN"} | 角色更新成功 |
| 更新为普通用户 | POST /api/user/role {role:"USER"} | 角色更新成功 |
| 无效角色 | POST /api/user/role {role:"INVALID"} | 参数错误 |

### 8.2 删除权限测试

| 用例 | 用户角色 | 文件所有者 | 预期结果 |
|------|----------|------------|----------|
| 删除自己的文件 | 普通用户 | 自己 | 成功 |
| 删除他人文件 | 普通用户 | 他人 | 无权限 |
| 删除他人文件 | 管理员 | 他人 | 成功 |
| 删除不存在的文件 | 管理员 | - | 文件不存在 |

### 8.3 带数据模板测试

| 用例 | 参数 | 预期结果 |
|------|------|----------|
| 下载1条数据 | count=1 | 生成1条随机数据 |
| 下载100条数据 | count=100 | 生成100条随机数据 |
| 数据条数为0 | count=0 | 参数错误 |
| 数据条数为1000001 | count=1000001 | 参数错误 |
| 数据格式校验 | - | 所有字段符合格式要求 |

---

## 9. 实施计划

### 9.1 开发任务

| ID | 任务 | 依赖 |
|----|------|------|
| 1 | 创建用户角色DTO | - |
| 2 | 实现角色查询和更新接口 | 1 |
| 3 | 创建随机数据生成工具类 | - |
| 4 | 扩展TemplateService接口 | 3 |
| 5 | 实现带数据模板生成 | 4 |
| 6 | 修改删除文件权限逻辑 | - |
| 7 | 编写单元测试 | 1-6 |
| 8 | 编写集成测试 | 1-6 |

### 9.2 文件清单

**新增文件**
```
src/main/java/com/traespace/filemanager/dto/request/user/RoleUpdateRequest.java
src/main/java/com/traespace/filemanager/dto/response/user/RoleResponse.java
src/main/java/com/traespace/filemanager/util/MockDataGenerator.java
src/main/java/com/traespace/filemanager/controller/user/UserRoleController.java
```

**修改文件**
```
src/main/java/com/traespace/filemanager/service/user/UserService.java
src/main/java/com/traespace/filemanager/service/impl/UserServiceImpl.java
src/main/java/com/traespace/filemanager/service/template/TemplateService.java
src/main/java/com/traespace/filemanager/service/impl/TemplateServiceImpl.java
src/main/java/com/traespace/filemanager/service/impl/FileServiceImpl.java
src/main/java/com/traespace/filemanager/controller/template/TemplateController.java
```

---

## 10. 验收标准

1. 用户可以成功切换角色，刷新后生效
2. 管理员可以删除任何用户的文件
3. 普通用户只能删除自己的文件
4. 带数据模板可以生成1-1000000条随机数据
5. 所有生成的数据符合格式校验规则
6. 下载的文件表头顺序与用户配置一致
7. 操作日志正确记录删除操作
