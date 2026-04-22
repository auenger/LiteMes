# Tasks: feat-process-frontend

## 任务分解

### 1. API 封装
- [x] 创建 api/process.ts
  - listProcesses(params) — 分页查询
  - getProcess(id) — 获取详情
  - createProcess(data) — 创建
  - updateProcess(id, data) — 更新
  - deleteProcess(id) — 删除
  - updateProcessStatus(id, status) — 状态切换
  - TypeScript 类型定义

### 2. 视图组件
- [x] 创建 views/process/ProcessList.vue
  - 搜索栏（编码、名称、工作中心下拉、状态筛选）
  - 数据表格（编码、名称、工作中心、状态、操作）
  - 新增/编辑对话框（el-dialog + el-form，编码编辑时禁用）
  - 状态切换按钮
  - 删除确认
  - 分页

### 3. 路由
- [x] router/index.ts 添加 /processes 路由

## 进度日志
| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-22 | 创建 | 任务分解完成 |
| 2026-04-22 | 完成 | 3/3 tasks implemented |
