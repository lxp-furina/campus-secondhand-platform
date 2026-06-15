<template>
  <div class="admin-login">
    <el-card class="box">
      <h2>管理员登录</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="70px">
        <el-form-item label="账号" prop="account"><el-input v-model="form.account" /></el-form-item>
        <el-form-item label="密码" prop="password"><el-input v-model="form.password" type="password" show-password /></el-form-item>
        <el-button type="primary" @click="submit">登录后台</el-button>
      </el-form>
    </el-card>
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
  display: flex;
  align-items: center;
  justify-content: center;
  background: #111827;
}
.box {
  width: 430px;
}
</style>
