<template>
  <div class="page">
    <div class="page-header">
      <h2>我的订单</h2>
      <p class="page-subtitle">追踪您的买入与卖出记录</p>
    </div>

    <el-card>
      <el-tabs v-model="tab" @tab-change="load" class="order-tabs">
        <el-tab-pane label="我买到的" name="buyer" />
        <el-tab-pane label="我卖出的" name="seller" />
      </el-tabs>
      <el-table :data="orders" stripe>
        <el-table-column label="订单号" prop="order.orderNo" min-width="170" />
        <el-table-column label="物品" prop="item.title" min-width="140" />
        <el-table-column label="金额" width="100">
          <template #default="{ row }">
            <span class="price-sm">{{ row.order.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="order.status" width="100" />
        <el-table-column label="买家" prop="buyer.username" width="100" />
        <el-table-column label="卖家" prop="seller.username" width="100" />
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

<style scoped>
.order-tabs {
  margin-bottom: 8px;
}

.price-sm {
  font-family: var(--font-display);
  color: var(--color-burgundy);
  font-weight: 600;
}

.price-sm::before {
  content: '¥';
  font-size: 0.85em;
}
</style>
