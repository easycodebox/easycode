package com.easycodebox.common.enums;

import com.easycodebox.common.jackson.DetailEnumDeserializer;
import com.easycodebox.common.jackson.DetailEnumSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author WangXiaoJin
 * 
 */
@JsonSerialize(using = DetailEnumSerializer.class)
@JsonDeserialize(using = DetailEnumDeserializer.class)
public interface DetailEnum<T> {

	T getValue(); 

	String getDesc();

	String getClassName(); 
}
