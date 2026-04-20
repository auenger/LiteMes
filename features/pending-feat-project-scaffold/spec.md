# Feature: feat-project-scaffold 项目工程骨架

## 基本信息
- **ID**: feat-project-scaffold
- **名称**: 项目工程骨架
- **优先级**: 100
- **规模**: M
- **依赖**: 无
- **父需求**: 无
- **子需求**: 无
- **创建时间**: 2026-04-20

## 需求描述
搭建 .NET Core 四层架构解决方案骨架，建立共享基础设施，为所有后续业务模块提供统一的技术底座。

包含：
- 四层项目结构（WebApi / Application / Domain / Infrastructure）
- SqlSugar ORM 配置与数据库上下文
- 全局异常处理中间件
- JWT 认证授权配置
- 审计日志基类（创建人、修改人、变更时间）
- 软删除全局过滤器
- Swagger API 文档配置
- Serilog 结构化日志配置
- Vue 3 前端项目初始化（Pinia + Vite + Router）

## 用户价值点
1. 统一的项目架构规范，降低后续开发成本
2. 开箱即用的基础设施（认证、日志、异常处理）
3. 前后端项目模板，快速启动业务开发

## 上下文分析
### 需要参考的现有代码
- 无（首个需求）

### 相关文档
- README.md — 架构概览
- feature-workflow/project-context.md — 技术栈与代码规范

## 技术方案
<!-- 待开发时填写 -->

## 验收标准 (Acceptance Criteria)

### 用户故事
作为开发者，我希望有一个开箱即用的项目骨架，以便快速开始业务功能开发。

### Gherkin 验收场景

#### 场景 1: 后端四层项目结构
```gherkin
Given 一个空的 .NET Core 解决方案
When 完成骨架搭建
Then 包含 WebApi / Application / Domain / Infrastructure 四个项目
And 项目间引用关系正确（WebApi→Application→Domain←Infrastructure）
```

#### 场景 2: 数据库连接与 ORM
```gherkin
Given SqlSugar 已配置
When 启动应用
Then 数据库连接正常
And 全局查询过滤器已注册
```

#### 场景 3: 前端项目
```gherkin
Given Vue 3 项目已初始化
When 运行 npm run dev
Then 前端页面可正常访问
```
