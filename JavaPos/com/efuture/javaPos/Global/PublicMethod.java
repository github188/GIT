package com.efuture.javaPos.Global;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;

public class PublicMethod
{
	private static long timestart = -1;
	
    public static void DEBUG_MSG(String msg)
    {
        if (ConfigClass.DebugMode == true)
        {
            System.out.println(msg);
        }
    }

    public static void DEBUG_MSGBOX(String msg)
    {
        if (ConfigClass.DebugMode == true)
        {
            new MessageBox(msg);
        }
    }
    
    //检查是否有致命错误，马上退出
    public static void forceQuit()
    {
        LoadSysInfo.getDefault().ExitSystem();
    }
    
    // 创建全局异常日志文件
    public static PrintStream createExceptionStream()
    {
    	try
    	{
    		// 开发模式错误信息还是输出到控制台,便于开发人员查看
    		if (ConfigClass.isDeveloperMode()) return System.err;
    			
    		// 检查是否需要生成新的异常日志文件
    		boolean append = false;
    		if (new File("exception.tm").exists())
    		{
    			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("exception.tm"), "UTF-8"));
    			String line = null;
                while ((line = br.readLine()) != null)
                {
                    if (line.length() <= 0)
                    {
                        continue;
                    }
                    break;
                }
    			br.close();
    			if (new ManipulateDateTime().compareDate(ManipulateDateTime.getCurrentDate(),line) <= 3)
    			{
    				// 每3天重新生成异常日志文件,以避免日志文件过大
    				append = true;
    			}
    		}
    		
    		// 生成新的异常日志文件时间标记
    		if (!append)
    		{
    			PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("exception.tm",false),"UTF-8")));
    			pw.println(ManipulateDateTime.getCurrentDate());
    			pw.close();
    			
    			// 备份上次异常日志
    			if (new File("exception.log").exists()) PathFile.copyPath("exception.log", "exception.bak");
    		}
    		
    		// 重定向系统err输出流到exception.log文件
	    	GlobalInfo.exceptionps = new PrintStream(new FileOutputStream("exception.log",append), false);
	    	GlobalInfo.exceptionps.println("****************** "+ManipulateDateTime.getCurrentDateTime()+" *******************");
    		System.setErr(GlobalInfo.exceptionps);
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
            new MessageBox(Language.apply("创建全局异常日志失败，即将关闭系统\n") + ex.getMessage(), null, false);
    	}
    	
    	return GlobalInfo.exceptionps;
    }
/*    
    public static void printStackTrace(Exception ex)
    {
    	ex.printStackTrace();
        if (GlobalInfo.exceptionps == null) return;
        
        ManipulateDateTime mdt = new ManipulateDateTime();
        GlobalInfo.exceptionps.print(mdt.getDateTimeString() + "        ");           
        ex.printStackTrace(GlobalInfo.exceptionps);
        GlobalInfo.exceptionps.flush();
    }
*/    
    
    public static PrintWriter createCmdLog(String file)
    {
    	try
    	{
    		if (GlobalInfo.cmdlogwriter == null)
    		{
        		// 检查是否需要生成新的跟踪日志文件
        		boolean append = false;
        		if (new File("enableCmd.tm").exists())
        		{
        			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("enableCmd.tm"), "UTF-8"));
        			String line = ManipulateDateTime.getCurrentDate();
                    while ((line = br.readLine()) != null)
                    {
                        if (line.length() <= 0)
                        {
                            continue;
                        }
                        break;
                    }
        			br.close();
        			if (new ManipulateDateTime().compareDate(ManipulateDateTime.getCurrentDate(),line) <= 3)
        			{
        				// 每3天重新生成跟踪日志文件,以避免日志文件过大
        				append = true;
        			}
        		}
        		
        		// 生成新的跟踪日志文件时间标记
        		if (!append)
        		{
        			PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("enableCmd.tm",false),"UTF-8")));
        			pw.println(ManipulateDateTime.getCurrentDate());
        			pw.close();
        			
        			// 备份上次跟踪日志
        			if (new File(file).exists()) PathFile.copyPath(file, file + ".bak");
        		}
        		
        		// 创建跟踪日志文件
    			GlobalInfo.cmdlogwriter = new PrintWriter(new BufferedWriter(new FileWriter(file, append)),true);
    		}
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}    	
    	
    	return GlobalInfo.cmdlogwriter;
    }
    
    public static PrintWriter createTraceDebugLog(String file)
    {
    	try
    	{
    		if (GlobalInfo.printwriter == null)
    		{
        		// 检查是否需要生成新的跟踪日志文件
        		boolean append = false;
        		if (new File("debugtrace.tm").exists())
        		{
        			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("debugtrace.tm"), "UTF-8"));
        			String line = null;
                    while ((line = br.readLine()) != null)
                    {
                        if (line.length() <= 0)
                        {
                            continue;
                        }
                        break;
                    }
        			br.close();
        			if (new ManipulateDateTime().compareDate(ManipulateDateTime.getCurrentDate(),line) <= 3)
        			{
        				// 每3天重新生成跟踪日志文件,以避免日志文件过大
        				append = true;
        			}
        		}
        		
        		// 生成新的跟踪日志文件时间标记
        		if (!append)
        		{
        			PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("debugtrace.tm",false),"UTF-8")));
        			pw.println(ManipulateDateTime.getCurrentDate());
        			pw.close();
        			
        			// 备份上次跟踪日志
        			if (new File(file).exists()) PathFile.copyPath(file, file + ".bak");
        		}
        		
        		// 创建跟踪日志文件
    			GlobalInfo.printwriter = new PrintWriter(new BufferedWriter(new FileWriter(file, append)),true);
    		}
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}    	
    	
    	return GlobalInfo.printwriter;
    }
    
    public static void traceDebugLog(String msg)
    {
    	if (GlobalInfo.sysPara == null || !GlobalInfo.sysPara.debugtracelog) return;
    	
    	try
    	{
    		if (GlobalInfo.printwriter == null) createTraceDebugLog("debugtrace.log");
    		
	        if (GlobalInfo.printwriter != null)
	        {
	            ManipulateDateTime mdt = new ManipulateDateTime();
	            GlobalInfo.printwriter.println(mdt.getDateTimeString() + "     " + msg);
	            GlobalInfo.printwriter.flush();
	            mdt = null;
	        }
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }
    
    
    public static void traceCmdLog(String msg)
    {
    	try
    	{
    		if (GlobalInfo.cmdlogwriter == null)
    			createCmdLog("enableCmd.log");
    		
	        if (GlobalInfo.cmdlogwriter != null)
	        {
	            ManipulateDateTime mdt = new ManipulateDateTime();
	            GlobalInfo.cmdlogwriter.println(mdt.getDateTimeString() + "     " + msg);
	            GlobalInfo.cmdlogwriter.flush();
	            mdt = null;
	        }
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }
    
    public static void timeStart(String msg)
    {
    	GlobalInfo.statusBar.setHelpMessage(msg);
    	timestart = System.currentTimeMillis();
    }
    
    public static void timeEnd(String msg)
    {
    	if (timestart == -1) return;
    	
        long end = System.currentTimeMillis();
        GlobalInfo.statusBar.setHelpMessage(msg + (end - timestart) + " ms");
        timestart = -1;
    }
    
    public static boolean transferInfo(Object orObject, Object destObject,
                                       String name, String name1)
    {
        Field field = null;
        Field field1 = null;
        Class classInst = orObject.getClass();
        Class classInst1 = destObject.getClass();
        String[] ref;
        String[] ref1;

        try
        {
            ref  = (String[]) orObject.getClass().getDeclaredField(name)
                                      .get(orObject);
            ref1 = (String[]) destObject.getClass().getDeclaredField(name1)
                                        .get(destObject);

            if (ref.length != ref1.length)
            {
                return false;
            }

            for (int i = 0; i < ref.length; i++)
            {
                if (ref[i].length() <= 0)
                {
                    continue;
                }

                if (ref1[i].length() <= 0)
                {
                    continue;
                }

                field  = classInst.getDeclaredField(ref[i]);
                field1 = classInst1.getDeclaredField(ref1[i]);

                try
                {
                    if (field1.getType().getName().equals("java.lang.String"))
                    {
                        field1.set(destObject, field.get(orObject).toString());
                    }
                    else if (field1.getType().getName().equals("char"))
                    {
                        field1.setChar(destObject, field.getChar(orObject));
                    }
                    else if (field1.getType().getName().equals("int"))
                    {
                        field1.setInt(destObject, field.getInt(orObject));
                    }
                    else if (field1.getType().getName().equals("double"))
                    {
                        field1.setDouble(destObject, field.getDouble(orObject));
                    }
                    else if (field1.getType().getName().equals("float"))
                    {
                        field1.setFloat(destObject, field.getFloat(orObject));
                    }
                    else if (field1.getType().getName().equals("long"))
                    {
                        field1.setLong(destObject, field.getLong(orObject));
                    }
                }
                catch (Exception er)
                {
                    continue;
                }
            }

            return true;
        }
        catch (SecurityException e)
        {
        	e.printStackTrace();
            return false;
        }
        catch (NoSuchFieldException e)
        {
        	e.printStackTrace();
            return false;
        }
        catch (IllegalArgumentException e)
        {
        	e.printStackTrace();
            return false;
        }
        catch (IllegalAccessException e)
        {
        	e.printStackTrace();
            return false;
        }
    }
}
