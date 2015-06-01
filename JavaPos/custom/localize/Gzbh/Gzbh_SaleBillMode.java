package custom.localize.Gzbh;

import java.math.BigDecimal;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Gzbh_SaleBillMode extends SaleBillMode
{
	private String popInfoN;
	private String popInfoE;
	private String popInfoG;
	
	private double couponJe;
	
	protected final static int SBM_ZQZK = 201;// 赠券折扣

	public void printBill()
	{
		couponJe = calcCouponPay();
		super.printBill();
	}
	
	public void printDzkInfo(String[] info, String code)
	{
		Printer.getDefault().startPrint_Normal();
		if (code.trim().equals("0010"))
		{
			Printer.getDefault().printLine_Normal("广百积分卡结算单：");
			Printer.getDefault().printLine_Normal("收款机终端号：" + GlobalInfo.syjDef.syjh + "     收款员：" + GlobalInfo.posLogin.gh);
			Printer.getDefault().printLine_Normal("共有付款交易 " + info[1] + " 笔");
			Printer.getDefault().printLine_Normal("付款总额 " + info[0] + " 元");
			Printer.getDefault().printLine_Normal("共有冲正交易 " + info[3] + " 笔");
			Printer.getDefault().printLine_Normal("冲正总额 " + info[2] + " 元");
			Printer.getDefault().printLine_Normal("日结共有交易 " + new BigDecimal(info[1]).add(new BigDecimal(info[3])) + " 笔");
			Printer.getDefault().printLine_Normal("交易总额 " + new BigDecimal(info[0]).add(new BigDecimal(info[2])) + " 元");
			Printer.getDefault().printLine_Normal("--------------------------------");
			Printer.getDefault().printLine_Normal("结算时间: " + ManipulateDateTime.getCurrentDateTime());
		}
		if (code.trim().equals("0021"))
		{
			Printer.getDefault().printLine_Normal("新大新积分卡结算单：");
			Printer.getDefault().printLine_Normal("收款机终端号：" + GlobalInfo.syjDef.syjh + "     收款员：" + GlobalInfo.posLogin.gh);
			Printer.getDefault().printLine_Normal("共有消费卡交易 " + info[1] + " 笔");
			Printer.getDefault().printLine_Normal("消费卡总额 " + info[0] + " 元");
			Printer.getDefault().printLine_Normal("共有消费券交易 " + info[3] + " 笔");
			Printer.getDefault().printLine_Normal("消费券总额 " + info[2] + " 元");
			Printer.getDefault().printLine_Normal("日结共有交易 " + new BigDecimal(info[1]).add(new BigDecimal(info[3])) + " 笔");
			Printer.getDefault().printLine_Normal("交易总额 " + new BigDecimal(info[0]).add(new BigDecimal(info[2])) + " 元");
			Printer.getDefault().printLine_Normal("--------------------------------");
			Printer.getDefault().printLine_Normal("结算时间: " + ManipulateDateTime.getCurrentDateTime());
		}
		if (code.trim().equals("0031"))
		{
			Printer.getDefault().printLine_Normal("条码现金券结算单：");
			Printer.getDefault().printLine_Normal("收款机终端号：" + GlobalInfo.syjDef.syjh + "     收款员：" + GlobalInfo.posLogin.gh);
			Printer.getDefault().printLine_Normal("共有付款交易 " + info[1] + " 笔");
			Printer.getDefault().printLine_Normal("付款总额 " + info[0] + " 元");
			Printer.getDefault().printLine_Normal("共有冲正交易 " + info[3] + " 笔");
			Printer.getDefault().printLine_Normal("冲正总额 " + info[2] + " 元");
			Printer.getDefault().printLine_Normal("日结共有交易 " + new BigDecimal(info[1]).add(new BigDecimal(info[3])) + " 笔");
			Printer.getDefault().printLine_Normal("交易总额 " + new BigDecimal(info[0]).add(new BigDecimal(info[2])) + " 元");
			Printer.getDefault().printLine_Normal("--------------------------------");
			Printer.getDefault().printLine_Normal("结算时间: " + ManipulateDateTime.getCurrentDateTime());
		}
		Printer.getDefault().cutPaper_Normal();
	}

	public void printMZKBill(int type)
	{
		int i = 0;

		if (GlobalInfo.sysPara.mzkbillnum <= 0) return;

		System.out.println("sysPara.mzkbillnum: " + GlobalInfo.sysPara.mzkbillnum);

		try
		{
			// 先检查是否有需要打印的付款方式
			for (i = 0; i < originalsalepay.size(); i++)
			{
				SalePayDef pay = (SalePayDef) originalsalepay.elementAt(i);
				PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

				// 条码现金券不打印消费联
				if (pay.paycode.equals("0031"))
				{
					continue;
				}

				System.out.println("pay.paycode :" + pay.paycode);
				System.out.println("pay.paycode :" + mode.code);

				if (mode == null)
				{
					continue;
				}

				if ((type == 1) && (mode.type == '4'))
				{
					break;
				}

				if ((type == 2) && CreatePayment.getDefault().isPaymentFjk(mode.code))
				{
					break;
				}
			}

			if (i >= originalsalepay.size()) { return; }

			for (int n = 0; n < GlobalInfo.sysPara.mzkbillnum; n++)
			{
				// 开始新打印
				Printer.getDefault().startPrint_Journal();

				if (type == 1)
				{
					Printer.getDefault().printLine_Journal("\n             积分卡打印");
				}

				if (type == 2)
				{
					Printer.getDefault().printLine_Journal("\n             返券卡打印");
				}

				Printer.getDefault().printLine_Journal(
														"交易类型:"
																+ String.valueOf(SellType.getDefault().typeExchange(salehead.djlb, salehead.hhflag,
																													salehead)));

				Printer.getDefault().printLine_Journal("交易时间:" + salehead.rqsj);
				Printer.getDefault().printLine_Journal("收银机号:" + salehead.syjh + "     交易号:" + Convert.increaseLong(salehead.fphm, 8));
				Printer.getDefault().printLine_Journal("收银员号:" + salehead.syyh + "     门店号:" + GlobalInfo.sysPara.mktcode);
				Printer.getDefault().printLine_Journal("\n");

				int num = 0;
				double hj = 0;
				StringBuffer line = null;

				for (i = 0; i < originalsalepay.size(); i++)
				{
					SalePayDef pay = (SalePayDef) originalsalepay.elementAt(i);
					PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

					if ((((type == 1) && (mode.type == '4')) || ((type == 2) && (CreatePayment.getDefault().isPaymentFjk(mode.code))))
							&& !pay.paycode.equals("0031"))
					{
						line = new StringBuffer();
						num++;

						//卡号
						line.append("卡号:  " + pay.payno + "\n");

						//流水号补零（10位）
						String seqno = pay.batch;
						while (seqno.length() < 10)
						{
							seqno = "0" + seqno;
						}

						//门店号
						String mkt = GlobalInfo.sysPara.mktcode;

						//门店号取后两位
						mkt = mkt.substring(mkt.length() - 2, mkt.length());

						//组合流水号
						seqno = mkt + "5" + salehead.syjh + seqno;
						line.append("流水:  " + seqno + "\n");
						line.append("消费:  " + ManipulatePrecision.doubleToString(pay.ybje * SellType.SELLSIGN(salehead.djlb)) + "     " + "余额:  "
								+ ManipulatePrecision.doubleToString(pay.kye) + "\n");
						line.append("-----------------------------------");

						Printer.getDefault().printLine_Journal(line.toString());

						if (pay.hl == 0)
						{
							pay.hl = 1;
						}

						hj += (pay.ybje * pay.hl);
					}
				}

				if (type == 1)
				{
					Printer.getDefault().printLine_Journal("本次共 " + num + " 张积分卡消费");
				}

				if (type == 2)
				{
					Printer.getDefault().printLine_Journal("本次共 " + num + " 张返券卡消费");
				}

				Printer.getDefault().printLine_Journal("合计消费金额     " + ManipulatePrecision.doubleToString(hj * SellType.SELLSIGN(salehead.djlb)));
				Printer.getDefault().printLine_Journal("欢迎再次光临！");

				if (n == 0)
				{
					Printer.getDefault().printLine_Journal("第一联：交给顾客");
				}
				else if (n == 1)
				{
					Printer.getDefault().printLine_Journal("第二联：商场保留");
				}

				Printer.getDefault().cutPaper_Journal();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	protected String extendCase(PrintTemplateItem item, int index)
	{
		String line = null;
		int printCode = Integer.parseInt(item.code);
		if (printCode == SBM_payname || printCode == SBM_ybje || printCode == SBM_payno || printCode == SBM_ye || printCode == SBM_payfkje
				|| printCode == SBM_paycode || printCode == SBM_fkyy)
		{
			String code = ((SalePayDef) salepay.elementAt(index)).paycode;
			if (isCouponPay(code)) return "&!";
		}

		switch (printCode)
		{
			case SBM_spzkbfb:
				SaleGoodsDef saleGoodsDef = (SaleGoodsDef) salegoods.elementAt(index);
				String zkbfb = ManipulatePrecision.doubleToString((1 - (saleGoodsDef.hjzk / saleGoodsDef.hjje)) * 100, 0, 1, false);
				line = zkbfb + "%";
				break;

			case SBM_hjzke:
				if (salehead.hjzke == 0)
				{
					line = null;
				}
				else
				{
					String zzkbfb = ManipulatePrecision.doubleToString((1 - (salehead.hjzke / salehead.hjzje)) * 100, 0, 1, false);
					line = zzkbfb + "%";
				}
				break;
			case SBM_bcjf:
				if (salehead.hykh != null && salehead.hykh.length() > 0 && !GlobalInfo.isOnline)
				{
					line = "稍后计算积分";
				}
				else
				{
					line = ManipulatePrecision.doubleToString(salehead.bcjf);
				}
				break;
			case SBM_printinfo1:
				if (GlobalInfo.sysPara.printInfo1.split(",").length > printnum)
				{
					line = GlobalInfo.sysPara.printInfo1.split(",")[printnum];
				}
				break;
			case SBM_sjfk:
				double sjfk = (salehead.sjfk - salehead.zl) * SellType.SELLSIGN(salehead.djlb);
				if (this.couponJe > 0) sjfk -= this.couponJe * SellType.SELLSIGN(salehead.djlb);
				line = ManipulatePrecision.doubleToString(sjfk);
				break;
			case SBM_ybje:
				SalePayDef sp = (SalePayDef) salepay.elementAt(index);
				if (sp.paycode.equals("0001"))
				{
					line = ManipulatePrecision.doubleToString((sp.ybje - salehead.zl) * SellType.SELLSIGN(salehead.djlb));
				}
				else
				{
					line = ManipulatePrecision.doubleToString(sp.ybje * SellType.SELLSIGN(salehead.djlb));
				}
				break;
			case SBM_ZQZK:
				if (this.couponJe > 0) line = ManipulatePrecision.doubleToString(this.couponJe);
				else line = null;
				break;
		}
		return line;
	}

	private boolean isCouponPay(String payCode)
	{
		// 0003，0005，0013，0014，0015，0017，0023，0025，0032，0031
		if (payCode.equals("0003") || payCode.equals("0005") || payCode.equals("0013") || payCode.equals("0014") || payCode.equals("0015")
				|| payCode.equals("0017") || payCode.equals("0023") || payCode.equals("0025") || payCode.equals("0032") || payCode.equals("0031")) { return true; }
		return false;
	}

	private double calcCouponPay()
	{
		SalePayDef sp = null;
		double je = 0;
		for (int i = 0; i < salepay.size(); i++)
		{
			sp = (SalePayDef) salepay.get(i);
			if (isCouponPay(sp.paycode) && sp.flag == '1')
			{
				je += sp.je;
			}
		}
		return ManipulatePrecision.doubleConvert(je);
	}

	/*
	public void printTotal()
	{
		super.printTotal();
		if (this.couponJe > 0)
		{
			if (SellType.ISSALE(salehead.djlb))
			{
				Printer.getDefault().printLine_Normal(" 赠券折扣: " + this.couponJe);
			}
			else
			{
				Printer.getDefault().printLine_Journal(" 赠券折扣: " + this.couponJe);
			}
		}
	}
	*/

	// 重打印时提示是否打印附加联
	protected void printAppendBill()
	{
		if (salehead.printnum > 0)
		{
			boolean hasMzkPay = false;
			for (int i = 0; i < originalsalepay.size(); i++)
			{
				SalePayDef pay = (SalePayDef) originalsalepay.elementAt(i);
				PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

				// 条码现金券不打印消费联
				if (pay.paycode.equals("0031"))
				{
					continue;
				}

				if (mode == null)
				{
					continue;
				}

				if (mode.type == '4' || CreatePayment.getDefault().isPaymentFjk(mode.code))
				{
					hasMzkPay = true;
					break;
				}
			}

			if (hasMzkPay)
			{
				if (new MessageBox("是否打印卡券类消费联？", null, true).verify() == GlobalVar.Key1)
				{
					super.printAppendBill();
				}
			}
		}
		else
		{
			super.printAppendBill();
		}
	}

	public void printBottom()
	{
		// 打印满赠信息
		if (salehead.memo != null && salehead.memo.trim().length() > 0)
		{
			if (salehead.memo.trim().charAt(0) == 'Y')
			{
				Printer.getDefault().printLine_Normal("本小票符合参与本期赠礼活动\n");
				Printer.getDefault().printLine_Normal("请到指定服务点领取礼品一份,感谢您的参与!");
			}

			if (salehead.memo.trim().length() > 1 && salehead.memo.trim().charAt(1) == 'Y')
			{
				Printer.getDefault().printLine_Normal("本小票符合参与本期换购活动\n");
				Printer.getDefault().printLine_Normal("请到指定服务点参与换购活动,感谢您的参与！");
			}
		}
		// 打印换购信息
		super.printBottom();
		//		printPopInfo();
	}

	public void printPopInfo()
	{
		if (!SellType.ISSALE(salehead.djlb)) { return; }
		if (SellType.NOPOP(salehead.djlb)) { return; }

		popInfoN = "";
		popInfoE = "";
		popInfoG = "";
		boolean havePop = false;
		SaleGoodsDef sg = null;
		for (int i = 0; i < salegoods.size(); i++)
		{
			sg = (SaleGoodsDef) salegoods.get(i);
			if (sg.str7 != null && sg.str7.length() > 0)
			{
				if (sg.str7.equals("N"))
				{
					if (!havePop) havePop = true;
					popInfoN += String.valueOf(sg.rowno) + " ";
				}
				else if (sg.str7.equals("E"))
				{
					if (!havePop) havePop = true;
					popInfoE += String.valueOf(sg.rowno) + " ";
				}
				else if (sg.str7.equals("G"))
				{
					if (!havePop) havePop = true;
					popInfoG += String.valueOf(sg.rowno) + " ";
				}
			}
		}
		if (!havePop) return;

		if (popInfoN.trim().length() > 0)
		{
			Printer.getDefault().printLine_Normal("第" + popInfoN + "行满足组合促销A");
		}
		if (popInfoE.trim().length() > 0)
		{
			Printer.getDefault().printLine_Normal("第" + popInfoE + "行满足组合促销B");
		}
		if (popInfoG.trim().length() > 0)
		{
			Printer.getDefault().printLine_Normal("第" + popInfoG + "行满足满减促销");
		}
	}
}
