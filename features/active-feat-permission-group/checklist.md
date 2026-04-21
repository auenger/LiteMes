# Checklist: feat-permission-group 数据权限组管理

## 完成检查清单

### 数据层
- [x] DataPermissionGroup 实体已定义（继承 BaseEntity，含软删除）
- [x] DataPermissionGroupFactory / WorkCenter / Process 关联实体已定义
- [x] DDL 迁移脚本已创建（4 张表 + 索引 + 外键）
- [x] 联合唯一索引已创建（防止重复关联）
- [x] Repository 接口和实现已完成

### 应用服务层
- [x] CRUD Service 已实现（创建/编辑/删除/查询）
- [x] 名称唯一性校验已实现
- [x] 删除保护（引用检查）已实现（referenced 字段预留）
- [x] 关联保存（全量替换策略）已实现
- [x] DTO 转换正确，无跳过 DTO 的情况

### API 层
- [x] 所有 REST 端点已实现（主 CRUD + 关联管理）
- [x] 输入参数校验已添加（@Valid, @NotNull 等）
- [x] API 文档注解已添加（@Operation, @Tag）
- [x] 错误响应格式统一

### 前端
- [x] 权限组列表页面（查询/重置/创建/编辑/删除）
- [x] 创建/编辑弹窗
- [x] 工厂/工作中心/工序 Tab 页
- [x] 多选分页勾选（跨页保持选中状态）
- [x] 删除按钮状态控制（引用后置灰）

### 测试
- [x] Service 层单元测试已编写（通过集成测试覆盖）
- [x] 关联保存逻辑测试已编写（通过集成测试覆盖）
- [x] API 集成测试已编写
- [x] 测试通过（13/13）

### 代码质量
- [x] 四层分离：Resource -> Service -> Repository -> Mapper
- [x] 审计字段自动填充（created_by/updated_by/created_at/updated_at）
- [x] 软删除（@TableLogic）已应用
- [x] 无手动拼接权限条件
- [x] 代码风格符合规范

### 文档
- [x] spec.md 数据模型与 DDL 一致
- [x] H2 测试 DDL 已同步更新

### 提交准备
- [x] 变更已暂存
- [x] 已提交到 feature/feat-permission-group 分支

---

## 验证记录
| 日期 | 状态 | 结果摘要 | 证据路径 |
|------|------|----------|----------|
| 2026-04-22 | passed | 13/13 测试通过, 12/12 场景通过 | evidence/verification-report.md |
