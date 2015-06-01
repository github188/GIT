package custom.localize.Hzjb;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Payment.PaymentMzkEvent;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Hzjb_JniICPaymentMzk extends PaymentMzk
{
	public Hzjb_JniICPaymentMzk()
	{
		super();
	}

	public Hzjb_JniICPaymentMzk(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Hzjb_JniICPaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	protected boolean getPasswdBeforeFindMzk(StringBuffer passwd)
	{
		TextBox txt = new TextBox();

		if (GlobalInfo.sysPara.cardpasswd.equals("Y"))
		{
			if (!txt.open("请输入面值卡密码", "PASSWORD", "请输入卡密码后按确认键!", passwd, 0, 0, false, TextBox.AllInput))
				return false;
		}

		return true;
	}

	public void specialDeal(PaymentMzkEvent event)
	{
		event.accountTxt.setText("请将IC卡插入读卡设备并回车");
	}

	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		ProgressBox pb = null;
		try
		{
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

			pb = new ProgressBox();
			pb.setText("正在查询卡余额,请稍等...");

			return icCheck();
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
		}
	}

	public boolean mzkAccount(boolean isAccount)
	{
		ProgressBox pb = null;
		try
		{
			do
			{
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

				// 记录面值卡交易日志
				BankLogDef bld = mzkAccountLog(false, null, mzkreq, mzkret);

				pb = new ProgressBox();
				pb.setText("正在进行扣款处理,请稍等...");

				// 发送交易请求
				if (!icPay(mzkreq, mzkret))
				{
					if (paynoMsrflag)
					{
						salepay.payno = "";
						continue;
					}
					
					mzkAccountLog(true, bld, mzkreq, mzkret);
					return false;
				}

				// 记录应答信息, batch标记本付款方式已记账,这很重要
				saveAccountMzkResultToSalePay();

				// 记账完成操作,可用于记录记账日志或其他操作
				return mzkAccountFinish(isAccount, bld);
			} while (true);
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
		}
	}

	// IC芯片扣款，必须得实时记帐
	public boolean realAccountPay()
	{
		// 付款即时记账
		if (mzkAccount(true))
		{
			// 将整单件数传入打印
			mzkreq.num3 = salehead.hjzsl;
			Hzjb_ICCardCaller.getDefault().printBill(mzkreq, mzkret);
			return true;
		}

		return false;
	}

	public boolean collectAccountPay()
	{
		// 已记账,直接返回
		return true;
	}

	public boolean findMzk(String track1, String track2, String track3)
	{
		ProgressBox pb = null;
		try
		{
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
			pb = new ProgressBox();
			pb.setText("正在查询卡余额,请稍等...");

			return icCheck();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
		}
	}

	public boolean cancelPay()
	{
		new MessageBox("不允许删除该付款项\n请完成交易后走退货流程");
		return false;

		/*
		 * mzkreq.num3 = salehead.hjzsl;
		 * Hzjb_LhIcExecutor.getDefault().printBill(mzkreq, mzkret); return
		 * true;
		 */
	}

	public BankLogDef mzkAccountLog(boolean success, BankLogDef bld, MzkRequestDef req, MzkResultDef ret)
	{
		try
		{
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

				// 记录消费时的卡号
				newbld.cardno = "";
				newbld.memo = req.memo;
				newbld.oldtrace = 0;

				// 记录查询出的卡号
				newbld.crc = mzkreq.track3;
				// 记录查询出的卡余额
				newbld.allotje = mzkret.ye;

				newbld.retcode = "";
				newbld.retmsg = "";
				newbld.retbz = 'N';
				newbld.net_bz = 'N';

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
				if (ret == null || CommonMethod.isNull(ret.cardno) || ret.cardno.trim().equals(""))
				{
					bld.retbz = 'N';

					bld.cardno = "";
					bld.retcode = "XX";
					bld.retmsg = "交易失败";
				}
				else
				{
					bld.retbz = 'Y';

					// 消费后的卡号
					bld.cardno = ret.cardno;
					// 消费后的卡余额
					bld.kye = ret.ye;

					// 对应IC返回的流水号
					bld.memo = ret.memo;

					bld.retcode = "00";
					bld.retmsg = "交易成功";
				}

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

	public boolean icCheck()
	{
		try
		{
			// 设置默认值
			int checkMode = 0;
			int scoreSrc = 0;

			if (ConfigClass.CustomItem2 != null && ConfigClass.CustomItem2.trim().length() > 0)
				checkMode = Convert.toInt(String.valueOf(ConfigClass.CustomItem2.trim().charAt(0)));

			if (ConfigClass.CustomItem2 != null && ConfigClass.CustomItem2.trim().length() > 1)
				scoreSrc = Convert.toInt(String.valueOf(ConfigClass.CustomItem2.trim().charAt(1)));

			String[] result = Hzjb_ICCardCaller.getDefault().check(checkMode, scoreSrc);

			if (result != null && result.length > 0)
			{
				//result[0] = "0";
				//mzkret.cardno = "88888";
				//mzkreq.track3 = mzkret.cardno;
				//mzkret.ye = 2;

				if (!result[0].trim().equals("0"))
				{
					new MessageBox(Hzjb_ICCardCaller.getDefault().getLastError());
					return false;
				}

				if (result.length > 1)
				{
					mzkret.cardno = result[1]; //用于查询时显示卡号
					mzkreq.track3 = result[1];
				}

				if (result.length > 2)
					mzkret.cardpwd = result[2];

				if (result.length > 3)
					mzkret.cardname = result[3];

				if (result.length > 4)
					mzkret.func = result[4];

				if (result.length > 5)
					mzkret.ye = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Convert.toDouble(result[5]), 100), 2, 1);

				if (result.length > 6)
					mzkret.num1 = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Convert.toDouble(result[6]), 100), 2, 1);

				if (result.length > 7)
					mzkret.num2 = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Convert.toDouble(result[7]), 100), 2, 1);

				if (result.length > 8)
					mzkret.str1 = result[8];

				if (result.length > 9)
					mzkret.status = result[9];

				if (result.length > 10)
					mzkret.str3 = result[10];

				if (result.length > 11)
					mzkret.num3 = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Convert.toDouble(result[11]), 100), 2, 1);

				return true;
			}
			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean icPay(MzkRequestDef req, MzkResultDef ret)
	{
		// 撤销时直接返回
		if (req.type.equals("03"))
		{
			ret.memo = "";
			ret.value2 = 0;
			ret.value3 = 0;
			return true;
		}

		try
		{
			// 清除结果字段
			mzkret.cardno = "";
			mzkret.memo = "";

			String xid = String.valueOf(mzkreq.fphm);
			String card_num = mzkreq.track3;
			String pwd = "";
			long total = ((long) ManipulatePrecision.doubleConvert(mzkreq.je * 100, 2, 1));
			int clear_cost = 0;

			if (ConfigClass.CustomItem2 != null && ConfigClass.CustomItem2.trim().length() > 2)
				clear_cost = Convert.toInt(String.valueOf(ConfigClass.CustomItem2.trim().charAt(2)));

			String[] result = Hzjb_ICCardCaller.getDefault().pay(xid, card_num, pwd, total, clear_cost);

			if (result != null && result.length > 0)
			{
				if (!result[0].trim().equals("0"))
				{
					// 消费时，如果返回失败，则将卡号置为空
					mzkret.cardno = "";
					new MessageBox(Hzjb_ICCardCaller.getDefault().getLastError());
					
					//付款时若写卡失败，则会返回-7009，此时关闭付款框，重新进行卡校验
					
					
					if(result[0].equals("-7009"))
						this.inputMoney = String.valueOf(mzkreq.je);
					
					
					return false;
				}

				if (result.length > 1)
					mzkret.memo = result[1];
				if (result.length > 2)
					mzkret.value1 = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Convert.toDouble(result[2]), 100), 2, 1);
				if (result.length > 3)
					mzkret.ye = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Convert.toDouble(result[3]), 100), 2, 1);
				if (result.length > 4)
					mzkret.value2 = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Convert.toDouble(result[4]), 100), 2, 1);
				if (result.length > 5)
					mzkret.value3 = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Convert.toDouble(result[5]), 100), 2, 1);

				//将卡号重新赋值给cardno
				mzkret.cardno = mzkreq.track3;

				return true;
			}
			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public static void main(String[] args)
	{
		Hzjb_ICCardCaller caller = new Hzjb_ICCardCaller();
		long ret = caller.start();
		ret = caller.login("8888");
		System.out.println(caller.getLastError());
		// String[] retary = caller.check(0, 0);
		String[] retpay = caller.pay("888", "2222", "", 10000, 0);
		System.out.println(caller.getLastError());
	}

}
