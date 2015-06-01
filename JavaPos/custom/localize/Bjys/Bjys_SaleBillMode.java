package custom.localize.Bjys;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.CalcRulePopDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Bjys_SaleBillMode extends SaleBillMode
{
	protected final int SBM_PAYDATE = 101;
	protected final int SBM_BYERCODE = 102;
	protected final int SBM_JFZK = 103;
	Vector sort_Detail = null;

	// 标志是否第二站打印
	private boolean isSecondPrint = false;

	protected String extendCase(PrintTemplateItem item, int index)
	{
		String line = null;
		PayModeDef pmd = null;

		switch (Integer.parseInt(item.code))
		{
			case SBM_inputbarcode: // 打印输入商品编码
				line = ((SaleGoodsDef) salegoods.elementAt(index)).barcode;
				break;
			case SBM_PAYDATE: // 支票付款方式的付货日期

				pmd = new DataService().searchPayMode(((SalePayDef) salepay.elementAt(index)).paycode);

				if (pmd != null && (pmd.type == '2'))
				{
					line = "提货日期:" + ((SalePayDef) salepay.elementAt(index)).memo;
				}

				break;
			case SBM_BYERCODE: // 旅行团号

				if (salehead.buyerinfo != null && !salehead.buyerinfo.trim().equals("") && (!salehead.buyerinfo.trim().equals("0000") && !salehead.buyerinfo.trim().equals("000000")))
					line = "旅游团号:" + salehead.buyerinfo.substring(4, 6);
				break;
			case SBM_JFZK: // 积分折扣
				if (salehead.num1 == 0)
					line = null;
				else
					line = ManipulatePrecision.doubleToString(salehead.num1);
				break;
		}

		return line;
	}

	protected void printLine(String s)
	{
		if (isSecondPrint)
		{
			Printer.getDefault().printLine_Journal(s);
		}
		else
		{
			Printer.getDefault().printLine_Normal(s);
		}
	}

	protected void printArea(int startRow, int endRow)
	{
		if (isSecondPrint)
		{
			Printer.getDefault().setPrintArea_Journal(startRow, endRow);
		}
		else
		{
			Printer.getDefault().setPrintArea_Normal(startRow, endRow);
		}
	}

	public void printCutPaper()
	{
		if (isSecondPrint)
		{
			Printer.getDefault().cutPaper_Journal();
		}
		else
		{
			Printer.getDefault().cutPaper_Normal();
		}
	}

	public void printBottom(String msg)
	{
		super.printBottom();

		if (getMktCode() != null && (getMktCode().trim().equals("0005") || getMktCode().trim().equals("0001")))
			if (msg != null && !msg.equals(""))
				printLine(msg);
	}

	public void printSetPage()
	{
		if (isSecondPrint)
		{
			// 设置是否分页打印
			if (PagePrint != 1)
			{
				Printer.getDefault().setPagePrint_Journal(false, 1);
			}
			else
			{
				Printer.getDefault().setPagePrint_Journal(true, Area_PageFeet);
			}
		}
		else
		{
			// 设置是否分页打印
			if (PagePrint != 1)
			{
				Printer.getDefault().setPagePrint_Normal(false, 1);
			}
			else
			{
				Printer.getDefault().setPagePrint_Normal(true, Area_PageFeet);
			}
		}
	}

	public void printRealTimeHead()
	{
		// 设置打印方式
		printSetPage();

		// 打印头部区域
		printHeader();
	}

	public void printRealTimeDetail(int index)
	{
		// 设置打印区域
		setPrintArea("Detail");

		// 打印商品明细
		printVector(getCollectDataString(Detail, index, Width));
	}

	public void printRealTimeCancel()
	{
		if (SellType.ISBACK(salehead.djlb) || SellType.ISHC(salehead.djlb))
		{
			Printer.getDefault().printLine_Journal("--------------------------");
			Printer.getDefault().printLine_Journal(" 以上小票明细作废，重新打印 ");
			Printer.getDefault().printLine_Journal("--------------------------");

			Printer.getDefault().cutPaper_Journal();
		}
		else
		{
			Printer.getDefault().printLine_Normal("--------------------------");
			Printer.getDefault().printLine_Normal(" 以上小票明细作废，重新打印 ");
			Printer.getDefault().printLine_Normal("--------------------------");

			Printer.getDefault().cutPaper_Normal();
		}
	}

	public void printRealTimeBottom()
	{
		// 打印汇总区域
		printTotal();

		// 打印付款区域
		printPay();

		// 打印尾部区域
		printBottom();

		// 切纸
		printCutPaper();

		// 打印银联交易签购单
		printBankBill();

		// 等于2代表打印双站
		if (ConfigClass.CustomItem1.trim().equals("2"))
		{
			isSecondPrint = true;

			// 设置打印方式
			printSetPage();

			// 打印头部区域
			printHeader();

			// 打印明细区域
			printDetail();

			// 打印汇总区域
			printTotal();

			// 打印付款区域
			printPay();

			// 打印重打印标志
			if (salehead.printnum > 0)
			{
				printLine("-----------重打印---------");
			}

			// 切纸
			printCutPaper();

			//
			isSecondPrint = false;
		}
	}

	public void printDetail(CalcRulePopDef crp)
	{
		// 设置打印区域
		setPrintArea("Detail");

		double xj = 0; // 小计
		double zk = 0; // 折扣

		// 循环打印商品明细
		for (int i = 0; i < crp.row_set.size(); i++)
		{
			int row = Integer.parseInt((String) crp.row_set.elementAt(i));
			SaleGoodsDef sgd = (SaleGoodsDef) salegoods.elementAt(row);

			// 赠品商品不打印
			if (sgd.flag == '1')
			{
				continue;
			}

			xj += sgd.hjje;
			zk += sgd.hjzk;

			printVector(getCollectDataString(Detail, row, Width));
		}

		printLine(" 营业员:          " + crp.code);
		printLine(" 小计:" + Convert.appendStringSize("", ManipulatePrecision.doubleToString(xj), 0, 10, 10) + "  折扣:" + Convert.appendStringSize("", ManipulatePrecision.doubleToString(zk), 0, 10, 10));
		printLine(" " + Convert.increaseChar("", '-', 37));

		printLine(" 会员卡号:   " + salehead.hykh);
	}

	public void printBill()
	{
		// 执行一次垃圾回收机制后再打印
		System.gc();

		String mktcode = null;
		// 根据参数控制打印销售小票的份数
		printnum = 0;
		if (GlobalInfo.sysPara.mktcode != null)
		{
			if (GlobalInfo.sysPara.mktcode.split(",").length >= 2)
			{
				mktcode = GlobalInfo.sysPara.mktcode.substring(GlobalInfo.sysPara.mktcode.indexOf(",") + 1);
			}
			else
			{
				mktcode = GlobalInfo.sysPara.mktcode;
			}
		}

		// 亮马桥:0001 金源:0005 太源:0006 无论是重打还是销售都打两次 salehead.printnum < 1
		if (mktcode != null && (mktcode.trim().equals("0005") || mktcode.trim().equals("0001")))
		{
			for (int i = 0; i < GlobalInfo.sysPara.salebillnum; i++)
			{
				// 先打印顾客留存
				if (i == 0)
					printSellBill("Big&****顾客留存****");
				else
					printSellBill("Big&****柜组留存****");

				printnum++;
			}
		}
		else
		{
			printSellBill();
		}
	}

	private void setEnableCutPaper(boolean flag)
	{
		Printer.getDefault().setEnableCutPaper(flag);
	}

	public void printSellBill(String msg)
	{
		// 设置打印方式
		if (SellType.ISSALE(salehead.djlb) || SellType.ISBACK(salehead.djlb))
		{
			// 正常打印，用于超市
			if (GlobalInfo.syjDef.issryyy != 'Y')
			{
				setEnableCutPaper(false);
				
				// 设置打印方式
				printSetPage();

				// 打印头部区域
				printHeader();

				// 打印明细区域
				printDetail();

				// 打印汇总区域
				printTotal();

				// 打印付款区域
				printPay();

				// 打印尾部区域
				printBottom(msg);

				setEnableCutPaper(true);
				// 切纸
				printCutPaper();

			}
			else
			// 用于百货，分单打印
			{
				for (int i = 0; i < sort_Detail.size(); i++)
				{
					setEnableCutPaper(false);
					
					// 设置打印方式
					printSetPage();

					// 打印头部区域
					printHeader();

					// 打印明细区域
					printDetail((CalcRulePopDef) sort_Detail.elementAt(i));

					//
					if (i < (sort_Detail.size() - 1))
					{
						printPay();

						// 打印提示信息 金源特殊需求
						if (getMktCode() != null && (getMktCode().trim().equals("0005") || getMktCode().trim().equals("0001")))
							if (msg != null && !msg.equals(""))
								printLine(msg);

						if (getMktCode() != null && !getMktCode().trim().equals("0006"))
						{
							setEnableCutPaper(true);
							// 切纸
							printCutPaper();
						}
					}
				}

				setEnableCutPaper(false);

				// 打印汇总区域
				printTotal();

				// 打印付款区域
				printPay();

				// 打印尾部区域
				printBottom(msg);

				setEnableCutPaper(true);
				// 切纸
				printCutPaper();
			}
		}

		if (GlobalInfo.syjDef.issryyy != 'Y' && SellType.ISHC(salehead.djlb))
		{
			printBankBill();
		}

		if (ConfigClass.CustomItem1.trim().equals("2"))
		{
			isSecondPrint = true;

			setEnableCutPaper(false);

			// 设置打印方式
			printSetPage();

			// 打印头部区域
			printHeader();

			// 打印明细区域
			printDetail();

			// 打印汇总区域
			printTotal();

			// 打印付款区域
			printPay();

			// 打印重打印标志
			if (salehead.printnum > 0)
			{
				printLine("-----------重打印---------");
			}

			setEnableCutPaper(true);
			// 切纸
			printCutPaper();

			isSecondPrint = false;
		}
	}

	public Vector convertGoodsDetail(Vector s)
	{
		if (GlobalInfo.syjDef.issryyy == 'Y' && GlobalInfo.syjDef.printfs != '1')
		{
			Vector newp = new Vector();
			SaleGoodsDef sgd = null;
			SaleGoodsDef sgd1 = null;
			CalcRulePopDef calPop;
			CalcRulePopDef calPop1;

			// 开始排序
			Vector new_s = (Vector) s.clone();
			Vector sort = new Vector();

			while (new_s.size() > 0)
			{
				int low = 0;
				sgd = (SaleGoodsDef) new_s.elementAt(low);

				for (int i = low + 1; i < new_s.size(); i++)
				{
					sgd1 = (SaleGoodsDef) new_s.elementAt(i);

					if (sgd.yyyh.trim().compareTo(sgd1.yyyh.trim()) >= 0)
					{
						low = i;
						sgd = (SaleGoodsDef) new_s.elementAt(low);
					}
				}

				sgd1 = (SaleGoodsDef) new_s.elementAt(low);
				sort.add(sgd1);
				new_s.removeElementAt(low);
			}

			// 分组汇总相同的商品明细
			for (int i = 0; i < sort.size(); i++)
			{
				sgd = (SaleGoodsDef) sort.elementAt(i);

				int j = 0;

				for (j = 0; j < newp.size(); j++)
				{
					calPop1 = (CalcRulePopDef) newp.elementAt(j);

					if (calPop1.code.equals(sgd.yyyh))
					{
						break;
					}
				}

				if (j >= newp.size())
				{
					calPop = new CalcRulePopDef();
					calPop.code = sgd.yyyh;
					calPop.row_set = new Vector();
					calPop.row_set.add(String.valueOf(i));
					newp.add(calPop);
				}
				else
				{
					calPop1 = (CalcRulePopDef) newp.elementAt(j);
					calPop1.row_set.add(String.valueOf(i));
				}
			}

			sort_Detail = newp;

			return sort;
		}
		else
		{
			return s;
		}
	}

	public boolean convertPayDetail(SalePayDef spd, SalePayDef spd1)
	{
		if ((spd1.paycode != null) && spd1.paycode.equals("05"))
		{
			return true;
		}
		else
		{
			return super.convertPayDetail(spd, spd1);
		}
	}

	public void printBankBill()
	{
		// 在原始付款清单中,查找是否有银联卡付款方式
		for (int i = 0; i < originalsalepay.size(); i++)
		{
			SalePayDef pay = (SalePayDef) originalsalepay.elementAt(i);

			PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

			if (mode.isbank == 'Y' && pay.batch != null && pay.batch.length() > 0)
			{
				Bjys_PaymentBank.printXYKDoc(pay.batch);
			}
		}
	}

	private String getMktCode()
	{
		String mktcode = null;

		if (GlobalInfo.sysPara.mktcode != null)
		{
			if (GlobalInfo.sysPara.mktcode.split(",").length >= 2)
			{
				mktcode = GlobalInfo.sysPara.mktcode.substring(GlobalInfo.sysPara.mktcode.indexOf(",") + 1);
			}
			else
			{
				mktcode = GlobalInfo.sysPara.mktcode;
			}
		}

		return mktcode;

	}
}
