# Checklist: feat-data-permission 数据权限控制

## 完成检查清单（模块级）

### 前置依赖确认
- [ ] Factory 实体已在 feat-factory 中定义并可引用
- [ ] User 实体已在 feat-department-user 中定义并可引用
- [ ] WorkCenter 实体已有对应 Feature 并定义（**关键阻塞项**）
- [ ] Process 实体已有对应 Feature 并定义（**关键阻塞项**）

### 子 Feature 完成检查
- [ ] [feat-permission-group](../pending-feat-permission-group/checklist.md) 全部检查项通过
- [ ] [feat-user-permission](../pending-feat-user-permission/checklist.md) 全部检查项通过

### 模块集成检查
- [ ] 全局数据权限拦截器（MyBatis-Plus）正确注册和生效
- [ ] 权限缓存（Redis）登录时加载、变更时刷新
- [ ] 权限组删除保护（引用检查）生效
- [ ] source 字段（GROUP/DIRECT）逻辑正确
- [ ] 无权限组用户（group_id = NULL）的权限正常工作
- [ ] 超级管理员豁免逻辑（如需要）已实现

### 技术一致性
- [ ] 无 SqlSugar 残留引用（统一使用 MyBatis-Plus）
- [ ] 四层架构规范遵守
- [ ] 审计字段自动填充
- [ ] 软删除标记已应用

### 测试
- [ ] 全局拦截器端到端集成测试通过
- [ ] 批量授权 → 缓存刷新 → 查询过滤 链路测试通过
- [ ] 权限组删除保护测试通过

### 文档
- [ ] spec.md 中待决事项已全部解决
- [ ] 整体数据模型关系图与实际一致

### 提交准备
- [ ] 变更已暂存
- [ ] 准备好提交信息
