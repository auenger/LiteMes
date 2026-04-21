# Tasks: feat-shift-schedule 班制班次管理

## 任务清单

### 后端
- [x] 实体 `ShiftSchedule` 定义
- [x] 实体 `Shift` 定义
- [x] Repository 接口与实现
- [x] Service (CRUD + 唯一性校验 + 默认班制逻辑 + 跨天计算)
- [x] REST API 端点
- [ ] Excel 导入导出服务（基础架构就绪，Excel导入导出留待通用功能模块实现）

### 前端
- [x] 班制列表页面
- [x] 班制创建/编辑弹窗
- [x] 班次列表展示（作为班制的从表）
- [x] 班次创建/编辑弹窗
- [x] 查询功能（编码/名称模糊查询、状态下拉筛选）
- [ ] 导入按钮与文件选择（留待通用功能模块实现）
- [ ] 导出按钮（留待通用功能模块实现）
- [x] 启用/禁用功能

### 通用
- [x] 审计字段自动填充（继承 SoftDeleteEntity + AuditMetaObjectHandler）
- [ ] 变更日志记录（留待通用功能模块实现）

---

## 进度记录

| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-20 | 已拆分 | 从 feat-enterprise-org 拆分 |
| 2026-04-21 | 实现完成 | 后端: 16个Java文件(2实体+2仓储接口+2Mapper+2仓储实现+2Service+2Resource+6DTO+1PageDto), 前端: API模块+班制管理页面+路由 |
