export async function getBatchCompleteness(batchId) {
  const response = await fetch(
    `/api/farmer/batches/${encodeURIComponent(batchId)}/completeness`,
    { credentials: 'same-origin', cache: 'no-store' }
  );
  const result = await response.json();
  if (!response.ok || result.code !== 200) {
    throw new Error(result.message || '读取完整性评分失败');
  }
  return result.data;
}
