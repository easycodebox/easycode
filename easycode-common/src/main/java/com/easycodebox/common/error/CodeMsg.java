package com.easycodebox.common.error;

import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.file.PropertiesPool;
import com.easycodebox.common.jackson.*;
import com.easycodebox.common.lang.Strings;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang.ArrayUtils;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * 
 * 1. 当code = [SUC_CODE | null] 时，视执行成功。
 * 2. 当返回CodeMsg的对象中msg属性没值，意味着提示信息由客户端自己控制。当有值时，会覆盖客户端定义的提示信息。 
 * @author WangXiaoJin
 *
 */
@JsonSerialize(using = CodeMsgSerializer.class)
public class CodeMsg implements Serializable {
	
	/**
	 * 不显示提示信息
	 */
	public static CodeMsg NONE 		= new CodeMsg(null, null);
	/**
	 * 成功
	 */
	public static CodeMsg SUC 		= new CodeMsg(Code.SUC_CODE, null);
	/**
	 * 成功
	 */
	public static CodeMsg SUC_MSG 	= new CodeMsg(Code.SUC_CODE, Msg.SUC_MSG_INFO);
	/**
	 * 失败
	 */
	public static CodeMsg FAIL 		= new CodeMsg(Code.FAIL_CODE, null);
	/**
	 * 失败
	 */
	public static CodeMsg FAIL_MSG 	= new CodeMsg(Code.FAIL_CODE, Msg.FAIL_MSG_INFO);
	/**
	 * 未登录
	 */
	public static CodeMsg NO_LOGIN 	= new CodeMsg(Code.NO_LOGIN_CODE, null);
	
	/*****************  属性及方法    *************************/
	
	private String code;
	/**
	 * 如果对应的properties文件有数据则依据properties
	 */
	private String msg;
	private Object data;
	
	protected CodeMsg(String code, String msg) {
		this(code, msg, null);
    }
	
	protected CodeMsg(String code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
	
	/**
     * 设置code。返回一个新的CodeMsg对象，不会修改原有的对象
     * 只有CodeMsg.SUC_CODE视为成功，其他全为失败
     */
    public CodeMsg code(String code) {
    	CodeMsg codeMsg = this.copy();
		codeMsg.code = code;
		return codeMsg;
	}
    
    /**
	 * 设置msg。返回一个新的CodeMsg对象，不会修改原有的对象
	 * @param msg
	 * @param args	用于替换msg中的占位符
	 * @return
	 */
	public CodeMsg msg(String msg, Object... args) {
		CodeMsg codeMsg = this.copy();
		codeMsg.msg = Strings.format(msg, args);
		return codeMsg;
	}
	
	/**
	 * 同时设置code、msg
	 * @param code
	 * @param msg
	 * @param args
	 * @return
	 */
	public CodeMsg codeMsg(String code, String msg, Object... args) {
    	CodeMsg codeMsg = this.copy();
		codeMsg.code = code;
		codeMsg.msg = Strings.format(msg, args);
		return codeMsg;
	}
	
	/**
	 * 把args填充到msg中。如果args有值则返回一个新的CodeMsg对象，不会修改原有的对象
	 * @param args
	 * @return
	 */
    public CodeMsg fillArgs(Object... args) {
    	if(args != null && args.length > 0)
    		return msg(getMsg(), args);
    	else
    		return this;
	}
	
    /**
	 * 设置额外的数据。返回一个新的CodeMsg对象，不会修改原有的对象
	 * @param data
	 * @return
	 */
	public CodeMsg data(Object data) {
		CodeMsg codeMsg = this.copy();
		codeMsg.data = data;
		return codeMsg;
	}
	
	/**
	 * 获取错误信息的code
	 * @return
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * 获取提示信息
	 * @return
	 */
	public String getMsg() {
		if(this.msg == null && this.getCode() != null) {
			this.msg = PropertiesPool.getProperty(this.getCode());
		}
		return this.msg;
	}
	
	/**
	 * 获取存储的数据
	 * @return
	 */
	public Object getData() {
		return this.data;
	}
	
	@Override
	public String toString() {
		try {
			return Jacksons.COMMUNICATE.toJson(this);
		} catch (JsonProcessingException e) {
			throw new BaseException("Transform BaseCodeMsg obj to JSON error.", e);
		}
	}
	
	public CodeMsg copy() {
		return new CodeMsg(this.getCode(), this.getMsg(), this.getData());
	}
	
	/**
	 * code == null || code == SUC_CODE 时被视为成功
	 * @return
	 */
	public boolean isSuc() {
		return this.getCode() == null || Code.SUC_CODE.equals(this.getCode());
	}
	
	/**
	 *	code常量
	 */
	public static class Code {
		
		public static final String SUC_CODE 		= BaseConstants.codeSuc;
		public static final String FAIL_CODE 		= BaseConstants.codeFail;
		public static final String NO_LOGIN_CODE 	= BaseConstants.codeNoLogin;
		
	}
	
	/**
	 *	msg常量
	 */
	public static class Msg {
		
		public static String SUC_MSG_INFO 			= "操作成功";
		public static String FAIL_MSG_INFO 			= "操作失败";
		
	}
	
	public static class CodeMsgs {
		
		@SuppressWarnings("rawtypes")
		public static CodeMsg json2Bean(String json) throws IOException {
			Map map = Jacksons.COMMUNICATE.readValue(json, Map.class);
			String code = (String)map.get("code"),
					msg = (String)map.get("msg");
			Object data = map.get("data");
			return new CodeMsg(code, msg, data);
		}
		
		public static CodeMsg json2Bean(String json, Class<?> dataValueType) throws IOException {
			return json2Bean(json, dataValueType == null ? null : 
				Jacksons.COMMUNICATE.getTypeFactory().constructType(dataValueType));
		}
		
		public static CodeMsg json2Bean(String json, TypeReference<?> dataValueTypeRef) throws IOException {
			return json2Bean(json, dataValueTypeRef == null ? null :
				Jacksons.COMMUNICATE.getTypeFactory().constructType(dataValueTypeRef));
		}
		
		public static CodeMsg json2Bean(String json, JavaType dataValueType) throws IOException {
			
			if(dataValueType == null) {
				return json2Bean(json);
			}else {
				JsonNode jsonNode = Jacksons.COMMUNICATE.readTree(json);
				String code = jsonNode.get("code").asText(),
						msg = jsonNode.get("msg").asText();
				Object data = null;
				JsonNode dataNode = jsonNode.get("data");
				
				if(!dataNode.isNull()) {
					data = Jacksons.COMMUNICATE.readValue(
							Jacksons.COMMUNICATE.treeAsTokens(dataNode), 
							dataValueType);
				}
				return new CodeMsg(code, msg, data);
			}
		}
		
		/**
		 * code/msg存储到Properties文件中
		 * @throws IllegalAccessException 
		 * @throws IllegalArgumentException 
		 * @throws IOException 
		 * @throws FileNotFoundException 
		 */
		public static void storePropertiesFile(Class<? extends CodeMsg> clazz, File propertiesFile) 
				throws IllegalArgumentException, IllegalAccessException, IOException {
			
			//排除的Code
			String[] excludeCodes = {Code.SUC_CODE, Code.FAIL_CODE, Code.NO_LOGIN_CODE};
			
			Properties p = new Properties() {
				
				/**
				 * 为了生成的属性文件内容排序
				 */
				@Override
			    public synchronized Enumeration<Object> keys() {
					Set<Object> set = new TreeSet<>(new Comparator<Object>() {
						
						@Override
						public int compare(Object o1, Object o2) {
							String str1 = o1.toString(),
									str2 = o2.toString();
							int len1 = str1.length(),
									len2 = str2.length();
							return len1 == len2 ? str1.compareTo(str2) : len1 - len2;
						}
						
					});
					set.addAll(keySet());
			        return Collections.enumeration(set);
			    }
			};
			
			for(Field f : clazz.getFields()) {
				if(Modifier.isStatic(f.getModifiers())
						&& CodeMsg.class.isAssignableFrom(f.getType())) {
					CodeMsg code = (CodeMsg)f.get(clazz);
					if(code.getCode() != null && code.getMsg() != null
							&& !ArrayUtils.contains(excludeCodes, code.getCode()))
						p.put(code.getCode(), code.getMsg());
				}
			}
			p.store(new FileOutputStream(propertiesFile), "code msg.");
		}
		
	}
	
}
