<template>
  <div class="login-page"><div class="login-card">
    <div class="brand"><div class="brand-mark">✦</div><h1>公益咨询平台</h1><div class="muted">连接教育资源 · 助力学习成长</div></div>
    <div style="font-weight:600">请选择登录身份</div>
    <div class="role-select">
      <div v-for="item in roles" :key="item.value" class="role-option" :class="{active:form.role===item.value}" @click="form.role=item.value">
        <div class="role-icon">{{ item.icon }}</div><div>{{ item.label }}</div>
      </div>
    </div>
    <el-input v-model.trim="form.username" prefix-icon="el-icon-user" placeholder="请输入账号" @keyup.enter.native="submit" />
    <el-input v-model="form.password" prefix-icon="el-icon-lock" type="password" show-password placeholder="请输入密码" @keyup.enter.native="submit" />
    <el-button type="primary" style="width:100%;margin-top:8px" :loading="loading" @click="submit">登录平台</el-button>
    <div class="login-links muted">还没有账号？<router-link to="/register">注册普通用户 / 教授账号</router-link></div>
    <div class="muted" style="font-size:12px;text-align:center;margin-top:18px">教授账号须经管理员审核通过后方可登录</div>
  </div></div>
</template>
<script>
import { login } from '../api/auth'
export default {
  data: () => ({ loading: false, form: { username: '', password: '', role: 'USER' }, roles: [
    { value: 'USER', label: '普通用户', icon: '♙' }, { value: 'PROFESSOR', label: '教授', icon: '♧' }, { value: 'ADMIN', label: '管理员', icon: '◇' }
  ] }),
  methods: {
    async submit() {
      if (!this.form.username || !this.form.password) return this.$message.warning('请输入账号和密码')
      this.loading = true
      try {
        const data = await login(this.form)
        localStorage.setItem('platform_token', data.token)
        localStorage.setItem('platform_user', JSON.stringify(data))
        this.$message.success('登录成功')
        this.$router.replace('/dashboard')
      } finally { this.loading = false }
    }
  }
}
</script>
