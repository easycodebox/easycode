package com.easycodebox.common.tag;

import java.io.IOException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.jsp.JspException;

import com.easycodebox.common.lang.DataConvert;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.TextUtils;
import com.easycodebox.common.validate.Regex;

/**
 * 
 * 建议使用CSS3的text-overflow功能
 * @author WangXiaoJin
 *
 */
public class TextCut extends TagExt {
	
	private static final long serialVersionUID = 1L;
	
	private Object value;
	private Integer remainNum;
	private String symbol;
	private String arraySeparator;
	private String wrap;
	private String key;	//当value为Object时需要指定显示哪个属性的值
	private boolean escape;
	private boolean cutPattern;
	
	@Override
	protected void init() {
		value = null;
		remainNum = 50;
		symbol = "...";
		arraySeparator = " ";
		wrap = key = null;
		escape = cutPattern = false;
		super.init();
	}
	
	@Override
	public int doStartTag() throws JspException {
		if(value == null)
			return SKIP_BODY;
		
		String valStr = null;
		if(value.getClass().isArray()
				|| value instanceof Collection) {
			valStr = DataConvert.arrayCollection2Str(value, arraySeparator, null, null, true, key);
		}else {
			valStr = DataConvert.Object2String(value, true, key);
		}

		try {
			if(cutPattern) {
				Pattern p = Pattern.compile(Regex.HTML);
				Matcher m = p.matcher(valStr);
				StringBuffer stringbuffer = new StringBuffer();
				while (m.find()) {
					m.appendReplacement(stringbuffer, "");
				}
				m.appendTail(stringbuffer);
				valStr = stringbuffer.toString();
			}
			
			String remainValue = valStr = StringUtils.trim(valStr);
			
			if(remainValue.length() > remainNum*2)
				remainValue = remainValue.substring(0, remainNum*2-1);
			String newVal = remainValue.replaceAll("([^\\x00-\\xff])", "$1⊙");
			if(newVal.length() <= remainNum) {
				remainValue = prepare(remainValue);
			} else {
				remainValue = prepare(newVal.substring(0,remainNum).replaceAll("⊙", "")) + symbol;
			}
			
			if(wrap != null) {
				pageContext.getOut().append(
						wrap.replaceAll("\\{\\s*value\\s*\\}", valStr)
						.replaceAll("\\{\\s*remainValue\\s*\\}", remainValue)
				);
			}else
				pageContext.getOut().append(remainValue);
		} catch (IOException e) {
			log.error("TextCut Tag processing error.", e);
		}
		return EVAL_BODY_INCLUDE;
	}
	
	private String prepare(String value) {
        if (escape) {
            return TextUtils.htmlEncode(value);
        } else {
            return value;
        }
    }

	public void setValue(Object value) {
		this.value = obtainVal(value, Object.class);
	}

	public void setRemainNum(Object remainNum) {
		this.remainNum = obtainVal(remainNum, Integer.class);
	}

	public void setSymbol(String symbol) {
		this.symbol = obtainVal(symbol, String.class);
	}

	public void setArraySeparator(String arraySeparator) {
		this.arraySeparator = arraySeparator;
	}

	public void setWrap(String wrap) {
		this.wrap = obtainVal(wrap, String.class);
	}

	public void setKey(String key) {
		this.key = obtainVal(key, String.class);
	}

	public void setEscape(Object escape) {
		this.escape = obtainVal(escape, Boolean.class);
	}

	public void setCutPattern(Object cutPattern) {
		this.cutPattern = obtainVal(cutPattern, Boolean.class);
	}
	
}
