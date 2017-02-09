package com.easycodebox.common;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * @author WangXiaoJin
 * 
 */
public class Reference<T> implements Copyable, Externalizable {
	
	private static final long serialVersionUID = 4576376399671156792L;
	
	private T value;

	public Reference() {
	}
	
	public Reference(T value) {
		this.value = value;
	}
	
	public String toString() {
		return (value == null ? "-" : value.getClass().getSimpleName()) + "[" + value + "]";
	}
	
	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	@Override
	public Reference<T> copy() {
		return new Reference<T>(value);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(this.value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.value = (T)in.readObject();
	}
}
