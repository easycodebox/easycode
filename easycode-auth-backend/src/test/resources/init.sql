

/* 密码 @123456@ */
INSERT INTO `auth`.`u_user` (`id`, `groupId`, `userNo`, `username`, `nickname`, `password`, `realname`, `status`, `userType`, `pic`, `sort`, `gender`, `email`, `mobile`, `loginFail`, `creator`, `createTime`, `modifier`, `modifyTime`) VALUES ('1', NULL, '1', 'superadmin', '超级管理员', '9572aca7c1b67bdc0c25bbb30fbee61d', '超级管理员', '0', '1', NULL, '1000', '0', NULL, NULL, '0', '0', '2015-02-02 12:35:12', '0', '2015-02-02 12:35:23');


INSERT INTO `auth`.`sys_project` (`id`, `name`, `projectNo`, `status`, `sort`, `remark`, `creator`, `createTime`, `modifier`, `modifyTime`) VALUES ('1', '权限项目（后台）', 'EASYCODE-AUTH-BACKEND', '0', '10000', NULL, '0', '2015-02-02 13:47:26', '0', '2015-02-02 13:47:33');
