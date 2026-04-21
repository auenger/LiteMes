<template>
  <div class="department-user-page">
    <div class="page-header">
      <div class="header-left">
        <button class="btn" @click="goBack">&lt; 返回部门列表</button>
        <h2>{{ departmentName }} - 部门用户</h2>
      </div>
      <button class="btn btn-primary" @click="openSelectUserDialog">选择用户</button>
    </div>

    <!-- Department User Table -->
    <table class="table">
      <thead>
        <tr>
          <th>用户名</th>
          <th>姓名</th>
          <th>分配时间</th>
          <th>分配人</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="loading">
          <td colspan="5" class="text-center">加载中...</td>
        </tr>
        <tr v-else-if="users.length === 0">
          <td colspan="5" class="text-center">暂无用户，点击"选择用户"添加</td>
        </tr>
        <tr v-for="du in users" :key="du.id">
          <td>{{ du.username }}</td>
          <td>{{ du.realName }}</td>
          <td>{{ formatDate(du.createdAt) }}</td>
          <td>{{ du.createdBy || '-' }}</td>
          <td class="actions">
            <button class="btn btn-sm btn-danger" @click="confirmRemove(du)">移除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Select User Dialog -->
    <div v-if="selectDialogVisible" class="dialog-overlay" @click.self="closeSelectDialog">
      <div class="dialog dialog-lg">
        <div class="dialog-header">
          <h3>选择用户</h3>
          <button class="dialog-close" @click="closeSelectDialog">&times;</button>
        </div>
        <div class="dialog-body">
          <!-- User Search -->
          <div class="search-bar">
            <input
              v-model="userQuery.username"
              type="text"
              placeholder="用户名"
              class="input"
              @keyup.enter="searchUsers"
            />
            <input
              v-model="userQuery.realName"
              type="text"
              placeholder="姓名"
              class="input"
              @keyup.enter="searchUsers"
            />
            <button class="btn" @click="searchUsers">查询</button>
          </div>

          <!-- User List for Selection -->
          <table class="table">
            <thead>
              <tr>
                <th class="col-check">
                  <input type="checkbox" v-model="selectAll" @change="toggleSelectAll" />
                </th>
                <th>用户名</th>
                <th>姓名</th>
                <th>状态</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="userLoading">
                <td colspan="4" class="text-center">加载中...</td>
              </tr>
              <tr v-else-if="availableUsers.length === 0">
                <td colspan="4" class="text-center">没有找到用户</td>
              </tr>
              <tr v-for="u in availableUsers" :key="u.id">
                <td class="col-check">
                  <input type="checkbox" :value="u.id" v-model="selectedUserIds" />
                </td>
                <td>{{ u.username }}</td>
                <td>{{ u.realName }}</td>
                <td>
                  <span :class="['status-badge', u.status === 1 ? 'status-enabled' : 'status-disabled']">
                    {{ u.status === 1 ? '启用' : '禁用' }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>

          <!-- User Pagination -->
          <div class="pagination" v-if="userTotal > 0">
            <span class="pagination-info">
              共 {{ userTotal }} 条，第 {{ userQuery.page }} / {{ userTotalPages }} 页
            </span>
            <button class="btn btn-sm" :disabled="userQuery.page <= 1" @click="userQuery.page--; searchUsers()">上一页</button>
            <button class="btn btn-sm" :disabled="userQuery.page >= userTotalPages" @click="userQuery.page++; searchUsers()">下一页</button>
          </div>
        </div>
        <div class="dialog-footer">
          <span class="selected-info">已选择 {{ selectedUserIds.length }} 人</span>
          <button class="btn" @click="closeSelectDialog">取消</button>
          <button class="btn btn-primary" @click="doAssign" :disabled="submitting || selectedUserIds.length === 0">
            {{ submitting ? '提交中...' : '确认分配' }}
          </button>
        </div>
        <div v-if="formError" class="form-error">{{ formError }}</div>
      </div>
    </div>

    <!-- Remove Confirm Dialog -->
    <div v-if="removeDialogVisible" class="dialog-overlay" @click.self="closeRemoveDialog">
      <div class="dialog dialog-sm">
        <div class="dialog-header">
          <h3>确认移除</h3>
          <button class="dialog-close" @click="closeRemoveDialog">&times;</button>
        </div>
        <div class="dialog-body">
          <p>确定要将用户 "{{ removeTarget?.realName }}" ({{ removeTarget?.username }}) 从该部门移除吗？</p>
        </div>
        <div class="dialog-footer">
          <button class="btn" @click="closeRemoveDialog">取消</button>
          <button class="btn btn-danger" @click="doRemove" :disabled="submitting">
            {{ submitting ? '移除中...' : '确认移除' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  listDepartmentUsers,
  assignUsers,
  removeUser,
  searchUsers as searchUsersApi,
  type DepartmentUserDto,
  type UserDto,
} from '../../api/departmentUser';
import { getDepartment, type DepartmentDto } from '../../api/department';

const route = useRoute();
const router = useRouter();
const departmentId = computed(() => Number(route.params.id));

const users = ref<DepartmentUserDto[]>([]);
const loading = ref(false);
const submitting = ref(false);
const formError = ref('');
const departmentName = ref('');

// Select user dialog state
const selectDialogVisible = ref(false);
const userLoading = ref(false);
const availableUsers = ref<UserDto[]>([]);
const selectedUserIds = ref<number[]>([]);
const selectAll = ref(false);
const userTotal = ref(0);

const userQuery = reactive({
  username: '',
  realName: '',
  page: 1,
  size: 10,
});

const userTotalPages = computed(() => Math.ceil(userTotal.value / userQuery.size));

// Remove dialog state
const removeDialogVisible = ref(false);
const removeTarget = ref<DepartmentUserDto | null>(null);

async function fetchDepartmentName() {
  try {
    const res = await getDepartment(departmentId.value);
    if (res.code === 200 && res.data) {
      departmentName.value = res.data.name;
    }
  } catch (e) {
    console.error('Failed to fetch department', e);
  }
}

async function fetchUsers() {
  loading.value = true;
  try {
    const res = await listDepartmentUsers(departmentId.value);
    if (res.code === 200 && res.data) {
      users.value = res.data;
    }
  } catch (e) {
    console.error('Failed to fetch department users', e);
  } finally {
    loading.value = false;
  }
}

function goBack() {
  router.push('/departments');
}

// Select user dialog
async function openSelectUserDialog() {
  selectedUserIds.value = [];
  selectAll.value = false;
  userQuery.username = '';
  userQuery.realName = '';
  userQuery.page = 1;
  formError.value = '';
  selectDialogVisible.value = true;
  await doSearchUsers();
}

function closeSelectDialog() {
  selectDialogVisible.value = false;
}

async function doSearchUsers() {
  userLoading.value = true;
  try {
    const res = await searchUsersApi({
      username: userQuery.username || undefined,
      realName: userQuery.realName || undefined,
      page: userQuery.page,
      size: userQuery.size,
    });
    if (res.code === 200 && res.data) {
      availableUsers.value = res.data.records;
      userTotal.value = res.data.total;
    }
  } catch (e) {
    console.error('Failed to search users', e);
  } finally {
    userLoading.value = false;
  }
}

function searchUsers() {
  userQuery.page = 1;
  doSearchUsers();
}

function toggleSelectAll() {
  if (selectAll.value) {
    const currentIds = availableUsers.value.map(u => u.id);
    const newIds = currentIds.filter(id => !selectedUserIds.value.includes(id));
    selectedUserIds.value = [...selectedUserIds.value, ...newIds];
  } else {
    const currentIds = new Set(availableUsers.value.map(u => u.id));
    selectedUserIds.value = selectedUserIds.value.filter(id => !currentIds.has(id));
  }
}

async function doAssign() {
  if (selectedUserIds.value.length === 0) return;
  submitting.value = true;
  formError.value = '';
  try {
    await assignUsers(departmentId.value, selectedUserIds.value);
    closeSelectDialog();
    fetchUsers();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '分配失败';
    formError.value = msg;
  } finally {
    submitting.value = false;
  }
}

// Remove user dialog
function confirmRemove(du: DepartmentUserDto) {
  removeTarget.value = du;
  removeDialogVisible.value = true;
}

function closeRemoveDialog() {
  removeDialogVisible.value = false;
  removeTarget.value = null;
}

async function doRemove() {
  if (!removeTarget.value) return;
  submitting.value = true;
  try {
    await removeUser(departmentId.value, removeTarget.value.userId);
    closeRemoveDialog();
    fetchUsers();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '移除失败';
    alert(msg);
  } finally {
    submitting.value = false;
  }
}

function formatDate(dateStr: string | undefined) {
  if (!dateStr) return '-';
  return new Date(dateStr).toLocaleString('zh-CN');
}

onMounted(() => {
  fetchDepartmentName();
  fetchUsers();
});
</script>

<style scoped>
.department-user-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
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

.col-check {
  width: 40px;
  text-align: center;
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
  align-items: center;
  gap: 10px;
  padding: 12px 24px;
  border-top: 1px solid #f0f0f0;
}

.selected-info {
  margin-right: auto;
  font-size: 14px;
  color: #666;
}

.form-error {
  padding: 8px 24px 16px;
  color: #ff4d4f;
  font-size: 13px;
}
</style>
