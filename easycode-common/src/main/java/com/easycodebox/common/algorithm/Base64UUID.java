package com.easycodebox.common.algorithm;

import java.util.UUID;
import org.apache.commons.codec.binary.Base64;

/**
 * @author WangXiaoJin
 * 
 */
public class Base64UUID {
	
	public static String compressUUID() {
		return compressUUID(UUID.randomUUID());
	}
	
	public static String compressUUID(String uuid) {
		return compressUUID(UUID.fromString(uuid));
	}

	public static String compressUUID(UUID uuid) {
		byte[] byUuid = new byte[16];
		long least = uuid.getLeastSignificantBits();
		long most = uuid.getMostSignificantBits();
		long2bytes(most, byUuid, 0);
		long2bytes(least, byUuid, 8);
		String compressUUID = Base64.encodeBase64URLSafeString(byUuid);
		return compressUUID;
	}

	protected static void long2bytes(long value, byte[] bytes, int offset) {
		for (int i = 7; i > -1; i--) {
			bytes[offset++] = (byte) ((value >> 8 * i) & 0xFF);
		}
	}

	public static String uncompress(String compressedUuid) {
		if (compressedUuid.length() != 22) {
			throw new IllegalArgumentException("Invalid uuid!");
		}
		byte[] byUuid = Base64.decodeBase64(compressedUuid + "==");
		long most = bytes2long(byUuid, 0);
		long least = bytes2long(byUuid, 8);
		UUID uuid = new UUID(most, least);
		return uuid.toString();
	}

	protected static long bytes2long(byte[] bytes, int offset) {
		long value = 0;
		for (int i = 7; i > -1; i--) {
			value |= (((long) bytes[offset++]) & 0xFF) << 8 * i;
		}
		return value;
	}
	
	public static void main(String[] args) {
			System.out.println(Base64UUID.compressUUID("f6e0e040-be3d-4573-964c-88724a8fa7d3"));
			System.out.println(Base64UUID.uncompress("9uDgQL49RXOWTIhySo-n0w"));
	}
	
}
