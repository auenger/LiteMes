# Tasks: feat-e2e-equipment

## Task Breakdown

### 1. 设备类型测试 — `e2e/equipment/equipment-type.spec.ts`
- [ ] 设备类型列表加载测试 — 导航到 /equipment-types，断言表格可见 — Scenario: 设备类型列表加载
- [ ] 新建设备类型测试 — 填写编码/名称，断言成功 — Scenario: 新建设备类型
- [ ] 设备类型必填校验 — 空表单提交，断言校验提示 — Scenario: 设备类型编码必填校验
- [ ] 编辑设备类型测试 — 修改名称，断言更新 + 编码只读 — Scenario: 编辑设备类型
- [ ] 删除设备类型测试 — 删除 + 确认，断言消失 — Scenario: 删除设备类型

### 2. 设备型号测试 — `e2e/equipment/equipment-model.spec.ts`
- [ ] 设备型号列表加载测试 — 导航到 /equipment-models，断言表格可见 — Scenario: 设备型号列表加载
- [ ] 新建设备型号测试（关联类型）— 填写编码/名称，选择设备类型，断言成功 — Scenario: 新建设备型号
- [ ] 编辑设备型号测试 — 修改名称，断言更新 + 编码只读 — Scenario: 编辑设备型号
- [ ] 删除设备型号测试 — 删除 + 确认，断言消失 — Scenario: 删除设备型号

### 3. 设备台账测试 — `e2e/equipment/equipment-ledger.spec.ts`
- [ ] 设备台账列表加载测试 — 导航到 /equipment-ledger，断言表格可见 — Scenario: 设备台账列表加载
- [ ] 新建设备测试（完整表单）— 填写编码/名称，级联选择类型→型号，选择运行/管理状态，选择工厂，填写制造商，选择日期，断言成功 — Scenario: 新建设备
- [ ] 级联选择器联动验证 — 选择类型后断言型号下拉仅显示对应型号 — Scenario: 级联选择器联动验证
- [ ] 编辑设备台账测试 — 修改名称和运行状态，断言更新 + 编码只读 — Scenario: 编辑设备台账
- [ ] 删除设备台账测试 — 删除 + 确认，断言消失 — Scenario: 删除设备台账

### 4. 数据清理
- [ ] 每个 spec 实现 afterEach 清理逻辑（反向依赖：台账 → 型号 → 类型）

## Progress Log
| Date | Progress | Notes |
|------|----------|-------|
| 2026-04-22 | Feature 创建 | 等待开发 |
