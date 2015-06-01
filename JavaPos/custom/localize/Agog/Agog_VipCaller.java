package custom.localize.Agog;

import java.io.IOException;
import java.net.Socket;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Agog.Agog_VipDef.QueryVipRet;

public class Agog_VipCaller
{
	// 总得说就是会员消费就要调用2002,2003接口，什么时候调用我不管。
	public static Agog_VipCaller caller = new Agog_VipCaller();
	private String serverIp = "";
	private int serverPort = 0;
	private Agog_VipDef vip = new Agog_VipDef();
	private boolean isCheckTrade = false;
	private boolean isCert = false;
	
	public static Agog_VipCaller getDefault()
	{
		return caller;
	}

	public boolean isCert()
	{
		return this.isCert;
	}

	public void setCert(boolean flag)
	{
		this.isCert = flag;
	}

	public boolean isCheckTrade()
	{
		return isCheckTrade;
	}

	public void setCheckTrade(boolean flag)
	{
		this.isCheckTrade = flag;
	}

	public void initVipConn()
	{
		if (serverIp.length() > 0 && serverIp.indexOf(".") > 0)
			return;

		if (GlobalInfo.sysPara.cyCrmUrl == null || GlobalInfo.sysPara.cyCrmUrl.trim().equals("") || GlobalInfo.sysPara.cyCrmUrl.trim().indexOf(":") == -1)
		{
			new MessageBox("请设置正确的会员服务器连接地址");
			return;
		}

		String[] urlAry = GlobalInfo.sysPara.cyCrmUrl.split(":");
		if (urlAry != null && urlAry.length > 0)
			serverIp = urlAry[0];
		if (urlAry != null && urlAry.length > 1)
			serverPort = Convert.toInt(urlAry[1]);
	}

	public String getCheckCode(String track2)
	{
		if (!isEnable())
			return null;

		try
		{
			String req = vip.getCheckCodeRequestJson(track2);

			if (req == null || req.trim().equals(""))
			{
				new MessageBox("查询请求数据非法");
				return null;
			}

			String ret = sendData("1003", req);

			if (ret == null || ret.trim().equals(""))
			{
				new MessageBox("查询返回数据非法");
				return null;
			}

			return vip.parseCheckCodeJson(ret);

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	private boolean isEnable()
	{
		return GlobalInfo.sysPara.isEnableLHCard == 'Y' ? true : false;
	}

	public boolean queryVip(CustomerDef cust, String track2,boolean flag)
	{
		try
		{
			if (!isEnable())
				return false;

			String reqQuery = vip.getQueryRequestJson(track2, "");

			if (reqQuery == null || reqQuery.trim().equals(""))
			{
				new MessageBox("查询请求数据非法");
				return false;
			}

			String retQuery = sendData("1001", reqQuery);

			if (retQuery == null || retQuery.trim().equals(""))
			{
				new MessageBox("查询返回数据非法");
				return false;
			}

			QueryVipRet vipRet = vip.parseQueryJson(retQuery);

			if (vipRet == null)
			{
				new MessageBox("未解析出会员信息");
				return false;
			}

			isCert = false;
			isCheckTrade = false;
		
			
			if (flag && vipRet.IsHandInput.equals("0"))
			{
				TextBox txt = new TextBox();
				StringBuffer passwd = new StringBuffer();
				if (!txt.open("请输入密码", "PASSWORD", "请输入会员卡密码", passwd, 0, 0, false, TextBox.AllInput))
					return false;

				if (!passwd.toString().equals(vipRet.Password))
				{
					new MessageBox("密码错误");
					return false;
				}

				isCert = true;
			}

			cust.zkl = 1;
			cust.ishy = 'Y';
			cust.value5 = 1;
			cust.code = vipRet.CardNum;
			cust.name = vipRet.MemberName;
			cust.type = vipRet.GradeCode;
			cust.maxdate = vipRet.CardUseLimitDate;
			cust.valuememo = Convert.toDouble(vipRet.CardIntegral);
			cust.num1 = Convert.toDouble(vipRet.CashBalance);
			cust.status = vipRet.StatusName + "(" + vipRet.StatusCode + ")";
			cust.ispay = true;
			cust.valstr1 = vipRet.Password;

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean queryVipFromRoomCode(CustomerDef cust, String roomCode)
	{
		try
		{
			if (!isEnable())
				return false;

			isCheckTrade = false;

			String reqXml = vip.getRoomVipRequestJson(roomCode);

			if (reqXml == null || reqXml.trim().equals(""))
			{
				new MessageBox("请求数据非法");
				return false;
			}

			String retXml = sendData("1004", reqXml);

			if (retXml == null || retXml.trim().equals(""))
			{
				new MessageBox("返回数据非法");
				return false;
			}

			QueryVipRet vipRet = vip.parseRoomVipJson(retXml);
			
			isCert = false;
			isCheckTrade = false;
			
			if (vipRet == null)
			{
				new MessageBox("未解析出会员信息");
				return false;
			}

			isCheckTrade = vipRet.IsHandInput.equals("0") ? true : false;
			
			cust.zkl = 1;
			cust.ishy = 'Y';
			cust.value5 = 1;
			cust.code = vipRet.CardNum;
			cust.name = vipRet.MemberName;
			cust.type = vipRet.GradeCode;
			cust.maxdate = vipRet.CardUseLimitDate;
			cust.valuememo = Convert.toDouble(vipRet.CardIntegral);
			cust.num1 = Convert.toDouble(vipRet.CashBalance);
			cust.status = vipRet.StatusName + "(" + vipRet.StatusCode + ")";
			cust.ispay = true;
			cust.valstr1 = vipRet.Password;

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean sendVipSale(MzkRequestDef req, String checkcode)
	{
		if (!isEnable())
			return true;

		String reqXml = vip.getSubmitBillRequestJson(req, checkcode);

		if (reqXml == null || reqXml.trim().equals(""))
		{
			new MessageBox("请求数据非法");
			return false;
		}

		String retXml = sendData("1002", reqXml);
		if (retXml == null || retXml.trim().equals(""))
		{
			new MessageBox("返回数据非法");
			return false;
		}

		return vip.parseSubmitBillJson(retXml);
	}

	public boolean sendVipScore(SaleHeadDef req)
	{
		if (!isEnable())
			return true;

		String reqXml = vip.getSubmitScoreRequestJson(req);

		if (reqXml == null || reqXml.trim().equals(""))
		{
			new MessageBox("请求数据非法");
			return false;
		}

		String retXml = sendData("1005", reqXml);
		if (retXml == null || retXml.trim().equals(""))
		{
			new MessageBox("返回数据非法");
			return false;
		}

		return vip.parseSubmitBillJson(retXml);
	}

	public static byte[] intToByte(int len)
	{
		byte[] result = new byte[4];

		result[0] = (byte) ((len & 0xFF000000) >> 24);
		result[1] = (byte) ((len >> 16) & 0xFF);
		result[2] = (byte) ((len >> 8) & 0xFF);
		result[3] = (byte) (len & 0xFF);

		return result;
	}

	// 该函数用于与服务器进行通讯
	private String sendData(String cmdcode, String xmlData)
	{
		Socket socket = null;
		byte[] readBuffer = new byte[5000];

		try
		{
			initVipConn();

			socket = new Socket(serverIp, serverPort);
			socket.setSoTimeout(ConfigClass.ReceiveTimeout);

			socket.getOutputStream().write(cmdcode.getBytes());
			socket.getOutputStream().write(xmlData.getBytes());
			socket.getOutputStream().write("#".getBytes());
			socket.getOutputStream().flush();

			socket.getInputStream().read(readBuffer);

			String recv = new String(readBuffer, "GB2312").trim();
			System.out.println(recv);
			recv = recv.substring(4);
			System.out.println(recv);

			return recv;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(ex.getMessage());
			return null;
		}
		finally
		{
			try
			{
				if (socket != null)
					socket.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				socket = null;
			}
		}
	}
}
