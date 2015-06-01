package device.ICCard;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.Interface.Interface_ICCard;
import com.efuture.javaPos.Global.ConfigClass;

public class SZZYYS_ICCard implements Interface_ICCard
{
	private int cardType = 1;
	
	public boolean close()
	{
		// TODO 自动生成方法存根
		return true;
	}

	public String findCard()
	{
		try
		{
			 // 先删除上次交易数据文件
            if (PathFile.fileExist(ConfigClass.BankPath + "\\request.txt"))
            {
                PathFile.deletePath(ConfigClass.BankPath + "\\request.txt");
                
                if (PathFile.fileExist(ConfigClass.BankPath + "\\request.txt"))
                {
            		new MessageBox("删除上次交易的请求文件失败");
            		return null;   	
                }
            }
            
            if (PathFile.fileExist(ConfigClass.BankPath + "\\result.txt"))
            {
                PathFile.deletePath(ConfigClass.BankPath + "\\result.txt");
                
                if (PathFile.fileExist(ConfigClass.BankPath + "\\result.txt"))
                {
            		new MessageBox("删除上次交易的应答文件失败");
            		return null;   	
                }
            }
            
			String request = "";
			// 会员卡格式
			if (this.cardType == 1)
			{
				request = "000000000000000000000000000000000   002";
			}
			else
			{
				new MessageBox("错误的卡类型，生成请求数据失败 " + this.cardType);
				return null;
			}
			
	        PrintWriter pw = null;
            pw = CommonMethod.writeFile(ConfigClass.BankPath + "\\request.txt");
            if (pw != null)
            {
                pw.println(request);
                pw.flush();
            }
        	if (pw != null)
        	{
        		pw.close();
        	}
        	
			if (PathFile.fileExist(ConfigClass.BankPath + "\\javaposbank.exe"))
			{
				CommonMethod.waitForExec(ConfigClass.BankPath + "\\javaposbank.exe NJYSMZDABC", "javaposbank.exe");
			}
			else
			{
				new MessageBox("找不到" + ConfigClass.BankPath + "\\javaposbank.exe");
				return null;
			}
			
			BufferedReader br = null;
			if (!PathFile.fileExist(ConfigClass.BankPath + "\\result.txt") || ((br = CommonMethod.readFile(ConfigClass.BankPath + "\\result.txt")) == null))
			{
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return null;
			}
			String result = br.readLine();
			return result;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public String getDiscription()
	{
		// TODO 自动生成方法存根
		return "深圳卓越银联IC卡设备";
	}

	public Vector getPara()
	{
		// TODO 自动生成方法存根
		return null;
	}

	public boolean open()
	{
		return true;
	}

	public String updateCardMoney(String cardno, String operator, double ye)
	{
		return "error:该设备不支持本功能";
	}

}
