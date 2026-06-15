<template>
  <div>
    <h2>数据概览</h2>
    <div class="cards" v-if="data">
      <el-card><p>用户总数</p><h1>{{ data.userTotal }}</h1></el-card>
      <el-card><p>物品总数</p><h1>{{ data.itemTotal }}</h1></el-card>
      <el-card><p>订单总数</p><h1>{{ data.orderTotal }}</h1></el-card>
      <el-card><p>待处理举报</p><h1>{{ data.pendingReportTotal }}</h1></el-card>
    </div>
    <el-card class="mt" v-if="data">
      <h3>订单状态</h3>
      <el-tag v-for="(count, status) in data.orderStatus" :key="status" class="tag">{{ status }}：{{ count }}</el-tag>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getDashboard } from '../../api/admin'

const data = ref(null)
onMounted(async () => {
  data.value = await getDashboard()
})
</script>

<style scoped>
.cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 18px;
}
.mt {
  margin-top: 18px;
}
.tag {
  margin-right: 12px;
}
</style>
