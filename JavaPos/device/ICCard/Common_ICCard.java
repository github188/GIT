package device.ICCard;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_ICCard;

public class Common_ICCard implements Interface_ICCard
{
	String cmd = null;
	String reqfile = null;
	String retfile = null;
	
	public boolean open()
	{
    	if (DeviceName.deviceICCard.length() <= 0) return false;
    	
    	if (cmd == null)
    	{
    		String[] arg = DeviceName.deviceICCard.split(",");
    		if (arg.length > 0) cmd = arg[0];
    		if (arg.length > 1) reqfile = arg[1];
    		if (arg.length > 2) retfile = arg[2];
    		
    		if (cmd == null || cmd.trim().length() <= 0) cmd = "javaposbank.exe";
    		if (reqfile == null || reqfile.trim().length() <= 0) reqfile = "request.txt";
    		if (retfile == null || retfile.trim().length() <= 0) retfile = "result.txt";
    	}

		return true;
	}
	
	public boolean close()
	{
		return true;
	}

	public String getDiscription()
	{
		return "通用扩展IC卡设备";
	}

	public Vector getPara()
	{
		Vector v = new Vector();
		v.add(new String[]{"IC卡调用命令"});
		v.add(new String[]{"IC卡请求文件"});
		v.add(new String[]{"IC卡应答文件"});
		
		return v;
	}

	public String findCard()
	{
        try
        {    	
			// 先删除上次交易数据文件
			if (PathFile.fileExist(reqfile))
			{
				PathFile.deletePath(reqfile);
			   
				if (PathFile.fileExist(reqfile))
				{
					new MessageBox("读卡请求文件 "+ reqfile + " 无法删除,请重试");
					return null;   	
				}
			}
			if (PathFile.fileExist(retfile))
			{
				PathFile.deletePath(retfile);
			   
				if (PathFile.fileExist(retfile))
				{
					new MessageBox("读卡结果文件 " + retfile + " 无法删除,请重试");
					return null;   	
				}
			}
			
			// 写入请求
			PrintWriter pw = CommonMethod.writeFile(reqfile);
			pw.write("read");
			pw.close();
			
			
			if (!PathFile.fileExist(reqfile))
			{
				new MessageBox("未成功生成request.txt请求文件!");
				return null;
			}
			
            // 调用接口模块
            CommonMethod.waitForExec(cmd);

            // 读取应答
            BufferedReader br = null;
            if (!PathFile.fileExist(retfile) || ((br = CommonMethod.readFileGBK(retfile)) == null))
            {
                new MessageBox("读卡结果文件数据读取失败!");
                return null;
            }
            
            String data = br.readLine();
            br.close();
            
            return data;
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	new MessageBox("IC卡读卡调用异常\n\n" + ex.getMessage());
        	return null;
        }
        finally
        {
			// 删除上次交易数据文件
			if (PathFile.fileExist(reqfile))
			{
				PathFile.deletePath(reqfile);
			}
			if (PathFile.fileExist(retfile))
			{
				PathFile.deletePath(retfile);
			}
        }
	}
	
	public String updateCardMoney(String cardno, String operator, double ye)
	{
        try
        {    	
			// 先删除上次交易数据文件
			if (PathFile.fileExist(reqfile))
			{
				PathFile.deletePath(reqfile);
			   
				if (PathFile.fileExist(reqfile))
				{
					new MessageBox("写卡请求文件 "+ reqfile + " 无法删除,请重试");
					return null;
				}
			}
			if (PathFile.fileExist(retfile))
			{
				PathFile.deletePath(retfile);
			   
				if (PathFile.fileExist(retfile))
				{
					new MessageBox("写卡结果文件 " + retfile + " 无法删除,请重试");
					return null;   	
				}
			}
			
			// 写入请求
			PrintWriter pw = CommonMethod.writeFile(reqfile);
			pw.write("write,"+cardno+","+operator+","+ye);
			pw.close();
			
			if (!PathFile.fileExist(reqfile))
			{
				new MessageBox("未成功生成request.txt请求文件!");
				return null;
			}
			
            // 调用接口模块
            CommonMethod.waitForExec(cmd);

            // 读取应答
            BufferedReader br = null;
            if (!PathFile.fileExist(retfile) || ((br = CommonMethod.readFileGBK(retfile)) == null))
            {
                new MessageBox("写卡结果文件数据读取失败!");
                return null;
            }
            String data = br.readLine();
            br.close();
            
            return data;
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	new MessageBox("IC卡写卡调用异常\n\n" + ex.getMessage());
        	return null;
        }
        finally
        {
			// 删除上次交易数据文件
			if (PathFile.fileExist(reqfile))
			{
				PathFile.deletePath(reqfile);
			}
			if (PathFile.fileExist(retfile))
			{
				PathFile.deletePath(retfile);
			}
        }
	}
}
