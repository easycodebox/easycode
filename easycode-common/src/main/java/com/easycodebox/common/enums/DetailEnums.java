package com.easycodebox.common.enums;

import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;

/**
 * @author WangXiaoJin
 * 
 */
public final class DetailEnums {
	
	private static final Logger log = LoggerFactory.getLogger(DetailEnums.class);
	
	/**
	 *  根据DetailEnum的value值解析成DetailEnum。如果clazz中没有null值，value==null时return null值
	 */
	public static <T extends DetailEnum<?>> T parse(Class<T> clazz, Object value) {
		if (!clazz.isEnum()) return null;
		for(T t : clazz.getEnumConstants()) {
			if(t.getValue() == null && value == null
					|| value != null && value.equals(t.getValue())) {
				return t;
			}
		}
		return null;
	}
	
	/**
	 * 返回格式 :<code>"0":"启用","1":"禁用"</code>
	 * @param clz
	 * @return
	 */
	public static <T extends Enum<T> & DetailEnum<V>, V> String getEnum(Class<T> clz) {
        try {
        	T[] vals = clz.getEnumConstants();
            return getEnum(vals);
        } catch ( Exception e ) {
        	log.error(clz.getSimpleName(), e);
        }
        return Symbol.EMPTY;
    }
	
	/**
	 * 返回格式 :<code>"0":"启用","1":"禁用"</code>
	 * @param pe
	 * @return
	 */
    public static String getEnum(DetailEnum<?>[] pe) {
    	StringBuilder sb = new StringBuilder();
        for (DetailEnum< ? > item : pe) {
            sb.append( "\"" )
            	.append( item.getValue() )
            	.append( "\":\"" )
            	.append( item.getDesc() )
            	.append( "\"," );
        }
        return sb.substring( 0 , sb.length() - 1 );
    }
	
    /**
     * String值可以是：
	 * 1、YES ==> enum class name
	 * 2、0	==> DetailEnum的value属性
	 * 3、是	==> DetailEnum的desc属性
	 * 4、枚举的索引值
	 * 转换优先级顺序: className -> value属性 -> desc属性 -> 枚举的索引值
     * @param enumType
     * @param value
     * @param enableOrdinal	是否开启枚举的ordinal匹配
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> T deserialize(Class<T> enumType, String value, boolean enableOrdinal) {
    	if (!enumType.isEnum()) return null;
    	T data = null;
		try {
			data = (T)Enum.valueOf((Class<Enum>)enumType, value);
		} catch (Exception ignored) {
			
		}
		if(data == null) {
			if(DetailEnum.class.isAssignableFrom(enumType)) {
				//根据DetailEnum的Valuel属性赋值
	        	for(DetailEnum e : (DetailEnum[])enumType.getEnumConstants()) {
					if(e.getValue() == null && value == null
							|| value != null && e.getValue() != null && value.equals(e.getValue().toString())) {
						data = (T)e;
						break;
					}
				}
	        	
	        	if(data == null) {
	        		//根据DetailEnum的desc属性赋值
	        		for(DetailEnum e : (DetailEnum[])enumType.getEnumConstants()) {
	        			if(e.getDesc() == null && value == null
	        					|| value != null && e.getDesc() != null && value.equals(e.getDesc())) {
	        				data = (T)e;
	        				break;
	        			}
	        		}
	        	}
			}
        	
        	//根据枚举的索引赋值
        	if(data == null && enableOrdinal && value != null) {
        		try {
					int index = Integer.parseInt(value);
			        T[] constants = enumType.getEnumConstants();
			        if (index < constants.length) {
				        data = constants[index];
			        }
				} catch (NumberFormatException ignored) {
					
				}
        	}
        }
		return data;
    }
    
}
