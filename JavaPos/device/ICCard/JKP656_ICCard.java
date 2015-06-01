package device.ICCard;


import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Vector;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_ICCard;

public class JKP656_ICCard implements Interface_ICCard
{
	String requestFile = "c:\\javapos\\request.txt";

	String resultFile = "c:\\javapos\\result.txt";

	String cmdLine = "";

	public String findCard()
	{
		String[] tmpTrack = null;
		BufferedReader br = null;
		String track1 = "", track2 = "", track3 = "";

		String[] arg = DeviceName.deviceICCard.split(",");

		try
		{
			close();

			if (arg.length > 0)
				cmdLine = arg[0].trim();
			else
				cmdLine = "1";

			if (arg.length > 1)
				cmdLine += "," + arg[1].trim();
			else
				cmdLine += ",9600";

			if (arg.length > 2)
				cmdLine += "," + arg[2].trim();
			else
				cmdLine += ",8";

			if (arg.length > 3)
				cmdLine += "," + arg[3].trim();
			else
				cmdLine += ",1";

			if (arg.length > 4)
				cmdLine += "," + arg[4].trim();
			else
				cmdLine += ",3";

			// 写入请求, 但不调用
			PrintWriter pw = CommonMethod.writeFile(requestFile);
			pw.write(cmdLine.toString());
			pw.close();

			// 调用接口模块
			if (PathFile.fileExist("c:\\JavaPOS\\javaposbank.exe"))
			{
				CommonMethod.waitForExec("c:\\JavaPOS\\javaposbank.exe JKPMSR");
			}
			else
			{
				new MessageBox("找不到读卡模块 javaposbank.exe");
				return "";
			}

			if (!PathFile.fileExist(resultFile) || ((br = CommonMethod.readFileGBK(resultFile)) == null))
			{
				new MessageBox("刷卡失败!");
				return "";
			}

			String track = br.readLine();

			if (track == null)
			{
				new MessageBox("刷卡失败!");
				return "";
			}

			tmpTrack = track.split(",");

			if (tmpTrack.length == 1)
			{
				if (tmpTrack[0].equals("2"))
					new MessageBox("串口打开失败!");
				if (tmpTrack[0].equals("0"))
					new MessageBox("读卡失败!");
				else if (tmpTrack[0].equals("-2"))
					new MessageBox("操作操时!");

				return "";
			}
			// 1,,23323,323
			if (tmpTrack.length > 1)
			{
				track1 = tmpTrack[1];
			}
			if (tmpTrack.length > 2)
			{
				track2 = tmpTrack[2];
			}
			if (tmpTrack.length > 3)
			{
				track3 = tmpTrack[3];
			}
			return track1 + ";" + track2 + ";" + track3;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "";
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
				new MessageBox("读卡请求文件request.txt无法删除,请重试");
		}

		if (PathFile.fileExist(resultFile))
		{
			PathFile.deletePath(resultFile);

			if (PathFile.fileExist(resultFile))
				new MessageBox("读卡结果文件result.txt无法删除,请重试");
		}

		return true;
	}

	public String getDiscription()
	{
		return "京开普IC卡设备";
	}

	public Vector getPara()
	{
		Vector v = new Vector();
		v.add(new String[] { "端口号", "0", "1", "2", "3" });
		v.add(new String[] { "波特率", "9600", "110", "300", "600", "1200", "2400", "4800", "19200" });
		v.add(new String[] { "数据位" });
		v.add(new String[] { "校验位" });
		v.add(new String[] { "读卡模式" });
		return v;
	}

	public String updateCardMoney(String cardno, String operator, double ye)
	{
		return "error:该设备不支持本功能";
	}
}
