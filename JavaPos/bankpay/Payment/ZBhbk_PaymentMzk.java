package bankpay.Payment;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Payment.PaymentMzkForm;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class ZBhbk_PaymentMzk extends PaymentMzk
{
	Socket s = null;
	OutputStream ps = null;
	InputStream br = null;
	boolean connect = false;
	public String ip = null;
	public int port = 0;

	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		if ((track1 == null || track1.trim().length() <= 0) && (track2 == null || track2.trim().length() <= 0)
				&& (track3 == null || track3.trim().length() <= 0))
		{
			new MessageBox("磁道数据为空!");
			return false;
		}

		// 解析磁道
		String[] s = parseTrack(track1, track2, track3);
		if (s == null) return false;
		track1 = s[0];
		track2 = s[1];
		track3 = s[2];

		// 设置请求数据
		setRequestDataByFind(track1, track2, track3);

		// 设置用户输入密码
		StringBuffer passwd = new StringBuffer();
		if (!getPasswdBeforeFindMzk(passwd))
		{
			return false;
		}
		else
		{
			mzkreq.passwd = passwd.toString();
		}

		//
		return getMzkInfo();
	}

	public boolean getMzkInfo()
	{
		return sentInfo(mzkreq, mzkret);

	}

	/**
	 * 数据包头  
	 * 校验号    4+1
	 * 收银机号  8+1
	 * 命令代码  4+1   0060
	 * 优先级    2+1   00
	 * 记录个数  4+1   0001
	 * 
	 */
	/**
	 * 数据包体长度  
	 * 长度     4   0220
	 * 
	 */

	/**
	 * 数据包体  
	 * 流水号    6+1
	 * 交易类型  2+1
	 * 终端号    8+1   
	 * 收银员号  8+1   
	 * 小票号    7+1   
	 * 交易金额  12+1
	 * 二磁道    37+1
	 * 三磁道    104+1
	 * 密码      6+1
	 * 保留      20+1
	 */
	//交易类型,’01’-消费,’02’-消费冲正,’03’-退货,’04’-退货冲正,’05’-余额查询
	public boolean sentInfo(MzkRequestDef mzkreq, MzkResultDef mzkret)
	{
		//交换数据,避免消费时卡号记录为磁道号
		if (mzkreq.type.equals("05"))
		{
			String temp = mzkreq.track2;
			mzkreq.track2 = mzkreq.memo;
			mzkreq.memo = temp;
		}
		if (!mzkreq.type.equals("01") && !mzkreq.type.equals("05")) { return false; }

		String random = Convert.increaseChar(ManipulatePrecision.getRandom(), '\0', 5);
		String head = random + Convert.increaseChar(mzkreq.syjh, '\0', 9) + Convert.increaseChar("0060", '\0', 5)
				+ Convert.increaseChar("00", '\0', 3) + Convert.increaseChar("0001", '\0', 5);
		String len = "0220";
		
		String je = String.valueOf(ManipulatePrecision.doubleConvert(mzkreq.je * 100, 2, 1));
		if (je.indexOf(".") > 0) je = je.substring(0, je.indexOf("."));
		String body = Convert.increaseChar(String.valueOf(mzkreq.seqno), '\0', 7) + Convert.increaseChar(mzkreq.type, '\0', 3)
				+ Convert.increaseChar(GlobalInfo.sysPara.mktcode + mzkreq.syjh, '\0', 9) + Convert.increaseChar(mzkreq.syyh, '\0', 9)
				+ Convert.increaseChar(String.valueOf(mzkreq.fphm), '\0', 8) + Convert.increaseChar(je, '\0', 13)
				+ Convert.increaseChar(mzkreq.memo, '\0', 38) + Convert.increaseChar(mzkreq.track3, '\0', 105)
				+ Convert.increaseChar(mzkreq.passwd, '\0', 7) + Convert.increaseChar("", '\0', 21);
		System.out.println("length " + Convert.countLength(body));
		String retline = sendMessage(head + len + body, null);
		//String retline = "774  9999     0001 00 0001 014000 卡余额是10.000元                         200738372            000000001000 2013.05.23 10:50:55                                           ";
		//String retline ="803  9999     0001 00 0001 014000                                          200738373            000000001200 2013.05.24 09:48:58   ";
		//String retline = "024  8888     0001 00 0001 014000 此卡为本地卡，适用门店为0011             200738370            000000000976 2013.05.24 17:02:52                                           ";
		if (retline == null || retline.trim().equals("")) return false;
		String result = retline.replace('\0', ' ');
		PosLog.getLog(getClass()).info("result:" + result);
		if (result != null && !result.trim().equals(""))
		{
			if (getMzkResult(result, random, mzkreq.memo)) return true;

		}
		return false;

	}

	public boolean getstatus()
	{
		try
		{
			s = new Socket(ip, port);
			s.setSoTimeout(10000);
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
			PosLog.getLog(getClass()).error(e);
			return false;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			PosLog.getLog(getClass()).error(e);
			return false;
		}

		return true;
	}

	public String sendMessage(String line, String memo)
	{
		if (!getSocketInfo()) return null;

		try
		{
			PosLog.getLog(getClass()).info("socket send");
			if (!s.isConnected() || s.isClosed())
			{
				PosLog.getLog(getClass()).info("socket closed");
				if (getstatus() != true)
				{
					new MessageBox("SOCKET重新连接失败。请检查网络或服务器是否正常");
					return null;
				}
			}
			PosLog.getLog(getClass()).info("request:" + new String(line));
			//System.out.println("socket send1");
			ps = new DataOutputStream(s.getOutputStream());
			br = new DataInputStream(s.getInputStream());

			//PosLog.getLog(getClass()).info("------------发送-------------------\n" + line);
			ps.write(line.getBytes());
			ps.flush();
			byte[] leng = new byte[171];
			int len = 0;

			while (len == 0)
			{
				len = br.read(leng, len, 171 - len);
				//System.out.println("String :" + new String(leng));
				//PosLog.getLog(getClass()).info("request:" + new String(leng));
				if (len >= 170) break;
			}

			//System.out.println("String :" + new String(leng));
			return new String(leng);

		}
		catch (Exception er)
		{
			PosLog.getLog(getClass()).error(er);
			er.printStackTrace();
		}
		finally
		{
			try
			{
				if (ps != null) ps.close();
				if (br != null) br.close();
				if (s != null) s.close();
			}
			catch (IOException e)
			{
				PosLog.getLog(getClass()).error(e);
				e.printStackTrace();
			}

		}
		return null;

	}

	private boolean getSocketInfo()
	{
		BufferedReader br = null;

		// 读取socket.ini文件
		br = CommonMethod.readFile(GlobalVar.ConfigPath + "\\SOCKET.ini");
		if (br == null)
		{
			new MessageBox("没有找到SOCKET.ini文件");
			return false;
		}
		String sinfo = null;
		try
		{
			while ((sinfo = br.readLine()) != null)
			{
				if (sinfo.indexOf(":") >= 0)
				{
					ip = sinfo.substring(0, sinfo.indexOf(":"));
					port = Convert.toInt(sinfo.substring(sinfo.indexOf(":") + 1));
					if (!getstatus())
					{
						new MessageBox(sinfo + " 无法连接");
						return false;
					}
				}
			}
		}
		catch (IOException e)
		{
			PosLog.getLog("SocketService").error(e);
			e.printStackTrace();
			return false;
		}
		finally
		{
			try
			{
				if (br != null) br.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return true;

	}

	public int isChinese(String strName)
	{

		int num = 0;
		char[] ch = strName.toCharArray();

		for (int i = 0; i < ch.length; i++)
		{

			char c = ch[i];

			if (isChinese(c))
			{

				num = num + 1;

			}

		}

		return num;

	}

	private static boolean isChinese(char c)
	{

		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS

		|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS

		|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A

		|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B

		|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION

		|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS

		|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {

		return true;

		}

		return false;

	}

	//读取返回信息
	private boolean getMzkResult(String result, String random, String string)
	{
		//查询有多少个中文字符
		int count = isChinese(result);

		if (result.length() > 5)
		{
			if (!random.trim().equals(result.substring(0, 5).trim())) return false;
		}
		if ((result.length() > 19) && (!result.substring(14, 19).trim().equals("0001")))
		{
			new MessageBox("失败返回代码：" + result.substring(14, 19).trim() + result.substring(34, 75 - count).trim());
			return false;
		}
		if (result.length() > 34 && (!result.substring(31, 34).trim().equals("00")))
		{
			new MessageBox("失败返回代码：" + result.substring(31, 34).trim() + result.substring(34, 75 - count).trim());
			return false;
		}

		if (result.length() > 31) if (!result.substring(31, 34).trim().equals("00"))
		{
			if (result.substring(31, 34).trim().equals("21"))
			{
				new MessageBox("当前流水号比服务上的小");
			}
			else if (result.length() > 75)
			{
				new MessageBox(result.substring(34, 75).trim());
			}
			return false;
		}
		String a = result.substring(75 - count);

		mzkret.memo = string.trim();
		mzkret.cardno = a.substring(0, 21).trim();

		if (a.length() > 34) mzkret.ye = ManipulatePrecision.doubleConvert(Double.parseDouble(a.substring(21, 34).trim()) / 100, 2, 1);

		if (a.length() > 75) mzkret.cardname = a.substring(54, 75).trim();

		return true;
	}

	public boolean findMzk(String track1, String track2, String track3)
	{
		if ((track1 == null || track1.trim().length() <= 0) && (track2 == null || track2.trim().length() <= 0)
				&& (track3 == null || track3.trim().length() <= 0))
		{
			new MessageBox("磁道数据为空!");
			return false;
		}

		// 解析磁道
		String[] s = parseTrack(track1, track2, track3);
		if (s == null) return false;
		track1 = s[0];
		track2 = s[1];
		track3 = s[2];

		// 设置请求数据
		setRequestDataByFind(track1, track2, track3);

		// 设置用户输入密码
		StringBuffer passwd = new StringBuffer();
		if (!getPasswdBeforeFindMzk(passwd))
		{
			return false;
		}
		else
		{
			mzkreq.passwd = passwd.toString();
		}

		//
		return sendMzkSale(mzkreq, mzkret);
	}

	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{
		return sentInfo(req, ret);

	}

	public boolean sendAccountCz()
	{
		return true;
	}

	public boolean writeMzkCz()
	{
		return true;
	}

	public BankLogDef mzkAccountLog(boolean success, BankLogDef bld, MzkRequestDef req, MzkResultDef ret)
	{
		try
		{
			GlobalInfo.sysPara.usemzklog = 'Y';
			if (GlobalInfo.sysPara.usemzklog != 'Y') return null;

			if (!success)
			{
				// 记录开始交易日志
				BankLogDef newbld = new BankLogDef();
				Object obj = GlobalInfo.dayDB.selectOneData("select max(rowcode) from BANKLOG");
				if (obj == null) newbld.rowcode = 1;
				else newbld.rowcode = Integer.parseInt(String.valueOf(obj)) + 1;
				newbld.rqsj = ManipulateDateTime.getCurrentDateTime();
				newbld.syjh = req.syjh;
				newbld.fphm = req.fphm;
				newbld.syyh = req.syyh;
				newbld.type = req.type;
				newbld.je = req.je;
				if (req.type.equals("01")) newbld.typename = "消费";
				else if (req.type.equals("02")) newbld.typename = "消费冲正";
				else if (req.type.equals("03")) newbld.typename = "退货";
				else if (req.type.equals("04")) newbld.typename = "退货冲正";
				else if (req.type.equals("05")) newbld.typename = "查询";
				else newbld.typename = "未知";
				newbld.classname = this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1);
				newbld.trace = req.seqno;
				newbld.oldrq = req.mktcode + "|" + req.invdjlb;
				newbld.bankinfo = req.paycode;
				newbld.cardno = req.track2;
				newbld.memo = req.memo;
				newbld.oldtrace = 0;
				newbld.kye = ret.ye;
				newbld.crc = "";
				newbld.retcode = "";
				newbld.retmsg = "";
				newbld.retbz = 'N';
				newbld.net_bz = 'N';
				newbld.allotje = 0;
				newbld.memo1 = "Y";

				if (!AccessDayDB.getDefault().writeBankLog(newbld))
				{
					new MessageBox("记录储值卡交易日志失败!");
					return null;
				}

				return newbld;
			}
			else
			{
				if (bld == null) return null;

				// 更新交易应答数据
				if (ret != null && !CommonMethod.isNull(ret.cardno)) bld.cardno = ret.cardno;
				bld.retcode = "00";

				if (bld.retmsg != null && !bld.retmsg.trim().equals(""))
				{
					bld.retmsg = "交易成功|" + bld.retmsg;
				}
				else
				{
					bld.retmsg = "交易成功";
				}
				bld.kye = ret.ye;
				bld.retbz = 'Y';
				bld.net_bz = 'N';
				if (NetService.getDefault().sendBankLog(bld)) bld.net_bz = 'Y';
				AccessDayDB.getDefault().updateBankLog(bld);
				return bld;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public boolean cancelPay()
	{

		// 即时记账模式,取消已记账的付款

		if (mzkAccount(false))
		{
			deleteMzkCz();

			return true;
		}
		else
		{
			return false;
		}

	}

	public boolean collectAccountPay()
	{
		// 已记账,直接返回
		return true;
	}

	public boolean isRealAccountPay()
	{
		return true;
	}

	public boolean realAccountPay()
	{
		// 付款即时记账			
		if (mzkAccount(true))
		{
			deleteMzkCz();

			return true;
		}
		else
		{
			return false;
		}
	}
	
	public SalePayDef inputPay(String money)
	{
		try
		{
			// 退货小票不能使用,退货扣回按销售算
			if (checkMzkIsBackMoney())
			{
				new MessageBox("退货时不能使用" + paymode.name);
				return null;
			}
			
			// 先检查是否有冲正未发送
			if (!sendAccountCz()) return null;
			
			// 打开明细输入窗口
			new PaymentMzkForm().open(this,saleBS);
			
			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
        }
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return null;
	}
}
