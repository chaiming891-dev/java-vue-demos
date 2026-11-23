const BASE = '/api'

function clean(params = {}) {
  const search = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (value === undefined || value === null || value === '') {
      return
    }
    search.set(key, String(value))
  })
  return search.toString()
}

async function request(path, options = {}) {
  const response = await fetch(`${BASE}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {})
    },
    ...options
  })

  const payload = await response.json().catch(() => ({}))
  if (!response.ok) {
    const error = new Error(payload.message || `请求失败（${response.status}）`)
    error.fields = payload.fields || {}
    error.code = payload.code
    throw error
  }
  return payload
}

export const api = {
  health: () => request('/health'),
  listCapabilities: (params) => request(`/capabilities?${clean(params)}`),
  getCapability: (id) => request(`/capabilities/${id}`),
  createCapability: (body) =>
    request('/capabilities', { method: 'POST', body: JSON.stringify(body) }),
  updateCapability: (id, body) =>
    request(`/capabilities/${id}`, { method: 'PUT', body: JSON.stringify(body) }),
  listRequests: (params) => request(`/access-requests?${clean(params)}`),
  createRequest: (body) =>
    request('/access-requests', { method: 'POST', body: JSON.stringify(body) }),
  reviewRequest: (id, body) =>
    request(`/access-requests/${id}/review`, { method: 'POST', body: JSON.stringify(body) })
}
