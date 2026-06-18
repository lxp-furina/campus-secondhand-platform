<template>
  <div class="page" v-if="detail">
    <div class="detail card">
      <div class="gallery">
        <el-carousel height="400px" indicator-position="outside">
          <el-carousel-item v-for="img in detail.images" :key="img.id">
            <img :src="img.imageUrl" class="image" />
          </el-carousel-item>
          <el-carousel-item v-if="!detail.images?.length">
            <img :src="placeholder" class="image" />
          </el-carousel-item>
        </el-carousel>
      </div>
      <div class="info">
        <div class="info-header">
          <span class="tag-pill">{{ detail.category?.name }}</span>
          <span class="tag-pill condition">{{ detail.item.conditionLevel }}</span>
        </div>
        <h1>{{ detail.item.title }}</h1>
        <p class="price">{{ detail.item.price }}</p>

        <div class="meta-grid">
          <div class="meta-item">
            <span class="meta-label">卖家</span>
            <span class="meta-value">{{ detail.seller?.username }}</span>
          </div>
          <div class="meta-item" v-if="detail.seller?.phone">
            <span class="meta-label">联系方式</span>
            <span class="meta-value">{{ detail.seller?.phone }}</span>
          </div>
        </div>

        <div class="desc-block">
          <h3>物品描述</h3>
          <p class="desc">{{ detail.item.description }}</p>
        </div>

        <div class="action-bar">
          <el-button type="primary" size="large" @click="buy">立即下单</el-button>
          <el-button size="large" @click="toggleFavorite">
            {{ detail.favorite ? '♥ 已收藏' : '♡ 收藏' }}
          </el-button>
          <el-button type="danger" plain size="large" @click="reportDialog = true">举报</el-button>
        </div>
      </div>
    </div>

    <div class="card messages">
      <div class="page-header">
        <h2>留言咨询</h2>
        <p class="page-subtitle">与卖家沟通，了解更多细节</p>
      </div>
      <el-form :model="messageForm" class="message-form">
        <el-input v-model="messageForm.content" type="textarea" :rows="3" placeholder="输入留言内容…" />
        <el-button type="primary" class="mt" @click="sendMessage()">发送留言</el-button>
      </el-form>

      <div class="message-list">
        <div v-for="m in messages" :key="m.message.id" class="message">
          <div class="message-header">
            <span class="message-author">{{ m.user?.username }}</span>
            <el-button link @click="replyTo = m.message.id">回复</el-button>
          </div>
          <p class="message-body">{{ m.message.content }}</p>
          <div class="reply" v-for="r in m.replies" :key="r.message.id">
            <span class="reply-author">{{ r.user?.username }}</span>
            <span class="reply-body">{{ r.message.content }}</span>
          </div>
        </div>
        <el-empty v-if="messages.length === 0" description="暂无留言，来第一个提问吧" />
      </div>

      <el-input v-if="replyTo" v-model="replyContent" placeholder="回复内容" class="mt reply-input">
        <template #append><el-button @click="sendReply">发送回复</el-button></template>
      </el-input>
    </div>

    <el-dialog v-model="reportDialog" title="提交举报" width="440px">
      <el-input v-model="reportReason" type="textarea" :rows="4" placeholder="请详细描述举报原因" />
      <template #footer>
        <el-button @click="reportDialog = false">取消</el-button>
        <el-button type="danger" @click="submitReport">提交举报</el-button>
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
const placeholder = 'https://dummyimage.com/800x500/ede6d8/6b7c72&text=No+Image'

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
  grid-template-columns: 1.15fr 1fr;
  gap: 36px;
  padding: 28px;
}

.gallery {
  border-radius: var(--radius-lg);
  overflow: hidden;
  border: 1px solid var(--border-gold);
}

.image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.info-header {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.tag-pill {
  display: inline-flex;
  padding: 3px 12px;
  border-radius: 100px;
  font-size: 0.78rem;
  font-weight: 500;
  background: rgba(30, 82, 64, 0.08);
  color: var(--color-emerald);
  border: 1px solid rgba(30, 82, 64, 0.12);
}

.tag-pill.condition {
  background: rgba(184, 149, 106, 0.1);
  color: var(--color-gold);
  border-color: rgba(184, 149, 106, 0.2);
}

.info h1 {
  font-size: clamp(1.5rem, 3vw, 2rem);
  margin-bottom: 12px;
}

.price {
  margin-bottom: 24px;
}

.meta-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  padding: 20px;
  background: rgba(30, 82, 64, 0.04);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-gold);
  margin-bottom: 24px;
}

.meta-label {
  display: block;
  font-size: 0.78rem;
  color: var(--color-muted);
  letter-spacing: 0.06em;
  margin-bottom: 4px;
}

.meta-value {
  font-weight: 500;
  color: var(--color-text);
}

.desc-block h3 {
  font-size: 1rem;
  margin-bottom: 10px;
  color: var(--color-emerald);
}

.desc {
  line-height: 1.85;
  color: var(--color-text-soft);
}

.action-bar {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 28px;
  padding-top: 24px;
  border-top: 1px solid var(--border-gold);
}

.messages {
  margin-top: 24px;
}

.message-form {
  margin-bottom: 24px;
}

.message-list {
  border-top: 1px solid var(--border-gold);
}

.message {
  padding: 18px 0;
  border-bottom: 1px solid rgba(184, 149, 106, 0.12);
}

.message-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.message-author {
  font-family: var(--font-serif);
  font-weight: 600;
  color: var(--color-forest);
}

.message-body {
  margin: 0;
  line-height: 1.7;
  color: var(--color-text-soft);
}

.reply {
  margin: 12px 0 0 24px;
  padding: 12px 16px;
  background: rgba(184, 149, 106, 0.06);
  border-radius: var(--radius-sm);
  border-left: 3px solid var(--color-gold);
}

.reply-author {
  font-weight: 600;
  color: var(--color-emerald);
  margin-right: 8px;
}

.reply-body {
  color: var(--color-text-soft);
}

.mt {
  margin-top: 14px;
}

@media (max-width: 768px) {
  .detail {
    grid-template-columns: 1fr;
  }
}
</style>
