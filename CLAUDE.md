# CLAUDE.md

## 项目概述

小区物业管理系统 — 基于 Spring Boot 3 + React 的前后端分离 Web 应用。
包含 8 个业务模块：业主、员工、房屋、费用、停车位、投诉、报修、值班，以及 JWT 登录认证。

## 技术栈

| 层 | 技术 | 版本 |
|---|---|---|
| 后端框架 | Spring Boot | 3.2.5 |
| ORM | MyBatis | 3.0.3 (Spring Boot Starter) |
| 数据库 | MySQL | 8.0.33 |
| 连接池 | Alibaba Druid | 1.2.21 |
| 认证 | Spring Security + JWT (jjwt) | 0.12.5 |
| API 文档 | SpringDoc OpenAPI (Swagger UI) | 2.8.6 |
| 前端框架 | React | 19.2 |
| 前端语言 | TypeScript | 6.0 |
| 前端构建 | Vite | 8.x |
| 样式 | Tailwind CSS | 4.3 |
| 路由 | React Router | 7.x |
| HTTP | Axios | 1.16 |
| 图标 | Lucide React | — |
| Java | JDK 17 | — |
| Node.js | 24.x | — |

## 目录结构

```
├── src/main/
│   ├── java/com/property/
│   │   ├── PropertyManagementApplication.java  # Spring Boot 启动类
│   │   ├── controller/    # 9 个 Controller (8 业务 + Auth)
│   │   ├── entity/        # 9 个实体类 (8 业务 + User)
│   │   ├── mapper/        # 9 个 MyBatis Mapper 接口
│   │   ├── service/       # 9 个 Service 接口 + Impl
│   │   └── security/      # JWT 工具 + 过滤器 + SecurityConfig (RBAC)
│   └── resources/
│       ├── application.yml           # Spring Boot 配置
│       ├── property_management.sql   # 建库建表 + 测试数据
│       └── mapper/                   # 9 个 MyBatis XML 映射文件
├── frontend/                         # React 前端
│   ├── src/
│   │   ├── api.ts          # Axios API 层
│   │   ├── types.ts        # TypeScript 类型定义
│   │   ├── components/     # 通用组件 (Layout, DataTable, FormModal, ConfirmDialog)
│   │   └── pages/          # 页面 (Login, Dashboard, 8 业务列表页)
│   ├── vite.config.ts      # Vite 配置 + API 代理
│   └── package.json
└── docs/                             # 项目文档
    ├── 答辩稿.md                     # 答辩 PPT 提纲 + 讲稿 + 问答准备
    ├── 项目记录.md                   # 开发问题与解决方案
    └── archive/                      # AI 提示词归档
```

## 构建与启动

### 前置条件

- JDK 17+
- Maven 3.x
- MySQL 8.x（localhost:3306）
- Node.js 18+

### 启动步骤

1. **初始化数据库**：在 MySQL 中执行 `src/main/resources/property_management.sql`
2. **修改数据库密码**：编辑 `src/main/resources/application.yml` 中的 `spring.datasource.password`
3. **启动后端**：`mvn spring-boot:run`（或在 IDEA 中运行 Application 类）
4. **启动前端**：`cd frontend && npm install && npm run dev`
5. **访问**：`http://localhost:3000`
6. **默认账号**：admin / admin123

### API 说明

- 所有 API 前缀：`/api/`
- 认证接口：`POST /api/auth/login`，`GET /api/auth/info`
- 业务接口：`/api/{模块}/page|get/{id}|add|update|delete/{id}`
- 需在请求头携带 `Authorization: Bearer <token>`
- Swagger UI：`http://localhost:8080/swagger-ui.html`（启动后端后访问）

## 架构说明

- **后端**：Spring Boot + MyBatis 三层架构，Controller 返回 JSON
- **前端**：React SPA，Vite 开发服务器代理 `/api` 到后端 8080 端口
- **认证**：JWT Token，前端存储在 localStorage，请求时通过 Authorization Header 传递
- **权限**：RBAC 角色控制，admin 可执行增删改，普通用户仅可查看
- **分页**：后端手动分页（offset + limit），前端 DataTable 组件渲染

## 注意事项

- 数据库密码硬编码在 `application.yml`，部署前请修改
- JWT secret 硬编码在 `application.yml`，生产环境请更换
- 前端无单元测试，后端无单元测试
