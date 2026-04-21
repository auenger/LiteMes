<template>
  <div class="supplier-page">
    <div class="page-header">
      <h2>供应商管理</h2>
      <div class="header-actions">
        <div class="table-settings-wrapper" style="position: relative;">
          <button class="btn" @click="showTableSettings = !showTableSettings">表格设置</button>
          <TableSettingsPanel
            :visible="showTableSettings"
            :columns="columns"
            @close="showTableSettings = false"
          />
        </div>
        <button class="btn btn-primary" @click="openCreateDialog">新建供应商</button>
      </div>
    </div>

    <!-- Search Bar -->
    <div class="search-bar">
      <input
        v-model="query.supplierCode"
        type="text"
        placeholder="供应商编码"
        class="input"
        @keyup.enter="search"
      />
      <input
        v-model="query.supplierName"
        type="text"
        placeholder="供应商名称"
        class="input"
        @keyup.enter="search"
      />
      <select v-model="query.status" class="input select">
        <option :value="undefined">全部状态</option>
        <option :value="1">启用</option>
        <option :value="0">禁用</option>
      </select>
      <button class="btn" @click="search">查询</button>
      <button class="btn btn-secondary" @click="resetQuery">重置</button>
    </div>

    <!-- Supplier Table -->
    <table class="table">
      <thead>
        <tr>
          <th v-if="isColumnVisible('supplierCode')">供应商编码</th>
          <th v-if="isColumnVisible('supplierName')">供应商名称</th>
          <th v-if="isColumnVisible('shortName')">简称</th>
          <th v-if="isColumnVisible('contactPerson')">联系人</th>
          <th v-if="isColumnVisible('phone')">电话</th>
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
        <tr v-else-if="suppliers.length === 0">
          <td :colspan="visibleColCount + 1" class="text-center">暂无数据</td>
        </tr>
        <tr v-for="supplier in suppliers" :key="supplier.id">
          <td v-if="isColumnVisible('supplierCode')">{{ supplier.supplierCode }}</td>
          <td v-if="isColumnVisible('supplierName')">{{ supplier.supplierName }}</td>
          <td v-if="isColumnVisible('shortName')">{{ supplier.shortName || '-' }}</td>
          <td v-if="isColumnVisible('contactPerson')">{{ supplier.contactPerson || '-' }}</td>
          <td v-if="isColumnVisible('phone')">{{ supplier.phone || '-' }}</td>
          <td v-if="isColumnVisible('status')">
            <span :class="['status-badge', supplier.status === 1 ? 'status-enabled' : 'status-disabled']">
              {{ supplier.status === 1 ? '启用' : '禁用' }}
            </span>
          </td>
          <td v-if="isColumnVisible('createdBy')">{{ supplier.createdBy || '-' }}</td>
          <td v-if="isColumnVisible('createdAt')">{{ formatDate(supplier.createdAt) }}</td>
          <td class="actions">
            <button class="btn btn-sm" @click="openEditDialog(supplier)">编辑</button>
            <button class="btn btn-sm btn-info" @click="openMaterialDialog(supplier)">物料</button>
            <button
              v-if="supplier.status === 1"
              class="btn btn-sm btn-warning"
              @click="toggleStatus(supplier)"
            >
              禁用
            </button>
            <button
              v-else
              class="btn btn-sm btn-success"
              @click="toggleStatus(supplier)"
            >
              启用
            </button>
            <button class="btn btn-sm btn-danger" @click="confirmDelete(supplier)">删除</button>
            <button class="btn btn-sm btn-info" @click="openAuditLog(supplier)">变更履历</button>
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
          <h3>{{ isEdit ? '编辑供应商' : '新建供应商' }}</h3>
          <button class="dialog-close" @click="closeDialog">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label>供应商编码 <span class="required">*</span></label>
            <input
              v-model="form.supplierCode"
              type="text"
              class="input"
              :disabled="isEdit"
              :class="{ 'input-disabled': isEdit }"
              placeholder="请输入供应商编码"
            />
          </div>
          <div class="form-group">
            <label>供应商名称 <span class="required">*</span></label>
            <input
              v-model="form.supplierName"
              type="text"
              class="input"
              placeholder="请输入供应商名称"
            />
          </div>
          <div class="form-group">
            <label>简称</label>
            <input
              v-model="form.shortName"
              type="text"
              class="input"
              placeholder="请输入简称"
            />
          </div>
          <div class="form-group">
            <label>联系人</label>
            <input
              v-model="form.contactPerson"
              type="text"
              class="input"
              placeholder="请输入联系人"
            />
          </div>
          <div class="form-group">
            <label>电话</label>
            <input
              v-model="form.phone"
              type="text"
              class="input"
              placeholder="请输入电话"
            />
          </div>
          <div class="form-group">
            <label>地址</label>
            <input
              v-model="form.address"
              type="text"
              class="input"
              placeholder="请输入地址"
            />
          </div>
          <div class="form-group">
            <label>邮箱</label>
            <input
              v-model="form.email"
              type="text"
              class="input"
              placeholder="请输入邮箱"
            />
          </div>
          <div class="form-group">
            <label>描述</label>
            <input
              v-model="form.description"
              type="text"
              class="input"
              placeholder="请输入描述"
            />
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
          <p>确定要删除供应商 "{{ deleteTarget?.supplierName }}" ({{ deleteTarget?.supplierCode }}) 吗？</p>
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

    <!-- Material Association Dialog -->
    <div v-if="materialDialogVisible" class="dialog-overlay" @click.self="closeMaterialDialog">
      <div class="dialog dialog-lg">
        <div class="dialog-header">
          <h3>物料关联 — {{ materialSupplier?.supplierName }}</h3>
          <button class="dialog-close" @click="closeMaterialDialog">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="material-section">
            <h4>已关联物料</h4>
            <table class="table table-sm" v-if="supplierMaterials.length > 0">
              <thead>
                <tr>
                  <th>物料编码</th>
                  <th>物料名称</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="sm in supplierMaterials" :key="sm.id">
                  <td>{{ sm.materialCode }}</td>
                  <td>{{ sm.materialName }}</td>
                  <td>
                    <button class="btn btn-sm btn-danger" @click="doUnlinkMaterial(sm)">取消关联</button>
                  </td>
                </tr>
              </tbody>
            </table>
            <p v-else class="text-muted">暂无关联物料</p>
          </div>
          <div class="material-section">
            <h4>添加物料</h4>
            <div class="add-material-bar">
              <select v-model="selectedMaterialId" class="input select">
                <option :value="undefined">请选择物料</option>
                <option v-for="m in availableMaterials" :key="m.id" :value="m.id">
                  {{ m.code }} - {{ m.name }}
                </option>
              </select>
              <button class="btn btn-primary" @click="doLinkMaterial" :disabled="!selectedMaterialId">
                添加
              </button>
            </div>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn" @click="closeMaterialDialog">关闭</button>
        </div>
        <div v-if="materialError" class="form-error">{{ materialError }}</div>
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
  listSuppliers,
  createSupplier,
  updateSupplier,
  deleteSupplier,
  updateSupplierStatus,
  linkMaterials,
  unlinkMaterial,
  getSupplierMaterials,
  type SupplierDto,
  type SupplierMaterialDto,
  type SupplierQueryParams,
} from '../../api/supplier';
import { apiGet } from '../../api/http';
import AuditLogDialog from '../../components/AuditLogDialog.vue';
import TableSettingsPanel from '../../components/TableSettingsPanel.vue';
import { useTableSettings, type ColumnDef } from '../../components/useTableSettings';

interface MaterialDropdown {
  id: number;
  code: string;
  name: string;
}

const suppliers = ref<SupplierDto[]>([]);
const total = ref(0);
const loading = ref(false);
const submitting = ref(false);
const formError = ref('');

const query = reactive<SupplierQueryParams>({
  supplierCode: '',
  supplierName: '',
  status: undefined,
  page: 1,
  size: 10,
});

const totalPages = computed(() => Math.ceil(total.value / (query.size || 10)));

// Table settings
const defaultColumns: ColumnDef[] = [
  { key: 'supplierCode', label: '供应商编码' },
  { key: 'supplierName', label: '供应商名称' },
  { key: 'shortName', label: '简称' },
  { key: 'contactPerson', label: '联系人' },
  { key: 'phone', label: '电话' },
  { key: 'status', label: '状态' },
  { key: 'createdBy', label: '创建人' },
  { key: 'createdAt', label: '创建时间' },
];

const { columns, toggleColumn, resetSettings } = useTableSettings('supplier-list', defaultColumns);
const showTableSettings = ref(false);

function isColumnVisible(key: string): boolean {
  return columns.value.find(c => c.key === key)?.visible !== false;
}

const visibleColCount = computed(() => columns.value.filter(c => c.visible).length);

// Audit log dialog state
const auditLogVisible = ref(false);
const auditLogTableName = ref('supplier');
const auditLogRecordId = ref(0);

function openAuditLog(supplier: SupplierDto) {
  auditLogTableName.value = 'supplier';
  auditLogRecordId.value = supplier.id;
  auditLogVisible.value = true;
}

// Dialog state
const dialogVisible = ref(false);
const isEdit = ref(false);
const editingId = ref<number | null>(null);
const form = reactive({
  supplierCode: '',
  supplierName: '',
  shortName: '',
  contactPerson: '',
  phone: '',
  address: '',
  email: '',
  description: '',
});

// Delete dialog state
const deleteDialogVisible = ref(false);
const deleteTarget = ref<SupplierDto | null>(null);

// Material dialog state
const materialDialogVisible = ref(false);
const materialSupplier = ref<SupplierDto | null>(null);
const supplierMaterials = ref<SupplierMaterialDto[]>([]);
const allMaterials = ref<MaterialDropdown[]>([]);
const selectedMaterialId = ref<number | undefined>(undefined);
const materialError = ref('');

const availableMaterials = computed(() => {
  const linkedIds = new Set(supplierMaterials.value.map(sm => sm.materialId));
  return allMaterials.value.filter(m => !linkedIds.has(m.id));
});

async function fetchList() {
  loading.value = true;
  try {
    const res = await listSuppliers(query);
    if (res.code === 200 && res.data) {
      suppliers.value = res.data.records;
      total.value = res.data.total;
    }
  } catch (e) {
    console.error('Failed to fetch supplier list', e);
  } finally {
    loading.value = false;
  }
}

function search() {
  query.page = 1;
  fetchList();
}

function resetQuery() {
  query.supplierCode = '';
  query.supplierName = '';
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
  form.supplierCode = '';
  form.supplierName = '';
  form.shortName = '';
  form.contactPerson = '';
  form.phone = '';
  form.address = '';
  form.email = '';
  form.description = '';
  formError.value = '';
  dialogVisible.value = true;
}

function openEditDialog(supplier: SupplierDto) {
  isEdit.value = true;
  editingId.value = supplier.id;
  form.supplierCode = supplier.supplierCode;
  form.supplierName = supplier.supplierName;
  form.shortName = supplier.shortName || '';
  form.contactPerson = supplier.contactPerson || '';
  form.phone = supplier.phone || '';
  form.address = supplier.address || '';
  form.email = supplier.email || '';
  form.description = supplier.description || '';
  formError.value = '';
  dialogVisible.value = true;
}

function closeDialog() {
  dialogVisible.value = false;
}

async function submitForm() {
  if (!form.supplierCode && !isEdit.value) {
    formError.value = '供应商编码不能为空';
    return;
  }
  if (!form.supplierName) {
    formError.value = '供应商名称不能为空';
    return;
  }

  submitting.value = true;
  formError.value = '';
  try {
    if (isEdit.value && editingId.value) {
      await updateSupplier(editingId.value, {
        supplierName: form.supplierName,
        shortName: form.shortName || undefined,
        contactPerson: form.contactPerson || undefined,
        phone: form.phone || undefined,
        address: form.address || undefined,
        email: form.email || undefined,
        description: form.description || undefined,
      });
    } else {
      await createSupplier({
        supplierCode: form.supplierCode,
        supplierName: form.supplierName,
        shortName: form.shortName || undefined,
        contactPerson: form.contactPerson || undefined,
        phone: form.phone || undefined,
        address: form.address || undefined,
        email: form.email || undefined,
        description: form.description || undefined,
      });
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

function confirmDelete(supplier: SupplierDto) {
  deleteTarget.value = supplier;
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
    await deleteSupplier(deleteTarget.value.id);
    closeDeleteDialog();
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '删除失败';
    alert(msg);
  } finally {
    submitting.value = false;
  }
}

async function toggleStatus(supplier: SupplierDto) {
  const newStatus = supplier.status === 1 ? 0 : 1;
  try {
    await updateSupplierStatus(supplier.id, newStatus);
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '状态更新失败';
    alert(msg);
  }
}

// Material dialog functions
async function openMaterialDialog(supplier: SupplierDto) {
  materialSupplier.value = supplier;
  materialError.value = '';
  selectedMaterialId.value = undefined;
  materialDialogVisible.value = true;
  await fetchSupplierMaterials(supplier.id);
  await fetchAllMaterials();
}

function closeMaterialDialog() {
  materialDialogVisible.value = false;
  materialSupplier.value = null;
  supplierMaterials.value = [];
}

async function fetchSupplierMaterials(supplierId: number) {
  try {
    const res = await getSupplierMaterials(supplierId);
    if (res.code === 200 && res.data) {
      supplierMaterials.value = res.data;
    }
  } catch (e) {
    console.error('Failed to fetch supplier materials', e);
  }
}

async function fetchAllMaterials() {
  try {
    const res = await apiGet<any>('/api/dropdown/materials');
    const data = res.data || res;
    if (Array.isArray(data)) {
      allMaterials.value = data.map((d: any) => ({ id: d.id, code: d.code, name: d.name }));
    }
  } catch (e) {
    console.error('Failed to fetch materials dropdown', e);
  }
}

async function doLinkMaterial() {
  if (!materialSupplier.value || !selectedMaterialId.value) return;
  materialError.value = '';
  try {
    await linkMaterials(materialSupplier.value.id, [selectedMaterialId.value]);
    selectedMaterialId.value = undefined;
    await fetchSupplierMaterials(materialSupplier.value.id);
  } catch (e: any) {
    const msg = e?.response?.data?.message || '关联物料失败';
    materialError.value = msg;
  }
}

async function doUnlinkMaterial(sm: SupplierMaterialDto) {
  if (!materialSupplier.value) return;
  materialError.value = '';
  try {
    await unlinkMaterial(materialSupplier.value.id, sm.materialId);
    await fetchSupplierMaterials(materialSupplier.value.id);
  } catch (e: any) {
    const msg = e?.response?.data?.message || '取消关联失败';
    materialError.value = msg;
  }
}

function formatDate(dateStr: string | undefined) {
  if (!dateStr) return '-';
  return new Date(dateStr).toLocaleString('zh-CN');
}

onMounted(() => {
  fetchList();
});
</script>

<style scoped>
.supplier-page {
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

.table-sm th,
.table-sm td {
  padding: 6px 8px;
  font-size: 13px;
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
  width: 480px;
  max-width: 90vw;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.dialog-sm {
  width: 400px;
}

.dialog-lg {
  width: 640px;
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

.material-section {
  margin-bottom: 20px;
}

.material-section h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #333;
}

.add-material-bar {
  display: flex;
  gap: 10px;
}

.add-material-bar .select {
  flex: 1;
}
</style>
