<template>
  <div class="h-full flex flex-col space-y-3">
    <!-- Filter Card -->
    <div class="bg-card p-3 border border-border-color shadow-sm rounded-sm shrink-0">
      <el-form :inline="true" :model="query" size="default" class="flex flex-wrap gap-y-3 items-center !mb-0">
        <el-form-item label="设备型号编码" class="!mb-0">
          <el-input v-model="query.modelCode" placeholder="设备型号编码" clearable class="!w-40" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="设备型号名称" class="!mb-0">
          <el-input v-model="query.modelName" placeholder="设备型号名称" clearable class="!w-40" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="设备类型" class="!mb-0">
          <el-select v-model="query.equipmentTypeId" placeholder="全部设备类型" clearable class="!w-44">
            <el-option v-for="t in equipmentTypes" :key="t.id" :label="`${t.typeName} (${t.typeCode})`" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" class="!mb-0">
          <el-select v-model="query.status" placeholder="全部状态" clearable class="!w-32">
            <el-option :label="'启用'" :value="1" />
            <el-option :label="'禁用'" :value="0" />
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
          <el-button type="primary" :icon="Plus" @click="openCreateDialog">新建设备型号</el-button>
          <el-button :icon="Setting" @click="showTableSettings = !showTableSettings">表格设置</el-button>
        </div>
        <TableSettingsPanel
          :visible="showTableSettings"
          :columns="columns"
          @close="showTableSettings = false"
          @toggle="toggleColumn"
          @reset="resetSettings"
        />
      </div>
      <div class="flex-1 p-2.5 overflow-hidden">
        <el-table :data="models" border stripe height="100%" v-loading="loading">
          <el-table-column v-if="isColumnVisible('modelCode')" prop="modelCode" label="设备型号编码" min-width="140" />
          <el-table-column v-if="isColumnVisible('modelName')" prop="modelName" label="设备型号名称" min-width="140" />
          <el-table-column v-if="isColumnVisible('typeCode')" prop="typeCode" label="设备类型编码" min-width="130" />
          <el-table-column v-if="isColumnVisible('typeName')" prop="typeName" label="设备类型名称" min-width="130" />
          <el-table-column v-if="isColumnVisible('status')" label="状态" min-width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('createdBy')" prop="createdBy" label="创建人" min-width="100">
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑设备型号' : '新建设备型号'" width="480px" @close="closeDialog">
      <el-form label-width="120px">
        <el-form-item label="设备型号编码" required>
          <el-input v-model="form.modelCode" :disabled="isEdit" placeholder="请输入设备型号编码" />
        </el-form-item>
        <el-form-item label="设备型号名称" required>
          <el-input v-model="form.modelName" placeholder="请输入设备型号名称" />
        </el-form-item>
        <el-form-item label="设备类型" required>
          <el-select v-model="form.equipmentTypeId" placeholder="请选择设备类型" class="!w-full" @change="onTypeChange">
            <el-option v-for="t in equipmentTypes" :key="t.id" :label="`${t.typeName} (${t.typeCode})`" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="selectedTypeName" label="设备类型名称">
          <el-input :model-value="selectedTypeName" disabled />
        </el-form-item>
      </el-form>
      <div v-if="formError" class="text-red-500 text-sm px-4 pb-2">{{ formError }}</div>
      <template #footer>
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- Delete Confirm Dialog -->
    <el-dialog v-model="deleteDialogVisible" title="确认删除" width="400px" @close="closeDeleteDialog">
      <p>确定要删除设备型号 "{{ deleteTarget?.modelName }}" ({{ deleteTarget?.modelCode }}) 吗？</p>
      <p class="text-gray-400 text-sm mt-2">已被设备台账引用的型号不可删除。删除后不可恢复。</p>
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
  listEquipmentModels,
  createEquipmentModel,
  updateEquipmentModel,
  deleteEquipmentModel,
  updateEquipmentModelStatus,
  type EquipmentModelDto,
  type EquipmentModelQueryParams,
} from '../../api/equipmentModel';
import {
  listEquipmentTypes,
  type EquipmentTypeDto,
} from '../../api/equipmentType';
import AuditLogDialog from '../../components/AuditLogDialog.vue';
import TableSettingsPanel from '../../components/TableSettingsPanel.vue';
import { useTableSettings, type ColumnDef } from '../../components/useTableSettings';

const models = ref<EquipmentModelDto[]>([]);
const total = ref(0);
const loading = ref(false);
const submitting = ref(false);
const formError = ref('');

// Equipment type dropdown data
const equipmentTypes = ref<EquipmentTypeDto[]>([]);

const query = reactive<EquipmentModelQueryParams>({
  modelCode: '',
  modelName: '',
  equipmentTypeId: undefined,
  status: undefined,
  page: 1,
  size: 10,
});

const totalPages = computed(() => Math.ceil(total.value / (query.size || 10)));

// Table settings
const defaultColumns: ColumnDef[] = [
  { key: 'modelCode', label: '设备型号编码' },
  { key: 'modelName', label: '设备型号名称' },
  { key: 'typeCode', label: '设备类型编码' },
  { key: 'typeName', label: '设备类型名称' },
  { key: 'status', label: '状态' },
  { key: 'createdBy', label: '创建人' },
  { key: 'createdAt', label: '创建时间' },
];

const { columns, toggleColumn, resetSettings } = useTableSettings('equipment-model-list', defaultColumns);
const showTableSettings = ref(false);

function isColumnVisible(key: string): boolean {
  return columns.value.find(c => c.key === key)?.visible !== false;
}

const visibleColCount = computed(() => columns.value.filter(c => c.visible).length);

// Audit log dialog state
const auditLogVisible = ref(false);
const auditLogTableName = ref('equipment_model');
const auditLogRecordId = ref(0);

function openAuditLog(item: EquipmentModelDto) {
  auditLogTableName.value = 'equipment_model';
  auditLogRecordId.value = item.id;
  auditLogVisible.value = true;
}

// Dialog state
const dialogVisible = ref(false);
const isEdit = ref(false);
const editingId = ref<number | null>(null);
const form = reactive({
  modelCode: '',
  modelName: '',
  equipmentTypeId: undefined as number | undefined,
});

// Selected type name display
const selectedTypeName = computed(() => {
  if (!form.equipmentTypeId) return '';
  const t = equipmentTypes.value.find(et => et.id === form.equipmentTypeId);
  return t ? t.typeName : '';
});

function onTypeChange() {
  // Just updates selectedTypeName via computed
}

// Delete dialog state
const deleteDialogVisible = ref(false);
const deleteTarget = ref<EquipmentModelDto | null>(null);

async function fetchEquipmentTypes() {
  try {
    const res = await listEquipmentTypes({ status: 1, size: 1000 });
    if (res.code === 200 && res.data) {
      equipmentTypes.value = res.data.records;
    }
  } catch (e) {
    console.error('Failed to fetch equipment types', e);
  }
}

async function fetchList() {
  loading.value = true;
  try {
    const res = await listEquipmentModels(query);
    if (res.code === 200 && res.data) {
      models.value = res.data.records;
      total.value = res.data.total;
    }
  } catch (e) {
    console.error('Failed to fetch equipment model list', e);
  } finally {
    loading.value = false;
  }
}

function search() {
  query.page = 1;
  fetchList();
}

function resetQuery() {
  query.modelCode = '';
  query.modelName = '';
  query.equipmentTypeId = undefined;
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
  form.modelCode = '';
  form.modelName = '';
  form.equipmentTypeId = undefined;
  formError.value = '';
  dialogVisible.value = true;
}

function openEditDialog(item: EquipmentModelDto) {
  isEdit.value = true;
  editingId.value = item.id;
  form.modelCode = item.modelCode;
  form.modelName = item.modelName;
  form.equipmentTypeId = item.equipmentTypeId;
  formError.value = '';
  dialogVisible.value = true;
}

function closeDialog() {
  dialogVisible.value = false;
}

async function submitForm() {
  if (!form.modelCode && !isEdit.value) {
    formError.value = '设备型号编码不能为空';
    return;
  }
  if (!form.modelName) {
    formError.value = '设备型号名称不能为空';
    return;
  }
  if (!form.equipmentTypeId) {
    formError.value = '设备类型不能为空';
    return;
  }

  submitting.value = true;
  formError.value = '';
  try {
    if (isEdit.value && editingId.value) {
      await updateEquipmentModel(editingId.value, {
        modelName: form.modelName,
        equipmentTypeId: form.equipmentTypeId,
      });
    } else {
      await createEquipmentModel({
        modelCode: form.modelCode,
        modelName: form.modelName,
        equipmentTypeId: form.equipmentTypeId,
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

function confirmDelete(item: EquipmentModelDto) {
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
    await deleteEquipmentModel(deleteTarget.value.id);
    closeDeleteDialog();
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '删除失败';
    alert(msg);
  } finally {
    submitting.value = false;
  }
}

async function toggleStatus(item: EquipmentModelDto) {
  const newStatus = item.status === 1 ? 0 : 1;
  try {
    await updateEquipmentModelStatus(item.id, newStatus);
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
  fetchEquipmentTypes();
  fetchList();
});
</script>
