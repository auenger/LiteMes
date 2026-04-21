import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '');
  const username = ref<string>('');
  const roles = ref<string[]>([]);

  function setToken(newToken: string) {
    token.value = newToken;
    localStorage.setItem('token', newToken);
  }

  function clearToken() {
    token.value = '';
    username.value = '';
    roles.value = [];
    localStorage.removeItem('token');
  }

  function isAuthenticated() {
    return !!token.value;
  }

  return {
    token,
    username,
    roles,
    setToken,
    clearToken,
    isAuthenticated,
  };
});
