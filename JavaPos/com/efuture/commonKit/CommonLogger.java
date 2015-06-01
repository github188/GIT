package com.efuture.commonKit;

import java.io.PrintWriter;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;

public class CommonLogger
{
	private String logFile;
	private boolean isEnable;

	public static CommonLogger logger = new CommonLogger();

	public static CommonLogger getDefault()
	{
		return logger;
	}

	public CommonLogger()
	{
		this("temp");
	}

	public CommonLogger(String name)
	{
		this(name, true);
	}

	public CommonLogger(String name, boolean flag)
	{
		logFile = new ManipulateDateTime().getDateByEmpty() + "_" + name + ".log";
		isEnable = flag;
	}

	public CommonLogger(boolean isenableseqno)
	{
		init(isenableseqno,"");
	}
	
	public CommonLogger(boolean isenableseqno,String prefix)
	{
		init(isenableseqno,prefix);
	}

	public void init(boolean isenableseqno,String prefix)
	{
		// 启用系统流水记录日志
		if (isenableseqno)
		{
			String date = GlobalInfo.balanceDate.replaceAll("/", "");
			String logdir = ConfigClass.LocalDBPath + "Invoice/" + date + "/" + "log";

			if (!PathFile.fileExist(logdir))
				PathFile.createDir(logdir);

			if (PathFile.fileExist(logdir))
				isEnable = true;

			logFile = logdir + "/" + prefix+GlobalInfo.syjStatus.fphm + ".log";
		}
	}

	public void setEnable(boolean flag)
	{
		this.isEnable = flag;
	}

	public void log(String msg)
	{
		log(msg, "", true);
	}

	public void log(String msg, String splitter)
	{
		log(msg, splitter, true);
	}

	public void log(String msg, String splitter, boolean flag)
	{
		if (!isEnable)
			return;

		PrintWriter pw = null;
		String logDate = "[" + ManipulateDateTime.getDateTimeAll() + "]: ";

		try
		{
			pw = CommonMethod.writeFileAppend(logFile);
			pw.print(logDate + msg + splitter);

			if (flag)
				pw.print("\r\n");

			pw.flush();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (pw != null)
				pw.close();
			pw = null;
		}

	}

}
