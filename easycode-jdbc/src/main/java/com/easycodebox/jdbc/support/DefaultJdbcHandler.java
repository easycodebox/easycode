package com.easycodebox.jdbc.support;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;

import com.easycodebox.common.enums.entity.YesNo;
import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.security.SecurityUtils;
import com.easycodebox.jdbc.Property;
import com.easycodebox.jdbc.entity.CreateEntity;
import com.easycodebox.jdbc.entity.Entity;
import com.easycodebox.jdbc.entity.ModifyEntity;
import com.easycodebox.jdbc.grammar.SqlGrammar;

/**
 * @author WangXiaoJin
 *
 */
public class DefaultJdbcHandler implements JdbcHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultJdbcHandler.class);
	
	private Object sysUserId = "0";
	private String sysUsername = "系统";
	private String creatorPropName = "creator";
	private String createTimePropName = "createTime";
	private String modifierPropName = "modifier";
	private String modifyTimePropName = "modifyTime";
	private String statusPropName = "status";
	private String deletedPropName = "deleted";
	private Object deletedValue = YesNo.YES;
	
	private SecurityUser securityUser = new SecurityUser() {

		@Override
		public Object getUserId() {
			return SecurityUtils.getUserId();
		}

	};
	
	private DateFactory dateFactory = new DateFactory() {

		@Override
		public Object instance() {
			return new Date();
		}
		
	};
	
	@Override
	public void beforeSave(Entity entity) {
		Object userId = securityUser.getUserId() == null ? sysUserId : securityUser.getUserId();
		if(entity instanceof CreateEntity) {
			CreateEntity e = (CreateEntity) entity;
			if(e.getCreator() == null)
				e.setCreator((String)userId);
			if(e.getCreateTime() == null)
				e.setCreateTime((Date)dateFactory.instance());
		}else {
			setBeanProperty(entity, creatorPropName, userId);
			setBeanProperty(entity, createTimePropName, dateFactory.instance());
		}
		beforeUpdate(entity);
	}

	@Override
	public void beforeUpdate(Entity entity) {
		Object userId = securityUser.getUserId() == null ? sysUserId : securityUser.getUserId();
		if(entity instanceof ModifyEntity) {
			ModifyEntity e = (ModifyEntity) entity;
			if(e.getModifier() == null)
				e.setModifier((String)userId);
			if(e.getModifyTime() == null)
				e.setModifyTime((Date)dateFactory.instance());
		}else {
			setBeanProperty(entity, modifierPropName, userId);
			setBeanProperty(entity, modifyTimePropName, dateFactory.instance());
		}
	}
	
	@Override
	public void beforeUpdate(SqlGrammar sqlGrammar) {
		Object userId = securityUser.getUserId() == null ? sysUserId : securityUser.getUserId();
		boolean isModifyEntity = ModifyEntity.class.isAssignableFrom(sqlGrammar.getEntity());
		if(!updateSqlHad(sqlGrammar, modifierPropName)
				&& (
						isModifyEntity
						|| existProperty(sqlGrammar.getEntity(), modifierPropName)
						)) {
			sqlGrammar.update(Property.instance(modifierPropName, sqlGrammar.getEntity(), false), userId);
		}
		if(!updateSqlHad(sqlGrammar, modifyTimePropName)
				&& (
						isModifyEntity
						|| existProperty(sqlGrammar.getEntity(), modifyTimePropName)
						)) {
			sqlGrammar.update(Property.instance(modifyTimePropName, sqlGrammar.getEntity(), false), dateFactory.instance());
		}
	}
	
	/**
	 * 验证是否存在指定的property
	 * @param clazz
	 * @param name
	 * @return
	 */
	private boolean existProperty(Class<?> clazz, String name) {
		PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(clazz);
		for (PropertyDescriptor pd : pds) {
			if (name.equals(pd.getName())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 设置指定属性的值，该属性值为null设值，不为null则不设值
	 */
	private void setBeanProperty(Object obj, String name, Object val) {
		PropertyDescriptor prop = null;
		PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(obj);
		for (PropertyDescriptor pd : pds) {
			if (name.equals(pd.getName())) {
				prop = pd;
				break;
			}
		}
		if(prop != null) {
			try {
				Method read = prop.getReadMethod();
				if (!Modifier.isPublic(read.getModifiers())) {
					read.setAccessible(true);
				}
				if (read.invoke(obj) == null) {
					Method write = prop.getWriteMethod();
					if (!Modifier.isPublic(write.getModifiers())) {
						write.setAccessible(true);
					}
					//设置值
					write.invoke(obj, val);
				}
			} catch (Exception e) {
				LOG.warn("read or write object({0}) property({1}) error.", e, obj, name);
			}
		}
	}
	
	private boolean updateSqlHad(SqlGrammar sqlGrammar, String propertyName) {
		String updSql = sqlGrammar.getUpdateSql().toString();
		if(updSql.matches("(\\s+|,|\\.)" + propertyName + "(\\s+|,)")) {
			return true;
		}
		return false;
	}
	
	/**
	 * 获取用户信息接口
	 * @author WangXiaoJin
	 *
	 */
	public static interface SecurityUser {
		
		/**
		 * 返回当前操作人id
		 * @return
		 */
		Object getUserId();
		
	}
	
	/**
	 * 创建日期类型的工厂
	 * @author WangXiaoJin
	 *
	 */
	public static interface DateFactory {
		
		Object instance();
		
	}
	
	public Object getSysUserId() {
		return sysUserId;
	}

	public void setSysUserId(Object sysUserId) {
		this.sysUserId = sysUserId;
	}

	public SecurityUser getSecurityUser() {
		return securityUser;
	}

	public void setSecurityUser(SecurityUser securityUser) {
		this.securityUser = securityUser;
	}

	public DateFactory getDateFactory() {
		return dateFactory;
	}

	public void setDateFactory(DateFactory dateFactory) {
		this.dateFactory = dateFactory;
	}

	public String getSysUsername() {
		return sysUsername;
	}

	public void setSysUsername(String sysUsername) {
		this.sysUsername = sysUsername;
	}

	public String getCreatorPropName() {
		return creatorPropName;
	}

	public void setCreatorPropName(String creatorPropName) {
		this.creatorPropName = creatorPropName;
	}

	public String getCreateTimePropName() {
		return createTimePropName;
	}

	public void setCreateTimePropName(String createTimePropName) {
		this.createTimePropName = createTimePropName;
	}

	public String getModifierPropName() {
		return modifierPropName;
	}

	public void setModifierPropName(String modifierPropName) {
		this.modifierPropName = modifierPropName;
	}

	public String getModifyTimePropName() {
		return modifyTimePropName;
	}

	public void setModifyTimePropName(String modifyTimePropName) {
		this.modifyTimePropName = modifyTimePropName;
	}

	public String getStatusPropName() {
		return statusPropName;
	}

	public void setStatusPropName(String statusPropName) {
		this.statusPropName = statusPropName;
	}

	public String getDeletedPropName() {
		return deletedPropName;
	}

	public void setDeletedPropName(String deletedPropName) {
		this.deletedPropName = deletedPropName;
	}

	public Object getDeletedValue() {
		return deletedValue;
	}

	public void setDeletedValue(Object deletedValue) {
		this.deletedValue = deletedValue;
	}
	
}
