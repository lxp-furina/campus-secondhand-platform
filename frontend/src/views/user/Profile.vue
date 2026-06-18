<template>
  <div class="page profile">
    <el-card class="profile-card">
      <div class="page-header">
        <h2>个人信息</h2>
        <p class="page-subtitle">管理您的账户资料</p>
      </div>
      <el-form :model="profile" label-width="90px" label-position="top">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学号"><el-input v-model="profile.studentNo" disabled /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="昵称"><el-input v-model="profile.username" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机"><el-input v-model="profile.phone" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱"><el-input v-model="profile.email" /></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="头像 URL"><el-input v-model="profile.avatar" placeholder="输入头像图片地址" /></el-form-item>
          </el-col>
        </el-row>
        <el-button type="primary" @click="saveProfile">保存资料</el-button>
      </el-form>
    </el-card>

    <el-card class="profile-card">
      <div class="page-header">
        <h2>修改密码</h2>
        <p class="page-subtitle">定期更新密码，保障账户安全</p>
      </div>
      <el-form ref="pwdRef" :model="password" :rules="rules" label-width="90px" label-position="top">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="password.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="password.newPassword" type="password" show-password />
        </el-form-item>
        <el-button type="primary" @click="savePassword">修改密码</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getProfile, updatePassword, updateProfile } from '../../api/auth'
import { useAuthStore } from '../../store/auth'

const auth = useAuthStore()
const pwdRef = ref()
const profile = reactive({ studentNo: '', username: '', phone: '', email: '', avatar: '' })
const password = reactive({ oldPassword: '', newPassword: '' })
const rules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [{ required: true, min: 6, message: '新密码至少6位', trigger: 'blur' }]
}

async function load() {
  Object.assign(profile, await getProfile())
}
async function saveProfile() {
  const user = await updateProfile(profile)
  auth.setUser(user)
  ElMessage.success('资料已保存')
}
async function savePassword() {
  await pwdRef.value.validate()
  await updatePassword(password)
  password.oldPassword = ''
  password.newPassword = ''
  ElMessage.success('密码已修改')
}
onMounted(load)
</script>

<style scoped>
.profile {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  align-items: start;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--color-text-soft);
}

@media (max-width: 768px) {
  .profile {
    grid-template-columns: 1fr;
  }
}
</style>
