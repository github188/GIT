package custom.localize.Wqls;



import com.efuture.commonKit.ProgressBox;

import custom.localize.Bhls.Bhls_SaleBillMode;

public class Wqls_SaleBillMode extends Bhls_SaleBillMode
{
	public void printBill()
	{
		ProgressBox progress = new ProgressBox();
		progress.setText("正在打印小票,请等待......");
		
		try{
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
	
			// 打印重打印标志
			if (salehead.printnum > 0)
			{
				printLine("-----------重打印---------");
			}
			
			// 打印赠品联
			printGift();
			
	        // 切纸
	        printCutPaper();
	        
	        /////////////////////// 打印附加的各个小票联
	        
	        super.printAppendBill();
	        
//			//打印面值卡联
//	    	printMZKBill(1);
//	    	
//	    	//打印返券卡联
//	    	printMZKBill(2);
		}
		catch(Exception er)
		{
			er.printStackTrace();
		}
		finally
		{
			progress.close();
			progress = null;
		}
	}
}
