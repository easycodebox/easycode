package com.easycodebox.common.lang.reflect;

import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.*;

/**
 * @author WangXiaoJin
 *
 */
public class ClassUtils extends org.apache.commons.lang.ClassUtils {
	
	private static final Logger log = LoggerFactory.getLogger(ClassUtils.class);
	
    public static List<Class<?>> getAllTypes(Class<?> clazz) {
        //
    	List<Class<?>> clazzes = new ArrayList<>();
    	List<Class<?>> list = getAllClasses(clazz);
    	clazzes.addAll(list);
        
        //
        for(Class<?> c : list) {
        	clazzes.addAll(getInterfaces(c));
        }
        return clazzes;
    }
    
	public static boolean isVoid(Class<?> clazz) {
		return clazz == void.class || clazz == Void.class;
	}
	
	public static ClassLoader getClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		}
		catch (Throwable ex) {
			// Cannot access thread context ClassLoader - falling back...
		}
		if (cl == null) {
			// No thread context class loader -> use class loader of this class.
			cl = ClassUtils.class.getClassLoader();
			if (cl == null) {
				// getClassLoader() returning null indicates the bootstrap ClassLoader
				try {
					cl = ClassLoader.getSystemClassLoader();
				}
				catch (Throwable ex) {
					// Cannot access system ClassLoader - oh well, maybe the caller can live with null...
				}
			}
		}
		return cl;
	}

	public static ClassLoader overrideThreadContextClassLoader(ClassLoader classLoaderToUse) {
		Thread currentThread = Thread.currentThread();
		ClassLoader threadContextClassLoader = currentThread.getContextClassLoader();
		if (classLoaderToUse != null && !classLoaderToUse.equals(threadContextClassLoader)) {
			currentThread.setContextClassLoader(classLoaderToUse);
			return threadContextClassLoader;
		}
		else {
			return null;
		}
	}
	
	/**
	 * 通过反射,获得指定类的指定索引实现接口的泛型参数的实际类型. 如ComplaintTypeEnum implements DetailEnum<Integer>
	 * @param clazz clazz 需要反射的类,该类必须实现范型接口
	 * @return 范型参数的实际类型, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回<code>Object.class</code>
	 */
	@SuppressWarnings("rawtypes")
	public static Class getInterfacesGenricType(Class clazz){
		return getInterfacesGenricType(clazz, 0, 0);
	}
	
	/**
	 * 通过反射,获得指定类的父类的泛型参数的实际类型. 如DaoSupport<Buyer>
	 * @param clazz clazz 需要反射的类,该类必须继承范型父类
	 * @param index 泛型参数所在索引,从0开始.
	 * @return 范型参数的实际类型, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回<code>Object.class</code>
	 */
	@SuppressWarnings("rawtypes")
	public static Class getSuperClassGenricType(Class clazz, int index){
		Type genType = clazz.getGenericSuperclass();// 得到泛型父类
		// 如果没有实现ParameterizedType接口，即不支持泛型，直接返回Object.class
		if (!(genType instanceof ParameterizedType)){
			return Object.class;
		}
		// 返回表示此类型实际类型参数的Type对象的数组,数组里放的都是对应类型的Class, 如BuyerServiceBean extends DaoSupport<Buyer,Contact>就返回Buyer和Contact类型
		Type[] params = ((ParameterizedType)genType).getActualTypeArguments();
		if (index >= params.length || index < 0){
			throw new RuntimeException("你输入的索引" + (index < 0 ? "不能小于0" : "超出了参数的总数"));
		}
		if (!(params[index] instanceof Class)){
			return Object.class;
		}
		return (Class)params[index];
	}

	/**
	 * 通过反射,获得指定类的父类的第一个泛型参数的实际类型. 如DaoSupport<Buyer>
	 * @param clazz clazz 需要反射的类,该类必须继承泛型父类
	 * @return 泛型参数的实际类型, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回<code>Object.class</code>
	 */
	@SuppressWarnings("rawtypes")
	public static Class getSuperClassGenricType(Class clazz){
		return getSuperClassGenricType(clazz, 0);
	}
	
	/**
	 * 通过反射,获得指定类的指定索引实现接口的泛型参数的实际类型. 如ComplaintTypeEnum implements DetailEnum<Integer>
	 * @param clazz clazz 需要反射的类,该类必须实现范型接口
	 * @param interfaceIndex 实现泛型接口的索引,从0开始.
	 * @param genricTypeIndex 泛型接口的泛型参数索引,从0开始.
	 * @return 范型参数的实际类型, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回<code>Object.class</code>
	 */
	@SuppressWarnings("rawtypes")
	public static Class getInterfacesGenricType(Class clazz, int interfaceIndex, int genricTypeIndex){
		//获取所有接口
		Type[] inteTypes = clazz.getGenericInterfaces();
		if (interfaceIndex >= inteTypes.length || interfaceIndex < 0){
			throw new RuntimeException("你输入的接口索引值" + (interfaceIndex < 0 ? "不能小于0" : "超出了实现接口的总数"));
		}
		Type genType = inteTypes[interfaceIndex];
		// 如果没有实现ParameterizedType接口，即不支持泛型，直接返回Object.class
		if (!(genType instanceof ParameterizedType)){
			return Object.class;
		}
		// 返回表示此类型实际类型参数的Type对象的数组,数组里放的都是对应类型的Class, 如BuyerServiceBean extends DaoSupport<Buyer,Contact>就返回Buyer和Contact类型
		Type[] params = ((ParameterizedType)genType).getActualTypeArguments();
		if (genricTypeIndex >= params.length || genricTypeIndex < 0){
			throw new RuntimeException("你输入的泛型索引值" + (genricTypeIndex < 0 ? "不能小于0" : "超出了参数的总数"));
		}
		if (!(params[genricTypeIndex] instanceof Class)){
			return Object.class;
		}
		return (Class)params[genricTypeIndex];
	}
    
	public static List<Class<?>> getAllClasses(Class<?> clazz) {
		return getAllClasses(clazz, false);
	}
	
    public static List<Class<?>> getAllClasses(Class<?> clazz, boolean ignoreObjectClass) {
    	List<Class<?>> clazzes = new ArrayList<>();
    	clazzes.add(clazz);
        for (Class<?> superClass = clazz.getSuperclass(); superClass != null; superClass = superClass.getSuperclass()) {
        	if(ignoreObjectClass && superClass == Object.class)
        		continue;
        	clazzes.add(superClass);
        }
        return clazzes;
    }

    private static List<Class<?>> getInterfaces(Class<?> clazz) {
    	List<Class<?>> r = new ArrayList<>();
        LinkedList<Class<?>> stack = new LinkedList<>();
        stack.addAll(Arrays.asList(clazz.getInterfaces()));
        while (!stack.isEmpty()) {
            Class<?> intf = stack.removeFirst();
            if (!r.contains(intf)) {
                r.add(intf);
                stack.addAll(Arrays.asList(intf.getInterfaces()));
            }
        }
        return r;
    }
    
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T newInstance(Class<T> clazz) {
		if(clazz == null) 
			return null;
		if(!clazz.isInterface()) {
			try {
				Constructor<T> c = clazz.getDeclaredConstructor();
				c.setAccessible(true);
				return c.newInstance();
			} catch (Exception e) {
				log.debug("There is no empty param Constructor in class({0})", clazz);
			}
		}
			
		if(Map.class.isAssignableFrom(clazz)) {
			return (T)new HashMap();
		}else if(List.class.isAssignableFrom(clazz)) {
			return (T)new ArrayList();
		}else if(Set.class.isAssignableFrom(clazz)) {
			return (T)new HashSet();
		}else if(clazz.getClass().isArray()) {
			return (T)Array.newInstance(clazz.getComponentType(), 0);
		}
		return null;
	}
	
	/**
	 * 组装指定类成sql映射格式  ==>  s.name, s.age, s.id
	 * @param clazz
	 * @return
	 */
	public static String assembleProperty2Sql(Class<?> clazz, String alias) {
		List<Class<?>> clazzes = getAllClasses(clazz, true);
		StringBuilder sb = new StringBuilder();
		for(Class<?> c : clazzes) {
			Field[] fields = c.getDeclaredFields();
			for(Field f : fields) {
				int m = f.getModifiers();
				if(Modifier.isFinal(m) || Modifier.isStatic(m))
					continue;
				sb.append(alias).append(Symbol.PERIOD).append(f.getName())
				.append(Symbol.COMMA).append(Symbol.SPACE);
			}
		}
		if(sb.length() > 0)
			sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
	
	/**
	 * com.deying.util.interceptor => interceptor
	 * @param pkg
	 * @return
	 */
	public static String getLastPkg(String pkg) {
		if(StringUtils.isBlank(pkg)) return null;
		int index = pkg.lastIndexOf(".");
		return pkg.substring(index + 1);
	}
	
	/**
	 * com.deying.util.interceptor.Test.class => interceptor
	 * @param clazz
	 * @return
	 */
	public static String getLastPkg(Class<?> clazz) {
		return getLastPkg(getPackageName(clazz));
	}
	
}
