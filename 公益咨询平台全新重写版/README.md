# 公益咨询平台（全新重写工程）

本工程采用前后端分离结构：`backend` 为 Spring Boot 服务，`frontend` 为 Vue 管理端/用户端界面，`database` 保存 MySQL 初始化脚本。

## 技术栈
- Java 17、Spring Boot 2.7.18、Spring MVC、MyBatis 2.3.2
- MySQL 8、BCrypt、JWT
- Vue 2.7、Vue Router 3、Element UI、ECharts 5

## 当前已实现内容
- 三种身份登录入口：普通用户、教授、管理员。
- 登录身份与数据库角色严格比对；不能通过前端选择身份切换权限。
- 普通用户和教授注册；教授注册自动建立待审核档案，未审核教授不能登录。
- JWT 签发、请求拦截、当前用户接口；密码 BCrypt 加密。
- 角色化工作台；统计卡片和预约图表通过 MyBatis SQL 查询数据库。
- 初版完整业务数据模型（用户、教授、排期、预约、咨询、资源、计划、社区、问答、消息、评价、公告、RBAC、日志等）。

## 尚未完成（重要）
这不是用户最初要求的全部最终系统。教授排期/预约全流程、资源收藏下载审核、学习计划打卡、社区发帖评论点赞、问答采纳、消息中心、评价、新闻公告、轮播图、RBAC 管理界面、操作日志和完整演示数据仍需继续开发。当前这些模块在前端明确标记为未接入，不会伪装成已实现功能。

## 数据库初始化
1. 在 MySQL 8 执行 `database/education_platform.sql`。
2. 后端默认连接 `localhost:3306/education_platform`，默认用户名 `root`，密码 `123456`。请通过环境变量 `DB_PASSWORD` 修改。
3. JWT 默认密钥仅供本地开发，部署前必须设置 `JWT_SECRET` 为足够长的随机字符串。

## 启动后端
在 IDEA 中以 Maven 项目打开 `backend/pom.xml`，使用 JDK 17，等待 Maven 依赖下载完成后运行 `com.publicconsult.platform.PlatformApplication`。

## 启动前端
```bash
cd frontend
npm install
npm run serve
```
访问 `http://localhost:8081`。前端开发服务器将 `/api` 代理到 `http://localhost:8080`。

## 账号说明
当前 SQL 不内置明文或弱口令管理员账号。初始化管理员时，应使用 BCrypt 生成密码哈希，再插入 `sys_user`，角色设为 `ADMIN`、状态设为 `正常`。普通用户可直接注册；教授账号须经管理员审核（待补齐审核管理接口）。

## 已接入的预约接口
- `GET /api/appointments`：普通用户仅查询本人预约；教授仅查询分配给本人的预约；管理员查询全平台预约。
- `POST /api/appointments`：普通用户提交预约；服务端校验目标教授账号状态及审核状态。
- `POST /api/appointments/{id}/cancel`：预约发起人仅可取消待确认预约。
- `POST /api/appointments/{id}/decision`：对应教授仅可接受或拒绝自己的待确认预约。

## 初始化管理员
先在 IDEA 中运行 `com.publicconsult.platform.tools.PasswordHashGenerator`，传入新密码参数，复制控制台输出的 BCrypt 密文，然后执行：
```sql
INSERT INTO sys_user(username,password,nickname,role,status)
VALUES ('admin','这里替换为BCrypt密文','平台管理员','ADMIN','正常');
```
请勿将明文密码写入 SQL 或提交到代码仓库。
