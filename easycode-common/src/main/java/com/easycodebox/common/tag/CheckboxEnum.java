package com.easycodebox.common.tag;

import com.easycodebox.common.enums.DetailEnum;
import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.lang.Symbol;

import javax.servlet.jsp.JspException;
import java.io.IOException;
import java.util.List;

/**
 * @author WangXiaoJin
 *
 */
public class CheckboxEnum extends EnumGroupTag {
	
	private String labelClass;
	private String labelStyle;
	
	@Override
	protected void init() {
		labelClass = labelStyle = null;
		super.init();
	}
	
	@Override
	public int doStartTag() throws JspException {
		if(begin != null && end != null && begin > end) 
			return SKIP_BODY;
		
		List<Enum<?>> enumsList = getEnumList();
		
		end = end == null ? enumsList.size() - 1 : end;
		
		String html = super.generateHtmlNoID(),
				pattern = "<label {} > <input type='checkbox' value='{}' {} /> {} </label> ",
				labelAttr = Symbol.EMPTY;
		StringBuilder all = new StringBuilder();
		//组装labelAttr
		if(Strings.isNotBlank(labelClass))
			labelAttr += " class='" + labelClass + "' ";
		if(Strings.isNotBlank(labelStyle))
			labelAttr += " style='" + labelStyle + "' ";
		
		for(; begin <= end; begin++) {
			String inputAttr = Symbol.EMPTY;
			DetailEnum<?> cur = (DetailEnum<?>)enumsList.get(begin);
			String enumName = ((Enum<?>)cur).name();		//枚举实体的名字
			String checkValue = dataType.equals(DATA_TYPE_VALUE) ? cur.getValue().toString() : enumName;
			if (Strings.isNotBlank(id)) {
				inputAttr += Strings.format(" id = '{0}_{1}' ", id, begin);
			}
			inputAttr += html + checkoutTagAttr(begin, end + 1);
			if(selectedValue != null && (enumName.equals(selectedValue) || cur.getValue().toString().equals(selectedValue)))
				inputAttr += " checked='checked' ";
			all.append(Strings.format(pattern, labelAttr, checkValue, inputAttr, cur.getDesc()));
		}
		try {
			pageContext.getOut().append(all);
		} catch (IOException e) {
			log.error("RadioEnum Tag processing error.", e);
		}
		return EVAL_BODY_INCLUDE;
	}

	public void setLabelClass(String labelClass) {
		this.labelClass = labelClass;
	}

	public void setLabelStyle(String labelStyle) {
		this.labelStyle = labelStyle;
	}

}
