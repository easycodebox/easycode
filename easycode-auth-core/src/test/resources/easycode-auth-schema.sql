
-- ----------------------------
-- Table structure for sys_id_generator
-- ----------------------------
DROP TABLE IF EXISTS `sys_id_generator`;
CREATE TABLE `sys_id_generator` (
  `id` varchar(32) NOT NULL COMMENT '类型 - 主键生成器类型',
  `initialVal` varchar(32) NOT NULL COMMENT '初始值',
  `currentVal` varchar(32) NOT NULL COMMENT '当前值',
  `maxVal` varchar(32) DEFAULT NULL COMMENT '最大值',
  `fetchSize` int(9) NOT NULL COMMENT '批次容量 - 每批获取的数目大小',
  `increment` int(9) NOT NULL COMMENT '增长值 - 每次增加多少',
  `isCycle` int(1) NOT NULL COMMENT '是否循环 - 值是否循环累加',
  `creator` varchar(32) NOT NULL COMMENT '创建人',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `modifier` varchar(32) NOT NULL COMMENT '修改人',
  `modifyTime` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='主键生成器 - 主键生成器';

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
  `id` bigint(18) NOT NULL COMMENT '主键',
  `title` varchar(512) DEFAULT NULL COMMENT '标题',
  `method` varchar(1024) DEFAULT NULL COMMENT '方法 - 执行的方法',
  `url` varchar(1024) DEFAULT NULL COMMENT '请求地址',
  `params` varchar(2048) DEFAULT NULL COMMENT '请求参数',
  `moduleType` int(9) DEFAULT NULL COMMENT '模块类型',
  `logLevel` int(1) DEFAULT NULL COMMENT '日志级别',
  `result` varchar(2048) DEFAULT NULL COMMENT '返回数据',
  `clientIp` varchar(32) DEFAULT NULL COMMENT '客户端IP',
  `errorMsg` varchar(2048) DEFAULT NULL COMMENT '错误信息',
  `creator` varchar(32) NOT NULL COMMENT '创建人',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='日志 - 记录系统日志';

-- ----------------------------
-- Table structure for sys_partner
-- ----------------------------
DROP TABLE IF EXISTS `sys_partner`;
CREATE TABLE `sys_partner` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `name` varchar(128) NOT NULL COMMENT '合作商名',
  `partnerKey` varchar(32) NOT NULL COMMENT '密钥 - 加密解密数据的密钥值',
  `website` varchar(512) DEFAULT NULL COMMENT '网址',
  `status` int(1) NOT NULL COMMENT '状态',
  `deleted` int(1) NOT NULL COMMENT '是否删除',
  `sort` int(9) NOT NULL COMMENT '排序值',
  `contract` varchar(512) DEFAULT NULL COMMENT '合同',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `creator` varchar(32) NOT NULL COMMENT '创建人',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `modifier` varchar(32) NOT NULL COMMENT '修改人',
  `modifyTime` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='合作商 - 合作商调用接口配置';

-- ----------------------------
-- Table structure for sys_project
-- ----------------------------
DROP TABLE IF EXISTS `sys_project`;
CREATE TABLE `sys_project` (
  `id` int(9) NOT NULL COMMENT '主键',
  `name` varchar(32) NOT NULL COMMENT '项目名',
  `projectNo` varchar(32) NOT NULL COMMENT '项目编号',
  `status` int(1) NOT NULL COMMENT '状态',
  `deleted` int(1) NOT NULL COMMENT '是否删除',
  `sort` int(9) NOT NULL COMMENT '排序值',
  `num` int(9) NOT NULL COMMENT '项目数量 - 从1开始依次递增',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `creator` varchar(32) NOT NULL COMMENT '创建人',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `modifier` varchar(32) NOT NULL COMMENT '修改人',
  `modifyTime` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目 - 受权限管理的项目';

-- ----------------------------
-- Table structure for u_group
-- ----------------------------
DROP TABLE IF EXISTS `u_group`;
CREATE TABLE `u_group` (
  `id` int(9) NOT NULL COMMENT '主键',
  `parentId` int(9) DEFAULT NULL COMMENT '上级组织',
  `name` varchar(32) NOT NULL COMMENT '组名',
  `sort` int(9) NOT NULL COMMENT '排序值',
  `status` int(1) NOT NULL COMMENT '状态',
  `deleted` int(1) NOT NULL COMMENT '是否删除',
  `creator` varchar(32) NOT NULL COMMENT '创建人',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `modifier` varchar(32) NOT NULL COMMENT '修改人',
  `modifyTime` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户组 - 用户组';

-- ----------------------------
-- Table structure for u_group_role
-- ----------------------------
DROP TABLE IF EXISTS `u_group_role`;
CREATE TABLE `u_group_role` (
  `roleId` int(9) NOT NULL COMMENT '角色ID',
  `groupId` int(9) NOT NULL COMMENT '用户组ID',
  `creator` varchar(32) NOT NULL COMMENT '创建人',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`roleId`,`groupId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户组角色 - 用户组与角色对应的关系';

-- ----------------------------
-- Table structure for u_permission
-- ----------------------------
DROP TABLE IF EXISTS `u_permission`;
CREATE TABLE `u_permission` (
  `id` bigint(18) NOT NULL COMMENT '主键',
  `parentId` int(9) DEFAULT NULL COMMENT '上级权限',
  `projectId` int(9) NOT NULL COMMENT '项目ID',
  `name` varchar(128) NOT NULL COMMENT '权限名',
  `status` int(1) NOT NULL COMMENT '状态',
  `deleted` int(1) NOT NULL COMMENT '是否删除',
  `isMenu` int(1) NOT NULL COMMENT '菜单 - 是否为菜单按钮',
  `url` varchar(128) DEFAULT NULL COMMENT '地址',
  `sort` int(9) NOT NULL COMMENT '排序值',
  `icon` varchar(128) DEFAULT NULL COMMENT '图标',
  `description` varchar(512) DEFAULT NULL COMMENT '描述',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `creator` varchar(32) NOT NULL COMMENT '创建人',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `modifier` varchar(32) NOT NULL COMMENT '修改人',
  `modifyTime` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='权限 - 权限';

-- ----------------------------
-- Table structure for u_role
-- ----------------------------
DROP TABLE IF EXISTS `u_role`;
CREATE TABLE `u_role` (
  `id` int(9) NOT NULL COMMENT '主键',
  `name` varchar(32) NOT NULL COMMENT '角色名',
  `sort` int(9) NOT NULL COMMENT '排序值',
  `status` int(1) NOT NULL COMMENT '状态',
  `deleted` int(1) NOT NULL COMMENT '是否删除',
  `description` varchar(512) DEFAULT NULL COMMENT '描述',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `creator` varchar(32) NOT NULL COMMENT '创建人',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `modifier` varchar(32) NOT NULL COMMENT '修改人',
  `modifyTime` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色 - 角色';

-- ----------------------------
-- Table structure for u_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `u_role_permission`;
CREATE TABLE `u_role_permission` (
  `roleId` int(9) NOT NULL COMMENT '角色ID',
  `permissionId` bigint(18) NOT NULL COMMENT '权限ID',
  `creator` varchar(32) NOT NULL COMMENT '创建人',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`roleId`,`permissionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色权限 - 角色与权限的对应关系';

-- ----------------------------
-- Table structure for u_role_project
-- ----------------------------
DROP TABLE IF EXISTS `u_role_project`;
CREATE TABLE `u_role_project` (
  `roleId` int(9) NOT NULL COMMENT '角色ID',
  `projectId` int(9) NOT NULL COMMENT '项目ID',
  `creator` varchar(32) NOT NULL COMMENT '创建人',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`roleId`,`projectId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色项目 - 角色与项目的对应关系';

-- ----------------------------
-- Table structure for u_user
-- ----------------------------
DROP TABLE IF EXISTS `u_user`;
CREATE TABLE `u_user` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `groupId` int(9) DEFAULT NULL COMMENT '用户组ID',
  `userNo` varchar(32) DEFAULT NULL COMMENT '员工编号',
  `username` varchar(32) NOT NULL COMMENT '用户名',
  `nickname` varchar(32) NOT NULL COMMENT '昵称',
  `password` varchar(32) NOT NULL COMMENT '密码',
  `realname` varchar(32) DEFAULT NULL COMMENT '真实姓名',
  `status` int(1) NOT NULL COMMENT '状态',
  `deleted` int(1) NOT NULL COMMENT '是否删除',
  `isSuperAdmin` int(1) NOT NULL COMMENT '是否是超级管理员 - 超级管理员具备任何权限',
  `pic` varchar(512) DEFAULT NULL COMMENT '用户头像',
  `sort` int(9) NOT NULL COMMENT '排序值',
  `gender` int(1) DEFAULT NULL COMMENT '性别',
  `email` varchar(512) DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(32) DEFAULT NULL COMMENT '手机号',
  `loginFail` int(9) NOT NULL DEFAULT '0' COMMENT '错误登陆 - 连续错误登陆次数，正确登录后清零',
  `creator` varchar(32) NOT NULL COMMENT '创建人',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `modifier` varchar(32) NOT NULL COMMENT '修改人',
  `modifyTime` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户 - 用户登录后台的用户';

-- ----------------------------
-- Table structure for u_user_role
-- ----------------------------
DROP TABLE IF EXISTS `u_user_role`;
CREATE TABLE `u_user_role` (
  `userId` varchar(32) NOT NULL COMMENT '用户ID',
  `roleId` int(9) NOT NULL COMMENT '角色ID',
  `creator` varchar(32) NOT NULL COMMENT '创建人',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`userId`,`roleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户角色 - 用户与角色的对应关系';

