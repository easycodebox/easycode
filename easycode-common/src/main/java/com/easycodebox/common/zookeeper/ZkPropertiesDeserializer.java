package com.easycodebox.common.zookeeper;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.easycodebox.common.file.PropertiesUtils;

/**
 * zookeeper的Properties数据解析器
 * @author WangXiaoJin
 * 
 */
public class ZkPropertiesDeserializer implements ZkDeserializer<Properties> {

	@Override
	public Properties deserialize(byte[] data) throws ZkDeserializeException {
		if (data == null)
			return null;
		String str = new String(data);
		if (StringUtils.isBlank(str)) 
			return new Properties();
		else {
			Properties ps = new Properties();
			try {
				PropertiesUtils.load(ps, str);
			} catch (IOException e) {
				throw new ZkDeserializeException("ZkPropertiesDeserializer deserialize data error.", e);
			}
			return ps;
		}
	}

}
