package custom.localize.Hzjb;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateByte;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bcrm.Bcrm_SaleBillMode;


public class Hzjb_SaleBillMode extends Bcrm_SaleBillMode
{
    protected final int Hzjb_dzqje = 100; //此单返券金额
    protected final int Hzjb_tcq = 101; //停车条形码

    public Vector convertPayDetail(Vector v)
    {
        // 解百要求打印的付款明细为 应付金额
        for (int i = 0; i < v.size(); i++)
        {
            SalePayDef spd = (SalePayDef) v.elementAt(i);

            if (spd.flag != '2')
            {
                continue;
            }

            double je = spd.ybje;

            for (int j = 0; j < v.size(); j++)
            {
                SalePayDef spd1 = (SalePayDef) v.elementAt(j);

                if (spd1.paycode.equals(spd.paycode) && (spd1.flag == '1') && spd1.payno.equals(spd.payno))
                {
                    spd1.ybje -= je;

                    if (spd1.ybje <= 0)
                    {
                        spd1.ybje = 0;
                    }

                    spd1.je = spd1.hl * spd1.ybje;

                    je -= spd1.ybje;

                    if (je <= 0)
                    {
                        break;
                    }
                }
            }
        }

        return v;
    }

    protected String extendCase(PrintTemplateItem item, int index)
    {
        String line = null;

        switch (Integer.parseInt(item.code))
        {
            case SBM_fphm: //小票号码
                line = String.valueOf(salehead.fphm);

                break;

            case SBM_hykh:

                if ((salehead.hykh == null) || (salehead.hykh.length() <= 0))
                {
                    line = "&!";
                }
                else
                {
                    if (salehead.hykh.length() > 6)
                    {
                        line = "****" + salehead.hykh.substring(salehead.hykh.length() - 6);
                    }
                    else
                    {
                        line = salehead.hykh;
                    }
                }

                break;

            case SBM_payno: //付款方式帐号
                line = ((SalePayDef) salepay.elementAt(index)).payno;

                if ((line == null) || (line.length() <= 0))
                {
                    line = "&!";

                    break;
                }

                if(((SalePayDef) salepay.elementAt(index)).paycode.equals("0402"))
                {
                	;//0402 为联华储值卡消费凭证，客户要求显示全部卡号
                }
                else if (((SalePayDef) salepay.elementAt(index)).paycode.equals("0400"))
                {
                    if ((line != null) && (line.length() > 8))
                    {
                        line = "****" + line.substring(3, 8);
                    }
                }
                else
                {
                    if ((line != null) && (line.length() > 8))
                    {
                        line = "****" + line.substring(line.length() - 6);
                    }
                }

                break;

            case Hzjb_dzqje:
                double zqje = 0;
                if ((zq != null) && (zq.size() > 0))
                {
                    for (int i = 0; i < zq.size(); i++)
                    {
                        GiftGoodsDef gift = (GiftGoodsDef) zq.elementAt(i);
                        zqje = gift.je;
                    }
                }

                line = ManipulatePrecision.doubleToString(zqje);

                break;

            case Hzjb_tcq:

                char[] a = { 0x1D, 0x6B, (int) 02 };
                char[] b = { 0x1D, 0x48, (int) 02 };
                String line1 = Convert.increaseLong(salehead.fphm, 8);
                line = String.valueOf(b) + String.valueOf(a) + salehead.syjh + line1 + String.valueOf((char) 0x00);

                break;

            default:
                line = null;
        }

        return line;
    }
    //打印电子卷信息
    public void printJxx()
    {
    	  PosLog.getLog(this.getClass()).info("========》printJxx() 11111111111");
    	  boolean head = false;

          for (int i = 0; i < zq.size(); i++)
          {
              GiftGoodsDef def = (GiftGoodsDef) zq.elementAt(i);

//              if (!def.type.trim().equals("1") && !def.type.trim().equals("2")
//            		  && !def.type.trim().equals("3")
//            		  && !def.type.trim().equals("11")
//            		  && !def.type.trim().equals("89")
//            		  && !def.type.trim().equals("90"))
//              {
//                  continue;
//              }
              if(def.type.trim().equals("4"))
              {
            	  PosLog.getLog(this.getClass()).info("========》printJxx()"+"22222");
            	  if (!head)
            	  {
            		  Printer.getDefault().printLine_Normal(" =====================================");
            		  Printer.getDefault().printLine_Normal(" 已送券信息");
            		  head = true;
            	  }
            	 
            	  String[] infos = def.info.split(",");
            	  PosLog.getLog(this.getClass()).info("========》printJxx()"+"33333"+def.info);
            	  String type = "";

            	  if ((infos != null) && (infos.length > 1))
            	  {
            		  for(int j = 0 ;j<infos.length;j++)
            		  {
            			  String [] types = infos[j].split(":");
            			  type = types[1];
            			  Printer.getDefault().printLine_Normal(" 券号;" + Convert.increaseCharForward(def.code, 10) + " 券类型:" + type);
                    	  Printer.getDefault().printLine_Normal(" 金额:" + Convert.increaseCharForward(ManipulatePrecision.doubleToString(def.je), 10));
                    	  Printer.getDefault().printLine_Normal(" 券有效期至" +def.enddate+"止");
                    	  
                    	  
                    	  PosLog.getLog(this.getClass()).info("========》printJxx()"+" 券有效期至" +def.enddate+"止");
            		  }
            		 
            	  }

            	
              }
          }
    }
    

    public void printBottom()
    {
        try
        {
        	
        	PosLog.getLog(this.getClass()).info("========》printJxx(1)");
        	printJxx();
        	PosLog.getLog(this.getClass()).info("========》printJxx(2)");
            double zszke = 0;

            for (int i = 0; i < salegoods.size(); i++)
            {
                SaleGoodsDef sg = (SaleGoodsDef) salegoods.elementAt(i);

                if (sg.zszke > 0)
                {
                    zszke += sg.zszke;
                }
            }

            if (zszke > 0)
            {
                Printer.getDefault().printLine_Normal(" -------------------------------------");
                Printer.getDefault().printLine_Normal(" 此单满减打折:" + ManipulatePrecision.doubleToString(zszke));
                Printer.getDefault().printLine_Normal(" -------------------------------------");
            }

            if ((this.salemsinvo != 0) && (salehead.fphm != this.salemsinvo))
            {
            	this.salemsinvo = 0;
            	this.zq   = null;
            	this.gift = null;
                return;
            }

            if ((zq == null) || (zq.size() <= 0))
            {
                return;
            }

            boolean head = false;

            for (int i = 0; i < zq.size(); i++)
            {
                GiftGoodsDef def = (GiftGoodsDef) zq.elementAt(i);

                if (!def.type.trim().equals("1") && !def.type.trim().equals("2"))
                {
                    continue;
                }

                if (!head)
                {
                    Printer.getDefault().printLine_Normal(" =====================================");
                    Printer.getDefault().printLine_Normal(" 已送券信息");
                    head = true;
                }

                String[] infos = def.info.split("_");
                String type = "";

                if ((infos != null) && (infos.length > 1))
                {
                    type = infos[1];
                }

                Printer.getDefault().printLine_Normal(" 券号;" + Convert.increaseCharForward(def.code, 10) + " 券类型:" + type);
                Printer.getDefault().printLine_Normal(" 金额:" + Convert.increaseCharForward(ManipulatePrecision.doubleToString(def.je), 10));
                Printer.getDefault().printLine_Normal(" 券有效期至" +def.enddate+"止");
                PosLog.getLog(this.getClass()).info("========》printJxx(3)");
            }
        }
        catch (Exception er)
        {
            er.printStackTrace();

            return;
        }
        finally
        {
            super.printBottom();
        }
    }

    // 打印面值卡联
    public void printMZKBill(int type)
    {
        int i = 0;

        //不能打印返券签购单
        if (type == 2)
        {
            return;
        }

        try
        {
            // 先检查是否有需要打印的付款方式
            for (i = 0; i < originalsalepay.size(); i++)
            {
                SalePayDef pay = (SalePayDef) originalsalepay.elementAt(i);
                PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

                if ((type == 1) && (mode.type == '4') && mode.code.equals("0400"))
                {
                    break;
                }

                if ((type == 2) && CreatePayment.getDefault().isPaymentFjk(mode.code))
                {
                    break;
                }
            }

            if (i >= originalsalepay.size())
            {
                return;
            }

            // 开始新打印
            Printer.getDefault().startPrint_Journal();

            for (int j = 0; j < 2; j++)
            {
                if (type == 1)
                {
                    String lab = "\n     杭州解百IC卡消费签购";

                    if (salehead.printnum > 0)
                    {
                        lab += "  (重打印)";
                    }

                    Printer.getDefault().printLine_Journal(lab);
                }

                if (type == 2)
                {
                    String lab = "\n    返券卡 消费签购";

                    if (salehead.printnum > 0)
                    {
                        lab += "  (重打印)";
                    }

                    Printer.getDefault().printLine_Journal(lab);
                }

                if (j == 0)
                {
                    Printer.getDefault().printLine_Journal("第一联                持卡人留存");
                }
                else if (j == 1)
                {
                    Printer.getDefault().printLine_Journal("第二联                商场 留存");
                }

                Printer.getDefault().printLine_Journal("终端机号:" + ConfigClass.CashRegisterCode + "  操作员号:" + GlobalInfo.posLogin.gh);
                Printer.getDefault().printLine_Journal("单据号  :" + Convert.increaseLong(salehead.fphm, 8));
                Printer.getDefault().printLine_Journal("日期:" + salehead.rqsj);
                Printer.getDefault().printLine_Journal("=====================================");

                int num = 0;
                double hj = 0;

                for (i = 0; i < originalsalepay.size(); i++)
                {
                    SalePayDef pay = (SalePayDef) originalsalepay.elementAt(i);
                    PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

                    if (((type == 1) && (mode.type == '4') && (mode.code.equals("0400"))) ||
                            ((type == 2) && (CreatePayment.getDefault().isPaymentFjk(mode.code))))
                    {
                        num++;

                        String line1 = "";
                        if (SellType.ISBACK(salehead.djlb))
                        {}
                        else if (((pay.str5 != null) && pay.str5.equals("Y")) || (pay.kye == 0))
                        {
                            line1 = "(卡回收)";
                        }

                        Printer.getDefault().printLine_Journal("卡 号:" + Convert.increaseCharForward(pay.payno.substring(0, 8), ' ', 10) + line1);
                        Printer.getDefault()
                               .printLine_Journal("消费金额:   " + Convert.increaseCharForward(ManipulatePrecision.doubleToString(pay.je), ' ', 10));
                        
                        if (SellType.ISBACK(salehead.djlb))
                        {
                        	
                        }
                        else
                        {
                        	Printer.getDefault()
                               .printLine_Journal("本卡余额:   " + Convert.increaseCharForward(ManipulatePrecision.doubleToString(pay.kye), ' ', 10));
                        }
                        if (pay.hl == 0)
                        {
                            pay.hl = 1;
                        }

                        hj += (pay.ybje * pay.hl);
                    }
                }

                if (type == 1)
                {
                    Printer.getDefault().printLine_Journal("本次共 " + num + " 张储值卡消费");
                }

                if (type == 2)
                {
                    Printer.getDefault().printLine_Journal("本次共 " + num + " 张返券卡消费");
                }

                Printer.getDefault().printLine_Journal("合计消费金额     " + ManipulatePrecision.doubleToString(hj * SellType.SELLSIGN(salehead.djlb)));

                Printer.getDefault().printLine_Journal("=====================================");
                Printer.getDefault().printLine_Journal("本人同意支付上述款项");
                Printer.getDefault().printLine_Journal("持卡人签字：");
                Printer.getDefault().printLine_Journal("");
                Printer.getDefault().printLine_Journal("");
                Printer.getDefault().printLine_Journal("");
                Printer.getDefault().printLine_Journal("=====================================");
                Printer.getDefault().printLine_Journal("");
                Printer.getDefault().printLine_Journal("");

                Printer.getDefault().cutPaper_Journal();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void printzqfp()
    {
        if ((salemsinvo == 0) || ((salemsinvo != 0) && (salehead.fphm != salemsinvo)))
        {
        	salemsinvo = 0;
            zq   = null;
            gift = null;

            return;
        }

        if ((zq == null) || (zq.size() <= 0))
        {
            return;
        }

        Printer.getDefault().printLine_Normal("------");
        Printer.getDefault().printLine_Normal("本次小票存在赠券");

        for (int i = 0; i < zq.size(); i++)
        {
            GiftGoodsDef def = (GiftGoodsDef) zq.elementAt(i);
            Printer.getDefault().printLine_Normal("赠券号  :" + def.code);
            Printer.getDefault().printLine_Normal("赠券金额:" + def.je);
            Printer.getDefault().printLine_Normal("赠券类型:" + def.info);
        }

        Printer.getDefault().printLine_Normal("------");
    }

    // 打印赠券
    public void printSaleTicketMSInfo()
    {
        if ((salemsinvo == 0) || ((salemsinvo != 0) && (salehead.fphm != salemsinvo)))
        {
        	salemsinvo = 0;
            zq   = null;
            gift = null;

            return;
        }

        if ((zq == null) || (zq.size() <= 0))
        {
            return;
        }

        for (int i = 0; i < zq.size(); i++)
        {
            GiftGoodsDef def = (GiftGoodsDef) zq.elementAt(i);

            //券名称描述&券信息&券使用范围描述
            String[] infos = def.info.split("_");

            if (!def.type.trim().equals("1") && !def.type.trim().equals("2"))
            {
                continue;
            }

            String lab = "";

            if (salehead.printnum > 0)
            {
                lab += "  (重打印)";
            }

            Printer.getDefault().printLine_Normal("     " + lab);
            Printer.getDefault().printLine_Normal("      杭州解百集团股份有限公司");

            if (infos.length > 1)
            {
                Printer.getDefault().printLine_Normal("Big&    " + infos[1]);
            }

            Printer.getDefault().printLine_Normal("");
            Printer.getDefault().printLine_Normal("=====================================");
            Printer.getDefault().printLine_Normal("Big&券号: " + def.code);
            Printer.getDefault().printLine_Normal("单据号:   " + salehead.fphm + "   收银机号:" + ConfigClass.CashRegisterCode);
            Printer.getDefault().printLine_Normal("发券日期: " + GlobalInfo.balanceDate);

            if (infos.length > 0)
            {
                Printer.getDefault().printLine_Normal("券信息:   " + infos[0]);
            }

            Printer.getDefault().printLine_Normal("");
            Printer.getDefault().printLine_Normal("-------------------------------------");
            Printer.getDefault().printLine_Normal("Big&   " + ManipulatePrecision.doubleToString(def.je) + "元");
            Printer.getDefault().printLine_Normal("-------------------------------------");
            Printer.getDefault().printLine_Normal("        赠           券              ");
            Printer.getDefault().printLine_Normal("");

            if (infos.length > 2)
            {
                Printer.getDefault().printLine_Normal("本券使用范围：" + infos[2]);
            }

            /**
            Printer.getDefault().printLine_Normal("·本券过期作废，不兑换，不找零");
            Printer.getDefault().printLine_Normal("·本活动最终解释权归解百公司");
            Printer.getDefault().printLine_Normal("·赠券不可用于支付满减内的实付金额");
            Printer.getDefault().printLine_Normal("·本券消费不计入二次送券");
            Printer.getDefault().printLine_Normal("·本券盖章有效, 一旦遗失, 不予补办");*/
            String[] r = def.memo.split(";");
            String startdate = "";
            String enddate = "";

            if (r.length > 1)
            {
                startdate = r[0].trim();
                enddate   = r[1].trim();
            }
            else
            {
                enddate = def.memo;
            }

            Printer.getDefault().printLine_Normal("·本券使用开始日期：" + startdate);
            Printer.getDefault().printLine_Normal("·本券使用有效期至：" + enddate);

            if (r.length > 2)
            {
                Printer.getDefault().printLine_Normal(r[2]);
            }

            Printer.getDefault().cutPaper_Normal();
        }
    }

    protected void printSellBill()
    {
        String line = "," + GlobalInfo.sysPara.noprintCashier + ",";

        if ((GlobalInfo.sysPara.noprintCashier != null) && (GlobalInfo.sysPara.noprintCashier.trim().length() > 0) &&
                (line.indexOf("," + ConfigClass.CashRegisterCode + ",") >= 0))
        {
            return;
        }

        super.printSellBill();
    }
}
