# Tasks: feat-e2e-production-base

## Task Breakdown

### 1. 工作中心测试 — `e2e/production/work-center.spec.ts`
- [ ] 工作中心列表加载测试 — 导航到 /work-centers，断言表格和分页可见 — Scenario: 工作中心列表加载
- [ ] 新建工作中心测试（关联工厂）— 点击"新建工作中心"，填写编码/名称，选择工厂，断言成功 — Scenario: 新建工作中心成功
- [ ] 工作中心必填校验 — 空表单提交，断言校验提示 — Scenario: 工作中心编码必填校验
- [ ] 编辑工作中心测试 — 修改名称，断言更新 + 编码只读 — Scenario: 编辑工作中心
- [ ] 删除工作中心测试 — 删除 + 确认，断言消失 — Scenario: 删除工作中心

### 2. 计量单位测试 — `e2e/production/uom.spec.ts`
- [ ] 计量单位列表加载测试 — 导航到 /uoms，断言表格可见 — Scenario: 计量单位列表加载
- [ ] 新建计量单位测试 — 点击"新建单位"，填写编码/名称/精度，断言成功 — Scenario: 新建计量单位成功
- [ ] 计量单位必填校验 — 空表单提交，断言校验提示 — Scenario: 计量单位编码必填校验
- [ ] 编辑计量单位测试 — 修改名称和精度，断言更新 + 编码只读 — Scenario: 编辑计量单位
- [ ] 删除计量单位测试 — 删除 + 确认，断言消失 — Scenario: 删除计量单位

### 3. 单位换算测试 — `e2e/production/uom-conversion.spec.ts`
- [ ] 单位换算列表加载测试 — 导航到 /uom-conversions，断言表格可见 — Scenario: 单位换算列表加载
- [ ] 新建单位换算测试 — 先创建两个计量单位，选择源/目标单位，填写比例，断言成功 — Scenario: 新建单位换算关系
- [ ] 删除单位换算测试 — 删除 + 确认，断言消失 — Scenario: 删除单位换算关系

### 4. 数据清理
- [ ] 每个 spec 实现 afterEach 清理逻辑（按反向依赖顺序删除：换算 → 单位 → 工作中心）

## Progress Log
| Date | Progress | Notes |
|------|----------|-------|
| 2026-04-22 | Feature 创建 | 等待开发 |
