import axios from 'axios'
import { Message } from 'element-ui'
const request = axios.create({ baseURL: '/api', timeout: 15000 })
request.interceptors.request.use(config => {
  const token = localStorage.getItem('platform_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})
request.interceptors.response.use(response => {
  const body = response.data
  if (body && body.success === false) {
    Message.error(body.message || '请求失败')
    return Promise.reject(new Error(body.message || '请求失败'))
  }
  return body.data
}, error => {
  if (error.response && error.response.status === 401) {
    localStorage.removeItem('platform_token')
    localStorage.removeItem('platform_user')
    if (window.location.hash !== '#/login') window.location.hash = '#/login'
  }
  Message.error(error.response?.data?.message || '网络请求失败')
  return Promise.reject(error)
})
export default request
