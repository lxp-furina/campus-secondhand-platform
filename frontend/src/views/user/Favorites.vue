<template>
  <div class="page">
    <el-card>
      <h2>我的收藏</h2>
      <div class="grid" v-if="items.length">
        <el-card v-for="v in items" :key="v.item.id" class="item-card">
          <img :src="v.images?.[0]?.imageUrl || placeholder" class="cover" @click="goDetail(v.item.id)" />
          <h3 @click="goDetail(v.item.id)">{{ v.item.title }}</h3>
          <p class="muted">{{ v.category?.name }} · {{ v.item.conditionLevel }}</p>
          <p class="price">￥{{ v.item.price }}</p>
          <p class="muted">状态：{{ statusText(v.item.status) }}</p>
          <div class="actions">
            <el-button size="small" type="primary" @click="goDetail(v.item.id)">查看详情</el-button>
            <el-button size="small" @click="remove(v.item.id)">取消收藏</el-button>
          </div>
        </el-card>
      </div>
      <el-empty v-else description="还没有收藏任何物品" />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMyFavorites, unfavoriteItem } from '../../api/item'

const router = useRouter()
const placeholder = 'https://dummyimage.com/600x400/e5e7eb/6b7280&text=Campus'
const items = ref([])

const statusMap = {
  ON_SALE: '在售',
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
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 18px;
  margin-top: 16px;
}
.cover {
  width: 100%;
  height: 170px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
}
.item-card h3 {
  cursor: pointer;
}
.price {
  color: #e11d48;
  font-weight: 700;
}
.actions {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}
</style>
