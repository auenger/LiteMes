# Feature: feat-frontend-layout 导航布局

## 基本信息
- **ID**: feat-frontend-layout
- **名称**: 导航布局（侧边栏 + 面包屑 + Tab 页签）
- **优先级**: 90
- **规模**: M
- **依赖**: feat-frontend-ui-lib
- **父模块**: feat-frontend-overhaul
- **创建时间**: 2026-04-22

## 需求来源
- 当前 App.vue 仅为 `<router-view />`，无导航、无菜单
- 用户只能通过手动输入 URL 访问页面
- 参考 demo/litemes/src/App.vue 的布局模式

## 需求描述
参考 demo 实现，创建完整的后台管理布局：顶部 Header（Logo + 面包屑 + 主题切换 + 用户信息）、左侧 Sidebar（可折叠导航菜单，按模块分组）、Tab 页签栏（多页签切换 + 关闭）、主内容区（router-view + transition 动画）。支持暗色主题切换。

## 用户价值点

### VP1: 页面可发现性
通过侧边栏菜单，用户可直观发现和访问所有功能模块，无需记忆 URL。

### VP2: 多页签效率
Tab 页签系统支持同时打开多个页面快速切换，关闭已访问页签，提升操作效率。

### VP3: 主题切换
亮色/暗色主题切换，适应不同使用场景。

## 技术方案

### 布局结构（参考 demo/litemes/src/App.vue）
```
┌─────────────────────────────────────────────┐
│  Header: Logo | 面包屑 | 主题 | 用户信息     │
├────────┬────────────────────────────────────┤
│        │  Tab Bar: [首页] [工厂] [客户] ×    │
│  Side  ├────────────────────────────────────┤
│  bar   │                                    │
│        │  Main Content                      │
│  ──    │  <router-view />                   │
│  菜单  │                                    │
│  分组  │                                    │
│        │                                    │
├────────┴────────────────────────────────────┤
```

### 菜单结构
```
Dashboard（首页）
组织架构
  ├── 公司管理
  ├── 工厂管理
  ├── 部门管理
  └── 班制班次
生产基础
  ├── 工作中心
  ├── 工序管理
  ├── 计量单位
  └── 单位换算
物料管理
  ├── 物料分类
  ├── 物料信息
  └── 免检清单
设备管理
  ├── 设备类型
  ├── 设备型号
  └── 设备台账
供应链
  ├── 客户管理
  └── 供应商管理
数据权限
  ├── 权限组管理
  └── 用户权限
```

### 需要创建的文件
- `layouts/MainLayout.vue` — 主布局组件（替代 App.vue 中的纯 router-view）
- `stores/tabs.ts` — Tab 页签状态管理（参考 demo/stores/tabs.ts）
- `stores/theme.ts` — 主题状态管理（参考 demo/stores/theme.ts）
- 更新 `App.vue` — 使用 MainLayout
- 更新 `router/index.ts` — 嵌套路由结构

### 参考 demo 代码
- demo/litemes/src/App.vue — 完整布局（Header + Sidebar + Tabs + Content）
- demo/litemes/src/stores/tabs.ts — Tab 页签管理
- demo/litemes/src/stores/theme.ts — 暗色主题切换

## 验收标准 (Gherkin)

```gherkin
Feature: 导航布局

  Scenario: 侧边栏导航
    Given 用户已登录
    When 查看侧边栏
    Then 菜单按模块分组显示所有已开发功能
    And 当前页面对应的菜单项高亮
    When 点击菜单项
    Then 页面跳转到对应路由
    And 新增一个 Tab 页签

  Scenario: Tab 页签管理
    Given 用户已打开多个页面
    When 查看 Tab 栏
    Then 所有已打开页面以 Tab 形式显示
    And 当前页面 Tab 高亮
    When 点击 Tab 上的关闭按钮
    Then 该 Tab 关闭并切换到相邻 Tab
    And 首页 Tab 不可关闭

  Scenario: 面包屑导航
    Given 用户在任意子页面
    When 查看面包屑
    Then 显示 首页 > 模块名 > 页面名 的层级路径

  Scenario: 暗色主题
    When 点击主题切换按钮
    Then 整个界面在亮色/暗色之间切换
    And 所有 Element Plus 组件主题同步切换
```
