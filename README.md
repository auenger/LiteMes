# LiteMes - PCB 工业自动化轻量级 MES 系统

> 基于 Quarkus (Java 17) + Vue 3 的单体服务四层架构，面向 PCB 制造场景的轻量级制造执行系统。
> 支持 JVM 模式与 GraalVM Native Image 双模式部署。

## 架构概览

```
┌─────────────────────────────────────────────────────┐
│                   Vue 3 (Pinia + Vite)              │
│              前端 · 功能权限 · 数据展示               │
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
│           Quarkus Scheduled (硬件轮询)                │
└─────────────────────────────────────────────────────┘
```

## 技术栈

| 模块 | 技术方案 | 说明 |
| :--- | :--- | :--- |
| 运行时 | Quarkus 3.x | 云原生 Java 框架，启动快，内存占用低 |
| JDK | Java 17 (LTS) | 稳定长期支持版本 |
| 构建工具 | Maven | 依赖管理稳定，工业项目主流 |
| ORM | MyBatis-Plus | SQL 控制力强，分表插件成熟，国内工业项目验证充分 |
| 硬件通信 | Vert.x MQTT Client | 高性能异步 MQTT 客户端，适配工业网关 |
| 领域事件 | CDI Events | Quarkus 原生事件机制，解耦硬件通信与业务逻辑 |
| 定时任务 | Quarkus Scheduler | 内置 cron 调度，工单排程、自动结案等 |
| 日志 | JBoss Logging + SLF4J | Quarkus 内置结构化日志 |
| 缓存/锁 | Quarkus Redis (Lettuce) | 分布式锁 + 信号同步 (Pub/Sub) |
| 实时通信 | Quarkus WebSocket | 轻量双向通信，跨节点状态推送 |
| API 文档 | SmallRye OpenAPI | 自动生成 OpenAPI 3.0 文档 + Swagger UI |
| 认证授权 | SmallRye JWT | Token 校验 + RBAC 权限 |
| Native 编译 | GraalVM | 支持 Native Image，启动 <100ms，内存极低 |
| 前端框架 | Vue 3 + Pinia | 状态管理清晰，Vite 构建极快 |

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

## 权限设计

### 功能权限 (RBAC)

- **前端：** 通过 `v-if` 指令控制按钮级操作权限（下发表单、导出报表等）
- **API：** 使用 `[Authorize]` 特性拦截非法请求

### 数据权限 (Data Scoping)

PCB 流程链路长（内层 → 压合 → 钻孔 → 电镀），数据权限基于**产线/工序**隔离：

- 用户绑定可访问的 `LineIds`（产线 ID 列表）
- 仓储层通过**全局查询过滤器**自动过滤数据

```java
// MyBatis-Plus 全局拦截器示例
@Bean
public MybatisPlusInterceptor dataScopeInterceptor(CurrentUser currentUser) {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    interceptor.addInnerInterceptor(new DataScopeInnerInterceptor(
        currentUser.getAccessibleLineIds()
    ));
    return interceptor;
}
```

- 领域方法内嵌权限校验：`Lot.MoveToNextStep(userId)` 内部校验用户是否拥有目标工序的操作权限

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
| 跨节点信号同步 | Redis Pub/Sub 或 SignalR 背板 |
| 会话状态共享 | Redis Session / JWT 无状态 |

## 数据量规划

PCB 行业数据增长极快（每次过站、每个检测均产生记录），建议：

1. **读写分离** — 报表查询与实时过站业务分开
2. **按月分表** — `ProcessLog`（过站日志）等大表按时间分表
3. **冷热分离** — 历史数据归档，保持在线表轻量

## 项目结构 (规划)

```
LiteMes/
├── src/
│   └── main/
│       ├── java/com/litemes/
│       │   ├── web/                    # REST API 层 (JAX-RS)
│       │   ├── application/            # 应用服务层
│       │   ├── domain/                 # 领域层
│       │   └── infrastructure/         # 基础设施层
│       └── resources/
│           ├── application.yml         # Quarkus 配置
│           └── mapper/                 # MyBatis XML 映射
├── frontend/                           # Vue 3 前端
│   ├── src/
│   │   ├── views/                      # 页面
│   │   ├── stores/                     # Pinia 状态管理
│   │   ├── api/                        # 接口封装
│   │   └── components/                 # 公共组件
│   └── vite.config.ts
├── src/test/                           # 单元测试 / 集成测试
├── docs/                               # 设计文档
└── pom.xml                             # Maven 构建
```

## 快速开始

> 项目初始化中，后续补充。

```bash
# 后端 (JVM 模式)
./mvnw quarkus:dev

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
