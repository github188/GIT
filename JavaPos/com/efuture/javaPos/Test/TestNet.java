package com.efuture.javaPos.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Communication.Http;

public class TestNet {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自动生成方法存根
		//String textname = args[0].trim();
		//String syjh = args[1].trim();
		//String fphm = args[2].trim();
		//String HttpPath = args[3].trim();
		//new TestNet(textname,syjh,fphm,HttpPath);
		new TestNet();
	}
	
	
	//tn:文件名称,syjh:收银机号,fphm:小票号 httpPath:对应发送的IP地址
	public TestNet()
	{
        BufferedReader br = null;
        InputStreamReader read = null;

        try
        {
            read = new InputStreamReader(new FileInputStream(new File("E:\\a.txt")),
                                         "GB2312");
            br   = new BufferedReader(read);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        finally
        {
            read = null;
        }
        
        PrintWriter pw = CommonMethod.writeFile("file.txt");
        
        //long num=Long.parseLong(fphm);

	        String line = "";
	        String line1= null;
	        try {
				while ((line1 = br.readLine())!= null)
				{
					line+=line1;
				}
				
				String a1 = line.substring(0,line.indexOf("^ABC^"));
				line = line.substring(line.indexOf("^ABC^")+5);
				String a2 = line.substring(0,line.indexOf("^ABC^"));
				line = line.substring(line.indexOf("^ABC^")+5);
				
				Http abc = new Http("172.16.2.11",9080,"/PosServerPos/PosServer");
				abc.init();
				
		        if (true)
		        {
		        	//fphm = String.valueOf(num);	
					String newLine = a1+"0004"+a2+"31116"+line;
					abc.setRequestString(newLine);
					String f = abc.execute();
					System.out.println(f);
					pw.println(f);
					pw.flush();
					//num++;
					Thread.sleep(1000);
		        }
				
			} catch (Exception e) {
				// TODO 自动生成 catch 块
				e.printStackTrace();
			}
			
			
        
		
	}
	
	

}
