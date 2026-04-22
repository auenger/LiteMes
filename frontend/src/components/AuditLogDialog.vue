<template>
  <el-dialog
    :model-value="visible"
    title="变更履历"
    width="800px"
    @close="close"
  >
    <!-- Filter -->
    <el-form :inline="true" class="mb-4">
      <el-form-item label="时间范围">
        <el-date-picker
          v-model="startTime"
          type="date"
          placeholder="开始日期"
          value-format="YYYY-MM-DD"
          clearable
        />
        <span class="mx-2">至</span>
        <el-date-picker
          v-model="endTime"
          type="date"
          placeholder="结束日期"
          value-format="YYYY-MM-DD"
          clearable
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="fetchLogs">查询</el-button>
      </el-form-item>
    </el-form>

    <!-- Log Table -->
    <el-table :data="logs" border stripe v-loading="loading">
      <el-table-column prop="action" label="操作类型" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="getActionTagType(row.action)" size="small">
            {{ getActionLabel(row.action) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="operatorName" label="操作人" width="120">
        <template #default="{ row }">{{ row.operatorName || '-' }}</template>
      </el-table-column>
      <el-table-column prop="createdAt" label="操作时间" width="180">
        <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column prop="changedFields" label="变更字段" min-width="120">
        <template #default="{ row }">{{ row.changedFields || '-' }}</template>
      </el-table-column>
      <el-table-column label="详情" width="80" align="center">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="showDetail(row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Pagination -->
    <div v-if="total > 0" class="flex items-center justify-end mt-4">
      <el-pagination
        layout="total, sizes, prev, pager, next"
        :total="total"
        :page-size="pageSize"
        :current-page="page"
        background
        size="small"
        @current-change="goPage"
      />
    </div>

    <!-- Detail Sub-Dialog -->
    <el-dialog
      v-model="detailVisible"
      title="变更详情"
      width="600px"
      append-to-body
    >
      <div v-if="detailLog" class="text-sm">
        <div class="mb-2">
          <span class="font-semibold">操作类型：</span>
          <el-tag :type="getActionTagType(detailLog.action)" size="small">
            {{ getActionLabel(detailLog.action) }}
          </el-tag>
        </div>
        <div class="mb-2">
          <span class="font-semibold">操作人：</span>
          <span>{{ detailLog.operatorName || '-' }}</span>
        </div>
        <div class="mb-2">
          <span class="font-semibold">操作时间：</span>
          <span>{{ formatDate(detailLog.createdAt) }}</span>
        </div>
        <div v-if="detailLog.changedFields" class="mb-2">
          <span class="font-semibold">变更字段：</span>
          <span>{{ detailLog.changedFields }}</span>
        </div>
        <div v-if="detailLog.oldValue" class="mt-3">
          <span class="font-semibold">变更前：</span>
          <pre class="bg-gray-50 p-3 rounded text-xs overflow-auto max-h-72 mt-1">{{ formatJson(detailLog.oldValue) }}</pre>
        </div>
        <div v-if="detailLog.newValue" class="mt-3">
          <span class="font-semibold">变更后：</span>
          <pre class="bg-gray-50 p-3 rounded text-xs overflow-auto max-h-72 mt-1">{{ formatJson(detailLog.newValue) }}</pre>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { Search } from '@element-plus/icons-vue';
import {
  listAuditLogs,
  type AuditLogDto,
} from '../api/auditLog';

const props = defineProps<{
  visible: boolean;
  tableName: string;
  recordId: number;
}>();

const emit = defineEmits<{
  (e: 'update:visible', val: boolean): void;
  (e: 'close'): void;
}>();

const logs = ref<AuditLogDto[]>([]);
const total = ref(0);
const loading = ref(false);
const page = ref(1);
const pageSize = 10;
const startTime = ref('');
const endTime = ref('');

const detailVisible = ref(false);
const detailLog = ref<AuditLogDto | null>(null);

watch(() => props.visible, (val) => {
  if (val) {
    page.value = 1;
    startTime.value = '';
    endTime.value = '';
    fetchLogs();
  }
});

function close() {
  emit('update:visible', false);
  emit('close');
}

async function fetchLogs() {
  loading.value = true;
  try {
    const res = await listAuditLogs({
      tableName: props.tableName,
      recordId: props.recordId,
      startTime: startTime.value ? `${startTime.value} 00:00:00` : undefined,
      endTime: endTime.value ? `${endTime.value} 23:59:59` : undefined,
      page: page.value,
      size: pageSize,
    });
    if (res.code === 200 && res.data) {
      logs.value = res.data.records;
      total.value = res.data.total;
    }
  } catch (e) {
    console.error('Failed to fetch audit logs', e);
  } finally {
    loading.value = false;
  }
}

function goPage(p: number) {
  page.value = p;
  fetchLogs();
}

function showDetail(log: AuditLogDto) {
  detailLog.value = log;
  detailVisible.value = true;
}

function getActionLabel(action: string): string {
  switch (action) {
    case 'CREATE': return '创建';
    case 'UPDATE': return '修改';
    case 'DELETE': return '删除';
    default: return action;
  }
}

function getActionTagType(action: string): 'success' | 'primary' | 'danger' | 'info' {
  switch (action) {
    case 'CREATE': return 'success';
    case 'UPDATE': return 'primary';
    case 'DELETE': return 'danger';
    default: return 'info';
  }
}

function formatDate(dateStr: string | undefined): string {
  if (!dateStr) return '-';
  return new Date(dateStr).toLocaleString('zh-CN');
}

function formatJson(jsonStr: string | null): string {
  if (!jsonStr) return '';
  try {
    return JSON.stringify(JSON.parse(jsonStr), null, 2);
  } catch {
    return jsonStr;
  }
}
</script>
