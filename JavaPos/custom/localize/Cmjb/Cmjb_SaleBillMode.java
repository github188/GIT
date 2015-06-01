package custom.localize.Cmjb;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.GiftBillMode;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import custom.localize.Cmls.Cmls_SaleBillMode;


public class Cmjb_SaleBillMode extends Cmls_SaleBillMode
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

                if (spd1.paycode.equals(spd.paycode) && (spd1.flag == '1') && spd1.payno.equals(spd.payno)&&!spd1.str4.equals("Y"))
                {
                    spd1.ybje -= je;

                    if (spd1.ybje <= 0)
                    {
                        spd1.ybje = 0;
                    }

                    //标记是否已经转换
                    spd1.str4 = "Y";
                    
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
    	 // PosLog.getLog(this.getClass()).info("========》printJxx() 11111111111");
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
            	 //PosLog.getLog(this.getClass()).info("========》printJxx()"+"22222");
            	  if (!head)
            	  {
            		  Printer.getDefault().printLine_Normal(" =====================================");
            		  Printer.getDefault().printLine_Normal(" 已送券信息");
            		  head = true;
            	  }
            	 
            	  String[] infos = def.info.split(",");
            	  //PosLog.getLog(this.getClass()).info("========》printJxx()33333"+def.info);
            	  String type = "";

            	  if ((infos != null) && (infos.length > 1))
            	  {
            		  for(int j = 0 ;j<infos.length;j++)
            		  {
            			  String [] types = infos[j].split(":");
            			  type = types[1];
                    	  Printer.getDefault().printLine_Normal(" 金额:" + Convert.increaseCharForward(type, 10) + " 券类型:" + def.type);
                    	  Printer.getDefault().printLine_Normal(" 券有效期:" +def.memo);
            		  }
            	  }
            	  else
            	  {
                	  Printer.getDefault().printLine_Normal(" 金额:" + Convert.increaseCharForward(ManipulatePrecision.doubleToString(def.je), 10)+ " 券类型:" + type);
                	  Printer.getDefault().printLine_Normal(" 券有效期:" +def.memo);
            	  }
              }
          }
          Printer.getDefault().printLine_Normal(" =====================================");
    }
    
    public void printBottom()
    {
        try
        {
        	printJxx();
        	
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

                Printer.getDefault().printLine_Normal(" 券号:" + Convert.increaseCharForward(def.code, 10) + " 券类型:" + type);
                Printer.getDefault().printLine_Normal(" 金额:" + Convert.increaseCharForward(ManipulatePrecision.doubleToString(def.je), 10));
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
    
	public void setSaleTicketMSInfo(SaleHeadDef sh,Vector gifts)
	{
		// 记录小票赠送清单
		this.salemsinvo = sh.fphm;
		this.salemsgift = gifts;
		
        // 分解赠品清单
        Vector goodsinfo = new Vector();
        Vector fj = new Vector();
        for (int i = 0; gifts != null && i < gifts.size(); i++)
        {
            GiftGoodsDef g = (GiftGoodsDef)gifts.elementAt(i);

            if (g.type.trim().equals("0"))
            {
                //无促销
                break;
            }
            else if (g.type.trim().equals("1") || g.type.trim().equals("2"))
            {
                fj.add(g);
            }
            else if (g.type.trim().equals("3"))
            {
                goodsinfo.add(g);
            }
            else if (g.type.trim().equals("4"))
            {
            	fj.add(g);
            }
            else if (g.type.trim().equals("11"))
            {
            	fj.add(g);
            }
            else if (g.type.trim().equals("90")) // 停车券
            {
            	fj.add(g);
            }
            else if (g.type.trim().equals("55")) // 投票联
            {
            	fj.add(g);
            }
        }
        
        // 提示
        StringBuffer buff = new StringBuffer();
        double je = 0;
        
        Vector xv = new Vector();
        for (int i = 0 ; i < fj.size(); i++)
        {
        	GiftGoodsDef g = (GiftGoodsDef)fj.elementAt(i);
        	
        	if (g.type.trim().equals("90")) // 停车券
        	{
        		continue;
        	}
        	
        	if (g.type.trim().equals("55")) // 投票联
        	{
        		continue;
        	}
        	
        	int j =0;
        	for (j = 0; j < xv.size(); j++)
        	{
        		String[] g1 = (String[])xv.elementAt(j);
        		if (g1[0].equals(g.info))
        		{
        			g1[1] = ManipulatePrecision.doubleToString(Convert.toDouble(g1[1]) + g.je);
        			break;
        		}
        	}
        	
        	if (j >= xv.size())
        	{
        		xv.add(new String[]{g.info,ManipulatePrecision.doubleToString(g.je)});
        	}
        }
        
        for (int i = 0 ; i < xv.size(); i++)
        {
        	String[] g1 = (String[])xv.elementAt(i);
        	
        	String l = Convert.appendStringSize("",g1[0],1,16,17,1);
        	
        	buff.append(l+":"+Convert.appendStringSize("",g1[1],1,10,10,0)+"\n");
        	//buff.append(g.code+"   "+g.info+"      "+Convert.increaseChar(ManipulatePrecision.doubleToString(g.je), 14)+"\n");
        	je += Convert.toDouble(g1[1]);
        }
        buff.append(Convert.increaseChar("-", '-',27)+"\n");
        buff.append("返券总金额为: "+ManipulatePrecision.doubleToString(je));
        if (je > 0)
        {
        	new MessageBox(buff.toString());
        }
        
        // 设置
        if (fj.size() > 0) this.zq = fj;
        else this.zq = null;
        if (goodsinfo.size() > 0) this.gift = goodsinfo;
        else this.gift = null;
	}
	
//	 打印赠券
	public void printSaleTicketMSInfo()
	{
		if (this.zq == null || this.zq.size() <= 0)
		{
			return ;
		}
		
		if (this.salemsinvo != 0 && salehead.fphm != this.salemsinvo)
		{
			this.salemsinvo = 0;
			this.zq = null;
			this.gift = null;
			return ;
		}
		
		for (int i = 0; i < this.zq.size(); i++)
		{
			GiftGoodsDef def = (GiftGoodsDef) this.zq.elementAt(i);
			if (!def.type.trim().equals("99") && !def.type.trim().equals("4"))
			{
				
	            if(GiftBillMode.getDefault(def.type).checkTemplateFile())
	            {
	            	GiftBillMode.getDefault(def.type).setTemplateObject(salehead, def);
	            	GiftBillMode.getDefault(def.type).PrintGiftBill();
	            	Printer.getDefault().cutPaper_Journal();
	            	continue;
	            }
	            
				Printer.getDefault().printLine_Journal("收银机号："+salehead.syjh+"  小票号："+Convert.increaseLong(salehead.fphm, 8));
				if (SellType.ISCOUPON(salehead.djlb))
					Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "买券交易", 1, 37, 38,2));

	            Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "				手 工 券", 1, 37, 38));
	            Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "=================================================", 1, 37, 38));
				Printer.getDefault().printLine_Journal("== 券  号  : "+def.code);
				Printer.getDefault().printLine_Journal("== 券信息  : "+def.info);
				Printer.getDefault().printLine_Journal("== 券总额  : "+def.je);
	            if (salehead.printnum > 0)
	        	{
	            	Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "				重 打 印", 1, 37, 38));
	        	}
				Printer.getDefault().printLine_Journal("== 券有效期: "+def.memo);
				Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "=================================================", 1, 37, 38));
				Printer.getDefault().cutPaper_Journal();
			}
		}
	}



}
