package com.easycodebox.common.enums;

import java.lang.reflect.Method;

import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;

/**
 * @author WangXiaoJin
 * 
 */
public final class Enums {
	
	private static final Logger LOG = LoggerFactory.getLogger(Enums.class);
	
	/**
	 *  根据DetailEnum的value值解析成DetailEnum。如果clazz中没有null值，value==null时return null值
	 * @param <K>
	 * @param <T>
	 * @param clazz
	 * @param value
	 * @return
	 */
	public static <T extends Enum<T> & DetailEnum<V>, V> T parse(Class<T> clazz, V value) {
		for(T t : clazz.getEnumConstants()) {
			if(t.getValue() == null && value == null) {
				return t;
			} else if(t.getValue() == null || value == null){
				continue;
			} else if(t.getValue().equals(value)){
				return t;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Enum<T> & DetailEnum<V>, V> String getEnum(Class<T> clz) {
        try {
            Method values = clz.getDeclaredMethod("values");
            DetailEnum<T>[] v = (DetailEnum<T>[])values.invoke(clz);
            return getEnum(v);
        } catch ( Exception e ) {
        	LOG.error(clz.getSimpleName(), e);
        }
        return Symbol.EMPTY;
    }
	
    public static String getEnum( DetailEnum<?>[] pe ) {
    	StringBuilder sb = new StringBuilder();
        for ( DetailEnum< ? > item : pe ) {
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
    	T data = null;
		try {
			data = (T)Enum.valueOf((Class<Enum>)enumType, value);
		} catch (Exception e) {
			
		}
		if(data == null) {
			if(DetailEnum.class.isAssignableFrom(enumType)) {
				//根据DetailEnum的Valuel属性赋值
	        	for(DetailEnum e : (DetailEnum[])enumType.getEnumConstants()) {
					if(e.getValue() == null && value == null
							|| value != null && value.equalsIgnoreCase(e.getValue().toString())) {
						data = (T)e;
						break;
					}
				}
	        	
	        	if(data == null) {
	        		//根据DetailEnum的desc属性赋值
	        		for(DetailEnum e : (DetailEnum[])enumType.getEnumConstants()) {
	        			if(e.getDesc() == null && value == null
	        					|| value != null && value.equalsIgnoreCase(e.getDesc().toString())) {
	        				data = (T)e;
	        				break;
	        			}
	        		}
	        	}
			}
        	
        	//根据枚举的索引赋值
        	if(data == null && enableOrdinal) {
        		try {
					int index = Integer.parseInt(value);
					for(T e : enumType.getEnumConstants()) {
	    				if(((Enum)e).ordinal() == index) {
	    					data = e;
	    					break;
	    				}
	    			}
				} catch (NumberFormatException e1) {
					
				}
        	}
        }
		return data;
    }
    
}
