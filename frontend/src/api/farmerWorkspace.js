export async function getFarmerOverview() {
  const response = await fetch('/api/farmer/overview', {
    credentials: 'same-origin',
    cache: 'no-store'
  });
  const result = await response.json();
  if (!response.ok || result.code !== 200) {
    throw new Error(result.message || '无法读取工作台数据');
  }
  return result.data;
}
