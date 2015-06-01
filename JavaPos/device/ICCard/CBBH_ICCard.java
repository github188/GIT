package device.ICCard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulateByte;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.Interface.Interface_ICCard;
import com.efuture.javaPos.Global.ConfigClass;

/**
 * 重百百货（银联设备读取轨道信息）
 * @author wangy
 *
 */
public class CBBH_ICCard implements Interface_ICCard
{	
	public boolean close()
	{
		// TODO 自动生成方法存根
		return true;
	}

	/**
	 * 读取卡号或轨道
	 */
	public String findCard()
	{

		BufferedReader br = null;
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
            
            
			String request = "71011    280105  0700000000000000000000000000000000000000754";//请求文件内容
						
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
				CommonMethod.waitForExec(ConfigClass.BankPath + "\\javaposbank.exe CQXDS");
			}
			else
			{
				new MessageBox("找不到" + ConfigClass.BankPath + "\\javaposbank.exe");
				return null;
			}
			
			if ((br = CommonMethod.readFileGB2312(ConfigClass.BankPath + "\\result.txt")) == null)
			{
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return null;
			}
			String result = "111";
			result = br.readLine();
			
			if(result != null && result.length() > 0)result = result.split(",")[1];else return "";
			
			if(result.substring(0,2).equals("00"))
			{
/*				String track = ManipulateByte.getString(ManipulateByte.subBytes(result.getBytes(),122,300));//全部磁道信息
				String track1= ManipulateByte.getString(ManipulateByte.subBytes(track.getBytes(),0,120));//磁道1
				String track2=ManipulateByte.getString(ManipulateByte.subBytes(track.getBytes(),120,37));//磁道2
				String track3=ManipulateByte.getString(ManipulateByte.subBytes(track.getBytes(),157,104));//磁道3
*/		
				String track = ManipulateByte.getString(ManipulateByte.getStringBytes(result,122,423));//全部磁道信息
				String track1= ManipulateByte.getString(ManipulateByte.getStringBytes(track,0,121));//磁道1
				String track2=ManipulateByte.getString(ManipulateByte.getStringBytes(track,120,158));//磁道2
				String track3=ManipulateByte.getString(ManipulateByte.getStringBytes(track,157,262));//磁道3
				result = track1.trim()+";"+track2.trim()+";"+track3.trim()+";";
			}
			
			return result;//解析卡号（待加），注意储值卡、普通会员卡、联名会员卡，看银联是否能正确返回
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			if(br != null)
			{
				try {
					br.close();
				} catch (IOException e) {
					// TODO 自动生成 catch 块
					e.printStackTrace();
				}
			}
		}
	}

	public String getDiscription()
	{
		return "重百百货银联设备读卡器";
	}

	public Vector getPara()
	{
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
