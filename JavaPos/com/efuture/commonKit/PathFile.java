package com.efuture.commonKit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


//这个类主要用于控制文件
public class PathFile
{
    static int filecopycount = 1;
    private static long pathlength;//保存目录大小的变量
    
    public static boolean fileExist(String str)
    {
        return new File(str).exists();
    }

    public static String fileLastmodified(String str)
    {
    	return fileLastmodified(str,"");
    }
    
    public static String fileLastmodified(String str,String format)
    {	
    	if (format.equals(""))
    	{
    		format = "yyyy-MM-dd  hh:mm:ss";
    	}
    	
    	File file = new File(str);
    	
    	if (!file.exists() || !file.isFile())
    	{
    		return "";
    	}
    	
    	Date filedate = new Date(file.lastModified());
    	
    	SimpleDateFormat sdf = new SimpleDateFormat(format);
    	
    	return sdf.format(filedate);
    }
    
    public static void copyPath(String url1, String url2)
    {
        try
        {
            if (!(new File(url1)).isDirectory())
            {
            	url2 = url2.replace('\\','/');
            	
            	// 建立目录
            	if (url2.indexOf('/') >= 0)
            	{
	            	String pathurl2 = url2.substring(0,url2.lastIndexOf('/'));
	            	if (!new File(pathurl2).exists())
	            	{
	            		(new File(pathurl2)).mkdirs();
	            	}
            	}
            	
            	// 拷贝文件
                FileInputStream input = new FileInputStream(url1);
                FileOutputStream output = new FileOutputStream(url2);
                byte[] b = new byte[1024 * 5];

                int len;

                while ((len = input.read(b)) != -1)
                {
                    output.write(b, 0, len);
                }

                output.flush();
                output.close();
                input.close();

                return;
            }
            else
            {
            	//
	            (new File(url2)).mkdirs();
	
	            //
	            File[] file = (new File(url1)).listFiles();
	            for (int i = 0; i < file.length; i++)
	            {
	                if (file[i].isFile())
	                {
	                    FileInputStream input = new FileInputStream(file[i]);
	                    FileOutputStream output = new FileOutputStream(url2 + "/" +
	                                                                   file[i].getName());
	                    byte[] b = new byte[1024 * 5];
	
	                    int len;
	
	                    while ((len = input.read(b)) != -1)
	                    {
	                        output.write(b, 0, len);
	                    }
	
	                    output.flush();
	                    output.close();
	                    input.close();
	                }
	
	                if (file[i].isDirectory())
	                {
	                    copyPath(url1 + "/" + file[i].getName(),
	                             url2 + "/" + file[i].getName());
	                }
	            }
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();

            filecopycount++;
            if (filecopycount <= 3)
            {
                copyPath(url1, url2);
            }
        }
    }
/*
    public static void copyDirectiory(String file1, String file2)
    {
        try
        {
            (new File(file1)).mkdirs();

            File[] file = (new File(file2)).listFiles();

            for (int i = 0; i < file.length; i++)
            {
                if (file[i].isFile())
                {
                    FileInputStream input = new FileInputStream(file[i]);
                    FileOutputStream output = new FileOutputStream(file1 + "/" +
                                                                   file[i].getName());
                    byte[] b = new byte[1024 * 5];
                    int len;

                    while ((len = input.read(b)) != -1)
                    {
                        output.write(b, 0, len);
                    }

                    output.flush();
                    output.close();
                    input.close();
                }

                if (file[i].isDirectory())
                {
                    copyDirectiory(file1 + "/" + file[i].getName(),
                                   file2 + "/" + file[i].getName());
                }
            }
        }
        catch (Exception er)
        {
            er.printStackTrace();
        }
    }

    public static void main(String args[])
    {
    	String file1= "c:\\javapos\\request.txt";
    	String file2 = "c:\\javapos\\result1.txt";
    	
    	renameFile(file1,file2);
    	
    }
    */
    
    public static boolean renameFile(String filename1,String filename2)
    {
        try
        {
            File file = new File(filename1);

            if (file.renameTo(new File(filename2)))
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

            return false;
        }
    
    }
    public static boolean createDir(String filename)
    {
        try
        {
            File file = new File(filename);

            if (file.mkdir())
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

            return false;
        }
    }

    public static void deletePath(String delpath)
    {
        try
        {
            File file = new File(delpath);

            if (!file.isDirectory())
            {
                file.delete();

                return;
            }
            else if (file.isDirectory())
            {
                String[] filelist = file.list();

                for (int i = 0; i < filelist.length; i++)
                {
                    File delfile = new File(delpath + "/" + filelist[i]);

                    if (!delfile.isDirectory())
                    {
                        delfile.delete();
                    }
                    else if (delfile.isDirectory())
                    {
                        deletePath(delfile.getPath());
                    }
                }

                file.delete();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static String[] getAllDirName(String path)
    {
        try
        {
            File file = new File(path);

            return file.list();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }

    public static void delAllFile(String filePath)
    {
        File file = new File(filePath);
        File[] fileList = file.listFiles();
        String dirPath = null;

        if (fileList != null)
        {
            for (int i = 0; i < fileList.length; i++)
            {
                if (fileList[i].isFile())
                {
                    fileList[i].delete();
                }

                if (fileList[i].isDirectory())
                {
                    dirPath = fileList[i].getPath();
                    delAllFile(dirPath);
                }
            }

            file.delete();
        }
    }
    
    public static boolean isPathExists(String path)
    {
    	File file = new File(path);
    	
    	if (!file.exists())
    	{
    		return false;
    	}
/*    	
    	if (!file.isDirectory())
    	{
    		return false;
    	}
*/    	
    	return true;
    }
    
    public static void pathSize(String pathname)
    {
    	try
    	{
		    File dir = new File(pathname);
		    
		    if (!dir.isDirectory())
		    {
		    	pathlength += dir.length();
		    	return;
		    }
		    else
		    {
			    String f[]=dir.list();
			    File f1;
			    
			    for(int i=0;i<f.length;i++)
			    {
			    	f1 = new File (pathname + "/" + f[i]);
			    	if (!f1.isDirectory())
			    		pathlength += f1.length();
			    	else
			    		pathSize(pathname + "/" + f[i]);//如果是目录,递归调用
			    }
		    }
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }
    
    public static long getPathLength()
    {
    	try
    	{
    		return pathlength;
    	}
    	finally
    	{
    		pathlength = 0;
    	}
    }
    
    /***
	 * 查看文件名是否存着传入字符串中
	 * @param name
	 * @param exceptFile
	 * @return
	 */
	private static boolean isMatch(String name, String exceptFile)
	{
		String[] val = exceptFile.trim().split(",");
		for (int i = 0; i < val.length; i++)
		{
			if (name.equalsIgnoreCase(val[i])) { return true; }
		}
		return false;
	}

	/***
	 * 删除文件
	 * @param path
	 * @param exceptFile
	 * @return
	 */
	public static boolean delPathFile(String path, String exceptFile)
	{
		String dirname[] = null;

		try
		{
			dirname = PathFile.getAllDirName(path);

			for (int i = 0; i < dirname.length; i++)
			{
				if (!isMatch(dirname[i].trim(), exceptFile)) PathFile.deletePath(path + "/" + dirname[i].trim());
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
}
