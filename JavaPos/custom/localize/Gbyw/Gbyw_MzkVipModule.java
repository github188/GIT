package custom.localize.Gbyw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;

public class Gbyw_MzkVipModule
{
	private static Gbyw_MzkVipModule module = new Gbyw_MzkVipModule();

	private String serverIp;
	private int serverPort;

	public static Gbyw_MzkVipModule getDefault()
	{
		return module;
	}

	public boolean initConnection()
	{
		if (GlobalInfo.sysPara.cyCrmUrl == null || GlobalInfo.sysPara.cyCrmUrl.trim().equals("") || GlobalInfo.sysPara.cyCrmUrl.trim().indexOf(":") == -1)
		{
			new MessageBox("请设置正确的面值卡会员卡服务器连接地址");
			return false;
		}

		String[] urlAry = GlobalInfo.sysPara.cyCrmUrl.split(":");
		if (urlAry != null && urlAry.length > 0)
			serverIp = urlAry[0];
		if (urlAry != null && urlAry.length > 1)
			serverPort = Convert.toInt(urlAry[1]);

		return true;
	}

	public String sendData(String data)
	{
		Socket socket = null;
		String recvData = "";
		char[] buf = new char[200];
		PrintWriter printOut = null;
		BufferedReader reader = null;
		try
		{
			socket = new Socket(this.serverIp, this.serverPort);
			socket.setSoTimeout(ConfigClass.ConnectTimeout);
			System.out.println("send:" + data);
			
			printOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
			printOut.print(data);
			printOut.flush();

			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "GB2312"));
			reader.read(buf);

			
			//返回值中第个字符之间均带有空字符，所以，要将之去掉
			for (int i = 0; i < buf.length; i++)
			{
				if (buf[i] == 0x0)
					buf[i] = ' ';
			}

			recvData = new String(buf).replace(" ", "");
			System.out.println("recv:"+recvData);
			return recvData;
		}
		catch (Exception ex)
		{
			new MessageBox(ex.getMessage());
			ex.printStackTrace();
			return null;
		}
		finally
		{
			try
			{
				if (socket != null)
					socket.close();
				socket = null;

				if (printOut != null)
					printOut.close();

				printOut = null;

				if (reader != null)
					reader.close();

				reader = null;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				socket = null;
			}
		}
	}

	public String getError(String code)
	{
		if (code.equals("0"))
			return "成功";
		else if (code.equals("2"))
			return "交易失败，联系发卡商";
		else if (code.equals("3"))
			return "商户未登记";
		else if (code.equals("14"))
			return "无效卡号";
		else if (code.equals("31"))
			return "未开通的卡";
		else if (code.equals("33"))
			return "卡已停用";
		else if (code.equals("41"))
			return "挂失的卡";
		else if (code.equals("51"))
			return "余额不足";
		else if (code.equals("54"))
			return "过期卡";
		else if (code.equals("55"))
			return "密码错误";
		else if (code.equals("66"))
			return "无此账户";
		else if (code.equals("99"))
			return "校验错，请请重新签到";
		else if (code.equals("101"))
			return "此卡已撤销过一次";
		else if (code.equals("102"))
			return "流水号重复或该流水号已经撤销过一次";
		else if (code.equals("103"))
			return "流水号无效";
		else if (code.equals("104"))
			return "撤销交易的金额（积分）与原纪录的不符";
		else if (code.equals("105"))
			return "该卡不是储值卡";
		else if (code.equals("106"))
			return "该卡不是会员卡";
		else if (code.equals("107"))
			return "积分不足";
		else if (code.equals("108"))
			return "零钱包不足";
		else if (code.equals("140"))
			return "pos机号有误";
		else if (code.equals("141"))
			return "交易金额、积分、零钱包格式有误";
		else if (code.equals("142"))
			return "商户号有误";
		else if (code.equals("143"))
			return "流水号有误";
		else if (code.equals("144"))
			return "旧流水号有误";
		else if (code.equals("145"))
			return "卡号有误";
		else if (code.equals("146"))
			return "暗码有误";
		else if (code.equals("147"))
			return "交易金额不允许为负";
		else if (code.equals("201"))
			return "交易或撤销成功";
		else
			return " 未知错误";
	}
}
