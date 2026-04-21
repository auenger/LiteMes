# Tasks: feat-factory 工厂管理

## 任务清单

### 后端
- [x] 实体 `Factory` 定义
- [x] Repository 接口与实现
- [x] Service (CRUD + 唯一性校验 + 引用校验)
- [x] REST API 端点

### 前端
- [x] 工厂列表页面
- [x] 工厂创建弹窗（含公司下拉选择）
- [x] 工厂编辑弹窗
- [x] 查询功能（编码/名称/公司模糊查询、状态下拉筛选）
- [x] 删除确认（引用校验提示）
- [x] 启用/禁用功能

### 通用
- [x] 审计字段自动填充
- [x] 变更日志记录

---

## 进度记录

| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-20 | 已拆分 | 从 feat-enterprise-org 拆分 |
| 2026-04-22 | 已实现 | 后端：Factory实体、Repository、Service、Resource、DTOs、DDL；前端：API模块、工厂列表页、路由注册 |
