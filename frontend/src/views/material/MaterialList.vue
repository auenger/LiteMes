<template>
  <div class="h-full flex flex-col space-y-3">
    <!-- Filter Card -->
    <div class="bg-card p-3 border border-border-color shadow-sm rounded-sm shrink-0">
      <el-form :inline="true" :model="query" size="default" class="flex flex-wrap gap-y-3 items-center !mb-0">
        <el-form-item label="物料编码" class="!mb-0">
          <el-input v-model="query.materialCode" placeholder="物料编码" clearable class="!w-40" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="物料名称" class="!mb-0">
          <el-input v-model="query.materialName" placeholder="物料名称" clearable class="!w-40" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="物料分类" class="!mb-0">
          <el-select v-model="query.categoryId" placeholder="全部物料分类" clearable class="!w-40">
            <el-option v-for="cat in categoryDropdown" :key="cat.id" :value="cat.id" :label="cat.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="基本分类" class="!mb-0">
          <el-select v-model="query.basicCategory" placeholder="全部基本分类" clearable class="!w-36">
            <el-option value="FINISHED_PRODUCT" label="成品" />
            <el-option value="SEMI_FINISHED" label="半成品" />
            <el-option value="RAW_MATERIAL" label="原材料" />
            <el-option value="SPARE_PART" label="备件" />
          </el-select>
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

    <!-- Main Content Card -->
    <div class="flex-1 bg-card border border-border-color shadow-sm rounded-sm flex flex-col overflow-hidden">
      <div class="px-4 py-2.5 border-b border-border-color flex justify-between items-center shrink-0">
        <div class="flex gap-2">
          <el-button type="primary" :icon="Plus" @click="openCreateDialog">新建物料</el-button>
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
        <el-table :data="materials" border stripe height="100%" v-loading="loading">
          <el-table-column v-if="isColumnVisible('materialCode')" prop="materialCode" label="物料编码" min-width="120" />
          <el-table-column v-if="isColumnVisible('materialName')" prop="materialName" label="物料名称" min-width="120" />
          <el-table-column v-if="isColumnVisible('basicCategory')" prop="basicCategory" label="基本分类" min-width="90">
            <template #default="{ row }">{{ formatBasicCategory(row.basicCategory) }}</template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('categoryName')" prop="categoryName" label="物料分类" min-width="100">
            <template #default="{ row }">{{ row.categoryName || '-' }}</template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('attributeCategory')" prop="attributeCategory" label="属性分类" min-width="110">
            <template #default="{ row }">{{ formatAttributeCategory(row.attributeCategory) }}</template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('uomName')" prop="uomName" label="单位" min-width="80">
            <template #default="{ row }">{{ row.uomName || '-' }}</template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('model')" prop="model" label="型号" min-width="100">
            <template #default="{ row }">{{ row.model || '-' }}</template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('specification')" prop="specification" label="规格" min-width="100">
            <template #default="{ row }">{{ row.specification || '-' }}</template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('status')" prop="status" label="状态" min-width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('createdBy')" prop="createdBy" label="创建人" min-width="100">
            <template #default="{ row }">{{ row.createdBy || '-' }}</template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('createdAt')" prop="createdAt" label="创建时间" min-width="160">
            <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
          </el-table-column>
          <el-table-column label="操作" min-width="320" fixed="right">
            <template #default="{ row }">
              <div class="flex gap-1">
                <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
                <el-button size="small" type="info" @click="openVersionDialog(row)">版本</el-button>
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

    <!-- Create/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑物料' : '新建物料'"
      width="800px"
      destroy-on-close
    >
      <el-form label-width="80px">
        <div class="grid grid-cols-3 gap-x-4 gap-y-2">
          <el-form-item label="物料编码" required>
            <el-input v-model="form.materialCode" placeholder="请输入物料编码" :disabled="isEdit" />
          </el-form-item>
          <el-form-item label="物料名称" required>
            <el-input v-model="form.materialName" placeholder="请输入物料名称" />
          </el-form-item>
          <el-form-item label="基本分类" required>
            <el-select v-model="form.basicCategory" placeholder="请选择" class="!w-full">
              <el-option value="FINISHED_PRODUCT" label="成品" />
              <el-option value="SEMI_FINISHED" label="半成品" />
              <el-option value="RAW_MATERIAL" label="原材料" />
              <el-option value="SPARE_PART" label="备件" />
            </el-select>
          </el-form-item>
          <el-form-item label="物料分类" required>
            <el-select v-model="form.categoryId" placeholder="请选择" class="!w-full">
              <el-option v-for="cat in categoryDropdown" :key="cat.id" :value="cat.id" :label="cat.name" />
            </el-select>
          </el-form-item>
          <el-form-item label="属性分类" required>
            <el-select v-model="form.attributeCategory" placeholder="请选择" class="!w-full">
              <el-option value="PURCHASED" label="采购件" />
              <el-option value="SELF_MANUFACTURED" label="自制件" />
              <el-option value="PURCHASED_AND_SELF" label="采购&自制件" />
            </el-select>
          </el-form-item>
          <el-form-item label="单位" required>
            <el-select v-model="form.uomId" placeholder="请选择" class="!w-full">
              <el-option v-for="uom in uomDropdown" :key="uom.id" :value="uom.id" :label="uom.name" />
            </el-select>
          </el-form-item>
        </div>

        <!-- PCB Attributes Section -->
        <div class="text-sm font-semibold text-gray-700 mt-4 mb-2 pb-1.5 border-b border-gray-200">PCB 属性</div>
        <div class="grid grid-cols-3 gap-x-4 gap-y-2">
          <el-form-item label="尺寸">
            <el-input-number v-model="form.size" :step="0.0001" :precision="4" controls-position="right" class="!w-full" />
          </el-form-item>
          <el-form-item label="长">
            <el-input-number v-model="form.length" :step="0.0001" :precision="4" controls-position="right" class="!w-full" />
          </el-form-item>
          <el-form-item label="宽">
            <el-input-number v-model="form.width" :step="0.0001" :precision="4" controls-position="right" class="!w-full" />
          </el-form-item>
          <el-form-item label="型号">
            <el-input v-model="form.model" placeholder="型号" />
          </el-form-item>
          <el-form-item label="规格">
            <el-input v-model="form.specification" placeholder="规格" />
          </el-form-item>
          <el-form-item label="厚度">
            <el-input-number v-model="form.thickness" :step="0.0001" :precision="4" controls-position="right" class="!w-full" />
          </el-form-item>
          <el-form-item label="颜色">
            <el-input v-model="form.color" placeholder="颜色" />
          </el-form-item>
          <el-form-item label="TG值">
            <el-input v-model="form.tgValue" placeholder="TG值" />
          </el-form-item>
          <el-form-item label="铜厚">
            <el-input v-model="form.copperThickness" placeholder="铜厚" />
          </el-form-item>
          <el-form-item label="是否含铜">
            <el-select v-model="form.isCopperContained" placeholder="-" class="!w-full">
              <el-option :value="true" label="是" />
              <el-option :value="false" label="否" />
            </el-select>
          </el-form-item>
          <el-form-item label="直径">
            <el-input-number v-model="form.diameter" :step="0.0001" :precision="4" controls-position="right" class="!w-full" />
          </el-form-item>
          <el-form-item label="刃长">
            <el-input-number v-model="form.bladeLength" :step="0.0001" :precision="4" controls-position="right" class="!w-full" />
          </el-form-item>
          <el-form-item label="总长">
            <el-input-number v-model="form.totalLength" :step="0.0001" :precision="4" controls-position="right" class="!w-full" />
          </el-form-item>
        </div>

        <!-- Extension Fields Section -->
        <div class="text-sm font-semibold text-gray-700 mt-4 mb-2 pb-1.5 border-b border-gray-200">扩展字段</div>
        <div class="grid grid-cols-3 gap-x-4 gap-y-2">
          <el-form-item label="扩展1">
            <el-input v-model="form.ext1" placeholder="扩展字段1" />
          </el-form-item>
          <el-form-item label="扩展2">
            <el-input v-model="form.ext2" placeholder="扩展字段2" />
          </el-form-item>
          <el-form-item label="扩展3">
            <el-input v-model="form.ext3" placeholder="扩展字段3" />
          </el-form-item>
          <el-form-item label="扩展4">
            <el-input v-model="form.ext4" placeholder="扩展字段4" />
          </el-form-item>
          <el-form-item label="扩展5">
            <el-input v-model="form.ext5" placeholder="扩展字段5" />
          </el-form-item>
        </div>
      </el-form>
      <div v-if="formError" class="text-red-500 text-xs px-4 pb-2">{{ formError }}</div>
      <template #footer>
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- Version Dialog -->
    <el-dialog
      v-model="versionDialogVisible"
      :title="`物料版本管理 - ${versionMaterial?.materialCode}`"
      width="600px"
      destroy-on-close
    >
      <div class="mb-4">
        <el-button type="primary" :icon="Plus" @click="showAddVersion = true">新增版本</el-button>
      </div>
      <div v-if="showAddVersion" class="mb-4 flex gap-2 items-center">
        <el-input v-model="newVersionNo" placeholder="版本号，如 A.1" class="!w-48" />
        <el-button type="primary" @click="addVersion" :loading="submitting">添加</el-button>
        <el-button @click="showAddVersion = false; newVersionNo = ''">取消</el-button>
      </div>
      <el-table :data="versions" border stripe v-loading="versionsLoading">
        <el-table-column prop="versionNo" label="版本号" min-width="100" />
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
      </el-table>
      <template #footer>
        <el-button @click="closeVersionDialog">关闭</el-button>
      </template>
    </el-dialog>

    <!-- Delete Confirm Dialog -->
    <el-dialog
      v-model="deleteDialogVisible"
      title="确认删除"
      width="420px"
      destroy-on-close
    >
      <p>确定要删除物料 "{{ deleteTarget?.materialName }}" ({{ deleteTarget?.materialCode }}) 吗？</p>
      <p class="text-gray-400 text-sm mt-2">已被免检清单引用的物料不可删除。删除后不可恢复。</p>
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
  listMaterials,
  createMaterial,
  updateMaterial,
  deleteMaterial,
  updateMaterialStatus,
  listMaterialVersions,
  createMaterialVersion,
  type MaterialDto,
  type MaterialVersionDto,
  type MaterialQueryParams,
} from '../../api/material';
import { getMaterialCategoryDropdown, getUomDropdown, type DropdownItem } from '../../api/dropdown';
import AuditLogDialog from '../../components/AuditLogDialog.vue';
import TableSettingsPanel from '../../components/TableSettingsPanel.vue';
import { useTableSettings, type ColumnDef } from '../../components/useTableSettings';

const materials = ref<MaterialDto[]>([]);
const total = ref(0);
const loading = ref(false);
const submitting = ref(false);
const formError = ref('');

const categoryDropdown = ref<DropdownItem[]>([]);
const uomDropdown = ref<DropdownItem[]>([]);

const query = reactive<MaterialQueryParams>({
  materialCode: '',
  materialName: '',
  categoryId: undefined,
  basicCategory: undefined,
  status: undefined,
  page: 1,
  size: 10,
});

const totalPages = computed(() => Math.ceil(total.value / (query.size || 10)));

// Table settings
const defaultColumns: ColumnDef[] = [
  { key: 'materialCode', label: '物料编码' },
  { key: 'materialName', label: '物料名称' },
  { key: 'basicCategory', label: '基本分类' },
  { key: 'categoryName', label: '物料分类' },
  { key: 'attributeCategory', label: '属性分类' },
  { key: 'uomName', label: '单位' },
  { key: 'model', label: '型号' },
  { key: 'specification', label: '规格' },
  { key: 'status', label: '状态' },
  { key: 'createdBy', label: '创建人' },
  { key: 'createdAt', label: '创建时间' },
];

const { columns, toggleColumn, resetSettings } = useTableSettings('material-list', defaultColumns);
const showTableSettings = ref(false);

function isColumnVisible(key: string): boolean {
  return columns.value.find(c => c.key === key)?.visible !== false;
}

const visibleColCount = computed(() => columns.value.filter(c => c.visible).length);

// Audit log dialog state
const auditLogVisible = ref(false);
const auditLogTableName = ref('material_master');
const auditLogRecordId = ref(0);

function openAuditLog(mat: MaterialDto) {
  auditLogTableName.value = 'material_master';
  auditLogRecordId.value = mat.id;
  auditLogVisible.value = true;
}

// Dialog state
const dialogVisible = ref(false);
const isEdit = ref(false);
const editingId = ref<number | null>(null);
const form = reactive({
  materialCode: '',
  materialName: '',
  basicCategory: '',
  categoryId: null as number | null,
  attributeCategory: '',
  uomId: null as number | null,
  size: null as number | null,
  length: null as number | null,
  width: null as number | null,
  model: '',
  specification: '',
  thickness: null as number | null,
  color: '',
  tgValue: '',
  copperThickness: '',
  isCopperContained: null as boolean | null,
  diameter: null as number | null,
  bladeLength: null as number | null,
  totalLength: null as number | null,
  ext1: '',
  ext2: '',
  ext3: '',
  ext4: '',
  ext5: '',
});

// Delete dialog state
const deleteDialogVisible = ref(false);
const deleteTarget = ref<MaterialDto | null>(null);

// Version dialog state
const versionDialogVisible = ref(false);
const versionMaterial = ref<MaterialDto | null>(null);
const versions = ref<MaterialVersionDto[]>([]);
const versionsLoading = ref(false);
const showAddVersion = ref(false);
const newVersionNo = ref('');

async function fetchDropdowns() {
  try {
    const [catRes, uomRes] = await Promise.all([
      getMaterialCategoryDropdown(),
      getUomDropdown(),
    ]);
    if (catRes.code === 200 && catRes.data) {
      categoryDropdown.value = catRes.data;
    }
    if (uomRes.code === 200 && uomRes.data) {
      uomDropdown.value = uomRes.data;
    }
  } catch (e) {
    console.error('Failed to fetch dropdowns', e);
  }
}

async function fetchList() {
  loading.value = true;
  try {
    const res = await listMaterials(query);
    if (res.code === 200 && res.data) {
      materials.value = res.data.records;
      total.value = res.data.total;
    }
  } catch (e) {
    console.error('Failed to fetch material list', e);
  } finally {
    loading.value = false;
  }
}

function search() {
  query.page = 1;
  fetchList();
}

function resetQuery() {
  query.materialCode = '';
  query.materialName = '';
  query.categoryId = undefined;
  query.basicCategory = undefined;
  query.status = undefined;
  query.page = 1;
  fetchList();
}

function goPage(page: number) {
  query.page = page;
  fetchList();
}

function openCreateDialog() {
  isEdit.value = false;
  editingId.value = null;
  Object.assign(form, {
    materialCode: '', materialName: '', basicCategory: '', categoryId: null,
    attributeCategory: '', uomId: null, size: null, length: null, width: null,
    model: '', specification: '', thickness: null, color: '', tgValue: '',
    copperThickness: '', isCopperContained: null, diameter: null, bladeLength: null,
    totalLength: null, ext1: '', ext2: '', ext3: '', ext4: '', ext5: '',
  });
  formError.value = '';
  dialogVisible.value = true;
}

function openEditDialog(mat: MaterialDto) {
  isEdit.value = true;
  editingId.value = mat.id;
  Object.assign(form, {
    materialCode: mat.materialCode, materialName: mat.materialName,
    basicCategory: mat.basicCategory, categoryId: mat.categoryId,
    attributeCategory: mat.attributeCategory, uomId: mat.uomId,
    size: mat.size, length: mat.length, width: mat.width,
    model: mat.model || '', specification: mat.specification || '',
    thickness: mat.thickness, color: mat.color || '', tgValue: mat.tgValue || '',
    copperThickness: mat.copperThickness || '', isCopperContained: mat.isCopperContained,
    diameter: mat.diameter, bladeLength: mat.bladeLength, totalLength: mat.totalLength,
    ext1: mat.ext1 || '', ext2: mat.ext2 || '', ext3: mat.ext3 || '',
    ext4: mat.ext4 || '', ext5: mat.ext5 || '',
  });
  formError.value = '';
  dialogVisible.value = true;
}

function closeDialog() {
  dialogVisible.value = false;
}

async function submitForm() {
  if (!form.materialCode && !isEdit.value) {
    formError.value = '物料编码不能为空';
    return;
  }
  if (!form.materialName) {
    formError.value = '物料名称不能为空';
    return;
  }
  if (!form.basicCategory) {
    formError.value = '基本分类不能为空';
    return;
  }
  if (!form.categoryId) {
    formError.value = '物料分类不能为空';
    return;
  }
  if (!form.attributeCategory) {
    formError.value = '属性分类不能为空';
    return;
  }
  if (!form.uomId) {
    formError.value = '单位不能为空';
    return;
  }

  submitting.value = true;
  formError.value = '';
  try {
    if (isEdit.value && editingId.value) {
      const updateData: Record<string, unknown> = {
        materialName: form.materialName,
        basicCategory: form.basicCategory,
        categoryId: form.categoryId,
        attributeCategory: form.attributeCategory,
        uomId: form.uomId,
        size: form.size,
        length: form.length,
        width: form.width,
        model: form.model || null,
        specification: form.specification || null,
        thickness: form.thickness,
        color: form.color || null,
        tgValue: form.tgValue || null,
        copperThickness: form.copperThickness || null,
        isCopperContained: form.isCopperContained,
        diameter: form.diameter,
        bladeLength: form.bladeLength,
        totalLength: form.totalLength,
        ext1: form.ext1 || null,
        ext2: form.ext2 || null,
        ext3: form.ext3 || null,
        ext4: form.ext4 || null,
        ext5: form.ext5 || null,
      };
      await updateMaterial(editingId.value, updateData);
    } else {
      await createMaterial(form);
    }
    closeDialog();
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '操作失败';
    formError.value = msg;
  } finally {
    submitting.value = false;
  }
}

function confirmDelete(mat: MaterialDto) {
  deleteTarget.value = mat;
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
    await deleteMaterial(deleteTarget.value.id);
    closeDeleteDialog();
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '删除失败';
    alert(msg);
  } finally {
    submitting.value = false;
  }
}

async function toggleStatus(mat: MaterialDto) {
  const newStatus = mat.status === 1 ? 0 : 1;
  try {
    await updateMaterialStatus(mat.id, newStatus);
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '状态更新失败';
    alert(msg);
  }
}

// Version management
async function openVersionDialog(mat: MaterialDto) {
  versionMaterial.value = mat;
  versionDialogVisible.value = true;
  showAddVersion.value = false;
  newVersionNo.value = '';
  await fetchVersions(mat.id);
}

function closeVersionDialog() {
  versionDialogVisible.value = false;
  versionMaterial.value = null;
  versions.value = [];
}

async function fetchVersions(materialId: number) {
  versionsLoading.value = true;
  try {
    const res = await listMaterialVersions(materialId);
    if (res.code === 200 && res.data) {
      versions.value = res.data;
    }
  } catch (e) {
    console.error('Failed to fetch versions', e);
  } finally {
    versionsLoading.value = false;
  }
}

async function addVersion() {
  if (!newVersionNo.value || !versionMaterial.value) return;
  submitting.value = true;
  try {
    await createMaterialVersion(versionMaterial.value.id, { versionNo: newVersionNo.value });
    newVersionNo.value = '';
    showAddVersion.value = false;
    await fetchVersions(versionMaterial.value.id);
  } catch (e: any) {
    const msg = e?.response?.data?.message || '添加版本失败';
    alert(msg);
  } finally {
    submitting.value = false;
  }
}

function formatBasicCategory(val: string): string {
  const map: Record<string, string> = {
    FINISHED_PRODUCT: '成品',
    SEMI_FINISHED: '半成品',
    RAW_MATERIAL: '原材料',
    SPARE_PART: '备件',
  };
  return map[val] || val;
}

function formatAttributeCategory(val: string): string {
  const map: Record<string, string> = {
    PURCHASED: '采购件',
    SELF_MANUFACTURED: '自制件',
    PURCHASED_AND_SELF: '采购&自制件',
  };
  return map[val] || val;
}

function formatDate(dateStr: string | undefined) {
  if (!dateStr) return '-';
  return new Date(dateStr).toLocaleString('zh-CN');
}

onMounted(() => {
  fetchDropdowns();
  fetchList();
});
</script>
