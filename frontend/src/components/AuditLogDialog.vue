<template>
  <div v-if="visible" class="dialog-overlay" @click.self="close">
    <div class="dialog dialog-lg">
      <div class="dialog-header">
        <h3>变更履历</h3>
        <button class="dialog-close" @click="close">&times;</button>
      </div>
      <div class="dialog-body">
        <!-- Filter -->
        <div class="filter-bar">
          <label>时间范围：</label>
          <input
            type="date"
            v-model="startTime"
            class="input"
          />
          <span>至</span>
          <input
            type="date"
            v-model="endTime"
            class="input"
          />
          <button class="btn" @click="fetchLogs">查询</button>
        </div>

        <!-- Log Table -->
        <table class="table">
          <thead>
            <tr>
              <th>操作类型</th>
              <th>操作人</th>
              <th>操作时间</th>
              <th>变更字段</th>
              <th>详情</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading">
              <td colspan="5" class="text-center">加载中...</td>
            </tr>
            <tr v-else-if="logs.length === 0">
              <td colspan="5" class="text-center">暂无变更记录</td>
            </tr>
            <tr v-for="log in logs" :key="log.id">
              <td>
                <span :class="['action-badge', getActionClass(log.action)]">
                  {{ getActionLabel(log.action) }}
                </span>
              </td>
              <td>{{ log.operatorName || '-' }}</td>
              <td>{{ formatDate(log.createdAt) }}</td>
              <td>{{ log.changedFields || '-' }}</td>
              <td>
                <button class="btn btn-sm" @click="showDetail(log)">查看</button>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- Pagination -->
        <div class="pagination" v-if="total > 0">
          <span class="pagination-info">
            共 {{ total }} 条，第 {{ page }} / {{ totalPages }} 页
          </span>
          <button class="btn btn-sm" :disabled="page <= 1" @click="goPage(1)">首页</button>
          <button class="btn btn-sm" :disabled="page <= 1" @click="goPage(page - 1)">上一页</button>
          <button class="btn btn-sm" :disabled="page >= totalPages" @click="goPage(page + 1)">下一页</button>
          <button class="btn btn-sm" :disabled="page >= totalPages" @click="goPage(totalPages)">末页</button>
        </div>
      </div>

      <!-- Detail Sub-Dialog -->
      <div v-if="detailVisible" class="dialog-overlay detail-overlay" @click.self="closeDetail">
        <div class="dialog">
          <div class="dialog-header">
            <h3>变更详情</h3>
            <button class="dialog-close" @click="closeDetail">&times;</button>
          </div>
          <div class="dialog-body">
            <div v-if="detailLog" class="detail-content">
              <div class="detail-row">
                <span class="detail-label">操作类型：</span>
                <span :class="['action-badge', getActionClass(detailLog.action)]">
                  {{ getActionLabel(detailLog.action) }}
                </span>
              </div>
              <div class="detail-row">
                <span class="detail-label">操作人：</span>
                <span>{{ detailLog.operatorName || '-' }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">操作时间：</span>
                <span>{{ formatDate(detailLog.createdAt) }}</span>
              </div>
              <div v-if="detailLog.changedFields" class="detail-row">
                <span class="detail-label">变更字段：</span>
                <span>{{ detailLog.changedFields }}</span>
              </div>
              <div v-if="detailLog.oldValue" class="detail-section">
                <span class="detail-label">变更前：</span>
                <pre class="json-block">{{ formatJson(detailLog.oldValue) }}</pre>
              </div>
              <div v-if="detailLog.newValue" class="detail-section">
                <span class="detail-label">变更后：</span>
                <pre class="json-block">{{ formatJson(detailLog.newValue) }}</pre>
              </div>
            </div>
          </div>
          <div class="dialog-footer">
            <button class="btn" @click="closeDetail">关闭</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
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

const totalPages = computed(() => Math.ceil(total.value / pageSize));

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

function closeDetail() {
  detailVisible.value = false;
  detailLog.value = null;
}

function getActionLabel(action: string): string {
  switch (action) {
    case 'CREATE': return '创建';
    case 'UPDATE': return '修改';
    case 'DELETE': return '删除';
    default: return action;
  }
}

function getActionClass(action: string): string {
  switch (action) {
    case 'CREATE': return 'action-create';
    case 'UPDATE': return 'action-update';
    case 'DELETE': return 'action-delete';
    default: return '';
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

<style scoped>
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
  width: 800px;
  max-width: 95vw;
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

.filter-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.filter-bar label {
  font-size: 14px;
  color: #333;
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

.btn-sm {
  padding: 2px 8px;
  font-size: 12px;
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

.text-center {
  text-align: center;
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

.action-badge {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.action-create {
  background: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.action-update {
  background: #e6f7ff;
  color: #1890ff;
  border: 1px solid #91d5ff;
}

.action-delete {
  background: #fff2f0;
  color: #ff4d4f;
  border: 1px solid #ffccc7;
}

.detail-overlay {
  z-index: 1100;
}

.detail-content {
  font-size: 14px;
}

.detail-row {
  margin-bottom: 8px;
}

.detail-label {
  font-weight: 600;
  color: #333;
}

.detail-section {
  margin-top: 12px;
}

.json-block {
  background: #f5f5f5;
  padding: 12px;
  border-radius: 4px;
  font-size: 12px;
  overflow-x: auto;
  max-height: 300px;
  overflow-y: auto;
  margin-top: 4px;
}
</style>
