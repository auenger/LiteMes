# Tasks: fix-audit-serialization
## Task Breakdown

### 1. 问题诊断
- [x] 读取 AuditLogService.java，定位序列化代码和 ObjectMapper 使用方式
- [x] 检查 pom.xml 是否已包含 jackson-datatype-jsr310 依赖
- [x] 检查 application.yml 中 Quarkus Jackson 配置

### 2. 修复实施
- [x] 在 ObjectMapper 中注册 JavaTimeModule 或启用 Quarkus 配置 `quarkus.jackson.register-java-time-module=true`

### 3. 验证
- [x] 运行 EquipmentLedgerResourceTest，确认日志中无序列化错误
- [x] 运行 InspectionExemptionResourceTest，确认日志中无序列化错误

## Progress Log
| Date | Progress | Notes |
|------|----------|-------|
| 2026-04-22 | 创建 | 通过 /verify-feature 发现审计日志序列化错误 |
| 2026-04-22 | 诊断完成 | AuditLogService 通过 CDI 注入 ObjectMapper，缺少 JavaTimeModule 注册 |
| 2026-04-22 | 修复完成 | 在 application.yml 添加 quarkus.jackson.register-java-time-module=true 和 write-dates-as-timestamps=false |
