# Checklist: feat-e2e-org-base

## Completion Checklist

### Development
- [x] 公共 auth fixture (`e2e/fixtures/auth.ts`) 创建并可被导入
- [x] 公共工具函数 (`e2e/helpers/common.ts`) 创建
- [x] 2 个 spec 文件编写完成（company/factory）
- [x] 代码自测通过

### Code Quality
- [x] 测试命名规范统一（test.describe + test 描述清晰）
- [x] 测试数据隔离（E2E_ 前缀 + 时间戳）
- [x] 无硬编码等待（使用 waitFor/toBeVisible）
- [x] 选择器稳定（优先 placeholder，避免 CSS 类名依赖）

### Testing — 公司管理
- [x] 列表加载 + 分页可见
- [x] 新建公司（编码/名称/简码）
- [x] 编码必填校验
- [x] 编辑公司（名称可改，编码只读）
- [x] 删除公司

### Testing — 工厂管理
- [x] 列表加载
- [x] 新建工厂（关联公司选择器）
- [x] 必填校验
- [x] 编辑工厂（编码只读）
- [x] 删除工厂

### Cleanup
- [x] 测试可重复执行（唯一时间戳数据，无冲突）

## Verification Record
- **Date**: 2026-04-22
- **Status**: PASSED (10/10 scenarios)
- **Evidence**: `features/active-feat-e2e-org-base/evidence/verification-report.md`
