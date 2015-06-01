package posserver.tools.PressTest;

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
	public void run()
	{
		GlobalStatus.arrThreadStatus = new ThreadStatus[GlobalConfig.threadCount];

		try
		{
			GlobalStatus.startTimer1();
			GlobalStatus.startUpdateStatus();
			
			for (int i = 0; i < GlobalConfig.threadCount; i++)
			{
				if (GlobalStatus.isstoping) break;
				
				// 将创建的线程放入数组
				GlobalStatus.arrThreadStatus[i] = new ThreadStatus();
				GlobalStatus.arrThreadStatus[i].setThread(new ThreadPressure(GlobalStatus.arrThreadStatus[i]));
				GlobalStatus.arrThreadStatus[i].getThread().setName(String.valueOf(i));
				GlobalStatus.arrThreadStatus[i].setIndex(i);
				GlobalStatus.arrThreadStatus[i].setStarttime(PresCommonMethod.getCurrentTime());

				PosSrvPressureEvent.getDefault().updateTabLine(GlobalStatus.arrThreadStatus[i]);
				
				GlobalStatus.increseActiveThreadNum();
				
				if (GlobalConfig.threadDelaytime >= 0)
				{
					GlobalStatus.arrThreadStatus[i].getThread().start();
					Thread.sleep(GlobalConfig.threadDelaytime);
				}
			}
			
			if (GlobalStatus.getActiveThreadNum() <= 0)
			{
				GlobalStatus.setIsstopcompleted(true);
			}
			
			// 等待所有线程执行结束
			try
			{
				if (GlobalConfig.threadDelaytime < 0)
				{
					for (int i = 0; i < GlobalStatus.getActiveThreadNum(); i++)
					{
						GlobalStatus.arrThreadStatus[i].getThread().start();
					}
				}
				
				for (int i = 0; i < GlobalStatus.getActiveThreadNum(); i++)
				{
					GlobalStatus.arrThreadStatus[i].getThread().join();
				}
				
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

	// 测试线程类
	public class ThreadPressure extends Thread
	{
		public ThreadStatus status = null;
		
		public ThreadPressure(ThreadStatus status)
		{
			this.status = status;
		}

		public void run()
		{
			PressureHttp http = null;

			try
			{	
				http = new PressureHttp(GlobalConfig.srvUrl);
				http.initHttp();
				http.setCtimeout(GlobalConfig.connectTimeout);
				http.setRtimeout(GlobalConfig.receiveTimeout);
					
				while (!GlobalStatus.isstoping)
				{
					status.setIssuc(true);
					
					http.setExeFlag(false);

					status.increseRunCount();
					
					for (int i = 0; i < GlobalStatus.cmdlisting.size(); i++)
					{
						if (GlobalStatus.isstoping) break;
						
						String[] tmpAry;
						String srvCmd = null;
						tmpAry = (String[]) GlobalStatus.cmdlisting.elementAt(i);
						if (tmpAry != null && tmpAry.length > 1) srvCmd = tmpAry[1];
						else continue;
						
						http.setRequestString(srvCmd);
						http.execute();
	
						status.setResult(http.getAnswerString());
	
						if (http.getAnswerString() != null)
						{
							if (http.getExceptionFlag())
							{
								status.increseErrorCount();
								status.setIssuc(false);
								PosSrvPressureEvent.getDefault().updateTabLine(status);
								break;
							}
							else
							{
								status.increseSucCount();
								status.setIssuc(true);
								PosSrvPressureEvent.getDefault().updateTabLine(status);
							}
						}

						if (GlobalConfig.cmdDelaytime < 1000)
						{
							sleep(1000);
						}
						else
						{
							sleep(GlobalConfig.cmdDelaytime);
						}
					}
					
					if (!status.isIssuc())
					{
						Thread.sleep(GlobalConfig.exceptiondelaytime);
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
			
			status.setStoptime(PresCommonMethod.getCurrentTime());
			
			PosSrvPressureEvent.getDefault().updateTabLine(status);
			
			GlobalStatus.decreseActiveThreadNum();
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
