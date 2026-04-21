# Checklist: feat-company 公司管理

## Implementation
- [x] Company entity (domain layer)
- [x] CompanyMapper (infrastructure layer)
- [x] CompanyRepository interface (domain layer)
- [x] CompanyRepositoryImpl (infrastructure layer)
- [x] Company DTOs (web layer: CompanyDto, CompanyCreateDto, CompanyUpdateDto, CompanyQueryDto, PagedResult)
- [x] CompanyService (application layer)
- [x] CompanyResource (web layer)
- [x] SQL DDL (company table in schema.sql)
- [x] Frontend API module (company.ts)
- [x] Company list page with CRUD, query, enable/disable
- [x] Router updated with /companies route
- [ ] 变更日志记录 (deferred to feat-enterprise-common)

## Verification
- [x] Backend compilation passes
- [x] Frontend TypeScript type-check passes
- [x] All existing tests pass (7/7)
- [x] No regressions introduced

## Gherkin Scenarios
- [x] Scenario 1: 创建公司
- [x] Scenario 2: 创建公司编码重复
- [x] Scenario 3: 编辑公司
- [x] Scenario 4: 公司编码不可修改
- [x] Scenario 5: 删除未引用公司
- [ ] Scenario 6: 删除被引用公司 (deferred: depends on feat-factory)
- [x] Scenario 7: 模糊查询公司
- [x] Scenario 8: 按状态筛选

## Known Deferrals
1. Factory reference check (delete/disable) -- TODO in CompanyService, to be implemented in feat-factory
2. 变更日志记录 -- Cross-cutting concern, to be implemented in feat-enterprise-common

---

## Verification Records

| Date | Status | Tests | Scenarios | Notes |
|------|--------|-------|-----------|-------|
| 2026-04-21 | PASSED | 7/7 | 7/8 (1 deferred) | All quality checks pass, 1 scenario deferred pending feat-factory |
