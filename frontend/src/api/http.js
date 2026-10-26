import axios from 'axios';

const http = axios.create({
  baseURL: '/',
  timeout: 10000
});

// 开发期利用 vue.config.js 的 /api 代理避免跨域。
// 目前后端还没有登录鉴权，前端也不存储或模拟登录令牌。
export default http;
