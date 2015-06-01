package custom.localize.Hsjc;

import java.math.BigDecimal;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.SaleGoodsDef;

import custom.localize.Cmls.Cmls_SaleBillMode;


public class Hsjc_SaleBillMode extends Cmls_SaleBillMode {
	
	protected void printSellBill()
    {
		// GlobalInfo.sysPara.fdprintyyy = (N-不打营业员联但打印小票联/Y-打印营业员联也打印小票/A-打印营业员联但不打印小票)
    	// 非超市小票且系统参数定义只打印营业员分单，则不打印机制小票
		if (!(
			(GlobalInfo.syjDef.issryyy == 'N') || 
			(GlobalInfo.syjDef.issryyy == 'A' && ((SaleGoodsDef)salegoods.elementAt(0)).yyyh.equals(Language.apply("超市")))) &&
		    (GlobalInfo.sysPara.fdprintyyy == 'A')
			)
    	{
    		return;
    	}
		try{
		
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

        // 打印赠品联
        printGift();

        //打印二维码
        print_Qrcode();
        
        // 切纸
        printCutPaper();
        
		}catch(Exception er)
		{
			er.printStackTrace();
		}
    }
	public void print_Qrcode() 
	{
//		//二维码规则如下： 小票总长度+收银机号（4位）+小票号（8位）+201（门店号3位）+销售日期时间(14位)+ 销售金额（四舍五入到整数，固定6位，不足六位前面补0）
//		 String syjh = Convert.increaseCharForward(ConfigClass.CashRegisterCode,'0', 4);
//	     String xph = Convert.increaseCharForward(String.valueOf(salehead.fphm), '0', 8);
//	     String mkt = Convert.increaseCharForward(GlobalInfo.sysPara.mktcode, '0', 3);
//	     String date = Convert.increaseCharForward(getDateTimeStamp(salehead.rqsj), '0',14);
////	     String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(salehead.sjfk* 100,2,1));
//	     BigDecimal je= new BigDecimal(Convert.toDouble(salehead.sjfk)).setScale(0, BigDecimal.ROUND_HALF_UP);
//	     String jestr = Convert.increaseCharForward(je.toString(),'0',6);
//	     String line="#Qrcode:"+syjh+xph+mkt+date+jestr;
//	     Printer.getDefault().printLine_Normal(line);
		
//		二维码规则如下： 小票总长度+收银机号（4位）+小票号（8位）+201（门店号3位）+销售日期时间(14位)+ 销售金额（截断到元的整数，固定6位，不足六位前面补零）+小票号*销售金额验证码（截断到元取后6位，不足补0）
		 String syjh = Convert.increaseCharForward(ConfigClass.CashRegisterCode,'0', 4);
	     String xph = Convert.increaseCharForward(String.valueOf(salehead.fphm), '0', 8);
	     String mkt = Convert.increaseCharForward(GlobalInfo.sysPara.mktcode, '0', 3);
	     String date = Convert.increaseCharForward(getDateTimeStamp(salehead.rqsj), '0',14);
	     String jestr = null;
	     jestr = String.valueOf((long)ManipulatePrecision.doubleConvert(salehead.sjfk, 0, 0));
	     if(jestr.length()<6)
	     {
	    	 jestr = Convert.increaseCharForward(jestr,'0',6);
	     }else
	    	 jestr = Convert.newSubString(jestr ,jestr.length()-6 ,jestr.length());
	     
//	     小票号（取四位）*销售金额验证码（取整后四位）
	     String yzm = null;
	     long x = Convert.toLong(xph) ;
	     long y = Convert.toLong(jestr) ;
	     String xy = String.valueOf(x*y);
	     if(xy.length()<6)
	     {
	    	 yzm = Convert.increaseCharForward(xy,'0',6);
	     }else
	     {
	    	 yzm = Convert.newSubString(xy, xy.length()-6 ,xy.length()) ;
	     }
	     
	     String line="#Qrcode:"+syjh+xph+mkt+date+jestr+yzm;
	     Printer.getDefault().printLine_Normal(line);
	}
	public static String getDateTimeStamp(String dateTime){
		
		String temp[] = dateTime.split(" ");
		String[] date=temp[0].split("/");
		String year = date[0];
		String month = date[1];
		String day = date[2];
		String time[] =temp[1].split(":");
		
		String hour = time[0];
		String minute = time[1];
		String second = time[2];
		
		return year+month+day+hour+minute+second;
	}

}
