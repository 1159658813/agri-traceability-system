import http from './http';

function request(url, params) {
  return http.get(url, { params }).then(res => {
    const body = res.data;
    if (!body || body.code !== 200) throw new Error((body && body.message) || '数据读取失败');
    return body.data;
  });
}

export const getOverview = () => request('/api/public/stats/overview');
export const getTrend = () => request('/api/public/stats/trend');
export const getCategories = () => request('/api/public/stats/categories');
export const getRegions = () => request('/api/public/stats/regions');
export const getNotices = (limit = 5) => request('/api/public/notices', { limit });
export const getNotice = id => request(`/api/public/notices/${id}`);
