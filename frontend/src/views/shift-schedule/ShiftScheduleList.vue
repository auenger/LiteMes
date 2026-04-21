<template>
  <div class="shift-schedule-list">
    <h2>班制管理</h2>

    <!-- Search Bar -->
    <div class="search-bar">
      <input v-model="searchForm.shiftCode" placeholder="班制编码" class="search-input" />
      <input v-model="searchForm.name" placeholder="班制名称" class="search-input" />
      <select v-model="searchForm.status" class="search-select">
        <option :value="undefined">全部状态</option>
        <option :value="1">启用</option>
        <option :value="0">禁用</option>
      </select>
      <button @click="handleSearch" class="btn btn-primary">查询</button>
      <button @click="handleReset" class="btn btn-default">重置</button>
      <button @click="showCreateDialog = true" class="btn btn-success">新增班制</button>
    </div>

    <!-- Shift Schedule Table -->
    <table class="data-table">
      <thead>
        <tr>
          <th>班制编码</th>
          <th>班制名称</th>
          <th>是否默认</th>
          <th>状态</th>
          <th>创建人</th>
          <th>创建时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in schedules" :key="item.id">
          <td>{{ item.shiftCode }}</td>
          <td>{{ item.name }}</td>
          <td>{{ item.isDefault === 1 ? '是' : '否' }}</td>
          <td>
            <span :class="['status-tag', item.status === 1 ? 'status-enabled' : 'status-disabled']">
              {{ item.status === 1 ? '启用' : '禁用' }}
            </span>
          </td>
          <td>{{ item.createdBy }}</td>
          <td>{{ formatDateTime(item.createdAt) }}</td>
          <td class="action-cell">
            <button @click="handleEdit(item)" class="btn btn-sm btn-primary">编辑</button>
            <button @click="handleToggleStatus(item)" class="btn btn-sm" :class="item.status === 1 ? 'btn-warning' : 'btn-success'">
              {{ item.status === 1 ? '禁用' : '启用' }}
            </button>
            <button @click="handleViewShifts(item)" class="btn btn-sm btn-info">班次</button>
            <button @click="handleDelete(item)" class="btn btn-sm btn-danger">删除</button>
          </td>
        </tr>
        <tr v-if="schedules.length === 0">
          <td colspan="7" class="empty-row">暂无数据</td>
        </tr>
      </tbody>
    </table>

    <!-- Pagination -->
    <div class="pagination" v-if="pagination.total > 0">
      <span class="pagination-info">
        共 {{ pagination.total }} 条，第 {{ pagination.pageNum }} / {{ totalPages }} 页
      </span>
      <button @click="handlePageChange(1)" :disabled="pagination.pageNum <= 1" class="btn btn-sm">首页</button>
      <button @click="handlePageChange(pagination.pageNum - 1)" :disabled="pagination.pageNum <= 1" class="btn btn-sm">上一页</button>
      <button @click="handlePageChange(pagination.pageNum + 1)" :disabled="pagination.pageNum >= totalPages" class="btn btn-sm">下一页</button>
      <button @click="handlePageChange(totalPages)" :disabled="pagination.pageNum >= totalPages" class="btn btn-sm">末页</button>
    </div>

    <!-- Create/Edit Dialog -->
    <div v-if="showCreateDialog || showEditDialog" class="dialog-overlay" @click.self="closeDialog">
      <div class="dialog-content">
        <h3>{{ showEditDialog ? '编辑班制' : '新增班制' }}</h3>
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label>班制编码</label>
            <input v-model="formData.shiftCode" :disabled="showEditDialog" required maxlength="50" />
          </div>
          <div class="form-group">
            <label>班制名称</label>
            <input v-model="formData.name" required maxlength="50" />
          </div>
          <div class="form-group">
            <label>
              <input type="checkbox" v-model="formData.isDefault" :true-value="1" :false-value="0" />
              设为默认班制
            </label>
          </div>
          <div class="form-actions">
            <button type="submit" class="btn btn-primary">确定</button>
            <button type="button" @click="closeDialog" class="btn btn-default">取消</button>
          </div>
        </form>
      </div>
    </div>

    <!-- Shift List Dialog -->
    <div v-if="showShiftDialog" class="dialog-overlay" @click.self="showShiftDialog = false">
      <div class="dialog-content dialog-wide">
        <h3>{{ currentSchedule?.name }} - 班次管理</h3>
        <div class="search-bar">
          <button @click="showShiftCreateDialog = true" class="btn btn-success">新增班次</button>
        </div>
        <table class="data-table">
          <thead>
            <tr>
              <th>班次编码</th>
              <th>班次名称</th>
              <th>开始时间</th>
              <th>结束时间</th>
              <th>是否跨天</th>
              <th>工时(h)</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="shift in shifts" :key="shift.id">
              <td>{{ shift.shiftCode }}</td>
              <td>{{ shift.name }}</td>
              <td>{{ shift.startTime }}</td>
              <td>{{ shift.endTime }}</td>
              <td>{{ shift.crossDay === 1 ? '是' : '否' }}</td>
              <td>{{ shift.workHours }}</td>
              <td>
                <span :class="['status-tag', shift.status === 1 ? 'status-enabled' : 'status-disabled']">
                  {{ shift.status === 1 ? '启用' : '禁用' }}
                </span>
              </td>
              <td class="action-cell">
                <button @click="handleEditShift(shift)" class="btn btn-sm btn-primary">编辑</button>
                <button @click="handleDeleteShift(shift)" class="btn btn-sm btn-danger">删除</button>
              </td>
            </tr>
            <tr v-if="shifts.length === 0">
              <td colspan="8" class="empty-row">暂无班次</td>
            </tr>
          </tbody>
        </table>
        <div class="form-actions">
          <button @click="showShiftDialog = false" class="btn btn-default">关闭</button>
        </div>
      </div>
    </div>

    <!-- Shift Create/Edit Dialog -->
    <div v-if="showShiftCreateDialog || showShiftEditDialog" class="dialog-overlay" @click.self="closeShiftFormDialog">
      <div class="dialog-content">
        <h3>{{ showShiftEditDialog ? '编辑班次' : '新增班次' }}</h3>
        <form @submit.prevent="handleShiftSubmit">
          <div class="form-group">
            <label>班次编码</label>
            <input v-model="shiftFormData.shiftCode" :disabled="showShiftEditDialog" required maxlength="50" />
          </div>
          <div class="form-group">
            <label>班次名称</label>
            <input v-model="shiftFormData.name" required maxlength="50" />
          </div>
          <div class="form-group">
            <label>开始时间</label>
            <input v-model="shiftFormData.startTime" type="time" required />
          </div>
          <div class="form-group">
            <label>结束时间</label>
            <input v-model="shiftFormData.endTime" type="time" required />
          </div>
          <div class="form-group">
            <label>
              <input type="checkbox" v-model="shiftFormData.crossDay" :true-value="1" :false-value="0" />
              跨天班次
            </label>
          </div>
          <div class="form-actions">
            <button type="submit" class="btn btn-primary">确定</button>
            <button type="button" @click="closeShiftFormDialog" class="btn btn-default">取消</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import {
  listShiftSchedules,
  createShiftSchedule,
  updateShiftSchedule,
  deleteShiftSchedule,
  updateShiftScheduleStatus,
  listShifts,
  createShift,
  updateShift,
  deleteShift,
  type ShiftScheduleDto,
  type ShiftDto,
} from '../../api/shiftSchedule';

// Schedule state
const schedules = ref<ShiftScheduleDto[]>([]);
const searchForm = ref<{ shiftCode: string; name: string; status: number | undefined }>({
  shiftCode: '',
  name: '',
  status: undefined,
});
const pagination = ref({ total: 0, pageNum: 1, pageSize: 10 });
const totalPages = computed(() => Math.ceil(pagination.value.total / pagination.value.pageSize));

// Dialog state
const showCreateDialog = ref(false);
const showEditDialog = ref(false);
const editingId = ref<number | null>(null);
const formData = ref({ shiftCode: '', name: '', isDefault: 0 });

// Shift dialog state
const showShiftDialog = ref(false);
const currentSchedule = ref<ShiftScheduleDto | null>(null);
const shifts = ref<ShiftDto[]>([]);
const showShiftCreateDialog = ref(false);
const showShiftEditDialog = ref(false);
const editingShiftId = ref<number | null>(null);
const shiftFormData = ref({ shiftCode: '', name: '', startTime: '', endTime: '', crossDay: 0 });

// Load schedules
async function loadSchedules() {
  try {
    const res = await listShiftSchedules({
      shiftCode: searchForm.value.shiftCode || undefined,
      name: searchForm.value.name || undefined,
      status: searchForm.value.status,
      pageNum: pagination.value.pageNum,
      pageSize: pagination.value.pageSize,
    });
    if (res.data?.data) {
      schedules.value = res.data.data.records || [];
      pagination.value.total = res.data.data.total;
    }
  } catch (e) {
    console.error('Failed to load schedules:', e);
  }
}

// Load shifts for a schedule
async function loadShifts(scheduleId: number) {
  try {
    const res = await listShifts(scheduleId);
    if (res.data?.data) {
      shifts.value = res.data.data;
    }
  } catch (e) {
    console.error('Failed to load shifts:', e);
  }
}

function handleSearch() {
  pagination.value.pageNum = 1;
  loadSchedules();
}

function handleReset() {
  searchForm.value = { shiftCode: '', name: '', status: undefined };
  pagination.value.pageNum = 1;
  loadSchedules();
}

function handlePageChange(page: number) {
  pagination.value.pageNum = page;
  loadSchedules();
}

function handleEdit(item: ShiftScheduleDto) {
  editingId.value = item.id;
  formData.value = {
    shiftCode: item.shiftCode,
    name: item.name,
    isDefault: item.isDefault,
  };
  showEditDialog.value = true;
}

async function handleSubmit() {
  try {
    if (showEditDialog.value && editingId.value) {
      await updateShiftSchedule(editingId.value, {
        name: formData.value.name,
        isDefault: formData.value.isDefault,
      });
    } else {
      await createShiftSchedule(formData.value);
    }
    closeDialog();
    loadSchedules();
  } catch (e) {
    console.error('Failed to save schedule:', e);
    alert('保存失败，请检查输入');
  }
}

async function handleDelete(item: ShiftScheduleDto) {
  if (!confirm(`确定要删除班制「${item.name}」吗？`)) return;
  try {
    await deleteShiftSchedule(item.id);
    loadSchedules();
  } catch (e) {
    console.error('Failed to delete schedule:', e);
    alert('删除失败，可能存在关联班次');
  }
}

async function handleToggleStatus(item: ShiftScheduleDto) {
  const newStatus = item.status === 1 ? 0 : 1;
  try {
    await updateShiftScheduleStatus(item.id, newStatus);
    loadSchedules();
  } catch (e) {
    console.error('Failed to toggle status:', e);
  }
}

function handleViewShifts(item: ShiftScheduleDto) {
  currentSchedule.value = item;
  showShiftDialog.value = true;
  loadShifts(item.id);
}

// Shift form handlers
function handleEditShift(shift: ShiftDto) {
  editingShiftId.value = shift.id;
  shiftFormData.value = {
    shiftCode: shift.shiftCode,
    name: shift.name,
    startTime: shift.startTime,
    endTime: shift.endTime,
    crossDay: shift.crossDay,
  };
  showShiftEditDialog.value = true;
}

async function handleShiftSubmit() {
  if (!currentSchedule.value) return;
  try {
    if (showShiftEditDialog.value && editingShiftId.value) {
      await updateShift(currentSchedule.value.id, editingShiftId.value, {
        name: shiftFormData.value.name,
        startTime: shiftFormData.value.startTime,
        endTime: shiftFormData.value.endTime,
        crossDay: shiftFormData.value.crossDay,
      });
    } else {
      await createShift(currentSchedule.value.id, shiftFormData.value);
    }
    closeShiftFormDialog();
    loadShifts(currentSchedule.value.id);
  } catch (e) {
    console.error('Failed to save shift:', e);
    alert('保存失败，请检查输入');
  }
}

async function handleDeleteShift(shift: ShiftDto) {
  if (!currentSchedule.value) return;
  if (!confirm(`确定要删除班次「${shift.name}」吗？`)) return;
  try {
    await deleteShift(currentSchedule.value.id, shift.id);
    loadShifts(currentSchedule.value.id);
  } catch (e) {
    console.error('Failed to delete shift:', e);
    alert('删除失败');
  }
}

function closeDialog() {
  showCreateDialog.value = false;
  showEditDialog.value = false;
  editingId.value = null;
  formData.value = { shiftCode: '', name: '', isDefault: 0 };
}

function closeShiftFormDialog() {
  showShiftCreateDialog.value = false;
  showShiftEditDialog.value = false;
  editingShiftId.value = null;
  shiftFormData.value = { shiftCode: '', name: '', startTime: '', endTime: '', crossDay: 0 };
}

function formatDateTime(dt: string | undefined): string {
  if (!dt) return '-';
  return new Date(dt).toLocaleString('zh-CN');
}

onMounted(() => {
  loadSchedules();
});
</script>

<style scoped>
.shift-schedule-list {
  padding: 20px;
}

.search-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  flex-wrap: wrap;
  align-items: center;
}

.search-input,
.search-select {
  padding: 6px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.data-table th,
.data-table td {
  padding: 10px 12px;
  border: 1px solid #e8e8e8;
  text-align: left;
}

.data-table th {
  background: #fafafa;
  font-weight: 600;
}

.data-table tr:hover {
  background: #f5f5f5;
}

.empty-row {
  text-align: center;
  color: #999;
  padding: 40px;
}

.action-cell {
  white-space: nowrap;
}

.btn {
  padding: 6px 14px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  background: #fff;
  transition: all 0.2s;
}

.btn:hover {
  opacity: 0.85;
}

.btn-primary {
  background: #1890ff;
  color: #fff;
  border-color: #1890ff;
}

.btn-success {
  background: #52c41a;
  color: #fff;
  border-color: #52c41a;
}

.btn-danger {
  background: #ff4d4f;
  color: #fff;
  border-color: #ff4d4f;
}

.btn-warning {
  background: #faad14;
  color: #fff;
  border-color: #faad14;
}

.btn-info {
  background: #13c2c2;
  color: #fff;
  border-color: #13c2c2;
}

.btn-default {
  background: #fff;
  border-color: #d9d9d9;
}

.btn-sm {
  padding: 3px 8px;
  font-size: 12px;
  margin-right: 4px;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.status-tag {
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
  background: #fff2e8;
  color: #fa8c16;
  border: 1px solid #ffd591;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 16px;
  justify-content: flex-end;
}

.pagination-info {
  font-size: 14px;
  color: #666;
  margin-right: auto;
}

.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog-content {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  min-width: 480px;
  max-height: 80vh;
  overflow-y: auto;
}

.dialog-wide {
  min-width: 720px;
}

.dialog-content h3 {
  margin: 0 0 20px 0;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 4px;
  font-size: 14px;
  color: #333;
}

.form-group input[type="text"],
.form-group input:not([type]) {
  width: 100%;
  padding: 6px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-group input:disabled {
  background: #f5f5f5;
  color: #999;
}

.form-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
