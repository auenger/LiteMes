# Feature: fix-audit-serialization 修复 AuditLogService Jackson 序列化错误

## Basic Information
- **ID**: fix-audit-serialization
- **Name**: 修复 AuditLogService Jackson 日期时间序列化错误
- **Priority**: 80
- **Size**: S
- **Dependencies**: 无
- **Parent**: null
- **Children**: []
- **Created**: 2026-04-22

## Description

AuditLogService 在记录审计日志时，序列化包含 Java 8 日期时间类型（LocalDate、LocalDateTime）的 DTO 失败。

### 错误信息
```
Failed to record CREATE audit log: Java 8 date/time type `java.time.LocalDate` not supported by default:
add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" to enable handling
(through reference chain: com.litemes.web.dto.EquipmentLedgerDto["commissioningDate"])
```

### 受影响 DTO
- `EquipmentLedgerDto` — commissioningDate (LocalDate)
- `InspectionExemptionDto` — validFrom (LocalDate), createdAt (LocalDateTime)
- `MaterialCategoryDto` — createdAt (LocalDateTime)

### 影响范围
虽然审计日志记录失败不影响测试通过（Service 捕获了异常），但生产环境中审计日志会丢失，违反项目规范中的"审计日志强制记录"要求。

## User Value Points
1. 审计日志正确记录所有包含日期时间字段的实体变更

## Context Analysis
### 相关文件
- `src/main/java/com/litemes/application/service/AuditLogService.java` — 审计日志服务
- `src/main/resources/application.yml` — Quarkus 配置（可能需要 `quarkus.jackson.register-java-time-module`）
- `pom.xml` — 检查 jackson-datatype-jsr310 依赖

## Technical Solution
在 `application.yml` 中启用 Quarkus Jackson 配置：
- `quarkus.jackson.register-java-time-module: true` — 注册 JavaTimeModule
- `quarkus.jackson.write-dates-as-timestamps: false` — 使用 ISO-8601 格式

## Merge Record
- **Completed**: 2026-04-22T22:10:00+08:00
- **Branch**: feature/fix-audit-serialization
- **Merge Commit**: b2ab282
- **Archive Tag**: fix-audit-serialization-20260422
- **Conflicts**: None
- **Verification**: 36/36 tests passed, 3/3 Gherkin scenarios verified
- **Stats**: 1 file changed, 5 insertions

## Acceptance Criteria (Gherkin)

### Scenario 1: 审计日志正确序列化 LocalDate 字段
```gherkin
Given 实体 DTO 包含 LocalDate 类型字段（如 commissioningDate）
When 执行 CREATE/UPDATE/DELETE 操作触发审计日志
Then 审计日志成功记录，无序列化异常
```

### Scenario 2: 审计日志正确序列化 LocalDateTime 字段
```gherkin
Given 实体 DTO 包含 LocalDateTime 类型字段（如 createdAt）
When 执行 CREATE/UPDATE/DELETE 操作触发审计日志
Then 审计日志成功记录，无序列化异常
```

### Scenario 3: 运行测试无审计日志错误
```gherkin
Given 序列化修复已部署
When 运行 EquipmentLedgerResourceTest 和 InspectionExemptionResourceTest
Then 日志中无 "Failed to record" 错误
```
