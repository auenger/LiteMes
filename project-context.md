---
last_updated: '2026-04-22'
version: 3
features_completed: 24
---

# Project Context: LiteMes - PCB 轻量级 MES 系统

> 基于 Quarkus 3.21 (Java 17) + Vue 3 + Element Plus 的单体服务四层架构，面向 PCB 制造场景的轻量级制造执行系统。
> 支持 JVM 模式与 GraalVM Native Image 双模式部署。
> **基础数据模块（24 个 Feature）已全部完成**，下一步进入生产执行模块开发。

---

## Technology Stack

| Category | Technology | Version | Notes |
|----------|-----------|---------|-------|
| 运行时框架 | Quarkus | 3.21.4 | 云原生 Java 框架，启动快，内存占用低 |
| JDK | Java 17 | LTS | 稳定长期支持版本 |
| 构建工具 | Maven | — | 依赖管理稳定，工业项目主流 |
| ORM | MyBatis-Plus (Quarkiverse) | 2.4.2 | SQL 控制力强，分页插件成熟，国内工业项目验证充分 |
| REST API | JAX-RS | — | Quarkus 原生 REST 框架 |
| 硬件通信 | Vert.x MQTT Client | — | 高性能异步 MQTT 客户端，适配工业网关 |
| 领域事件 | CDI Events | — | Quarkus 原生事件机制，解耦硬件通信与业务逻辑 |
| 定时任务 | Quarkus Scheduler | — | 内置 cron 调度，工单排程、自动结案等 |
| 日志 | JBoss Logging + SLF4J | — | Quarkus 内置结构化日志 |
| 缓存/锁 | Quarkus Redis (Lettuce) | — | 分布式锁 + 信号同步 (Pub/Sub) |
| 实时通信 | Quarkus WebSocket | — | 轻量双向通信，跨节点状态推送 |
| API 文档 | SmallRye OpenAPI | — | 自动生成 OpenAPI 3.0 文档 + Swagger UI |
| 认证授权 | SmallRye JWT | — | Token 校验 + RBAC 权限 |
| 参数校验 | Hibernate Validator | — | Bean Validation (JSR 380) |
| Native 编译 | GraalVM | — | 支持 Native Image，启动 <100ms，内存极低 |
| 前端框架 | Vue 3 + Pinia | 3.5.32 / 3.0.4 | 状态管理清晰，组合式 API |
| UI 组件库 | Element Plus | 2.13.7 | 企业级 Vue 3 组件库 |
| 前端构建 | Vite | 8.0.9 | HMR 极快 |
| 前端语言 | TypeScript | — | 类型安全 |
| HTTP 客户端 | Axios | — | 前端 API 请求封装，JWT 拦截器 |
| 测试框架 | JUnit 5 + REST Assured | — | QuarkusTest 集成测试 |

## Directory Structure

```
LiteMes/
├── src/
│   └── main/
│       ├── java/com/litemes/
│       │   ├── web/                    # REST API 层 (25 Resource, DTO, ExceptionMapper)
│       │   ├── application/            # 应用服务层 (25 Service, 业务编排, 事务管理)
│       │   ├── domain/                 # 领域层 (30 Entity, Repository接口, DomainService, Event)
│       │   └── infrastructure/         # 基础设施层 (Mapper, RepositoryImpl, Config, MQTT)
│       └── resources/
│           ├── application.yml         # Quarkus 配置
│           └── mapper/                 # MyBatis XML 映射文件
├── src/test/java/com/litemes/          # 测试 (JUnit 5 + REST Assured)
├── frontend/                           # Vue 3 前端
│   ├── src/
│   │   ├── api/                        # Axios 接口封装 (26 模块)
│   │   ├── assets/                     # 静态资源
│   │   ├── components/                 # 公共组件 (CascadingSelector, AuditLogDialog, TableSettingsPanel 等)
│   │   ├── composables/                # Vue 3 组合式函数
│   │   ├── layouts/                    # 布局组件 (MainLayout)
│   │   ├── router/                     # Vue Router 路由 + 认证守卫
│   │   ├── stores/                     # Pinia 状态管理 (user, tabs, theme)
│   │   ├── views/                      # 页面组件 (28+ 视图)
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

## Implemented Entities (30)

### Enterprise Management
- `Company` — 公司
- `Factory` — 工厂
- `Department` — 部门（树形）
- `DepartmentUser` — 部门用户关系
- `ShiftSchedule` / `Shift` — 班制/班次
- `WorkCenter` — 工作中心
- `Process` — 工序

### Material Master
- `Uom` — 计量单位
- `UomConversion` — 单位换算
- `MaterialCategory` — 物料分类（树形）
- `MaterialMaster` — 物料主数据
- `MaterialVersion` — 物料版本
- `InspectionExemption` — 免检清单

### Equipment Master
- `EquipmentType` — 设备类型
- `EquipmentModel` — 设备型号
- `EquipmentLedger` — 设备台账

### Supply Chain
- `Customer` / `CustomerMaterial` — 客户及关联物料
- `Supplier` / `SupplierMaterial` — 供应商及关联物料

### Data Permissions
- `DataPermissionGroup` — 数据权限组
- `UserDataPermission` — 用户数据权限
- `UserDataPermissionFactory` / `UserDataPermissionProcess` / `UserDataPermissionWorkCenter` — 权限明细

## REST API Endpoints (25 Resources)

| Resource | 路径 | 操作 |
|----------|------|------|
| AuthResource | /api/auth | 登录认证 |
| DropdownResource | /api/dropdown | 通用下拉数据 |
| CompanyResource | /api/companies | CRUD |
| FactoryResource | /api/factories | CRUD |
| DepartmentResource | /api/departments | CRUD + 树形 |
| DepartmentUserResource | /api/department-users | CRUD |
| ShiftScheduleResource | /api/shift-schedules | CRUD |
| ShiftResource | /api/shifts | CRUD |
| WorkCenterResource | /api/work-centers | CRUD |
| ProcessResource | /api/processes | CRUD |
| MaterialResource | /api/materials | CRUD |
| MaterialVersionResource | /api/material-versions | CRUD |
| MaterialCategoryResource | /api/material-categories | CRUD + 树形 |
| UomResource | /api/uoms | CRUD |
| UomConversionResource | /api/uom-conversions | CRUD |
| EquipmentTypeResource | /api/equipment-types | CRUD |
| EquipmentModelResource | /api/equipment-models | CRUD |
| EquipmentLedgerResource | /api/equipment-ledgers | CRUD |
| CustomerResource | /api/customers | CRUD |
| SupplierResource | /api/suppliers | CRUD |
| InspectionExemptionResource | /api/inspection-exemptions | CRUD |
| DataPermissionGroupResource | /api/data-permission-groups | CRUD |
| UserDataPermissionResource | /api/user-data-permissions | CRUD |
| AuditLogResource | /api/audit-logs | 查询 |

## Critical Rules

### Must Follow

- **四层架构严格分离**：web 层仅做路由与参数校验；application 层负责 DTO-Entity 映射与事务编排；domain 层封装业务规则与仓储接口；infrastructure 层实现仓储与外部通信。不可跨层调用。
- **关键编码不可变**：物料编码、工厂编码等业务主键创建后**不可编辑**，通过 Domain 层强制约束。
- **引用数据不可删除**：已被其他实体引用的基础数据（如被工单引用的物料）**不可删除**，采用软删除（`@TableLogic`）。
- **数据权限全局过滤**：仓储层通过 MyBatis-Plus `MybatisPlusInterceptor` 实现数据隔离，禁止在业务代码中手动拼接权限过滤条件。
- **审计日志强制记录**：所有基础数据的增删改操作必须记录变更履历（操作人、变更时间），通过 `BaseEntity` + `AuditMetaObjectHandler` 自动填充，不可跳过。
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

### API Response Pattern

```java
// 统一响应体
public class R<T> {
    private int code;
    private String message;
    private T data;

    public static <T> R<T> ok(T data) { ... }
    public static <T> R<T> fail(String code, String message) { ... }
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
@ApplicationScoped
public class MyBatisPlusConfig {
    @Produces
    @ApplicationScoped
    public MybatisPlusInterceptor mybatisPlusInterceptor(CurrentUser currentUser) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
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
        log.error("Unexpected error", e);
        return Response.status(500)
            .entity(new ErrorResponse("INTERNAL_ERROR", "服务器内部错误"))
            .build();
    }
}
```

### Frontend Pattern (Vue 3 + Element Plus)

```typescript
// API 封装 (api/material.ts)
export const materialApi = {
  list: (params: MaterialQuery) => http.get<R<PageResult<MaterialDto>>>('/api/materials', { params }),
  getById: (id: number) => http.get<R<MaterialDto>>(`/api/materials/${id}`),
  create: (data: MaterialCreateDto) => http.post<R<number>>('/api/materials', data),
  update: (id: number, data: MaterialUpdateDto) => http.put(`/api/materials/${id}`, data),
  delete: (id: number) => http.delete(`/api/materials/${id}`),
}

// JWT 拦截器自动附加 Token
http.interceptors.request.use(config => {
  const token = useUserStore().token
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})
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

## Completed Features (24)

### Enterprise Organization (8)
| Feature | Description |
|---------|-------------|
| feat-company | 公司管理 CRUD |
| feat-factory | 工厂管理 CRUD |
| feat-department | 部门管理（树形结构） |
| feat-department-user | 部门用户关系 |
| feat-shift-schedule | 班制管理 |
| feat-work-center | 工作中心管理 |
| feat-process | 工序管理 |
| feat-enterprise-common | 通用下拉 + 级联选择器 |

### Material Master (4)
| Feature | Description |
|---------|-------------|
| feat-uom | 计量单位 + 单位换算 |
| feat-material-category | 物料分类（树形） |
| feat-material-info | 物料主数据 + 版本管理 |
| feat-inspection-exemption | 免检清单 |

### Equipment Master (3)
| Feature | Description |
|---------|-------------|
| feat-equipment-type | 设备类型管理 |
| feat-equipment-model | 设备型号管理 |
| feat-equipment-ledger | 设备台账管理 |

### Supply Chain (2)
| Feature | Description |
|---------|-------------|
| feat-customer | 客户管理 + 物料关联 |
| feat-supplier | 供应商管理 + 物料关联 |

### Data Permissions (2)
| Feature | Description |
|---------|-------------|
| feat-permission-group | 数据权限组管理 |
| feat-user-permission | 用户数据权限绑定 |

### Infrastructure & Frontend (5)
| Feature | Description |
|---------|-------------|
| feat-project-scaffold | 项目骨架（四层架构 + 统一响应 + 异常处理） |
| feat-frontend-ui-lib | Element Plus 集成 + 全局样式 |
| feat-frontend-layout | 导航布局 + Tab 管理 + 主题切换 |
| feat-cascading-selector | 级联选择器通用组件 |
| feat-auth-frontend | 登录页 + JWT 路由守卫 + Token 管理 |

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
| 2026-04-22 | feat-auth-frontend | 登录认证前端（登录页 + JWT + 路由守卫） |
| 2026-04-22 | feat-cascading-selector | 级联选择器通用组件（工厂→工作中心→工序） |
| 2026-04-22 | feat-frontend-ui-lib + layout + process-frontend | 前端全面升级：Element Plus 集成、导航布局、工序管理前端 |
| 2026-04-21 | 基础数据后端全量 | 企业架构/物料/设备/供应链/数据权限 5 大子模块后端 CRUD 完成 |
| 2026-04-21 | 技术栈迁移 | .NET Core → Quarkus (Java 17)，全面重构项目骨架设计 |
| 2026-04-20 | 项目初始化 | 创建 README 与项目架构设计 |

## Update Log

- 2026-04-22: 基础数据模块 24 个 Feature 全部完成（后端 + 前端），登录认证前端就绪
- 2026-04-21: 技术栈从 .NET Core 迁移至 Quarkus (Java 17)，基础数据后端全量开发完成
- 2026-04-20: Initial project context created, Master Data module requirements defined
