<template>
  <div class="permission-group-page">
    <div class="page-header">
      <h2>数据权限组管理</h2>
      <div class="header-actions">
        <button class="btn btn-primary" @click="openCreateDialog">新建权限组</button>
      </div>
    </div>

    <!-- Search Bar -->
    <div class="search-bar">
      <input
        v-model="query.groupName"
        type="text"
        placeholder="权限组名称"
        class="input"
        @keyup.enter="search"
      />
      <button class="btn" @click="search">查询</button>
      <button class="btn btn-secondary" @click="resetQuery">重置</button>
    </div>

    <!-- Group Table -->
    <table class="table">
      <thead>
        <tr>
          <th>权限组名称</th>
          <th>备注</th>
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
          <td colspan="8" class="text-center">加载中...</td>
        </tr>
        <tr v-else-if="groups.length === 0">
          <td colspan="8" class="text-center">暂无数据</td>
        </tr>
        <tr v-for="group in groups" :key="group.id">
          <td>{{ group.groupName }}</td>
          <td>{{ group.remark || '-' }}</td>
          <td>{{ group.factoryCount }}</td>
          <td>{{ group.workCenterCount }}</td>
          <td>{{ group.processCount }}</td>
          <td>{{ group.createdBy || '-' }}</td>
          <td>{{ formatDate(group.createdAt) }}</td>
          <td class="actions">
            <button class="btn btn-sm" @click="openEditDialog(group)">编辑</button>
            <button class="btn btn-sm btn-info" @click="openTabDialog(group)">关联管理</button>
            <button
              class="btn btn-sm btn-danger"
              :disabled="group.referenced"
              @click="confirmDelete(group)"
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

    <!-- Create/Edit Dialog -->
    <div v-if="dialogVisible" class="dialog-overlay" @click.self="closeDialog">
      <div class="dialog">
        <div class="dialog-header">
          <h3>{{ isEdit ? '编辑权限组' : '新建权限组' }}</h3>
          <button class="dialog-close" @click="closeDialog">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label>权限组名称 <span class="required">*</span></label>
            <input
              v-model="form.groupName"
              type="text"
              class="input"
              placeholder="请输入权限组名称"
            />
          </div>
          <div class="form-group">
            <label>备注</label>
            <textarea
              v-model="form.remark"
              class="input"
              placeholder="请输入备注"
              rows="3"
            ></textarea>
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
          <p>确定要删除权限组 "{{ deleteTarget?.groupName }}" 吗？</p>
          <p class="text-muted">删除后关联的工厂/工作中心/工序关联记录将一并删除，不可恢复。</p>
        </div>
        <div class="dialog-footer">
          <button class="btn" @click="closeDeleteDialog">取消</button>
          <button class="btn btn-danger" @click="doDelete" :disabled="submitting">
            {{ submitting ? '删除中...' : '删除' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Tab Dialog for Association Management -->
    <div v-if="tabDialogVisible" class="dialog-overlay" @click.self="closeTabDialog">
      <div class="dialog dialog-lg">
        <div class="dialog-header">
          <h3>关联管理 - {{ tabGroup?.groupName }}</h3>
          <button class="dialog-close" @click="closeTabDialog">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="tab-bar">
            <button
              :class="['tab-btn', activeTab === 'factory' ? 'tab-active' : '']"
              @click="switchTab('factory')"
            >
              工厂 ({{ selectedFactoryIds.length }})
            </button>
            <button
              :class="['tab-btn', activeTab === 'workCenter' ? 'tab-active' : '']"
              @click="switchTab('workCenter')"
            >
              工作中心 ({{ selectedWorkCenterIds.length }})
            </button>
            <button
              :class="['tab-btn', activeTab === 'process' ? 'tab-active' : '']"
              @click="switchTab('process')"
            >
              工序 ({{ selectedProcessIds.length }})
            </button>
          </div>

          <!-- Factory Tab -->
          <div v-if="activeTab === 'factory'" class="tab-content">
            <div class="select-actions">
              <span class="selection-info">已选 {{ selectedFactoryIds.length }} 项</span>
            </div>
            <table class="table">
              <thead>
                <tr>
                  <th><input type="checkbox" :checked="allFactoryChecked" @change="toggleAllFactories" /></th>
                  <th>工厂编码</th>
                  <th>工厂名称</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="factoryOptions.length === 0">
                  <td colspan="3" class="text-center">暂无工厂数据</td>
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
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Work Center Tab -->
          <div v-if="activeTab === 'workCenter'" class="tab-content">
            <div class="select-actions">
              <span class="selection-info">已选 {{ selectedWorkCenterIds.length }} 项</span>
            </div>
            <table class="table">
              <thead>
                <tr>
                  <th><input type="checkbox" :checked="allWorkCenterChecked" @change="toggleAllWorkCenters" /></th>
                  <th>工作中心编码</th>
                  <th>工作中心名称</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="workCenterOptions.length === 0">
                  <td colspan="3" class="text-center">暂无工作中心数据</td>
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
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Process Tab -->
          <div v-if="activeTab === 'process'" class="tab-content">
            <div class="select-actions">
              <span class="selection-info">已选 {{ selectedProcessIds.length }} 项</span>
            </div>
            <table class="table">
              <thead>
                <tr>
                  <th><input type="checkbox" :checked="allProcessChecked" @change="toggleAllProcesses" /></th>
                  <th>工序编码</th>
                  <th>工序名称</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="processOptions.length === 0">
                  <td colspan="3" class="text-center">暂无工序数据</td>
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
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn" @click="closeTabDialog">取消</button>
          <button class="btn btn-primary" @click="saveAssociations" :disabled="submitting">
            {{ submitting ? '保存中...' : '保存' }}
          </button>
        </div>
        <div v-if="tabError" class="form-error">{{ tabError }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
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

function toggleAllFactories(e: Event) {
  const checked = (e.target as HTMLInputElement).checked;
  if (checked) {
    selectedFactoryIds.value = factoryOptions.value.map(f => f.id);
  } else {
    selectedFactoryIds.value = [];
  }
}

function toggleAllWorkCenters(e: Event) {
  const checked = (e.target as HTMLInputElement).checked;
  if (checked) {
    selectedWorkCenterIds.value = workCenterOptions.value.map(wc => wc.id);
  } else {
    selectedWorkCenterIds.value = [];
  }
}

function toggleAllProcesses(e: Event) {
  const checked = (e.target as HTMLInputElement).checked;
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

<style scoped>
.permission-group-page {
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
