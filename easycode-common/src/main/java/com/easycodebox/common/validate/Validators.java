package com.easycodebox.common.validate;

import com.easycodebox.common.enums.entity.LogLevel;
import com.easycodebox.common.error.CodeMsg;
import com.easycodebox.common.error.CodeMsg.Code;
import com.easycodebox.common.error.ErrorContext;
import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.lang.Symbol;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author WangXiaoJin
 * 
 */
public class Validators implements Serializable {
	
	private transient Object data;
	/**
	 * 标识 ，用来替换提示信息中的{0}占位符
	 */
	private transient String identify;
	
	/**
	 * 当校验失败后抛出异常
	 */
	private transient boolean throwError;
	
	/**
	 * verifyEnd = true 指明当验证失败后，还会继续往下验证（但throwError = true 时除外）
	 */
	private transient boolean verifyEnd = false;
	/**
	 * 验证是否有效
	 */
	private Boolean valid;
	/**
	 * 提示信息
	 */
	private String msg;
	
	/**
	 * 默认当验证失败后抛出异常
	 */
	public static Validators instance(Object data) {
		return instance(null, data, true);
	}
	
	/**
	 * 
	 * @param data
	 * @param throwError	值为true，当验证失败后抛出异常 
	 * @return
	 */
	public static Validators instance(Object data, boolean throwError) {
		return instance(null, data, throwError);
	}
	
	/**
	 * 默认当验证失败后抛出异常
	 * @param identify	验证的对象名，替换异常信息中的{0}占位符用。自定义msg时，不需要传
	 * @param data
	 * @return
	 */
	public static Validators instance(String identify, Object data) {
		return instance(identify, data, true);
	}
	
	/**
	 * 
	 * @param identify	验证的对象名，替换异常信息中的{0}占位符用。自定义msg时，不需要传
	 * @param data
	 * @param throwError	值为true，当验证失败后抛出异常 
	 * @return
	 */
	public static Validators instance(String identify, Object data, boolean throwError) {
		return new Validators(identify, data, throwError);
	}
	
	private Validators(String identify, Object data, boolean throwError) {
		super();
		this.identify = identify;
		this.data = data;
		this.throwError = throwError;
	}
	
	/**
	 * 判断是否已经验证失败
	 * @return
	 */
	private boolean invalid() {
		return valid != null && !valid;
	}
	/**
	 * 判断是否跳出验证
	 * @return
	 */
	private boolean jumpValidate() {
		return invalid() && !verifyEnd;
	}
	/**
	 * 重置错误信息
	 * @param reset
	 * @param msg
	 * @return
	 */
	private String resetMsg(String reset, String... msg) {
		if(msg == null || msg.length == 0)
			return reset;
		else
			return msg[0];
	}
	
	/**
	 * 设置验证后的结果
	 * @param valid
	 * @param msg
	 * @return
	 */
	private Validators result(boolean valid, String msg) {
		if(this.valid == null || this.valid)
			this.valid = valid;
		//验证失败时才会设置错误信息
		if(!this.valid && msg != null && Strings.isNotBlank(msg)) {
			if(Strings.isNotBlank(this.msg))
				this.msg += msg + Symbol.SPACE;
			else
				this.msg = msg + Symbol.SPACE;
		}
		if(throwError && !valid)
			throw ErrorContext.instance(Code.FAIL_CODE, this.msg).logLevel(LogLevel.WARN);
		return this;
	}
	
	/**
	 * 通过传入验证器来验证
	 * @param validator
	 * @return
	 */
	public Validators validate(Validator validator, String... msg) {
		if(!jumpValidate()) {
			result(validator.validate(data), resetMsg(null, msg));
		}
		return this;
	}
	
	/**
	 * 正则表达式验证
	 * @param regex
	 * @return
	 */
	public Validators regex(Regex regex, String... msg) {
		if(!jumpValidate()) {
			boolean va = false;
			if(data != null && regex != null) {
				Pattern p = Pattern.compile(regex.getRegex());
		        Matcher m = p.matcher(data.toString());
				if(m.find())
					va = true;
			}
			result(va, resetMsg(regex.getMsg(), msg));
		}
		return this;
	}
	
	/**
	 * 正则表达式验证
	 * @param regex
	 * @return
	 */
	public Validators regex(Regex regex, int flags, String... msg) {
		if(!jumpValidate()) {
			boolean va;
			if(data == null || regex == null) 
				va = false;
			else {
				Pattern p = Pattern.compile(regex.getRegex(), flags);
				Matcher m = p.matcher(data.toString());
				va = m.matches();
			}
			result(va, resetMsg(regex.getMsg(), msg));
		}
		return this;
	}
	
	/**
	 * 正则表达式验证
	 * @param regex
	 * @return
	 */
	public Validators regex(String regex, String... msg) {
		if(!jumpValidate()) {
			boolean va;
			if(data == null || regex == null) 
				va = false;
			else {
				va = Pattern.matches(regex, data.toString());
			}
			result(va, resetMsg(null, msg));
		}
		return this;
	}
	
	/**
	 * 正则表达式验证
	 * @param regex
	 * @return
	 */
	public Validators regex(String regex, int flags, String... msg) {
		if(!jumpValidate()) {
			boolean va;
			if(data == null || regex == null) 
				va = false;
			else {
				Pattern p = Pattern.compile(regex, flags);
				Matcher m = p.matcher(data.toString());
				va = m.matches();
			}
			result(va, resetMsg(null, msg));
		}
		return this;
	}
	
	/**
	 * 判断是否相等
	 * @param obj
	 * @return
	 */
	public Validators equalTo(Object obj, String... msg) {
		if(!jumpValidate()) {
			boolean va;
			if(data == null && obj == null) 
				va = true;
			else if(data == null && obj != null)
				va = false;
			else 
				va = data.equals(obj);
			result(va, resetMsg(null, msg));
		}
		return this;
	}
	
	/**
	 * 判断是否不等
	 * @param obj
	 * @return
	 */
	public Validators differs(Object obj, String... msg) {
		if(!jumpValidate()) {
			boolean va;
			if(data == null && obj == null) 
				va = false;
			else if(data == null && obj != null)
				va = true;
			else 
				va = !data.equals(obj);
			result(va, resetMsg(null, msg));
		}
		return this;
	}
	
	/**
	 * 判断是否为NULL
	 * @return
	 */
	public Validators nulls(String... msg) {
		if(!jumpValidate()) {
			result(data == null, resetMsg("{0}只能为NULL", msg));
		}
		return this;
	}
	
	/**
	 * 判断是否不等于NULL
	 * @return
	 */
	public Validators notNull(String... msg) {
		if(!jumpValidate()) {
			result(data != null, resetMsg("{0}不能为NULL", msg));
		}
		return this;
	}
	
	/**
	 * 判断数组、集合内部不能出现null值
	 * @return
	 */
	public Validators notNullInside(String... msg) {
		if(!jumpValidate()) {
			boolean va = true;
			if(data == null) {
				va = false;
			}else {
				if(data.getClass().isArray()) {
					Object[] tmp = (Object[])data;
					for(Object o : tmp) {
						if(o == null) {
							va = false;
							break;
						}
					}
				}else if(data instanceof Collection<?>){
					Collection<?> tmp = (Collection<?>)data;
					Iterator<?> ite = tmp.iterator();
					while (ite.hasNext()) {
						if(ite.next() == null) {
							va = false;
							break;
						}
					}
				}else if(data instanceof Map<?,?>) {
					Map<?,?> tmp = (Map<?,?>)data;
					Collection<?> vals = tmp.values();
					Iterator<?> ite = vals.iterator();
					while (ite.hasNext()) {
						if(ite.next() == null) {
							va = false;
							break;
						}
					}
				}
			}
			result(va, resetMsg("{0}数组内部不能出现null值", msg));
		}
		return this;
	}
	
	/**
	 * 判断是否为空（包含NULL和空字符窜）
	 * @return
	 */
	public Validators empty(String... msg) {
		if(!jumpValidate()) {
			result(data == null || Strings.isBlank(data.toString()),
					resetMsg("{0}只能为空", msg));
		}
		return this;
	}
	
	/**
	 * 判断是否不等于空
	 * @return
	 */
	public Validators notEmpty(String... msg) {
		if(!jumpValidate()) {
			result(data != null && Strings.isNotBlank(data.toString()),
					resetMsg("{0}不能为空", msg));
		}
		return this;
	}
	
	/**
	 * 判断数组、集合内部值不等于空
	 * @return
	 */
	public Validators notEmptyInside(String... msg) {
		if(!jumpValidate()) {
			boolean va = true;
			if(data == null || Strings.isBlank(data.toString())) {
				va = false;
			}else {
				if(data.getClass().isArray()) {
					Object[] tmp = (Object[])data;
					for(Object o : tmp) {
						if(o == null || Strings.isBlank(o.toString())) {
							va = false;
							break;
						}
					}
				}else if(data instanceof Collection<?>){
					Collection<?> tmp = (Collection<?>)data;
					Iterator<?> ite = tmp.iterator();
					while (ite.hasNext()) {
						Object o = ite.next();
						if(o == null || Strings.isBlank(o.toString())) {
							va = false;
							break;
						}
					}
				}else if(data instanceof Map<?,?>) {
					Map<?,?> tmp = (Map<?,?>)data;
					Collection<?> vals = tmp.values();
					Iterator<?> ite = vals.iterator();
					while (ite.hasNext()) {
						Object o = ite.next();
						if(o == null || Strings.isBlank(o.toString())) {
							va = false;
							break;
						}
					}
				}
			}
			result(va, resetMsg("{0}数组内部不能出现空值", msg));
		}
		return this;
	}
	
	/**
	 * 判断长度，字符窜是字符的长度，数组、集合是数据个数
	 * @param min
	 * @param msg
	 * @return
	 */
	public Validators minLength(int min, String... msg) {
		if(!jumpValidate()) {
			boolean va = false;
			if(data != null) {
				if((data instanceof String 
						&& ((String)data).length() >= min)
						||
					(data.getClass().isArray()
							&& ((Object[])data).length >= min)
						||
					(data instanceof Collection<?>
							&& ((Collection<?>)data).size() >= min)
						||
					(data instanceof Map<?,?>
							&& ((Map<?,?>)data).size() >= min) ) {
					va = true;
				}
			}
			result(va, resetMsg("{0}长度不能小于" + min, msg));
		}
		return this;
	}
	
	/**
	 * 判断长度，字符窜是字符的长度，数组、集合是数据个数
	 * @param max
	 * @param msg
	 * @return
	 */
	public Validators maxLength(int max, String... msg) {
		if(!jumpValidate()) {
			boolean va = true;
			if(data != null) {
				if((data instanceof String 
						&& ((String)data).length() > max)
						||
					(data.getClass().isArray()
							&& ((Object[])data).length > max)
						||
					(data instanceof Collection<?>
							&& ((Collection<?>)data).size() > max)
						||
					(data instanceof Map<?,?>
							&& ((Map<?,?>)data).size() > max) ) {
					va = false;
				}
			}
			result(va, resetMsg("{0}长度不能大于" + max, msg));
		}
		return this;
	}
	
	/**
	 * 判断长度，字符窜是字符的长度，数组、集合是数据个数
	 * @param min
	 * @param max
	 * @param msg
	 * @return
	 */
	public Validators rangeLength(int min, int max, String... msg) {
		if(!jumpValidate()) {
			boolean va = false;
			if(data != null) {
				if((data instanceof String 
						&& ((String)data).length() >= min
						&& ((String)data).length() <= max)
						||
					(data.getClass().isArray()
							&& ((Object[])data).length >= min
							&& ((Object[])data).length <= max)
						||
					(data instanceof Collection<?>
							&& ((Collection<?>)data).size() >= min
							&& ((Collection<?>)data).size() <= max)
						||
					(data instanceof Map<?,?>
							&& ((Map<?,?>)data).size() >= min
							&& ((Map<?,?>)data).size() <= max) ) {
					va = true;
				}
			}
			result(va, resetMsg("{0}长度不能大于" + max, msg));
		}
		return this;
	}
	
	/**
	 * 判断数据的最小值，数据只能是数字或数字的字符窜
	 * @param min
	 * @param msg
	 * @return
	 */
	public Validators min(Number min, String... msg) {
		if(!jumpValidate()) {
			boolean va = false;
			if(data != null) {
				if(data instanceof String) {
					if(Pattern.matches(Regex.NUMBER.getRegex(), data.toString())) {
						double value = Double.valueOf(data.toString());
						va = value >= min.doubleValue();
					}
				}else if(data.getClass().isPrimitive()
						|| data instanceof Number) {
					double value = ((Number)data).doubleValue();
					va = value >= min.doubleValue();
				}
			}
			result(va, resetMsg("{0}数值不能小于" + min, msg));
		}
		return this;
	}
	
	/**
	 *  判断数据的最大值，数据只能是数字或数字的字符窜
	 * @param max
	 * @param msg
	 * @return
	 */
	public Validators max(Number max, String... msg) {
		if(!jumpValidate()) {
			boolean va = false;
			if(data != null) {
				if(data instanceof String) {
					if(Pattern.matches(Regex.NUMBER.getRegex(), data.toString())) {
						double value = Double.valueOf(data.toString());
						va = value <= max.doubleValue();
					}
				}else if(data.getClass().isPrimitive()
						|| data instanceof Number) {
					double value = ((Number)data).doubleValue();
					va = value <= max.doubleValue();
				}
			}
			result(va, resetMsg("{0}数值不能大于" + max, msg));
		}
		return this;
	}
	
	/**
	 * 判断数据的值区间，数据只能是数字或数字的字符窜
	 * @param min
	 * @param max
	 * @param msg
	 * @return
	 */
	public Validators between(Number min, Number max, String... msg) {
		if(!jumpValidate()) {
			boolean va = false;
			if(data != null) {
				if(data instanceof String) {
					if(Pattern.matches(Regex.NUMBER.getRegex(), data.toString())) {
						double value = Double.valueOf(data.toString());
						va = value >= min.doubleValue() && value <= max.doubleValue();
					}
				}else if(data.getClass().isPrimitive()
						|| data instanceof Number) {
					double value = ((Number)data).doubleValue();
					va = value >= min.doubleValue() && value <= max.doubleValue();
				}
			}
			result(va, resetMsg("{0}数值必须位于" + min +"-" + max + "之间", msg));
		}
		return this;
	}
	
	/**
	 * 判断是否是数字，int类型或字符窜数字
	 * @param msg
	 * @return
	 */
	public Validators digit(String... msg) {
		return regex(Regex.DIGIT, msg);
	}
	
	/**
	 * 判断中文
	 * @param msg
	 * @return
	 */
	public Validators chinese(String... msg) {
		return regex(Regex.CHINESE, msg);
	}
	
	/**
	 * 判断只要出现中文
	 * @param msg
	 * @return
	 */
	public Validators anyChinese(String... msg) {
		return regex(Regex.ANY_CHINESE, msg);
	}
	
	/**
	 * 判断邮件
	 * @param msg
	 * @return
	 */
	public Validators email(String... msg) {
		return regex(Regex.EMAIL, msg);
	}
	
	/**
	 * 判断URL
	 * @param msg
	 * @return
	 */
	public Validators url(String... msg) {
		return regex(Regex.URL, msg);
	}
	
	/**
	 * 字母、下划线和数字
	 * @param msg
	 * @return
	 */
	public Validators word(String... msg) {
		return regex(Regex.WORD, msg);
	}
	
	/**
	 * 中文、字母、下划线、左括号、右括号和数字
	 * @param msg
	 * @return
	 */
	public Validators wordZh(String... msg) {
		return regex(Regex.WORD_ZH, msg);
	}
	
	/**
	 * 手机号
	 * @param msg
	 * @return
	 */
	public Validators mobile(String... msg) {
		return regex(Regex.MOBILE, msg);
	}
	
	/**
	 * 身份证
	 * @param msg
	 * @return
	 */
	public Validators idcard(String... msg) {
		return regex(Regex.IDCARD, msg);
	}
	
	/**
	 * 固话
	 * @param msg
	 * @return
	 */
	public Validators tel(String... msg) {
		return regex(Regex.TEL, msg);
	}
	
	/**
	 * 浮点数 或 整数(包含正负数)
	 * @param msg
	 * @return
	 */
	public Validators number(String... msg) {
		return regex(Regex.NUMBER, msg);
	}
	
	public Boolean isValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	/**
	 * 获取原始错误信息，建议使用getFmtMsg方法
	 * @return
	 */
	public String getRawMsg() {
		return msg;
	}
	
	/**
	 * 获取格式化后的信息，即替换掉{0}这样的占位符
	 * @return
	 */
	public String getMsg() {
		return Strings.format(msg, identify);
	}
	
	public CodeMsg getError() {
		return (isValid() ? CodeMsg.SUC : CodeMsg.FAIL).msg(getMsg());
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isVerifyEnd() {
		return verifyEnd;
	}

	public Validators setVerifyEnd(boolean verifyEnd) {
		this.verifyEnd = verifyEnd;
		return this;
	}
	
	public boolean isThrowError() {
		return throwError;
	}

	public Validators setThrowError(boolean throwError) {
		this.throwError = throwError;
		return this;
	}

	public static void main(String[] args) {
		System.out.println(Pattern.matches("[12.]", "6"));
	}
	
}
