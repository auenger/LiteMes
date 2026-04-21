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
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
