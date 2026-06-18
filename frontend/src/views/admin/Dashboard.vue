<template>
  <div class="dashboard">
    <div class="page-header">
      <h2>数据概览</h2>
      <p class="page-subtitle">平台运营数据一览</p>
    </div>

    <div class="stat-cards" v-if="data">
      <div class="stat-card" v-for="(item, i) in statItems" :key="item.key">
        <div class="stat-card-inner" :style="{ animationDelay: `${i * 0.08}s` }">
          <span class="stat-icon">{{ item.icon }}</span>
          <p class="stat-label">{{ item.label }}</p>
          <h1 class="stat-value">{{ data[item.key] }}</h1>
        </div>
      </div>
    </div>

    <el-card class="status-card mt" v-if="data">
      <h3>订单状态分布</h3>
      <div class="tag-row">
        <el-tag v-for="(count, status) in data.orderStatus" :key="status" class="status-tag" effect="plain">
          {{ status }}：{{ count }}
        </el-tag>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getDashboard } from '../../api/admin'

const data = ref(null)

const statItems = computed(() => [
  { key: 'userTotal', label: '用户总数', icon: '◉' },
  { key: 'itemTotal', label: '物品总数', icon: '◇' },
  { key: 'orderTotal', label: '订单总数', icon: '◆' },
  { key: 'pendingReportTotal', label: '待处理举报', icon: '◎' }
])

onMounted(async () => {
  data.value = await getDashboard()
})
</script>

<style scoped>
.dashboard {
  animation: pageReveal var(--duration-slow) var(--ease-out-expo) both;
}

.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-card-inner {
  padding: 24px 28px;
  background: var(--surface-glass-strong);
  border: 1px solid var(--border-gold);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
  transition: all var(--duration-normal) var(--ease-out-expo);
  animation: slideUp var(--duration-slow) var(--ease-out-expo) both;
}

.stat-card-inner:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-elevated);
  border-color: var(--border-gold-strong);
}

.stat-icon {
  font-size: 1.2rem;
  color: var(--color-gold);
  opacity: 0.8;
}

.stat-label {
  color: var(--color-muted);
  font-size: 0.85rem;
  margin: 12px 0 6px;
  letter-spacing: 0.04em;
}

.stat-value {
  font-family: var(--font-display);
  font-size: 2.4rem;
  color: var(--color-emerald);
  margin: 0;
  line-height: 1;
}

.status-card h3 {
  font-size: 1.1rem;
  margin-bottom: 16px;
}

.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.status-tag {
  font-size: 0.85rem !important;
}

.mt {
  margin-top: 24px;
}

@media (max-width: 900px) {
  .stat-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
