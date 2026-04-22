<template>
  <div class="flex gap-2">
    <el-select
      v-if="level >= 1"
      v-model="companyId"
      :placeholder="companyPlaceholder"
      :disabled="disabled"
      :clearable="clearable"
      class="!w-44"
      @change="onCompanyChange"
    >
      <el-option v-for="c in companies" :key="c.id" :label="c.name" :value="c.id" />
    </el-select>
    <el-select
      v-if="level >= 2"
      v-model="factoryId"
      :placeholder="factoryPlaceholder"
      :disabled="disabled || !companyId"
      :clearable="clearable"
      class="!w-44"
      @change="onFactoryChange"
    >
      <el-option v-for="f in factories" :key="f.id" :label="f.name" :value="f.id" />
    </el-select>
    <el-select
      v-if="level >= 3"
      v-model="departmentId"
      :placeholder="departmentPlaceholder"
      :disabled="disabled || !factoryId"
      :clearable="clearable"
      class="!w-44"
      @change="onDepartmentChange"
    >
      <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
    </el-select>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue';
import {
  getCompanyDropdown,
  getFactoryDropdown,
  getDepartmentDropdown,
  type DropdownItem,
} from '../api/dropdown';

export interface CascadingValue {
  companyId?: number;
  factoryId?: number;
  departmentId?: number;
}

const props = withDefaults(
  defineProps<{
    modelValue?: CascadingValue;
    level?: number;
    disabled?: boolean;
    clearable?: boolean;
    companyPlaceholder?: string;
    factoryPlaceholder?: string;
    departmentPlaceholder?: string;
  }>(),
  {
    modelValue: () => ({}),
    level: 3,
    disabled: false,
    clearable: true,
    companyPlaceholder: '选择公司',
    factoryPlaceholder: '选择工厂',
    departmentPlaceholder: '选择部门',
  }
);

const emit = defineEmits<{
  'update:modelValue': [value: CascadingValue];
  change: [value: CascadingValue];
}>();

const companyId = ref<number | undefined>(props.modelValue?.companyId);
const factoryId = ref<number | undefined>(props.modelValue?.factoryId);
const departmentId = ref<number | undefined>(props.modelValue?.departmentId);

const companies = ref<DropdownItem[]>([]);
const factories = ref<DropdownItem[]>([]);
const departments = ref<DropdownItem[]>([]);

function emitValue() {
  const value: CascadingValue = {
    companyId: companyId.value,
    factoryId: factoryId.value,
    departmentId: departmentId.value,
  };
  emit('update:modelValue', value);
  emit('change', value);
}

async function loadCompanies() {
  try {
    const res = await getCompanyDropdown();
    if (res.code === 200 && res.data) {
      companies.value = res.data;
    }
  } catch (e) {
    console.error('Failed to load companies', e);
  }
}

async function loadFactories(cId: number) {
  try {
    const res = await getFactoryDropdown(cId);
    if (res.code === 200 && res.data) {
      factories.value = res.data;
    }
  } catch (e) {
    console.error('Failed to load factories', e);
  }
}

async function loadDepartments(fId: number) {
  try {
    const res = await getDepartmentDropdown(fId);
    if (res.code === 200 && res.data) {
      departments.value = res.data;
    }
  } catch (e) {
    console.error('Failed to load departments', e);
  }
}

function onCompanyChange(val: number | undefined) {
  factoryId.value = undefined;
  departmentId.value = undefined;
  factories.value = [];
  departments.value = [];
  if (val) {
    loadFactories(val);
  }
  emitValue();
}

function onFactoryChange(val: number | undefined) {
  departmentId.value = undefined;
  departments.value = [];
  if (val) {
    loadDepartments(val);
  }
  emitValue();
}

function onDepartmentChange() {
  emitValue();
}

watch(
  () => props.modelValue,
  (val) => {
    if (val?.companyId !== companyId.value) {
      companyId.value = val?.companyId;
    }
    if (val?.factoryId !== factoryId.value) {
      factoryId.value = val?.factoryId;
    }
    if (val?.departmentId !== departmentId.value) {
      departmentId.value = val?.departmentId;
    }
  }
);

watch(
  () => props.modelValue?.companyId,
  async (newId) => {
    if (newId && factories.value.length === 0) {
      await loadFactories(newId);
    }
  }
);

watch(
  () => props.modelValue?.factoryId,
  async (newId) => {
    if (newId && departments.value.length === 0) {
      await loadDepartments(newId);
    }
  }
);

onMounted(async () => {
  await loadCompanies();
  if (companyId.value) {
    await loadFactories(companyId.value);
  }
  if (factoryId.value) {
    await loadDepartments(factoryId.value);
  }
});
</script>
