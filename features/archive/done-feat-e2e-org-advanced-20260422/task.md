# Tasks: feat-e2e-org-advanced

## Task Breakdown

### 1. 部门管理测试 — `e2e/org/department.spec.ts`
- [x] 部门列表加载测试（树形）— 断言树形结构可见 — Scenario: 部门列表加载
- [x] 新建顶级部门测试 — 填写编码/名称，选择工厂，断言成功 — Scenario: 新建顶级部门
- [x] 新建子部门测试 — 选择上级部门，断言挂在父节点下 — Scenario: 新建子部门
- [x] 编辑部门测试 — 修改名称，断言更新 — Scenario: 编辑部门
- [x] 删除子部门测试 — 删除 + 确认，断言消失 — Scenario: 删除子部门

### 2. 班制班次测试 — `e2e/org/shift-schedule.spec.ts`
- [x] 班制列表加载测试 — 断言 .el-table 可见 — Scenario: 班制列表加载
- [x] 新建班制测试 — 点击"新增班制"，填写编码/名称，断言成功 — Scenario: 新建班制
- [x] 管理班次测试（嵌套对话框）— 点击管理班次，新增班次（编码/名称/时间/跨天），断言成功 — Scenario: 管理班次
- [x] 删除班制测试 — 删除 + 确认，断言消失 — Scenario: 删除班制

### 3. 数据清理
- [x] 每个 spec 实现 afterEach 清理逻辑（反向顺序：班制 → 部门）

## Progress Log
| Date | Progress | Notes |
|------|----------|-------|
| 2026-04-22 | Feature 创建 | Split from feat-e2e-org |
