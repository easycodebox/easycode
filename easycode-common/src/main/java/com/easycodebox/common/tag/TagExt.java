package com.easycodebox.common.tag;

import com.easycodebox.common.lang.DataConvert;
import com.easycodebox.common.lang.StringToken.OgnlToken;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author WangXiaoJin
 *
 */
public abstract class TagExt extends BodyTagSupport {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	private final String EXP = "^\\s*%\\s*\\{\\s*([\\w\\.]+)\\s*\\}\\s*$";
	private final Pattern expPattern = Pattern.compile(EXP); 
	
	public TagExt() {
		super();
		init();
	}
	
	/**
	 * 子类实现的方法，初始化数据
	 */
	protected void init() {
		
	}
	
	@Override
	public int doEndTag() throws JspException {
		this.release();
		return super.doEndTag();
	}

	@Override
	public void release() {
        super.release();
        init();
    }
	
	/**
	 * 如果name是%{}表达式，则从pageContext获取name相对应的value，否则返回name值本身
	 * @param name
	 * @param returnClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> T obtainVal(Object name, Class<T> returnClass) {
		if(name == null) return null;
		if(returnClass.isAssignableFrom(name.getClass())
				&& !String.class.equals(name.getClass()))
			return (T)name;
		
		String str = name.toString();
		boolean isExp = false;
		Matcher matcher = expPattern.matcher(str);
		if(matcher.find()) {
			isExp = true;
			str = matcher.group(1);
		}
		if(isExp)
			return (T)findAttribute(str);
		else 
			return DataConvert.convertType(str, returnClass);
	}
	
	private Object findAttribute(String key) {
		OgnlToken token = new OgnlToken(key);
		String tmpKey = token.nextKey();
		Object data = pageContext.findAttribute(tmpKey);
		while(data != null && !(tmpKey = token.nextKey()).isEmpty()) {
			if(token.isDynamicKey()) {
				token.resetDynamicKey();
				tmpKey = pageContext.findAttribute(tmpKey).toString();
			}
			try {
				data = PropertyUtils.getProperty(data, tmpKey);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				log.warn("Obtain obj({} -- {}) property({}) error.", data.getClass(), data, tmpKey);
				return null;
			}
		}
		return data;
	}
	
	/**
	 * 判断value是不是表达式
	 * @param value
	 * @return
	 */
	protected boolean isExp(String value) {
		return expPattern.matcher(value).matches();
	}
	
}
