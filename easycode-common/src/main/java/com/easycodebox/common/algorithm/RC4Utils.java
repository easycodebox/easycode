package com.easycodebox.common.algorithm;

import java.util.Date;


public class RC4Utils {

	private byte state[] = new byte[256];
	private int x;
	private int y;

	/**
	 * Initializes the class with a string key. The length of a normal key
	 * should be between 1 and 2048 bits. But this method doens't check the
	 * length at all.
	 * @param key the encryption/decryption key
	 */
	public RC4Utils(String key) throws NullPointerException {
		this(key.getBytes());
	}

	/**
	 * Initializes the class with a byte array key. The length of a normal key
	 * should be between 1 and 2048 bits. But this method doens't check the
	 * length at all.
	 * @param key the encryption/decryption key
	 */
	public RC4Utils(byte[] key) throws NullPointerException {

		for (int i = 0; i < 256; i++) {
			state[i] = (byte) i;
		}

		x = 0;
		y = 0;

		int index1 = 0;
		int index2 = 0;

		byte tmp;

		if (key == null || key.length == 0) {
			throw new NullPointerException();
		}

		for (int i = 0; i < 256; i++) {

			index2 = ((key[index1] & 0xff) + (state[i] & 0xff) + index2) & 0xff;

			tmp = state[i];
			state[i] = state[index2];
			state[index2] = tmp;

			index1 = (index1 + 1) % key.length;
		}

	}

	/**
	 * RC4 encryption/decryption.
	 * @param data the data to be encrypted/decrypted
	 * @return the result of the encryption/decryption
	 */
	public byte[] rc4(String data) {

		if (data == null) {
			return null;
		}

		byte[] tmp = data.getBytes();

		this.rc4(tmp);

		return tmp;
	}

	/**
	 * RC4 encryption/decryption.
	 * @param buf the data to be encrypted/decrypted
	 * @return the result of the encryption/decryption
	 */
	public byte[] rc4(byte[] buf) {

		// int lx = this.x;
		// int ly = this.y;
		int xorIndex;
		byte tmp;

		if (buf == null) {
			return null;
		}

		byte[] result = new byte[buf.length];

		for (int i = 0; i < buf.length; i++) {

			x = (x + 1) & 0xff;
			y = ((state[x] & 0xff) + y) & 0xff;

			tmp = state[x];
			state[x] = state[y];
			state[y] = tmp;

			xorIndex = ((state[x] & 0xff) + (state[y] & 0xff)) & 0xff;
			result[i] = (byte) (buf[i] ^ state[xorIndex]);
		}

		// this.x = lx;
		// this.y = ly;
		return result;
	}

	/**
	 * 字符串转java字节码
	 * @param b
	 * @return
	 */
	public static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0) {
			throw new IllegalArgumentException("length is wrong!");
		}
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			// 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		b = null;
		return b2;
	}


	public static String byteToString(byte[] bytes) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			int d = bytes[i];
			if (d < 0) {
				d += 256;
			}
			if (d < 16) {
				buf.append("0");
			}
			buf.append(Integer.toString(d, 16));
		}
		return buf.toString();
	}
	
	public static String encrypt(String val, String key) {
		if(val == null) throw new NullPointerException();
		return RC4Utils.byteToString((new RC4Utils(key).rc4(val.getBytes())));
	}
	
	public static String decrypt(String val, String key) {
		if(val == null) throw new NullPointerException();
		RC4Utils rc = new RC4Utils(key);
		byte[] ts = RC4Utils.hex2byte(val.getBytes());
		return new String(rc.rc4(ts));
	}
	
	public static void main(String[] args){
		System.out.println(encrypt(new Date().getTime() + "", "key"));
		System.out.println(decrypt("4cc11bef1829f3f080ebf2ef83f90956393ad53a3ffdae24a2f303207bc28aa31600559a2993dd870bb77d27bd176d7e25b8", "key"));
	}
	
}