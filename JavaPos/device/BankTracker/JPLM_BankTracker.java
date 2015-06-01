package device.BankTracker;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_BankTracker;
import com.efuture.javaPos.Global.Language;

public class JPLM_BankTracker implements Interface_BankTracker
{
	String requestFile = "c:\\javapos\\request.txt";
	String resultFile = "c:\\javapos\\result.txt";
	String cmdLine = "";

	public boolean open()
	{
		return true;
	}

	public boolean close()
	{

		if (PathFile.fileExist(requestFile))
		{
			PathFile.deletePath(requestFile);

			if (PathFile.fileExist(requestFile))
				//new MessageBox("读卡请求文件trackerReq.txt无法删除!");
				new MessageBox(Language.apply("读卡请求文件trackerReq.txt无法删除!"));
		}

		if (PathFile.fileExist(resultFile))
		{
			PathFile.deletePath(resultFile);

			if (PathFile.fileExist(resultFile))
				//new MessageBox("读卡结果文件trackerRet.txt无法删除!");
				new MessageBox(Language.apply("读卡结果文件trackerRet.txt无法删除!"));
		}

		return true;
	}

	public String getTracker()
	{
		String[] tmpTrack = null;
		BufferedReader br = null;
		String track1 = "", track2 = "", track3 = "";

		String[] arg = DeviceName.deviceBankTracker.split("\\|");

		try
		{
			if (arg.length > 1)
				cmdLine = arg[1].trim();

			// 写入请求, 但不调用
			PrintWriter pw = CommonMethod.writeFile(requestFile);
			pw.write(cmdLine.toString());
			pw.close();

			// 调用接口模块
			if (PathFile.fileExist("c:\\JavaPOS\\javaposbank.exe"))
			{
				CommonMethod.waitForExec("c:\\JavaPOS\\javaposbank.exe JPLM");
			}
			else
			{
				//new MessageBox("找不到读卡模块 javaposbank.exe");
				//return "未发现读卡模块";
				return Language.apply("未发现读卡模块");
			}

			if (!PathFile.fileExist(resultFile) || ((br = CommonMethod.readFileGBK(resultFile)) == null))
			{
				//return "刷卡失败!";
				return Language.apply("刷卡失败!");
			}

			String track = br.readLine();

			if (track == null)
				//return "刷卡失败";
				return Language.apply("刷卡失败!");
		
			tmpTrack = track.split(",");
			
			if (tmpTrack!=null && tmpTrack.length>0)
			{
				if (!tmpTrack[0].equals("00"))
				{
					if (tmpTrack.length>4)
						return tmpTrack[4];
					else
						//return "刷卡失败";
						return Language.apply("刷卡失败!");
				}
				
				if (tmpTrack.length > 1)
				{
					track1 = tmpTrack[1].trim();
				}
				if (tmpTrack.length > 2)
				{
					track2 = tmpTrack[2].trim();
				}
				if (tmpTrack.length > 3)
				{
					track3 = tmpTrack[3].trim();
				}
				return track1 + ";" + track2 + ";" + track3;
			}
			//return "无法解析轨道信息";
			return Language.apply("无法解析轨道信息");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			//return "刷卡异常";
			return Language.apply("刷卡异常");
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
		}

	}

	public Vector getPara()
	{
		Vector v = new Vector();
		//v.add(new String[] { "读卡命令", "AA" });
		v.add(new String[] { Language.apply("读卡命令"), "AA" });
		return v;
	}

	public String getDiscription()
	{
		//return "九江派拉蒙银联刷卡设备";
		return Language.apply("九江派拉蒙银联刷卡设备");
	}
}
