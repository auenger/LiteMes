# Tasks: feat-work-center 工作中心管理

## 任务清单

### 1. 数据层 (domain + infrastructure)
- [x] 定义 WorkCenter 实体（继承 SoftDeleteEntity）— 场景 1
- [x] 创建 Flyway 迁移脚本（work_center 表 + 唯一索引 + 外键）— 场景 2
- [x] 定义 WorkCenterMapper（继承 BaseMapper<WorkCenter>）
- [x] 定义 WorkCenterRepository 接口（domain 层）
- [x] 实现 WorkCenterRepositoryImpl（infrastructure 层）

### 2. 应用服务层 (application)
- [x] 定义 DTO：
  - [x] WorkCenterCreateDto（编码、名称、工厂ID）
  - [x] WorkCenterUpdateDto（名称）
  - [x] WorkCenterQueryDto（编码/名称模糊 + 工厂ID + 状态）
  - [x] WorkCenterVo（含工厂名称关联展示）
- [x] 实现 WorkCenterService：
  - [x] 创建工作中心（编码唯一性校验 + 工厂存在性校验）— 场景 1, 2, 10
  - [x] 编辑工作中心（名称更新，编码不可改）— 场景 3
  - [x] 删除工作中心（引用检查：工序/数据权限）— 场景 4, 5
  - [x] 分页查询（编码/名称模糊 + 工厂筛选 + 状态筛选）— 场景 6, 7, 8
  - [x] 状态切换（启用/禁用）— 场景 9

### 3. API 层 (web)
- [x] 实现 WorkCenterResource：
  - [x] POST /api/work-centers — 创建工作中心
  - [x] PUT /api/work-centers/{id} — 编辑工作中心
  - [x] DELETE /api/work-centers/{id} — 删除工作中心
  - [x] GET /api/work-centers — 分页查询工作中心列表
  - [x] GET /api/work-centers/{id} — 获取工作中心详情
  - [x] PUT /api/work-centers/{id}/status — 状态切换

### 4. 前端 (frontend)
- [x] 工作中心列表页面（查询/重置/创建/编辑/删除/状态切换）
- [x] 创建弹窗（编码、名称、所属工厂下拉）
- [x] 编辑弹窗（名称，编码只读，工厂只读）
- [x] 状态切换确认提示

### 5. 测试
- [x] WorkCenterService 单元测试（CRUD + 唯一性 + 引用检查 + 状态管理）
- [x] WorkCenterResource 集成测试

### 6. 通用
- [x] 审计字段自动填充（MetaObjectHandler）— 已有基础设施
- [x] 软删除标记（@TableLogic）— 已有基础设施

---

## 进度记录
| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-21 | Feature 创建 | 从 feat-enterprise-org 新增，支撑数据权限模块 |
| 2026-04-21 | Spec enriched | 价值点: 3, 场景: 10, 任务: 6类共22项 |
| 2026-04-22 | 后端+前端实现完成 | domain/infrastructure/application/web 四层 + Vue 前端 |
| 2026-04-22 | 测试验证通过 | 14/14 集成测试通过，10/10 Gherkin 场景验证 |
