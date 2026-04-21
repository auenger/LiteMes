---
last_updated: '2026-04-21'
version: 2
features_completed: 0
---

# Project Context: LiteMes - PCB 轻量级 MES 系统

> 基于 Quarkus 3.x (Java 17) + Vue 3 的单体服务四层架构，面向 PCB 制造场景的轻量级制造执行系统。
> 支持 JVM 模式与 GraalVM Native Image 双模式部署。当前开发阶段聚焦**基础数据（Master Data）模块**。

---

## Technology Stack

| Category | Technology | Version | Notes |
|----------|-----------|---------|-------|
| 运行时框架 | Quarkus | 3.x | 云原生 Java 框架，启动快，内存占用低 |
| JDK | Java 17 | LTS | 稳定长期支持版本 |
| 构建工具 | Maven | - | 依赖管理稳定，工业项目主流 |
| ORM | MyBatis-Plus | Latest | SQL 控制力强，分页插件成熟，国内工业项目验证充分 |
| REST API | JAX-RS | - | Quarkus 原生 REST 框架 |
| 硬件通信 | Vert.x MQTT Client | - | 高性能异步 MQTT 客户端，适配工业网关 |
| 领域事件 | CDI Events | - | Quarkus 原生事件机制，解耦硬件通信与业务逻辑 |
| 定时任务 | Quarkus Scheduler | - | 内置 cron 调度，工单排程、自动结案等 |
| 日志 | JBoss Logging + SLF4J | - | Quarkus 内置结构化日志 |
| 缓存/锁 | Quarkus Redis (Lettuce) | 7.x | 分布式锁 + 信号同步 (Pub/Sub) |
| 实时通信 | Quarkus WebSocket | - | 轻量双向通信，跨节点状态推送 |
| API 文档 | SmallRye OpenAPI | - | 自动生成 OpenAPI 3.0 文档 + Swagger UI |
| 认证授权 | SmallRye JWT | - | Token 校验 + RBAC 权限 |
| 参数校验 | Hibernate Validator | - | Bean Validation (JSR 380) |
| Native 编译 | GraalVM | - | 支持 Native Image，启动 <100ms，内存极低 |
| 前端框架 | Vue 3 + Pinia | 3.x | 状态管理清晰，Vite 构建极快 |
| 前端构建 | Vite | Latest | HMR 极快 |
| 前端语言 | TypeScript | - | 类型安全 |
| 测试框架 | JUnit 5 + REST Assured | - | QuarkusTest 集成测试 |

## Directory Structure

```
LiteMes/
├── src/
│   └── main/
│       ├── java/com/litemes/
│       │   ├── web/                    # REST API 层 (JAX-RS Resource, DTO, ExceptionMapper)
│       │   ├── application/            # 应用服务层 (Service, 业务编排, 事务管理)
│       │   ├── domain/                 # 领域层 (Entity, Repository接口, DomainService, Event)
│       │   └── infrastructure/         # 基础设施层 (MyBatis Mapper, Repository实现, Config, MQTT)
│       └── resources/
│           ├── application.yml         # Quarkus 配置
│           └── mapper/                 # MyBatis XML 映射文件
├── src/test/java/com/litemes/          # 测试 (JUnit 5 + QuarkusTest)
├── frontend/                           # Vue 3 前端
│   ├── src/
│   │   ├── api/                        # Axios 接口封装
│   │   ├── assets/                     # 静态资源
│   │   ├── components/                 # 公共组件
│   │   ├── composables/                # Vue 3 组合式函数
│   │   ├── layouts/                    # 布局组件
│   │   ├── router/                     # Vue Router 路由
│   │   ├── stores/                     # Pinia 状态管理
│   │   ├── views/                      # 页面组件
│   │   └── utils/                      # 工具函数
│   ├── index.html
│   ├── vite.config.ts
│   └── package.json
├── docs/                               # 设计文档
├── features/                           # Feature workflow 文档
├── feature-workflow/                   # Feature workflow 配置
├── pom.xml                             # Maven 构建
├── CLAUDE.md                           # Claude Code 开发指南
└── project-context.md                  # 项目上下文知识库
```

## Core Domain Model

```
工单 (WorkOrder)
 └── 批次 (Lot)
      └── 过站记录 (ProcessLog)
           └── 检测结果 (InspectionResult)

设备 (Device)
 └── 产线 (ProductionLine)
      └── 工序 (ProcessStep)

用户 (User)
 └── 角色 (Role)
      └── 权限 (Permission)
```

## Critical Rules

### Must Follow

- **四层架构严格分离**：web 层仅做路由与参数校验；application 层负责 DTO-Entity 映射与事务编排；domain 层封装业务规则与仓储接口；infrastructure 层实现仓储与外部通信。不可跨层调用。
- **关键编码不可变**：物料编码、工厂编码等业务主键创建后**不可编辑**，通过 Domain 层强制约束。
- **引用数据不可删除**：已被其他实体引用的基础数据（如被工单引用的物料）**不可删除**，采用软删除（`@TableLogic`）。
- **数据权限全局过滤**：仓储层通过 MyBatis-Plus `MybatisPlusInterceptor` 实现数据隔离，禁止在业务代码中手动拼接权限过滤条件。
- **审计日志强制记录**：所有基础数据的增删改操作必须记录变更履历（操作人、变更时间），通过 `BaseEntity` + `MetaObjectHandler` 自动填充，不可跳过。
- **领域事件解耦硬件通信**：`Quarkus Scheduled Job` 接收硬件数据后通过 CDI Events 发布领域事件，业务逻辑在 Observer 中处理，禁止在通信层编写业务代码。

### Must Avoid

- **禁止在 JAX-RS Resource 中编写业务逻辑**：Resource 仅负责参数校验、调用 Application Service、返回响应。
- **禁止跨节点直接操作共享状态**：批次过站等并发操作必须通过 Redis 分布式锁保护，不可依赖内存锁。
- **禁止硬编码 PCB 工序链路**：工序流转（内层 → 压合 → 钻孔 → 电镀）应通过配置驱动，不可硬编码在代码中。
- **禁止跳过 DTO 直接暴露领域实体**：API 层必须通过 DTO 与前端交互，避免领域模型泄露。

## Code Patterns

### Naming Conventions

| 类型 | 规范 | 示例 |
|------|------|------|
| 实体类 | PascalCase | `MaterialMaster`, `EquipmentLedger` |
| REST Resource | `{Entity}Resource` | `MaterialResource` |
| 应用服务 | `{Entity}Service` | `MaterialService` |
| 仓储接口 (domain) | `{Entity}Repository` | `MaterialRepository` |
| 仓储实现 (infra) | `{Entity}RepositoryImpl` | `MaterialRepositoryImpl` |
| MyBatis Mapper | `{Entity}Mapper` | `MaterialMapper` |
| DTO | `{Entity}Dto` / `{Entity}CreateDto` / `{Entity}UpdateDto` | `MaterialCreateDto` |
| 数据库表名 | snake_case | `material_master`, `equipment_ledger` |

### API Design Pattern (JAX-RS)

```java
@Path("/api/materials")
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MaterialResource {

    @Inject MaterialService materialService;

    @GET
    public PagedResult<MaterialDto> list(@BeanParam MaterialQueryDto query) { ... }

    @GET
    @Path("/{id}")
    public MaterialDto getById(@PathParam("id") Long id) { ... }

    @POST
    public Response create(@Valid @NotNull MaterialCreateDto dto) {
        Long id = materialService.create(dto);
        return Response.status(201).entity(id).build();
    }

    @PUT
    @Path("/{id}")
    public void update(@PathParam("id") Long id, @Valid @NotNull MaterialUpdateDto dto) { ... }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) { ... }
}
```

### Entity Base Classes (MyBatis-Plus)

```java
// 审计字段基类
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

// 软删除基类
@MappedSuperclass
public abstract class SoftDeleteEntity extends BaseEntity {
    @TableLogic
    private Boolean deleted;
}
```

### Data Permission Filter (MyBatis-Plus)

```java
// MybatisPlusInterceptor — 数据权限隔离
@ApplicationScoped
public class MyBatisPlusConfig {
    @Produces
    @ApplicationScoped
    public MybatisPlusInterceptor mybatisPlusInterceptor(CurrentUser currentUser) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        // 数据权限过滤
        interceptor.addInnerInterceptor(new DataScopeInnerInterceptor(
            currentUser.getAccessibleLineIds()
        ));
        return interceptor;
    }
}
```

### Error Handling

```java
// 领域层抛出业务异常
throw new BusinessException("MATERIAL_CODE_DUPLICATE", "物料编码已存在");

// Application 层统一包装
// web 层通过 ExceptionMapper 全局捕获，返回标准化错误响应
@Provider
@Priority(1)
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception e) {
        if (e instanceof BusinessException be) {
            return Response.status(400)
                .entity(new ErrorResponse(be.getCode(), be.getMessage()))
                .build();
        }
        // 未知异常 → 500 + 结构化日志
        log.error("Unexpected error", e);
        return Response.status(500)
            .entity(new ErrorResponse("INTERNAL_ERROR", "服务器内部错误"))
            .build();
    }
}
```

### Import/Export Pattern

```java
// Excel 导入导出统一通过 Application Service 处理
@POST
@Path("/import")
@Consumes(MediaType.MULTIPART_FORM_DATA)
public ImportResult importData(@MultipartForm FileUpload file) { ... }

@GET
@Path("/export")
@Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
public Response export(@BeanParam MaterialQueryDto query) { ... }
```

## Testing Patterns

### Unit Tests

- 测试文件位置：`src/test/java/com/litemes/domain/`, `src/test/java/com/litemes/application/`
- 命名：`{ClassName}{MethodName}{Scenario}{ExpectedResult}Test`
- 重点测试：领域规则（编码唯一性、引用完整性、状态流转约束）
- 使用 `@QuarkusTest` 进行集成测试

### Integration Tests

- 位置：`src/test/java/com/litemes/`
- 框架：QuarkusTest + REST Assured
- 重点验证：仓储实现、全局拦截器、并发锁机制、API 端到端
- 数据库：使用 Quarkus Test Profile 配置测试数据源

```java
@QuarkusTest
class MaterialResourceTest {
    @Test
    void shouldReturn401WhenNoToken() {
        given()
            .when().get("/api/materials")
            .then().statusCode(401);
    }
}
```

## Master Data Module (Phase 1)

### 1. 企业架构与组织管理 (Enterprise Management)

| 子功能 | 核心实体 | 关键规则 |
|--------|---------|---------|
| 公司管理 | `Company` | 编码唯一，支持启用/禁用 |
| 工厂管理 | `Factory` | 隶属公司，编码唯一 |
| 部门管理 | `Department` | 隶属工厂，支持多级树形结构 |
| 部门用户关系 | `DepartmentUser` | 用户可归属多个部门 |
| 班制班次管理 | `ShiftSchedule` / `Shift` | 班制含多个班次，班次支持跨天（如 22:00-06:00） |
| 企业通用功能 | — | 通用下拉、级联选择器接口 |

### 2. 物料主数据 (Material Master Data)

| 子功能 | 核心实体 | 关键规则 |
|--------|---------|---------|
| 计量单位管理 | `Uom` | 编码与名称唯一，支持计算精度设置 |
| 单位换算比例 | `UomConversion` | 原单位↔目标单位换算率，支持多级换算 |
| 物料分类 | `MaterialCategory` | 多级分类树形结构 |
| 物料基本信息 | `MaterialMaster` | 编码/名称/规格/分类/版本，版本管理支持 A.1→A.2 更迭 |
| 免检清单 | `InspectionExemption` | 供应商+物料+有效时间范围定义免检规则 |

### 3. 设备主数据 (Equipment Master Data)

| 子功能 | 核心实体 | 关键规则 |
|--------|---------|---------|
| 设备分类与型号 | `EquipmentType` / `EquipmentModel` | 类型（钻孔/测试/压合等）→型号，规范技术参数 |
| 设备台账 | `EquipmentLedger` | 唯一编码，运行状态（运行/故障/停机/维修），管理状态（使用中/闲置/报废） |

### 4. 供应链主数据 (Supply Chain Master Data)

| 子功能 | 核心实体 | 关键规则 |
|--------|---------|---------|
| 客户管理 | `Customer` | 基础信息 + 物料关联，用于成品出库确认 |
| 供应商管理 | `Supplier` | 基础信息 + 联系人 + 地址 + 物料关联，用于采购/入库/质量追溯 |

### 5. 数据权限控制 (Data Authorization)

| 子功能 | 核心实体 | 关键规则 |
|--------|---------|---------|
| 数据权限组 | `DataPermissionGroup` | 将工厂+工作中心+工序打包为权限集合 |
| 权限绑定 | `UserPermissionGroup` | 权限组授予用户/角色，实现工序级数据隔离 |

### Common Requirements (All Sub-modules)

- **CRUD**：标准增删改查，关键编码创建后不可编辑，被引用数据不可删除
- **Import/Export**：Excel 批量导入导出
- **Audit Log**：全量变更履历（创建人、修改人、变更时间、变更内容）

## Non-functional Requirements

- **响应速度**：基础数据查询接口响应 ≤ 200ms（单体服务，P99）
- **批量操作**：所有基础数据支持 Excel 导入导出，单次导入上限 5000 行
- **可扩展性**：四层架构确保 PCB 业务流程变更时领域模型可快速适配，不修改基础设施层
- **并发安全**：批次过站等关键操作通过 Redis 分布式锁保护，不支持内存锁
- **数据完整性**：基础数据被业务单据引用后禁止物理删除，采用软删除（`@TableLogic`）
- **Native Image**：项目必须保持 GraalVM 兼容，支持编译为 Native 可执行文件

## Architecture Notes

### Hardware Communication (Edge Middleware)

```
PLC / 机台设备
     │
     ▼ (Modbus TCP / MQTT)
┌──────────────────────────┐
│  Quarkus Scheduled Job   │  ← 定时任务，独立于 API 生命周期
│  (硬件数据轮询/监听)      │
└──────────┬───────────────┘
           │ CDI Domain Events
           ▼
┌──────────────────────────┐
│  Domain Event Observers  │  ← 业务逻辑，不污染通信层
└──────────────────────────┘
```

### Multi-point Deployment

| 问题 | 方案 |
|------|------|
| 批次过站并发冲突 | Redis 分布式锁 |
| 跨节点信号同步 | Redis Pub/Sub |
| 会话状态共享 | JWT 无状态认证 |

### Data Volume Strategy

- **读写分离**：报表查询与实时过站业务分开
- **按月分表**：`ProcessLog` 等大表按时间分表
- **冷热分离**：历史数据归档，保持在线表轻量

## Recent Changes

| Date | Feature | Impact |
|------|---------|--------|
| 2026-04-21 | 技术栈迁移 | .NET Core → Quarkus (Java 17)，全面重构项目骨架设计 |
| 2026-04-20 | 项目初始化 | 创建 README 与项目架构设计 |

## Update Log

- 2026-04-21: 技术栈从 .NET Core 迁移至 Quarkus (Java 17)，更新全部代码模式、命名规范、目录结构
- 2026-04-20: Initial project context created, Master Data module requirements defined
