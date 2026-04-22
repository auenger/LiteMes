<template>
  <div class="h-full flex flex-col space-y-3">
    <!-- Filter Card -->
    <div class="bg-card p-3 border border-border-color shadow-sm rounded-sm shrink-0">
      <el-form :inline="true" :model="query" size="default" class="flex flex-wrap gap-y-3 items-center !mb-0">
        <el-form-item label="工序编码" class="!mb-0">
          <el-input v-model="query.processCode" placeholder="工序编码" clearable class="!w-40" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="工序名称" class="!mb-0">
          <el-input v-model="query.name" placeholder="工序名称" clearable class="!w-40" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="所属工厂" class="!mb-0">
          <el-select v-model="query.factoryId" placeholder="全部工厂" clearable class="!w-40" @change="onFactoryChange">
            <el-option v-for="f in factoryOptions" :key="f.id" :label="f.name" :value="f.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="工作中心" class="!mb-0">
          <el-select v-model="query.workCenterId" placeholder="全部工作中心" clearable class="!w-40">
            <el-option v-for="wc in filteredWorkCenterOptions" :key="wc.id" :label="wc.name" :value="wc.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" class="!mb-0">
          <el-select v-model="query.status" placeholder="全部状态" clearable class="!w-28">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item class="!mb-0 !mr-0">
          <el-button type="primary" :icon="Search" @click="search">查询</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- Main Content Card -->
    <div class="flex-1 bg-card border border-border-color shadow-sm rounded-sm flex flex-col overflow-hidden">
      <!-- Toolbar -->
      <div class="px-4 py-2.5 border-b border-border-color flex justify-between items-center shrink-0">
        <div class="flex gap-2">
          <el-button type="primary" :icon="Plus" @click="openCreateDialog">新建工序</el-button>
        </div>
        <div class="flex gap-2 items-center">
          <el-button :icon="Setting" @click="showTableSettings = !showTableSettings">表格设置</el-button>
          <TableSettingsPanel
            :visible="showTableSettings"
            :columns="columns"
            @close="showTableSettings = false"
            @toggle="toggleColumn"
            @reset="resetSettings"
          />
        </div>
      </div>

      <!-- Table -->
      <div class="flex-1 p-2.5 overflow-hidden">
        <el-table :data="processes" border stripe height="100%" v-loading="loading">
          <el-table-column v-if="isColumnVisible('processCode')" prop="processCode" label="工序编码" width="160" />
          <el-table-column v-if="isColumnVisible('name')" prop="name" label="工序名称" width="180" />
          <el-table-column v-if="isColumnVisible('workCenterName')" label="工作中心" width="180">
            <template #default="{ row }">{{ row.workCenterName || '-' }}</template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('factoryName')" label="所属工厂" width="180">
            <template #default="{ row }">{{ row.factoryName || '-' }}</template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('status')" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('createdBy')" label="创建人" width="120">
            <template #default="{ row }">{{ row.createdBy || '-' }}</template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('createdAt')" label="创建时间" width="180">
            <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="240" fixed="right" align="center">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="openEditDialog(row)">编辑</el-button>
              <el-button link :type="row.status === 1 ? 'warning' : 'success'" size="small" @click="toggleStatus(row)">
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button link type="danger" size="small" @click="confirmDelete(row)">删除</el-button>
              <el-button link type="primary" size="small" @click="openAuditLog(row)">履历</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- Pagination -->
      <div class="px-4 py-3 border-t border-border-color flex justify-end shrink-0">
        <el-pagination
          layout="total, sizes, prev, pager, next"
          :total="total"
          :page-sizes="[10, 20, 50]"
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          background
          size="small"
          @current-change="fetchList"
          @size-change="fetchList"
        />
      </div>
    </div>

    <!-- Create/Edit Dialog -->
    <el-dialog :title="isEdit ? '编辑工序' : '新建工序'" width="480px" v-model="dialogVisible">
      <el-form label-width="100px">
        <el-form-item label="工序编码" required>
          <el-input v-model="form.processCode" :disabled="isEdit" placeholder="请输入工序编码" />
        </el-form-item>
        <el-form-item label="工序名称" required>
          <el-input v-model="form.name" placeholder="请输入工序名称" />
        </el-form-item>
        <el-form-item label="工作中心" required>
          <el-select v-model="form.workCenterId" :disabled="isEdit" placeholder="请选择工作中心" class="!w-full">
            <el-option v-for="wc in dialogWorkCenterOptions" :key="wc.id" :label="wc.name" :value="wc.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <div v-if="formError" class="text-red-500 text-sm px-4">{{ formError }}</div>
      <template #footer>
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- Delete Confirm -->
    <el-dialog title="确认删除" width="400px" v-model="deleteDialogVisible">
      <p>确定要删除工序 "{{ deleteTarget?.name }}" ({{ deleteTarget?.processCode }}) 吗？</p>
      <p class="text-gray-400 text-sm">删除后不可恢复。</p>
      <template #footer>
        <el-button @click="closeDeleteDialog">取消</el-button>
        <el-button type="danger" @click="doDelete" :loading="submitting">删除</el-button>
      </template>
    </el-dialog>

    <!-- Audit Log -->
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
import { Search, Refresh, Plus, Setting } from '@element-plus/icons-vue';
import {
  listProcesses,
  createProcess,
  updateProcess,
  deleteProcess,
  updateProcessStatus,
  type ProcessDto,
  type ProcessQueryParams,
} from '../../api/process';
import { getFactoryDropdown, getWorkCenterDropdown, type DropdownItem } from '../../api/dropdown';
import AuditLogDialog from '../../components/AuditLogDialog.vue';
import TableSettingsPanel from '../../components/TableSettingsPanel.vue';
import { useTableSettings, type ColumnDef } from '../../components/useTableSettings';

const processes = ref<ProcessDto[]>([]);
const total = ref(0);
const loading = ref(false);
const submitting = ref(false);
const formError = ref('');
const factoryOptions = ref<DropdownItem[]>([]);
const workCenterOptions = ref<DropdownItem[]>([]);

const query = reactive<ProcessQueryParams>({
  processCode: '',
  name: '',
  factoryId: undefined,
  workCenterId: undefined,
  status: undefined,
  page: 1,
  size: 10,
});

// Filter work centers by selected factory in query
const filteredWorkCenterOptions = computed(() => {
  if (!query.factoryId) return workCenterOptions.value;
  return workCenterOptions.value;
});

// Table settings
const defaultColumns: ColumnDef[] = [
  { key: 'processCode', label: '工序编码' },
  { key: 'name', label: '工序名称' },
  { key: 'workCenterName', label: '工作中心' },
  { key: 'factoryName', label: '所属工厂' },
  { key: 'status', label: '状态' },
  { key: 'createdBy', label: '创建人' },
  { key: 'createdAt', label: '创建时间' },
];

const { columns, toggleColumn, resetSettings, isColumnVisible } = useTableSettings('process-list', defaultColumns);
const showTableSettings = ref(false);

// Audit log dialog state
const auditLogVisible = ref(false);
const auditLogTableName = ref('process');
const auditLogRecordId = ref(0);

function openAuditLog(row: ProcessDto) {
  auditLogTableName.value = 'process';
  auditLogRecordId.value = row.id;
  auditLogVisible.value = true;
}

// Dialog state
const dialogVisible = ref(false);
const isEdit = ref(false);
const editingId = ref<number | null>(null);
const form = reactive({
  processCode: '',
  name: '',
  workCenterId: undefined as number | undefined,
});

// Work center options for dialog (filtered by factory if cascading is desired)
const dialogWorkCenterOptions = computed(() => workCenterOptions.value);

// Delete dialog state
const deleteDialogVisible = ref(false);
const deleteTarget = ref<ProcessDto | null>(null);

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

async function fetchWorkCenters(factoryId?: number) {
  try {
    const res = await getWorkCenterDropdown(factoryId);
    if (res.code === 200 && res.data) {
      workCenterOptions.value = res.data;
    }
  } catch (e) {
    console.error('Failed to fetch work center options', e);
  }
}

function onFactoryChange(factoryId: number | undefined) {
  query.workCenterId = undefined;
  fetchWorkCenters(factoryId);
}

async function fetchList() {
  loading.value = true;
  try {
    const res = await listProcesses(query);
    if (res.code === 200 && res.data) {
      processes.value = res.data.records;
      total.value = res.data.total;
    }
  } catch (e) {
    console.error('Failed to fetch process list', e);
  } finally {
    loading.value = false;
  }
}

function search() {
  query.page = 1;
  fetchList();
}

function resetQuery() {
  query.processCode = '';
  query.name = '';
  query.factoryId = undefined;
  query.workCenterId = undefined;
  query.status = undefined;
  query.page = 1;
  fetchWorkCenters();
  fetchList();
}

function openCreateDialog() {
  isEdit.value = false;
  editingId.value = null;
  form.processCode = '';
  form.name = '';
  form.workCenterId = undefined;
  formError.value = '';
  dialogVisible.value = true;
}

function openEditDialog(row: ProcessDto) {
  isEdit.value = true;
  editingId.value = row.id;
  form.processCode = row.processCode;
  form.name = row.name;
  form.workCenterId = row.workCenterId;
  formError.value = '';
  dialogVisible.value = true;
}

function closeDialog() {
  dialogVisible.value = false;
}

async function submitForm() {
  if (!form.processCode && !isEdit.value) {
    formError.value = '工序编码不能为空';
    return;
  }
  if (!form.name) {
    formError.value = '工序名称不能为空';
    return;
  }
  if (!form.workCenterId && !isEdit.value) {
    formError.value = '请选择工作中心';
    return;
  }

  submitting.value = true;
  formError.value = '';
  try {
    if (isEdit.value && editingId.value) {
      await updateProcess(editingId.value, { name: form.name });
    } else {
      await createProcess({
        processCode: form.processCode,
        name: form.name,
        workCenterId: form.workCenterId!,
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

function confirmDelete(row: ProcessDto) {
  deleteTarget.value = row;
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
    await deleteProcess(deleteTarget.value.id);
    closeDeleteDialog();
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '删除失败';
    alert(msg);
  } finally {
    submitting.value = false;
  }
}

async function toggleStatus(row: ProcessDto) {
  const newStatus = row.status === 1 ? 0 : 1;
  try {
    await updateProcessStatus(row.id, newStatus);
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
  fetchWorkCenters();
  fetchList();
});
</script>
