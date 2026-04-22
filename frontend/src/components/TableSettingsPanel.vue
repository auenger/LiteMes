<template>
  <el-popover
    :visible="visible"
    placement="bottom-end"
    :width="240"
    trigger="click"
    @update:visible="$emit('close')"
  >
    <template #reference>
      <el-button :icon="Setting" size="default">表格设置</el-button>
    </template>
    <div class="py-2">
      <div
        v-for="col in columns"
        :key="col.key"
        class="px-2 py-1"
      >
        <el-checkbox
          :model-value="col.visible"
          @change="$emit('toggle', col.key)"
        >
          {{ col.label }}
        </el-checkbox>
      </div>
    </div>
    <div class="border-t pt-2 text-right">
      <el-button size="small" @click="$emit('reset')">恢复默认</el-button>
    </div>
  </el-popover>
</template>

<script setup lang="ts">
import { Setting } from '@element-plus/icons-vue';
import type { ColumnDef } from './useTableSettings';

defineProps<{
  visible: boolean;
  columns: ColumnDef[];
}>();

defineEmits<{
  (e: 'close'): void;
  (e: 'toggle', key: string): void;
  (e: 'reset'): void;
}>();
</script>
