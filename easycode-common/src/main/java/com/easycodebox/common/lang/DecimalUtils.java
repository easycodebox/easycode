package com.easycodebox.common.lang;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @author WangXiaoJin
 * 
 */
public class DecimalUtils {

	/**
	 * 默认小数后面的0不删除
	 * @param data 为null返回空字符窜
	 * @param digit 小数的位数
	 * @return
	 */
	public static String fmt(Object data, int digit) {
		return fmt(data, digit, false);
	}

	/**
	 * @param data 为null返回空字符窜
	 * @param digit 小数的位数
	 * @param removeZero 是否删除小数后面的0
	 * @return
	 */
	public static String fmt(Object data, int digit, boolean removeZero) {
		if(data == null) return Symbol.EMPTY;
		DecimalFormat df = obatainDecimalFormat(digit, removeZero);
		if(!(data instanceof Number))
			data = new BigDecimal(data.toString());
		return df.format(data);
	}
	
	/**
	 * data x mul (Multiply)
	 * 计算两个数据相乘，返回小数
	 * @param mul,data 为null返回空字符窜
	 * @param digit 小数的位数
	 * @param removeZero 是否删除小数后面的0
	 * @return
	 */
	public static String fmtMul(Object data, Object mul, int digit, boolean removeZero) {
		if(data == null || mul == null) return Symbol.EMPTY;
		DecimalFormat df = obatainDecimalFormat(digit, removeZero);
		return df.format(new BigDecimal(data.toString())
					.multiply(new BigDecimal(mul.toString())));
	}
	
	/**
	 * data/divide (Divide)
	 * 计算两个数据相除，返回小数
	 * @param data 为null返回空字符窜
	 * @param digit 小数的位数
	 * @param removeZero 是否删除小数后面的0
	 * @return
	 */
	public static String fmtDiv(Object data, Object divide, int digit, boolean removeZero) {
		if(data == null || divide == null) return Symbol.EMPTY;
		DecimalFormat df = obatainDecimalFormat(digit, removeZero);
		return df.format(new BigDecimal(data.toString())
					.divide(new BigDecimal(divide.toString())));
	}
	
	/**
	 * data+add
	 * 计算两个数据相加，返回小数
	 * @param data 为null返回空字符窜
	 * @param digit 小数的位数
	 * @param removeZero 是否删除小数后面的0
	 * @return
	 */
	public static String fmtAdd(Object data, Object add, int digit, boolean removeZero) {
		if(data == null || add == null) return Symbol.EMPTY;
		DecimalFormat df = obatainDecimalFormat(digit, removeZero);
		return df.format(new BigDecimal(data.toString())
					.add(new BigDecimal(add.toString())));
	}
	
	/**
	 * data-add (Subtract)
	 * 计算两个数据相减，返回小数
	 * @param data 为null返回空字符窜
	 * @param digit 小数的位数
	 * @param removeZero 是否删除小数后面的0
	 * @return
	 */
	public static String fmtSub(Object data, Object subtract, int digit, boolean removeZero) {
		if(data == null || subtract == null) return Symbol.EMPTY;
		DecimalFormat df = obatainDecimalFormat(digit, removeZero);
		return df.format(new BigDecimal(data.toString())
					.subtract(new BigDecimal(subtract.toString())));
	}
	
	private static DecimalFormat obatainDecimalFormat(int digit, boolean removeZero) {
		String decimalFmt;
		if(removeZero)
			decimalFmt = org.apache.commons.lang.StringUtils.repeat("#", digit);
		else
			decimalFmt = org.apache.commons.lang.StringUtils.repeat("0", digit);
		if(decimalFmt.length() > 0)
			decimalFmt = Symbol.PERIOD + decimalFmt;
		DecimalFormat df = new DecimalFormat("0" + decimalFmt);
		df.setRoundingMode(RoundingMode.HALF_UP);
		return df;
	}
	
	/**
	 * 设置保留几位小数
	 * @param dec
	 * @param scale
	 * @return
	 */
	public static BigDecimal scale(BigDecimal dec, int scale) {
		if(dec == null) return null;
		return dec.setScale(scale, BigDecimal.ROUND_HALF_UP);
	}
}
