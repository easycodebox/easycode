package com.easycodebox.login.util;

import java.util.LinkedList;
import java.util.List;

import org.apache.shiro.session.Session;

import com.easycodebox.common.BaseConstants;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.dto.UserInfo;
import com.easycodebox.common.security.SecurityContext;
import com.easycodebox.common.security.SecurityContexts;
import com.easycodebox.common.security.SecurityUtils;
import com.easycodebox.login.ws.bo.OperationWsBo;
import com.easycodebox.login.ws.bo.UserExtWsBo;

public class ShiroSecurityUtils {

	public static void setSubject(UserInfo userInfo) {
		Session session = org.apache.shiro.SecurityUtils.getSubject().getSession(true);
		session.setAttribute(BaseConstants.USER_KEY, userInfo);
		SecurityContext<UserInfo> sc = SecurityUtils.getCurSecurityContext();
		if(sc == null) {
			SecurityContext<UserInfo> tmp = new SecurityContext<UserInfo>();
			//暂时获取不到
			tmp.setSessionId(session.getId().toString());
			tmp.setSecurity(userInfo);
			SecurityContexts.setCurSecurityContext(tmp);
		}else {
			sc.setSecurity(userInfo);
			sc.setSessionId(session.getId().toString());
		}
	}
	
	public static void setSubject(UserExtWsBo user) {
		UserInfo userInfo = new UserInfo(user.getId(), 
				StringUtils.isBlank(user.getRealname()) ? user.getNickname() : user.getRealname(), 
				user.getUsername(), user.getNickname(), user.getRealname(), user.getPic(), 
				user.getStatus(), user.getGroupId(), user.getGroupName());
		
		Session session = org.apache.shiro.SecurityUtils.getSubject().getSession(true);
		session.setAttribute(BaseConstants.USER_KEY, userInfo);
		SecurityContext<UserInfo> sc = SecurityUtils.getCurSecurityContext();
		if(sc == null) {
			SecurityContext<UserInfo> tmp = new SecurityContext<UserInfo>();
			//暂时获取不到
			tmp.setSessionId(session.getId().toString());
			tmp.setSecurity(userInfo);
			SecurityContexts.setCurSecurityContext(tmp);
		}else {
			sc.setSecurity(userInfo);
			sc.setSessionId(session.getId().toString());
		}
		
		Permits.cacheOperations(session, user.getAllOsMap());
		session.setAttribute(BaseConstants.LEFT_MENU_KEY, treeOperations(null, user.getMenus()));
	}
	
	/**
	 * 为权限列表组装成树形结构
	 * @param parentId
	 * @param all	所有的权限
	 * @return
	 */
	private static List<OperationWsBo> treeOperations(Long parentId, List<OperationWsBo> all) {
		List<OperationWsBo> cur = new LinkedList<OperationWsBo>();
		for(OperationWsBo o : all) {
			if(parentId == null ? o.getParentId() == null : parentId.equals(o.getParentId())) {
				o.setChildren(treeOperations(o.getId(), all));
				cur.add(o);
			}
		}
		return cur;
	}
}
