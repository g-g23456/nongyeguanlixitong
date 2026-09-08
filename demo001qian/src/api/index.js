import api from './request'

const postAction = (url, payload = {}) => api.post(url, payload)

export const authApi = {
  login(payload) {
    return postAction('/auth/login', payload)
  },
  getProfile(payload = {}) {
    return postAction('/auth/me', payload)
  },
  logout() {
    const saToken = sessionStorage.getItem('sa_token')
    return postAction('/auth/logout', { satoken: saToken })
  },
}

export const dashboardApi = {
  getOverview(payload = {}) {
    return postAction('/dashboard/overview', payload)
  },
}

export const farmlandApi = {
  list(payload = {}) {
    return postAction('/farmland/list', payload)
  },
  create(payload) {
    return postAction('/farmland/create', payload)
  },
  update(payload) {
    return postAction('/farmland/update', payload)
  },
  delete(payload) {
    return postAction('/farmland/delete', payload)
  },
  optimize(payload = {}) {
    return postAction('/farmland/optimize', payload)
  },
  getLatestOptimize(payload = {}) {
    return postAction('/farmland/optimize/latest', payload)
  },
  getBlocksByRegion(payload = {}) {
    return postAction('/farmland/optimize/blocks', payload)
  },
  rotation(payload = {}) {
    return postAction('/farmland/rotation', payload)
  },
}

export const waterApi = {
  quota(payload = {}) {
    return postAction('/water/quota', payload)
  },
  status(payload = {}) {
    return postAction('/water/status', payload)
  },
  allocation(payload = {}) {
    return postAction('/water/allocation', payload)
  },
  analysis(payload = {}) {
    return postAction('/water/analysis', payload)
  },
  updateQuota(payload = {}) {
    return postAction('/water/quota/update', payload)
  },
  delete(payload) {
    return postAction('/water/quota/delete', payload)
  },
}
export const seedApi = {
  inventory(payload = {}) {
    return postAction('/seed/inventory', payload)
  },
  create(payload) {
    return postAction('/seed/create', payload)
  },
  update(payload) {
    return postAction('/seed/update', payload)
  },
  delete(payload) {
    return postAction('/seed/delete', payload)
  },
  allocation(payload = {}) {
    return postAction('/seed/allocation', payload)
  },
  predict(payload = {}) {
    return postAction('/seed/predict', payload)
  },
}

export const laborApi = {
  list(payload = {}) {
    return postAction('/labor/list', payload)
  },
  create(payload) {
    return postAction('/labor/create', payload)
  },
  update(payload) {
    return postAction('/labor/update', payload)
  },
  delete(payload) {
    return postAction('/labor/delete', payload)
  },
  schedule(payload = {}) {
    return postAction('/labor/schedule', payload)
  },
  oldSchedule(payload = {}) {
    return postAction('/labor/old/schedule', payload)
  },
}

export const equipmentApi = {
  list(payload = {}) {
    return postAction('/equipment/list', payload)
  },
  create(payload) {
    return postAction('/equipment/create', payload)
  },
  update(payload) {
    return postAction('/equipment/update', payload)
  },
  delete(payload) {
    return postAction('/equipment/delete', payload)
  },
  status(payload = {}) {
    return postAction('/equipment/status', payload)
  },
  allocation(payload = {}) {
    return postAction('/equipment/allocation', payload)
  },
  maintenance(payload = {}) {
    return postAction('/equipment/maintenance', payload)
  },
}

export const aiApi = {
  decision(payload = {}) {
    return postAction('/ai/decision', payload)
  },
}

export const predictApi = {
  yield(payload = {}) {
    return postAction('/predict/yield', payload)
  },
  resource(payload = {}) {
    return postAction('/predict/resource', payload)
  },
}

export const systemApi = {
  users(payload = {}) {
    return postAction('/system/users', payload)
  },
  create(payload) {
    return postAction('/system/create', payload)
  },
}

export default {
  authApi,
  dashboardApi,
  farmlandApi,
  waterApi,
  seedApi,
  laborApi,
  equipmentApi,
  aiApi,
  predictApi,
  systemApi,
}