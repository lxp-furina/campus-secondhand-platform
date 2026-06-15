import { defineStore } from 'pinia'

const savedToken = localStorage.getItem('token')
const savedUser = localStorage.getItem('user')

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: savedToken || '',
    user: savedUser ? JSON.parse(savedUser) : null
  }),
  getters: {
    isLogin: (state) => !!state.token,
    role: (state) => state.user?.role || ''
  },
  actions: {
    setSession(data) {
      this.token = data.token
      this.user = data.user
      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify(data.user))
    },
    setUser(user) {
      this.user = user
      localStorage.setItem('user', JSON.stringify(user))
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})
