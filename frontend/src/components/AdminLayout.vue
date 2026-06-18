<template>
  <div class="admin-shell">
    <aside class="side">
      <div class="side-brand">
        <img src="../assets/images/brand-mark.png" alt="" class="side-icon" />
        <div>
          <h3>管理后台</h3>
          <span class="side-sub">Campus Atelier</span>
        </div>
      </div>

      <nav class="side-nav">
        <router-link
          v-for="link in navLinks"
          :key="link.path"
          :to="link.path"
          class="side-link"
          :class="{ active: $route.path === link.path }"
        >
          <span class="side-link-icon">{{ link.icon }}</span>
          {{ link.label }}
        </router-link>
      </nav>

      <button class="logout" @click="logout">
        <span>退出后台</span>
      </button>
    </aside>

    <main class="main">
      <PageScene tone="admin">
        <router-view />
      </PageScene>
    </main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import PageScene from './PageScene.vue'

const auth = useAuthStore()
const router = useRouter()

const navLinks = [
  { path: '/admin/dashboard', label: '数据概览', icon: '◈' },
  { path: '/admin/users', label: '用户管理', icon: '◉' },
  { path: '/admin/items', label: '物品管理', icon: '◇' },
  { path: '/admin/orders', label: '订单管理', icon: '◆' },
  { path: '/admin/reports', label: '举报处理', icon: '◎' }
]

function logout() {
  auth.logout()
  router.push('/admin/login')
}
</script>

<style scoped>
.admin-shell {
  display: flex;
  min-height: 100vh;
  background: var(--color-forest);
}

.side {
  width: 240px;
  background: linear-gradient(180deg, #0a1a12 0%, #0f2419 50%, #122b1e 100%);
  color: var(--color-ivory);
  padding: 28px 0;
  display: flex;
  flex-direction: column;
  border-right: 1px solid rgba(184, 149, 106, 0.15);
  position: relative;
}

.side::after {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 1px;
  height: 100%;
  background: linear-gradient(180deg, transparent, rgba(184, 149, 106, 0.3), transparent);
}

.side-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 24px 28px;
  border-bottom: 1px solid rgba(184, 149, 106, 0.12);
  margin-bottom: 16px;
}

.side-icon {
  width: 36px;
  height: 36px;
  opacity: 0.9;
}

.side-brand h3 {
  font-family: var(--font-serif);
  font-size: 1rem;
  color: var(--color-ivory);
  margin: 0;
  letter-spacing: 0.06em;
}

.side-sub {
  font-family: var(--font-display);
  font-size: 0.68rem;
  color: var(--color-gold);
  letter-spacing: 0.12em;
  font-style: italic;
}

.side-nav {
  flex: 1;
  padding: 8px 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.side-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: var(--radius-sm);
  color: rgba(247, 243, 235, 0.65);
  font-size: 0.88rem;
  font-weight: 500;
  letter-spacing: 0.02em;
  transition: all var(--duration-fast) ease;
  border: 1px solid transparent;
}

.side-link-icon {
  font-size: 0.75rem;
  color: var(--color-gold);
  opacity: 0.7;
}

.side-link:hover {
  color: var(--color-ivory);
  background: rgba(184, 149, 106, 0.08);
  border-color: rgba(184, 149, 106, 0.12);
}

.side-link.active {
  color: var(--color-ivory);
  background: rgba(30, 82, 64, 0.4);
  border-color: rgba(184, 149, 106, 0.25);
  box-shadow: inset 3px 0 0 var(--color-gold);
}

.side-link.active .side-link-icon {
  opacity: 1;
}

.logout {
  margin: 16px 20px 0;
  padding: 10px 16px;
  background: transparent;
  border: 1px solid rgba(184, 149, 106, 0.25);
  border-radius: var(--radius-sm);
  color: rgba(247, 243, 235, 0.6);
  font-family: var(--font-body);
  font-size: 0.85rem;
  cursor: pointer;
  transition: all var(--duration-fast) ease;
}

.logout:hover {
  border-color: var(--color-burgundy);
  color: #e8a0b0;
  background: rgba(139, 41, 66, 0.15);
}

.main {
  flex: 1;
  padding: 32px;
  overflow-y: auto;
  position: relative;
}
</style>
