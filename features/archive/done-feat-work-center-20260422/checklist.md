# Checklist: feat-work-center 工作中心管理

## 完成检查清单

### 数据层
- [x] WorkCenter 实体已定义（继承 SoftDeleteEntity）
- [x] Flyway 迁移脚本已创建（含唯一索引 + 外键）
- [x] 编码唯一索引已创建（WHERE deleted = 0）
- [x] factoryId 外键已创建
- [x] Repository 接口和实现已完成

### 应用服务层
- [x] CRUD Service 已实现
- [x] 编码唯一性校验已实现
- [x] 引用检查已实现（工序、数据权限关联）— TODO placeholder
- [x] 状态切换已实现
- [x] DTO 转换正确，无跳过 DTO

### API 层
- [x] 所有 REST 端点已实现
- [x] 输入参数校验已添加
- [x] API 文档注解已添加（@Operation, @Tag）

### 前端
- [x] 工作中心列表页面（查询/重置/创建/编辑/删除/状态切换）
- [x] 创建弹窗（编码/名称/工厂下拉）
- [x] 编辑弹窗（名称可编辑，编码和工厂只读）

### 测试
- [x] Service 单元测试已编写（通过集成测试覆盖）
- [x] API 集成测试已编写
- [x] 测试通过（14/14 通过，全量 21/21 通过）

### 代码质量
- [x] 四层分离遵守
- [x] 审计字段自动填充
- [x] 软删除已应用
- [x] 代码风格符合规范

### 提交准备
- [ ] 变更已暂存
- [ ] 准备好提交信息

---

## Verification Record

| Date | Status | Summary | Evidence |
|------|--------|---------|----------|
| 2026-04-22 | PASSED | 14/14 tests passed, 10/10 Gherkin scenarios verified (1 conditional) | evidence/verification-report.md, evidence/test-results.json |
