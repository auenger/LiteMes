import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { login as loginApi, getUserInfo as getUserInfoApi, type UserInfo } from '../api/auth';

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '');
  const userInfo = ref<UserInfo | null>(null);

  const isAuthenticated = computed(() => !!token.value);
  const username = computed(() => userInfo.value?.realName || userInfo.value?.username || '');
  const roles = computed(() => userInfo.value?.roles || []);

  async function login(user: string, password: string) {
    const res = await loginApi(user, password);
    if (res.code === 200 && res.data) {
      token.value = res.data.token;
      localStorage.setItem('token', res.data.token);
      await fetchUserInfo();
      return true;
    }
    throw new Error(res.message || '登录失败');
  }

  async function fetchUserInfo() {
    try {
      const res = await getUserInfoApi();
      if (res.code === 200 && res.data) {
        userInfo.value = res.data;
      }
    } catch {
      userInfo.value = null;
    }
  }

  function logout() {
    token.value = '';
    userInfo.value = null;
    localStorage.removeItem('token');
  }

  return {
    token,
    userInfo,
    isAuthenticated,
    username,
    roles,
    login,
    logout,
    fetchUserInfo,
  };
});
