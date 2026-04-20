import { defineStore } from 'pinia';
import { ref, watch } from 'vue';

export const useThemeStore = defineStore('theme', () => {
  // Default to light mode (null or 'light' -> false)
  const isDark = ref(localStorage.getItem('theme') === 'dark');

  const toggleTheme = () => {
    isDark.value = !isDark.value;
  };

  watch(isDark, (val) => {
    localStorage.setItem('theme', val ? 'dark' : 'light');
    const root = document.documentElement;
    if (val) {
      root.classList.add('dark');
    } else {
      root.classList.remove('dark');
    }
  }, { immediate: true });

  return { isDark, toggleTheme };
});
