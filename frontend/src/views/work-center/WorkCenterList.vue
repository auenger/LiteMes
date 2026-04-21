<template>
  <div class="work-center-page">
    <div class="page-header">
      <h2>工作中心管理</h2>
      <button class="btn btn-primary" @click="openCreateDialog">新建工作中心</button>
    </div>

    <!-- Search Bar -->
    <div class="search-bar">
      <input
        v-model="query.workCenterCode"
        type="text"
        placeholder="工作中心编码"
        class="input"
        @keyup.enter="search"
      />
      <input
        v-model="query.name"
        type="text"
        placeholder="工作中心名称"
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

    <!-- WorkCenter Table -->
    <table class="table">
      <thead>
        <tr>
          <th>工作中心编码</th>
          <th>工作中心名称</th>
          <th>所属工厂</th>
          <th>状态</th>
          <th>创建人</th>
          <th>创建时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="loading">
          <td colspan="7" class="text-center">加载中...</td>
        </tr>
        <tr v-else-if="workCenters.length === 0">
          <td colspan="7" class="text-center">暂无数据</td>
        </tr>
        <tr v-for="wc in workCenters" :key="wc.id">
          <td>{{ wc.workCenterCode }}</td>
          <td>{{ wc.name }}</td>
          <td>{{ wc.factoryName || '-' }}</td>
          <td>
            <span :class="['status-badge', wc.status === 1 ? 'status-enabled' : 'status-disabled']">
              {{ wc.status === 1 ? '启用' : '禁用' }}
            </span>
          </td>
          <td>{{ wc.createdBy || '-' }}</td>
          <td>{{ formatDate(wc.createdAt) }}</td>
          <td class="actions">
            <button class="btn btn-sm" @click="openEditDialog(wc)">编辑</button>
            <button
              v-if="wc.status === 1"
              class="btn btn-sm btn-warning"
              @click="toggleStatus(wc)"
            >
              禁用
            </button>
            <button
              v-else
              class="btn btn-sm btn-success"
              @click="toggleStatus(wc)"
            >
              启用
            </button>
            <button class="btn btn-sm btn-danger" @click="confirmDelete(wc)">删除</button>
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
          <h3>{{ isEdit ? '编辑工作中心' : '新建工作中心' }}</h3>
          <button class="dialog-close" @click="closeDialog">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label>工作中心编码 <span class="required">*</span></label>
            <input
              v-model="form.workCenterCode"
              type="text"
              class="input"
              :disabled="isEdit"
              :class="{ 'input-disabled': isEdit }"
              placeholder="请输入工作中心编码"
            />
          </div>
          <div class="form-group">
            <label>工作中心名称 <span class="required">*</span></label>
            <input
              v-model="form.name"
              type="text"
              class="input"
              placeholder="请输入工作中心名称"
            />
          </div>
          <div class="form-group">
            <label>所属工厂 <span class="required">*</span></label>
            <select
              v-model="form.factoryId"
              class="input"
              :disabled="isEdit"
              :class="{ 'input-disabled': isEdit }"
            >
              <option :value="undefined" disabled>请选择所属工厂</option>
              <option v-for="f in factoryOptions" :key="f.id" :value="f.id">{{ f.name }}</option>
            </select>
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
          <p>确定要删除工作中心 "{{ deleteTarget?.name }}" ({{ deleteTarget?.workCenterCode }}) 吗？</p>
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import {
  listWorkCenters,
  createWorkCenter,
  updateWorkCenter,
  deleteWorkCenter,
  updateWorkCenterStatus,
  type WorkCenterDto,
  type WorkCenterQueryParams,
} from '../../api/workCenter';
import { listFactories, type FactoryDto } from '../../api/factory';

const workCenters = ref<WorkCenterDto[]>([]);
const total = ref(0);
const loading = ref(false);
const submitting = ref(false);
const formError = ref('');
const factoryOptions = ref<FactoryDto[]>([]);

const query = reactive<WorkCenterQueryParams>({
  workCenterCode: '',
  name: '',
  factoryId: undefined,
  status: undefined,
  page: 1,
  size: 10,
});

const totalPages = computed(() => Math.ceil(total.value / (query.size || 10)));

// Dialog state
const dialogVisible = ref(false);
const isEdit = ref(false);
const editingId = ref<number | null>(null);
const form = reactive({
  workCenterCode: '',
  name: '',
  factoryId: undefined as number | undefined,
});

// Delete dialog state
const deleteDialogVisible = ref(false);
const deleteTarget = ref<WorkCenterDto | null>(null);

async function fetchFactories() {
  try {
    const res = await listFactories({ status: 1, size: 1000 });
    if (res.code === 200 && res.data) {
      factoryOptions.value = res.data.records;
    }
  } catch (e) {
    console.error('Failed to fetch factory options', e);
  }
}

async function fetchList() {
  loading.value = true;
  try {
    const res = await listWorkCenters(query);
    if (res.code === 200 && res.data) {
      workCenters.value = res.data.records;
      total.value = res.data.total;
    }
  } catch (e) {
    console.error('Failed to fetch work center list', e);
  } finally {
    loading.value = false;
  }
}

function search() {
  query.page = 1;
  fetchList();
}

function resetQuery() {
  query.workCenterCode = '';
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

function openCreateDialog() {
  isEdit.value = false;
  editingId.value = null;
  form.workCenterCode = '';
  form.name = '';
  form.factoryId = undefined;
  formError.value = '';
  dialogVisible.value = true;
}

function openEditDialog(wc: WorkCenterDto) {
  isEdit.value = true;
  editingId.value = wc.id;
  form.workCenterCode = wc.workCenterCode;
  form.name = wc.name;
  form.factoryId = wc.factoryId;
  formError.value = '';
  dialogVisible.value = true;
}

function closeDialog() {
  dialogVisible.value = false;
}

async function submitForm() {
  if (!form.workCenterCode && !isEdit.value) {
    formError.value = '工作中心编码不能为空';
    return;
  }
  if (!form.name) {
    formError.value = '工作中心名称不能为空';
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
      await updateWorkCenter(editingId.value, {
        name: form.name,
      });
    } else {
      await createWorkCenter({
        workCenterCode: form.workCenterCode,
        name: form.name,
        factoryId: form.factoryId,
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

function confirmDelete(wc: WorkCenterDto) {
  deleteTarget.value = wc;
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
    await deleteWorkCenter(deleteTarget.value.id);
    closeDeleteDialog();
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '删除失败';
    alert(msg);
  } finally {
    submitting.value = false;
  }
}

async function toggleStatus(wc: WorkCenterDto) {
  const newStatus = wc.status === 1 ? 0 : 1;
  try {
    await updateWorkCenterStatus(wc.id, newStatus);
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '状态更新失败';
    alert(msg);
  }
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
.work-center-page {
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
