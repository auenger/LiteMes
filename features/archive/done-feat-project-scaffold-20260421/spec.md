# Feature: feat-project-scaffold 项目工程骨架

## 基本信息
- **ID**: feat-project-scaffold
- **名称**: 项目工程骨架
- **优先级**: 100
- **规模**: M
- **依赖**: 无
- **父需求**: 无
- **子需求**: 无
- **创建时间**: 2026-04-20
- **完成时间**: 2026-04-21

## Merge Record
- **Merged branch:** feature/feat-project-scaffold
- **Merge commit:** 876ba74
- **Archive tag:** feat-project-scaffold-20260421
- **Conflicts:** None
- **Verification:** PASS (7/7 tests, 8 scenarios validated)
- **Stats:** 57 files changed, 4571 insertions, 1 commit

## 需求描述
搭建 Quarkus 3.x (Java 17) 四层架构单体服务骨架，建立共享基础设施，为所有后续业务模块提供统一的技术底座。支持 JVM 模式与 GraalVM Native Image 双模式部署。

包含：
- Maven 单模块四层包结构（web / application / domain / infrastructure）
- MyBatis-Plus ORM 配置与数据库上下文
- 全局异常处理（JAX-RS ExceptionMapper）
- SmallRye JWT 认证授权配置
- 审计字段基类（创建人、修改人、变更时间）
- 软删除 MyBatis-Plus 全局拦截器
- SmallRye OpenAPI / Swagger UI 文档配置
- JBoss Logging + SLF4J 结构化日志配置
- Quarkus Redis 配置（分布式锁 + Pub/Sub）
- Quarkus WebSocket 基础配置
- Vue 3 前端项目初始化（Pinia + Vite + Router + Axios）

## 用户价值点
1. **统一架构规范** — 四层包结构清晰分层（web → application → domain ← infrastructure），后续模块直接在对应层扩展
2. **开箱即用的基础设施** — JWT 认证、日志、异常处理、数据权限拦截器一次配好，业务开发无需重复搭建
3. **前后端一体化模板** — 后端 Quarkus + 前端 Vue 3 项目结构就绪，`mvn quarkus:dev` + `npm run dev` 即可联调
4. **Native Image 就绪** — 项目配置 GraalVM 兼容，支持一键编译为 Native 可执行文件（启动 <100ms）

## 上下文分析
### 需要参考的现有代码
- 无（首个 Feature）
- `pom.xml` — Maven 依赖管理与 Quarkus BOM
- `src/main/resources/application.yml` — Quarkus 配置入口

### 相关文档
- README.md — 架构概览、技术栈、项目结构、数据量规划
- feature-workflow/config.yaml — 技术栈 java-17-quarkus、测试框架 junit5

### 相关需求
- feat-enterprise-org (企业管理) — 后续首个依赖此骨架的业务模块
- feat-material-master (物料主数据) — 使用 MyBatis-Plus 仓储层模式
- feat-data-permission (数据权限) — 使用全局查询拦截器

## 技术方案

### 后端四层包结构
```
src/main/java/com/litemes/
├── web/                          # REST API 层 (JAX-RS Resource)
│   ├── dto/                      # 请求/响应 DTO
│   ├── mapper/                   # DTO ↔ 领域模型转换 (MapStruct)
│   └── exception/                # ExceptionMapper 全局异常处理
├── application/                  # 应用服务层
│   └── service/                  # 业务编排、事务管理
├── domain/                       # 领域层
│   ├── entity/                   # 聚合根、实体基类
│   ├── repository/               # 仓储接口
│   ├── event/                    # 领域事件 (CDI Events)
│   └── service/                  # 领域服务
└── infrastructure/               # 基础设施层
    ├── persistence/              # MyBatis-Plus 仓储实现
    │   ├── mapper/               # MyBatis Mapper 接口
    │   └── converter/            # 数据库 ↔ 领域模型转换
    ├── config/                   # Quarkus 配置类
    │   ├── MyBatisPlusConfig     # 分页插件、全局拦截器
    │   ├── JwtConfig             # JWT 生产/验证
    │   ├── RedisConfig           # Redis 分布式锁
    │   └── OpenApiConfig         # Swagger UI 定制
    └── security/                 # 认证/授权过滤器
```

### 核心依赖 (pom.xml)
```xml
<!-- Quarkus BOM -->
<dependency>io.quarkus:quarkus-bom</dependency>
<!-- ORM -->
<dependency>io.quarkus:quarkus-mybatis-plus</dependency>
<!-- 认证 -->
<dependency>io.quarkus:quarkus-smallrye-jwt</dependency>
<!-- API 文档 -->
<dependency>io.quarkus:quarkus-smallrye-openapi</dependency>
<!-- 缓存 -->
<dependency>io.quarkus:quarkus-redis-client</dependency>
<!-- 实时通信 -->
<dependency>io.quarkus:quarkus-websockets</dependency>
<!-- 定时任务 -->
<dependency>io.quarkus:quarkus-scheduler</dependency>
<!-- 验证 -->
<dependency>io.quarkus:quarkus-hibernate-validator</dependency>
<!-- JSON -->
<dependency>io.quarkus:quarkus-jackson</dependency>
<!-- 测试 -->
<dependency>io.quarkus:quarkus-junit5</dependency>
<dependency>io.rest-assured:rest-assured</dependency>
```

### 实体基类设计
```java
@MappedSuperclass
public abstract class BaseEntity {
    @TableField(fill = FieldFill.INSERT)
    private String createdBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}

@MappedSuperclass
public abstract class SoftDeleteEntity extends BaseEntity {
    @TableLogic
    private Boolean deleted;
}
```

### 全局异常处理
```java
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {
    // 业务异常 → 400
    // 认证异常 → 401
    // 权限异常 → 403
    // 参数校验 → 422
    // 未知异常 → 500 + 结构化日志
}
```

### 前端项目结构
```
frontend/
├── src/
│   ├── api/          # Axios 封装 + 接口模块
│   ├── assets/       # 静态资源
│   ├── components/   # 公共组件
│   ├── composables/  # Vue 3 组合式函数
│   ├── layouts/      # 布局组件
│   ├── router/       # Vue Router 路由配置
│   ├── stores/       # Pinia 状态管理
│   ├── views/        # 页面组件
│   └── utils/        # 工具函数
├── public/
├── index.html
├── vite.config.ts
└── package.json
```

## 验收标准 (Acceptance Criteria)

### 用户故事
作为开发者，我希望有一个基于 Quarkus + Vue 3 的开箱即用项目骨架，以便快速开始 PCB MES 业务功能开发。

### Gherkin 验收场景

#### 场景 1: 后端四层包结构
```gherkin
Given 一个空的 Maven 项目
When 完成骨架搭建
Then 项目包含 com.litemes.web / application / domain / infrastructure 四个包
And web 层依赖 application 层，application 层依赖 domain 层
And infrastructure 层依赖 domain 层（实现仓储接口）
And pom.xml 包含 Quarkus BOM 及所有核心依赖
```

#### 场景 2: 数据库连接与 ORM
```gherkin
Given application.yml 配置了数据源
When 执行 mvn quarkus:dev 启动应用
Then Quarkus 成功连接数据库
And MyBatis-Plus 分页插件生效
And 全局拦截器（审计字段填充、软删除过滤）已注册
```

#### 场景 3: JWT 认证
```gherkin
Given SmallRye JWT 已配置
When 发送不带 Token 的请求到受保护接口
Then 返回 401 Unauthorized
When 发送带有效 Token 的请求
Then 正常访问并从 SecurityContext 获取用户信息
```

#### 场景 4: 全局异常处理
```gherkin
Given ExceptionMapper 已注册
When 业务层抛出 BusinessException
Then 返回 HTTP 400 + 结构化错误 JSON（code, message, timestamp）
When 发生未预期异常
Then 返回 HTTP 500 + 记录 ERROR 级别日志（不暴露堆栈）
```

#### 场景 5: API 文档
```gherkin
Given SmallRye OpenAPI 已配置
When 访问 /q/swagger-ui
Then Swagger UI 正常渲染
When 访问 /q/openapi
Then 返回 OpenAPI 3.0 JSON 规范文档
```

#### 场景 6: 前端项目
```gherkin
Given Vue 3 项目已初始化
When 执行 npm run dev
Then 前端页面可正常访问
And Pinia 状态管理已配置
And Vue Router 已配置
And Axios 实例已封装（baseURL、请求/响应拦截器）
```

#### 场景 7: 健康检查
```gherkin
Given 应用已启动
When 访问 /q/health
Then 返回 UP 状态
And 包含数据库连接状态信息
```

#### 场景 8: Native Image 编译
```gherkin
Given GraalVM 已安装
When 执行 mvn package -Pnative
Then 成功生成 Native 可执行文件
And 可执行文件启动时间 < 100ms
```
