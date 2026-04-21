# Checklist: feat-factory (工厂管理)

## Implementation
- [x] Factory entity defined (extends SoftDeleteEntity)
- [x] FactoryRepository interface and implementation
- [x] FactoryService with CRUD, uniqueness check, reference validation
- [x] FactoryResource REST API endpoints
- [x] FactoryCreateDto, FactoryUpdateDto, FactoryDto, FactoryQueryDto
- [x] Factory table DDL in schema.sql

## Frontend
- [x] Factory API module (frontend/src/api/factory.ts)
- [x] Factory list page with search/filter
- [x] Create dialog with company dropdown
- [x] Edit dialog (factory code disabled)
- [x] Delete confirmation dialog
- [x] Enable/disable toggle
- [x] Route registered (/factories)

## Business Rules
- [x] Factory code is immutable after creation
- [x] Company must be selected when creating factory
- [x] Factory code must be unique
- [x] Soft delete support
- [x] Audit fields auto-filled
- [x] Company name enriched in response DTO

## Verification Record
- **Date**: 2026-04-22
- **Status**: PASSED
- **Unit Tests**: 7/7 passed
- **Gherkin Scenarios**: 4 passed, 1 partial (department reference check deferred)
- **Evidence**: features/active-feat-factory/evidence/
