# easycode
Java企业级项目一站式解决方案 

### 注意事项

1. 项目中禁止使用Web容器的Session，否则会出现302重定向循环。因为项目中使用了Shiro的Native Session，如果你用到Web容器的Session时（即调用了request.getSession()或request.getSession(true)），Web容器会根据JSESSIONID的Cookie值去容器里找，没有找到则创建一个新的Web Session然后把此Session Id存入到JSESSIONID的Cookie中。这样就会更新Shiro之前保存的JSESSIONID Cookie值，所以会出现302重定向循环。

	如果你就是想用Web容器的Session，你可以修改Shiro的Session Id保存到Cookie中的key值：

	```xml
	<bean id="sessionIdCookie" class="org.apache.shiro.web.servlet.SimpleCookie">
		<property name="name" value="sid" />
	</bean>
	<bean id="sessionManager" class="org.apache.shiro.web.session.mgt.DefaultWebSessionManager">
		<property name="sessionIdCookie" ref="sessionIdCookie" />
		...
	</bean>
	```
