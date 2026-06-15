import request from '../utils/request'

export const getDashboard = () => request.get('/api/admin/dashboard')
export const getUsers = (params) => request.get('/api/admin/users', { params })
export const updateUserStatus = (id, status) => request.put(`/api/admin/users/${id}/status`, { status })
