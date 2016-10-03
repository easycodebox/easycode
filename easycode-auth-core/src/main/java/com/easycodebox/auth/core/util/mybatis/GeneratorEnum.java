package com.easycodebox.auth.core.util.mybatis;

import com.easycodebox.common.enums.DetailEnum;
import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.generator.AbstractGenerator;
import com.easycodebox.common.generator.GeneratorType;
import com.easycodebox.common.generator.impl.AlphaNumericGenerator;
import com.easycodebox.common.generator.impl.IntegerGenerator;
import com.easycodebox.common.generator.impl.LongGenerator;
import com.easycodebox.common.generator.impl.UUIDGenerator;

/**
 * @author WangXiaoJin
 * 
 */
public enum GeneratorEnum implements DetailEnum<String>, GeneratorType {
	
	UUID("UUID", "UUID", new UUIDGenerator()) {
		@SuppressWarnings("rawtypes")
		public AbstractGenerator getGenerator() {
			return new UUIDGenerator();
		}
	},
	
	/********************* 业务逻辑 **********************************/
	
	IMG_NAME("img_name", "图片名", new AlphaNumericGenerator()),
	NICKNAME("nickname", "昵称", new AlphaNumericGenerator()),
	KEY("key", "密钥", new AlphaNumericGenerator(49, 500, "a15db6f", "a15db6f", null, YesNo.NO)),
	
	
	
	/********************* 表ID字段 **********************************/
	
	LOG_ID("log_id", "日志表ID字段", new LongGenerator()),
	PROJECT_ID("project_id", "项目表ID字段", new IntegerGenerator()),
	PARTNER_ID("partner_id", "合作商表ID字段", new AlphaNumericGenerator(59, 500, "a15b6", "a15b6", null, YesNo.NO)),
	
	GROUP_ID("group_id", "用户组表ID字段", new IntegerGenerator()),
	OPERATION_ID("operation_id", "权限表ID字段", new LongGenerator(1, 500, "300000000000", "300000000000", null, YesNo.NO)),
	ROLE_ID("role_id", "角色表ID字段", new IntegerGenerator()),
	USER_ID("user_id", "用户表ID字段", new AlphaNumericGenerator()),
	
	;
	
	private String value;
	private String desc;
	@SuppressWarnings("rawtypes")
	private AbstractGenerator rawGenerator;
	@SuppressWarnings("rawtypes")
	private volatile AbstractGenerator generator;
   
	@SuppressWarnings("rawtypes")
	private GeneratorEnum(String value, String desc, AbstractGenerator rawGenerator) {
        this.value = value;
        this.desc = desc;
        this.rawGenerator = rawGenerator;
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
	@SuppressWarnings("rawtypes")
	public AbstractGenerator getRawGenerator() {
		return rawGenerator;
	}

	@Override
	public String getClassName() {
		return this.name();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public AbstractGenerator getGenerator() {
		return generator;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void setGenerator(AbstractGenerator generator) {
		this.generator = generator;
	}
	
}
