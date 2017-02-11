package com.easycodebox.common.tag;

import com.easycodebox.common.idconverter.IdConverter;
import com.easycodebox.common.idconverter.IdConverterRegistry;
import com.easycodebox.common.spring.BeanFactory;
import com.easycodebox.common.validate.Assert;

import javax.servlet.jsp.JspException;
import java.io.IOException;

/**
 * @author WangXiaoJin
 * 
 */
public class ConvertIdTag extends TagExt {
	
	/**
	 * IdConverter注册器
	 */
	private static IdConverterRegistry idConverterRegistry;
	
	private String module;
	/**
	 * 因父类已有id属性，所以用cid - convert id
	 */
	private Object cid;
	private String prop;
	
	@Override
	protected void init() {
		super.init();
		module = prop = null;
		cid = null;
		if (idConverterRegistry == null) {
			idConverterRegistry = BeanFactory.getBean(IdConverterRegistry.class);
		}
	}
	
	@Override
	public int doStartTag() throws JspException {
		try {
			IdConverter idConverter = idConverterRegistry.getIdConverter(module, true);
			Assert.notNull(idConverter, "Can't find corresponding IdConverter : {0}.", module);
			Object data = idConverter.convert(cid, prop);
			if(data != null)
				pageContext.getOut().append(data.toString());
		} catch (IOException e) {
			log.error("IOException.", e);
		}
		return super.doStartTag();
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public Object getCid() {
		return cid;
	}

	public void setCid(Object cid) {
		this.cid = cid;
	}

	public String getProp() {
		return prop;
	}

	public void setProp(String prop) {
		this.prop = prop;
	}
	
}
