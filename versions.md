## Version Logs

###【v0.1.0】 - 2016.09.07

1. 增加Vue相关功能，为后续重构做准备
2. 改成单页面应用
3. 弹出框改用layer
4. 前端页面代码整体重构，后期版本会继续重构
5. easycode项目代码基本成型

###【v0.2.3】 - 2016.10.20

1. 分离出jdbc jar包easycode-jdbc和easycode-jdbc-mybatis
2. 重构页面框架
3. 增强ErrorContextFilter功能，提供各种配置
4. 重构easycode-login

###【v0.2.4】 - 2016.11.13

1. 实现权限的导入/导出新版功能
2. SqlGrammar的增删改查	增加通用Dao
3. easycode-common包中Enums类名改为DetailEnums
4. 通信用的UserWsBo/OperationWsBo改为User/Permission，并分离出easycode-auth-model包，为了以后各项目间统一使用用户信息缓存做准备
5. 日志属性名由LOG换成log，与lombok统一，方便集成
6. Shiro + CAS 提供多reamls验证
7. CAS ticketValidator由Cas20ServiceTicketValidator改为Cas30ServiceTicketValidator
8. 重构权限表
9. 集成Sitemesh3 + Pjax

###【v0.2.5】 - 2016.11.20

1. DefaultConfigurableSiteMeshFilter增加是否启用装饰的规则
2. 升级bvalidator插件
