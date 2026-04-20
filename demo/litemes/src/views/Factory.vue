<template>
  <div class="h-full flex flex-col space-y-3">
    <!-- Filter Card -->
    <div class="bg-card p-3 border border-border-color shadow-sm rounded-sm shrink-0">
      <el-form :inline="true" :model="filters" size="default" class="flex flex-wrap gap-y-3 items-center !mb-0">
        <el-form-item label="工厂名称" class="!mb-0">
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
          <el-button type="primary" :icon="Plus">新建工厂</el-button>
          <el-button :icon="Download">导出</el-button>
        </div>
        <div class="flex items-center gap-3 text-muted">
          <el-icon class="cursor-pointer text-lg hover:text-primary transition-colors" @click="handleQuery"><Refresh /></el-icon>
          <el-icon class="cursor-pointer text-lg hover:text-primary transition-colors"><Operation /></el-icon>
          <el-icon class="cursor-pointer text-lg hover:text-primary transition-colors"><Setting /></el-icon>
        </div>
      </div>

      <div class="flex-1 p-2.5 overflow-hidden">
        <el-table :data="store.factories" height="100%" border stripe class="ele-table">
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="code" label="工厂编码" width="140" />
          <el-table-column prop="name" label="工厂全称" min-width="200" show-overflow-tooltip />
          <el-table-column prop="company" label="所属组织" min-width="200" />
          <el-table-column prop="creator" label="创建者" width="120" />
          <el-table-column prop="time" label="创建时间" width="180" />
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-switch v-model="row.status" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right" align="center">
            <template #default>
              <div class="flex items-center justify-center gap-2">
                <el-button link type="primary" size="small" :icon="Edit">编辑</el-button>
                <el-button link type="info" size="small" :icon="Document">日志</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="px-4 py-3 border-t border-border-color flex justify-end shrink-0">
        <el-pagination 
          layout="total, sizes, prev, pager, next, jumper" 
          :total="store.factories.length" 
          background
          size="small"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useMasterDataStore } from '../stores/masterData';
import { Search, Plus, Refresh, Download, Edit, Document, Setting, Operation } from '@element-plus/icons-vue';

const store = useMasterDataStore();
const filters = ref({ name: '' });
const handleQuery = () => {};
const resetQuery = () => { filters.value.name = ''; };
</script>

<style scoped>
.ele-table :deep(.el-table__header) {
  --el-table-header-bg-color: #fafafa;
}
.dark .ele-table :deep(.el-table__header) {
  --el-table-header-bg-color: #1d1d1d;
}
</style>
