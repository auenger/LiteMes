# Tasks: feat-permission-group 数据权限组管理

## 任务清单

### 1. 数据层 (domain + infrastructure)
- [ ] 定义 DataPermissionGroup 实体（继承 BaseEntity，含 @TableLogic 软删除）— 场景 1, 10
- [ ] 定义 DataPermissionGroupFactory 实体 — 场景 4, 8
- [ ] 定义 DataPermissionGroupWorkCenter 实体 — 场景 5
- [ ] 定义 DataPermissionGroupProcess 实体 — 场景 6
- [ ] 创建 4 张表的 Flyway 迁移脚本（含唯一索引、外键）— 场景 11
- [ ] 定义 DataPermissionGroupMapper（继承 BaseMapper）
- [ ] 定义 DataPermissionGroupFactoryMapper / WorkCenterMapper / ProcessMapper
- [ ] 定义 DataPermissionGroupRepository 接口（domain 层）
- [ ] 实现 DataPermissionGroupRepositoryImpl（infrastructure 层，含关联表的批量保存/删除）

### 2. 应用服务层 (application)
- [ ] 定义 DataPermissionGroupCreateDto / UpdateDto / QueryDto / Vo — 场景 1, 3, 12
- [ ] 定义 DataPermissionGroupFactoryDto / WorkCenterDto / ProcessDto（关联保存 DTO）
- [ ] 实现 DataPermissionGroupService：
  - [ ] 创建权限组（名称唯一性校验）— 场景 1, 2
  - [ ] 编辑权限组（名称更新）— 场景 3
  - [ ] 删除权限组（引用检查 + 关联级联删除）— 场景 9, 10
  - [ ] 分页查询权限组（名称模糊查询）— 场景 12
  - [ ] 保存工厂关联（全量替换策略：对比已有关联，删除/新增）— 场景 4, 7, 8
  - [ ] 保存工作中心关联（同上策略）— 场景 5
  - [ ] 保存工序关联（同上策略）— 场景 6
  - [ ] 查询已关联工厂/工作中心/工序列表

### 3. API 层 (web)
- [ ] 实现 DataPermissionGroupResource：
  - [ ] POST /api/data-permission-groups — 创建权限组
  - [ ] PUT /api/data-permission-groups/{id} — 编辑权限组
  - [ ] DELETE /api/data-permission-groups/{id} — 删除权限组
  - [ ] GET /api/data-permission-groups — 分页查询权限组列表
  - [ ] GET /api/data-permission-groups/{id} — 获取权限组详情
  - [ ] GET /api/data-permission-groups/{id}/factories — 获取已关联工厂
  - [ ] POST /api/data-permission-groups/{id}/factories — 保存工厂关联
  - [ ] GET /api/data-permission-groups/{id}/work-centers — 获取已关联工作中心
  - [ ] POST /api/data-permission-groups/{id}/work-centers — 保存工作中心关联
  - [ ] GET /api/data-permission-groups/{id}/processes — 获取已关联工序
  - [ ] POST /api/data-permission-groups/{id}/processes — 保存工序关联

### 4. 前端 (frontend)
- [ ] 数据权限组列表页面（查询/重置/创建/编辑/删除）— 场景 1, 12
- [ ] 创建/编辑弹窗（权限组名称 + 备注）— 场景 1, 3
- [ ] 工厂 Tab 页（选择工厂弹窗，多选分页勾选）— 场景 4, 7
- [ ] 工作中心 Tab 页（选择工作中心弹窗，多选分页勾选）— 场景 5
- [ ] 工序 Tab 页（选择工序弹窗，多选分页勾选）— 场景 6
- [ ] 删除按钮状态控制（引用检查后置灰）— 场景 9

### 5. 测试
- [ ] DataPermissionGroupService 单元测试（CRUD + 名称唯一性 + 删除保护）
- [ ] 关联保存逻辑单元测试（全量替换策略、重复关联防护）
- [ ] DataPermissionGroupResource 集成测试（API 端到端）
- [ ] 多页勾选功能前端测试

### 6. 通用
- [ ] 审计字段自动填充（MetaObjectHandler）
- [ ] 引用检查接口（供删除保护调用，查询 UserDataPermission 中是否有引用）

---

## 进度记录
| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-21 | 已拆分 | 从 feat-data-permission 拆分 |
| 2026-04-21 | Spec enriched | 价值点: 3, 场景: 12, 任务: 6类共31项 |
