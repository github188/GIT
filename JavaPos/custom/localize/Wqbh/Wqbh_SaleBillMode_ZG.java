package custom.localize.Wqbh;

import java.util.LinkedList;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.SaleGoodsDef;


public class Wqbh_SaleBillMode_ZG extends Wqbh_SaleBillMode
{
	protected static LinkedList saleHeadList = new LinkedList(); //记录单据号和开票类型
	protected static LinkedList dzDjList = new LinkedList(); //电子单商品
	protected static LinkedList sgDjList = new LinkedList(); //手工单商品
	protected static LinkedList fyqzDjList = new LinkedList(); //非延期提货电子单据
	
	protected void printSellBill()
    {

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
		try{
			saleHeadList = new LinkedList();
			dzDjList = new LinkedList();
			sgDjList = new LinkedList();
			fyqzDjList = new LinkedList();
			for(int i =0;i<salegoods.size();i++){
				SaleGoodsDef salegoodsdef = (SaleGoodsDef) salegoods.elementAt(i);
				if(salegoodsdef.str9 != null && !"".equals(salegoodsdef.str9))
				{
					//添加所有电子单据商品
					dzDjList.add(salegoodsdef);
					//添加非延期提货电子单据商品   ----万达百货的正常开票=延期提货，所以 1 2 都需排除
					if(!salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2).equals("2")&&!salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2).equals("1")){
						fyqzDjList.add(salegoodsdef);
					}
					
					if(saleHeadList.contains(salegoodsdef.str9+";"+salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2)))
					{
						saleHeadList.remove(salegoodsdef.str9+";"+salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2));
						//如果开单类型为延期提货(2)，放到第一位 -----更改为正常开票，放到第一位
						if(salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2).equals("1")){
							saleHeadList.addFirst(salegoodsdef.str9+";"+salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2));
						}
						else
						{
							saleHeadList.add(salegoodsdef.str9+";"+salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2));
						}
					}
					else
					{
	//					如果开单类型为延期提货(2)，放到第一位  -----更改为正常开票，放到第一位
						if(salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2).equals("1")){
							saleHeadList.addFirst(salegoodsdef.str9+";"+salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2));
						}
						else
						{
						saleHeadList.add(salegoodsdef.str9+";"+salegoodsdef.fph.substring(salegoodsdef.fph.lastIndexOf(";")+1,salegoodsdef.fph.lastIndexOf(";")+2));
						}
					}
				}
				else
				{
					//添加手工单据商品
					sgDjList.add(salegoodsdef);
				}
			}
			
			
//	        // 设置打印方式
//	        printSetPage();
//	
//	        // 多联小票打印不同抬头
//			printDifTitle();
			
			String line = null;
			if(!(SellType.ISBACK(salehead.djlb)||SellType.ISHC(salehead.djlb))){
//				延期提货单据打印   
				if(saleHeadList.size()>0){

					
					for(int i =0;i<saleHeadList.size();i++){
						//----更改为1 正常开票打印
						if(!saleHeadList.get(i).toString().substring(saleHeadList.get(i).toString().indexOf(";")+1).equals("1")){continue;}
						Printer.getDefault().printLine_Journal(Convert.appendStringSize(""," 万达购物广场", 0, 38, 38,2));
						//printLine(Convert.appendStringSize(""," 把美好打开", 0, 38, 38,2));
						Printer.getDefault().printLine_Journal("");
						
						if(GlobalInfo.sysPara.mktcode.equals("002,205")){
							line = Convert.appendStringSize("", "门店名:", 0, 7, 38);
							line = Convert.appendStringSize(line, GlobalInfo.sysPara.mktname, 8, 20, 38);
							Printer.getDefault().printLine_Journal(line);
						}
//						line = Convert.appendStringSize("", "门店号:", 0, 7, 38);
//						line = Convert.appendStringSize(line, GlobalInfo.sysPara.mktcode, 8, 7, 38);
//						printLine(line);
						line = Convert.appendStringSize("", "交易时间:",0, 9, 38);
						line = Convert.appendStringSize(line, salehead.rqsj, 10, 28, 38);
						Printer.getDefault().printLine_Journal(line);
						line = Convert.appendStringSize("", "收银机:", 0, 7, 38);
						line = Convert.appendStringSize(line, GlobalInfo.syjDef.syjh, 8, 7, 38);
						line = Convert.appendStringSize(line, "小票号:", 15, 7, 38);
						line = Convert.appendStringSize(line, String.valueOf(salehead.fphm), 23, 7, 38);
						Printer.getDefault().printLine_Journal(line);
						line = Convert.appendStringSize("", "收银员:", 0, 7, 38);
						line = Convert.appendStringSize(line, GlobalInfo.posLogin.gh, 8, 6, 38);
						line = Convert.appendStringSize(line, "交易类型:", 15, 9, 38);
						line = Convert.appendStringSize(line, SellType.getDefault().typeExchange(salehead.djlb, salehead.hhflag, salehead), 24, 10, 38);
						Printer.getDefault().printLine_Journal(line);
						//----更改为1 正常开票打印
						if(saleHeadList.get(i).toString().substring(saleHeadList.get(i).toString().indexOf(";")+1).equals("1")){
							line = Convert.appendStringSize("", "提货号:", 0, 7, 38);
							line = Convert.appendStringSize(line, String.valueOf(saleHeadList.get(i)).substring(0,saleHeadList.get(i).toString().indexOf(";")), 8, 16, 38);
							Printer.getDefault().printLine_Journal(line);
						}
						
						if (salehead.printnum > 0)
						{
							Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "  **重打" + salehead.printnum + "**", 1, 37, 38, 2));
						}
						
						Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "--------------------------------------------------", 0, 37, 38));
						Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "商品名称    数量     单价    成交价", 0, 37, 38));
						
						double ss = 0;
						double vipzk = 0;
						for(int j=0;j<dzDjList.size();j++){
							SaleGoodsDef goodsDef = (SaleGoodsDef) dzDjList.get(j);
		//					if(saleHeadList.contains(goodsDef.str9));
							if(String.valueOf(saleHeadList.get(i)).substring(0,saleHeadList.get(i).toString().indexOf(";")).equals(goodsDef.str9)){
								ss = ss+(goodsDef.hjje-goodsDef.hjzk);//实收
								vipzk = vipzk+goodsDef.hyzke;//VIP折扣
								line = Convert.appendStringSize("", goodsDef.code, 0, 13, 38);
								line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString(goodsDef.sl * SellType.SELLSIGN(salehead.djlb))), 13, 7, 38);
								line = Convert.appendStringSize(line, String.valueOf(goodsDef.lsj), 20, 8, 38);
								line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString((goodsDef.hjje-goodsDef.hjzk) * SellType.SELLSIGN(salehead.djlb))), 28, 10, 38);
								Printer.getDefault().printLine_Journal(line);
								line = Convert.appendStringSize("", goodsDef.name, 0, 18, 38);
								if(!"".equals(goodsDef.str7)){
									line = Convert.appendStringSize(line,goodsDef.str7, 19, 19, 38);
								}
								Printer.getDefault().printLine_Journal(line);
							}
							else
							{
								continue;
							}
						}
						line = Convert.appendStringSize("", " VIP折扣:", 0, 9, 38);
						line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString(vipzk * SellType.SELLSIGN(salehead.djlb))), 8, 10, 38);
						line = Convert.appendStringSize(line, " 实收:", 19, 6, 38);
						line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString(ss * SellType.SELLSIGN(salehead.djlb))), 25, 10, 38);
						Printer.getDefault().printLine_Journal(line);
						Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "--------------------------------------------------", 0, 37, 38));
						Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "凭此提货并办理退换货业务，请妥善保管", 0, 37, 38));
						//printLine(Convert.appendStringSize("", "客服电话：88198096，88198098 ", 0, 37, 38));
						//----更改为1 正常开票打印
						if(saleHeadList.get(i).toString().substring(saleHeadList.get(i).toString().indexOf(";")+1).equals("1")){
							//切纸
							Printer.getDefault().cutPaper_Journal();
						}
						else
						{
							Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "--------------------------------------------------------------------------------------------------", 0, 37, 38));

						}
					}
				
				}
					
			}
			
			/*
//			非延期提货单据打印
			if(sgDjList.size() > 0 || fyqzDjList.size() > 0)
			{
					printLine(Convert.appendStringSize(""," 卓展购物中心", 0, 38, 38,2));
					printLine(Convert.appendStringSize(""," 把美好打开", 0, 38, 38,2));
					printLine("");
					if(GlobalInfo.sysPara.mktcode.equals("002,205")){
						line = Convert.appendStringSize("", "门店名:", 0, 7, 38);
						line = Convert.appendStringSize(line, "北京店", 8, 7, 38);
						printLine(line);
					}
					line = Convert.appendStringSize("", "交易时间:",0, 9, 38);
					line = Convert.appendStringSize(line, salehead.rqsj, 10, 28, 38);
					printLine(line);
					line = Convert.appendStringSize("", "收银机:", 0, 7, 38);
					line = Convert.appendStringSize(line, GlobalInfo.syjDef.syjh, 8, 7, 38);
					line = Convert.appendStringSize(line, "小票号:", 15, 7, 38);
					line = Convert.appendStringSize(line, String.valueOf(salehead.fphm), 23, 7, 38);
					printLine(line);
					line = Convert.appendStringSize("", "收银员:", 0, 7, 38);
					line = Convert.appendStringSize(line, GlobalInfo.posLogin.gh, 8, 6, 38);
					line = Convert.appendStringSize(line, "交易类型:", 15, 9, 38);
					line = Convert.appendStringSize(line, SellType.getDefault().typeExchange(salehead.djlb, salehead.hhflag, salehead), 24, 10, 38);
					printLine(line);
					if (salehead.printnum > 0)
					{
						printLine(Convert.appendStringSize("", "  **重打" + salehead.printnum + "**", 1, 37, 38, 2));
					}
					
					printLine(Convert.appendStringSize("", "--------------------------------------------------", 0, 37, 38));
					printLine(Convert.appendStringSize("", "商品名称     数量   单价     成交价", 0, 37, 38));
					
					double ss = 0;
					double vipzk = 0;
//					非延期提货电子单打印
					if(fyqzDjList.size() > 0){
						for(int k=0;k<fyqzDjList.size();k++){
							SaleGoodsDef goodsDef = (SaleGoodsDef) fyqzDjList.get(k);
								ss = ss+(goodsDef.hjje-goodsDef.hjzk);//实收
								vipzk = vipzk+goodsDef.hyzke;//VIP折扣
								line = Convert.appendStringSize("", goodsDef.code, 0, 13, 38);
								line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString(goodsDef.sl * SellType.SELLSIGN(salehead.djlb))), 13, 7, 38);
								line = Convert.appendStringSize(line, String.valueOf(goodsDef.lsj), 20, 8, 38);
								line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString((goodsDef.hjje-goodsDef.hjzk) * SellType.SELLSIGN(salehead.djlb))), 28, 10, 38);
								printLine(line);
								line = Convert.appendStringSize("", goodsDef.name, 0, 18, 38);
								if(!"".equals(goodsDef.str7)){
									line = Convert.appendStringSize(line,goodsDef.str7, 19, 19, 38);
								}
								printLine(line);
						}
					}
						
//					手工单打印
					if(sgDjList.size() > 0){
						for(int j=0;j<sgDjList.size();j++){
							SaleGoodsDef goodsDef = (SaleGoodsDef) sgDjList.get(j);
								ss = ss+(goodsDef.hjje-goodsDef.hjzk);//实收
								vipzk = vipzk+goodsDef.hyzke;//VIP折扣
								line = Convert.appendStringSize("", goodsDef.code, 0, 13, 38);
								line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString(goodsDef.sl * SellType.SELLSIGN(salehead.djlb))), 13, 7, 38);
								line = Convert.appendStringSize(line, String.valueOf(goodsDef.lsj), 20, 8, 38);
								line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString((goodsDef.hjje-goodsDef.hjzk) * SellType.SELLSIGN(salehead.djlb))), 28, 10, 38);
								printLine(line);
								line = Convert.appendStringSize("", goodsDef.name, 0, 18, 38);
								printLine(line);
							}
					}
					
					line = Convert.appendStringSize("", " VIP折扣:", 0, 9, 38);
					line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString(vipzk * SellType.SELLSIGN(salehead.djlb))), 8, 10, 38);
					line = Convert.appendStringSize(line, " 实收:", 19, 6, 38);
					line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString(ss * SellType.SELLSIGN(salehead.djlb))), 25, 10, 38);
					printLine(line);
				
	//				 切纸
	//		        printCutPaper();
					printLine(Convert.appendStringSize("", "--------------------------------------------------------------------------------------------------", 0, 37, 38));
					
			}
				
					line = Convert.appendStringSize("", "累计", 0, 10, 38);
					printLine(line);
					
					// 打印汇总区域
			        printTotal();
			        
			        // 打印尾部区域
			        printBottom();
			        
			     if(sgDjList.size() > 0 || fyqzDjList.size() > 0)
			     {
			    	printLine(Convert.appendStringSize("", "凭以上收银联办理退换货，请妥善保管", 0, 37, 38));
					printLine(Convert.appendStringSize("", "客服电话：88198096，88198098 ", 0, 37, 38));
					
			     }
			     printLine(Convert.appendStringSize("", "=================================================", 0, 37, 38));
			     printLine(Convert.appendStringSize("","以下为您的付款明细", 0, 38, 38));
					printLine(Convert.appendStringSize("","可凭此在一个月内开具发票", 0, 38, 38));
					
//					 打印付款区域
			        printPay();
		
			        // 打印赠品联
			        printGift();
		
			        printLine(Convert.appendStringSize("", "--------------------------------------------------------------------------------------------------", 0, 37, 38));
			        
			        
					line = Convert.appendStringSize("", salehead.rqsj,0, 10, 38);
					line = Convert.appendStringSize(line,"单据号:"+GlobalInfo.syjDef.syjh+"-"+String.valueOf(salehead.fphm),11, 38, 38);
					printLine(line);
//			        printLine(Convert.appendStringSize("", "=================================================", 0, 37, 38));
					printLine(Convert.appendStringSize("", "商品编码     品名           实收金额", 0, 37, 38));
			        
			    	for(int j=0;j<salegoods.size();j++){
			    		SaleGoodsDef goodsDef = (SaleGoodsDef) salegoods.elementAt(j);
			    		line = Convert.appendStringSize("", goodsDef.code, 0, 12, 38);
						line = Convert.appendStringSize(line, String.valueOf(goodsDef.name), 12, 18, 38);
						line = Convert.appendStringSize(line, String.valueOf(ManipulatePrecision.doubleToString((goodsDef.hjje-goodsDef.hjzk) * SellType.SELLSIGN(salehead.djlb))), 30, 8, 38);
						printLine(line);
			    	}

			    	 printLine(Convert.appendStringSize("", "=================================================", 0, 37, 38));
			    	 
			    	//促销联
			    //	printPopBill();
			    	

			 		
			 		super.printBottom();
 	
				     // 切纸
				     printCutPaper();
				     */
				  /*   // 设置打印方式
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

				        // 打印赠品联
				        printGift();

				        // 切纸
				        printCutPaper();
				        */
//			 设置打印方式
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

			printAppend();

			// 打印赠品联
			printGift();

			// 打印税控信息
			//			printFaxInfo();
			// 切纸
			printCutPaper();
//			 还原默认的打印模板
			ReadTemplateFile(GlobalVar.ConfigPath + "//SalePrintMode.ini");
				        
		}catch(Exception er)
		{
			er.printStackTrace();
		}
		finally
		{
			if(saleHeadList.size()>0)
			{
				saleHeadList.clear();
			}
			if(dzDjList.size()>0)
			{
				dzDjList.clear();
			}
			if(sgDjList.size()>0)
			{
				sgDjList.clear();
			}
		}
    
    }
}
