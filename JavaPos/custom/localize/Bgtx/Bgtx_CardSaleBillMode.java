package custom.localize.Bgtx;

import com.efuture.javaPos.PrintTemplate.CardSaleBillMode;

public class Bgtx_CardSaleBillMode extends CardSaleBillMode
{
	public void printBill()
    {
		num = 0;
		hj = 0 ;
		
		// 设置打印方式
        printSetPage();

        // 打印头部区域
        printHeader();

        // 打印明细区域
        printDetail();
        
        // 打印尾部区域
        printBottom();
        
        // 打印付款区域
		printPay();
		
        // 打印汇总区域
        printTotal();

        // 切纸(保定先天下要求不切)
//        printCutPaper();
    }
}
