<template>
  <div class="h-full flex flex-col space-y-3">
    <!-- Filter Card -->
    <div class="bg-card p-3 border border-border-color shadow-sm rounded-sm shrink-0">
      <el-form :inline="true" :model="query" size="default" class="flex flex-wrap gap-y-3 items-center !mb-0">
        <el-form-item label="权限组名称" class="!mb-0">
          <el-input v-model="query.groupName" placeholder="权限组名称" clearable class="!w-40" @keyup.enter="search" />
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
          <el-button type="primary" :icon="Plus" @click="openCreateDialog">新建权限组</el-button>
        </div>
      </div>
      <div class="flex-1 p-2.5 overflow-hidden">
        <el-table :data="groups" border stripe height="100%" v-loading="loading">
          <el-table-column prop="groupName" label="权限组名称" min-width="140" />
          <el-table-column label="备注" min-width="160">
            <template #default="{ row }">{{ row.remark || '-' }}</template>
          </el-table-column>
          <el-table-column prop="factoryCount" label="工厂数" min-width="80" />
          <el-table-column prop="workCenterCount" label="工作中心数" min-width="100" />
          <el-table-column prop="processCount" label="工序数" min-width="80" />
          <el-table-column label="创建人" min-width="100">
            <template #default="{ row }">{{ row.createdBy || '-' }}</template>
          </el-table-column>
          <el-table-column label="创建时间" min-width="160">
            <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
          </el-table-column>
          <el-table-column label="操作" min-width="180" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
              <el-button size="small" type="info" @click="openTabDialog(row)">关联管理</el-button>
              <el-button size="small" type="danger" :disabled="row.referenced" @click="confirmDelete(row)">删除</el-button>
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑权限组' : '新建权限组'" width="480px" @close="closeDialog">
      <el-form label-width="100px">
        <el-form-item label="权限组名称" required>
          <el-input v-model="form.groupName" placeholder="请输入权限组名称" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
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
      <p>确定要删除权限组 "{{ deleteTarget?.groupName }}" 吗？</p>
      <p class="text-gray-400 text-sm mt-2">删除后关联的工厂/工作中心/工序关联记录将一并删除，不可恢复。</p>
      <template #footer>
        <el-button @click="closeDeleteDialog">取消</el-button>
        <el-button type="danger" @click="doDelete" :loading="submitting">删除</el-button>
      </template>
    </el-dialog>

    <!-- Tab Dialog for Association Management -->
    <el-dialog v-model="tabDialogVisible" :title="`关联管理 - ${tabGroup?.groupName}`" width="700px" @close="closeTabDialog">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="工厂" name="factory">
          <div class="text-sm text-gray-500 mb-2">已选 {{ selectedFactoryIds.length }} 项</div>
          <el-table :data="factoryOptions" border size="small" max-height="400">
            <el-table-column label="选择" min-width="60">
              <template #header>
                <el-checkbox :model-value="allFactoryChecked" @change="toggleAllFactories" />
              </template>
              <template #default="{ row }">
                <el-checkbox :value="row.id" v-model="selectedFactoryIds" />
              </template>
            </el-table-column>
            <el-table-column prop="code" label="工厂编码" min-width="120" />
            <el-table-column prop="name" label="工厂名称" min-width="120" />
          </el-table>
          <el-empty v-if="factoryOptions.length === 0" description="暂无工厂数据" />
        </el-tab-pane>
        <el-tab-pane label="工作中心" name="workCenter">
          <div class="text-sm text-gray-500 mb-2">已选 {{ selectedWorkCenterIds.length }} 项</div>
          <el-table :data="workCenterOptions" border size="small" max-height="400">
            <el-table-column label="选择" min-width="60">
              <template #header>
                <el-checkbox :model-value="allWorkCenterChecked" @change="toggleAllWorkCenters" />
              </template>
              <template #default="{ row }">
                <el-checkbox :value="row.id" v-model="selectedWorkCenterIds" />
              </template>
            </el-table-column>
            <el-table-column prop="code" label="工作中心编码" min-width="120" />
            <el-table-column prop="name" label="工作中心名称" min-width="120" />
          </el-table>
          <el-empty v-if="workCenterOptions.length === 0" description="暂无工作中心数据" />
        </el-tab-pane>
        <el-tab-pane label="工序" name="process">
          <div class="text-sm text-gray-500 mb-2">已选 {{ selectedProcessIds.length }} 项</div>
          <el-table :data="processOptions" border size="small" max-height="400">
            <el-table-column label="选择" min-width="60">
              <template #header>
                <el-checkbox :model-value="allProcessChecked" @change="toggleAllProcesses" />
              </template>
              <template #default="{ row }">
                <el-checkbox :value="row.id" v-model="selectedProcessIds" />
              </template>
            </el-table-column>
            <el-table-column prop="code" label="工序编码" min-width="120" />
            <el-table-column prop="name" label="工序名称" min-width="120" />
          </el-table>
          <el-empty v-if="processOptions.length === 0" description="暂无工序数据" />
        </el-tab-pane>
      </el-tabs>
      <div v-if="tabError" class="text-red-500 text-sm mt-3">{{ tabError }}</div>
      <template #footer>
        <el-button @click="closeTabDialog">取消</el-button>
        <el-button type="primary" @click="saveAssociations" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { Search, Refresh, Plus } from '@element-plus/icons-vue';
import {
  listDataPermissionGroups,
  createDataPermissionGroup,
  updateDataPermissionGroup,
  deleteDataPermissionGroup,
  getGroupFactories,
  getGroupWorkCenters,
  getGroupProcesses,
  saveGroupFactories,
  saveGroupWorkCenters,
  saveGroupProcesses,
  type DataPermissionGroupDto,
  type DataPermissionGroupQueryParams,
} from '../../api/dataPermissionGroup';
import {
  getFactoryDropdown,
  getWorkCenterDropdown,
  getProcessDropdown,
  type DropdownItem,
} from '../../api/dropdown';

const groups = ref<DataPermissionGroupDto[]>([]);
const total = ref(0);
const loading = ref(false);
const submitting = ref(false);
const formError = ref('');

const query = reactive<DataPermissionGroupQueryParams>({
  groupName: '',
  page: 1,
  size: 10,
});

const totalPages = computed(() => Math.ceil(total.value / (query.size || 10)));

// Dialog state
const dialogVisible = ref(false);
const isEdit = ref(false);
const editingId = ref<number | null>(null);
const form = reactive({
  groupName: '',
  remark: '',
});

// Delete dialog state
const deleteDialogVisible = ref(false);
const deleteTarget = ref<DataPermissionGroupDto | null>(null);

// Tab dialog state
const tabDialogVisible = ref(false);
const tabGroup = ref<DataPermissionGroupDto | null>(null);
const tabError = ref('');
const activeTab = ref<'factory' | 'workCenter' | 'process'>('factory');

// Dropdown options
const factoryOptions = ref<DropdownItem[]>([]);
const workCenterOptions = ref<DropdownItem[]>([]);
const processOptions = ref<DropdownItem[]>([]);

// Selected IDs (persist across tab switches)
const selectedFactoryIds = ref<number[]>([]);
const selectedWorkCenterIds = ref<number[]>([]);
const selectedProcessIds = ref<number[]>([]);

// Computed: check all
const allFactoryChecked = computed(() =>
  factoryOptions.value.length > 0 && factoryOptions.value.every(f => selectedFactoryIds.value.includes(f.id))
);
const allWorkCenterChecked = computed(() =>
  workCenterOptions.value.length > 0 && workCenterOptions.value.every(wc => selectedWorkCenterIds.value.includes(wc.id))
);
const allProcessChecked = computed(() =>
  processOptions.value.length > 0 && processOptions.value.every(p => selectedProcessIds.value.includes(p.id))
);

function toggleAllFactories(checked: boolean | string | number) {
  if (checked) {
    selectedFactoryIds.value = factoryOptions.value.map(f => f.id);
  } else {
    selectedFactoryIds.value = [];
  }
}

function toggleAllWorkCenters(checked: boolean | string | number) {
  if (checked) {
    selectedWorkCenterIds.value = workCenterOptions.value.map(wc => wc.id);
  } else {
    selectedWorkCenterIds.value = [];
  }
}

function toggleAllProcesses(checked: boolean | string | number) {
  if (checked) {
    selectedProcessIds.value = processOptions.value.map(p => p.id);
  } else {
    selectedProcessIds.value = [];
  }
}

async function fetchList() {
  loading.value = true;
  try {
    const res = await listDataPermissionGroups(query);
    if (res.code === 200 && res.data) {
      groups.value = res.data.records;
      total.value = res.data.total;
    }
  } catch (e) {
    console.error('Failed to fetch permission group list', e);
  } finally {
    loading.value = false;
  }
}

function search() {
  query.page = 1;
  fetchList();
}

function resetQuery() {
  query.groupName = '';
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
  form.groupName = '';
  form.remark = '';
  formError.value = '';
  dialogVisible.value = true;
}

function openEditDialog(group: DataPermissionGroupDto) {
  isEdit.value = true;
  editingId.value = group.id;
  form.groupName = group.groupName;
  form.remark = group.remark || '';
  formError.value = '';
  dialogVisible.value = true;
}

function closeDialog() {
  dialogVisible.value = false;
}

async function submitForm() {
  if (!form.groupName) {
    formError.value = '权限组名称不能为空';
    return;
  }

  submitting.value = true;
  formError.value = '';
  try {
    if (isEdit.value && editingId.value) {
      await updateDataPermissionGroup(editingId.value, {
        groupName: form.groupName,
        remark: form.remark || undefined,
      });
    } else {
      await createDataPermissionGroup({
        groupName: form.groupName,
        remark: form.remark || undefined,
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

function confirmDelete(group: DataPermissionGroupDto) {
  deleteTarget.value = group;
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
    await deleteDataPermissionGroup(deleteTarget.value.id);
    closeDeleteDialog();
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '删除失败';
    alert(msg);
  } finally {
    submitting.value = false;
  }
}

async function openTabDialog(group: DataPermissionGroupDto) {
  tabGroup.value = group;
  activeTab.value = 'factory';
  tabError.value = '';
  tabDialogVisible.value = true;

  // Load all options and current associations in parallel
  const [factoryRes, wcRes, processRes, groupFactories, groupWcs, groupProcesses] = await Promise.all([
    getFactoryDropdown(),
    getWorkCenterDropdown(),
    getProcessDropdown(),
    getGroupFactories(group.id),
    getGroupWorkCenters(group.id),
    getGroupProcesses(group.id),
  ]);

  factoryOptions.value = (factoryRes.code === 200 && factoryRes.data) ? factoryRes.data : [];
  workCenterOptions.value = (wcRes.code === 200 && wcRes.data) ? wcRes.data : [];
  processOptions.value = (processRes.code === 200 && processRes.data) ? processRes.data : [];

  selectedFactoryIds.value = (groupFactories.code === 200 && groupFactories.data)
    ? groupFactories.data.map(f => f.id) : [];
  selectedWorkCenterIds.value = (groupWcs.code === 200 && groupWcs.data)
    ? groupWcs.data.map(wc => wc.id) : [];
  selectedProcessIds.value = (groupProcesses.code === 200 && groupProcesses.data)
    ? groupProcesses.data.map(p => p.id) : [];
}

function closeTabDialog() {
  tabDialogVisible.value = false;
}

function switchTab(tab: 'factory' | 'workCenter' | 'process') {
  activeTab.value = tab;
}

async function saveAssociations() {
  if (!tabGroup.value) return;
  submitting.value = true;
  tabError.value = '';
  try {
    await Promise.all([
      saveGroupFactories(tabGroup.value.id, selectedFactoryIds.value),
      saveGroupWorkCenters(tabGroup.value.id, selectedWorkCenterIds.value),
      saveGroupProcesses(tabGroup.value.id, selectedProcessIds.value),
    ]);
    closeTabDialog();
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '保存失败';
    tabError.value = msg;
  } finally {
    submitting.value = false;
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
