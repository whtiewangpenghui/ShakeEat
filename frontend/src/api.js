const JSON_HEADERS = {
  "Content-Type": "application/json; charset=UTF-8",
  Accept: "application/json"
};

async function request(path, options = {}) {
  const response = await fetch(path, options);
  const data = await response.json();
  if (!response.ok || !data.success) {
    throw new Error(data.message || "请求失败");
  }
  return data.data;
}

export function getTags() {
  return request("/api/tags");
}

export function getAllTags() {
  return request("/api/tags/all");
}

export function createTag(payload) {
  return request("/api/tags", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  });
}

export function updateTag(id, payload) {
  return request(`/api/tags/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  });
}

export function deleteTag(id) {
  return request(`/api/tags/${id}`, {
    method: "DELETE"
  });
}

export function getFoods() {
  return request("/api/foods");
}

export function exportFoods() {
  return request("/api/foods/export");
}

export function importFoods(payload) {
  return request("/api/foods/import", {
    method: "POST",
    headers: JSON_HEADERS,
    body: JSON.stringify(payload)
  });
}

export function getFood(id) {
  return request(`/api/foods/${id}`);
}

export function createFood(payload) {
  return request("/api/foods", {
    method: "POST",
    headers: JSON_HEADERS,
    body: JSON.stringify(payload)
  });
}

export function updateFood(id, payload) {
  return request(`/api/foods/${id}`, {
    method: "PUT",
    headers: JSON_HEADERS,
    body: JSON.stringify(payload)
  });
}

export function deleteFood(id) {
  return request(`/api/foods/${id}`, {
    method: "DELETE"
  });
}

export function updateFoodEnabled(id, enabled) {
  return request(`/api/foods/${id}/enable`, {
    method: "PATCH",
    headers: JSON_HEADERS,
    body: JSON.stringify({ enabled })
  });
}

export function getPreference() {
  return request("/api/preferences");
}

export function updatePreference(payload) {
  return request("/api/preferences", {
    method: "PUT",
    headers: JSON_HEADERS,
    body: JSON.stringify(payload)
  });
}

export function shake(payload) {
  return request("/api/shake", {
    method: "POST",
    headers: JSON_HEADERS,
    body: JSON.stringify(payload)
  });
}

export function getShakeQuota() {
  return request("/api/shake/quota");
}

export function acceptShake(historyId) {
  return request(`/api/shake/${historyId}/accept`, {
    method: "POST"
  });
}

export function rejectShake(historyId, payload) {
  return request(`/api/shake/${historyId}/reject`, {
    method: "POST",
    headers: JSON_HEADERS,
    body: JSON.stringify(payload)
  });
}

export function getHistories(status = "", pageNo = 1, pageSize = 10) {
  const query = new URLSearchParams({
    pageNo: String(pageNo),
    pageSize: String(pageSize)
  });
  if (status) {
    query.set("status", status);
  }
  return request(`/api/histories?${query.toString()}`);
}

export function getHistory(id) {
  return request(`/api/histories/${id}`);
}

export function getHistoryStats() {
  return request("/api/histories/stats");
}
