<template>
  <div class="h-full flex flex-col space-y-3">
    <!-- Filter Card -->
    <div class="bg-card p-3 border border-border-color shadow-sm rounded-sm shrink-0">
      <el-form :inline="true" :model="query" size="default" class="flex flex-wrap gap-y-3 items-center !mb-0">
        <el-form-item label="设备编码" class="!mb-0">
          <el-input v-model="query.equipmentCode" placeholder="设备编码" clearable class="!w-36" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="设备名称" class="!mb-0">
          <el-input v-model="query.equipmentName" placeholder="设备名称" clearable class="!w-36" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="设备类型" class="!mb-0">
          <el-select v-model="query.equipmentTypeId" placeholder="全部设备类型" clearable class="!w-44">
            <el-option v-for="t in equipmentTypes" :key="t.id" :label="`${t.typeName} (${t.typeCode})`" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="设备型号" class="!mb-0">
          <el-select v-model="query.equipmentModelId" placeholder="全部设备型号" clearable class="!w-44">
            <el-option v-for="m in filteredModels" :key="m.id" :label="`${m.modelName} (${m.modelCode})`" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="运行状态" class="!mb-0">
          <el-select v-model="query.runningStatus" placeholder="全部运行状态" clearable class="!w-36">
            <el-option label="运行" value="RUNNING" />
            <el-option label="故障" value="FAULT" />
            <el-option label="停机" value="SHUTDOWN" />
            <el-option label="维修保养" value="MAINTENANCE" />
          </el-select>
        </el-form-item>
        <el-form-item label="管理状态" class="!mb-0">
          <el-select v-model="query.manageStatus" placeholder="全部管理状态" clearable class="!w-36">
            <el-option label="使用中" value="IN_USE" />
            <el-option label="闲置" value="IDLE" />
            <el-option label="报废" value="SCRAPPED" />
          </el-select>
        </el-form-item>
        <el-form-item label="工厂" class="!mb-0">
          <el-select v-model="query.factoryId" placeholder="全部工厂" clearable class="!w-40">
            <el-option v-for="f in factories" :key="f.id" :label="`${f.name} (${f.code})`" :value="f.id" />
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
      <div class="px-4 py-2.5 border-b border-border-color flex justify-between items-center shrink-0">
        <div class="flex gap-2">
          <el-button type="primary" :icon="Plus" @click="openCreateDialog">新建设备</el-button>
          <el-button :icon="Setting" @click="showTableSettings = !showTableSettings">表格设置</el-button>
        </div>
        <TableSettingsPanel
          :visible="showTableSettings"
          :columns="columns"
          @close="showTableSettings = false"
        />
      </div>
      <div class="flex-1 p-2.5 overflow-hidden">
        <el-table :data="ledgers" border stripe height="100%" v-loading="loading">
          <el-table-column v-if="isColumnVisible('equipmentCode')" prop="equipmentCode" label="设备编码" min-width="120" />
          <el-table-column v-if="isColumnVisible('equipmentName')" prop="equipmentName" label="设备名称" min-width="120" />
          <el-table-column v-if="isColumnVisible('typeCode')" prop="typeCode" label="设备类型编码" min-width="120" />
          <el-table-column v-if="isColumnVisible('typeName')" prop="typeName" label="设备类型名称" min-width="120" />
          <el-table-column v-if="isColumnVisible('modelCode')" prop="modelCode" label="设备型号编码" min-width="120" />
          <el-table-column v-if="isColumnVisible('modelName')" prop="modelName" label="设备型号名称" min-width="120" />
          <el-table-column v-if="isColumnVisible('runningStatus')" label="运行状态" min-width="100">
            <template #default="{ row }">
              <el-tag :type="runningStatusTagType(row.runningStatus)" size="small">
                {{ getRunningStatusLabel(row.runningStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('manageStatus')" label="管理状态" min-width="100">
            <template #default="{ row }">
              <el-tag :type="manageStatusTagType(row.manageStatus)" size="small">
                {{ getManageStatusLabel(row.manageStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('factoryName')" prop="factoryName" label="工厂" min-width="100" />
          <el-table-column v-if="isColumnVisible('manufacturer')" label="生产厂家" min-width="100">
            <template #default="{ row }">{{ row.manufacturer || '-' }}</template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('commissioningDate')" label="入场时间" min-width="110">
            <template #default="{ row }">{{ row.commissioningDate || '-' }}</template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('status')" label="状态" min-width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('createdBy')" label="创建人" min-width="100">
            <template #default="{ row }">{{ row.createdBy || '-' }}</template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('createdAt')" label="创建时间" min-width="160">
            <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
          </el-table-column>
          <el-table-column label="操作" min-width="260" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
              <el-button
                v-if="row.status === 1"
                size="small"
                type="warning"
                @click="toggleStatus(row)"
              >禁用</el-button>
              <el-button
                v-else
                size="small"
                type="success"
                @click="toggleStatus(row)"
              >启用</el-button>
              <el-button size="small" type="danger" @click="confirmDelete(row)">删除</el-button>
              <el-button size="small" type="info" @click="openAuditLog(row)">变更履历</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="px-4 py-3 border-t border-border-color flex justify-end shrink-0">
        <el-pagination
          layout="total, sizes, prev, pager, next"
          :total="total"
          :page-size="query.size"
          :current-page="query.page"
          background
          size="small"
          @current-change="goPage"
          @size-change="(size: number) => { query.size = size; search() }"
        />
      </div>
    </div>

    <!-- Create/Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑设备台账' : '新建设备台账'" width="680px" @close="closeDialog">
      <el-form label-width="100px">
        <div class="flex gap-4">
          <el-form-item label="设备编码" required class="flex-1">
            <el-input v-model="form.equipmentCode" :disabled="isEdit" placeholder="请输入设备编码" />
          </el-form-item>
          <el-form-item label="设备名称" required class="flex-1">
            <el-input v-model="form.equipmentName" placeholder="请输入设备名称" />
          </el-form-item>
        </div>
        <div class="flex gap-4">
          <el-form-item label="设备型号" required class="flex-1">
            <el-select v-model="form.equipmentModelId" placeholder="请选择设备型号" class="!w-full" @change="onModelChange">
              <el-option v-for="m in formModels" :key="m.id" :label="`${m.modelName} (${m.modelCode})`" :value="m.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="设备类型名称" class="flex-1">
            <el-input :model-value="selectedTypeName" disabled placeholder="根据型号自动带出" />
          </el-form-item>
        </div>
        <div class="flex gap-4">
          <el-form-item label="运行状态" required class="flex-1">
            <el-select v-model="form.runningStatus" class="!w-full">
              <el-option label="运行" value="RUNNING" />
              <el-option label="故障" value="FAULT" />
              <el-option label="停机" value="SHUTDOWN" />
              <el-option label="维修保养" value="MAINTENANCE" />
            </el-select>
          </el-form-item>
          <el-form-item label="管理状态" required class="flex-1">
            <el-select v-model="form.manageStatus" class="!w-full">
              <el-option label="使用中" value="IN_USE" />
              <el-option label="闲置" value="IDLE" />
              <el-option label="报废" value="SCRAPPED" />
            </el-select>
          </el-form-item>
        </div>
        <div class="flex gap-4">
          <el-form-item label="工厂" required class="flex-1">
            <el-select v-model="form.factoryId" placeholder="请选择工厂" class="!w-full" @change="onFactoryChange">
              <el-option v-for="f in factories" :key="f.id" :label="`${f.name} (${f.code})`" :value="f.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="工厂名称" class="flex-1">
            <el-input :model-value="selectedFactoryName" disabled placeholder="根据工厂自动带出" />
          </el-form-item>
        </div>
        <div class="flex gap-4">
          <el-form-item label="生产厂家" class="flex-1">
            <el-input v-model="form.manufacturer" placeholder="请输入生产厂家" />
          </el-form-item>
          <el-form-item label="入场时间" required class="flex-1">
            <el-input v-model="form.commissioningDate" type="date" />
          </el-form-item>
        </div>
      </el-form>
      <div v-if="formError" class="text-red-500 text-sm px-4 pb-2">{{ formError }}</div>
      <template #footer>
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- Delete Confirm Dialog -->
    <el-dialog v-model="deleteDialogVisible" title="确认删除" width="400px" @close="closeDeleteDialog">
      <p>确定要删除设备 "{{ deleteTarget?.equipmentName }}" ({{ deleteTarget?.equipmentCode }}) 吗？</p>
      <p class="text-gray-400 text-sm mt-2">已被业务引用的设备不可删除。删除后不可恢复。</p>
      <template #footer>
        <el-button @click="closeDeleteDialog">取消</el-button>
        <el-button type="danger" @click="doDelete" :loading="submitting">删除</el-button>
      </template>
    </el-dialog>

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
import { Search, Refresh, Plus, Setting } from '@element-plus/icons-vue';
import {
  listEquipmentLedgers,
  createEquipmentLedger,
  updateEquipmentLedger,
  deleteEquipmentLedger,
  updateEquipmentLedgerStatus,
  type EquipmentLedgerDto,
  type EquipmentLedgerQueryParams,
} from '../../api/equipmentLedger';
import {
  listEquipmentTypes,
  type EquipmentTypeDto,
} from '../../api/equipmentType';
import {
  listEquipmentModels,
  type EquipmentModelDto,
} from '../../api/equipmentModel';
import {
  getFactoryDropdown,
  type DropdownItem,
} from '../../api/dropdown';
import AuditLogDialog from '../../components/AuditLogDialog.vue';
import TableSettingsPanel from '../../components/TableSettingsPanel.vue';
import { useTableSettings, type ColumnDef } from '../../components/useTableSettings';

const ledgers = ref<EquipmentLedgerDto[]>([]);
const total = ref(0);
const loading = ref(false);
const submitting = ref(false);
const formError = ref('');

// Dropdown data
const equipmentTypes = ref<EquipmentTypeDto[]>([]);
const equipmentModels = ref<EquipmentModelDto[]>([]);
const factories = ref<DropdownItem[]>([]);

const query = reactive<EquipmentLedgerQueryParams>({
  equipmentCode: '',
  equipmentName: '',
  equipmentTypeId: undefined,
  equipmentModelId: undefined,
  runningStatus: undefined,
  manageStatus: undefined,
  factoryId: undefined,
  status: undefined,
  page: 1,
  size: 10,
});

const totalPages = computed(() => Math.ceil(total.value / (query.size || 10)));

// Filter models by selected type in search bar
const filteredModels = computed(() => {
  if (!query.equipmentTypeId) return equipmentModels.value;
  return equipmentModels.value.filter(m => m.equipmentTypeId === query.equipmentTypeId);
});

// Table settings
const defaultColumns: ColumnDef[] = [
  { key: 'equipmentCode', label: '设备编码' },
  { key: 'equipmentName', label: '设备名称' },
  { key: 'typeCode', label: '设备类型编码' },
  { key: 'typeName', label: '设备类型名称' },
  { key: 'modelCode', label: '设备型号编码' },
  { key: 'modelName', label: '设备型号名称' },
  { key: 'runningStatus', label: '运行状态' },
  { key: 'manageStatus', label: '管理状态' },
  { key: 'factoryName', label: '工厂' },
  { key: 'manufacturer', label: '生产厂家' },
  { key: 'commissioningDate', label: '入场时间' },
  { key: 'status', label: '状态' },
  { key: 'createdBy', label: '创建人' },
  { key: 'createdAt', label: '创建时间' },
];

const { columns } = useTableSettings('equipment-ledger-list', defaultColumns);
const showTableSettings = ref(false);

function isColumnVisible(key: string): boolean {
  return columns.value.find(c => c.key === key)?.visible !== false;
}

const visibleColCount = computed(() => columns.value.filter(c => c.visible).length);

// Audit log dialog state
const auditLogVisible = ref(false);
const auditLogTableName = ref('equipment_ledger');
const auditLogRecordId = ref(0);

function openAuditLog(item: EquipmentLedgerDto) {
  auditLogTableName.value = 'equipment_ledger';
  auditLogRecordId.value = item.id;
  auditLogVisible.value = true;
}

// Dialog state
const dialogVisible = ref(false);
const isEdit = ref(false);
const editingId = ref<number | null>(null);
const form = reactive({
  equipmentCode: '',
  equipmentName: '',
  equipmentModelId: undefined as number | undefined,
  runningStatus: 'SHUTDOWN' as string,
  manageStatus: 'IN_USE' as string,
  factoryId: undefined as number | undefined,
  manufacturer: '',
  commissioningDate: '',
});

// Form models (all models for the form dialog)
const formModels = computed(() => equipmentModels.value);

// Selected type name from model cascade
const selectedTypeName = computed(() => {
  if (!form.equipmentModelId) return '';
  const m = equipmentModels.value.find(em => em.id === form.equipmentModelId);
  return m ? m.typeName : '';
});

// Selected factory name
const selectedFactoryName = computed(() => {
  if (!form.factoryId) return '';
  const f = factories.value.find(f => f.id === form.factoryId);
  return f ? f.name : '';
});

function onModelChange() {
  // selectedTypeName is computed from form.equipmentModelId
}

function onFactoryChange() {
  // selectedFactoryName is computed from form.factoryId
}

// Delete dialog state
const deleteDialogVisible = ref(false);
const deleteTarget = ref<EquipmentLedgerDto | null>(null);

// Status label helpers
function getRunningStatusLabel(status: string): string {
  const map: Record<string, string> = {
    RUNNING: '运行',
    FAULT: '故障',
    SHUTDOWN: '停机',
    MAINTENANCE: '维修保养',
  };
  return map[status] || status;
}

function runningStatusTagType(status: string): string {
  const map: Record<string, string> = {
    RUNNING: 'success',
    FAULT: 'danger',
    SHUTDOWN: 'info',
    MAINTENANCE: '',
  };
  return map[status] || 'info';
}

function getManageStatusLabel(status: string): string {
  const map: Record<string, string> = {
    IN_USE: '使用中',
    IDLE: '闲置',
    SCRAPPED: '报废',
  };
  return map[status] || status;
}

function manageStatusTagType(status: string): string {
  const map: Record<string, string> = {
    IN_USE: 'success',
    IDLE: 'warning',
    SCRAPPED: 'danger',
  };
  return map[status] || 'info';
}

async function fetchDropdownData() {
  try {
    const [typeRes, modelRes, factoryRes] = await Promise.all([
      listEquipmentTypes({ status: 1, size: 1000 }),
      listEquipmentModels({ status: 1, size: 1000 }),
      getFactoryDropdown(),
    ]);
    if (typeRes.code === 200 && typeRes.data) {
      equipmentTypes.value = typeRes.data.records;
    }
    if (modelRes.code === 200 && modelRes.data) {
      equipmentModels.value = modelRes.data.records;
    }
    if (factoryRes.code === 200 && factoryRes.data) {
      factories.value = factoryRes.data;
    }
  } catch (e) {
    console.error('Failed to fetch dropdown data', e);
  }
}

async function fetchList() {
  loading.value = true;
  try {
    const res = await listEquipmentLedgers(query);
    if (res.code === 200 && res.data) {
      ledgers.value = res.data.records;
      total.value = res.data.total;
    }
  } catch (e) {
    console.error('Failed to fetch equipment ledger list', e);
  } finally {
    loading.value = false;
  }
}

function search() {
  query.page = 1;
  fetchList();
}

function resetQuery() {
  query.equipmentCode = '';
  query.equipmentName = '';
  query.equipmentTypeId = undefined;
  query.equipmentModelId = undefined;
  query.runningStatus = undefined;
  query.manageStatus = undefined;
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
  form.equipmentCode = '';
  form.equipmentName = '';
  form.equipmentModelId = undefined;
  form.runningStatus = 'SHUTDOWN';
  form.manageStatus = 'IN_USE';
  form.factoryId = undefined;
  form.manufacturer = '';
  form.commissioningDate = '';
  formError.value = '';
  dialogVisible.value = true;
}

function openEditDialog(item: EquipmentLedgerDto) {
  isEdit.value = true;
  editingId.value = item.id;
  form.equipmentCode = item.equipmentCode;
  form.equipmentName = item.equipmentName;
  form.equipmentModelId = item.equipmentModelId;
  form.runningStatus = item.runningStatus;
  form.manageStatus = item.manageStatus;
  form.factoryId = item.factoryId;
  form.manufacturer = item.manufacturer || '';
  form.commissioningDate = item.commissioningDate || '';
  formError.value = '';
  dialogVisible.value = true;
}

function closeDialog() {
  dialogVisible.value = false;
}

async function submitForm() {
  if (!form.equipmentCode && !isEdit.value) {
    formError.value = '设备编码不能为空';
    return;
  }
  if (!form.equipmentName) {
    formError.value = '设备名称不能为空';
    return;
  }
  if (!form.equipmentModelId) {
    formError.value = '设备型号不能为空';
    return;
  }
  if (!form.runningStatus) {
    formError.value = '运行状态不能为空';
    return;
  }
  if (!form.manageStatus) {
    formError.value = '管理状态不能为空';
    return;
  }
  if (!form.factoryId) {
    formError.value = '工厂不能为空';
    return;
  }
  if (!form.commissioningDate) {
    formError.value = '入场时间不能为空';
    return;
  }

  submitting.value = true;
  formError.value = '';
  try {
    if (isEdit.value && editingId.value) {
      await updateEquipmentLedger(editingId.value, {
        equipmentName: form.equipmentName,
        equipmentModelId: form.equipmentModelId,
        runningStatus: form.runningStatus,
        manageStatus: form.manageStatus,
        factoryId: form.factoryId,
        manufacturer: form.manufacturer || undefined,
        commissioningDate: form.commissioningDate,
      });
    } else {
      await createEquipmentLedger({
        equipmentCode: form.equipmentCode,
        equipmentName: form.equipmentName,
        equipmentModelId: form.equipmentModelId,
        runningStatus: form.runningStatus,
        manageStatus: form.manageStatus,
        factoryId: form.factoryId,
        manufacturer: form.manufacturer || undefined,
        commissioningDate: form.commissioningDate,
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

function confirmDelete(item: EquipmentLedgerDto) {
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
    await deleteEquipmentLedger(deleteTarget.value.id);
    closeDeleteDialog();
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '删除失败';
    alert(msg);
  } finally {
    submitting.value = false;
  }
}

async function toggleStatus(item: EquipmentLedgerDto) {
  const newStatus = item.status === 1 ? 0 : 1;
  try {
    await updateEquipmentLedgerStatus(item.id, newStatus);
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
  fetchDropdownData();
  fetchList();
});
</script>
