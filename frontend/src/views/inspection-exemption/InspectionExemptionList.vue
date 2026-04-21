<template>
  <div class="inspection-exemption-page">
    <div class="page-header">
      <h2>免检清单管理</h2>
      <div class="header-actions">
        <div class="table-settings-wrapper" style="position: relative;">
          <button class="btn" @click="showTableSettings = !showTableSettings">表格设置</button>
          <TableSettingsPanel
            :visible="showTableSettings"
            :columns="columns"
            @close="showTableSettings = false"
          />
        </div>
        <button class="btn btn-primary" @click="openCreateDialog">新建免检规则</button>
      </div>
    </div>

    <!-- Search Bar -->
    <div class="search-bar">
      <select v-model="query.materialId" class="input select">
        <option :value="undefined">全部物料</option>
        <option v-for="m in materialOptions" :key="m.id" :value="m.id">
          {{ m.code }} - {{ m.name }}
        </option>
      </select>
      <select v-model="query.status" class="input select">
        <option :value="undefined">全部状态</option>
        <option :value="1">启用</option>
        <option :value="0">禁用</option>
      </select>
      <button class="btn" @click="search">查询</button>
      <button class="btn btn-secondary" @click="resetQuery">重置</button>
    </div>

    <!-- Inspection Exemption Table -->
    <table class="table">
      <thead>
        <tr>
          <th v-if="isColumnVisible('materialCode')">物料编码</th>
          <th v-if="isColumnVisible('materialName')">物料名称</th>
          <th v-if="isColumnVisible('supplierCode')">供应商编码</th>
          <th v-if="isColumnVisible('supplierName')">供应商名称</th>
          <th v-if="isColumnVisible('validPeriod')">有效期</th>
          <th v-if="isColumnVisible('status')">状态</th>
          <th v-if="isColumnVisible('createdBy')">创建人</th>
          <th v-if="isColumnVisible('createdAt')">创建时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="loading">
          <td :colspan="visibleColCount + 1" class="text-center">加载中...</td>
        </tr>
        <tr v-else-if="exemptions.length === 0">
          <td :colspan="visibleColCount + 1" class="text-center">暂无数据</td>
        </tr>
        <tr
          v-for="item in exemptions"
          :key="item.id"
          :class="{ 'row-expired': item.expired }"
        >
          <td v-if="isColumnVisible('materialCode')">{{ item.materialCode }}</td>
          <td v-if="isColumnVisible('materialName')">{{ item.materialName }}</td>
          <td v-if="isColumnVisible('supplierCode')">{{ item.supplierCode || '-' }}</td>
          <td v-if="isColumnVisible('supplierName')">{{ item.supplierName || '-' }}</td>
          <td v-if="isColumnVisible('validPeriod')">
            <template v-if="item.validFrom || item.validTo">
              {{ item.validFrom || '...' }} ~ {{ item.validTo || '...' }}
            </template>
            <template v-else>
              <span class="text-muted">永久</span>
            </template>
          </td>
          <td v-if="isColumnVisible('status')">
            <span :class="['status-badge', getStatusClass(item)]">
              {{ getStatusLabel(item) }}
            </span>
          </td>
          <td v-if="isColumnVisible('createdBy')">{{ item.createdBy || '-' }}</td>
          <td v-if="isColumnVisible('createdAt')">{{ formatDate(item.createdAt) }}</td>
          <td class="actions">
            <button class="btn btn-sm" @click="openEditDialog(item)">编辑</button>
            <button
              v-if="item.status === 1 && !item.expired"
              class="btn btn-sm btn-warning"
              @click="toggleStatus(item)"
            >
              禁用
            </button>
            <button
              v-else-if="item.status === 0"
              class="btn btn-sm btn-success"
              @click="toggleStatus(item)"
            >
              启用
            </button>
            <button class="btn btn-sm btn-danger" @click="confirmDelete(item)">删除</button>
            <button class="btn btn-sm btn-info" @click="openAuditLog(item)">变更履历</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Pagination -->
    <div class="pagination" v-if="total > 0">
      <span class="pagination-info">
        共 {{ total }} 条，第 {{ query.page || 1 }} / {{ totalPages }} 页
      </span>
      <button class="btn btn-sm" :disabled="query.page <= 1" @click="goPage(1)">首页</button>
      <button class="btn btn-sm" :disabled="query.page <= 1" @click="goPage((query.page || 1) - 1)">上一页</button>
      <button class="btn btn-sm" :disabled="query.page >= totalPages" @click="goPage((query.page || 1) + 1)">下一页</button>
      <button class="btn btn-sm" :disabled="query.page >= totalPages" @click="goPage(totalPages)">末页</button>
    </div>

    <!-- Create/Edit Dialog -->
    <div v-if="dialogVisible" class="dialog-overlay" @click.self="closeDialog">
      <div class="dialog">
        <div class="dialog-header">
          <h3>{{ isEdit ? '编辑免检规则' : '新建免检规则' }}</h3>
          <button class="dialog-close" @click="closeDialog">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label>物料 <span class="required">*</span></label>
            <select v-model="form.materialId" class="input select" :disabled="isEdit">
              <option :value="null" disabled>请选择物料</option>
              <option v-for="m in materialOptions" :key="m.id" :value="m.id">
                {{ m.code }} - {{ m.name }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>供应商</label>
            <input
              v-model="form.supplierDisplay"
              type="text"
              class="input"
              disabled
              placeholder="供应商模块尚未开发，暂不可选"
            />
            <small class="form-hint">供应商功能开发后可选择，留空表示全局免检</small>
          </div>
          <div class="form-group">
            <label>有效开始日期</label>
            <input
              v-model="form.validFrom"
              type="date"
              class="input"
            />
          </div>
          <div class="form-group">
            <label>有效结束日期</label>
            <input
              v-model="form.validTo"
              type="date"
              class="input"
            />
            <small class="form-hint">留空表示永久有效</small>
          </div>
          <div class="form-group" v-if="!isEdit">
            <label>规则说明</label>
            <p class="rule-description">{{ getRuleDescription() }}</p>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn" @click="closeDialog">取消</button>
          <button class="btn btn-primary" @click="submitForm" :disabled="submitting">
            {{ submitting ? '提交中...' : '确定' }}
          </button>
        </div>
        <div v-if="formError" class="form-error">{{ formError }}</div>
      </div>
    </div>

    <!-- Delete Confirm Dialog -->
    <div v-if="deleteDialogVisible" class="dialog-overlay" @click.self="closeDeleteDialog">
      <div class="dialog dialog-sm">
        <div class="dialog-header">
          <h3>确认删除</h3>
          <button class="dialog-close" @click="closeDeleteDialog">&times;</button>
        </div>
        <div class="dialog-body">
          <p>确定要删除免检规则 "{{ deleteTarget?.materialName }}" ({{ deleteTarget?.materialCode }}) 吗？</p>
          <p class="text-muted">删除后不可恢复。</p>
        </div>
        <div class="dialog-footer">
          <button class="btn" @click="closeDeleteDialog">取消</button>
          <button class="btn btn-danger" @click="doDelete" :disabled="submitting">
            {{ submitting ? '删除中...' : '删除' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Audit Log Dialog -->
    <AuditLogDialog
      :visible="auditLogVisible"
      :tableName="auditLogTableName"
      :recordId="auditLogRecordId"
      @close="auditLogVisible = false"
      @update:visible="auditLogVisible = $event"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import {
  listInspectionExemptions,
  createInspectionExemption,
  updateInspectionExemption,
  deleteInspectionExemption,
  updateInspectionExemptionStatus,
  type InspectionExemptionDto,
  type InspectionExemptionQueryParams,
} from '../../api/inspectionExemption';
import { getMaterialDropdown, type DropdownItem } from '../../api/dropdown';
import AuditLogDialog from '../../components/AuditLogDialog.vue';
import TableSettingsPanel from '../../components/TableSettingsPanel.vue';
import { useTableSettings, type ColumnDef } from '../../components/useTableSettings';

const exemptions = ref<InspectionExemptionDto[]>([]);
const materialOptions = ref<DropdownItem[]>([]);
const total = ref(0);
const loading = ref(false);
const submitting = ref(false);
const formError = ref('');

const query = reactive<InspectionExemptionQueryParams>({
  materialId: undefined,
  supplierId: undefined,
  status: undefined,
  page: 1,
  size: 10,
});

const totalPages = computed(() => Math.ceil(total.value / (query.size || 10)));

// Table settings
const defaultColumns: ColumnDef[] = [
  { key: 'materialCode', label: '物料编码' },
  { key: 'materialName', label: '物料名称' },
  { key: 'supplierCode', label: '供应商编码' },
  { key: 'supplierName', label: '供应商名称' },
  { key: 'validPeriod', label: '有效期' },
  { key: 'status', label: '状态' },
  { key: 'createdBy', label: '创建人' },
  { key: 'createdAt', label: '创建时间' },
];

const { columns, toggleColumn, resetSettings } = useTableSettings('inspection-exemption-list', defaultColumns);
const showTableSettings = ref(false);

function isColumnVisible(key: string): boolean {
  return columns.value.find(c => c.key === key)?.visible !== false;
}

const visibleColCount = computed(() => columns.value.filter(c => c.visible).length);

// Audit log dialog state
const auditLogVisible = ref(false);
const auditLogTableName = ref('inspection_exemption');
const auditLogRecordId = ref(0);

function openAuditLog(item: InspectionExemptionDto) {
  auditLogTableName.value = 'inspection_exemption';
  auditLogRecordId.value = item.id;
  auditLogVisible.value = true;
}

// Dialog state
const dialogVisible = ref(false);
const isEdit = ref(false);
const editingId = ref<number | null>(null);
const form = reactive({
  materialId: null as number | null,
  supplierDisplay: '',
  validFrom: '' as string,
  validTo: '' as string,
});

// Delete dialog state
const deleteDialogVisible = ref(false);
const deleteTarget = ref<InspectionExemptionDto | null>(null);

async function fetchMaterials() {
  try {
    const res = await getMaterialDropdown();
    if (res.code === 200 && res.data) {
      materialOptions.value = res.data;
    }
  } catch (e) {
    console.error('Failed to fetch material dropdown', e);
  }
}

async function fetchList() {
  loading.value = true;
  try {
    const res = await listInspectionExemptions(query);
    if (res.code === 200 && res.data) {
      exemptions.value = res.data.records;
      total.value = res.data.total;
    }
  } catch (e) {
    console.error('Failed to fetch inspection exemption list', e);
  } finally {
    loading.value = false;
  }
}

function search() {
  query.page = 1;
  fetchList();
}

function resetQuery() {
  query.materialId = undefined;
  query.supplierId = undefined;
  query.status = undefined;
  query.page = 1;
  fetchList();
}

function goPage(page: number) {
  query.page = page;
  fetchList();
}

function openCreateDialog() {
  isEdit.value = false;
  editingId.value = null;
  form.materialId = null;
  form.supplierDisplay = '';
  form.validFrom = '';
  form.validTo = '';
  formError.value = '';
  dialogVisible.value = true;
}

function openEditDialog(item: InspectionExemptionDto) {
  isEdit.value = true;
  editingId.value = item.id;
  form.materialId = item.materialId;
  form.supplierDisplay = item.supplierName || '';
  form.validFrom = item.validFrom || '';
  form.validTo = item.validTo || '';
  formError.value = '';
  dialogVisible.value = true;
}

function closeDialog() {
  dialogVisible.value = false;
}

function getRuleDescription(): string {
  const hasSupplier = false; // supplier not available yet
  const hasValidity = !!(form.validFrom || form.validTo);

  if (!hasSupplier && !hasValidity) {
    return '全局永久免检：所有供应商的该物料均免检';
  } else if (hasSupplier && !hasValidity) {
    return '指定供应商永久免检';
  } else if (hasSupplier && hasValidity) {
    return '指定供应商+有效期免检';
  } else {
    return '全局有效期免检：所有供应商的该物料在有效期内免检';
  }
}

async function submitForm() {
  if (!form.materialId) {
    formError.value = '请选择物料';
    return;
  }

  // Validate date range
  if (form.validFrom && form.validTo && form.validFrom > form.validTo) {
    formError.value = '有效开始日期不能晚于有效结束日期';
    return;
  }

  submitting.value = true;
  formError.value = '';
  try {
    const payload: any = {
      materialId: form.materialId,
      validFrom: form.validFrom || null,
      validTo: form.validTo || null,
    };

    if (isEdit.value && editingId.value) {
      await updateInspectionExemption(editingId.value, payload);
    } else {
      await createInspectionExemption(payload);
    }
    closeDialog();
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '操作失败';
    formError.value = msg;
  } finally {
    submitting.value = false;
  }
}

function confirmDelete(item: InspectionExemptionDto) {
  deleteTarget.value = item;
  deleteDialogVisible.value = true;
}

function closeDeleteDialog() {
  deleteDialogVisible.value = false;
  deleteTarget.value = null;
}

async function doDelete() {
  if (!deleteTarget.value) return;
  submitting.value = true;
  try {
    await deleteInspectionExemption(deleteTarget.value.id);
    closeDeleteDialog();
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '删除失败';
    alert(msg);
  } finally {
    submitting.value = false;
  }
}

async function toggleStatus(item: InspectionExemptionDto) {
  const newStatus = item.status === 1 ? 0 : 1;
  try {
    await updateInspectionExemptionStatus(item.id, newStatus);
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '状态更新失败';
    alert(msg);
  }
}

function getStatusClass(item: InspectionExemptionDto): string {
  if (item.expired) return 'status-expired';
  return item.status === 1 ? 'status-enabled' : 'status-disabled';
}

function getStatusLabel(item: InspectionExemptionDto): string {
  if (item.expired) return '已过期';
  return item.status === 1 ? '启用' : '禁用';
}

function formatDate(dateStr: string | undefined) {
  if (!dateStr) return '-';
  return new Date(dateStr).toLocaleString('zh-CN');
}

onMounted(() => {
  fetchMaterials();
  fetchList();
});
</script>

<style scoped>
.inspection-exemption-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.input {
  padding: 6px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}

.input:focus {
  border-color: #1890ff;
}

.input-disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.select {
  min-width: 120px;
}

.btn {
  padding: 6px 16px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  background: #fff;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.btn:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-primary {
  background: #1890ff;
  color: #fff;
  border-color: #1890ff;
}

.btn-primary:hover {
  background: #40a9ff;
  color: #fff;
}

.btn-secondary {
  background: #f0f0f0;
  border-color: #d9d9d9;
}

.btn-sm {
  padding: 2px 8px;
  font-size: 12px;
}

.btn-danger {
  color: #ff4d4f;
  border-color: #ff4d4f;
}

.btn-danger:hover {
  background: #ff4d4f;
  color: #fff;
}

.btn-warning {
  color: #faad14;
  border-color: #faad14;
}

.btn-warning:hover {
  background: #faad14;
  color: #fff;
}

.btn-success {
  color: #52c41a;
  border-color: #52c41a;
}

.btn-success:hover {
  background: #52c41a;
  color: #fff;
}

.btn-info {
  color: #1890ff;
  border-color: #1890ff;
}

.btn-info:hover {
  background: #1890ff;
  color: #fff;
}

.header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 16px;
}

.table th,
.table td {
  padding: 10px 12px;
  border: 1px solid #f0f0f0;
  text-align: left;
  font-size: 14px;
}

.table th {
  background: #fafafa;
  font-weight: 600;
  color: #333;
}

.table tbody tr:hover {
  background: #f5f5f5;
}

.row-expired {
  opacity: 0.6;
}

.row-expired td {
  text-decoration: line-through;
  color: #999;
}

.actions {
  display: flex;
  gap: 6px;
}

.status-badge {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-enabled {
  background: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.status-disabled {
  background: #fff2f0;
  color: #ff4d4f;
  border: 1px solid #ffccc7;
}

.status-expired {
  background: #f5f5f5;
  color: #999;
  border: 1px solid #d9d9d9;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pagination-info {
  font-size: 14px;
  color: #666;
  margin-right: auto;
}

.text-center {
  text-align: center;
}

.text-muted {
  color: #999;
  font-size: 13px;
}

.required {
  color: #ff4d4f;
}

.form-hint {
  color: #999;
  font-size: 12px;
  margin-top: 4px;
  display: block;
}

.rule-description {
  margin: 0;
  padding: 8px 12px;
  background: #f0f5ff;
  border-radius: 4px;
  font-size: 13px;
  color: #1890ff;
}

/* Dialog styles */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog {
  background: #fff;
  border-radius: 8px;
  width: 520px;
  max-width: 90vw;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.dialog-sm {
  width: 400px;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.dialog-header h3 {
  margin: 0;
  font-size: 16px;
}

.dialog-close {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #999;
}

.dialog-body {
  padding: 24px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 12px 24px;
  border-top: 1px solid #f0f0f0;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
  color: #333;
}

.form-group .input {
  width: 100%;
  box-sizing: border-box;
}

.form-error {
  padding: 8px 24px 16px;
  color: #ff4d4f;
  font-size: 13px;
}
</style>
