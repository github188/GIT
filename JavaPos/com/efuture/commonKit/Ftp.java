package com.efuture.commonKit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTPClient;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;

//这个类用于,FTP进行通讯的类
public class Ftp
{
	private FTPClient ftpclient = new FTPClient();
	private long curdatalen = 0,curdatatime = 0;
	
	public Ftp()
	{
		// timeout
		ftpclient.setDefaultTimeout(ConfigClass.FtpDefaultTimeout);//ConnectTimeout
		ftpclient.setDataTimeout(ConfigClass.FtpDataTimeout);//ReceiveTimeout
		
	}

	public boolean exist(String name)
	{
		try
		{
			String[] names = ftpclient.listNames();

			if (names == null) return false;

			for (int i = 0; i < names.length; i++)
			{
				if (names[i].indexOf(name) >= 0) return true;
			}
			return false;

		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean connect(String ipaddress, int port, String userName, String passwd,String pasv)
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

			// 不设置这个东西,访问外网可能有问题
			ftpclient.setRemoteVerificationEnabled(false);
			
			if (pasv == null)
			{
				if (ConfigClass.Ftppasv.endsWith("Y"))
				{
					ftpclient.enterLocalPassiveMode();
				}
			}
			else if(pasv.equals("Y"))
			{
				ftpclient.enterLocalPassiveMode();
			}
			
			// set timeout
			if (ConfigClass.FtpDefaultTimeout > 0) ftpclient.setDefaultTimeout(ConfigClass.FtpDefaultTimeout);
			if (ConfigClass.FtpDataTimeout > 0)ftpclient.setDataTimeout(ConfigClass.FtpDataTimeout);
			if (ConfigClass.FtpTimeout > 0) ftpclient.setSoTimeout(ConfigClass.FtpTimeout);

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
	}
	
	public boolean connect(String ipaddress, int port, String userName, String passwd)
	{
		return connect(ipaddress, port, userName, passwd,null);
	}

	public boolean isConnected()
	{
		return ftpclient.isConnected();
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

	public long getRetrieveDataLength()
	{
		return curdatalen;		//KB
	}
	
	public long getRetrieveDataSpeed()
	{
		long s = (System.currentTimeMillis() - curdatatime) / 1000;
		if (s <= 0) s = 1;
		return (curdatalen/s);	// K/S
	}
	
	public boolean getFile(String name, String localName)
	{
		return getFile(name, localName, null);
	}

	public boolean getFile(String name, String localName, Runnable runback)
	{
		try
		{
			int datasize = 8192;
			File file_conf = new File(GlobalVar.ConfigPath+"//DataSize.ini");
			if (file_conf.exists())
			{
				BufferedReader br = null;
				try{
					br = CommonMethod.readFile(GlobalVar.ConfigPath+"//DataSize.ini");
					String line = br.readLine();
					int n = Convert.toInt(line.trim());
					if (n != 0)
					{
						datasize = n;
					}
				}catch(Exception er)
				{
					er.printStackTrace();
				}
				finally
				{
					if (br != null)
						br.close();
				}
			}
			
			File file_out = new File(localName);
			FileOutputStream os = new FileOutputStream(file_out);

			// 下载
			boolean ret = false;
			if (runback == null || PathFile.isPathExists(GlobalVar.ConfigPath + "//FtpNoProgressBar.ini"))
			{
				ret = ftpclient.retrieveFile(name, os);
			}
			else
			{
				InputStream in = ftpclient.retrieveFileStream(name);
				if (in != null)
				{
					try
					{
						byte[] data = new byte[datasize];
						curdatalen = 0;
						curdatatime = System.currentTimeMillis();
						while (true)
						{
							int l = in.read(data);
							if (l <= 0) break;
							os.write(data,0,l);
							curdatalen += l / 1024;
							
							// 回调
							runback.run();
						}
						in.close();
						ret = ftpclient.completePendingCommand();	//必须调用该方法
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
						ret = false;
					}
				}
			}

			// 关闭
			os.close();
			if (!ret)
			{
				file_out.delete();
				throw new Exception(Language.apply("下载 {0} 文件为 {1} 失败!", new Object[]{name, localName}));
//				throw new Exception("下载 " + name + " 文件为 " + localName + " 失败!");
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
	}

	public String[] listNames()
	{
		try
		{
			return ftpclient.listNames();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public boolean changeWorkPath(String path)
	{
		try
		{
			return ftpclient.changeWorkingDirectory(path);
		}
		catch (Exception er)
		{
			er.printStackTrace();
			return false;
		}
	}

	public boolean getFile(String name, String localName, String localPath, String ftppath)
	{
		try
		{
			if (ftppath != null && !ftppath.trim().equals(""))
			{
				if (!ftpclient.changeWorkingDirectory(ftppath)) { return false; }

				PathFile.createDir(localPath + "/");

				if (localName == null || localName.length() <= 0) localName = name;

				return getFile(name, localPath + "/" + localName);
			}
			else
			{
				return getFile(name, localName);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
	}
	/*
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
	 */
}
