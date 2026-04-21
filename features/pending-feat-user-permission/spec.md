# Feature: feat-user-permission 用户数据权限

## 基本信息
- **ID**: feat-user-permission
- **名称**: 用户数据权限
- **优先级**: 51
- **规模**: M
- **依赖**: feat-permission-group
- **父模块**: feat-data-permission
- **创建时间**: 2026-04-21

## 需求来源
- 数据权限_功能设计_V1.0.docx 第4章「数据权限」
- 父 feature: feat-data-permission

## 需求描述
为用户分配数据权限，支持按权限组批量授权，也支持直接为用户指定工厂/工作中心/工序级别的权限。通过 SqlSugar 全局查询过滤器，在仓储层自动过滤数据，业务代码无需手动处理权限。

### 核心实体
| 实体 | 说明 |
|------|------|
| `UserDataPermission` | 用户数据权限主表（用户+权限组关联） |
| `UserDataPermissionFactory` | 用户权限-工厂（直接授权或从权限组继承） |
| `UserDataPermissionWorkCenter` | 用户权限-工作中心 |
| `UserDataPermissionProcess` | 用户权限-工序 |

### 功能清单
1. **用户权限列表** — 按用户名/姓名查询，展示用户及其权限概览
2. **批量添加权限** — 选择多个用户，选择权限组，批量赋值该权限组已有的数据权限
3. **用户级工厂/工作中心/工序管理** — 为单个用户直接选择工厂、工作中心、工序
4. **导入/导出** — Excel 导入导出权限数据
5. **全局查询过滤器** — SqlSugar 全局过滤器，根据当前用户权限自动过滤查询结果

### 页面设计
- 主页面：用户数据权限列表（查询条件：用户名/姓名-模糊查询）
- 批量操作：勾选多个用户 → 选择权限组 → 确认批量授权
- Tab 页：工厂 / 工作中心 / 工序（为单个用户直接配置）
- 支持 Excel 导入/导出

### 业务规则
- 批量添加权限时，选择权限组后为所有勾选用户赋值该权限组的数据权限
- 用户权限 = 权限组继承的权限 + 直接授权的权限
- 全局过滤器在仓储层自动生效，拦截所有涉及数据权限实体的查询
- 过滤器根据当前登录用户的权限上下文自动注入过滤条件

## 数据模型

### UserDataPermission（用户数据权限）
| 字段 | 类型 | 必填 | 编辑 | 备注 |
|------|------|------|------|------|
| id | bigint | Y | N | 主键 |
| user_id | bigint | Y | N | 关联用户 |
| group_id | bigint | Y | Y | 关联权限组 |
| created_by/created_time/updated_by/updated_time | - | N | N | 审计字段 |

### UserDataPermissionFactory（用户权限-工厂）
| 字段 | 类型 | 必填 | 备注 |
|------|------|------|------|
| id | bigint | Y | 主键 |
| user_permission_id | bigint | Y | 关联用户权限 |
| factory_id | bigint | Y | 关联工厂 |
| source | enum | Y | 来源：GROUP(权限组继承) / DIRECT(直接授权) |
| created_by/created_time/updated_by/updated_time | - | N | 审计字段 |

### UserDataPermissionWorkCenter（用户权限-工作中心）
| 字段 | 类型 | 必填 | 备注 |
|------|------|------|------|
| id | bigint | Y | 主键 |
| user_permission_id | bigint | Y | 关联用户权限 |
| work_center_id | bigint | Y | 关联工作中心 |
| source | enum | Y | 来源：GROUP / DIRECT |
| created_by/created_time/updated_by/updated_time | - | N | 审计字段 |

### UserDataPermissionProcess（用户权限-工序）
| 字段 | 类型 | 必填 | 备注 |
|------|------|------|------|
| id | bigint | Y | 主键 |
| user_permission_id | bigint | Y | 关联用户权限 |
| process_id | bigint | Y | 关联工序 |
| source | enum | Y | 来源：GROUP / DIRECT |
| created_by/created_time/updated_by/updated_time | - | N | 审计字段 |

## 验收标准 (Gherkin)

### 场景 1: 批量添加权限
```gherkin
Given 用户 U001、U002 未分配数据权限
And 权限组"冲压车间"包含工厂 F001 + 工序 PS001、PS002
When 勾选 U001、U002，选择权限组"冲压车间"，点击确认
Then U001 和 U002 均获得工厂 F001 + 工序 PS001、PS002 的数据权限
```

### 场景 2: 直接为用户添加工序权限
```gherkin
Given 用户 U001 已有权限组"冲压车间"的权限
When 为 U001 直接添加工序 PS003 的权限
Then U001 的权限范围扩大为 PS001 + PS002 + PS003
```

### 场景 3: 全局过滤器自动生效
```gherkin
Given 用户 U001 的数据权限仅含工序 PS001
When 用户 U001 查询工单列表
Then 仓储层自动过滤，仅返回工序 PS001 的数据
And 用户无法看到其他工序的数据
```

### 场景 4: Excel 导入导出
```gherkin
Given 用户数据权限页面有权限数据
When 点击导出 Excel
Then 系统下载包含权限数据的 Excel 文件
When 上传格式正确的权限 Excel 文件
Then 系统正确导入权限数据
```
