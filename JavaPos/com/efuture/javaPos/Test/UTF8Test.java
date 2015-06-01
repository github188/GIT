package com.efuture.javaPos.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import com.efuture.commonKit.UnicodeReader;


public class UTF8Test {
	public static void main(String[] args) throws IOException {
		File f  = new File("D:\\Code_Java\\tfsworkspace\\ConfigFile\\AutoTest.ini");
		FileInputStream in = new FileInputStream(f);
		// ָ����ȡ�ļ�ʱ��UTF-8�ĸ�ʽ��ȡ
		//BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		BufferedReader br = new BufferedReader(new UnicodeReader(in, Charset.defaultCharset().name()));
		
		String line = br.readLine();
		while(line != null)
		{
			System.out.println(line);
			line = br.readLine();
		}
	}
}
