package com.efuture.configure.wizard;

import java.io.*;
import java.util.Calendar;
import java.util.Vector;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;

import com.efuture.commonKit.CommonMethod;

/**
 * 测试线程启动类
 * 
 * @author Administrator
 * 
 */
public class PosSrvPressureBS implements Runnable
{
	public static int allCount = 0;
	public static int stopCount = 0;

	private String srvUrl;
	private int startCount; // 线程数量
	private int cmdDelaytime; // 命令延时时间
	private int threadDelaytime; // 线程启动延时时间
	private int exceptiondelaytime;// 线程遇到异常时停多长时间
	
	private Vector cmdlist;

	public PosSrvPressureBS(String url, int cmdIndex, String count,
			int cmddelay, int thddelay,int exceptiondelay)
	{
		allCount = 0;
		stopCount = 0;
		cmdlist =new Vector();
		//String tmpCmd = "";
		if (cmdIndex == 0)
		{
			cmdlist.addAll(PosSrvPressureEvent.cmdList);
		}
		else
		{
			cmdlist.add((String[]) PosSrvPressureEvent.cmdList.elementAt(cmdIndex-1));
		}
		//String[] tmpAry = (String[]) PosSrvPressureEvent.cmdList.elementAt(cmdIndex+1);
		//if (tmpAry != null && tmpAry.length > 1)
			//tmpCmd = tmpAry[1];

		srvUrl = url;
		//srvCmd = tmpCmd;
		startCount = Integer.parseInt(count);
		cmdDelaytime = cmddelay;
		threadDelaytime = thddelay;
		exceptiondelaytime = exceptiondelay;
	}

	public void run()
	{
		PosSrvPressureEvent.threadAry = new Thread[startCount];
		PosSrvPressureEvent.result = new String[startCount];
		String[] line;

		try
		{
			for (int i = 0; i < startCount; i++)
			{
				// 终止线程创建
				if (PosSrvPressureEvent.getStopBtnStatus() == true)
					break;

				// 将创建的线程放入数组
				ThreadPressure thread = new ThreadPressure(srvUrl, cmdlist, cmdDelaytime,exceptiondelaytime);
				thread.setName(String.valueOf(i));
				PosSrvPressureEvent.threadAry[i] = thread;

				line = new String[5];
				line[0] = String.valueOf(i);
				line[1] = getCurrentTime();
				line[2] = "";
				line[3] = "";
				line[4] = "";

				PosSrvPressureEvent.getDefault().showThreadInfo(line);

				thread.start();

				Thread.sleep(threadDelaytime);

				PosSrvPressureBS.allCount = i + 1; // 记录启动的线程总数
				PosSrvPressureEvent.getDefault().updateStatus(new int[] { PosSrvPressureBS.allCount, PosSrvPressureBS.stopCount });
			}
			// 等待所有线程执行结束
			try
			{
				for (int i = 0; i < PosSrvPressureBS.allCount; i++)
					PosSrvPressureEvent.threadAry[i].join();

				PosSrvPressureEvent.isStartBtnPressed = false;
				PosSrvPressureEvent.isStopBtnPressed = true;
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

	// 得到当前时间
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

	// 测试线程类
	public class ThreadPressure extends Thread
	{
		private String srvUrl;
		private Vector srvCmdList;
		private int delayTime; // 延时时间
		private int exceptiondelaytime;// 线程遇到异常时延时时间

		public ThreadPressure(String url, Vector cmd, int delay,int exceptiondelay)
		{
			srvUrl = url;
			srvCmdList = cmd;
			delayTime = delay;
			exceptiondelaytime = exceptiondelay;
		}

		public void run()
		{
			int runCount = 0;

			int sucCount = 0;
			int errorCount = 0;

			boolean isStop = false;
			PressureHttp http = null;

			try
			{
				http = new PressureHttp(srvUrl);
				http.initHttp();
				http.setCtimeout(PosSrvPressureEvent.connectTimeout);
				http.setRtimeout(PosSrvPressureEvent.receiveTimeout);

				while (!PosSrvPressureEvent.getStopBtnStatus())
				{
					isStop = false;
					
					http.setExeFlag(false);

					for (int i = 0; i < srvCmdList.size(); i++)
					{
						if (PosSrvPressureEvent.getStopBtnStatus()) break;
						
						String[] tmpAry;
						String srvCmd = null;
						tmpAry = (String[]) srvCmdList.elementAt(i);
						if (tmpAry != null && tmpAry.length > 1) srvCmd = tmpAry[1];
						else continue;
						
						int index = Integer.parseInt(currentThread().getName());
						
						if (srvCmd.toLowerCase().indexOf("bankfunc:")==0)
						{
							String cmd = srvCmd.substring("bankfunc:".length());
							CommonMethod.waitForExec(cmd);
							
							if (cmd.indexOf("javaposbank.exe") > 0)
							{
								String path = cmd.substring(0,cmd.indexOf("javaposbank.exe"));
//								 读取 result.txt 文件
								BufferedReader br = CommonMethod.readFile(path+"result.txt");
								String line = br.readLine();
								br.close();
								PosSrvPressureEvent.result[index] = line;
								
								if (line.substring(0, 2).equals("00") || line.substring(0, 2).equals("97"))
								{
									PosSrvPressureEvent.getDefault().updateTabLine(new String[] { Thread.currentThread().getName(), String.valueOf(runCount) });
									continue;
								}
								else
								{
									PosSrvPressureEvent.getDefault().updateTabLine(new String[] { Thread.currentThread().getName(), String.valueOf(runCount), getCurrentTime(), "发生异常,请双击查看..." });
									isStop = true;
									break;
								}
							}
							
							PosSrvPressureEvent.getDefault().updateTabLine(new String[] { Thread.currentThread().getName(), String.valueOf(runCount) });
							
							continue;
							/**
							// 读取 result.txt 文件
							BufferedReader br = CommonMethod.readFile("result.txt");
							String line = br.readLine();
							PosSrvPressureEvent.result[index] = line;
							
							if (!line.substring(0, 2).equals("00"))
							{
								PosSrvPressureEvent.getDefault().updateTabLine(new String[] { Thread.currentThread().getName(), String.valueOf(runCount), getCurrentTime(), "发生异常,请双击查看..." });
								isStop = true;
								break;
							}
							else
							{
								PosSrvPressureEvent.getDefault().updateTabLine(new String[] { Thread.currentThread().getName(), String.valueOf(runCount) });
								continue;
							}
							*/
						}
						http.setRequestString(srvCmd);
						http.execute();
	
						// 线程id与table中的行一一对应
						
						PosSrvPressureEvent.result[index] = http.getAnswerString();
	
						if (http.getAnswerString() != null)
						{
							if (http.getExceptionFlag())
							{
								errorCount++;
								PosSrvPressureEvent.getDefault().updateTabLine(new String[] { Thread.currentThread().getName(), String.valueOf(runCount + " " + sucCount + " " + errorCount), getCurrentTime(), "发生异常,请双击查看..." });
								isStop = true;
								break;
							}
							else
							{
								sucCount++;
								PosSrvPressureEvent.getDefault().updateTabLine(new String[] { Thread.currentThread().getName(), String.valueOf(runCount + " " + sucCount + " " + errorCount) });
							}
						}
						System.out.println(delayTime);
						if (delayTime < 1000)
						{
							sleep(1000);
						}
						else
						{
							sleep(delayTime);
						}
					}
					
					runCount++;
					
					if (isStop)
					{
						Thread.sleep(exceptiondelaytime);
						//break;
					}
				}				
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if (http != null)
				{
					http.closeConnection();
					http = null;
				}
			}
			
			synchronized (this)
			{
					PosSrvPressureBS.allCount --;
					if (PosSrvPressureBS.allCount < 0) PosSrvPressureBS.allCount = 0;
			}
			
			PosSrvPressureEvent.getDefault().updateTabLine(new String[] { Thread.currentThread().getName(), String.valueOf(runCount + " " + sucCount + " " + errorCount), getCurrentTime() });
			PosSrvPressureEvent.getDefault().updateStatus(new int[] { PosSrvPressureBS.allCount, errorCount});

		}
	}

	// http通讯类
	public class PressureHttp
	{
		private String srvurl = null;
		private int ctimeout = 10000; // 默认值
		private int rtimeout = 20000; // 默认值
		private String requestString = null;
		private String answerString = null;
		private HttpClient httpClient = null;
		private boolean isException = false;

		public PressureHttp(String url)
		{
			srvurl = url;
			isException = false;
		}

		public boolean initHttp()
		{
			httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
			httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(ctimeout);
			httpClient.getHttpConnectionManager().getParams().setSoTimeout(rtimeout);

			return true;
		}

		public boolean execute()
		{
			PostMethod postMethod = null;
			try
			{
				// 创建请求方法
				postMethod = new PostMethod(srvurl);
				postMethod.addRequestHeader("Content-Type", "text/xml;charset=UTF-8");

				// 设置请求数据
				byte[] requestBuff = null;
				requestBuff = requestString.getBytes("UTF-8");
				postMethod.setRequestEntity(new ByteArrayRequestEntity(requestBuff));

				// 发送HTTP请求
				httpClient.executeMethod(postMethod);

				// 读取HTTP应答数据
				String s;
				StringBuffer sb = new StringBuffer();
				InputStream is = postMethod.getResponseBodyAsStream(); // 得到服务器返回的结果
				BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				while ((s = in.readLine()) != null)
				{
					sb.append(s);
					sb.append("\n");
				}
				answerString = sb.toString();
				return true;
			}
			catch (IOException ex)
			{
				isException = true;

				// 获取异常堆栈
				StringWriter writer = new StringWriter();
				ex.printStackTrace(new PrintWriter(writer, true));
				answerString = writer.toString();

				return false;
			}
			finally
			{
				if (postMethod != null)
				{
					postMethod.releaseConnection();
				}
			}
		}

		public void closeConnection()
		{
			if (httpClient != null)
			{
				httpClient.getHttpConnectionManager().closeIdleConnections(0);
				httpClient = null;
			}
		}

		public String getAnswerString()
		{
			return answerString;
		}

		public void setRequestString(String requestString)
		{
			this.requestString = requestString;
		}

		public void setRtimeout(int rtimeout)
		{
			this.rtimeout = rtimeout;
			httpClient.getHttpConnectionManager().getParams().setSoTimeout(rtimeout);
		}

		public void setCtimeout(int ctimeout)
		{
			this.ctimeout = ctimeout;
			httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(ctimeout);
		}

		public boolean getExceptionFlag()
		{
			return isException;
		}

		public void setExeFlag(boolean flag)
		{
			isException = flag;
		}
	}
}
