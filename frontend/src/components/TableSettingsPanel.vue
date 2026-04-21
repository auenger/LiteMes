<template>
  <div v-if="visible" class="settings-panel">
    <div class="settings-header">
      <h4>表格设置</h4>
      <button class="settings-close" @click="$emit('close')">&times;</button>
    </div>
    <div class="settings-body">
      <div
        v-for="col in columns"
        :key="col.key"
        class="column-item"
      >
        <label class="column-checkbox">
          <input
            type="checkbox"
            :checked="col.visible"
            @change="toggleColumn(col.key)"
          />
          <span>{{ col.label }}</span>
        </label>
      </div>
    </div>
    <div class="settings-footer">
      <button class="btn btn-sm" @click="resetSettings">恢复默认</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ColumnDef } from './useTableSettings';

defineProps<{
  visible: boolean;
  columns: ColumnDef[];
}>();

const emit = defineEmits<{
  (e: 'close'): void;
}>();

function toggleColumn(key: string) {
  // This is handled by the parent component via useTableSettings
}

function resetSettings() {
  // This is handled by the parent component via useTableSettings
}
</script>

<script lang="ts">
// Re-export for parent use
export default {
  name: 'TableSettingsPanel',
};
</script>

<style scoped>
.settings-panel {
  position: absolute;
  top: 40px;
  right: 0;
  background: #fff;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  width: 240px;
  z-index: 100;
}

.settings-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
}

.settings-header h4 {
  margin: 0;
  font-size: 14px;
  color: #333;
}

.settings-close {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  color: #999;
}

.settings-body {
  padding: 8px 16px;
  max-height: 300px;
  overflow-y: auto;
}

.column-item {
  padding: 6px 0;
}

.column-checkbox {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  cursor: pointer;
  color: #333;
}

.column-checkbox input[type="checkbox"] {
  cursor: pointer;
}

.settings-footer {
  padding: 8px 16px;
  border-top: 1px solid #f0f0f0;
  text-align: right;
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

.btn-sm {
  padding: 2px 8px;
  font-size: 12px;
}
</style>
