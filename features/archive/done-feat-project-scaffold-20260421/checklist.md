# Checklist: feat-project-scaffold

## 完成检查清单

### 项目结构
- [ ] Maven 项目可编译 (`mvn compile` 无错误)
- [ ] 四层包结构完整 (web / application / domain / infrastructure)
- [ ] pom.xml 包含所有核心 Quarkus 扩展
- [ ] application.yml 数据源配置正确

### 基础设施
- [ ] BaseEntity / SoftDeleteEntity 基类已实现
- [ ] MyBatis-Plus 审计字段自动填充生效
- [ ] 全局异常处理 (ExceptionMapper) 覆盖所有异常类型
- [ ] SmallRye JWT 认证拦截器正常工作
- [ ] Swagger UI 可访问 (/q/swagger-ui)
- [ ] OpenAPI JSON 可访问 (/q/openapi)

### 测试
- [ ] 示例 CRUD 集成测试通过 (`mvn test`)
- [ ] QuarkusTest + REST Assured 测试框架可用
- [ ] 未带 Token 请求返回 401
- [ ] 全局异常处理返回结构化 JSON

### 前端
- [ ] Vue 3 项目可启动 (`npm run dev`)
- [ ] Pinia store 可正常读写
- [ ] Vue Router 路由跳转正常
- [ ] Axios 拦截器正常工作

### 日志与监控
- [ ] 结构化日志输出正常 (JSON 格式)
- [ ] /q/health 返回 UP 状态
- [ ] 数据库连接信息出现在健康检查中

### Native Image
- [ ] `mvn package -Pnative` 编译成功
- [ ] Native 可执行文件启动时间 < 100ms

### 代码质量
- [ ] 无编译警告
- [ ] 代码风格一致（Java 命名规范）
- [ ] 关键类有简要 Javadoc

### 文档
- [ ] spec.md 技术方案已完善
- [ ] README.md 快速开始章节可执行

### 提交准备
- [ ] 变更已暂存
- [ ] 准备好提交信息
