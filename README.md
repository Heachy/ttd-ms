# 通天代 TTD-MS

> 软工大作业

**常规功能点：**

- 用户注册登录绑定学生用户；

- 用户发布需要代拿订单

- 帮手承接订单

- 平台维护订单状态
- 帮手与客户的聊天室(随着订单产生和失效)

**比较好玩的点：**

- 帮手承接订单的时候，可通过webSocket推送相同起始点和重点的订单(新发布的时候)

​		5分钟内吧(估摸着帮手还在起始点附近)

- 推荐路线(还待完善)



# 项目模块

## common

> 存放整个项目里头的通用类，像是什么exception，网络接口返回数据的类型规范，工具类等

### api

`CommonResult` 作为网络接口返回的统一类，可在里面携带前端所需要的数据

### exception

`ApiException`，`SaTokenException`

### utils

工具类包，不过姑且只有阿里云OSS的文件上下传的

## generated

> 存放着由mybatis-plus生成的实体类的包
>
> 感觉写错了，具体业务应该再开个service，不该写道这个模块下，这个应该就只是存放生成后的数据

由domain，mapper，service，vo包组成

## rabbitMQ

> 通过消息队列来实现一个定时的效果

通过与redis相结合
