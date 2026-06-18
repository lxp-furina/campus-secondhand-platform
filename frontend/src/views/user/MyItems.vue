<template>
  <div class="page">
    <div class="toolbar card page-toolbar">
      <div>
        <h2>我的发布</h2>
        <p class="page-subtitle">管理您发布的闲置物品</p>
      </div>
      <el-button type="primary" @click="$router.push('/publish')">+ 发布新物品</el-button>
    </div>

    <el-card class="table-card">
      <el-table :data="items" stripe>
        <el-table-column label="图片" width="90">
          <template #default="{ row }">
            <img :src="row.images?.[0]?.imageUrl || placeholder" class="thumb" />
          </template>
        </el-table-column>
        <el-table-column label="标题" prop="item.title" min-width="160" />
        <el-table-column label="分类" prop="category.name" width="100" />
        <el-table-column label="价格" width="100">
          <template #default="{ row }">
            <span class="price-sm">{{ row.item.price }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="item.status" width="100" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="$router.push(`/items/${row.item.id}/edit`)">编辑</el-button>
            <el-button size="small" type="warning" @click="off(row.item.id)">下架</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getMyItems, offShelfItem } from '../../api/item'

const placeholder = 'https://dummyimage.com/120/ede6d8/6b7c72&text=Item'
const items = ref([])

async function load() {
  items.value = await getMyItems()
}
async function off(id) {
  await offShelfItem(id)
  await load()
}
onMounted(load)
</script>

<style scoped>
.page-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-toolbar h2 {
  margin-bottom: 4px;
}

.thumb {
  width: 56px;
  height: 56px;
  object-fit: cover;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border-gold);
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
