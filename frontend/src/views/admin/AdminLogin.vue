<template>
  <div class="admin-login">
    <div class="login-visual">
      <img src="../../assets/images/hero-bg.png" alt="" class="visual-bg" />
      <div class="visual-overlay"></div>
      <div class="visual-content">
        <img src="../../assets/images/brand-mark.png" alt="" class="brand-mark" />
        <h1>管理后台</h1>
        <p>HIT · Campus Atelier</p>
      </div>
    </div>
    <div class="login-form">
      <div class="login-card card">
        <h2>管理员登录</h2>
        <p class="auth-hint">请输入管理员凭据</p>
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
          <el-form-item label="账号" prop="account">
            <el-input v-model="form.account" size="large" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" show-password size="large" />
          </el-form-item>
          <el-button type="primary" size="large" style="width: 100%" @click="submit">登录后台</el-button>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { adminLogin } from '../../api/auth'
import { useAuthStore } from '../../store/auth'

const formRef = ref()
const form = reactive({ account: 'admin', password: 'admin123' })
const rules = {
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}
const router = useRouter()
const auth = useAuthStore()

async function submit() {
  await formRef.value.validate()
  auth.setSession(await adminLogin(form))
  router.push('/admin/dashboard')
}
</script>

<style scoped>
.admin-login {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1fr 1fr;
}

.login-visual {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.visual-bg {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.visual-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(160deg, rgba(10, 26, 18, 0.92), rgba(30, 82, 64, 0.75));
}

.visual-content {
  position: relative;
  z-index: 1;
  text-align: center;
  color: var(--color-ivory);
}

.brand-mark {
  width: 72px;
  height: 72px;
  margin-bottom: 24px;
  filter: drop-shadow(0 4px 16px rgba(0, 0, 0, 0.3));
}

.visual-content h1 {
  font-family: var(--font-display);
  font-size: 2.4rem;
  color: var(--color-ivory);
  margin-bottom: 8px;
}

.visual-content p {
  font-family: var(--font-display);
  font-style: italic;
  color: var(--color-gold-light);
  letter-spacing: 0.1em;
}

.login-form {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: var(--color-parchment);
}

.login-card {
  width: 100%;
  max-width: 400px;
  animation: slideUp var(--duration-slow) var(--ease-out-expo) both;
}

.login-card h2 {
  font-family: var(--font-display);
  font-size: 1.8rem;
}

.auth-hint {
  color: var(--color-muted);
  margin-bottom: 24px;
  font-size: 0.92rem;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--color-text-soft);
}

@media (max-width: 768px) {
  .admin-login {
    grid-template-columns: 1fr;
  }

  .login-visual {
    min-height: 240px;
  }
}
</style>
