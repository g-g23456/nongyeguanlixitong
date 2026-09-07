import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 20000,
  headers: {
    'Content-Type': 'application/json',
  },
})

const AI_ENDPOINTS = [
  '/ai/decision',
  '/farmland/optimize',
  '/water/allocation',
  '/seed/allocation',
  '/seed/predict',
  '/labor/schedule',
  '/equipment/allocation',
  '/predict/yield',
  '/predict/resource',
]

const pendingMap = new Map()

function isAiEndpoint(url) {
  return AI_ENDPOINTS.some((ep) => url && url.includes(ep))
}

function buildRequestKey(config) {
  return config.url + '_' + JSON.stringify(config.data || {})
}

api.interceptors.request.use(
  (config) => {
    if (isAiEndpoint(config.url)) {
      const key = buildRequestKey(config)
      if (pendingMap.has(key)) {
        const controller = new AbortController()
        config.signal = controller.signal
        controller.abort()
        return Promise.reject(new Error('AI接口正在处理中，请勿重复请求'))
      }
      pendingMap.set(key, true)
      config._aiKey = key
    }

    const token = localStorage.getItem('agri_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    const saToken = localStorage.getItem('sa_token')
    if (saToken && saToken !== 'undefined') {
      config.headers['satoken'] = saToken
      if (config.url && !config.url.includes('/auth/login')) {
        const body = config.data || {}
        config.data = { satoken: saToken, ...body }
      }
    }
    return config
  },
  (error) => Promise.reject(error),
)

api.interceptors.response.use(
  (response) => {
    const key = response.config._aiKey
    if (key) {
      pendingMap.delete(key)
    }
    return response.data
  },
  (error) => {
    const key = error.config?._aiKey
    if (key) {
      pendingMap.delete(key)
    }
    const message = error.response?.data?.message || error.message || '请求失败'
    return Promise.reject(new Error(message))
  },
)

export default api