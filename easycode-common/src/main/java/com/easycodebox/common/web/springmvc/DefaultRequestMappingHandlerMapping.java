package com.easycodebox.common.web.springmvc;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;

/**
 * 如果你想controller中不配置@RequestMapping注解http请求也能找到对应的方法，请使用此类
 * @author WangXiaoJin
 *
 */
public class DefaultRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	/**
	 * 当url中没有指定执行的方法名，默认执行defaultMethod方法
	 */
	private String defaultMethod = "execute";
	private String controllerPostfix;
	private String[] excludePatterns;
	
	private ConcurrentHashMap<String, HandlerMethod> handlerCache = new ConcurrentHashMap<>();
	/**
	 * 缓存没有对应HandlerMethod的lookupPath
	 */
	private ConcurrentSkipListSet<String> noHandlerCache = new ConcurrentSkipListSet<>();
	/**
	 * noHandlerCache的计数器，因为ConcurrentSkipListSet的size方法耗时与其大小成正比，size方法是通过遍历元素来取值的，此方法非常耗性能。
	 * 所以用计数器代替其size方法。
	 */
	private AtomicInteger counter = new AtomicInteger();
	/**
	 * counter计数器最大值 - 缓存没有对应handler的url最大个数
	 */
	private int noHandlerCacheMaxSize = 1024;

	@Override
	protected HandlerMethod lookupHandlerMethod(String lookupPath, 
			HttpServletRequest request) throws Exception {
		if(isExclude(excludePatterns, lookupPath))
			return null;
		HandlerMethod handlerMethod = super.lookupHandlerMethod(lookupPath, request);
		if(handlerMethod == null) {
			int index = lookupPath.lastIndexOf(Symbol.PERIOD);
			if(index > -1)
				lookupPath = lookupPath.substring(0, index);
			if(handlerCache.containsKey(lookupPath))
				return handlerCache.get(lookupPath);
		}
		if(handlerMethod == null && lookupPath.length() > 0) {
			if(noHandlerCache.contains(lookupPath)) {
				return null;
			}
			
			boolean startSlash = lookupPath.charAt(0) == '/';
			String[] paths = (startSlash ? lookupPath.substring(1) : lookupPath).split(Symbol.SLASH);
			String className = null,
					methodName = null;
			
			if(paths.length == 0 || paths.length > 2) {
				//当path不符合自动搜Controller条件时，不需要缓存此lookupPath，减少noHandlerCache频繁存储。 - 所以注释掉以下代码。
				//addNoHandlerCache(lookupPath);
				return null;
			} else if(paths.length == 1) {
				className = getControllerName(paths[0]);
				methodName = defaultMethod;
			} else if(paths.length == 2) {
				className = getControllerName(paths[0]);
				methodName = paths[1];
			}
			
			Object controller = null;
			Method method = null;
			if(getApplicationContext().containsBean(className)) {
				try {
					controller = getApplicationContext().getBean(className);
				} catch (Exception e) {
					log.error("Obtain bean [{0}] error.", e, className);
				}
			}
			if(controller == null || !isHandler(controller.getClass())) {
				addNoHandlerCache(lookupPath);
				return null;
			}
			
			method = findMehtod(controller.getClass(), methodName);
			if(method == null) {
				addNoHandlerCache(lookupPath);
				return null;
			}
			
			RequestMappingInfo info = null;
			RequestMapping methodAnnotation = AnnotationUtils.findAnnotation(method, RequestMapping.class);
			if (methodAnnotation != null) {
				RequestCondition<?> methodCondition = getCustomMethodCondition(method);
				info = createRequestMappingInfo(lookupPath, methodAnnotation, methodCondition);
			}
			RequestMapping typeAnnotation = AnnotationUtils.findAnnotation(controller.getClass(), RequestMapping.class);
			if (typeAnnotation != null) {
				RequestCondition<?> typeCondition = getCustomTypeCondition(controller.getClass());
				if(info == null) {
					info = createRequestMappingInfo(lookupPath, typeAnnotation, typeCondition);
				}else
					info = createRequestMappingInfo(null, typeAnnotation, typeCondition).combine(info);
			}
			
			if(info == null)
				info = new  RequestMappingInfo(null, null, null, null, null, null, null);
			handleMatch(info, lookupPath, request);
			
			HandlerMethod handler = new HandlerMethod(controller, method);
			handlerCache.putIfAbsent(lookupPath, handler);
			return handler;
		}
		
		return handlerMethod;
	}
	
	/**
	 * 当lookupPath找不到对应的HandlerMethod时，存入noHandlerCache对象中
	 * @param lookupPath
	 * @param handler
	 */
	private boolean addNoHandlerCache(String lookupPath) {
		boolean suc = noHandlerCache.add(lookupPath);
		if(suc) {
			int count = counter.incrementAndGet();
			if(count > noHandlerCacheMaxSize) {
				String val = noHandlerCache.pollFirst();
				if(val != null) {
					counter.decrementAndGet();
				}
			}
		}
		return suc;
	}
	
	private Method findMehtod(Class<?> clazz, String methodName) {
		Method method = null;
		try {
			BEGIN:
			while(!clazz.equals(Object.class)) {
				Method[] methods = clazz.getDeclaredMethods();
				for(Method me : methods) {
					if(Modifier.isPublic(me.getModifiers())
							&& me.getName().equals(methodName)) {
						method = me;
						break BEGIN;
					}
				}
				clazz = clazz.getSuperclass();
			}
		} catch (Exception e) {
			
		}
		return method;
	}
	
	private String getControllerName(String forShort) {
		return controllerPostfix == null ? forShort : forShort + controllerPostfix;
	}
	
	private RequestMappingInfo createRequestMappingInfo(String lookupPath, RequestMapping annotation, 
			RequestCondition<?> customCondition) {
		String[] patterns = lookupPath == null ? new String[0] : new String[]{lookupPath};
		return new RequestMappingInfo(
				new PatternsRequestCondition(patterns, getUrlPathHelper(), getPathMatcher(),
						this.useSuffixPatternMatch(), this.useTrailingSlashMatch(), getFileExtensions()),
				new RequestMethodsRequestCondition(annotation.method()),
				new ParamsRequestCondition(annotation.params()),
				new HeadersRequestCondition(annotation.headers()),
				new ConsumesRequestCondition(annotation.consumes(), annotation.headers()),
				new ProducesRequestCondition(annotation.produces(), annotation.headers(), getContentNegotiationManager()),
				customCondition);
	}
	private boolean isExclude(String[] excludePatterns, String lookupPath) {
		if(excludePatterns == null || excludePatterns.length == 0)
			return false;
		for(String pattern : excludePatterns) {
			if (pattern.equals(lookupPath)) {
				return true;
			}
			if (this.useSuffixPatternMatch()) {
				if (getFileExtensions() != null && !getFileExtensions().isEmpty() && lookupPath.indexOf('.') != -1) {
					for (String extension : getFileExtensions()) {
						if (getPathMatcher().match(pattern + extension, lookupPath)) {
							return true;
						}
					}
				}
				else {
					boolean hasSuffix = pattern.indexOf('.') != -1;
					if (!hasSuffix && getPathMatcher().match(pattern + ".*", lookupPath)) {
						return true;
					}
				}
			}
			if (getPathMatcher().match(pattern, lookupPath)) {
				return true;
			}
			boolean endsWithSlash = pattern.endsWith(Symbol.SLASH);
			if (this.useTrailingSlashMatch()) {
				if (!endsWithSlash && getPathMatcher().match(pattern + Symbol.SLASH, lookupPath)) {
					return true;
				}
			}
		}
		return false;
	}

	public void setDefaultMethod(String defaultMethod) {
		this.defaultMethod = defaultMethod;
	}

	public void setControllerPostfix(String controllerPostfix) {
		this.controllerPostfix = controllerPostfix;
	}

	public void setExcludePatterns(String[] excludePatterns) {
		this.excludePatterns = excludePatterns;
	}

	public int getNoHandlerCacheMaxSize() {
		return noHandlerCacheMaxSize;
	}

	public void setNoHandlerCacheMaxSize(int noHandlerCacheMaxSize) {
		this.noHandlerCacheMaxSize = noHandlerCacheMaxSize;
	}
	
}
