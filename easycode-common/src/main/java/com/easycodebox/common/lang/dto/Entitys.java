package com.easycodebox.common.lang.dto;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.exception.NestableRuntimeException;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.core.Converter;

import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.jdbc.Configuration;
import com.easycodebox.common.jpa.Column;
import com.easycodebox.common.jpa.Table;
import com.easycodebox.common.lang.reflect.FieldUtils;
import com.easycodebox.common.validate.Assert;

/**
 * @author WangXiaoJin
 * 
 */
public final class Entitys {
	//
	private static final ConcurrentHashMap<Class<?>, BeanCopier> BEAN_COPIERS = new ConcurrentHashMap<Class<?>, BeanCopier>();
	private static final ConcurrentHashMap<Class<?>, List<ColumnField>> COLUMN_FIELD_CACHE = new ConcurrentHashMap<Class<?>, List<ColumnField>>();

	/**
	 * 
	 */
	public static String getTableName(Class<? extends Entity> clazz) {
		// Precondition checking
		if(!clazz.isAnnotationPresent(Table.class)) {
			throw new IllegalArgumentException("failed to get table name for class: " + clazz.getName());
		}
		
		//
		return clazz.getAnnotation(Table.class).name();
	}
	
	/**
	 * 获取主键的值
	 * @param clazz	Entity的class类型
	 * @return	如果该Entity没有@Id注解，则return null，如果有一个则返回{val}，两个则返回{val1, val2}
	 * 			其中的val值可能为null，所以需要实时判断下
	 */
	public static Object[] getPkValues(Object target) {
		Assert.notNull(target, "target param must not be null.");
		Assert.isInstanceOf(Entity.class, target);
		com.easycodebox.common.jdbc.Table table = Configuration.getTable(target.getClass());
		if(table != null) {
			List<com.easycodebox.common.jdbc.PkColumn> pks = table.getPrimaryKeys();
			if(pks != null && pks.size() > 0) {
				Object[] vals = new Object[pks.size()];
				for(int i = 0; i < pks.size(); i++) {
					Object val = null;
					try {
						val = PropertyUtils.getSimpleProperty(target, pks.get(i).getName());
					} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
						throw new BaseException("Obtain object({0}) property({1}) error.", e, target, pks.get(i).getName());
					}
					vals[i] = val;
				}
				return vals;
			}
		}
		return null;
	}
	
	public static Entity copy(Entity entity) {
		// Precondition checking
		if(entity == null) {
			return null;
		}
		
		//
		final Class<? extends Entity> clazz = entity.getClass();
		try {
			//
			BeanCopier copier = BEAN_COPIERS.get(clazz);
			if(copier == null) {
				copier = BeanCopier.create(clazz, clazz, true);
				BeanCopier existing = BEAN_COPIERS.putIfAbsent(clazz, copier);
				if(existing != null) {
					copier = existing;
				}
			}
			
			//
			Entity target = clazz.newInstance();
			copier.copy(entity, target, new Converter() {
				@SuppressWarnings("rawtypes")
				public Object convert(Object value, Class target, Object context) {
					return value;
				}
			});
			return target;
		} catch(Exception e) {
			throw new NestableRuntimeException("failed to copy class: " + clazz, e); 
		}
	}

	/**
	 * 
	 */
	public static Map<String, Object> inspect(Entity entity) {
		// Precondition checking
		if(entity == null) {
			throw new IllegalArgumentException("invalid parameter entity");
		}
		
		//
		final Class<?> clazz = entity.getClass();
		try {
			//
			List<ColumnField> columnFields = COLUMN_FIELD_CACHE.get(clazz);
			if(columnFields == null) {
				//
				columnFields = new ArrayList<ColumnField>();
				List<Field> fields = FieldUtils.getAllFields(clazz, true);
				for(Field field : fields) {
					columnFields.addAll(getColumnFields(field));
				}
				
				//
				List<ColumnField> existing = COLUMN_FIELD_CACHE.putIfAbsent(clazz, columnFields);
				if(existing != null) {
					columnFields = existing;
				}
			}
			
			//
			Map<String, Object> r = new HashMap<String, Object>();
			for(ColumnField cf : columnFields) {
				r.put(cf.getColumn(), cf.getFieldValue(entity));
			}
			return r;
		} catch(Exception e) {
			throw new NestableRuntimeException("failed to inspect class: " + clazz, e); 
		}
	}
	
	protected static List<ColumnField> getColumnFields(Field field) throws Exception {
		//
		field.setAccessible(true);
		
		//
		List<ColumnField> r = new ArrayList<ColumnField>();
		if(field.isAnnotationPresent(Column.class)) {
			Column c = field.getAnnotation(Column.class);
			r.add(new ColumnField(c.name(), field));			
		}
		return r;
	}

	/**
	 * 
	 */
	protected static class ColumnField {
		//
		private String column;
		private List<Field> fields = new ArrayList<Field>();

		/**
		 * 
		 */
		public ColumnField() {
		}
		
		public ColumnField(String column, Field field) {
			this.column = column;
			this.fields.add(field);
		}
		
		/**
		 * 
		 */
		public Object getFieldValue(Object target) throws Exception {
			Object r = target;
			for(Field f : fields) {
				r = f.get(r);
			}
			return r;
		}
		
		/**
		 * 
		 */
		public String getColumn() {
			return column;
		}
		
		public void setColumn(String column) {
			this.column = column;
		}
		
		public List<Field> getFields() {
			return fields;
		}

		public void addField(Field field) {
			this.fields.add(field);
		}
	}
}
