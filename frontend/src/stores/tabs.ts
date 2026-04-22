import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { RouteLocationNormalized } from 'vue-router';

export interface Tab {
  name: string;
  path: string;
  title: string;
}

export const useTabStore = defineStore('tabs', () => {
  const tabs = ref<Tab[]>([
    { name: 'Dashboard', path: '/', title: '首页' }
  ]);
  const activeTab = ref('/');

  const addTab = (tab: Tab) => {
    if (!tabs.value.find(t => t.path === tab.path)) {
      tabs.value.push(tab);
    }
    activeTab.value = tab.path;
  };

  const addTabFromRoute = (route: RouteLocationNormalized) => {
    const title = (route.meta?.title as string) || route.name?.toString() || route.path;
    addTab({
      name: route.name?.toString() || route.path,
      path: route.path,
      title,
    });
  };

  const removeTab = (path: string) => {
    if (path === '/') return;
    const index = tabs.value.findIndex(t => t.path === path);
    if (index !== -1) {
      tabs.value.splice(index, 1);
      if (activeTab.value === path) {
        const nextTab = tabs.value[index] || tabs.value[index - 1];
        if (nextTab) {
          activeTab.value = nextTab.path;
        }
      }
    }
  };

  return { tabs, activeTab, addTab, addTabFromRoute, removeTab };
});
