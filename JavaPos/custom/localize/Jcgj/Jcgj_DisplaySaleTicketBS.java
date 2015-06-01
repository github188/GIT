package custom.localize.Jcgj;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Jcgj_DisplaySaleTicketBS extends DisplaySaleTicketBS
{
	protected boolean isMzkSell()
	{
		PayModeDef pmd = null;

		try
		{
			for (int i = 0; i < salepay.size(); i++)
			{
				SalePayDef spd = (SalePayDef) salepay.get(i);

				if ((pmd = DataService.getDefault().searchMainPayMode(spd.paycode)) == null) return false;

				// 储值卡付款要授权
				if (pmd.type == '4' || pmd.code.equals("0111") || pmd.code.equals("0509") || pmd.code.equals("0405") || pmd.code.equals("0408")) return true;

				// 电子券和打印券也需要授权
				if (GlobalInfo.sysPara.fjkyetype != null && !GlobalInfo.sysPara.fjkyetype.equals(""))
				{
					String s[] = GlobalInfo.sysPara.fjkyetype.split(";");
					for (int j = 0; j < s.length; j++)
					{
						String p[] = s[j].split("=");
						if (spd.paycode.trim().equals(p[0].trim()) && p.length >= 2) { return true; }
					}
				}
			}

			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	protected boolean checkSaleRedQuash()
	{
		boolean b = super.checkSaleRedQuash();
		if (!b) return b;
		else
		{
			if (SellType.CARD_SALE.equals(salehead.djlb))
			{
				new MessageBox("售卡交易不允许红冲！");
				return false;
			}
			return b;
		}
	}
	
	public void printSaleTicket()
	{
        try
        {
            // 检查发票是否打印完,打印完未设置新发票号则不能交易
            if (Printer.getDefault().getSaleFphmComplate())
            {
            	return ;
            }
            
            if (!((Jcgj_SaleBS)GlobalInfo.saleform.sale.saleBS).rePrintSellTicket()) return;
            
        	salehead.printnum++;
    		AccessDayDB.getDefault().updatePrintNum(salehead.syjh, String.valueOf(salehead.fphm), String.valueOf(salehead.printnum));
    		ProgressBox pb = new ProgressBox();
    		pb.setText("现在正在重打印小票,请等待.....");
    		printSaleTicket(salehead,salegoods,salepay,false);
    		pb.close();
        }
        catch (Exception er)
        {
            er.printStackTrace();
        }
	}
}
