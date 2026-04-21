<template>
  <div class="category-page">
    <div class="page-header">
      <h2>物料分类管理</h2>
      <div class="header-actions">
        <div class="table-settings-wrapper" style="position: relative;">
          <button class="btn" @click="showTableSettings = !showTableSettings">表格设置</button>
          <TableSettingsPanel
            :visible="showTableSettings"
            :columns="columns"
            @close="showTableSettings = false"
          />
        </div>
        <button class="btn btn-primary" @click="openCreateDialog">新建分类</button>
      </div>
    </div>

    <div class="main-content">
      <!-- Left: Tree Panel -->
      <div class="tree-panel">
        <div class="tree-header">
          <h3>分类结构</h3>
        </div>
        <div class="tree-body">
          <div v-if="treeLoading" class="text-center">加载中...</div>
          <div v-else-if="treeData.length === 0" class="text-center text-muted">暂无分类数据</div>
          <div v-else>
            <div
              v-for="node in treeData"
              :key="node.id"
              class="tree-node-wrapper"
            >
              <TreeNode
                :node="node"
                :selectedId="selectedCategoryId"
                @select="onTreeSelect"
              />
            </div>
          </div>
        </div>
      </div>

      <!-- Right: Detail / List Panel -->
      <div class="detail-panel">
        <!-- Search Bar -->
        <div class="search-bar">
          <input
            v-model="query.categoryCode"
            type="text"
            placeholder="分类编码"
            class="input"
            @keyup.enter="search"
          />
          <input
            v-model="query.categoryName"
            type="text"
            placeholder="分类名称"
            class="input"
            @keyup.enter="search"
          />
          <select v-model="query.status" class="input select">
            <option :value="undefined">全部状态</option>
            <option :value="1">启用</option>
            <option :value="0">禁用</option>
          </select>
          <button class="btn" @click="search">查询</button>
          <button class="btn btn-secondary" @click="resetQuery">重置</button>
        </div>

        <!-- Category Table -->
        <table class="table">
          <thead>
            <tr>
              <th>分类编码</th>
              <th>分类名称</th>
              <th>是否质量分类</th>
              <th>上级分类</th>
              <th>状态</th>
              <th>创建人</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading">
              <td colspan="8" class="text-center">加载中...</td>
            </tr>
            <tr v-else-if="categories.length === 0">
              <td colspan="8" class="text-center">暂无数据</td>
            </tr>
            <tr v-for="cat in categories" :key="cat.id">
              <td>{{ cat.categoryCode }}</td>
              <td>{{ cat.categoryName }}</td>
              <td>
                <span :class="['status-badge', cat.isQualityCategory ? 'status-enabled' : 'status-disabled']">
                  {{ cat.isQualityCategory ? '是' : '否' }}
                </span>
              </td>
              <td>{{ cat.parentName || '顶级分类' }}</td>
              <td>
                <span :class="['status-badge', cat.status === 1 ? 'status-enabled' : 'status-disabled']">
                  {{ cat.status === 1 ? '启用' : '禁用' }}
                </span>
              </td>
              <td>{{ cat.createdBy || '-' }}</td>
              <td>{{ formatDate(cat.createdAt) }}</td>
              <td class="actions">
                <button class="btn btn-sm" @click="openEditDialog(cat)">编辑</button>
                <button
                  v-if="cat.status === 1"
                  class="btn btn-sm btn-warning"
                  @click="toggleStatus(cat)"
                >
                  禁用
                </button>
                <button
                  v-else
                  class="btn btn-sm btn-success"
                  @click="toggleStatus(cat)"
                >
                  启用
                </button>
                <button class="btn btn-sm btn-danger" @click="confirmDelete(cat)">删除</button>
                <button class="btn btn-sm btn-audit" @click="openAuditLog(cat)">变更履历</button>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- Pagination -->
        <div class="pagination" v-if="total > 0">
          <span class="pagination-info">
            共 {{ total }} 条，第 {{ query.page || 1 }} / {{ totalPages }} 页
          </span>
          <button class="btn btn-sm" :disabled="query.page <= 1" @click="goPage(1)">首页</button>
          <button class="btn btn-sm" :disabled="query.page <= 1" @click="goPage((query.page || 1) - 1)">上一页</button>
          <button class="btn btn-sm" :disabled="query.page >= totalPages" @click="goPage((query.page || 1) + 1)">下一页</button>
          <button class="btn btn-sm" :disabled="query.page >= totalPages" @click="goPage(totalPages)">末页</button>
        </div>
      </div>
    </div>

    <!-- Create/Edit Dialog -->
    <div v-if="dialogVisible" class="dialog-overlay" @click.self="closeDialog">
      <div class="dialog">
        <div class="dialog-header">
          <h3>{{ isEdit ? '编辑物料分类' : '新建物料分类' }}</h3>
          <button class="dialog-close" @click="closeDialog">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label>分类编码 <span class="required">*</span></label>
            <input
              v-model="form.categoryCode"
              type="text"
              class="input"
              :disabled="isEdit"
              :class="{ 'input-disabled': isEdit }"
              placeholder="请输入分类编码"
            />
          </div>
          <div class="form-group">
            <label>分类名称 <span class="required">*</span></label>
            <input
              v-model="form.categoryName"
              type="text"
              class="input"
              placeholder="请输入分类名称"
            />
          </div>
          <div class="form-group">
            <label>是否质量分类</label>
            <select v-model="form.isQualityCategory" class="input">
              <option :value="false">否</option>
              <option :value="true">是</option>
            </select>
          </div>
          <div class="form-group">
            <label>上级分类</label>
            <select v-model="form.parentId" class="input">
              <option :value="null">无（顶级分类）</option>
              <option v-for="c in parentCategoryOptions" :key="c.id" :value="c.id">{{ c.name }} ({{ c.code }})</option>
            </select>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn" @click="closeDialog">取消</button>
          <button class="btn btn-primary" @click="submitForm" :disabled="submitting">
            {{ submitting ? '提交中...' : '确定' }}
          </button>
        </div>
        <div v-if="formError" class="form-error">{{ formError }}</div>
      </div>
    </div>

    <!-- Delete Confirm Dialog -->
    <div v-if="deleteDialogVisible" class="dialog-overlay" @click.self="closeDeleteDialog">
      <div class="dialog dialog-sm">
        <div class="dialog-header">
          <h3>确认删除</h3>
          <button class="dialog-close" @click="closeDeleteDialog">&times;</button>
        </div>
        <div class="dialog-body">
          <p>确定要删除分类 "{{ deleteTarget?.categoryName }}" ({{ deleteTarget?.categoryCode }}) 吗？</p>
          <p class="text-muted">删除后不可恢复。若该分类下存在子分类或已被引用，则无法删除。</p>
        </div>
        <div class="dialog-footer">
          <button class="btn" @click="closeDeleteDialog">取消</button>
          <button class="btn btn-danger" @click="doDelete" :disabled="submitting">
            {{ submitting ? '删除中...' : '删除' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Audit Log Dialog -->
    <AuditLogDialog
      :visible="auditLogVisible"
      :tableName="auditLogTableName"
      :recordId="auditLogRecordId"
      @close="auditLogVisible = false"
      @update:visible="auditLogVisible = $event"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import {
  listMaterialCategories,
  getMaterialCategoryTree,
  createMaterialCategory,
  updateMaterialCategory,
  deleteMaterialCategory,
  updateMaterialCategoryStatus,
  type MaterialCategoryDto,
  type MaterialCategoryTreeDto,
  type MaterialCategoryQueryParams,
} from '../../api/materialCategory';
import { getMaterialCategoryDropdown, type DropdownItem } from '../../api/dropdown';
import AuditLogDialog from '../../components/AuditLogDialog.vue';
import TableSettingsPanel from '../../components/TableSettingsPanel.vue';
import { useTableSettings, type ColumnDef } from '../../components/useTableSettings';
import TreeNode from '../../components/TreeNode.vue';

const categories = ref<MaterialCategoryDto[]>([]);
const total = ref(0);
const loading = ref(false);
const treeLoading = ref(false);
const submitting = ref(false);
const formError = ref('');
const treeData = ref<MaterialCategoryTreeDto[]>([]);
const selectedCategoryId = ref<number | null>(null);
const parentCategoryOptions = ref<DropdownItem[]>([]);

const query = reactive<MaterialCategoryQueryParams>({
  categoryCode: '',
  categoryName: '',
  status: undefined,
  page: 1,
  size: 10,
});

const totalPages = computed(() => Math.ceil(total.value / (query.size || 10)));

// Table settings
const defaultColumns: ColumnDef[] = [
  { key: 'categoryCode', label: '分类编码' },
  { key: 'categoryName', label: '分类名称' },
  { key: 'isQualityCategory', label: '是否质量分类' },
  { key: 'parentName', label: '上级分类' },
  { key: 'status', label: '状态' },
  { key: 'createdBy', label: '创建人' },
  { key: 'createdAt', label: '创建时间' },
];

const { columns, toggleColumn, resetSettings } = useTableSettings('material-category-list', defaultColumns);
const showTableSettings = ref(false);

// Audit log dialog state
const auditLogVisible = ref(false);
const auditLogTableName = ref('material_category');
const auditLogRecordId = ref(0);

function openAuditLog(cat: MaterialCategoryDto) {
  auditLogTableName.value = 'material_category';
  auditLogRecordId.value = cat.id;
  auditLogVisible.value = true;
}

// Dialog state
const dialogVisible = ref(false);
const isEdit = ref(false);
const editingId = ref<number | null>(null);
const form = reactive({
  categoryCode: '',
  categoryName: '',
  isQualityCategory: false as boolean,
  parentId: null as number | null,
});

// Delete dialog state
const deleteDialogVisible = ref(false);
const deleteTarget = ref<MaterialCategoryDto | null>(null);

async function fetchTree() {
  treeLoading.value = true;
  try {
    const res = await getMaterialCategoryTree();
    if (res.code === 200 && res.data) {
      treeData.value = res.data;
    }
  } catch (e) {
    console.error('Failed to fetch category tree', e);
  } finally {
    treeLoading.value = false;
  }
}

async function fetchParentOptions() {
  try {
    const res = await getMaterialCategoryDropdown();
    if (res.code === 200 && res.data) {
      parentCategoryOptions.value = res.data.filter(
        (c) => !isEdit.value || c.id !== editingId.value
      );
    }
  } catch (e) {
    console.error('Failed to fetch parent category options', e);
  }
}

async function fetchList() {
  loading.value = true;
  try {
    const res = await listMaterialCategories(query);
    if (res.code === 200 && res.data) {
      categories.value = res.data.records;
      total.value = res.data.total;
    }
  } catch (e) {
    console.error('Failed to fetch category list', e);
  } finally {
    loading.value = false;
  }
}

function onTreeSelect(id: number) {
  selectedCategoryId.value = id;
  // Load detail and set as filter
  query.categoryCode = '';
  query.categoryName = '';
  query.status = undefined;
  query.page = 1;
  // Fetch list filtered by selected category — just highlight in tree
  fetchList();
}

function search() {
  query.page = 1;
  fetchList();
}

function resetQuery() {
  query.categoryCode = '';
  query.categoryName = '';
  query.status = undefined;
  query.page = 1;
  selectedCategoryId.value = null;
  fetchList();
}

function goPage(page: number) {
  query.page = page;
  fetchList();
}

async function openCreateDialog() {
  isEdit.value = false;
  editingId.value = null;
  form.categoryCode = '';
  form.categoryName = '';
  form.isQualityCategory = false;
  form.parentId = null;
  formError.value = '';
  await fetchParentOptions();
  dialogVisible.value = true;
}

async function openEditDialog(cat: MaterialCategoryDto) {
  isEdit.value = true;
  editingId.value = cat.id;
  form.categoryCode = cat.categoryCode;
  form.categoryName = cat.categoryName;
  form.isQualityCategory = cat.isQualityCategory;
  form.parentId = cat.parentId;
  formError.value = '';
  await fetchParentOptions();
  dialogVisible.value = true;
}

function closeDialog() {
  dialogVisible.value = false;
}

async function submitForm() {
  if (!form.categoryCode && !isEdit.value) {
    formError.value = '分类编码不能为空';
    return;
  }
  if (!form.categoryName) {
    formError.value = '分类名称不能为空';
    return;
  }

  submitting.value = true;
  formError.value = '';
  try {
    if (isEdit.value && editingId.value) {
      await updateMaterialCategory(editingId.value, {
        categoryName: form.categoryName,
        isQualityCategory: form.isQualityCategory,
        parentId: form.parentId,
      });
    } else {
      await createMaterialCategory({
        categoryCode: form.categoryCode,
        categoryName: form.categoryName,
        isQualityCategory: form.isQualityCategory || undefined,
        parentId: form.parentId || undefined,
      });
    }
    closeDialog();
    fetchList();
    fetchTree();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '操作失败';
    formError.value = msg;
  } finally {
    submitting.value = false;
  }
}

function confirmDelete(cat: MaterialCategoryDto) {
  deleteTarget.value = cat;
  deleteDialogVisible.value = true;
}

function closeDeleteDialog() {
  deleteDialogVisible.value = false;
  deleteTarget.value = null;
}

async function doDelete() {
  if (!deleteTarget.value) return;
  submitting.value = true;
  try {
    await deleteMaterialCategory(deleteTarget.value.id);
    closeDeleteDialog();
    fetchList();
    fetchTree();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '删除失败';
    alert(msg);
  } finally {
    submitting.value = false;
  }
}

async function toggleStatus(cat: MaterialCategoryDto) {
  const newStatus = cat.status === 1 ? 0 : 1;
  try {
    await updateMaterialCategoryStatus(cat.id, newStatus);
    fetchList();
    fetchTree();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '状态更新失败';
    alert(msg);
  }
}

function formatDate(dateStr: string | undefined) {
  if (!dateStr) return '-';
  return new Date(dateStr).toLocaleString('zh-CN');
}

onMounted(() => {
  fetchTree();
  fetchList();
});
</script>

<style scoped>
.category-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.main-content {
  display: flex;
  gap: 16px;
}

.tree-panel {
  width: 260px;
  min-width: 260px;
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  background: #fff;
  max-height: calc(100vh - 160px);
  overflow-y: auto;
}

.tree-header {
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
}

.tree-header h3 {
  margin: 0;
  font-size: 14px;
  color: #333;
}

.tree-body {
  padding: 8px;
}

.detail-panel {
  flex: 1;
  min-width: 0;
}

.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
  flex-wrap: wrap;
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

.input-disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.select {
  min-width: 120px;
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

.btn-primary {
  background: #1890ff;
  color: #fff;
  border-color: #1890ff;
}

.btn-primary:hover {
  background: #40a9ff;
  color: #fff;
}

.btn-secondary {
  background: #f0f0f0;
  border-color: #d9d9d9;
}

.btn-sm {
  padding: 2px 8px;
  font-size: 12px;
}

.btn-danger {
  color: #ff4d4f;
  border-color: #ff4d4f;
}

.btn-danger:hover {
  background: #ff4d4f;
  color: #fff;
}

.btn-warning {
  color: #faad14;
  border-color: #faad14;
}

.btn-warning:hover {
  background: #faad14;
  color: #fff;
}

.btn-success {
  color: #52c41a;
  border-color: #52c41a;
}

.btn-success:hover {
  background: #52c41a;
  color: #fff;
}

.btn-info {
  color: #1890ff;
  border-color: #1890ff;
}

.btn-info:hover {
  background: #1890ff;
  color: #fff;
}

.btn-audit {
  color: #722ed1;
  border-color: #722ed1;
}

.btn-audit:hover {
  background: #722ed1;
  color: #fff;
}

.header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
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

.actions {
  display: flex;
  gap: 6px;
}

.status-badge {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-enabled {
  background: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.status-disabled {
  background: #fff2f0;
  color: #ff4d4f;
  border: 1px solid #ffccc7;
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

.text-center {
  text-align: center;
}

.text-muted {
  color: #999;
  font-size: 13px;
}

.required {
  color: #ff4d4f;
}

/* Dialog styles */
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

.dialog-sm {
  width: 400px;
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

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
  color: #333;
}

.form-group .input {
  width: 100%;
  box-sizing: border-box;
}

.form-error {
  padding: 8px 24px 16px;
  color: #ff4d4f;
  font-size: 13px;
}
</style>
