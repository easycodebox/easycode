package com.easycodebox.common.tag;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import com.easycodebox.common.enums.DetailEnum;

/**
 * @author WangXiaoJin
 *
 */
public class RadioEnum extends EnumGroupTag {
	
	private static final long serialVersionUID = -7557255606359958682L;

	private String labelClass;
	private String labelStyle;
	
	private String tmpId;
	
	@Override
	protected void init() {
		labelClass = labelStyle = tmpId = null;
		super.init();
	}

	@Override
	public int doStartTag() throws JspException {

		if(begin != null && end != null && begin > end) 
			return SKIP_BODY;
		
		List<Enum<?>> enumsList = getEnumList();
		
		end = end == null ? enumsList.size() - 1 : end;
		tmpId = (tmpId == null ? "e_radio"  : tmpId) + "_" + RandomStringUtils.randomAlphanumeric(8);
		
		String html = super.generateHtmlNoID();
		StringBuilder sb = new StringBuilder();
		for(; begin <= end; begin++) {
			id = tmpId + "_" + begin;
			DetailEnum<?> cur = (DetailEnum<?>)enumsList.get(begin);
			String enumName = ((Enum<?>)cur).name();		//枚举实体的名字
			String radioValue = dataType.equals(DATA_TYPE_VALUE) ? cur.getValue().toString() : enumName;
			sb.append("<input type='radio' value='" + radioValue + "' ");
			sb.append(" id = '" + id + "' ")
				.append(html).append(checkoutTagAttr(begin, end + 1));
			if(selectedValue != null && (enumName.equals(selectedValue) || cur.getValue().toString().equals(selectedValue)))
				sb.append(" checked='checked' ");
			sb.append(" /> ");
			sb.append(" <label ");
			if(StringUtils.isNotBlank(labelClass))
				sb.append(" class='" + labelClass + "' ");
			if(StringUtils.isNotBlank(labelStyle))
				sb.append(" style='" + labelStyle + "' ");
			sb.append(" for='" + id + "' >" + cur.getDesc() + "</label> ")
				.append("&nbsp;&nbsp;");
		}
		try {
			pageContext.getOut().append(sb);
		} catch (IOException e) {
			LOG.error("RadioEnum Tag processing error.", e);
		}
		return EVAL_BODY_INCLUDE;
	}

	public void setLabelClass(String labelClass) {
		this.labelClass = labelClass;
	}

	public void setLabelStyle(String labelStyle) {
		this.labelStyle = labelStyle;
	}

	public void setId(String id) {
		this.id = this.tmpId = id;
	}
	
}
