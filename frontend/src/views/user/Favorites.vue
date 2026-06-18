<template>
  <div class="page">
    <div class="page-header">
      <h2>我的收藏</h2>
      <p class="page-subtitle">您心仪的物品，随时查看</p>
    </div>

    <div class="grid" v-if="items.length">
      <el-card v-for="v in items" :key="v.item.id" class="item-card">
        <div class="cover-wrap">
          <img :src="v.images?.[0]?.imageUrl || placeholder" class="cover" @click="goDetail(v.item.id)" />
        </div>
        <h3 @click="goDetail(v.item.id)">{{ v.item.title }}</h3>
        <div class="item-meta">
          <span class="tag-pill">{{ v.category?.name }}</span>
          <span class="tag-pill condition">{{ v.item.conditionLevel }}</span>
        </div>
        <p class="price">{{ v.item.price }}</p>
        <p class="muted status-line">状态：{{ statusText(v.item.status) }}</p>
        <div class="actions">
          <el-button size="small" type="primary" @click="goDetail(v.item.id)">查看详情</el-button>
          <el-button size="small" @click="remove(v.item.id)">取消收藏</el-button>
        </div>
      </el-card>
    </div>

    <div class="empty-wrap" v-else>
      <el-empty description="还没有收藏任何物品">
        <el-button type="primary" @click="$router.push('/')">去发现好物</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMyFavorites, unfavoriteItem } from '../../api/item'

const router = useRouter()
const placeholder = 'https://dummyimage.com/600x400/ede6d8/6b7c72&text=Campus+Atelier'
const items = ref([])

const statusMap = {
  PENDING_REVIEW: '审核中',
  ON_SALE: '在售',
  REJECTED: '审核未通过',
  OFF_SHELF: '已下架',
  SOLD: '已售出'
}

function statusText(status) {
  return statusMap[status] || status
}

async function load() {
  items.value = await getMyFavorites()
}

function goDetail(id) {
  router.push(`/items/${id}`)
}

async function remove(id) {
  await unfavoriteItem(id)
  ElMessage.success('已取消收藏')
  await load()
}

onMounted(load)
</script>

<style scoped>
.item-card h3 {
  cursor: pointer;
}

.tag-pill {
  display: inline-flex;
  padding: 2px 10px;
  border-radius: 100px;
  font-size: 0.75rem;
  background: rgba(30, 82, 64, 0.08);
  color: var(--color-emerald);
  border: 1px solid rgba(30, 82, 64, 0.12);
}

.tag-pill.condition {
  background: rgba(184, 149, 106, 0.1);
  color: var(--color-gold);
  border-color: rgba(184, 149, 106, 0.2);
}

.item-meta {
  display: flex;
  gap: 8px;
  margin: 8px 0;
}

.status-line {
  margin: 8px 0;
}

.actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--border-gold);
}

.empty-wrap {
  padding: 48px 0;
}
</style>
