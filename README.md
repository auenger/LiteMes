# LiteMes - PCB 工业自动化轻量级 MES 系统

> 基于 Quarkus 3.21 (Java 17) + Vue 3 + Element Plus 的单体服务四层架构，面向 PCB 制造场景的轻量级制造执行系统。
> 支持 JVM 模式与 GraalVM Native Image 双模式部署。

## 当前状态

- **基础数据（Master Data）模块** — 后端 + 前端已完成，涵盖企业架构、物料、设备、供应链、数据权限 5 大子模块共 24 个 Feature
- **登录认证** — JWT 认证 + 前端路由守卫已完成
- **下一步** — 生产执行（Work Order / Lot / ProcessLog）模块

## 架构概览

```
┌─────────────────────────────────────────────────────┐
│              Vue 3 + Element Plus + Pinia            │
│        前端 · JWT 认证 · 功能权限 · 数据展示          │
├─────────────────────────────────────────────────────┤
│              REST API Layer (JAX-RS / Vert.x)        │
│       RESTful · SmallRye JWT · OpenAPI / Swagger     │
├─────────────────────────────────────────────────────┤
│              Application Service Layer               │
│        业务编排 · 事务管理 · DTO <-> 领域模型转换      │
├─────────────────────────────────────────────────────┤
│                    Domain Layer                      │
│    聚合根 · 领域服务 · 仓储接口 · 领域事件(CDI Events) │
├─────────────────────────────────────────────────────┤
│                Infrastructure Layer                  │
│  MyBatis-Plus 仓储 · Vert.x MQTT · SLF4J 结构化日志  │
│      Redis 缓存/锁 · 数据权限拦截器 · 审计自动填充     │
└─────────────────────────────────────────────────────┘
```

## 技术栈

| 模块 | 技术方案 | 版本 | 说明 |
| :--- | :--- | :--- | :--- |
| 运行时 | Quarkus | 3.21.4 | 云原生 Java 框架，启动快，内存占用低 |
| JDK | Java 17 | LTS | 稳定长期支持版本 |
| 构建工具 | Maven | — | 依赖管理稳定，工业项目主流 |
| ORM | MyBatis-Plus (Quarkiverse) | 2.4.2 | SQL 控制力强，分页插件成熟 |
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
| HTTP 客户端 | Axios | — | 前端 API 请求封装 |
| 测试框架 | JUnit 5 + REST Assured | — | QuarkusTest 集成测试 |

## 核心领域模型

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

## 已完成功能

### 基础数据模块 — 后端 + 前端

| 子模块 | Feature | 核心实体 | 状态 |
|--------|---------|---------|------|
| **企业架构** | feat-company | Company | 已完成 |
| | feat-factory | Factory | 已完成 |
| | feat-department | Department | 已完成 |
| | feat-department-user | DepartmentUser | 已完成 |
| | feat-shift-schedule | ShiftSchedule / Shift | 已完成 |
| | feat-work-center | WorkCenter | 已完成 |
| | feat-process | Process | 已完成 |
| | feat-enterprise-common | 通用下拉/级联 | 已完成 |
| **物料管理** | feat-uom | Uom / UomConversion | 已完成 |
| | feat-material-category | MaterialCategory | 已完成 |
| | feat-material-info | MaterialMaster / MaterialVersion | 已完成 |
| | feat-inspection-exemption | InspectionExemption | 已完成 |
| **设备管理** | feat-equipment-type | EquipmentType | 已完成 |
| | feat-equipment-model | EquipmentModel | 已完成 |
| | feat-equipment-ledger | EquipmentLedger | 已完成 |
| **供应链** | feat-customer | Customer / CustomerMaterial | 已完成 |
| | feat-supplier | Supplier / SupplierMaterial | 已完成 |
| **数据权限** | feat-permission-group | DataPermissionGroup | 已完成 |
| | feat-user-permission | UserDataPermission | 已完成 |

### 基础设施与前端框架

| Feature | 说明 | 状态 |
|---------|------|------|
| feat-project-scaffold | 项目骨架、四层架构搭建 | 已完成 |
| feat-frontend-ui-lib | Element Plus 集成、全局样式 | 已完成 |
| feat-frontend-layout | 导航布局、Tab 管理、主题切换 | 已完成 |
| feat-auth-frontend | 登录页、JWT 路由守卫、Token 管理 | 已完成 |
| feat-cascading-selector | 级联选择器通用组件 | 已完成 |
| fix-test-create-400 | 测试修复 | 已完成 |

## 权限设计

### 功能权限 (RBAC)

- **前端：** 通过 `v-if` 指令控制按钮级操作权限（下发表单、导出报表等）
- **API：** 使用 `@Authenticated` 注解拦截非法请求

### 数据权限 (Data Scoping)

PCB 流程链路长（内层 → 压合 → 钻孔 → 电镀），数据权限基于**工厂/工作中心/工序**隔离：

- 数据权限组将工厂 + 工作中心 + 工序打包为权限集合
- 用户通过绑定权限组实现工序级数据隔离
- 仓储层通过 `DataPermissionInterceptor` 全局拦截器自动过滤数据

```java
// MyBatis-Plus 全局拦截器
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

## 硬件通信中间件 (Edge Middleware)

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
│  Domain Event Handlers   │  ← 业务逻辑，不污染通信层
└──────────────────────────┘
```

## 多点部署支持

单体服务支持 Load Balance 多点部署，需关注：

| 问题 | 方案 |
| :--- | :--- |
| 批次过站并发冲突 | Redis 分布式锁 |
| 跨节点信号同步 | Redis Pub/Sub |
| 会话状态共享 | JWT 无状态认证 |

## 数据量规划

PCB 行业数据增长极快（每次过站、每个检测均产生记录），建议：

1. **读写分离** — 报表查询与实时过站业务分开
2. **按月分表** — `ProcessLog`（过站日志）等大表按时间分表
3. **冷热分离** — 历史数据归档，保持在线表轻量

## 项目结构

```
LiteMes/
├── src/
│   └── main/
│       ├── java/com/litemes/
│       │   ├── web/                    # REST API 层 (JAX-RS Resource, DTO, ExceptionMapper)
│       │   ├── application/            # 应用服务层 (Service, 业务编排, 事务管理)
│       │   ├── domain/                 # 领域层 (Entity, Repository接口, DomainService, Event)
│       │   └── infrastructure/         # 基础设施层 (Mapper, RepositoryImpl, Config, MQTT)
│       └── resources/
│           ├── application.yml         # Quarkus 配置
│           └── mapper/                 # MyBatis XML 映射文件
├── src/test/java/com/litemes/          # 测试 (JUnit 5 + REST Assured)
├── frontend/                           # Vue 3 前端
│   ├── src/
│   │   ├── api/                        # Axios 接口封装 (26 模块)
│   │   ├── assets/                     # 静态资源
│   │   ├── components/                 # 公共组件 (CascadingSelector, AuditLogDialog 等)
│   │   ├── composables/                # Vue 3 组合式函数
│   │   ├── layouts/                    # 布局组件 (MainLayout)
│   │   ├── router/                     # Vue Router 路由 + 守卫
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

## 快速开始

```bash
# 后端 (JVM 模式)
./mvnw quarkus:dev

# 运行测试
./mvnw test

# 后端 (Native Image 编译)
./mvnw package -Pnative
./target/litemes-1.0.0-SNAPSHOT-runner

# 前端
cd frontend
npm install
npm run dev
```

## 许可证

待定
