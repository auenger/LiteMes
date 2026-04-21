<template>
  <div class="uom-conversion-page">
    <div class="page-header">
      <h2>单位换算比例</h2>
      <div class="header-actions">
        <button class="btn btn-primary" @click="openCreateDialog">新建换算比例</button>
      </div>
    </div>

    <!-- Search Bar -->
    <div class="search-bar">
      <input
        v-model="query.fromUom"
        type="text"
        placeholder="原单位编码/名称"
        class="input"
        @keyup.enter="search"
      />
      <input
        v-model="query.toUom"
        type="text"
        placeholder="目标单位编码/名称"
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

    <!-- Conversion Table -->
    <table class="table">
      <thead>
        <tr>
          <th>原单位编码</th>
          <th>原单位名称</th>
          <th>目标单位编码</th>
          <th>目标单位名称</th>
          <th>换算率</th>
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
        <tr v-else-if="conversions.length === 0">
          <td colspan="9" class="text-center">暂无数据</td>
        </tr>
        <tr v-for="conv in conversions" :key="conv.id">
          <td>{{ conv.fromUomCode }}</td>
          <td>{{ conv.fromUomName }}</td>
          <td>{{ conv.toUomCode }}</td>
          <td>{{ conv.toUomName }}</td>
          <td>{{ conv.conversionRate }}</td>
          <td>
            <span :class="['status-badge', conv.status === 1 ? 'status-enabled' : 'status-disabled']">
              {{ conv.status === 1 ? '启用' : '禁用' }}
            </span>
          </td>
          <td>{{ conv.createdBy || '-' }}</td>
          <td>{{ formatDate(conv.createdAt) }}</td>
          <td class="actions">
            <button class="btn btn-sm" @click="openEditDialog(conv)">编辑</button>
            <button class="btn btn-sm btn-danger" @click="confirmDelete(conv)">删除</button>
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
          <h3>{{ isEdit ? '编辑换算比例' : '新建换算比例' }}</h3>
          <button class="dialog-close" @click="closeDialog">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label>原单位 <span class="required">*</span></label>
            <select v-model="form.fromUomId" class="input" :disabled="isEdit" :class="{ 'input-disabled': isEdit }">
              <option :value="null" disabled>请选择原单位</option>
              <option v-for="uom in uomOptions" :key="uom.id" :value="uom.id">
                {{ uom.code }} - {{ uom.name }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>目标单位 <span class="required">*</span></label>
            <select v-model="form.toUomId" class="input" :disabled="isEdit" :class="{ 'input-disabled': isEdit }">
              <option :value="null" disabled>请选择目标单位</option>
              <option v-for="uom in uomOptions" :key="uom.id" :value="uom.id">
                {{ uom.code }} - {{ uom.name }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>换算率 <span class="required">*</span></label>
            <input
              v-model.number="form.conversionRate"
              type="number"
              step="0.000001"
              class="input"
              placeholder="请输入换算率"
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
          <p>确定要删除换算比例 "{{ deleteTarget?.fromUomName }} → {{ deleteTarget?.toUomName }}" 吗？</p>
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
  listUomConversions,
  createUomConversion,
  updateUomConversion,
  deleteUomConversion,
  type UomConversionDto,
  type UomConversionQueryParams,
} from '../../api/uom';
import { getUomDropdown, type DropdownItem } from '../../api/dropdown';

const conversions = ref<UomConversionDto[]>([]);
const total = ref(0);
const loading = ref(false);
const submitting = ref(false);
const formError = ref('');
const uomOptions = ref<DropdownItem[]>([]);

const query = reactive<UomConversionQueryParams>({
  fromUom: '',
  toUom: '',
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
  fromUomId: null as number | null,
  toUomId: null as number | null,
  conversionRate: null as number | null,
});

// Delete dialog state
const deleteDialogVisible = ref(false);
const deleteTarget = ref<UomConversionDto | null>(null);

async function fetchUomOptions() {
  try {
    const res = await getUomDropdown();
    if (res.code === 200 && res.data) {
      uomOptions.value = res.data;
    }
  } catch (e) {
    console.error('Failed to fetch uom dropdown', e);
  }
}

async function fetchList() {
  loading.value = true;
  try {
    const res = await listUomConversions(query);
    if (res.code === 200 && res.data) {
      conversions.value = res.data.records;
      total.value = res.data.total;
    }
  } catch (e) {
    console.error('Failed to fetch uom conversion list', e);
  } finally {
    loading.value = false;
  }
}

function search() {
  query.page = 1;
  fetchList();
}

function resetQuery() {
  query.fromUom = '';
  query.toUom = '';
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
  form.fromUomId = null;
  form.toUomId = null;
  form.conversionRate = null;
  formError.value = '';
  dialogVisible.value = true;
}

function openEditDialog(conv: UomConversionDto) {
  isEdit.value = true;
  editingId.value = conv.id;
  form.fromUomId = conv.fromUomId;
  form.toUomId = conv.toUomId;
  form.conversionRate = conv.conversionRate;
  formError.value = '';
  dialogVisible.value = true;
}

function closeDialog() {
  dialogVisible.value = false;
}

async function submitForm() {
  if (!form.fromUomId) {
    formError.value = '原单位不能为空';
    return;
  }
  if (!form.toUomId) {
    formError.value = '目标单位不能为空';
    return;
  }
  if (form.conversionRate === null || form.conversionRate === undefined) {
    formError.value = '换算率不能为空';
    return;
  }

  submitting.value = true;
  formError.value = '';
  try {
    if (isEdit.value && editingId.value) {
      await updateUomConversion(editingId.value, {
        conversionRate: form.conversionRate,
      });
    } else {
      await createUomConversion({
        fromUomId: form.fromUomId,
        toUomId: form.toUomId,
        conversionRate: form.conversionRate,
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

function confirmDelete(conv: UomConversionDto) {
  deleteTarget.value = conv;
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
    await deleteUomConversion(deleteTarget.value.id);
    closeDeleteDialog();
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '删除失败';
    alert(msg);
  } finally {
    submitting.value = false;
  }
}

function formatDate(dateStr: string | undefined) {
  if (!dateStr) return '-';
  return new Date(dateStr).toLocaleString('zh-CN');
}

onMounted(() => {
  fetchUomOptions();
  fetchList();
});
</script>

<style scoped>
.uom-conversion-page {
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
