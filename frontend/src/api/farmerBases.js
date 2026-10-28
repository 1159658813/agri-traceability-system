import { currentUser } from './auth';

async function request(path, options = {}) {
  const response = await fetch(`/api/farmer/bases${path}`, {
    credentials: 'same-origin',
    cache: 'no-store',
    ...options
  });
  const body = await response.json();
  if (!response.ok || body.code !== 200) {
    throw new Error(body.message || '基地操作失败');
  }
  return body.data;
}

async function write(path, method, payload) {
  const user = await currentUser();
  return request(path, {
    method,
    headers: { 'Content-Type': 'application/json', 'X-CSRF-Token': user.csrfToken },
    body: JSON.stringify(payload)
  });
}

export function listFarmerBases(pageNum = 1, pageSize = 10) {
  return request(`?pageNum=${pageNum}&pageSize=${pageSize}`);
}

export function getFarmerBase(id) { return request(`/${id}`); }
export function createFarmerBase(form) { return write('', 'POST', form); }
export function updateFarmerBase(id, form) { return write(`/${id}`, 'PUT', form); }
export function setFarmerBaseStatus(id, status) {
  return write(`/${id}/status`, 'PUT', { status });
}
