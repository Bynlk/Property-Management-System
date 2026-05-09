# 小区物业管理系统

基于 Spring Boot 3 + React 18 的前后端分离物业管理 Web 应用，采用液态玻璃（Liquid Glass）UI 设计风格。

## 功能模块

| 模块 | 说明 |
|------|------|
| 工作台 | 数据概览、快捷操作、系统信息 |
| 业主管理 | 业主信息的增删改查，支持按姓名/手机号搜索 |
| 员工管理 | 员工信息与岗位管理 |
| 房屋管理 | 楼栋、单元、房间信息及入住状态 |
| 费用管理 | 物业费/水费/电费/燃气费的收缴记录 |
| 停车位管理 | 车位编号、车牌绑定、使用状态 |
| 投诉管理 | 投诉工单的提交与处理流转 |
| 报修管理 | 设备报修工单的创建与维修跟踪 |
| 值班管理 | 员工排班（早班/中班/晚班） |

## 技术栈

**后端**

- Java 17 + Spring Boot 3.2.5
- MyBatis 3.0.3（Spring Boot Starter）
- MySQL 8.0 + Alibaba Druid 连接池
- Spring Security + JWT（jjwt 0.12.5）认证

**前端**

- React 18 + TypeScript
- Vite 6 构建
- Tailwind CSS 4 样式
- React Router 6 路由
- Axios HTTP 请求
- Lucide React 图标

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.x
- MySQL 8.x
- Node.js 18+

### 1. 初始化数据库

```bash
mysql -u root -p < src/main/resources/property_management.sql
```

### 2. 配置数据库连接

编辑 `src/main/resources/application.yml`，修改数据库密码：

```yaml
spring:
  datasource:
    password: your_password
```

### 3. 启动后端

```bash
mvn spring-boot:run
```

后端运行在 `http://localhost:8080`。

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端运行在 `http://localhost:3000`，开发模式下自动代理 `/api` 请求到后端。

### 5. 登录

- 地址：`http://localhost:3000`
- 账号：`admin`
- 密码：`admin123`

## 项目结构

```
├── src/main/
│   ├── java/com/property/
│   │   ├── PropertyManagementApplication.java   # 启动类
│   │   ├── controller/     # 9 个 Controller（8 业务 + Auth）
│   │   ├── entity/         # 9 个实体类
│   │   ├── mapper/         # 9 个 MyBatis Mapper 接口
│   │   ├── service/        # 9 个 Service 接口 + 实现
│   │   └── security/       # JWT 工具、过滤器、SecurityConfig
│   └── resources/
│       ├── application.yml
│       ├── property_management.sql
│       └── mapper/         # 9 个 MyBatis XML 映射文件
└── frontend/
    └── src/
        ├── api.ts           # Axios API 封装
        ├── types.ts         # TypeScript 类型定义
        ├── index.css        # 全局样式（液态玻璃、动画、主题）
        ├── components/      # 通用组件（Layout、DataTable、FormModal、ConfirmDialog）
        └── pages/           # 页面（Login、Dashboard、8 个业务页）
```

## API 接口

所有接口前缀 `/api`，需在请求头携带 `Authorization: Bearer <token>`。

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/auth/login` | POST | 登录，返回 JWT Token |
| `/api/auth/info` | GET | 获取当前用户信息 |
| `/api/{module}/page` | GET | 分页列表（pageNum, pageSize） |
| `/api/{module}/get/{id}` | GET | 按 ID 查询 |
| `/api/{module}/add` | POST | 新增 |
| `/api/{module}/update` | PUT | 更新 |
| `/api/{module}/delete/{id}` | DELETE | 删除 |

模块名：`owner`、`employee`、`house`、`fee`、`parking`、`complaint`、`repair`、`duty`

## UI 设计

前端采用暗色主题 + 液态玻璃（Liquid Glass）设计语言：

- 深色背景配合环境光（Ambient Orb）动画
- 图标容器使用毛玻璃效果，带光线折射高光和色彩渲染
- 鼠标跟随辉光交互（Dashboard 统计卡片）
- 页面切换渐入动画
- 噪点纹理叠加增强质感

## 注意事项

- 数据库密码和 JWT Secret 硬编码在 `application.yml`，部署前请修改
- 前后端均无单元测试，生产部署前建议补充
- 前端构建产物需通过 Nginx 等反向代理部署
