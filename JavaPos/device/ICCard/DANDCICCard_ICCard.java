package device.ICCard;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Global.ConfigClass;

public class DANDCICCard_ICCard extends Mwurf35lt_ICCard
{
	public boolean open()
	{
		return true;
	}
	
	public boolean close()
	{
		return true;
	}

	public String getDiscription()
	{
		return "D&C IC卡设备";
	}

	public String findCard()
	{
    	if (DeviceName.deviceICCard.length() <= 0) return null;
    	
        try
        {    	
	        String[] arg = DeviceName.deviceICCard.split(",");
	
        	StringBuffer line = new StringBuffer();
	        	
        	if (arg.length > 0) line.append(arg[0]);
        	else line.append("100");
	        line.append(",");
	        
            if (arg.length > 1) line.append(arg[1]);
            else line.append("115200");
	        line.append(",");

            if (arg.length > 2) line.append(arg[2]);
            else line.append("27");
	        line.append(",");
	        
            if (arg.length > 3) line.append(arg[3]);
            else line.append("8");
	        line.append(",");
	        
	        String key = ManipulatePrecision.getRegisterCodeKey(ConfigClass.CDKey);
	           
            if (arg.length > 4 && arg[4].trim().length() > 0) 
            {
    	        // 解密IC卡密码A
    	        String pasa = ManipulatePrecision.DecodeString(arg[4].trim(), key);
            	line.append(pasa);
            	//line.append(arg[4].trim());
            }
            else line.append("");
	        line.append(",");
	        
            if (arg.length > 5 && arg[5].trim().length() > 0)
            {
    	        // 解密IC卡密码B
    	        String pasb = ManipulatePrecision.DecodeString(arg[5], key);
            	line.append(pasb);
            	//line.append(arg[5].trim());
            }
            else line.append("");
            line.append(",");
            
            if (arg.length > 6) line.append(arg[6]);
            else line.append("10");
	        line.append(",");
            
            if (arg.length > 7) line.append(arg[7]);
            else line.append("50");
	        
            if (arg.length > 8) line.append(arg[8]);
            else line.append(0);
            
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
            	CommonMethod.waitForExec("javaposbank.exe DANDCICCARD");
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
            
            if (arg.length > 9)
            {
            	cardno = cardno.trim();
            	if (arg[9].trim().equals("Y"))
            	{
            		int i = cardno.length() - 1;
            		for (i = (cardno.length() - 1); i >=0 ; i-- )
            		{
            			char a = cardno.charAt(i);
            			if (a != 'F')
            			{
            				break;
            			}
            		}
            		cardno = cardno.substring(0,i+1);
            	}
            }
            return cardno;
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	return null;
        }
        finally
        {
			//	先删除上次交易数据文件
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
	
	public Vector getPara()
	{
		Vector v = new Vector();
		v.add(new String[]{"端口号","0","1","2","3","100"});
		v.add(new String[]{"波特率","9600","14400","19200","28800","38400","57600","115200"});
		v.add(new String[]{"卡号地址"});
		v.add(new String[]{"卡号的长度"});
		v.add(new String[]{"密码A"});
		v.add(new String[]{"密码B"});
		v.add(new String[]{"读卡成功蜂鸣时长(ms)"});
		v.add(new String[]{"读卡失败蜂鸣时长(ms)"});
		v.add(new String[]{"卡号偏移位置"});
		v.add(new String[]{"是否自动截取卡号后的F","Y","N"});
		return v;
	}
}
