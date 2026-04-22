# Tasks: feat-e2e-material-category

## Task Breakdown

### 1. 物料分类测试 — `e2e/material/material-category.spec.ts`
- [ ] 物料分类列表加载测试（树形）— 断言树形结构可见 — Scenario: 物料分类列表加载
- [ ] 新建物料分类测试 — 填写编码/名称，断言树中出现 — Scenario: 新建物料分类
- [ ] 新建子分类测试 — 在父节点下新建，断言层级关系 — Scenario: 新建子分类
- [ ] 编辑物料分类测试 — 修改名称，断言更新 — Scenario: 编辑物料分类
- [ ] 删除子分类测试 — 删除 + 确认，断言消失 — Scenario: 删除子分类

### 2. 数据清理
- [ ] afterEach 清理测试数据（先删子分类再删父分类）

## Progress Log
| Date | Progress | Notes |
|------|----------|-------|
| 2026-04-22 | Feature 创建 | Split from feat-e2e-material |
