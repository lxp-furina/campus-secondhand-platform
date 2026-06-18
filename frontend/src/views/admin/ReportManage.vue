<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>举报处理</h2>
      <p class="page-subtitle">审核用户举报并处理违规物品</p>
    </div>
    <el-card>
      <div class="toolbar">
      <el-select v-model="query.status" clearable placeholder="举报状态" style="width: 180px" @change="load">
        <el-option label="待处理" value="PENDING" />
        <el-option label="已驳回" value="REJECTED" />
        <el-option label="已下架物品" value="ITEM_REMOVED" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      </div>
      <el-table :data="reports" stripe>
      <el-table-column prop="report.id" label="ID" width="80" />
      <el-table-column prop="item.title" label="被举报物品" />
      <el-table-column prop="reporter.username" label="举报人" />
      <el-table-column prop="report.reason" label="原因" />
      <el-table-column prop="report.status" label="状态" />
      <el-table-column prop="report.resultNote" label="处理备注" />
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button v-if="row.report.status === 'PENDING'" size="small" @click="open(row, 'REJECTED')">驳回</el-button>
          <el-button v-if="row.report.status === 'PENDING'" size="small" type="danger" @click="open(row, 'ITEM_REMOVED')">下架物品</el-button>
        </template>
      </el-table-column>
      </el-table>
      <el-pagination class="pager" layout="prev, pager, next" :total="total" :current-page="query.current" :page-size="query.size" @current-change="onPageChange" />
    </el-card>
    <el-dialog v-model="dialog" title="处理举报" width="420px">
      <el-input v-model="note" type="textarea" placeholder="处理备注" />
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="submit">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { getReports, handleReport } from '../../api/report'

const reports = ref([])
const total = ref(0)
const query = reactive({ status: 'PENDING', current: 1, size: 10 })
const dialog = ref(false)
const current = ref(null)
const nextStatus = ref('')
const note = ref('')

async function load() {
  const page = await getReports(query)
  reports.value = page.records || []
  total.value = page.total || 0
}
function onPageChange(page) {
  query.current = page
  load()
}
function open(row, status) {
  current.value = row
  nextStatus.value = status
  note.value = ''
  dialog.value = true
}
async function submit() {
  await handleReport(current.value.report.id, { status: nextStatus.value, note: note.value })
  dialog.value = false
  await load()
}
onMounted(load)
</script>

<style scoped>
.admin-page {
  animation: pageReveal var(--duration-slow) var(--ease-out-expo) both;
}
.pager {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
