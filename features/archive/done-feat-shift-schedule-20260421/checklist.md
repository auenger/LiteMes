# Checklist: feat-shift-schedule 班制班次管理

## Implementation Checklist
- [x] ShiftSchedule entity defined with soft delete and audit fields
- [x] Shift entity defined with LocalTime fields and cross-day support
- [x] Repository interfaces with query methods
- [x] MyBatis-Plus repository implementations with pagination
- [x] ShiftScheduleService with CRUD, uniqueness validation, default schedule logic
- [x] ShiftService with CRUD, cross-day auto-detection, work hour calculation
- [x] REST API endpoints for shift schedules (paginated query, CRUD, status toggle)
- [x] REST API endpoints for shifts (nested under schedule, CRUD)
- [x] DTOs with validation annotations (create, update, response DTOs)
- [x] DDL for shift_schedule and shift tables
- [x] Frontend API module (shiftSchedule.ts)
- [x] Frontend ShiftSchedule list page with CRUD, search, pagination
- [x] Frontend shift sub-table with CRUD dialogs
- [x] Route added for /shift-schedule
- [ ] Excel import service (deferred to feat-enterprise-common)
- [ ] Excel export service (deferred to feat-enterprise-common)
- [ ] Change log recording (deferred to feat-enterprise-common)

## Verification Record

| Date | Status | Results | Evidence |
|------|--------|---------|----------|
| 2026-04-21 | PASSED | 11/16 tasks complete, 7/7 tests pass, 5/7 Gherkin scenarios pass, 2 deferred | evidence/verification-report.md, evidence/test-results.json |
