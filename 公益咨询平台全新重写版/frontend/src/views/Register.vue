<template><div class="login-page"><div class="login-card">
  <div class="brand"><div class="brand-mark">✦</div><h1>创建平台账号</h1><div class="muted">请选择注册身份并填写基本信息</div></div>
  <el-form :model="form" :rules="rules" ref="form" label-position="top">
    <el-form-item label="注册身份" prop="role">
      <el-radio-group
          id="role"
          v-model="form.role"
      >
        <el-radio-button label="USER">普通用户</el-radio-button>
        <el-radio-button label="PROFESSOR">教授</el-radio-button>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="用户名" prop="username"><el-input v-model.trim="form.username" placeholder="4-30位用户名" /></el-form-item>
    <el-form-item label="昵称 / 姓名" prop="nickname"><el-input v-model.trim="form.nickname" placeholder="请输入昵称或真实姓名" /></el-form-item>
    <el-form-item label="密码" prop="password"><el-input v-model="form.password" type="password" show-password placeholder="至少8位" /></el-form-item>
    <el-button type="primary" style="width:100%" :loading="loading" @click="submit">提交注册</el-button>
  </el-form>
  <div class="login-links muted"><router-link to="/login">返回登录</router-link></div>
  <p v-if="form.role==='PROFESSOR'" class="muted" style="font-size:12px">教授账号注册后处于待审核状态，审核通过后才能登录教授端。</p>
</div></div></template>
<script>
import { register } from '../api/auth'
export default { data: () => ({ loading:false, form:{username:'',nickname:'',password:'',role:'USER'}, rules:{username:[{required:true,message:'请输入用户名',trigger:'blur'},{min:4,max:30,message:'用户名长度为4-30位',trigger:'blur'}],nickname:[{required:true,message:'请输入昵称或姓名',trigger:'blur'}],password:[{required:true,message:'请输入密码',trigger:'blur'},{min:8,max:64,message:'密码长度为8-64位',trigger:'blur'}]} }), methods:{ submit(){this.$refs.form.validate(async valid=>{if(!valid)return;this.loading=true;try{await register(this.form);this.$message.success(this.form.role==='PROFESSOR'?'注册成功，请等待管理员审核':'注册成功，请登录');this.$router.replace('/login')}finally{this.loading=false}})} } }
</script>
