import { currentUser } from './auth';

async function request(path, options = {}) {
  const response = await fetch(`/api/farmer/batches${path}`, {
    credentials: 'same-origin', cache: 'no-store', ...options
  });
  const result = await response.json();
  if (!response.ok || result.code !== 200) {
    throw new Error(result.message || '批次操作失败');
  }
  return result.data;
}

async function write(path, method, data) {
  const account = await currentUser();
  return request(path, {
    method,
    headers: {
      'X-CSRF-Token': account.csrfToken,
      ...(data === undefined ? {} : { 'Content-Type': 'application/json' })
    },
    ...(data === undefined ? {} : { body: JSON.stringify(data) })
  });
}

export const listBatchOptions = () => request('/options');
export const listFarmerBatches = (pageNum = 1, pageSize = 10) =>
  request(`?pageNum=${pageNum}&pageSize=${pageSize}`);
export const createFarmerBatch = form => write('', 'POST', form);
export const updateFarmerBatch = (id, form) => write(`/${id}`, 'PUT', form);
export const submitFarmerBatch = id => write(`/${id}/submit`, 'POST');
