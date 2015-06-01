package com.efuture.configure.wizard;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.InputStream;
import java.util.Calendar;
import java.io.File;
import java.io.FileOutputStream;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.Language;

public class FtpSrvPressureBS implements Runnable
{
	public static int allCount = 0;
	public static int stopCount = 0;
	private int startCount = 0;

	private String srvUrl;
	private int srvPort;
	private String srvFile;
	private String localPath;
	private String srvUsr;
	private String srvPass;
	private int cmdDelay;
	private int threadDelay;

	public FtpSrvPressureBS(String url, String port, String file, String path,
			String usr, String pass, String count, int cmddelay, int thddelay)
	{
		allCount = 0;
		stopCount = 0;

		srvUrl = url;
		srvPort = Integer.parseInt(port);
		srvFile = file;
		localPath = path;
		srvUsr = usr;
		srvPass = pass;
		startCount = Integer.parseInt(count);
		cmdDelay = cmddelay;
		threadDelay = thddelay;
	}

	public String getCurrentTime()
	{
		int h, m, s;
		String time = "";

		Calendar calendar = Calendar.getInstance();

		h = calendar.get(Calendar.HOUR_OF_DAY);
		if (h < 10)
			time = "0" + h + ":";
		else
			time = String.valueOf(h) + ":";

		m = calendar.get(Calendar.MINUTE);
		if (m < 10)
			time += "0" + m + ":";
		else
			time += String.valueOf(m) + ":";

		s = calendar.get(Calendar.SECOND);
		if (s < 10)
			time += "0" + s;
		else
			time += String.valueOf(s);

		return time;

	}

	public void run()
	{
		FtpSrvPressureEvent.threadAry = new Thread[startCount];
		FtpSrvPressureEvent.result = new String[startCount];
		String[] line;

		try
		{
			for (int i = 0; i < startCount; i++)
			{
				// 终止线程创建
				if (FtpSrvPressureEvent.getStopBtnStatus() == true)
					break;

				// 将创建的线程放入数组
				ThreadPressure thread = new ThreadPressure(srvUrl, srvPort, srvFile, localPath, srvUsr, srvPass, cmdDelay);
				thread.setName(String.valueOf(i));
				FtpSrvPressureEvent.threadAry[i] = thread;

				line = new String[5];
				line[0] = String.valueOf(i);
				line[1] = getCurrentTime();
				line[2] = "";
				line[3] = "";
				line[4] = "";

				FtpSrvPressureEvent.getDefault().showThreadInfo(line);
				thread.start();

				Thread.sleep(threadDelay);

				FtpSrvPressureBS.allCount = i + 1; // 记录启动的线程总数
				FtpSrvPressureEvent.getDefault().updateStatus(new int[] { FtpSrvPressureBS.allCount, FtpSrvPressureBS.stopCount });
			}
			// 等待所有线程执行结束
			try
			{
				for (int i = 0; i < FtpSrvPressureBS.allCount; i++)
					FtpSrvPressureEvent.threadAry[i].join();

				FtpSrvPressureEvent.isStartBtnPressed = false;
				FtpSrvPressureEvent.isStopBtnPressed = true;
				System.gc();
			}
			catch (InterruptedException ex)
			{
				ex.printStackTrace();
				return;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return;
		}
	}

	public static synchronized void setStopCount()
	{
		stopCount++;
	}

	// 测试线程类
	public class ThreadPressure extends Thread
	{
		private String srvUrl;
		private int srvPort;
		private String srvFile;
		private String localPath;
		private String srvUsr;
		private String srvPass;
		private int delayTime;

		public ThreadPressure(String url, int port, String file, String path,
				String usr, String pass, int delay)
		{
			srvUrl = url;
			srvPort = port;
			srvFile = file;
			localPath = path;
			srvUsr = usr;
			srvPass = pass;
			delayTime = delay;
		}

		public void run()
		{
			PressureFtp ftp = null; 
			boolean isStop = false;
			int runCount = 0;
			String fileStr = "";
			String tmpFile = "";
			int index = 0;

			try
			{
				ftp = new PressureFtp();
				index = Integer.parseInt(currentThread().getName());

				if (ftp.connect(srvUrl, srvPort, srvUsr, srvPass) && ftp.checkFile(srvFile))
				{
					while (!FtpSrvPressureEvent.getStopBtnStatus())
					{
						fileStr = localPath + "\\" + Thread.currentThread().getName() + "_get_" + String.valueOf(runCount) + ".tmp";

						if (!tmpFile.equals(""))
						{
							File file = new File(tmpFile);
							if (file != null && file.exists())
								file.delete();
						}

						ftp.getFile(srvFile, fileStr);

						tmpFile = fileStr;
						runCount++;

						FtpSrvPressureEvent.result[index] = ftp.getRetInfo();

						if (ftp.getExceptionFlag())
						{
							FtpSrvPressureEvent.getDefault().updateTabLine(new String[] { Thread.currentThread().getName(), String.valueOf(runCount), getCurrentTime(), "发生异常,请双击查看" });
							isStop = true;
							break;
						}
						FtpSrvPressureEvent.getDefault().updateTabLine(new String[] { Thread.currentThread().getName(), String.valueOf(runCount), "", ftp.getRetInfo() });

						sleep(delayTime);
					}
					ftp.close();
				}
				else
				{
					isStop = true;
					FtpSrvPressureEvent.result[index] = ftp.getRetInfo();
					FtpSrvPressureEvent.getDefault().updateTabLine(new String[] { Thread.currentThread().getName(), String.valueOf(runCount), getCurrentTime(), ftp.getRetInfo() });
				}

				if (isStop)
				{
					FtpSrvPressureBS.setStopCount();
					FtpSrvPressureEvent.getDefault().updateStatus(new int[] { FtpSrvPressureBS.allCount, FtpSrvPressureBS.stopCount });
					return;
					// Thread.currentThread().interrupt();
				}
				else
				{
					FtpSrvPressureEvent.getDefault().updateTabLine(new String[] { Thread.currentThread().getName(), String.valueOf(runCount), getCurrentTime() });
					return;
				}
			}
			catch (Exception ex)
			{
				ftp.close();
				ftp = null;
				ex.printStackTrace();
			}
			finally
			{
				ftp.close();
				ftp = null;
			}
		}
	}

	//FTP
	public class PressureFtp
	{
		private FTPClient ftpClient;
		private String retInfo = "";
		private boolean isException;

		public PressureFtp()
		{
			ftpClient = new FTPClient();
			ftpClient.setDefaultTimeout(FtpSrvPressureEvent.connectTimeout);
			ftpClient.setDataTimeout(FtpSrvPressureEvent.receiveTimeout);
		}

		public boolean getExceptionFlag()
		{
			return isException;
		}

		public String getRetInfo()
		{
			return retInfo;
		}

		public boolean connect(String url, int port, String usr, String pass)
		{
			int reply = 0;
			try
			{
				if (ftpClient == null)
				{
					//retInfo = "Ftp 初始化失败";
					retInfo = Language.apply("Ftp 初始化失败");
					return false;
				}
				if (ftpClient.isConnected())
					ftpClient.disconnect();

				ftpClient.connect(url, port);
				reply = ftpClient.getReplyCode();

				if (!FTPReply.isPositiveCompletion(reply))
				{
					ftpClient.disconnect();
					//retInfo = "连接失败";
					retInfo = Language.apply("连接失败");
					return false;
				}

				if (!ftpClient.login(usr, pass))
				{
					//retInfo = "登录失败";
					retInfo = Language.apply("登录失败");
					return false;
				}

				ftpClient.setDefaultTimeout(ConfigClass.FtpDefaultTimeout);//ConnectTimeout
				ftpClient.setDataTimeout(ConfigClass.FtpDataTimeout);//ReceiveTimeout

				ftpClient.enterLocalPassiveMode();
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

				ftpClient.changeWorkingDirectory(FtpSrvPressureEvent.ftpDir);

				if (ConfigClass.FtpTimeout > 0)
					ftpClient.setSoTimeout(ConfigClass.FtpTimeout);
				//retInfo = "成功连接服务器...";
				retInfo = Language.apply("成功连接服务器...");
				return true;
			}
			catch (IOException ex)
			{
				isException = true;

				ex.printStackTrace();
				getStackTrace(ex);
				//retInfo = "连接异常:" + getStackTrace(ex);
				retInfo = Language.apply("连接异常:") + getStackTrace(ex);

				try
				{
					if (ftpClient == null)
						return false;
					if (ftpClient.isConnected())
						ftpClient.disconnect();
					ftpClient = null;
				}
				catch (IOException e)
				{
					e.printStackTrace();
					ftpClient = null;
				}
				return false;
			}
		}

		public boolean getFile(String srcFile, String localFile)
		{
			FileOutputStream os = null;
			InputStream in = null;

			try
			{
				File outFile = new File(localFile);
				os = new FileOutputStream(outFile);

				in = ftpClient.retrieveFileStream(srcFile);
				byte[] bytes = new byte[1024];
				int c = 0;
				while ((c = in.read(bytes)) != -1)
				{
					os.write(bytes, 0, c);
				}
				in.close();
				os.close();
				ftpClient.completePendingCommand();

				//retInfo = "文件获取完毕...";
				retInfo = Language.apply("文件获取完毕...");
				return true;
			}
			catch (IOException ex)
			{
				try
				{
					if (os != null)
					{
						os.close();
						os = null;
					}
					if (in != null)
					{
						in.close();
						in = null;
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				isException = true;
//				retInfo = "文件获取异常: " + getStackTrace(ex);
  				retInfo = Language.apply("文件获取异常: ") + getStackTrace(ex);

				return false;
			}
			finally
			{
				try
				{
					if (os != null)
					{
						os.close();
						os = null;
					}
					if (in != null)
					{
						in.close();
						in = null;
					}
				}
				catch (IOException ex)
				{
					// ex.printStackTrace();

					isException = true;
					//retInfo = "文件流关闭异常: " + getStackTrace(ex);
					retInfo = Language.apply("文件流关闭异常: ") + getStackTrace(ex);
				}
			}
		}

		public boolean close()
		{
			try
			{
				if (ftpClient == null)
					return true;
				if (ftpClient.isConnected())
				{
					ftpClient.disconnect();
				}
				ftpClient = null;
				return true;
			}
			catch (Exception ex)
			{
				ftpClient = null;
				ex.printStackTrace();
				//retInfo = "连接关闭发生异常: " + getStackTrace(ex);
				retInfo = Language.apply("连接关闭发生异常: ") + getStackTrace(ex);

				return false;
			}
		}

		public boolean checkFile(String file)
		{
			try
			{
				String[] list = ftpClient.listNames();

				for (int i = 0; i < list.length; i++)
				{
					if (list[i].equalsIgnoreCase(file))
						return true;
				}

				//retInfo = "未发现下载文件";
				retInfo = Language.apply("未发现下载文件");
				return false;
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
				//retInfo = "检查文件发生异常: " + getStackTrace(ex);
				retInfo = Language.apply("检查文件发生异常: ") + getStackTrace(ex);
				return false;
			}
		}

		public String getStackTrace(Exception ex)
		{
			StringWriter writer = new StringWriter();
			ex.printStackTrace(new PrintWriter(writer));
			return writer.toString();
		}
	}

}