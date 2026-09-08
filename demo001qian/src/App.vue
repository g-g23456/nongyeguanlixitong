<template>
  <div id="app">
    <Login v-if="!isLoggedIn" @login-success="handleLoginSuccess" />
    <MainSystem v-else :user-info="userInfo" @logout="handleLogout" />
  </div>
</template>

<script>
import Login from './components/Login.vue'
import MainSystem from './components/MainSystem.vue'
import { authApi } from './api'

export default {
  name: 'App',
  components: {
    Login,
    MainSystem,
  },
  data() {
    return {
      isLoggedIn: false,
      userInfo: {
        name: '',
        role: '',
        roleName: '',
      },
    }
  },
  mounted() {
    const savedUser = sessionStorage.getItem('agri_user')
    const token = sessionStorage.getItem('agri_token')

    if (token) {
      authApi
        .getProfile()
        .then((res) => {
          const user = res?.data || res?.user || res || (savedUser ? JSON.parse(savedUser) : {})
          if (user && user.role) {
            this.userInfo = user
            this.isLoggedIn = true
            sessionStorage.setItem('agri_user', JSON.stringify(user))
          } else {
            this.clearLoginState()
          }
        })
        .catch(() => {
          this.clearLoginState()
        })
      return
    }

    this.clearLoginState()
  },
  methods: {
    clearLoginState() {
      this.isLoggedIn = false
      this.userInfo = { name: '', role: '', roleName: '' }
      sessionStorage.removeItem('agri_user')
      sessionStorage.removeItem('agri_token')
      sessionStorage.removeItem('sa_token')
    },
    handleLoginSuccess(userData) {
      const finalUser = userData && userData.user ? userData.user : userData
      this.userInfo = finalUser
      this.isLoggedIn = true
      if (userData && userData.token) {
        sessionStorage.setItem('agri_token', userData.token)
      }
      if (userData && userData.saToken) {
        sessionStorage.setItem('sa_token', userData.saToken)
      }
      sessionStorage.setItem('agri_user', JSON.stringify(finalUser))
    },
    handleLogout() {
      this.clearLoginState()
      authApi.logout().catch(() => {})
    },
  },
}
</script>

<style>
html,
body {
  margin: 0;
  padding: 0;
  height: 100%;
}

#app {
  height: 100%;
}
</style>