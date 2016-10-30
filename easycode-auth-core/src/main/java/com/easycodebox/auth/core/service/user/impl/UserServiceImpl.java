package com.easycodebox.auth.core.service.user.impl;

import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easycodebox.auth.core.enums.ModuleType;
import com.easycodebox.auth.core.pojo.user.User;
import com.easycodebox.auth.core.service.user.UserService;
import com.easycodebox.auth.core.util.CodeMsgExt;
import com.easycodebox.auth.core.util.Constants;
import com.easycodebox.auth.core.util.R;
import com.easycodebox.auth.core.util.aop.log.Log;
import com.easycodebox.auth.core.util.mybatis.GeneratorEnum;
import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.enums.entity.OpenClose;
import com.easycodebox.common.error.ErrorContext;
import com.easycodebox.common.generator.Generators;
import com.easycodebox.jdbc.JoinType;
import com.easycodebox.jdbc.support.AbstractService;
import com.easycodebox.common.lang.StringUtils;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.common.validate.Assert;
import com.easycodebox.login.ws.bo.UserWsBo;

/**
 * @author WangXiaoJin
 *
 */
@Service("userService")
public class UserServiceImpl extends AbstractService<User> implements UserService {
	
	@Override
	public List<User> list() {
		return super.list(sql()
				.eq(R.User.deleted, YesNo.NO)
				.desc(R.User.sort)
				.desc(R.User.createTime)
				);
	}

	@Override
	@Cacheable(Constants.CN.USER)
	public User load(String id) {
		return super.get(sql()
				.eq(R.User.id, id)
				.eq(R.User.deleted, YesNo.NO)
				);
	}
	
	@Override
	public User loadByUsername(String username) {
		return super.get(sql()
				.eq(R.User.username, username)
				.eq(R.User.deleted, YesNo.NO)
				);
	}

	@Override
	@Transactional
	@Log(title = "添加用户", moduleType = ModuleType.USER)
	public User add(User user) {
		
		if(StringUtils.isNotBlank(user.getNickname()))
			Assert.isFalse(this.existNickname(user.getNickname(), user.getId()),
					CodeMsgExt.FAIL.msg("昵称{0}已被占用", user.getNickname()));
		
		Assert.isFalse(this.existUsername(user.getUsername(), user.getId()),
				CodeMsgExt.FAIL.msg("用户名{0}已被占用", user.getUsername()));
		
		if(user.getStatus() == null)
			user.setStatus(OpenClose.OPEN);
		user.setLoginFail(0);
		if(StringUtils.isBlank(user.getRealname()))
			user.setRealname(null);
		if(StringUtils.isBlank(user.getPassword()))
			user.setPassword(DigestUtils.md5Hex(Constants.resetPwd));
		if(user.getSort() == null)
			user.setSort(0);
		if(StringUtils.isBlank(user.getMobile()))
			user.setMobile(null);
		if(StringUtils.isBlank(user.getEmail()))
			user.setEmail(null);
		if(StringUtils.isBlank(user.getPic()))
			user.setPic(null);
		if(StringUtils.isBlank(user.getNickname())) {
			String nickname = null;
			do {
				nickname = (String)Generators.getGeneratorNextVal(GeneratorEnum.NICKNAME);
			}while(this.existNickname(nickname, null));
			user.setNickname(nickname);
		}
		//不能通过外部添加超级管理员
		user.setIsSuperAdmin(YesNo.NO);
		user.setDeleted(YesNo.NO);
		super.save(user);
		return user;
	}
	
	@Override
	@Log(title = "修改用户", moduleType = ModuleType.USER)
	@Caching(evict={
			@CacheEvict(cacheNames=Constants.CN.USER, key="#user.id"),
			//由于有可能修改group，导致角色有所变动
			@CacheEvict(cacheNames=Constants.CN.USER_ROLE, key="#user.id"),
			//由于有可能修改group，导致权限有所变动
			@CacheEvict(cacheNames=Constants.CN.OPERATION, allEntries=true)
	})
	public int update(User user) {
		
		if(StringUtils.isNotBlank(user.getNickname()))
			Assert.isFalse(this.existNickname(user.getNickname(), user.getId()),
					CodeMsgExt.FAIL.msg("昵称{0}已被占用", user.getNickname()));
		
		Assert.isFalse(this.existUsername(user.getUsername(), user.getId()),
				CodeMsgExt.FAIL.msg("用户名{0}已被占用", user.getUsername()));
		
		if(user.getStatus() != null) {
			log.info("The update method can not update status property.");
		}
		
		if(StringUtils.isBlank(user.getRealname()))
			user.setRealname(null);
		if(StringUtils.isBlank(user.getMobile()))
			user.setMobile(null);
		if(StringUtils.isBlank(user.getEmail()))
			user.setEmail(null);
		if(StringUtils.isBlank(user.getPic()))
			user.setPic(null);
		if(StringUtils.isBlank(user.getNickname())) {
			String nickname = null;
			do {
				nickname = (String)Generators.getGeneratorNextVal(GeneratorEnum.NICKNAME);
			}while(this.existNickname(nickname, null));
			user.setNickname(nickname);
		}
		
		return super.update(sql()
				.updateNeed(R.User.groupId, user.getGroupId())
				.updateNeed(R.User.userNo, user.getUserNo())
				.update(R.User.username, user.getUsername())
				.updateNeed(R.User.nickname, user.getNickname())
				.updateNeed(R.User.realname, user.getRealname())
				//.update(R.User.status, user.getStatus())
				.update(R.User.sort, user.getSort())
				.updateNeed(R.User.gender, user.getGender())
				.updateNeed(R.User.email, user.getEmail())
				.updateNeed(R.User.mobile, user.getMobile())
				.eqAst(R.User.id, user.getId())
				);
	}

	@Override
	@Log(title = "逻辑删除用户", moduleType = ModuleType.USER)
	@Caching(evict={
			@CacheEvict(cacheNames=Constants.CN.USER, keyGenerator=Constants.MULTI_KEY_GENERATOR),
			@CacheEvict(cacheNames=Constants.CN.USER_ROLE, keyGenerator=Constants.MULTI_KEY_GENERATOR),
			@CacheEvict(cacheNames=Constants.CN.OPERATION, allEntries=true)
	})
	public int remove(String[] ids) {
		return super.delete(ids);
	}
	
	@Override
	@Log(title = "物理删除用户", moduleType = ModuleType.USER)
	@Caching(evict={
			@CacheEvict(cacheNames=Constants.CN.USER, keyGenerator=Constants.MULTI_KEY_GENERATOR),
			@CacheEvict(cacheNames=Constants.CN.USER_ROLE, keyGenerator=Constants.MULTI_KEY_GENERATOR),
			@CacheEvict(cacheNames=Constants.CN.OPERATION, allEntries=true)
	})
	public int removePhy(String[] ids) {
		return super.deletePhy(ids);
	}

	@Override
	public DataPage<User> page(String groupName, String userNo, String username, 
			String nickname, String realname, OpenClose status, 
			String email, String mobile, int pageNo, int pageSize) {
		return super.page(sql()
				.join(R.User.group, "g", JoinType.LEFT_OUTER_JOIN)
				.columnAll(User.class)
				.column(R.Group.name, R.User.groupName)
				.likeTrim(R.Group.name, groupName)
				.likeTrim(R.User.userNo, userNo)
				.likeTrim(R.User.username, username)
				.likeTrim(R.User.nickname, nickname)
				.likeTrim(R.User.realname, realname)
				.likeTrim(R.User.email, email)
				.likeTrim(R.User.mobile, mobile)
				.eq(R.User.status, status)
				.eq(R.User.deleted, YesNo.NO)
				.desc(R.User.sort)
				.desc(R.User.createTime)
				.limit(pageNo, pageSize)
				);
	}
	
	@Override
	public DataPage<UserWsBo> page(Integer groupId, String userNo,
			String username, String nickname, String realname,
			OpenClose status, String email, String mobile, String[] ids,
			Integer pageNo, Integer pageSize) {
		if(ids != null && ids.length == 0)
			return new DataPage<UserWsBo>();
		return super.page(sql()
				.eq(R.User.groupId, groupId)
				.likeTrim(R.User.userNo, userNo)
				.likeTrim(R.User.username, username)
				.likeTrim(R.User.nickname, nickname)
				.likeTrim(R.User.realname, realname)
				.likeTrim(R.User.email, email)
				.likeTrim(R.User.mobile, mobile)
				.eq(R.User.status, status)
				.eq(R.User.deleted, YesNo.NO)
				.in(R.User.id, ids)
				.desc(R.User.sort)
				.desc(R.User.createTime)
				.limit(pageNo, pageSize)
				, UserWsBo.class);
	}
	
	@Override
	@Log(title = "开启关闭用户", moduleType = ModuleType.USER)
	@Caching(evict={
			@CacheEvict(cacheNames=Constants.CN.USER, keyGenerator=Constants.MULTI_KEY_GENERATOR),
			@CacheEvict(cacheNames=Constants.CN.USER_ROLE, keyGenerator=Constants.MULTI_KEY_GENERATOR),
			@CacheEvict(cacheNames=Constants.CN.OPERATION, allEntries=true)
	})
	public int openClose(String[] ids, OpenClose status) {
		return super.updateStatus(ids, status);
	}
	
	@Override
	public User login(String username, String password) {
		User u = this.loadByUsername(username);
		if(u == null)
			return null;
		else if (!u.getPassword().equals(DigestUtils.md5Hex(password))) {
			Integer loginFail = u.getLoginFail() + 1;
			//用户状态为开启下，登录超过指定次数，则锁住该用户
			if(loginFail > Constants.loginFail
					&& u.getStatus() == OpenClose.OPEN)
				
				super.update(sql()
						.update(R.User.loginFail, loginFail)
						.update(R.User.status, OpenClose.CLOSE)
						.eq(R.User.id, u.getId())
						);
			return null;
		} else if (u.getStatus() == OpenClose.OPEN
					&& u.getLoginFail() > 0) {
			super.update(sql()
					.update(R.User.loginFail, 0)
					.eq(R.User.id, u.getId())
					);
		}
		return u;
	}
	
	@Override
	public boolean existUsername(String username, String excludeId) {
		return this.exist(sql()
				.eqAst(R.User.username, username)
				.eq(R.User.deleted, YesNo.NO)
				.ne(R.User.id, excludeId)
				);
	}

	@Override
	public boolean existNickname(String nickname, String excludeId) {
		return this.exist(sql()
				.eqAst(R.User.nickname, nickname)
				.eq(R.User.deleted, YesNo.NO)
				.ne(R.User.id, excludeId)
				);
	}

	@Override
	@Log(title = "修改用户昵称", moduleType = ModuleType.USER)
	@CacheEvict(cacheNames=Constants.CN.USER, key="#id")
	public int updateNickname(String nickname, String id) {
		Assert.notBlank(id, CodeMsgExt.PARAM_BLANK.fillArgs("主键"));
		
		Assert.isFalse(this.existNickname(nickname, id),
				CodeMsgExt.FAIL.msg("昵称{0}已被占用", nickname));
		return super.update(sql()
				.update(R.User.nickname, nickname)
				.eq(R.User.id, id)
				);
	}

	@Override
	public String getPwd(String id) {
		return super.get(sql()
				.column(R.User.password)
				.eq(R.User.deleted, YesNo.NO)
				.eqAst(R.User.id, id)
				, String.class);
	}

	@Override
	@Log(title = "修改用户密码", moduleType = ModuleType.USER)
	@CacheEvict(cacheNames=Constants.CN.USER, key="#id")
	public int updatePwd(String newPwd, String id) {
		return super.update(sql()
				.updateAst(R.User.password, newPwd)
				.eq(R.User.deleted, YesNo.NO)
				.eqAst(R.User.id, id)
				);
	}

	@Override
	@Log(title = "修改用户头像", moduleType = ModuleType.USER)
	@CacheEvict(cacheNames=Constants.CN.USER, key="#id")
	public int updatePortrait(String id, String portrait) {
		return super.update(sql()
				.updateNeed(R.User.pic, portrait)
				.eqAst(R.User.id, id)
				);
	}

	@Override
	@Log(title = "修改用户基本信息", moduleType = ModuleType.USER)
	@CacheEvict(cacheNames=Constants.CN.USER, key="#id")
	public int updateBaseInfo(String id, String nickname, String realname,
			String email, String mobile) {
		return super.update(sql()
				.updateAst(R.User.nickname, nickname)
				.updateNeed(R.User.realname, realname)
				.updateNeed(R.User.email, email)
				.updateNeed(R.User.mobile, mobile)
				.eqAst(R.User.id, id)
				);
	}

	@Override
	public YesNo isSuperAdmin(String id) {
		return super.get(sql()
				.column(R.User.isSuperAdmin)
				.eq(R.User.deleted, YesNo.NO)
				.eqAst(R.User.id, id)
				, YesNo.class);
	}

	@Override
	public Integer getGroupId(String id) {
		return super.get(sql()
				.column(R.User.groupId)
				.eq(R.User.deleted, YesNo.NO)
				.eqAst(R.User.id, id)
				, Integer.class);
	}
	
	@Override
	@CacheEvict(cacheNames=Constants.CN.USER)
	public boolean clearCache(String userId) throws ErrorContext {
		log.info("清除用户缓存 userId:{0}", userId);
		return true;
	}
	
}
