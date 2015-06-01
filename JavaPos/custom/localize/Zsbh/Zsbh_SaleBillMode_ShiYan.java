/**
 * 
 */
package custom.localize.Zsbh;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;

/**
 * @author wangyong
 * 十堰店客户化打印
 */
public class Zsbh_SaleBillMode_ShiYan extends Zsbh_SaleBillMode
{	
	protected String getItemDataString(PrintTemplateItem item, int index)
	{
		String line = null;

		line = extendCase(item, index);

		String text = item.text;

		if (line == null)
		{
			switch (Integer.parseInt(item.code))
			{			
				case 101://填充商品行数
					line = null;
					text = "";
					int detailLine = getDetailLine();
					
					//是否给签购单留空行
					if (detailLine >= 1)
					{
						detailLine = detailLine - 1;
					}
					
					//给小票与签购单之间留的空行数(最多2行)
					/*if (detailLine < 2)
					{
						if (detailLine <= 0)
						{
							break;
						}
						else if (detailLine == 1)
						{
							detailLine = detailLine -1;
						}
					}
					else
					{
						detailLine = detailLine - 2;
					}*/
					
					//打印商品区域的空行数
					for (int n=1; n<=detailLine; n++)
					{
						printLine(" \n");
					}
										
					break;
				case 102: // 客户化:无会员卡时,写营业员
					//打印格式为:会员卡号:   123456789012/营业员号:   123456789

					text = "";
					if ((salehead.hykh == null) || (salehead.hykh.trim().length() <= 0))
					{
						//无会员卡时打营业员号
						line = "营业员号:" + String.valueOf(((SaleGoodsDef) salegoods.elementAt(0)).yyyh);
					}
					else
					{
						line = "会员卡号:" + salehead.hykh;
					}

					line = appendStringSize(line,item);

					break;
				case 103://打印面值卡付款信息 
					//打印格式为:面值卡1234567890123456张数1金额123.00余额110.00
					//所有面值卡合计为打一次
					text = "";
					try
					{
						line = null;
						if (((SalePayDef) salepay.elementAt(index)).paycode.equals(_mzkPayCode))//当前为面值卡付款
						{
							SalePayDef spd = null;
							for (int i = 0; i < salepay.size(); i++)
							{
								spd = (SalePayDef) salepay.elementAt(i);
								if (spd.paycode.equals(_mzkPayCode))
								{
									if (index > i) 
									{
										line = null;
									}
									else
									{
										int mzkCount = 0;		//面值卡消费总张数
										double mzkTotalJe = 0;	//面值卡消费总金额
										double mzkYe = 0; 		//余额
										String payNo = "";
										SalePayDef pay = null;
										
										for (int j = 0; j < salepay.size(); j++)
										{
											pay = (SalePayDef) salepay.elementAt(j);
											if (pay.paycode.equals(spd.paycode))
											{
												mzkCount++;
												mzkTotalJe += pay.ybje * pay.hl;
												if (mzkCount == 1)
												{
													mzkYe = pay.kye;
													payNo = pay.payno;
												}
												if (mzkCount > 1 && pay.kye > 0)
												{
													mzkYe = pay.kye;
													payNo = pay.payno;
												}												
											}
										}
										if (mzkCount > 0)
										{
											//面值卡  1234567890123456  总张数:1  总金额100.00
											line = appendStringSize(spd.payname + " " + payNo + " 总张数:" + String.valueOf(mzkCount) + " 总金额:" + ManipulatePrecision.doubleToString(mzkTotalJe) + " 余额:" + ManipulatePrecision.doubleToString(mzkYe),item);
											
										}
									}
									break;//找到一个就退出循环
								}
							}
						}
							
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}	
					
					break;	
				case 113://打印工贸卡付款信息 
					//打印格式为:工贸卡1234567890123456张数1金额123.00余额110.00
					//所有工贸卡合计为打一次
					text = "";
					try
					{
						line = null;
						if (((SalePayDef) salepay.elementAt(index)).paycode.equals(this._gmkPayCode))//当前为面值卡付款
						{
							SalePayDef spd = null;
							for (int i = 0; i < salepay.size(); i++)
							{
								spd = (SalePayDef) salepay.elementAt(i);
								if (spd.paycode.equals(this._gmkPayCode))
								{
									if (index > i) 
									{
										line = null;
									}
									else
									{
										int mzkCount = 0;		//工贸卡消费总张数
										double mzkTotalJe = 0;	//工贸卡消费总金额
										double mzkYe = 0; 		//余额
										String payNo = "";
										SalePayDef pay = null;
										
										for (int j = 0; j < salepay.size(); j++)
										{
											pay = (SalePayDef) salepay.elementAt(j);
											if (pay.paycode.equals(spd.paycode))
											{
												mzkCount++;
												mzkTotalJe += pay.ybje * pay.hl;
												if (mzkCount == 1)
												{
													mzkYe = pay.kye;
													payNo = pay.payno;
												}
												if (mzkCount > 1 && pay.kye > 0)
												{
													mzkYe = pay.kye;
													payNo = pay.payno;
												}												
											}
										}
										if (mzkCount > 0)
										{
											//工贸卡  1234567890123456  总张数:1  总金额100.00
											if (mzkYe < 0)
											{
												line = appendStringSize(spd.payname + " " + payNo + " 总张数:" + String.valueOf(mzkCount) + " 总金额:" + ManipulatePrecision.doubleToString(mzkTotalJe),item);
											}
											else
											{
												line = appendStringSize(spd.payname + " " + payNo + " 总张数:" + String.valueOf(mzkCount) + " 总金额:" + ManipulatePrecision.doubleToString(mzkTotalJe) + " 余额:" + ManipulatePrecision.doubleToString(mzkYe),item);
											}
											
											
										}
									}
									break;//找到一个就退出循环
								}
							}
						}
							
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}	
					
					break;	
						
				case 104://打印银行卡付款信息
					//打印格式为:银行卡1234567890123456流水155568授权155568 12200.2
					//一张卡打印一行
					text = "";
					SalePayDef sp= ((SalePayDef) salepay.elementAt(index));
					if (sp.paycode.equals(_bankPayCode))
					{
						String sq = "";//授权
						/*if (sp.memo.trim().equals("000000") || sp.memo.trim().length() < 1)
						{
							sq = "";
						}
						else
						{
							sq = "授权" + String.valueOf(sp.memo);
						}*/
						sq = "授权" + String.valueOf(sp.memo);
						line = appendStringSize(sp.payname +  sp.payno + "流水" + Convert.increaseCharForward(String.valueOf(sp.batch),'0', 6) + sq + " " + ManipulatePrecision.doubleToString(sp.je * sp.hl),item);
						 
					}
					else
					{
						line = null;
					}
					
					break;
				
				/*case 105://打印非MZK/BANK/SP的付款信息
					//打印格式为:人民币                1.00
					text = "";
					SalePayDef pay= ((SalePayDef) salepay.elementAt(index));
					if (pay.paycode.equals("04") || pay.paycode.equals("0317") || pay.paycode.equals("0721"))// || pay.paycode.equals("05") || pay.paycode.equals("52")
					{
						line = null;
					}
					else
					{
						line = appendStringSize(pay.payname + " " + ManipulatePrecision.doubleToString(pay.je * pay.hl),item);
					}
					
					break;*/
				case 106://打印BANK签购单Header
					//打印格式为:\r\n机号:1129小票号:9998081营业员号:6251收银员号:6251
					//只打一次
					text = "";
					line = null ;
					/*boolean isHaveHeader = false;
					int detailLine_Header = this.getDetailLine();
					//给小票与签购单之间留的空行数(最多2行)
					if (detailLine_Header < 2)
					{
						if (detailLine_Header <= 0)
						{
							detailLine_Header = 0;
						}
						else if (detailLine_Header == 1)
						{
							detailLine_Header = 1;
						}
					}
					else
					{
						detailLine_Header = 2;
					}*/
					
					for (int i = 0; i < salepay.size(); i++)
					{
						SalePayDef p= ((SalePayDef) salepay.elementAt(i));
						if (p.paycode.equals(_bankPayCode))//只打银行卡的,其它不打  p.paycode.equals("04") || p.paycode.equals("0721") ||
						{												
							line = "";							
							/*isHaveHeader = true;
							//打印小票与签购单之间的空行
							for (int k=1; k<= detailLine_Header; k++)
							{
								printLine(" \n");
								//line += k + "\n";
							}*/
							
							line += "机号:" + salehead.syjh + "小票号:" + String.valueOf(salehead.fphm) + 
									"营业员号:" + String.valueOf(((SaleGoodsDef) salegoods.elementAt(0)).yyyh) + 
									"收银员号:" + salehead.syyh;
							line = appendStringSize(line ,item);
							
							break;
						}						
					}
					
					/*if (isHaveHeader == false)
					{
						//line = "";
						for (int k=1; k<= detailLine_Header; k++)
						{
							printLine(" \n");							
							//line += k + "\n";
						}
					}*/
					break;
				case 112://签购单之间的间隔
					text = "";
					line = null;
					if (this.getDetailLine() >= 1)
					{
						line = item.text;
						line = appendStringSize(line ,item);
					}
				break;
				
				default:
					return super.getItemDataString(item, index);
			}
		}

		if ((line != null) && line.equals("&!"))
		{
			line = null;
		}

		// if (line != null && Integer.parseInt(item.code) != 0 && item.text != null && !item.text.trim().equals(""))
		if ((line != null) && (Integer.parseInt(item.code) != 0) && (text != null) && !text.trim().equals(""))
		{
			// line = item.text + line;
			int maxline = item.length - Convert.countLength(text);
			line = text + Convert.appendStringSize("", line, 0, maxline, maxline, item.alignment);
		}

		return line;
	}
	
	public void printBill()
	{		
		if (ConfigClass.CustomItem5.split("\\|").length >= 4 
				&& ConfigClass.CustomItem5.split("\\|")[3].trim().equalsIgnoreCase("Y")) 
		{
			
		}
		else
		{

			if (CheckTH())
			{
				//boolean isExistsBankPay = false;
				// 在原始付款清单中,查找是否有银联卡付款方式
				for (int i = 0; i < originalsalepay.size(); i++)
				{
					SalePayDef pay = (SalePayDef) originalsalepay.elementAt(i);
					PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

					//退货时,只打银行卡签购单,不打POS小票
					if (SellType.ISBACK(salehead.djlb) && (mode.code.equals(_bankPayCode)) && (pay.batch != null) && (pay.batch.length() > 0))//mode.isbank == 'Y
					{					
						//isExistsBankPay = true;
						printSetPage();
						// 设置打印区域(非head即可):将打印内容打到空白区域
						setPrintArea("Total");
						break;
					}
				}
				//if (!isExistsBankPay) return;
				/*if (isExistsBankPay)
				{
					printBankBillEx(true);				
				}*/
			}
		}
		
		
		
		super.printBill();
		
		if (ConfigClass.CustomItem5.split("\\|").length >= 4 
				&& !ConfigClass.CustomItem5.split("\\|")[3].trim().equalsIgnoreCase("Y")) 
		{
			//只有新银联老打印或老接口时（即自己生成签购单时），才切小票
			if (CheckTH() == false) printCutPaper();
		}
		
	}
	
	protected void printSellBill()
	{
		if (CheckTH()) return;	
				
		// GlobalInfo.sysPara.fdprintyyy = (N-不打营业员联但打印小票联/Y-打印营业员联也打印小票/A-打印营业员联但不打印小票)
    	// 非超市小票且系统参数定义只打印营业员分单，则不打印机制小票
		if (!(
			(GlobalInfo.syjDef.issryyy == 'N') || 
		    (GlobalInfo.syjDef.issryyy == 'A' && ((SaleGoodsDef)salegoods.elementAt(0)).yyyh.equals("超市"))) &&
		    (GlobalInfo.sysPara.fdprintyyy == 'A')
			)
    	{
    		return;
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
		//if (ConfigClass.RepPrintTrack != 3)
		//{
		if (ConfigClass.CustomItem5.split("\\|").length >= 4 
				&& ConfigClass.CustomItem5.split("\\|")[3].trim().equalsIgnoreCase("Y")) 
		{
			//只有新银联签购单立即打印时，才切小票
			printCutPaper();
		}
			
		//}
		
	}
	
	public void printBankBill()
	{
		//super.printBankBill();

		//若为新银联接口，则跳出（新接口是先打印，旧接口是后打印）
		if (ConfigClass.CustomItem5.split("\\|").length >= 4 
				&& ConfigClass.CustomItem5.split("\\|")[3].trim().equalsIgnoreCase("Y")) return;		
			
		boolean isExistsBank = false;
		// 在原始付款清单中,查找是否有银联卡付款方式
		for (int i = 0; i < originalsalepay.size(); i++)
		{
			SalePayDef pay = (SalePayDef) originalsalepay.elementAt(i);
			PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

			if ((mode.code.equals(_bankPayCode)) && (pay.batch != null) && (pay.batch.length() > 0))//mode.isbank == 'Y
			{
				/*if (isExistsBank==false)
				{
					printLine("机号:" + salehead.syjh + "小票号:" + String.valueOf(salehead.fphm) + "收银员号:" + salehead.syyh);
				}*/
				isExistsBank=true;
				Zsbh_PaymentBank.printXYKDoc("Bankdoc_" + salehead.syjh + "_" + salehead.fphm + "_" + pay.batch + ".txt",false);
			}
		}
		if (isExistsBank && CheckTH())
		{
	
			printCutPaper();
				
			
		}
		
	}
	
	private boolean CheckTH()
	{
		//GlobalInfo.sysPara.printInBill
		//当为 N 时,退货与红冲都不打印小票
		//当为 A 时,退货不打印小票
		//当为 B 时,红冲不打印小票
		if ((SellType.ISBACK(salehead.djlb) || SellType.ISHC(salehead.djlb)) && (GlobalInfo.sysPara.printInBill == 'N')
				|| (SellType.ISBACK(salehead.djlb) && GlobalInfo.sysPara.printInBill == 'A')
				|| (SellType.ISHC(salehead.djlb)) && GlobalInfo.sysPara.printInBill == 'B')
		{
			return true;
		}
		return false;
	}
	
		
	protected void setPrintArea(String area)
	{   
		int startRow,endRow;
		
		// 非套打，打印区域为整页
		if (AreaPrint != 1)
		{
			//old
			//startRow = Area_PageHead + 1;
			//endRow 	 = Area_Bottom;
			
			if (area.equalsIgnoreCase("Header"))
			{
				startRow = Area_PageHead + 1;
				endRow 	 = Area_Bottom;//Area_PageFeet;				
			}
			else
			{
				//此模板中的Area_Header表示从内容打印区域行开始打
				startRow = Area_Header + 1;
				endRow 	 = Area_Bottom;//Area_PageFeet;//Area_Bottom;
			}
		}
		else if (area.equalsIgnoreCase("Header"))
	    {
	    	startRow = Area_PageHead + 1;
	    	endRow 	 = Area_Header ;
	    }
	    else if (area.equalsIgnoreCase("Detail"))
	    {
	    	startRow = Area_Header + 1;
	    	endRow 	 = Area_Detail ;
	    }
	    else if (area.equalsIgnoreCase("Total"))
	    {
	    	startRow = Area_Detail + 1;
	    	endRow 	 = Area_Total ;
	    }
	    else if (area.equalsIgnoreCase("Pay"))
	    {
	    	startRow = Area_Total + 1;
	    	endRow 	 = Area_Pay ;
	    }
	    else if (area.equalsIgnoreCase("Bottom"))
	    {
	    	startRow = Area_Pay + 1;
	    	endRow 	 = Area_Bottom;
	    }/*
	    else if (area.equalsIgnoreCase("Memo"))
	    {
	    	startRow = Area_Pay + 1;
	    	endRow 	 = Area_Header;
	    }*/
	    else
	    {
			startRow = Area_PageHead + 1;
			endRow 	 = Area_Bottom;
	    }
		
		// 设置打印区域
		printArea(startRow,endRow);
	}

	//获取商品区域的空行数
	protected int getDetailLine()
	{
		int detailLine = 0;
		
		//商品区域总行数 = 总行数-银行卡签够单行数-付款信息行数-实际付款找零等行数-重印提示行数(正常就是0)
		//商品区域空行数 = 商品区域总行数 - 实际商品行数 - X
		detailLine = this.Area_Bottom - this.Area_Header - this.Area_Total;// - this.Area_Bottom;
		boolean isexistsBankPay = false;
		for (int i = 0; i < salepay.size(); i++)
		{
			//计算实际付款行数及银行卡签购单行数(无面值卡签购单)
			if (((SalePayDef) salepay.elementAt(i)).paycode.equals(_bankPayCode))
			{
				detailLine = detailLine - 1 - 3;//其中1为付款方式行数,3为银行卡签购单行数
				isexistsBankPay = true;
			}
			else
			{
				detailLine = detailLine - 1;
			}
		}
		
		//减去签购单头行
		if (isexistsBankPay) detailLine = detailLine - 1;
		
		if (salehead.printnum > 0)
		{
			//减去重印行
			detailLine = detailLine - 1;
		}
	
		//减去实际商品行数
		detailLine = detailLine - salegoods.size();
		
		return detailLine;
		
	}
		
}
