# Tasks: feat-process 工序管理

## 任务清单

### 1. 数据层 (domain + infrastructure)
- [x] 定义 Process 实体（继承 SoftDeleteEntity）— 场景 1
- [x] 创建数据库迁移脚本（process 表 + 唯一索引 + 外键）— 场景 2
- [x] 定义 ProcessMapper（继承 BaseMapper<Process>）
- [x] 定义 ProcessRepository 接口（domain 层）
- [x] 实现 ProcessRepositoryImpl（infrastructure 层，含工厂级联查询）

### 2. 应用服务层 (application)
- [x] 定义 DTO：
  - [x] ProcessCreateDto（编码、名称、工作中心ID）
  - [x] ProcessUpdateDto（名称）
  - [x] ProcessQueryDto（编码/名称模糊 + 工作中心ID + 工厂ID + 状态）
  - [x] ProcessDto（含工作中心名称、工厂名称关联展示）
- [x] 实现 ProcessService：
  - [x] 创建工序（编码唯一性校验 + 工作中心存在性校验）— 场景 1, 2, 10
  - [x] 编辑工序（名称更新，编码不可改）— 场景 3
  - [x] 删除工序（引用检查：数据权限关联）— 场景 4, 5
  - [x] 分页查询（编码/名称模糊 + 工作中心筛选 + 工厂级联筛选 + 状态筛选）— 场景 6, 7, 8, 9
  - [x] 状态切换（启用/禁用）

### 3. API 层 (web)
- [x] 实现 ProcessResource：
  - [x] POST /api/processes — 创建工序
  - [x] PUT /api/processes/{id} — 编辑工序
  - [x] DELETE /api/processes/{id} — 删除工序
  - [x] GET /api/processes — 分页查询工序列表
  - [x] GET /api/processes/{id} — 获取工序详情
  - [x] PUT /api/processes/{id}/status — 状态切换

### 4. 前端 (frontend)
- [ ] 工序列表页面（查询/重置/创建/编辑/删除/状态切换）
- [ ] 创建弹窗（编码、名称、工厂下拉 → 工作中心联动下拉）— 场景 11
- [ ] 编辑弹窗（名称可编辑，编码和工作中心只读）
- [ ] 工厂-工作中心级联选择器组件（复用 feat-enterprise-common 的级联接口）

### 5. 测试
- [ ] ProcessService 单元测试（CRUD + 唯一性 + 引用检查 + 状态管理 + 级联查询）
- [x] ProcessResource 集成测试

### 6. 通用
- [x] 审计字段自动填充（MetaObjectHandler）— 已有基础设施
- [x] 软删除标记（@TableLogic）— 继承 SoftDeleteEntity

---

## 进度记录
| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-21 | Feature 创建 | 从 feat-enterprise-org 新增，支撑数据权限模块 |
| 2026-04-21 | Spec enriched | 价值点: 3, 场景: 11, 任务: 6类共22项 |
