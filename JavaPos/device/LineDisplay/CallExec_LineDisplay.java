package device.LineDisplay;

import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_LineDisplay;
import com.efuture.javaPos.Global.Language;

public class CallExec_LineDisplay implements Interface_LineDisplay
{
	String filepath		= null;						// 文件路径
	String execfilename = null;						// 执行的文件名称
	String initcode 	= null;						// 初始化客显
	String opencode 	= null;						// 打开客显
	String displaycode  = null;						// 显示客显
	String clearcode    = null;						// 清屏客显
	String closecode    = null;						// 关闭客显
	
	public void clearText()
	{
		try
		{
			if (clearcode == null || clearcode.equals("")) return ;
			
    		if (!WriteRequest(clearcode)) return ;
        	
			CommonMethod.waitForExec(execfilename);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (PathFile.fileExist(filepath + "request.txt"))
            {
            	PathFile.deletePath(filepath + "request.txt");	
            }
		}
		
	}

	public void close()
	{
		try
		{
			if (closecode == null || closecode.equals("")) return ;
			
    		if (!WriteRequest(closecode)) return ;
        	
			CommonMethod.waitForExec(execfilename);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (PathFile.fileExist(filepath + "request.txt"))
            {
            	PathFile.deletePath(filepath + "request.txt");	
            }
		}
		
	}

	public void display(String message)
	{
		 displayAt(0, 0, message);	
	}

	public void displayAt(int row, int col, String message)
	{
		try
		{
    		if (!WriteRequest(displaycode,row,col,message)) return ;
        	
			CommonMethod.waitForExec(execfilename);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (PathFile.fileExist(filepath + "request.txt"))
            {
            	PathFile.deletePath(filepath + "request.txt");	
            }
		}
	}

	public String getDiscription()
	{
		return Language.apply("调用第三方动态库客显");
	}

	public Vector getPara()
	{
		Vector v = new Vector();
		v.add(new String[]{Language.apply("适合所有exe调用客显:中科英泰") + " DISPLAYWINTECCD320"});
		return v;
	}

	public boolean open()
	{
		if (DeviceName.deviceLineDisplay.length() <= 0) return false;
		
		try
		{
			 String[] arg = DeviceName.deviceLineDisplay.split(",");
			 
			 if (arg[0].length() < 15 || !arg[0].substring(0,15).equals("javaposbank.exe"))
			 {
				 if (arg.length > 0)
				 {
					 filepath = arg[0];
				 }
			 
				 if (arg.length > 1)
				 {
					 execfilename = arg[1];
				 }
			 
				 if (arg.length > 2)
				 { 
					initcode = arg[2]; 
				 }
				 
				 if (arg.length > 3)
				 { 
					 opencode = arg[3];
				 }
				 
				 if (arg.length > 4)
				 {
					 displaycode = arg[4];
				 }
				 
				 if (arg.length > 5)
				 {
					 clearcode = arg[5];
				 }
				 
				 if (arg.length > 6)
				 {
					 closecode = arg[6];
				 } 
				 
				
			 }
			 else
			 {
				 filepath = "C:\\JavaPOS\\";
				 
				 if (arg.length > 0)
				 {
					 execfilename = arg[0];
				 }
			 
				 if (arg.length > 1)
				 { 
					initcode = arg[1]; 
				 }
				 
				 if (arg.length > 2)
				 { 
					 opencode = arg[2];
				 }
				 
				 if (arg.length > 3)
				 {
					 displaycode = arg[3];
				 }
				 
				 if (arg.length > 4)
				 {
					 clearcode = arg[4];
				 }
				 
				 if (arg.length > 5)
				 {
					 closecode = arg[5];
				 }
			 }
			 
			 if (opencode == null || opencode.equals("")) return true;
				
	    	 if (!WriteRequest(opencode)) return false;
	        	
	    	 CommonMethod.waitForExec(execfilename);
				
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (PathFile.fileExist(filepath + "request.txt"))
            {
            	PathFile.deletePath(filepath + "request.txt");	
            }
		}
		
		return true;
	}
	
	private boolean WriteRequest(String cmdcode)
    {
		return this.WriteRequest(cmdcode,0,0,"");
    }
	
	private boolean WriteRequest(String cmdcode,int row,int col,String message)
    {
    	PrintWriter pw = null;
    	
    	try
    	{
    		if (PathFile.fileExist(filepath + "request.txt"))
            {
            	PathFile.deletePath(filepath + "request.txt");
            	
            	if (PathFile.fileExist(filepath + "request.txt"))
            	{
            		new MessageBox(Language.apply("客显请求文件无法删除,无法写入请求文件"));
            		return false;
            	}
            }
            
            pw = CommonMethod.writeFile(filepath + "request.txt");
        	
        	if (pw != null)
            {
        		if (cmdcode != null)
        		{
        			pw.println(cmdcode + "," + row + "," + col + "," + message);
        		}
        		else
        		{
        			pw.println(row + "," + col + "," + message);
        		}
            }
        	
    		return true;
    	}
    	catch (Exception ex)
    	{
    		new MessageBox(Language.apply("写入客显文件模块异常!") + "\n\n" + ex.getMessage(), null, false);
    		return false;
    	}
    	finally
    	{
    		if (pw != null)
    		{
    			pw.flush();
                pw.close();
    			pw = null;
    		}
    	}
    }
	
	public void setEnable(boolean enable)
	{
		try
		{
			if (initcode == null || initcode.equals("")) return ;
			
    		if (!WriteRequest(initcode)) return ;
        	
			CommonMethod.waitForExec(execfilename);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (PathFile.fileExist(filepath + "request.txt"))
            {
            	PathFile.deletePath(filepath + "request.txt");	
            }
		}
	}

}
