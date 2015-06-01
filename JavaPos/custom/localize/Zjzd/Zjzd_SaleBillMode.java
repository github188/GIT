package custom.localize.Zjzd;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.GiftBillMode;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.PrintTemplate.YyySaleBillMode;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bcrm.Bcrm_SaleBillMode;


public class Zjzd_SaleBillMode extends Bcrm_SaleBillMode {
    //protected static final int SBM_yhzke = 102;
    //protected static final int SBM_mjzke = 103;

    protected String extendCase(PrintTemplateItem item, int index) {
        String line = null;

        switch (Integer.parseInt(item.code)) {
        case 102:

            double yhzke = 0.0;

            for (int i = 0; i < this.salegoods.size(); ++i) {
                SaleGoodsDef sgd = (SaleGoodsDef) this.salegoods.elementAt(i);
                yhzke += sgd.yhzke;
            }

            if (yhzke > 0) {
                line = ManipulatePrecision.doubleToString(yhzke);
            }

            break;

        case 103:

            double mjzke = 0.0;

            for (int i = 0; i < this.salegoods.size(); ++i) {
                SaleGoodsDef sgd = (SaleGoodsDef) this.salegoods.elementAt(i);
                mjzke += sgd.zszke;
            }

            if (mjzke > 0) {
                line = ManipulatePrecision.doubleToString(mjzke);
            }
        }

        return line;
    }

    public void printTotal() {
        super.printTotal();

        for (int i = 0; (this.zq != null) && (i < this.zq.size()); ++i) {
            GiftGoodsDef g = (GiftGoodsDef) this.zq.elementAt(i);

            if (!(g.type.trim().equals("99"))) {
                continue;
            }

            printLine("本笔小票有返券：");

            String[] rows = g.info.split(",");

            for (int x = 0; x < rows.length; ++x) {
                String lines = rows[x];

                if (lines.split(":").length < 2) {
                    continue;
                }

                printLine(Convert.appendStringSize("", lines.split(":")[0], 0,
                        14, 14, 1) + ":" +
                    Convert.appendStringSize("", lines.split(":")[1], 0, 14, 14) +
                    "\n");
            }
        }
    }

    public void printBill() {
        if (((YyySaleBillMode) YyySaleBillMode.getDefault()).isLoad()) {
            printYyyBillPrintMode();
        } else {
            printYYYBill();
        }

        this.printnum = 0;

        for (int salebillnum = 0; salebillnum < GlobalInfo.sysPara.salebillnum;
                ++salebillnum) {
            printSellBill();
            this.printnum += 1;
        }

        printAppendBill();
        
        Printer.getDefault().cutPaper_Journal();
    	
    }
    
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
    }

    public void setSaleTicketMSInfo(SaleHeadDef sh, Vector gifts) {
        this.salemsinvo = sh.fphm;
        this.salemsgift = gifts;

        Vector goodsinfo = new Vector();
        Vector fj = new Vector();

        for (int i = 0; (gifts != null) && (i < gifts.size()); ++i) {
            GiftGoodsDef g = (GiftGoodsDef) gifts.elementAt(i);

            if (g.type.trim().equals("0")) {
                break;
            }

            if ((g.type.trim().equals("1")) || (g.type.trim().equals("2"))) {
                fj.add(g);
            } else if (g.type.trim().equals("3")) {
                goodsinfo.add(g);
            } else if (g.type.trim().equals("4")) {
                fj.add(g);
            } else if (g.type.trim().equals("11")) {
                fj.add(g);
            } else {
                if (!(g.type.trim().equals("99"))) {
                    continue;
                }

                fj.add(g);
            }
        }

        StringBuffer buff = new StringBuffer();
        double je = 0.0D;

        for (int i = 0; i < fj.size(); ++i) {
            GiftGoodsDef g = (GiftGoodsDef) fj.elementAt(i);

            if (!(g.type.trim().equals("99"))) {
                continue;
            }

            String[] rows = g.info.split(",");

            for (int x = 0; x < rows.length; ++x) {
                String lines = rows[x];

                if (lines.split(":").length < 2) {
                    continue;
                }

                buff.append(Convert.appendStringSize("", lines.split(":")[0],
                        0, 14, 14, 1) + ":" +
                    Convert.appendStringSize("", lines.split(":")[1], 0, 14, 14) +
                    "\n");
            }

            buff.append(Convert.increaseChar("-", '-', 27) + "\n");
            buff.append("返券总金额为: " + ManipulatePrecision.doubleToString(g.je));
            je += g.je;
        }

        if (je > 0.0D) {
            new MessageBox(buff.toString());
        }

        if (fj.size() > 0) {
            this.zq = fj;
        } else {
            this.zq = null;
        }

        if (goodsinfo.size() > 0) {
            this.gift = goodsinfo;
        } else {
            this.gift = null;
        }
    }

    public void printBottom() {
        if (this.zq != null) {
            double je = 0.0D;

            for (int i = 0; i < this.zq.size(); ++i) {
                GiftGoodsDef def = (GiftGoodsDef) this.zq.elementAt(i);

                if (!(def.type.equals("4"))) {
                    continue;
                }

                je += def.je;
            }

            if (je > 0.0D) {
                Printer.getDefault()
                       .printLine_Normal("本次小票有返券，返券金额为:" +
                    ManipulatePrecision.doubleToString(je));
            }
        }

        super.printBottom();
    }

    public void printSaleTicketMSInfo() {
        if ((this.zq == null) || (this.zq.size() <= 0)) {
            return;
        }

        if ((this.salemsinvo != 0L) && (this.salehead.fphm != this.salemsinvo)) {
            this.salemsinvo = 0L;
            this.zq = null;
            this.gift = null;

            return;
        }

        if (GiftBillMode.getDefault().checkTemplateFile())
        {
        	GiftBillMode.getDefault().ReadTemplateFile();
        }
        
        for (int i = 0; i < this.zq.size(); ++i) {
            GiftGoodsDef def = (GiftGoodsDef) this.zq.elementAt(i);

            if ((def.type.trim().equals("99")) ||
                    (def.type.trim().equals("4"))) {
                continue;
            }
            
            if(GiftBillMode.getDefault().checkTemplateFile())
            {
            	Printer.getDefault().printLine_Journal("----------------------------------");
            	Printer.getDefault().printLine_Journal("         此券自行撕下无效");
            	Printer.getDefault().printLine_Journal("----------------------------------");
            	
            	GiftBillMode.getDefault().setTemplateObject(salehead, def);
            	GiftBillMode.getDefault().PrintGiftBill();
            	
            	continue;
            }
            
            Printer.getDefault().printLine_Journal("----------------------------------");
        	Printer.getDefault().printLine_Journal("         此券自行撕下无效");
        	Printer.getDefault().printLine_Journal("----------------------------------");

            Printer.getDefault()
                   .printLine_Journal(Convert.appendStringSize("",
                    GlobalInfo.sysPara.mktname, 1, 37, 38, 2));
            Printer.getDefault().printLine_Journal("");
            Printer.getDefault()
                   .printLine_Journal("收银机号：" + this.salehead.syjh + "  小票号：" +
                Convert.increaseLong(this.salehead.fphm, 8));

            if (SellType.ISCOUPON(this.salehead.djlb)) {
                Printer.getDefault()
                       .printLine_Journal(Convert.appendStringSize("", "买券交易",
                        1, 37, 38, 2));
            }

            Printer.getDefault()
                   .printLine_Journal(Convert.appendStringSize("",
                    "\t\t\t\t手 工 券", 1, 37, 38));
            Printer.getDefault()
                   .printLine_Journal(Convert.appendStringSize("",
                    "=================================================", 1, 37,
                    38));
            Printer.getDefault().printLine_Journal("Big&=券号: " + def.code);
            Printer.getDefault().printLine_Journal("");
            Printer.getDefault().printLine_Journal("Big&=券信息:" + def.info);
            Printer.getDefault().printLine_Journal("");
            Printer.getDefault()
                   .printLine_Journal("Big&=券总额:" +
                ManipulatePrecision.doubleToString(def.je));
            Printer.getDefault().printLine_Journal("");

            if (this.salehead.printnum > 0) {
                Printer.getDefault()
                       .printLine_Journal(Convert.appendStringSize("",
                        "\t\t\t\t重 打 印", 1, 37, 38));
            }

            Printer.getDefault().printLine_Journal("== 券有效期: " + def.memo);
            Printer.getDefault().printLine_Journal("");
            Printer.getDefault().printLine_Journal("本券不找零，不挂失，不开发票，不兑现");
            Printer.getDefault()
                   .printLine_Journal(Convert.appendStringSize("", "逾期不能使用", 1,
                    37, 38, 2));
            Printer.getDefault()
                   .printLine_Journal(Convert.appendStringSize("", "（本券盖章有效)",
                    1, 37, 38, 2));
            Printer.getDefault()
                   .printLine_Journal(Convert.appendStringSize("",
                    "=================================================", 1, 37,
                    38));
            Printer.getDefault().printLine_Journal("");
            Printer.getDefault().printLine_Journal("");
            Printer.getDefault().printLine_Journal("");
            Printer.getDefault().printLine_Journal("");
            
        }
    }
}
