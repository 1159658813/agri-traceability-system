import { currentUser } from './auth';

async function request(batchId, suffix = '', options = {}) {
  const response = await fetch(`/api/farmer/batches/${encodeURIComponent(batchId)}/records${suffix}`, {
    credentials: 'same-origin', cache: 'no-store', ...options
  });
  const result = await response.json();
  if (!response.ok || result.code !== 200) {
    throw new Error(result.message || '生产记录操作失败');
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

export const getBatchRecords = batchId => request(batchId);
export const addFarmWork = (batchId, form) => save(batchId, '/farm', 'POST', form);
export const editFarmWork = (batchId, recordId, form) => save(batchId, `/farm/${recordId}`, 'PUT', form);
export const addHarvest = (batchId, form) => save(batchId, '/harvest', 'POST', form);
export const editHarvest = (batchId, recordId, form) => save(batchId, `/harvest/${recordId}`, 'PUT', form);
