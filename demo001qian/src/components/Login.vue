<template>
  <div class="login-page">
    <div class="particles">
      <div class="particle" v-for="i in 8" :key="i"></div>
    </div>

    <div class="login-box">
      <div class="login-left">
        <div class="logo-section">
          <div class="logo-icon">🌾</div>
          <h1>基于AI算法的农业资源<br />智能调配Web管理系统</h1>
          <p class="sub">纯软件·零硬件·十五五数字农业</p>
        </div>
        <div class="feature-list">
          <div class="feature-item">
            <div class="feature-icon">📊</div>
            <span>多维度数据可视化驾驶舱</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon">🤖</div>
            <span>AI智能优化调配算法引擎</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon">🌱</div>
            <span>耕地/水/农资/人力四维协同</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon">🔐</div>
            <span>RBAC权限管理与角色分级</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon">📈</div>
            <span>农业大数据分析与趋势预测</span>
          </div>
        </div>
      </div>

      <div class="login-right">
        <h2 class="login-title">欢迎登录</h2>
        <p class="login-sub">请选择角色并输入账号信息</p>

        <div class="error-msg" v-if="errorMsg">{{ errorMsg }}</div>

        <div class="role-selector">
          <div
            class="role-card"
            :class="{ active: currentRole === 'admin' }"
            @click="selectRole('admin')"
          >
            <div class="role-icon">👨‍💼</div>
            <div class="role-name">系统管理员</div>
          </div>
          <div
            class="role-card"
            :class="{ active: currentRole === 'dispatcher' }"
            @click="selectRole('dispatcher')"
          >
            <div class="role-icon">📋</div>
            <div class="role-name">生产调度员</div>
          </div>
          <div
            class="role-card"
            :class="{ active: currentRole === 'farmer' }"
            @click="selectRole('farmer')"
          >
            <div class="role-icon">👨‍🌾</div>
            <div class="role-name">片区经理/农户</div>
          </div>
          <div
            class="role-card"
            :class="{ active: currentRole === 'analyst' }"
            @click="selectRole('analyst')"
          >
            <div class="role-icon">📊</div>
            <div class="role-name">数据分析师</div>
          </div>
        </div>

        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label class="form-label">用户名</label>
            <input type="text" class="form-input" v-model="username" placeholder="请输入用户名" />
          </div>
          <div class="form-group">
            <label class="form-label">密码</label>
            <input type="password" class="form-input" v-model="password" placeholder="请输入密码" />
          </div>
          <button type="submit" class="login-btn">登 录</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { authApi } from '../api'

export default {
  name: 'Login',
  data() {
    return {
      currentRole: 'admin',
      username: '',
      password: '',
      errorMsg: '',
    }
  },
  mounted() {
    const saved = sessionStorage.getItem('agri_user')
    if (saved) {
      try {
        const userData = JSON.parse(saved)
        this.$emit('login-success', userData)
      } catch (e) {
        sessionStorage.removeItem('agri_user')
      }
    }
  },
  methods: {
    selectRole(role) {
      this.currentRole = role
      this.errorMsg = ''
    },
    showError(msg) {
      this.errorMsg = msg
    },
    async handleLogin() {
      if (!this.username.trim()) {
        this.showError('请输入用户名')
        return
      }
      if (!this.password) {
        this.showError('请输入密码')
        return
      }

      try {
        const res = await authApi.login({
          username: this.username.trim(),
          password: this.password,
          role: this.currentRole,
        })

        console.log('=== 登录原始响应 ===', res)

        const result = res?.data || res
        console.log('=== 提取 result(res?.data || res) ===', result)

        const user = result?.user || (result?.id ? result : null)
        console.log('=== 提取 user ===', user)

        const token = result?.token || result?.accessToken
        const saToken =
          result?.saToken ||
          result?.satoken ||
          result?.tokenValue ||
          result?.sa_token ||
          result?.token

        console.log('=== token ===', token, '=== saToken ===', saToken)
        console.log('=== user.role ===', user?.role)

        if (user && user.role) {
          if (token) {
            sessionStorage.setItem('agri_token', token)
          }
          if (saToken) {
            sessionStorage.setItem('sa_token', saToken)
          }
          sessionStorage.setItem('agri_user', JSON.stringify(user))
          this.$emit('login-success', token ? { ...user, token, saToken } : user)
          return
        }

        if (token || saToken) {
          console.log('=== 响应中无 user 对象，尝试通过 token 获取用户信息 ===')
          if (token) {
            sessionStorage.setItem('agri_token', token)
          }
          if (saToken) {
            sessionStorage.setItem('sa_token', saToken)
          }
          try {
            const profileRes = await authApi.getProfile()
            console.log('=== getProfile 响应 ===', profileRes)
            const profileData = profileRes?.data || profileRes
            const profileUser = profileData?.user || (profileData?.id ? profileData : null)
            if (profileUser && profileUser.role) {
              sessionStorage.setItem('agri_user', JSON.stringify(profileUser))
              this.$emit('login-success', token ? { ...profileUser, token, saToken } : profileUser)
              return
            }
            console.error('=== getProfile 返回的 user 无效 ===', profileUser)
          } catch (profileError) {
            console.error('=== getProfile 调用失败 ===', profileError)
          }
        }

        const backendMsg = res?.message || res?.msg || result?.message || result?.msg
        console.error('=== 登录失败：user 或 user.role 为空 ===', {
          user,
          role: user?.role,
          res,
          result,
        })
        this.showError(backendMsg || '登录失败：响应数据格式异常，请查看控制台日志')
      } catch (error) {
        console.error('=== 登录异常 ===', error)
        this.showError('登录失败：' + (error.message || '后端服务不可用，请检查服务是否启动'))
        return
      }
    },
  },
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #001529, #003d6b 50%, #00664d);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  font-family:
    'Microsoft YaHei',
    -apple-system,
    BlinkMacSystemFont,
    'Segoe UI',
    Roboto,
    sans-serif;
}

.login-page::before {
  content: '';
  position: absolute;
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(82, 196, 26, 0.15), transparent 70%);
  top: -200px;
  left: -200px;
  border-radius: 50%;
}

.login-page::after {
  content: '';
  position: absolute;
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(24, 144, 255, 0.15), transparent 70%);
  bottom: -150px;
  right: -150px;
  border-radius: 50%;
}

.particles {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.particle {
  position: absolute;
  width: 6px;
  height: 6px;
  background: rgba(82, 196, 26, 0.4);
  border-radius: 50%;
  animation: float 15s infinite ease-in-out;
}

.particle:nth-child(1) {
  left: 10%;
  top: 20%;
  animation-delay: 0s;
}
.particle:nth-child(2) {
  left: 30%;
  top: 60%;
  animation-delay: 2s;
}
.particle:nth-child(3) {
  left: 50%;
  top: 30%;
  animation-delay: 4s;
}
.particle:nth-child(4) {
  left: 70%;
  top: 70%;
  animation-delay: 6s;
}
.particle:nth-child(5) {
  left: 85%;
  top: 40%;
  animation-delay: 8s;
}
.particle:nth-child(6) {
  left: 20%;
  top: 80%;
  animation-delay: 10s;
}
.particle:nth-child(7) {
  left: 60%;
  top: 15%;
  animation-delay: 12s;
}
.particle:nth-child(8) {
  left: 90%;
  top: 85%;
  animation-delay: 14s;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0) translateX(0);
    opacity: 0.3;
  }
  50% {
    transform: translateY(-30px) translateX(20px);
    opacity: 0.8;
  }
}

.login-box {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  width: 920px;
  height: 580px;
  display: flex;
  overflow: hidden;
  position: relative;
  z-index: 10;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #002140, #003d6b);
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: #fff;
  position: relative;
}

.login-left::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%2352c41a' fill-opacity='0.05'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
  opacity: 0.5;
}

.logo-section {
  position: relative;
  z-index: 1;
}

.logo-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.login-left h1 {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 10px;
  background: linear-gradient(90deg, #52c41a, #1890ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1.4;
}

.login-left .sub {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 28px;
}

.feature-list {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
}

.feature-icon {
  width: 32px;
  height: 32px;
  background: rgba(82, 196, 26, 0.2);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.login-right {
  width: 420px;
  padding: 40px 45px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.login-title {
  font-size: 22px;
  color: #333;
  margin-bottom: 6px;
  font-weight: 600;
}

.login-sub {
  font-size: 13px;
  color: #999;
  margin-bottom: 20px;
}

.error-msg {
  background: #fff2f0;
  border: 1px solid #ffccc7;
  color: #ff4d4f;
  padding: 10px 14px;
  border-radius: 6px;
  font-size: 13px;
  margin-bottom: 14px;
  animation: shake 0.3s ease-in-out;
}

@keyframes shake {
  0%,
  100% {
    transform: translateX(0);
  }
  25% {
    transform: translateX(-5px);
  }
  75% {
    transform: translateX(5px);
  }
}

.role-selector {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  margin-bottom: 16px;
}

.role-card {
  padding: 12px;
  border: 2px solid #e8e8e8;
  border-radius: 8px;
  cursor: pointer;
  text-align: center;
  background: #fafafa;
  transition: all 0.3s;
}

.role-card:hover {
  border-color: #52c41a;
  background: #f6ffed;
}

.role-card.active {
  border-color: #52c41a;
  background: #f6ffed;
  box-shadow: 0 2px 8px rgba(82, 196, 26, 0.2);
}

.role-card .role-icon {
  font-size: 24px;
  margin-bottom: 6px;
}

.role-card .role-name {
  font-size: 13px;
  font-weight: 600;
  color: #333;
}

.role-card.active .role-name {
  color: #52c41a;
}

.form-group {
  margin-bottom: 14px;
}

.form-label {
  display: block;
  font-size: 12px;
  color: #666;
  margin-bottom: 6px;
  font-weight: 500;
}

.form-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  font-size: 13px;
  outline: none;
  transition: all 0.3s;
}

.form-input:focus {
  border-color: #52c41a;
  box-shadow: 0 0 0 2px rgba(82, 196, 26, 0.1);
}

.login-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #52c41a, #389e0d);
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  letter-spacing: 2px;
  transition: all 0.3s;
}

.login-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(82, 196, 26, 0.4);
}

.login-btn:active {
  transform: translateY(0);
}

@media (max-width: 960px) {
  .login-box {
    width: 90%;
    max-width: 450px;
    height: auto;
    flex-direction: column;
  }
  .login-left {
    padding: 30px;
  }
  .login-left h1 {
    font-size: 20px;
  }
  .feature-list {
    display: none;
  }
  .login-right {
    width: 100%;
    padding: 30px;
  }
}
</style>