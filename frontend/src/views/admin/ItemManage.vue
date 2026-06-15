<template>
  <div>
    <h2>物品管理</h2>
    <div class="toolbar">
      <el-input v-model="query.keyword" placeholder="标题/描述" clearable style="width: 240px" />
      <el-select v-model="query.status" clearable placeholder="状态" style="width: 160px">
        <el-option label="上架" value="ON_SALE" />
        <el-option label="下架" value="OFF_SHELF" />
        <el-option label="已售出" value="SOLD" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
    </div>
    <el-table :data="items">
      <el-table-column prop="item.id" label="ID" width="80" />
      <el-table-column prop="item.title" label="标题" />
      <el-table-column prop="category.name" label="分类" />
      <el-table-column prop="seller.username" label="卖家" />
      <el-table-column prop="item.price" label="价格" />
      <el-table-column prop="item.status" label="状态" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button v-if="row.item.status === 'ON_SALE'" type="danger" size="small" @click="off(row.item.id)">违规下架</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination class="pager" layout="prev, pager, next" :total="total" v-model:current-page="query.current" :page-size="query.size" @current-change="load" />
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { adminOffShelfItem, getAdminItems } from '../../api/item'

const items = ref([])
const total = ref(0)
const query = reactive({ keyword: '', status: '', current: 1, size: 10 })

async function load() {
  const page = await getAdminItems(query)
  items.value = page.records || []
  total.value = page.total || 0
}
async function off(id) {
  await adminOffShelfItem(id)
  await load()
}
onMounted(load)
</script>

<style scoped>
.pager {
  margin-top: 16px;
}
</style>
