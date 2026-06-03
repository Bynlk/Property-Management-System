# IDEA 启动指南

## 前置条件

- JDK 17+
- Maven 3.x（IDEA 自带即可）
- MySQL 8.x
- Node.js 18+

## 步骤一：初始化数据库

1. 打开 MySQL 命令行或 Navicat 等工具
2. 执行项目根目录下的 `src/main/resources/property_management.sql`

```sql
source /path/to/property_management.sql
```

## 步骤二：修改数据库密码

编辑 `src/main/resources/application.yml`，将 `spring.datasource.password` 改为你的 MySQL 密码：

```yaml
spring:
  datasource:
    password: 你的密码
```

## 步骤三：用 IDEA 打开项目

1. 打开 IDEA → `File` → `Open`
2. 选择项目根目录（包含 `pom.xml` 的文件夹）
3. 等待 Maven 依赖下载完成（右下角进度条）

## 步骤四：启动后端

1. 找到 `src/main/java/com/property/PropertyManagementApplication.java`
2. 右键 → `Run 'PropertyManagementApplication'`
3. 看到 `Started PropertyManagementApplication` 表示启动成功
4. 后端运行在 `http://localhost:8080`

## 步骤五：启动前端

1. 打开 IDEA 终端（或外部终端），执行：

```bash
cd frontend
npm install
npm run dev
```

2. 看到 `Local: http://localhost:3000` 表示启动成功

## 步骤六：访问系统

- 浏览器打开 `http://localhost:3000`
- 默认账号：`admin`
- 默认密码：`admin123`

## 常见问题

**Q: Maven 依赖下载慢？**
A: 在 IDEA 中 `File` → `Settings` → `Build` → `Build Tools` → `Maven` → `Repositories`，添加阿里云镜像：
```
https://maven.aliyun.com/repository/public
```

**Q: 端口 8080 被占用？**
A: 修改 `application.yml` 中的 `server.port`

**Q: 前端 npm install 报错？**
A: 确保 Node.js 版本 >= 18，建议使用 nvm 管理 Node 版本
