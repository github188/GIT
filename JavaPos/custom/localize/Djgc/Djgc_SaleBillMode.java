package custom.localize.Djgc;


import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.CardSaleBillMode;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Cmls.Cmls_SaleBillMode;
import custom.localize.Djgc.Djgc_DisplaySaleTicketBS;
import custom.localize.Djgc.Djgc_NetService;
import custom.localize.Djgc.Djgc_SaleBS;

public class Djgc_SaleBillMode extends Cmls_SaleBillMode
{
	protected void printSellBill()
	{
		String[] ss = null;
		//如果是正常销售就不穿重打原因字段
		if(salehead.printnum == 0)
		{
			String printType = "1";              //发票打印类型  1正常销售 2重打
			
			String startfph = String.valueOf(Printer.getDefault().getCurrentSaleFphm());
			String usedfphnum = "";

			String[] s = {GlobalInfo.sysPara.mktcode, ConfigClass.CashRegisterCode ,String.valueOf(salehead.fphm) ,printType ,"0" ,salehead.syyh ,startfph, usedfphnum,ManipulateDateTime.getDateTimeByClock()};
			ss = s;
		}
		
		
		// GlobalInfo.sysPara.fdprintyyy =
		// (N-不打营业员联但打印小票联/Y-打印营业员联也打印小票/A-打印营业员联但不打印小票)
		// 非超市小票且系统参数定义只打印营业员分单，则不打印机制小票
		if (!((GlobalInfo.syjDef.issryyy == 'N') || (GlobalInfo.syjDef.issryyy == 'A' && ((SaleGoodsDef) salegoods.elementAt(0)).yyyh.equals(Language.apply("超市")))) && (GlobalInfo.sysPara.fdprintyyy == 'A')) { return; }

		if (!SellType.ISEXERCISE(salehead.djlb) && printnum < 1 && salehead.printnum < 1 && !getFaxInfo())
			new MessageBox(Language.apply("获取税控信息失败！"));

		// 设置打印方式
		printSetPage();

		// 多联小票打印不同抬头
		printDifTitle();

		// 打印头部区域
		printHeader();

		// 打印明细区域
		printDetail();

		// 打印汇总区域
		printTotal();

		// 打印付款区域
		printPay();

		// 打印尾部区域
		printBottom();

		// 切纸
		printCutPaper();
		
		
		
		if(salehead.printnum == 0)
		{
			ss[7] = String.valueOf(Printer.getDefault().getCurrentSaleFphm()-1);
			new Djgc_NetService().postReprint(ss);
		}
	}
	
//	 打印面值卡联
	public void printMZKBill(int type)
	{
		int i = 0;
		
		if (GlobalInfo.sysPara.mzkbillnum <= 0) { return; }

		try
		{
			// 先检查是否有需要打印的付款方式
			for (i = 0; i < originalsalepay.size(); i++)
			{
				SalePayDef pay = (SalePayDef) originalsalepay.elementAt(i);
				PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

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
				printStart();

				if (type == 1)
				{
					Printer.getDefault().printLine_Journal("\n        "+Language.apply("消费卡")+ "    " + Language.apply("**重印"+salehead.printnum+"**"));
				}

				if (type == 2)
				{
					Printer.getDefault().printLine_Journal("\n        "+Language.apply("返券卡"));
				}

				Printer.getDefault().printLine_Journal(Language.apply(" 门店号:") + GlobalInfo.sysPara.mktcode + "   "+Language.apply("交易时间:") + salehead.rqsj.substring(0, 10));
				Printer.getDefault().printLine_Journal(Language.apply(" 交易号:") + Convert.increaseLong(salehead.fphm, 8) + "     "+Language.apply("收银机号:") + salehead.syjh);
				Printer.getDefault().printLine_Journal(Language.apply(" 收银员:") + GlobalInfo.posLogin.name + "     "+Language.apply("交易类型:") + String.valueOf(SellType.getDefault().typeExchange(salehead.djlb, salehead.hhflag, salehead)));
				Printer.getDefault().printLine_Journal(" --------------------------------");
				Printer.getDefault().printLine_Journal(Language.apply(" 卡号")+"                     "+Language.apply("消费金额"));

				int num = 0;
				double hj = 0;
				String line = null;

				for (i = 0; i < originalsalepay.size(); i++)
				{
					SalePayDef pay = (SalePayDef) originalsalepay.elementAt(i);
					PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

					if (((type == 1) && (mode.type == '4')) || ((type == 2) && (CreatePayment.getDefault().isPaymentFjk(mode.code))))
					{
						num++;
						line = Convert.appendStringSize("", pay.payno, 1, 20, 40, 0);
						line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(pay.ybje * SellType.SELLSIGN(salehead.djlb)), 25, 7, 40, 0);

						Printer.getDefault().printLine_Journal(line);

						if (pay.hl == 0)
						{
							pay.hl = 1;
						}

						hj += (pay.ybje * pay.hl);
					}
				}

				Printer.getDefault().printLine_Journal(" --------------------------------");
				
				if (type == 1)
				{
					Printer.getDefault().printLine_Journal(Language.apply(" 本次共 {0} 张消费卡" ,new Object[]{num+""}));
				}

				if (type == 2)
				{
					Printer.getDefault().printLine_Journal(Language.apply(" 本次共 {0} 张返券卡消费" ,new Object[]{num+""}));
				}

				Printer.getDefault().printLine_Journal(Language.apply(" 合计金额")+ ManipulatePrecision.doubleToString(hj * SellType.SELLSIGN(salehead.djlb))+ "  " + "元");
//				printCutPaper();
				Printer.getDefault().cutPaper_Journal();
				
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	
//	 通过模版来打印卡联
	public void printMZKBillPrintMode()
	{
		boolean bool = false;

		if (GlobalInfo.sysPara.mzkbillnum <= 0) { return; }

		if ((GlobalInfo.sysPara.printpaymode == null) || GlobalInfo.sysPara.printpaymode.equals("")) { return; }

		CardSaleBillMode.getDefault().setTemplateObject(salehead, salegoods, originalsalepay);

		String[] printpaymode = GlobalInfo.sysPara.printpaymode.split("\\|");

		for (int i = 0; i < printpaymode.length; i++)
		{
			CardSaleBillMode.getDefault().setPayCodes(printpaymode[i]);

			for (int j = 0; j < originalsalepay.size(); j++)
			{
				SalePayDef pay = (SalePayDef) originalsalepay.elementAt(j);
				PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

				if (mode == null)
					continue;

				if (!CardSaleBillMode.getDefault().isExistPaycode(mode.code))
					continue;

				bool = true;

				CardSaleBillMode.getDefault().setPayName(mode.name);

				break;
			}

			for (int n = 0; (n < GlobalInfo.sysPara.mzkbillnum) && bool; n++)
			{
//				String[] ss = null;
////				如果是正常销售就不穿重打原因字段
//				if(salehead.printnum == 0)
//				{
//					String printType = "1";              //发票打印类型  1正常销售 2重打
//					
//					String startfph = String.valueOf(Printer.getDefault().getCurrentSaleFphm());
//					String usedfphnum = "";
//
//					String[] s = {GlobalInfo.sysPara.mktcode, ConfigClass.CashRegisterCode ,String.valueOf(salehead.fphm) ,printType ,"0" ,salehead.syyh ,startfph, usedfphnum,ManipulateDateTime.getDateTimeByClock()};
//					ss = s;
//				}
				
				CardSaleBillMode.getDefault().printBill();
				
//				if(salehead.printnum == 0)
//				{
//					ss[7] = String.valueOf(Printer.getDefault().getCurrentSaleFphm());;
//					new Djgc_NetService().postReprint(ss);
//				}
				
			}
			bool = false;
		}
	}

}
