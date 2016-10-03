package com.easycodebox.common.lang.dto;

import com.easycodebox.common.enums.DetailEnum;

/**
 * @author WangXiaoJin
 * 
 */
public class UserInfo extends AbstractBo {

	private static final long serialVersionUID = 1L;
	
	private String userId;
	private String username;
	private String nickname;
	private String realname;
	private Integer groupId;
	private String groupName;		//用户所属组织名
	private String pic;				//头像
	private String password;
	private DetailEnum<Integer> status;
	
	
	public UserInfo(){
		super();
	}
	
	public UserInfo(UserInfo userInfo){
		this(userInfo.getUserId(), userInfo.getUsername(), userInfo.getNickname(), userInfo.getRealname(),
				userInfo.getPic(), userInfo.getStatus());
	}
	
	/**
	 * 
	 * @param userId
	 * @param operator	执行操作后记录的名字，即POJO类的creator、modifier
	 * @param loginName
	 * @param state
	 */
	public UserInfo(String userId, String username, String nickname, String realname, String pic, 
			DetailEnum<Integer> status) {
		this(userId, username, nickname, realname, pic, status, null, null);
	}
	
	public UserInfo(String userId, String username, String nickname, String realname, String pic, 
			DetailEnum<Integer> status, Integer groupId, String groupName) {
		super();
		this.userId = userId;
		this.username = username;
		this.nickname = nickname;
		this.realname = realname;
		this.pic = pic;
		this.status = status;
		this.groupId = groupId;
		this.groupName = groupName;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public DetailEnum<Integer> getStatus() {
		return status;
	}

	public void setStatus(DetailEnum<Integer> status) {
		this.status = status;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}
	
}
