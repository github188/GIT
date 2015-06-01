package custom.localize.Bjcx;


import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.CardSaleBillMode;
import com.efuture.javaPos.PrintTemplate.GiftBillMode;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Cmls.Cmls_SaleBillMode;

public class Bjcx_SaleBillMode extends Cmls_SaleBillMode
{
	

//	 打印赠券
	public void printSaleTicketMSInfo()
	{
		if (this.zq == null || this.zq.size() <= 0)
		{
			return ;
		}
		
		if (this.salemsinvo != 0 && salehead.fphm != this.salemsinvo)
		{
			this.salemsinvo = 0;
			this.zq = null;
			this.gift = null;
			return ;
		}
		
		for (int i = 0; i < this.zq.size(); i++)
		{
			GiftGoodsDef def = (GiftGoodsDef) this.zq.elementAt(i);
			if (!def.type.trim().equals("99") && !def.type.trim().equals("4"))
			{
				
	            if(GiftBillMode.getDefault().checkTemplateFile())
	            {
	            	GiftBillMode.getDefault().setTemplateObject(salehead, def);
	            	GiftBillMode.getDefault().PrintGiftBill();
	            	Printer.getDefault().cutPaper_Normal();//.cutPaper_Journal();
	            	continue;
	            }
	            
				Printer.getDefault().printLine_Journal("收银机号："+salehead.syjh+"  小票号："+Convert.increaseLong(salehead.fphm, 8));
				if (SellType.ISCOUPON(salehead.djlb))
					Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "买券交易", 1, 37, 38,2));

	            Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "				手 工 券", 1, 37, 38));
	            Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "=================================================", 1, 37, 38));
				Printer.getDefault().printLine_Journal("== 券  号  : "+def.code);
				Printer.getDefault().printLine_Journal("== 券信息  : "+def.info);
				Printer.getDefault().printLine_Journal("== 券总额  : "+def.je);
	            if (salehead.printnum > 0)
	        	{
	            	Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "				重 打 印", 1, 37, 38));
	        	}
				Printer.getDefault().printLine_Journal("== 券有效期: "+def.memo);
				Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "=================================================", 1, 37, 38));
				Printer.getDefault().cutPaper_Journal();
			}
		}
	}
	
	
	protected String getItemDataString(PrintTemplateItem item, int index)
	{
		String line = null;

		line = extendCase(item, index);

		String text = item.text;

		if (line == null)
		{
			switch (Integer.parseInt(item.code))
			{

				case SBM_payname: // 付款方式名称
					SalePayDef pay = (SalePayDef) salepay.elementAt(index);
					if (pay.paycode.equals("0111"))
                	{
                		if (pay.ybje > 0)
                		{
                			line = pay.payname + "消费";
                		}
                		else if (pay.ybje < 0)
                		{
                			line = pay.payname + "存入";
                		}
                		
                	}
					else
					{
						line = pay.payname;
					}
					

					break;

				case SBM_ybje: // 付款方式金额:零钞转存金额都为正
					SalePayDef pay1 = (SalePayDef) salepay.elementAt(index);
					double je = pay1.ybje * SellType.SELLSIGN(salehead.djlb);
					if (pay1.paycode.equals("0111"))
                	{
                		if (pay1.ybje > 0)
                		{
                			//line = ManipulatePrecision.doubleToString(pay1.ybje * SellType.SELLSIGN(salehead.djlb));
                		}
                		else if (pay1.ybje < 0)
                		{
                			//line = ManipulatePrecision.doubleToString(-1 * pay1.ybje * SellType.SELLSIGN(salehead.djlb));
                			je = -1*je;
                		}
                		
                	}
					line = ManipulatePrecision.doubleToString(je);
					

					break;
				
					
				default:
					return super.getItemDataString(item, index);
					  
				
			}
		}

		if ((line != null) && line.equals("&!"))
		{
			line = null;
		}

		// if (line != null && Integer.parseInt(item.code) != 0 && item.text !=
		// null && !item.text.trim().equals(""))
		if ((line != null) && (Integer.parseInt(item.code) != 0) && (text != null) && !text.trim().equals(""))
		{
			// line = item.text + line;
			int maxline = item.length - Convert.countLength(text);
			
			line = text + Convert.appendStringSize("", line, 0, maxline, maxline, item.alignment);
		}

		return line;
	}


	// 通过模版来打印卡联
	public void printMZKBillPrintMode()
	{
		boolean bool = false;

		if (GlobalInfo.sysPara.mzkbillnum <= 0)
		{
			return;
		}

		if ((GlobalInfo.sysPara.printpaymode == null) || GlobalInfo.sysPara.printpaymode.equals(""))
		{
			return;
		}

		CardSaleBillMode.getDefault().setTemplateObject(salehead, salegoods, originalsalepay);

		String[] printpaymode = GlobalInfo.sysPara.printpaymode.split("\\|");

		for (int i = 0; i < printpaymode.length; i++)
		{
			CardSaleBillMode.getDefault().setPayCodes(printpaymode[i]);

			for (int j = 0; j < originalsalepay.size(); j++)
			{
				SalePayDef pay = (SalePayDef) originalsalepay.elementAt(j);
				PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

				if (!CardSaleBillMode.getDefault().isExistPaycode(mode.code))
				{
					continue;
				}

				bool = true;

				CardSaleBillMode.getDefault().setPayName(mode.name);
				
				String printType = null;
				if (pay.paycode.equals("0111"))
            	{
            		if (pay.ybje > 0)
            		{
            			printType = "（消费）";
            		}
            		else if (pay.ybje < 0)
            		{
            			printType = "（存入）";
            		}
            		
            	}
				
				//零钞转存客户化打印
				((Bjcx_CardSaleBillMode)CardSaleBillMode.getDefault()).setPrintType(printType);

				break;
			}

			for (int n = 0; (n < GlobalInfo.sysPara.mzkbillnum) && bool; n++)
			{
				CardSaleBillMode.getDefault().printBill();
			}

			bool = false;
		}
	}

	
	protected void printSellBill()
	{
		// GlobalInfo.sysPara.fdprintyyy =
		// (N-不打营业员联但打印小票联/Y-打印营业员联也打印小票/A-打印营业员联但不打印小票)
		// 非超市小票且系统参数定义只打印营业员分单，则不打印机制小票
		// 红冲要打印小票联，但不打印营业联
		if (!SellType.ISHC(salehead_temp.djlb))
		{
			if (!((GlobalInfo.syjDef.issryyy == 'N') || (GlobalInfo.syjDef.issryyy == 'A' && ((SaleGoodsDef) salegoods.elementAt(0)).yyyh.equals("超市"))) && (GlobalInfo.sysPara.fdprintyyy == 'A'))
			{
				return;
			}
		}

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
		printBottom();

		// 切纸
		printCutPaper();
	}
	
	public void printYyyBillPrintMode()
	{
		//红冲不打印营业员联,只打小票联
		if (SellType.ISHC(salehead_temp.djlb))
		{
			return;
		}
		
		super.printYyyBillPrintMode();
	}
	
	public void printYYYBill()
	{
		//红冲不打印营业员联,只打小票联
		if (SellType.ISHC(salehead_temp.djlb))
		{
			return;
		}
		super.printYYYBill();
	}

	public void printBill()
	{
		int i = GlobalInfo.sysPara.salebillnum;//存储原打印份数
		try
		{

			if (GlobalInfo.syjDef.issryyy == 'N' && GlobalInfo.sysPara.salebillnum > 1 && SellType.isGroupbuy(GlobalInfo.saleform.sale.saleBS.saletype)==false)
			{
				GlobalInfo.sysPara.salebillnum = 1;//小菜邮件新需求：同门店的款机，一层超市要求打一联，二至五层百货每次打印两联。打印联数的参数不适用
			}
			
			if (SellType.isGroupbuy(GlobalInfo.saleform.sale.saleBS.saletype))
			{
				//团购模式下：
				//			城乡大厦的机关是超市模式，要打印两联
				//			小南庄的机关是百货模式，要打印一联
				if (GlobalInfo.syjDef.issryyy == 'N' || GlobalInfo.saleform.sale.yyyh.equals("超市"))
				{
					GlobalInfo.sysPara.salebillnum = 2;//打印两联小票
				}
				else
				{
					GlobalInfo.sysPara.salebillnum = 1;//打印一联小票
				}
				
			}
			
			super.printBill();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally
		{
			GlobalInfo.sysPara.salebillnum = i;//还原打印份数
		}
	}
	
	//打印指定字符串
	public void printOneLine(String str)
	{
		try
		{
			printSetPage();
			
			this.printLine(str);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
