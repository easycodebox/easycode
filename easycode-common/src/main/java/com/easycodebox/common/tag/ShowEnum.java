package com.easycodebox.common.tag;

import com.easycodebox.common.enums.DetailEnum;
import com.easycodebox.common.enums.EnumClassFactory;
import com.easycodebox.common.validate.Assert;

import javax.servlet.jsp.JspException;
import java.io.IOException;

/**
 * @author WangXiaoJin
 * 
 */
public class ShowEnum extends TagExt {
	
	public static final String TYPE_VAL_TO_DESC = "valToDesc";
	public static final String TYPE_OBJ_TO_VAL = "objToVal";
	public static final String TYPE_OBJ_TO_DESC = "objToDesc";
	public static final String TYPE_DESC_TO_VAL = "descToVal";
	
	private String type;
	private String value;
	private String enumName;

	@Override
	protected void init() {
		type = value = enumName = null;
		super.init();
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int doStartTag() throws JspException {
		
		Assert.notNull(type, "type can't be null.");
		Assert.notNull(enumName, "enumName can't be null.");
		
		Class<? extends DetailEnum<?>> enumClass 
				= (Class<? extends DetailEnum<?>>)EnumClassFactory.newInstance(enumName);
		
		String writeVal = null;
		if(TYPE_VAL_TO_DESC.equalsIgnoreCase(type)) {
			for(DetailEnum e : enumClass.getEnumConstants()) {
				if(e.getValue() == null && value == null
						|| value != null && value.equals(e.getValue().toString())) {
					writeVal = e.getDesc();
					break;
				}
			}
		}else if(TYPE_OBJ_TO_VAL.equalsIgnoreCase(type)) {
			DetailEnum e = (DetailEnum)Enum.valueOf((Class)enumClass, value);
			writeVal = e.getValue() == null ? null : e.getValue().toString();
		}else if(TYPE_OBJ_TO_DESC.equalsIgnoreCase(type)) {
			DetailEnum e = (DetailEnum)Enum.valueOf((Class)enumClass, value);
			writeVal = e.getDesc();
		}else if(TYPE_DESC_TO_VAL.equalsIgnoreCase(type)) {
			for(DetailEnum e : enumClass.getEnumConstants()) {
				if(e.getDesc() == null && value == null
						|| value != null && value.equals(e.getDesc())) {
					writeVal = e.getValue() == null ? null : e.getValue().toString();
					break;
				}
			}
		}
		try {
			pageContext.getOut().append(writeVal);
		} catch (IOException e) {
			log.error("ShowEnum Tag processing error.", e);
		}
		return EVAL_BODY_INCLUDE;
	}
	
	public void setType(String type) {
		this.type = obtainVal(type, String.class);
	}

	public void setValue(String value) {
		this.value = obtainVal(value, String.class);
	}

	public void setEnumName(String enumName) {
		this.enumName = obtainVal(enumName, String.class);
	}

}
