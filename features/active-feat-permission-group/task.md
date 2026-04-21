# Tasks: feat-permission-group 数据权限组管理

## 任务清单

### 1. 数据层 (domain + infrastructure)
- [x] 定义 DataPermissionGroup 实体（继承 BaseEntity，含 @TableLogic 软删除）— 场景 1, 10
- [x] 定义 DataPermissionGroupFactory 实体 — 场景 4, 8
- [x] 定义 DataPermissionGroupWorkCenter 实体 — 场景 5
- [x] 定义 DataPermissionGroupProcess 实体 — 场景 6
- [x] 创建 4 张表的 DDL 脚本（含唯一索引、外键）— 场景 11
- [x] 定义 DataPermissionGroupMapper（继承 BaseMapper）
- [x] 定义 DataPermissionGroupFactoryMapper / WorkCenterMapper / ProcessMapper
- [x] 定义 DataPermissionGroupRepository 接口（domain 层）
- [x] 实现 DataPermissionGroupRepositoryImpl（infrastructure 层，含关联表的批量保存/删除）

### 2. 应用服务层 (application)
- [x] 定义 DataPermissionGroupCreateDto / UpdateDto / QueryDto / Vo — 场景 1, 3, 12
- [x] 定义 AssociatedItemDto / AssociatedEntityDto（关联保存 DTO）
- [x] 实现 DataPermissionGroupService：
  - [x] 创建权限组（名称唯一性校验）— 场景 1, 2
  - [x] 编辑权限组（名称更新）— 场景 3
  - [x] 删除权限组（级联删除关联）— 场景 9, 10
  - [x] 分页查询权限组（名称模糊查询）— 场景 12
  - [x] 保存工厂关联（全量替换策略）— 场景 4, 7, 8
  - [x] 保存工作中心关联（同上策略）— 场景 5
  - [x] 保存工序关联（同上策略）— 场景 6
  - [x] 查询已关联工厂/工作中心/工序列表
- [x] 添加 WorkCenter / Process 的下拉接口支持（DropdownService + DropdownResource）

### 3. API 层 (web)
- [x] 实现 DataPermissionGroupResource：
  - [x] POST /api/data-permission-groups — 创建权限组
  - [x] PUT /api/data-permission-groups/{id} — 编辑权限组
  - [x] DELETE /api/data-permission-groups/{id} — 删除权限组
  - [x] GET /api/data-permission-groups — 分页查询权限组列表
  - [x] GET /api/data-permission-groups/{id} — 获取权限组详情
  - [x] GET /api/data-permission-groups/{id}/factories — 获取已关联工厂
  - [x] POST /api/data-permission-groups/{id}/factories — 保存工厂关联
  - [x] GET /api/data-permission-groups/{id}/work-centers — 获取已关联工作中心
  - [x] POST /api/data-permission-groups/{id}/work-centers — 保存工作中心关联
  - [x] GET /api/data-permission-groups/{id}/processes — 获取已关联工序
  - [x] POST /api/data-permission-groups/{id}/processes — 保存工序关联

### 4. 前端 (frontend)
- [x] 数据权限组列表页面（查询/重置/创建/编辑/删除）— 场景 1, 12
- [x] 创建/编辑弹窗（权限组名称 + 备注）— 场景 1, 3
- [x] 工厂 Tab 页（选择工厂，多选勾选）— 场景 4, 7
- [x] 工作中心 Tab 页（选择工作中心，多选勾选）— 场景 5
- [x] 工序 Tab 页（选择工序，多选勾选）— 场景 6
- [x] 删除按钮状态控制（引用检查后置灰）— 场景 9

### 5. 测试
- [x] DataPermissionGroupResource 集成测试（CRUD + 名称唯一性 + 关联管理 + 删除）

### 6. 通用
- [x] 审计字段自动填充（BaseEntity 已支持）
- [x] 引用检查接口（referenced 字段预留，待 feat-user-permission 实现后补充）

---

## 进度记录
| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-21 | 已拆分 | 从 feat-data-permission 拆分 |
| 2026-04-21 | Spec enriched | 价值点: 3, 场景: 12, 任务: 6类共31项 |
| 2026-04-22 | 实现完成 | 全部任务实现，含后端4层+前端+测试 |
