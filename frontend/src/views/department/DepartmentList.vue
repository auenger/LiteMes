<template>
  <div class="h-full flex flex-col space-y-3">
    <!-- Filter Card -->
    <div class="bg-card p-3 border border-border-color shadow-sm rounded-sm shrink-0">
      <el-form :inline="true" :model="query" size="default" class="flex flex-wrap gap-y-3 items-center !mb-0">
        <el-form-item label="部门编码" class="!mb-0">
          <el-input v-model="query.departmentCode" placeholder="部门编码" clearable class="!w-40" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="部门名称" class="!mb-0">
          <el-input v-model="query.name" placeholder="部门名称" clearable class="!w-40" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="所属工厂" class="!mb-0">
          <el-select v-model="query.factoryId" placeholder="全部工厂" clearable class="!w-40">
            <el-option v-for="f in factoryOptions" :key="f.id" :label="f.name" :value="f.id" />
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
          <el-button type="primary" :icon="Plus" @click="openCreateDialog">新建部门</el-button>
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
        <el-table :data="departments" border stripe height="100%" v-loading="loading">
          <el-table-column prop="departmentCode" label="部门编码" width="150" />
          <el-table-column prop="name" label="部门名称" width="180" />
          <el-table-column label="所属工厂" width="180">
            <template #default="{ row }">{{ row.factoryName || '-' }}</template>
          </el-table-column>
          <el-table-column label="上级部门" width="150">
            <template #default="{ row }">{{ row.parentName || '顶级部门' }}</template>
          </el-table-column>
          <el-table-column prop="sortOrder" label="排序号" width="100" align="center" />
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="创建人" width="120">
            <template #default="{ row }">{{ row.createdBy || '-' }}</template>
          </el-table-column>
          <el-table-column label="创建时间" width="180">
            <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="280" fixed="right" align="center">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="manageUsers(row)">用户</el-button>
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
    <el-dialog :title="isEdit ? '编辑部门' : '新建部门'" width="480px" v-model="dialogVisible">
      <el-form label-width="80px">
        <el-form-item label="部门编码" required>
          <el-input v-model="form.departmentCode" :disabled="isEdit" placeholder="请输入部门编码" />
        </el-form-item>
        <el-form-item label="部门名称" required>
          <el-input v-model="form.name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="所属工厂" required>
          <el-select v-model="form.factoryId" placeholder="请选择所属工厂" class="!w-full">
            <el-option v-for="f in factoryOptions" :key="f.id" :label="f.name" :value="f.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="上级部门">
          <el-select v-model="form.parentId" placeholder="无（顶级部门）" clearable class="!w-full">
            <el-option v-for="d in parentDepartmentOptions" :key="d.id" :label="`${d.name} (${d.code})`" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序号">
          <el-input-number v-model="form.sortOrder" :min="0" placeholder="0" />
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
      <p>确定要删除部门 "{{ deleteTarget?.name }}" ({{ deleteTarget?.departmentCode }}) 吗？</p>
      <p class="text-gray-400 text-sm">删除后不可恢复。若该部门下存在子部门或已被引用，则无法删除。</p>
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
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { Search, Refresh, Plus, Setting } from '@element-plus/icons-vue';
import {
  listDepartments,
  createDepartment,
  updateDepartment,
  deleteDepartment,
  updateDepartmentStatus,
  type DepartmentDto,
  type DepartmentQueryParams,
} from '../../api/department';
import { getFactoryDropdown, getDepartmentDropdown, type DropdownItem } from '../../api/dropdown';
import AuditLogDialog from '../../components/AuditLogDialog.vue';
import TableSettingsPanel from '../../components/TableSettingsPanel.vue';
import { useTableSettings, type ColumnDef } from '../../components/useTableSettings';

const router = useRouter();

const departments = ref<DepartmentDto[]>([]);
const total = ref(0);
const loading = ref(false);
const submitting = ref(false);
const formError = ref('');
const factoryOptions = ref<DropdownItem[]>([]);

const query = reactive<DepartmentQueryParams>({
  departmentCode: '',
  name: '',
  factoryId: undefined,
  status: undefined,
  page: 1,
  size: 10,
});

const totalPages = computed(() => Math.ceil(total.value / (query.size || 10)));

// Table settings
const defaultColumns: ColumnDef[] = [
  { key: 'departmentCode', label: '部门编码' },
  { key: 'name', label: '部门名称' },
  { key: 'factoryName', label: '所属工厂' },
  { key: 'parentName', label: '上级部门' },
  { key: 'sortOrder', label: '排序号' },
  { key: 'status', label: '状态' },
  { key: 'createdBy', label: '创建人' },
  { key: 'createdAt', label: '创建时间' },
];

const { columns, toggleColumn, resetSettings } = useTableSettings('department-list', defaultColumns);
const showTableSettings = ref(false);

// Audit log dialog state
const auditLogVisible = ref(false);
const auditLogTableName = ref('department');
const auditLogRecordId = ref(0);

function openAuditLog(dept: DepartmentDto) {
  auditLogTableName.value = 'department';
  auditLogRecordId.value = dept.id;
  auditLogVisible.value = true;
}

// Dialog state
const dialogVisible = ref(false);
const isEdit = ref(false);
const editingId = ref<number | null>(null);
const form = reactive({
  departmentCode: '',
  name: '',
  factoryId: undefined as number | undefined,
  parentId: null as number | null,
  sortOrder: 0,
});

// Parent department options for selected factory
const parentDepartmentOptions = ref<DropdownItem[]>([]);

// Delete dialog state
const deleteDialogVisible = ref(false);
const deleteTarget = ref<DepartmentDto | null>(null);

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

async function fetchParentDepartments(factoryId: number | undefined) {
  if (!factoryId) {
    parentDepartmentOptions.value = [];
    return;
  }
  try {
    const res = await getDepartmentDropdown(factoryId);
    if (res.code === 200 && res.data) {
      parentDepartmentOptions.value = res.data.filter(
        (d) => !isEdit.value || d.id !== editingId.value
      );
    }
  } catch (e) {
    console.error('Failed to fetch parent department options', e);
  }
}

async function fetchList() {
  loading.value = true;
  try {
    const res = await listDepartments(query);
    if (res.code === 200 && res.data) {
      departments.value = res.data.records;
      total.value = res.data.total;
    }
  } catch (e) {
    console.error('Failed to fetch department list', e);
  } finally {
    loading.value = false;
  }
}

function search() {
  query.page = 1;
  fetchList();
}

function resetQuery() {
  query.departmentCode = '';
  query.name = '';
  query.factoryId = undefined;
  query.status = undefined;
  query.page = 1;
  fetchList();
}

function goPage(page: number) {
  query.page = page;
  fetchList();
}

async function openCreateDialog() {
  isEdit.value = false;
  editingId.value = null;
  form.departmentCode = '';
  form.name = '';
  form.factoryId = undefined;
  form.parentId = null;
  form.sortOrder = 0;
  parentDepartmentOptions.value = [];
  formError.value = '';
  dialogVisible.value = true;
}

async function openEditDialog(dept: DepartmentDto) {
  isEdit.value = true;
  editingId.value = dept.id;
  form.departmentCode = dept.departmentCode;
  form.name = dept.name;
  form.factoryId = dept.factoryId;
  form.parentId = dept.parentId;
  form.sortOrder = dept.sortOrder;
  formError.value = '';
  await fetchParentDepartments(dept.factoryId);
  dialogVisible.value = true;
}

function closeDialog() {
  dialogVisible.value = false;
}

// Load parent departments when factory changes in create dialog
watch(() => form.factoryId, (newFactoryId) => {
  if (!isEdit.value) {
    form.parentId = null;
    fetchParentDepartments(newFactoryId);
  }
});

async function submitForm() {
  if (!form.departmentCode && !isEdit.value) {
    formError.value = '部门编码不能为空';
    return;
  }
  if (!form.name) {
    formError.value = '部门名称不能为空';
    return;
  }
  if (!form.factoryId) {
    formError.value = '请选择所属工厂';
    return;
  }

  submitting.value = true;
  formError.value = '';
  try {
    if (isEdit.value && editingId.value) {
      await updateDepartment(editingId.value, {
        name: form.name,
        factoryId: form.factoryId,
        parentId: form.parentId,
        sortOrder: form.sortOrder,
      });
    } else {
      await createDepartment({
        departmentCode: form.departmentCode,
        name: form.name,
        factoryId: form.factoryId,
        parentId: form.parentId || undefined,
        sortOrder: form.sortOrder || 0,
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

function confirmDelete(dept: DepartmentDto) {
  deleteTarget.value = dept;
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
    await deleteDepartment(deleteTarget.value.id);
    closeDeleteDialog();
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '删除失败';
    alert(msg);
  } finally {
    submitting.value = false;
  }
}

async function toggleStatus(dept: DepartmentDto) {
  const newStatus = dept.status === 1 ? 0 : 1;
  try {
    await updateDepartmentStatus(dept.id, newStatus);
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '状态更新失败';
    alert(msg);
  }
}

function manageUsers(dept: DepartmentDto) {
  router.push(`/departments/${dept.id}/users`);
}

function formatDate(dateStr: string | undefined) {
  if (!dateStr) return '-';
  return new Date(dateStr).toLocaleString('zh-CN');
}

onMounted(() => {
  fetchFactories();
  fetchList();
});
</script>
