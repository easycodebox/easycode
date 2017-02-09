package com.easycodebox.common.lang;

import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * @author WangXiaoJin
 * 
 */
public abstract class SerializationUtils {
	
	/**
	 * 
	 */
	public static Serializable copy(Serializable obj) {
		// Precondition checking
		if(obj == null) {
			return null;
		}
		return deserialize(serialize(obj));
	}
	
	/**
	 * 
	 */
	public static byte[] serialize(Serializable s)  {
		// Precondition checking
		if(s == null) {
			return null;
		}
		
		//
		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(s);
			oos.flush();
			return bos.toByteArray();
		} catch(Exception e) {
			throw new RuntimeException("failed to serialize s: " + s, e);
		} finally {
			IOUtils.closeQuietly(bos);
		}
	}
	
	public static Serializable deserialize(byte data[]) {
		// Precondition checking
		if(data == null || data.length == 0) {
			return null;
		}
		
		//
		return deserialize(data, 0, data.length);
	}
	
	public static Serializable deserialize(byte data[], int offset, int length) {
		// Precondition checking
		if(data == null || data.length == 0) {
			return null;
		}
		
		//
		ByteArrayInputStream bis = null;
		try {
			bis = new ByteArrayInputStream(data, offset, length);
			ObjectInputStream ois = new ObjectInputStream(bis);
			return (Serializable)ois.readObject();
		} catch(Exception e) {
			throw new RuntimeException("failed to deserialize", e);
		} finally {
			IOUtils.closeQuietly(bis);
		}
	}
}
