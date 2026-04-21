import { createRouter, createWebHistory } from 'vue-router';

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue'),
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('../views/About.vue'),
  },
  {
    path: '/shift-schedule',
    name: 'ShiftSchedule',
    component: () => import('../views/shift-schedule/ShiftScheduleList.vue'),
  },
  {
    path: '/companies',
    name: 'CompanyList',
    component: () => import('../views/company/CompanyList.vue'),
  },
  {
    path: '/factories',
    name: 'FactoryList',
    component: () => import('../views/factory/FactoryList.vue'),
  },
  {
    path: '/departments',
    name: 'DepartmentList',
    component: () => import('../views/department/DepartmentList.vue'),
  },
  {
    path: '/departments/:id/users',
    name: 'DepartmentUserList',
    component: () => import('../views/department/DepartmentUserList.vue'),
  },
  {
    path: '/work-centers',
    name: 'WorkCenterList',
    component: () => import('../views/work-center/WorkCenterList.vue'),
  },
  {
    path: '/material-categories',
    name: 'MaterialCategoryList',
    component: () => import('../views/material-category/MaterialCategoryList.vue'),
  },
  {
    path: '/uoms',
    name: 'UomList',
    component: () => import('../views/uom/UomList.vue'),
  },
  {
    path: '/uom-conversions',
    name: 'UomConversionList',
    component: () => import('../views/uom/UomConversionList.vue'),
  },
  {
    path: '/materials',
    name: 'MaterialList',
    component: () => import('../views/material/MaterialList.vue'),
  },
  {
    path: '/equipment-types',
    name: 'EquipmentTypeList',
    component: () => import('../views/equipment-type/EquipmentTypeList.vue'),
  },
  {
    path: '/equipment-models',
    name: 'EquipmentModelList',
    component: () => import('../views/equipment-model/EquipmentModelList.vue'),
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
