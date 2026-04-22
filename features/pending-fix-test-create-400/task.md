# Tasks: fix-test-create-400
## Task Breakdown

### 1. 问题诊断
- [ ] 分析 EquipmentTypeResourceTest.shouldCreateEquipmentType 失败原因（读取测试代码、DTO、Resource、Service）
- [ ] 分析 UomResourceTest.shouldCreateUom 失败原因
- [ ] 分析 ProcessResourceTest.shouldCreateProcess 失败原因
- [ ] 分析 WorkCenterResourceTest.shouldCreateWorkCenter 失败原因

### 2. 修复实施
- [ ] 修复 EquipmentType 相关测试（1 个根因 + EquipmentModel 3 个级联）
- [ ] 修复 Uom 相关测试（1 个根因）
- [ ] 修复 Process 相关测试（1 个根因 + 5 个级联）
- [ ] 修复 WorkCenter 相关测试（1 个根因 + 5 个级联）

### 3. 验证
- [ ] 运行 mvnw test 全量测试通过（160/160）

## Progress Log
| Date | Progress | Notes |
|------|----------|-------|
| 2026-04-22 | 创建 | 通过 /verify-feature 发现 17 个测试失败 |
