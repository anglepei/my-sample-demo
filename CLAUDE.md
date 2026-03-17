Markdown
# 项目规范

## 技术栈
Java 17 / Spring Boot 3.2 / MyBatis-Plus / MySQL 8.0 / Redis 

## 构建与测试
- 构建：mvn clean package -DskipTests
- 单元测试：mvn test
- 集成测试：mvn verify -P integration-test
- 代码检查：mvn checkstyle:check

## 编码规范
- 遵循阿里巴巴Java开发手册
- Controller只做参数校验和结果封装
- Service层接口+实现分离
- 业务异常使用 BizException(ErrorCode)
- 日志格式：log.info("[模块] 操作, param={}", param)

## 禁止事项
- 不使用 System.out.println
- 不使用 java.util.Date
- 不手动拼接SQL
- 不在Controller写业务逻辑

## Git规范
- commit message遵循Conventional Commits
- 格式：type(scope): description