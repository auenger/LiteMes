<template>
  <div class="h-full overflow-y-auto space-y-4 pb-4">
    <!-- Stat Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-3">
      <div v-for="stat in stats" :key="stat.title" class="bg-card p-3.5 border border-border-color shadow-sm rounded-sm transition-all hover:shadow-md group cursor-pointer">
        <div class="flex items-center justify-between">
          <div>
            <div class="text-[11px] text-muted font-medium mb-1">{{ stat.title }}</div>
            <div class="text-xl font-bold text-slate-800 dark:text-slate-100">{{ stat.value }}</div>
          </div>
          <div :class="`w-10 h-10 rounded-sm ${stat.bgColor} flex items-center justify-center text-lg transition-transform`">
            <el-icon :class="stat.color"><component :is="stat.icon" /></el-icon>
          </div>
        </div>
        <div class="mt-3 flex items-center text-[11px]">
          <span class="text-emerald-500 font-bold flex items-center mr-2">
            <el-icon><CaretTop /></el-icon> {{ stat.trend }}
          </span>
          <span class="text-muted">较上周</span>
        </div>
      </div>
    </div>

    <!-- Charts / Tables Section -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-4">
      <!-- Recent Materials Table -->
      <div class="lg:col-span-2 bg-card border border-border-color shadow-sm rounded-sm flex flex-col overflow-hidden">
        <div class="px-4 py-2.5 border-b border-border-color flex justify-between items-center bg-slate-50/50 dark:bg-white/5">
          <h3 class="text-xs font-bold flex items-center gap-2">
            <el-icon class="text-primary"><List /></el-icon>
            最近更新物料
          </h3>
          <el-button link type="primary" size="small" @click="$router.push('/material')">详情</el-button>
        </div>
        <div class="p-2.5">
          <el-table :data="masterData.materials.slice(0, 5)" style="width: 100%" size="small">
            <el-table-column prop="code" label="编码" width="120" />
            <el-table-column prop="name" label="名称" show-overflow-tooltip />
            <el-table-column prop="category" label="分类" width="100" align="center" />
            <el-table-column label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status ? 'success' : 'info'" size="small">
                  {{ row.status ? '在线' : '停用' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <!-- System Logs -->
      <div class="bg-card border border-border-color shadow-sm rounded-sm flex flex-col overflow-hidden">
        <div class="px-4 py-2.5 border-b border-border-color flex justify-between items-center bg-slate-50/50 dark:bg-white/5">
          <h3 class="text-xs font-bold flex items-center gap-2">
            <el-icon class="text-orange-500"><Bell /></el-icon>
            系统动态
          </h3>
          <el-icon class="text-muted cursor-pointer hover:text-primary"><MoreFilled /></el-icon>
        </div>
        <div class="p-3 flex-1 overflow-y-auto max-h-[250px] custom-scroll">
          <div v-for="log in logs" :key="log.id" class="flex gap-3 items-start pb-3 border-b border-border-color/50 last:border-0 last:pb-0 mb-3 last:mb-0 group">
            <div :class="`w-7 h-7 rounded-sm shrink-0 flex items-center justify-center ${log.type === 'error' ? 'bg-red-50 text-red-500' : 'bg-blue-50 text-blue-500'}`">
              <el-icon :size="12"><component :is="log.type === 'error' ? 'Warning' : 'InfoFilled'" /></el-icon>
            </div>
            <div class="flex-1">
              <div class="text-[11px] font-medium group-hover:text-primary transition-colors line-clamp-1 leading-tight">{{ log.message }}</div>
              <div class="text-[9px] text-muted mt-0.5">{{ log.time }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useMasterDataStore } from '../stores/masterData';
import { 
  Box, User, OfficeBuilding, List, Bell, Warning, InfoFilled, 
  CaretTop, MoreFilled
} from '@element-plus/icons-vue';

const masterData = useMasterDataStore();

const stats = [
  { title: '物料总数', value: '1,280', icon: Box, color: 'text-blue-500', bgColor: 'bg-blue-50', trend: '12%' },
  { title: '活跃客户', value: '456', icon: User, color: 'text-emerald-500', bgColor: 'bg-emerald-50', trend: '5%' },
  { title: '工厂负荷', value: '82%', icon: OfficeBuilding, color: 'text-orange-500', bgColor: 'bg-orange-50', trend: '3%' },
  { title: '待处理告警', value: '2', icon: Warning, color: 'text-red-500', bgColor: 'bg-red-50', trend: '-2%' },
];

const logs = [
  { id: 1, type: 'info', message: 'luoxueting 成功导入了 50 条物料主数据', time: '10分钟前' },
  { id: 2, type: 'error', message: '苏州工厂服务器连接超时，自动重连中...', time: '25分钟前' },
  { id: 3, type: 'info', message: '核心业务引擎更新至 v2.4.1 版本', time: '1小时前' },
  { id: 4, type: 'info', message: '新客户 [华为技术] 已完成资料审核', time: '2小时前' },
  { id: 5, type: 'info', message: '系统自动备份完成 (Path: /snap/bak/0420)', time: '3小时前' },
];
</script>
