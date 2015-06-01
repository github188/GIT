package com.efuture.commonKit;


import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

//压缩并且解压缩
public class Unzip
{
	 public Unzip() 
	 {
	 	
	 }
	 
	 public void zip(ZipOutputStream out, File f, String base)
     {
         try 
         {
             //判断File是否为目录
             if (f.isDirectory()) 
             {
                 //获取f目录下所有文件及目录,作为一个File数组返回
                 File[] fl = f.listFiles();
                 out.putNextEntry(new ZipEntry(base + "/"));
                 base = base.length() == 0 ? "" : base + "/";
                 for (int i = 0; i < fl.length; i++) 
                 {
                     zip(out, fl[i], base + fl[i].getName());
                 }
             }
             else 
             {
                 out.putNextEntry(new ZipEntry(base));
                 FileInputStream in = new FileInputStream(f);
                 int b;
                 
                 while ((b = in.read()) != -1) 
                 {
                 	out.write(b);
                 }
                 
                 in.close();
             }
         }
         catch (FileNotFoundException e) 
		 {
             e.printStackTrace();
         }
         catch (IOException e) 
		 {
             e.printStackTrace();
		 }
     }
	 
	 //开始打包的方法
    public void zipAnt(String inputFileName) throws Exception 
    {     
        zipAnt(inputFileName+".zip",new File(inputFileName));  
    }

    private void zipAnt(String zipFileName, File inputFile) throws Exception 
    {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
        zipAnt(out, inputFile, "");
        out.close();
    }

    private void zipAnt(ZipOutputStream out, File f, String base) throws Exception
    {
    	if (f.isDirectory()) 
    	{
    		File[] fl = f.listFiles();
    		//设置entry压缩方法,缺省值为DEFLATED
    		//在向ZIP输出流写入数据之前，必须首先使用out.putNextEntry(entry); 方法安置压缩条目对象 
    		//out.putNextEntry(new org.apache.tools.zip.ZipEntry(base +"\\"));//创建一个指定名称的zip项
    		
    		base = base.length() == 0 ? "" : base + "";
    		for (int i = 0; i < fl.length; i++) 
    		{
    			zip(out, fl[i], base + fl[i].getName());
    		}
    	}
    	else 
    	{
    		out.putNextEntry(new org.apache.tools.zip.ZipEntry(base));
    		FileInputStream in = new FileInputStream(f);
    		int b;
    		
    		while ( (b = in.read()) != -1) 
    		{
    			out.write(b);
    		}
    		in.close();
    	}
    }
    
    
    //解压
    public void unzipAnt(String zipFile,String outFilePath,int mode) throws Exception
    {
    	ZipFile zf = null;
    	
         try 
         {
     		File file = new File(zipFile);
     		
     		String fileName = file.getName();
              
            //通过整行組成输出路径
     		if	(mode == 1)
     		{
                 outFilePath += File.separator;
            }
            else
            {
                 outFilePath += File.separator+fileName.substring(0,fileName.length()-4)+ File.separator;
            }
              
            File tmpFileDir = new File(outFilePath);
              
            //创建目录
            tmpFileDir.mkdirs();
            
            //打开一个要读取的zip文件
            zf = new ZipFile(zipFile);
            
            FileOutputStream fos = null;
              
            byte[] buf = new byte[1024];
              
            //返回ZIP文件项的枚举
            Enumeration em = zf.getEntries();
           
            while(em.hasMoreElements())
            {
            	//返回压缩文件项
              	ZipEntry ze = (ZipEntry) em.nextElement();
              	
              	//如果此为目录项，返回true。
              	if(ze.isDirectory())
              	{
              		continue;
              	}
                  
              	//返回读取指定zip文件项内容的输入流
                DataInputStream dis = new DataInputStream(zf.getInputStream(ze) );
                  
                //返回项的名称
                String currentFileName = ze.getName();
                
                if(currentFileName.trim().equals("\\"))
                {
                  	continue;
                }
              
                int dex = currentFileName.lastIndexOf('/');
                  
                String currentoutFilePath = outFilePath;
                  
                if(dex > 0)
                {
                	currentoutFilePath =currentoutFilePath+ currentFileName.substring(0,dex)+File.separator;
                    File currentFileDir = new File(currentoutFilePath);
                    currentFileDir.mkdirs();
                }
                  
                fos = new FileOutputStream(outFilePath + ze.getName());
                  
                int readLen = 0;
                  
                while( (readLen = dis.read(buf,0,1024)) > 0 )
                {
                       fos.write(buf , 0 ,readLen);
                }
                  
                dis.close();
                fos.close();
            }
            
            zf.close();
         }
         catch (Exception ex)
		 {
		    ex.printStackTrace();
		    
		    if (zf != null) zf.close();
		 }
	}
    
    //判断是否是derby数据库
    public boolean needCreateDerbyDir(String zipFile) throws Exception
    {
    	ZipFile zf = null;
    	boolean bool1 = false;
    	boolean bool2 = false;
    	
         try 
         {         
            //打开一个要读取的zip文件
            zf = new ZipFile(zipFile);
                       
            //返回ZIP文件项的枚举
            Enumeration em = zf.getEntries();
           
            while(em.hasMoreElements())
            {
            	//返回压缩文件项
              	ZipEntry ze = (ZipEntry) em.nextElement();
        
              	if(ze.isDirectory())
              	{
              		if (ze.getName().equals("Base/"))
              		{
              			bool1 = true;
              		}
              		
              		if (ze.getName().equals("seg0/"))
              		{
              			bool2 = true;
              		}
              		
              		continue;
              	}
            }
              	
            // 没找到BASE目录但有seg0目录，说明压缩的是Derby数据库，但是没有外层BASE目录，解压时需创建Base目录
            if (!bool1 && bool2)
            {
            	return true;
            }
            else
            {
            	return false;
            }
         }
         catch (Exception ex)
		 {
		    ex.printStackTrace();
		    
		    if (zf != null) zf.close();
		    
		    return false;
		 }
    }
}