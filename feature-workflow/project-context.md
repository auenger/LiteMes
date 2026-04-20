---
last_updated: '2026-04-20'
version: 1
features_completed: 0
---

# Project Context: LiteMes - PCB 轻量级 MES 系统

> 基于 .NET Core + Vue 3 的单体服务四层架构，面向 PCB 制造场景的轻量级制造执行系统。当前开发阶段聚焦**基础数据（Master Data）模块**，为生产、质量、设备等后续业务提供标准化底座。

---

## Technology Stack

| Category | Technology | Version | Notes |
|----------|-----------|---------|-------|
| 后端框架 | .NET Core | 8.0+ | 单体服务，四层架构（WebApi / Application / Domain / Infrastructure） |
| ORM | SqlSugar | Latest | 支持分表、全局查询过滤器，国内工业项目验证 |
| 硬件通信 | MQTTnet | Latest | 工业网关数据上报 |
| 领域事件 | MediatR | Latest | 解耦硬件通信与业务逻辑 |
| 定时任务 | Quartz.NET | Latest | 工单排程、自动结案 |
| 日志 | Serilog | Latest | 结构化日志 |
| 缓存/锁 | Redis | 7.x | 分布式锁 + SignalR 背板 Pub/Sub |
| 实时通信 | SignalR | Latest | 跨节点状态推送 |
| 前端框架 | Vue 3 + Pinia | 3.x | Vite 构建，Pinia 状态管理 |
| 接口文档 | Swagger | Latest | 自动生成 API 文档 |
| 认证授权 | JWT | - | Token 校验 + RBAC 权限 |

## Directory Structure

```
LiteMes/
├── src/
│   ├── LiteMes.WebApi/            # Web API 层（Controllers, Middleware）
│   ├── LiteMes.Application/       # 应用服务层（DTO, Service, Mapping）
│   ├── LiteMes.Domain/            # 领域层（Entity, ValueObject, DomainService, Repository Interface）
│   └── LiteMes.Infrastructure/    # 基础设施层（SqlSugar Repository, MQTT, Serilog）
├── frontend/                      # Vue 3 前端
│   ├── src/
│   │   ├── views/                 # 页面
│   │   ├── stores/                # Pinia 状态管理
│   │   ├── api/                   # 接口封装
│   │   └── components/            # 公共组件
│   └── vite.config.ts
├── tests/                         # 单元测试 / 集成测试
├── docs/                          # 设计文档
└── feature-workflow/              # Feature workflow 管理
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

- **四层架构严格分离**：WebApi 层仅做路由与 JWT 校验；Application 层负责 DTO-Entity 映射与事务编排；Domain 层封装业务规则与仓储接口；Infrastructure 层实现仓储与外部通信。不可跨层调用。
- **关键编码不可变**：物料编码、工厂编码等业务主键创建后**不可编辑**，通过 Domain 层强制约束。
- **引用数据不可删除**：已被其他实体引用的基础数据（如被工单引用的物料）**不可删除**，采用软删除或状态标记。
- **数据权限全局过滤**：仓储层通过 SqlSugar 全局查询过滤器实现数据隔离，禁止在业务代码中手动拼接权限过滤条件。
- **审计日志强制记录**：所有基础数据的增删改操作必须记录变更履历（操作人、变更时间、变更内容），不可跳过。
- **领域事件解耦硬件通信**：BackgroundService 接收硬件数据后通过 MediatR 发布领域事件，业务逻辑在 Handler 中处理，禁止在通信层编写业务代码。

### Must Avoid

- **禁止在 Controller 中编写业务逻辑**：Controller 仅负责参数校验、权限标注和调用 Application Service。
- **禁止跨节点直接操作共享状态**：批次过站等并发操作必须通过 Redis 分布式锁保护，不可依赖内存锁。
- **禁止硬编码 PCB 工序链路**：工序流转（内层 → 压合 → 钻孔 → 电镀）应通过配置驱动，不可硬编码在代码中。
- **禁止跳过 DTO 直接暴露领域实体**：API 层必须通过 DTO 与前端交互，避免领域模型泄露。

## Code Patterns

### Naming Conventions

- 实体类：PascalCase（如 `MaterialMaster`, `EquipmentLedger`）
- DTO：`{Entity}Dto` / `{Entity}CreateDto` / `{Entity}UpdateDto`
- 仓储接口：`I{Entity}Repository`（定义在 Domain 层）
- 仓储实现：`{Entity}Repository`（定义在 Infrastructure 层）
- 应用服务：`{Entity}AppService`
- Controller：`{Entity}Controller`
- 数据库表名：snake_case（如 `material_master`, `equipment_ledger`）

### API Design Pattern

```csharp
// RESTful API 标准模式
[ApiController]
[Route("api/[controller]")]
[Authorize]
public class MaterialController : ControllerBase
{
    [HttpGet]
    public Task<PagedResult<MaterialDto>> GetList([FromQuery] MaterialQueryDto query);

    [HttpGet("{id}")]
    public Task<MaterialDto> GetById(long id);

    [HttpPost]
    public Task<long> Create([FromBody] MaterialCreateDto dto);

    [HttpPut("{id}")]
    public Task Update(long id, [FromBody] MaterialUpdateDto dto);

    [HttpDelete("{id}")]
    public Task Delete(long id);
}
```

### Data Permission Filter (SqlSugar)

```csharp
// 全局查询过滤器 - 数据权限隔离
sqlSugarClient.QueryFilter.AddTableFilter<WorkOrder>(w =>
    currentUser.AccessibleLineIds.Contains(w.LineId));
```

### Error Handling

```csharp
// 领域层抛出业务异常
throw new BusinessException("MATERIAL_CODE_DUPLICATE", "物料编码已存在");

// Application 层统一包装
// WebApi 层通过中间件全局捕获，返回标准化错误响应
```

### Import/Export Pattern

```csharp
// Excel 导入导出统一通过 Application Service 处理
[HttpPost("import")]
public Task<ImportResult> Import(IFormFile file);

[HttpGet("export")]
public Task<FileResult> Export([FromQuery] MaterialQueryDto query);
```

## Testing Patterns

### Unit Tests

- 测试文件位置：`tests/LiteMes.Domain.Tests/`, `tests/LiteMes.Application.Tests/`
- 命名：`{ClassName}_{MethodName}_{Scenario}_{ExpectedResult}`
- 重点测试：领域规则（编码唯一性、引用完整性、状态流转约束）

### Integration Tests

- 位置：`tests/LiteMes.Integration.Tests/`
- 重点验证：仓储实现、全局过滤器、并发锁机制

## Master Data Module (Phase 1)

### 1. 物料主数据 (Material Master Data)

| 子功能 | 核心实体 | 关键规则 |
|--------|---------|---------|
| 计量单位管理 | `Uom` | 编码与名称唯一，支持计算精度设置 |
| 单位换算比例 | `UomConversion` | 原单位↔目标单位换算率，支持多级换算 |
| 物料分类 | `MaterialCategory` | 多级分类树形结构 |
| 物料基本信息 | `MaterialMaster` | 编码/名称/规格/分类/版本，版本管理支持 A.1→A.2 更迭 |
| 免检清单 | `InspectionExemption` | 供应商+物料+有效时间范围定义免检规则 |

### 2. 企业架构与组织管理 (Enterprise Management)

| 子功能 | 核心实体 | 关键规则 |
|--------|---------|---------|
| 组织架构 | `Company` / `Factory` / `Department` | 三级结构：公司→工厂→部门，逐级关联 |
| 生产班制 | `ShiftSchedule` / `Shift` | 班制含多个班次，班次支持跨天（如 22:00-06:00） |
| 工作中心与工序 | `WorkCenter` / `ProcessStep` | 工厂→工作中心→工序的层级关系 |

### 3. 设备主数据 (Equipment Master Data)

| 子功能 | 核心实体 | 关键规则 |
|--------|---------|---------|
| 设备分类与型号 | `EquipmentCategory` / `EquipmentModel` | 类型（钻孔/测试/压合等）→型号，规范技术参数 |
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
- **数据完整性**：基础数据被业务单据引用后禁止物理删除，采用软删除（`IsDeleted` 标记）

## Architecture Notes

### Multi-point Deployment

| 问题 | 方案 |
|------|------|
| 批次过站并发冲突 | Redis 分布式锁 |
| 跨节点信号同步 | Redis Pub/Sub 或 SignalR 背板 |
| 会话状态共享 | JWT 无状态认证 |

### Data Volume Strategy

- **读写分离**：报表查询与实时过站业务分开
- **按月分表**：`ProcessLog` 等大表按时间分表
- **冷热分离**：历史数据归档，保持在线表轻量

## Recent Changes

| Date | Feature | Impact |
|------|---------|--------|
| 2026-04-20 | 项目初始化 | 创建 README 与项目架构设计 |

## Update Log

- 2026-04-20: Initial project context created, Master Data module requirements defined
