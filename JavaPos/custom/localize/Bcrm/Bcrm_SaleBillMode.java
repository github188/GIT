package custom.localize.Bcrm;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.PrintTemplate.GiftBillMode;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bhls.Bhls_SaleBillMode;


public class Bcrm_SaleBillMode extends Bhls_SaleBillMode
{
	/*public Vector zq = null;
	public Vector gift = null;*/
	
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
//            	new MessageBox("本次交易电子券返到"+sh.hykh+"\n返券金额为 "+ManipulatePrecision.doubleToString(g.je));
            	new MessageBox(Language.apply("本次交易电子券返到{0}\n返券金额为 {1}" ,new Object[]{sh.hykh ,ManipulatePrecision.doubleToString(g.je)}));
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
        	buff.append(g.code+"   "+g.info+"      "+Convert.increaseChar(ManipulatePrecision.doubleToString(g.je), 14)+"\n");
        	je += g.je;
        }
        buff.append(Language.apply("返券总金额为: ")+Convert.increaseChar(ManipulatePrecision.doubleToString(je), 14));
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

	public boolean needMSInfoPrintGrant()
	{
		if (this.zq != null && this.zq.size() > 0) return true;
		return false;
	}
	
	// 打印赠券
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
			GiftGoodsDef def = (GiftGoodsDef) zq.elementAt(i);
			
            if(GiftBillMode.getDefault().checkTemplateFile())
            {
            	GiftBillMode.getDefault().setTemplateObject(salehead, def);
            	GiftBillMode.getDefault().PrintGiftBill();
            	Printer.getDefault().cutPaper_Journal();
            	continue;
            }
            
			Printer.getDefault().printLine_Normal("===================================");
			Printer.getDefault().printLine_Normal("=="+Language.apply("券  号: ")+def.code);
			Printer.getDefault().printLine_Normal("=="+Language.apply("券信息: ")+def.info);
			Printer.getDefault().printLine_Normal("=="+Language.apply("券金额: ")+def.je);
			Printer.getDefault().printLine_Normal("=="+Language.apply("券备注: ")+def.memo);
			Printer.getDefault().printLine_Normal("==================================");
			Printer.getDefault().cutPaper_Normal();
		}
	}
	
	// 打印赠品信息，测试时使用，打印结构再议
	public void printerGift()
	{
		if (this.gift == null || this.gift.size() <= 0)
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

		for (int i = 0; i < this.gift.size(); i++)
		{
			GiftGoodsDef def = (GiftGoodsDef) gift.elementAt(i);
			Printer.getDefault().printLine_Normal(def.code+" "+def.info+" "+def.sl+" "+def.je);
		}
		
		Printer.getDefault().cutPaper_Normal();
	}
	
    public void printAppendBill()
    {
    	super.printAppendBill();
    	
    	printerGift();
    }
}
