import { test, expect } from '../fixtures/auth';
import {
  findTableRowByText,
  clickRowAction,
  assertTableContains,
  assertTableNotContains,
} from '../helpers/common';

test.describe('用户权限 E2E 测试', () => {
  const TS = Date.now().toString(36);

  test.beforeEach(async ({ authenticatedPage: page }) => {
    await page.goto('/user-data-permissions');
    await page.waitForLoadState('networkidle', { timeout: 15000 });
  });

  test('用户权限列表加载', async ({ authenticatedPage: page }) => {
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });
    await expect(page.locator('.el-pagination')).toBeVisible({ timeout: 5000 });
  });

  test('新建用户权限（关联权限组和用户）', async ({ authenticatedPage: page }) => {
    // First create a permission group via the permission groups page
    await page.goto('/data-permission-groups');
    await page.waitForLoadState('networkidle');
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });

    const groupName = `E2E_PERM_UP_${TS}`;
    await page.locator('button:has-text("新建权限组")').click();
    const groupDialog = page.locator('.el-dialog:visible');
    await expect(groupDialog).toBeVisible({ timeout: 3000 });
    await groupDialog.locator('input[placeholder="请输入权限组名称"]').fill(groupName);
    await groupDialog.locator('button:has-text("确定")').click();
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });

    // Navigate back to user permissions page
    await page.goto('/user-data-permissions');
    await page.waitForLoadState('networkidle');
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });

    // Click "批量授权" button
    await page.locator('button:has-text("批量授权")').click();
    const dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });
    await expect(dialog.locator('.el-dialog__title')).toContainText('批量授权');

    // Select a user (admin) - find the checkbox for admin user
    const adminLabel = dialog.locator('label').filter({ hasText: /admin/ }).first();
    await expect(adminLabel).toBeVisible({ timeout: 5000 });
    await adminLabel.locator('input[type="checkbox"]').check({ force: true });
    await page.waitForTimeout(300);

    // Select the permission group via dropdown
    const groupSelect = dialog.locator('.el-select');
    await groupSelect.click({ force: true });
    await page.waitForTimeout(500);

    // Wait for dropdown to appear and select the option with our group name
    // Use a broader locator since el-select dropdowns are teleported to body
    const option = page.locator('.el-select-dropdown__item').filter({ hasText: groupName }).first();
    await expect(option).toBeVisible({ timeout: 5000 });
    await option.click({ force: true });

    // Click "确认授权" button
    await dialog.locator('button:has-text("确认授权")').click();

    // Wait for dialog to close
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });

    // Verify the user permission appears in table
    await assertTableContains(page, 'admin');
  });

  test('删除用户权限', async ({ authenticatedPage: page }) => {
    // First create a permission group and assign user
    await page.goto('/data-permission-groups');
    await page.waitForLoadState('networkidle');
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });

    const groupName = `E2E_PERM_UP_DEL_${TS}`;
    await page.locator('button:has-text("新建权限组")').click();
    const groupDialog = page.locator('.el-dialog:visible');
    await expect(groupDialog).toBeVisible({ timeout: 3000 });
    await groupDialog.locator('input[placeholder="请输入权限组名称"]').fill(groupName);
    await groupDialog.locator('button:has-text("确定")').click();
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });

    // Navigate back and create user permission
    await page.goto('/user-data-permissions');
    await page.waitForLoadState('networkidle');
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });

    // Batch assign
    await page.locator('button:has-text("批量授权")').click();
    let dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    // Select admin user
    const adminLabel = dialog.locator('label').filter({ hasText: /admin/ }).first();
    await expect(adminLabel).toBeVisible({ timeout: 5000 });
    await adminLabel.locator('input[type="checkbox"]').check({ force: true });
    await page.waitForTimeout(300);

    // Select permission group
    const groupSelect = dialog.locator('.el-select');
    await groupSelect.click({ force: true });
    await page.waitForTimeout(500);

    const option = page.locator('.el-select-dropdown__item').filter({ hasText: groupName }).first();
    await expect(option).toBeVisible({ timeout: 5000 });
    await option.click({ force: true });

    await dialog.locator('button:has-text("确认授权")').click();
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });

    // Wait for table to refresh
    await page.waitForTimeout(500);

    // Find the row with the group name and delete
    const groupInput = page.locator('input[placeholder="用户名"]');
    await groupInput.fill('admin');
    await page.locator('button:has-text("查询")').click();
    await page.waitForLoadState('networkidle');

    // Find row with group name and click delete
    const table = page.locator('.el-table');
    const row = table.locator('.el-table__row').filter({ hasText: groupName }).first();
    await expect(row).toBeVisible({ timeout: 5000 });

    // Click delete button
    await row.locator('button:has-text("删除")').click();

    // Confirm delete dialog
    const deleteDialog = page.locator('.el-dialog:visible').filter({ hasText: '确认删除' });
    await expect(deleteDialog).toBeVisible({ timeout: 3000 });
    await deleteDialog.locator('button:has-text("删除")').click();

    // Wait for dialog to close
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });

    // Verify the record is gone (search again)
    await page.waitForTimeout(500);
    await page.locator('button:has-text("查询")').click();
    await page.waitForLoadState('networkidle');

    // The group name should not appear in the table anymore
    await assertTableNotContains(page, groupName);
  });
});
