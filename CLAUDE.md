# CLAUDE.md — LiteMes 项目开发指南

## 项目概述

LiteMes 是面向 PCB 制造场景的轻量级 MES 系统，单体服务四层架构，基于 **Quarkus 3.x (Java 17) + Vue 3** 构建。支持 JVM 模式与 GraalVM Native Image 双模式部署。

当前开发阶段聚焦 **基础数据（Master Data）模块**。

## 技术栈

| 层 | 技术 |
|----|------|
| 后端框架 | Quarkus 3.x, Java 17, Maven |
| REST API | JAX-RS |
| ORM | MyBatis-Plus |
| 认证 | SmallRye JWT |
| API 文档 | SmallRye OpenAPI (Swagger UI) |
| 缓存/锁 | Quarkus Redis (Lettuce) |
| 实时通信 | Quarkus WebSocket |
| 硬件通信 | Vert.x MQTT Client |
| 领域事件 | CDI Events |
| 定时任务 | Quarkus Scheduler |
| 日志 | JBoss Logging + SLF4J |
| 测试 | JUnit 5 + REST Assured (QuarkusTest) |
| Native | GraalVM Native Image |
| 前端 | Vue 3 + Pinia + Vite + TypeScript |

## 构建与运行

```bash
# 后端开发模式
./mvnw quarkus:dev

# 运行测试
./mvnw test

# Native Image 编译
./mvnw package -Pnative
./target/litemes-1.0.0-SNAPSHOT-runner

# 前端开发
cd frontend && npm install && npm run dev
```

## 四层架构

严格分层，不可跨层调用：

```
web/ (REST API 层 — JAX-RS Resource)
  ↓ 调用
application/ (应用服务层 — 业务编排、事务、DTO 转换)
  ↓ 调用
domain/ (领域层 — 实体、仓储接口、领域服务、领域事件)
  ↑ 实现
infrastructure/ (基础设施层 — MyBatis-Plus 仓储、MQTT、Redis、配置)
```

- `web` → 依赖 `application`
- `application` → 依赖 `domain`
- `infrastructure` → 依赖 `domain`（实现仓储接口）
- `web` 可直接依赖 `infrastructure`（CDI 注入）

## 编码规范

### 命名

| 类型 | 规范 | 示例 |
|------|------|------|
| 实体类 | PascalCase | `MaterialMaster`, `EquipmentLedger` |
| REST Resource | `{Entity}Resource` | `MaterialResource` |
| 应用服务 | `{Entity}Service` | `MaterialService` |
| 仓储接口 | `{Entity}Repository` | `MaterialRepository` |
| DTO | `{Entity}Dto` / `Create/Update` | `MaterialCreateDto` |
| MyBatis Mapper | `{Entity}Mapper` | `MaterialMapper` |
| 数据库表名 | snake_case | `material_master` |

### 关键规则

- **四层分离**：Resource 仅做参数校验和调用 Service；Service 负责编排；Domain 封装规则；Infrastructure 实现仓储
- **关键编码不可变**：物料编码、工厂编码等业务主键创建后不可编辑
- **引用数据不可删除**：被引用的基础数据采用软删除（`@TableLogic`）
- **数据权限全局过滤**：通过 MyBatis-Plus 拦截器实现，禁止在业务代码中手动拼接权限条件
- **审计日志强制记录**：所有增删改必须通过 BaseEntity 基类记录操作人和时间
- **禁止跳过 DTO**：API 层必须通过 DTO 与前端交互
- **领域事件解耦硬件通信**：MQTT 数据接收后通过 CDI Events 发布，业务逻辑在 Observer 中处理

## Feature Workflow

项目使用 Feature Workflow 管理开发流程：
- 配置：`feature-workflow/config.yaml`
- 队列：`feature-workflow/queue.yaml`
- Feature 文档：`features/pending-{id}/` 或 `features/active-{id}/`
- 归档：`features/archive/`

常用命令：
- `/list-features` — 查看所有 Feature 状态
- `/new-feature` — 创建新 Feature
- `/start-feature <id>` — 开始开发
- `/complete-feature <id>` — 完成并归档

## 相关文件

- `project-context.md` — 项目上下文知识库（技术栈、编码模式、领域模型）
- `README.md` — 项目概述与架构说明
