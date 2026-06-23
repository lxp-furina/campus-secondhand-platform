import { defineStore } from 'pinia'

// 清除旧版持久化登录态，避免刷新后自动进入
localStorage.removeItem('token')
localStorage.removeItem('user')

const CREDENTIAL_KEYS = {
  USER: { account: 'login_account', password: 'login_password' },
  ADMIN: { account: 'admin_login_account', password: 'admin_login_password' }
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: '',
    user: null
  }),
  getters: {
    isLogin: (state) => !!state.token,
    role: (state) => state.user?.role || ''
  },
  actions: {
    setSession(data) {
      this.token = data.token
      this.user = data.user
    },
    setUser(user) {
      this.user = user
    },
    logout() {
      this.token = ''
      this.user = null
    },
    saveCredentials(account, password, type = 'USER') {
      const keys = CREDENTIAL_KEYS[type]
      localStorage.setItem(keys.account, account)
      localStorage.setItem(keys.password, password)
    },
    loadCredentials(type = 'USER') {
      const keys = CREDENTIAL_KEYS[type]
      return {
        account: localStorage.getItem(keys.account) || '',
        password: localStorage.getItem(keys.password) || ''
      }
    }
  }
})
