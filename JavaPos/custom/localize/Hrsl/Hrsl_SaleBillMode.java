package custom.localize.Hrsl;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.PrintTemplate.CardSaleBillMode;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bstd.Bstd_SaleBillMode;

public class Hrsl_SaleBillMode extends Bstd_SaleBillMode
{
	protected void printAppendBill()
	{
		printAppendTicket();

		// 检查是否有未打印的银联签购单
		if (PaymentBank.haveXYKDoc)
		{
			printBankBill();
		}

		// 检查是否有
		if (CardSaleBillMode.getDefault().isLoad())
		{
			printMZKBillPrintMode();
		}
		else
		{
			// 打印面值卡联
			printMZKBill(1);

			// 打印返券卡联
			printMZKBill(2);
		}

		// 打印赠券联
		printSaleTicketMSInfo();

	}

	private void printAppendTicket()
	{
		if (salehead.printnum > 0)
		{
			MessageBox me = new MessageBox("是否重打印小票副联？", null, true);
			if (me.verify() != GlobalVar.Key1)
				return;
		}
		try
		{//店号，小票号，收银号，以及付款名称和付款金额
			printLine("          小 票 副 联            ");
			if (salehead.printnum > 0)
			{
				printLine("          *重打印*             ");

			}
			printLine("-------------------------------");
			printLine("门店号:" + salehead.mkt + "  收银机号:" + salehead.syjh);
			printLine("收银员:" + salehead.syyh + " " + salehead.rqsj);
			printLine("小票号:" + salehead.fphm + "   件数:" + salehead.hjzsl);
			printLine("-------------------------------");
			printLine("付款名称              付款金额");
			for (int j = 0; j < salepay.size(); j++)
			{
				SalePayDef spd = (SalePayDef) salepay.elementAt(j);
				printLine(spd.payname + "     " + spd.ybje * SellType.SELLSIGN(salehead.djlb));

			}
			printLine("应收:" + (salehead.ysje + salehead.sswr_sysy) * SellType.SELLSIGN(salehead.djlb));
			printLine("实收:" + (salehead.sjfk * SellType.SELLSIGN(salehead.djlb)));
			printLine("   ");

			Printer.getDefault().cutPaper_Normal();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}
	protected void printSellBill()
	{
		// GlobalInfo.sysPara.fdprintyyy =
		// (N-不打营业员联但打印小票联/Y-打印营业员联也打印小票/A-打印营业员联但不打印小票)
		// 非超市小票且系统参数定义只打印营业员分单，则不打印机制小票
		if (!((GlobalInfo.syjDef.issryyy == 'N') || (GlobalInfo.syjDef.issryyy == 'A' && ((SaleGoodsDef) salegoods.elementAt(0)).yyyh.equals("超市"))) && (GlobalInfo.sysPara.fdprintyyy == 'A'))
		{
			return;
		}
		
		if (!SellType.ISEXERCISE(salehead.djlb) && printnum < 1 && salehead.printnum < 1 && !getFaxInfo()) new MessageBox("获取税控信息失败！");

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
	//	printCutPaper();
	}
	
}
