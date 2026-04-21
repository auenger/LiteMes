<template>
  <div class="equipment-ledger-page">
    <div class="page-header">
      <h2>设备台账管理</h2>
      <div class="header-actions">
        <div class="table-settings-wrapper" style="position: relative;">
          <button class="btn" @click="showTableSettings = !showTableSettings">表格设置</button>
          <TableSettingsPanel
            :visible="showTableSettings"
            :columns="columns"
            @close="showTableSettings = false"
          />
        </div>
        <button class="btn btn-primary" @click="openCreateDialog">新建设备</button>
      </div>
    </div>

    <!-- Search Bar -->
    <div class="search-bar">
      <input
        v-model="query.equipmentCode"
        type="text"
        placeholder="设备编码"
        class="input"
        @keyup.enter="search"
      />
      <input
        v-model="query.equipmentName"
        type="text"
        placeholder="设备名称"
        class="input"
        @keyup.enter="search"
      />
      <select v-model="query.equipmentTypeId" class="input select">
        <option :value="undefined">全部设备类型</option>
        <option v-for="t in equipmentTypes" :key="t.id" :value="t.id">{{ t.typeName }} ({{ t.typeCode }})</option>
      </select>
      <select v-model="query.equipmentModelId" class="input select">
        <option :value="undefined">全部设备型号</option>
        <option v-for="m in filteredModels" :key="m.id" :value="m.id">{{ m.modelName }} ({{ m.modelCode }})</option>
      </select>
      <select v-model="query.runningStatus" class="input select">
        <option :value="undefined">全部运行状态</option>
        <option value="RUNNING">运行</option>
        <option value="FAULT">故障</option>
        <option value="SHUTDOWN">停机</option>
        <option value="MAINTENANCE">维修保养</option>
      </select>
      <select v-model="query.manageStatus" class="input select">
        <option :value="undefined">全部管理状态</option>
        <option value="IN_USE">使用中</option>
        <option value="IDLE">闲置</option>
        <option value="SCRAPPED">报废</option>
      </select>
      <select v-model="query.factoryId" class="input select">
        <option :value="undefined">全部工厂</option>
        <option v-for="f in factories" :key="f.id" :value="f.id">{{ f.name }} ({{ f.code }})</option>
      </select>
      <button class="btn" @click="search">查询</button>
      <button class="btn btn-secondary" @click="resetQuery">重置</button>
    </div>

    <!-- Equipment Ledger Table -->
    <table class="table">
      <thead>
        <tr>
          <th v-if="isColumnVisible('equipmentCode')">设备编码</th>
          <th v-if="isColumnVisible('equipmentName')">设备名称</th>
          <th v-if="isColumnVisible('typeCode')">设备类型编码</th>
          <th v-if="isColumnVisible('typeName')">设备类型名称</th>
          <th v-if="isColumnVisible('modelCode')">设备型号编码</th>
          <th v-if="isColumnVisible('modelName')">设备型号名称</th>
          <th v-if="isColumnVisible('runningStatus')">运行状态</th>
          <th v-if="isColumnVisible('manageStatus')">管理状态</th>
          <th v-if="isColumnVisible('factoryName')">工厂</th>
          <th v-if="isColumnVisible('manufacturer')">生产厂家</th>
          <th v-if="isColumnVisible('commissioningDate')">入场时间</th>
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
        <tr v-else-if="ledgers.length === 0">
          <td :colspan="visibleColCount + 1" class="text-center">暂无数据</td>
        </tr>
        <tr v-for="item in ledgers" :key="item.id">
          <td v-if="isColumnVisible('equipmentCode')">{{ item.equipmentCode }}</td>
          <td v-if="isColumnVisible('equipmentName')">{{ item.equipmentName }}</td>
          <td v-if="isColumnVisible('typeCode')">{{ item.typeCode }}</td>
          <td v-if="isColumnVisible('typeName')">{{ item.typeName }}</td>
          <td v-if="isColumnVisible('modelCode')">{{ item.modelCode }}</td>
          <td v-if="isColumnVisible('modelName')">{{ item.modelName }}</td>
          <td v-if="isColumnVisible('runningStatus')">
            <span :class="['status-badge', getRunningStatusClass(item.runningStatus)]">
              {{ getRunningStatusLabel(item.runningStatus) }}
            </span>
          </td>
          <td v-if="isColumnVisible('manageStatus')">
            <span :class="['status-badge', getManageStatusClass(item.manageStatus)]">
              {{ getManageStatusLabel(item.manageStatus) }}
            </span>
          </td>
          <td v-if="isColumnVisible('factoryName')">{{ item.factoryName }}</td>
          <td v-if="isColumnVisible('manufacturer')">{{ item.manufacturer || '-' }}</td>
          <td v-if="isColumnVisible('commissioningDate')">{{ item.commissioningDate || '-' }}</td>
          <td v-if="isColumnVisible('status')">
            <span :class="['status-badge', item.status === 1 ? 'status-enabled' : 'status-disabled']">
              {{ item.status === 1 ? '启用' : '禁用' }}
            </span>
          </td>
          <td v-if="isColumnVisible('createdBy')">{{ item.createdBy || '-' }}</td>
          <td v-if="isColumnVisible('createdAt')">{{ formatDate(item.createdAt) }}</td>
          <td class="actions">
            <button class="btn btn-sm" @click="openEditDialog(item)">编辑</button>
            <button
              v-if="item.status === 1"
              class="btn btn-sm btn-warning"
              @click="toggleStatus(item)"
            >
              禁用
            </button>
            <button
              v-else
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
      <div class="dialog dialog-lg">
        <div class="dialog-header">
          <h3>{{ isEdit ? '编辑设备台账' : '新建设备台账' }}</h3>
          <button class="dialog-close" @click="closeDialog">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="form-row">
            <div class="form-group">
              <label>设备编码 <span class="required">*</span></label>
              <input
                v-model="form.equipmentCode"
                type="text"
                class="input"
                :disabled="isEdit"
                :class="{ 'input-disabled': isEdit }"
                placeholder="请输入设备编码"
              />
            </div>
            <div class="form-group">
              <label>设备名称 <span class="required">*</span></label>
              <input
                v-model="form.equipmentName"
                type="text"
                class="input"
                placeholder="请输入设备名称"
              />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>设备型号 <span class="required">*</span></label>
              <select
                v-model="form.equipmentModelId"
                class="input select"
                @change="onModelChange"
              >
                <option :value="undefined" disabled>请选择设备型号</option>
                <option v-for="m in formModels" :key="m.id" :value="m.id">{{ m.modelName }} ({{ m.modelCode }})</option>
              </select>
            </div>
            <div class="form-group">
              <label>设备类型名称</label>
              <input
                :value="selectedTypeName"
                type="text"
                class="input"
                disabled
                :class="{ 'input-disabled': true }"
                placeholder="根据型号自动带出"
              />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>运行状态 <span class="required">*</span></label>
              <select v-model="form.runningStatus" class="input select">
                <option value="RUNNING">运行</option>
                <option value="FAULT">故障</option>
                <option value="SHUTDOWN">停机</option>
                <option value="MAINTENANCE">维修保养</option>
              </select>
            </div>
            <div class="form-group">
              <label>管理状态 <span class="required">*</span></label>
              <select v-model="form.manageStatus" class="input select">
                <option value="IN_USE">使用中</option>
                <option value="IDLE">闲置</option>
                <option value="SCRAPPED">报废</option>
              </select>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>工厂 <span class="required">*</span></label>
              <select v-model="form.factoryId" class="input select" @change="onFactoryChange">
                <option :value="undefined" disabled>请选择工厂</option>
                <option v-for="f in factories" :key="f.id" :value="f.id">{{ f.name }} ({{ f.code }})</option>
              </select>
            </div>
            <div class="form-group">
              <label>工厂名称</label>
              <input
                :value="selectedFactoryName"
                type="text"
                class="input"
                disabled
                :class="{ 'input-disabled': true }"
                placeholder="根据工厂自动带出"
              />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>生产厂家</label>
              <input
                v-model="form.manufacturer"
                type="text"
                class="input"
                placeholder="请输入生产厂家"
              />
            </div>
            <div class="form-group">
              <label>入场时间 <span class="required">*</span></label>
              <input
                v-model="form.commissioningDate"
                type="date"
                class="input"
              />
            </div>
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
          <p>确定要删除设备 "{{ deleteTarget?.equipmentName }}" ({{ deleteTarget?.equipmentCode }}) 吗？</p>
          <p class="text-muted">已被业务引用的设备不可删除。删除后不可恢复。</p>
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

function getRunningStatusClass(status: string): string {
  const map: Record<string, string> = {
    RUNNING: 'status-running',
    FAULT: 'status-fault',
    SHUTDOWN: 'status-shutdown',
    MAINTENANCE: 'status-maintenance',
  };
  return map[status] || '';
}

function getManageStatusLabel(status: string): string {
  const map: Record<string, string> = {
    IN_USE: '使用中',
    IDLE: '闲置',
    SCRAPPED: '报废',
  };
  return map[status] || status;
}

function getManageStatusClass(status: string): string {
  const map: Record<string, string> = {
    IN_USE: 'status-in-use',
    IDLE: 'status-idle',
    SCRAPPED: 'status-scrapped',
  };
  return map[status] || '';
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

<style scoped>
.equipment-ledger-page {
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

/* Running status colors */
.status-running {
  background: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.status-fault {
  background: #fff2f0;
  color: #ff4d4f;
  border: 1px solid #ffccc7;
}

.status-shutdown {
  background: #f0f0f0;
  color: #999;
  border: 1px solid #d9d9d9;
}

.status-maintenance {
  background: #e6f7ff;
  color: #1890ff;
  border: 1px solid #91d5ff;
}

/* Manage status colors */
.status-in-use {
  background: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.status-idle {
  background: #fffbe6;
  color: #faad14;
  border: 1px solid #ffe58f;
}

.status-scrapped {
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

.dialog-lg {
  width: 680px;
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

.form-row {
  display: flex;
  gap: 16px;
  margin-bottom: 0;
}

.form-row .form-group {
  flex: 1;
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
