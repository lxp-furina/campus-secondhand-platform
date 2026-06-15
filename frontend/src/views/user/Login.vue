<template>
  <div class="page auth">
    <el-card>
      <h2>用户登录</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="70px">
        <el-form-item label="账号" prop="account">
          <el-input v-model="form.account" placeholder="学号或昵称" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-button type="primary" @click="submit">登录</el-button>
        <el-button link @click="$router.push('/register')">注册账号</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../../api/auth'
import { useAuthStore } from '../../store/auth'

const formRef = ref()
const form = reactive({ account: '', password: '' })
const rules = {
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}
const router = useRouter()
const auth = useAuthStore()

async function submit() {
  await formRef.value.validate()
  const data = await login(form)
  auth.setSession(data)
  router.push('/')
}
</script>

<style scoped>
.auth {
  max-width: 460px;
}
</style>
