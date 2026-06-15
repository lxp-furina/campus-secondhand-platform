import request from '../utils/request'

export const getCategories = () => request.get('/api/public/categories')
export const getItems = (params) => request.get('/api/public/items', { params })
export const getItemDetail = (id) => request.get(`/api/public/items/${id}`)
export const getAdminItems = (params) => request.get('/api/admin/items', { params })
export const createItem = (data) => request.post('/api/items', data)
export const updateItem = (id, data) => request.put(`/api/items/${id}`, data)
export const offShelfItem = (id) => request.put(`/api/items/${id}/off-shelf`)
export const adminOffShelfItem = (id) => request.put(`/api/admin/items/${id}/off-shelf`)
export const getMyItems = () => request.get('/api/items/mine')
export const favoriteItem = (id) => request.post(`/api/items/${id}/favorite`)
export const unfavoriteItem = (id) => request.delete(`/api/items/${id}/favorite`)
export const uploadFile = (file) => {
  const form = new FormData()
  form.append('file', file)
  return request.post('/api/upload', form, { headers: { 'Content-Type': 'multipart/form-data' } })
}
