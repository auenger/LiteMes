<template>
  <div class="department-page">
    <div class="page-header">
      <h2>部门管理</h2>
      <div class="header-actions">
        <div class="table-settings-wrapper" style="position: relative;">
          <button class="btn" @click="showTableSettings = !showTableSettings">表格设置</button>
          <TableSettingsPanel
            :visible="showTableSettings"
            :columns="columns"
            @close="showTableSettings = false"
          />
        </div>
        <button class="btn btn-primary" @click="openCreateDialog">新建部门</button>
      </div>
    </div>

    <!-- Search Bar -->
    <div class="search-bar">
      <input
        v-model="query.departmentCode"
        type="text"
        placeholder="部门编码"
        class="input"
        @keyup.enter="search"
      />
      <input
        v-model="query.name"
        type="text"
        placeholder="部门名称"
        class="input"
        @keyup.enter="search"
      />
      <select v-model="query.factoryId" class="input select">
        <option :value="undefined">全部工厂</option>
        <option v-for="f in factoryOptions" :key="f.id" :value="f.id">{{ f.name }}</option>
      </select>
      <select v-model="query.status" class="input select">
        <option :value="undefined">全部状态</option>
        <option :value="1">启用</option>
        <option :value="0">禁用</option>
      </select>
      <button class="btn" @click="search">查询</button>
      <button class="btn btn-secondary" @click="resetQuery">重置</button>
    </div>

    <!-- Department Table -->
    <table class="table">
      <thead>
        <tr>
          <th>部门编码</th>
          <th>部门名称</th>
          <th>所属工厂</th>
          <th>上级部门</th>
          <th>排序号</th>
          <th>状态</th>
          <th>创建人</th>
          <th>创建时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="loading">
          <td colspan="9" class="text-center">加载中...</td>
        </tr>
        <tr v-else-if="departments.length === 0">
          <td colspan="9" class="text-center">暂无数据</td>
        </tr>
        <tr v-for="dept in departments" :key="dept.id">
          <td>{{ dept.departmentCode }}</td>
          <td>{{ dept.name }}</td>
          <td>{{ dept.factoryName || '-' }}</td>
          <td>{{ dept.parentName || '顶级部门' }}</td>
          <td>{{ dept.sortOrder }}</td>
          <td>
            <span :class="['status-badge', dept.status === 1 ? 'status-enabled' : 'status-disabled']">
              {{ dept.status === 1 ? '启用' : '禁用' }}
            </span>
          </td>
          <td>{{ dept.createdBy || '-' }}</td>
          <td>{{ formatDate(dept.createdAt) }}</td>
          <td class="actions">
            <button class="btn btn-sm btn-info" @click="manageUsers(dept)">用户</button>
            <button class="btn btn-sm" @click="openEditDialog(dept)">编辑</button>
            <button
              v-if="dept.status === 1"
              class="btn btn-sm btn-warning"
              @click="toggleStatus(dept)"
            >
              禁用
            </button>
            <button
              v-else
              class="btn btn-sm btn-success"
              @click="toggleStatus(dept)"
            >
              启用
            </button>
            <button class="btn btn-sm btn-danger" @click="confirmDelete(dept)">删除</button>
            <button class="btn btn-sm btn-audit" @click="openAuditLog(dept)">变更履历</button>
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
          <h3>{{ isEdit ? '编辑部门' : '新建部门' }}</h3>
          <button class="dialog-close" @click="closeDialog">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label>部门编码 <span class="required">*</span></label>
            <input
              v-model="form.departmentCode"
              type="text"
              class="input"
              :disabled="isEdit"
              :class="{ 'input-disabled': isEdit }"
              placeholder="请输入部门编码"
            />
          </div>
          <div class="form-group">
            <label>部门名称 <span class="required">*</span></label>
            <input
              v-model="form.name"
              type="text"
              class="input"
              placeholder="请输入部门名称"
            />
          </div>
          <div class="form-group">
            <label>所属工厂 <span class="required">*</span></label>
            <select
              v-model="form.factoryId"
              class="input"
            >
              <option :value="undefined" disabled>请选择所属工厂</option>
              <option v-for="f in factoryOptions" :key="f.id" :value="f.id">{{ f.name }}</option>
            </select>
          </div>
          <div class="form-group">
            <label>上级部门</label>
            <select v-model="form.parentId" class="input">
              <option :value="null">无（顶级部门）</option>
              <option v-for="d in parentDepartmentOptions" :key="d.id" :value="d.id">{{ d.name }} ({{ d.code }})</option>
            </select>
          </div>
          <div class="form-group">
            <label>排序号</label>
            <input
              v-model.number="form.sortOrder"
              type="number"
              class="input"
              placeholder="0"
              min="0"
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
          <p>确定要删除部门 "{{ deleteTarget?.name }}" ({{ deleteTarget?.departmentCode }}) 吗？</p>
          <p class="text-muted">删除后不可恢复。若该部门下存在子部门或已被引用，则无法删除。</p>
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
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import {
  listDepartments,
  createDepartment,
  updateDepartment,
  deleteDepartment,
  updateDepartmentStatus,
  type DepartmentDto,
  type DepartmentQueryParams,
} from '../../api/department';
import { getFactoryDropdown, getDepartmentDropdown, type DropdownItem } from '../../api/dropdown';
import AuditLogDialog from '../../components/AuditLogDialog.vue';
import TableSettingsPanel from '../../components/TableSettingsPanel.vue';
import { useTableSettings, type ColumnDef } from '../../components/useTableSettings';

const router = useRouter();

const departments = ref<DepartmentDto[]>([]);
const total = ref(0);
const loading = ref(false);
const submitting = ref(false);
const formError = ref('');
const factoryOptions = ref<DropdownItem[]>([]);

const query = reactive<DepartmentQueryParams>({
  departmentCode: '',
  name: '',
  factoryId: undefined,
  status: undefined,
  page: 1,
  size: 10,
});

const totalPages = computed(() => Math.ceil(total.value / (query.size || 10)));

// Table settings
const defaultColumns: ColumnDef[] = [
  { key: 'departmentCode', label: '部门编码' },
  { key: 'name', label: '部门名称' },
  { key: 'factoryName', label: '所属工厂' },
  { key: 'parentName', label: '上级部门' },
  { key: 'sortOrder', label: '排序号' },
  { key: 'status', label: '状态' },
  { key: 'createdBy', label: '创建人' },
  { key: 'createdAt', label: '创建时间' },
];

const { columns, toggleColumn, resetSettings } = useTableSettings('department-list', defaultColumns);
const showTableSettings = ref(false);

// Audit log dialog state
const auditLogVisible = ref(false);
const auditLogTableName = ref('department');
const auditLogRecordId = ref(0);

function openAuditLog(dept: DepartmentDto) {
  auditLogTableName.value = 'department';
  auditLogRecordId.value = dept.id;
  auditLogVisible.value = true;
}

// Dialog state
const dialogVisible = ref(false);
const isEdit = ref(false);
const editingId = ref<number | null>(null);
const form = reactive({
  departmentCode: '',
  name: '',
  factoryId: undefined as number | undefined,
  parentId: null as number | null,
  sortOrder: 0,
});

// Parent department options for selected factory
const parentDepartmentOptions = ref<DropdownItem[]>([]);

// Delete dialog state
const deleteDialogVisible = ref(false);
const deleteTarget = ref<DepartmentDto | null>(null);

async function fetchFactories() {
  try {
    const res = await getFactoryDropdown();
    if (res.code === 200 && res.data) {
      factoryOptions.value = res.data;
    }
  } catch (e) {
    console.error('Failed to fetch factory options', e);
  }
}

async function fetchParentDepartments(factoryId: number | undefined) {
  if (!factoryId) {
    parentDepartmentOptions.value = [];
    return;
  }
  try {
    const res = await getDepartmentDropdown(factoryId);
    if (res.code === 200 && res.data) {
      parentDepartmentOptions.value = res.data.filter(
        (d) => !isEdit.value || d.id !== editingId.value
      );
    }
  } catch (e) {
    console.error('Failed to fetch parent department options', e);
  }
}

async function fetchList() {
  loading.value = true;
  try {
    const res = await listDepartments(query);
    if (res.code === 200 && res.data) {
      departments.value = res.data.records;
      total.value = res.data.total;
    }
  } catch (e) {
    console.error('Failed to fetch department list', e);
  } finally {
    loading.value = false;
  }
}

function search() {
  query.page = 1;
  fetchList();
}

function resetQuery() {
  query.departmentCode = '';
  query.name = '';
  query.factoryId = undefined;
  query.status = undefined;
  query.page = 1;
  fetchList();
}

function goPage(page: number) {
  query.page = page;
  fetchList();
}

async function openCreateDialog() {
  isEdit.value = false;
  editingId.value = null;
  form.departmentCode = '';
  form.name = '';
  form.factoryId = undefined;
  form.parentId = null;
  form.sortOrder = 0;
  parentDepartmentOptions.value = [];
  formError.value = '';
  dialogVisible.value = true;
}

async function openEditDialog(dept: DepartmentDto) {
  isEdit.value = true;
  editingId.value = dept.id;
  form.departmentCode = dept.departmentCode;
  form.name = dept.name;
  form.factoryId = dept.factoryId;
  form.parentId = dept.parentId;
  form.sortOrder = dept.sortOrder;
  formError.value = '';
  await fetchParentDepartments(dept.factoryId);
  dialogVisible.value = true;
}

function closeDialog() {
  dialogVisible.value = false;
}

// Load parent departments when factory changes in create dialog
watch(() => form.factoryId, (newFactoryId) => {
  if (!isEdit.value) {
    form.parentId = null;
    fetchParentDepartments(newFactoryId);
  }
});

async function submitForm() {
  if (!form.departmentCode && !isEdit.value) {
    formError.value = '部门编码不能为空';
    return;
  }
  if (!form.name) {
    formError.value = '部门名称不能为空';
    return;
  }
  if (!form.factoryId) {
    formError.value = '请选择所属工厂';
    return;
  }

  submitting.value = true;
  formError.value = '';
  try {
    if (isEdit.value && editingId.value) {
      await updateDepartment(editingId.value, {
        name: form.name,
        factoryId: form.factoryId,
        parentId: form.parentId,
        sortOrder: form.sortOrder,
      });
    } else {
      await createDepartment({
        departmentCode: form.departmentCode,
        name: form.name,
        factoryId: form.factoryId,
        parentId: form.parentId || undefined,
        sortOrder: form.sortOrder || 0,
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

function confirmDelete(dept: DepartmentDto) {
  deleteTarget.value = dept;
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
    await deleteDepartment(deleteTarget.value.id);
    closeDeleteDialog();
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '删除失败';
    alert(msg);
  } finally {
    submitting.value = false;
  }
}

async function toggleStatus(dept: DepartmentDto) {
  const newStatus = dept.status === 1 ? 0 : 1;
  try {
    await updateDepartmentStatus(dept.id, newStatus);
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '状态更新失败';
    alert(msg);
  }
}

function manageUsers(dept: DepartmentDto) {
  router.push(`/departments/${dept.id}/users`);
}

function formatDate(dateStr: string | undefined) {
  if (!dateStr) return '-';
  return new Date(dateStr).toLocaleString('zh-CN');
}

onMounted(() => {
  fetchFactories();
  fetchList();
});
</script>

<style scoped>
.department-page {
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

.btn-audit {
  color: #722ed1;
  border-color: #722ed1;
}

.btn-audit:hover {
  background: #722ed1;
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
