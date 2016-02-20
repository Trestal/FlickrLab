package com.lab.flickr.Util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Matt on 19/02/2016.
 */
public class Security {

	private static final String CHAR_ENCODING = "UTF-8";
	private static final String MD5 = "MD5";

	public static String computeMD5(String input) {
		return compute(input, MD5);
	}

	public static String compute(String input, String algorithm) {
		MessageDigest digest;
		StringBuffer sb = new StringBuffer();
		try {
			digest = MessageDigest.getInstance(algorithm);
			digest.reset();
			byte[] byteData = digest.digest(input.getBytes(CHAR_ENCODING));
			for (int i = 0, ii = byteData.length; i < ii; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1)); //Base16 encoding
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
