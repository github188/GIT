
package device.ICCard;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_ICCard;
import com.efuture.javaPos.Global.GlobalInfo;

public class IC_Cpu_cczz implements Interface_ICCard
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
		return "卓展CPU卡";
	}

	public Vector getPara()
	{
		Vector v = new Vector();
		v.add(new String[]{"端口号","0","1","2","3","100"});
		v.add(new String[]{"波特率","9600","110","300","600","1200","2400","4800","19200"});
		v.add(new String[]{"密码"});
		v.add(new String[]{"起始位"});
		v.add(new String[]{"长度"});
		v.add(new String[]{"路径"});
		v.add(new String[]{"javaposbank.exe参数行"});
		return v;
	}

	public String findCard()
	{
    	if (DeviceName.deviceICCard.length() <= 0) return null;
    	if (GlobalInfo.sysPara.cupCardPwd.length() == 0)
		{
    		return "error:cpu卡密码错误！无法使用";
		}
    	
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

	        line.append(GlobalInfo.sysPara.cupCardPwd);
	        line.append(",");
	        
            if (arg.length > 3) line.append(arg[3]);
            else line.append("0");
	        line.append(",");
	        
            if (arg.length > 4) line.append(arg[4]);
            else line.append("32");
	        line.append(",");
	        
	        String path = "./";
            if (arg.length > 5 && arg[5].trim().length() > 0) path = arg[5];

	        
            String cmd = "";
            
            if (arg.length > 6 && arg[6].trim().length() > 0) cmd = arg[6];
            else cmd = "MWIC";
            
			//	先删除上次交易数据文件
			if (PathFile.fileExist(path+"request.txt"))
			{
				PathFile.deletePath(path+"request.txt");
			   
				if (PathFile.fileExist(path+"request.txt"))
				{
					new MessageBox("读卡请求文件request.txt无法删除,请重试");
					return null;   	
				}
			}
			if (PathFile.fileExist(path+"result.txt"))
			{
				PathFile.deletePath(path+"result.txt");
			   
				if (PathFile.fileExist(path+"result.txt"))
				{
					new MessageBox("读卡结果文件result.txt无法删除,请重试");
					return null;   	
				}
			}
			
			// 写入请求
			PrintWriter pw = CommonMethod.writeFile(path+"request.txt");
			pw.write(line.toString());
			pw.close();
			
            // 调用接口模块
            if (PathFile.fileExist(path+"JavaPosIC.exe"))
            {
            	CommonMethod.waitForExec(path+"JavaPosIC.exe " + cmd);
            }
            else
            {
                new MessageBox("找不到IC卡模块 JavaPosIC.exe");
                return null;
            }

            // 读取应答
            BufferedReader br = null;
            if (!PathFile.fileExist(path+"result.txt") || ((br = CommonMethod.readFileGBK(path+"result.txt")) == null))
            {
                //new MessageBox("读取卡号应答数据失败!");
                return "error:读取卡号应答数据失败";
            }
            String cardno = br.readLine();
            br.close();
            if (cardno.indexOf(",")>=0)
            {
            	String flg = cardno.substring(0, cardno.indexOf(","));
            	if (!flg.equals("00"))
            	{
            		if (cardno.indexOf("NUM") > -1)
            		{
            			String strNum = cardno.substring(cardno.indexOf("NUM") + 3, cardno.indexOf("NUM") + 4);
            			int num = Integer.parseInt(strNum);
            			if (num> 0) return "error:密钥错误,刷卡【" + num + "】次后此卡将报废";
            			else return "error:已达到错误次数上限,该卡已锁死";
        				
            		}
            		 return "error:读取卡号应答数据失败";
            	}
            	else
            	{
            		cardno = cardno.substring(cardno.indexOf(",")+1);
            	}
            }
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
		return "error:该设备不支持本功能";
	}
}

