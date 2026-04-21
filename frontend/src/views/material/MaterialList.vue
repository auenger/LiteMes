<template>
  <div class="material-page">
    <div class="page-header">
      <h2>物料信息管理</h2>
      <div class="header-actions">
        <div class="table-settings-wrapper" style="position: relative;">
          <button class="btn" @click="showTableSettings = !showTableSettings">表格设置</button>
          <TableSettingsPanel
            :visible="showTableSettings"
            :columns="columns"
            @close="showTableSettings = false"
          />
        </div>
        <button class="btn btn-primary" @click="openCreateDialog">新建物料</button>
      </div>
    </div>

    <!-- Search Bar -->
    <div class="search-bar">
      <input
        v-model="query.materialCode"
        type="text"
        placeholder="物料编码"
        class="input"
        @keyup.enter="search"
      />
      <input
        v-model="query.materialName"
        type="text"
        placeholder="物料名称"
        class="input"
        @keyup.enter="search"
      />
      <select v-model="query.categoryId" class="input select">
        <option :value="undefined">全部物料分类</option>
        <option v-for="cat in categoryDropdown" :key="cat.id" :value="cat.id">
          {{ cat.name }}
        </option>
      </select>
      <select v-model="query.basicCategory" class="input select">
        <option :value="undefined">全部基本分类</option>
        <option value="FINISHED_PRODUCT">成品</option>
        <option value="SEMI_FINISHED">半成品</option>
        <option value="RAW_MATERIAL">原材料</option>
        <option value="SPARE_PART">备件</option>
      </select>
      <select v-model="query.status" class="input select">
        <option :value="undefined">全部状态</option>
        <option :value="1">启用</option>
        <option :value="0">禁用</option>
      </select>
      <button class="btn" @click="search">查询</button>
      <button class="btn btn-secondary" @click="resetQuery">重置</button>
    </div>

    <!-- Material Table -->
    <table class="table">
      <thead>
        <tr>
          <th v-if="isColumnVisible('materialCode')">物料编码</th>
          <th v-if="isColumnVisible('materialName')">物料名称</th>
          <th v-if="isColumnVisible('basicCategory')">基本分类</th>
          <th v-if="isColumnVisible('categoryName')">物料分类</th>
          <th v-if="isColumnVisible('attributeCategory')">属性分类</th>
          <th v-if="isColumnVisible('uomName')">单位</th>
          <th v-if="isColumnVisible('model')">型号</th>
          <th v-if="isColumnVisible('specification')">规格</th>
          <th v-if="isColumnVisible('status')">状态</th>
          <th v-if="isColumnVisible('createdBy')">创建人</th>
          <th v-if="isColumnVisible('createdAt')">创建时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="loading">
          <td :colspan="visibleColCount + 1" class="text-center">加载中...</td>
        </tr>
        <tr v-else-if="materials.length === 0">
          <td :colspan="visibleColCount + 1" class="text-center">暂无数据</td>
        </tr>
        <tr v-for="mat in materials" :key="mat.id">
          <td v-if="isColumnVisible('materialCode')">{{ mat.materialCode }}</td>
          <td v-if="isColumnVisible('materialName')">{{ mat.materialName }}</td>
          <td v-if="isColumnVisible('basicCategory')">{{ formatBasicCategory(mat.basicCategory) }}</td>
          <td v-if="isColumnVisible('categoryName')">{{ mat.categoryName || '-' }}</td>
          <td v-if="isColumnVisible('attributeCategory')">{{ formatAttributeCategory(mat.attributeCategory) }}</td>
          <td v-if="isColumnVisible('uomName')">{{ mat.uomName || '-' }}</td>
          <td v-if="isColumnVisible('model')">{{ mat.model || '-' }}</td>
          <td v-if="isColumnVisible('specification')">{{ mat.specification || '-' }}</td>
          <td v-if="isColumnVisible('status')">
            <span :class="['status-badge', mat.status === 1 ? 'status-enabled' : 'status-disabled']">
              {{ mat.status === 1 ? '启用' : '禁用' }}
            </span>
          </td>
          <td v-if="isColumnVisible('createdBy')">{{ mat.createdBy || '-' }}</td>
          <td v-if="isColumnVisible('createdAt')">{{ formatDate(mat.createdAt) }}</td>
          <td class="actions">
            <button class="btn btn-sm" @click="openEditDialog(mat)">编辑</button>
            <button class="btn btn-sm btn-info" @click="openVersionDialog(mat)">版本</button>
            <button
              v-if="mat.status === 1"
              class="btn btn-sm btn-warning"
              @click="toggleStatus(mat)"
            >
              禁用
            </button>
            <button
              v-else
              class="btn btn-sm btn-success"
              @click="toggleStatus(mat)"
            >
              启用
            </button>
            <button class="btn btn-sm btn-danger" @click="confirmDelete(mat)">删除</button>
            <button class="btn btn-sm btn-info" @click="openAuditLog(mat)">变更履历</button>
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

    <!-- Create/Edit Dialog -->
    <div v-if="dialogVisible" class="dialog-overlay" @click.self="closeDialog">
      <div class="dialog dialog-wide">
        <div class="dialog-header">
          <h3>{{ isEdit ? '编辑物料' : '新建物料' }}</h3>
          <button class="dialog-close" @click="closeDialog">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="form-grid">
            <div class="form-group">
              <label>物料编码 <span class="required">*</span></label>
              <input
                v-model="form.materialCode"
                type="text"
                class="input"
                :disabled="isEdit"
                :class="{ 'input-disabled': isEdit }"
                placeholder="请输入物料编码"
              />
            </div>
            <div class="form-group">
              <label>物料名称 <span class="required">*</span></label>
              <input
                v-model="form.materialName"
                type="text"
                class="input"
                placeholder="请输入物料名称"
              />
            </div>
            <div class="form-group">
              <label>基本分类 <span class="required">*</span></label>
              <select v-model="form.basicCategory" class="input">
                <option value="">请选择</option>
                <option value="FINISHED_PRODUCT">成品</option>
                <option value="SEMI_FINISHED">半成品</option>
                <option value="RAW_MATERIAL">原材料</option>
                <option value="SPARE_PART">备件</option>
              </select>
            </div>
            <div class="form-group">
              <label>物料分类 <span class="required">*</span></label>
              <select v-model="form.categoryId" class="input">
                <option :value="null">请选择</option>
                <option v-for="cat in categoryDropdown" :key="cat.id" :value="cat.id">
                  {{ cat.name }}
                </option>
              </select>
            </div>
            <div class="form-group">
              <label>属性分类 <span class="required">*</span></label>
              <select v-model="form.attributeCategory" class="input">
                <option value="">请选择</option>
                <option value="PURCHASED">采购件</option>
                <option value="SELF_MANUFACTURED">自制件</option>
                <option value="PURCHASED_AND_SELF">采购&自制件</option>
              </select>
            </div>
            <div class="form-group">
              <label>单位 <span class="required">*</span></label>
              <select v-model="form.uomId" class="input">
                <option :value="null">请选择</option>
                <option v-for="uom in uomDropdown" :key="uom.id" :value="uom.id">
                  {{ uom.name }}
                </option>
              </select>
            </div>
          </div>

          <!-- PCB Attributes Section -->
          <div class="section-title">PCB 属性</div>
          <div class="form-grid">
            <div class="form-group">
              <label>尺寸</label>
              <input v-model.number="form.size" type="number" step="0.0001" class="input" placeholder="尺寸" />
            </div>
            <div class="form-group">
              <label>长</label>
              <input v-model.number="form.length" type="number" step="0.0001" class="input" placeholder="长" />
            </div>
            <div class="form-group">
              <label>宽</label>
              <input v-model.number="form.width" type="number" step="0.0001" class="input" placeholder="宽" />
            </div>
            <div class="form-group">
              <label>型号</label>
              <input v-model="form.model" type="text" class="input" placeholder="型号" />
            </div>
            <div class="form-group">
              <label>规格</label>
              <input v-model="form.specification" type="text" class="input" placeholder="规格" />
            </div>
            <div class="form-group">
              <label>厚度</label>
              <input v-model.number="form.thickness" type="number" step="0.0001" class="input" placeholder="厚度" />
            </div>
            <div class="form-group">
              <label>颜色</label>
              <input v-model="form.color" type="text" class="input" placeholder="颜色" />
            </div>
            <div class="form-group">
              <label>TG值</label>
              <input v-model="form.tgValue" type="text" class="input" placeholder="TG值" />
            </div>
            <div class="form-group">
              <label>铜厚</label>
              <input v-model="form.copperThickness" type="text" class="input" placeholder="铜厚" />
            </div>
            <div class="form-group">
              <label>是否含铜</label>
              <select v-model="form.isCopperContained" class="input">
                <option :value="null">-</option>
                <option :value="true">是</option>
                <option :value="false">否</option>
              </select>
            </div>
            <div class="form-group">
              <label>直径</label>
              <input v-model.number="form.diameter" type="number" step="0.0001" class="input" placeholder="直径" />
            </div>
            <div class="form-group">
              <label>刃长</label>
              <input v-model.number="form.bladeLength" type="number" step="0.0001" class="input" placeholder="刃长" />
            </div>
            <div class="form-group">
              <label>总长</label>
              <input v-model.number="form.totalLength" type="number" step="0.0001" class="input" placeholder="总长" />
            </div>
          </div>

          <!-- Extension Fields Section -->
          <div class="section-title">扩展字段</div>
          <div class="form-grid">
            <div class="form-group">
              <label>扩展1</label>
              <input v-model="form.ext1" type="text" class="input" placeholder="扩展字段1" />
            </div>
            <div class="form-group">
              <label>扩展2</label>
              <input v-model="form.ext2" type="text" class="input" placeholder="扩展字段2" />
            </div>
            <div class="form-group">
              <label>扩展3</label>
              <input v-model="form.ext3" type="text" class="input" placeholder="扩展字段3" />
            </div>
            <div class="form-group">
              <label>扩展4</label>
              <input v-model="form.ext4" type="text" class="input" placeholder="扩展字段4" />
            </div>
            <div class="form-group">
              <label>扩展5</label>
              <input v-model="form.ext5" type="text" class="input" placeholder="扩展字段5" />
            </div>
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

    <!-- Version Dialog -->
    <div v-if="versionDialogVisible" class="dialog-overlay" @click.self="closeVersionDialog">
      <div class="dialog dialog-wide">
        <div class="dialog-header">
          <h3>物料版本管理 - {{ versionMaterial?.materialCode }}</h3>
          <button class="dialog-close" @click="closeVersionDialog">&times;</button>
        </div>
        <div class="dialog-body">
          <div style="margin-bottom: 16px;">
            <button class="btn btn-primary" @click="showAddVersion = true">新增版本</button>
          </div>
          <div v-if="showAddVersion" style="margin-bottom: 16px; display: flex; gap: 10px; align-items: center;">
            <input
              v-model="newVersionNo"
              type="text"
              class="input"
              placeholder="版本号，如 A.1"
              style="width: 200px;"
            />
            <button class="btn btn-primary" @click="addVersion" :disabled="submitting">
              {{ submitting ? '添加中...' : '添加' }}
            </button>
            <button class="btn" @click="showAddVersion = false; newVersionNo = ''">取消</button>
          </div>
          <table class="table">
            <thead>
              <tr>
                <th>版本号</th>
                <th>状态</th>
                <th>创建人</th>
                <th>创建时间</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="versionsLoading">
                <td colspan="4" class="text-center">加载中...</td>
              </tr>
              <tr v-else-if="versions.length === 0">
                <td colspan="4" class="text-center">暂无版本</td>
              </tr>
              <tr v-for="ver in versions" :key="ver.id">
                <td>{{ ver.versionNo }}</td>
                <td>
                  <span :class="['status-badge', ver.status === 1 ? 'status-enabled' : 'status-disabled']">
                    {{ ver.status === 1 ? '启用' : '禁用' }}
                  </span>
                </td>
                <td>{{ ver.createdBy || '-' }}</td>
                <td>{{ formatDate(ver.createdAt) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="dialog-footer">
          <button class="btn" @click="closeVersionDialog">关闭</button>
        </div>
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
          <p>确定要删除物料 "{{ deleteTarget?.materialName }}" ({{ deleteTarget?.materialCode }}) 吗？</p>
          <p class="text-muted">已被免检清单引用的物料不可删除。删除后不可恢复。</p>
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

<style scoped>
.material-page {
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

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin: 20px 0 10px;
  padding-bottom: 6px;
  border-bottom: 1px solid #f0f0f0;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px 16px;
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
  max-height: 85vh;
  overflow-y: auto;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.dialog-wide {
  width: 800px;
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
  margin-bottom: 8px;
}

.form-group label {
  display: block;
  margin-bottom: 4px;
  font-size: 13px;
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
