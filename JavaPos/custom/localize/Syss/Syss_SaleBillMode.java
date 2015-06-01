package custom.localize.Syss;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bhls.Bhls_SaleBillMode;


public class Syss_SaleBillMode extends Bhls_SaleBillMode
{
    protected void printLine(String s)
    {
        Printer.getDefault().printLine_Normal(s);
    }

    public void printSetPage()
    {
        //所有打印接口都从Normal打印
        Printer.getDefault().setPagePrint_Normal(false, 1);
    }

    public void printCutPaper()
    {
        Printer.getDefault().cutPaper_Normal();
    }

    public void printYYYBill()
    {
        //如果不为销售小票，不打印平推联
        if (!SellType.ISSALE(salehead.djlb))
        {
            return;
        }

        int i = 0;
        String appendSpace = "                                               ";
        SaleGoodsDef sgd = null;
        String line;

        // 系统参数定义为打印分单且是营业员小票，才打印营业员联
        if (!((GlobalInfo.sysPara.fdprintyyy == 'Y') &&
                ((GlobalInfo.syjDef.issryyy == 'Y') ||
                    ((GlobalInfo.syjDef.issryyy == 'A') && !((SaleGoodsDef) salegoods.elementAt(0)).yyyh.equals("超市")))))
        {
            return;
        }

        // 按分组进行分单打印
        for (i = 0; i < salegoods.size(); i++)
        {
            sgd = (SaleGoodsDef) salegoods.elementAt(i);

            if (new MessageBox("请将 商品\"" + sgd.name + "\"的销售单放入打印机\n\n按‘回车’键后开始打印\n按‘退出’键则跳过打印").verify() == GlobalVar.Exit)
            {
                continue;
            }

            Printer.getDefault().startPrint_Slip();

            Printer.getDefault().printLine_Slip(appendSpace + "时间:" + salehead.rqsj + " NO." + salehead.syjh + "-" + salehead.fphm);
            Printer.getDefault()
                   .printLine_Slip(appendSpace + "收银员:" + salehead.syyh + "          " + SellType.getDefault().typeExchange(salehead.djlb, salehead.hhflag,salehead));

            if (salehead.printnum > 0)
            {
                Printer.getDefault().printLine_Slip(appendSpace + "---------------重打印-----------------");
            }
            else
            {
                Printer.getDefault().printLine_Slip(appendSpace + "--------------------------------------");
            }

            double hjje = 0;
            double hjzk = 0;

            line = Convert.appendStringSize("", sgd.barcode, 0, 10, Width);
            line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(sgd.sl, 4, 1, true), 12, 4, Width, 1);
            line = Convert.appendStringSize(line, " x ", 16, 3, Width);
            line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(sgd.jg), 19, 8, Width);
            line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(ManipulatePrecision.sub(sgd.hjje, sgd.hjzk)), 28, 9, Width, 1);

            Printer.getDefault().printLine_Slip(appendSpace + line);
            Printer.getDefault().printLine_Slip(appendSpace + sgd.name);

            hjje += sgd.hjje;
            hjzk += sgd.hjzk;

            Printer.getDefault().printLine_Slip(appendSpace + "--------------------------------------");
            if (salehead.hykh != null && salehead.hykh.length() > 0)
            {
                Printer.getDefault()
                .printLine_Slip(appendSpace + "会员号:" + Convert.appendStringSize("", salehead.hykh, 0, 20, 20));
            }
            
            Printer.getDefault()
                   .printLine_Slip(appendSpace + "营业员:" + Convert.appendStringSize("", sgd.yyyh, 0, 10, 10) + "       柜组:" +
                                   Convert.appendStringSize("", sgd.gz, 0, 10, 10));
            Printer.getDefault()
                   .printLine_Slip(appendSpace + "总小计:" + Convert.appendStringSize("", ManipulatePrecision.doubleToString(hjje), 0, 10, 10) +
                                   "       折扣:" + Convert.appendStringSize("", ManipulatePrecision.doubleToString(hjzk), 0, 10, 10));
            Printer.getDefault().printLine_Slip(appendSpace + "应收金额：" + ManipulatePrecision.doubleToString(ManipulatePrecision.sub(hjje, hjzk)));

            // 循环打印付款明细
            for (int j = 0; j < salepay.size(); j++)
            {
                SalePayDef spd = (SalePayDef) salepay.elementAt(j);

                // 找零付款不打印
                if (spd.flag == '2')
                {
                    continue;
                }

                Printer.getDefault().printLine_Slip(appendSpace + Convert.appendStringSize("", spd.payname, 0, 14, 10) + ":        " + spd.ybje);
            }

            Printer.getDefault().cutPaper_Slip();
        }
    }
}
