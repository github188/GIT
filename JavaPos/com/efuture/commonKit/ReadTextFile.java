package com.efuture.commonKit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.efuture.javaPos.Struct.ShortcutKeyDef;

//这个类用来管理文件
public class ReadTextFile
{
		UnicodeReader read = null;
		BufferedReader br = null;
		String classname;
		
		//load .txt file
		public boolean loadFile(String classname,String encoding)
		{
			this.classname = classname;
			
			try
			{
				if (encoding == null || encoding.trim().length() <= 0) encoding = "UTF-8";
				read = new UnicodeReader (new FileInputStream(new File(classname)),encoding);
				br = new BufferedReader(read);
				return true;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		public boolean loadFile(String classname)
		{
			return loadFile(classname,"UTF-8");
		}
		
		public boolean loadFileByGBK(String classname)
		{
			return loadFile(classname,"GBK");
		}
		
		public void close()
		{
			try
			{
				if (br != null)
				{
					br.close();
					br = null;
				}
				if (read != null)
				{
					read.close();
					read = null;
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		//下一条记录
		public String nextRecord()
		{
			try
			{
				String line = br.readLine();
				
				if (line != null)
				{
					return line;
				}
				else 
				{
					return null;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
		
		//写入文档
		public void writeFile(String[][] keypad,int len)
		{
			String line1 = null;

			int n;
			try
			{

			 OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File(classname)),"UTF-8");
			 PrintWriter out = new PrintWriter(new BufferedWriter(writer));
			 
			 for(int i=0;i<len;i++)
			 {	
				line1=keypad[i][0].trim();
				n=line1.getBytes().length-line1.length();
				keypad[i][0]=Convert.increaseChar(keypad[i][0].trim(),15-n);
				line1=keypad[i][1].trim();
				n=line1.getBytes().length-line1.length();
				keypad[i][1]=Convert.increaseChar(keypad[i][1].trim(),5-n);
				line1=keypad[i][2].trim();
				n=line1.getBytes().length-line1.length();
				keypad[i][2]=Convert.increaseChar(keypad[i][2].trim(),10-n);
				line1=keypad[i][3].trim();
				n=line1.getBytes().length-line1.length();
				keypad[i][3]=Convert.increaseChar(keypad[i][3].trim(),10-n);
				String line=keypad[i][0]+"\t= "+keypad[i][1]+",\t"+keypad[i][2]+",\t"+keypad[i][3];
				out.println(line);	
			 }
			 
			 out.close();
			 writer.close();
			}
			catch(IOException e)
			{
				System.out.println(e.getMessage());
			}
		}
		
		//写入快捷键文档
		public boolean writeFile(String filename,ArrayList keyList)
		{
			String line1 = null;
			
			try
			{
				OutputStreamWriter writer=new OutputStreamWriter(new FileOutputStream(new File(filename)),"UTF-8");
				PrintWriter out = new PrintWriter(new BufferedWriter(writer));
				
				for (int i = 0;i < keyList.size();i++)
				{
					ShortcutKeyDef skd = (ShortcutKeyDef)keyList.get(i);
					
					line1 = String.valueOf(skd.getShortcutKey());
					line1 = line1 + "     ="+skd.getKeyString();
					out.println(line1);
					
				}
				
				out.close();
				writer.close();
				return true;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return false;
			}
		}
		
		//写入字符串		
		public boolean writeFile(String filename,String info,String encoding)
		{
			try
			{
				if (encoding == null || encoding.trim().length() <= 0) encoding = "UTF-8";
				OutputStreamWriter writer=new OutputStreamWriter(new FileOutputStream(new File(filename)),encoding);
				PrintWriter out = new PrintWriter(new BufferedWriter(writer));
				out.println(info);
				out.close();
				
				return true;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return false;
			}
		}
		
		public boolean writeFile(String filename,String info)
		{
			return writeFile(filename,info,"UTF-8");
		}
}
		
		
		

