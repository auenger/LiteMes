import { createRouter, createWebHistory } from 'vue-router';
import Dashboard from '../views/Dashboard.vue';
import MaterialInfo from '../views/MaterialInfo.vue';
import Customer from '../views/Customer.vue';
import Factory from '../views/Factory.vue';

const routes = [
  { path: '/', component: Dashboard, name: 'Dashboard' },
  { path: '/material', component: MaterialInfo, name: 'MaterialInfo' },
  { path: '/customer', component: Customer, name: 'Customer' },
  { path: '/factory', component: Factory, name: 'Factory' },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
