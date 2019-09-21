<h1 align="center">
 <a href="http://noark.xyz/" title="Noark">Noark</a>
</h1>

[![Build Status](https://travis-ci.org/xiaoe/noark3.svg?branch=master)](https://gitee.com/xiaoe/noark3)
[![JDK](https://img.shields.io/badge/JDK-1.8%2B-green.svg)](https://www.oracle.com/technetwork/java/javase/downloads/index.html)
[![maven](https://maven-badges.herokuapp.com/maven-central/xyz.noark/noark-game/badge.svg)](https://search.maven.org/search?q=g:xyz.noark)
[![license](https://img.shields.io/badge/license-MulanPSL-blue)](http://license.coscl.org.cn/MulanPSL)
[![GitHub last commit](https://img.shields.io/github/last-commit/xiaoe/noark3.svg?style=flat-square)](https://github.com/xiaoe/noark3/commits)
[![Coverage Status](https://coveralls.io/repos/github/xiaoe/noark3/badge.svg?branch=master)](https://coveralls.io/github/xiaoe/noark3?branch=master)

官方网站 [www.noark.xyz](http://www.noark.xyz)

### 简介
Noark是一个游戏服务器端框架，可快速开发出一个易维护、易扩展且稳定高能的游戏服务器，让开发者专注于业务功能的开发<br>
实现了配置注入，协议映射，模板加载，数据存储，异步事件，延迟任务，内部指令等功能模块<br>
从而达到了松散耦合的效果，提高了系统的可重用性、可维护性以及可扩展性<br>
精心设计过的它大大简化了网络编程和多线程编程，众多的工具类库就是为了解决开发中那些重复劳动而产生的框架<br>

**优点：**

* 使用简单，学习成本低
* 功能强大，很容易写出性能优秀的服务
* 十分灵活，并且可与常用技术无缝衔接


### 安装

Gradle
```
implementation "xyz.noark:noark-game:3.1.18.Final"
```
当前需要Jdk1.8，Noark版本最新已是3.1.18了

引入Noark，按照历史惯例，先来一个Hello Kitty...


### Hello Kitty
第一个游戏服务器Demo，来开始我们的ABC三步走

#### A、Application应用启动入口

在【com.company.slg】包下创建一个入口类
```
package com.company.slg;
import xyz.noark.game.Noark;
public class GameServerApplication {
	public static void main(String[] args) {
		Noark.run(GameServerBootstrap.class, args);
	}
}
```

#### B、Bootstrap启动引导入口
在【com.company.slg】包下创建一个引导启动类，继承BaseServerBootstrap
```
package com.company.slg;
import xyz.noark.game.bootstrap.BaseServerBootstrap;
public class GameServerBootstrap extends BaseServerBootstrap {
	@Override protected String getServerName() {
		return "game-server";
	}
}
```

#### C、Configuration配置中心
这个不是必选项，用于配置第三方服务类
```
package com.company.slg;
import xyz.noark.core.annotation.Configuration;
@Configuration
public class GameServerConfiguration {}
```
#### 启动游戏服务器
直接运行main方法，一个简单的游戏服务器就跑起来了
```
2018-08-16 18:23:38.178 [main] INFO AbstractServerBootstrap.java:62 - starting game-server service...
2018-08-16 18:23:38.181 [main] DEBUG NoarkIoc.java:47 - init ioc, packages=com.company.slg
2018-08-16 18:23:38.504 [main] INFO ReloadManager.java:41 - loading template data. checkValidity=true
2018-08-16 18:23:38.504 [main] INFO ReloadManager.java:47 - load template data success.
2018-08-16 18:23:38.504 [main] INFO ReloadManager.java:50 - check template data...
2018-08-16 18:23:38.505 [main] INFO ReloadManager.java:52 - check template success.
2018-08-16 18:23:38.505 [delay-event] INFO DelayEventThread.java:41 - 延迟任务调度线程开始啦...
2018-08-16 18:23:38.606 [main] INFO NettyServer.java:119 - game tcp server start on 9527
2018-08-16 18:23:38.607 [main] INFO NettyServer.java:128 - game tcp server start is success.
game-server is running, interval=427.21872 ms
2018-08-16 18:23:38.607 [main] INFO AbstractServerBootstrap.java:76 - game-server is running, interval=427.21872 ms
2018-08-16 18:23:38.609 [main] INFO AbstractServerBootstrap.java:166 - :: Noark :: 3.1.18.Final
  _   _     U  ___ u    _       ____      _  __   _____  
 | \ |"|     \/"_ \/U  /"\  uU |  _"\ u  |"|/ /  |___"/u 
<|  \| |>    | | | | \/ _ \/  \| |_) |/  | ' /   U_|_ \/ 
U| |\  |u.-,_| |_| | / ___ \   |  _ <  U/| . \\u  ___) | 
 |_| \_|  \_)-\___/ /_/   \_\  |_| \_\   |_|\_\  |____/  
 ||   \\,-.    \\    \\    >>  //   \\_,-,>> \\,-._// \\ 
 (_")  (_/    (__)  (__)  (__)(__)  (__)\.)   (_/(__)(__)
```

相关文档[传送门](http://blog.noark.xyz/article/2018/4/1/noark%E5%85%A5%E9%97%A8%E4%B9%8B%E6%9E%81%E9%80%9F%E4%BD%93%E9%AA%8C/)

### 目标
我们的目标：稳定、高性能、可扩展、易维护、提高开发效率，我们没有要取代谁，也没有要超越谁，我们只做我们自己。

### QQ群
85750544(Noark官方交流群)