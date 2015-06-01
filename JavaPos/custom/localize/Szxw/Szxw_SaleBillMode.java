package custom.localize.Szxw;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bhls.Bhls_SaleBillMode;

public class Szxw_SaleBillMode extends Bhls_SaleBillMode
{
	protected final int SBM_yfphm = 101;

	private final static String blank = "                                               ";

	public boolean isSecondPrint = false;

	public void printCutPaper()

	{
		if (isSecondPrint)
		{
			Printer.getDefault().cutPaper_Slip();
		}
		else
		{
			Printer.getDefault().cutPaper_Normal();
		}
	}

	// 打印面值卡联
	public void printMZKBill(int type)
	{
		return;
	}

	protected void printLine(String s)
	{
		if (isSecondPrint)
		{
			Printer.getDefault().printLine_Slip(blank + s);
		}
		else
		{
			Printer.getDefault().printLine_Normal(s);
		}
	}

	// 平推打印定金签发
	public void printDjqf()
	{
		if (SellType.ISHC(salehead.djlb)) { return; }

		for (int i = 0; i < salepay.size(); i++)
		{
			SalePayDef pay = (SalePayDef) salepay.elementAt(i);

			// 只查询用定金单付款且有余额 和 定金签发
			if (!(pay.paycode.equals("0411") && pay.memo != null && pay.memo.length() > 0) && !(pay.paycode.equals("DJQF")))
			{
				continue;
			}

			if ((pay.paycode.equals("0411") && pay.memo != null && pay.memo.length() > 0))
			{
				int ret = new MessageBox("定金单付款存在余额，是否打印新定金单?\n", null, true).verify();
				if (ret != GlobalVar.Key1 && ret != GlobalVar.Enter)
				{
					continue;
				}
			}

			if (salehead.printnum > 0 && GlobalInfo.posLogin.priv.charAt(6) != 'Y')
			{
				OperUserDef usr = DataService.getDefault().personGrant("授权重打印签购单");
				if (usr.priv.charAt(6) != 'Y')
				{
					new MessageBox("此人员无重打印定金单权限，无法打印定金单");
					return;
				}

				// 记录日志
				String log = "授权单品折扣,小票号:" + salehead.fphm + " 收银机号:" + salehead.syjh + ",重打印授权:" + usr.gh;
				AccessDayDB.getDefault().writeWorkLog(log);
			}

			if (pay.paycode.equals("0411") && pay.memo != null && pay.memo.length() > 0) Printer.getDefault()
																								.setEmptyMsg_Slip("用定金单付款且存在余额\n请塞入新定金单......");
			else if (salehead.djlb .equals( SellType.EARNEST_BACK)) Printer.getDefault().setEmptyMsg_Slip("退定金,请塞入退货单......");
			else Printer.getDefault().setEmptyMsg_Slip("新定金签发,请塞入定金收据......");

			Printer.getDefault().startPrint_Slip();
			if (GlobalInfo.sysPara.slipPrinter_area != null && GlobalInfo.sysPara.slipPrinter_area.trim().length() > 0
					&& GlobalInfo.sysPara.slipPrinter_area.split(";").length == 3)
			{
				String para[] = GlobalInfo.sysPara.slipPrinter_area.split(";");
				int start = 0;
				int end = 0;

				String line = para[2];
				String[] paras = line.split(",");
				start = Convert.toInt(paras[0]);
				end = Convert.toInt(paras[1]);

				if (start > end || end == 0) { return; }
				Printer.getDefault().setPagePrint_Slip(true, 150);
				Printer.getDefault().setPrintArea_Slip(start, end);
			}
			else
			{
				Printer.getDefault().printLine_Slip(blank);

				for (int j = 0; j < 23; j++)
				{
					Printer.getDefault().printLine_Slip(blank);
				}
			}

			try
			{
				// 消费，卡存在余额
				if (pay.paycode.equals("0411") && pay.memo != null && pay.memo.length() > 0)
				{
					String[] row = pay.memo.split(",");
					String payno = row[0];
					String payje = row[1];
					Printer.getDefault().printLine_Slip(blank);
					// 订单号
					Printer.getDefault().printLine_Slip(blank + "单号:  " + payno);
					// 来定金额
					Printer.getDefault().printLine_Slip(blank + "来定金额:  " + ManipulatePrecision.doubleToString(Convert.toDouble(payje)));
					// 付款方式
					if (pay.str4 != null && pay.str4.trim().length() > 0)
					{
						String[] memo = pay.str4.split(",");
						if (memo.length > 0 && memo[0] != null)
						{
							PayModeDef mode = DataService.getDefault().searchPayMode(memo[0]);
							if (mode != null) Printer.getDefault().printLine_Slip(blank + "付款方式:  " + mode.name);
						}

						if (memo.length > 1 && memo[1] != null)
						{
							Printer.getDefault().printLine_Slip(blank + "卡号:  " + memo[1]);
						}

						if (memo.length > 1 && memo[2] != null)
						{
							// 打印发定柜组
							Printer.getDefault().printLine_Slip(blank + "柜组:  " + memo[2]);
						}
					}
				}
				else
				// 定金单签发
				{
					Printer.getDefault().printLine_Slip(blank);
					// 订单号
					Printer.getDefault().printLine_Slip(blank + "单号:  " + pay.payno);
					// 来定金额
					if (SellType.ISSALE(salehead.djlb))
					{
						Printer.getDefault().printLine_Slip(blank + "来定金额:  " + ManipulatePrecision.doubleToString(pay.ybje));
					}
					else
					{
						Printer.getDefault().printLine_Slip(blank + "退还金额： " + ManipulatePrecision.doubleToString((-1) * pay.ybje));
					}

					// 付款方式
					for (int k = 0; k < salepay.size(); k++)
					{
						SalePayDef salePayDef = (SalePayDef) salepay.elementAt(k);
						// 重打印时去掉DJQF的付款方式
						if ("DJQF".equals(salePayDef.paycode))
						{
							continue;
						}
						// 定金签发时打印付款方式
						String payname = salePayDef.payname;
						// 定金签发时打印付款账号
						String cardno = salePayDef.payno;

						Printer.getDefault().printLine_Slip(blank + "付款方式:  " + payname);

						if (null != cardno && cardno.trim().length() > 0)
						{
							Printer.getDefault().printLine_Slip(blank + "卡号:  " + cardno);
						}
						// 打印发定柜组
						Printer.getDefault().printLine_Slip(blank + "柜组:  " + ((SaleGoodsDef) salegoods.elementAt(0)).gz);
					}
				}

				// 交易时间
				Printer.getDefault().printLine_Slip(blank + ManipulateDateTime.getCurrentDateTime());

				// 小票号，收银机号，收银员号
				Printer.getDefault().printLine_Slip(blank + pay.fphm + "  " + GlobalInfo.syjDef.syjh + "  " + salehead.syyh);
				Printer.getDefault().printLine_Slip(blank);
			}
			catch (Exception er)
			{
				er.printStackTrace();
			}

			Printer.getDefault().cutPaper_Slip();
		}
	}

	public void printBill()
	{
		printDjqf();

		super.printBill();
	}

	protected void printAppendBill()
	{
		super.printAppendBill();

		// 打印订金卡/定金单的余额流水,只选择打印有余额的票据，用完的不打（提示是否需要打印余额流水给顾客）

		boolean isprint = false;

		for (int i = 0; i < salepay.size(); i++)
		{
			SalePayDef pay = (SalePayDef) salepay.elementAt(i);
			PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

			if (mode == null) continue;

			if ((mode.type == '4' && pay.kye > 0) || (pay.paycode.equals("0411") && pay.memo != null && pay.memo.trim().length() > 0))
			{
				if (new MessageBox("是否需要打印余额流水?\n", null, true).verify() == GlobalVar.Key1)
				{
					isprint = true;
					break;
				}
			}
		}

		if (isprint)
		{
			try
			{
				Printer.getDefault().startPrint_Normal();
				Printer.getDefault().printLine_Normal("          余 额 流 水 单");
				Printer.getDefault().printLine_Normal(" 收银机:" + ConfigClass.CashRegisterCode);
				Printer.getDefault().printLine_Normal(" 发票号:" + salehead.fphm);

				for (int i = 0; i < salepay.size(); i++)
				{
					SalePayDef pay = (SalePayDef) salepay.elementAt(i);
					PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

					if (mode == null) continue;

					if ((mode.type == '4' && !pay.paycode.equals("0411") && pay.kye > 0))
					{
						String line = Convert.appendStringSize("", pay.payname, 1, 20, 38);
						line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(pay.kye), 25, 10, 38, 1);
						Printer.getDefault().printLine_Normal(line);
						line = Convert.appendStringSize("", pay.payno, 1, 20, 38);
						Printer.getDefault().printLine_Normal(line);

					}
					else if ((pay.paycode.equals("0411") && pay.memo != null && pay.memo.trim().length() > 0))
					{
						String[] row = pay.memo.split(",");
						String payno = row[0];
						String payje = row[1];
						String line = Convert.appendStringSize("", pay.payname, 1, 20, 38);
						line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(Convert.toDouble(payje)), 25, 10, 38, 1);
						Printer.getDefault().printLine_Normal(line);
						line = Convert.appendStringSize("", payno, 1, 20, 38);
						Printer.getDefault().printLine_Normal(line);
					}
				}
			}
			catch (Exception er)
			{
				er.printStackTrace();
			}
			finally
			{
				Printer.getDefault().cutPaper_Normal();
			}
		}

	}

	public void printYYYBill()
	{
		printYYYBillXW();
		return;
	}

	// 按西武的样张格式用平推打印营业员联
	public void printYYYBillXW()
	{
		// 系统参数定义为打印分单且是营业员小票，才打印营业员联
		if (GlobalInfo.syjDef.issryyy == 'N') { return; }

		if (!SellType.ISSALE(salehead.djlb) && !SellType.ISBACK(salehead.djlb)) { return; }

		if (SellType.ISHC(salehead.djlb)) { return; }

		if (SellType.ISEARNEST(salehead.djlb)) { return; }

		try
		{
			if (SellType.ISSALE(salehead.djlb)) Printer.getDefault().setEmptyMsg_Slip("请插入销售单......");
			else Printer.getDefault().setEmptyMsg_Slip("请插入退货单......");
			Printer.getDefault().startPrint_Slip();

			isSecondPrint = true;

			if (GlobalInfo.sysPara.slipPrinter_area != null && GlobalInfo.sysPara.slipPrinter_area.trim().length() > 0
					&& GlobalInfo.sysPara.slipPrinter_area.split(";").length == 3)
			{
				String para[] = GlobalInfo.sysPara.slipPrinter_area.split(";");
				int start = 0;
				int end = 0;
				if (SellType.ISSALE(salehead.djlb))
				{
					String line = para[0];
					String[] paras = line.split(",");
					start = Convert.toInt(paras[0]);
					end = Convert.toInt(paras[1]);
				}
				else
				{
					String line = para[1];
					String[] paras = line.split(",");
					start = Convert.toInt(paras[0]);
					end = Convert.toInt(paras[1]);
				}

				if (start > end || end == 0) { return; }
				Printer.getDefault().setPagePrint_Slip(true, 150);
				Printer.getDefault().setPrintArea_Slip(start, end);

			}

			// 打印头部区域
			printHeader();

			// 打印明细区域
			printDetail();

			// 打印汇总区域
			printTotal();

			// 打印付款区域
			printPay();
			
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}
		finally
		{
			printCutPaper();

			isSecondPrint = false;
		}
	}

	public void printBottom()
	{
		if (null != salehead.str5 && salehead.str5.length() > 0)
		{
			Printer.getDefault().printLine_Normal(" " + salehead.str5);
		}
		super.printBottom();
	}

	public void printPay()
	{

		// 设置打印区域
		setPrintArea("Pay");

		for (int i = 0; i < salepay.size(); i++)
		{
			SalePayDef salePayDef = (SalePayDef) salepay.elementAt(i);
			if (salePayDef.paycode.equals("01") && salePayDef.str3!=null && !salePayDef.str3.equalsIgnoreCase("Y"))
			{
				salepay.remove(i);
				salepay.add(0, salePayDef);
			}
		}

		// 循环打印付款明细
		for (int i = 0; i < salepay.size(); i++)
		{
			SalePayDef spd = (SalePayDef) salepay.elementAt(i);

			// 找零付款不打印
			if (spd.flag == '2')
			{
				continue;
			}

			printVector(getCollectDataString(Pay, i, Width));

			// 西武要求销售单据和小票打印定金单的付款明细
			if (spd.paycode.equals("0411"))
			{
				if (spd.str4 != null && spd.str4.trim().length() > 0)
				{
					String[] memo = spd.str4.split(",");

					if (memo.length > 0 && memo[0] != null)
					{
						PayModeDef mode = DataService.getDefault().searchPayMode(memo[0]);
						if (mode != null) printLine("   付款方式(签发)  :" + mode.name);
					}

					if (memo.length > 1 && memo[1] != null)
					{
						printLine("   卡号(签发)  :" + memo[1]);
					}

					if (memo.length > 1 && memo[2] != null)
					{
						// 打印发定柜组
						printLine("   柜组(签发)  :" + memo[2]);
					}
				}
			}
		}
	}
	
    public void printGift()
    {
        boolean first = false;

        for (int i = 0; i < salegoods.size(); i++)
        {
            SaleGoodsDef saleGoodsDef = (SaleGoodsDef) salegoods.elementAt(i);

            if (saleGoodsDef.flag == '1' || saleGoodsDef.flag == '5')
            {
                if (!first)
                {
                    printLine(Convert.appendStringSize("", "\n赠品栏", 0, Width, Width, 2));
                }

                first = true;
                printLine("商品编码:" + saleGoodsDef.code);
                printLine("商品柜组:" + saleGoodsDef.gz);

                String line = Convert.appendStringSize("", "赠送价值:", 0, 9, Width);
                line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(saleGoodsDef.lsj), 10, 10, Width);
                line = Convert.appendStringSize(line, "赠送数量:", 21, 9, Width);
                line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(saleGoodsDef.sl, 4, 1), 30, 8, Width);
                printLine(line);
                printLine("\n");
            }
            if (first)
            {
            	printLine(Convert.appendStringSize("", "\n赠品限当日领取", 0, Width, Width, 2));
            }
        }
    }

	protected String extendCase(PrintTemplateItem item, int index)
	{
		String line = null;
		switch (Integer.parseInt(item.code))
		{
			case SBM_yfphm:
				if (SellType.ISHC(salehead.djlb) && salegoods.size() > 0)
				{
					line = "原交易号：" + ((SaleGoodsDef) salegoods.elementAt(0)).yfphm;
				}
				else
				{
					line = "";
				}
				break;
			case SBM_dphjzk: // 折扣
				if (((SaleGoodsDef) salegoods.elementAt(index)).hjzk == 0)
				{
					line = "&!";
				}
				else
				{
					line = ManipulatePrecision.doubleToString(((SaleGoodsDef) salegoods.elementAt(index)).hjzk * SellType.SELLSIGN(salehead.djlb)
							* (-1));
				}

				break;
		}
		return line;
	}
}
