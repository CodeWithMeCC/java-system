import request from './request'
export const login = data => request.post('/auth/login', data)
export const register = data => request.post('/auth/register', data)
export const currentUser = () => request.get('/auth/me')
export const dashboard = () => request.get('/statistics/dashboard')
