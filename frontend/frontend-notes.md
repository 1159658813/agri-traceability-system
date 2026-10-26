# 页面和接口说明

- `/`：系统工作台；真实调用 `/api/system/status` 和 `/api/bases/page?pageNum=1&pageSize=5`。
- `/bases`：种植基地列表；真实调用 `/api/bases/page?pageNum=&pageSize=`。
- `/module/:section`：业务模块占位页；没有伪造统计数据。
- `/trace`：消费者溯源页面的静态预览；查询功能明确禁用。

本项目没有登录页面或身份模拟：目前后端未完成认证和授权，故只能用于本地开发预览，不应作为公开互联网部署版本。
