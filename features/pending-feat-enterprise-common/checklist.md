# Checklist: feat-enterprise-common

## Pre-Implementation
- [x] Spec reviewed and understood
- [x] Dependencies satisfied
- [x] Implementation plan created

## Implementation
- [x] Backend: AuditLog entity + DDL
- [x] Backend: AuditLogRepository + Mapper + Impl
- [x] Backend: AuditLogService (logCreate/logUpdate/logDelete + query)
- [x] Backend: AuditLogResource (REST API)
- [x] Backend: DropdownItem DTO
- [x] Backend: DropdownService (company/factory/department/shift-schedule + cascade)
- [x] Backend: DropdownResource (REST API)
- [x] Backend: Repository findAllActive/findByCompanyId methods
- [x] Frontend: auditLog.ts + dropdown.ts API modules
- [x] Frontend: AuditLogDialog.vue reusable component
- [x] Frontend: useTableSettings.ts composable + TableSettingsPanel.vue
- [x] Frontend: Integrated into CompanyList, FactoryList, WorkCenterList, DepartmentList

## Verification
- [x] Backend compiles (mvnw compile)
- [x] Frontend type-checks (vue-tsc --noEmit)
- [x] All tasks completed (15/15)
- [x] Gherkin scenarios validated (10/10)

## Verification Record

| Date | Status | Summary |
|------|--------|---------|
| 2026-04-22 | PASSED | 15/15 tasks done, 10/10 scenarios verified, pre-existing test failures unrelated |
