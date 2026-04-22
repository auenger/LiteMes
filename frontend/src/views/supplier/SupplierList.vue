<template>
  <div class="h-full flex flex-col space-y-3">
    <!-- Filter Card -->
    <div class="bg-card p-3 border border-border-color shadow-sm rounded-sm shrink-0">
      <el-form :inline="true" :model="query" size="default" class="flex flex-wrap gap-y-3 items-center !mb-0">
        <el-form-item label="供应商编码" class="!mb-0">
          <el-input v-model="query.supplierCode" placeholder="供应商编码" clearable class="!w-40" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="供应商名称" class="!mb-0">
          <el-input v-model="query.supplierName" placeholder="供应商名称" clearable class="!w-40" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="状态" class="!mb-0">
          <el-select v-model="query.status" placeholder="全部状态" clearable class="!w-32">
            <el-option :label="'启用'" :value="1" />
            <el-option :label="'禁用'" :value="0" />
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
          <el-button type="primary" :icon="Plus" @click="openCreateDialog">新建供应商</el-button>
          <el-button :icon="Setting" @click="showTableSettings = !showTableSettings">表格设置</el-button>
        </div>
        <TableSettingsPanel
          :visible="showTableSettings"
          :columns="columns"
          @close="showTableSettings = false"
          @toggle="toggleColumn"
          @reset="resetSettings"
        />
      </div>
      <div class="flex-1 p-2.5 overflow-hidden">
        <el-table :data="suppliers" border stripe height="100%" v-loading="loading">
          <el-table-column v-if="isColumnVisible('supplierCode')" prop="supplierCode" label="供应商编码" min-width="120" />
          <el-table-column v-if="isColumnVisible('supplierName')" prop="supplierName" label="供应商名称" min-width="120" />
          <el-table-column v-if="isColumnVisible('shortName')" label="简称" min-width="100">
            <template #default="{ row }">{{ row.shortName || '-' }}</template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('contactPerson')" label="联系人" min-width="100">
            <template #default="{ row }">{{ row.contactPerson || '-' }}</template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('phone')" label="电话" min-width="120">
            <template #default="{ row }">{{ row.phone || '-' }}</template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('status')" label="状态" min-width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('createdBy')" label="创建人" min-width="100">
            <template #default="{ row }">{{ row.createdBy || '-' }}</template>
          </el-table-column>
          <el-table-column v-if="isColumnVisible('createdAt')" label="创建时间" min-width="160">
            <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
          </el-table-column>
          <el-table-column label="操作" min-width="300" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
              <el-button size="small" type="info" @click="openMaterialDialog(row)">物料</el-button>
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑供应商' : '新建供应商'" width="480px" @close="closeDialog">
      <el-form label-width="80px">
        <el-form-item label="供应商编码" required>
          <el-input v-model="form.supplierCode" :disabled="isEdit" placeholder="请输入供应商编码" />
        </el-form-item>
        <el-form-item label="供应商名称" required>
          <el-input v-model="form.supplierName" placeholder="请输入供应商名称" />
        </el-form-item>
        <el-form-item label="简称">
          <el-input v-model="form.shortName" placeholder="请输入简称" />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="form.contactPerson" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <div v-if="formError" class="text-red-500 text-sm px-4 pb-2">{{ formError }}</div>
      <template #footer>
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- Delete Confirm Dialog -->
    <el-dialog v-model="deleteDialogVisible" title="确认删除" width="400px" @close="closeDeleteDialog">
      <p>确定要删除供应商 "{{ deleteTarget?.supplierName }}" ({{ deleteTarget?.supplierCode }}) 吗？</p>
      <p class="text-gray-400 text-sm mt-2">删除后不可恢复。</p>
      <template #footer>
        <el-button @click="closeDeleteDialog">取消</el-button>
        <el-button type="danger" @click="doDelete" :loading="submitting">删除</el-button>
      </template>
    </el-dialog>

    <!-- Material Association Dialog -->
    <el-dialog v-model="materialDialogVisible" :title="`物料关联 — ${materialSupplier?.supplierName}`" width="640px" @close="closeMaterialDialog">
      <div class="mb-5">
        <h4 class="text-sm font-medium text-gray-700 mb-2">已关联物料</h4>
        <el-table v-if="supplierMaterials.length > 0" :data="supplierMaterials" border size="small">
          <el-table-column prop="materialCode" label="物料编码" min-width="120" />
          <el-table-column prop="materialName" label="物料名称" min-width="120" />
          <el-table-column label="操作" min-width="100">
            <template #default="{ row }">
              <el-button size="small" type="danger" @click="doUnlinkMaterial(row)">取消关联</el-button>
            </template>
          </el-table-column>
        </el-table>
        <p v-else class="text-gray-400 text-sm">暂无关联物料</p>
      </div>
      <div>
        <h4 class="text-sm font-medium text-gray-700 mb-2">添加物料</h4>
        <div class="flex gap-2">
          <el-select v-model="selectedMaterialId" placeholder="请选择物料" clearable class="flex-1">
            <el-option v-for="m in availableMaterials" :key="m.id" :label="`${m.code} - ${m.name}`" :value="m.id" />
          </el-select>
          <el-button type="primary" @click="doLinkMaterial" :disabled="!selectedMaterialId">添加</el-button>
        </div>
      </div>
      <div v-if="materialError" class="text-red-500 text-sm mt-3">{{ materialError }}</div>
      <template #footer>
        <el-button @click="closeMaterialDialog">关闭</el-button>
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
  listSuppliers,
  createSupplier,
  updateSupplier,
  deleteSupplier,
  updateSupplierStatus,
  linkMaterials,
  unlinkMaterial,
  getSupplierMaterials,
  type SupplierDto,
  type SupplierMaterialDto,
  type SupplierQueryParams,
} from '../../api/supplier';
import { apiGet } from '../../api/http';
import AuditLogDialog from '../../components/AuditLogDialog.vue';
import TableSettingsPanel from '../../components/TableSettingsPanel.vue';
import { useTableSettings, type ColumnDef } from '../../components/useTableSettings';

interface MaterialDropdown {
  id: number;
  code: string;
  name: string;
}

const suppliers = ref<SupplierDto[]>([]);
const total = ref(0);
const loading = ref(false);
const submitting = ref(false);
const formError = ref('');

const query = reactive<SupplierQueryParams>({
  supplierCode: '',
  supplierName: '',
  status: undefined,
  page: 1,
  size: 10,
});

const totalPages = computed(() => Math.ceil(total.value / (query.size || 10)));

// Table settings
const defaultColumns: ColumnDef[] = [
  { key: 'supplierCode', label: '供应商编码' },
  { key: 'supplierName', label: '供应商名称' },
  { key: 'shortName', label: '简称' },
  { key: 'contactPerson', label: '联系人' },
  { key: 'phone', label: '电话' },
  { key: 'status', label: '状态' },
  { key: 'createdBy', label: '创建人' },
  { key: 'createdAt', label: '创建时间' },
];

const { columns, toggleColumn, resetSettings } = useTableSettings('supplier-list', defaultColumns);
const showTableSettings = ref(false);

function isColumnVisible(key: string): boolean {
  return columns.value.find(c => c.key === key)?.visible !== false;
}

const visibleColCount = computed(() => columns.value.filter(c => c.visible).length);

// Audit log dialog state
const auditLogVisible = ref(false);
const auditLogTableName = ref('supplier');
const auditLogRecordId = ref(0);

function openAuditLog(supplier: SupplierDto) {
  auditLogTableName.value = 'supplier';
  auditLogRecordId.value = supplier.id;
  auditLogVisible.value = true;
}

// Dialog state
const dialogVisible = ref(false);
const isEdit = ref(false);
const editingId = ref<number | null>(null);
const form = reactive({
  supplierCode: '',
  supplierName: '',
  shortName: '',
  contactPerson: '',
  phone: '',
  address: '',
  email: '',
  description: '',
});

// Delete dialog state
const deleteDialogVisible = ref(false);
const deleteTarget = ref<SupplierDto | null>(null);

// Material dialog state
const materialDialogVisible = ref(false);
const materialSupplier = ref<SupplierDto | null>(null);
const supplierMaterials = ref<SupplierMaterialDto[]>([]);
const allMaterials = ref<MaterialDropdown[]>([]);
const selectedMaterialId = ref<number | undefined>(undefined);
const materialError = ref('');

const availableMaterials = computed(() => {
  const linkedIds = new Set(supplierMaterials.value.map(sm => sm.materialId));
  return allMaterials.value.filter(m => !linkedIds.has(m.id));
});

async function fetchList() {
  loading.value = true;
  try {
    const res = await listSuppliers(query);
    if (res.code === 200 && res.data) {
      suppliers.value = res.data.records;
      total.value = res.data.total;
    }
  } catch (e) {
    console.error('Failed to fetch supplier list', e);
  } finally {
    loading.value = false;
  }
}

function search() {
  query.page = 1;
  fetchList();
}

function resetQuery() {
  query.supplierCode = '';
  query.supplierName = '';
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
  form.supplierCode = '';
  form.supplierName = '';
  form.shortName = '';
  form.contactPerson = '';
  form.phone = '';
  form.address = '';
  form.email = '';
  form.description = '';
  formError.value = '';
  dialogVisible.value = true;
}

function openEditDialog(supplier: SupplierDto) {
  isEdit.value = true;
  editingId.value = supplier.id;
  form.supplierCode = supplier.supplierCode;
  form.supplierName = supplier.supplierName;
  form.shortName = supplier.shortName || '';
  form.contactPerson = supplier.contactPerson || '';
  form.phone = supplier.phone || '';
  form.address = supplier.address || '';
  form.email = supplier.email || '';
  form.description = supplier.description || '';
  formError.value = '';
  dialogVisible.value = true;
}

function closeDialog() {
  dialogVisible.value = false;
}

async function submitForm() {
  if (!form.supplierCode && !isEdit.value) {
    formError.value = '供应商编码不能为空';
    return;
  }
  if (!form.supplierName) {
    formError.value = '供应商名称不能为空';
    return;
  }

  submitting.value = true;
  formError.value = '';
  try {
    if (isEdit.value && editingId.value) {
      await updateSupplier(editingId.value, {
        supplierName: form.supplierName,
        shortName: form.shortName || undefined,
        contactPerson: form.contactPerson || undefined,
        phone: form.phone || undefined,
        address: form.address || undefined,
        email: form.email || undefined,
        description: form.description || undefined,
      });
    } else {
      await createSupplier({
        supplierCode: form.supplierCode,
        supplierName: form.supplierName,
        shortName: form.shortName || undefined,
        contactPerson: form.contactPerson || undefined,
        phone: form.phone || undefined,
        address: form.address || undefined,
        email: form.email || undefined,
        description: form.description || undefined,
      });
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

function confirmDelete(supplier: SupplierDto) {
  deleteTarget.value = supplier;
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
    await deleteSupplier(deleteTarget.value.id);
    closeDeleteDialog();
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '删除失败';
    alert(msg);
  } finally {
    submitting.value = false;
  }
}

async function toggleStatus(supplier: SupplierDto) {
  const newStatus = supplier.status === 1 ? 0 : 1;
  try {
    await updateSupplierStatus(supplier.id, newStatus);
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '状态更新失败';
    alert(msg);
  }
}

// Material dialog functions
async function openMaterialDialog(supplier: SupplierDto) {
  materialSupplier.value = supplier;
  materialError.value = '';
  selectedMaterialId.value = undefined;
  materialDialogVisible.value = true;
  await fetchSupplierMaterials(supplier.id);
  await fetchAllMaterials();
}

function closeMaterialDialog() {
  materialDialogVisible.value = false;
  materialSupplier.value = null;
  supplierMaterials.value = [];
}

async function fetchSupplierMaterials(supplierId: number) {
  try {
    const res = await getSupplierMaterials(supplierId);
    if (res.code === 200 && res.data) {
      supplierMaterials.value = res.data;
    }
  } catch (e) {
    console.error('Failed to fetch supplier materials', e);
  }
}

async function fetchAllMaterials() {
  try {
    const res = await apiGet<any>('/api/dropdown/materials');
    const data = res.data || res;
    if (Array.isArray(data)) {
      allMaterials.value = data.map((d: any) => ({ id: d.id, code: d.code, name: d.name }));
    }
  } catch (e) {
    console.error('Failed to fetch materials dropdown', e);
  }
}

async function doLinkMaterial() {
  if (!materialSupplier.value || !selectedMaterialId.value) return;
  materialError.value = '';
  try {
    await linkMaterials(materialSupplier.value.id, [selectedMaterialId.value]);
    selectedMaterialId.value = undefined;
    await fetchSupplierMaterials(materialSupplier.value.id);
  } catch (e: any) {
    const msg = e?.response?.data?.message || '关联物料失败';
    materialError.value = msg;
  }
}

async function doUnlinkMaterial(sm: SupplierMaterialDto) {
  if (!materialSupplier.value) return;
  materialError.value = '';
  try {
    await unlinkMaterial(materialSupplier.value.id, sm.materialId);
    await fetchSupplierMaterials(materialSupplier.value.id);
  } catch (e: any) {
    const msg = e?.response?.data?.message || '取消关联失败';
    materialError.value = msg;
  }
}

function formatDate(dateStr: string | undefined) {
  if (!dateStr) return '-';
  return new Date(dateStr).toLocaleString('zh-CN');
}

onMounted(() => {
  fetchList();
});
</script>
