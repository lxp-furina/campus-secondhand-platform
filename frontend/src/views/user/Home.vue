<template>
  <div class="page">
    <div class="toolbar card">
      <el-select v-model="query.categoryId" clearable placeholder="分类" style="width: 180px" @change="loadItems">
        <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <el-input v-model="query.keyword" clearable placeholder="搜索物品关键词" style="width: 320px" @keyup.enter="loadItems" />
      <el-button type="primary" @click="loadItems">搜索</el-button>
      <el-button @click="$router.push('/publish')">发布闲置</el-button>
    </div>
    <div class="grid">
      <el-card v-for="v in items" :key="v.item.id" class="item-card" @click="$router.push(`/items/${v.item.id}`)">
        <img :src="v.images?.[0]?.imageUrl || placeholder" class="cover" />
        <h3>{{ v.item.title }}</h3>
        <p class="muted">{{ v.category?.name }} · {{ v.item.conditionLevel }}</p>
        <p class="price">￥{{ v.item.price }}</p>
      </el-card>
    </div>
    <el-empty v-if="items.length === 0" description="暂无物品" />
    <div class="pager">
      <el-pagination layout="prev, pager, next" :total="total" :page-size="query.size" v-model:current-page="query.current" @current-change="loadItems" />
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { getCategories, getItems } from '../../api/item'

const placeholder = 'https://dummyimage.com/600x400/e5e7eb/6b7280&text=Campus'
const categories = ref([])
const items = ref([])
const total = ref(0)
const query = reactive({ categoryId: undefined, keyword: '', current: 1, size: 12 })

async function loadItems() {
  const page = await getItems(query)
  items.value = page.records || []
  total.value = page.total || 0
}

onMounted(async () => {
  categories.value = await getCategories()
  await loadItems()
})
</script>

<style scoped>
.cover {
  width: 100%;
  height: 170px;
  object-fit: cover;
  border-radius: 8px;
}
.item-card {
  cursor: pointer;
}
.pager {
  display: flex;
  justify-content: center;
  padding: 24px;
}
</style>
