package com.easycodebox.common.lang.dto;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.easycodebox.common.lang.BuilderEntity;


/**
 * @author WangXiaoJin
 * 
 */
public abstract class AbstractEntity extends BuilderEntity implements Entity {

	private static final long serialVersionUID = -693263196512271119L;
	
	
	@Override
	public int hashCode() {
		Object[] pkVals = Entitys.getPkValues(this);
		boolean isNull = pkVals == null || pkVals.length == 0;
		int index = 0;
		HashCodeBuilder hashCode = new HashCodeBuilder();
		while(!isNull && index < pkVals.length) {
			Object val = pkVals[index++];
			if(val == null) {
				isNull = true;
				break;
			}
			hashCode.append(val);
		}
		if(isNull)
			return super.hashCode();
		else
			return hashCode.toHashCode();
	}
	
	@Override
	public boolean equals(Object target) {
		if(this == target) return true;
		if(target == null || !this.getClass().isAssignableFrom(target.getClass())) return false;
		Object[] thisPkVals = Entitys.getPkValues(this);
		Object[] targetPkVals = Entitys.getPkValues(target);
		if(thisPkVals != null 
				&& targetPkVals != null
				&& thisPkVals.length == targetPkVals.length
				&& thisPkVals.length > 0) {
			EqualsBuilder builder = new EqualsBuilder();
			for(int i = 0; i < thisPkVals.length; i++) {
				if(thisPkVals[i] == null || targetPkVals[i] == null)
					return super.equals(target);
				builder.append(thisPkVals[i],targetPkVals[i]);
			}
			return builder.isEquals();
		}else
			return super.equals(target);
	}
	
	@Override
	public Object copy() {
		return Entitys.copy(this);
	}
	
}
