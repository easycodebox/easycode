package com.easycodebox.jdbc.util;

import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.lang.Strings;
import com.easycodebox.common.lang.reflect.Classes;
import com.easycodebox.common.validate.Assert;
import com.easycodebox.common.idgenerator.AbstractIdGenTypeParser;
import com.easycodebox.common.idgenerator.IdGeneratedValue;
import com.easycodebox.jdbc.*;
import com.easycodebox.jdbc.Column;
import com.easycodebox.jdbc.ManyToMany;
import com.easycodebox.jdbc.ManyToOne;
import com.easycodebox.jdbc.OneToMany;
import com.easycodebox.jdbc.OneToOne;
import com.easycodebox.jdbc.Table;

import javax.persistence.*;
import java.lang.reflect.*;
import java.util.Collection;
import java.util.List;

/**
 * @author WangXiaoJin
 *
 */
public class AnnotateUtils {
	
	/**
	 * 加载并解析table annotation
	 */
	public static void fitTableAnno(Table table, Class<?> entity) {
		javax.persistence.Table tableAnno = entity.getAnnotation(javax.persistence.Table.class);
		if(tableAnno == null)
			table.setName(entity.getSimpleName());
		else {
			table.setName(tableAnno.name());
			table.setCatalog(tableAnno.catalog());
			table.setSchema(tableAnno.schema());
		}
	}
	
	/**
	 * 加载并解析Column annotation(包含Id annotation)
	 * @param table
	 * @param field
	 * @return
	 */
	public static void fitColumn(Table table, Field field) {
		Column column;
		//获取Id注解
		javax.persistence.Id idAnno = field.getAnnotation(javax.persistence.Id.class);
		if(idAnno != null) {
			column = new PkColumn(field.getName());
			column.setPrimaryKey(true);
			table.addPrimaryKey((PkColumn)column);
			//只要value的值实现了GeneratorType就可以
			IdGeneratedValue idGeneratedValue = field.getAnnotation(IdGeneratedValue.class);
			if (idGeneratedValue != null) {
				((PkColumn)column).setIdGeneratorType(AbstractIdGenTypeParser.parseIdGeneratedValue(idGeneratedValue));
			}
		}else {
			column = new Column(field.getName());
			column.setPrimaryKey(false);
		}
		
		column.setType(field.getType());
		//获取Column注解
		javax.persistence.Column columnAnno = field.getAnnotation(javax.persistence.Column.class);
		if(columnAnno == null) {
			column.setSqlName(field.getName());
		}else {
			column.setSqlName(columnAnno.name());
		}
		
		table.addColumn(column);
	}
	
	public static boolean isTransient(Field field) {
		return field.getAnnotation(javax.persistence.Transient.class) != null;
	}
	
	/**
	 * 处理关联关系ManyToOne、OneToOne等
	 * @return true 表明此属性是关联关系属性
	 */
	public static boolean fitAssociatedColumn(Table table, Field field) {
		AssociatedColumn associated = null;
		javax.persistence.OneToOne oneToOneAnno
			= field.getAnnotation(javax.persistence.OneToOne.class);
		if(oneToOneAnno != null) {
			associated = new OneToOne();
			if(!Classes.isVoid(oneToOneAnno.targetEntity()))
				associated.setAssociatedClass(oneToOneAnno.targetEntity());
			if(Strings.isNotBlank(oneToOneAnno.mappedBy()))
				((OneToOne)associated).setMappedBy(oneToOneAnno.mappedBy());
		}
		if(associated == null) {
			javax.persistence.ManyToOne manyToOneAnno
				= field.getAnnotation(javax.persistence.ManyToOne.class);
			if(manyToOneAnno != null) {
				associated = new ManyToOne();
				if(!Classes.isVoid(manyToOneAnno.targetEntity()))
					associated.setAssociatedClass(manyToOneAnno.targetEntity());
			}
		}
		if(associated == null) {
			javax.persistence.OneToMany oneToManyAnno
				= field.getAnnotation(javax.persistence.OneToMany.class);
			if(oneToManyAnno != null) {
				associated = new OneToMany();
				if(!Classes.isVoid(oneToManyAnno.targetEntity()))
					associated.setAssociatedClass(oneToManyAnno.targetEntity());
				if(Strings.isNotBlank(oneToManyAnno.mappedBy()))
					((OneToMany)associated).setMappedBy(oneToManyAnno.mappedBy());
			}
		}
		if(associated == null) {
			javax.persistence.ManyToMany manyToManyAnno
				= field.getAnnotation(javax.persistence.ManyToMany.class);
			if(manyToManyAnno != null) {
				associated = new ManyToMany();
				if(!Classes.isVoid(manyToManyAnno.targetEntity()))
					associated.setAssociatedClass(manyToManyAnno.targetEntity());
				if(Strings.isNotBlank(manyToManyAnno.mappedBy()))
					((ManyToMany)associated).setMappedBy(manyToManyAnno.mappedBy());
			}
		}
		
		if(associated != null) {
			associated.setPropertyName(field.getName());
			associated.setPropertyType(field.getType());
			if(associated.getAssociatedClass() == null) {
				associated.setAssociatedClass(getReferencedClass(field));
			}
			//添加关联关系
			if(associated instanceof ManyToMany) {
				JoinTable joinTable = field.getAnnotation(JoinTable.class);
				if(joinTable != null) {
					ManyToMany manyToMany = (ManyToMany)associated;
					manyToMany.setJoinTableName(joinTable.name());
					manyToMany.setCatalog(joinTable.catalog());
					manyToMany.setSchema(joinTable.schema());
					manyToMany.setJoinColumns(JoinColumnObj.transfer(joinTable.joinColumns()));
					manyToMany.setInverseJoinColumns(JoinColumnObj.transfer(joinTable.inverseJoinColumns()));
				}
			}else {
				JoinColumns joinColumns = field.getAnnotation(JoinColumns.class);
				if(joinColumns != null && joinColumns.value().length > 0) {
					associated.setJoinColumns(JoinColumnObj.transfer(joinColumns.value()));
				}else {
					JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
					if(joinColumn != null) {
						associated.setJoinColumns(new JoinColumnObj[]{JoinColumnObj.transfer(joinColumn)});
					}
				}
			}
			
			table.addAssociatedColumn(field.getName(), associated);
		}
		
		return associated != null;
	}
	
	/**
	 * 获取指定属性的引用类，如果属性类型使用了泛型，则返回泛型
	 * @param field
	 * @return
	 */
	public static Class<?> getReferencedClass(Field field) {
		Class<?> clazz;
		if(field.getType().isArray()) {
			clazz = field.getType().getComponentType();
		}else if(Collection.class.isAssignableFrom(field.getType())) {
			Type type = field.getGenericType();
			if(type instanceof ParameterizedType) {
				Type[] acTypes = ((ParameterizedType)type).getActualTypeArguments();
				Assert.notEmpty(acTypes, "The field {0} has no generic type.", field);
				clazz = (Class<?>)acTypes[0];
			}else {
				throw new BaseException("The field {0} has no generic type.", field);
			}
		}else {
			clazz = field.getType();
		}
		return clazz;
	}
	
	/**
	 * 获取entity的主键
	 * @param entityClass
	 * @return
	 */
	public static List<PkColumn> getPrimaryKeys(Class<?> entityClass) {
		Table table = com.easycodebox.jdbc.config.Configuration.getTable(entityClass);
		List<PkColumn> pks = table.getPrimaryKeys();
		Assert.notEmpty(pks, "{0} class has no @id", entityClass.getName());
		return pks;
	}
	
}
