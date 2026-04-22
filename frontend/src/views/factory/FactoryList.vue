<template>
  <div class="h-full flex flex-col space-y-3">
    <!-- Filter Card -->
    <div class="bg-card p-3 border border-border-color shadow-sm rounded-sm shrink-0">
      <el-form :inline="true" :model="query" size="default" class="flex flex-wrap gap-y-3 items-center !mb-0">
        <el-form-item label="工厂编码" class="!mb-0">
          <el-input v-model="query.factoryCode" placeholder="工厂编码" clearable class="!w-40" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="工厂名称" class="!mb-0">
          <el-input v-model="query.name" placeholder="工厂名称" clearable class="!w-40" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="所属公司" class="!mb-0">
          <el-select v-model="query.companyId" placeholder="全部公司" clearable class="!w-40">
            <el-option v-for="c in companyOptions" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" class="!mb-0">
          <el-select v-model="query.status" placeholder="全部状态" clearable class="!w-28">
            <el-option :label="1" value="启用" />
            <el-option :label="0" value="禁用" />
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
          <el-button type="primary" :icon="Plus" @click="openCreateDialog">新建工厂</el-button>
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
        <el-table :data="factories" border stripe height="100%" v-loading="loading">
          <el-table-column v-if="isColumnVisible('factoryCode')" prop="factoryCode" label="工厂编码" width="150" />
          <el-table-column v-if="isColumnVisible('name')" prop="name" label="工厂名称" width="180" />
          <el-table-column v-if="isColumnVisible('shortName')" label="简称" width="120">
            <template #default="{ row }">{{ row.shortName || '-' }}</template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('companyName')" label="所属公司" width="180">
            <template #default="{ row }">{{ row.companyName || '-' }}</template>
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
    <el-dialog :title="isEdit ? '编辑工厂' : '新建工厂'" width="480px" v-model="dialogVisible">
      <el-form label-width="80px">
        <el-form-item label="工厂编码" required>
          <el-input v-model="form.factoryCode" :disabled="isEdit" placeholder="请输入工厂编码" />
        </el-form-item>
        <el-form-item label="工厂名称" required>
          <el-input v-model="form.name" placeholder="请输入工厂名称" />
        </el-form-item>
        <el-form-item label="简称">
          <el-input v-model="form.shortName" placeholder="请输入简称" />
        </el-form-item>
        <el-form-item label="所属公司" required>
          <el-select v-model="form.companyId" :disabled="isEdit" placeholder="请选择所属公司" class="!w-full">
            <el-option v-for="c in companyOptions" :key="c.id" :label="c.name" :value="c.id" />
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
      <p>确定要删除工厂 "{{ deleteTarget?.name }}" ({{ deleteTarget?.factoryCode }}) 吗？</p>
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
  listFactories,
  createFactory,
  updateFactory,
  deleteFactory,
  updateFactoryStatus,
  type FactoryDto,
  type FactoryQueryParams,
} from '../../api/factory';
import { getCompanyDropdown, type DropdownItem } from '../../api/dropdown';
import AuditLogDialog from '../../components/AuditLogDialog.vue';
import TableSettingsPanel from '../../components/TableSettingsPanel.vue';
import { useTableSettings, type ColumnDef } from '../../components/useTableSettings';

const factories = ref<FactoryDto[]>([]);
const total = ref(0);
const loading = ref(false);
const submitting = ref(false);
const formError = ref('');
const companyOptions = ref<DropdownItem[]>([]);

const query = reactive<FactoryQueryParams>({
  factoryCode: '',
  name: '',
  companyId: undefined,
  status: undefined,
  page: 1,
  size: 10,
});

const totalPages = computed(() => Math.ceil(total.value / (query.size || 10)));

// Table settings
const defaultColumns: ColumnDef[] = [
  { key: 'factoryCode', label: '工厂编码' },
  { key: 'name', label: '工厂名称' },
  { key: 'shortName', label: '简称' },
  { key: 'companyName', label: '所属公司' },
  { key: 'status', label: '状态' },
  { key: 'createdBy', label: '创建人' },
  { key: 'createdAt', label: '创建时间' },
];

const { columns, toggleColumn, resetSettings } = useTableSettings('factory-list', defaultColumns);
const showTableSettings = ref(false);

function isColumnVisible(key: string): boolean {
  return columns.value.find(c => c.key === key)?.visible !== false;
}

const visibleColCount = computed(() => columns.value.filter(c => c.visible).length);

// Audit log dialog state
const auditLogVisible = ref(false);
const auditLogTableName = ref('factory');
const auditLogRecordId = ref(0);

function openAuditLog(factory: FactoryDto) {
  auditLogTableName.value = 'factory';
  auditLogRecordId.value = factory.id;
  auditLogVisible.value = true;
}

// Dialog state
const dialogVisible = ref(false);
const isEdit = ref(false);
const editingId = ref<number | null>(null);
const form = reactive({
  factoryCode: '',
  name: '',
  shortName: '',
  companyId: undefined as number | undefined,
});

// Delete dialog state
const deleteDialogVisible = ref(false);
const deleteTarget = ref<FactoryDto | null>(null);

async function fetchCompanies() {
  try {
    const res = await getCompanyDropdown();
    if (res.code === 200 && res.data) {
      companyOptions.value = res.data;
    }
  } catch (e) {
    console.error('Failed to fetch company options', e);
  }
}

async function fetchList() {
  loading.value = true;
  try {
    const res = await listFactories(query);
    if (res.code === 200 && res.data) {
      factories.value = res.data.records;
      total.value = res.data.total;
    }
  } catch (e) {
    console.error('Failed to fetch factory list', e);
  } finally {
    loading.value = false;
  }
}

function search() {
  query.page = 1;
  fetchList();
}

function resetQuery() {
  query.factoryCode = '';
  query.name = '';
  query.companyId = undefined;
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
  form.factoryCode = '';
  form.name = '';
  form.shortName = '';
  form.companyId = undefined;
  formError.value = '';
  dialogVisible.value = true;
}

function openEditDialog(factory: FactoryDto) {
  isEdit.value = true;
  editingId.value = factory.id;
  form.factoryCode = factory.factoryCode;
  form.name = factory.name;
  form.shortName = factory.shortName || '';
  form.companyId = factory.companyId;
  formError.value = '';
  dialogVisible.value = true;
}

function closeDialog() {
  dialogVisible.value = false;
}

async function submitForm() {
  if (!form.factoryCode && !isEdit.value) {
    formError.value = '工厂编码不能为空';
    return;
  }
  if (!form.name) {
    formError.value = '工厂名称不能为空';
    return;
  }
  if (!form.companyId) {
    formError.value = '请选择所属公司';
    return;
  }

  submitting.value = true;
  formError.value = '';
  try {
    if (isEdit.value && editingId.value) {
      await updateFactory(editingId.value, {
        name: form.name,
        shortName: form.shortName || undefined,
      });
    } else {
      await createFactory({
        factoryCode: form.factoryCode,
        name: form.name,
        shortName: form.shortName || undefined,
        companyId: form.companyId,
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

function confirmDelete(factory: FactoryDto) {
  deleteTarget.value = factory;
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
    await deleteFactory(deleteTarget.value.id);
    closeDeleteDialog();
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '删除失败';
    alert(msg);
  } finally {
    submitting.value = false;
  }
}

async function toggleStatus(factory: FactoryDto) {
  const newStatus = factory.status === 1 ? 0 : 1;
  try {
    await updateFactoryStatus(factory.id, newStatus);
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
  fetchCompanies();
  fetchList();
});
</script>
