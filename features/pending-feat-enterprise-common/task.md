# Tasks: feat-enterprise-common 企业管理通用功能

## 任务清单

### 后端
- [x] AuditLog 实体类（继承 BaseEntity）
- [x] audit_log 表 DDL
- [x] AuditLogRepository + AuditLogMapper + AuditLogRepositoryImpl
- [x] AuditLogService（记录 CREATE/UPDATE/DELETE 审计日志）
- [x] AuditLogResource（分页查询审计日志 REST API）
- [x] DropdownItem DTO
- [x] DropdownService（公司/工厂/部门/班制下拉 + 级联过滤）
- [x] DropdownResource（通用下拉接口 REST API）
- [x] Repository 层添加 findAllActive/findByCompanyId 等方法

### 前端
- [x] auditLog.ts API 模块
- [x] dropdown.ts API 模块
- [x] AuditLogDialog.vue 可复用组件（变更履历弹窗）
- [x] useTableSettings.ts composable（表格列设置 + localStorage 持久化）
- [x] TableSettingsPanel.vue 组件（表格设置面板）
- [x] CompanyList.vue 集成变更履历按钮和表格设置
- [x] FactoryList.vue 集成变更履历按钮 + 使用 dropdown API
- [x] WorkCenterList.vue 集成变更履历按钮 + 使用 dropdown API
- [x] DepartmentList.vue 集成变更履历按钮 + 使用 dropdown API

---

## 进度记录

| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-20 | 已拆分 | 从 feat-enterprise-org 拆分 |
| 2026-04-22 | 已实现 | 后端审计日志 + 通用下拉 + 前端集成 |
