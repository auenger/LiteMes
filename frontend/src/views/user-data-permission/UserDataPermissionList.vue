<template>
  <div class="user-permission-page">
    <div class="page-header">
      <h2>用户数据权限</h2>
      <div class="header-actions">
        <button class="btn btn-primary" @click="openBatchDialog">批量授权</button>
      </div>
    </div>

    <!-- Search Bar -->
    <div class="search-bar">
      <input
        v-model="query.username"
        type="text"
        placeholder="用户名"
        class="input"
        @keyup.enter="search"
      />
      <input
        v-model="query.realName"
        type="text"
        placeholder="姓名"
        class="input"
        @keyup.enter="search"
      />
      <button class="btn" @click="search">查询</button>
      <button class="btn btn-secondary" @click="resetQuery">重置</button>
    </div>

    <!-- Table -->
    <table class="table">
      <thead>
        <tr>
          <th>用户名</th>
          <th>姓名</th>
          <th>权限组</th>
          <th>工厂数</th>
          <th>工作中心数</th>
          <th>工序数</th>
          <th>创建人</th>
          <th>创建时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="loading">
          <td colspan="9" class="text-center">加载中...</td>
        </tr>
        <tr v-else-if="permissions.length === 0">
          <td colspan="9" class="text-center">暂无数据</td>
        </tr>
        <tr v-for="perm in permissions" :key="perm.userId">
          <td>{{ perm.username }}</td>
          <td>{{ perm.realName }}</td>
          <td>
            <span v-if="perm.groupName" class="tag tag-blue">{{ perm.groupName }}</span>
            <span v-else class="text-muted">-</span>
          </td>
          <td>{{ perm.factoryCount || 0 }}</td>
          <td>{{ perm.workCenterCount || 0 }}</td>
          <td>{{ perm.processCount || 0 }}</td>
          <td>{{ perm.createdBy || '-' }}</td>
          <td>{{ formatDate(perm.createdAt) }}</td>
          <td class="actions">
            <button
              class="btn btn-sm btn-info"
              @click="openTabDialog(perm)"
            >
              权限管理
            </button>
            <button
              v-if="perm.id"
              class="btn btn-sm btn-danger"
              @click="confirmDelete(perm)"
            >
              删除
            </button>
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

    <!-- Batch Assign Dialog -->
    <div v-if="batchDialogVisible" class="dialog-overlay" @click.self="closeBatchDialog">
      <div class="dialog dialog-lg">
        <div class="dialog-header">
          <h3>批量授权</h3>
          <button class="dialog-close" @click="closeBatchDialog">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label>选择用户 <span class="required">*</span></label>
            <div class="user-select-area">
              <input
                v-model="userSearchKeyword"
                type="text"
                placeholder="搜索用户名/姓名"
                class="input input-sm"
                @input="filterUsers"
              />
              <div class="checkbox-list">
                <label v-for="user in filteredUsers" :key="user.id" class="checkbox-item">
                  <input
                    type="checkbox"
                    :value="user.id"
                    v-model="selectedUserIds"
                  />
                  {{ user.username }} ({{ user.realName }})
                </label>
                <p v-if="filteredUsers.length === 0" class="text-muted">无匹配用户</p>
              </div>
            </div>
            <p class="selection-summary">已选 {{ selectedUserIds.length }} 个用户</p>
          </div>
          <div class="form-group">
            <label>选择权限组 <span class="required">*</span></label>
            <select v-model="selectedGroupId" class="input">
              <option :value="null" disabled>请选择权限组</option>
              <option v-for="group in permissionGroups" :key="group.id" :value="group.id">
                {{ group.groupName }}
              </option>
            </select>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn" @click="closeBatchDialog">取消</button>
          <button class="btn btn-primary" @click="submitBatchAssign" :disabled="submitting">
            {{ submitting ? '提交中...' : '确认授权' }}
          </button>
        </div>
        <div v-if="batchError" class="form-error">{{ batchError }}</div>
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
          <p>确定要删除用户 "{{ deleteTarget?.username }}" 的数据权限吗？</p>
          <p class="text-muted">删除后所有关联的工厂/工作中心/工序权限将一并删除，不可恢复。</p>
        </div>
        <div class="dialog-footer">
          <button class="btn" @click="closeDeleteDialog">取消</button>
          <button class="btn btn-danger" @click="doDelete" :disabled="submitting">
            {{ submitting ? '删除中...' : '删除' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Tab Dialog for Direct Assignment -->
    <div v-if="tabDialogVisible" class="dialog-overlay" @click.self="closeTabDialog">
      <div class="dialog dialog-lg">
        <div class="dialog-header">
          <h3>权限管理 - {{ tabPermission?.username }} ({{ tabPermission?.realName }})</h3>
          <button class="dialog-close" @click="closeTabDialog">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="tab-bar">
            <button
              :class="['tab-btn', activeTab === 'factory' ? 'tab-active' : '']"
              @click="switchTab('factory')"
            >
              工厂 ({{ currentFactories.length }})
            </button>
            <button
              :class="['tab-btn', activeTab === 'workCenter' ? 'tab-active' : '']"
              @click="switchTab('workCenter')"
            >
              工作中心 ({{ currentWorkCenters.length }})
            </button>
            <button
              :class="['tab-btn', activeTab === 'process' ? 'tab-active' : '']"
              @click="switchTab('process')"
            >
              工序 ({{ currentProcesses.length }})
            </button>
          </div>

          <!-- Factory Tab -->
          <div v-if="activeTab === 'factory'" class="tab-content">
            <div class="select-actions">
              <span class="selection-info">已选 {{ selectedFactoryIds.length }} 项</span>
              <button class="btn btn-sm btn-primary" @click="saveDirectFactories" :disabled="submitting">
                {{ submitting ? '保存中...' : '保存直接授权' }}
              </button>
            </div>
            <table class="table">
              <thead>
                <tr>
                  <th>选择</th>
                  <th>工厂编码</th>
                  <th>工厂名称</th>
                  <th>来源</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="factoryOptions.length === 0">
                  <td colspan="4" class="text-center">暂无工厂数据</td>
                </tr>
                <tr v-for="f in factoryOptions" :key="f.id">
                  <td>
                    <input
                      type="checkbox"
                      :value="f.id"
                      v-model="selectedFactoryIds"
                    />
                  </td>
                  <td>{{ f.code }}</td>
                  <td>{{ f.name }}</td>
                  <td>
                    <span v-if="getSourceTag(f.id, 'factory')" :class="['tag', getSourceTag(f.id, 'factory') === 'GROUP' ? 'tag-green' : 'tag-orange']">
                      {{ getSourceTag(f.id, 'factory') === 'GROUP' ? '权限组' : '直接' }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Work Center Tab -->
          <div v-if="activeTab === 'workCenter'" class="tab-content">
            <div class="select-actions">
              <span class="selection-info">已选 {{ selectedWorkCenterIds.length }} 项</span>
              <button class="btn btn-sm btn-primary" @click="saveDirectWorkCenters" :disabled="submitting">
                {{ submitting ? '保存中...' : '保存直接授权' }}
              </button>
            </div>
            <table class="table">
              <thead>
                <tr>
                  <th>选择</th>
                  <th>工作中心编码</th>
                  <th>工作中心名称</th>
                  <th>来源</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="workCenterOptions.length === 0">
                  <td colspan="4" class="text-center">暂无工作中心数据</td>
                </tr>
                <tr v-for="wc in workCenterOptions" :key="wc.id">
                  <td>
                    <input
                      type="checkbox"
                      :value="wc.id"
                      v-model="selectedWorkCenterIds"
                    />
                  </td>
                  <td>{{ wc.code }}</td>
                  <td>{{ wc.name }}</td>
                  <td>
                    <span v-if="getSourceTag(wc.id, 'workCenter')" :class="['tag', getSourceTag(wc.id, 'workCenter') === 'GROUP' ? 'tag-green' : 'tag-orange']">
                      {{ getSourceTag(wc.id, 'workCenter') === 'GROUP' ? '权限组' : '直接' }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Process Tab -->
          <div v-if="activeTab === 'process'" class="tab-content">
            <div class="select-actions">
              <span class="selection-info">已选 {{ selectedProcessIds.length }} 项</span>
              <button class="btn btn-sm btn-primary" @click="saveDirectProcesses" :disabled="submitting">
                {{ submitting ? '保存中...' : '保存直接授权' }}
              </button>
            </div>
            <table class="table">
              <thead>
                <tr>
                  <th>选择</th>
                  <th>工序编码</th>
                  <th>工序名称</th>
                  <th>来源</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="processOptions.length === 0">
                  <td colspan="4" class="text-center">暂无工序数据</td>
                </tr>
                <tr v-for="p in processOptions" :key="p.id">
                  <td>
                    <input
                      type="checkbox"
                      :value="p.id"
                      v-model="selectedProcessIds"
                    />
                  </td>
                  <td>{{ p.code }}</td>
                  <td>{{ p.name }}</td>
                  <td>
                    <span v-if="getSourceTag(p.id, 'process')" :class="['tag', getSourceTag(p.id, 'process') === 'GROUP' ? 'tag-green' : 'tag-orange']">
                      {{ getSourceTag(p.id, 'process') === 'GROUP' ? '权限组' : '直接' }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn" @click="closeTabDialog">关闭</button>
        </div>
        <div v-if="tabError" class="form-error">{{ tabError }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
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

<style scoped>
.user-permission-page {
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

.input-sm {
  padding: 4px 8px;
  font-size: 13px;
}

select.input {
  min-width: 200px;
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

/* Tags */
.tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 3px;
  font-size: 12px;
  font-weight: 500;
}

.tag-blue {
  background: #e6f7ff;
  color: #1890ff;
  border: 1px solid #91d5ff;
}

.tag-green {
  background: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.tag-orange {
  background: #fff7e6;
  color: #fa8c16;
  border: 1px solid #ffd591;
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
  max-height: 85vh;
  overflow-y: auto;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.dialog-lg {
  width: 700px;
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

/* User select area */
.user-select-area {
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  padding: 8px;
  max-height: 250px;
  overflow-y: auto;
}

.checkbox-list {
  margin-top: 8px;
}

.checkbox-item {
  display: block;
  padding: 4px 0;
  cursor: pointer;
  font-size: 14px;
}

.checkbox-item:hover {
  background: #f5f5f5;
}

.selection-summary {
  margin-top: 8px;
  font-size: 13px;
  color: #666;
}

/* Tab styles */
.tab-bar {
  display: flex;
  gap: 0;
  border-bottom: 2px solid #f0f0f0;
  margin-bottom: 16px;
}

.tab-btn {
  padding: 8px 20px;
  border: none;
  background: none;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  border-bottom: 2px solid transparent;
  margin-bottom: -2px;
  transition: all 0.2s;
}

.tab-btn:hover {
  color: #1890ff;
}

.tab-active {
  color: #1890ff;
  border-bottom-color: #1890ff;
  font-weight: 600;
}

.tab-content {
  max-height: 400px;
  overflow-y: auto;
}

.select-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.selection-info {
  font-size: 13px;
  color: #666;
}
</style>
