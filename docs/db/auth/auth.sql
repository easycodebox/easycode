/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50528
Source Host           : localhost:3306
Source Database       : auth

Target Server Type    : MYSQL
Target Server Version : 50528
File Encoding         : 65001

Date: 2016-08-16 18:57:18
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_generator
-- ----------------------------
DROP TABLE IF EXISTS `sys_generator`;
CREATE TABLE `sys_generator` (
  `generatorType` varchar(32) NOT NULL COMMENT '类型 - 主键生成器类型',
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
  PRIMARY KEY (`generatorType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='主键生成器 - 主键生成器';

-- ----------------------------
-- Records of sys_generator
-- ----------------------------
INSERT INTO `sys_generator` VALUES ('group_id', '359', '5859', '2147483647', '500', '1', '0', '1', '2015-03-04 12:55:40', '1', '2016-07-21 08:20:09');
INSERT INTO `sys_generator` VALUES ('img_name', 'a15b6', 'a15b6', null, '500', '1', '0', '0', '2015-10-13 08:40:41', '0', '2015-10-13 08:40:41');
INSERT INTO `sys_generator` VALUES ('key', 'a15db6f', 'a15fxp7', null, '500', '49', '0', '1', '2015-03-24 16:25:49', '1', '2016-07-24 10:56:24');
INSERT INTO `sys_generator` VALUES ('log_id', '10059', '66111', '9223372036854775807', '500', '1', '0', 'a162y', '2015-04-01 12:09:43', '1', '2016-08-16 14:19:18');
INSERT INTO `sys_generator` VALUES ('nickname', 'a15b6', 'a1fq6', null, '500', '1', '0', 'a162y', '2015-03-25 17:02:04', '1', '2016-07-22 19:26:53');
INSERT INTO `sys_generator` VALUES ('operation_id', '350', '8859', '2147483647', '500', '1', '1', '1', '2015-02-11 12:07:53', '1', '2016-07-23 19:33:48');
INSERT INTO `sys_generator` VALUES ('partner_id', 'a15b6', 'a4b4e', null, '500', '59', '1', '1', '2015-03-24 16:25:49', '1', '2016-07-24 10:56:24');
INSERT INTO `sys_generator` VALUES ('project_id', '359', '4859', '2147483647', '500', '1', '1', '1', '2015-03-04 14:02:33', '1', '2016-07-24 13:22:34');
INSERT INTO `sys_generator` VALUES ('role_id', '359', '8359', '2147483647', '500', '1', '0', '1', '2015-03-04 09:11:50', '1', '2016-08-16 13:59:44');
INSERT INTO `sys_generator` VALUES ('user_id', 'a15b6', 'a1jl2', null, '500', '1', '0', '1', '2015-03-04 15:38:24', '1', '2016-07-22 19:26:53');

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
-- Records of sys_log
-- ----------------------------

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
-- Records of sys_partner
-- ----------------------------
INSERT INTO `sys_partner` VALUES ('a15b6', '云商', 'a15db6f', '', '0', '0', 'contract/partner/6a9w4.jpg', '  ', '1', '2015-03-24 16:25:49', '1', '2016-07-23 20:27:22');
INSERT INTO `sys_partner` VALUES ('a3ocy', 'test11', 'a15fesn', '123123', '0', '12312', 'contract/partner/6ajy4.jpg', '22', '1', '2016-07-24 10:56:24', '1', '2016-07-24 12:20:15');
INSERT INTO `sys_partner` VALUES ('a3oel', 'test2', 'a15feu0', '22', '0', '0', 'contract/partner/6ajy8.jpg', '', '1', '2016-07-24 12:22:25', '1', '2016-07-24 12:28:49');
INSERT INTO `sys_partner` VALUES ('a3og8', 'tettt', 'a15fevd', 'tt', '0', '0', 'contract/partner/6ajy7.jpg', '', '1', '2016-07-24 12:28:42', '1', '2016-07-24 13:02:54');

-- ----------------------------
-- Table structure for sys_project
-- ----------------------------
DROP TABLE IF EXISTS `sys_project`;
CREATE TABLE `sys_project` (
  `id` int(9) NOT NULL COMMENT '主键',
  `name` varchar(32) NOT NULL COMMENT '项目名',
  `projectNo` varchar(32) NOT NULL COMMENT '项目编号',
  `status` int(1) NOT NULL COMMENT '状态',
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
-- Records of sys_project
-- ----------------------------
INSERT INTO `sys_project` VALUES ('1359', '权限项目（后台）', 'EASYCODE-AUTH-BACKEND', '0', '7', '1', '  ', '1', '2015-03-11 17:00:36', '1', '2016-08-12 16:19:34');
INSERT INTO `sys_project` VALUES ('4359', 'test', 'te', '9', '324', '2', '', '1', '2016-07-24 13:22:34', '1', '2016-07-24 13:22:45');

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
  `creator` varchar(32) NOT NULL COMMENT '创建人',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `modifier` varchar(32) NOT NULL COMMENT '修改人',
  `modifyTime` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户组 - 用户组';

-- ----------------------------
-- Records of u_group
-- ----------------------------
INSERT INTO `u_group` VALUES ('360', null, '研发部', '55', '0', '1', '2015-03-04 13:00:27', '1', '2016-07-21 08:14:10');
INSERT INTO `u_group` VALUES ('362', null, '市场部', '2', '0', '1', '2015-03-04 13:21:19', '1', '2016-07-20 12:45:19');
INSERT INTO `u_group` VALUES ('3360', null, '1', '2', '9', '1', '2016-07-19 16:18:37', '1', '2016-07-20 12:12:56');
INSERT INTO `u_group` VALUES ('3361', null, 'bbbb11', '0', '0', '1', '2016-07-19 16:19:03', '1', '2016-07-22 09:51:42');
INSERT INTO `u_group` VALUES ('3859', '360', '121', '1', '0', '1', '2016-07-19 19:17:28', '1', '2016-07-19 19:17:28');
INSERT INTO `u_group` VALUES ('3860', null, 's', '2', '9', '1', '2016-07-19 19:22:18', '1', '2016-07-20 12:12:56');
INSERT INTO `u_group` VALUES ('4359', null, 'tt12', '1', '0', '1', '2016-07-20 14:23:30', '1', '2016-07-20 14:24:07');
INSERT INTO `u_group` VALUES ('4360', null, '21--', '22', '1', '1', '2016-07-20 14:23:47', '1', '2016-07-20 19:35:56');
INSERT INTO `u_group` VALUES ('4361', null, 'test', '2', '0', '1', '2016-07-20 15:34:52', '1', '2016-07-20 17:23:54');
INSERT INTO `u_group` VALUES ('4859', '3361', '111', '1', '0', '1', '2016-07-20 19:35:38', '1', '2016-07-20 19:35:38');
INSERT INTO `u_group` VALUES ('5359', '3361', '23122', '22', '0', '1', '2016-07-21 08:20:09', '1', '2016-07-21 08:24:52');

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
-- Records of u_group_role
-- ----------------------------
INSERT INTO `u_group_role` VALUES ('1359', '4361', '1', '2016-07-22 12:37:28');
INSERT INTO `u_group_role` VALUES ('1361', '360', '1', '2016-07-22 12:37:47');
INSERT INTO `u_group_role` VALUES ('1361', '4361', '1', '2016-07-22 12:37:28');
INSERT INTO `u_group_role` VALUES ('1361', '5359', '1', '2016-07-22 18:34:07');
INSERT INTO `u_group_role` VALUES ('3859', '360', '1', '2016-07-22 12:37:47');
INSERT INTO `u_group_role` VALUES ('3859', '3361', '1', '2016-07-20 13:15:01');
INSERT INTO `u_group_role` VALUES ('3859', '5359', '1', '2016-07-22 18:34:07');

-- ----------------------------
-- Table structure for u_operation
-- ----------------------------
DROP TABLE IF EXISTS `u_operation`;
CREATE TABLE `u_operation` (
  `id` bigint(18) NOT NULL COMMENT '主键',
  `parentId` int(9) DEFAULT NULL COMMENT '上级权限',
  `projectId` int(9) NOT NULL COMMENT '项目ID',
  `name` varchar(128) NOT NULL COMMENT '权限名',
  `status` int(1) NOT NULL COMMENT '状态',
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
-- Records of u_operation
-- ----------------------------
INSERT INTO `u_operation` VALUES ('101000000', null, '1359', '系统管理', '0', '1', null, '0', 'sys', '系统管理', null, '1', '2016-07-22 11:20:02', '1', '2016-07-22 11:20:02');
INSERT INTO `u_operation` VALUES ('101010000', '101000000', '1359', '日志管理', '0', '1', 'log/list', '0', null, '跳转到日志管理页面', null, '1', '2016-07-22 11:20:02', '1', '2016-07-22 11:20:02');
INSERT INTO `u_operation` VALUES ('101015000', '101010000', '1359', '日志详情', '0', '0', 'log/load', '0', null, '日志详情', null, '1', '2016-07-22 11:20:02', '1', '2016-07-22 11:20:02');
INSERT INTO `u_operation` VALUES ('101015100', '101010000', '1359', '删除日志', '0', '0', 'log/removePhy', '0', null, '删除日志', null, '1', '2016-07-22 11:20:02', '1', '2016-07-22 11:20:02');
INSERT INTO `u_operation` VALUES ('101020000', '101000000', '1359', '授权项目管理', '0', '1', 'project/list', '0', null, '跳转到授权项目管理页面', null, '1', '2016-07-22 11:20:02', '1', '2016-07-22 11:20:02');
INSERT INTO `u_operation` VALUES ('101025000', '101020000', '1359', '授权项目详情', '0', '0', 'project/load', '0', null, '授权项目详情', null, '1', '2016-07-22 11:20:02', '1', '2016-07-22 11:20:02');
INSERT INTO `u_operation` VALUES ('101025100', '101020000', '1359', '跳转到修改授权项目页面', '0', '0', 'project/update', '0', null, '跳转到修改授权项目页面', null, '1', '2016-07-22 11:20:02', '1', '2016-07-22 11:20:02');
INSERT INTO `u_operation` VALUES ('101025200', '101020000', '1359', '修改授权项目', '0', '0', 'project/updating', '0', null, '修改授权项目', null, '1', '2016-07-22 11:20:02', '1', '2016-07-22 11:20:02');
INSERT INTO `u_operation` VALUES ('101025300', '101020000', '1359', '删除授权项目', '0', '0', 'project/remove', '0', null, '删除授权项目', null, '1', '2016-07-22 11:20:02', '1', '2016-07-22 11:20:02');
INSERT INTO `u_operation` VALUES ('101025400', '101020000', '1359', '启用/禁用授权项目', '0', '0', 'project/openClose', '0', null, '启用/禁用授权项目', null, '1', '2016-07-22 11:20:02', '1', '2016-07-22 11:20:02');
INSERT INTO `u_operation` VALUES ('101030000', '101000000', '1359', '生成策略管理', '0', '1', 'generator/list', '0', null, '跳转到生成策略管理页面', null, '1', '2016-07-22 11:20:02', '1', '2016-07-22 11:20:02');
INSERT INTO `u_operation` VALUES ('101035000', '101030000', '1359', '生成策略详情', '0', '0', 'generator/load', '0', null, '生成策略详情', null, '1', '2016-07-22 11:20:02', '1', '2016-07-22 11:20:02');
INSERT INTO `u_operation` VALUES ('101035100', '101030000', '1359', '跳转到修改生成策略页面', '0', '0', 'generator/update', '0', null, '跳转到修改生成策略页面', null, '1', '2016-07-22 11:20:02', '1', '2016-07-22 11:20:02');
INSERT INTO `u_operation` VALUES ('101035200', '101030000', '1359', '修改生成策略', '0', '0', 'generator/updating', '0', null, '修改生成策略', null, '1', '2016-07-22 11:20:02', '1', '2016-07-22 11:20:02');
INSERT INTO `u_operation` VALUES ('101040000', '101000000', '1359', '合作商管理', '0', '1', 'partner/list', '0', null, '跳转到合作商管理页面', null, '1', '2016-07-22 11:20:02', '1', '2016-07-22 11:20:02');
INSERT INTO `u_operation` VALUES ('101045000', '101040000', '1359', '合作商详情', '0', '0', 'partner/load', '0', null, '合作商详情', null, '1', '2016-07-22 11:20:02', '1', '2016-07-22 11:20:02');
INSERT INTO `u_operation` VALUES ('101045100', '101040000', '1359', '跳转到修改合作商页面', '0', '0', 'partner/update', '0', null, '跳转到修改合作商页面', null, '1', '2016-07-22 11:20:02', '1', '2016-07-22 11:20:02');
INSERT INTO `u_operation` VALUES ('101045200', '101040000', '1359', '修改合作商', '0', '0', 'partner/updating', '0', null, '修改合作商', null, '1', '2016-07-22 11:20:02', '1', '2016-07-22 11:20:02');
INSERT INTO `u_operation` VALUES ('101045300', '101040000', '1359', '删除合作商', '0', '0', 'partner/remove', '0', null, '删除合作商', null, '1', '2016-07-22 11:20:02', '1', '2016-07-22 11:20:02');
INSERT INTO `u_operation` VALUES ('101045400', '101040000', '1359', '启用/禁用合作商', '0', '0', 'partner/openClose', '0', null, '启用/禁用合作商', null, '1', '2016-07-22 11:20:02', '1', '2016-07-22 11:20:02');
INSERT INTO `u_operation` VALUES ('102000000', null, '1359', '用户管理', '0', '1', null, '0', 'user', '用户管理', null, '1', '2016-07-22 11:20:02', '1', '2016-07-22 11:20:02');
INSERT INTO `u_operation` VALUES ('102010000', '102000000', '1359', '用户管理', '0', '1', 'user/list', '0', null, '跳转到用户管理页面', null, '1', '2016-07-22 11:20:02', '1', '2016-07-22 11:20:02');
INSERT INTO `u_operation` VALUES ('102015000', '102010000', '1359', '用户详情', '0', '0', 'user/load', '0', null, '用户详情', null, '1', '2016-07-22 11:20:02', '1', '2016-07-22 11:20:02');
INSERT INTO `u_operation` VALUES ('102015100', '102010000', '1359', '跳转到修改用户页面', '0', '0', 'user/update', '0', null, '跳转到修改用户页面', null, '1', '2016-07-22 11:20:02', '1', '2016-07-22 11:20:02');
INSERT INTO `u_operation` VALUES ('102015200', '102010000', '1359', '修改用户', '0', '0', 'user/updating', '0', null, '修改用户', null, '1', '2016-07-22 11:20:02', '1', '2016-07-22 11:20:02');
INSERT INTO `u_operation` VALUES ('102015300', '102010000', '1359', '跳转到添加用户页面', '0', '0', 'user/add', '0', null, '跳转到添加用户页面', null, '1', '2016-07-22 11:20:02', '1', '2016-07-22 11:20:02');
INSERT INTO `u_operation` VALUES ('102015400', '102010000', '1359', '添加用户', '0', '0', 'user/adding', '0', null, '添加用户', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102015500', '102010000', '1359', '删除用户', '0', '0', 'user/remove', '0', null, '删除用户', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102015600', '102010000', '1359', '启用/禁用用户', '0', '0', 'user/openClose', '0', null, '启用/禁用用户', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102015700', '102010000', '1359', '显示配置用户角色页面', '0', '0', 'user/roleSetup', '0', null, '显示为用户配置角色页面', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102015800', '102010000', '1359', '执行配置用户角色', '0', '0', 'user/roleSetuping', '0', null, '执行配置用户角色', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102015900', '102010000', '1359', '重置密码', '0', '0', 'user/resetpwd', '0', null, '重置指定用户的密码', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102020000', '102000000', '1359', '组织管理', '0', '1', 'group/list', '0', null, '跳转到组织管理页面', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102025000', '102020000', '1359', '组织详情', '0', '0', 'group/load', '0', null, '组织详情', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102025100', '102020000', '1359', '跳转到修改组织页面', '0', '0', 'group/update', '0', null, '跳转到修改组织页面', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102025200', '102020000', '1359', '修改组织', '0', '0', 'group/updating', '0', null, '修改组织', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102025300', '102020000', '1359', '跳转到添加组织页面', '0', '0', 'group/add', '0', null, '跳转到添加组织页面', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102025400', '102020000', '1359', '添加组织', '0', '0', 'group/adding', '0', null, '添加组织', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102025500', '102020000', '1359', '删除组织', '0', '0', 'group/remove', '0', null, '删除组织', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102025600', '102020000', '1359', '启用/禁用组织', '0', '0', 'group/openClose', '0', null, '启用/禁用组织', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102025700', '102020000', '1359', '显示角色设置页面', '0', '0', 'group/roleSetup', '0', null, '显示角色设置页面', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102025800', '102020000', '1359', '执行设置组织的角色', '0', '0', 'group/roleSetuping', '0', null, '执行设置组织的角色', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102030000', '102000000', '1359', '角色管理', '0', '1', 'role/list', '0', null, '跳转到角色管理页面', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102035000', '102030000', '1359', '角色详情', '0', '0', 'role/load', '0', null, '角色详情', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102035100', '102030000', '1359', '跳转到修改角色页面', '0', '0', 'role/update', '0', null, '跳转到修改角色页面', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102035200', '102030000', '1359', '修改角色', '0', '0', 'role/updating', '0', null, '修改角色', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102035300', '102030000', '1359', '跳转到添加角色页面', '0', '0', 'role/add', '0', null, '跳转到添加角色页面', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102035400', '102030000', '1359', '添加角色', '0', '0', 'role/adding', '0', null, '添加角色', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102035500', '102030000', '1359', '删除角色', '0', '0', 'role/remove', '0', null, '删除角色', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102035600', '102030000', '1359', '启用/禁用角色', '0', '0', 'role/openClose', '0', null, '启用/禁用角色', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102035700', '102030000', '1359', '跳转到配置角色权限页面', '0', '0', 'role/empower', '0', null, '跳转到配置角色权限页面', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102035800', '102030000', '1359', '执行角色授权', '0', '0', 'role/empowering', '0', null, '为指定角色授权限', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102040000', '102000000', '1359', '权限管理', '0', '1', 'operation/list', '0', null, '跳转到权限管理页面', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102045000', '102040000', '1359', '权限详情', '0', '0', 'operation/load', '0', null, '权限详情', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102045100', '102040000', '1359', '跳转到修改权限页面', '0', '0', 'operation/update', '0', null, '跳转到修改权限页面', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102045200', '102040000', '1359', '修改权限', '0', '0', 'operation/updating', '0', null, '修改权限', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102045300', '102040000', '1359', '跳转到添加权限页面', '0', '0', 'operation/add', '0', null, '跳转到添加权限页面', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102045400', '102040000', '1359', '添加权限', '0', '0', 'operation/adding', '0', null, '添加权限', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102045500', '102040000', '1359', '删除权限', '0', '0', 'operation/remove', '0', null, '删除权限', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102045600', '102040000', '1359', '启用/禁用权限', '0', '0', 'operation/openClose', '0', null, '启用/禁用权限', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102045700', '102040000', '1359', '导入权限', '0', '0', 'operation/imports', '0', null, '导入权限', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');
INSERT INTO `u_operation` VALUES ('102045800', '102040000', '1359', '导出权限', '0', '0', 'operation/exports', '0', null, '导出权限', null, '1', '2016-07-22 11:20:03', '1', '2016-07-22 11:20:03');

-- ----------------------------
-- Table structure for u_role
-- ----------------------------
DROP TABLE IF EXISTS `u_role`;
CREATE TABLE `u_role` (
  `id` int(9) NOT NULL COMMENT '主键',
  `name` varchar(32) NOT NULL COMMENT '角色名',
  `sort` int(9) NOT NULL COMMENT '排序值',
  `status` int(1) NOT NULL COMMENT '状态',
  `description` varchar(512) DEFAULT NULL COMMENT '描述',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `creator` varchar(32) NOT NULL COMMENT '创建人',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `modifier` varchar(32) NOT NULL COMMENT '修改人',
  `modifyTime` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色 - 角色';

-- ----------------------------
-- Records of u_role
-- ----------------------------
INSERT INTO `u_role` VALUES ('1359', '市场营销', '0', '0', ' ', ' ', 'a162y', '2015-03-25 14:43:36', 'a162y', '2015-03-25 14:43:36');
INSERT INTO `u_role` VALUES ('1361', '商家管理员', '0', '0', ' ', ' ', '1', '2015-03-26 14:20:54', '1', '2016-07-22 18:35:24');
INSERT INTO `u_role` VALUES ('3859', '管理员', '0', '0', ' 管理员', ' ', '1', '2015-04-08 15:27:05', '1', '2016-07-22 18:35:11');
INSERT INTO `u_role` VALUES ('6859', 'test', '0', '0', '  ', '  ', '1', '2016-07-13 20:07:26', '1', '2016-07-22 18:34:21');
INSERT INTO `u_role` VALUES ('7359', 'test1123213', '123', '9', '32323', '23213123', '1', '2016-07-22 17:28:52', '1', '2016-07-22 17:29:30');
INSERT INTO `u_role` VALUES ('7859', '2', '0', '0', '12143', '213', '1', '2016-08-16 13:59:18', '1', '2016-08-16 14:19:23');

-- ----------------------------
-- Table structure for u_role_operation
-- ----------------------------
DROP TABLE IF EXISTS `u_role_operation`;
CREATE TABLE `u_role_operation` (
  `roleId` int(9) NOT NULL COMMENT '角色ID',
  `operationId` bigint(18) NOT NULL COMMENT '权限ID',
  `creator` varchar(32) NOT NULL COMMENT '创建人',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`roleId`,`operationId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色权限 - 角色与权限的对应关系';

-- ----------------------------
-- Records of u_role_operation
-- ----------------------------
INSERT INTO `u_role_operation` VALUES ('1361', '101000000', '1', '2016-07-22 18:33:44');
INSERT INTO `u_role_operation` VALUES ('1361', '101010000', '1', '2016-07-22 18:33:44');
INSERT INTO `u_role_operation` VALUES ('1361', '101015000', '1', '2016-07-22 18:33:44');
INSERT INTO `u_role_operation` VALUES ('1361', '101015100', '1', '2016-07-22 18:33:44');
INSERT INTO `u_role_operation` VALUES ('1361', '101020000', '1', '2016-07-22 18:33:44');
INSERT INTO `u_role_operation` VALUES ('1361', '101025000', '1', '2016-07-22 18:33:44');
INSERT INTO `u_role_operation` VALUES ('1361', '101025100', '1', '2016-07-22 18:33:44');
INSERT INTO `u_role_operation` VALUES ('1361', '101025200', '1', '2016-07-22 18:33:44');
INSERT INTO `u_role_operation` VALUES ('1361', '101025300', '1', '2016-07-22 18:33:44');
INSERT INTO `u_role_operation` VALUES ('1361', '101025400', '1', '2016-07-22 18:33:44');
INSERT INTO `u_role_operation` VALUES ('1361', '101030000', '1', '2016-07-22 18:33:44');
INSERT INTO `u_role_operation` VALUES ('1361', '101035000', '1', '2016-07-22 18:33:45');
INSERT INTO `u_role_operation` VALUES ('1361', '101035100', '1', '2016-07-22 18:33:45');
INSERT INTO `u_role_operation` VALUES ('1361', '101035200', '1', '2016-07-22 18:33:45');
INSERT INTO `u_role_operation` VALUES ('1361', '101040000', '1', '2016-07-22 18:33:45');
INSERT INTO `u_role_operation` VALUES ('1361', '101045000', '1', '2016-07-22 18:33:45');
INSERT INTO `u_role_operation` VALUES ('1361', '101045100', '1', '2016-07-22 18:33:45');
INSERT INTO `u_role_operation` VALUES ('1361', '101045200', '1', '2016-07-22 18:33:45');
INSERT INTO `u_role_operation` VALUES ('1361', '101045300', '1', '2016-07-22 18:33:45');
INSERT INTO `u_role_operation` VALUES ('1361', '101045400', '1', '2016-07-22 18:33:45');
INSERT INTO `u_role_operation` VALUES ('1361', '102000000', '1', '2016-07-22 18:33:45');
INSERT INTO `u_role_operation` VALUES ('1361', '102010000', '1', '2016-07-22 18:33:46');
INSERT INTO `u_role_operation` VALUES ('1361', '102015000', '1', '2016-07-22 18:33:47');
INSERT INTO `u_role_operation` VALUES ('1361', '102015100', '1', '2016-07-22 18:33:47');
INSERT INTO `u_role_operation` VALUES ('1361', '102015200', '1', '2016-07-22 18:33:47');
INSERT INTO `u_role_operation` VALUES ('1361', '102015300', '1', '2016-07-22 18:33:47');
INSERT INTO `u_role_operation` VALUES ('1361', '102015400', '1', '2016-07-22 18:33:47');
INSERT INTO `u_role_operation` VALUES ('1361', '102015500', '1', '2016-07-22 18:33:46');
INSERT INTO `u_role_operation` VALUES ('1361', '102015600', '1', '2016-07-22 18:33:46');
INSERT INTO `u_role_operation` VALUES ('1361', '102015700', '1', '2016-07-22 18:33:47');
INSERT INTO `u_role_operation` VALUES ('1361', '102015800', '1', '2016-07-22 18:33:46');
INSERT INTO `u_role_operation` VALUES ('1361', '102015900', '1', '2016-07-22 18:33:46');
INSERT INTO `u_role_operation` VALUES ('1361', '102020000', '1', '2016-07-22 18:33:45');
INSERT INTO `u_role_operation` VALUES ('1361', '102025000', '1', '2016-07-22 18:33:45');
INSERT INTO `u_role_operation` VALUES ('1361', '102025100', '1', '2016-07-22 18:33:45');
INSERT INTO `u_role_operation` VALUES ('1361', '102025200', '1', '2016-07-22 18:33:45');
INSERT INTO `u_role_operation` VALUES ('1361', '102025300', '1', '2016-07-22 18:33:45');
INSERT INTO `u_role_operation` VALUES ('1361', '102025400', '1', '2016-07-22 18:33:45');
INSERT INTO `u_role_operation` VALUES ('1361', '102025500', '1', '2016-07-22 18:33:45');
INSERT INTO `u_role_operation` VALUES ('1361', '102025600', '1', '2016-07-22 18:33:45');
INSERT INTO `u_role_operation` VALUES ('1361', '102025700', '1', '2016-07-22 18:33:45');
INSERT INTO `u_role_operation` VALUES ('1361', '102025800', '1', '2016-07-22 18:33:45');
INSERT INTO `u_role_operation` VALUES ('1361', '102030000', '1', '2016-07-22 18:33:45');
INSERT INTO `u_role_operation` VALUES ('1361', '102035000', '1', '2016-07-22 18:33:45');
INSERT INTO `u_role_operation` VALUES ('1361', '102035100', '1', '2016-07-22 18:33:46');
INSERT INTO `u_role_operation` VALUES ('1361', '102035200', '1', '2016-07-22 18:33:45');
INSERT INTO `u_role_operation` VALUES ('1361', '102035300', '1', '2016-07-22 18:33:46');
INSERT INTO `u_role_operation` VALUES ('1361', '102035400', '1', '2016-07-22 18:33:46');
INSERT INTO `u_role_operation` VALUES ('1361', '102035500', '1', '2016-07-22 18:33:45');
INSERT INTO `u_role_operation` VALUES ('1361', '102035600', '1', '2016-07-22 18:33:46');
INSERT INTO `u_role_operation` VALUES ('1361', '102035700', '1', '2016-07-22 18:33:46');
INSERT INTO `u_role_operation` VALUES ('1361', '102035800', '1', '2016-07-22 18:33:46');
INSERT INTO `u_role_operation` VALUES ('1361', '102040000', '1', '2016-07-22 18:33:46');
INSERT INTO `u_role_operation` VALUES ('1361', '102045000', '1', '2016-07-22 18:33:46');
INSERT INTO `u_role_operation` VALUES ('1361', '102045100', '1', '2016-07-22 18:33:46');
INSERT INTO `u_role_operation` VALUES ('1361', '102045200', '1', '2016-07-22 18:33:46');
INSERT INTO `u_role_operation` VALUES ('1361', '102045300', '1', '2016-07-22 18:33:46');
INSERT INTO `u_role_operation` VALUES ('1361', '102045400', '1', '2016-07-22 18:33:46');
INSERT INTO `u_role_operation` VALUES ('1361', '102045500', '1', '2016-07-22 18:33:46');
INSERT INTO `u_role_operation` VALUES ('1361', '102045600', '1', '2016-07-22 18:33:46');
INSERT INTO `u_role_operation` VALUES ('1361', '102045700', '1', '2016-07-22 18:33:46');
INSERT INTO `u_role_operation` VALUES ('1361', '102045800', '1', '2016-07-22 18:33:46');
INSERT INTO `u_role_operation` VALUES ('3859', '101000000', '1', '2016-07-22 18:31:42');
INSERT INTO `u_role_operation` VALUES ('3859', '101010000', '1', '2016-07-22 18:31:42');
INSERT INTO `u_role_operation` VALUES ('3859', '101015000', '1', '2016-07-22 18:31:42');
INSERT INTO `u_role_operation` VALUES ('3859', '101015100', '1', '2016-07-22 18:31:42');
INSERT INTO `u_role_operation` VALUES ('3859', '101020000', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '101025000', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '101025100', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '101025200', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '101025300', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '101025400', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '101030000', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '101035000', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '101035100', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '101035200', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '101040000', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '101045000', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '101045100', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '101045200', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '101045300', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '101045400', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '102000000', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '102010000', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102015000', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102015100', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102015200', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102015300', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102015400', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102015500', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102015600', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102015700', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102015800', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102015900', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102020000', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '102025000', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '102025100', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '102025200', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '102025300', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '102025400', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '102025500', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '102025600', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '102025700', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '102025800', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '102030000', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '102035000', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102035100', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102035200', '1', '2016-07-22 18:31:43');
INSERT INTO `u_role_operation` VALUES ('3859', '102035300', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102035400', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102035500', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102035600', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102035700', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102035800', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102040000', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102045000', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102045100', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102045200', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102045300', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102045400', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102045500', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102045600', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102045700', '1', '2016-07-22 18:31:44');
INSERT INTO `u_role_operation` VALUES ('3859', '102045800', '1', '2016-07-22 18:31:44');

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
-- Records of u_role_project
-- ----------------------------
INSERT INTO `u_role_project` VALUES ('1361', '1359', '1', '2016-07-22 18:33:47');
INSERT INTO `u_role_project` VALUES ('3859', '1359', '1', '2016-07-22 18:31:45');

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
-- Records of u_user
-- ----------------------------
INSERT INTO `u_user` VALUES ('1', null, '1', 'superadmin', '超级管理员', '96e79218965eb72c92a549dd5a330112', '超级管理员', '0', '1', null, '1000', '0', null, null, '0', '0', '2015-02-02 12:35:12', '1', '2016-07-22 20:01:20');
INSERT INTO `u_user` VALUES ('a15b7', null, '12321', 'user', 'nicheng', '123456', '吴刚', '9', '0', null, '34', '0', 'wnag@qq.com', '15069866954', '0', '1', '2015-03-04 15:57:53', '1', '2015-03-04 16:01:18');
INSERT INTO `u_user` VALUES ('a162y', '360', '', 'admin', '管理员', 'e10adc3949ba59abbe56e057f20f883e', null, '0', '0', null, '2', null, null, null, '0', '1', '2015-03-09 13:17:43', 'a162y', '2016-02-18 14:00:38');
INSERT INTO `u_user` VALUES ('a1j76', '3361', '12', 'test', 'a1fca', '698d51a19d8a121ce581499d7b701668', '12', '9', '0', null, '0', '1', null, null, '0', '1', '2016-07-22 19:26:53', '1', '2016-07-22 19:28:45');
INSERT INTO `u_user` VALUES ('a1j77', '362', '123', '123', '123', 'b59c67bf196a4758191e42f76670ceba', '213', '0', '0', null, '0', '0', null, null, '0', '1', '2016-07-22 19:28:12', '1', '2016-07-22 19:29:07');

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

-- ----------------------------
-- Records of u_user_role
-- ----------------------------
INSERT INTO `u_user_role` VALUES ('a1j77', '1361', '1', '2016-07-22 19:28:57');
INSERT INTO `u_user_role` VALUES ('a1j77', '3859', '1', '2016-07-22 19:28:57');
