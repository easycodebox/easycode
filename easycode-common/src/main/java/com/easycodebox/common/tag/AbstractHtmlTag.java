package com.easycodebox.common.tag;

import java.util.Map;

import com.easycodebox.common.jackson.Jacksons;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.Symbol;

/**
 * @author WangXiaoJin
 *
 */
public abstract class AbstractHtmlTag extends TagExt {

	private static final long serialVersionUID = 5416226129998370809L;
	
	protected String name;
	protected String title;
	protected String cssClass;
	protected String cssStyle;
	protected String disabled;
	
	protected String tagAttr;
	protected Map<String, Object> tagAttrMap = null;
	private static final String ALL_KEY = "all";
	private static final String FIRST_KEY = "first";
	private static final String LAST_KEY = "last";
	
	protected String onblur;
	protected String onchange;
	protected String onclick;
	protected String ondblclick;
	protected String onfocus;
	protected String onkeydown;
	protected String onkeypress;
	protected String onkeyup;
	protected String onmousedown;
	protected String onmousemove;
	protected String onmouseout;
	protected String onmouseover;
	protected String onmouseup;
	protected String onselect;
	
	@Override
	protected void init() {
		name = title = cssClass = cssStyle = disabled =
			tagAttr = onblur = onchange = onclick =
			ondblclick = onfocus = onkeydown = onkeypress =
			onkeyup = onmousedown = onmousemove = onmouseout =
			onmouseover = onmouseup = onselect = null;
		super.init();
	}



	/**
	 * 生成HTML格式文本
	 * @return
	 */
	protected String generateHtml() {
		StringBuilder sb = new StringBuilder();
		appendIfNotNull(sb, "id", id);
		sb.append(generateHtmlNoID());
		return sb.toString();
	}
	
	/**
	 * 生成HTML格式文本不含Id属性
	 * @return
	 */
	protected String generateHtmlNoID() {
		StringBuilder sb = new StringBuilder();
		appendIfNotNull(sb, "name", name);
		appendIfNotNull(sb, "title", title);
		appendIfNotNull(sb, "class", cssClass);
		appendIfNotNull(sb, "style", cssStyle);
		appendIfNotNull(sb, "disabled", disabled);
		appendIfNotNull(sb, "onblur", onblur);
		appendIfNotNull(sb, "onchange", onchange);
		appendIfNotNull(sb, "onclick", onclick);
		appendIfNotNull(sb, "ondblclick", ondblclick);
		appendIfNotNull(sb, "onfocus", onfocus);
		appendIfNotNull(sb, "onkeydown", onkeydown);
		appendIfNotNull(sb, "onkeypress", onkeypress);
		appendIfNotNull(sb, "onkeyup", onkeyup);
		appendIfNotNull(sb, "onmousedown", onmousedown);
		appendIfNotNull(sb, "onmousemove", onmousemove);
		appendIfNotNull(sb, "onmouseout", onmouseout);
		appendIfNotNull(sb, "onmouseover", onmouseover);
		appendIfNotNull(sb, "onmouseup", onmouseup);
		appendIfNotNull(sb, "onselect", onselect);
		if(tagAttrMap == null) {
			if(StringUtils.isNotBlank(tagAttr))
				sb.append(tagAttr).append(Symbol.SPACE);
		}else if(tagAttrMap.containsKey(ALL_KEY)) {
			sb.append(tagAttrMap.get(ALL_KEY)).append(Symbol.SPACE);
		}
		return sb.toString();
	}
	
	/**
	 * index 从0开始
	 * @param index
	 * @param size
	 * @return
	 */
	protected String checkoutTagAttr(int index, int size) {
		if(tagAttrMap == null)
			return Symbol.EMPTY;
		if(tagAttrMap.containsKey(String.valueOf(index))) {
			return tagAttrMap.get(String.valueOf(index)).toString();
		}else if(index == 0 && tagAttrMap.containsKey(FIRST_KEY)) {
			return tagAttrMap.get(FIRST_KEY).toString();
		}else if(index == size - 1 && tagAttrMap.containsKey(LAST_KEY)) {
			return tagAttrMap.get(LAST_KEY).toString();
		}else {
			return Symbol.EMPTY;
		}
	}
	
	private StringBuilder appendIfNotNull(StringBuilder sb, String name, String val) {
		if(name != null && val != null)
			sb.append(" " + name + "='" + val + "' ");
		return sb;
	}
	
	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public String getCssStyle() {
		return cssStyle;
	}

	public void setCssStyle(String cssStyle) {
		this.cssStyle = cssStyle == null ? null : cssStyle.replaceAll("'", "\"");
	}

	public String getDisabled() {
		return disabled;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = obtainVal(title, String.class);
	}

	public String getOnblur() {
		return onblur;
	}

	public void setOnblur(String onblur) {
		this.onblur = onblur;
	}

	public String getOnchange() {
		return onchange;
	}

	public void setOnchange(String onchange) {
		this.onchange = onchange;
	}

	public String getOnclick() {
		return onclick;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public String getOndblclick() {
		return ondblclick;
	}

	public void setOndblclick(String ondblclick) {
		this.ondblclick = ondblclick;
	}

	public String getOnfocus() {
		return onfocus;
	}

	public void setOnfocus(String onfocus) {
		this.onfocus = onfocus;
	}

	public String getOnkeydown() {
		return onkeydown;
	}

	public void setOnkeydown(String onkeydown) {
		this.onkeydown = onkeydown;
	}

	public String getOnkeypress() {
		return onkeypress;
	}

	public void setOnkeypress(String onkeypress) {
		this.onkeypress = onkeypress;
	}

	public String getOnkeyup() {
		return onkeyup;
	}

	public void setOnkeyup(String onkeyup) {
		this.onkeyup = onkeyup;
	}

	public String getOnmousedown() {
		return onmousedown;
	}

	public void setOnmousedown(String onmousedown) {
		this.onmousedown = onmousedown;
	}

	public String getOnmousemove() {
		return onmousemove;
	}

	public void setOnmousemove(String onmousemove) {
		this.onmousemove = onmousemove;
	}

	public String getOnmouseout() {
		return onmouseout;
	}

	public void setOnmouseout(String onmouseout) {
		this.onmouseout = onmouseout;
	}

	public String getOnmouseover() {
		return onmouseover;
	}

	public void setOnmouseover(String onmouseover) {
		this.onmouseover = onmouseover;
	}

	public String getOnmouseup() {
		return onmouseup;
	}

	public void setOnmouseup(String onmouseup) {
		this.onmouseup = onmouseup;
	}

	public String getOnselect() {
		return onselect;
	}

	public void setOnselect(String onselect) {
		this.onselect = onselect;
	}

	public String getTagAttr() {
		return tagAttr;
	}

	@SuppressWarnings("unchecked")
	public void setTagAttr(String tagAttr) {
		if(tagAttr != null) {
			tagAttr = tagAttr.trim();
			if(tagAttr.startsWith(Symbol.L_BRACE)) {
				try {
					tagAttrMap = Jacksons.COMMUNICATE.toBean(tagAttr, Map.class);
				} catch (Exception e) {
					LOG.warn("Parse json string error.【{0}】", e, tagAttr);
				}
			}
			this.tagAttr = tagAttr;
		}
	}

}
