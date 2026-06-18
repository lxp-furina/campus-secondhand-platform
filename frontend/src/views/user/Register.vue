<template>
  <PageScene variant="auth">
    <div class="auth-page">
    <div class="auth-visual">
      <div class="auth-visual-content">
        <img src="../../assets/images/brand-mark.png" alt="" class="brand-mark" />
        <div class="deco-line"></div>
        <h1>加入<br /><em>HIT</em></h1>
        <p>注册成为会员，开启您的校园闲置交易之旅。</p>
      </div>
    </div>
    <div class="auth-form-panel">
      <div class="auth-card card">
        <h2>学号注册</h2>
        <p class="auth-hint">填写信息，即刻开启优雅交易体验</p>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" label-position="top">
          <el-form-item label="学号" prop="studentNo">
            <el-input v-model="form.studentNo" placeholder="请输入学号" />
          </el-form-item>
          <el-form-item label="昵称" prop="username">
            <el-input v-model="form.username" placeholder="您的昵称" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" show-password placeholder="至少6位" />
          </el-form-item>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="手机">
                <el-input v-model="form.phone" placeholder="选填" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="邮箱">
                <el-input v-model="form.email" placeholder="选填" />
              </el-form-item>
            </el-col>
          </el-row>
          <div class="form-actions">
            <el-button type="primary" size="large" @click="submit">注册并登录</el-button>
            <el-button link @click="$router.push('/login')">已有账号 →</el-button>
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
import { register } from '../../api/auth'
import { useAuthStore } from '../../store/auth'
import PageScene from '../../components/PageScene.vue'

const formRef = ref()
const form = reactive({ studentNo: '', username: '', password: '', phone: '', email: '' })
const rules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  username: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  password: [{ required: true, min: 6, message: '密码至少6位', trigger: 'blur' }]
}
const router = useRouter()
const auth = useAuthStore()

async function submit() {
  await formRef.value.validate()
  const data = await register(form)
  auth.setSession(data)
  router.push('/')
}
</script>

<style scoped>
.auth-card {
  max-width: 480px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--color-text-soft);
}
</style>
