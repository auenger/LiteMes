import { createRouter, createWebHistory } from 'vue-router';
import MainLayout from '../layouts/MainLayout.vue';
import { setupRouterGuards } from './guards';

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/auth/Login.vue'),
    meta: { title: '登录', public: true },
  },
  {
    path: '/',
    component: MainLayout,
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '首页', icon: 'Monitor', module: '' },
      },
      {
        path: '/about',
        name: 'About',
        component: () => import('../views/About.vue'),
        meta: { title: '关于', icon: 'InfoFilled', module: '' },
      },
      // ── 组织架构 ──
      {
        path: '/companies',
        name: 'CompanyList',
        component: () => import('../views/company/CompanyList.vue'),
        meta: { title: '公司管理', icon: 'OfficeBuilding', module: '组织架构' },
      },
      {
        path: '/factories',
        name: 'FactoryList',
        component: () => import('../views/factory/FactoryList.vue'),
        meta: { title: '工厂管理', icon: 'House', module: '组织架构' },
      },
      {
        path: '/departments',
        name: 'DepartmentList',
        component: () => import('../views/department/DepartmentList.vue'),
        meta: { title: '部门管理', icon: 'User', module: '组织架构' },
      },
      {
        path: '/departments/:id/users',
        name: 'DepartmentUserList',
        component: () => import('../views/department/DepartmentUserList.vue'),
        meta: { title: '部门人员', icon: 'Avatar', module: '组织架构', hidden: true },
      },
      {
        path: '/shift-schedule',
        name: 'ShiftSchedule',
        component: () => import('../views/shift-schedule/ShiftScheduleList.vue'),
        meta: { title: '班制班次', icon: 'Clock', module: '组织架构' },
      },
      // ── 生产基础 ──
      {
        path: '/work-centers',
        name: 'WorkCenterList',
        component: () => import('../views/work-center/WorkCenterList.vue'),
        meta: { title: '工作中心', icon: 'MapLocation', module: '生产基础' },
      },
      {
        path: '/processes',
        name: 'ProcessList',
        component: () => import('../views/process/ProcessList.vue'),
        meta: { title: '工序管理', icon: 'Operation', module: '生产基础' },
      },
      {
        path: '/uoms',
        name: 'UomList',
        component: () => import('../views/uom/UomList.vue'),
        meta: { title: '计量单位', icon: 'ScaleToOriginal', module: '生产基础' },
      },
      {
        path: '/uom-conversions',
        name: 'UomConversionList',
        component: () => import('../views/uom/UomConversionList.vue'),
        meta: { title: '单位换算', icon: 'Switch', module: '生产基础' },
      },
      // ── 物料管理 ──
      {
        path: '/material-categories',
        name: 'MaterialCategoryList',
        component: () => import('../views/material-category/MaterialCategoryList.vue'),
        meta: { title: '物料分类', icon: 'FolderOpened', module: '物料管理' },
      },
      {
        path: '/materials',
        name: 'MaterialList',
        component: () => import('../views/material/MaterialList.vue'),
        meta: { title: '物料信息', icon: 'Box', module: '物料管理' },
      },
      {
        path: '/inspection-exemptions',
        name: 'InspectionExemptionList',
        component: () => import('../views/inspection-exemption/InspectionExemptionList.vue'),
        meta: { title: '免检清单', icon: 'CircleCheck', module: '物料管理' },
      },
      // ── 设备管理 ──
      {
        path: '/equipment-types',
        name: 'EquipmentTypeList',
        component: () => import('../views/equipment-type/EquipmentTypeList.vue'),
        meta: { title: '设备类型', icon: 'Cpu', module: '设备管理' },
      },
      {
        path: '/equipment-models',
        name: 'EquipmentModelList',
        component: () => import('../views/equipment-model/EquipmentModelList.vue'),
        meta: { title: '设备型号', icon: ' SetUp', module: '设备管理' },
      },
      {
        path: '/equipment-ledger',
        name: 'EquipmentLedgerList',
        component: () => import('../views/equipment-ledger/EquipmentLedgerList.vue'),
        meta: { title: '设备台账', icon: 'Notebook', module: '设备管理' },
      },
      // ── 供应链 ──
      {
        path: '/customers',
        name: 'CustomerList',
        component: () => import('../views/customer/CustomerList.vue'),
        meta: { title: '客户管理', icon: 'Avatar', module: '供应链' },
      },
      {
        path: '/suppliers',
        name: 'SupplierList',
        component: () => import('../views/supplier/SupplierList.vue'),
        meta: { title: '供应商管理', icon: 'Van', module: '供应链' },
      },
      // ── 数据权限 ──
      {
        path: '/data-permission-groups',
        name: 'DataPermissionGroupList',
        component: () => import('../views/data-permission-group/DataPermissionGroupList.vue'),
        meta: { title: '权限组管理', icon: 'Lock', module: '数据权限' },
      },
      {
        path: '/user-data-permissions',
        name: 'UserDataPermissionList',
        component: () => import('../views/user-data-permission/UserDataPermissionList.vue'),
        meta: { title: '用户权限', icon: 'Key', module: '数据权限' },
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

setupRouterGuards(router);

export default router;
