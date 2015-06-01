package com.efuture.DeBugTools;

import java.io.File;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;

public class PosLog
{
	private  static boolean connect = true;
	private  static PosLog log = null;

	// 用户没有获取到收银机的时候
	private Vector info_vec = null;
	private Vector debug_vec = null;
	private Vector warn_vec = null;
	private Vector error_vec = null;
	private Vector fatal_vec = null;
	
	private String time = null;
	
	private String Month = null;
	
	
	private String errorClassName = null;
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

	}
	
	public PosLog()
	{
		Month = new ManipulateDateTime().getDateByEmpty().substring(0, 6);
		
		//只保留3个月的日志
		String year = Month.substring(0,4);
		int ye = Convert.toInt(year);
		String mon = Month.substring(4);
		int mo = Convert.toInt(mon);
		
		String[] months = new String[4];
		for (int i = 0 ; i < 4 ; i++)
		{
			if (mo == 0)
			{
				mo = 12;
				ye--;
			}
			months[i] = String.valueOf(ye)+String.valueOf(Convert.increaseInt(mo, 2));
			mo--;
		}
		
		String[] filelist = PathFile.getAllDirName("PosLog");
		if (filelist != null)
		{
			for (int i = 0 ; i < filelist.length; i++)
			{
				System.out.println(filelist[i]);
				String filename = filelist[i];
				boolean done = false;
				for (int j = 0 ; j < months.length; j++)
				{
					if (filename.equals(months[j]))
					{
						done = true;
						break;
					}
				}
				
				if (!done) PathFile.delAllFile("PosLog//"+filename);
			}
		}
	}
	
	public String getDay()
	{
		return Month;
	}
	
	public void setClass(String name)
	{
		
		if (time == null) time = new ManipulateDateTime().getDateByEmpty();
		if (info_vec == null) info_vec = new Vector();
		if (debug_vec == null) debug_vec = new Vector();
		if (warn_vec == null) warn_vec = new Vector();
		if (error_vec == null) error_vec = new Vector();
		if (fatal_vec == null) fatal_vec = new Vector();
		if (!new File("PosLog").exists()) PathFile.createDir("PosLog");
		
		errorClassName = name;
	}
	
	public void setConnect(boolean done)
	{
		if (!done)
		{
			info(Language.apply("关闭日志"));
		}
		
		connect = done;
		if (connect)
		{
			info(Language.apply("开启日志"));
		}
	}
	
	public boolean getConnect()
	{
		return connect;
	}
	
	public static PosLog getLog(String name)
	{
		if (log == null)
		{
			log = new PosLog();
		}
		
		log.setClass(name);
		
		return log;
	}
	
	public static PosLog getLog(Class cl)
	{
		return getLog((cl != null)?cl.getName():"");
	}
	
	private void printInfo(String type,String info,Vector infovec)
	{
		System.out.println(info);
		
		if (!connect) return ;
		
		String syjh = "Local";
		if (ConfigClass.CashRegisterCode != null)
			syjh = ConfigClass.CashRegisterCode;

		
		String syyh = "Guest";
		if (GlobalInfo.posLogin !=null)
			syyh = GlobalInfo.posLogin.gh;
		
//		 生成写入信息
		String line1 = ManipulateDateTime.getCurrentDateTimeMilliSencond()+"\t"+syyh+"\t"+errorClassName+"\t"+info;
		//String line2 = "\t"+;
		
		//如果没有获取收银机号，将信息记录下来，直到找到收银机号为止
		if (syjh.equals("Local"))
		{
			infovec.add(line1);
			//debug.add(line2);
		}
		if (!PathFile.fileExist("PosLog//"+Month)) PathFile.createDir("PosLog//"+Month);
		
		PrintWriter pw = CommonMethod.writeFileAppend("PosLog//"+Month+"//"+""+type+"-"+syjh+"-"+time+".txt");
		if(pw == null)
			return ;
		
		
		if (!syjh.equals("Local") && infovec.size() > 0)
		{
			for (int i = 0; i < infovec.size(); i++)
			{
				pw.println((String)infovec.elementAt(i));
			}
			infovec.removeAllElements();
		}
		
		pw.println(line1);
		//pw.println(line2);
		pw.flush();
		pw.close();
	}
	
	public void debug(String infos)
	{
		printInfo("debug",infos,debug_vec);	
	}
	
	public void info(String infos)
	{
		printInfo("info",infos,info_vec);	
	}
	
	public void warn(String infos)
	{
		printInfo("warn",infos,warn_vec);	
	}
	
	public void error(String infos)
	{
		printInfo("error",infos,error_vec);	
	}
	
	public void fatal(String infos)
	{
		printInfo("fatal",infos,fatal_vec);	
	}
	
	public void debug(Exception ex)
	{
		printEx("debug",ex);
	}
	
	public void info(Exception ex)
	{
		printEx("info",ex);
	}
	
	public void warn(Exception ex)
	{
		printEx("warn",ex);
	}
	
	public void error(Exception ex)
	{
		printEx("error",ex);
	}
	
	public void fatal(Exception ex)
	{
		printEx("fatal",ex);
	}
	
	public void printEx(String level,Exception ex)
	{
		if (!connect) return ;
		
		String syjh = "Local";
		if (ConfigClass.CashRegisterCode != null)
			syjh = ConfigClass.CashRegisterCode;

		
		String syyh = "Guest";
		if (GlobalInfo.posLogin !=null)
			syyh = GlobalInfo.posLogin.gh;
		
		if (!PathFile.fileExist("PosLog//"+Month)) PathFile.createDir("PosLog//"+Month);
		
		PrintWriter pw = CommonMethod.writeFileAppend("PosLog//"+Month+"//"+level+"-"+syjh+"-"+time+".txt");
		if(pw == null)
			return ;
		
		String line1 = ManipulateDateTime.getCurrentDateTimeMilliSencond()+"\t"+syyh+"\t"+errorClassName+"\t";
		pw.println(line1);
		pw.print("  ");
		ex.printStackTrace(pw);
		pw.flush();
		pw.close();
	}

}
