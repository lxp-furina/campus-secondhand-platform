<template>
  <div class="admin-shell">
    <aside class="side">
      <h3>管理后台</h3>
      <el-menu router :default-active="$route.path" background-color="#111827" text-color="#cbd5e1" active-text-color="#fff">
        <el-menu-item index="/admin/dashboard">数据概览</el-menu-item>
        <el-menu-item index="/admin/users">用户管理</el-menu-item>
        <el-menu-item index="/admin/items">物品管理</el-menu-item>
        <el-menu-item index="/admin/orders">订单管理</el-menu-item>
        <el-menu-item index="/admin/reports">举报处理</el-menu-item>
      </el-menu>
      <el-button class="logout" @click="logout">退出后台</el-button>
    </aside>
    <main class="main"><router-view /></main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'

const auth = useAuthStore()
const router = useRouter()

function logout() {
  auth.logout()
  router.push('/admin/login')
}
</script>

<style scoped>
.admin-shell {
  display: flex;
  min-height: 100vh;
}
.side {
  width: 220px;
  background: #111827;
  color: #fff;
  padding: 20px 0;
}
.side h3 {
  padding: 0 20px;
}
.main {
  flex: 1;
  padding: 24px;
}
.logout {
  margin: 20px;
}
</style>
