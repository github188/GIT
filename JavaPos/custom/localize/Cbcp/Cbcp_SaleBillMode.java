package custom.localize.Cbcp;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.CalcRulePopDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;

import custom.localize.Bcrm.Bcrm_SaleBillMode;

public class Cbcp_SaleBillMode extends Bcrm_SaleBillMode
{

	protected final static int SBM_ewm = 202;//二维码打印
	
	protected String getItemDataString(PrintTemplateItem item, int index)
	{
		String line = null;

		line = extendCase(item, index);

		String text = item.text;

		if (line == null)
		{
			switch (Integer.parseInt(item.code))
			{
			
				case SBM_cjje: // 成交金额
					line = ManipulatePrecision.doubleToString((((SaleGoodsDef) salegoods.elementAt(index)).hjje - ((SaleGoodsDef) salegoods.elementAt(index)).hjzk) * SellType.SELLSIGN(salehead.djlb));

					if(((SaleGoodsDef) salegoods.elementAt(index)).str13 != null && ((SaleGoodsDef) salegoods.elementAt(index)).str13.equals("T") && SellType.HH_SALE.equals(salehead.djlb))
					{
						line = ManipulatePrecision.doubleToString(-1*(((SaleGoodsDef) salegoods.elementAt(index)).hjje - ((SaleGoodsDef) salegoods.elementAt(index)).hjzk));
					}
					else if(((SaleGoodsDef) salegoods.elementAt(index)).str13 != null && ((SaleGoodsDef) salegoods.elementAt(index)).str13.equals("S") && SellType.HH_BACK.equals(salehead.djlb))
					{
						line = ManipulatePrecision.doubleToString(1*(((SaleGoodsDef) salegoods.elementAt(index)).hjje - ((SaleGoodsDef) salegoods.elementAt(index)).hjzk));
					}

				break;
			
				case SBM_Aqje: // 打印返券信息
					line = "";					
					if(salehead.memo!=null && salehead.memo.length()>0)
					{
						String[] memo = salehead.memo.split("\n");
						for(int i=0; i<memo.length; i++)
						{
							if(memo[i].length()>0) printLine(memo[i]);
						}
					}
					break;
					
				case SBM_ewm: // 二维码打印
					
//					new MessageBox("fphm："+String.valueOf(salehead.fphm));
					line = "#Qrcode:"+salehead.mkt+"-"+GlobalInfo.syjDef.syjh+"-"+String.valueOf(salehead.fphm);
					
					break;
					
				case SBM_printinfo1: // 自定义打印信息
				case SBM_printinfo2: // 自定义打印信息
				{
					  String printInfo = null;

						printInfo = GlobalInfo.sysPara.printInfo1+GlobalInfo.sysPara.printInfo2;

						if ((printInfo == null) || printInfo.trim().equals(""))
						{
							printLine("");
						}
						else
						{
							String[] l = printInfo.split(";");
							
							for (int i = 0; i < l.length; i++)
							{
								
								printLine(l[i].trim() + "\n");
								
							}
						}
					break;
				}
				
				
					
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
	
	
	public void printDetail()
	{
		int i = 0;
		int j = 0;
		Vector set = null;
		CalcRulePopDef calPop = null;
		SaleGoodsDef sgd = null;
		//String line;

		// 先把商品进行分组
		set = new Vector();

		for (i = 0; i < salegoods.size(); i++)
		{
			sgd = (SaleGoodsDef) salegoods.elementAt(i);

			// 按营业员柜组分组
			for (j = 0; j < set.size(); j++)
			{
				calPop = (CalcRulePopDef) set.elementAt(j);

				if (calPop.code.equals(sgd.yyyh))
				{
					calPop.row_set.add(String.valueOf(i));

					break;
				}
			}

			//若没有找到,则添加一条新的
			if (j >= set.size())
			{
				calPop = new CalcRulePopDef();
				calPop.code = sgd.yyyh;
				//calPop.gz = sgd.gz;
				calPop.row_set = new Vector();
				calPop.row_set.add(String.valueOf(i));//商品行号
				set.add(calPop);
			}
		}
				

		// 设置打印区域
		setPrintArea("Detail");
		
		// 按分组进行分单打印(循环打印商品明细)
		for (i = 0; i < set.size(); i++)
		{
			calPop = (CalcRulePopDef) set.elementAt(i);

			/*if (new MessageBox("请将营业员(" + calPop.code + ")的销售单放入打印机\n\n按‘回车’键后开始打印\n按‘退出’键则跳过打印").verify() == GlobalVar.Exit)
			{
				continue;
			}

			Printer.getDefault().startPrint_Slip();

			Printer.getDefault().printLine_Slip("时间:" + salehead.rqsj + " NO." + salehead.syjh + "-" + salehead.fphm);
			Printer.getDefault().printLine_Slip("收银员:" + salehead.syyh + "          " + SellType.getDefault().typeExchange(salehead.djlb, salehead.hhflag, salehead));

			if (salehead.printnum > 0)
			{
				Printer.getDefault().printLine_Slip("---------------重打印-----------------");
			}
			else
			{
				Printer.getDefault().printLine_Slip("--------------------------------------");
			}
*/
			
			double hjje = 0;
			double hjzk = 0;
			double hhje = 0;

			//打印营业员头
			printLine("营业员: " + calPop.code + " 票号：" + salehead.fphm + " 机号：" + salehead.syjh + "\n");//打印:营业员: 9653

			int goodsIndex = -1;//当前商品行号
			for (j = 0; j < calPop.row_set.size(); j++)
			{
				goodsIndex = Integer.parseInt((String) calPop.row_set.elementAt(j));
				sgd = (SaleGoodsDef) salegoods.elementAt(goodsIndex);

				// 赠品商品不打印
				if (sgd.flag == '1')
				{
					continue;
				}
				
				/*line = Convert.appendStringSize("", sgd.barcode, 0, 10, Width);
				line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(sgd.sl, 4, 1, true), 12, 4, Width, 1);
				line = Convert.appendStringSize(line, " x ", 16, 3, Width);
				line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(sgd.jg), 19, 8, Width);
				line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(sgd.hjje), 28, 9, Width, 1);

				Printer.getDefault().printLine_Slip(line);
				Printer.getDefault().printLine_Slip(sgd.name);*/
				if(sgd.str13 != null && sgd.str13.equals("T"))
				{
					hhje += sgd.hjje;
				}
				else
				{
					hjje += sgd.hjje;
				}
				hjzk += sgd.hjzk;
				//sgd.rowno = j;//以新序号为准,否则乱了
				printVector(getCollectDataString(Detail, goodsIndex, Width));
			}
			
			
			if(salehead.djlb.equals(SellType.HH_BACK))hjje = hhje-hjje;
			if(salehead.djlb.equals(SellType.HH_SALE))hjje = hjje -hhje;
			
			//打印营业员尾
			printLine("小计: " + ManipulatePrecision.doubleToString(hjje * SellType.SELLSIGN(salehead.djlb)) + "\n");//打印:小计:          10.00

			/*Printer.getDefault().printLine_Slip("--------------------------------------");
			Printer.getDefault().printLine_Slip("营业员:" + Convert.appendStringSize("", calPop.code, 0, 10, 10) + "        柜组:" + Convert.appendStringSize("", calPop.gz, 0, 10, 10));
			Printer.getDefault().printLine_Slip("总小计:" + Convert.appendStringSize("", ManipulatePrecision.doubleToString(hjje), 0, 10, 10) + "        折扣:" + Convert.appendStringSize("", ManipulatePrecision.doubleToString(hjzk), 0, 10, 10));

			Printer.getDefault().cutPaper_Slip();*/
			//多个营业员时，中间空几行 wangyong by 2014.6.23 for dong
			if( set.size()>1 
					&& i>=0 
					&& ((i+1) < set.size()) )
			{
				printLine("");
				printLine(Convert.increaseChar("",'=',Width));
				printLine("");
			}
		}
				
	}
	
	public void printBill()
	{
		super.printBill();
		
		
		if(GlobalInfo.sysPara.istcl == 'Y')
		{
			String line = "";  
			printLine("------------------------------");
			line = Convert.appendStringSize(line,"交易时间："+salehead.rqsj, 0, 32, 32);
			printLine(line);
			line = Convert.appendStringSize(line,"小票号:"+ String.valueOf(salehead.fphm),0, 32, 32);
			printLine(line);
			line = Convert.appendStringSize(line,"收银机:"+GlobalInfo.syjDef.syjh, 0, 32,32);
			printLine(line);
			line = Convert.appendStringSize(line,"收银员:"+GlobalInfo.posLogin.gh, 0, 32,32);
			printLine(line);
			line = Convert.appendStringSize(line,"交易类型:"+String.valueOf(SellType.getDefault().typeExchange(salehead.djlb, salehead.hhflag, salehead)),0,32, 32);
			printLine(line);
			if (salehead.printnum > 0)
			{
				printLine(Language.apply("**重打印**"));
			}
			line = Convert.appendStringSize(line,"件数:"+ManipulatePrecision.doubleToString(salehead.hjzsl * SellType.SELLSIGN(salehead.djlb)), 0, 32,32);
			printLine(line);
			line = Convert.appendStringSize(line,"实收:"+ManipulatePrecision.doubleToString(salehead.sjfk * SellType.SELLSIGN(salehead.djlb)),0,32, 32);
			printLine(line);
			printLine("");
			printLine("");
			printLine("");
		}
		
		/*if(GlobalInfo.sysPara.iser == 'Y')
		{
			String line = "";
			line = Convert.appendStringSize(line,"#Qrcode:"+salehead.mkt+"-"+GlobalInfo.syjDef.syjh+"-"+String.valueOf(salehead.fphm), 0, 32, 32);
			printLine(line);
		}*/
	}
}
