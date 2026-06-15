<template>
  <div class="page auth">
    <el-card>
      <h2>学号注册</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学号" prop="studentNo"><el-input v-model="form.studentNo" /></el-form-item>
        <el-form-item label="昵称" prop="username"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="密码" prop="password"><el-input v-model="form.password" type="password" show-password /></el-form-item>
        <el-form-item label="手机"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-button type="primary" @click="submit">注册并登录</el-button>
        <el-button link @click="$router.push('/login')">已有账号</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '../../api/auth'
import { useAuthStore } from '../../store/auth'

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
.auth {
  max-width: 520px;
}
</style>
