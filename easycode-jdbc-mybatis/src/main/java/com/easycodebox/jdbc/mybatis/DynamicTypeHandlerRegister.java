package com.easycodebox.jdbc.mybatis;

import com.easycodebox.common.validate.Assert;
import org.apache.commons.lang.ArrayUtils;
import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.*;

import java.util.Set;

/**
 * MyBatis不支持根据package来动态组装TypeHandler，MyBatis只支持根据package扫描出已定义好的TypeHandler类。
 * <p>
 * 经常会有这样的需求：我只需要提供一个可转换特定接口的TypeHandler，只要实现了此接口的类都会被此TypeHandler转换类型。如枚举类型，
 * 枚举类型都实现了统一的接口，枚举是不能继承的，只能实现接口。
 * <p>
 * 非常遗憾的是MyBatis在3.4.2版本之前TypeHandler都是精准匹配，直到3.4.2才加入了可继承的TypeHandler
 * （如：A继承B，B对应了BTypeHandler，则A也可以使用BTypeHandler来数据转换）。但这还不能够解决接口类的TypeHandler继承。
 * MyBatis对TypeHandlerRegistry类做的太保守，导致不能拓展相应类。只能通过{@link DynamicTypeHandlerRegister}的功能来曲线实现。
 * <p>
 *     此类功能：指定某些package包下只要是filterClass的子类或者实现类可以用typeHandlerClass来转换数据，通过这种方案来减少配置。
 *     MyBatis官方提供方案是在此typeHandlerClass类注解{@link MappedTypes}里面包含所有可转换的Type，这种方案适用面比较窄。
 * </p>
 * @author WangXiaoJin
 */
public class DynamicTypeHandlerRegister {
	
	/**
	 * 需要扫描的包
	 */
	private String[] packages;
	
	/**
	 * 需要使用TypeHandler转换的类，此参数会经过filterClass参数过滤的
	 */
	private Class[] classes;
	
	/**
	 * 判断packages和classes中的类是否能转成TypeHandler的依据
	 */
	private Class<?> filterClass;
	
	/**
	 * 注册的TypeHandler类型
	 */
	private Class<TypeHandler> typeHandlerClass;
	
	private volatile boolean registered;
	
	public DynamicTypeHandlerRegister(Class filterClass, Class<TypeHandler> typeHandlerClass) {
		Assert.notNull(filterClass);
		Assert.notNull(typeHandlerClass);
		this.filterClass = filterClass;
		this.typeHandlerClass = typeHandlerClass;
	}
	
	/**
	 * 把packages和classes中过滤出来的类注册进{@link Configuration}中的TypeHandlerRegistry
	 * @param configuration
	 */
	public void register(Configuration configuration) {
		if (registered) return;
		registered = true;
		TypeHandlerRegistry registry = configuration.getTypeHandlerRegistry();
		if (ArrayUtils.isNotEmpty(packages)) {
			for (String pkg : packages) {
				ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
				resolverUtil.find(new ResolverUtil.IsA(filterClass), pkg);
				Set<Class<? extends Class<?>>> handlerSet = resolverUtil.getClasses();
				for (Class<? extends Class<?>> clazz : handlerSet) {
					registry.register(clazz, typeHandlerClass);
				}
			}
		}
		if (ArrayUtils.isNotEmpty(classes)) {
			for (Class clazz : classes) {
				if (filterClass.isAssignableFrom(clazz)) {
					registry.register(clazz, typeHandlerClass);
				}
			}
		}
	}
	
	public String[] getPackages() {
		return packages;
	}
	
	public void setPackages(String[] packages) {
		this.packages = packages;
	}
	
	public Class[] getClasses() {
		return classes;
	}
	
	public void setClasses(Class[] classes) {
		this.classes = classes;
	}
	
	public Class getFilterClass() {
		return filterClass;
	}
	
	public void setFilterClass(Class filterClass) {
		this.filterClass = filterClass;
	}
	
	public Class<TypeHandler> getTypeHandlerClass() {
		return typeHandlerClass;
	}
	
	public void setTypeHandlerClass(Class<TypeHandler> typeHandlerClass) {
		this.typeHandlerClass = typeHandlerClass;
	}
}
