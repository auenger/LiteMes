import { test, expect } from '@playwright/test';

test.describe('Login Authentication', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login');
  });

  test('login success with valid credentials', async ({ page }) => {
    await page.locator('input[placeholder="用户名"]').fill('admin');
    await page.locator('input[placeholder="密码"]').fill('admin123');
    await page.locator('button:has-text("登 录")').click();

    await page.waitForURL('**/', { timeout: 10000 });
    expect(page.url()).not.toContain('/login');

    const token = await page.evaluate(() => localStorage.getItem('token'));
    expect(token).toBeTruthy();
  });

  test('login failure with wrong password', async ({ page }) => {
    await page.locator('input[placeholder="用户名"]').fill('admin');
    await page.locator('input[placeholder="密码"]').fill('wrongpassword');
    await page.locator('button:has-text("登 录")').click();

    await page.waitForTimeout(2000);
    expect(page.url()).toContain('/login');
  });

  test('route guard redirects unauthenticated user to login', async ({ page }) => {
    await page.goto('/companies');
    await page.waitForURL('**/login**', { timeout: 10000 });
    expect(page.url()).toContain('/login');
  });
});
