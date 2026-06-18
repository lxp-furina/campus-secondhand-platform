<template>
  <div class="home">
    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-bg">
        <img src="../../assets/images/hero-bg.png" alt="" class="hero-img" />
        <div class="hero-overlay"></div>
      </div>
      <div class="hero-content">
        <p class="hero-eyebrow">HIT · Campus Atelier</p>
        <h1 class="hero-title">
          发现校园里的<br /><em>精致闲置</em>
        </h1>
        <p class="hero-desc">
          以优雅的方式流转物品，让每一件闲置都找到新的归宿
        </p>
        <div class="hero-actions">
          <el-button type="primary" size="large" @click="scrollToItems">浏览好物</el-button>
          <el-button size="large" @click="$router.push('/publish')">发布闲置</el-button>
        </div>
      </div>
      <div class="hero-stats">
        <div class="stat-item">
          <span class="stat-num">{{ total || '—' }}</span>
          <span class="stat-label">在售物品</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-num">{{ categories.length || '—' }}</span>
          <span class="stat-label">物品分类</span>
        </div>
      </div>
    </section>

    <div class="page home-content">
      <!-- Search Toolbar -->
      <div class="search-panel card" ref="itemsRef">
        <div class="search-header">
          <h2>探索好物</h2>
          <p class="page-subtitle">筛选你心仪的校园闲置</p>
        </div>
        <div class="toolbar">
          <el-select v-model="query.categoryId" clearable placeholder="全部分类" style="width: 160px" @change="loadItems">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
          <el-input
            v-model="query.keyword"
            clearable
            placeholder="搜索物品关键词…"
            style="flex: 1; max-width: 360px"
            @keyup.enter="loadItems"
          >
            <template #prefix>
              <span class="search-icon">⌕</span>
            </template>
          </el-input>
          <el-button type="primary" @click="loadItems">搜索</el-button>
        </div>
      </div>

      <!-- Item Grid -->
      <div class="grid item-grid">
        <el-card
          v-for="(v, i) in items"
          :key="v.item.id"
          class="item-card"
          :style="{ animationDelay: `${i * 0.06}s` }"
          @click="$router.push(`/items/${v.item.id}`)"
        >
          <div class="cover-wrap">
            <img :src="v.images?.[0]?.imageUrl || placeholder" class="cover" />
            <div class="cover-shine"></div>
          </div>
          <h3>{{ v.item.title }}</h3>
          <div class="item-meta">
            <span class="tag-pill">{{ v.category?.name }}</span>
            <span class="tag-pill condition">{{ v.item.conditionLevel }}</span>
          </div>
          <p class="price">{{ v.item.price }}</p>
        </el-card>
      </div>

      <div class="empty-wrap" v-if="items.length === 0">
        <el-empty description="暂无物品，成为第一个发布者吧">
          <el-button type="primary" @click="$router.push('/publish')">发布闲置</el-button>
        </el-empty>
      </div>

      <div class="pager" v-if="total > query.size">
        <el-pagination
          layout="prev, pager, next"
          :total="total"
          :page-size="query.size"
          :current-page="query.current"
          @current-change="onPageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { getCategories, getItems } from '../../api/item'

const placeholder = 'https://dummyimage.com/600x400/ede6d8/6b7c72&text=Campus+Atelier'
const categories = ref([])
const items = ref([])
const total = ref(0)
const itemsRef = ref(null)
const query = reactive({ categoryId: undefined, keyword: '', current: 1, size: 12 })

async function loadItems() {
  const page = await getItems(query)
  items.value = page.records || []
  total.value = page.total || 0
}

function onPageChange(page) {
  query.current = page
  loadItems()
}

function scrollToItems() {
  itemsRef.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

onMounted(async () => {
  categories.value = await getCategories()
  await loadItems()
})
</script>

<style scoped>
.home {
  animation: pageReveal var(--duration-slow) var(--ease-out-expo) both;
}

/* ─── Hero ─── */
.hero {
  position: relative;
  min-height: 420px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  inset: 0;
}

.hero-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    135deg,
    rgba(15, 36, 25, 0.88) 0%,
    rgba(30, 82, 64, 0.65) 50%,
    rgba(15, 36, 25, 0.8) 100%
  );
}

.hero-content {
  position: relative;
  z-index: 2;
  max-width: var(--page-max);
  margin: 0 auto;
  padding: 80px 32px 40px;
  color: var(--color-ivory);
}

.hero-eyebrow {
  font-family: var(--font-display);
  font-size: 0.85rem;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: var(--color-gold-light);
  margin: 0 0 16px;
  font-style: italic;
  animation: fadeIn var(--duration-slow) var(--ease-out-expo) 0.1s both;
}

.hero-title {
  font-family: var(--font-display);
  font-size: clamp(2.4rem, 5vw, 3.8rem);
  font-weight: 600;
  line-height: 1.15;
  color: var(--color-ivory);
  margin: 0 0 20px;
  animation: slideUp var(--duration-slow) var(--ease-out-expo) 0.2s both;
}

.hero-title em {
  font-style: italic;
  color: var(--color-gold-light);
}

.hero-desc {
  font-size: 1.05rem;
  line-height: 1.7;
  color: rgba(247, 243, 235, 0.75);
  max-width: 440px;
  margin: 0 0 32px;
  font-weight: 300;
  animation: slideUp var(--duration-slow) var(--ease-out-expo) 0.3s both;
}

.hero-actions {
  display: flex;
  gap: 14px;
  animation: slideUp var(--duration-slow) var(--ease-out-expo) 0.4s both;
}

.hero-actions .el-button--default {
  background: rgba(255, 252, 247, 0.12) !important;
  border: 1px solid rgba(184, 149, 106, 0.4) !important;
  color: var(--color-ivory) !important;
  backdrop-filter: blur(8px);
}

.hero-actions .el-button--default:hover {
  background: rgba(255, 252, 247, 0.2) !important;
  border-color: var(--color-gold-light) !important;
}

.hero-stats {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  gap: 32px;
  max-width: var(--page-max);
  margin: 0 auto;
  padding: 0 32px 48px;
  animation: slideUp var(--duration-slow) var(--ease-out-expo) 0.5s both;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-num {
  font-family: var(--font-display);
  font-size: 2rem;
  font-weight: 700;
  color: var(--color-gold-light);
  line-height: 1;
}

.stat-label {
  font-size: 0.82rem;
  color: rgba(247, 243, 235, 0.55);
  letter-spacing: 0.06em;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: rgba(184, 149, 106, 0.3);
}

/* ─── Content ─── */
.home-content {
  padding-top: 36px;
}

.search-panel {
  margin-bottom: 28px;
}

.search-header {
  margin-bottom: 18px;
}

.search-header h2 {
  margin-bottom: 4px;
}

.search-icon {
  color: var(--color-gold);
  font-size: 1.1rem;
}

.item-grid .item-card {
  animation: slideUp var(--duration-slow) var(--ease-out-expo) both;
}

.cover-shine {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    135deg,
    rgba(255, 255, 255, 0.15) 0%,
    transparent 50%
  );
  pointer-events: none;
  opacity: 0;
  transition: opacity var(--duration-normal) ease;
}

.item-card:hover .cover-shine {
  opacity: 1;
}

.tag-pill.condition {
  background: rgba(184, 149, 106, 0.1);
  color: var(--color-gold);
  border-color: rgba(184, 149, 106, 0.2);
}

.empty-wrap {
  padding: 48px 0;
}

.pager {
  display: flex;
  justify-content: center;
  padding: 36px 0 12px;
}

@media (max-width: 768px) {
  .hero {
    min-height: 360px;
  }

  .hero-content {
    padding: 60px 16px 32px;
  }

  .hero-stats {
    padding: 0 16px 36px;
  }
}
</style>
