package device.ICCard;

import java.io.BufferedReader;
import java.io.PrintWriter;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.DeviceName;

public class MwicURF35_ICCard extends Mwic_ICCard
{
	public String findCard()
	{
    	if (DeviceName.deviceICCard.length() <= 0) return null;
        BufferedReader br = null;
        try
        {    	
	        String[] arg = DeviceName.deviceICCard.split(",");
	
        	StringBuffer line = new StringBuffer();
	        	
        	if (arg.length > 0) line.append(arg[0]);
        	else line.append("0");
	        line.append(",");
	        
            if (arg.length > 1) line.append(arg[1]);
            else line.append("115200");
	        line.append(",");

            if (arg.length > 2) line.append(arg[2]);
            else line.append("0");
	        line.append(",");
	        
            if (arg.length > 3) line.append(arg[3]);
            else line.append("1");
	        line.append(",");
	        
            if (arg.length > 4) line.append(arg[4]);
            else line.append("0");
	        line.append(",");
	        
            if (arg.length > 5) line.append(arg[5]);
            else line.append("343033373936");

            String cmd = "";
            
            if (arg.length > 6 && arg[6].trim().length() > 0) cmd = arg[6];
            else cmd = "URF35";
            
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
            if (!PathFile.fileExist("result.txt") || ((br = CommonMethod.readFileGBK("result.txt")) == null))
            {
                new MessageBox("读取卡号应答数据失败!");
                return null;
            }
            
            String cardNo = "";
            String strLine = br.readLine();
            if (strLine.indexOf(",") > -1)
            {
            	String[] a = strLine.split(",");
            	if (a[0].equals("00"))
            	{
            		cardNo = a[1];
            	}
            	else
            	{
            		new MessageBox("调用IC卡设备失败，错误码：" + a[0]);
            	}
            }
            
            br.close();
            return cardNo;
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	return null;
        }
	}
	
	public String getDiscription()
	{
		return "明华URF-35IC卡设备";
	}
}
