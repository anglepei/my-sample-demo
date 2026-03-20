# 包结构设计方案

## 1. 现有项目包结构

```
com.traespace.filemanager
├── annotation/              # 注解
│   ├── Log.java            # 操作日志注解
│   └── RequireRole.java    # 角色权限注解
├── aspect/                 # 切面
│   └── OperationLogAspect.java
├── config/                 # 配置类
│   ├── JacksonConfig.java
│   ├── MyBatisPlusConfig.java
│   ├── RedisConfig.java
│   ├── ScheduleConfig.java
│   ├── UserContext.java   # 用户上下文
│   └── WebConfig.java
├── controller/             # 控制器层（按模块划分）
│   ├── auth/
│   ├── field/
│   ├── file/
│   ├── log/
│   ├── statistics/
│   ├── template/
│   └── user/
├── dto/                    # 数据传输对象
│   ├── request/           # 请求DTO（按模块划分）
│   │   ├── auth/
│   │   ├── common/
│   │   └── field/
│   └── response/          # 响应DTO（按模块划分）
│       ├── auth/
│       ├── common/
│       ├── field/
│       ├── file/
│       └── statistics/
├── entity/                 # 实体类
│   ├── DataDetail.java
│   ├── FieldConfig.java
│   ├── FileRecord.java
│   ├── OperationLog.java
│   └── User.java
├── enums/                  # 枚举类
│   ├── ErrorCode.java
│   ├── FieldType.java
│   ├── FileType.java
│   ├── OperationType.java
│   └── UserRole.java
├── exception/              # 异常类
│   ├── BizException.java
│   ├── BadRequestException.java
│   ├── GlobalExceptionHandler.java
│   └── UnauthorizedException.java
├── handler/                # 类型处理器
│   └── JsonTypeHandler.java
├── interceptor/            # 拦截器
│   └── JwtInterceptor.java
├── mapper/                 # MyBatis Mapper
│   ├── DataDetailMapper.java
│   ├── FieldConfigMapper.java
│   ├── FileRecordMapper.java
│   ├── OperationLogMapper.java
│   └── UserMapper.java
├── service/                # 服务层
│   ├── auth/
│   ├── database/
│   ├── field/
│   ├── file/
│   ├── log/
│   ├── statistics/
│   ├── template/
│   └── user/
├── util/                   # 工具类
│   ├── CsvUtil.java
│   ├── ExcelUtil.java
│   ├── JsonUtil.java
│   ├── JwtUtil.java
│   ├── PasswordUtil.java
│   └── ValidationUtil.java
└── FileManagerApplication.java
```

## 2. 本次功能新增/修改的包结构

### 2.1 新增文件

```
com.traespace.filemanager
├── dto/
│   ├── request/
│   │   └── user/
│   │       └── RoleUpdateRequest.java      # [新增] 角色更新请求
│   └── response/
│       └── user/
│           └── RoleResponse.java           # [新增] 角色响应
└── util/
    └── MockDataGenerator.java             # [新增] 随机数据生成工具
```

### 2.2 修改文件

```
com.traespace.filemanager
├── controller/
│   ├── template/
│   │   └── TemplateController.java         # [修改] 新增带数据模板下载接口
│   └── user/
│       └── UserController.java             # [修改] 新增角色查询/更新接口
├── service/
│   ├── template/
│   │   ├── TemplateService.java            # [修改] 新增带数据模板方法
│   │   └── impl/
│   │       └── TemplateServiceImpl.java    # [修改] 实现带数据模板生成
│   ├── user/
│   │   ├── UserService.java                # [修改] 新增角色方法
│   │   └── impl/
│   │       └── UserServiceImpl.java        # [修改] 实现角色方法
│   └── file/
│       └── impl/
│           └── FileServiceImpl.java        # [修改] 删除权限逻辑调整
```

## 3. 详细包结构设计

### 3.1 Controller 层

#### UserController.java
| 方法 | 说明 | HTTP方法 | 路径 |
|------|------|----------|------|
| getCurrentRole | 获取当前用户角色 | GET | /api/user/role |
| updateRole | 更新用户角色 | POST | /api/user/role |
| create | 创建用户（已有） | POST | /api/user/create |
| getCurrentUser | 获取当前用户信息（已有） | GET | /api/user/current |

#### TemplateController.java
| 方法 | 说明 | HTTP方法 | 路径 |
|------|------|----------|------|
| downloadExcelTemplate | 下载Excel模板（已有） | GET | /api/template/download/excel |
| downloadCsvTemplate | 下载CSV模板（已有） | GET | /api/template/download/csv |
| downloadExcelTemplateWithData | 下载带数据Excel模板（新增） | GET | /api/template/download/excelWithData |
| downloadCsvTemplateWithData | 下载带数据CSV模板（新增） | GET | /api/template/download/csvWithData |

### 3.2 Service 层

#### UserService.java
```java
public interface UserService {
    // 现有方法
    UserDTO getCurrentUser(Long userId);
    void createUser(String username, String password, UserRole role);

    // 新增方法
    UserRoleResponse getCurrentRole(Long userId);
    void updateRole(Long userId, UserRole role);
}
```

#### TemplateService.java
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

### 3.3 DTO 层

#### RoleUpdateRequest.java
```java
package com.traespace.filemanager.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RoleUpdateRequest {
    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "^(USER|ADMIN)$", message = "角色必须是USER或ADMIN")
    private String role;

    // getter/setter
}
```

#### RoleResponse.java
```java
package com.traespace.filemanager.dto.response.user;

import com.traespace.filemanager.enums.UserRole;

public class RoleResponse {
    private UserRole role;
    private String roleDesc;

    // getter/setter
}
```

### 3.4 Util 层

#### MockDataGenerator.java
```java
package com.traespace.filemanager.util;

/**
 * 随机数据生成工具类
 * 用于生成带数据模板的测试数据
 */
public class MockDataGenerator {
    private static final int MAX_COUNT = 1_000_000;

    /**
     * 生成随机身份证号（18位，符合格式校验）
     */
    public static String generateIdCard();

    /**
     * 生成随机手机号（11位，1开头）
     */
    public static String generatePhone();

    /**
     * 生成随机日期（YYYY-MM-DD格式）
     */
    public static String generateDate();

    /**
     * 生成随机文本（中文）
     */
    public static String generateText(int maxLength);

    /**
     * 生成随机数字字符串
     */
    public static String generateNumber();
}
```

## 4. 测试包结构

```
src/test/java/com/traespace/filemanager
├── controller/
│   ├── user/
│   │   └── UserControllerTest.java        # [修改] 新增角色相关测试
│   └── template/
│       └── TemplateControllerTest.java   # [修改] 新增带数据模板测试
├── service/
│   ├── user/
│   │   └── UserServiceTest.java           # [修改] 新增角色相关测试
│   ├── template/
│   │   └── TemplateServiceTest.java       # [修改] 新增带数据模板测试
│   └── file/
│       └── FileServiceTest.java          # [修改] 新增删除权限测试
└── util/
    └── MockDataGeneratorTest.java        # [新增] 随机数据生成测试
```

## 5. 包设计原则

### 5.1 分层原则
- **Controller层**: 只负责参数校验、调用Service、封装响应结果
- **Service层**: 业务逻辑处理，事务控制
- **Mapper层**: 数据库访问

### 5.2 命名规范
- **接口**: 以功能命名，如 `UserService`
- **实现类**: 接口名 + `Impl`，如 `UserServiceImpl`
- **DTO请求**: `XxxRequest`
- **DTO响应**: `XxxResponse`
- **VO**: `XxxVO`

### 5.3 依赖原则
- Controller → Service → Mapper
- 同层之间不允许相互依赖
- 上层可以依赖下层，下层不能依赖上层

## 6. 包结构图

```
┌─────────────────────────────────────────────────────────────┐
│                        Controller Layer                      │
│  ┌─────────────┐  ┌─────────────┐                           │
│  │ UserController│  │TemplateController│                        │
│  │  - role     │  │  - downloadWithData│                       │
│  └──────┬──────┘  └──────┬──────┘                           │
├─────────┼──────────────────┼─────────────────────────────────┤
│         │                  │                                    │
│         ▼                  ▼                                    │
│  ┌─────────────┐  ┌─────────────┐                           │
│  │ UserService │  │TemplateService│                          │
│  │  - updateRole│  │ - generateWithData│                     │
│  └──────┬──────┘  └──────┬──────┘                           │
├─────────┼──────────────────┼─────────────────────────────────┤
│         │                  │                                    │
│         ▼                  ▼                                    │
│  ┌─────────────┐  ┌─────────────┐  ┌──────────────┐        │
│  │ UserMapper  │  │FieldConfigMapper│ │MockDataGenerator│    │
│  └─────────────┘  └─────────────┘  └──────────────┘        │
└─────────────────────────────────────────────────────────────┘
```
