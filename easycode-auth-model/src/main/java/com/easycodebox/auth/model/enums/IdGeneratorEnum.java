package com.easycodebox.auth.model.enums;

import com.easycodebox.common.enums.DetailEnum;
import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.idgenerator.AbstractIdGenerator;
import com.easycodebox.common.idgenerator.IdGeneratorType;
import com.easycodebox.common.idgenerator.impl.*;

/**
 * @author WangXiaoJin
 * 
 */
public enum IdGeneratorEnum implements DetailEnum<String>, IdGeneratorType {
	
	UUID("UUID", "UUID", new UuidGenerator()) {
		@SuppressWarnings("rawtypes")
		public AbstractIdGenerator getIdGenerator() {
			return new UuidGenerator();
		}
	},
	
	/********************* 业务逻辑 **********************************/
	
	IMG_NAME("img_name", "图片名", new AlphaNumericIdGenerator()),
	NICKNAME("nickname", "昵称", new AlphaNumericIdGenerator()),
	KEY("key", "密钥", new AlphaNumericIdGenerator(49, 500, "a15db6f", "a15db6f", null, YesNo.NO)),
	
	
	
	/********************* 表ID字段 **********************************/
	
	LOG_ID("log_id", "日志表ID字段", new LongIdGenerator()),
	PROJECT_ID("project_id", "项目表ID字段", new IntegerIdGenerator()),
	PARTNER_ID("partner_id", "合作商表ID字段", new AlphaNumericIdGenerator(59, 500, "a15b6", "a15b6", null, YesNo.NO)),
	
	GROUP_ID("group_id", "用户组表ID字段", new IntegerIdGenerator()),
	PERMISSION_ID("permission_id", "权限表ID字段", new LongIdGenerator(1, 500, 300000000000L, 300000000000L, null, YesNo.NO)),
	ROLE_ID("role_id", "角色表ID字段", new IntegerIdGenerator()),
	USER_ID("user_id", "用户表ID字段", new AlphaNumericIdGenerator()),
	
	;
	
	private String value;
	private String desc;
	@SuppressWarnings("rawtypes")
	private AbstractIdGenerator rawIdGenerator;
	@SuppressWarnings("rawtypes")
	private volatile AbstractIdGenerator idGenerator;
   
	@SuppressWarnings("rawtypes")
	IdGeneratorEnum(String value, String desc, AbstractIdGenerator rawIdGenerator) {
        this.value = value;
        this.desc = desc;
        this.rawIdGenerator = rawIdGenerator;
    }

	@Override
	public String getDesc() {
		return desc;
	}

	@Override
	public String getValue() {
		return value;
	}
	
	@Override
	public String getPersistentKey() {
		return getValue();
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public AbstractIdGenerator getRawIdGenerator() {
		return rawIdGenerator;
	}

	@Override
	public String getClassName() {
		return this.name();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public AbstractIdGenerator getIdGenerator() {
		return idGenerator;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void setIdGenerator(AbstractIdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}
	
}
