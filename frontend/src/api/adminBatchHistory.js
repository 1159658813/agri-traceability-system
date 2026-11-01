export async function getAdminBatchHistory(batchId) {
  const response = await fetch(`/api/admin/batches/${encodeURIComponent(String(batchId))}/history`, {
    credentials: 'same-origin',
    cache: 'no-store'
  });
  const result = await response.json();
  if (!response.ok || result.code !== 200) {
    throw new Error(result.message || '读取审核历史失败');
  }
  return result.data;
}
