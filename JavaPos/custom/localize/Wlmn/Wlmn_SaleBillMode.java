package custom.localize.Wlmn;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bhcm.Bhcm_SaleBillMode;

public class Wlmn_SaleBillMode extends Bhcm_SaleBillMode
{
	public void printBottom()
	{
		if (zq != null)
		{
			StringBuffer line = new StringBuffer();
			double je = 0;
			for (int i = 0; i < this.zq.size(); i++)
			{
				GiftGoodsDef def = (GiftGoodsDef) this.zq.elementAt(i);
				
				if (def.type.equals("4")||def.type.equals("2"))
				{
					String[] infos = def.info.split("&");
					String strje = ManipulatePrecision.doubleToString(def.je);
					line.append(Convert.appendStringSize("",infos[0],1,16,16,1)+":"+ Convert.appendStringSize("",strje,1,10,10,0)+String.valueOf(def.sl)+"张"+"\n");
					if (GlobalInfo.sysPara.printYXQ == 'Y') line.append(Convert.appendStringSize("","券有效期",1,16,16,1)+":"+ Convert.appendStringSize("",def.memo,1,24,24,0)+"\n");
					je +=(def.je*def.sl);
				}
			}
			
			if (je > 0)
			Printer.getDefault().printLine_Normal("本小票有返券:" + ManipulatePrecision.doubleToString(je));
			Printer.getDefault().printLine_Normal(line.toString());
		}
		

		// 设置打印区域
		setPrintArea("Bottom");
		
        printVector(getCollectDataString(Bottom,-1,Width));	

	}
	// 打印赠券
	public void printSaleTicketMSInfo()
	{
		return;
		/*
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
			if (!def.type.trim().equals("99") && !def.type.trim().equals("4") &&!def.type.trim().equals("2"))
			{
				
	            if(GiftBillMode.getDefault().checkTemplateFile())
	            {
	            	GiftBillMode.getDefault().setTemplateObject(salehead, def);
	            	GiftBillMode.getDefault().PrintGiftBill();
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
		*/
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
        }
        
        // 提示
        StringBuffer buff = new StringBuffer();
        double je = 0;
        for (int i = 0 ; i < fj.size(); i++)
        {
        	GiftGoodsDef g = (GiftGoodsDef)fj.elementAt(i);
        	buff.append(Convert.appendStringSize("",g.info,1,16,16,1)+":"+Convert.appendStringSize("",ManipulatePrecision.doubleToString(g.je*g.sl),1,10,10,0)+"\n");
        	//buff.append(g.code+"   "+g.info+"      "+Convert.increaseChar(ManipulatePrecision.doubleToString(g.je), 14)+"\n");
        	je += (g.je*g.sl);
        }
        buff.append(Convert.increaseChar("-", '-',27)+"\n");
        buff.append("券总金额为: "+ManipulatePrecision.doubleToString(je));
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
	
}
