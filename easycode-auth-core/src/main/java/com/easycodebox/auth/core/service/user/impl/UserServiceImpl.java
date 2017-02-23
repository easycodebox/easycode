package com.easycodebox.auth.core.service.user.impl;

import com.easycodebox.auth.core.config.CoreProperties;
import com.easycodebox.auth.core.service.user.UserService;
import com.easycodebox.auth.core.util.*;
import com.easycodebox.auth.core.util.aop.log.Log;
import com.easycodebox.auth.model.entity.user.User;
import com.easycodebox.auth.model.enums.ModuleType;
import com.easycodebox.auth.model.util.R;
import com.easycodebox.auth.model.enums.IdGeneratorEnum;
import com.easycodebox.common.enums.entity.*;
import com.easycodebox.common.error.ErrorContext;
import com.easycodebox.common.idgenerator.IdGenerators;
import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.lang.dto.DataPage;
import com.easycodebox.common.validate.Assert;
import com.easycodebox.jdbc.JoinType;
import com.easycodebox.jdbc.support.AbstractServiceImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author WangXiaoJin
 */
@Service("userService")
public class UserServiceImpl extends AbstractServiceImpl<User> implements UserService {
	
	@Resource
	private CoreProperties coreProperties;
	
	@Override
	public List<User> list() {
		return super.list(sql()
				.eq(R.User.isSuperAdmin, coreProperties.isModifySuperAdmin() ? null : YesNo.NO)
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
		
		if (Strings.isNotBlank(user.getNickname()))
			Assert.isFalse(this.existNickname(user.getNickname(), user.getId()),
					CodeMsgExt.FAIL.msg("昵称{0}已被占用", user.getNickname()));
		
		Assert.isFalse(this.existUsername(user.getUsername(), user.getId()),
				CodeMsgExt.FAIL.msg("用户名{0}已被占用", user.getUsername()));
		
		if (user.getStatus() == null)
			user.setStatus(OpenClose.OPEN);
		user.setLoginFail(0);
		if (Strings.isBlank(user.getRealname()))
			user.setRealname(null);
		if (Strings.isBlank(user.getPassword()))
			user.setPassword(DigestUtils.md5Hex(coreProperties.getResetPwd()));
		if (user.getSort() == null)
			user.setSort(0);
		if (Strings.isBlank(user.getMobile()))
			user.setMobile(null);
		if (Strings.isBlank(user.getEmail()))
			user.setEmail(null);
		if (Strings.isBlank(user.getPic()))
			user.setPic(null);
		if (Strings.isBlank(user.getNickname())) {
			user.setNickname(generateNickname());
		}
		//不能通过外部添加超级管理员
		user.setIsSuperAdmin(YesNo.NO);
		user.setDeleted(YesNo.NO);
		super.save(user);
		return user;
	}
	
	private String generateNickname() {
		String nickname;
		do {
			nickname = (String) IdGenerators.nextVal(IdGeneratorEnum.NICKNAME);
		} while (this.existNickname(nickname, null));
		return nickname;
	}
	
	@Override
	@Log(title = "修改用户", moduleType = ModuleType.USER)
	@Transactional
	@Caching(evict = {
			@CacheEvict(cacheNames = Constants.CN.USER, key = "#user.id"),
			//由于有可能修改group，导致角色有所变动
			@CacheEvict(cacheNames = Constants.CN.USER_ROLE, key = "#user.id"),
			//由于有可能修改group，导致权限有所变动
			@CacheEvict(cacheNames = Constants.CN.PERMISSION, allEntries = true)
	})
	public int update(User user) {
		
		if (Strings.isNotBlank(user.getNickname()))
			Assert.isFalse(this.existNickname(user.getNickname(), user.getId()),
					CodeMsgExt.FAIL.msg("昵称{0}已被占用", user.getNickname()));
		
		Assert.isFalse(this.existUsername(user.getUsername(), user.getId()),
				CodeMsgExt.FAIL.msg("用户名{0}已被占用", user.getUsername()));
		
		if (user.getStatus() != null) {
			log.info("The update method can not update status property.");
		}
		
		if (Strings.isBlank(user.getRealname()))
			user.setRealname(null);
		if (Strings.isBlank(user.getMobile()))
			user.setMobile(null);
		if (Strings.isBlank(user.getEmail()))
			user.setEmail(null);
		if (Strings.isBlank(user.getPic()))
			user.setPic(null);
		if (Strings.isBlank(user.getNickname())) {
			user.setNickname(generateNickname());
		}
		
		return super.update(sql()
				.upd(R.User.groupId, user.getGroupId())
				.upd(R.User.userNo, user.getUserNo())
				.updNonNull(R.User.username, user.getUsername())
				.upd(R.User.nickname, user.getNickname())
				.upd(R.User.realname, user.getRealname())
				.updNonNull(R.User.sort, user.getSort())
				.upd(R.User.gender, user.getGender())
				.upd(R.User.email, user.getEmail())
				.upd(R.User.mobile, user.getMobile())
				.eqAst(R.User.id, user.getId())
		);
	}
	
	@Override
	@Log(title = "逻辑删除用户", moduleType = ModuleType.USER)
	@Transactional
	@Caching(evict = {
			@CacheEvict(cacheNames = Constants.CN.USER, keyGenerator = Constants.MULTI_KEY_GENERATOR),
			@CacheEvict(cacheNames = Constants.CN.USER_ROLE, keyGenerator = Constants.MULTI_KEY_GENERATOR),
			@CacheEvict(cacheNames = Constants.CN.PERMISSION, allEntries = true)
	})
	public int remove(String[] ids) {
		return super.delete(ids);
	}
	
	@Override
	@Log(title = "物理删除用户", moduleType = ModuleType.USER)
	@Transactional
	@Caching(evict = {
			@CacheEvict(cacheNames = Constants.CN.USER, keyGenerator = Constants.MULTI_KEY_GENERATOR),
			@CacheEvict(cacheNames = Constants.CN.USER_ROLE, keyGenerator = Constants.MULTI_KEY_GENERATOR),
			@CacheEvict(cacheNames = Constants.CN.PERMISSION, allEntries = true)
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
				.eq(R.User.isSuperAdmin, coreProperties.isModifySuperAdmin() ? null : YesNo.NO)
				.eq(R.User.deleted, YesNo.NO)
				.desc(R.User.sort)
				.desc(R.User.createTime)
				.limit(pageNo, pageSize)
		);
	}
	
	@Override
	public DataPage<User> page(Integer groupId, String userNo,
	                           String username, String nickname, String realname,
	                           OpenClose status, String email, String mobile, String[] ids,
	                           Integer pageNo, Integer pageSize) {
		if (ids != null && ids.length == 0)
			return new DataPage<>();
		return super.page(sql()
				.eq(R.User.groupId, groupId)
				.likeTrim(R.User.userNo, userNo)
				.likeTrim(R.User.username, username)
				.likeTrim(R.User.nickname, nickname)
				.likeTrim(R.User.realname, realname)
				.likeTrim(R.User.email, email)
				.likeTrim(R.User.mobile, mobile)
				.eq(R.User.status, status)
				.eq(R.User.isSuperAdmin, coreProperties.isModifySuperAdmin() ? null : YesNo.NO)
				.eq(R.User.deleted, YesNo.NO)
				.in(R.User.id, ids)
				.desc(R.User.sort)
				.desc(R.User.createTime)
				.limit(pageNo, pageSize)
		);
	}
	
	@Override
	@Log(title = "开启关闭用户", moduleType = ModuleType.USER)
	@Transactional
	@Caching(evict = {
			@CacheEvict(cacheNames = Constants.CN.USER, keyGenerator = Constants.MULTI_KEY_GENERATOR),
			@CacheEvict(cacheNames = Constants.CN.USER_ROLE, keyGenerator = Constants.MULTI_KEY_GENERATOR),
			@CacheEvict(cacheNames = Constants.CN.PERMISSION, allEntries = true)
	})
	public int openClose(String[] ids, OpenClose status) {
		return super.updateStatus(ids, status);
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
	@Transactional
	@CacheEvict(cacheNames = Constants.CN.USER, key = "#id")
	public int updateNickname(String nickname, String id) {
		Assert.notBlank(id, CodeMsgExt.PARAM_BLANK.fillArgs("主键"));
		
		Assert.isFalse(this.existNickname(nickname, id),
				CodeMsgExt.FAIL.msg("昵称{0}已被占用", nickname));
		return super.update(sql()
				.upd(R.User.nickname, nickname)
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
	@Transactional
	@CacheEvict(cacheNames = Constants.CN.USER, key = "#id")
	public int updatePwd(String newPwd, String id) {
		return super.update(sql()
				.updAst(R.User.password, newPwd)
				.eq(R.User.deleted, YesNo.NO)
				.eqAst(R.User.id, id)
		);
	}
	
	@Override
	@Log(title = "修改用户头像", moduleType = ModuleType.USER)
	@Transactional
	@CacheEvict(cacheNames = Constants.CN.USER, key = "#id")
	public int updatePortrait(String id, String portrait) {
		return super.update(sql()
				.upd(R.User.pic, portrait)
				.eqAst(R.User.id, id)
		);
	}
	
	@Override
	@Log(title = "修改用户基本信息", moduleType = ModuleType.USER)
	@Transactional
	@CacheEvict(cacheNames = Constants.CN.USER, key = "#id")
	public int updateBaseInfo(String id, String nickname, String realname,
	                          String email, String mobile) {
		return super.update(sql()
				.updAst(R.User.nickname, nickname)
				.upd(R.User.realname, realname)
				.upd(R.User.email, email)
				.upd(R.User.mobile, mobile)
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
	@Transactional
	@CacheEvict(cacheNames = Constants.CN.USER)
	public boolean clearCache(String userId) throws ErrorContext {
		log.info("清除用户缓存 userId:{0}", userId);
		return true;
	}
	
}
