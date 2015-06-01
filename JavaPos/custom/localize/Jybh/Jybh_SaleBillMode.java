package custom.localize.Jybh;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bhdd.Bhdd_SaleBillMode;


public class Jybh_SaleBillMode extends Bhdd_SaleBillMode
{
    protected void printAppendBill()
    {
    	printSaleTicketMSInfo();
    	
        //打印面值卡联
        printMZKBill(1);

        //打印返券卡联
        //printMZKBill(2);

        //打印积分折现
        printJFZX();

        //打印退货卡
        printTHK();
    }
    
    //  打印赠券
	public void printSaleTicketMSInfo()
	{
		if (this.salemsgift == null || this.salemsgift.size() <= 0)
		{
			return;
		}
				
		if (this.salemsinvo!= 0 && salehead.fphm != this.salemsinvo)
		{
			this.salemsinvo = 0;
			this.salemsgift = null;
			return ;
		}

		String line1 = null;
        if (salehead.printnum == 0)
        {
            line1 = null;
        }
        else
        {
            line1 = "**重印" + salehead.printnum + "**";
        }
		
		for (int i = 0; i < this.salemsgift.size(); i++)
		{
			GiftGoodsDef def = (GiftGoodsDef) this.salemsgift.elementAt(i);
			if (def == null)  continue;
			printLine("--------------------------------------");
			printLine(Convert.appendStringSize("","返 券 单",1,36,38,2));
			if (line1 == null)
				printLine("");
			else
				printLine(Convert.appendStringSize("",line1,1,36,38,2));
			
			ManipulateDateTime mdt = new ManipulateDateTime(); 
			String line = Convert.appendStringSize("", "日   期:", 1, 8, 38);
			line = Convert.appendStringSize(line,ManipulateDateTime.staticGetDateBySlash(), 9, 10, 38);
			line = Convert.appendStringSize(line,"时   间:", 21, 8, 38);
			line = Convert.appendStringSize(line,mdt.getTime(), 29, 10, 38);
			printLine(line);
			
			line = Convert.appendStringSize("", "收银小票号:", 1, 11, 38);
			line = Convert.appendStringSize(line,String.valueOf(salehead.fphm), 12, 8, 38);
			line = Convert.appendStringSize(line,"POS机号:", 21, 8, 38);
			line = Convert.appendStringSize(line,salehead.syjh, 29, 10, 38);
			printLine(line);
			
			line = Convert.appendStringSize("", "卡 券 号:", 1, 11, 38);
			line = Convert.appendStringSize(line,def.code, 12, 20, 38);
			printLine(line);
			
			String infos[] = def.info.split("_");
			
			line = Convert.appendStringSize("", "检验代码:", 1, 11, 38);
			line = Convert.appendStringSize(line,infos[2], 12, 15, 38);
			printLine("Big&"+line);
			
			line = Convert.appendStringSize("", "券 类 型:", 1, 11, 38);
			line = Convert.appendStringSize(line,infos[0], 12, 8, 38);
			printLine(line);
			
			line = Convert.appendStringSize("", "券 规 则:", 1, 11, 38);
			line = Convert.appendStringSize(line,infos[1], 12, 26, 38);
			printLine(line);
			
			line = Convert.appendStringSize("", "券 金 额:", 1, 11, 38);
			line = Convert.appendStringSize(line,ManipulatePrecision.doubleToString(def.je), 12, 10, 38);
			line = Convert.appendStringSize(line, "元", 30, 2, 38);
			printLine("Big&"+line);
			
			line = Convert.appendStringSize("", "有 效 期:", 1, 11, 38);
			line = Convert.appendStringSize(line,def.memo, 12, 10, 38);
			line = Convert.appendStringSize(line, "止", 30, 2, 38);
			printLine(line);
			
			line = Convert.appendStringSize("", "注意事项:", 1, 11, 38);
			line = Convert.appendStringSize(line,"详见店内公示细则", 12, 26, 38);
			printLine(line);
			
			if (i != this.salemsgift.size() - 1)
			{
				printLine("");
				printLine("");
				printLine("");
				printLine("");
				
			}
			else
			{
				Printer.getDefault().cutPaper_Normal();
			}
		}
	}

    //打印积分折现
    public void printJFZX()
    {
        try
        {
            for (int i = 0; i < originalsalepay.size(); i++)
            {
                SalePayDef pay = (SalePayDef) originalsalepay.elementAt(i);
                PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

                //打印
                if (mode.code.equals("0508"))
                {
                    Printer.getDefault().printLine_Journal("积分折现付款打印");
                    Printer.getDefault().printLine_Journal("付款方式 :" + mode.name + "(" + mode.code + ")");
                    Printer.getDefault()
                           .printLine_Journal("金额	    :" + ManipulatePrecision.doubleToString(pay.ybje * SellType.SELLSIGN(salehead.djlb)));
                    Printer.getDefault().cutPaper_Journal();
                }
            }
        }
        catch (Exception er)
        {
            er.printStackTrace();
        }
    }

    //打印退货卡
    public void printTHK()
    {
        try
        {
            for (int i = 0; i < originalsalepay.size(); i++)
            {
                SalePayDef pay = (SalePayDef) originalsalepay.elementAt(i);
                PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

                //打印
                if (mode.code.equals("0702"))
                {
                    Printer.getDefault().printLine_Journal("退货卡付款打印");
                    Printer.getDefault().printLine_Journal("付款方式	:" + mode.name + "(" + mode.code + ")");
                    Printer.getDefault()
                           .printLine_Journal("金额	    :" + ManipulatePrecision.doubleToString(pay.ybje * SellType.SELLSIGN(salehead.djlb)));
                    Printer.getDefault().cutPaper_Journal();
                }
            }
        }
        catch (Exception er)
        {
            er.printStackTrace();
        }
    }
}
