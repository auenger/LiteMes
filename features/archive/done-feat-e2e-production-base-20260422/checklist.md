# Checklist: feat-e2e-production-base

## Completion Checklist

### Development
- [x] 3 个 spec 文件编写完成（work-center/uom/uom-conversion）
- [x] 复用 auth fixture 和公共工具函数
- [x] 代码自测通过

### Code Quality
- [x] 测试命名规范统一
- [x] 测试数据隔离（E2E_PROD_ 前缀）
- [x] 无硬编码等待

### Testing -- 工作中心
- [x] 列表加载 + 分页
- [x] 新建工作中心（关联工厂选择器）
- [x] 必填校验
- [x] 编辑（编码只读）
- [x] 删除

### Testing -- 计量单位
- [x] 列表加载
- [x] 新建计量单位（编码/名称/精度 number 输入）
- [x] 必填校验
- [x] 编辑（名称 + 精度可改，编码只读）
- [x] 删除

### Testing -- 单位换算
- [x] 列表加载
- [x] 新建换算关系（源单位/目标单位选择器 + 比例）
- [x] 删除换算关系

### Cleanup
- [x] afterEach 清理测试数据（反向依赖顺序）
- [x] 测试可重复执行

## Verification Record

| Date | Status | Results | Evidence |
|------|--------|---------|----------|
| 2026-04-22 | PASSED | 13/13 tests passed. Fixed bug in WorkCenterList.vue (missing isColumnVisible function). | evidence/verification-report.md, evidence/test-results.json |
