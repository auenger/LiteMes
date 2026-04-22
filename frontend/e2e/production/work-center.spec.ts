import { test, expect } from '../fixtures/auth';
import {
  findTableRowByText,
  clickRowAction,
  deleteWithConfirm,
  assertTableContains,
  assertTableNotContains,
} from '../helpers/common';

test.describe('工作中心管理 E2E 测试', () => {
  test.describe.configure({ timeout: 60000, mode: 'serial' });

  const TS = Date.now().toString(36);
  const WC_CODE = `E2E_PROD_WC_${TS}`;
  const WC_NAME = `E2E测试工作中心_${TS}`;
  const CO_CODE = `E2E_PROD_CO_${TS}`;
  const CO_NAME = `E2E生产基础公司_${TS}`;
  const FAC_CODE = `E2E_PROD_FAC_${TS}`;
  const FAC_NAME = `E2E生产基础工厂_${TS}`;
  let setupDone = false;

  async function ensureCompanyAndFactory(page: any) {
    if (setupDone) return;

    // Create company
    await page.goto('/companies');
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });

    const existingCo = page.locator('.el-table__body-wrapper .el-table__row').filter({ hasText: CO_CODE });
    if (!(await existingCo.isVisible({ timeout: 2000 }).catch(() => false))) {
      await page.locator('button:has-text("新建公司")').click();
      const dialog = page.locator('.el-dialog:visible');
      await dialog.locator('input[placeholder="请输入公司编码"]').fill(CO_CODE);
      await dialog.locator('input[placeholder="请输入公司名称"]').fill(CO_NAME);
      await dialog.locator('button:has-text("确定")').click();
      await assertTableContains(page, CO_CODE);
    }

    // Create factory
    await page.goto('/factories');
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });

    const existingFac = page.locator('.el-table__body-wrapper .el-table__row').filter({ hasText: FAC_CODE });
    if (!(await existingFac.isVisible({ timeout: 2000 }).catch(() => false))) {
      await page.locator('button:has-text("新建工厂")').click();
      const dialog = page.locator('.el-dialog:visible');
      await dialog.locator('input[placeholder="请输入工厂编码"]').fill(FAC_CODE);
      await dialog.locator('input[placeholder="请输入工厂名称"]').fill(FAC_NAME);
      const select = dialog.locator('.el-select');
      await select.click();
      await page.waitForTimeout(500);
      const items = page.locator('.el-select-dropdown__item:visible');
      await expect(items.first()).toBeVisible({ timeout: 5000 });
      await items.filter({ hasText: CO_NAME }).first().click();
      await dialog.locator('button:has-text("确定")').click();
      await assertTableContains(page, FAC_CODE);
    }

    setupDone = true;
  }

  async function selectFactoryInDialog(page: any) {
    const dialog = page.locator('.el-dialog:visible');
    const selects = dialog.locator('.el-select');
    await selects.click();
    await page.waitForTimeout(500);
    const items = page.locator('.el-select-dropdown__item:visible');
    await expect(items.first()).toBeVisible({ timeout: 5000 });
    await items.filter({ hasText: FAC_NAME }).first().click();
  }

  test.beforeEach(async ({ authenticatedPage: page }) => {
    await ensureCompanyAndFactory(page);
    await page.goto('/work-centers');
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });
  });

  test('工作中心列表加载', async ({ authenticatedPage: page }) => {
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 5000 });
    await expect(page.locator('.el-pagination')).toBeVisible({ timeout: 5000 });
  });

  test('新建工作中心成功', async ({ authenticatedPage: page }) => {
    await page.locator('button:has-text("新建工作中心")').click();
    const dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    await dialog.locator('input[placeholder="请输入工作中心编码"]').fill(WC_CODE);
    await dialog.locator('input[placeholder="请输入工作中心名称"]').fill(WC_NAME);
    await selectFactoryInDialog(page);
    await dialog.locator('button:has-text("确定")').click();

    await assertTableContains(page, WC_CODE);
  });

  test('工作中心编码必填校验', async ({ authenticatedPage: page }) => {
    await page.locator('button:has-text("新建工作中心")').click();
    const dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    await dialog.locator('button:has-text("确定")').click();
    await expect(dialog.locator('.text-red-500')).toBeVisible({ timeout: 3000 });
  });

  test('编辑工作中心', async ({ authenticatedPage: page }) => {
    // Create
    await page.locator('button:has-text("新建工作中心")').click();
    const createDialog = page.locator('.el-dialog:visible');
    await createDialog.locator('input[placeholder="请输入工作中心编码"]').fill(WC_CODE);
    await createDialog.locator('input[placeholder="请输入工作中心名称"]').fill(WC_NAME);
    await selectFactoryInDialog(page);
    await createDialog.locator('button:has-text("确定")').click();
    await assertTableContains(page, WC_CODE);

    // Edit
    const row = await findTableRowByText(page, WC_CODE);
    await clickRowAction(row, '编辑');
    const editDialog = page.locator('.el-dialog:visible');
    await expect(editDialog).toBeVisible({ timeout: 3000 });

    const codeInput = editDialog.locator('input[placeholder="请输入工作中心编码"]');
    await expect(codeInput).toBeDisabled();

    const nameInput = editDialog.locator('input[placeholder="请输入工作中心名称"]');
    await nameInput.clear();
    await nameInput.fill(`${WC_NAME}_编辑`);
    await editDialog.locator('button:has-text("确定")').click();
    await assertTableContains(page, `${WC_NAME}_编辑`);
  });

  test('删除工作中心', async ({ authenticatedPage: page }) => {
    // Create
    await page.locator('button:has-text("新建工作中心")').click();
    const createDialog = page.locator('.el-dialog:visible');
    await createDialog.locator('input[placeholder="请输入工作中心编码"]').fill(WC_CODE);
    await createDialog.locator('input[placeholder="请输入工作中心名称"]').fill(WC_NAME);
    await selectFactoryInDialog(page);
    await createDialog.locator('button:has-text("确定")').click();
    await assertTableContains(page, WC_CODE);

    // Delete
    const row = await findTableRowByText(page, WC_CODE);
    await deleteWithConfirm(page, row);
    await assertTableNotContains(page, WC_CODE);
  });
});
