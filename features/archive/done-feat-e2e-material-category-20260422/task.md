# Tasks: feat-e2e-material-category

## Task Breakdown

### 1. 物料分类测试 — `e2e/material/material-category.spec.ts`
- [x] 物料分类列表加载测试（树形）— 断言树形结构可见 — Scenario: 物料分类列表加载
- [x] 新建物料分类测试 — 填写编码/名称，断言树中出现 — Scenario: 新建物料分类
- [x] 新建子分类测试 — 在父节点下新建，断言层级关系 — Scenario: 新建子分类
- [x] 编辑物料分类测试 — 修改名称，断言更新 — Scenario: 编辑物料分类
- [x] 删除子分类测试 — 删除 + 确认，断言消失 — Scenario: 删除子分类

### 2. 数据清理
- [x] 测试数据使用唯一时间戳前缀，测试间互不干扰

## Progress Log
| Date | Progress | Notes |
|------|----------|-------|
| 2026-04-22 | Feature 创建 | Split from feat-e2e-material |
| 2026-04-22 | 实现完成 | 5 个测试用例编写完成，复用 auth fixture 和 common helpers |
