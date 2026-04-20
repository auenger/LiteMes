<template>
  <div class="h-screen w-full flex flex-col overflow-hidden transition-colors duration-200" :class="{ dark: themeStore.isDark }">
    <!-- Header -->
    <header class="h-12 border-b border-border-color bg-card flex items-center justify-between px-4 shrink-0 z-30 shadow-sm transition-colors duration-200">
      <div class="flex items-center space-x-6">
        <!-- Logo -->
        <div class="flex items-center space-x-2 w-52">
          <div class="w-8 h-8 rounded bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center text-white shadow-md">
            <el-icon :size="20"><Cpu /></el-icon>
          </div>
          <span class="font-bold text-lg tracking-tight text-slate-800 dark:text-slate-200 uppercase">LiteMes</span>
        </div>
        
        <div class="flex items-center space-x-4">
          <el-icon class="text-lg cursor-pointer text-muted hover:text-primary transition-colors"><Expand /></el-icon>
          <el-icon class="text-lg cursor-pointer text-muted hover:text-primary transition-colors" @click="handleRefresh"><Refresh /></el-icon>
          
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">
              <el-icon><HomeFilled /></el-icon>
            </el-breadcrumb-item>
            <el-breadcrumb-item v-if="$route.path !== '/'">基础数据</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRouteName }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </div>

      <div class="flex items-center space-x-4">
        <div class="flex items-center space-x-3 text-muted">
          <!-- Theme Toggle -->
          <el-tooltip :content="themeStore.isDark ? '切换亮色模式' : '切换暗色模式'" placement="bottom">
            <div 
              class="w-8 h-8 flex items-center justify-center rounded hover:bg-slate-100 dark:hover:bg-white/10 cursor-pointer transition-colors"
              @click="themeStore.toggleTheme"
            >
              <el-icon class="text-lg">
                <Moon v-if="!themeStore.isDark" />
                <Sunny v-else />
              </el-icon>
            </div>
          </el-tooltip>
          
          <el-icon class="text-lg cursor-pointer hover:text-primary transition-colors"><FullScreen /></el-icon>
          <el-icon class="text-lg cursor-pointer hover:text-primary transition-colors"><Bell /></el-icon>
          <el-icon class="text-lg cursor-pointer hover:text-primary transition-colors"><Setting /></el-icon>
        </div>
        
        <el-divider direction="vertical" class="!border-border-color" />
        
        <el-dropdown trigger="click">
          <div class="flex items-center space-x-2 cursor-pointer outline-none">
            <el-avatar :size="28" src="https://api.dicebear.com/7.x/avataaars/svg?seed=Felix" />
            <span class="text-sm font-medium text-slate-700 dark:text-slate-200">luoxueting</span>
            <el-icon><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>个人中心</el-dropdown-item>
              <el-dropdown-item>修改密码</el-dropdown-item>
              <el-dropdown-item divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <div class="flex flex-1 overflow-hidden bg-background transition-colors duration-200">
      <!-- Sidebar -->
      <aside class="w-60 border-r border-border-color bg-card flex flex-col h-full shrink-0 z-20 transition-colors duration-200">
        <nav class="flex-1 py-2 overflow-y-auto custom-scroll">
          <router-link to="/" class="menu-item" :class="{ active: $route.path === '/' }" @click="handleNavClick('/', '首页', 'Dashboard')">
            <el-icon><Monitor /></el-icon>
            <span>Dashboard</span>
          </router-link>

          <div class="px-4 pt-4 pb-2 text-[11px] font-bold text-muted/60 dark:text-muted/40 uppercase tracking-widest">组织架构</div>
          
          <el-collapse v-model="activeCollapse" class="clean-collapse">
            <el-collapse-item name="master">
              <template #title>
                <div class="flex items-center gap-3 px-4 w-full h-full">
                  <el-icon><Collection /></el-icon>
                  <span class="font-medium text-sm">基础数据管理</span>
                </div>
              </template>
              <div class="space-y-0.5">
                <router-link to="/factory" class="sub-menu-item" :class="{ active: $route.path === '/factory' }" @click="handleNavClick('/factory', '工厂管理', 'Factory')">
                  工厂管理
                </router-link>
                <router-link to="/customer" class="sub-menu-item" :class="{ active: $route.path === '/customer' }" @click="handleNavClick('/customer', '客户管理', 'Customer')">
                  客户管理
                </router-link>
                <router-link to="/material" class="sub-menu-item" :class="{ active: $route.path === '/material' }" @click="handleNavClick('/material', '物料主数据', 'MaterialInfo')">
                  物料管理
                </router-link>
                <div class="sub-menu-item opacity-40 cursor-not-allowed">部门管理</div>
                <div class="sub-menu-item opacity-40 cursor-not-allowed">供应商管理</div>
              </div>
            </el-collapse-item>
          </el-collapse>

          <div class="px-4 pt-6 pb-2 text-[11px] font-bold text-muted/60 dark:text-muted/40 uppercase tracking-widest">生产执行</div>
          <div class="menu-item opacity-50 cursor-not-allowed">
            <el-icon><List /></el-icon>
            <span>生产排程</span>
          </div>
          <div class="menu-item opacity-50 cursor-not-allowed">
            <el-icon><Histogram /></el-icon>
            <span>报表中心</span>
          </div>
        </nav>
      </aside>

      <!-- Main Content Container (Unified background) -->
      <main class="flex-1 flex flex-col min-w-0 overflow-hidden bg-background">
        <!-- Tab Bar -->
        <div class="h-9 border-b border-border-color bg-card flex items-center px-4 space-x-1 shrink-0 overflow-x-auto no-scrollbar z-10 transition-colors duration-200">
          <div 
            v-for="tab in tabStore.tabs" 
            :key="tab.path"
            @click="navigateTo(tab.path)"
            class="tab-pill group"
            :class="{ active: tabStore.activeTab === tab.path }"
          >
            <el-icon v-if="tab.path === '/'" class="mr-1.5"><HomeFilled /></el-icon>
            <span class="truncate">{{ tab.title }}</span>
            <el-icon 
              v-if="tab.path !== '/'" 
              class="ml-1.5 opacity-0 group-hover:opacity-100 hover:bg-black/10 dark:hover:bg-white/10 rounded transition-all"
              @click.stop="tabStore.removeTab(tab.path)"
            >
              <Close />
            </el-icon>
          </div>
        </div>

        <!-- Scrollable view area -->
        <div class="flex-1 overflow-y-auto p-2.5 custom-scroll relative">
          <router-view v-slot="{ Component }">
            <transition name="fade-slide" mode="out-in">
              <component :is="Component" class="h-full" />
            </transition>
          </router-view>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useThemeStore } from './stores/theme';
import { useTabStore } from './stores/tabs';
import { 
  Monitor, 
  OfficeBuilding, 
  User, 
  Box, 
  Bell, 
  Setting,
  Search,
  Moon,
  Sunny,
  MoreFilled,
  Avatar,
  Collection,
  Cpu,
  Histogram,
  Close,
  Expand,
  Refresh,
  HomeFilled,
  FullScreen,
  ArrowDown,
  List
} from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();
const themeStore = useThemeStore();
const tabStore = useTabStore();

const activeCollapse = ref(['master']);

const handleRefresh = () => {
  window.location.reload();
};

const handleNavClick = (path: string, title: string, name: string) => {
  tabStore.addTab({ path, title, name });
};

const navigateTo = (path: string) => {
  router.push(path);
  tabStore.activeTab = path;
};

watch(() => route.path, (newPath) => {
  tabStore.activeTab = newPath;
}, { immediate: true });

const currentRouteName = computed(() => {
  const names: Record<string, string> = {
    'Dashboard': '监控页',
    'MaterialInfo': '物料管理',
    'Customer': '客户管理',
    'Factory': '工厂管理'
  };
  return names[route.name as string] || '首页';
});
</script>

<style scoped>
/* Sidebar Items */
.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 16px;
  margin: 2px 8px;
  border-radius: 4px;
  font-size: 14px;
  color: var(--text-main);
  transition: all 0.2s ease;
  cursor: pointer;
  text-decoration: none;
}
.menu-item:hover {
  background: rgba(24, 144, 255, 0.05);
  color: var(--el-color-primary);
}
.menu-item.active {
  background: rgba(24, 144, 255, 0.1);
  color: var(--el-color-primary);
  font-weight: 500;
}

.sub-menu-item {
  display: flex;
  align-items: center;
  padding: 8px 16px 8px 48px;
  margin: 1px 8px;
  font-size: 14px;
  color: var(--text-main);
  text-decoration: none;
  border-radius: 4px;
  transition: 0.2s;
}
.sub-menu-item:hover {
  color: var(--el-color-primary);
}
.sub-menu-item.active {
  background: rgba(24, 144, 255, 0.1);
  color: var(--el-color-primary);
  font-weight: 500;
}

.clean-collapse :deep(.el-collapse-item__header) {
  height: 44px;
  background: transparent;
  border: none;
  padding-right: 16px;
  color: var(--text-main);
}
.clean-collapse :deep(.el-collapse-item__header:hover) {
  background: rgba(0,0,0,0.02);
}
.clean-collapse :deep(.el-collapse-item__wrap) {
  background: transparent;
  border: none;
}
.clean-collapse :deep(.el-collapse-item__content) {
  padding-bottom: 4px;
}

/* Tabs Bar */
.tab-pill {
  height: 32px;
  padding: 0 12px;
  background: #fff;
  border: 1px solid var(--border-color);
  border-radius: 2px;
  font-size: 12px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.2s;
  color: var(--text-muted);
  box-shadow: 0 1px 2px rgba(0,0,0,0.03);
}
.dark .tab-pill {
  background: #141414;
}
.tab-pill.active {
  background: rgba(24, 144, 255, 0.1);
  color: var(--el-color-primary);
  border-color: rgba(24, 144, 255, 0.3);
  position: relative;
}
.tab-pill.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--el-color-primary);
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.2s ease;
}
.fade-slide-enter-from { opacity: 0; transform: translateY(5px); }
.fade-slide-leave-to { opacity: 0; transform: translateY(-5px); }

.no-scrollbar::-webkit-scrollbar { display: none; }
</style>

<style>
/* Breadcrumb Overrides */
.el-breadcrumb__inner, .el-breadcrumb__inner a {
  font-weight: 400 !important;
  color: var(--text-muted) !important;
}
.el-breadcrumb__item:last-child .el-breadcrumb__inner {
  color: var(--text-main) !important;
}

/* Scrollbar */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
::-webkit-scrollbar-thumb {
  background: #d9d9d9;
  border-radius: 3px;
}
</style>

<style>
.custom-search .el-input__wrapper {
  background: var(--bg-main) !important;
  border: 1px solid var(--color-border-base) !important;
  box-shadow: none !important;
}
</style>
