import { test, expect } from '../fixtures/auth';
import {
  findTableRowByText,
  clickRowAction,
  deleteWithConfirm,
  assertTableContains,
  assertTableNotContains,
} from '../helpers/common';

test.describe('单位换算管理 E2E 测试', () => {
  const TS = Date.now().toString(36);
  const UOM_A_CODE = `E2E_PROD_UA_${TS}`;
  const UOM_A_NAME = `E2E测试单位A_${TS}`;
  const UOM_B_CODE = `E2E_PROD_UB_${TS}`;
  const UOM_B_NAME = `E2E测试单位B_${TS}`;

  async function ensureUomUnits(page: any) {
    // Create UOM A
    await page.goto('/uoms');
    await page.waitForLoadState('networkidle');

    const existingA = page.locator('.el-table__body-wrapper .el-table__row').filter({ hasText: UOM_A_CODE });
    if (!(await existingA.isVisible({ timeout: 2000 }).catch(() => false))) {
      await page.locator('button:has-text("新建单位")').click();
      const dialog = page.locator('.el-dialog:visible');
      await dialog.locator('input[placeholder="请输入单位编码"]').fill(UOM_A_CODE);
      await dialog.locator('input[placeholder="请输入单位名称"]').fill(UOM_A_NAME);
      await dialog.locator('button:has-text("确定")').click();
      await assertTableContains(page, UOM_A_CODE);
    }

    // Create UOM B
    const existingB = page.locator('.el-table__body-wrapper .el-table__row').filter({ hasText: UOM_B_CODE });
    if (!(await existingB.isVisible({ timeout: 2000 }).catch(() => false))) {
      await page.locator('button:has-text("新建单位")').click();
      const dialog = page.locator('.el-dialog:visible');
      await dialog.locator('input[placeholder="请输入单位编码"]').fill(UOM_B_CODE);
      await dialog.locator('input[placeholder="请输入单位名称"]').fill(UOM_B_NAME);
      await dialog.locator('button:has-text("确定")').click();
      await assertTableContains(page, UOM_B_CODE);
    }
  }

  async function selectUomInDialog(page: any, label: string, uomText: string) {
    const dialog = page.locator('.el-dialog:visible');
    // Find the form item by label, then its select
    const formItems = dialog.locator('.el-form-item');
    const count = await formItems.count();
    for (let i = 0; i < count; i++) {
      const itemLabel = await formItems.nth(i).locator('.el-form-item__label').textContent();
      if (itemLabel?.trim() === label) {
        const select = formItems.nth(i).locator('.el-select');
        await select.click();
        await page.waitForTimeout(500);
        const items = page.locator('.el-select-dropdown__item:visible');
        await expect(items.first()).toBeVisible({ timeout: 5000 });
        await items.filter({ hasText: uomText }).first().click();
        return;
      }
    }
  }

  test.beforeEach(async ({ authenticatedPage: page }) => {
    await ensureUomUnits(page);
    await page.goto('/uom-conversions');
    await page.waitForLoadState('networkidle');
  });

  test('单位换算列表加载', async ({ authenticatedPage: page }) => {
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 5000 });
  });

  test('新建单位换算关系', async ({ authenticatedPage: page }) => {
    await page.locator('button:has-text("新建换算比例")').click();
    const dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    // Select from unit (UOM A)
    await selectUomInDialog(page, '原单位', UOM_A_CODE);
    // Select to unit (UOM B)
    await selectUomInDialog(page, '目标单位', UOM_B_CODE);

    // Fill conversion rate
    const rateInput = dialog.locator('.el-input-number .el-input__inner');
    await rateInput.fill('1000');

    await dialog.locator('button:has-text("确定")').click();

    // Verify the conversion appears in the table
    await assertTableContains(page, UOM_A_CODE);
    await assertTableContains(page, UOM_B_CODE);
  });

  test('删除单位换算关系', async ({ authenticatedPage: page }) => {
    // Create conversion first
    await page.locator('button:has-text("新建换算比例")').click();
    const createDialog = page.locator('.el-dialog:visible');
    await expect(createDialog).toBeVisible({ timeout: 3000 });

    await selectUomInDialog(page, '原单位', UOM_A_CODE);
    await selectUomInDialog(page, '目标单位', UOM_B_CODE);

    const rateInput = createDialog.locator('.el-input-number .el-input__inner');
    await rateInput.fill('1000');
    await createDialog.locator('button:has-text("确定")').click();

    await assertTableContains(page, UOM_A_CODE);

    // Delete the conversion
    const row = await findTableRowByText(page, UOM_A_CODE);
    await deleteWithConfirm(page, row);
    await assertTableNotContains(page, '1000');
  });
});
