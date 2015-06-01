package com.efuture.javaPos.Communication;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;

public class SocketService
{
	Socket s = null;
	OutputStream ps  = null;
	InputStream br = null;
	boolean connect = false;
	public String ip = null;
	public int port = 0;
	
	// 主要与SOCKET做连接
	public static Vector socks = null;
	
	public static SocketService getDefault(int index)
	{
		BufferedReader br = null;
		if (socks == null)
		{
			socks = new Vector();
			// 读取socket.ini文件
			br = CommonMethod.readFile(GlobalVar.ConfigPath+"\\SOCKET.ini");
			if (br == null)
			{
				new MessageBox(Language.apply("没有找到SOCKET.ini文件"));
				return null;
			}
			String line = null;
			try
			{
				while ((line = br.readLine()) != null)
				{
					 if (line.indexOf(":") >= 0)
					 {
						 String ip = line.substring(0,line.indexOf(":"));
						 int port = Convert.toInt(line.substring(line.indexOf(":")+1));
						 SocketService a = new SocketService(ip,port);
						 if (a.getstatus() == true)
						 {
							 socks.add(a);
						 }
						 else
						 {
							 new MessageBox(line + " " + Language.apply("无法连接"));
						 }
					 }
				}
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				PosLog.getLog("SocketService").error(e);
				e.printStackTrace();
			}
		}
		
		System.out.println("getDefault "+index);
		
		if (socks.size() <=0) return null;
		
		System.out.println("getDefault111 "+index);
		
		if (index > socks.size()-1)
		{
			return (SocketService) socks.elementAt(index);
		}
		else
		{
			return (SocketService) socks.elementAt(index);
		}
	}
	
	public SocketService(String ip,int port)
	{
		this.ip = ip;
		this.port = port;
		connect = load(ip,port);
	}
	
	public boolean load(String ip,int port)
	{
		try
		{
			s = new Socket(ip, port);
			
			//ps= new PrintStream(s.getOutputStream());
			//br= new BufferedReader(new InputStreamReader(s.getInputStream()));
		}
		catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			PosLog.getLog(getClass()).error(e);
			return false;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			PosLog.getLog(getClass()).error(e);
			return false;
		}
		return true;
	}
	
	public boolean getstatus()
	{
		connect = load(ip,port);
		return connect;
	}
	
	public String sendMessage(String line,String memo)
	{
		try
		{
			System.out.println("socket send");
			if (!s.isConnected() || s.isClosed())
			{
				System.out.println("socket closed");
				if (getstatus() != true)
				{
					new MessageBox(Language.apply("SOCKET重新连接失败。请检查网络或服务器是否正常"));
					return null;
				}
			}
			
			System.out.println("socket send1");
			ps = new DataOutputStream( s.getOutputStream() );
			br = new DataInputStream( s.getInputStream() );
			
			System.out.println(Language.apply("------------发送-------------------\n")+line);
			ps.write(line.getBytes());

			byte[] leng = new byte[4];
			int len =0;
			
			while(true)
			{
				len = br.read(leng, len, 4-len);
				if (len >=4) break;
			}
			System.out.println("String :"+new String(leng));
			//System.out.println("toString :"+leng.toString());
			int len1 = Integer.parseInt(new String(leng));
			byte[] retinfo = new byte[len1];

			len =0;
			while (true)
			{
				len = br.read(retinfo, 0, len1 - len);
				if (len >= len1) break;
			}
			System.out.println(new String(leng)+new String(retinfo));
		    return new String(leng)+new String(retinfo);
		    
			//return "0088130100509999000130030000307363049.90   1002019-10-1001    0.00    0.002010-11-23        ";
		}
		catch(Exception er)
		{
			PosLog.getLog(getClass()).error(er);
			er.printStackTrace();
		}
		finally
		{
			try{
			if (ps != null) ps.close();
			if (br != null) br.close();
			}catch (IOException e)
			{
				// TODO Auto-generated catch block
				PosLog.getLog(getClass()).error(e);
				e.printStackTrace();
			}
			
		}
		return null;
	}

}
