package com.easycodebox.common.lang;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;

import com.easycodebox.common.enums.DetailEnum;
import com.easycodebox.common.enums.EnumClassFactory;
import com.easycodebox.common.enums.entity.DataType;
import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.jackson.Jacksons;
import com.easycodebox.common.lang.StringToken.StringFormatToken;
import com.easycodebox.common.lang.reflect.ClassUtils;
import com.easycodebox.common.lang.reflect.FieldUtils;
import com.easycodebox.common.validate.Regex;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author WangXiaoJin
 * 
 */
public class StringUtils extends org.apache.commons.lang.StringUtils {
	
	//private static final Logger log = LoggerFactory.getLogger(StringUtils.class);
	
	public static String string2unicode(String str) {
		if(str == null) return null;
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch < 0x10) {
				sb.append("\\u000" + Integer.toHexString(ch));
	        } else if (ch < 0x100) {
	        	sb.append("\\u00" + Integer.toHexString(ch));
	        } else if (ch < 0x1000) {
	        	sb.append("\\u0" + Integer.toHexString(ch));
	        }else
	        	sb.append("\\u" + Integer.toHexString(ch));
		}
        return sb.toString();
    }
	
	
	public static String unicode2string(String str) {
		if(str == null) return null;
		String[] strs = str.toLowerCase().split("\\\\u");
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < strs.length; i++) {
			if(strs[i].trim().equals("")) continue;
			char ch = (char)Integer.parseInt(strs[i].trim(),16);
			sb.append(ch);
		}
        return sb.toString();
    }
	
	/**
	 * 定位参数数组的索引值格式：{} - 花括号里面不包含任何东西
	 */
	public static final int EMPTY_INDEX_MODEL 	= 0b0001;
	/**
	 * 定位参数数组的索引值格式：{0} - 花括号里面为数字，即为对应的索引值
	 */
	public static final int NUM_INDEX_MODEL 	= 0b0010;
	/**
	 * 定位参数值的格式：{key} - 花括号里面为key，依次遍历参数数组，找出Map和VO对象，取对应key的值
	 */
	public static final int KEY_VALUE_MODEL 	= 0b0100;
	
	/**
	 * 默认使用 EMPTY_INDEX_MODEL | NUM_INDEX_MODEL | KEY_VALUE_MODEL混合模式。defaultVal = "null" <br>
	 * 注：支持嵌套格式（例：aaa{name{msg}}aaa）
	 * @param str
	 * @param args 可以传map或者vo对象
	 * @return
	 */
	public static String format(String str, Object... args) {
		return formatMix(str, EMPTY_INDEX_MODEL | NUM_INDEX_MODEL | KEY_VALUE_MODEL, "null", args);
	}
	
	/**
	 * 多种模式并存时，优先级：KEY_VALUE_MODEL > NUM_INDEX_MODEL <br>
	 * 注：支持嵌套格式（例：aaa{name{msg}}aaa）
	 * @param str
	 * @param model	三种值得组合：EMPTY_INDEX_MODEL | NUM_INDEX_MODEL | KEY_VALUE_MODEL 具体解释请看常量
	 * @param defaultVal	当没有对应的参数或参数为null时，默认代替的值。defaultVal:null - 不做任何值替换；defaultVal:"" - 空字符窜代替；defaultVal:"null" - "null"字符窜代替
	 * @param args
	 * @return
	 */
	public static String formatMix(String str, int model, String defaultVal, Object... args) {
		if (isBlank(str)) return str;
		if (args == null) args = ArrayUtils.EMPTY_OBJECT_ARRAY;
		
		StringFormatToken token = new StringFormatToken(str, true);
		String key = null;
		List<Object> maps = null;
		int index = -1;
		
		if ((model & KEY_VALUE_MODEL) > 0) {
			for(Object arg : args) {
				if(arg == null)
					continue;
				Class<?> clazz = arg.getClass();
				if(!clazz.isPrimitive()
						&& !CharSequence.class.isAssignableFrom(clazz)
						&& !Number.class.isAssignableFrom(clazz) 
						&& !Boolean.class.isAssignableFrom(clazz)
						&& !Character.class.isAssignableFrom(clazz)
						&& !Date.class.isAssignableFrom(clazz)
						&& !clazz.isArray()
						&& !Collection.class.isAssignableFrom(clazz)) {
					maps = new ArrayList<>(4);
					maps.add(arg);
				}
			}
		}
		
		while((key = token.nextKey()) != null) {
			String val = null;
			if(maps != null && (model & KEY_VALUE_MODEL) > 0 && isNotBlank(key)) {
				for(Object map : maps) {
					if (Map.class.isAssignableFrom(map.getClass())) {
						Map<?, ?> tmpMap = (Map<?, ?>)map;
						if(tmpMap.containsKey(key)) {
							Object obj = tmpMap.get(key);
							val = obj == null ? null : obj.toString();
							//val为null时则继续遍历，直到碰到有值为止
							if (val != null) {
								break;
							}
						}
					} else {
						try {
							Object obj = PropertyUtils.getProperty(map, key);
							val = obj == null ? null : obj.toString();
							//val为null时则继续遍历，直到碰到有值为止
							if (val != null) {
								break;
							}
						} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
							
						}
					}
				}
			} 
			if (val == null && (model & EMPTY_INDEX_MODEL) > 0 && isBlank(key)) {
				index++;
				val = args.length > index ? args[index] == null ? null : args[index].toString() : null;
			} 
			if (val == null && (model & NUM_INDEX_MODEL) > 0 && Pattern.matches(Regex.DIGIT.getRegex(), key)) {
				int intKey = Integer.parseInt(key);
				val = args.length > intKey ? args[intKey] == null ? null : args[intKey].toString() : null;
			}
			token.insertBack(val == null ? defaultVal == null ? token.getOpen() + key + token.getClose() : defaultVal : val);
		}
		return token.getAssemble();
    }
	
	private static final String JSON_VALUE = "-JV";
	private static final String ENUM_VALUE = "-ENUM-";
	/**
	 * 通过表达式替换值
	 * 原始字符窜 name=${user.name-JV}&shop={name: ${name}, id: ${id}}&list1=[shopList /]&list2=[shopList][/shopList]&list3=[shopList]{name: ${shopname}}[/shopList]
	 * 可以用"."作为嵌套对象使用，“-JV”指的是此值为JSON格式的值
	 * @param paramsStr 
	 * @param enums	暴露接口方提供的枚举常量值，格式如：{"GENDER": [{"name": "MALE", "value": 0, "describe": "男"}, {"name": "FEMALE", "value": 1, "describe": "女"}]}  
	 * （name 自己项目枚举名字，value为第三方需要的值）
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	public static String formatExp2String(String paramsStr, Object data, DataType dataType, Map<String, List<Map<String, Object>>> enums) 
			throws JsonGenerationException, JsonMappingException, IOException {
		if(isBlank(paramsStr) || data == null) return paramsStr;
		Pattern p = Pattern.compile("([\\w\\.\"-]+)(\\s*[=:]\\s*)((\\[\\s*([\\w\\.-]+)\\s*/\\s*\\])|(\\[\\s*([\\w\\.-]+)\\s*\\]([\\s\\S]*?)\\[\\s*/\\s*\\7\\s*\\])|(\\$\\{\\s*([\\w\\.-]+)\\s*\\}))");
		Matcher matcher = p.matcher(paramsStr);
		boolean result = matcher.find();
		if (result) {
			StringBuffer sb = new StringBuffer();
			do {
				String keyName = matcher.group(1),
						symbol = matcher.group(2),
						//枚举常量值
						enumClassName = null;
				boolean isJson = dataType == DataType.JSON ? true : false;
				if(isNotBlank(matcher.group(4))) {
					String key = matcher.group(5);
					if(dataType == DataType.MIX) {
						int jsonIndex = matcher.group(5).indexOf(JSON_VALUE);
						key = jsonIndex > -1 ? key.substring(0, jsonIndex) : key;
						isJson = true;
					}
					if(key.indexOf(ENUM_VALUE) > -1) {
						String[] frags = key.split(ENUM_VALUE);
						key = frags[0];
						enumClassName = frags[1];
					}
					Object val = getMappingValue(data, key, enumClassName, enums);
					
					if(val == null 
							|| val instanceof Collection<?>
							|| val.getClass().isArray())
						val = convertParam(keyName, symbol, val, isJson);
					else 
						val = convertParam(keyName, symbol, new Object[]{val}, isJson);
					matcher.appendReplacement(sb, val.toString());
					
				}else if ( isNotBlank(matcher.group(6)) && isBlank(matcher.group(8))) {
					
					String key = matcher.group(7);
					if(dataType == DataType.MIX) {
						int jsonIndex = matcher.group(7).indexOf(JSON_VALUE);
						key = jsonIndex > -1 ? key.substring(0, jsonIndex) : key;
						isJson = true;
					}
					if(key.indexOf(ENUM_VALUE) > -1) {
						String[] frags = key.split(ENUM_VALUE);
						key = frags[0];
						enumClassName = frags[1];
					}
					Object val = getMappingValue(data, key, enumClassName, enums);
					
					if(val == null 
							|| val instanceof Collection<?>
							|| val.getClass().isArray())
						val = convertParam(keyName, symbol, val, isJson);
					else 
						val = convertParam(keyName, symbol, new Object[]{val}, isJson);
					matcher.appendReplacement(sb, val.toString());
					
				}else if(isNotBlank(matcher.group(6)) && isNotBlank(matcher.group(8))) {
					
					String key = matcher.group(7);
					if(dataType == DataType.MIX) {
						int jsonIndex = matcher.group(7).indexOf(JSON_VALUE);
						key = jsonIndex > -1 ? key.substring(0, jsonIndex) : key;
						isJson = true;
					}
					if(key.indexOf(ENUM_VALUE) > -1) {
						String[] frags = key.split(ENUM_VALUE);
						key = frags[0];
						enumClassName = frags[1];
					}
					Object val = getMappingValue(data, key, enumClassName, enums);
					
					if(val == null ) {
						val = convertParam(keyName, symbol, val, isJson);
					}else {
						if(val instanceof Collection<?>)
							val = ((Collection<?>)val).toArray();
						Object[] vals = val.getClass().isArray() ? (Object[])val : new Object[]{val};
						String fmtStr = "";
						if(vals.length > 0) {
							for(int i = 0; i < vals.length; i++) {
								String sub = formatExp2String(matcher.group(8), vals[i], dataType, enums);
								if(isJson) {
									if(i == 0) 
										fmtStr += keyName + symbol + "[" + sub;
									else
										fmtStr += "," + sub;
									if(i == vals.length - 1)
										fmtStr += "]";
								}else {
									fmtStr += keyName + "[]" + symbol + sub;
									if(i < vals.length - 1) 
										fmtStr += "&";
								}
							}
						}else {
							if(isJson) {
								fmtStr += keyName + symbol + "[]";
							}else {
								fmtStr += keyName + "[]" + symbol;
							}
						}
						val = fmtStr;
					}
					matcher.appendReplacement(sb, val.toString());
					
				}else {
					String key = matcher.group(10);
					if(dataType == DataType.MIX) {
						int jsonIndex = matcher.group(10).indexOf(JSON_VALUE);
						key = jsonIndex > -1 ? key.substring(0, jsonIndex) : key;
						isJson = true;
					}
					if(key.indexOf(ENUM_VALUE) > -1) {
						String[] frags = key.split(ENUM_VALUE);
						key = frags[0];
						enumClassName = frags[1];
					}
					Object val = getMappingValue(data, key, enumClassName, enums);
					
					matcher.appendReplacement(sb, 
							convertParam(keyName, symbol, val, isJson) );
				}
				result = matcher.find();
			} while (result);
			matcher.appendTail(sb);
			return sb.toString();
		}
		return paramsStr;
	}
	
	@Deprecated
	private static Object getMappingValue(Object data, String key, String enumClassName, 
			Map<String, List<Map<String, Object>>> enums) {
		Object val = ObjectUtils.getMappingValue(data, key);
		if(enumClassName != null && val != null) {
			//转换枚举值
			if(enums == null || !enums.containsKey(enumClassName))
				throw new BaseException("has no enum named {0} in enums", enumClassName);
			String enumName = null;
			if(val instanceof Enum<?>) {
				enumName = ((Enum<?>)val).name();
			}else
				enumName = val.toString();
			List<Map<String, Object>> enumObjs = enums.get(enumClassName);
			boolean exist = false;
			for(Map<String, Object> enumObj : enumObjs) {
				Object name = enumObj.get("name");
				if(enumName.equals(name)) {
					val = enumObj.get("value");
					exist = true;
					break;
				}
			}
			if(!exist) {
				throw new BaseException("has no enum named {0} in enums", enumName);
			}
		}
		return val;
	}
	
	/**
	 * @param data
	 * @param isJson	指定此值是否转换成json字符窜
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	private static String convertParam(String keyName, String symbol, Object data, boolean isJson)
			throws JsonGenerationException, JsonMappingException, IOException {
		String val = keyName + symbol;
		if(isJson) {
			val += Jacksons.COMMUNICATE.toJson(data);
		}else {
			if(data != null) {
				if(data instanceof Date)
					val += ((Date)data).getTime() + "";
				else if(data instanceof DetailEnum<?>)
					val += ((DetailEnum<?>)data).getValue().toString();
				else if(data.getClass().isArray()) {
					Object[] array = (Object[])data;
					val = "";
					for(int i = 0; i < array.length; i++) {
						val += keyName + "[]" + symbol + array[i].toString();
						if(i < array.length - 1) 
							val += "&";
					}
				}else if(data instanceof Collection<?>) {
					val = "";
					for(Object obj : (Collection<?>)data) {
						val += keyName + "[]" + symbol + obj.toString() + "&";
					}
					if(val.length() > 1 && "&".equals("" + val.charAt(val.length() - 1)))
						val = val.substring(0, val.length() - 2);
				}else
					val += data.toString();
			}
		}
		return val;
	}
	
	/**
	 * 把普通的string转换成正则表达式字符窜
	 * @param data
	 * @return
	 */
	public static String convertStr2Reg(String data) {
		if(data == null) return null;
		StringBuilder fragBuffer = new StringBuilder();
		for(int i = 0; i < data.length(); i++) {
			char f = data.charAt(i);
			switch(f) {
			
			case '.':
			case '\\':
			case '{':
			case '}':
			case '[':
			case ']':
			case '*':
			case '?':
			case '=':
			case '+':
			case ':':
			case '!':
			case '|':
			case '&':
				fragBuffer.append("\\").append(f);
				break;
			case '\b':
				fragBuffer.append("\\b");
				break;
			case '\t':
				fragBuffer.append("\\t");
				break;
			case '\n':
				fragBuffer.append("\\n");
				break;
			case '\f':
				fragBuffer.append("\\f");
				break;
			case '\r':
				fragBuffer.append("\\r");
				break;
			default:
				fragBuffer.append(f);
				break;
			
			}
		}
		return fragBuffer.toString();
	}
	
	@Deprecated
	public static <T> T formatExp2Object(String fmtStr, String dataStr, Class<T> dataClass, 
			Map<String, List<Map<String, Object>>> enums) throws JsonParseException, JsonMappingException, IOException {
		return formatExp2Object(fmtStr, dataStr, dataClass, null, enums);
	}
	
	@Deprecated
	public static <T> T formatExp2Object(String fmtStr, String dataStr, Class<T> dataClass, 
			Map<String, Class<?>> assignClasses, Map<String, List<Map<String, Object>>> enums) 
					throws JsonParseException, JsonMappingException, IOException {
		return formatExp2Object(fmtStr, dataStr, dataClass, assignClasses, Symbol.EMPTY, enums);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Deprecated
	public static <T> T formatExp2Object(String fmtStr, String dataStr, Class<T> dataClass, 
			Map<String, Class<?>> assignClasses, String keyPrefix, Map<String, List<Map<String, Object>>> enums) 
					throws JsonParseException, JsonMappingException, IOException {
		Map originalData = Jacksons.NON_NULL.toBean(dataStr, Map.class);
		T data = ClassUtils.newInstance(dataClass);
		if(isBlank(fmtStr) || isBlank(dataStr))
			return data;
		
		Pattern p = Pattern.compile("([\\w\\.\"-]+)(\\s*[=:]\\s*)((\\[\\s*([\\w\\.-]+)\\s*/\\s*\\])|(\\[\\s*([\\w\\.-]+)\\s*\\]([\\s\\S]*?)\\[\\s*/\\s*\\7\\s*\\])|(\\$\\{\\s*([\\w\\.-]+)\\s*\\}))");
		Matcher matcher = p.matcher(fmtStr);
		boolean result = matcher.find();
		if (result) {
			do {
				String key = matcher.group(1),
						wrapper = null,
						array = null,
						enumClassName = null;
				if(isNotBlank(matcher.group(4))) {
					wrapper = matcher.group(5);
				}else if ( isNotBlank(matcher.group(6)) && isBlank(matcher.group(8))) {
					wrapper = matcher.group(7);
				}else if(isNotBlank(matcher.group(6)) && isNotBlank(matcher.group(8))) {
					wrapper = matcher.group(7);
					array = matcher.group(8);
				}else {
					wrapper = matcher.group(10);
				}
				
				Object proVal = ObjectUtils.getMappingValue(originalData, unquote(key));
				
				if(wrapper.indexOf(ENUM_VALUE) > -1) {
					String[] frags = wrapper.split(ENUM_VALUE);
					wrapper = frags[0];
					enumClassName = frags[1];
				}
				if(enumClassName != null) {
					if(enums == null || !enums.containsKey(enumClassName))
						throw new BaseException("has no enum named {0} in enums.", enumClassName);
					List<Map<String, Object>> enumObjs = enums.get(enumClassName);
					boolean exist = false;
					for(Map<String, Object> enumObj : enumObjs) {
						Object value = enumObj.get("value");
						if(value == proVal 
								|| (value != null && value.equals(proVal))
								|| (value != null && proVal != null && value.toString().equals(proVal.toString()))) {
							String name = enumObj.get("name").toString();
							proVal = Enum.valueOf((Class)EnumClassFactory.newInstance(enumClassName), name);
							exist = true;
							break;
						}
					}
					if(!exist) {
						throw new BaseException("has no enum valued {0} in enums", proVal);
					}
				}
				
				if(array != null) {
					List listVal = null;
					if(proVal instanceof List) {
						listVal = (List)proVal;
					}else {
						listVal = new ArrayList();
						listVal.add(proVal);
					}
					String newKey = keyPrefix.length() > 0 ? keyPrefix + "." + wrapper : wrapper;
					Class proClazz = assignClasses == null ? null : assignClasses.get(newKey + "[]");
					proClazz = proClazz == null ? FieldUtils.getFieldGenericType(data.getClass(), wrapper) : proClazz;
					for(int i = 0; i < listVal.size(); i++) {
						Object val = listVal.get(i);
						if(val == null) continue;
						Object subData = formatExp2Object(array, Jacksons.NON_NULL.toJson(val), 
								proClazz == null ? val.getClass() : proClazz, assignClasses, wrapper, enums);
						ObjectUtils.setMappingValue(data, wrapper + "[" + i + "]", subData, assignClasses);
					}
				}else {
					ObjectUtils.setMappingValue(data, wrapper, proVal, assignClasses);
				}
				result = matcher.find();
			} while(result);
		}
		return data;
	}
	
	/**
	 * 去除字符窜两端的引号,要两端同时有引号才会去掉
	 * @param value
	 * @return
	 */
	public static String unquote(String value) {
		if(value == null) return null;
		value = value.trim();
		if(value.charAt(0) == '"'
				&& value.charAt(value.length() - 1) == '"') {
			return value.substring(1, value.length() - 1);
		}else
			return value;
	}
	
	/**
	 * 改字符窜增加"" stringVal ==> "stringVal"
	 * @param string 为null时 返回 ""
	 * @return
	 */
	public static String quote(String string) {
		if (string == null || string.length() == 0) {
			return "\"\"";
		}
		char b;
		char c = 0;
		int i;
		int len = string.length();
		StringBuilder sb = new StringBuilder(len + 4);
		String t;
		sb.append('"');
		for (i = 0; i < len; i += 1) {
			b = c;
			c = string.charAt(i);
			switch (c) {
			case '\\':
				sb.append(c);
				break;
			case '"':
				sb.append('\\');
				sb.append(c);
				break;
			case '/':
				if (b == '<') {
					sb.append('\\');
				}
				sb.append(c);
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\r':
				sb.append("\\r");
				break;
			default:
				if (c < ' ') {
					t = "000" + Integer.toHexString(c);
					sb.append("\\u").append(t.substring(t.length() - 4));
				} else {
					sb.append(c);
				}
			}
		}
		sb.append('"');
		return sb.toString();
	}
	
	/**
	 * 把json的value转换成字符选
	 * @param value
	 * @return
	 */
	public static String valueToString(Object value) {
		if (value == null)
			return "null";
		else if (value instanceof Boolean 
				|| value instanceof Number)
			return value.toString();
		else
			return quote(value.toString());
	}
	
	/**
	 * 过滤字符窜的html标签。
	 * 删除<script>/<style>标签以及标签中所有的内容，
	 * 其他的只删除html标签，开始标签和介素标签的内容不过滤
	 * @return
	 */
	public static String filterHtml(String str) {
		if(isBlank(str)) 
			return str;
		Pattern p = null;
		Matcher m = null;
		p = Pattern.compile(Regex.SCRIPT,Pattern.CASE_INSENSITIVE);
		m = p.matcher(str);
		str = m.replaceAll(Symbol.EMPTY);
		p = Pattern.compile(Regex.STYLE,Pattern.CASE_INSENSITIVE);
		m = p.matcher(str);
		str = m.replaceAll(Symbol.EMPTY);
		p = Pattern.compile(Regex.HTML);
		m = p.matcher(str);
		str = m.replaceAll(Symbol.EMPTY);
		return str;
	}
	
	/**
	 * 过滤htnl中不安全的标签
	 * @param str
	 * @return
	 */
	public static String filterUnsafeHtml(String str) {
		if(isBlank(str)) 
			return str;
		Pattern p = null;
		Matcher m = null;
		p = Pattern.compile(Regex.SCRIPT,Pattern.CASE_INSENSITIVE);
		m = p.matcher(str);
		str = m.replaceAll(Symbol.EMPTY);
		p = Pattern.compile(Regex.STYLE,Pattern.CASE_INSENSITIVE);
		m = p.matcher(str);
		str = m.replaceAll(Symbol.EMPTY);
		p = Pattern.compile(Regex.FRAME,Pattern.CASE_INSENSITIVE);
		m = p.matcher(str);
		str = m.replaceAll(Symbol.EMPTY);
		return str;
	}
	
	/**
	 * 将字符串对象按给定的分隔符separator转象为ArrayList对象
	 * 
	 */
	public static List<String> split2List(String str, String separator) {
		return split2List(str, separator, true);
	}
	
	public static List<String> split2List(String str, String separator, boolean trim) {
		StringTokenizer strTokens = new StringTokenizer(str, separator);
		List<String> list = new ArrayList<String>();
		while (strTokens.hasMoreTokens()) {
			list.add(trim ? strTokens.nextToken().trim() : strTokens.nextToken());
		}
		return list;
	}
	
	/**
	 * 判断一个字符串是否是合法的Java标识符
	 * @return 如果参数s是一个合法的Java标识符返回真，否则返回假
	 */
	public static boolean isJavaIdentifier(String str) {
		if (isBlank(str) || !Character.isJavaIdentifierStart(str.charAt(0)))
			return false;
		for (int i = 1; i < str.length(); i++)
			if (!Character.isJavaIdentifierPart(str.charAt(i)))
				return false;
		return true;
	}
	
}
