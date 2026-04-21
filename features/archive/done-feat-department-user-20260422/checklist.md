# Checklist: feat-department-user 部门用户关系

## 后端
- [x] 实体 DepartmentUser 定义 (extends BaseEntity, no soft delete)
- [x] User 实体 (minimal, for FK reference)
- [x] DepartmentUserRepository 接口与实现
- [x] UserRepository 接口与实现
- [x] DepartmentUserService (assign, remove, query)
- [x] DepartmentUserResource REST API (4 endpoints)
- [x] DDL: user + department_user tables in schema.sql
- [x] DepartmentService.delete() cascade-deletes DepartmentUser

## 前端
- [x] departmentUser.ts API module
- [x] DepartmentUserList.vue (user list, select dialog, remove)
- [x] Route: /departments/:id/users
- [x] DepartmentList.vue "用户" button added

## 验证
- [x] Maven compile passed
- [x] All 6 Gherkin scenarios validated
- [x] No new test failures introduced

---

## Verification Record

| Date | Status | Result | Evidence |
|------|--------|--------|----------|
| 2026-04-22 | PASSED | 10/10 tasks, 6/6 scenarios, 15/15 non-preexisting tests | evidence/verification-report.md |
