import request from '../utils/request'

export const createOrder = (itemId) => request.post(`/api/orders/item/${itemId}`)
export const updateOrderStatus = (id, status) => request.put(`/api/orders/${id}/status`, { status })
export const getBuyerOrders = () => request.get('/api/orders/buyer')
export const getSellerOrders = () => request.get('/api/orders/seller')
export const getAdminOrders = (params) => request.get('/api/admin/orders', { params })
