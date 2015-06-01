package bankpay.Payment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Payment.PaymentMzkForm;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class PlmWxc_PaymentMzk extends PaymentMzk
{
	WxcRequestDef req = new WxcRequestDef();
	WxcResponseDef res = new WxcResponseDef();

	public PlmWxc_PaymentMzk()
	{
		super();
	}

	public PlmWxc_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		super(mode, sale);
	}

	public PlmWxc_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		super(pay, head);
	}

	public int getAccountInputMode()
	{
		return TextBox.IntegerInput;
	}

	public boolean findMzk(String track1, String track2, String track3)
	{
		setRequestDataByFind(track1, track2, track3);
		return sendMzkSale(mzkreq, mzkret);
	}

	protected boolean needFindAccount()
	{
		return true;
	}

	public String getDefaultCardno()
	{
		return "按回车键进行交易";
	}

	public void setRequestDataByFind(String track1, String track2, String track3)
	{
		super.setRequestDataByFind(track1, track2, track3);

		if (mzkreq.type.equals("05"))
			req.type = "9";

		req.amount = 0;
		req.fphm = String.valueOf(mzkreq.fphm);
		req.syjh = mzkreq.syjh;
		req.syyh = mzkreq.syyh;
		req.payseq = "";
	}

	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		if (findMzk(track1, track2, track3))
			return true;

		return false;
	}

	public boolean cancelPay()
	{
		if (mzkAccount(false))
			return true;

		return false;
	}

	public SalePayDef inputPay(String money)
	{
		try
		{
			// 打开明细输入窗口
			new PaymentMzkForm().open(this, saleBS);

			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;
	}

	public boolean sendAccountCzData(MzkRequestDef req, String czfile, String czname)
	{
		return true;
	}

	// 保存交易数据进行交易
	protected boolean setRequestDataByAccount()
	{
		if (super.setRequestDataByAccount())
		{
			req.amount = mzkreq.je;
			req.fphm = String.valueOf(mzkreq.fphm);
			req.syjh = mzkreq.syjh;
			req.syyh = mzkreq.syyh;

			// 赋原消费流水
			req.payseq = salepay.idno;

			if (mzkreq.type.equals("01"))
			{
				req.type = "0";
			}

			if (mzkreq.type.equals("03"))
			{
				req.type = "1";
			}

			return true;
		}

		return false;
	}

	public boolean sendMzkSale(MzkRequestDef mzkreq, MzkResultDef mzkret)
	{
		if (!callJavaPosBank(req, res))
			return false;

		if (res.retcode != 0)
		{
			new MessageBox(res.retmsg);
			return false;
		}

		mzkret.cardno = res.cardno;
		mzkret.ye = res.amount;
		mzkret.cardname = res.vipid;

		return true;

	}

	public boolean collectAccountPay()
	{
		return true;
	}

	protected void saveAccountMzkResultToSalePay()
	{
		// batch标记本付款方式已记账,这很重要
		salepay.batch = String.valueOf(mzkreq.seqno);

		// 标记记账返回的卡号
		if (!CommonMethod.isNull(mzkret.cardno))
			salepay.payno = mzkret.cardno;

		// 消费流水
		if (!CommonMethod.isNull(res.saleseq))
			salepay.idno = res.saleseq;

		salepay.kye = mzkret.ye;

		// 更新付款断点数据，标记为已付款状态,否则在记账以后如果掉电,断点读入的还是未记账状态
		if (this.saleBS != null)
			this.saleBS.writeBrokenData();
	}

	public boolean realAccountPay()
	{
		// 会员付款即时记账
		if (mzkAccount(true))
			return true;

		return false;
	}

	public boolean mzkAccount(boolean isAccount)
	{
		// 设置交易类型,isAccount=true是记账,false是撤销
		if (isAccount)
		{
			if (SellType.SELLSIGN(salehead.djlb) > 0)
			{
				mzkreq.type = "01"; // 消费,减

			}
			else
			{
				mzkreq.type = "03"; // 退货,加
			}
		}
		else
		{
			if (SellType.SELLSIGN(salehead.djlb) > 0)
			{
				mzkreq.type = "03"; // 退货,加

			}
			else
			{
				mzkreq.type = "01"; // 消费,减

			}
		}

		// 保存交易数据进行交易
		if (!setRequestDataByAccount())
			return false;

		// 记录面值卡交易日志
		BankLogDef bld = mzkAccountLog(false, null, mzkreq, mzkret);

		// 发送交易请求
		if (!sendMzkSale(mzkreq, mzkret))
			return false;

		saveAccountMzkResultToSalePay();

		// 记账完成操作,可用于记录记账日志或其他操作
		return mzkAccountFinish(isAccount, bld);
	}


	public boolean callJavaPosBank(WxcRequestDef req, WxcResponseDef res)
	{
		BufferedReader br = null;

		try
		{
			// 先删除上次交易数据文件
			if (PathFile.fileExist("c:\\JavaPOS\\request.txt"))
			{
				PathFile.deletePath("c:\\JavaPOS\\request.txt");

				if (PathFile.fileExist("c:\\JavaPOS\\request.txt"))
				{
					new MessageBox("面值卡交易请求文件request.txt无法删除,请重试");
					return false;
				}
			}

			if (PathFile.fileExist("c:\\JavaPOS\\result.txt"))
			{
				PathFile.deletePath("c:\\JavaPOS\\result.txt");

				if (PathFile.fileExist("c:\\JavaPOS\\result.txt"))
				{
					new MessageBox("面值卡交易请求文件result.txt无法删除,请重试");
					return false;
				}
			}

			PrintWriter pw = null;
			try
			{
				pw = CommonMethod.writeFile("c:\\JavaPOS\\request.txt");
				if (pw != null)
				{
					pw.println(req.toString());
					pw.flush();
				}
			}
			finally
			{
				if (pw != null)
				{
					pw.close();
				}
			}

			// 调用接口模块
			if (PathFile.fileExist("c:\\JavaPOS\\javaposbank.exe"))

			{
				CommonMethod.waitForExec("c:\\JavaPOS\\javaposbank.exe FX3000");
			}
			else
			{
				new MessageBox("找不到模块 javaposbank.exe");
				return false;
			}

			if (!PathFile.fileExist("c:\\JavaPOS\\result.txt") || ((br = CommonMethod.readFileGBK("c:\\JavaPOS\\result.txt")) == null))
			{
				new MessageBox("读取面值卡应答数据失败!", null, false);
				return false;
			}

			String line = br.readLine();

			if (line == null || line.length() <= 0)
			{
				new MessageBox("未读取到面值卡应答数据!");
				return false;
			}

			return res.parse(line);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			try
			{
				if (br != null)
					br.close();

				br = null;
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			if (!ConfigClass.DebugMode)
			{
				if (PathFile.fileExist("c:\\JavaPOS\\request.txt"))
				{
					PathFile.deletePath("c:\\JavaPOS\\request.txt");

					if (PathFile.fileExist("c:\\JavaPOS\\request.txt"))
					{
						new MessageBox("面值卡交易请求文件request.txt无法删除,请重试");
						return false;
					}
				}

				if (PathFile.fileExist("c:\\JavaPOS\\result.txt"))
				{
					PathFile.deletePath("c:\\JavaPOS\\result.txt");

					if (PathFile.fileExist("c:\\JavaPOS\\result.txt"))
					{
						new MessageBox("面值卡交易请求文件result.txt无法删除,请重试");
						return false;
					}
				}
			}
		}
	}

	class WxcRequestDef
	{
		public String type;
		public double amount;
		public String syjh;
		public String fphm;
		public String syyh;
		public String payseq;

		public String toString()
		{
			String jestr = ManipulateStr.PadLeft(String.valueOf((long) ManipulatePrecision.doubleConvert(amount * 100, 2, 1)), 12, '0');
			syjh = ManipulateStr.PadRight(syjh, 10, ' ');
			fphm = ManipulateStr.PadRight(fphm, 20, ' ');
			syyh = ManipulateStr.PadRight(syyh, 10, ' ');
			payseq = ManipulateStr.PadRight(payseq, 30, ' ');

			return type + jestr + syjh + fphm + syyh + payseq;
		}
	}

	class WxcResponseDef
	{
		public int retcode;

		public double amount;
		public String cardno;
		public String vipid;
		public String saleseq;
		public String retmsg;

		private void init()
		{
			amount = 0;
			cardno = "";
			vipid = "";
			saleseq = "";
			retmsg = "";
		}

		public boolean parse(String info)
		{
			init();

			try
			{
				String[] ary = info.split(",");
				if (ary == null)
				{
					retcode = -1;
					retmsg = "未知错误";
				}

				if (ary.length > 0 && ary[0] != null)
					retcode = Convert.toInt(ary[0]);

				if (retcode != 0)
				{
					if (ary.length > 1 && ary[1] != null)
						retmsg = ary[1].replaceAll(" ", "");
					else
						retmsg = "未知错误";

					return true;
				}

				if (ary.length > 1 && ary[1] != null)
				{
					if (ary[1].length() < 82)
					{
						retcode = -1;
						retmsg = "接口调用成功,但数据返回长度不合法";
						return true;
					}

					amount = ManipulatePrecision.doubleConvert(Convert.toDouble(ary[1].substring(0, 12)) / 100, 2, 1);
					cardno = ary[1].substring(12, 32).trim();
					vipid = ary[1].substring(32, 52).trim();
					saleseq = ary[1].substring(52, 82).trim();
					retmsg = ary[1].substring(82).trim();

					return true;
				}

				return false;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();

				retcode = -1;
				retmsg = "解析异常";
				return false;
			}
		}
	}
}
