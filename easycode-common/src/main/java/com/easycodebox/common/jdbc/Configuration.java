package com.easycodebox.common.jdbc;

import static com.easycodebox.common.jdbc.AnnotateUtils.fitAssociatedColumn;
import static com.easycodebox.common.jdbc.AnnotateUtils.fitColumn;
import static com.easycodebox.common.jdbc.AnnotateUtils.fitTableAnno;
import static com.easycodebox.common.jdbc.AnnotateUtils.isTransient;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.easycodebox.common.error.BaseException;
import com.easycodebox.common.validate.Assert;


/**
 * @author WangXiaoJin
 *
 */
public class Configuration implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected static ConcurrentHashMap<String, Table> tables = new ConcurrentHashMap<String, Table>();
	protected static ConcurrentHashMap<String, Class<?>> tableNames = new ConcurrentHashMap<String, Class<?>>();

	public static final Dialect dialect = new MySqlDialect();
	public static final TableRule tableRule = new DefaultTableRule();
	
	private static Set<String> filterFields = new HashSet<String>();
	
	public static void addAnnotatedClass(Class<?> clazz) {
		if(tables.containsKey(clazz.getName()))
			return;
		Table table = new Table();
		table.setEntityType(clazz);
		
		fitTableAnno(table, clazz);
		
		for(Class<?> curClazz = clazz; 
				curClazz != null && curClazz != Object.class; 
				curClazz = curClazz.getSuperclass()) {
			
			Field[] fields = curClazz.getDeclaredFields();
			for(Field field : fields) {
				//排除掉final 和 static 修饰的属性
				if(Modifier.isFinal(field.getModifiers())
						|| Modifier.isStatic(field.getModifiers())) 
					continue;
				if(!filterFields.contains(field.getName())) {
					/******** 获取Transient注解   **************/
					if(isTransient(field))
						continue;
					/******** 处理关联关系ManyToOne、OneToOne等   **************/
					if(fitAssociatedColumn(table, field))
						continue;
					fitColumn(table, field);
				}
			}
		}
		
		tables.putIfAbsent(clazz.getName(), table);
		tableNames.putIfAbsent(table.getName(), clazz);
	}
	
	/**
	 * 加载完所有的entity class后需要调用此方法来初始化各个entity之间的关联关系
	 * 分析JoinColumn、JoinColumns、JoinTable注解，自动加上默认值
	 */
	public static void initTablesAssociate() {
		for(Table table : tables.values()) {
			
			Map<String, AssociatedColumn> columns = table.getAssociatedColumns();
			for(AssociatedColumn ac : columns.values()) {
				if(ac instanceof OneToOne) {
					OneToOne oneToOne = (OneToOne)ac;
					if(oneToOne.getMappedBy() != null) {
						//关联关系有mappedBy属性，执行逻辑待考虑
					}else {
						JoinColumnObj[] joinColumns = autoAssociatedColumn(
								ac.getJoinColumns(), table, 
								getTable(oneToOne.getAssociatedClass()));
						ac.setJoinColumns(joinColumns);
					}
				}else if(ac instanceof OneToMany) {
					OneToMany oneToMany = (OneToMany)ac;
					if(oneToMany.getMappedBy() != null) {
						//关联关系有mappedBy属性，执行逻辑待考虑
					}else {
						JoinColumnObj[] joinColumns = autoAssociatedColumn(
								ac.getJoinColumns(), 
								getTable(oneToMany.getAssociatedClass()), table);
						ac.setJoinColumns(joinColumns);
					}
				}else if(ac instanceof ManyToOne) {
					ManyToOne manyToOne = (ManyToOne)ac;
					JoinColumnObj[] joinColumns = autoAssociatedColumn(
							ac.getJoinColumns(), table, 
							getTable(manyToOne.getAssociatedClass()));
					ac.setJoinColumns(joinColumns);
				}else if(ac instanceof ManyToMany) {
					ManyToMany manyToMany = (ManyToMany)ac;
					if(manyToMany.getMappedBy() != null) {
						//关联关系有mappedBy属性，执行逻辑待考虑
					}else {
						Class<?> entity = getEntityByTableName(manyToMany.getJoinTableName());
						Table joinTable = entity != null ? getTable(entity) 
								: new Table(manyToMany.getJoinTableName());
						JoinColumnObj[] joinColumns = autoAssociatedColumn(
								ac.getJoinColumns(), joinTable, table);
						ac.setJoinColumns(joinColumns);
						
						Table inverseTable = getTable(manyToMany.getAssociatedClass());
						JoinColumnObj[] inverseJoinColumns = autoAssociatedColumn(
								manyToMany.getInverseJoinColumns(), joinTable, inverseTable);
						manyToMany.setInverseJoinColumns(inverseJoinColumns);
					}
				}else {
					throw new BaseException("unknown AssociatedColumn class - {0}.", ac.getClass());
				}
			}
			
		}
	}
	
	/**
	 * 处理关联关系列，自动补全未初始化的属性,返回最新的处理过的JoinColumnObj[]
	 */
	private static JoinColumnObj[] autoAssociatedColumn(JoinColumnObj[] joinColumns, Table table, 
			Table refTable) {
		if(joinColumns == null || joinColumns.length == 0) {
			return generateJoinColumns(table, refTable);
		}
		List<PkColumn> pks = refTable.getPrimaryKeys();
		Assert.notEmpty(pks, "Entity {0} has no pk.", refTable.getEntityType());
		for(JoinColumnObj jc : joinColumns) {
			if(pks.size() > 1 && (jc.getName() == null || jc.getReferencedColumnName() == null)) {
				//自动创建joinColumn，并覆盖以前的
				return generateJoinColumns(table, refTable);
			}
			if(jc.getName() == null) {
				jc.setName(tableRule.generateFk(refTable.getName(), pks.get(0).getSqlName()));
			}
			if(jc.getReferencedColumnName() == null) {
				jc.setReferencedColumnName(pks.get(0).getSqlName());
			}
			if(jc.getTable() == null) {
				jc.setTable(table.getName());
			}
		}
		return joinColumns;
	}
	
	/**
	 * 如果关联注解（ManyToMany、ManyToOne、OneToOne等）没有JoinColumn或者JoinColumns注解时，则调用此方法自动生成JoinColumn。
	 * 如果关联表是联合主键，且JoinColumn没有初始化name或referencedColumnName属性时也会执行此方法
	 * @param table		主表（有外键的表）
	 * @param refTable	外键引用表
	 * @return
	 */
	private static JoinColumnObj[] generateJoinColumns(Table table, Table refTable) {
		List<PkColumn> refPks = refTable.getPrimaryKeys();
		JoinColumnObj[] objs = new JoinColumnObj[refPks.size()];
		for(int i = 0; i < refPks.size(); i++) {
			JoinColumnObj jco = JoinColumnObj.instance();
			jco.setTable(table.getName());
			jco.setName(tableRule.generateFk(refTable.getName(), refPks.get(i).getSqlName()));
			jco.setReferencedColumnName(refPks.get(i).getSqlName());
			objs[i] = jco;
		}
		return objs;
	}
	
	public static Table getTable(Class<?> entityClass) {
		return tables.get(entityClass.getName());
	}
	
	public static Table getTable(String entityName) {
		return tables.get(entityName);
	}
	
	public static Class<?> getEntityByTableName(String tableName) {
		return tableNames.get(tableName);
	}
	
}
