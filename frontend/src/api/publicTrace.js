export async function getPublicTrace(traceCode) {
  const response = await fetch(`/api/public/traces/${encodeURIComponent(traceCode)}`, {
    method: 'GET',
    credentials: 'omit',
    cache: 'no-store',
    headers: { Accept: 'application/json' }
  });

  let result;
  try {
    result = await response.json();
  } catch (error) {
    throw new Error('查询服务暂时不可用，请稍后再试');
  }

  if (response.status === 404) {
    throw new Error('未找到已发布的溯源批次，请检查溯源码');
  }
  if (!response.ok || result.code !== 200) {
    throw new Error(result.message || '查询失败，请稍后再试');
  }
  return result.data;
}
