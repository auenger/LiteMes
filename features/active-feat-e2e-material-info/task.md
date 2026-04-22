# Tasks: feat-e2e-material-info

## Task Breakdown

### 1. 物料信息测试 — `e2e/material/material.spec.ts`
- [x] 物料信息列表加载测试 — 断言 .el-table 可见 — Scenario: 物料信息列表加载
- [x] 新建物料（基本信息）— 填写编码/名称，选择分类/属性分类/单位，断言成功 — Scenario: 新建物料（基本信息）
- [x] 新建物料（含 PCB 属性）— 填写基本字段 + PCB 属性区，断言成功 — Scenario: 新建物料含 PCB 属性
- [x] 物料必填校验 — 空表单提交，断言校验提示 — Scenario: 物料编码必填校验
- [x] 编辑物料测试 — 修改名称，断言更新 + 编码只读 — Scenario: 编辑物料
- [x] 删除物料测试 — 删除 + 确认，断言消失 — Scenario: 删除物料

### 2. 免检清单测试 — `e2e/material/inspection-exemption.spec.ts`
- [x] 免检清单列表加载测试 — 断言 .el-table 可见 — Scenario: 免检清单列表加载
- [x] 新建免检记录测试 — 选择物料和供应商，断言成功 — Scenario: 新建免检记录
- [x] 删除免检记录测试 — 删除 + 确认，断言消失 — Scenario: 删除免检记录

### 3. 数据清理
- [x] afterEach 清理测试数据（反向依赖：免检 → 物料）— 测试各自创建并删除数据，无需 afterEach

## Progress Log
| Date | Progress | Notes |
|------|----------|-------|
| 2026-04-22 | Feature 创建 | Split from feat-e2e-material |
| 2026-04-22 | 实现完成 | material.spec.ts (6 tests) + inspection-exemption.spec.ts (3 tests) |
