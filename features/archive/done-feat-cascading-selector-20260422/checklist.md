# Checklist: feat-cascading-selector

## 完成检查

### 开发
- [x] CascadingSelector.vue 已创建
- [x] 级联加载逻辑正确
- [x] v-model 双向绑定正常

### 代码质量
- [x] 使用 Element Plus 组件
- [x] 组件可复用（Props/Emits 设计合理）
- [x] TypeScript 类型正确

### 测试
- [x] 公司→工厂级联正常
- [x] 工厂→部门级联正常
- [x] 上级变更时下级清空
- [x] 清空操作正常

### 文档
- [x] spec.md 技术方案已填写

## Verification Record

| 日期 | 状态 | 结果 | 证据 |
|------|------|------|------|
| 2026-04-22 | PASS | 2/2 Gherkin 场景通过，TypeScript + Build 通过 | evidence/verification-report.md |
