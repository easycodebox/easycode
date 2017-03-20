# easycode
致力于打造Java企业级项目一站式解决方案 

### 主要功能

1. 单点登录 - 所有系统共用同一个登录系统，可根据不同系统切换主题，甚至可以根据不同的系统显示不同的登录页面

2. 统一权限验证/分配 - 所有的系统都可以接入进来，共用同一个权限系统，可视化分配权限

3. 使用Shiro Session，Session数据并保存至Redis。所有服务共享Session，可以分布式部署服务

4. easycode-jdbc面向对象编写SQL语句 - 编写SQL时代码补全；高度抽象出增删查改通用接口；编写Service层/Dao层非常简单、非常快捷，效率提升了好几倍

	简单例子：
	
	```java
	//返回单条数据 - 等价于： SELECT * FROM user WHERE id = '1'
	super.get(sql()
		.eq(R.User.id, "1")
	);
	
	//返回单条数据 - 等价于： SELECT realname FROM user WHERE id = '1'
	super.get(sql()
		.column(R.User.realname)
		.eq(R.User.id, "1")
	);
	
	//返回多条数据 - 等价于： SELECT * FROM user WHERE deleted = 0 ORDER BY createTime DESC
	//枚举YesNo.NO转成SQL时为 0
	super.list(sql()
		.eq(R.User.deleted, YesNo.NO)
		.desc(R.User.createTime)
	);
	
	//分页查询 - 返回多条数据 - 等价于： SELECT * FROM user WHERE deleted = 0 ORDER BY createTime DESC LIMIT ?, ?
	//枚举YesNo.NO转成SQL时为 0
	super.page(sql()
		.eq(R.User.deleted, YesNo.NO)
		.desc(R.User.createTime)
		.limit(pageNo, pageSize)
	);
	
	//等价于： INSERT INTO user (...) values (...)
	super.save(user);
	
	//全表更新 - 等价于： UPDATE user SET ... WHERE id = ?
	super.update(user);
	
	//等价于：  UPDATE user SET realname = '张三' WHERE id = '1'
	super.update(sql()
		.upd(R.User.realname, "张三")
		.eq(R.User.id, "1")
	);
	
	//物理删除
	super.deletePhy("1");
	```
	
	> 执行insert、update时，自动帮你把创建人、创建时间、修改人、修改时间属性设置好  
	> 上述例子中`R`是通过`easycode-auth-model`（各自项目model包）中`SysTest`测试类的`testGenerateRes`方法生成的。只需要运行此测试方法自动生成R文件  
	> 如果想在Dao层使用此功能，只需要继承AbstractDaoImpl类；如果在Service层使用，则Service实现类继承AbstractServiceImpl  

5. 主键生成策略 - 主键生成控制权完全由开发者自己控制，不依赖于数据库的主键生成策略。并且可以自由控制生成规则，提供了Integer、Long、纯字母、字母和数字组合等各种生成策略。应用场景：1.数据库的分表分库  2.数据库的更换不需要修改任何代码  3.需要自己控制主键生成规则

6. 枚举类型的使用 - 项目中任何地方（Dao、Service、Controller、Html等）都是使用的枚举类型，入库时自动把枚举值转换成对应的数字或字符窜（这可以自己定义），枚举类型的好处想必大家都知道，开发快捷、看一眼就知道这个字段可能会出现哪些值、Html页面显示对应的中文含义时直接取枚举值对应的属性就行，以前的做法是需要开发人员自己来写判断逻辑：如果值为1显示啥；值为2显示啥。

7. 监控JS、CSS资源文件，当文件被修改/新增后立即生成对应的压缩文件，压缩文件名自动加上`.min`后缀。如果想研究的话请阅读[easycode-static-watcher](https://github.com/easycodebox/easycode-static-watcher)[【下载链接】](https://github.com/easycodebox/easycode-static-watcher)项目，里面提供了安装使用文档

	多数公司的前端开发是这样搞的，JS、CSS等静态资源文件由前端来管理，等前端开发好相关功能后，该压缩的压缩该合并的合并，然后上传至服务器或者交由后端。当在测试环境或生产环境出了问题了，前端把源码从代码服务器上download下来，在本地测试，解决问题后再压缩合并上传至服务器。太繁琐了，难道压缩合并这种事不能直接交给服务器处理吗？难道就不能直接在服务器端修改一个配置参数后，刷新一下页面直接请求JS源文件，然后前端直接用测试环境或预发环境直接调试（有些现象是和数据有关的，你在本地测试就测不出相关问题）？  
	
	使用了`easycode-static-watcher`后，你就不需要在管什么压缩/合并 JS/CSS了，这些事情都交由它处理，你可以自己决定是加载min文件还是源文件，你可以自己决定哪些JS/CSS直接合并（当然合并是需要另一种技术支持的，下面会提到）
	
8. 合并JS/CSS文件 - 请使用nginx concat模块

9. 图片服务器动态生成不同长宽比的小图
	
	你可以先体验下：  
	
	* 原图：`http://img.easycodebox.com/contract/partner/6a59f.jpg`
	
	* 宽(40)*高(40)：`http://img.easycodebox.com/contract/partner/6a59f_r40c40.jpg`
	
	你只需要上传原图就行，其他的什么裁剪、压缩等都由图片服务器来给你处理，规则都在URL地址中。你可能会想，既然小图是动态生成的，那会不会影响性能啊？答案是：不会的。
	
	只有在第一次请求某规则的小图时才会生成下，下次再请求同样规则的小图时就会返回之前已生成的小图，根本不会再次生成。你可能还会问，如果有人恶意搞破坏，写了个脚本不停的请求不同的规则图片，这样服务器的空间会被恶意占满。针对这种情况你可以自己实现某些特殊的功能：比如一个原图，最多只能生产50张小图；或者相同IP 5分钟内只能针对某原图生成Y个小图，如连续生成Z个小图后视此IP为恶意地址，封此IP 1天等。

	> 当时我是用 `Nginx + Lua + GraphicsMagick`来实现的，现在已经有开源的Nginx模块提供了类似的功能，不过我觉得如果你想更好的控制的话还是自己写Lua脚本更灵活点
	
	### **由于时间有限，只能说些大概，后期会提供使用文档。建议看下代码，相信你能得到点启发的，如有`问题/建议`请联系本人**


> 注：后期版本会集成更多的技术

### 使用技术

1. MySql
2. Mybatis + EasyCodeJdbc
3. SpringDataRedis + Jedis + Redis
3. CAS + Shiro
4. Spring + Logback
5. SpringMVC + Freemarker
6. Sitemesh3 + Pjax
7. Vuejs + Bootstrap3

> 注意：项目中使用的公共JS、CSS在[easycode-static](https://github.com/easycodebox/easycode-static)[【下载链接】](https://github.com/easycodebox/easycode-static)项目下，使用最频繁的是`easycode/utils/utils-*.js`，里面提供了日常开发中80%需要的JS工具函数。

### Demo

1. 权限系统 - http://auth.easycodebox.com
2. 集成单点登录、权限系统的Example - http://example.easycodebox.com

> 账号：`superadmin` 密码：`111111` - 如果测试用户的权限配置，不要配置超级管理员，因为没有特殊条件的前提下超级管理员拥有所有权限    
> 注意：每天会初始化数据库数据，不会保留之前的操作数据。因本人经济能力有限，服务器硬件和带宽都是最低配。如果你访问时发现服务端响应较慢的话，请不用怀疑你自己的网速  :joy:

### 安装

1. 下载[easycode](https://github.com/easycodebox/easycode)和[easycode-example](https://github.com/easycodebox/easycode-example)[【下载链接】](https://github.com/easycodebox/easycode-example)代码

2. 在本地数据库中新增加`easycode-auth`和`easycode-example`两个库。项目中数据库相关配置在`jdbc.properties`和`cas.properties`文件中。默认配置，账号：root 密码：root 
	
	* 在`easycode-auth` DB中执行 `easycode-auth-core/src/test/resources/easycode-auth-data.sql`、`easycode-auth-core/src/test/resources/easycode-auth-schema.sql`
	* 在`easycode-example` DB中执行 `easycode-example-core/src/test/resources/easycode-example-data.sql`、`easycode-example-core/src/test/resources/easycode-example-schema.sql`

	> 相关配置可自行修改，但密码需要配置为加密的值，用`ConfigTools.encrypt("password")`加密。修改时一定要改全，最好是全局搜索properties文件中包含**jdbc.username**关键字

3. 安装Redis，Redis安装请参考我之前写的文档[Redis安装文档](https://github.com/WangXiaoJin/docs/blob/master/Redis/Redis%E5%AE%89%E8%A3%85%E5%8F%8A%E4%BD%BF%E7%94%A8.md)。
	
	示例中我只安装了一个Redis实例，如果你只是用来测试使用，可以只安装一个Redis实例。当你需要部署到生产环境时，你必须部署两套Redis实例。一套用来存储Shiro Session
	数据，这套实例必须开启RDB、AOF达到持久化功能，防止Redis服务挂了后用户的Session信息也丢失了。另外一套Redis实例只需要作为纯缓存使用，禁用RDB、AOF功能，
	因为持久化功能会消耗性能的。
	
	在Redis配置文件中，我已经列出Redis的Sentinel Mode和Cluster Mode相关配置，使用者只需要把相关注释去掉就行，
	目前Jedis版本不支持读写分离，读写都是走Master，有兴趣的朋友可以换成redisson，redisson实现了读写分离。
	
	安装好Redis后修改相关配置文件[**redis.properties**]

4. `easycode-auth-backend`项目端口号改为`7080`，`easycode-cas`项目端口号改为`7081`，`easycode-example-app`项目端口号改为`8080`。这三个项目的Context Path 全部修改为`/`。（如果你是用jetty:run启动的话，端口号和根路径默认已经配置好了。jetty启动easycode-cas项目会有点慢，因为这个项目中依赖了很多jar包，jetty会扫描这些jar包加载`web-fragment.xml`，你可以自定义jetty加载规则来提高启动速度。）最后启动这三个项目，启动顺序随意。启动成功后你就可以访问了：

	* `http://localhost:7080/` - 权限系统     
	* `http://localhost:8080/` - 集成easycode的demo

	>注意： 项目的根路径一定要修改为'/'，不要带项目名。修改这个是为了方便开发，特别是与前端交叉开发时，因为前端使用的项目的根路径一般都是'/'，你如果想集成前端开发的页面就需要修改url地址了，这很蛋疼。我以前试过很多方案，什么用Filter在request请求中增加basePath参数，用JSP获取basePath，设置base标签，js中定义basePath变量等等，用过后都非常不爽，还不如直接修改web容器的Context path来的干脆直接。    
	> 端口号是可以自己修改的，修改后一定要记得把项目中`login.properties`配置修改下，因为这些URL CAS需要使用

### 直接引用easycode jar包

`easycode`的`easycode-common`、`easycode-jdbc`、`easycode-jdbc-mybatis`、`easycode-login`、`easycode-idgenerator`已上传至中央仓库，可直接用Maven依赖

```xml
<dependency>
	<groupId>com.easycodebox</groupId>
	<artifactId>easycode-jdbc-mybatis</artifactId>
	<version>0.3.2</version>
</dependency>
```

### 欢迎进群闲聊

QQ群：**368028459**

