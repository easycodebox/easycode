package com.easycodebox.common.tag;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang.RandomStringUtils;

import com.easycodebox.common.enums.DetailEnum;

/**
 * @author WangXiaoJin
 *
 */
public class SelectEnum extends EnumGroupTag {
	
	private static final long serialVersionUID = 6181195802229352505L;

	private String headerKey;
	private String headerValue;
	
	@Override
	protected void init() {
		headerKey = headerValue = null;
		super.init();
	}

	@Override
	public int doStartTag() throws JspException {
		
		if(begin != null && end != null && begin > end) 
			return SKIP_BODY;
		
		List<Enum<?>> enumsList = getEnumList();
		
		end = end == null ? enumsList.size() - 1 : end;
		
		StringBuilder sb = new StringBuilder();
		if(id == null)
			id = "e_select_" + RandomStringUtils.randomAlphanumeric(8);
		sb.append("<select " + super.generateHtml() + ">");
		if(headerValue != null) {
			sb.append("<option value='" + (headerKey == null ? "" : headerKey) + "' >" + headerValue + "</option>");
		}
		for(; begin <= end; begin++) {
			DetailEnum<?> cur = (DetailEnum<?>)enumsList.get(begin);
			String enumName = ((Enum<?>)cur).name();		//枚举实体的名字
			String selectValue = dataType.equals(DATA_TYPE_VALUE) ? cur.getValue().toString() : enumName;
			sb.append("<option value='" + selectValue + "' ");
			if(selectedValue != null && (enumName.equals(selectedValue) || cur.getValue().toString().equals(selectedValue)))
				sb.append("  selected='selected' ");
			sb.append(" > ");
			sb.append(cur.getDesc());
			sb.append("</option>");
		}
		sb.append("</select>");
		try {
			pageContext.getOut().append(sb);
		} catch (IOException e) {
			LOG.error("RadioEnum Tag processing error.", e);
		}
	
		return EVAL_BODY_INCLUDE;
	}

	public void setHeaderKey(String headerKey) {
		this.headerKey = headerKey;
	}

	public void setHeaderValue(String headerValue) {
		this.headerValue = headerValue;
	}
	
}
