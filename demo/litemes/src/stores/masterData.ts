import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useMasterDataStore = defineStore('masterData', () => {
  const materials = ref([
    { id: '1', code: '6907017A', name: 'HP6907017A', category: '成品', type: '自制件', unit: 'PCS', status: true, creator: '骆雪婷', time: '2026-01-08 14:44:04' },
    { id: '2', code: '6907017A-0405', name: 'HS6907017A-0405', category: '半成品', type: '自制件', unit: 'PCS', status: true, creator: '骆雪婷', time: '2026-01-08 14:44:04' },
    { id: '3', code: 'MCode1', name: '物料15', category: '原材料', type: '采购件', unit: 'KG', status: true, creator: '骆雪婷', time: '2026-01-08 17:42:35' },
  ]);

  const customers = ref([
    { id: '1', code: 'C001', name: 'AKE 电子', shortName: 'AKE', type: '内贸客户', contact: '张三', phone: '13800138000', status: true },
    { id: '2', code: 'C002', name: 'Global Tech', shortName: 'GT', type: '外贸客户', contact: 'John Doe', phone: '+1 650 253 0000', status: true },
  ]);

  const factories = ref([
    { id: '1', code: '2000', name: '总装工厂', company: 'XXX有限公司', status: true, creator: '刘国良', time: '2026-01-09 15:03:06' },
  ]);

  return { materials, customers, factories };
});
