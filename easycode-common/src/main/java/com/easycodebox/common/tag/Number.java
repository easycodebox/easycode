package com.easycodebox.common.tag;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.servlet.jsp.JspException;


/**
 * @author WangXiaoJin
 * 
 */
public class Number extends TagExt {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * pattern string,currency, integer, number or percent
	 */
	private String pattern;
	private Object value;
	private Boolean groupUsed;
	private Integer groupSize;
    private Integer maxFractionDigits;
    private Integer maxIntDigits;
    private Integer minFractionDigits;
    private Integer minIntDigits;
    private Boolean onlyInt;
    private RoundingMode roundingMode;
	
	@Override
	protected void init() {
		pattern = "0.##";
		value = null;
		groupUsed = false;
		groupSize = maxFractionDigits = maxIntDigits = 
				minFractionDigits = minIntDigits = null;
		onlyInt = null;
		roundingMode = RoundingMode.FLOOR;
		super.init();
	}
	
	@Override
	public int doStartTag() throws JspException {
		NumberFormat format = getNumberFormat();
		if(value == null)
			value = 0;
		else if(!java.lang.Number.class.isAssignableFrom(value.getClass()))
			value = new BigDecimal(value.toString());
		String data = format.format(value);
		try {
			if(data != null)
				pageContext.getOut().append(data);
		} catch (IOException e) {
			LOG.error("IOException.", e);
		}
		return super.doStartTag();
	}

	private NumberFormat getNumberFormat() {
		NumberFormat format = null;
		if(pattern.equalsIgnoreCase("integer")) {
			format = NumberFormat.getIntegerInstance(Locale.getDefault());
		}else if(pattern.equalsIgnoreCase("number")) {
			format = NumberFormat.getNumberInstance(Locale.getDefault());
		}else if(pattern.equalsIgnoreCase("percent")) {
			format = NumberFormat.getPercentInstance(Locale.getDefault());
		}else if(pattern.equalsIgnoreCase("currency")) {
			format = NumberFormat.getCurrencyInstance(Locale.getDefault());
		}else {
			format = new DecimalFormat(pattern);
			if(groupSize != null)
				((DecimalFormat)format).setGroupingSize(groupSize);
		}
		if (groupUsed != null) {
            format.setGroupingUsed(groupUsed);
        }
        if (maxFractionDigits != null) {
            format.setMaximumFractionDigits(maxFractionDigits);
        }
        if (maxIntDigits != null) {
            format.setMaximumIntegerDigits(maxIntDigits);
        }
        if (minFractionDigits != null) {
            format.setMinimumFractionDigits(minFractionDigits);
        }
        if (minIntDigits != null) {
            format.setMinimumIntegerDigits(minIntDigits);
        }
        if (onlyInt != null) {
            format.setParseIntegerOnly(onlyInt);
        }
        if(roundingMode != null) {
        	format.setRoundingMode(roundingMode);
        }
		return format;
	}

	public void setPattern(String pattern) {
		this.pattern = obtainVal(pattern, String.class);
	}

	public void setValue(Object value) {
		this.value = obtainVal(value, Object.class);
	}

	public void setGroupUsed(Object groupUsed) {
		this.groupUsed = obtainVal(groupUsed, Boolean.class);
	}

	public void setGroupSize(Object groupSize) {
		this.groupSize = obtainVal(groupSize, Integer.class);
	}

	public void setMaxFractionDigits(Object maxFractionDigits) {
		this.maxFractionDigits = obtainVal(maxFractionDigits, Integer.class);
	}

	public void setMaxIntDigits(Object maxIntDigits) {
		this.maxIntDigits = obtainVal(maxIntDigits, Integer.class);
	}

	public void setMinFractionDigits(Object minFractionDigits) {
		this.minFractionDigits = obtainVal(minFractionDigits, Integer.class);
	}

	public void setMinIntDigits(Object minIntDigits) {
		this.minIntDigits = obtainVal(minIntDigits, Integer.class);
	}

	public void setOnlyInt(Object onlyInt) {
		this.onlyInt = obtainVal(onlyInt, Boolean.class);
	}

	public void setRoundingMode(String roundingMode) {
		this.roundingMode = RoundingMode.valueOf(roundingMode);
	}

}
