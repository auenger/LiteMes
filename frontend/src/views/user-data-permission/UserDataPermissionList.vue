<template>
  <div class="h-full flex flex-col space-y-3">
    <!-- Filter Card -->
    <div class="bg-card p-3 border border-border-color shadow-sm rounded-sm shrink-0">
      <el-form :inline="true" :model="query" size="default" class="flex flex-wrap gap-y-3 items-center !mb-0">
        <el-form-item label="用户名" class="!mb-0">
          <el-input v-model="query.username" placeholder="用户名" clearable class="!w-40" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="姓名" class="!mb-0">
          <el-input v-model="query.realName" placeholder="姓名" clearable class="!w-40" @keyup.enter="search" />
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
          <el-button type="primary" :icon="Plus" @click="openBatchDialog">批量授权</el-button>
        </div>
      </div>
      <div class="flex-1 p-2.5 overflow-hidden">
        <el-table :data="permissions" border stripe height="100%" v-loading="loading">
          <el-table-column prop="username" label="用户名" min-width="120" />
          <el-table-column prop="realName" label="姓名" min-width="100" />
          <el-table-column label="权限组" min-width="120">
            <template #default="{ row }">
              <el-tag v-if="row.groupName" type="primary" size="small">{{ row.groupName }}</el-tag>
              <span v-else class="text-gray-400">-</span>
            </template>
          </el-table-column>
          <el-table-column label="工厂数" min-width="80">
            <template #default="{ row }">{{ row.factoryCount || 0 }}</template>
          </el-table-column>
          <el-table-column label="工作中心数" min-width="100">
            <template #default="{ row }">{{ row.workCenterCount || 0 }}</template>
          </el-table-column>
          <el-table-column label="工序数" min-width="80">
            <template #default="{ row }">{{ row.processCount || 0 }}</template>
          </el-table-column>
          <el-table-column label="创建人" min-width="100">
            <template #default="{ row }">{{ row.createdBy || '-' }}</template>
          </el-table-column>
          <el-table-column label="创建时间" min-width="160">
            <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
          </el-table-column>
          <el-table-column label="操作" min-width="160" fixed="right">
            <template #default="{ row }">
              <el-button size="small" type="info" @click="openTabDialog(row)">权限管理</el-button>
              <el-button v-if="row.id" size="small" type="danger" @click="confirmDelete(row)">删除</el-button>
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

    <!-- Batch Assign Dialog -->
    <el-dialog v-model="batchDialogVisible" title="批量授权" width="700px" @close="closeBatchDialog">
      <el-form label-width="100px">
        <el-form-item label="选择用户" required>
          <div class="border border-gray-200 rounded p-2 max-h-60 overflow-y-auto">
            <el-input v-model="userSearchKeyword" placeholder="搜索用户名/姓名" size="small" class="mb-2" @input="filterUsers" />
            <div>
              <label v-for="user in filteredUsers" :key="user.userId" class="flex items-center gap-2 py-1 px-1 cursor-pointer hover:bg-gray-50">
                <input type="checkbox" :value="user.userId" v-model="selectedUserIds" />
                <span class="text-sm">{{ user.username }} ({{ user.realName }})</span>
              </label>
              <p v-if="filteredUsers.length === 0" class="text-gray-400 text-sm">无匹配用户</p>
            </div>
          </div>
          <p class="text-sm text-gray-500 mt-1">已选 {{ selectedUserIds.length }} 个用户</p>
        </el-form-item>
        <el-form-item label="选择权限组" required>
          <el-select v-model="selectedGroupId" placeholder="请选择权限组" class="!w-full">
            <el-option v-for="group in permissionGroups" :key="group.id" :label="group.name" :value="group.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <div v-if="batchError" class="text-red-500 text-sm px-4 pb-2">{{ batchError }}</div>
      <template #footer>
        <el-button @click="closeBatchDialog">取消</el-button>
        <el-button type="primary" @click="submitBatchAssign" :loading="submitting">确认授权</el-button>
      </template>
    </el-dialog>

    <!-- Delete Confirm Dialog -->
    <el-dialog v-model="deleteDialogVisible" title="确认删除" width="400px" @close="closeDeleteDialog">
      <p>确定要删除用户 "{{ deleteTarget?.username }}" 的数据权限吗？</p>
      <p class="text-gray-400 text-sm mt-2">删除后所有关联的工厂/工作中心/工序权限将一并删除，不可恢复。</p>
      <template #footer>
        <el-button @click="closeDeleteDialog">取消</el-button>
        <el-button type="danger" @click="doDelete" :loading="submitting">删除</el-button>
      </template>
    </el-dialog>

    <!-- Tab Dialog for Direct Assignment -->
    <el-dialog v-model="tabDialogVisible" :title="`权限管理 - ${tabPermission?.username} (${tabPermission?.realName})`" width="700px" @close="closeTabDialog">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="工厂" name="factory">
          <div class="flex justify-between items-center mb-2">
            <span class="text-sm text-gray-500">已选 {{ selectedFactoryIds.length }} 项</span>
            <el-button size="small" type="primary" @click="saveDirectFactories" :loading="submitting">
              {{ submitting ? '保存中...' : '保存直接授权' }}
            </el-button>
          </div>
          <el-table :data="factoryOptions" border size="small" max-height="400">
            <el-table-column label="选择" min-width="60">
              <template #default="{ row }">
                <el-checkbox :value="row.id" v-model="selectedFactoryIds" />
              </template>
            </el-table-column>
            <el-table-column prop="code" label="工厂编码" min-width="120" />
            <el-table-column prop="name" label="工厂名称" min-width="120" />
            <el-table-column label="来源" min-width="80">
              <template #default="{ row }">
                <el-tag v-if="getSourceTag(row.id, 'factory')" :type="getSourceTag(row.id, 'factory') === 'GROUP' ? 'success' : 'warning'" size="small">
                  {{ getSourceTag(row.id, 'factory') === 'GROUP' ? '权限组' : '直接' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="factoryOptions.length === 0" description="暂无工厂数据" />
        </el-tab-pane>
        <el-tab-pane label="工作中心" name="workCenter">
          <div class="flex justify-between items-center mb-2">
            <span class="text-sm text-gray-500">已选 {{ selectedWorkCenterIds.length }} 项</span>
            <el-button size="small" type="primary" @click="saveDirectWorkCenters" :loading="submitting">
              {{ submitting ? '保存中...' : '保存直接授权' }}
            </el-button>
          </div>
          <el-table :data="workCenterOptions" border size="small" max-height="400">
            <el-table-column label="选择" min-width="60">
              <template #default="{ row }">
                <el-checkbox :value="row.id" v-model="selectedWorkCenterIds" />
              </template>
            </el-table-column>
            <el-table-column prop="code" label="工作中心编码" min-width="120" />
            <el-table-column prop="name" label="工作中心名称" min-width="120" />
            <el-table-column label="来源" min-width="80">
              <template #default="{ row }">
                <el-tag v-if="getSourceTag(row.id, 'workCenter')" :type="getSourceTag(row.id, 'workCenter') === 'GROUP' ? 'success' : 'warning'" size="small">
                  {{ getSourceTag(row.id, 'workCenter') === 'GROUP' ? '权限组' : '直接' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="workCenterOptions.length === 0" description="暂无工作中心数据" />
        </el-tab-pane>
        <el-tab-pane label="工序" name="process">
          <div class="flex justify-between items-center mb-2">
            <span class="text-sm text-gray-500">已选 {{ selectedProcessIds.length }} 项</span>
            <el-button size="small" type="primary" @click="saveDirectProcesses" :loading="submitting">
              {{ submitting ? '保存中...' : '保存直接授权' }}
            </el-button>
          </div>
          <el-table :data="processOptions" border size="small" max-height="400">
            <el-table-column label="选择" min-width="60">
              <template #default="{ row }">
                <el-checkbox :value="row.id" v-model="selectedProcessIds" />
              </template>
            </el-table-column>
            <el-table-column prop="code" label="工序编码" min-width="120" />
            <el-table-column prop="name" label="工序名称" min-width="120" />
            <el-table-column label="来源" min-width="80">
              <template #default="{ row }">
                <el-tag v-if="getSourceTag(row.id, 'process')" :type="getSourceTag(row.id, 'process') === 'GROUP' ? 'success' : 'warning'" size="small">
                  {{ getSourceTag(row.id, 'process') === 'GROUP' ? '权限组' : '直接' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="processOptions.length === 0" description="暂无工序数据" />
        </el-tab-pane>
      </el-tabs>
      <div v-if="tabError" class="text-red-500 text-sm mt-3">{{ tabError }}</div>
      <template #footer>
        <el-button @click="closeTabDialog">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { Search, Refresh, Plus } from '@element-plus/icons-vue';
import {
  listUserDataPermissions,
  batchAssignPermission,
  deleteUserDataPermission,
  getUserFactories,
  directAssignFactories,
  getUserWorkCenters,
  directAssignWorkCenters,
  getUserProcesses,
  directAssignProcesses,
  type UserDataPermissionVo,
  type UserDataPermissionQueryParams,
  type UserPermissionAssociatedEntityDto,
} from '../../api/userDataPermission';
import {
  getFactoryDropdown,
  getWorkCenterDropdown,
  getProcessDropdown,
  getPermissionGroupDropdown,
  type DropdownItem,
} from '../../api/dropdown';

const permissions = ref<UserDataPermissionVo[]>([]);
const total = ref(0);
const loading = ref(false);
const submitting = ref(false);

const query = reactive<UserDataPermissionQueryParams>({
  username: '',
  realName: '',
  page: 1,
  size: 10,
});

const totalPages = computed(() => Math.ceil(total.value / (query.size || 10)));

// Batch assign dialog state
const batchDialogVisible = ref(false);
const batchError = ref('');
const userSearchKeyword = ref('');
const selectedUserIds = ref<number[]>([]);
const selectedGroupId = ref<number | null>(null);
const allUsers = ref<UserDataPermissionVo[]>([]);
const filteredUsers = ref<UserDataPermissionVo[]>([]);
const permissionGroups = ref<DropdownItem[]>([]);

// Delete dialog state
const deleteDialogVisible = ref(false);
const deleteTarget = ref<UserDataPermissionVo | null>(null);

// Tab dialog state
const tabDialogVisible = ref(false);
const tabPermission = ref<UserDataPermissionVo | null>(null);
const tabError = ref('');
const activeTab = ref<'factory' | 'workCenter' | 'process'>('factory');

// Dropdown options
const factoryOptions = ref<DropdownItem[]>([]);
const workCenterOptions = ref<DropdownItem[]>([]);
const processOptions = ref<DropdownItem[]>([]);

// Current associations (loaded from backend)
const currentFactories = ref<UserPermissionAssociatedEntityDto[]>([]);
const currentWorkCenters = ref<UserPermissionAssociatedEntityDto[]>([]);
const currentProcesses = ref<UserPermissionAssociatedEntityDto[]>([]);

// Selected IDs for direct assignment
const selectedFactoryIds = ref<number[]>([]);
const selectedWorkCenterIds = ref<number[]>([]);
const selectedProcessIds = ref<number[]>([]);

async function fetchList() {
  loading.value = true;
  try {
    const res = await listUserDataPermissions(query);
    if (res.code === 200 && res.data) {
      permissions.value = res.data.records;
      total.value = res.data.total;
    }
  } catch (e) {
    console.error('Failed to fetch user permission list', e);
  } finally {
    loading.value = false;
  }
}

function search() {
  query.page = 1;
  fetchList();
}

function resetQuery() {
  query.username = '';
  query.realName = '';
  query.page = 1;
  fetchList();
}

function goPage(page: number) {
  query.page = page;
  fetchList();
}

// Batch assign
async function openBatchDialog() {
  batchDialogVisible.value = true;
  batchError.value = '';
  selectedUserIds.value = [];
  selectedGroupId.value = null;
  userSearchKeyword.value = '';

  // Load all users and permission groups
  const allRes = await listUserDataPermissions({ page: 1, size: 1000 });
  if (allRes.code === 200 && allRes.data) {
    allUsers.value = allRes.data.records;
    filteredUsers.value = allRes.data.records;
  }

  const groupRes = await getPermissionGroupDropdown();
  permissionGroups.value = (groupRes.code === 200 && groupRes.data) ? groupRes.data : [];
}

function closeBatchDialog() {
  batchDialogVisible.value = false;
}

function filterUsers() {
  const keyword = userSearchKeyword.value.toLowerCase();
  if (!keyword) {
    filteredUsers.value = allUsers.value;
  } else {
    filteredUsers.value = allUsers.value.filter(
      u => u.username.toLowerCase().includes(keyword) || u.realName.toLowerCase().includes(keyword)
    );
  }
}

async function submitBatchAssign() {
  if (selectedUserIds.value.length === 0) {
    batchError.value = '请选择至少一个用户';
    return;
  }
  if (!selectedGroupId.value) {
    batchError.value = '请选择权限组';
    return;
  }

  submitting.value = true;
  batchError.value = '';
  try {
    await batchAssignPermission(selectedUserIds.value, selectedGroupId.value);
    closeBatchDialog();
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '批量授权失败';
    batchError.value = msg;
  } finally {
    submitting.value = false;
  }
}

// Delete
function confirmDelete(perm: UserDataPermissionVo) {
  deleteTarget.value = perm;
  deleteDialogVisible.value = true;
}

function closeDeleteDialog() {
  deleteDialogVisible.value = false;
  deleteTarget.value = null;
}

async function doDelete() {
  if (!deleteTarget.value || !deleteTarget.value.id) return;
  submitting.value = true;
  try {
    await deleteUserDataPermission(deleteTarget.value.id);
    closeDeleteDialog();
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '删除失败';
    alert(msg);
  } finally {
    submitting.value = false;
  }
}

// Tab dialog for direct assignment
async function openTabDialog(perm: UserDataPermissionVo) {
  tabPermission.value = perm;
  activeTab.value = 'factory';
  tabError.value = '';
  tabDialogVisible.value = true;

  // If user has no permission record yet, we need to create one first via batch-assign
  // For now, only allow direct assignment if permission record exists
  if (!perm.id) {
    tabError.value = '该用户尚未分配权限，请先通过批量授权创建权限记录';
    return;
  }

  // Load options and current associations in parallel
  const [factoryRes, wcRes, processRes, userFactories, userWcs, userProcesses] = await Promise.all([
    getFactoryDropdown(),
    getWorkCenterDropdown(),
    getProcessDropdown(),
    getUserFactories(perm.id),
    getUserWorkCenters(perm.id),
    getUserProcesses(perm.id),
  ]);

  factoryOptions.value = (factoryRes.code === 200 && factoryRes.data) ? factoryRes.data : [];
  workCenterOptions.value = (wcRes.code === 200 && wcRes.data) ? wcRes.data : [];
  processOptions.value = (processRes.code === 200 && processRes.data) ? processRes.data : [];

  currentFactories.value = (userFactories.code === 200 && userFactories.data) ? userFactories.data : [];
  currentWorkCenters.value = (userWcs.code === 200 && userWcs.data) ? userWcs.data : [];
  currentProcesses.value = (userProcesses.code === 200 && userProcesses.data) ? userProcesses.data : [];

  // Pre-select all current associations
  selectedFactoryIds.value = currentFactories.value.map(f => f.id);
  selectedWorkCenterIds.value = currentWorkCenters.value.map(wc => wc.id);
  selectedProcessIds.value = currentProcesses.value.map(p => p.id);
}

function closeTabDialog() {
  tabDialogVisible.value = false;
}

function switchTab(tab: 'factory' | 'workCenter' | 'process') {
  activeTab.value = tab;
}

function getSourceTag(id: number, type: 'factory' | 'workCenter' | 'process'): string | null {
  const list = type === 'factory' ? currentFactories.value
    : type === 'workCenter' ? currentWorkCenters.value
    : currentProcesses.value;
  const found = list.find(item => item.id === id);
  return found ? found.source : null;
}

async function saveDirectFactories() {
  if (!tabPermission.value?.id) return;
  submitting.value = true;
  tabError.value = '';
  try {
    await directAssignFactories(tabPermission.value.id, selectedFactoryIds.value);
    // Reload associations
    const res = await getUserFactories(tabPermission.value.id);
    currentFactories.value = (res.code === 200 && res.data) ? res.data : [];
    selectedFactoryIds.value = currentFactories.value.map(f => f.id);
  } catch (e: any) {
    tabError.value = e?.response?.data?.message || '保存失败';
  } finally {
    submitting.value = false;
  }
}

async function saveDirectWorkCenters() {
  if (!tabPermission.value?.id) return;
  submitting.value = true;
  tabError.value = '';
  try {
    await directAssignWorkCenters(tabPermission.value.id, selectedWorkCenterIds.value);
    const res = await getUserWorkCenters(tabPermission.value.id);
    currentWorkCenters.value = (res.code === 200 && res.data) ? res.data : [];
    selectedWorkCenterIds.value = currentWorkCenters.value.map(wc => wc.id);
  } catch (e: any) {
    tabError.value = e?.response?.data?.message || '保存失败';
  } finally {
    submitting.value = false;
  }
}

async function saveDirectProcesses() {
  if (!tabPermission.value?.id) return;
  submitting.value = true;
  tabError.value = '';
  try {
    await directAssignProcesses(tabPermission.value.id, selectedProcessIds.value);
    const res = await getUserProcesses(tabPermission.value.id);
    currentProcesses.value = (res.code === 200 && res.data) ? res.data : [];
    selectedProcessIds.value = currentProcesses.value.map(p => p.id);
  } catch (e: any) {
    tabError.value = e?.response?.data?.message || '保存失败';
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
