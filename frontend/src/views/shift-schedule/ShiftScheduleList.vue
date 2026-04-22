<template>
  <div class="h-full flex flex-col space-y-3">
    <!-- Filter Card -->
    <div class="bg-card p-3 border border-border-color shadow-sm rounded-sm shrink-0">
      <el-form :inline="true" :model="searchForm" size="default" class="flex flex-wrap gap-y-3 items-center !mb-0">
        <el-form-item label="班制编码" class="!mb-0">
          <el-input v-model="searchForm.shiftCode" placeholder="班制编码" clearable class="!w-40" />
        </el-form-item>
        <el-form-item label="班制名称" class="!mb-0">
          <el-input v-model="searchForm.name" placeholder="班制名称" clearable class="!w-40" />
        </el-form-item>
        <el-form-item label="状态" class="!mb-0">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable class="!w-28">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item class="!mb-0 !mr-0">
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- Main Content Card -->
    <div class="flex-1 bg-card border border-border-color shadow-sm rounded-sm flex flex-col overflow-hidden">
      <!-- Toolbar -->
      <div class="px-4 py-2.5 border-b border-border-color flex justify-between items-center shrink-0">
        <div class="flex gap-2">
          <el-button type="primary" :icon="Plus" @click="showCreateDialog = true">新增班制</el-button>
        </div>
      </div>

      <!-- Table -->
      <div class="flex-1 p-2.5 overflow-hidden">
        <el-table :data="schedules" border stripe height="100%">
          <el-table-column prop="shiftCode" label="班制编码" width="150" />
          <el-table-column prop="name" label="班制名称" width="180" />
          <el-table-column label="是否默认" width="100" align="center">
            <template #default="{ row }">{{ row.isDefault === 1 ? '是' : '否' }}</template>
          </el-table-column>
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdBy" label="创建人" width="120" />
          <el-table-column label="创建时间" width="180">
            <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="240" fixed="right" align="center">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button link :type="row.status === 1 ? 'warning' : 'success'" size="small" @click="handleToggleStatus(row)">
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button link type="primary" size="small" @click="handleViewShifts(row)">班次</el-button>
              <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- Pagination -->
      <div class="px-4 py-3 border-t border-border-color flex justify-end shrink-0">
        <el-pagination
          layout="total, sizes, prev, pager, next"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          background
          size="small"
          @current-change="loadSchedules"
          @size-change="loadSchedules"
        />
      </div>
    </div>

    <!-- Create/Edit Schedule Dialog -->
    <el-dialog :title="showEditDialog ? '编辑班制' : '新增班制'" width="480px" :model-value="showCreateDialog || showEditDialog" @update:model-value="closeDialog">
      <el-form label-width="80px">
        <el-form-item label="班制编码" required>
          <el-input v-model="formData.shiftCode" :disabled="showEditDialog" placeholder="请输入班制编码" maxlength="50" />
        </el-form-item>
        <el-form-item label="班制名称" required>
          <el-input v-model="formData.name" placeholder="请输入班制名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="默认班制">
          <el-checkbox v-model="formData.isDefault" :true-value="1" :false-value="0">设为默认班制</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="false">确定</el-button>
      </template>
    </el-dialog>

    <!-- Shift List Dialog -->
    <el-dialog :title="`${currentSchedule?.name} - 班次管理`" width="720px" v-model="showShiftDialog">
      <div class="mb-3">
        <el-button type="primary" :icon="Plus" @click="showShiftCreateDialog = true">新增班次</el-button>
      </div>
      <el-table :data="shifts" border stripe max-height="400">
        <el-table-column prop="shiftCode" label="班次编码" width="120" />
        <el-table-column prop="name" label="班次名称" width="120" />
        <el-table-column prop="startTime" label="开始时间" width="100" />
        <el-table-column prop="endTime" label="结束时间" width="100" />
        <el-table-column label="是否跨天" width="100" align="center">
          <template #default="{ row }">{{ row.crossDay === 1 ? '是' : '否' }}</template>
        </el-table-column>
        <el-table-column prop="workHours" label="工时(h)" width="90" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEditShift(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDeleteShift(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="showShiftDialog = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- Shift Create/Edit Dialog -->
    <el-dialog :title="showShiftEditDialog ? '编辑班次' : '新增班次'" width="480px" :model-value="showShiftCreateDialog || showShiftEditDialog" @update:model-value="closeShiftFormDialog">
      <el-form label-width="80px">
        <el-form-item label="班次编码" required>
          <el-input v-model="shiftFormData.shiftCode" :disabled="showShiftEditDialog" placeholder="请输入班次编码" maxlength="50" />
        </el-form-item>
        <el-form-item label="班次名称" required>
          <el-input v-model="shiftFormData.name" placeholder="请输入班次名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="开始时间" required>
          <el-input v-model="shiftFormData.startTime" type="time" />
        </el-form-item>
        <el-form-item label="结束时间" required>
          <el-input v-model="shiftFormData.endTime" type="time" />
        </el-form-item>
        <el-form-item label="跨天班次">
          <el-checkbox v-model="shiftFormData.crossDay" :true-value="1" :false-value="0">跨天班次</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeShiftFormDialog">取消</el-button>
        <el-button type="primary" @click="handleShiftSubmit" :loading="false">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { Search, Refresh, Plus } from '@element-plus/icons-vue';
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
    if (res.code === 200 && res.data) {
      schedules.value = res.data.records || [];
      pagination.value.total = res.data.total;
    }
  } catch (e) {
    console.error('Failed to load schedules:', e);
  }
}

// Load shifts for a schedule
async function loadShifts(scheduleId: number) {
  try {
    const res = await listShifts(scheduleId);
    if (res.code === 200 && res.data) {
      shifts.value = res.data;
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
