# Tasks: feat-project-scaffold

## 任务分解

### 1. Maven 项目初始化
- [x] 创建 Quarkus Maven 项目 (groupId: com.litemes, artifactId: litemes)
- [x] 配置 pom.xml — Quarkus BOM、核心依赖、编译插件、Native Image profile
- [x] 配置 application.yml — 数据源、日志级别、Quarkus 基础设置
- [x] 创建四层包结构：web / application / domain / infrastructure
- [x] 配置 Maven Wrapper (mvnw) — 场景 1

### 2. 领域基类与基础设施
- [x] 实现 BaseEntity 抽象类（createdBy, createdAt, updatedBy, updatedAt）
- [x] 实现 SoftDeleteEntity 抽象类（继承 BaseEntity，添加 @TableLogic deleted）
- [x] 实现 MyBatis-Plus MetaObjectHandler（审计字段自动填充）— 场景 2
- [x] 实现 MybatisPlusInterceptor（分页插件 + 软删除拦截器注册）— 场景 2
- [x] 实现 DataScopeInnerInterceptor（数据权限全局过滤器骨架）— 场景 2

### 3. 全局异常处理
- [x] 定义 BusinessException (code, message)
- [x] 定义统一响应体 R<T>（code, message, data, timestamp）
- [x] 实现 GlobalExceptionMapper（400/401/403/422/500 分类处理）— 场景 4
- [x] 实现 ValidationExceptionMapper（参数校验错误 → 422）— 场景 4

### 4. JWT 认证授权
- [x] 配置 SmallRye JWT（公钥/密钥、issuer、token lifespan）
- [x] 实现 CurrentUserProducer（从 JWT Claims 注入当前用户信息）
- [ ] 定义 @RequiresAuth / @RequiresRole 注解（如需自定义）
- [x] 配置 /q/health 和 OpenAPI 端点为公开路径 — 场景 3

### 5. API 文档与配置
- [x] 配置 SmallRye OpenAPI（标题、版本、描述、服务器 URL）
- [x] 定制 Swagger UI（/q/swagger-ui 路径）— 场景 5
- [x] 配置 CORS（允许前端开发联调）— 场景 5

### 6. 日志与监控
- [x] 配置 JBoss Logging + SLF4J 日志格式（结构化 JSON）
- [x] 配置 Quarkus Health（数据库健康检查）— 场景 7
- [x] 配置 Quarkus Metrics 端点（/q/metrics）— 场景 7

### 7. Redis 与 WebSocket 基础
- [x] 配置 Quarkus Redis Client（application.yml 连接参数）
- [x] 实现 RedisDistributedLock 工具类骨架
- [x] 配置 WebSocket 端点骨架（/ws）

### 8. 前端项目初始化
- [x] 创建 Vue 3 + Vite + TypeScript 项目 (frontend/)
- [x] 安装并配置 Pinia 状态管理 — 场景 6
- [x] 配置 Vue Router（基础路由表 + Layout 组件）— 场景 6
- [x] 封装 Axios 实例（baseURL、Token 拦截器、错误处理拦截器）— 场景 6
- [ ] 创建基础布局组件（侧边栏 + 顶栏 + 内容区）

### 9. 示例 CRUD（验证骨架可用性）
- [x] 定义示例实体 ExampleEntity（继承 SoftDeleteEntity）
- [x] 实现 ExampleMapper（MyBatis-Plus BaseMapper）
- [x] 实现 ExampleRepository 接口 + ExampleRepositoryImpl
- [x] 实现 ExampleService（应用层 CRUD 编排）
- [x] 实现 ExampleResource（JAX-RS REST 端点）— 场景 5
- [ ] 编写 ExampleResource 集成测试（QuarkusTest + REST Assured）

### 10. Native Image 验证
- [ ] 配置 GraalVM 替换配置（反射注册、资源包含）
- [ ] 执行 mvn package -Pnative 并验证可执行文件生成 — 场景 8
- [ ] 验证 Native 模式启动 < 100ms — 场景 8

---

## 进度记录

| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-21 | Spec enriched | Value points: 4, Scenarios: 8, Tasks: 33, Tech stack: Quarkus (Java 17) |
| 2026-04-21 | Implementation complete | Backend: 17 Java files, Frontend: Vue 3 + Pinia + Router + Axios |
