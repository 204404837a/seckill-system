# 秒杀系统

基于 Spring Boot、MyBatis-Plus、MySQL 和 Redis 的本地优惠券秒杀演示。前端使用 Vue 2 和 Element UI，由 Nginx 提供页面，使用 Docker Compose 启动。

登录后可在商户详情页抢购演示券。Redis Lua 校验库存与一人一单，Redis Stream 将下单请求交给后台写入 MySQL。

在项目目录打开 PowerShell 并运行以下命令。

```powershell
docker compose up -d --build
& .\deploy\seed-demo.ps1
```

打开 [103 茶餐厅的代金券区](http://127.0.0.1:8080/shop-detail.html?id=1#vouchers)。验证码可用 `docker compose logs --since 2m backend` 查看。演示券有效期七天，生成的订单用于本机体验。

本项目基于课程参考代码改造。原始来源见 [后端仓库](https://gitee.com/huyi612/hm-dianping) 和 [前端仓库](https://gitee.com/huyi612/hmdp-web)。
