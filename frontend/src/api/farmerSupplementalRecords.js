import { currentUser } from './auth';

async function request(batchId, suffix, options = {}) {
  const response = await fetch(`/api/farmer/batches/${encodeURIComponent(batchId)}/records${suffix}`, {
    credentials: 'same-origin', cache: 'no-store', ...options
  });
  const result = await response.json();
  if (!response.ok || result.code !== 200) {
    throw new Error(result.message || '质检或流通记录操作失败');
  }
  return result.data;
}

async function save(batchId, suffix, method, form) {
  const account = await currentUser();
  return request(batchId, suffix, {
    method,
    headers: { 'Content-Type': 'application/json', 'X-CSRF-Token': account.csrfToken },
    body: JSON.stringify(form)
  });
}

export const getSupplementalRecords = batchId => request(batchId, '/extended');
export const addQuality = (batchId, form) => save(batchId, '/quality', 'POST', form);
export const editQuality = (batchId, id, form) => save(batchId, `/quality/${id}`, 'PUT', form);
export const addCirculation = (batchId, form) => save(batchId, '/circulation', 'POST', form);
export const editCirculation = (batchId, id, form) => save(batchId, `/circulation/${id}`, 'PUT', form);
