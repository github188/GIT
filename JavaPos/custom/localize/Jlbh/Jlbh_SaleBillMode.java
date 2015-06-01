package custom.localize.Jlbh;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Cmls.Cmls_SaleBillMode;

public class Jlbh_SaleBillMode extends Cmls_SaleBillMode
{
	Vector v1 = new Vector();
	
    protected String extendCase(PrintTemplateItem item, int index)
    {
    	String line = null;
    	switch (Integer.parseInt(item.code))
		{
			case SBM_ye: // 付款余额
				if (((SalePayDef) salepay.elementAt(index)).paycode.equals("0401"))
				{
					line = ManipulatePrecision.doubleToString(((SalePayDef) salepay.elementAt(index)).kye);
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
		v1.removeAllElements();
		
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
        for (int i = 0 ; i < fj.size(); i++)
        {
        	GiftGoodsDef g = (GiftGoodsDef)fj.elementAt(i);
        	
        	if (g.type.trim().equals("90")) // 停车券
        	{
        		continue;
        	}
        	String l = Convert.appendStringSize("",g.info,1,16,17,1);
        	
        	buff.append(l+":"+Convert.appendStringSize("",ManipulatePrecision.doubleToString(g.je),1,10,10,0)+"\n");
        	v1.add(new String[]{String.valueOf(sh.fphm),l+":"+Convert.appendStringSize("",ManipulatePrecision.doubleToString(g.je),1,10,10,0)});
        	//buff.append(g.code+"   "+g.info+"      "+Convert.increaseChar(ManipulatePrecision.doubleToString(g.je), 14)+"\n");
        	je += g.je;
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
	
	public void printBottom()
	{
		if (v1.size() > 0)
		{
			boolean done = false;
			for (int i= 0 ;i < v1.size(); i++)
			{
				String[] row = (String[]) v1.elementAt(i);
				if (Convert.toLong(row[0]) == salehead.fphm)
				{
					if (!done)
					{
						printLine("返券汇总如下:");
						done =true;
					}
					printLine(row[1]);
				}
			}
		}
		super.printBottom();
	}
}
