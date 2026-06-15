<template>
  <div class="page">
    <el-card>
      <h2>我的订单</h2>
      <el-tabs v-model="tab" @tab-change="load">
        <el-tab-pane label="我买到的" name="buyer" />
        <el-tab-pane label="我卖出的" name="seller" />
      </el-tabs>
      <el-table :data="orders">
        <el-table-column label="订单号" prop="order.orderNo" min-width="170" />
        <el-table-column label="物品" prop="item.title" />
        <el-table-column label="金额" prop="order.amount" />
        <el-table-column label="状态" prop="order.status" />
        <el-table-column label="买家" prop="buyer.username" />
        <el-table-column label="卖家" prop="seller.username" />
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button v-if="tab === 'seller' && row.order.status === 'PENDING'" size="small" type="primary" @click="status(row.order.id, 'PROCESSING')">确认</el-button>
            <el-button v-if="tab === 'buyer' && row.order.status === 'PROCESSING'" size="small" type="success" @click="status(row.order.id, 'COMPLETED')">完成</el-button>
            <el-button v-if="['PENDING','PROCESSING'].includes(row.order.status)" size="small" type="danger" @click="status(row.order.id, 'CANCELLED')">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getBuyerOrders, getSellerOrders, updateOrderStatus } from '../../api/order'

const tab = ref('buyer')
const orders = ref([])

async function load() {
  orders.value = tab.value === 'buyer' ? await getBuyerOrders() : await getSellerOrders()
}
async function status(id, next) {
  await updateOrderStatus(id, next)
  await load()
}
onMounted(load)
</script>
