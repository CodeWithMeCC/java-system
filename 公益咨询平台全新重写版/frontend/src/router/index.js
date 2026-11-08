import Vue from 'vue'
import Router from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Dashboard from '../views/Dashboard.vue'
import Workspace from '../views/Workspace.vue'
Vue.use(Router)
const router = new Router({ mode: 'hash', routes: [
  { path: '/login', component: Login }, { path: '/register', component: Register },
  { path: '/', redirect: '/dashboard' }, { path: '/dashboard', component: Dashboard, meta: { auth: true } },
  { path: '/workspace/:module', component: Workspace, meta: { auth: true } }
] })
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('platform_token')
  if (to.meta.auth && !token) return next('/login')
  if ((to.path === '/login' || to.path === '/register') && token) return next('/dashboard')
  next()
})
export default router
