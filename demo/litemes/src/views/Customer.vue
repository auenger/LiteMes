<template>
  <div class="h-full flex flex-col space-y-3">
    <!-- Filter Card -->
    <div class="bg-card p-3 border border-border-color shadow-sm rounded-sm shrink-0">
      <el-form :inline="true" :model="filters" size="default" class="flex flex-wrap gap-y-3 items-center !mb-0">
        <el-form-item label="客户编码" class="!mb-0">
          <el-input v-model="filters.code" placeholder="请输入" clearable class="!w-40" />
        </el-form-item>
        <el-form-item label="客户名称" class="!mb-0">
          <el-input v-model="filters.name" placeholder="请输入" clearable class="!w-56" />
        </el-form-item>
        <el-form-item class="!mb-0 !mr-0">
          <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- Main Content Card -->
    <div class="flex-1 bg-card border border-border-color shadow-sm rounded-sm flex flex-col overflow-hidden">
      <div class="px-4 py-2.5 border-b border-border-color flex justify-between items-center shrink-0 bg-slate-50/50 dark:bg-white/5">
        <div class="flex gap-2">
          <el-button type="primary" :icon="Plus">新建客户</el-button>
          <el-button :icon="Download">导出报告</el-button>
        </div>
        <div class="flex items-center gap-3 text-muted">
          <el-icon class="cursor-pointer text-lg hover:text-primary transition-colors" @click="handleQuery"><Refresh /></el-icon>
          <el-icon class="cursor-pointer text-lg hover:text-primary transition-colors"><Operation /></el-icon>
          <el-icon class="cursor-pointer text-lg hover:text-primary transition-colors"><Setting /></el-icon>
        </div>
      </div>

      <div class="flex-1 p-2.5 overflow-hidden">
        <el-table :data="filteredData" height="100%" border stripe class="ele-table">
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="code" label="客户编码" width="120" />
          <el-table-column prop="name" label="客户名称" min-width="180" show-overflow-tooltip />
          <el-table-column prop="shortName" label="简称" width="100" />
          <el-table-column prop="type" label="类型" width="120" align="center" />
          <el-table-column prop="contact" label="联系人" width="100" />
          <el-table-column prop="phone" label="联系电话" width="140" />
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status ? 'success' : 'info'" size="small">
                {{ row.status ? '正常' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right" align="center">
            <template #default>
              <div class="flex items-center justify-center gap-2">
                <el-button link type="primary" size="small" :icon="Edit">编辑</el-button>
                <el-button link type="danger" size="small" :icon="Delete">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
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
import { Search, Refresh, Plus, Download, Edit, Delete, Setting, Operation } from '@element-plus/icons-vue';

const store = useMasterDataStore();
const filters = ref({ code: '', name: '' });

const filteredData = computed(() => {
  return store.customers.filter(item => {
    return (!filters.value.code || item.code.includes(filters.value.code)) &&
           (!filters.value.name || item.name.includes(filters.value.name));
  });
});

const handleQuery = () => {};
const resetQuery = () => { filters.value = { code: '', name: '' }; };
</script>

<style scoped>
.ele-table :deep(.el-table__header) {
  --el-table-header-bg-color: #fafafa;
}
.dark .ele-table :deep(.el-table__header) {
  --el-table-header-bg-color: #1d1d1d;
}
</style>
