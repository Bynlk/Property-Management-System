<div align="center">

# 小区物业管理系统

**Property Management System**

一个基于 **Spring Boot 3** 和 **React 19** 构建的全栈物业管理平台，采用 Liquid Glass 设计语言。

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-61DAFB?style=flat-square&logo=react&logoColor=black)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-6.0-3178C6?style=flat-square&logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![Vite](https://img.shields.io/badge/Vite-8-646CFF?style=flat-square&logo=vite&logoColor=white)](https://vite.dev/)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind%20CSS-4-06B6D4?style=flat-square&logo=tailwindcss&logoColor=white)](https://tailwindcss.com/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)](#license)

<br />

🌐 **[English](README.md)** · **中文**

[功能特性](#功能特性) · [技术栈](#技术栈) · [快速开始](#快速开始) · [项目架构](#项目架构) · [接口文档](#接口文档) · [界面设计](#界面设计)

</div>

---

## 功能特性

| 模块 | 说明 |
|:-----|:-----|
| **仪表盘** | 实时数据概览、快捷操作和系统信息 |
| **业主管理** | 业主信息的完整增删改查，支持按姓名或手机号搜索 |
| **员工管理** | 员工档案、职位和联系方式管理 |
| **房屋管理** | 楼栋/单元/房间跟踪，含入住状态（已入住/空置/装修中） |
| **费用管理** | 物业费、水费、电费、燃气费的账单记录，支持缴费状态跟踪 |
| **车位管理** | 车位分配、车牌绑定和可用状态管理 |
| **投诉处理** | 投诉提交与三态工作流（待处理 → 处理中 → 已解决） |
| **报修跟踪** | 设备故障上报与维修状态生命周期（待处理 → 处理中 → 已完成） |
| **值班排班** | 员工早班/中班/晚班的排班计划 |

## 技术栈

<table>
<tr>
<td valign="top" width="50%">

**后端**

| 层级 | 技术 |
|:-----|:-----|
| 编程语言 | Java 17 |
| 框架 | Spring Boot 3.2.5 |
| ORM | MyBatis 3.0.3 |
| 数据库 | MySQL 8.0 |
| 连接池 | Alibaba Druid 1.2.21 |
| 认证 | Spring Security + JWT (jjwt 0.12.5) |
| 构建工具 | Maven 3.x |

</td>
<td valign="top" width="50%">

**前端**

| 层级 | 技术 |
|:-----|:-----|
| 编程语言 | TypeScript 6.0 |
| 框架 | React 19 |
| 构建工具 | Vite 8 |
| 样式 | Tailwind CSS 4 |
| 路由 | React Router 7 |
| HTTP 客户端 | Axios |
| 图标 | Lucide React |

</td>
</tr>
</table>

## 快速开始

### 环境要求

- **JDK 17+**
- **Maven 3.x**
- **MySQL 8.x** 运行在 `localhost:3306`
- **Node.js 18+**

### 1. 初始化数据库

```bash
mysql -u root -p < src/main/resources/property_management.sql
```

### 2. 配置数据库连接

编辑 [`src/main/resources/application.yml`](src/main/resources/application.yml)，设置你的 MySQL 密码：

```yaml
spring:
  datasource:
    password: your_password_here
```

### 3. 启动后端

```bash
mvn spring-boot:run
```

后端启动地址：**http://localhost:8080**

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端启动地址：**http://localhost:3000** — 开发模式下 `/api` 请求会代理到后端。

### 5. 登录

| 字段 | 值 |
|:-----|:---|
| 地址 | http://localhost:3000 |
| 用户名 | `admin` |
| 密码 | `admin123` |

## 项目架构

```
┌─────────────────────────────────────────────────────────────┐
│                      前端 (React)                            │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌────────────┐  │
│  │  Layout   │  │  Pages   │  │ DataTable│  │ FormModal  │  │
│  │ (侧边栏)  │  │ (×10)    │  │          │  │            │  │
│  └──────────┘  └──────────┘  └──────────┘  └────────────┘  │
│        │              │                                      │
│        └──────┬───────┘                                      │
│          api.ts (Axios + CRUD 工厂)                          │
└───────────────┼──────────────────────────────────────────────┘
                │  Authorization: Bearer <JWT>
                ▼
┌─────────────────────────────────────────────────────────────┐
│                   后端 (Spring Boot)                          │
│  ┌────────────┐   ┌────────────┐   ┌──────────────────────┐ │
│  │ Controller  │──▶│  Service   │──▶│  Mapper (MyBatis XML)│ │
│  │  (REST API) │   │ (业务逻辑)  │   │       (SQL)          │ │
│  └────────────┘   └────────────┘   └──────────────────────┘ │
│  ┌────────────────────────────────────────────────────────┐  │
│  │  安全: JWT Filter → Spring Security → Controller        │  │
│  └────────────────────────────────────────────────────────┘  │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
                    ┌──────────────┐
                    │   MySQL 8.0  │
                    │  (9 张表)     │
                    └──────────────┘
```

### 项目目录结构

```
├── src/main/
│   ├── java/com/property/
│   │   ├── PropertyManagementApplication.java    # 入口类
│   │   ├── controller/       # REST 控制器 (8 个模块 + 认证)
│   │   ├── entity/           # 实体类
│   │   ├── mapper/           # MyBatis Mapper 接口
│   │   ├── service/          # 业务逻辑 (接口 + 实现)
│   │   └── security/         # JWT 工具、认证过滤器、安全配置
│   └── resources/
│       ├── application.yml                       # 应用配置
│       ├── property_management.sql               # 建表 + 种子数据
│       └── mapper/                               # MyBatis XML 映射
└── frontend/
    └── src/
        ├── api.ts             # Axios 实例 + 通用 CRUD 工厂
        ├── types.ts           # TypeScript 类型定义
        ├── index.css          # Liquid Glass 主题与动画
        ├── components/        # 可复用组件: Layout, DataTable, FormModal, ConfirmDialog
        └── pages/             # 路由页面: 登录、仪表盘、8 个业务模块
```

### 数据库表结构

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   sys_user   │     │    owner     │◀───▶│    house     │
│──────────────│     │──────────────│     │──────────────│
│ username     │     │ name         │     │ building     │
│ password     │     │ phone        │     │ unit         │
│ role         │     │ id_card      │     │ room_number  │
└──────────────┘     └──────┬───────┘     │ status       │
                            │             └──────────────┘
            ┌───────────────┼───────────────────┐
            ▼               ▼                   ▼
     ┌────────────┐  ┌────────────┐      ┌────────────┐
     │    fee     │  │  parking   │      │ complaint  │
     │────────────│  │────────────│      │────────────│
     │ fee_type   │  │ spot_number│      │ title      │
     │ amount     │  │ license    │      │ status     │
     │ status     │  │ status     │      └────────────┘
     └────────────┘  └────────────┘              │
                                            ┌────────────┐
┌──────────────┐     ┌──────────────┐       │   repair   │
│   employee   │◀───▶│     duty     │       │────────────│
│──────────────│     │──────────────│       │ device     │
│ name         │     │ duty_date    │       │ fault      │
│ position     │     │ shift        │       │ status     │
└──────────────┘     └──────────────┘       └────────────┘
```

## 接口文档

所有接口前缀为 `/api`。需要认证的路由需携带请求头：

```
Authorization: Bearer <token>
```

### 认证接口

| 方法 | 接口 | 说明 |
|:-----|:-----|:-----|
| `POST` | `/api/auth/login` | 登录 — 返回 JWT 令牌和用户信息 |
| `GET` | `/api/auth/info` | 获取当前登录用户信息 |

### 业务模块接口

> 模块：`owner` · `employee` · `house` · `fee` · `parking` · `complaint` · `repair` · `duty`

| 方法 | 接口 | 说明 |
|:-----|:-----|:-----|
| `GET` | `/api/{module}/page?pageNum=&pageSize=` | 分页查询列表 |
| `GET` | `/api/{module}/get/{id}` | 根据 ID 查询 |
| `POST` | `/api/{module}/add` | 新增记录 |
| `POST` | `/api/{module}/update` | 更新记录 |
| `POST` | `/api/{module}/delete/{id}` | 删除记录 |

### 响应格式

```jsonc
// 分页响应
{
  "list": [...],
  "total": 42,
  "pageNum": 1,
  "pageSize": 10,
  "totalPages": 5
}

// 操作响应
{ "code": 0, "msg": "操作成功" }   // 成功
{ "code": 1, "msg": "操作失败" }   // 失败
```

## 界面设计

前端采用 **暗色主题** 和 **Liquid Glass** 设计语言：

- **环境光球** — 动态背景光效，颜色微妙变化
- **毛玻璃效果** — 带有光线折射高光的磨砂玻璃图标容器
- **鼠标追踪发光** — 仪表盘统计卡片的交互式悬停效果
- **交错动画** — 平滑的页面进入过渡和级联显示效果
- **噪点纹理叠加** — 细微的颗粒层，增加深度和质感

## 环境变量

部署到生产环境前，请更新 [`application.yml`](src/main/resources/application.yml) 中的以下配置：

| 配置项 | 位置 | 说明 |
|:-------|:-----|:-----|
| `spring.datasource.password` | `application.yml` | MySQL 密码 |
| `jwt.secret` | `application.yml` | Base64 编码的 JWT 签名密钥 |
| `jwt.expiration` | `application.yml` | 令牌有效期（默认：24 小时） |

## 部署

### 后端

```bash
mvn clean package
java -jar target/property-management-2.0.0.jar
```

### 前端

```bash
cd frontend
npm run build   # 输出到 dist/
```

使用 Nginx 或任何静态文件服务器托管 `dist/` 目录。将 `/api` 反向代理到后端 `:8080`。

```nginx
server {
    listen 80;

    location / {
        root /var/www/property-frontend/dist;
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080;
    }
}
```

## 路线图

- [ ] 单元测试（后端 JUnit + 前端 Vitest）
- [ ] 全局异常处理器
- [ ] 基于角色的访问控制（管理员 vs 普通用户）
- [ ] 文件上传（头像、投诉照片）
- [ ] 仪表盘图表和统计
- [ ] Docker Compose 部署
- [x] 国际化（中文 / 英文）

## 开源许可

本项目基于 **MIT 许可证** 开源。

---

<div align="center">

**基于 Spring Boot & React 构建**

</div>
