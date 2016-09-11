package com.easycodebox.auth.core.pojo.user;

import java.util.List;

import javax.persistence.*;

import com.easycodebox.auth.core.util.mybatis.GeneratedValue;
import com.easycodebox.auth.core.util.mybatis.GeneratorEnum;
import com.easycodebox.common.enums.entity.Gender;
import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.enums.entity.status.CloseStatus;
import com.easycodebox.jdbc.entity.AbstractOperateEntity;

/**
 * 用户 - 用户登录后台的用户
 * @author WangXiaoJin
 *
 */
@Entity
@Table(name="u_user")
public class User extends AbstractOperateEntity {

	private static final long serialVersionUID = 5454155825314635342L;
	
	/**
	 * 主键
	 */
	@Id
	@GeneratedValue(GeneratorEnum.USER_ID)
	private String id;
	
	/**
	 * 用户组ID
	 */
	private Integer groupId;
	
	/**
	 * 员工编号
	 */
	private String userNo;
	
	/**
	 * 用户名
	 */
	private String username;
	
	/**
	 * 昵称
	 */
	private String nickname;
	
	/**
	 * 密码
	 */
	private String password;
	
	/**
	 * 真实姓名
	 */
	private String realname;
	
	/**
	 * 状态
	 */
	private CloseStatus status;
	
	/**
	 * 是否是超级管理员，超级管理员具备任何权限
	 */
	private YesNo isSuperAdmin;
	
	/**
	 * 用户头像
	 */
	private String pic;
	
	/**
	 * 排序值
	 */
	private Integer sort;
	
	/**
	 * 性别
	 */
	private Gender gender;
	
	/**
	 * 邮箱
	 */
	private String email;
	
	/**
	 * 手机号
	 */
	private String mobile;
	
	/**
	 * 错误登录 - 连续错误登录次数，正确登录后清零
	 */
	private Integer loginFail;
	

	@ManyToOne
	@JoinColumn(name="groupId") 
	private Group group;
	
	@OneToMany(mappedBy="user")
	private List<UserRole> userRoles;
	
	/************ 冗余字段 *******************/
	/**
	 * 组名
	 */
	@Transient
	private String groupName;
	
	public User(){
	
	}

	public User(String id){
		this.id = id;
	}
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public Integer getGroupId() {
		return groupId;
	}
	
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	
	public String getUserNo() {
		return userNo;
	}
	
	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getRealname() {
		return realname;
	}
	
	public void setRealname(String realname) {
		this.realname = realname;
	}
	
	public CloseStatus getStatus() {
		return status;
	}
	
	public void setStatus(CloseStatus status) {
		this.status = status;
	}
	
	public YesNo getIsSuperAdmin() {
		return isSuperAdmin;
	}

	public void setIsSuperAdmin(YesNo isSuperAdmin) {
		this.isSuperAdmin = isSuperAdmin;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public Integer getSort() {
		return sort;
	}
	
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	public Gender getGender() {
		return gender;
	}
	
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getMobile() {
		return mobile;
	}
	
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public Integer getLoginFail() {
		return loginFail;
	}
	
	public void setLoginFail(Integer loginFail) {
		this.loginFail = loginFail;
	}
	
	public void setGroup(Group group){
		this.group = group;
	}
	
	public Group getGroup() {
		return group;
	}
	public void setUserRoles(List<UserRole> userRoles){
		this.userRoles = userRoles;
	}
	
	public List<UserRole> getUserRoles() {
		return userRoles;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}

