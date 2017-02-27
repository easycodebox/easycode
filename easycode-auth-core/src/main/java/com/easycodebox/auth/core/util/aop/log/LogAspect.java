package com.easycodebox.auth.core.util.aop.log;

import com.easycodebox.auth.core.service.sys.LogService;
import com.easycodebox.common.CommonProperties;
import com.easycodebox.common.enums.DetailEnum;
import com.easycodebox.common.enums.entity.LogLevel;
import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.lang.Symbol;
import com.easycodebox.common.net.Https;
import com.easycodebox.common.security.SecurityContexts;
import com.easycodebox.common.security.SecurityUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;

import javax.servlet.http.HttpServletRequest;

/**
 * @author WangXiaoJin
 * 
 */
@Aspect
public final class LogAspect implements Ordered {
	
	//private final Logger log = LoggerFactory.getLogger(getClass());
	
	private final int DEFAULT_ORDER = 1;
	private int order = DEFAULT_ORDER;
	
	private CommonProperties commonProperties = CommonProperties.instance();
	
	/**
	 * 加上@Lazy标记是因为Spring配置了default-lazy-init="true"，且某个bean不是懒加载，
	 * 当次bean被logService或logService依赖对象依赖时，则会出现Spring AOP循环引用问题，
	 * 导致项目启动失败。
	 * 解决方案两种：
	 * 1. 去掉 default-lazy-init="true" 配置
	 * 2. 增加@Lazy配置，如下
	 */
	@Lazy
	@Autowired
	private LogService logService;
	
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
			logObj.setParams(Strings.substring(Https.getRequestParams(request, 0, commonProperties.isTraditionalHttp()), 0, 2048));
			logObj.setClientIp(SecurityUtils.getIp());
		}
		logObj.setTitle(log.title());
		logObj.setMethod(Strings.substring(getMethod(pjp), 0, 1024));
		logObj.setLogLevel(log.level());
		logObj.setModuleType(log.moduleType());
		
		Object result;
		try {
			result = pjp.proceed();
		} catch (Throwable e) {
			logObj.setErrorMsg(Strings.substring(ExceptionUtils.getStackTrace(e), 0, 2048));
			logObj.setLogLevel(LogLevel.ERROR);
			logService.add(logObj);
			throw e;
		}
		logObj.setResult(result == null ? null : Strings.substring(result.toString(), 0, 2048));
		logService.add(logObj);
		return result;
	}
	
	private String getMethod(final ProceedingJoinPoint pjp) {
        StringBuilder sb = new StringBuilder();
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
	
	public CommonProperties getCommonProperties() {
		return commonProperties;
	}
	
	public void setCommonProperties(CommonProperties commonProperties) {
		this.commonProperties = commonProperties;
	}
}
