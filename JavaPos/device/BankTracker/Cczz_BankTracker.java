package device.BankTracker;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_BankTracker;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;

//长春卓展使用银行设备读取会员卡
public class Cczz_BankTracker implements Interface_BankTracker
{
//	String requestFile = "c:\\javapos\\request.txt";
//	String resultFile = "c:\\javapos\\result.txt";
//	String cmdLine = "";

	public boolean open()
	{
		return true;
	}

	public boolean close()
	{

//		if (PathFile.fileExist(requestFile))
//		{
//			PathFile.deletePath(requestFile);
//
//			if (PathFile.fileExist(requestFile))
//				//new MessageBox("读卡请求文件trackerReq.txt无法删除!");
//				new MessageBox(Language.apply("读卡请求文件trackerReq.txt无法删除!"));
//		}
//
//		if (PathFile.fileExist(resultFile))
//		{
//			PathFile.deletePath(resultFile);
//
//			if (PathFile.fileExist(resultFile))
//				//new MessageBox("读卡结果文件trackerRet.txt无法删除!");
//				new MessageBox(Language.apply("读卡结果文件trackerRet.txt无法删除!"));
//		}

		return true;
	}

	public String getTracker()
	{
		BufferedReader br = null;
		String track1 = "", track2 = "", track3 = "";
		
//		DeviceName.deviceBankTracker = "C:\\GMC";
    	String path = GlobalVar.HomeBase; // 默认主目录
        try
        {    	
	        String[] arg = DeviceName.deviceBankTracker.split(",");
	        	
        	if (arg.length > 0 && !"".equals(arg[0].trim())) path = arg[0].trim();
        	
			//	先删除上次交易数据文件
			if (PathFile.fileExist(path + "\\request.txt"))
			{
				PathFile.deletePath(path + "\\request.txt");
			   
				if (PathFile.fileExist(path + "\\request.txt"))
				{
					new MessageBox("读卡请求文件request.txt无法删除,请重试");
					return null;   	
				}
			}
			if (PathFile.fileExist(path + "\\result.txt"))
			{
				PathFile.deletePath(path + "\\result.txt");
			   
				if (PathFile.fileExist(path + "\\result.txt"))
				{
					new MessageBox("读卡结果文件result.txt无法删除,请重试");
					return null;   	
				}
			}
			

//				char transType[2+1]; //交易指令 Q1签到 Q2结算 Q3重打印 S1消费
//				char transAmount[12+1]; //交易金额
//				char loyalty[12+1]; //积分
//				char MisTrace[6+1]; //MIS流水号
//				char InstallmentTimes[2+1];	//分期期数 03,06,09,12,18,24,36
//				char oldAuthNo[6+1]; //原交易授权号
//				char oldPostrace[6+1];  //原交易流水号（撤销交易需要）
//				char oldHostTrace[12 + 1];  //原交易系统检索号（退货交易需要）
//				char oldTransDate[8 + 1];	 //原交易日期（退货交易需要）
//				char cashPcNum[20+1];	// 后补空格 收银台号（打印小票需要）
//				char cashierNum[20+1];	//	后补空格 收银员工号（打印小票需要）
			
        	String rq = "28," + Convert.increaseChar("",'0', 12)  + "," + Convert.increaseChar("",'0', 12) + "," + Convert.increaseChar("",'0', 6) + "," + Convert.increaseChar("  ",' ', 2)  + "," + Convert.increaseChar("",' ', 6) + "," + Convert.increaseChar("",' ', 6) + "," + Convert.increaseChar("",' ', 12) + "," + Convert.increaseChar("",' ', 8) + "," + Convert.increaseChar(GlobalInfo.syjDef.syjh,' ', 20)  + "," + Convert.increaseChar(GlobalInfo.posLogin.gh,' ', 20);
//	        String rq = "28," + Convert.increaseChar("",'0', 12)  + "," + Convert.increaseChar("",'0', 12) + "," + Convert.increaseChar("",'0', 6) + "," + Convert.increaseChar("  ",' ', 2)  + "," + Convert.increaseChar("",' ', 6) + "," + Convert.increaseChar("",' ', 6) + "," + Convert.increaseChar("",' ', 12) + "," + Convert.increaseChar("",' ', 8) + "," + Convert.increaseChar("777",' ', 20)  + "," + Convert.increaseChar("0333",' ', 20);
        	
        	// 写入请求
			PrintWriter pw = CommonMethod.writeFile(path + "\\request.txt");
			pw.write(rq);
			pw.close();
			
            // 调用接口模块
            if (PathFile.fileExist(path + "\\javaposbank.exe"))
            {
            	CommonMethod.waitForExec(path + "\\javaposbank.exe KeeperClient3");
            }
            else
            {
                new MessageBox("找不到IC卡模块 javaposbank.exe");
                return null;
            }

            // 读取应答
            if (!PathFile.fileExist(path + "\\result.txt") || ((br = CommonMethod.readFileGBK(path + "\\result.txt")) == null))
            {
                new MessageBox("读取卡号应答数据失败!");
                return null;
            }
            String line = br.readLine();
            
            if (line != null && (line.split(",").length >= 3) && "00".equals(line.split(",")[0].trim()) )
            {
            	track2 = line.split(",")[3].trim();           	
            }
            else
            {
            	new MessageBox("读取会员卡信息失败!!!");
            	return null;
            }

            
            return track1 + ";" + track2 + ";" + track3;
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	new MessageBox("读取会员卡信息异常:" + ex.getMessage());
        	return null;
        }
        finally
        {
			try
			{
				br.close();
			}
			catch (Exception ex)
			{
				br = null;
			}
			close();
			//	先删除上次交易数据文件
//				if (PathFile.fileExist(path + "\\request.txt"))
//				{
//					PathFile.deletePath(path + "\\request.txt");
//				}
//				if (PathFile.fileExist(path + "\\result.txt"))
//				{
//					PathFile.deletePath(path + "\\result.txt");
//				}
        }

	}

	public Vector getPara()
	{
		Vector v = new Vector();
		v.add(new String[] { Language.apply("路径:") });
		return v;
	}

	public String getDiscription()
	{
		return Language.apply("长春卓展银联刷卡设备");
	}
}
