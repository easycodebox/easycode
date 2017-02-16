package com.easycodebox.auth.model.idconverter;

import com.easycodebox.auth.model.entity.user.User;
import com.easycodebox.auth.model.util.R;
import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.idconverter.UserIdConverter;
import com.easycodebox.jdbc.support.JdbcHandler;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * @author WangXiaoJin
 */
public abstract class AbstractUserIdConverter implements UserIdConverter {
	
	public static final String NICKNAME = R.User.nickname.getPropertyName();
	
	public static final String REALNAME = R.User.realname.getPropertyName();
	
	public static final String USERNAME = R.User.username.getPropertyName();
	
	private JdbcHandler jdbcHandler;
	
	public abstract User getUserById(Object id);
	
	/**
	 * @param prop （可选） 某些情况下需要提供对象的属性名，特别是提供不同的属性名显示不同值的场景。
	 *             例："nickname", "realname || nickname"，"||"和java的“逻辑或”是一个意思，依次取属性直到某个属性的值不为null为止。
	 */
	@Override
	public Object convert(Object id, String prop) {
		if (id == null)
			return null;
		else {
			User val;
			if (jdbcHandler != null && jdbcHandler.getSysUserId().equals(id)) {
				val = new User();
				val.setId(id.toString());
				val.setIsSuperAdmin(YesNo.YES);
				val.setNickname(jdbcHandler.getSysUsername());
				val.setRealname(jdbcHandler.getSysUsername());
			} else
				val = getUserById(id);
			if (val != null && StringUtils.isNotBlank(prop)) {
				String[] frags = prop.split("\\|\\|");
				Object newVal = null;
				for (String frag : frags) {
					try {
						if (NICKNAME.equals(frag)) {
							newVal = val.getNickname();
						} else if (REALNAME.equals(frag)) {
							newVal = val.getRealname();
						} else if (USERNAME.equals(frag)) {
							newVal = val.getUsername();
						} else {
							newVal = PropertyUtils.getProperty(val, frag.trim());
						}
						if (newVal != null)
							break;
					} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
						
					}
				}
				return newVal;
			} else {
				return val;
			}
		}
	}
	
	/**
	 * @param id
	 * @return 返回realname
	 */
	@Override
	public String idToRealname(Object id) {
		return (String) this.convert(id, REALNAME);
	}
	
	/**
	 * @param id
	 * @return 返回nickname
	 */
	@Override
	public String idToNickname(Object id) {
		return (String) this.convert(id, NICKNAME);
	}
	
	/**
	 * @param id
	 * @return 优先返回realname，realname为null则返回nickname
	 */
	@Override
	public String idToRealOrNickname(Object id) {
		String realname = idToRealname(id);
		return realname == null ? idToNickname(id) : realname;
	}
	
	public JdbcHandler getJdbcHandler() {
		return jdbcHandler;
	}
	
	public void setJdbcHandler(JdbcHandler jdbcHandler) {
		this.jdbcHandler = jdbcHandler;
	}
	
}
