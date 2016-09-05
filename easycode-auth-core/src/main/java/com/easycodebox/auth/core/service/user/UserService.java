package com.easycodebox.auth.core.service.user;

import java.util.List;

import com.easycodebox.auth.core.pojo.user.User;
import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.enums.entity.status.CloseStatus;
import com.easycodebox.common.error.ErrorContext;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.login.ws.bo.UserWsBo;

/**
 * @author WangXiaoJin
 *
 */
public interface UserService {
	
	/**
	 * 用户列表
	 * @return
	 */
	List<User> list();
	
	/**
	 * 用户详情 (包含禁用状态的用户)
	 * 注意：返回的loginFail和status字段值可能不准确，因为数据库中的值可能被CAS系统修改，除非CAS系统和此系统公用同一个缓存系统
	 * @param id
	 * @return
	 */
	User load(String id);
	
	/**
	 * 用户详情 (包含禁用状态的用户)
	 * @return
	 */
	User loadByUsername(String username);
	
	/**
	 * 新增用户
	 * @param user
	 * @return	应该实现返回数据能获取到主键
	 */
	User add(User user);
	
	/**
	 * 修改用户
	 * @param user
	 * @return
	 */
	int update(User user);
	
	/**
	 * 逻辑删除用户
	 * @param ids
	 * @return
	 */
	int remove(String[] ids);
	
	/**
	 * 物理删除用户
	 * @param ids
	 * @return
	 */
	int removePhy(String[] ids);
	
	/**
	 * 用户分页
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	DataPage<User> page(String groupName, String userNo, String username, 
			String nickname, String realname, CloseStatus status, 
			String email, String mobile, int pageNo, int pageSize);
	
	DataPage<UserWsBo> page(Integer groupId, String userNo, String username, String nickname, 
			String realname, CloseStatus status, String email, String mobile,
			String[] ids, Integer pageNo, Integer pageSize);
			
	/**
	 * 开启、关闭用户
	 * @param ids
	 * @param status
	 * @return
	 */
	int openClose(String[] ids, CloseStatus status);
	
	/**
	 * 登录统一使用CAS系统
	 * @return	返回对应的用户（包括被锁的用户），返回null说明不存在或者已被删除
	 */
	@Deprecated
	User login(String username, String password);
	
	boolean existUsername(String username, String excludeId);
	
	boolean existNickname(String nickname, String excludeId);
	
	/**
	 * 西瓜用户昵称
	 * @param nickname
	 * @param userId
	 */
	int updateNickname(String nickname, String id);
	
	/**
	 * 获取用户密码
	 * @param id
	 * @return
	 */
	String getPwd(String id);
	
	/**
	 * 修改密码
	 * @param newPwd	经过MD5加密过后的值
	 * @param id
	 * @return
	 */
	int updatePwd(String newPwd, String id);
	
	/**
	 * 更新头像
	 * @param id
	 * @param portrait
	 * @return
	 * @throws ErrorContext
	 */
	int updatePortrait(String id, String portrait);
	
	/**
	 * 更新用户基本信息，参数为null时，会把null更新到数据库
	 * @throws ErrorContext
	 */
	int updateBaseInfo(String id, String nickname, String realname, String email, String mobile);
	
	YesNo isSuperAdmin(String id);
	
	Integer getGroupId(String id);
	
	/**
	 * 清除user缓存
	 * @param userId
	 * @return
	 * @throws ErrorContext
	 */
	boolean clearCache(String userId);
	
}
