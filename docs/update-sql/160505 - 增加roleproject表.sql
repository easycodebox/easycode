

drop table if exists u_role_project;

/*==============================================================*/
/* Table: u_role_project                                        */
/*==============================================================*/
create table u_role_project
(
   roleId               int(9) not null comment '角色ID',
   projectId            int(9) not null comment '项目ID',
   creator              varchar(32) not null comment '创建人',
   createTime           datetime not null comment '创建时间',
   primary key (roleId, projectId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table u_role_project comment '角色项目 - 角色与项目的对应关系';

/*==============================================================*/
/* 增加此表后需要重新设置每个角色的权限，不然不能登录                                        */
/*==============================================================*/


