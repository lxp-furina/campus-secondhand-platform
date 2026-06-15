<template>
  <div class="page" v-if="detail">
    <div class="detail card">
      <div>
        <el-carousel height="360px">
          <el-carousel-item v-for="img in detail.images" :key="img.id">
            <img :src="img.imageUrl" class="image" />
          </el-carousel-item>
          <el-carousel-item v-if="!detail.images?.length">
            <img :src="placeholder" class="image" />
          </el-carousel-item>
        </el-carousel>
      </div>
      <div>
        <h1>{{ detail.item.title }}</h1>
        <p class="price">￥{{ detail.item.price }}</p>
        <p>分类：{{ detail.category?.name }}</p>
        <p>成色：{{ detail.item.conditionLevel }}</p>
        <p>卖家：{{ detail.seller?.username }} {{ detail.seller?.phone }}</p>
        <p class="desc">{{ detail.item.description }}</p>
        <el-button type="primary" @click="buy">下单</el-button>
        <el-button @click="toggleFavorite">{{ detail.favorite ? '取消收藏' : '收藏' }}</el-button>
        <el-button type="danger" plain @click="reportDialog = true">举报</el-button>
      </div>
    </div>

    <div class="card messages">
      <h2>留言咨询</h2>
      <el-form :model="messageForm">
        <el-input v-model="messageForm.content" type="textarea" placeholder="输入留言内容" />
        <el-button type="primary" class="mt" @click="sendMessage()">留言</el-button>
      </el-form>
      <div v-for="m in messages" :key="m.message.id" class="message">
        <b>{{ m.user?.username }}：</b>{{ m.message.content }}
        <el-button link @click="replyTo = m.message.id">回复</el-button>
        <div class="reply" v-for="r in m.replies" :key="r.message.id">
          <b>{{ r.user?.username }}：</b>{{ r.message.content }}
        </div>
      </div>
      <el-input v-if="replyTo" v-model="replyContent" placeholder="回复内容" class="mt">
        <template #append><el-button @click="sendReply">发送回复</el-button></template>
      </el-input>
    </div>

    <el-dialog v-model="reportDialog" title="提交举报" width="420px">
      <el-input v-model="reportReason" type="textarea" placeholder="填写举报原因" />
      <template #footer>
        <el-button @click="reportDialog = false">取消</el-button>
        <el-button type="danger" @click="submitReport">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createOrder } from '../../api/order'
import { createMessage, getMessages } from '../../api/message'
import { createReport } from '../../api/report'
import { favoriteItem, getItemDetail, unfavoriteItem } from '../../api/item'
import { useAuthStore } from '../../store/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const detail = ref(null)
const messages = ref([])
const messageForm = reactive({ content: '' })
const replyTo = ref(null)
const replyContent = ref('')
const reportDialog = ref(false)
const reportReason = ref('')
const placeholder = 'https://dummyimage.com/800x500/e5e7eb/6b7280&text=No+Image'

async function load() {
  detail.value = await getItemDetail(route.params.id)
  messages.value = await getMessages(route.params.id)
}

function requireLogin() {
  if (!auth.isLogin) {
    router.push('/login')
    return false
  }
  return true
}

async function buy() {
  if (!requireLogin()) return
  await createOrder(detail.value.item.id)
  ElMessage.success('下单成功，请等待卖家确认')
}

async function toggleFavorite() {
  if (!requireLogin()) return
  if (detail.value.favorite) await unfavoriteItem(detail.value.item.id)
  else await favoriteItem(detail.value.item.id)
  await load()
}

async function sendMessage(parentId, content) {
  if (!requireLogin()) return
  const text = content || messageForm.content
  if (!text) return ElMessage.warning('请输入留言')
  await createMessage({ itemId: detail.value.item.id, parentId, content: text })
  messageForm.content = ''
  replyContent.value = ''
  replyTo.value = null
  await load()
}

function sendReply() {
  sendMessage(replyTo.value, replyContent.value)
}

async function submitReport() {
  if (!requireLogin()) return
  if (!reportReason.value) return ElMessage.warning('请输入举报原因')
  await createReport({ itemId: detail.value.item.id, reason: reportReason.value })
  reportDialog.value = false
  reportReason.value = ''
  ElMessage.success('举报已提交')
}

onMounted(load)
</script>

<style scoped>
.detail {
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 28px;
}
.image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.desc {
  line-height: 1.8;
}
.messages {
  margin-top: 20px;
}
.message {
  padding: 12px 0;
  border-bottom: 1px solid #eee;
}
.reply {
  margin: 8px 0 0 28px;
  color: #4b5563;
}
.mt {
  margin-top: 12px;
}
</style>
