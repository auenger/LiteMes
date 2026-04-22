# Feature: feat-e2e-master-data 基础数据模块 E2E 测试

## Basic Information
- **ID**: feat-e2e-master-data
- **Name**: 基础数据模块 E2E 测试
- **Priority**: 70
- **Size**: L
- **Dependencies**: []
- **Parent**: null
- **Children**: [feat-e2e-org, feat-e2e-production-base, feat-e2e-material, feat-e2e-equipment, feat-e2e-supply-chain, feat-e2e-permission]
- **Created**: 2026-04-22

## Description

对基础数据模块全部 17 个功能页面进行 Playwright E2E 测试覆盖，按业务模块拆分为 6 个子 Feature 并行开发。测试范围包括页面加载、表格渲染、CRUD 操作、表单校验、数据联动等核心用户场景。

### 覆盖模块
1. 组织架构（公司/工厂/部门/班制班次）
2. 生产基础（工作中心/计量单位/单位换算）
3. 物料管理（物料分类/物料信息/免检清单）
4. 设备管理（设备类型/设备型号/设备台账）
5. 供应链（客户/供应商）
6. 数据权限（权限组/用户权限）

### 报告归档
Playwright 配置已更新，测试报告输出至 `frontend/evidence/` 目录：
- HTML 报告：`evidence/playwright-report/`
- JSON 结果：`evidence/test-results.json`
- 失败截图/视频：`evidence/test-artifacts/`

## User Value Points
1. 自动化验证所有基础数据页面的核心功能（CRUD + 列表渲染）
2. 回归测试保障：每次前端变更后快速验证所有页面
3. 测试报告归档：产出可追溯的测试证据

## Context Analysis
### Reference Code
- 现有冒烟测试：`frontend/e2e/auth.spec.ts`, `frontend/e2e/company-crud.spec.ts`
- Playwright 配置：`frontend/playwright.config.ts`
- 测试登录模式：admin/admin123，登录后 localStorage 存储 token

### Related Features
- feat-e2e-smoke-test（已完成，基础冒烟测试）
- 所有基础数据模块后端 Feature（均已完成）

## Technical Solution
- 测试框架：Playwright 1.59+
- 测试目录：`frontend/e2e/`
- 公共登录 fixture：复用已有登录逻辑
- 每个模块一个独立 spec 文件
- 测试数据清理：每个测试用例使用唯一编码，测试后清理

## Acceptance Criteria (Gherkin)

### User Story
作为开发者，我希望所有基础数据页面都有 E2E 测试覆盖，以便在代码变更后快速验证功能完整性。

### Scenarios (Given/When/Then)

```gherkin
Feature: 基础数据模块 E2E 测试

  Background:
    Given 系统运行在 localhost:3000
    And 数据库已初始化默认管理员 admin/admin123

  Scenario: 全部 E2E 测试通过
    When 执行 npx playwright test
    Then 6 个模块共 17 个页面的测试全部通过
    And HTML 报告生成在 evidence/playwright-report/
    And JSON 结果生成在 evidence/test-results.json
```

### General Checklist
- [ ] 每个页面至少覆盖：列表加载、新建、编辑、删除
- [ ] 表单校验场景（必填项、格式校验）
- [ ] 测试数据互不干扰（唯一编码前缀）
- [ ] 报告归档到 evidence 目录
