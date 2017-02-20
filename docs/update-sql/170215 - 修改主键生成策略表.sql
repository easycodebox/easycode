
ALTER TABLE sys_generator CHANGE generatorType id varchar(32) NOT NULL COMMENT '类型 - 主键生成器类型';


ALTER  TABLE sys_generator RENAME TO sys_id_generator;

UPDATE u_permission SET url = REPLACE(url, 'generator', 'idGenerator') WHERE url LIKE '%generator%';

UPDATE u_permission SET url = INSERT(url, 1, 0, '/') WHERE url IS NOT NULL;



