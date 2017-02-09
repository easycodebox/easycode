package com.easycodebox.common.tag;

import com.easycodebox.common.enums.EnumClassFactory;
import com.easycodebox.common.lang.CollectionUtils;
import com.easycodebox.common.lang.DataConvert;
import com.easycodebox.common.validate.Assert;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * @author WangXiaoJin
 *
 */
public abstract class EnumGroupTag extends AbstractHtmlTag {

	protected static final String DATA_TYPE_NAME = "NAME";
	protected static final String DATA_TYPE_VALUE = "VALUE";
	
	/**
	 * value的类型，两种选择 1.NAME 例：OPEN(0, "开启") 指的是OPEN。 2.VALUE 例：OPEN(0, "开启") 指的是0。默认dataType为 NAME
	 * 属性IteratorEnum不需要
	 */
	protected String dataType;
	protected String enumName;
	/**
	 * 属性IteratorEnum不需要
	 */
	protected String selectedValue;
	protected Integer begin;
	protected Integer end;
	protected String[] exclude;
	protected String[] include;
	
	@Override
	protected void init() {
		dataType = DATA_TYPE_NAME;
		enumName = selectedValue = null;
		begin = 0;
		end = null;
		exclude = include = null;
		super.init();
	}
	
	/**
	 * 更具指定的枚举类获取对应的数据集合
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected List<Enum<?>> getEnumList() {
		Assert.notNull(enumName, "enumName can't be null.");
		
		Class<? extends Enum<?>> enumClass 
				= (Class<? extends Enum<?>>)EnumClassFactory.newInstance(enumName);
		List<Enum<?>> enumsList;
		if(include != null && include.length > 0) {
			enumsList = new ArrayList<>(6);
			for(int i = 0; i < include.length; i++) {
				enumsList.add(Enum.valueOf((Class)enumClass, include[i]));
			}
		}else {
			Enum<?>[] enums = enumClass.getEnumConstants();
			enumsList = CollectionUtils.toList(enums);
		}
		//排除enums需要排除的值
		if(exclude != null && exclude.length > 0) {
			for(int j = 0; j < exclude.length; j++) {
				Enum e = Enum.valueOf((Class)enumClass, exclude[j]);
				enumsList.remove(e);
			}
		}
		return enumsList;
	}

	public void setEnumName(String enumName) {
		this.enumName = obtainVal(enumName, String.class);
	}

	public void setSelectedValue(Object selectedValue) {
		Object selected = obtainVal(selectedValue, Object.class);
		if(selected == null)
			this.selectedValue = null;
		else if(selected instanceof Enum)
			this.selectedValue = ((Enum<?>)selected).name();
		else
			this.selectedValue = selected.toString();
	}

	public void setBegin(Object begin) {
		this.begin = obtainVal(begin, Integer.class);
	}

	public void setEnd(Object end) {
		this.end = obtainVal(end, Integer.class);
	}

	@SuppressWarnings("unchecked")
    public void setExclude(String exclude) {
    	if(StringUtils.isNotBlank(exclude)) {
    		Object stackVal = obtainVal(exclude, Object.class);
    		if(stackVal instanceof String) {
    			this.exclude = DataConvert.convertArray((String)stackVal, String[].class);
    		}else if(stackVal instanceof Collection) {
    			this.exclude = ((Collection<String>)stackVal).toArray(this.exclude);
    		}else if(stackVal.getClass().isArray())
    			this.exclude = (String[])stackVal;
    	}
	}
    
    @SuppressWarnings("unchecked")
    public void setInclude(String include) {
    	if(StringUtils.isNotBlank(include)) {
    		Object stackVal = obtainVal(include, Object.class);
    		if(stackVal instanceof String) {
    			this.include = DataConvert.convertArray((String)stackVal, String[].class);
    		}else if(stackVal instanceof Collection) {
    			this.include = ((Collection<String>)stackVal).toArray(this.include);
    		}else if(stackVal.getClass().isArray())
    			this.include = (String[])stackVal;
    	}
	}
    
	public void setDataType(String dataType) {
		if(StringUtils.isNotBlank(dataType))
			this.dataType = dataType.toUpperCase();
	}
	
}
