package update.client;


import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.net.ftp.FTPClient;


//这个类用于,FTP进行通讯的类
public class Ftp
{
    private FTPClient ftpclient = new FTPClient();

    public Ftp()
    {
    	// timeout 30s
    	ftpclient.setDefaultTimeout(10000);
    	ftpclient.setDataTimeout(30000);
    }
    
    public void setTimeout(int timeout,int datatimeout)
    {
    	try 
    	{
	    	if (timeout > 0) ftpclient.setDefaultTimeout(timeout);
	    	if (datatimeout > 0) ftpclient.setDataTimeout(datatimeout);
			if (datatimeout > 0) ftpclient.setSoTimeout(datatimeout);
		}
    	catch (Exception e) 
    	{
			e.printStackTrace();
		}
    }
    
    public boolean connect(String ipaddress, int port, String userName,String passwd)
    {
    	return connect(ipaddress,port,userName,passwd,"N");
    }
    
    public boolean connect(String ipaddress, int port, String userName,String passwd,String Ftppasv)
    {
        try
        {
        	// 先关闭连接
        	close();
        	
        	// 连接FTP服务器
        	ftpclient.connect(ipaddress, port);
        	if (!ftpclient.login(userName, passwd)) return false;
        	
        	// 设置为BINARY方式
        	ftpclient.setFileType(FTPClient.BINARY_FILE_TYPE);

			if (Ftppasv.endsWith("Y"))
			{
				ftpclient.enterLocalPassiveMode();
			}
			
        	// setTimeout
        	setTimeout(10000,30000);
        	
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return false;
        }
    }

    public boolean close()
    {
        try
        {    	
			if (ftpclient.isConnected()) 
			{
				ftpclient.disconnect();
			}
			
			return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return false;
        }			
    }
    
    public boolean getFile(String name,String localName,String localPath,String ftppath)
    {
    	boolean boolfalg = false;
    	File file_out = null;
    	FileOutputStream os = null;
    	 
        try
        {
            if (ftppath != null && !ftppath.trim().equals(""))
            {
            	boolfalg = true;
            	
            	if (!ftpclient.changeWorkingDirectory(ftppath))
            	{
            		return false;
            	}
            	
            	ReadXmlFile.createDir(localPath + "/" + ftppath);
            	
            	file_out = new File(ReadXmlFile.SystemChangeChar(localPath + "/" + ftppath + "/" + name));
                os = new FileOutputStream(file_out);
            }
            else
            {
            	file_out = new File(localName);
                os = new FileOutputStream(file_out);
            }
            
            // 下载
            boolean ret = ftpclient.retrieveFile(name,os);
            os.close();
            
            if (boolfalg)
            {
            	changeRootDir(ftppath);
            	boolfalg = false;
            }
            
            //
            if (!ret)
            {
            	file_out.delete();
            	return false;
            }
            
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return false;
        }
    }
    
    private void changeRootDir(String ftppath)
    {
    	String str[] = null;
    	int num = 0;
    	
    	try
    	{
    		if (ftppath.indexOf("\\") > 0)
    		{
    			for (int i = 0 ;i < ftppath.length();i++)
    			{
    				if (ftppath.charAt(i) == '\\')
    				{
    					num = num + 1;
    				}
    			}
    			
    			if ((ftppath.length() -1)  == ftppath.lastIndexOf("\\"))
    			{
    				num = num - 1;
    			}
        		
        		for (int i = 0;i <= num;i++)
        		{
        			ftpclient.changeToParentDirectory();
        		}
    		}
    		else if (ftppath.indexOf("/") > 0)
    		{
    			str = ftppath.split("/");
    			
    			for (int i = 0;i < str.length;i++)
    			{
    				ftpclient.changeToParentDirectory();
    			}
    		}
    		else
    		{
    			ftpclient.changeToParentDirectory();
    		}
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }
}

