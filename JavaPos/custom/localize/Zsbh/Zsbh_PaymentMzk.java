package custom.localize.Zsbh;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Payment.PaymentMzkForm;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Zsbh_PaymentMzk extends PaymentMzk
{

	public Zsbh_PaymentMzk()
	{
		super();
	}

	public Zsbh_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		super(mode, sale);
	}

	public Zsbh_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		super(pay, head);
	}

	public SalePayDef inputPay(String money)
	{
		try
		{
			// 退货小票不能使用,退货扣回按销售算
			if (checkMzkIsBackMoney() && GlobalInfo.sysPara.thmzk != 'Y')
			{
				new MessageBox("退货时不能使用" + paymode.name);
				return null;
			}

			// 先检查是否有冲正未发送
			if (!sendAccountCz())
				return null;

			// 打开明细输入窗口
			inputMoney = money;
			
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

	private void writeLog(String content)
	{
		FileWriter writer = null;
		try
		{
			writer = new FileWriter(ConfigClass.LocalDBPath + "\\Invoice\\" + new ManipulateDateTime().getDateByEmpty() + "\\MZK" + new ManipulateDateTime().getDateByEmpty() + ".log", true);
			writer.write("[" + ManipulateDateTime.getCurrentTime() + "] " + content + "\n");
			writer.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				if (writer != null)
				{
					writer.close();
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

		}
	}

	public boolean mzkAccount(boolean isAccount)
	{
		do
		{
			writeLog("mzkAccount() start");
			// 退货交易卡号为空时提示刷卡
			paynoMsrflag = false;
			if (!paynoMSR())
				return false;

			// 设置交易类型,isAccount=true是记账,false是撤销
			if (isAccount)
			{
				if (SellType.SELLSIGN(salehead.djlb) > 0)
					mzkreq.type = "01"; // 消费,减
				else
					mzkreq.type = "03"; // 退货,加
			}
			else
			{
				if (SellType.SELLSIGN(salehead.djlb) > 0)
					mzkreq.type = "03"; // 退货,加
				else
					mzkreq.type = "01"; // 消费,减
			}

			writeLog("setRequestDataByAccount() start");
			// 保存交易数据进行交易
			if (!setRequestDataByAccount())
			{
				if (paynoMsrflag)
				{
					salepay.payno = "";
					continue;
				}
				return false;
			}

			writeLog("writeMzkCz() start");
			if (mzkreq.track2 == null || mzkreq.track2.trim().length() <= 0)
			{
				writeLog("writeMzkCz()时 mzkreq.track2为空");
				return false;
			}

			writeLog("writeMzkCz() start2");
			// 先写冲正文件
			if (!writeMzkCz())
			{
				if (paynoMsrflag)
				{
					salepay.payno = "";
					continue;
				}
				return false;
			}

			writeLog("sendMzkSale() start");
			// 发送交易请求
			if (!sendMzkSale(mzkreq, mzkret))
			{
				if (paynoMsrflag)
				{
					salepay.payno = "";
					continue;
				}
				return false;
			}

			writeLog("saveAccountMzkResultToSalePay() start");
			// 记录应答信息, batch标记本付款方式已记账,这很重要
			saveAccountMzkResultToSalePay();

			writeLog("mzkAccountFinish() start");
			// 记账完成操作,可用于记录记账日志或其他操作
			return mzkAccountFinish(isAccount);
		} while (true);
	}

	public boolean writeMzkCz()
	{
		FileOutputStream f = null;

		try
		{
			String name = GetMzkCzFile();

			f = new FileOutputStream(name);
			ObjectOutputStream s = new ObjectOutputStream(f);
			s.writeObject(mzkreq);
			s.flush();
			s.close();
			f.close();
			s = null;
			f = null;

			return true;
		}
		catch (Exception e)
		{
			writeLog("writeMzkCz() ex=" + e.toString());
			e.printStackTrace();

			return false;
		}
		finally
		{
			try
			{
				if (f != null)
					f.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public boolean checkMzkMoneyValid()
	{
		StringBuffer buffer = new StringBuffer();
		if (new TextBox().open("请输入" + GlobalInfo.sysPara.mzkChkLength + "位校验码", "校验码", "提示:请输入交易检验码", buffer, 0, 0, false))
		{
			
			String chkCode = this.mzkreq.track2;
			
			if(chkCode.indexOf("=")>0)
				chkCode = chkCode.split("=")[0];
			
			chkCode = chkCode.substring(chkCode.length() - GlobalInfo.sysPara.mzkChkLength);
			if (!buffer.toString().trim().substring(0,GlobalInfo.sysPara.mzkChkLength).equals(chkCode))
			{
				new MessageBox("校验码输入有误");
				return false;
			}
			return super.checkMzkMoneyValid();
		}
		else
		{
			new MessageBox("请输入校验码");
			return false;
		}
	}

	protected boolean setRequestDataByAccount()
	{
		// 得到消费序号
		long seqno = getMzkSeqno();
		if (seqno <= 0)
			return false;
		if (mzkreq == null || mzkreq.track2 == null || mzkreq.track2.trim().length() <= 0)
		{
			writeLog("setRequestDataByAccount() 失败:mzkreq.track2为空");
			return false;
		}

		// 打消费交易包
		mzkreq.seqno = seqno;
		mzkreq.je = salepay.ybje;
		mzkreq.syjh = ConfigClass.CashRegisterCode;
		mzkreq.mktcode = GlobalInfo.sysPara.mktcode;
		mzkreq.syyh = GlobalInfo.posLogin.gh;
		mzkreq.paycode = salepay.paycode;
		mzkreq.invdjlb = ((salehead != null) ? salehead.djlb : "");

		// 告诉后台过程磁道信息是存放的是卡号,只采用卡号记账方式,不使用磁道记账方式
		// mzkreq.track1 = "CARDNO";
		// mzkreq.track2 = salepay.payno;

		return true;
	}

	public boolean findMzk(String track1, String track2, String track3)
	{
		if ((track1 == null || track1.trim().length() <= 0) && (track2 == null || track2.trim().length() <= 0) && (track3 == null || track3.trim().length() <= 0))
		{
			new MessageBox("磁道数据为空!");
			return false;
		}

		// 解析磁道
		String[] s = parseTrack(track1, track2, track3);
		if (s == null)
			return false;
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

		mzkreq.memo = Convert.newSubString(track2, track2.length() - 6, track2.length());

		//
		boolean blnret = false;
		blnret = sendMzkSale(mzkreq, mzkret);
		/*
		 * if (mzkreq.track2.length() > 20) { //mzkret.ye = mzkret.ye/100; }
		 */

/*		if (blnret)
		{
			StringBuffer sb = new StringBuffer();
			sb.append("卡      号:" + mzkret.cardno + "\n");
			sb.append("余      额:" + mzkret.ye+"\n");
			sb.append("应付金额:" + curSaleJe);
			new MessageBox( sb.toString());
		}*/
			

		return blnret;
	}

	public boolean paynoMSR()
	{
		if (SellType.ISBACK(salehead.djlb))
		{
			if (GlobalInfo.sysPara.backRefundMSR.charAt(0) == 'Y' && (salepay.payno == null || salepay.payno.length() <= 0))
			{
				while (true)
				{
					StringBuffer cardno = new StringBuffer();

					TextBox txt = new TextBox();
					boolean kh = false;
					if (salepay.je < 0)
						kh = true;

					if (!txt.open((info == true) ? "上张卡无效，重新刷卡" : "请刷卡" + ((kh == true) ? "(扣回)" : ""), "卡号", "【" + ((salepay != null) ? salepay.payname : "") + "】" + "请从刷卡槽刷入", cardno, 0, 0, false, TextBox.MsrKeyInput)) { return false; }

					String[] track = parseTrack(txt.Track1, txt.Track2, txt.Track3);

					MzkRequestDef mzkreq1 = new MzkRequestDef();
					mzkreq1.type = "05"; // 查询类型
					mzkreq1.seqno = 0;
					mzkreq1.termno = ConfigClass.CustomItem2.toString().trim();// ConfigClass.CashRegisterCode;
					mzkreq1.mktcode = GlobalInfo.sysPara.mktcode;
					mzkreq1.syyh = GlobalInfo.posLogin.gh;
					mzkreq1.syjh = ConfigClass.CashRegisterCode;
					mzkreq1.fphm = GlobalInfo.syjStatus.fphm;

					if (kh)
						mzkreq1.invdjlb = SellType.RETAIL_SALE;
					else
						mzkreq1.invdjlb = ((salehead != null) ? salehead.djlb : "");

					mzkreq1.paycode = ((paymode != null) ? paymode.code : "");
					mzkreq1.je = 0;
					mzkreq1.track1 = track[0];
					mzkreq1.track2 = track[1];
					mzkreq1.track3 = track[2];
					mzkreq1.passwd = "";
					mzkreq1.memo = "";

					MzkResultDef mzkret1 = new MzkResultDef();
					info = true;

					if (!sendMzkSale(mzkreq1, mzkret1))
					{
						new MessageBox("此卡号未找到 或 此卡为不可用状态");
						continue;
					}

					salepay.payno = mzkret1.cardno;
					salepay.kye = 0;

					paynoMsrflag = true;
					break;
				}
			}
		}

		return true;
	}

	public void setRequestDataByFind(String track1, String track2, String track3)
	{
		// 根据磁道生成查询请求包
		mzkreq.type = "05"; // 查询类型
		mzkreq.seqno = 0;
		mzkreq.termno = ConfigClass.CustomItem2.toString().trim();// ConfigClass.CashRegisterCode;
		mzkreq.mktcode = GlobalInfo.sysPara.mktcode;
		mzkreq.syyh = GlobalInfo.posLogin.gh;
		mzkreq.syjh = ConfigClass.CashRegisterCode;
		mzkreq.fphm = GlobalInfo.syjStatus.fphm;
		mzkreq.invdjlb = ((salehead != null) ? salehead.djlb : "");
		mzkreq.paycode = ((paymode != null) ? paymode.code : "");
		mzkreq.je = 0;
		mzkreq.track1 = track1;
		mzkreq.track2 = track2;
		mzkreq.track3 = track3;
		mzkreq.passwd = "";
		mzkreq.memo = "";
	}

	public void setRequestDataBySalePay()
	{
		// 根据salepay生成交易请求包
		mzkreq.type = "XX"; // 未确定交易类型
		mzkreq.seqno = 0;
		mzkreq.termno = ConfigClass.CustomItem2.toString().trim();// ConfigClass.CashRegisterCode;
		mzkreq.mktcode = GlobalInfo.sysPara.mktcode;
		mzkreq.syyh = GlobalInfo.posLogin.gh;
		mzkreq.syjh = salehead.syjh;
		mzkreq.fphm = salehead.fphm;
		mzkreq.invdjlb = salehead.djlb;
		mzkreq.paycode = salepay.paycode;
		mzkreq.je = salepay.ybje;
		// mzkreq.track1 = "CARDNO"; // 告诉后台过程磁道信息是存放的是卡号
		// mzkreq.track2 = salepay.payno;
		mzkreq.track3 = "";
		mzkreq.passwd = "";
		mzkreq.memo = "";
	}

}
