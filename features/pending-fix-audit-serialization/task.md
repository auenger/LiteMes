# Tasks: fix-audit-serialization
## Task Breakdown

### 1. 问题诊断
- [ ] 读取 AuditLogService.java，定位序列化代码和 ObjectMapper 使用方式
- [ ] 检查 pom.xml 是否已包含 jackson-datatype-jsr310 依赖
- [ ] 检查 application.yml 中 Quarkus Jackson 配置

### 2. 修复实施
- [ ] 在 ObjectMapper 中注册 JavaTimeModule 或启用 Quarkus 配置 `quarkus.jackson.register-java-time-module=true`

### 3. 验证
- [ ] 运行 EquipmentLedgerResourceTest，确认日志中无序列化错误
- [ ] 运行 InspectionExemptionResourceTest，确认日志中无序列化错误

## Progress Log
| Date | Progress | Notes |
|------|----------|-------|
| 2026-04-22 | 创建 | 通过 /verify-feature 发现审计日志序列化错误 |
