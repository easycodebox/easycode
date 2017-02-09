package com.easycodebox.auth.core.idconverter;

import com.easycodebox.auth.core.service.user.UserService;
import com.easycodebox.auth.model.entity.user.User;
import com.easycodebox.auth.model.util.R;
import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.tag.IdConverter;
import com.easycodebox.jdbc.support.JdbcHandler;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;

/**
 * 用户ID转换器
 * @author WangXiaoJin
 *
 */
public class UserIdConverter implements IdConverter {

	public static final String NICKNAME = R.User.nickname.getPropertyName();
	
	public static final String REALNAME = R.User.realname.getPropertyName();
	
	public static final String USERNAME = R.User.username.getPropertyName();
	
	@Resource
	private UserService userService;
	@Resource
	private JdbcHandler jdbcHandler;
	
	/**
	 * @param prop （可选） 某些情况下需要提供对象的属性名，特别是提供不同的属性名显示不同值的场景。
	 * 	例："nickname", "realname || nickname"，"||"和java的“逻辑或”是一个意思，依次取属性直到某个属性的值不为null为止。
	 */
	@Override
	public Object convert(Object id, String prop) {
		if (id == null)
			return null;
		else {
			User val;
			if (jdbcHandler.getSysUserId().equals(id.toString())) {
				val = new User();
				val.setId(id.toString());
				val.setIsSuperAdmin(YesNo.YES);
				val.setNickname(jdbcHandler.getSysUsername());
				val.setRealname(jdbcHandler.getSysUsername());
			} else
				val = userService.load(id.toString());
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
	 * 
	 * @param id
	 * @return 返回realname
	 */
	public String id2Realname(Object id) {
		User user = (User)this.convert(id, null);
		return user == null ? null : user.getRealname();
	}

	/**
	 * 
	 * @param id
	 * @return 返回nickname
	 */
	public String id2Nickname(Object id) {
		User user = (User)this.convert(id, null);
		return user == null ? null : user.getNickname();
	}
	
	/**
	 * 
	 * @param id
	 * @return 优先返回realname，realname为null则返回nickname
	 */
	public String id2RealOrNickname(Object id) {
		User user = (User)this.convert(id, null);
		return user == null ? null : user.getRealname() == null ? user.getNickname() : user.getRealname();
	}
	
}
