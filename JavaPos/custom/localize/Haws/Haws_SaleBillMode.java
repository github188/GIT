package custom.localize.Haws;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Struct.SaleGoodsDef;

import custom.localize.Bcrm.Bcrm_SaleBillMode;

public class Haws_SaleBillMode extends Bcrm_SaleBillMode {
	public static boolean isPrize = false;
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
		
		for(int i = 0;i<salegoods.size();i++){
			SaleGoodsDef sgd = (SaleGoodsDef)salegoods.elementAt(i);
			if(sgd.name.indexOf("(")==0){
				if(sgd.name.substring(0, 3).equalsIgnoreCase("(Z)")){
					isPrize = true;
					break;
				}else if(sgd.name.length()>3&&sgd.name.substring(0, 1).equalsIgnoreCase("(")&&sgd.name.substring(3, 4).equalsIgnoreCase(")")&&sgd.name.substring(1, 3).indexOf("Z")!=-1){
					isPrize = true;
					break;
				}else if(sgd.name.length()>4&&sgd.name.substring(0, 1).equalsIgnoreCase("(")&&sgd.name.substring(4, 5).equalsIgnoreCase(")")&&sgd.name.substring(1, 4).indexOf("Z")!=-1){
					isPrize = true;
					break;
				}
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

        // 打印赠品联
        printGift();

        // 切纸
        printCutPaper();
		}catch(Exception er)
		{
			er.printStackTrace();
		}
    }
	
	 public void printAppendBill()
	    {
	    	super.printAppendBill();
	    	
	    	printerGift();
	    	
	    	if(isPrize&&salehead.djlb.equals("1") && salehead.printnum <1){
	    		if(salehead.printnum <1){
	    			printerPrize();
		    		isPrize = false;
	    		}else if(GlobalInfo.sysPara.isprintdjq.equals("Y")){
	    			printerPrize();
		    		isPrize = false;
	    		}
	    	}
	    }

	private void printerPrize()
	{
		String memo = "";
		if ((new File(GlobalVar.ConfigPath + "//DJQmemo.ini").exists())){
			BufferedReader br;
			br = CommonMethod.readFile(GlobalVar.ConfigPath + "/DJQmemo.ini");
			if (br != null){
				String line = "";
				
				try
				{
					while ((line = br.readLine()) != null)
					{
						if ((line == null) || (line.length() <= 0))
						{
							continue;
						}
						memo = memo+line+"\n";
					}
				}
				catch (IOException e)
				{
					// TODO 自动生成 catch 块
					e.printStackTrace();
				}
			}
			
		}
			

		

/**
 *  兑    奖    券

  NO: 00000035   2012.05.01   12.51.21     
-------------------------------------------------------
商品名称   数量   单价     成交价
000188      1     288       239      
七匹狼
000199      1     199       169
杉杉
合计：                      408
---------------------------------------------------------
收银机号：0011    收银员：2007

凭此联兑奖，兑奖完毕此联收回



注： 以上所现实的商品为活动期间参加赠送礼品活动的商品名称及商品编
case SBM_sl: // 数量
					line = ManipulatePrecision.doubleToString(((SaleGoodsDef) salegoods.elementAt(index)).sl * SellType.SELLSIGN(salehead.djlb), 4, 1, true);

					break;

				case SBM_jg: // 售价
					line = ManipulatePrecision.doubleToString(((SaleGoodsDef) salegoods.elementAt(index)).jg);

					break;

				case SBM_sjje: // 售价金额（数量*售价）
					line = ManipulatePrecision.doubleToString(((SaleGoodsDef) salegoods.elementAt(index)).hjje * SellType.SELLSIGN(salehead.djlb));

					break;

 */
		

		try
		{
			    printLine("            兑    奖    券            ");
			    printLine("NO: "+ Convert.increaseLong(salehead.fphm, 8)+"   "+ salehead.rqsj);
			    printLine("-------------------------------------");
			    printLine("商品名称   数量   单价     成交价");
			    String lineStr = "";
			    
			    double hj = 0.0;
			    for(int j = 0;j<salegoods.size();j++){
			    	SaleGoodsDef sgd = (SaleGoodsDef)salegoods.elementAt(j);
			    	if(sgd.name.indexOf("(")==0){
			    		if((sgd.name.substring(0, 3).equalsIgnoreCase("(z)"))||(sgd.name.substring(0, 1).equalsIgnoreCase("(")&&sgd.name.substring(3, 4).equalsIgnoreCase(")")&&sgd.name.substring(1, 3).indexOf("Z")!=-1)||(sgd.name.substring(0, 1).equalsIgnoreCase("(")&&sgd.name.substring(4, 5).equalsIgnoreCase(")")&&sgd.name.substring(1, 4).indexOf("Z")!=-1)){
							double cjj = sgd.hjje-sgd.hjzk;
				    		lineStr = sgd.code+"   "+ManipulatePrecision.doubleToString(sgd.sl , 4, 1, true)+"  "+ManipulatePrecision.doubleToString(sgd.jg)+"    "+ManipulatePrecision.doubleToString(sgd.hjje-sgd.hjzk);
							printLine(lineStr);
							printLine(sgd.name);
							hj = hj + cjj;
				    	}
			    	}
			    	
			    }
			    printLine("合计:                   "+hj);
			    printLine("-------------------------------------");
			    printLine("收银机号:" + salehead.syjh + "     收银员号:" + salehead.syyh);
			    printLine("    ");
			    printLine("凭此联兑奖，兑奖完毕此联收回,退换货时,领取");
			    printLine("的礼品应退回或补偿等价现金    ");
			    printLine(memo);
			    printLine("注： 以上所显示的商品为活动期间参加赠送礼品");	
			    printLine("活动的商品名称及商品编码");	
			    printLine("    ");
			    Printer.getDefault().cutPaper_Normal();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	
	}

}
