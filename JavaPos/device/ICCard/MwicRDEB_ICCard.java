package device.ICCard;

import java.io.BufferedReader;
import java.io.PrintWriter;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.DeviceName;

public class MwicRDEB_ICCard extends Mwic_ICCard
{
	public String getDiscription()
	{
		return "明华IC(RDEB)卡设备";
	}

	public String findCard()
	{
    	if (DeviceName.deviceICCard.length() <= 0) return null;
    	
        try
        {    	
	        String[] arg = DeviceName.deviceICCard.split(",");
	
        	StringBuffer line = new StringBuffer();
	        	
        	line.append("read");
        	line.append(",");
        	
        	if (arg.length > 0) line.append(arg[0]);
        	else line.append("0");
	        line.append(",");
	        
            if (arg.length > 1) line.append(arg[1]);
            else line.append("9600");
	        line.append(",");

            if (arg.length > 2) line.append(arg[2]);
            else line.append("32");
	        line.append(",");
	        
            if (arg.length > 3) line.append(arg[3]);
            else line.append("10");
	        line.append(",");
	        
            if (arg.length > 4) line.append(arg[4]);
            else line.append("10");
	        line.append(",");
	        
            if (arg.length > 5) line.append(arg[5]);
            else line.append("50");

            String cmd = "";
            
            if (arg.length > 6 && arg[6].trim().length() > 0) cmd = arg[6];
            else cmd = "MWICRDEB";
            
			//	先删除上次交易数据文件
			if (PathFile.fileExist("request.txt"))
			{
				PathFile.deletePath("request.txt");
			   
				if (PathFile.fileExist("request.txt"))
				{
					new MessageBox("读卡请求文件request.txt无法删除,请重试");
					return null;   	
				}
			}
			if (PathFile.fileExist("result.txt"))
			{
				PathFile.deletePath("result.txt");
			   
				if (PathFile.fileExist("result.txt"))
				{
					new MessageBox("读卡结果文件result.txt无法删除,请重试");
					return null;   	
				}
			}
			
			// 写入请求
			PrintWriter pw = CommonMethod.writeFile("request.txt");
			pw.write(line.toString());
			pw.close();
			
            // 调用接口模块
            if (PathFile.fileExist("javaposbank.exe"))
            {
            	CommonMethod.waitForExec("javaposbank.exe " + cmd);
            }
            else
            {
                new MessageBox("找不到IC卡模块 javaposbank.exe");
                return null;
            }

            // 读取应答
            BufferedReader br = null;
            if (!PathFile.fileExist("result.txt") || ((br = CommonMethod.readFileGBK("result.txt")) == null))
            {
                new MessageBox("读取卡号应答数据失败!");
                return null;
            }
            String cardno = br.readLine();
            br.close();
            
            return cardno;
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	return null;
        }
	}
	
	public String updateCardMoney(String cardno, String operator, double ye)
	{
        try
        {    	
        	String[] arg = DeviceName.deviceICCard.split(",");
        	
        	StringBuffer line = new StringBuffer();
	        	
        	line.append("write");
        	line.append(",");
        	
        	if (arg.length > 0) line.append(arg[0]);
        	else line.append("0");
	        line.append(",");
	        
            if (arg.length > 1) line.append(arg[1]);
            else line.append("9600");
	        line.append(",");

            if (arg.length > 2) line.append(arg[2]);
            else line.append("32");
	        line.append(",");
	        
            if (arg.length > 3) line.append(arg[3]);
            else line.append("10");
	        line.append(",");
	        
            if (arg.length > 4) line.append(arg[4]);
            else line.append("10");
	        line.append(",");
	        
            if (arg.length > 5) line.append(arg[5]);
            else line.append("50");
            line.append(",");
            
            line.append(cardno);
            
            String cmd = "";
            
            if (arg.length > 6 && arg[6].trim().length() > 0) cmd = arg[6];
            else cmd = "MWICRDEB";
            
			//	先删除上次交易数据文件
			if (PathFile.fileExist("request.txt"))
			{
				PathFile.deletePath("request.txt");
			   
				if (PathFile.fileExist("request.txt"))
				{
					new MessageBox("读卡请求文件request.txt无法删除,请重试");
					return null;   	
				}
			}
			if (PathFile.fileExist("result.txt"))
			{
				PathFile.deletePath("result.txt");
			   
				if (PathFile.fileExist("result.txt"))
				{
					new MessageBox("读卡结果文件result.txt无法删除,请重试");
					return null;   	
				}
			}
			
			// 写入请求
			PrintWriter pw = CommonMethod.writeFile("request.txt");
			pw.write(line.toString());
			pw.close();
			
            // 调用接口模块
            if (PathFile.fileExist("javaposbank.exe"))
            {
            	CommonMethod.waitForExec("javaposbank.exe " + cmd);
            }
            else
            {
                new MessageBox("找不到IC卡模块 javaposbank.exe");
                return null;
            }

            // 读取应答
            BufferedReader br = null;
            if (!PathFile.fileExist("result.txt") || ((br = CommonMethod.readFileGBK("result.txt")) == null))
            {
                new MessageBox("读取卡号应答数据失败!");
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
			if (PathFile.fileExist("request.txt"))
			{
				PathFile.deletePath("request.txt");
			}
			if (PathFile.fileExist("result.txt"))
			{
				PathFile.deletePath("result.txt");
			}
        }
	}
}
