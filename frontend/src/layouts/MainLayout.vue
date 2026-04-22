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
          <el-icon class="text-lg cursor-pointer text-muted hover:text-primary transition-colors" @click="toggleSidebar">
            <component :is="sidebarCollapsed ? 'Expand' : 'Fold'" />
          </el-icon>
          <el-icon class="text-lg cursor-pointer text-muted hover:text-primary transition-colors" @click="handleRefresh"><Refresh /></el-icon>

          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">
              <el-icon><HomeFilled /></el-icon>
            </el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentModule">{{ currentModule }}</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </div>

      <div class="flex items-center space-x-4">
        <div class="flex items-center space-x-3 text-muted">
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

        <el-dropdown trigger="click" @command="handleDropdownCommand">
          <div class="flex items-center space-x-2 cursor-pointer outline-none">
            <el-avatar :size="28" src="https://api.dicebear.com/7.x/avataaars/svg?seed=Felix" />
            <span class="text-sm font-medium text-slate-700 dark:text-slate-200">{{ userStore.username || '未登录' }}</span>
            <el-icon><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>个人中心</el-dropdown-item>
              <el-dropdown-item>修改密码</el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon class="mr-1"><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <div class="flex flex-1 overflow-hidden bg-background transition-colors duration-200">
      <!-- Sidebar -->
      <aside
        class="border-r border-border-color bg-card flex flex-col h-full shrink-0 z-20 transition-all duration-200 overflow-hidden"
        :class="sidebarCollapsed ? 'w-16' : 'w-60'"
      >
        <nav class="flex-1 py-2 overflow-y-auto custom-scroll">
          <!-- Dashboard -->
          <router-link to="/" class="menu-item" :class="{ active: route.path === '/' }">
            <el-icon><Monitor /></el-icon>
            <span v-show="!sidebarCollapsed">Dashboard</span>
          </router-link>

          <!-- Grouped menus -->
          <template v-for="group in menuGroups" :key="group.label">
            <div v-show="!sidebarCollapsed" class="px-4 pt-4 pb-2 text-[11px] font-bold text-muted/60 dark:text-muted/40 uppercase tracking-widest">
              {{ group.label }}
            </div>
            <el-menu
              :default-active="route.path"
              :collapse="sidebarCollapsed"
              :collapse-transition="false"
              class="sidebar-menu"
              @select="handleMenuSelect"
            >
              <el-menu-item v-for="item in group.items" :key="item.path" :index="item.path">
                <el-icon><component :is="item.icon" /></el-icon>
                <template #title>{{ item.title }}</template>
              </el-menu-item>
            </el-menu>
          </template>
        </nav>
      </aside>

      <!-- Main Content -->
      <main class="flex-1 flex flex-col min-w-0 overflow-hidden bg-background">
        <!-- Tab Bar -->
        <div class="h-9 border-b border-border-color bg-card flex items-center px-4 space-x-1 shrink-0 overflow-x-auto no-scrollbar z-10 transition-colors duration-200">
          <div
            v-for="tab in tabStore.tabs"
            :key="tab.path"
            class="tab-pill group"
            :class="{ active: tabStore.activeTab === tab.path }"
            @click="navigateTo(tab.path)"
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

        <!-- Content -->
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
import { computed, ref, watch, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useThemeStore } from '../stores/theme';
import { useTabStore } from '../stores/tabs';
import { useUserStore } from '../stores/user';
import {
  Monitor, Cpu, Refresh, HomeFilled, Moon, Sunny, FullScreen, Bell, Setting,
  ArrowDown, Close, Expand, Fold,
  OfficeBuilding, House, User, Avatar, Clock,
  MapLocation, ScaleToOriginal, Switch,
  FolderOpened, Box, CircleCheck,
  SetUp, Notebook,
  Van, Lock, Key,
  InfoFilled, SwitchButton,
} from '@element-plus/icons-vue';

interface MenuItem {
  path: string;
  title: string;
  icon: string;
}

interface MenuGroup {
  label: string;
  items: MenuItem[];
}

const route = useRoute();
const router = useRouter();
const themeStore = useThemeStore();
const tabStore = useTabStore();
const userStore = useUserStore();

const sidebarCollapsed = ref(false);

onMounted(() => {
  if (userStore.isAuthenticated && !userStore.userInfo) {
    userStore.fetchUserInfo();
  }
});

const handleDropdownCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout();
    router.push('/login');
  }
};

const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value;
};

const menuGroups = computed<MenuGroup[]>(() => [
  {
    label: '组织架构',
    items: [
      { path: '/companies', title: '公司管理', icon: 'OfficeBuilding' },
      { path: '/factories', title: '工厂管理', icon: 'House' },
      { path: '/departments', title: '部门管理', icon: 'User' },
      { path: '/shift-schedule', title: '班制班次', icon: 'Clock' },
    ],
  },
  {
    label: '生产基础',
    items: [
      { path: '/work-centers', title: '工作中心', icon: 'MapLocation' },
      { path: '/uoms', title: '计量单位', icon: 'ScaleToOriginal' },
      { path: '/uom-conversions', title: '单位换算', icon: 'Switch' },
    ],
  },
  {
    label: '物料管理',
    items: [
      { path: '/material-categories', title: '物料分类', icon: 'FolderOpened' },
      { path: '/materials', title: '物料信息', icon: 'Box' },
      { path: '/inspection-exemptions', title: '免检清单', icon: 'CircleCheck' },
    ],
  },
  {
    label: '设备管理',
    items: [
      { path: '/equipment-types', title: '设备类型', icon: 'Cpu' },
      { path: '/equipment-models', title: '设备型号', icon: 'SetUp' },
      { path: '/equipment-ledger', title: '设备台账', icon: 'Notebook' },
    ],
  },
  {
    label: '供应链',
    items: [
      { path: '/customers', title: '客户管理', icon: 'Avatar' },
      { path: '/suppliers', title: '供应商管理', icon: 'Van' },
    ],
  },
  {
    label: '数据权限',
    items: [
      { path: '/data-permission-groups', title: '权限组管理', icon: 'Lock' },
      { path: '/user-data-permissions', title: '用户权限', icon: 'Key' },
    ],
  },
]);

const currentModule = computed(() => (route.meta?.module as string) || '');
const currentTitle = computed(() => (route.meta?.title as string) || '首页');

const handleMenuSelect = (path: string) => {
  router.push(path);
};

const handleRefresh = () => {
  window.location.reload();
};

const navigateTo = (path: string) => {
  router.push(path);
  tabStore.activeTab = path;
};

// Auto-add tab on route change
watch(() => route.path, (newPath) => {
  if (route.name) {
    tabStore.addTabFromRoute(route);
  }
}, { immediate: true });
</script>

<style scoped>
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

.sidebar-menu {
  border-right: none !important;
  background: transparent !important;
}
.sidebar-menu .el-menu-item {
  height: 40px;
  line-height: 40px;
  margin: 1px 8px;
  border-radius: 4px;
  font-size: 14px;
}
.sidebar-menu .el-menu-item:hover {
  background: rgba(24, 144, 255, 0.05);
}
.sidebar-menu .el-menu-item.is-active {
  background: rgba(24, 144, 255, 0.1);
  color: var(--el-color-primary);
  font-weight: 500;
}

.tab-pill {
  height: 32px;
  padding: 0 12px;
  background: var(--bg-card);
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
.el-breadcrumb__inner, .el-breadcrumb__inner a {
  font-weight: 400 !important;
  color: var(--text-muted) !important;
}
.el-breadcrumb__item:last-child .el-breadcrumb__inner {
  color: var(--text-main) !important;
}
</style>
