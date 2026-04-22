# Tasks: feat-e2e-supply-chain

## Task Breakdown

### 1. 客户管理测试 — `e2e/supply-chain/customer.spec.ts`
- [ ] 客户列表加载测试 — 导航到 /customers，断言表格可见 — Scenario: 客户列表加载
- [ ] 新建客户测试 — 填写编码/名称，选择类型，断言成功 — Scenario: 新建客户
- [ ] 客户必填校验 — 空表单提交，断言校验提示 — Scenario: 客户编码必填校验
- [ ] 编辑客户测试 — 修改名称，断言更新 + 编码只读 — Scenario: 编辑客户
- [ ] 删除客户测试 — 删除 + 确认，断言消失 — Scenario: 删除客户

### 2. 供应商管理测试 — `e2e/supply-chain/supplier.spec.ts`
- [ ] 供应商列表加载测试 — 导航到 /suppliers，断言表格可见 — Scenario: 供应商列表加载
- [ ] 新建供应商测试 — 填写编码/名称，断言成功 — Scenario: 新建供应商
- [ ] 供应商必填校验 — 空表单提交，断言校验提示 — Scenario: 供应商编码必填校验
- [ ] 编辑供应商测试 — 修改名称，断言更新 + 编码只读 — Scenario: 编辑供应商
- [ ] 删除供应商测试 — 删除 + 确认，断言消失 — Scenario: 删除供应商

### 3. 数据清理
- [ ] 每个 spec 实现 afterEach 清理逻辑

## Progress Log
| Date | Progress | Notes |
|------|----------|-------|
| 2026-04-22 | Feature 创建 | 等待开发 |
