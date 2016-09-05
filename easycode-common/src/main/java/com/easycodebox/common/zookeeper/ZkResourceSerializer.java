package com.easycodebox.common.zookeeper;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

/**
 * @author WangXiaoJin
 * 
 */
public class ZkResourceSerializer implements ZkSerializer {

	@Override
	public byte[] serialize(Object data) throws ZkSerializeException {
		InputStream is = null;
		try {
			if(data instanceof String) {
				is = ResourceUtils.getURL((String)data).openStream();
			}else if(data instanceof Resource) {
				is = ((Resource) data).getInputStream();
			}else if(data instanceof InputStream)
				is = (InputStream)data;
			
			return IOUtils.toByteArray(is);
		} catch (IOException e) {
			throw new ZkSerializeException("ZkResourceSerializer serialize error.", e);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	@Override
	public <T> boolean support(Class<T> clazz) {
		return String.class.isAssignableFrom(clazz)
				|| Resource.class.isAssignableFrom(clazz)
				|| InputStream.class.isAssignableFrom(clazz);
	}
	
}
