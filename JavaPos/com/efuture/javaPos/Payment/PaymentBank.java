package com.efuture.javaPos.Payment;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class PaymentBank extends Payment
{
	public final static int XYKXF = 0; // 消费
	public final static int XYKCX = 1; // 消费撤销
	public final static int XYKTH = 2; // 隔日退货
	public final static int XYKQD = 3; // 交易签到
	public final static int XYKJZ = 4; // 交易结账
	public final static int XYKYE = 5; // 余额查询
	public final static int XYKCD = 6; // 签购单重打
	public final static int XKQT1 = 7; // 其他功能1
	public final static int XKQT2 = 8; // 其他功能2
	public final static int XKQT3 = 9; // 其他功能3
	public final static int XKQT4 = 10; // 其他功能4
	public final static int XKQT5 = 11; // 其他功能5
	public final static int XKQT6 = 12; // 其他功能6
	public final static int XKQT7 = 13; // 其他功能7
	public final static int XKQT8 = 14; // 其他功能8
	public final static int XKQT9 = 15; // 其他功能9

	protected BankLogDef bldlog = null;
	public static boolean haveXYKDoc = false;

	public PaymentBank()
	{
	}

	public PaymentBank(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public PaymentBank(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public SalePayDef inputPay(String money)
	{
		try
		{
			// 如果允许单独进行银联消费则先检查是否有单独的银联消费交易要分配
			if (GlobalInfo.sysPara.allowbankselfsale == 'Y')
			{
				getAllotSalePay();
				if (salepay != null)
					return salepay;
			}

			// 打开金卡输入窗口
			CreatePayment.getDefault().getPaymentBankForm().open(this, PaymentBank.XYKXF);

			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;
	}

	public String getCancelPayHint()
	{
//		return "撤销原流水号(参考号) " + salepay.str1 + " 的" + salepay.payname + "消费吗？";
		return Language.apply("撤销原流水号(参考号) {0} 的{1}消费吗？" ,new Object[]{salepay.str1 ,salepay.payname});
	}

	public String getBackPayHint()
	{
//		return "退金额 " + ManipulatePrecision.doubleToString(salepay.ybje) + " 的" + salepay.payname + "消费吗？";
		return Language.apply("退金额 {0} 的{1}消费吗？" ,new Object[]{ManipulatePrecision.doubleToString(salepay.ybje) ,salepay.payname});
	}

	public boolean cancelPay()
	{
		// 撤销交易
		try
		{
			// 如果允许单独进行银联消费则还原单独银联消费交易的可分配金额
			if (GlobalInfo.sysPara.allowbankselfsale == 'Y' && bldlog != null)
			{
				bldlog.allotje = ManipulatePrecision.add(bldlog.allotje, salepay.je);
				if (!AccessDayDB.getDefault().updateBankLog(bldlog, true))
				{
					return false;
				}
				else
				{
					return true;
				}
			}

			// 交易时取消付款,如果不是及时打印签购单,不允许撤销银联交易
			if (saleBS != null && !CreatePayment.getDefault().getPaymentBankFunc(paymode.code).getOnceXYKPrintDoc())
			{
//				new MessageBox("采用交易完成后才打印签购单的模式\n\n不能撤销已付款的" + salepay.payname + "消费");
				new MessageBox(Language.apply("采用交易完成后才打印签购单的模式\n\n不能撤销已付款的{0}消费" ,new Object[]{salepay.payname}));

				return false;
			}

			String msg = getCancelPayHint();
			if (msg != null && msg.length() > 0)
			{
				if ((GlobalInfo.sysPara.cancelBankGrant == 'Y') || (GlobalInfo.sysPara.cancelBankGrant == 'A'))
				{
//					if (new MessageBox("你要" + msg + "\n\n1-就在本机撤销 / 2-其他途径撤销", null, false).verify() == GlobalVar.Key2)
					if (new MessageBox(Language.apply("你要{0}\n\n1-就在本机撤销 / 2-其他途径撤销" ,new Object[]{msg}), null, false).verify() == GlobalVar.Key2)
					{
						//需要授权
						if (GlobalInfo.sysPara.cancelBankGrant == 'A' || GlobalInfo.posLogin.operrange != 'Y')
						{
							OperUserDef staff = DataService.getDefault().personGrant(Language.apply("其他途径撤销授权"));
							if (staff.operrange != 'Y')
							{
								new MessageBox(Language.apply("此人【操作数据范围】没有授权"));
								return false;
							}
							else
							{
//								AccessDayDB.getDefault().writeWorkLog("通过其他途径撤销" + salepay.payname + ",授权工号" + staff.gh, StatusType.WORK_CANCELBANK);
								AccessDayDB.getDefault().writeWorkLog(Language.apply("通过其他途径撤销{0},授权工号",new Object[]{salepay.payname}) + staff.gh, StatusType.WORK_CANCELBANK);
							}
						}

						//无须授权
//						String line = "你已经通过其他途径撤销" + salepay.payname + "消费\n\n所以不在本机" + msg;
						String line = Language.apply("你已经通过其他途径撤销{0}消费\n\n所以不在本机" ,new Object[]{salepay.payname}) + msg;
						if (new MessageBox(line, null, true).verify() == GlobalVar.Key1)
						{
							AccessDayDB.getDefault().writeWorkLog(Language.apply("通过其他途径撤销") + salepay.payname, StatusType.WORK_CANCELBANK);
							return true;
						}
						else
						{
							return false;
						}
					}
					return CreatePayment.getDefault().getPaymentBankForm().open(this, PaymentBank.XYKCX);
				}
				else
				{
					//cancelBankGrant  这个参数主要控制是否能用其他途径测试，并不是控制不能撤销的。如果想做不撤销，可以在金卡工程里面控制。
					return CreatePayment.getDefault().getPaymentBankForm().open(this, PaymentBank.XYKCX);
				}
			}

			new MessageBox(Language.apply("系统不允许撤销该交易"));

			return false;
		}
		catch (Exception er)
		{
			new MessageBox(er.getMessage());
			return false;
		}
	}

	protected boolean cancelPayBack()
	{
		// 退货交易
		boolean ret = false;

		try
		{
			// 扣回消费
			if (salepay != null && salepay.je < 0)
			{
				ret = CreatePayment.getDefault().getPaymentBankForm().open(this, PaymentBank.XYKXF);
				return ret;
			}

			String msg = getBackPayHint();
			if (msg != null && msg.length() > 0)
			{
//				if (new MessageBox("你要" + msg + "\n\n1-就在本机撤销 / 2-其他途径撤销", null, false).verify() == GlobalVar.Key2)
				if (new MessageBox(Language.apply("你要{0}\n\n1-就在本机撤销 / 2-其他途径撤销" ,new Object[]{msg}), null, false).verify() == GlobalVar.Key2)
				{
//					String line = "你已经通过其他途径进行" + salepay.payname + "退货\n\n所以不在本机" + msg;
					String line = Language.apply("你已经通过其他途径进行{0}退货\n\n所以不在本机" ,new Object[]{salepay.payname}) + msg;
					if (new MessageBox(line, null, true).verify() == GlobalVar.Key1) { return true; }
				}
			}

			// 退货交易
			ret = CreatePayment.getDefault().getPaymentBankForm().open(this, PaymentBank.XYKTH);
			return ret;
		}
		catch (Exception er)
		{
			new MessageBox(er.getMessage());
			return false;
		}
	}

	public boolean collectAccountPay()
	{
		// 如果不是即时记账,则集中记账
		if (salepay.batch == null || salepay.batch.trim().length() <= 0)
		{
			// 银联集中付款记账,只在红冲及后台退货时,这时记账应为撤销交易
			if (SellType.ISHC(salehead.djlb))
			{
				return cancelPay();
			}
			else
			{
				if (SellType.ISBACK(salehead.djlb))
				{
					return cancelPayBack();
				}
				else
				{
					return false;
				}
			}
		}
		else
		{
			// 已记账,直接返回
			return true;
		}
	}

	public boolean collectAccountClear()
	{
		return true;
	}

	/*
	 * public void saveAccount(boolean ret,PaymentBankFunc pbf) { if (ret ==
	 * true) { salepay.payno = pbf.getBankLog().cardno; salepay.batch =
	 * String.valueOf(pbf.getBankLog().trace);
	 * 
	 * // 替换付款明细名称为银行名称 if (pbf.getBankLog().bankinfo != null) { String bankinfo
	 * = pbf.getBankLog().bankinfo.trim(); if (bankinfo.length() > 0 && pbf !=
	 * null && pbf.getReplaceBankNameMode()) { salepay.payname = bankinfo; } } }
	 * }
	 */
	public void accountPay(boolean ret, PaymentBankFunc pbf)
	{
		accountPay(ret, pbf.getBankLog(), pbf);
	}

	public void accountPay(boolean ret, BankLogDef bld, PaymentBankFunc pbf)
	{
		if (!ret)
		{
			// 交易失败，放弃付款对象
			salepay = null;
		}
		else
		{
			// 交易成功，记录交易数据到付款对象，batch必须不为空,标记付款已记账
			salepay.payno = bld.cardno;
			salepay.batch = String.valueOf(bld.trace);
			salepay.str1 = salepay.batch;
			salepay.idno = salepay.batch;
			salepay.ybje = bld.je;
			salepay.je = ManipulatePrecision.doubleConvert(salepay.ybje * ((paymode != null) ? paymode.hl : 1));
			salepay.kye = bld.kye;
			salepay.memo = bld.memo;
			salepay.num6 = bld.ylzk;
			// 替换付款明细名称为银行名称
			if (bld.bankinfo != null)
			{
				String bankinfo = bld.bankinfo.trim();
				if (bankinfo.length() > 2 && pbf != null && pbf.getReplaceBankNameMode())
				{
					int p = bankinfo.indexOf("-");
					if (p > 0)
						salepay.payname = bankinfo.substring(p + 1);
					else
						salepay.payname = bankinfo;
				}
			}
		}

		// 更新付款断点数据，标记为已付款状态,否则在记账以后如果掉电,断点读入的还是未记账状态
		if (this.saleBS != null)
			this.saleBS.writeBrokenData();
	}

	public static void printXYKDoc(String batch)
	{
		BufferedReader br = null;
		String line = null;
		String filename = "bankdoc_" + batch + ".txt";

		if (!PaymentBank.haveXYKDoc)
			return;

		try
		{
			if (!PathFile.fileExist(filename))
			{
//				new MessageBox("找不到流水号[" + batch + "]的签购单打印文件!");
				new MessageBox(Language.apply("找不到流水号[{0}]的签购单打印文件!" ,new Object[]{batch}));

				return;
			}

			br = CommonMethod.readFile(filename);
			if (br == null)
			{
//				new MessageBox("打开流水号[" + batch + "]的签购单打印文件失败!");
				new MessageBox(Language.apply("打开流水号[{0}]的签购单打印文件失败!" ,new Object[]{batch}));

				return;
			}

			//
			Printer.getDefault().startPrint_Journal();

			//
			while ((line = br.readLine()) != null)
			{
				if (line.length() <= 0)
				{
					Printer.getDefault().printLine_Journal("\n");
				}
				else
				{
					Printer.getDefault().printLine_Journal(line);
				}
			}

			//
			Printer.getDefault().cutPaper_Journal();

			// 关闭并删除打印文件
			br.close();
			br = null;
			File f = new File(filename);
			f.delete();
		}
		catch (Exception e)
		{
			e.printStackTrace();

//			new MessageBox("打印签购单异常:\n\n" + e.getMessage());
			new MessageBox(Language.apply("打印签购单异常:\n\n") + e.getMessage());
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public SalePayDef getAllotSalePay()
	{
		Vector vlog = AccessDayDB.getDefault().getBankAllotLog();

		if (vlog == null || vlog.size() <= 0)
			return null;

		//
		Vector v = new Vector();
		Vector v2 = new Vector();
		for (int i = 0; i < vlog.size(); i++)
		{
			BankLogDef bld = (BankLogDef) vlog.elementAt(i);

			// 判断卡类型
			if (!CreatePayment.getDefault().getPaymentBankFunc(paymode.code).isCardType(bld, paymode))
			{
				continue;
			}

			String[] s = new String[4];
			s[0] = String.valueOf(bld.trace);
			s[1] = ManipulatePrecision.doubleToString(bld.je);
			s[2] = ManipulatePrecision.doubleToString(bld.allotje);
			s[3] = bld.cardno;

			v.add(s);
			v2.add(bld);
		}

		//
		int choice = -1;
		if (v.size() <= 0)
		{
			return null;
		}
		else if (v.size() >= 2)
		{
//			if ((choice = new MutiSelectForm().open("请输入流水号", new String[] { "流水号", "交易金额", "分配余额", "交易卡号" }, new int[] { 90, 125, 125, 240 }, v, true, 645, 319, 615, 192, false)) < 0) { return null; }
			if ((choice = new MutiSelectForm().open(Language.apply("请输入流水号"), new String[] { Language.apply("流水号"), Language.apply("交易金额"), Language.apply("分配余额"), Language.apply("交易卡号") }, new int[] { 90, 125, 125, 240 }, v, true, 645, 319, 615, 192, false)) < 0) { return null; }
		}
		else
		{
			choice = 0;
		}

		//
		BankLogDef bld = (BankLogDef) v2.elementAt(choice);
		StringBuffer hint = new StringBuffer();
		hint.append(Language.apply("交易流水: ") + bld.trace + "  ");
		hint.append(Language.apply("交易卡号: ") + bld.cardno + "\n");
		hint.append(Language.apply("交易金额: ") + ManipulatePrecision.doubleToString(bld.je) + "\n");
		hint.append(Language.apply("分配余额: ") + ManipulatePrecision.doubleToString(bld.allotje));
		StringBuffer buffer = new StringBuffer();

		// 默认金额
		String min = String.valueOf(bld.allotje);

		try
		{
			if (saleBS != null && paymode != null)
			{
				min = String.valueOf(Math.min(bld.allotje, saleBS.calcPayBalance() / paymode.hl));
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		buffer.append(min);

		String payname = Language.apply("有未入帐的银联消费,请输入本单的付款金额");

		if (paymode != null)
		{
//			payname = "有未入帐的" + paymode.name + "消费,请输入本单的付款金额";
			payname = Language.apply("有未入帐的{0}消费,请输入本单的付款金额" ,new Object[]{paymode.name});
		}

		if (!new TextBox().open(payname, Language.apply("本次付款"), hint.toString(), buffer, 0.01, bld.allotje, true)) { return null; }

		// 创建salepay对象
		if (!createSalePay(buffer.toString()))
			return null;

		// 记账并修改银联消费交易的可分配余额
		double oldje = bld.je;
		bld.je = salepay.je;
		accountPay(true, bld, null);
		salepay.kye = bld.allotje = ManipulatePrecision.sub(bld.allotje, bld.je);
		bld.je = oldje;
		if (!AccessDayDB.getDefault().updateBankLog(bld, true))
			salepay = null;

		// 记录对应的银联交易日志,删除付款时取消
		bldlog = bld;
		return salepay;
	}

	public boolean checkJeValid(double je)
	{
		if (salepay != null && salepay.je < 0)
		{
			if (Math.abs(salepay.je) != je)
			{
//				new MessageBox("金额必须为 " + Math.abs(salepay.je) + ",不允许修改");
				new MessageBox(Language.apply("金额必须为 {0},不允许修改" ,new Object[]{Math.abs(salepay.je)+""}));
				return false;
			}
		}

		return true;
	}
}
