# Feature: feat-process-frontend 工序管理前端

## 基本信息
- **ID**: feat-process-frontend
- **名称**: 工序管理前端
- **优先级**: 85
- **规模**: S
- **依赖**: feat-frontend-ui-lib
- **父模块**: feat-frontend-overhaul
- **创建时间**: 2026-04-22

## 需求来源
- feat-process 后端已完成归档，但前端完全缺失（无 API 文件、无 View、无路由）
- 工序是数据权限三维度中最细的粒度，也是设备绑定和报工管理的基础

## 需求描述
补齐工序管理前端模块，包括 API 封装、视图组件和路由配置。工序隶属于工作中心，提供标准 CRUD、搜索过滤、状态管理功能。

## 用户价值点

### VP1: 工序管理前端补齐
补齐 P0 缺失的前端模块，使工序数据可通过 UI 界面管理。

### VP2: 数据权限完整性
工序是数据权限（工厂/工作中心/工序）三维度之一，缺失会影响权限组的配置和使用。

## 技术方案

### 后端 API 对接（基于 ProcessResource.java）
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/processes | 分页查询工序 |
| GET | /api/processes/{id} | 获取工序详情 |
| POST | /api/processes | 创建工序 |
| PUT | /api/processes/{id} | 更新工序 |
| DELETE | /api/processes/{id} | 删除工序 |
| PUT | /api/processes/{id}/status | 启用/禁用 |

### 需要创建的文件
- `api/process.ts` — 工序 API 封装
- `views/process/ProcessList.vue` — 工序列表视图

### 需要更新的文件
- `router/index.ts` — 添加 /processes 路由

### 数据模型
```
Process {
  id: number
  processCode: string     // 工序编码（创建后不可修改）
  processName: string     // 工序名称
  workCenterId: number    // 所属工作中心 ID
  workCenterName: string  // 所属工作中心名称（显示用）
  status: number          // 状态（1=启用, 0=禁用）
  createdBy: string
  createdAt: string
  updatedBy: string
  updatedAt: string
}
```

### 查询条件
- 工序编码（模糊）
- 工序名称（模糊）
- 所属工作中心（下拉选择）
- 状态（启用/禁用）

## 验收标准 (Gherkin)

```gherkin
Feature: 工序管理前端

  Background:
    Given 后端工序管理 API 已部署

  Scenario: 工序列表查询
    Given 用户进入工序管理页面
    Then 显示工序列表（编码、名称、工作中心、状态、操作）
    When 输入编码进行模糊搜索
    Then 列表按条件过滤

  Scenario: 创建工序
    When 点击新增工序
    Then 弹出对话框，填写编码、名称、所属工作中心
    When 编码为空时提交
    Then 显示编码必填校验提示
    When 正确填写并提交
    Then 工序创建成功，列表刷新

  Scenario: 编辑工序
    When 点击编辑按钮
    Then 编码字段禁用（不可修改）
    When 修改名称并提交
    Then 更新成功

  Scenario: 启用/禁用工序
    When 点击状态切换按钮
    Then 工序状态在启用/禁用之间切换

  Scenario: 删除工序
    When 点击删除按钮
    Then 弹出确认提示
    When 确认删除
    Then 若未被引用则删除成功
    And 若已被引用则提示不可删除
```
