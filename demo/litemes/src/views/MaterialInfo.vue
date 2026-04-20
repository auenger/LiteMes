<template>
  <div class="h-full flex flex-col space-y-3">
    <!-- Filter Card -->
    <div class="bg-card p-3 border border-border-color shadow-sm rounded-sm shrink-0">
      <el-form :inline="true" :model="filters" size="default" class="flex flex-wrap gap-y-3 items-center !mb-0">
        <el-form-item label="物料编码" class="!mb-0">
          <el-input v-model="filters.code" placeholder="请输入" clearable class="!w-40" />
        </el-form-item>
        <el-form-item label="物料名称" class="!mb-0">
          <el-input v-model="filters.name" placeholder="请输入" clearable class="!w-40" />
        </el-form-item>
        <el-form-item label="启用状态" class="!mb-0">
          <el-select v-model="filters.status" placeholder="全部" clearable class="!w-28">
            <el-option label="已启用" :value="true" />
            <el-option label="已禁用" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item class="!mb-0 !mr-0">
          <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- Main Content Card -->
    <div class="flex-1 bg-card border border-border-color shadow-sm rounded-sm flex flex-col overflow-hidden">
      <!-- Toolbar -->
      <div class="px-4 py-2.5 border-b border-border-color flex justify-between items-center shrink-0 bg-slate-50/50 dark:bg-white/5">
        <div class="flex gap-2">
          <el-button type="primary" :icon="Plus" @click="handleAdd">新建</el-button>
          <el-button :icon="Download">导入</el-button>
          <el-button :icon="Upload">导出</el-button>
        </div>
        <div class="flex items-center gap-3 text-muted">
          <el-tooltip content="刷新数据" placement="top">
            <el-icon class="cursor-pointer text-lg hover:text-primary transition-colors" @click="handleQuery"><Refresh /></el-icon>
          </el-tooltip>
          <el-tooltip content="密度设置" placement="top">
            <el-icon class="cursor-pointer text-lg hover:text-primary transition-colors"><Operation /></el-icon>
          </el-tooltip>
          <el-tooltip content="列设置" placement="top">
            <el-icon class="cursor-pointer text-lg hover:text-primary transition-colors"><Setting /></el-icon>
          </el-tooltip>
        </div>
      </div>

      <!-- Table Section -->
      <div class="flex-1 p-2.5 overflow-hidden">
        <el-table 
          :data="filteredData" 
          height="100%" 
          border
          stripe
          class="ele-table"
          @current-change="handleSelect"
        >
          <el-table-column type="selection" width="45" align="center" />
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="code" label="物料编码" width="140" />
          <el-table-column prop="name" label="物料名称" show-overflow-tooltip wrap />
          <el-table-column prop="category" label="基本分类" width="120" align="center" />
          <el-table-column prop="unit" label="单位" width="80" align="center" />
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status ? 'success' : 'info'" size="small">
                {{ row.status ? '已启用' : '已禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="creator" label="创建者" width="120" />
          <el-table-column label="操作" width="180" fixed="right" align="center">
            <template #default>
              <div class="flex items-center justify-center gap-2">
                <el-button link type="primary" size="small" :icon="Plus">添加</el-button>
                <el-button link type="primary" size="small" :icon="Edit">编辑</el-button>
                <el-button link type="danger" size="small" :icon="Delete">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- Pagination -->
      <div class="px-4 py-3 border-t border-border-color flex justify-end shrink-0">
        <el-pagination 
          layout="total, sizes, prev, pager, next, jumper" 
          :total="filteredData.length" 
          background
          size="small"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useMasterDataStore } from '../stores/masterData';
import { 
  Search, Refresh, Plus, Download, Upload, Setting, Operation, Edit, Delete
} from '@element-plus/icons-vue';

const store = useMasterDataStore();
const filters = ref({
  code: '',
  name: '',
  status: null as boolean | null
});

const selectedRow = ref<any>(null);

const filteredData = computed(() => {
  return store.materials.filter(item => {
    const codeMatch = !filters.value.code || item.code.includes(filters.value.code);
    const nameMatch = !filters.value.name || item.name.includes(filters.value.name);
    const statusMatch = filters.value.status === null || item.status === filters.value.status;
    return codeMatch && nameMatch && statusMatch;
  });
});

const handleQuery = () => {};
const resetQuery = () => {
  filters.value = { code: '', name: '', status: null };
};
const handleAdd = () => {};
const handleSelect = (val: any) => {
  selectedRow.value = val;
};
</script>

<style scoped>
.ele-table :deep(.el-table__header) {
  --el-table-header-bg-color: #fafafa;
}
.dark .ele-table :deep(.el-table__header) {
  --el-table-header-bg-color: #1d1d1d;
}
</style>
