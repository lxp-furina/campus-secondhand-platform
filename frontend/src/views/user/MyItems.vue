<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <h2>我的发布</h2>
        <el-button type="primary" @click="$router.push('/publish')">发布新物品</el-button>
      </div>
      <el-table :data="items">
        <el-table-column label="图片" width="100">
          <template #default="{ row }"><img :src="row.images?.[0]?.imageUrl || placeholder" class="thumb" /></template>
        </el-table-column>
        <el-table-column label="标题" prop="item.title" />
        <el-table-column label="分类" prop="category.name" />
        <el-table-column label="价格" prop="item.price" />
        <el-table-column label="状态" prop="item.status" />
        <el-table-column label="操作" width="220">
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

const placeholder = 'https://dummyimage.com/120/e5e7eb/6b7280&text=Item'
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
.thumb {
  width: 64px;
  height: 64px;
  object-fit: cover;
  border-radius: 8px;
}
</style>
