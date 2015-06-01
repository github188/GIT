package com.efuture.javaPos.Device;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.PrintTemplate.DisplayAdvertMode;


//第2显示器
public class SecMonitor
{
    public static SecMonitor secMonitor = null;
    boolean status = false;
    PrintStream ps;
    Socket s;
    String serverName = null;
    int port = 0;
    boolean havetemplate = false;
    
    public SecMonitor(String serverName, int port)
    {
        this.serverName = serverName;
        this.port       = port;

        try
        {
        	havetemplate = DisplayAdvertMode.getDefault().ReadTemplateFile();
        	
            s = new Socket(serverName, port);

            //InputStreamReader isr = new InputStreamReader(s.getInputStream());
            ps     = new PrintStream(s.getOutputStream());
            status = true;
        }
        catch (Exception e)
        {
            status = false;
            PosLog.getLog(getClass()).debug(e);
            e.printStackTrace();
            new MessageBox(Language.apply("连接双屏广告端口失败\n\n")+e.getMessage());
        }
    }

    public boolean getStatus()
    {
        return status;
    }

    public void monitorLock(boolean lock)
    {
    	if (ConfigClass.DisplayMode.toLowerCase().indexOf("videoplayer") >= 0)
    	{
    		if (lock)
    		{
    			Vector v = CommonMethod.readFileByVector(GlobalVar.ConfigPath+"//LockMonitor.ini");
    			String msg = CommonMethod.getValueFromVector(v, "LockText");
    			if (msg == null || msg.trim().length() <= 0) msg = Language.apply("此收银台暂停服务，请您去其它收银台交款");
    			ps.print("MESSAGEWELCOME|"+msg);
    			
    			String lockimg = CommonMethod.getValueFromVector(v, "LockImage");
    			if (lockimg == null || lockimg.trim().length() <= 0) lockimg = ConfigClass.BackImagePath + "lockimg.jpg"+"|Y|1";
    			if (new File(lockimg.split("\\|")[0]).exists())
    			{
    				CommonMethod.Sleep(100);
    				ps.print("SHOWIMG|" + lockimg);
    			}
    		}
    		else
    		{
    			monitorShowWelcomeInfo();
    			
   				CommonMethod.Sleep(100);
    			ps.print("VIDEOCTRL|PLAY");
    		}
    	}
    	else
    	{
    		if (lock)
    		{
				String line = Convert.appendStringSize("", Language.apply("很抱歉，此收银台暂停服务"), 0, 40, 90, 2);
				String line1= Convert.appendStringSize("", Language.apply("请您去其它收银台交款"), 0, 40, 90, 2);
				line  += "#@#" + (37+GlobalVar.secFont) + "#@#255_0_0";
				line1 += "#@#" + (37+GlobalVar.secFont) + "#@#255_0_0";
	       		ps.println("1#@#" + line);
	    		ps.println("2#@#" + line1);
    			
	    		String lockimg = ConfigClass.BackImagePath + "lockimg.jpg";
	    		if (new File(lockimg).exists()) ps.println("lock#@#" + lockimg);
    		}
    		else
    		{
	       		ps.println("1#@#"+ " ");
	    		ps.println("2#@#"+ " ");    			
    			ps.println("close");
    			ps.println("unlock#@#");
    		}
    	}
    }
    
    public void monitorClose()
    {
    	if (ConfigClass.DisplayMode.toLowerCase().indexOf("videoplayer") >= 0)
    	{
    		ps.print("exit");
    	}
    	else
    	{
    		ps.println("dispose");
    	}
    }

    public void monitorShowWelcomeInfo()
    {
    	if (ConfigClass.DisplayMode.toLowerCase().indexOf("videoplayer") >= 0)
    	{
    		// 方便调试
    		if (ConfigClass.DebugMode && havetemplate) havetemplate = DisplayAdvertMode.getDefault().ReadTemplateFile();
    		
    		if (havetemplate) ps.print("MESSAGEWELCOME|"+DisplayAdvertMode.getDefault().lineDisplayWelcome());
    		else ps.print("MESSAGEWELCOME|");
    	}
    	else
    	{
    		ps.println("close");
    	}
    }
    
    public void monitorShowGoodsInfo(String lab1,String lab2,int index)
    {
    	if (ConfigClass.DisplayMode.toLowerCase().indexOf("videoplayer") >= 0)
    	{
    		// 方便调试
    		if (ConfigClass.DebugMode && havetemplate) havetemplate = DisplayAdvertMode.getDefault().ReadTemplateFile();
    		
    		if (havetemplate) ps.print("MESSAGEGOODS|"+DisplayAdvertMode.getDefault().lineDisplayGoods(index));
    		else ps.print("MESSAGEGOODS|"+lab1+"\n"+lab2);
    	}
    	else
    	{
    		 ps.println("1#@#" + lab1);
    		 ps.println("2#@#" + lab2);
    	}
    }

    public void monitorShowTotalInfo(String lab1,String lab2)
    {
    	if (ConfigClass.DisplayMode.toLowerCase().indexOf("videoplayer") >= 0)
    	{
    		// 方便调试
    		if (ConfigClass.DebugMode && havetemplate) havetemplate = DisplayAdvertMode.getDefault().ReadTemplateFile();
    		
    		if (havetemplate) ps.print("MESSAGETOTAL|"+DisplayAdvertMode.getDefault().lineDisplayTotal());
    		else ps.print("MESSAGETOTAL|"+lab1+"\n"+lab2);
    	}
    	else
    	{
    		 ps.println("1#@#" + lab1);
    		 ps.println("2#@#" + lab2);
    	}      	
    }

    public void monitorShowPayInfo(String lab1,String lab2)
    {
    	if (ConfigClass.DisplayMode.toLowerCase().indexOf("videoplayer") >= 0)
    	{
    		// 方便调试
    		if (ConfigClass.DebugMode && havetemplate) havetemplate = DisplayAdvertMode.getDefault().ReadTemplateFile();
    		
    		if (havetemplate) ps.print("MESSAGEPAY|"+DisplayAdvertMode.getDefault().lineDisplayPay());
    		else ps.print("MESSAGEPAY|"+lab1+"\n"+lab2);
    	}
    	else
    	{
    		 ps.println("1#@#" + lab1);
    		 ps.println("2#@#" + lab2);
    	}
    }

    public void monitorShowChangeInfo(String lab1,String lab2)
    {
    	if (ConfigClass.DisplayMode.toLowerCase().indexOf("videoplayer") >= 0)
    	{
    		// 方便调试
    		if (ConfigClass.DebugMode && havetemplate) havetemplate = DisplayAdvertMode.getDefault().ReadTemplateFile();
    		
    		if (havetemplate) ps.print("MESSAGECHANGE|"+DisplayAdvertMode.getDefault().lineDisplayChange());
    		else ps.print("MESSAGECHANGE|"+lab1+"\n"+lab2);
    	}
    	else
    	{
    		 ps.println("1#@#" + lab1);
    		 ps.println("2#@#" + lab2);
    	}      	
    }
    public void monitorShowPhoneInfo(String lab1,String lab2)
    {
    	if (ConfigClass.DisplayMode.toLowerCase().indexOf("videoplayer") >= 0)
    	{
    		// 方便调试
    		if (ConfigClass.DebugMode && havetemplate) havetemplate = DisplayAdvertMode.getDefault().ReadTemplateFile();
    		
    		ps.print("MESSAGECHANGE|"+lab1+"\n"+lab2);
    	}
    	else
    	{
    	    ps.println("1#@#" + lab1);
    	    ps.println("2#@#" + lab2);
    	}
    }
    
    public void reconnect()
    {
        try
        {
            ps.close();
            s.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        try
        {
            s = new Socket(serverName, port);

            //InputStreamReader isr = new InputStreamReader(s.getInputStream());
            ps     = new PrintStream(s.getOutputStream());
            status = true;
        }
        catch (UnknownHostException e)
        {
            status = false;

            e.printStackTrace();
        }
        catch (IOException e)
        {
            status = false;
            
            e.printStackTrace();
        }
    }
    
    public void sendCmd(String cmd)
    {
    	if (ps != null)
    	{
    		ps.print(cmd);
    	}
    }
}
