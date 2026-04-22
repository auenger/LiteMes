<template>
  <div class="h-full flex flex-col space-y-3">
    <!-- Header -->
    <div class="bg-card p-3 border border-border-color shadow-sm rounded-sm shrink-0 flex justify-between items-center">
      <div class="flex items-center gap-3">
        <el-button :icon="Refresh" @click="goBack">返回部门列表</el-button>
        <h2 class="text-lg font-semibold m-0">{{ departmentName }} - 部门用户</h2>
      </div>
      <el-button type="primary" :icon="Plus" @click="openSelectUserDialog">选择用户</el-button>
    </div>

    <!-- Main Content Card -->
    <div class="flex-1 bg-card border border-border-color shadow-sm rounded-sm flex flex-col overflow-hidden">
      <!-- Table -->
      <div class="flex-1 p-2.5 overflow-hidden">
        <el-table :data="users" border stripe height="100%" v-loading="loading">
          <el-table-column prop="username" label="用户名" width="180" />
          <el-table-column prop="realName" label="姓名" width="180" />
          <el-table-column label="分配时间" width="200">
            <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
          </el-table-column>
          <el-table-column label="分配人" width="150">
            <template #default="{ row }">{{ row.createdBy || '-' }}</template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right" align="center">
            <template #default="{ row }">
              <el-button link type="danger" size="small" @click="confirmRemove(row)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- Select User Dialog -->
    <el-dialog title="选择用户" width="680px" v-model="selectDialogVisible">
      <!-- User Search -->
      <el-form :inline="true" size="default" class="flex flex-wrap gap-y-3 items-center !mb-3">
        <el-form-item label="用户名" class="!mb-0">
          <el-input v-model="userQuery.username" placeholder="用户名" clearable class="!w-40" @keyup.enter="searchUsers" />
        </el-form-item>
        <el-form-item label="姓名" class="!mb-0">
          <el-input v-model="userQuery.realName" placeholder="姓名" clearable class="!w-40" @keyup.enter="searchUsers" />
        </el-form-item>
        <el-form-item class="!mb-0 !mr-0">
          <el-button type="primary" :icon="Search" @click="searchUsers">查询</el-button>
        </el-form-item>
      </el-form>

      <!-- User List for Selection -->
      <el-table :data="availableUsers" border stripe max-height="400" v-loading="userLoading">
        <el-table-column width="55" align="center">
          <template #header>
            <el-checkbox v-model="selectAll" @change="toggleSelectAll" />
          </template>
          <template #default="{ row }">
            <el-checkbox :value="row.id" v-model="selectedUserIds" />
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="realName" label="姓名" width="150" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <!-- User Pagination -->
      <div class="flex justify-end mt-3">
        <el-pagination
          layout="total, prev, pager, next"
          :total="userTotal"
          v-model:current-page="userQuery.page"
          v-model:page-size="userQuery.size"
          background
          size="small"
          @current-change="doSearchUsers"
        />
      </div>

      <div v-if="formError" class="text-red-500 text-sm px-4 mt-2">{{ formError }}</div>
      <template #footer>
        <span class="text-gray-500 text-sm mr-auto">已选择 {{ selectedUserIds.length }} 人</span>
        <el-button @click="closeSelectDialog">取消</el-button>
        <el-button type="primary" @click="doAssign" :loading="submitting" :disabled="selectedUserIds.length === 0">确认分配</el-button>
      </template>
    </el-dialog>

    <!-- Remove Confirm -->
    <el-dialog title="确认移除" width="400px" v-model="removeDialogVisible">
      <p>确定要将用户 "{{ removeTarget?.realName }}" ({{ removeTarget?.username }}) 从该部门移除吗？</p>
      <template #footer>
        <el-button @click="closeRemoveDialog">取消</el-button>
        <el-button type="danger" @click="doRemove" :loading="submitting">确认移除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Search, Refresh, Plus } from '@element-plus/icons-vue';
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
