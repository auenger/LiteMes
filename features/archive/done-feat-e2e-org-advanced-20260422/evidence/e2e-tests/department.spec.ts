import { test, expect } from '../fixtures/auth';
import {
  findTableRowByText,
  clickRowAction,
  deleteWithConfirm,
  assertTableContains,
  assertTableNotContains,
} from '../helpers/common';

test.describe('部门管理 E2E 测试', () => {
  const TS = Date.now().toString(36);
  const DEPT_CODE = `E2E_ORG_DEPT_${TS}`;
  const DEPT_NAME = `E2E测试部门_${TS}`;
  const CHILD_CODE = `E2E_ORG_SUB_${TS}`;
  const CHILD_NAME = `E2E子部门_${TS}`;
  const CO_CODE = `E2E_CO_D_${TS}`;
  const CO_NAME = `E2E部门所属公司_${TS}`;
  const FAC_CODE = `E2E_FAC_D_${TS}`;
  const FAC_NAME = `E2E部门所属工厂_${TS}`;

  async function selectFactory(page: any) {
    const dialog = page.locator('.el-dialog:visible');
    const select = dialog
      .locator('.el-form-item')
      .filter({ hasText: '所属工厂' })
      .locator('.el-select');
    await select.click();
    await page.waitForTimeout(500);
    const items = page.locator('.el-select-dropdown__item:visible');
    await expect(items.first()).toBeVisible({ timeout: 5000 });
    await items.filter({ hasText: FAC_NAME }).first().click();
  }

  async function selectParent(page: any) {
    const dialog = page.locator('.el-dialog:visible');
    const select = dialog
      .locator('.el-form-item')
      .filter({ hasText: '上级部门' })
      .locator('.el-select');
    await select.click();
    await page.waitForTimeout(500);
    const items = page.locator('.el-select-dropdown__item:visible');
    await expect(items.first()).toBeVisible({ timeout: 5000 });
    await items.filter({ hasText: DEPT_CODE }).first().click();
  }

  async function createDepartment(page: any, code: string, name: string, withParent = false) {
    // Dismiss any stale dialog from previous operations
    await page.keyboard.press('Escape');
    await page.waitForTimeout(300);

    await page.locator('button:has-text("新建部门")').click();
    const dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });
    await dialog.locator('input[placeholder="请输入部门编码"]').fill(code);
    await dialog.locator('input[placeholder="请输入部门名称"]').fill(name);
    await selectFactory(page);
    if (withParent) {
      await page.waitForTimeout(800);
      await selectParent(page);
    }
    await dialog.locator('button:has-text("确定")').click();
    await page.waitForTimeout(500);
    await assertTableContains(page, code);
  }

  test.beforeEach(async ({ authenticatedPage: page }) => {
    // Ensure parent company exists
    await page.goto('/companies');
    await page.waitForLoadState('networkidle');
    const existingCo = page
      .locator('.el-table__body-wrapper .el-table__row')
      .filter({ hasText: CO_CODE });
    if (!(await existingCo.isVisible({ timeout: 2000 }).catch(() => false))) {
      await page.locator('button:has-text("新建公司")').click();
      const dialog = page.locator('.el-dialog:visible');
      await dialog.locator('input[placeholder="请输入公司编码"]').fill(CO_CODE);
      await dialog.locator('input[placeholder="请输入公司名称"]').fill(CO_NAME);
      await dialog.locator('button:has-text("确定")').click();
      await assertTableContains(page, CO_CODE);
    }

    // Ensure parent factory exists
    await page.goto('/factories');
    await page.waitForLoadState('networkidle');
    const existingFac = page
      .locator('.el-table__body-wrapper .el-table__row')
      .filter({ hasText: FAC_CODE });
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

    await page.goto('/departments');
    await page.waitForLoadState('networkidle');
  });

  test('部门列表加载', async ({ authenticatedPage: page }) => {
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 5000 });
  });

  test('新建顶级部门', async ({ authenticatedPage: page }) => {
    await createDepartment(page, DEPT_CODE, DEPT_NAME);
  });

  test('新建子部门', async ({ authenticatedPage: page }) => {
    await createDepartment(page, DEPT_CODE, DEPT_NAME);
    await createDepartment(page, CHILD_CODE, CHILD_NAME, true);
  });

  test('删除子部门', async ({ authenticatedPage: page }) => {
    await createDepartment(page, DEPT_CODE, DEPT_NAME);
    await createDepartment(page, CHILD_CODE, CHILD_NAME, true);

    const row = await findTableRowByText(page, CHILD_CODE);
    await deleteWithConfirm(page, row);
    await assertTableNotContains(page, CHILD_CODE);
  });

  // Keep "编辑部门" last since its afterEach cleanup may leave stale dialogs
  test('编辑部门', async ({ authenticatedPage: page }) => {
    await createDepartment(page, DEPT_CODE, DEPT_NAME);

    const row = await findTableRowByText(page, DEPT_CODE);
    await clickRowAction(row, '编辑');
    const editDialog = page.locator('.el-dialog:visible');
    await expect(editDialog).toBeVisible({ timeout: 3000 });
    await expect(
      editDialog.locator('input[placeholder="请输入部门编码"]')
    ).toBeDisabled();

    const nameInput = editDialog.locator(
      'input[placeholder="请输入部门名称"]'
    );
    await nameInput.clear();
    await nameInput.fill(`${DEPT_NAME}_编辑`);
    await editDialog.locator('button:has-text("确定")').click();
    await assertTableContains(page, `${DEPT_NAME}_编辑`);
  });
});
