<template>
  <div class="tree-node">
    <div
      :class="['tree-node-content', { 'tree-node-selected': isSelected }]"
      @click="selectNode"
    >
      <span
        v-if="hasChildren"
        class="tree-toggle"
        @click.stop="toggleExpand"
      >
        {{ expanded ? '▼' : '▶' }}
      </span>
      <span v-else class="tree-toggle-placeholder"></span>
      <span class="tree-node-label">{{ node.categoryName }}</span>
      <span class="tree-node-code">{{ node.categoryCode }}</span>
    </div>
    <div v-if="expanded && hasChildren" class="tree-children">
      <TreeNode
        v-for="child in node.children"
        :key="child.id"
        :node="child"
        :selectedId="selectedId"
        @select="$emit('select', $event)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';

export interface TreeNodeData {
  id: number;
  categoryCode: string;
  categoryName: string;
  parentId: number | null;
  status: number;
  children: TreeNodeData[];
}

const props = defineProps<{
  node: TreeNodeData;
  selectedId: number | null;
}>();

const emit = defineEmits<{
  select: [id: number];
}>();

const expanded = ref(true);

const hasChildren = computed(() => props.node.children && props.node.children.length > 0);

const isSelected = computed(() => props.selectedId === props.node.id);

function toggleExpand() {
  expanded.value = !expanded.value;
}

function selectNode() {
  emit('select', props.node.id);
}
</script>

<style scoped>
.tree-node {
  user-select: none;
}

.tree-node-content {
  display: flex;
  align-items: center;
  padding: 4px 8px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.15s;
  gap: 4px;
}

.tree-node-content:hover {
  background-color: #f0f5ff;
}

.tree-node-selected {
  background-color: #e6f4ff;
  color: #1890ff;
  font-weight: 500;
}

.tree-toggle {
  font-size: 10px;
  width: 16px;
  text-align: center;
  flex-shrink: 0;
  color: #999;
}

.tree-toggle-placeholder {
  width: 16px;
  flex-shrink: 0;
}

.tree-node-label {
  font-size: 13px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.tree-node-code {
  font-size: 11px;
  color: #999;
  margin-left: auto;
  flex-shrink: 0;
}

.tree-children {
  padding-left: 20px;
}
</style>
