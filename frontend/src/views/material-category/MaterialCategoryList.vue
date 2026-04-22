<template>
  <div class="h-full flex flex-col space-y-3">
    <!-- Filter Card -->
    <div class="bg-card p-3 border border-border-color shadow-sm rounded-sm shrink-0">
      <el-form :inline="true" :model="query" size="default" class="flex flex-wrap gap-y-3 items-center !mb-0">
        <el-form-item label="分类编码" class="!mb-0">
          <el-input v-model="query.categoryCode" placeholder="分类编码" clearable class="!w-40" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="分类名称" class="!mb-0">
          <el-input v-model="query.categoryName" placeholder="分类名称" clearable class="!w-40" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="状态" class="!mb-0">
          <el-select v-model="query.status" placeholder="全部状态" clearable class="!w-32">
            <el-option :value="1" label="启用" />
            <el-option :value="0" label="禁用" />
          </el-select>
        </el-form-item>
        <el-form-item class="!mb-0 !mr-0">
          <el-button type="primary" :icon="Search" @click="search">查询</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- Main Content: Tree + Table -->
    <div class="flex-1 flex gap-4 overflow-hidden">
      <!-- Left: Tree Panel -->
      <div class="w-64 shrink-0 bg-card border border-border-color shadow-sm rounded-sm flex flex-col overflow-hidden">
        <div class="px-4 py-2.5 border-b border-border-color bg-gray-50 shrink-0">
          <span class="text-sm font-semibold text-gray-700">分类结构</span>
        </div>
        <div class="flex-1 p-2 overflow-y-auto">
          <div v-if="treeLoading" class="text-center text-gray-400 text-sm py-4">加载中...</div>
          <div v-else-if="treeData.length === 0" class="text-center text-gray-400 text-sm py-4">暂无分类数据</div>
          <div v-else>
            <div
              v-for="node in treeData"
              :key="node.id"
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

      <!-- Right: Table Panel -->
      <div class="flex-1 bg-card border border-border-color shadow-sm rounded-sm flex flex-col overflow-hidden">
        <div class="px-4 py-2.5 border-b border-border-color flex justify-between items-center shrink-0">
          <div class="flex gap-2">
            <el-button type="primary" :icon="Plus" @click="openCreateDialog">新建分类</el-button>
            <div class="relative">
              <el-button :icon="Setting" @click="showTableSettings = !showTableSettings">表格设置</el-button>
              <TableSettingsPanel
                :visible="showTableSettings"
                :columns="columns"
                @close="showTableSettings = false"
                @toggle="toggleColumn"
                @reset="resetSettings"
              />
            </div>
          </div>
        </div>
        <div class="flex-1 p-2.5 overflow-hidden">
          <el-table :data="categories" border stripe height="100%" v-loading="loading">
            <el-table-column prop="categoryCode" label="分类编码" min-width="120" />
            <el-table-column prop="categoryName" label="分类名称" min-width="120" />
            <el-table-column prop="isQualityCategory" label="是否质量分类" min-width="110">
              <template #default="{ row }">
                <el-tag :type="row.isQualityCategory ? 'success' : 'info'" size="small">
                  {{ row.isQualityCategory ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="parentName" label="上级分类" min-width="120">
              <template #default="{ row }">{{ row.parentName || '顶级分类' }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" min-width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                  {{ row.status === 1 ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdBy" label="创建人" min-width="100">
              <template #default="{ row }">{{ row.createdBy || '-' }}</template>
            </el-table-column>
            <el-table-column prop="createdAt" label="创建时间" min-width="160">
              <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
            </el-table-column>
            <el-table-column label="操作" min-width="300" fixed="right">
              <template #default="{ row }">
                <div class="flex gap-1">
                  <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
                  <el-button
                    v-if="row.status === 1"
                    size="small"
                    type="warning"
                    @click="toggleStatus(row)"
                  >禁用</el-button>
                  <el-button
                    v-else
                    size="small"
                    type="success"
                    @click="toggleStatus(row)"
                  >启用</el-button>
                  <el-button size="small" type="danger" @click="confirmDelete(row)">删除</el-button>
                  <el-button size="small" type="info" @click="openAuditLog(row)">变更履历</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="px-4 py-3 border-t border-border-color flex justify-end shrink-0">
          <el-pagination
            layout="total, sizes, prev, pager, next"
            :total="total"
            :page-size="query.size"
            :current-page="query.page"
            background
            size="small"
            @current-change="goPage"
            @size-change="(size: number) => { query.size = size; search() }"
          />
        </div>
      </div>
    </div>

    <!-- Create/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑物料分类' : '新建物料分类'"
      width="480px"
      destroy-on-close
    >
      <el-form label-width="100px">
        <el-form-item label="分类编码" required>
          <el-input v-model="form.categoryCode" placeholder="请输入分类编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="分类名称" required>
          <el-input v-model="form.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="是否质量分类">
          <el-select v-model="form.isQualityCategory" class="!w-full">
            <el-option :value="false" label="否" />
            <el-option :value="true" label="是" />
          </el-select>
        </el-form-item>
        <el-form-item label="上级分类">
          <el-select v-model="form.parentId" placeholder="无（顶级分类）" clearable class="!w-full">
            <el-option v-for="c in parentCategoryOptions" :key="c.id" :value="c.id" :label="`${c.name} (${c.code})`" />
          </el-select>
        </el-form-item>
      </el-form>
      <div v-if="formError" class="text-red-500 text-xs px-4 pb-2">{{ formError }}</div>
      <template #footer>
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- Delete Confirm Dialog -->
    <el-dialog
      v-model="deleteDialogVisible"
      title="确认删除"
      width="420px"
      destroy-on-close
    >
      <p>确定要删除分类 "{{ deleteTarget?.categoryName }}" ({{ deleteTarget?.categoryCode }}) 吗？</p>
      <p class="text-gray-400 text-sm mt-2">删除后不可恢复。若该分类下存在子分类或已被引用，则无法删除。</p>
      <template #footer>
        <el-button @click="closeDeleteDialog">取消</el-button>
        <el-button type="danger" @click="doDelete" :loading="submitting">删除</el-button>
      </template>
    </el-dialog>

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
import { Search, Refresh, Plus, Setting } from '@element-plus/icons-vue';
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
