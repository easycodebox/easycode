package com.easycodebox.common.zookeeper;

import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.lang.SystemUtils;

import com.easycodebox.common.lang.Symbol;

/**
 * @author WangXiaoJin
 * 
 */
public class ZkPropertiesSerializer implements ZkSerializer {

	@Override
	public byte[] serialize(Object data) throws ZkSerializeException {
		Properties ps = (Properties)data;
		StringBuilder sb = new StringBuilder();
		Enumeration<Object> er = ps.keys();
		while(er.hasMoreElements()) {
			Object key = er.nextElement();
			sb.append(key).append(Symbol.EQ).append(ps.get(key)).append(SystemUtils.LINE_SEPARATOR);
		}
		return sb.toString().getBytes();
	}

	@Override
	public <T> boolean support(Class<T> clazz) {
		return Properties.class.isAssignableFrom(clazz);
	}
	
}
