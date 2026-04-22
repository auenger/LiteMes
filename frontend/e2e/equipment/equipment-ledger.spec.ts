import { test, expect } from '../fixtures/auth';
import {
  findTableRowByText,
  clickRowAction,
  assertTableContains,
  assertTableNotContains,
} from '../helpers/common';

/**
 * Helper: click an el-select, then select an option from the visible dropdown.
 * Uses force click and waits for the dropdown popper to appear.
 */
async function selectOption(page: any, selectLocator: any, optionText: string) {
  // Ensure the select element is visible before clicking
  await expect(selectLocator).toBeVisible({ timeout: 5000 });
  await selectLocator.click({ force: true });
  // Wait longer for Element Plus popper animation
  await page.waitForTimeout(500);
  const visibleDropdown = page.locator('.el-select-dropdown:visible').last();
  await expect(visibleDropdown).toBeVisible({ timeout: 5000 });
  await visibleDropdown.locator('.el-select-dropdown__item').filter({ hasText: optionText }).first().click({ force: true });
  await page.waitForTimeout(300);
}

/**
 * Helper: click an el-select, then pick the first option from the visible dropdown.
 */
async function selectFirstOption(page: any, selectLocator: any) {
  await expect(selectLocator).toBeVisible({ timeout: 5000 });
  await selectLocator.click({ force: true });
  await page.waitForTimeout(500);
  const visibleDropdown = page.locator('.el-select-dropdown:visible').last();
  await expect(visibleDropdown).toBeVisible({ timeout: 5000 });
  await visibleDropdown.locator('.el-select-dropdown__item').first().click({ force: true });
  await page.waitForTimeout(300);
}

/**
 * Helper: create an equipment type via UI for prerequisite data.
 */
async function createEquipmentTypeViaUI(page: any, code: string, name: string) {
  await page.goto('/equipment-types');
  await page.waitForLoadState('networkidle');
  await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });

  await page.locator('button:has-text("新建设备类型")').click();
  const dialog = page.locator('.el-dialog:visible');
  await expect(dialog).toBeVisible({ timeout: 3000 });

  await dialog.locator('input[placeholder="请输入设备类型编码"]').fill(code);
  await dialog.locator('input[placeholder="请输入设备类型名称"]').fill(name);
  await dialog.locator('button:has-text("确定")').click();
  await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });
  await assertTableContains(page, code);
}

/**
 * Helper: create an equipment model via UI for prerequisite data.
 */
async function createEquipmentModelViaUI(page: any, modelCode: string, modelName: string, typeCode: string, typeName: string) {
  // First ensure the equipment type exists
  await createEquipmentTypeViaUI(page, typeCode, typeName);

  // Navigate to equipment models
  await page.goto('/equipment-models');
  await page.waitForLoadState('networkidle');
  await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });

  await page.locator('button:has-text("新建设备型号")').click();
  const dialog = page.locator('.el-dialog:visible');
  await expect(dialog).toBeVisible({ timeout: 3000 });

  await dialog.locator('input[placeholder="请输入设备型号编码"]').fill(modelCode);
  await dialog.locator('input[placeholder="请输入设备型号名称"]').fill(modelName);

  // Select equipment type
  const typeSelect = dialog.locator('.el-form-item').filter({ hasText: '设备类型' }).locator('.el-select');
  await selectOption(page, typeSelect, typeCode);

  await dialog.locator('button:has-text("确定")').click();
  await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });
  await assertTableContains(page, modelCode);
}

test.describe('设备台账 E2E 测试', () => {
  const TS = Date.now().toString(36);

  // Type & Model for basic CRUD
  const TYPE_CODE = `E2E_EQ_TYPE_L_${TS}`;
  const TYPE_NAME = `E2E台账测试类型_${TS}`;
  const MODEL_CODE = `E2E_EQ_MODEL_L_${TS}`;
  const MODEL_NAME = `E2E台账测试型号_${TS}`;
  // Ledger codes
  const LEDGER_CODE = `E2E_EQ_LEDGER_${TS}`;
  const LEDGER_NAME = `E2E测试设备_${TS}`;
  const LEDGER_CODE_EDIT = `E2E_EQ_LEDGER_ED_${TS}`;
  const LEDGER_NAME_EDIT = `E2E编辑设备_${TS}`;
  const LEDGER_CODE_DEL = `E2E_EQ_LEDGER_DL_${TS}`;
  const LEDGER_NAME_DEL = `E2E删除设备_${TS}`;
  // Extra type/model for edit and delete tests
  const TYPE_CODE_EDIT = `E2E_EQ_TYPE_LE_${TS}`;
  const TYPE_NAME_EDIT = `E2E台账编辑类型_${TS}`;
  const MODEL_CODE_EDIT = `E2E_EQ_MODEL_LE_${TS}`;
  const MODEL_NAME_EDIT = `E2E台账编辑型号_${TS}`;
  const TYPE_CODE_DEL = `E2E_EQ_TYPE_LD_${TS}`;
  const TYPE_NAME_DEL = `E2E台账删除类型_${TS}`;
  const MODEL_CODE_DEL = `E2E_EQ_MODEL_LD_${TS}`;
  const MODEL_NAME_DEL = `E2E台账删除型号_${TS}`;
  // Type/Model for cascade test
  const TYPE_CODE_CASCADE = `E2E_EQ_TYPE_CAS_${TS}`;
  const TYPE_NAME_CASCADE = `E2E级联测试类型_${TS}`;
  const MODEL_CODE_CASCADE_1 = `E2E_EQ_MODEL_CAS1_${TS}`;
  const MODEL_NAME_CASCADE_1 = `E2E级联型号1_${TS}`;
  const MODEL_CODE_CASCADE_2 = `E2E_EQ_MODEL_CAS2_${TS}`;
  const MODEL_NAME_CASCADE_2 = `E2E级联型号2_${TS}`;

  test.beforeEach(async ({ authenticatedPage: page }) => {
    await page.goto('/equipment-ledger');
    await page.waitForLoadState('networkidle');
  });

  test('设备台账列表加载', async ({ authenticatedPage: page }) => {
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });
  });

  test('新建设备（完整表单）', async ({ authenticatedPage: page }) => {
    // Create prerequisite type and model
    await createEquipmentModelViaUI(page, MODEL_CODE, MODEL_NAME, TYPE_CODE, TYPE_NAME);

    // Navigate to equipment ledger and wait for full load
    await page.goto('/equipment-ledger');
    await page.waitForLoadState('networkidle');
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });
    // Wait for dropdown data to load (fetchDropdownData runs on mount)
    await page.waitForTimeout(1000);

    await page.locator('button:has-text("新建设备")').click();
    const dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    // Fill basic fields
    await dialog.locator('input[placeholder="请输入设备编码"]').fill(LEDGER_CODE);
    await dialog.locator('input[placeholder="请输入设备名称"]').fill(LEDGER_NAME);

    // Select equipment model (which auto-fills type name)
    const modelSelect = dialog.locator('.el-select').first();
    await selectOption(page, modelSelect, MODEL_CODE);

    // Select running status - use the select that has "运行" option
    const runningStatusSelect = dialog.locator('.el-form-item').filter({ hasText: '运行状态' }).locator('.el-select');
    await selectOption(page, runningStatusSelect, '运行');

    // Select manage status
    const manageStatusSelect = dialog.locator('.el-form-item').filter({ hasText: '管理状态' }).locator('.el-select');
    await selectOption(page, manageStatusSelect, '使用中');

    // Select factory
    const factorySelect = dialog.locator('.el-form-item').filter({ hasText: '工厂' }).locator('.el-select');
    await selectFirstOption(page, factorySelect);

    // Fill manufacturer
    await dialog.locator('input[placeholder="请输入生产厂家"]').fill('E2E测试厂商');

    // Fill commissioning date (HTML date input)
    const today = new Date().toISOString().split('T')[0];
    await dialog.locator('input[type="date"]').fill(today);

    await dialog.locator('button:has-text("确定")').click();

    // Wait for dialog to close
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });

    // Verify equipment appears in table
    await assertTableContains(page, LEDGER_CODE);
  });

  test('级联选择器联动验证', async ({ authenticatedPage: page }) => {
    // Create prerequisite type with 2 models
    await createEquipmentTypeViaUI(page, TYPE_CODE_CASCADE, TYPE_NAME_CASCADE);

    // Create first model
    await page.goto('/equipment-models');
    await page.waitForLoadState('networkidle');
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });

    await page.locator('button:has-text("新建设备型号")').click();
    let dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });
    await dialog.locator('input[placeholder="请输入设备型号编码"]').fill(MODEL_CODE_CASCADE_1);
    await dialog.locator('input[placeholder="请输入设备型号名称"]').fill(MODEL_NAME_CASCADE_1);
    const typeSelect1 = dialog.locator('.el-form-item').filter({ hasText: '设备类型' }).locator('.el-select');
    await selectOption(page, typeSelect1, TYPE_CODE_CASCADE);
    await dialog.locator('button:has-text("确定")').click();
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });
    await assertTableContains(page, MODEL_CODE_CASCADE_1);

    // Create second model under same type
    await page.locator('button:has-text("新建设备型号")').click();
    dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });
    await dialog.locator('input[placeholder="请输入设备型号编码"]').fill(MODEL_CODE_CASCADE_2);
    await dialog.locator('input[placeholder="请输入设备型号名称"]').fill(MODEL_NAME_CASCADE_2);
    const typeSelect2 = dialog.locator('.el-form-item').filter({ hasText: '设备类型' }).locator('.el-select');
    await selectOption(page, typeSelect2, TYPE_CODE_CASCADE);
    await dialog.locator('button:has-text("确定")').click();
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });
    await assertTableContains(page, MODEL_CODE_CASCADE_2);

    // Now go to equipment ledger and verify cascade filtering in search bar
    await page.goto('/equipment-ledger');
    await page.waitForLoadState('networkidle');
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });
    // Wait for dropdown data to load
    await page.waitForTimeout(1000);

    // Open the filter's equipment type dropdown and select the cascade type
    // The filter form uses el-select with placeholder "全部设备类型"
    const filterTypeSelect = page.locator('.el-select').filter({ hasText: '全部设备类型' }).first();
    await selectOption(page, filterTypeSelect, TYPE_CODE_CASCADE);

    // Now open the filter's equipment model dropdown and verify only models of the selected type appear
    const filterModelSelect = page.locator('.el-select').filter({ hasText: '全部设备型号' }).first();
    await filterModelSelect.click({ force: true });
    await page.waitForTimeout(500);
    const filterDropdown = page.locator('.el-select-dropdown:visible').last();
    await expect(filterDropdown).toBeVisible({ timeout: 5000 });

    // Both cascade models should appear
    await expect(filterDropdown.locator('.el-select-dropdown__item').filter({ hasText: MODEL_CODE_CASCADE_1 }).first()).toBeVisible({ timeout: 3000 });
    await expect(filterDropdown.locator('.el-select-dropdown__item').filter({ hasText: MODEL_CODE_CASCADE_2 }).first()).toBeVisible({ timeout: 3000 });

    // Close dropdown
    await page.keyboard.press('Escape');
  });

  test('编辑设备台账', async ({ authenticatedPage: page }) => {
    // Create prerequisite type, model and equipment
    await createEquipmentModelViaUI(page, MODEL_CODE_EDIT, MODEL_NAME_EDIT, TYPE_CODE_EDIT, TYPE_NAME_EDIT);

    // Navigate to equipment ledger and create equipment
    await page.goto('/equipment-ledger');
    await page.waitForLoadState('networkidle');
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });
    // Wait for dropdown data to load
    await page.waitForTimeout(1000);

    await page.locator('button:has-text("新建设备")').click();
    let dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });
    await dialog.locator('input[placeholder="请输入设备编码"]').fill(LEDGER_CODE_EDIT);
    await dialog.locator('input[placeholder="请输入设备名称"]').fill(LEDGER_NAME_EDIT);

    const modelSelect = dialog.locator('.el-select').first();
    await selectOption(page, modelSelect, MODEL_CODE_EDIT);

    const runningStatusSelect = dialog.locator('.el-form-item').filter({ hasText: '运行状态' }).locator('.el-select');
    await selectOption(page, runningStatusSelect, '运行');

    const manageStatusSelect = dialog.locator('.el-form-item').filter({ hasText: '管理状态' }).locator('.el-select');
    await selectOption(page, manageStatusSelect, '使用中');

    const factorySelect = dialog.locator('.el-form-item').filter({ hasText: '工厂' }).locator('.el-select');
    await selectFirstOption(page, factorySelect);

    await dialog.locator('input[placeholder="请输入生产厂家"]').fill('E2E编辑厂商');
    const today = new Date().toISOString().split('T')[0];
    await dialog.locator('input[type="date"]').fill(today);

    await dialog.locator('button:has-text("确定")').click();
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });
    await assertTableContains(page, LEDGER_CODE_EDIT);

    // Now edit the equipment
    const row = await findTableRowByText(page, LEDGER_CODE_EDIT);
    await clickRowAction(row, '编辑');
    dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });

    // Code should be read-only
    const codeInput = dialog.locator('input[placeholder="请输入设备编码"]');
    await expect(codeInput).toBeDisabled();

    // Update name
    const nameInput = dialog.locator('input[placeholder="请输入设备名称"]');
    await nameInput.clear();
    await nameInput.fill(`${LEDGER_NAME_EDIT}_编辑后`);

    // Update running status to MAINTENANCE
    const editRunningStatus = dialog.locator('.el-form-item').filter({ hasText: '运行状态' }).locator('.el-select');
    await selectOption(page, editRunningStatus, '维修保养');

    await dialog.locator('button:has-text("确定")').click();
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });

    // Verify name update
    await assertTableContains(page, `${LEDGER_NAME_EDIT}_编辑后`);
  });

  test('删除设备台账', async ({ authenticatedPage: page }) => {
    // Create prerequisite type, model and equipment
    await createEquipmentModelViaUI(page, MODEL_CODE_DEL, MODEL_NAME_DEL, TYPE_CODE_DEL, TYPE_NAME_DEL);

    // Navigate to equipment ledger and create equipment
    await page.goto('/equipment-ledger');
    await page.waitForLoadState('networkidle');
    await expect(page.locator('.el-table')).toBeVisible({ timeout: 10000 });
    // Wait for dropdown data to load
    await page.waitForTimeout(1000);

    await page.locator('button:has-text("新建设备")').click();
    let dialog = page.locator('.el-dialog:visible');
    await expect(dialog).toBeVisible({ timeout: 3000 });
    await dialog.locator('input[placeholder="请输入设备编码"]').fill(LEDGER_CODE_DEL);
    await dialog.locator('input[placeholder="请输入设备名称"]').fill(LEDGER_NAME_DEL);

    const modelSelect = dialog.locator('.el-select').first();
    await selectOption(page, modelSelect, MODEL_CODE_DEL);

    const runningStatusSelect = dialog.locator('.el-form-item').filter({ hasText: '运行状态' }).locator('.el-select');
    await selectOption(page, runningStatusSelect, '运行');

    const manageStatusSelect = dialog.locator('.el-form-item').filter({ hasText: '管理状态' }).locator('.el-select');
    await selectOption(page, manageStatusSelect, '使用中');

    const factorySelect = dialog.locator('.el-form-item').filter({ hasText: '工厂' }).locator('.el-select');
    await selectFirstOption(page, factorySelect);

    await dialog.locator('input[placeholder="请输入生产厂家"]').fill('E2E删除厂商');
    const today = new Date().toISOString().split('T')[0];
    await dialog.locator('input[type="date"]').fill(today);

    await dialog.locator('button:has-text("确定")').click();
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });
    await assertTableContains(page, LEDGER_CODE_DEL);

    // Delete the equipment
    const row = await findTableRowByText(page, LEDGER_CODE_DEL);
    await clickRowAction(row, '删除');

    // Confirm delete dialog
    const deleteDialog = page.locator('.el-dialog:visible').filter({ hasText: '确认删除' });
    await expect(deleteDialog).toBeVisible({ timeout: 3000 });
    await deleteDialog.locator('button:has-text("删除")').click();

    // Wait for dialog to close
    await expect(page.locator('.el-dialog:visible')).not.toBeVisible({ timeout: 5000 });

    // Verify deletion
    await assertTableNotContains(page, LEDGER_CODE_DEL);
  });
});
