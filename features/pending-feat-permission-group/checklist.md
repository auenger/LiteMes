# Checklist: feat-permission-group 数据权限组管理

## 完成检查清单

### 数据层
- [ ] DataPermissionGroup 实体已定义（继承 BaseEntity，含软删除）
- [ ] DataPermissionGroupFactory / WorkCenter / Process 关联实体已定义
- [ ] Flyway 迁移脚本已创建（4 张表 + 索引 + 外键）
- [ ] 联合唯一索引已创建（防止重复关联）
- [ ] Repository 接口和实现已完成

### 应用服务层
- [ ] CRUD Service 已实现（创建/编辑/删除/查询）
- [ ] 名称唯一性校验已实现
- [ ] 删除保护（引用检查）已实现
- [ ] 关联保存（全量替换策略）已实现
- [ ] DTO 转换正确，无跳过 DTO 的情况

### API 层
- [ ] 所有 REST 端点已实现（主 CRUD + 关联管理）
- [ ] 输入参数校验已添加（@Valid, @NotNull 等）
- [ ] API 文档注解已添加（@Operation, @Tag）
- [ ] 错误响应格式统一

### 前端
- [ ] 权限组列表页面（查询/重置/创建/编辑/删除）
- [ ] 创建/编辑弹窗
- [ ] 工厂/工作中心/工序 Tab 页
- [ ] 多选分页勾选（跨页保持选中状态）
- [ ] 删除按钮状态控制（引用后置灰）

### 测试
- [ ] Service 层单元测试已编写
- [ ] 关联保存逻辑单元测试已编写
- [ ] API 集成测试已编写
- [ ] 测试通过

### 代码质量
- [ ] 四层分离：Resource → Service → Repository → Mapper
- [ ] 审计字段自动填充（created_by/updated_by/created_at/updated_at）
- [ ] 软删除（@TableLogic）已应用
- [ ] 无手动拼接权限条件
- [ ] 代码风格符合规范

### 文档
- [ ] spec.md 数据模型与 DDL 一致
- [ ] Flyway 迁移脚本版本号正确

### 提交准备
- [ ] 变更已暂存
- [ ] 准备好提交信息
