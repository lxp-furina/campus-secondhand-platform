import request from '../utils/request'

export const createReport = (data) => request.post('/api/reports', data)
export const getReports = (params) => request.get('/api/admin/reports', { params })
export const handleReport = (id, data) => request.put(`/api/admin/reports/${id}/handle`, data)
