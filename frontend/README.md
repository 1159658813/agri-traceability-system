# 田野档案 · 农产品产地溯源管理平台（前端 V1）

本源码适配当前毕业设计仓库 `agri-traceability-system`：

- Vue `2.7.16`、Vue Router `3.6.5`、Element UI `2.15.14`
- Vue CLI Service `5.0.9`，Node.js 22，npm 10
- 保留后端 Spring Boot（端口 8080）与前端 Vue 2（端口 8081）分离。
- 原创的自然风格后台布局，不依赖已有 Admin 大模板。

## 一、放到 IDEA 项目

将整个 `frontend` 文件夹复制到你的仓库根目录：

```text
agri-traceability-system/
├── backend/
├── frontend/              <- 本项目
├── sql/
├── docs/
└── .gitignore
```

**不要**放在 `backend/src` 下面，也不要覆盖已经有代码的 frontend 文件夹。如果你已自己创建了前端，先比较文件内容。

IDEA 2023.3.5：直接从项目根目录打开 Terminal（PowerShell）。无需全局安装 Vue CLI。

```powershell
cd .rontend
npm install
npm run serve
```

浏览器访问 http://127.0.0.1:8081/ ，也可以访问 http://127.0.0.1:8081/bases 。

先从 IDEA 启动现有 `AgriTraceabilityApplication`，确认 `http://localhost:8080/api/bases/page?pageNum=1&pageSize=5` 正常返回包含 `data.records` 的 JSON。

vue.config.js 中把 `/api` 代理给 `http://127.0.0.1:8080`，因此前端不需要修改 Spring Boot CORS 配置，开发时也不需要写死后端地址。官方代理参考：https://cli.vuejs.org/config/#devserver-proxy

如需要验证生产构建，在前端目录执行 `npm run build`，生成的 `dist/` 不纳入 Git。

## 二、V1 页面与功能

| 路由 | 功能状态 |
|---|---|
| `/` | 工作台：后端健康响应、基地总量和最近基地均取自真实接口 |
| `/bases` | 基地列表：分页、刷新、本页关键词筛选、真实字段详情抽屉 |
| `/module/products` 等 | 后续模块规划页面，均明确标记未接入 |
| `/trace` | 手机友好的消费者溯源视觉预览，查询操作禁用 |

### 与后端的对接约定

- `GET /api/system/status` → `{ "code": 200, "message": "..." }`
- `GET /api/bases/page?pageNum=1&pageSize=10` → `{ "code":200,"message":"操作成功","data": {"records": [...],"total": 1,"size":10,"current":1,"pages":1}}`

若基地分页结构不是 `data.records`，页面会明确提示接口结构不匹配，不会伪造数据。

目前仅查询，不提供新增/编辑/删除；因后端尚无认证/授权，不建议将现有管理 API 暴露到公网。

## 三、后续开发路线

1. 将 `feature/api-foundation` 通过 PR 合并到 main；从稳定 main 创建 `feature/vue-admin`。
2. 前端 V1 接口联调与页面样式调整。
3. 后端开发登录、角色和农户数据隔离，再增加前端认证与管理表单。
4. 实现产品、批次、质检、流通等真实 API 与页面。
5. 实现二维码、公众端完整溯源查询与信息完整性评价。

## 四、常见问题

- `npm install` 失败：检查 npm 镜像设置/代理与网络、Node 版本和错误日志。
- `8081` 被占用：关闭重复的 Vue 服务，或调整 `vue.config.js` 的端口。
- 工作台显示服务未连接：先确保 Spring Boot 正在运行于 8080，且本地接口可访问。
- 页面刷新后路由 404：本地 Vue CLI dev server 支持 history fallback。生产部署需配置 Web 服务器的 SPA 路由回退。
- 不需要安装 `vue-template-compiler`：Vue 2.7 已将其集成到 `vue` 包的编译入口。

## 五、Git 提交（只在检查源码后执行）

```powershell
# 在仓库根目录
 git status
 git add frontend
 git diff --cached --stat
 git commit -m "feat: add Vue2 agricultural dashboard prototype"
 git push -u origin feature/vue-admin
```

请勿将 `.env`、数据库密码、本地 tokens、`node_modules`、`dist` 加入公开仓库。
