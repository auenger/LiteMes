# Feature: feat-e2e-smoke-test 第一波端到端冒烟测试

## Basic Information
- **ID**: feat-e2e-smoke-test
- **Name**: 第一波端到端冒烟测试（CORS + 登录 + Playwright 验收）
- **Priority**: 90
- **Size**: M
- **Dependencies**: feat-auth-frontend
- **Parent**: null
- **Children**: []
- **Created**: 2026-04-22

## Merge Record
- **Completed**: 2026-04-22T16:45:00+08:00
- **Merged Branch**: feature/e2e-smoke-test
- **Merge Commit**: de236b38d064326bfbd91d679ea91182c899b64d
- **Archive Tag**: feat-e2e-smoke-test-20260422
- **Verification**: 5/5 Playwright tests passed
- **Root Cause**: JWT signing failed because `quarkus.jwt.secret.key` is not a recognized SmallRye JWT property. Fixed by using `SecretKeySpec` directly in code + CDI @Alternative producer for verification.
- **Duration**: ~40 minutes

## Description

前后端联调的第一波冒烟测试，发现并修复阻塞性问题，最终通过 Playwright CLI 脚本自动化验收基本功能。

**已知问题：**
1. CORS 跨域：前端 3000 端口访问后端 8080 被拦截（`application.yml` origins 缺少 3000）
2. 登录 500：POST `/api/auth/login` 返回 `INTERNAL_ERROR`，需排查（JWT 签名 / UserMapper 注入 / 数据库连接）
3. 前后端通信完整性未验证

**验收方式：** 使用 Playwright CLI（`npx playwright`）编写自动化脚本，覆盖登录 + 基础 CRUD，开发者最终人工确认。

## User Value Points

1. **前后端通信修复** — 解决 CORS + 登录 500，打通前后端链路
2. **登录认证端到端** — 登录 → Token → 受保护接口完整可用
3. **Playwright 自动化验收** — 可重复执行的验收脚本，覆盖核心场景

## Context Analysis

### Reference Code
- `src/main/resources/application.yml` — CORS 配置（第 10-17 行）
- `frontend/vite.config.ts` — Vite 开发服务器端口和代理配置（port 3000, proxy /api → 8080）
- `src/main/java/com/litemes/web/AuthResource.java` — 登录接口
- `src/main/java/com/litemes/application/service/AuthService.java` — 认证服务（JWT 签名）
- `src/main/java/com/litemes/web/exception/GlobalExceptionMapper.java` — 全局异常处理（兜底 500）
- `frontend/src/api/` — Axios 接口封装
- `frontend/src/stores/user.ts` — 用户状态管理（Token 存储）

### Environment
- 后端：`http://localhost:8080`（Quarkus dev mode）
- 前端：`http://localhost:3000`（Vite dev server）
- 数据库：MySQL 8 Docker `ruoyi-mysql`，默认管理员 `admin / admin123`

## Technical Solution

### Phase 1: 排查与修复

#### 1.1 登录 500 错误排查
- 检查后端日志（`./mvnw quarkus:dev` 输出），定位 INTERNAL_ERROR 根因
- 可能原因：
  - JWT 签名失败（secret key 配置或 SmallRye JWT 初始化问题）
  - UserMapper 未被 CDI 正确注入（MyBatis-Plus 扫描路径）
  - 数据库连接失败或 user 表不存在
  - BaseEntity 审计字段自动填充异常
- 修复后验证：`curl -X POST http://localhost:8080/api/auth/login -H 'Content-Type: application/json' -d '{"username":"admin","password":"admin123"}'`

#### 1.2 CORS 修复
- `application.yml` 的 `quarkus.http.cors.origins` 添加 `http://localhost:3000`
- 验证浏览器不再拦截跨域请求

### Phase 2: Playwright 验收脚本

在 `frontend/` 目录下使用 Playwright CLI 编写验收脚本：
- 安装：`cd frontend && npm install -D @playwright/test && npx playwright install chromium`
- 脚本目录：`frontend/e2e/`
- 执行：`npx playwright test`
- 报告：`npx playwright show-report`

**验收场景：**
1. 登录成功 → 跳转首页
2. 登录失败 → 显示错误提示
3. 未登录访问受保护路由 → 跳转登录页
4. 公司管理 CRUD（列表 → 新建 → 编辑 → 删除）
5. 工序管理查询

## Acceptance Criteria (Gherkin)

### User Story
作为开发者，我希望前后端通信正常，登录认证和基础数据 CRUD 端到端可用，并通过 Playwright 自动化脚本验收。

### Scenarios

#### Scenario 1: 登录接口正常响应
```gherkin
Given 后端服务运行在 localhost:8080
  And 数据库中存在用户 admin/admin123
When 发送 POST /api/auth/login {"username":"admin","password":"admin123"}
Then 响应状态码为 200
  And 响应体包含 token 字段（非空 JWT 字符串）
```

#### Scenario 2: CORS 跨域请求成功
```gherkin
Given 前端运行在 http://localhost:3000
  And 后端运行在 http://localhost:8080
When 前端发送 POST /api/auth/login 请求
Then 响应状态码为 200
  And 响应包含 Access-Control-Allow-Origin 头
```

#### Scenario 3: 浏览器登录完整链路
```gherkin
Given 用户打开 http://localhost:3000/login
When 输入 admin / admin123 并点击登录
Then 页面跳转到首页
  And localStorage 中存储了 JWT token
```

#### Scenario 4: 登录失败处理
```gherkin
Given 用户打开登录页
When 输入 admin / wrongpassword 并点击登录
Then 页面显示错误提示
  And 不发生跳转
```

#### Scenario 5: 路由守卫
```gherkin
Given 用户未登录
When 直接访问 /companies
Then 自动跳转到 /login?redirect=/companies
```

#### Scenario 6: 公司管理 CRUD
```gherkin
Given 用户已登录并进入公司管理页面
When 新建公司（编码 AUTO_TEST_001, 名称 冒烟测试公司）
Then 列表中出现新公司
When 编辑公司名称为 冒烟测试公司_已修改
Then 列表更新
When 删除该公司
Then 列表中不再出现
```

#### Scenario 7: Playwright 验收通过
```gherkin
Given 所有修复已部署
When 运行 npx playwright test
Then 所有测试用例通过
  And 生成 HTML 报告
```

### General Checklist
- [ ] 登录 500 错误已修复
- [ ] CORS 配置允许 3000 端口
- [ ] Playwright 验收脚本编写完成
- [ ] 所有 Playwright 测试通过
- [ ] 开发者人工验收确认
