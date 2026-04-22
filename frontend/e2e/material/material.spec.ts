import { test, expect } from '../fixtures/auth';
import {
  findTableRowByText,
  clickRowAction,
  deleteWithConfirm,
  assertTableContains,
  assertTableNotContains,
} from '../helpers/common';

/**
 * Helper: get the currently visible Element Plus select dropdown.
 */
function getVisibleDropdown(page: any) {
  return page.locator('.el-select-dropdown:visible').last();
}

/**
 * Helper: click an el-select, then select an option from the visible dropdown.
 * Uses force click on the option because Element Plus teleported poppers can
 * become "not stable" during animation transitions.
 */
async function selectOption(page: any, selectLocator: any, optionText: string) {
  await selectLocator.click({ force: true });
  await page.waitForTimeout(300);
  const visibleDropdown = getVisibleDropdown(page);
  await expect(visibleDropdown).toBeVisible({ timeout: 3000 });
  await visibleDropdown.locator('.el-select-dropdown__item').filter({ hasText: optionText }).first().click({ force: true });
}

/**
 * Helper: click an el-select, then pick the first option from the visible dropdown.
 */
async function selectFirstOption(page: any, selectLocator: any) {
  await selectLocator.click({ force: true });
  await page.waitForTimeout(300);
  const visibleDropdown = getVisibleDropdown(page);
  await expect(visibleDropdown).toBeVisible({ timeout: 3000 });
  await visibleDropdown.locator('.el-select-dropdown__item').first().click({ force: true });
}

test.describe('物料信息 E2E 测试', () => {
  const TS = Date.now().toString(36);
  const MAT_CODE = `E2E_MAT_${TS}`;
  const MAT_NAME = `E2E测试物料_${TS}`;
  const MAT_CODE_PCB = `E2E_MAT_PCB_${TS}`;
  const MAT_NAME_PCB = `E2E测试PCB物料_${TS}`;
  const MAT_CODE_EDIT = `E2E_MAT_EDIT_${TS}`;
  const MAT_NAME_EDIT = `E2E编辑物料_${TS}`;
  const MAT_CODE_DEL = `E2E_MAT_DEL_${TS}`;
  const MAT_NAME_DEL = `E2E删除物料_${TS}`;

  test.beforeEach(async ({ authenticatedPage: page }) => {
    await page.goto('/materials');
    await page.waitForLoadState('networkidle');
  });

  test('物料信息列表加载', async ({ authenticatedPage: page }) => {
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });
  });

  test('新建物料（基本信息）', async ({ authenticatedPage: page }) => {
    await page.locator('button:has-text("新建物料")').click();
    const dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    // Fill basic fields
    await dialog.locator('input[placeholder="请输入物料编码"]').fill(MAT_CODE);
    await dialog.locator('input[placeholder="请输入物料名称"]').fill(MAT_NAME);

    // Select basic category (基本分类)
    const basicCatSelect = dialog.locator('.el-form-item').filter({ hasText: '基本分类' }).locator('.el-select');
    await selectOption(page, basicCatSelect, '原材料');

    // Select material category (物料分类)
    const matCatSelect = dialog.locator('.el-form-item').filter({ hasText: '物料分类' }).locator('.el-select');
    await selectFirstOption(page, matCatSelect);

    // Select attribute category (属性分类)
    const attrCatSelect = dialog.locator('.el-form-item').filter({ hasText: '属性分类' }).locator('.el-select');
    await selectOption(page, attrCatSelect, '采购件');

    // Select UOM (计量单位)
    const uomSelect = dialog.locator('.el-form-item').filter({ hasText: '单位' }).locator('.el-select');
    await selectFirstOption(page, uomSelect);

    await dialog.locator('button:has-text("确定")').click();

    // Wait for dialog to close
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });

    // Verify material appears in table
    await assertTableContains(page, MAT_CODE);
  });

  test('新建物料含 PCB 属性', async ({ authenticatedPage: page }) => {
    await page.locator('button:has-text("新建物料")').click();
    const dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    // Fill basic fields
    await dialog.locator('input[placeholder="请输入物料编码"]').fill(MAT_CODE_PCB);
    await dialog.locator('input[placeholder="请输入物料名称"]').fill(MAT_NAME_PCB);

    // Select basic category
    const basicCatSelect = dialog.locator('.el-form-item').filter({ hasText: '基本分类' }).locator('.el-select');
    await selectOption(page, basicCatSelect, '原材料');

    // Select material category
    const matCatSelect = dialog.locator('.el-form-item').filter({ hasText: '物料分类' }).locator('.el-select');
    await selectFirstOption(page, matCatSelect);

    // Select attribute category
    const attrCatSelect = dialog.locator('.el-form-item').filter({ hasText: '属性分类' }).locator('.el-select');
    await selectOption(page, attrCatSelect, '采购件');

    // Select UOM
    const uomSelect = dialog.locator('.el-form-item').filter({ hasText: '单位' }).locator('.el-select');
    await selectFirstOption(page, uomSelect);

    // Fill PCB attributes section
    // Use locator('.el-form-item__label').filter({ hasText: /^长$/ }) for exact label matching
    // to avoid "长" matching "刃长"/"总长"
    const sizeInput = dialog.locator('.el-form-item__label').filter({ hasText: /^尺寸$/ }).locator('..').locator('.el-input-number .el-input__inner');
    await sizeInput.fill('1.5');

    const lengthInput = dialog.locator('.el-form-item__label').filter({ hasText: /^长$/ }).locator('..').locator('.el-input-number .el-input__inner');
    await lengthInput.fill('100');

    const widthInput = dialog.locator('.el-form-item__label').filter({ hasText: /^宽$/ }).locator('..').locator('.el-input-number .el-input__inner');
    await widthInput.fill('50');

    await dialog.locator('input[placeholder="型号"]').fill('FR-4');
    await dialog.locator('input[placeholder="规格"]').fill('100x50x1.6');

    const thicknessInput = dialog.locator('.el-form-item__label').filter({ hasText: /^厚度$/ }).locator('..').locator('.el-input-number .el-input__inner');
    await thicknessInput.fill('1.6');

    await dialog.locator('input[placeholder="颜色"]').fill('绿');
    await dialog.locator('input[placeholder="TG值"]').fill('TG150');
    await dialog.locator('input[placeholder="铜厚"]').fill('35um');

    // Is copper contained (是否含铜)
    const copperSelect = dialog.locator('.el-form-item').filter({ hasText: '是否含铜' }).locator('.el-select');
    await selectOption(page, copperSelect, '是');

    await dialog.locator('button:has-text("确定")').click();

    // Wait for dialog to close
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });

    // Verify material appears in table
    await assertTableContains(page, MAT_CODE_PCB);
  });

  test('物料编码必填校验', async ({ authenticatedPage: page }) => {
    await page.locator('button:has-text("新建物料")').click();
    const dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    // Click confirm without filling any fields
    await dialog.locator('button:has-text("确定")').click();

    // Expect validation error message
    await expect(dialog.locator('.text-red-500')).toBeVisible({ timeout: 3000 });
  });

  test('编辑物料', async ({ authenticatedPage: page }) => {
    // First create a material to edit
    await page.locator('button:has-text("新建物料")').click();
    let dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });
    await dialog.locator('input[placeholder="请输入物料编码"]').fill(MAT_CODE_EDIT);
    await dialog.locator('input[placeholder="请输入物料名称"]').fill(MAT_NAME_EDIT);

    const basicCatSelect = dialog.locator('.el-form-item').filter({ hasText: '基本分类' }).locator('.el-select');
    await selectOption(page, basicCatSelect, '原材料');

    const matCatSelect = dialog.locator('.el-form-item').filter({ hasText: '物料分类' }).locator('.el-select');
    await selectFirstOption(page, matCatSelect);

    const attrCatSelect = dialog.locator('.el-form-item').filter({ hasText: '属性分类' }).locator('.el-select');
    await selectOption(page, attrCatSelect, '采购件');

    const uomSelect = dialog.locator('.el-form-item').filter({ hasText: '单位' }).locator('.el-select');
    await selectFirstOption(page, uomSelect);

    await dialog.locator('button:has-text("确定")').click();
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });
    await assertTableContains(page, MAT_CODE_EDIT);

    // Now edit
    const row = await findTableRowByText(page, MAT_CODE_EDIT);
    await clickRowAction(row, '编辑');
    dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    // Code should be read-only
    const codeInput = dialog.locator('input[placeholder="请输入物料编码"]');
    await expect(codeInput).toBeDisabled();

    // Update name
    const nameInput = dialog.locator('input[placeholder="请输入物料名称"]');
    await nameInput.clear();
    await nameInput.fill(`${MAT_NAME_EDIT}_编辑后`);

    await dialog.locator('button:has-text("确定")').click();
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });

    // Verify name update
    await assertTableContains(page, `${MAT_NAME_EDIT}_编辑后`);
  });

  test('删除物料', async ({ authenticatedPage: page }) => {
    // First create a material to delete
    await page.locator('button:has-text("新建物料")').click();
    let dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });
    await dialog.locator('input[placeholder="请输入物料编码"]').fill(MAT_CODE_DEL);
    await dialog.locator('input[placeholder="请输入物料名称"]').fill(MAT_NAME_DEL);

    const basicCatSelect = dialog.locator('.el-form-item').filter({ hasText: '基本分类' }).locator('.el-select');
    await selectOption(page, basicCatSelect, '原材料');

    const matCatSelect = dialog.locator('.el-form-item').filter({ hasText: '物料分类' }).locator('.el-select');
    await selectFirstOption(page, matCatSelect);

    const attrCatSelect = dialog.locator('.el-form-item').filter({ hasText: '属性分类' }).locator('.el-select');
    await selectOption(page, attrCatSelect, '采购件');

    const uomSelect = dialog.locator('.el-form-item').filter({ hasText: '单位' }).locator('.el-select');
    await selectFirstOption(page, uomSelect);

    await dialog.locator('button:has-text("确定")').click();
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });
    await assertTableContains(page, MAT_CODE_DEL);

    // Delete
    const row = await findTableRowByText(page, MAT_CODE_DEL);
    await deleteWithConfirm(page, row);

    // Verify deletion
    await assertTableNotContains(page, MAT_CODE_DEL);
  });
});
