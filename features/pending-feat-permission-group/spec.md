# Feature: feat-permission-group 数据权限组管理

## 基本信息
- **ID**: feat-permission-group
- **名称**: 数据权限组管理
- **优先级**: 52
- **规模**: M
- **依赖**: feat-enterprise-org
- **父模块**: feat-data-permission
- **创建时间**: 2026-04-21

## 需求来源
- 数据权限_功能设计_V1.0.docx 第3章「数据权限组」
- 父 feature: feat-data-permission

## 需求描述
维护数据权限组基础信息，将工厂、工作中心、工序打包为权限集合，作为用户数据权限分配的基础单元。

### 核心实体
| 实体 | 说明 |
|------|------|
| `DataPermissionGroup` | 数据权限组主表（名称） |
| `DataPermissionGroupFactory` | 权限组-工厂关联 |
| `DataPermissionGroupWorkCenter` | 权限组-工作中心关联 |
| `DataPermissionGroupProcess` | 权限组-工序关联 |

### 功能清单
1. **权限组 CRUD** — 创建/编辑/删除数据权限组（名称唯一性校验）
2. **关联工厂** — 选择工厂（支持多选、分页多页勾选），关联到权限组
3. **关联工作中心** — 选择工作中心（支持多选、分页多页勾选），关联到权限组
4. **关联工序** — 选择工序（支持多选、分页多页勾选），关联到权限组
5. **删除保护** — 已被引用的权限组不可删除（按钮置灰）

### 页面设计
- 主页面：权限组列表（查询条件：权限组名称-模糊查询）
- Tab 页：工厂 / 工作中心 / 工序（每个 Tab 独立的选择关联管理）
- 创建/编辑弹窗：权限组名称

### 业务规则
- 权限组名称必填且唯一
- 工厂/工作中心/工序从企业管理模块已有数据中选择
- 关联操作允许多页勾选批量保存
- 已被用户权限引用的数据不可删除

## 数据模型

### DataPermissionGroup（数据权限组）
| 字段 | 类型 | 必填 | 编辑 | 备注 |
|------|------|------|------|------|
| id | bigint | Y | N | 主键 |
| group_name | varchar(50) | Y | Y | 权限组名称，唯一 |
| created_by | varchar | N | N | 创建人（系统登录人） |
| created_time | datetime | N | N | 创建时间 |
| updated_by | varchar | N | N | 修改人 |
| updated_time | datetime | N | N | 修改时间 |

### DataPermissionGroupFactory（权限组-工厂）
| 字段 | 类型 | 必填 | 备注 |
|------|------|------|------|
| id | bigint | Y | 主键 |
| group_id | bigint | Y | 关联权限组 |
| factory_id | bigint | Y | 关联工厂 |
| created_by/created_time/updated_by/updated_time | - | N | 审计字段 |

### DataPermissionGroupWorkCenter（权限组-工作中心）
| 字段 | 类型 | 必填 | 备注 |
|------|------|------|------|
| id | bigint | Y | 主键 |
| group_id | bigint | Y | 关联权限组 |
| work_center_id | bigint | Y | 关联工作中心 |
| created_by/created_time/updated_by/updated_time | - | N | 审计字段 |

### DataPermissionGroupProcess（权限组-工序）
| 字段 | 类型 | 必填 | 备注 |
|------|------|------|------|
| id | bigint | Y | 主键 |
| group_id | bigint | Y | 关联权限组 |
| process_id | bigint | Y | 关联工序 |
| created_by/created_time/updated_by/updated_time | - | N | 审计字段 |

## 验收标准 (Gherkin)

### 场景 1: 创建数据权限组
```gherkin
Given 用户进入数据权限组管理页面
When 创建数据权限组"冲压车间权限组"
Then 权限组创建成功，列表显示该记录
```

### 场景 2: 权限组名称唯一性
```gherkin
Given 已存在权限组"冲压车间权限组"
When 再次创建同名的数据权限组
Then 系统提示名称已存在，创建失败
```

### 场景 3: 关联工厂/工作中心/工序
```gherkin
Given 权限组"冲压车间权限组"已创建
And 工厂 F001 下有工作中心 WC001 含工序 PS001、PS002
When 为该权限组关联工厂 F001、工作中心 WC001、工序 PS001 和 PS002
Then 权限组正确关联所有选择的工厂、工作中心和工序
```

### 场景 4: 删除保护
```gherkin
Given 权限组"冲压车间权限组"已被用户权限引用
When 用户尝试删除该权限组
Then 删除按钮置灰不可点击
```
