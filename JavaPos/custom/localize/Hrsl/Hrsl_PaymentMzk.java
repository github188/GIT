package custom.localize.Hrsl;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Hrsl_PaymentMzk extends PaymentMzk
{
	public Hrsl_PaymentMzk()
	{
	}

	public Hrsl_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Hrsl_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public boolean findMzkInfo(String track1, String track2, String track3)
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

		return Hrsl_ServiceCrmModule.getDefault().queryMzkInfo(mzkreq, mzkret);
	}

	public boolean findMzk(String track1, String track2, String track3)
	{
		return findMzkInfo(track1, track2, track3);
	}

	protected boolean getPasswdBeforeFindMzk(StringBuffer passwd)
	{
		if (GlobalInfo.sysPara.cardpasswd.equals("Y"))
		{
			TextBox txt = new TextBox();

			if (!txt.open("请输入卡密码", "PASSWORD", "请输入卡密码,没有密码请直接按回车键!", passwd, 0, 0, false, TextBox.AllInput))
				return false;
		}
		
		return true;
	}

	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{
		return Hrsl_ServiceCrmModule.getDefault().mzkSale(req, ret);
	}

	protected boolean setRequestDataByAccount()
	{
		if (super.setRequestDataByAccount())
		{
			// 交易日期
			mzkreq.str2 = ManipulateDateTime.getCurrentDateBySign();
			return true;
		}
		return false;
	}

	public boolean mzkAccount(boolean isAccount)
	{
		BankLogDef bld = null;
		do
		{
			// 退货交易卡号为空时提示刷卡
			paynoMsrflag = false;
			if (!paynoMSR())
				return false;

			// 设置交易类型,isAccount=true是记账,false是撤销
			if (isAccount)
			{
				// 销售
				if (SellType.ISSALE((salehead.djlb)))
					mzkreq.type = "01"; // 消费,减
				else
					mzkreq.type = "03"; // 退货，加
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

				// 先获取交易ID，方便写冲正文件
				if (Hrsl_ServiceCrmModule.getDefault().mzkPrepareSale(mzkreq) == null)
					return false;

				// 将交易ID写到冲正文件
				if (!writeMzkCz())
				{
					if (paynoMsrflag)
					{
						salepay.payno = "";
						continue;
					}
					// 写冲正文失败，则取消当笔交易
					Hrsl_ServiceCrmModule.getDefault().mzkCancelSale(mzkreq);

					return false;
				}

				// 记录面值卡交易日志
				bld = mzkAccountLog(false, null, mzkreq, mzkret);

				// 发送交易请求
				if (!Hrsl_ServiceCrmModule.getDefault().mzkConfirmSale(mzkreq))
				{
					if (paynoMsrflag)
					{
						salepay.payno = "";
						continue;
					}
					Hrsl_ServiceCrmModule.getDefault().mzkCancelSale(mzkreq);
					return false;
				}

				// 记账过程没有返回最终余额，以查询的余额做基准加减计算新余额
				if (mzkreq.type == "01")
					salepay.kye -= mzkreq.je;
				else
					salepay.kye += mzkreq.je;

				// new MessageBox("salepay.kye="+salepay.kye);
				// 更新付款断点数据，标记为已付款状态,否则在记账以后如果掉电,断点读入的还是未记账状态

			}
			else
			{
				if (SellType.ISSALE((salehead.djlb)))
					mzkreq.type = "01"; // 消费,加
				else
					mzkreq.type = "03"; // 退货，减

				// 保存交易数据进行交易
				if (!setRequestDataByAccount())
					return false;

				if (!writeMzkCz())
					return false;

				bld = mzkAccountLog(false, null, mzkreq, mzkret);

				if (!Hrsl_ServiceCrmModule.getDefault().mzkCancelSale(mzkreq))
					return false;

				if (mzkreq.type == "01")
					salepay.kye += mzkreq.je;
				else
					salepay.kye -= mzkreq.je;
			}

			saveAccountMzkResultToSalePay();
			// 记账完成操作,可用于记录记账日志或其他操作
			return mzkAccountFinish(isAccount, bld);
		} while (true);
	}

	protected void saveAccountMzkResultToSalePay()
	{
		// batch标记本付款方式已记账,这很重要
		salepay.batch = String.valueOf(mzkreq.seqno);

		// 标记记账返回的卡号
		if (!CommonMethod.isNull(mzkret.cardno))
			salepay.payno = mzkret.cardno;

		salepay.kye = ManipulatePrecision.doubleConvert(salepay.kye);

		if (this.saleBS != null)
			this.saleBS.writeBrokenData();
	}

	public BankLogDef mzkAccountLog(boolean success, BankLogDef bld, MzkRequestDef req, MzkResultDef ret)
	{
		try
		{
			if (GlobalInfo.sysPara.usemzklog != 'Y')
				return null;

			if (!success)
			{
				// 记录开始交易日志
				BankLogDef newbld = new BankLogDef();
				Object obj = GlobalInfo.dayDB.selectOneData("select max(rowcode) from BANKLOG");
				if (obj == null)
					newbld.rowcode = 1;
				else
					newbld.rowcode = Integer.parseInt(String.valueOf(obj)) + 1;
				newbld.rqsj = ManipulateDateTime.getCurrentDateTime();
				newbld.syjh = req.syjh;
				newbld.fphm = req.fphm;
				newbld.syyh = req.syyh;
				newbld.type = req.type;
				newbld.je = req.je;
				if (req.type.equals("01"))
					newbld.typename = "消费";
				else if (req.type.equals("02"))
					newbld.typename = "消费冲正";
				else if (req.type.equals("03"))
					newbld.typename = "退货";
				else if (req.type.equals("04"))
					newbld.typename = "退货冲正";
				else if (req.type.equals("05"))
					newbld.typename = "查询";
				else
					newbld.typename = "未知";
				newbld.classname = this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1);
				newbld.trace = req.seqno;
				newbld.oldrq = req.mktcode + "|" + req.invdjlb;
				newbld.bankinfo = req.paycode;
				newbld.cardno = req.track2;
				newbld.memo = req.memo;
				newbld.oldtrace = 0;

				newbld.crc = mzkreq.str3; // 记录会员储值卡消费ID
				newbld.retcode = "";
				newbld.retmsg = "";
				newbld.retbz = 'N';
				newbld.net_bz = 'N';
				newbld.allotje = 0;

				if (!AccessDayDB.getDefault().writeBankLog(newbld))
				{
					new MessageBox("记录储值卡交易日志失败!");
					return null;
				}

				return newbld;
			}
			else
			{
				if (bld == null)
					return null;

				// 更新交易应答数据
				if (ret != null && !CommonMethod.isNull(ret.cardno))
					bld.cardno = ret.cardno;
				bld.retcode = "00";

				if (bld.retmsg != null && !bld.retmsg.trim().equals(""))
				{
					bld.retmsg = "交易成功|" + bld.retmsg;
				}
				else
				{
					bld.retmsg = "交易成功";
				}

				bld.retbz = 'Y';
				bld.net_bz = 'N';
				if (NetService.getDefault().sendBankLog(bld))
					bld.net_bz = 'Y';
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

}
