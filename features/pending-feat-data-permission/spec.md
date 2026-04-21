# Feature: feat-data-permission 数据权限控制

## 基本信息
- **ID**: feat-data-permission
- **名称**: 数据权限控制
- **优先级**: 50
- **规模**: L（模块级）
- **依赖**: feat-enterprise-org
- **创建时间**: 2026-04-20

## 需求来源
- 数据权限_功能设计_V1.0.docx

## 模块概述
数据权限用于统一维护数据权限组及数据权限配置，规范系统内数据访问与使用范围。通过标准化数据权限控制规则，为不同角色和用户提供安全、可控的数据访问机制，保障系统数据的安全性和业务运行的合规性。

## 子 Feature 列表

| ID | 名称 | 优先级 | 规模 | 依赖 |
|----|------|--------|------|------|
| [feat-permission-group](../pending-feat-permission-group/spec.md) | 数据权限组管理 | 52 | M | feat-enterprise-org |
| [feat-user-permission](../pending-feat-user-permission/spec.md) | 用户数据权限 | 51 | M | feat-permission-group |

## 依赖关系图
```
feat-enterprise-org
  ├── feat-factory（提供 Factory 实体和下拉数据）
  ├── feat-department-user（提供 User 用户数据）
  └── feat-enterprise-common（提供通用下拉接口、级联选择器）
        │
        ▼
feat-permission-group（权限组 CRUD + 工厂/工作中心/工序关联）
        │
        ▼
feat-user-permission（用户权限绑定 + 批量操作 + MyBatis-Plus 全局拦截器）
```

## 前置依赖实体说明

数据权限模块依赖以下实体，这些实体需要在关联 feature 中先定义，或在 project scaffold 阶段预留：

| 实体 | 来源 Feature | 状态 | 说明 |
|------|-------------|------|------|
| **Factory（工厂）** | feat-factory | pending | 权限组关联工厂，已有完整数据模型和 DDL |
| **User（用户）** | feat-department-user | pending | 用户权限的主体，需提供用户 ID、用户名、姓名 |
| **WorkCenter（工作中心）** | feat-work-center | pending | 已新增为 feat-enterprise-org 子 feature，含完整数据模型和 DDL |
| **Process（工序）** | feat-process | pending | 已新增为 feat-enterprise-org 子 feature，含完整数据模型和 DDL |

## 整体数据模型关系
```
DataPermissionGroup 1──N DataPermissionGroupFactory
                    1──N DataPermissionGroupWorkCenter
                    1──N DataPermissionGroupProcess

UserDataPermission  1──N UserDataPermissionFactory (source: GROUP / DIRECT)
                    1──N UserDataPermissionWorkCenter (source: GROUP / DIRECT)
                    1──N UserDataPermissionProcess (source: GROUP / DIRECT)

DataPermissionGroup 1──N UserDataPermission（权限组被用户权限引用，group_id 可为空）
User               1──1 UserDataPermission（一个用户一条权限主记录）
Factory            1──N DataPermissionGroupFactory / UserDataPermissionFactory
WorkCenter         1──N DataPermissionGroupWorkCenter / UserDataPermissionWorkCenter
Process            1──N DataPermissionGroupProcess / UserDataPermissionProcess
```

## 整体技术架构

### 全局数据权限过滤方案
```
┌──────────────┐     ┌──────────────────┐     ┌─────────────────┐
│  REST API    │────▶│  Application     │────▶│  Repository     │
│  (Resource)  │     │  Service         │     │  (MyBatis-Plus)  │
└──────────────┘     └──────────────────┘     └────────┬────────┘
                                                       │
                                              ┌────────▼────────┐
                                              │ DataPermission  │
                                              │ Interceptor     │
                                              │ (MyBatis-Plus)  │
                                              └────────┬────────┘
                                                       │
                       ┌───────────────────┬───────────┴──┐
                       ▼                   ▼              ▼
               ┌──────────┐      ┌──────────┐    ┌──────────┐
               │ Redis    │      │ SQL 自动  │    │ 权限配置  │
               │ 权限缓存  │      │ 追加条件  │    │ (表→维度) │
               └──────────┘      └──────────┘    └──────────┘
```

### 关键技术点
1. **MyBatis-Plus 全局拦截器**：实现 `Interceptor` 接口，在 SQL 执行前根据当前用户权限自动追加过滤条件
2. **Redis 权限缓存**：用户登录时加载权限到 Redis，权限变更时主动刷新，避免每次查库
3. **JWT + SecurityContext**：从 JWT Token 解析用户 ID，通过 SecurityContext 传递到拦截器
4. **权限维度配置**：定义哪些业务表需要数据权限过滤，以及按哪个维度（工厂/工作中心/工序）过滤

### source 字段设计
用户权限关联记录的 `source` 字段区分权限来源：
- `GROUP`：从权限组继承而来，批量授权时自动生成
- `DIRECT`：管理员直接授权，不受权限组变更影响

权限计算规则：**最终权限 = GROUP 权限 ∪ DIRECT 权限**（取并集）

## 模块进度
| 子 Feature | 状态 | 备注 |
|-----------|------|------|
| feat-permission-group | pending | Spec enriched: 3 价值点, 12 场景, 31 任务 |
| feat-user-permission | pending | Spec enriched: 3 价值点, 12 场景, 45 任务 |

## 待决事项
1. ~~**WorkCenter 和 Process 实体来源**~~：已解决，新增 `feat-work-center` 和 `feat-process` 作为 `feat-enterprise-org` 子 feature
2. **全局拦截器作用范围**：需确定哪些业务表需要数据权限过滤，以及对应的过滤维度
3. **超级管理员豁免**：是否需要超级管理员角色跳过数据权限过滤
