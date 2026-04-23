# Tasks: feat-e2e-org-base

## Task Breakdown

### 1. 公共基础设施（所有 E2E 测试共享）
- [ ] 创建 `e2e/fixtures/auth.ts` — Playwright test.extend 认证 fixture，封装登录流程（goto /login → fill admin/admin123 → click "登 录" → waitForURL）— 所有 Scenario 的 Background
- [ ] 创建 `e2e/helpers/common.ts` — 公共工具函数：findTableRowByText、confirmDialog、deleteWithConfirm、waitForToast

### 2. 公司管理测试 — `e2e/org/company.spec.ts`
- [ ] 公司列表加载测试 — 断言 .el-table 和 .el-pagination 可见 — Scenario: 公司列表加载
- [ ] 新建公司测试 — 点击"新建公司"，填写编码/名称/简码，断言表格出现 — Scenario: 新建公司成功
- [ ] 公司编码必填校验 — 空表单提交，断言校验提示 — Scenario: 公司编码必填校验
- [ ] 编辑公司测试 — 修改名称，断言更新 + 编码只读 — Scenario: 编辑公司
- [ ] 删除公司测试 — 删除 + 确认，断言消失 — Scenario: 删除公司

### 3. 工厂管理测试 — `e2e/org/factory.spec.ts`
- [ ] 工厂列表加载测试 — 断言 .el-table 可见 — Scenario: 工厂列表加载
- [ ] 新建工厂测试（关联公司）— 填写编码/名称，选择公司，断言成功 — Scenario: 新建工厂成功
- [ ] 工厂必填校验 — 空表单提交，断言校验 — Scenario: 工厂编码必填校验
- [ ] 编辑工厂测试 — 修改名称，断言更新 + 编码只读 — Scenario: 编辑工厂
- [ ] 删除工厂测试 — 删除 + 确认，断言消失 — Scenario: 删除工厂

### 4. 数据清理
- [ ] 每个 spec 实现 afterEach 清理逻辑（反向顺序：工厂 → 公司）

## Progress Log
| Date | Progress | Notes |
|------|----------|-------|
| 2026-04-22 | Feature 创建 | Split from feat-e2e-org |
