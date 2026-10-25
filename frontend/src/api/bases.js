import http from './http';

export function getBasePage(pageNum = 1, pageSize = 10) {
  return http.get('/api/bases/page', { params: { pageNum, pageSize } });
}
