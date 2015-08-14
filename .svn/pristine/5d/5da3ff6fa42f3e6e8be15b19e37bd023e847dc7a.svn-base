package com.yjk.mobilesafety.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamTools {
	/**
	 * @param is 输入流
	 * @return String 返回的字符串
	 * @throws IOException 
	 */
	public static String readFromStream(InputStream is) throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(is));  
        StringBuffer buffer = new StringBuffer();  
        String line = "";  
        while ((line = in.readLine()) != null){  
          buffer.append(line);  
        }  
       return buffer.toString();  
	}
}
