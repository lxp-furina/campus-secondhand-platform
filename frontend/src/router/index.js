import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../store/auth'
import UserLayout from '../components/UserLayout.vue'
import AdminLayout from '../components/AdminLayout.vue'
import Home from '../views/user/Home.vue'
import Login from '../views/user/Login.vue'
import Register from '../views/user/Register.vue'
import ItemDetail from '../views/user/ItemDetail.vue'
import ItemForm from '../views/user/ItemForm.vue'
import MyItems from '../views/user/MyItems.vue'
import Orders from '../views/user/Orders.vue'
import Profile from '../views/user/Profile.vue'
import AdminLogin from '../views/admin/AdminLogin.vue'
import Dashboard from '../views/admin/Dashboard.vue'
import UserManage from '../views/admin/UserManage.vue'
import ItemManage from '../views/admin/ItemManage.vue'
import OrderManage from '../views/admin/OrderManage.vue'
import ReportManage from '../views/admin/ReportManage.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: Login },
    { path: '/register', component: Register },
    {
      path: '/',
      component: UserLayout,
      meta: { requiresAuth: true, role: 'USER' },
      children: [
        { path: '', component: Home },
        { path: 'items/:id', component: ItemDetail },
        { path: 'publish', component: ItemForm, meta: { requiresAuth: true, role: 'USER' } },
        { path: 'items/:id/edit', component: ItemForm, meta: { requiresAuth: true, role: 'USER' } },
        { path: 'mine/items', component: MyItems, meta: { requiresAuth: true, role: 'USER' } },
        { path: 'orders', component: Orders, meta: { requiresAuth: true, role: 'USER' } },
        { path: 'profile', component: Profile, meta: { requiresAuth: true, role: 'USER' } }
      ]
    },
    { path: '/admin/login', component: AdminLogin },
    {
      path: '/admin',
      component: AdminLayout,
      redirect: '/admin/dashboard',
      meta: { requiresAuth: true, role: 'ADMIN' },
      children: [
        { path: 'dashboard', component: Dashboard },
        { path: 'users', component: UserManage },
        { path: 'items', component: ItemManage },
        { path: 'orders', component: OrderManage },
        { path: 'reports', component: ReportManage }
      ]
    }
  ]
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if ((to.path === '/login' || to.path === '/register') && auth.isLogin) {
    return auth.role === 'ADMIN' ? '/admin/dashboard' : '/'
  }
  if (to.path === '/admin/login' && auth.isLogin && auth.role === 'ADMIN') {
    return '/admin/dashboard'
  }
  if (to.meta.requiresAuth && !auth.isLogin) {
    return to.path.startsWith('/admin') ? '/admin/login' : '/login'
  }
  if (to.meta.role && auth.role !== to.meta.role) {
    return auth.role === 'ADMIN' ? '/admin/dashboard' : '/'
  }
  return true
})

export default router
