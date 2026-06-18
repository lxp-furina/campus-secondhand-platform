<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>订单管理</h2>
      <p class="page-subtitle">查看全平台订单记录</p>
    </div>
    <el-card>
      <div class="toolbar">
      <el-select v-model="query.status" clearable placeholder="订单状态" style="width: 180px" @change="load">
        <el-option label="待确认" value="PENDING" />
        <el-option label="进行中" value="PROCESSING" />
        <el-option label="已完成" value="COMPLETED" />
        <el-option label="已取消" value="CANCELLED" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      </div>
      <el-table :data="orders" stripe>
      <el-table-column prop="order.orderNo" label="订单号" min-width="170" />
      <el-table-column prop="item.title" label="物品" />
      <el-table-column prop="buyer.username" label="买家" />
      <el-table-column prop="seller.username" label="卖家" />
      <el-table-column prop="order.amount" label="金额" />
      <el-table-column prop="order.status" label="状态" />
      </el-table>
      <el-pagination class="pager" layout="prev, pager, next" :total="total" :current-page="query.current" :page-size="query.size" @current-change="onPageChange" />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { getAdminOrders } from '../../api/order'

const orders = ref([])
const total = ref(0)
const query = reactive({ status: '', current: 1, size: 10 })

async function load() {
  const page = await getAdminOrders(query)
  orders.value = page.records || []
  total.value = page.total || 0
}
function onPageChange(page) {
  query.current = page
  load()
}
onMounted(load)
</script>

<style scoped>
.admin-page {
  animation: pageReveal var(--duration-slow) var(--ease-out-expo) both;
}
.pager {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
