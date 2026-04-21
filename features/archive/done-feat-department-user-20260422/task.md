# Tasks: feat-department-user 部门用户关系

## 任务清单

### 后端
- [x] 实体 `DepartmentUser` 定义
- [x] Repository 接口与实现
- [x] Service (分配用户、移除用户)
- [x] REST API 端点

### 前端
- [x] 部门用户列表展示
- [x] "选择用户"按钮与弹窗
- [x] 用户查询功能（按用户名/姓名）
- [x] 勾选与确认分配
- [x] 移除用户功能

### 通用
- [x] 审计字段自动填充

---

## 进度记录

| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-20 | 已拆分 | 从 feat-enterprise-org 拆分 |
| 2026-04-22 | 后端完成 | 实体、仓储、服务、REST API 全部实现 |
| 2026-04-22 | 前端完成 | 用户列表、选择弹窗、移除功能全部实现 |
| 2026-04-22 | DDL完成 | user 和 department_user 表已添加到 schema.sql |
| 2026-04-22 | 集成完成 | DepartmentService.delete() 已更新，级联删除部门用户关系 |
