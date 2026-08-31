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
    const savedUser = localStorage.getItem('agri_user')
    const token = localStorage.getItem('agri_token')

    if (token) {
      authApi
        .getProfile()
        .then((res) => {
          const user = res?.data || res?.user || res || JSON.parse(savedUser || '{}')
          if (user && user.role) {
            this.userInfo = user
            this.isLoggedIn = true
            localStorage.setItem('agri_user', JSON.stringify(user))
          }
        })
        .catch(() => {
          if (savedUser) {
            try {
              this.userInfo = JSON.parse(savedUser)
              this.isLoggedIn = true
            } catch (e) {
              localStorage.removeItem('agri_user')
              localStorage.removeItem('agri_token')
            }
          }
        })
      return
    }

    if (savedUser) {
      try {
        this.userInfo = JSON.parse(savedUser)
        this.isLoggedIn = true
      } catch (e) {
        localStorage.removeItem('agri_user')
      }
    }
  },
  methods: {
    handleLoginSuccess(userData) {
      const finalUser = userData && userData.user ? userData.user : userData
      this.userInfo = finalUser
      this.isLoggedIn = true
      if (userData && userData.token) {
        localStorage.setItem('agri_token', userData.token)
      }
      if (userData && userData.saToken) {
        localStorage.setItem('sa_token', userData.saToken)
      }
      localStorage.setItem('agri_user', JSON.stringify(finalUser))
    },
    handleLogout() {
      this.isLoggedIn = false
      this.userInfo = { name: '', role: '', roleName: '' }
      localStorage.removeItem('agri_user')
      localStorage.removeItem('agri_token')
      localStorage.removeItem('sa_token')
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
