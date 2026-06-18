<template>
  <div class="app-shell">
    <header class="site-header">
      <div class="header-inner">
        <router-link to="/" class="brand">
          <img src="../assets/images/brand-mark.png" alt="" class="brand-icon" />
          <div class="brand-text">
            <span class="brand-name">HIT</span>
            <span class="brand-tagline">Campus Atelier</span>
          </div>
        </router-link>

        <nav class="main-nav">
          <router-link
            v-for="link in navLinks"
            :key="link.path"
            :to="link.path"
            class="nav-link"
            :class="{ active: isActive(link.path) }"
          >
            {{ link.label }}
          </router-link>
        </nav>

        <div class="header-actions">
          <router-link v-if="!auth.isLogin" to="/login" class="btn-login">登录</router-link>
          <button v-else class="btn-logout" @click="logout">
            <span class="logout-dot"></span>
            退出
          </button>
        </div>
      </div>
      <div class="header-accent"></div>
    </header>

    <main class="site-main">
      <router-view />
    </main>

    <footer class="site-footer">
      <div class="footer-inner">
        <div class="footer-brand">
          <img src="../assets/images/brand-mark.png" alt="" class="footer-icon" />
          <span>HIT · 让闲置流转成美好</span>
        </div>
        <p class="footer-copy">校园闲置物品交易与互助服务平台</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()

const navLinks = [
  { path: '/', label: '发现好物' },
  { path: '/publish', label: '发布物品' },
  { path: '/mine/items', label: '我的发布' },
  { path: '/mine/favorites', label: '我的收藏' },
  { path: '/orders', label: '我的订单' },
  { path: '/profile', label: '个人中心' }
]

function isActive(path) {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
}

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.app-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* ─── Header ─── */
.site-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(247, 243, 235, 0.82);
  backdrop-filter: blur(24px) saturate(1.3);
  -webkit-backdrop-filter: blur(24px) saturate(1.3);
  border-bottom: 1px solid var(--border-gold);
}

.header-inner {
  max-width: var(--page-max);
  margin: 0 auto;
  padding: 0 32px;
  height: 72px;
  display: flex;
  align-items: center;
  gap: 32px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
  transition: opacity var(--duration-fast) ease;
}

.brand:hover {
  opacity: 0.85;
}

.brand-icon {
  width: 40px;
  height: 40px;
  object-fit: contain;
  filter: drop-shadow(0 2px 6px rgba(15, 36, 25, 0.15));
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.brand-name {
  font-family: var(--font-body);
  font-size: 1.2rem;
  font-weight: 700;
  color: var(--color-forest);
  letter-spacing: 0.18em;
}

.brand-tagline {
  font-family: var(--font-display);
  font-size: 0.7rem;
  color: var(--color-gold);
  letter-spacing: 0.15em;
  text-transform: uppercase;
  font-style: italic;
}

.main-nav {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
  overflow-x: auto;
  scrollbar-width: none;
}

.main-nav::-webkit-scrollbar {
  display: none;
}

.nav-link {
  position: relative;
  padding: 8px 16px;
  font-size: 0.88rem;
  font-weight: 500;
  color: var(--color-text-soft);
  letter-spacing: 0.02em;
  white-space: nowrap;
  border-radius: var(--radius-sm);
  transition: all var(--duration-fast) ease;
}

.nav-link::after {
  content: '';
  position: absolute;
  bottom: 2px;
  left: 50%;
  transform: translateX(-50%) scaleX(0);
  width: 20px;
  height: 2px;
  background: var(--color-gold);
  border-radius: 1px;
  transition: transform var(--duration-normal) var(--ease-out-expo);
}

.nav-link:hover {
  color: var(--color-emerald);
  background: rgba(30, 82, 64, 0.05);
}

.nav-link.active {
  color: var(--color-emerald);
  font-weight: 600;
}

.nav-link.active::after {
  transform: translateX(-50%) scaleX(1);
}

.header-actions {
  flex-shrink: 0;
}

.btn-login {
  display: inline-flex;
  align-items: center;
  padding: 8px 22px;
  background: linear-gradient(135deg, var(--color-emerald), var(--color-forest-mid));
  color: var(--color-ivory);
  font-size: 0.88rem;
  font-weight: 500;
  letter-spacing: 0.04em;
  border-radius: 100px;
  border: 1px solid rgba(184, 149, 106, 0.3);
  box-shadow: 0 4px 16px rgba(30, 82, 64, 0.2);
  transition: all var(--duration-normal) var(--ease-out-expo);
}

.btn-login:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 24px rgba(30, 82, 64, 0.3);
}

.btn-logout {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 18px;
  background: transparent;
  border: 1px solid var(--border-gold);
  border-radius: 100px;
  color: var(--color-muted);
  font-family: var(--font-body);
  font-size: 0.85rem;
  cursor: pointer;
  transition: all var(--duration-fast) ease;
}

.btn-logout:hover {
  border-color: var(--color-burgundy);
  color: var(--color-burgundy);
  background: rgba(139, 41, 66, 0.04);
}

.logout-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--color-emerald-light);
}

.header-accent {
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--color-gold), var(--color-emerald-light), var(--color-gold), transparent);
  opacity: 0.6;
}

/* ─── Main ─── */
.site-main {
  flex: 1;
}

/* ─── Footer ─── */
.site-footer {
  margin-top: auto;
  border-top: 1px solid var(--border-gold);
  background: rgba(15, 36, 25, 0.03);
  padding: 32px;
}

.footer-inner {
  max-width: var(--page-max);
  margin: 0 auto;
  text-align: center;
}

.footer-brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  font-family: var(--font-serif);
  color: var(--color-forest);
  font-size: 0.95rem;
  margin-bottom: 8px;
}

.footer-icon {
  width: 24px;
  height: 24px;
  opacity: 0.7;
}

.footer-copy {
  color: var(--color-muted-light);
  font-size: 0.82rem;
  margin: 0;
  letter-spacing: 0.04em;
}

@media (max-width: 900px) {
  .header-inner {
    padding: 0 16px;
    height: 64px;
    gap: 16px;
  }

  .brand-tagline {
    display: none;
  }

  .nav-link {
    padding: 6px 10px;
    font-size: 0.82rem;
  }
}
</style>
