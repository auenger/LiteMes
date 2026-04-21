# Tasks: feat-company 公司管理

## 任务清单

### 后端
- [x] 实体 `Company` 定义
- [x] Repository 接口与实现
- [x] Service (CRUD + 唯一性校验 + 引用校验)
- [x] REST API 端点

### 前端
- [x] 公司列表页面
- [x] 公司创建弹窗
- [x] 公司编辑弹窗
- [x] 查询功能（编码/名称模糊查询、状态下拉筛选）
- [x] 删除确认（引用校验提示）
- [x] 启用/禁用功能

### 通用
- [x] 审计字段自动填充
- [ ] 变更日志记录（将在 feat-enterprise-common 中实现）

---

## 进度记录

| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-20 | 已拆分 | 从 feat-enterprise-org 拆分 |
| 2026-04-21 | 后端实现完成 | Company entity, mapper, repository, service, resource |
| 2026-04-21 | 前端实现完成 | CompanyList.vue 含 CRUD、查询、启用禁用 |
| 2026-04-21 | SQL DDL | company 表结构加入 schema.sql |
