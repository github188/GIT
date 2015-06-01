package custom.localize.Nnmk;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Struct.CustomerDef;

public class Nnmk_ExchangeCardBS
{
	public CustomerDef oldc = null;
	public CustomerDef newc = null;
	Nnmk_NetService ns = null;
	public boolean getCustInfo(String string, String string2, String string3,String type,Text card,StyledText text)
	{
		if (ns == null) ns = new Nnmk_NetService();
		if (type.equals("oldc"))
		{
			oldc = new CustomerDef();
			//注意：此地是findcust ，保证过程和findcustomer过程保持一致
			boolean done = ns.checkCustomer(ns.getMemCardHttp(CmdDef.FINDCUSTOMER), oldc, string2, "1");
			if (done)
			{
				card.setText(oldc.code);
				text.setText(oldc.name);
				newc = null;
				return true;
			}
			else
			{
				oldc = null;
				return false;
			}
		}
		if (type.equals("newc"))
		{
			newc = new CustomerDef();
			//注意：此地是findcust ，保证过程和findcustomer过程保持一致
			boolean done = ns.checkCustomer(ns.getMemCardHttp(CmdDef.FINDCUSTOMER), newc, string2, "2");
			if (done)
			{
				card.setText(newc.code);
				if (validCard())
				{
					
				}
				return true;
			}
			else
			{
				newc = null;
			}
		}
		return false;
	}
	
	public boolean validCard()
	{
		if (newc != null && oldc != null)
		{
			if (new MessageBox("新卡号为："+newc.code+"\n是否换卡？",null,true).verify() == GlobalVar.Key1)
			{
				if (ns == null) ns = new Nnmk_NetService();
				String[] infos = new String[6];
				boolean done = ns.changeCard(oldc,newc,infos);
				if (done)
				{
					new MessageBox(oldc.code+"和"+newc.code+"换卡成功");
					return true;
				}
			}
		}
		return false;
	}

}
