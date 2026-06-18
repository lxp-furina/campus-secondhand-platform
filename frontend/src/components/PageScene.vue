<template>
  <div class="page-scene" :class="sceneClass">
    <!-- 全页装饰背景 -->
    <div class="scene-bg" aria-hidden="true">
      <img :src="pageBg" alt="" class="scene-bg-img" />
      <div class="scene-bg-gradient"></div>
      <div class="scene-bg-pattern"></div>
    </div>

    <!-- 左侧角色 · 典藏学者 -->
    <figure class="scene-mascot scene-mascot--left" aria-hidden="true">
      <div class="mascot-halo"></div>
      <img :src="mascotLeft" alt="" class="mascot-img" />
      <figcaption class="mascot-caption">
        <span class="caption-title">典藏学者</span>
        <span class="caption-sub">Curator</span>
      </figcaption>
    </figure>

    <!-- 右侧角色 · 流转使者 -->
    <figure class="scene-mascot scene-mascot--right" aria-hidden="true">
      <div class="mascot-halo"></div>
      <img :src="mascotRight" alt="" class="mascot-img" />
      <figcaption class="mascot-caption">
        <span class="caption-title">流转使者</span>
        <span class="caption-sub">Exchange</span>
      </figcaption>
    </figure>

    <!-- 内页顶部装饰带（非首页） -->
    <div v-if="showInnerBanner" class="inner-banner" aria-hidden="true">
      <img :src="pageBg" alt="" class="inner-banner-img" />
      <div class="inner-banner-overlay"></div>
      <div class="inner-banner-line"></div>
    </div>

    <!-- 主内容 -->
    <div class="scene-content">
      <slot />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import pageBg from '../assets/images/page-bg.png'
import mascotLeft from '../assets/images/mascot-left.png'
import mascotRight from '../assets/images/mascot-right.png'

const props = defineProps({
  /** home: 首页 Hero 全宽，角色仅装饰内容区 */
  variant: {
    type: String,
    default: 'default'
  },
  /** admin: 管理后台浅色内容区 */
  tone: {
    type: String,
    default: 'light'
  }
})

const route = useRoute()

const sceneClass = computed(() => ({
  'page-scene--home': props.variant === 'home' || route.path === '/',
  'page-scene--auth': props.variant === 'auth',
  'page-scene--admin': props.tone === 'admin'
}))

const showInnerBanner = computed(() => {
  if (props.variant === 'auth') return false
  if (route.path === '/') return false
  return props.tone !== 'admin' || route.path.startsWith('/admin')
})
</script>

<style scoped>
.page-scene {
  position: relative;
  min-height: 100%;
  isolation: isolate;
}

/* ─── Background layers ─── */
.scene-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
  overflow: hidden;
  pointer-events: none;
}

.scene-bg-img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0.45;
  filter: saturate(0.9) contrast(1.02);
}

.scene-bg-gradient {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(180deg, rgba(247, 243, 235, 0.55) 0%, rgba(247, 243, 235, 0.82) 35%, rgba(247, 243, 235, 0.92) 100%),
    radial-gradient(ellipse 70% 50% at 15% 20%, rgba(30, 82, 64, 0.06) 0%, transparent 55%),
    radial-gradient(ellipse 60% 45% at 85% 75%, rgba(184, 149, 106, 0.08) 0%, transparent 50%);
}

.scene-bg-pattern {
  position: absolute;
  inset: 0;
  opacity: 0.35;
  background-image: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M30 0L60 30L30 60L0 30Z' fill='none' stroke='%23b8956a' stroke-width='0.4' opacity='0.15'/%3E%3C/svg%3E");
  background-size: 60px 60px;
  /* 网格仅出现在中间内容区，两侧留给角色 */
  mask-image:
    linear-gradient(180deg, transparent, black 12%, black 88%, transparent),
    linear-gradient(90deg, transparent 0%, transparent 18%, black 28%, black 72%, transparent 82%, transparent 100%);
  mask-composite: intersect;
  -webkit-mask-image:
    linear-gradient(180deg, transparent, black 12%, black 88%, transparent),
    linear-gradient(90deg, transparent 0%, transparent 18%, black 28%, black 72%, transparent 82%, transparent 100%);
  -webkit-mask-composite: source-in;
}

.page-scene--admin .scene-bg-img {
  opacity: 0.32;
}

.page-scene--admin .scene-bg-gradient {
  background:
    linear-gradient(180deg, rgba(247, 243, 235, 0.7) 0%, rgba(247, 243, 235, 0.95) 100%),
    radial-gradient(ellipse 50% 40% at 0% 50%, rgba(30, 82, 64, 0.05) 0%, transparent 60%);
}

.page-scene--auth .scene-bg-img {
  opacity: 0.28;
}

.page-scene--home .scene-bg {
  top: 420px;
}

@media (max-width: 900px) {
  .page-scene--home .scene-bg {
    top: 320px;
  }
}

/* ─── Mascots ─── */
.scene-mascot {
  position: fixed;
  bottom: 0;
  z-index: 1;
  margin: 0;
  pointer-events: none;
  display: flex;
  flex-direction: column;
  align-items: center;
  animation: mascotEnter 1.2s var(--ease-out-expo) both;
}

.scene-mascot--left {
  left: max(8px, calc((100vw - var(--page-max)) / 2 - 240px));
  animation-delay: 0.15s;
}

.scene-mascot--right {
  right: max(8px, calc((100vw - var(--page-max)) / 2 - 240px));
  animation-delay: 0.3s;
}

.mascot-halo {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 140px;
  height: 28px;
  background: radial-gradient(ellipse, rgba(15, 36, 25, 0.14) 0%, transparent 72%);
  pointer-events: none;
  z-index: 0;
}

.mascot-img {
  position: relative;
  z-index: 1;
  width: clamp(140px, 14vw, 200px);
  height: auto;
  max-height: min(52vh, 520px);
  object-fit: contain;
  object-position: bottom center;
  filter: drop-shadow(0 8px 32px rgba(15, 36, 25, 0.14));
  opacity: 0.95;
  transition: transform 0.6s var(--ease-out-expo), opacity 0.4s ease;
}

.scene-mascot:hover .mascot-img {
  opacity: 1;
}

.scene-mascot--left:hover .mascot-img {
  transform: translateY(-4px) rotate(-1deg);
}

.scene-mascot--right:hover .mascot-img {
  transform: translateY(-4px) rotate(1deg);
}

.mascot-caption {
  margin-top: -8px;
  text-align: center;
  opacity: 0.72;
  transition: opacity var(--duration-normal) ease;
}

.scene-mascot:hover .mascot-caption {
  opacity: 1;
}

.caption-title {
  display: block;
  font-family: var(--font-serif);
  font-size: 0.72rem;
  color: var(--color-emerald);
  letter-spacing: 0.12em;
}

.caption-sub {
  display: block;
  font-family: var(--font-display);
  font-size: 0.62rem;
  color: var(--color-gold);
  font-style: italic;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

/* ─── Inner page banner ─── */
.inner-banner {
  position: relative;
  width: 100vw;
  margin-left: calc(50% - 50vw);
  height: 140px;
  margin-bottom: 28px;
  overflow: hidden;
  border-bottom: 1px solid var(--border-gold);
}

.page-scene--admin .inner-banner {
  height: 110px;
  margin-bottom: 24px;
}

.inner-banner-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center 30%;
  opacity: 0.55;
}

.inner-banner-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    180deg,
    rgba(15, 36, 25, 0.35) 0%,
    rgba(247, 243, 235, 0.75) 100%
  );
}

.inner-banner-line {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--color-gold), var(--color-emerald-light), var(--color-gold), transparent);
  opacity: 0.7;
}

@media (max-width: 768px) {
  .inner-banner {
    height: 96px;
    margin-bottom: 20px;
  }
}

/* ─── Content ─── */
.scene-content {
  position: relative;
  z-index: 2;
}

/* ─── Home: mascots start below hero ─── */
.page-scene--home .scene-mascot {
  bottom: 24px;
}

/* ─── Auth: smaller mascots flanking form ─── */
.page-scene--auth .scene-mascot--left {
  left: max(16px, 4vw);
}

.page-scene--auth .scene-mascot--right {
  right: max(16px, 4vw);
}

.page-scene--auth .mascot-img {
  width: clamp(100px, 12vw, 160px);
  max-height: min(45vh, 400px);
  opacity: 0.9;
}

.page-scene--auth .mascot-halo {
  width: 110px;
  height: 22px;
}

.page-scene--auth .mascot-caption {
  display: none;
}

.page-scene--auth {
  min-height: 100vh;
}

.page-scene--admin {
  min-height: 100%;
}

/* ─── Responsive hide ─── */
@media (max-width: 1280px) {
  .scene-mascot {
    display: none;
  }
}

@media (min-width: 1281px) and (max-width: 1480px) {
  .mascot-img {
    width: clamp(120px, 11vw, 160px);
  }

  .mascot-caption {
    display: none;
  }
}

@keyframes mascotEnter {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
