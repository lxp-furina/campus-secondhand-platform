<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>用户管理</h2>
      <p class="page-subtitle">管理平台注册用户</p>
    </div>
    <el-card>
      <div class="toolbar">
        <el-input v-model="query.keyword" placeholder="学号/昵称" clearable style="width: 260px" />
        <el-button type="primary" @click="load">查询</el-button>
      </div>
      <el-table :data="users" stripe>
      <el-table-column prop="studentNo" label="学号" />
      <el-table-column prop="username" label="昵称" />
      <el-table-column prop="phone" label="手机" />
      <el-table-column prop="role" label="角色" />
      <el-table-column prop="status" label="状态" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button v-if="row.status === 'ENABLED' && row.role !== 'ADMIN'" type="danger" size="small" @click="setStatus(row.id, 'DISABLED')">禁用</el-button>
          <el-button v-if="row.status === 'DISABLED'" type="success" size="small" @click="setStatus(row.id, 'ENABLED')">启用</el-button>
        </template>
      </el-table-column>
      </el-table>
      <el-pagination class="pager" layout="prev, pager, next" :total="total" :current-page="query.current" :page-size="query.size" @current-change="onPageChange" />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { getUsers, updateUserStatus } from '../../api/admin'

const users = ref([])
const total = ref(0)
const query = reactive({ keyword: '', current: 1, size: 10 })

async function load() {
  const page = await getUsers(query)
  users.value = page.records || []
  total.value = page.total || 0
}
function onPageChange(page) {
  query.current = page
  load()
}
async function setStatus(id, status) {
  await updateUserStatus(id, status)
  await load()
}
onMounted(load)
</script>

<style scoped>
.admin-page {
  animation: pageReveal var(--duration-slow) var(--ease-out-expo) both;
}
.pager {
  margin-top: 20px;
  justify-content: center;
  display: flex;
}
</style>
