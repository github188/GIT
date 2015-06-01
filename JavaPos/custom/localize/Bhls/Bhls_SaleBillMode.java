package custom.localize.Bhls;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;


public class Bhls_SaleBillMode extends SaleBillMode
{
	public void setSaleTicketMSInfo(SaleHeadDef sh,Vector gifts)
	{
		// 记录小票赠送清单
		this.salemsinvo = sh.fphm;
		this.salemsgift = gifts;
		
		// 分解清单
        double aje = 0;
        double bje = 0;
        for (int i = 0; gifts != null && i < gifts.size(); i++)
        {
            GiftGoodsDef g = (GiftGoodsDef) gifts.elementAt(i);

            if (g.type.trim().equals("1"))
            {
                aje += ManipulatePrecision.doubleConvert(g.je);
            }
            else if (g.type.trim().equals("2"))
            {
                bje += ManipulatePrecision.doubleConvert(g.je);
            }
        }

        // 提示
        if ((aje > 0) || (bje > 0))
        {
        	sh.memo = aje + "," + bje;
            AccessDayDB.getDefault().updateSaleJf(sh.fphm, 2, aje, bje);
            
            StringBuffer sb = new StringBuffer();

            if (SellType.ISSALE(sh.djlb))
            {
                sb.append(Language.apply("本笔交易有活动返券\n"));
            }
            else if (SellType.ISBACK(sh.djlb))
            {
                sb.append(Language.apply("本笔交易有退券"));
            }

            sb.append(Language.apply("返A券: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(aje), 0, 10, 10, 1) + "\n");
            sb.append(Language.apply("返B券: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(bje), 0, 10, 10, 1));
            new MessageBox(sb.toString());
        }
	}
	
	public boolean needMSInfoPrintGrant()
	{
		if (this.salemsgift != null && this.salemsgift.size() > 0) return true;
		return false;
	}
	
	public void printSaleTicketMSInfo()
	{
		if (this.salemsgift == null || this.salemsgift.size() <= 0)
		{
			return ;
		}
		
		if (salemsinvo!= 0 && salehead.fphm != salemsinvo)
		{
			this.salemsinvo = 0;
			this.salemsgift = null;
			return ;
		}

		String lab = "";
    	if (salehead.printnum > 0)
    	{
    		lab += "  " + Language.apply("(重打印)");
    	}
    	
		for (int i = 0; i < salemsgift.size(); i++)
		{
			GiftGoodsDef def = (GiftGoodsDef) salemsgift.elementAt(i);
			printStart();
			printLine("       -----"+lab+"-----");
			printLine("===================================");
			printLine("== " + Language.apply("券  号: ")+def.code);
			printLine("== " + Language.apply("券信息: ")+def.info);
			printLine("== " + Language.apply("券金额: ")+def.je);
			printLine("== " + Language.apply("有效期: ")+def.memo);
			printLine("==================================");
			printCutPaper();
		}
	}
	
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

        // 切纸
        printCutPaper();
		}catch(Exception er)
		{
			er.printStackTrace();
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

        // 打印赠品联
        printGift();

        // 切纸
        printCutPaper();

        // 打印附加的各个小票联
        printAppendBill();
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
                    printLine(Convert.appendStringSize("", Language.apply("\n赠品栏"), 0, Width, Width, 2));
                }

                first = true;
                printLine(Language.apply("商品编码:") + saleGoodsDef.code);
                printLine(Language.apply("商品柜组:") + saleGoodsDef.gz);

                String line = Convert.appendStringSize("", Language.apply("赠送价值:"), 0, 9, Width);
                line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(saleGoodsDef.lsj), 10, 10, Width);
                line = Convert.appendStringSize(line, Language.apply("赠送数量:"), 21, 9, Width);
                line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(saleGoodsDef.sl, 4, 1), 30, 8, Width);
                printLine(line);
                printLine("\n");
            }
        }
    }
}
