<template>
  <PageScene variant="auth">
    <div class="auth-page">
    <div class="auth-visual">
      <div class="auth-visual-content">
        <img src="../../assets/images/brand-mark.png" alt="" class="brand-mark" />
        <div class="deco-line"></div>
        <h1>欢迎回到<br /><em>HIT</em></h1>
        <p>以优雅的方式，发现与分享校园里的每一件精致闲置。</p>
      </div>
    </div>
    <div class="auth-form-panel">
      <div class="auth-card card">
        <h2>用户登录</h2>
        <p class="auth-hint">使用学号或昵称登录您的账户</p>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="70px" label-position="top">
          <el-form-item label="账号" prop="account">
            <el-input v-model="form.account" placeholder="学号或昵称" size="large" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" size="large" />
          </el-form-item>
          <div class="form-actions">
            <el-button type="primary" size="large" @click="submit">登录</el-button>
            <el-button link @click="$router.push('/register')">注册账号 →</el-button>
          </div>
        </el-form>
      </div>
    </div>
    </div>
  </PageScene>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../../api/auth'
import { useAuthStore } from '../../store/auth'
import PageScene from '../../components/PageScene.vue'

const formRef = ref()
const auth = useAuthStore()
const saved = auth.loadCredentials('USER')
const form = reactive({ account: saved.account, password: saved.password })
const rules = {
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}
const router = useRouter()

async function submit() {
  await formRef.value.validate()
  const data = await login(form)
  auth.setSession(data)
  auth.saveCredentials(form.account, form.password, 'USER')
  router.push('/')
}
</script>

<style scoped>
:deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--color-text-soft);
  letter-spacing: 0.02em;
}
</style>
