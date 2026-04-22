# Tasks: feat-user-permission 用户数据权限

## 任务清单

### 1. 数据层 (domain + infrastructure)
- [ ] 定义 UserDataPermission 实体（继承 BaseEntity，group_id 可为空）— 场景 1, 4
- [ ] 定义 UserDataPermissionFactory 实体（含 source 枚举字段）— 场景 2, 5
- [ ] 定义 UserDataPermissionWorkCenter 实体（含 source 枚举字段）— 场景 3
- [ ] 定义 UserDataPermissionProcess 实体（含 source 枚举字段）— 场景 3
- [ ] 创建 4 张表的 Flyway 迁移脚本（含唯一索引、外键、source 字段）— 场景 11
- [ ] 定义 UserDataPermissionMapper（继承 BaseMapper）
- [ ] 定义 UserDataPermissionFactoryMapper / WorkCenterMapper / ProcessMapper
- [ ] 定义 UserDataPermissionRepository 接口（domain 层）
- [ ] 实现 UserDataPermissionRepositoryImpl（infrastructure 层，含关联表批量操作）

### 2. 应用服务层 (application)
- [ ] 定义 DTO：
  - [ ] UserDataPermissionQueryDto（查询条件：用户名/姓名）
  - [ ] UserDataPermissionVo（列表展示：用户名、姓名、权限组、关联概要）
  - [ ] BatchAssignPermissionDto（批量授权：用户ID列表 + 权限组ID）
  - [ ] DirectAssignDto（直接授权：工厂/工作中心/工序 ID 列表）
  - [ ] UserDataPermissionExportDto / ImportDto（导入导出）
- [ ] 实现 UserDataPermissionService：
  - [ ] 分页查询用户权限列表（关联用户名/姓名模糊查询）— 场景 8
  - [ ] 批量添加权限（多用户 + 权限组 → 事务内批量创建主记录+关联记录）— 场景 1
  - [ ] 用户级直接授权工厂（source = DIRECT，保留已有 GROUP 记录）— 场景 2, 4
  - [ ] 用户级直接授权工作中心 — 场景 3
  - [ ] 用户级直接授权工序 — 场景 3
  - [ ] 删除用户权限（级联删除关联 + 刷新缓存）— 场景 12
  - [ ] 查询用户已关联的工厂/工作中心/工序列表
  - [ ] Excel 导出 — 场景 9
  - [ ] Excel 导入（含错误行提示）— 场景 10

### 3. 全局拦截器（核心）
- [ ] 实现 DataPermissionInterceptor（MyBatis-Plus Interceptor）：
  - [ ] 拦截 SELECT 查询，自动追加权限过滤条件 — 场景 5
  - [ ] 从 SecurityContext 获取当前用户 ID
  - [ ] 从 Redis 缓存读取用户权限范围
  - [ ] 按业务表配置的权限维度（工厂/工作中心/工序）自动追加 WHERE 条件
- [ ] 定义权限过滤配置（哪些表需要过滤，按哪个维度过滤）
- [ ] 注册拦截器到 Quarkus CDI 容器

### 4. 权限缓存
- [ ] 定义权限缓存 Key 规则：`user:permission:{userId}`
- [ ] 实现用户登录时加载权限到 Redis — 场景 6
- [ ] 实现权限变更时主动刷新 Redis 缓存 — 场景 7
- [ ] 定义缓存数据结构（工厂 ID 列表、工作中心 ID 列表、工序 ID 列表）

### 5. API 层 (web)
- [ ] 实现 UserDataPermissionResource：
  - [ ] GET /api/user-data-permissions — 分页查询用户权限列表
  - [ ] POST /api/user-data-permissions/batch-assign — 批量添加权限
  - [ ] DELETE /api/user-data-permissions/{id} — 删除用户权限
  - [ ] GET /api/user-data-permissions/{id}/factories — 获取已关联工厂
  - [ ] POST /api/user-data-permissions/{id}/factories — 直接授权工厂
  - [ ] GET /api/user-data-permissions/{id}/work-centers — 获取已关联工作中心
  - [ ] POST /api/user-data-permissions/{id}/work-centers — 直接授权工作中心
  - [ ] GET /api/user-data-permissions/{id}/processes — 获取已关联工序
  - [ ] POST /api/user-data-permissions/{id}/processes — 直接授权工序
  - [ ] POST /api/user-data-permissions/import — Excel 导入
  - [ ] GET /api/user-data-permissions/export — Excel 导出

### 6. 前端 (frontend)
- [ ] 用户数据权限列表页面（查询/重置/删除）— 场景 8
- [ ] 批量添加权限弹窗（勾选用户 → 选择权限组 → 确认）— 场景 1
- [ ] 工厂 Tab 页（直接授权工厂弹窗，多选分页勾选）— 场景 2
- [ ] 工作中心 Tab 页（直接授权工作中心弹窗，多选分页勾选）— 场景 3
- [ ] 工序 Tab 页（直接授权工序弹窗，多选分页勾选）— 场景 3
- [ ] Excel 导入/导出功能 — 场景 9, 10
- [ ] 权限来源标识（GROUP/DIRECT 用不同标签展示）

### 7. 测试
- [ ] UserDataPermissionService 单元测试（批量授权、直接授权、source 标记）
- [ ] DataPermissionInterceptor 单元测试（SQL 拦截和条件注入）
- [ ] 权限缓存刷新测试
- [ ] UserDataPermissionResource 集成测试
- [ ] 全局拦截器集成测试（端到端验证权限过滤生效）
- [ ] Excel 导入/导出测试

### 8. 通用
- [ ] 审计字段自动填充
- [ ] 权限上下文中间件（JWT Token → SecurityContext → 权限缓存）

---

## 进度记录
| 日期 | 进度 | 备注 |
|------|------|------|
| 2026-04-21 | 已拆分 | 从 feat-data-permission 拆分 |
| 2026-04-21 | Spec enriched | 价值点: 3, 场景: 12, 任务: 8类共45项 |
