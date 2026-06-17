<template>
  <div>
    <el-menu mode="horizontal" router :default-active="$route.path" class="nav">
      <el-menu-item index="/">校园闲置</el-menu-item>
      <el-menu-item index="/publish">发布物品</el-menu-item>
      <el-menu-item index="/mine/items">我的发布</el-menu-item>
      <el-menu-item index="/mine/favorites">我的收藏</el-menu-item>
      <el-menu-item index="/orders">我的订单</el-menu-item>
      <el-menu-item index="/profile">个人中心</el-menu-item>
      <div class="spacer"></div>
      <el-menu-item v-if="!auth.isLogin" index="/login">登录</el-menu-item>
      <el-menu-item v-else @click="logout">退出</el-menu-item>
    </el-menu>
    <router-view />
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'

const auth = useAuthStore()
const router = useRouter()

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.nav {
  padding: 0 24px;
}
.spacer {
  flex: 1;
}
</style>
