

UPDATE u_user SET userType = 0;

UPDATE u_user SET userType = 1 WHERE username = 'superadmin';

ALTER TABLE u_user CHANGE userType isSuperAdmin int(1) not null comment '是否是超级管理员 - 超级管理员具备任何权限';

ALTER TABLE u_user  userType isSuperAdmin int(1) not null comment '是否是超级管理员 - 超级管理员具备任何权限';

ALTER TABLE u_user ADD deleted int(1) not null comment '是否删除' AFTER status;
ALTER TABLE u_group ADD deleted int(1) not null comment '是否删除' AFTER status;
ALTER TABLE u_permission ADD deleted int(1) not null comment '是否删除' AFTER status;
ALTER TABLE u_role ADD deleted int(1) not null comment '是否删除' AFTER status;
ALTER TABLE sys_partner ADD deleted int(1) not null comment '是否删除' AFTER status;
ALTER TABLE sys_project ADD deleted int(1) not null comment '是否删除' AFTER status;

