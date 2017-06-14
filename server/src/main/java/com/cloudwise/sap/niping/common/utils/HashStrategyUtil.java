package com.cloudwise.sap.niping.common.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashStrategyUtil {

	private HashStrategyUtil(){};

	private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

	public static String computeHash(String password, String salt) throws Exception {
		String algo = "SHA-512";
		MessageDigest md = MessageDigest.getInstance(algo);
		String concat = (salt == null ? "" : salt) + password;
		byte[] bHash = md.digest(concat.getBytes(StandardCharsets.UTF_8));
		return bytesToHex(bHash);
	}

	public static String bytesToHex(byte[] bytes) {
		char[] chars = new char[bytes.length * 2];
		for (int i = 0; i < bytes.length; i++) {
			int x = 0xFF & bytes[i];
			chars[i * 2] = HEX_CHARS[x >>> 4];
			chars[1 + i * 2] = HEX_CHARS[0x0F & x];
		}
		return new String(chars);
	}

	public static void main(String[] args) throws Exception {
		String pwd = "sap118";
		String salt = "sap998";
		if (args != null && args.length == 2) {
			pwd = args[0];
			salt = args[1];
		}
		String encPassowrd = HashStrategyUtil.computeHash(pwd, salt);
		System.out.println("Password=" + pwd + "\nEncodePassword=" + encPassowrd + "\nSalt=" + salt);
	}
}
