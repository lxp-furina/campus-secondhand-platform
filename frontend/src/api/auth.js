import request from '../utils/request'

export const register = (data) => request.post('/api/auth/register', data)
export const login = (data) => request.post('/api/auth/login', data)
export const adminLogin = (data) => request.post('/api/auth/admin/login', data)
export const getProfile = () => request.get('/api/auth/profile')
export const updateProfile = (data) => request.put('/api/auth/profile', data)
export const updatePassword = (data) => request.put('/api/auth/password', data)
