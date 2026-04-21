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
  └── feat-permission-group（权限组 CRUD + 工厂/工作中心/工序关联）
        └── feat-user-permission（用户权限绑定 + 批量操作 + 全局过滤器）
```

## 整体数据模型关系
```
DataPermissionGroup 1──N DataPermissionGroupFactory
                    1──N DataPermissionGroupWorkCenter
                    1──N DataPermissionGroupProcess

UserDataPermission  1──N UserDataPermissionFactory
                    1──N UserDataPermissionWorkCenter
                    1──N UserDataPermissionProcess

DataPermissionGroup 1──N UserDataPermission（权限组被用户权限引用）
```

## 模块进度
| 子 Feature | 状态 | 备注 |
|-----------|------|------|
| feat-permission-group | pending | |
| feat-user-permission | pending | |
