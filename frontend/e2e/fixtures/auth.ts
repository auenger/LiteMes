import { test as base, expect } from '@playwright/test';

type AuthFixture = {
  authenticatedPage: typeof base;
};

export const test = base.extend<AuthFixture>({
  authenticatedPage: async ({ page }, use) => {
    await page.goto('/login');
    await page.locator('input[placeholder="用户名"]').fill('admin');
    await page.locator('input[placeholder="密码"]').fill('admin123');
    await page.locator('button:has-text("登 录")').click();
    await page.waitForURL('**/', { timeout: 10000 });

    const token = await page.evaluate(() => localStorage.getItem('token'));
    expect(token).toBeTruthy();

    await use(page as any);
  },
});

export { expect };
