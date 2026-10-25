import http from './http';

export function getSystemStatus() {
  return http.get('/api/system/status');
}
