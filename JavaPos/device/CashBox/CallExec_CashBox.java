package device.CashBox;

import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_CashBox;
import com.efuture.javaPos.Global.Language;

public class CallExec_CashBox implements Interface_CashBox
{
	String filepath		= null;						// 文件路径
	String execfilename = null;						// 执行的文件名称
	String cmdpara 		= null;						// 命令参数					
	
	
    public boolean canCheckStatus()
    {
        return false;
    }

    public void close()
    {
    }

    public boolean getOpenStatus()
    {
    	return false;
    }

    public boolean open()
    {
    	if (DeviceName.deviceCashBox.length() <= 0) return false;
    	
    	try
		{
			 String[] arg = DeviceName.deviceCashBox.split(",");
			 
			 
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
					 cmdpara = ManipulateStr.getIndexStr(DeviceName.deviceCashBox,',',2);
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
					 cmdpara = DeviceName.deviceCashBox.substring(DeviceName.deviceCashBox.indexOf(',') + 1,DeviceName.deviceCashBox.length());
				 }
			 }
			 
		}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    	}
        
        return true;
    }

    public void openCashBox()
    {
    	try
		{
    		if (!WriteRequest()) return ;
        	
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
    
    private boolean WriteRequest()
    {
    	PrintWriter pw = null;
    	try
    	{
    		if (PathFile.fileExist(filepath + "request.txt"))
            {
            	PathFile.deletePath(filepath + "request.txt");
            	
            	if (PathFile.fileExist(filepath + "request.txt"))
            	{
            		new MessageBox(Language.apply("钱箱请求文件无法删除"));
            	}
            }
            
            pw = CommonMethod.writeFile(filepath + "request.txt");
        	
        	if (pw != null)
            {
                pw.println(cmdpara);
            }
        	
    		return true;
    	}
    	catch (Exception ex)
    	{
    		new MessageBox(Language.apply("写入钱箱文件模块异常!") + "\n\n" + ex.getMessage(), null, false);
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
    }

	public Vector getPara() {
		Vector v = new Vector();
		v.add(new String[]{ Language.apply("适合所有exe调用钱箱:桑达cr3000 CASHSED") + ",50,50,56576|632" });
		return v;
	}

	public String getDiscription() 
	{
		return Language.apply("调用第三方动态库钱箱");
	}
}
