# 秒杀系统

一个可在本机运行的优惠券秒杀系统。用户可以浏览商户、登录并抢购限时券。系统通过 Redis 校验库存和一人一单，将下单请求放入队列，由后台写入 MySQL。

## 功能

- 验证码登录和自动注册，支持退出登录。
- 商户分类、名称搜索、评分和距离排序，支持查看商户详情和优惠券。
- 限时抢购支持活动时间校验、库存扣减和重复下单拦截。页面会显示订单号，生成的订单用于本地演示。
- 支持发布图文笔记、点赞与关注。签到和关注动态可通过后端接口体验。

## 技术栈

| 层级 | 技术 |
| --- | --- |
| 前端 | Vue 2、Element UI、Axios |
| 后端 | Java 8、Spring Boot、MyBatis-Plus |
| 数据 | MySQL、Redis、Redisson |
| 部署 | Docker Compose、Nginx |

## 本地启动

打开 Docker Desktop，在 PowerShell 中克隆仓库并启动。

```powershell
git clone https://github.com/204404837a/seckill-system.git
cd seckill-system
docker compose up -d --build
& .\deploy\seed-demo.ps1
```

首次启动会导入商户与演示数据。脚本会等待接口就绪，为 103 茶餐厅添加一张有效期约七天的演示券。重复执行时会读取活动的当前库存。

## 体验秒杀

1. 打开 [103 茶餐厅的代金券区](http://127.0.0.1:8080/shop-detail.html?id=1#vouchers)，点击「限时抢购」。
2. 未登录时，输入测试手机号并点击「发送验证码」，随后执行下面的命令查看最新六位验证码。验证码有效期为两分钟。

   ```powershell
   docker compose logs --since 2m backend
   ```

3. 登录后会回到原店铺。再次点击「限时抢购」，页面会显示订单号。每个账号对同一张券限抢一次。

可用下列命令查看 Redis 中的可抢库存。把 `10` 换成初始化脚本实际显示的券编号，抢购前后各执行一次即可对比。

```powershell
docker compose exec -T redis redis-cli GET seckill:stock:10
```

## 下单流程

1. 后端检查登录状态、券状态和活动时间。
2. [Redis Lua 脚本](backend/src/main/resources/seckill.lua)在同一次执行中校验库存和购买记录、扣减库存，并写入 `stream.orders`。
3. [订单服务](backend/src/main/java/com/hmdp/service/impl/VoucherOrderServiceImpl.java)消费队列消息，使用 Redisson 用户锁处理订单，在同一数据库事务中扣减 MySQL 库存并保存订单。事务提交后确认消息。

Redis 还用于保存验证码与登录会话、缓存商户详情、按坐标检索附近商户，以及记录点赞和签到。

## 目录与运行管理

| 目录 | 内容 |
| --- | --- |
| `backend/` | Java 服务、Redis Lua 脚本和数据库初始化 SQL |
| `frontend/` | 商户、登录、秒杀和笔记页面 |
| `deploy/` | Nginx 配置和演示券初始化脚本 |

使用 `docker compose ps` 查看服务状态，使用 `docker compose stop` 停止服务并保留本机数据。如果 `8080` 已被占用，可在启动前设置 `$env:HMDP_PORT = '18080'`，并用 `& .\deploy\seed-demo.ps1 -Port 18080` 初始化演示券。浏览器地址也要改用对应端口。
