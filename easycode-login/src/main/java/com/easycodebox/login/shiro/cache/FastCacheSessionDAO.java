package com.easycodebox.login.shiro.cache;

import com.easycodebox.login.shiro.filter.ThreadContextFilter;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.util.ThreadContext;

import java.io.Serializable;

/**
 * 扩展{@link EnterpriseCacheSessionDAO}是因为，每次请求CAS都会去缓存中心（如：Redis）去取Session数据很多次，
 * 为了提高性能才把Session数据缓存到线程变量中。<br>
 * 使用此类时必须要结合{@link ThreadContextFilter}一起使用，因为AbstractShiroFilter#doFilterInternal首先会创建Subject，
 * 导致Session对象会提前存到线程变量里。<br>
 * 而SubjectCallable#call会先执行threadState.bind()，在finally块中执行threadState.restore()回收变量数据，
 * 但不会回收在执行threadState.bind()代码之前的线程变量，此问题会导致线程中的变量一直增加。<br/>
 * 注意：{@link ThreadContextFilter}必须在{@link ShiroFilterFactoryBean}之前执行。
 * @author WangXiaoJin
 */
public class FastCacheSessionDAO extends EnterpriseCacheSessionDAO {
	
	public static final String SESSION_KEY_PREFIX = ThreadContextFilter.REMOVABLE_KEY_PREFIX + "SESSION-";
	
	@Override
	protected void doUpdate(Session session) {
		ThreadContext.remove(SESSION_KEY_PREFIX + session.getId());
		super.doUpdate(session);
	}
	
	@Override
	protected void doDelete(Session session) {
		ThreadContext.remove(SESSION_KEY_PREFIX + session.getId());
		super.doDelete(session);
	}
	
	@Override
	public Session readSession(Serializable sessionId) throws UnknownSessionException {
		String key = SESSION_KEY_PREFIX + sessionId;
		Session cachedSession = (Session) ThreadContext.get(key);
		if (cachedSession == null) {
			cachedSession = super.readSession(sessionId);
			ThreadContext.put(key, cachedSession);
		}
		return cachedSession;
	}
	
}
