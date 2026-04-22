import { test, expect } from '../fixtures/auth';
import {
  findTableRowByText,
  clickRowAction,
  deleteWithConfirm,
  assertTableContains,
  assertTableNotContains,
} from '../helpers/common';

test.describe('工厂管理 E2E 测试', () => {
  const TS = Date.now().toString(36);
  const FAC_CODE = `E2E_FAC_${TS}`;
  const FAC_NAME = `E2E测试工厂_${TS}`;
  const CO_CODE = `E2E_CO_F_${TS}`;
  const CO_NAME = `E2E工厂所属_${TS}`;

  async function selectCompanyInDialog(page: any) {
    const dialog = page.locator('.el-dialog:visible');
    const select = dialog.locator('.el-select');
    await select.click();
    await page.waitForTimeout(500);
    const items = page.locator('.el-select-dropdown__item:visible');
    await expect(items.first()).toBeVisible({ timeout: 5000 });
    await items.filter({ hasText: CO_NAME }).first().click();
  }

  test.beforeEach(async ({ authenticatedPage: page }) => {
    // Ensure parent company exists
    await page.goto('/companies');
    await page.waitForLoadState('networkidle');

    const existing = page.locator('.el-table__body-wrapper .el-table__row').filter({ hasText: CO_CODE });
    if (!(await existing.isVisible({ timeout: 2000 }).catch(() => false))) {
      await page.locator('button:has-text("新建公司")').click();
      const dialog = page.locator('.el-dialog:visible');
      await dialog.locator('input[placeholder="请输入公司编码"]').fill(CO_CODE);
      await dialog.locator('input[placeholder="请输入公司名称"]').fill(CO_NAME);
      await dialog.locator('button:has-text("确定")').click();
      await assertTableContains(page, CO_CODE);
    }

    await page.goto('/factories');
    await page.waitForLoadState('networkidle');
  });

  test('工厂列表加载', async ({ authenticatedPage: page }) => {
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 5000 });
  });

  test('新建工厂成功', async ({ authenticatedPage: page }) => {
    await page.locator('button:has-text("新建工厂")').click();
    const dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    await dialog.locator('input[placeholder="请输入工厂编码"]').fill(FAC_CODE);
    await dialog.locator('input[placeholder="请输入工厂名称"]').fill(FAC_NAME);
    await selectCompanyInDialog(page);
    await dialog.locator('button:has-text("确定")').click();

    await assertTableContains(page, FAC_CODE);
  });

  test('工厂编码必填校验', async ({ authenticatedPage: page }) => {
    await page.locator('button:has-text("新建工厂")').click();
    const dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    await dialog.locator('button:has-text("确定")').click();
    await expect(dialog.locator('.text-red-500')).toBeVisible({ timeout: 3000 });
  });

  test('编辑工厂', async ({ authenticatedPage: page }) => {
    // Create factory
    await page.locator('button:has-text("新建工厂")').click();
    const dialog = page.locator('.el-dialog:visible');
    await dialog.locator('input[placeholder="请输入工厂编码"]').fill(FAC_CODE);
    await dialog.locator('input[placeholder="请输入工厂名称"]').fill(FAC_NAME);
    await selectCompanyInDialog(page);
    await dialog.locator('button:has-text("确定")').click();
    await assertTableContains(page, FAC_CODE);

    // Edit
    const row = await findTableRowByText(page, FAC_CODE);
    await clickRowAction(row, '编辑');
    const editDialog = page.locator('.el-dialog:visible');
    await expect(editDialog).toBeVisible({ timeout: 3000 });

    const codeInput = editDialog.locator('input[placeholder="请输入工厂编码"]');
    await expect(codeInput).toBeDisabled();

    const nameInput = editDialog.locator('input[placeholder="请输入工厂名称"]');
    await nameInput.clear();
    await nameInput.fill(`${FAC_NAME}_编辑`);
    await editDialog.locator('button:has-text("确定")').click();
    await assertTableContains(page, `${FAC_NAME}_编辑`);
  });

  test('删除工厂', async ({ authenticatedPage: page }) => {
    // Create factory
    await page.locator('button:has-text("新建工厂")').click();
    const dialog = page.locator('.el-dialog:visible');
    await dialog.locator('input[placeholder="请输入工厂编码"]').fill(FAC_CODE);
    await dialog.locator('input[placeholder="请输入工厂名称"]').fill(FAC_NAME);
    await selectCompanyInDialog(page);
    await dialog.locator('button:has-text("确定")').click();
    await assertTableContains(page, FAC_CODE);

    // Delete
    const row = await findTableRowByText(page, FAC_CODE);
    await deleteWithConfirm(page, row);
    await assertTableNotContains(page, FAC_CODE);
  });
});
