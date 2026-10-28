import { currentUser } from './auth';

async function request(path, options = {}) {
  const response = await fetch(`/api/farmer/products${path}`, {
    credentials: 'same-origin', cache: 'no-store', ...options
  });
  const body = await response.json();
  if (!response.ok || body.code !== 200) {
    throw new Error(body.message || '产品操作失败');
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

export const listProductCategories = () => request('/categories');
export const listFarmerProducts = (pageNum = 1, pageSize = 10) =>
  request(`?pageNum=${pageNum}&pageSize=${pageSize}`);
export const getFarmerProduct = id => request(`/${id}`);
export const createFarmerProduct = form => write('', 'POST', form);
export const updateFarmerProduct = (id, form) => write(`/${id}`, 'PUT', form);
export const setFarmerProductStatus = (id, status) => write(`/${id}/status`, 'PUT', { status });
