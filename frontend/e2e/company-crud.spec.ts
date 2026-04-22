import { test, expect } from '@playwright/test';

test.describe('Company Management CRUD', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login');
    await page.locator('input[placeholder="用户名"]').fill('admin');
    await page.locator('input[placeholder="密码"]').fill('admin123');
    await page.locator('button:has-text("登 录")').click();
    await page.waitForURL('**/', { timeout: 10000 });

    await page.goto('/companies');
    await page.waitForLoadState('networkidle');
  });

  test('create a new company', async ({ page }) => {
    await page.locator('button:has-text("新建公司")').click();
    await page.waitForTimeout(500);

    await page.locator('input[placeholder="请输入公司编码"]').fill('AUTO_TEST_001');
    await page.locator('input[placeholder="请输入公司名称"]').fill('冒烟测试公司');

    await page.locator('.el-dialog button:has-text("确定")').click();
    await page.waitForTimeout(2000);

    await expect(page.locator('text=AUTO_TEST_001')).toBeVisible({ timeout: 5000 });
  });

  test('list companies', async ({ page }) => {
    await page.waitForLoadState('networkidle');
    const table = page.locator('.el-table');
    await expect(table).toBeVisible({ timeout: 5000 });
  });
});
