# 流光工作台 · Lumen Desk（演示项目）

这是一份面向公开作品集的 **Java 17 + Spring Boot 3 / Vue 3** 示例，用于展示分层 REST 接口与后台管理界面的代码组织。

作者：[柴铭](https://github.com/chaiming891-dev)（GitHub: `chaiming891-dev`）

> **请先读这里：** 本仓库全部是虚构的演示数据与演示功能，**不是**生产系统，也 **不是** 任何公司、运营商或内部中台的真实项目。页面和接口里会明确标注「演示」。

## 演示里有什么

两个业务资源，足够看完一次完整的增查改与审核流：

1. **能力目录（Capability）**  
   登记一套虚构的内部服务能力（编码、版本、负责人、状态、说明）。支持关键词 / 状态筛选、分页、新建与编辑。
2. **开通申请（Access Request）**  
   针对某项能力提交开通申请，支持待审核 / 通过 / 驳回，以及按能力、状态筛选与分页。

技术要点：

- 后端：经典分层（`controller` / `service` / `repository`）、Bean Validation、统一异常处理、Spring Data 分页、启动时写入演示种子数据。
- 数据库：嵌入式 **H2（内存）**，无需 Docker、无需账号密码、无需外部中间件。可在浏览器打开 H2 Console 查看表数据。
- 前端：Vue 3 + Vite 的简洁管理后台，列表 / 筛选 / 新建 / 审核，开发服务器把 `/api` 代理到后端。

## 运行环境

- JDK 17 或更高版本
- Maven 3.9+（或使用 IDE 自带 Maven）
- Node.js 18+ 与 npm

## 启动后端

```bash
cd backend
mvn spring-boot:run
```

启动成功后：

| 地址 | 说明 |
| --- | --- |
| http://localhost:8080/api/health | 健康检查（返回 `demo: true`） |
| http://localhost:8080/api/capabilities | 能力目录（分页） |
| http://localhost:8080/api/access-requests | 开通申请（分页） |
| http://localhost:8080/h2-console | H2 控制台 |

H2 Console 连接信息（仅本地演示）：

- JDBC URL：`jdbc:h2:mem:lumendesk`
- User Name：`sa`
- Password：留空
- Driver Class：`org.h2.Driver`

可选测试：

```bash
cd backend
mvn test
```

## 启动前端

另开一个终端：

```bash
cd frontend
npm install
npm run dev
```

浏览器打开 [http://localhost:5173](http://localhost:5173)。

`vite.config.js` 已将 `/api` 代理到 `http://localhost:8080`，因此需要先启动后端。若只想改端口，请同时修改：

- 后端：`backend/src/main/resources/application.yml` 中的 `server.port`
- 前端：`frontend/vite.config.js` 中的 `server.proxy["/api"].target`

## 项目结构

```
java-vue-demos/
  backend/          Spring Boot 3 API
  frontend/         Vue 3 + Vite 管理界面
  README.md
  LICENSE           MIT，Copyright (c) 2026 柴铭
  .gitignore
```

## 许可

MIT License © 2026 柴铭
