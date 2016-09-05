

UPDATE u_user SET userType = 0;

UPDATE u_user SET userType = 1 WHERE username = 'superadmin';

ALTER TABLE u_user CHANGE userType isSuperAdmin int(1) not null comment '是否是超级管理员 - 超级管理员具备任何权限';

