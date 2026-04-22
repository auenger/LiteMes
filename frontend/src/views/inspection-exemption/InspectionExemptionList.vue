<template>
  <div class="h-full flex flex-col space-y-3">
    <!-- Filter Card -->
    <div class="bg-card p-3 border border-border-color shadow-sm rounded-sm shrink-0">
      <el-form :inline="true" :model="query" size="default" class="flex flex-wrap gap-y-3 items-center !mb-0">
        <el-form-item label="物料" class="!mb-0">
          <el-select v-model="query.materialId" placeholder="全部物料" clearable filterable class="!w-48">
            <el-option v-for="m in materialOptions" :key="m.id" :value="m.id" :label="`${m.code} - ${m.name}`" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" class="!mb-0">
          <el-select v-model="query.status" placeholder="全部状态" clearable class="!w-32">
            <el-option :value="1" label="启用" />
            <el-option :value="0" label="禁用" />
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
          <el-button type="primary" :icon="Plus" @click="openCreateDialog">新建免检规则</el-button>
          <div class="relative">
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
      </div>
      <div class="flex-1 p-2.5 overflow-hidden">
        <el-table :data="exemptions" border stripe height="100%" v-loading="loading" :row-class-name="rowClassName">
          <el-table-column v-if="isColumnVisible('materialCode')" prop="materialCode" label="物料编码" min-width="120" />
          <el-table-column v-if="isColumnVisible('materialName')" prop="materialName" label="物料名称" min-width="120" />
          <el-table-column v-if="isColumnVisible('supplierCode')" prop="supplierCode" label="供应商编码" min-width="110">
            <template #default="{ row }">{{ row.supplierCode || '-' }}</template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('supplierName')" prop="supplierName" label="供应商名称" min-width="110">
            <template #default="{ row }">{{ row.supplierName || '-' }}</template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('validPeriod')" label="有效期" min-width="180">
            <template #default="{ row }">
              <template v-if="row.validFrom || row.validTo">
                {{ row.validFrom || '...' }} ~ {{ row.validTo || '...' }}
              </template>
              <template v-else>
                <span class="text-gray-400">永久</span>
              </template>
            </template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('status')" prop="status" label="状态" min-width="80">
            <template #default="{ row }">
              <el-tag :type="getStatusTagType(row)" size="small">
                {{ getStatusLabel(row) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('createdBy')" prop="createdBy" label="创建人" min-width="100">
            <template #default="{ row }">{{ row.createdBy || '-' }}</template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('createdAt')" prop="createdAt" label="创建时间" min-width="160">
            <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
          </el-table-column>
          <el-table-column label="操作" min-width="280" fixed="right">
            <template #default="{ row }">
              <div class="flex gap-1">
                <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
                <el-button
                  v-if="row.status === 1 && !row.expired"
                  size="small"
                  type="warning"
                  @click="toggleStatus(row)"
                >禁用</el-button>
                <el-button
                  v-else-if="row.status === 0"
                  size="small"
                  type="success"
                  @click="toggleStatus(row)"
                >启用</el-button>
                <el-button size="small" type="danger" @click="confirmDelete(row)">删除</el-button>
                <el-button size="small" type="info" @click="openAuditLog(row)">变更履历</el-button>
              </div>
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
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑免检规则' : '新建免检规则'"
      width="520px"
      destroy-on-close
    >
      <el-form label-width="100px">
        <el-form-item label="物料" required>
          <el-select v-model="form.materialId" placeholder="请选择物料" :disabled="isEdit" filterable class="!w-full">
            <el-option v-for="m in materialOptions" :key="m.id" :value="m.id" :label="`${m.code} - ${m.name}`" />
          </el-select>
        </el-form-item>
        <el-form-item label="供应商">
          <el-input v-model="form.supplierDisplay" disabled placeholder="供应商模块尚未开发，暂不可选" />
          <span class="text-gray-400 text-xs mt-1 block">供应商功能开发后可选择，留空表示全局免检</span>
        </el-form-item>
        <el-form-item label="有效开始日期">
          <el-date-picker v-model="form.validFrom" type="date" value-format="YYYY-MM-DD" placeholder="选择开始日期" class="!w-full" />
        </el-form-item>
        <el-form-item label="有效结束日期">
          <el-date-picker v-model="form.validTo" type="date" value-format="YYYY-MM-DD" placeholder="选择结束日期" class="!w-full" />
          <span class="text-gray-400 text-xs mt-1 block">留空表示永久有效</span>
        </el-form-item>
        <el-form-item v-if="!isEdit" label="规则说明">
          <p class="m-0 p-2 bg-blue-50 rounded text-blue-500 text-sm">{{ getRuleDescription() }}</p>
        </el-form-item>
      </el-form>
      <div v-if="formError" class="text-red-500 text-xs px-4 pb-2">{{ formError }}</div>
      <template #footer>
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- Delete Confirm Dialog -->
    <el-dialog
      v-model="deleteDialogVisible"
      title="确认删除"
      width="420px"
      destroy-on-close
    >
      <p>确定要删除免检规则 "{{ deleteTarget?.materialName }}" ({{ deleteTarget?.materialCode }}) 吗？</p>
      <p class="text-gray-400 text-sm mt-2">删除后不可恢复。</p>
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
  listInspectionExemptions,
  createInspectionExemption,
  updateInspectionExemption,
  deleteInspectionExemption,
  updateInspectionExemptionStatus,
  type InspectionExemptionDto,
  type InspectionExemptionQueryParams,
} from '../../api/inspectionExemption';
import { getMaterialDropdown, type DropdownItem } from '../../api/dropdown';
import AuditLogDialog from '../../components/AuditLogDialog.vue';
import TableSettingsPanel from '../../components/TableSettingsPanel.vue';
import { useTableSettings, type ColumnDef } from '../../components/useTableSettings';

const exemptions = ref<InspectionExemptionDto[]>([]);
const materialOptions = ref<DropdownItem[]>([]);
const total = ref(0);
const loading = ref(false);
const submitting = ref(false);
const formError = ref('');

const query = reactive<InspectionExemptionQueryParams>({
  materialId: undefined,
  supplierId: undefined,
  status: undefined,
  page: 1,
  size: 10,
});

const totalPages = computed(() => Math.ceil(total.value / (query.size || 10)));

// Table settings
const defaultColumns: ColumnDef[] = [
  { key: 'materialCode', label: '物料编码' },
  { key: 'materialName', label: '物料名称' },
  { key: 'supplierCode', label: '供应商编码' },
  { key: 'supplierName', label: '供应商名称' },
  { key: 'validPeriod', label: '有效期' },
  { key: 'status', label: '状态' },
  { key: 'createdBy', label: '创建人' },
  { key: 'createdAt', label: '创建时间' },
];

const { columns, toggleColumn, resetSettings } = useTableSettings('inspection-exemption-list', defaultColumns);
const showTableSettings = ref(false);

function isColumnVisible(key: string): boolean {
  return columns.value.find(c => c.key === key)?.visible !== false;
}

const visibleColCount = computed(() => columns.value.filter(c => c.visible).length);

// Audit log dialog state
const auditLogVisible = ref(false);
const auditLogTableName = ref('inspection_exemption');
const auditLogRecordId = ref(0);

function openAuditLog(item: InspectionExemptionDto) {
  auditLogTableName.value = 'inspection_exemption';
  auditLogRecordId.value = item.id;
  auditLogVisible.value = true;
}

// Dialog state
const dialogVisible = ref(false);
const isEdit = ref(false);
const editingId = ref<number | null>(null);
const form = reactive({
  materialId: null as number | null,
  supplierDisplay: '',
  validFrom: '' as string,
  validTo: '' as string,
});

// Delete dialog state
const deleteDialogVisible = ref(false);
const deleteTarget = ref<InspectionExemptionDto | null>(null);

async function fetchMaterials() {
  try {
    const res = await getMaterialDropdown();
    if (res.code === 200 && res.data) {
      materialOptions.value = res.data;
    }
  } catch (e) {
    console.error('Failed to fetch material dropdown', e);
  }
}

async function fetchList() {
  loading.value = true;
  try {
    const res = await listInspectionExemptions(query);
    if (res.code === 200 && res.data) {
      exemptions.value = res.data.records;
      total.value = res.data.total;
    }
  } catch (e) {
    console.error('Failed to fetch inspection exemption list', e);
  } finally {
    loading.value = false;
  }
}

function search() {
  query.page = 1;
  fetchList();
}

function resetQuery() {
  query.materialId = undefined;
  query.supplierId = undefined;
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
  form.materialId = null;
  form.supplierDisplay = '';
  form.validFrom = '';
  form.validTo = '';
  formError.value = '';
  dialogVisible.value = true;
}

function openEditDialog(item: InspectionExemptionDto) {
  isEdit.value = true;
  editingId.value = item.id;
  form.materialId = item.materialId;
  form.supplierDisplay = item.supplierName || '';
  form.validFrom = item.validFrom || '';
  form.validTo = item.validTo || '';
  formError.value = '';
  dialogVisible.value = true;
}

function closeDialog() {
  dialogVisible.value = false;
}

function getRuleDescription(): string {
  const hasSupplier = false; // supplier not available yet
  const hasValidity = !!(form.validFrom || form.validTo);

  if (!hasSupplier && !hasValidity) {
    return '全局永久免检：所有供应商的该物料均免检';
  } else if (hasSupplier && !hasValidity) {
    return '指定供应商永久免检';
  } else if (hasSupplier && hasValidity) {
    return '指定供应商+有效期免检';
  } else {
    return '全局有效期免检：所有供应商的该物料在有效期内免检';
  }
}

async function submitForm() {
  if (!form.materialId) {
    formError.value = '请选择物料';
    return;
  }

  // Validate date range
  if (form.validFrom && form.validTo && form.validFrom > form.validTo) {
    formError.value = '有效开始日期不能晚于有效结束日期';
    return;
  }

  submitting.value = true;
  formError.value = '';
  try {
    const payload: any = {
      materialId: form.materialId,
      validFrom: form.validFrom || null,
      validTo: form.validTo || null,
    };

    if (isEdit.value && editingId.value) {
      await updateInspectionExemption(editingId.value, payload);
    } else {
      await createInspectionExemption(payload);
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

function confirmDelete(item: InspectionExemptionDto) {
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
    await deleteInspectionExemption(deleteTarget.value.id);
    closeDeleteDialog();
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '删除失败';
    alert(msg);
  } finally {
    submitting.value = false;
  }
}

async function toggleStatus(item: InspectionExemptionDto) {
  const newStatus = item.status === 1 ? 0 : 1;
  try {
    await updateInspectionExemptionStatus(item.id, newStatus);
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '状态更新失败';
    alert(msg);
  }
}

function getStatusTagType(item: InspectionExemptionDto): string {
  if (item.expired) return 'info';
  return item.status === 1 ? 'success' : 'danger';
}

function getStatusLabel(item: InspectionExemptionDto): string {
  if (item.expired) return '已过期';
  return item.status === 1 ? '启用' : '禁用';
}

function rowClassName({ row }: { row: InspectionExemptionDto }): string {
  return row.expired ? 'opacity-60' : '';
}

function formatDate(dateStr: string | undefined) {
  if (!dateStr) return '-';
  return new Date(dateStr).toLocaleString('zh-CN');
}

onMounted(() => {
  fetchMaterials();
  fetchList();
});
</script>
