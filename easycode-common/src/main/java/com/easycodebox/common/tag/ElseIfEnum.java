package com.easycodebox.common.tag;

import javax.servlet.jsp.JspException;

import org.apache.commons.beanutils.PropertyUtils;

import com.easycodebox.common.enums.EnumClassFactory;
import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.validate.Assert;

/**
 * @author WangXiaoJin
 * 
 */
public class ElseIfEnum extends TagExt {
	
	private static final long serialVersionUID = 7440207878254221968L;

	private Object test;
	/**
	 * 例子：OpenClose.OPEN
	 */
    private String enumValue;
    /**
     * test作为enumProperty指定的属性值，与enumValue中的enumProperty属性值进行比较
     * 如果enumProperty == null，则比较枚举对象本身，而不是枚举对象里面的属性
     */
    private String enumProperty;
	
	@Override
	protected void init() {
		test = enumValue = enumProperty = null;
		super.init();
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int doStartTag() throws JspException {
		Boolean ifResult = (Boolean) pageContext.getAttribute(IfEnum.ANSWER);
		if (ifResult == null || ifResult.booleanValue()) {
            return SKIP_BODY;
        }
		
		Assert.notNull(test, "test can not be null.");
		Assert.notBlank(enumValue, "enumValue can not be blank.");
		
		String[] array = enumValue.split("\\.");
		if(array.length != 2) {
			throw new IllegalArgumentException("enumValue error format.");
		}
		
		Class<? extends Enum<?>> enumClass 
				= (Class<? extends Enum<?>>)EnumClassFactory.newInstance(array[0]);
		
		Enum e = Enum.valueOf((Class)enumClass, array[1]);
		Boolean answer;
		if(enumProperty == null) {
			if(test instanceof String && e.name().equals(test)
					|| e.equals(test))
				answer = Boolean.TRUE;
			else
				answer = Boolean.FALSE;
		}else {
			try {
				Object pro = PropertyUtils.getSimpleProperty(e, enumProperty);
				if(pro != null && test.toString().equals(pro.toString()))
					answer = Boolean.TRUE;
				else
					answer = Boolean.FALSE;
			} catch (Exception e1) {
				throw new BaseException("Obtain enum({0}) property({1}) error.", e1, e, enumProperty);
			}
		}
		
        pageContext.setAttribute(IfEnum.ANSWER, answer);
		return answer ? EVAL_BODY_INCLUDE : SKIP_BODY;
	}

	public void setTest(Object test) {
		this.test = obtainVal(test, Object.class);
	}

	public void setEnumValue(String enumValue) {
		this.enumValue = obtainVal(enumValue, String.class);
	}

	public void setEnumProperty(String enumProperty) {
		this.enumProperty = obtainVal(enumProperty, String.class);
	}
	
}
