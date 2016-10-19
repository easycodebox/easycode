package com.easycodebox.auth.core.ws;

import com.easycodebox.common.enums.entity.OpenClose;
import com.easycodebox.common.error.ErrorContext;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.login.ws.bo.UserExtWsBo;
import com.easycodebox.login.ws.bo.UserWsBo;

public interface UserWsService {

	/**
	 * 详情 (包含禁用状态的用户) <br>
	 * 后期load方法删除后，此方法改名为load
	 * @param id
	 * @return
	 */
	UserWsBo loadById(String id) throws ErrorContext;
	
	/**
	 * 为了兼容老版本项目，就算username、nickname有值也会忽略。请使用loadById(String id)
	 */
	@Deprecated
	UserWsBo load(String id, String username, String nickname) throws ErrorContext;
	
	DataPage<UserWsBo> page(Integer groupId, String userNo, String username, String nickname, 
			String realname, OpenClose status, String email, String mobile,
			String[] ids, Integer pageNo, Integer pageSize) throws ErrorContext;
	
	int updatePwd(String id, String oldPwd, String newPwd) throws ErrorContext;
	
	/**
	 * 修改指定用户状态
	 * @param ids
	 * @param status
	 * @return
	 * @throws ErrorContext
	 */
	int updateStatus(String[] ids, OpenClose status) throws ErrorContext;
	
	int update(UserWsBo userWsBo) throws ErrorContext;
	
	/**
	 * 更新昵称
	 */
	int updateNickname(String id, String nickname) throws ErrorContext;
	
	/**
	 * 更新头像
	 * @param id
	 * @param portrait
	 * @return
	 * @throws ErrorContext
	 */
	int updatePortrait(String id, String portrait) throws ErrorContext;
	
	/**
	 * 更新用户基本信息，参数为null时，会把null更新到数据库
	 * @throws ErrorContext
	 */
	int updateBaseInfo(String id, String nickname, String realname, 
			String email, String mobile) throws ErrorContext;
	
	boolean existUsername(String username, String excludeId) throws ErrorContext;
	
	boolean existNickname(String nickname, String excludeId) throws ErrorContext;
	
	/**
	 * 新增
	 * @param user
	 * @return	返回主键
	 */
	String add(UserWsBo user, String roleName) throws ErrorContext;
	
	/**
	 * 逻辑删除
	 * @param ids
	 * @return
	 */
	int remove(String[] ids) throws ErrorContext;
	
	/**
	 * 登录成功后返回的用户信息
	 * @param validProjectAuth 是否需要验证此用户有没有权限登录对应的系统。
	 * 当为true，且project和当前用户的roles如果没有对应关联关系，则此用户没有权限登录此系统。
	 * 但有些系统（如：前台系统），只需要用户登录成功后就可以访问用户中心，不需要后台配置权限，所以此情况下需要validProjectAuth = false
	 * @return
	 * @throws ErrorContext
	 */
	UserExtWsBo loginSucBack(String userId, String projectNo, boolean validProjectAuth) throws ErrorContext;
	
	/**
	 * 请使用loginSucBack(String userId, String projectNo, boolean validProjectAuth)
	 * @return
	 * @throws ErrorContext
	 */
	@Deprecated
	UserExtWsBo loginSucNew(String userId, String projectNo) throws ErrorContext;
	
	/**
	 * 请使用loginSucBack(String userId, String projectNo, boolean validProjectAuth)
	 */
	@Deprecated
	UserExtWsBo loginSuc(String username, String projectNo) throws ErrorContext;
	
}
