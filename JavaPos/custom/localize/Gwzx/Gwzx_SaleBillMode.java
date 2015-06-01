package custom.localize.Gwzx;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.GiftBillMode;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bcrm.Bcrm_SaleBillMode;

public class Gwzx_SaleBillMode extends Bcrm_SaleBillMode
{
	protected final static int SBM_BxJf = 201;
	
	protected String extendCase(PrintTemplateItem item, int index) 
	{
		String line = null;
		switch (Integer.parseInt(item.code))
		{
			case SBM_BxJf:
                if (salehead.num4 == 0)
                {
                    line = "&!";
                }
                else
                {
                    line = ManipulatePrecision.doubleToString(salehead.num4);
                }
				break;
		}
		return line;
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
	}
}
