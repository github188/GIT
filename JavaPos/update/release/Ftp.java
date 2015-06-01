package update.release;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.net.ftp.FTPClient;

public class Ftp 
{
	private FTPClient ftpclient = new FTPClient();

    public Ftp()
    {
    	//发布程序不设置超时
    	//ftpclient.setDefaultTimeout(30000);
    	//ftpclient.setDataTimeout(30000);    	
    }
    
    public boolean connect(FtpCfgDef fcd)
    {
    	try
        {
        	// 连接FTP服务器
        	ftpclient.connect(fcd.FtpIP, fcd.FtpPort);
        	
        	if (!ftpclient.login(fcd.FtpUser, fcd.FtpPwd)) return false;
        	
			// 不设置这个东西,访问外网可能有问题
			ftpclient.setRemoteVerificationEnabled(false);
			
        	if (fcd.Ftppasv.equals("Y"))
        	{
        		ftpclient.enterLocalPassiveMode();
        	}
			
        	// 设置为BINARY方式
        	ftpclient.setFileType(FTPClient.BINARY_FILE_TYPE);

        	// set timeout
        	ftpclient.setDefaultTimeout(fcd.FtpDefaultTimeout);
        	ftpclient.setDataTimeout(fcd.FtpDataTimeout);
        	ftpclient.setSoTimeout(fcd.FtpTimeout);
        	
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return false;
        }
    }
    
    /*
    public boolean connect(String ipaddress, int port, String userName,String passwd,String pasv)
    {
        try
        {
        	// 连接FTP服务器
        	ftpclient.connect(ipaddress, port);
        	
        	if (!ftpclient.login(userName, passwd)) return false;
        	
			// 不设置这个东西,访问外网可能有问题
			ftpclient.setRemoteVerificationEnabled(false);
			
        	if (pasv.equals("Y"))
        	{
        		ftpclient.pasv();
        	}
			
        	// 设置为BINARY方式
        	ftpclient.setFileType(FTPClient.BINARY_FILE_TYPE);

        	// set timeout
        	ftpclient.setDefaultTimeout(10000);
        	ftpclient.setDataTimeout(30000);
        	ftpclient.setSoTimeout(30000);
        	
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return false;
        }
    }
    */
    
    /*
    public boolean connect(String ipaddress, int port, String userName,String passwd)
    {
        try
        {
        	return connect( ipaddress,  port,  userName, passwd,"N");
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return false;
        }
    }
*/
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
    
    public boolean getFile(String name,String localName)
    {
        try
        {
            File file_out = new File(localName);
            FileOutputStream os = new FileOutputStream(file_out);
            
            // 下载
            boolean ret = ftpclient.retrieveFile(name,os);
            os.close();
            
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
    
    public boolean sendFile(String name,String localName)
    {
    	try
    	{
    		Thread.sleep(2000);
    		
    		File f = new File(localName);
    			
    		FileInputStream in = new FileInputStream(f);
    			
    		boolean ret = ftpclient.storeFile(name,in);
    		
    		in.close();
    		
    		if (!ret)
    		{
    			return false;
    		}
    		
    		return true;
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    		return false;
    	}
    }
    
    public int sendFile(String name,String localName,String ftppath)
    {
    	String temppath[] = null;
    	int index = -1;
    	
    	try
    	{
    		File f = new File(localName);
    			
    		FileInputStream in = new FileInputStream(f);
    		
    		boolean bool = ftpclient.changeWorkingDirectory(ftppath);
    		
    		if (!bool)
    		{
    			String strpath = "";
    			
    			index = ftppath.indexOf('/');
    			if (index > -1)
    			{
    				temppath = ftppath.split("/");
    				for (int i = 0;i < temppath.length;i++)
        			{
        				strpath = strpath + temppath[i] + "/";
        				ftpclient.makeDirectory(strpath);
        			}
    			}
    			else
    			{
    				String str = ftppath.replaceAll("\\\\","\\\\\\\\");
    				temppath = str.split("\\\\\\\\");
    				
    				for (int i = 0;i < temppath.length;i++)
        			{
        				strpath = strpath + temppath[i] + "\\";
        				ftpclient.makeDirectory(strpath);
        			}
    			}
    			
    			bool = ftpclient.changeWorkingDirectory(ftppath);
    			
    			if (!bool)
    			{
    				return -1;
    			}
    		}
    
    		boolean ret = ftpclient.storeFile(name,in);
    		
    		in.close();
    		
    		if (!ret)
    		{
    			changeRootDir(ftppath);
    			
    			return -2;
    		}
    		
    		changeRootDir(ftppath);
    		
    		return 1;
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    		return -2;
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
    
    public boolean delFile(String name,String ftppath)
    {
    	try
    	{
    		if (ftppath != null && ftppath.trim().length() > 0) changeRootDir(ftppath);
    		return ftpclient.deleteFile(name);
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    		return false;
    	}
    }
}
