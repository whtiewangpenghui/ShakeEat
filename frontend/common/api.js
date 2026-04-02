import { BASE_URL } from './config';

const JSON_HEADERS = {
  'Content-Type': 'application/json; charset=UTF-8',
  Accept: 'application/json'
};

function request(path, options = {}) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${BASE_URL}${path}`,
      method: options.method || 'GET',
      data: options.data,
      header: {
        ...JSON_HEADERS,
        ...(options.header || {})
      },
      success: (response) => {
        const body = response.data || {};
        if (response.statusCode >= 200 && response.statusCode < 300 && body.success) {
          resolve(body.data);
          return;
        }
        reject(new Error(body.message || '请求失败'));
      },
      fail: (error) => {
        reject(new Error(error.errMsg || '网络异常'));
      }
    });
  });
}

export function getAllTags() {
  return request('/api/tags/all');
}

export function createTag(payload) {
  return request('/api/tags', {
    method: 'POST',
    data: payload
  });
}

export function updateTag(id, payload) {
  return request(`/api/tags/${id}`, {
    method: 'PUT',
    data: payload
  });
}

export function deleteTag(id) {
  return request(`/api/tags/${id}`, {
    method: 'DELETE'
  });
}

export function getFoods() {
  return request('/api/foods');
}

export function exportFoods() {
  return request('/api/foods/export');
}

export function importFoods(payload) {
  return request('/api/foods/import', {
    method: 'POST',
    data: payload
  });
}

export function getFood(id) {
  return request(`/api/foods/${id}`);
}

export function createFood(payload) {
  return request('/api/foods', {
    method: 'POST',
    data: payload
  });
}

export function updateFood(id, payload) {
  return request(`/api/foods/${id}`, {
    method: 'PUT',
    data: payload
  });
}

export function deleteFood(id) {
  return request(`/api/foods/${id}`, {
    method: 'DELETE'
  });
}

export function updateFoodEnabled(id, enabled) {
  return request(`/api/foods/${id}/enable`, {
    method: 'PATCH',
    data: { enabled }
  });
}

export function getPreference() {
  return request('/api/preferences');
}

export function updatePreference(payload) {
  return request('/api/preferences', {
    method: 'PUT',
    data: payload
  });
}

export function shake(payload) {
  return request('/api/shake', {
    method: 'POST',
    data: payload
  });
}

export function getShakeQuota() {
  return request('/api/shake/quota');
}

export function acceptShake(historyId) {
  return request(`/api/shake/${historyId}/accept`, {
    method: 'POST'
  });
}

export function rejectShake(historyId, payload) {
  return request(`/api/shake/${historyId}/reject`, {
    method: 'POST',
    data: payload || {}
  });
}

export function getHistories(status = '', pageNo = 1, pageSize = 10) {
  const query = [`pageNo=${encodeURIComponent(pageNo)}`, `pageSize=${encodeURIComponent(pageSize)}`];
  if (status) {
    query.push(`status=${encodeURIComponent(status)}`);
  }
  return request(`/api/histories?${query.join('&')}`);
}

export function getHistory(id) {
  return request(`/api/histories/${id}`);
}

export function getHistoryStats() {
  return request('/api/histories/stats');
}
