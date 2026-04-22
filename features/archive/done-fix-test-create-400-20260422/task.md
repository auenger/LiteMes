# Tasks: fix-test-create-400
## Task Breakdown

### 1. 问题诊断
- [x] 分析 EquipmentTypeResourceTest.shouldCreateEquipmentType 失败原因（读取测试代码、DTO、Resource、Service）
- [x] 分析 UomResourceTest.shouldCreateUom 失败原因
- [x] 分析 ProcessResourceTest.shouldCreateProcess 失败原因
- [x] 分析 WorkCenterResourceTest.shouldCreateWorkCenter 失败原因

### 2. 修复实施
- [x] 修复 EquipmentType 相关测试（1 个根因 + EquipmentModel 3 个级联）
- [x] 修复 Uom 相关测试（1 个根因）
- [x] 修复 Process 相关测试（1 个根因 + 5 个级联）
- [x] 修复 WorkCenter 相关测试（1 个根因 + 5 个级联）

### 3. 验证
- [x] 运行 mvnw test 全量测试通过（160/160）

## Progress Log
| Date | Progress | Notes |
|------|----------|-------|
| 2026-04-22 | 创建 | 通过 /verify-feature 发现 17 个测试失败 |
| 2026-04-22 | 完成 | 根因：测试间编码冲突。EquipmentLedgerResourceTest 使用 DRILL-L/CNC-500-L/PRESS-L，EquipmentTypeResourceTest 使用 DRILL-T，WorkCenter 断言修正为 WC-TEST-001。160/160 全部通过 |
