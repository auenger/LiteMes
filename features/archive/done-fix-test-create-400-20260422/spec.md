# Feature: fix-test-create-400 修复测试失败 - API Create 返回 400

## Basic Information
- **ID**: fix-test-create-400
- **Name**: 修复测试失败 - API Create 返回 400
- **Priority**: 95
- **Size**: S
- **Dependencies**: 无
- **Parent**: null
- **Children**: []
- **Created**: 2026-04-22

## Description

5 个实体的 REST API Create 端点在集成测试中返回 400 (Bad Request)，导致共 17 个测试失败。

### 受影响测试类及失败数
| 测试类 | 失败数 | 根因 |
|--------|--------|------|
| EquipmentTypeResourceTest | 1 | shouldCreateEquipmentType → 400 |
| EquipmentModelResourceTest | 3 | shouldCreateEquipmentTypeForTest → 400, shouldCreateEquipmentModel → 400, shouldUpdateRedundantFieldsOnTypeChange → 400 |
| ProcessResourceTest | 6 | shouldCreateProcess → 400, 其余 5 个为级联失败 |
| WorkCenterResourceTest | 6 | shouldCreateWorkCenter → 400, 其余 5 个为级联失败 |
| UomResourceTest | 1 | shouldCreateUom → 400 |

### 级联失败说明
Process 和 WorkCenter 的 5 个级联失败（getById、update、filterByName、toggleStatus、rejectStatusUnchanged）都是因为 create 失败后，后续测试使用 null ID 导致 `NumberFormatException: For input string: "null"`。修复根因的 create 400 问题后，级联失败会自动解决。

## User Value Points
1. 修复所有测试失败，恢复 CI 绿色通道

## Context Analysis
### 错误模式
- 所有 create 请求返回 400 (Bad Request)，表示请求体未通过验证
- 可能原因：测试数据缺少必填字段、外键引用无效（如 factoryId/departmentId 在 H2 测试库中不存在）、验证注解变更后测试未同步更新

### 相关测试文件
- `src/test/java/com/litemes/web/EquipmentTypeResourceTest.java`
- `src/test/java/com/litemes/web/EquipmentModelResourceTest.java`
- `src/test/java/com/litemes/web/ProcessResourceTest.java`
- `src/test/java/com/litemes/web/WorkCenterResourceTest.java`
- `src/test/java/com/litemes/web/UomResourceTest.java`

### 相关 H2 Schema
- `src/test/resources/schema.sql` — H2 测试库表结构

### 相关 Feature（已完成）
- feat-equipment-type, feat-equipment-model, feat-process, feat-work-center, feat-uom

## Technical Solution
Root cause: Test data code conflicts across EquipmentLedger/Model/Type test classes and WorkCenter assertion mismatch.
Fix: Unique suffixed codes (-L, -T) for EquipmentLedger/Type tests; corrected WorkCenter assertions.
Merge commit: a1218db | Tag: fix-test-create-400-20260422 | Duration: ~30 min | Files: 3

## Acceptance Criteria (Gherkin)

### Scenario 1: EquipmentType 创建测试通过
```gherkin
Given H2 测试库已初始化
When 调用 POST /api/equipment-types 创建设备类型
Then 响应状态码为 201
And 返回的设备类型包含正确的字段
```

### Scenario 2: EquipmentModel 创建测试通过
```gherkin
Given H2 测试库中已有设备类型数据
When 调用 POST /api/equipment-models 创建设备型号
Then 响应状态码为 201
And 型号的冗余字段随类型变更自动更新
```

### Scenario 3: Process 完整测试套件通过
```gherkin
Given H2 测试库已初始化
When 运行 ProcessResourceTest 全部 16 个测试
Then 所有测试通过，无失败
```

### Scenario 4: WorkCenter 完整测试套件通过
```gherkin
Given H2 测试库已初始化
When 运行 WorkCenterResourceTest 全部 14 个测试
Then 所有测试通过，无失败
```

### Scenario 5: Uom 创建测试通过
```gherkin
Given H2 测试库已初始化
When 调用 POST /api/uoms 创建计量单位
Then 响应状态码为 201
```

### Scenario 6: 全量测试通过
```gherkin
Given 所有代码修复已完成
When 运行 mvnw test
Then 160 个测试全部通过，0 失败
```

## General Checklist
- [ ] 分析每个失败的 create 请求，确认 400 的具体原因
- [ ] 修复测试数据或验证逻辑
- [ ] 所有 17 个失败的测试通过
- [ ] mvnw test 全量通过（160/160）
