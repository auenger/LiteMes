<template>
  <div class="h-full flex flex-col space-y-3">
    <!-- Filter Card -->
    <div class="bg-card p-3 border border-border-color shadow-sm rounded-sm shrink-0">
      <el-form :inline="true" :model="query" size="default" class="flex flex-wrap gap-y-3 items-center !mb-0">
        <el-form-item label="原单位" class="!mb-0">
          <el-input v-model="query.fromUom" placeholder="原单位编码/名称" clearable class="!w-40" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="目标单位" class="!mb-0">
          <el-input v-model="query.toUom" placeholder="目标单位编码/名称" clearable class="!w-40" @keyup.enter="search" />
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
          <el-button type="primary" :icon="Plus" @click="openCreateDialog">新建换算比例</el-button>
        </div>
      </div>
      <div class="flex-1 p-2.5 overflow-hidden">
        <el-table :data="conversions" border stripe height="100%" v-loading="loading">
          <el-table-column prop="fromUomCode" label="原单位编码" min-width="120" />
          <el-table-column prop="fromUomName" label="原单位名称" min-width="120" />
          <el-table-column prop="toUomCode" label="目标单位编码" min-width="120" />
          <el-table-column prop="toUomName" label="目标单位名称" min-width="120" />
          <el-table-column prop="conversionRate" label="换算率" min-width="100" />
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
          <el-table-column label="操作" min-width="160" fixed="right">
            <template #default="{ row }">
              <div class="flex gap-1">
                <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
                <el-button size="small" type="danger" @click="confirmDelete(row)">删除</el-button>
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
      :title="isEdit ? '编辑换算比例' : '新建换算比例'"
      width="480px"
      destroy-on-close
    >
      <el-form label-width="80px">
        <el-form-item label="原单位" required>
          <el-select v-model="form.fromUomId" placeholder="请选择原单位" :disabled="isEdit" class="!w-full">
            <el-option v-for="uom in uomOptions" :key="uom.id" :value="uom.id" :label="`${uom.code} - ${uom.name}`" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标单位" required>
          <el-select v-model="form.toUomId" placeholder="请选择目标单位" :disabled="isEdit" class="!w-full">
            <el-option v-for="uom in uomOptions" :key="uom.id" :value="uom.id" :label="`${uom.code} - ${uom.name}`" />
          </el-select>
        </el-form-item>
        <el-form-item label="换算率" required>
          <el-input-number v-model="form.conversionRate" :step="0.000001" :precision="6" placeholder="请输入换算率" controls-position="right" class="!w-full" />
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
      <p>确定要删除换算比例 "{{ deleteTarget?.fromUomName }} -> {{ deleteTarget?.toUomName }}" 吗？</p>
      <p class="text-gray-400 text-sm mt-2">删除后不可恢复。</p>
      <template #footer>
        <el-button @click="closeDeleteDialog">取消</el-button>
        <el-button type="danger" @click="doDelete" :loading="submitting">删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { Search, Refresh, Plus } from '@element-plus/icons-vue';
import {
  listUomConversions,
  createUomConversion,
  updateUomConversion,
  deleteUomConversion,
  type UomConversionDto,
  type UomConversionQueryParams,
} from '../../api/uom';
import { getUomDropdown, type DropdownItem } from '../../api/dropdown';

const conversions = ref<UomConversionDto[]>([]);
const total = ref(0);
const loading = ref(false);
const submitting = ref(false);
const formError = ref('');
const uomOptions = ref<DropdownItem[]>([]);

const query = reactive<UomConversionQueryParams>({
  fromUom: '',
  toUom: '',
  status: undefined,
  page: 1,
  size: 10,
});

const totalPages = computed(() => Math.ceil(total.value / (query.size || 10)));

// Dialog state
const dialogVisible = ref(false);
const isEdit = ref(false);
const editingId = ref<number | null>(null);
const form = reactive({
  fromUomId: null as number | null,
  toUomId: null as number | null,
  conversionRate: null as number | null,
});

// Delete dialog state
const deleteDialogVisible = ref(false);
const deleteTarget = ref<UomConversionDto | null>(null);

async function fetchUomOptions() {
  try {
    const res = await getUomDropdown();
    if (res.code === 200 && res.data) {
      uomOptions.value = res.data;
    }
  } catch (e) {
    console.error('Failed to fetch uom dropdown', e);
  }
}

async function fetchList() {
  loading.value = true;
  try {
    const res = await listUomConversions(query);
    if (res.code === 200 && res.data) {
      conversions.value = res.data.records;
      total.value = res.data.total;
    }
  } catch (e) {
    console.error('Failed to fetch uom conversion list', e);
  } finally {
    loading.value = false;
  }
}

function search() {
  query.page = 1;
  fetchList();
}

function resetQuery() {
  query.fromUom = '';
  query.toUom = '';
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
  form.fromUomId = null;
  form.toUomId = null;
  form.conversionRate = null;
  formError.value = '';
  dialogVisible.value = true;
}

function openEditDialog(conv: UomConversionDto) {
  isEdit.value = true;
  editingId.value = conv.id;
  form.fromUomId = conv.fromUomId;
  form.toUomId = conv.toUomId;
  form.conversionRate = conv.conversionRate;
  formError.value = '';
  dialogVisible.value = true;
}

function closeDialog() {
  dialogVisible.value = false;
}

async function submitForm() {
  if (!form.fromUomId) {
    formError.value = '原单位不能为空';
    return;
  }
  if (!form.toUomId) {
    formError.value = '目标单位不能为空';
    return;
  }
  if (form.conversionRate === null || form.conversionRate === undefined) {
    formError.value = '换算率不能为空';
    return;
  }

  submitting.value = true;
  formError.value = '';
  try {
    if (isEdit.value && editingId.value) {
      await updateUomConversion(editingId.value, {
        conversionRate: form.conversionRate,
      });
    } else {
      await createUomConversion({
        fromUomId: form.fromUomId,
        toUomId: form.toUomId,
        conversionRate: form.conversionRate,
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

function confirmDelete(conv: UomConversionDto) {
  deleteTarget.value = conv;
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
    await deleteUomConversion(deleteTarget.value.id);
    closeDeleteDialog();
    fetchList();
  } catch (e: any) {
    const msg = e?.response?.data?.message || '删除失败';
    alert(msg);
  } finally {
    submitting.value = false;
  }
}

function formatDate(dateStr: string | undefined) {
  if (!dateStr) return '-';
  return new Date(dateStr).toLocaleString('zh-CN');
}

onMounted(() => {
  fetchUomOptions();
  fetchList();
});
</script>
