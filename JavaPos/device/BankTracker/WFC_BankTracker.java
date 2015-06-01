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
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

//重庆环球，调用动态库（模块名：CQXDS；动态库(dll文件）：posinf.dll；函数：int umsbankproc(char * REQ, char * RSP)；）
public class WFC_BankTracker implements Interface_BankTracker
{
	String requestFile = "c:\\gmc\\request.txt";
	String resultFile = "c:\\gmc\\result.txt";
	//String cmdLine = "";

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
				//new MessageBox("读卡请求文件request.txt无法删除!");
				new MessageBox(Language.apply("读卡请求文件request.txt无法删除!"));
		}

		if (PathFile.fileExist(resultFile))
		{
			PathFile.deletePath(resultFile);

			if (PathFile.fileExist(resultFile))
				//new MessageBox("读卡结果文件result.txt无法删除!");
				new MessageBox(Language.apply("读卡结果文件result.txt无法删除!"));
		}

		return true;
	}

	public String getTracker()
	{
		String[] tmpTrack = null;
		BufferedReader br = null;
		String track1 = "", track2 = "", track3 = "";
		String line = "";
		String code = "";
		String trans = "";

		String[] arg = DeviceName.deviceBankTracker.split("\\|");

		try
		{
//			if (arg.length > 1)
//				cmdLine = arg[1].trim();
			
			
			String[] title = {Language.apply("代码"), Language.apply("会员卡类型")};
			int[] width = {60,300};
			Vector v =  new Vector();
			v.add(new String[]{"1", Language.apply("会员卡")});
			v.add(new String[]{"2", Language.apply("联名卡")});
			
			int choice = new MutiSelectForm().open(Language.apply("请选择卡类型"), title, width, v,true);
			
			if (choice == -1)
			{
				return Language.apply("请选择卡类型！！");
			}
			
			if (choice > v.size())
			{
				return Language.apply("请重新选择");
			}

			String s = ((String[])v.elementAt(choice))[0];
			
			if (s.equals("1"))
			{
				code = "7";
				trans = "10";
			}
			else
			{
				code = "1";
				trans =  "70";
			}
			//请求文件信息
			String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 8); //收银员号
			String syjh = Convert.increaseChar(GlobalInfo.syjDef.syjh, ' ', 8); //收银机号
			line = code + syjh + syyh + trans + "000000000000" + "00000000" + "000000000000" + "000000" + 222;
			// 写入请求, 但不调用
			PrintWriter pw = CommonMethod.writeFile(requestFile);
			pw.write(line);
			pw.close();

			// 调用接口模块
			if (PathFile.fileExist("c:\\gmc\\javaposbank.exe"))
			{
				CommonMethod.waitForExec("c:\\gmc\\javaposbank.exe CQXDS");
			}
			else
			{
				//new MessageBox("找不到读卡模块 javaposbank.exe");
				return Language.apply("未发现读卡模块");
			}

			if (!PathFile.fileExist(resultFile) || ((br = CommonMethod.readFileGBK(resultFile)) == null))
			{
				return Language.apply("刷卡失败!");
			}

			String track = br.readLine();

			if (track == null)
				return Language.apply("刷卡失败!");
		
			tmpTrack = track.split(",");
			
			if (tmpTrack.length < 1)
				return Language.apply("刷卡返回数据异常");
			
			if (tmpTrack!=null && tmpTrack.length>0)
			{
				line = tmpTrack[1];
				int len = line.length();
				if (!line.substring(0,2).equals("00"))
				{
						return line.substring(len - 343, len - 303).trim();
				}
			
				track2 = line.substring(len - 303, len - 3).trim().replace('E', '=');
				
				return track1 + ";" + track2 + ";" + track3;
			}
			return Language.apply("无法解析轨道信息");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
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
		v.add(new String[] { Language.apply("会员卡"), "7,10" });
		v.add(new String[] { Language.apply("联名卡"), "1,70" });
		return v;
	}

	public String getDiscription()
	{
		return Language.apply("重庆环球银联刷卡设备");
	}
}
