# Checklist: feat-department 部门管理

## Pre-Implementation
- [x] Spec reviewed and understood
- [x] Dependencies verified (feat-factory completed)
- [x] Reference code analyzed (Factory, Company patterns)

## Backend Implementation
- [x] Department entity defined (extends SoftDeleteEntity)
- [x] DepartmentRepository interface created
- [x] DepartmentMapper (MyBatis-Plus BaseMapper) created
- [x] DepartmentRepositoryImpl implemented
- [x] DepartmentCreateDto with validation annotations
- [x] DepartmentDto with enriched fields (factoryName, parentName)
- [x] DepartmentQueryDto with pagination
- [x] DepartmentUpdateDto (code immutable)
- [x] DepartmentService with CRUD + validation + reference checks
- [x] DepartmentResource REST API endpoints
- [x] FactoryService updated with Department reference check
- [x] schema.sql updated with department table DDL

## Frontend Implementation
- [x] department.ts API module created
- [x] DepartmentList.vue with full CRUD UI
- [x] Factory dropdown selection in create/edit dialog
- [x] Parent department selection (filtered by factory)
- [x] Search by code/name, filter by factory/status
- [x] Delete confirmation with reference check messaging
- [x] Status toggle (enable/disable)
- [x] Router updated with /departments route

## Verification
- [x] Backend compiles without errors
- [x] Frontend type-checks without errors
- [x] All existing tests pass (7/7)
- [x] Gherkin scenarios validated via code analysis (6/6)

## Verification Record

| Date | Status | Summary |
|------|--------|---------|
| 2026-04-22 | PASS | All tasks complete, 7/7 tests pass, 6/6 Gherkin scenarios validated |

Evidence: `features/active-feat-department/evidence/verification-report.md`
