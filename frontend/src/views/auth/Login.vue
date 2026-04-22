<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <div class="login-logo">
          <div class="logo-icon">
            <el-icon :size="28"><Cpu /></el-icon>
          </div>
          <span class="logo-text">LiteMes</span>
        </div>
        <p class="login-subtitle">PCB 轻量级制造执行系统</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="用户名"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            size="large"
            show-password
            :prefix-icon="Lock"
          />
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="form.remember">记住我</el-checkbox>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { User, Lock, Cpu } from '@element-plus/icons-vue';
import type { FormInstance, FormRules } from 'element-plus';
import { useUserStore } from '../../stores/user';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const formRef = ref<FormInstance>();
const loading = ref(false);

const form = reactive({
  username: '',
  password: '',
  remember: false,
});

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
};

const handleLogin = async () => {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;

  loading.value = true;
  try {
    await userStore.login(form.username, form.password);
    ElMessage.success('登录成功');
    const redirect = (route.query.redirect as string) || '/';
    router.push(redirect);
  } catch (e: unknown) {
    const message = e instanceof Error ? e.message : '登录失败';
    ElMessage.error(message);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 380px;
  padding: 40px 36px 32px;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-bottom: 8px;
}

.logo-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: linear-gradient(135deg, #1890ff, #096dd9);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.logo-text {
  font-size: 24px;
  font-weight: 700;
  color: #262626;
  letter-spacing: 1px;
}

.login-subtitle {
  font-size: 13px;
  color: #8c8c8c;
  margin-top: 4px;
}

.login-btn {
  width: 100%;
}

:deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #d9d9d9 inset !important;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #4096c0 inset !important;
}
</style>
