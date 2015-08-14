package com.yjk.mobilesafety.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Utils {
	
	/*
	 * MD5加密方法
	 */
	public static String md5Password(String password){
		MessageDigest digest;
		StringBuffer buffer = new StringBuffer();
		try {
				digest = MessageDigest.getInstance("md5");
				byte[] result = digest.digest(password.getBytes());
				
				for(byte b : result){
					//与运算
					int number = b & 0xff;
					String str = Integer.toHexString(number);
					if(str.length() == 1){
						buffer.append("0");
					}
				
					buffer.append(str);
				}
				
				return buffer.toString();
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return "";
		}
		
		
	}
}
