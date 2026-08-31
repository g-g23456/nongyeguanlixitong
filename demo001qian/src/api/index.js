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
    const saToken = localStorage.getItem('sa_token')
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
  optimize(payload = {}) {
    return postAction('/farmland/optimize', payload)
  },
  rotation(payload = {}) {
    return postAction('/farmland/rotation', payload)
  },
}

export const waterApi = {
  quota(payload = {}) {
    return postAction('/water/quota', payload)
  },
  allocation(payload = {}) {
    return postAction('/water/allocation', payload)
  },
  analysis(payload = {}) {
    return postAction('/water/analysis', payload)
  },
}

export const seedApi = {
  inventory(payload = {}) {
    return postAction('/seed/inventory', payload)
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
  schedule(payload = {}) {
    return postAction('/labor/schedule', payload)
  },
}

export const equipmentApi = {
  list(payload = {}) {
    return postAction('/equipment/list', payload)
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
  roles(payload = {}) {
    return postAction('/system/roles', payload)
  },
  permissions(payload = {}) {
    return postAction('/system/permissions', payload)
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
