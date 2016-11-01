package com.easycodebox.auth.core.util.aop.log;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;

import com.easycodebox.auth.core.service.sys.LogService;
import com.easycodebox.common.enums.DetailEnum;
import com.easycodebox.common.enums.entity.LogLevel;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.net.HttpUtils;
import com.easycodebox.common.security.SecurityContexts;
import com.easycodebox.common.security.SecurityUtils;

/**
 * @author WangXiaoJin
 * 
 */
@Aspect
public final class LogAspect implements Ordered, InitializingBean {
	
	//private final Logger log = LoggerFactory.getLogger(getClass());
	
	private final int DEFAULT_ORDER = 1;
	private int order = DEFAULT_ORDER;
	
	/**
	 * 加上@Lazy标记是因为Spring配置了default-lazy-init="true"，且某个bean不是懒加载，
	 * 当次bean被logService或logService依赖对象依赖时，则会出现Spring AOP循环引用问题，
	 * 导致项目启动失败。
	 * 解决方案两种：
	 * 1. 去掉 default-lazy-init="true" 配置
	 * 2. 增加@Lazy配置，如下
	 */
	@Lazy
	@Resource
	private LogService logService;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
	
	@Override
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
	
	@Around("@annotation(log)")
	public Object logging(final ProceedingJoinPoint pjp, final Log log) throws Throwable {
		com.easycodebox.auth.model.entity.sys.Log logObj = new com.easycodebox.auth.model.entity.sys.Log();
		if(SecurityContexts.getCurSecurityContext() != null
				&& SecurityContexts.getCurSecurityContext().getRequest() != null) {
			HttpServletRequest request = SecurityContexts.getCurSecurityContext().getRequest();
			logObj.setUrl(request.getRequestURL().toString());
			logObj.setParams(StringUtils.substring(HttpUtils.getRequestParams(request, false), 0, 2048));
			logObj.setClientIp(SecurityUtils.getIp());
		}
		logObj.setTitle(log.title());
		logObj.setMethod(StringUtils.substring(getMethod(pjp), 0, 1024));
		logObj.setLogLevel(log.level());
		logObj.setModuleType(log.moduleType());
		
		Object result = null;
		try {
			result = pjp.proceed();
		} catch (Throwable e) {
			logObj.setErrorMsg(StringUtils.substring(ExceptionUtils.getStackTrace(e), 0, 2048));
			logObj.setLogLevel(LogLevel.ERROR);
			logService.add(logObj);
			throw e;
		}
		logObj.setResult(result == null ? null : StringUtils.substring(result.toString(), 0, 2048));
		logService.add(logObj);
		return result;
	}
	
	private String getMethod(final ProceedingJoinPoint pjp) {
        StringBuffer sb = new StringBuffer();
        sb.append(pjp.getTarget().getClass().getName())
        	.append(Symbol.PERIOD)
        	.append(pjp.getSignature().getName())
        	.append(Symbol.L_PARENTHESIS);
        Object[] arguments = pjp.getArgs();
        if (arguments != null && arguments.length > 0) {
            for (int i = 0; i < arguments.length; i++) {
                Object arg = arguments[i];
                if(arg instanceof DetailEnum)
                	sb.append(((DetailEnum<?>)arg).getClassName());
                else
                	sb.append(arg);
                if(i < arguments.length - 1)
                	sb.append(", ");
            }
        }
        sb.append(Symbol.R_PARENTHESIS);
        return sb.toString();
    }
	
}
