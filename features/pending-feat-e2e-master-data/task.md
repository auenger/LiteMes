# Tasks: feat-e2e-master-data

## Task Breakdown

### 1. 公共测试基础设施
- [ ] 创建 E2E 测试公共 fixture（登录、导航、表格断言）
- [ ] 配置 evidence 归档目录

### 2. 子 Feature 开发
- [ ] feat-e2e-org — 组织架构测试
- [ ] feat-e2e-production-base — 生产基础测试
- [ ] feat-e2e-material — 物料管理测试
- [ ] feat-e2e-equipment — 设备管理测试
- [ ] feat-e2e-supply-chain — 供应链测试
- [ ] feat-e2e-permission — 数据权限测试

### 3. 全量验证
- [ ] 执行全部 E2E 测试确认通过
- [ ] 验证报告归档到 evidence 目录

## Progress Log
| Date | Progress | Notes |
|------|----------|-------|
| 2026-04-22 | Feature 创建 | 按 6 模块拆分为子 Feature |
| 2026-04-22 | Spec enriched | 6 子 Feature 全量 enrich：共 46 Gherkin 场景、68 任务项、Archive refs: feat-e2e-smoke-test, feat-auth-frontend |
