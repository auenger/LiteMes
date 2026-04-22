# Tasks: feat-e2e-smoke-test

## Task Breakdown

### 1. 登录 500 错误排查与修复
- [ ] 查看后端 `./mvnw quarkus:dev` 日志，定位 INTERNAL_ERROR 根因
- [ ] 可能检查项：JWT secret key 配置、UserMapper CDI 注入、user 表结构匹配、BaseEntity 审计填充
- [ ] 修复后用 curl 验证：`curl -X POST http://localhost:8080/api/auth/login -H 'Content-Type: application/json' -d '{"username":"admin","password":"admin123"}'`
- [ ] 确认返回 `{"code":200,"data":{"token":"..."}}`

### 2. CORS 跨域修复
- [ ] `application.yml` 的 `quarkus.http.cors.origins` 添加 `http://localhost:3000`
- [ ] 确认 Vite proxy 配置（`vite.config.ts` server.proxy /api → localhost:8080）
- [ ] 浏览器验证跨域请求不再被拦截

### 3. Playwright 环境搭建
- [ ] `cd frontend && npm install -D @playwright/test`
- [ ] `npx playwright install chromium`
- [ ] 创建 `frontend/e2e/` 目录和 `playwright.config.ts`

### 4. Playwright 验收脚本编写
- [ ] `e2e/auth.spec.ts` — 登录成功、登录失败、路由守卫
- [ ] `e2e/company-crud.spec.ts` — 公司管理 CRUD 完整流程
- [ ] 使用 Playwright API 模拟用户操作（fill、click、waitForNavigation 等）

### 5. 执行验收与问题修复
- [ ] 运行 `npx playwright test`，确保全部通过
- [ ] 修复测试过程中发现的问题
- [ ] 生成 HTML 报告供开发者人工确认

## Progress Log
| Date | Progress | Notes |
|------|----------|-------|
| 2026-04-22 | Feature created | 待开始开发 |
