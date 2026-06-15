import request from '../utils/request'

export const getMessages = (itemId) => request.get(`/api/public/items/${itemId}/messages`)
export const createMessage = (data) => request.post('/api/messages', data)
