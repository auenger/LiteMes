<template>
  <div class="h-full flex flex-col space-y-3">
    <!-- Filter Card -->
    <div class="bg-card p-3 border border-border-color shadow-sm rounded-sm shrink-0">
      <el-form :inline="true" :model="query" size="default" class="flex flex-wrap gap-y-3 items-center !mb-0">
        <el-form-item label="单位编码" class="!mb-0">
          <el-input v-model="query.uomCode" placeholder="单位编码" clearable class="!w-40" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="单位名称" class="!mb-0">
          <el-input v-model="query.uomName" placeholder="单位名称" clearable class="!w-40" @keyup.enter="search" />
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
          <el-button type="primary" :icon="Plus" @click="openCreateDialog">新建单位</el-button>
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
        <el-table :data="uoms" border stripe height="100%" v-loading="loading">
          <el-table-column v-if="isColumnVisible('uomCode')" prop="uomCode" label="单位编码" min-width="120" />
          <el-table-column v-if="isColumnVisible('uomName')" prop="uomName" label="单位名称" min-width="120" />
          <el-table-column v-if="isColumnVisible('precision')" prop="precision" label="精度" min-width="80">
            <template #default="{ row }">{{ row.precision ?? '-' }}</template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('status')" prop="status" label="状态" min-width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('createdBy')" prop="createdBy" label="创建人" min-width="100">
            <template #default="{ row }">{{ row.createdBy || '-' }}</template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('createdAt')" prop="createdAt" label="创建时间" min-width="160">
            <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
          </el-table-column>
          <el-table-column label="操作" min-width="260" fixed="right">
            <template #default="{ row }">
              <div class="flex gap-1">
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
      :title="isEdit ? '编辑单位' : '新建单位'"
      width="480px"
      destroy-on-close
    >
      <el-form label-width="80px">
        <el-form-item label="单位编码" required>
          <el-input v-model="form.uomCode" placeholder="请输入单位编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="单位名称" required>
          <el-input v-model="form.uomName" placeholder="请输入单位名称" />
        </el-form-item>
        <el-form-item label="精度">
          <el-input-number v-model="form.precision" :step="0.0001" :precision="4" placeholder="请输入精度" controls-position="right" />
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
      <p>确定要删除单位 "{{ deleteTarget?.uomName }}" ({{ deleteTarget?.uomCode }}) 吗？</p>
      <p class="text-gray-400 text-sm mt-2">已被换算比例引用的单位不可删除。删除后不可恢复。</p>
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
  listUoms,
  createUom,
  updateUom,
  deleteUom,
  updateUomStatus,
  type UomDto,
  type UomQueryParams,
} from '../../api/uom';
import AuditLogDialog from '../../components/AuditLogDialog.vue';
import TableSettingsPanel from '../../components/TableSettingsPanel.vue';
import { useTableSettings, type ColumnDef } from '../../components/useTableSettings';

const uoms = ref<UomDto[]>([]);
const total = ref(0);
const loading = ref(false);
const submitting = ref(false);
const formError = ref('');

const query = reactive<UomQueryParams>({
  uomCode: '',
  uomName: '',
  status: undefined,
  page: 1,
  size: 10,
});

const totalPages = computed(() => Math.ceil(total.value / (query.size || 10)));

// Table settings
const defaultColumns: ColumnDef[] = [
  { key: 'uomCode', label: '单位编码' },
  { key: 'uomName', label: '单位名称' },
  { key: 'precision', label: '精度' },
  { key: 'status', label: '状态' },
  { key: 'createdBy', label: '创建人' },
  { key: 'createdAt', label: '创建时间' },
];

const { columns, toggleColumn, resetSettings } = useTableSettings('uom-list', defaultColumns);
const showTableSettings = ref(false);

function isColumnVisible(key: string): boolean {
  return columns.value.find(c => c.key === key)?.visible !== false;
}

const visibleColCount = computed(() => columns.value.filter(c => c.visible).length);

// Audit log dialog state
const auditLogVisible = ref(false);
const auditLogTableName = ref('uom');
const auditLogRecordId = ref(0);

function openAuditLog(uom: UomDto) {
  auditLogTableName.value = 'uom';
  auditLogRecordId.value = uom.id;
  auditLogVisible.value = true;
}

// Dialog state
const dialogVisible = ref(false);
const isEdit = ref(false);
const editingId = ref<number | null>(null);
const form = reactive({
  uomCode: '',
  uomName: '',
  precision: null as number | null,
});

// Delete dialog state
const deleteDialogVisible = ref(false);
const deleteTarget = ref<UomDto | null>(null);

async function fetchList() {
  loading.value = true;
  try {
    const res = await listUoms(query);
    if (res.code === 200 && res.data) {
      uoms.value = res.data.records;
      total.value = res.data.total;
    }
  } catch (e) {
    console.error('Failed to fetch uom list', e);
  } finally {
    loading.value = false;
  }
}

function search() {
  query.page = 1;
  fetchList();
}

function resetQuery() {
  query.uomCode = '';
  query.uomName = '';
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
  form.uomCode = '';
  form.uomName = '';
  form.precision = null;
  formError.value = '';
  dialogVisible.value = true;
}

function openEditDialog(uom: UomDto) {
  isEdit.value = true;
  editingId.value = uom.id;
  form.uomCode = uom.uomCode;
  form.uomName = uom.uomName;
  form.precision = uom.precision;
  formError.value = '';
  dialogVisible.value = true;
}

function closeDialog() {
  dialogVisible.value = false;
}

async function submitForm() {
  if (!form.uomCode && !isEdit.value) {
    formError.value = '单位编码不能为空';
    return;
  }
  if (!form.uomName) {
    formError.value = '单位名称不能为空';
    return;
  }

  submitting.value = true;
  formError.value = '';
  try {
    if (isEdit.value && editingId.value) {
      await updateUom(editingId.value, {
        uomName: form.uomName,
        precision: form.precision ?? undefined,
      });
    } else {
      await createUom({
        uomCode: form.uomCode,
        uomName: form.uomName,
        precision: form.precision ?? undefined,
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

function confirmDelete(uom: UomDto) {
  deleteTarget.value = uom;
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
    await deleteUom(deleteTarget.value.id);
    closeDeleteDialog();
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '删除失败';
    alert(msg);
  } finally {
    submitting.value = false;
  }
}

async function toggleStatus(uom: UomDto) {
  const newStatus = uom.status === 1 ? 0 : 1;
  try {
    await updateUomStatus(uom.id, newStatus);
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
  fetchList();
});
</script>
