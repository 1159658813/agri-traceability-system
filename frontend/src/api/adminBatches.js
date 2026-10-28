async function request(path, options = {}) {
  const response = await fetch(`/api/admin/batches${path}`, {
    credentials: 'same-origin',
    cache: 'no-store',
    ...options
  });
  const result = await response.json();
  if (!response.ok || result.code !== 200) {
    throw new Error(result.message || '请求失败');
  }
  return result.data;
}

export function getAdminBatches(status, pageNum = 1, pageSize = 10) {
  const query = new URLSearchParams({ status, pageNum, pageSize });
  return request(`?${query}`);
}

export function reviewBatch(id, decision, comment, csrfToken) {
  return request(`/${id}/review`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-CSRF-Token': csrfToken
    },
    body: JSON.stringify({ decision, comment })
  });
}
