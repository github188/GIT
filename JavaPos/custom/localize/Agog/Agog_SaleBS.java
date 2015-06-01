package custom.localize.Agog;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bstd.Bstd_SaleBS;

public class Agog_SaleBS extends Bstd_SaleBS
{
	public void enterInputYYY()
	{
		super.enterInputYYY();

		CustomerDef cust = new CustomerDef();

		if (Agog_VipCaller.getDefault().queryVipFromRoomCode(cust, saleEvent.yyyh.getText()))
		{
			memberGrantFinish(cust);
			saleEvent.setVIPInfo(getVipInfoLabel());
		}
	}

	public boolean memberGrantFinish(CustomerDef cust)
	{
		if (super.memberGrantFinish(cust))
		{
			this.saleHead.ljjf = cust.valuememo;
			return true;
		}
		
		return false;
	}

	public void takeBackTicketInfo(SaleHeadDef thsaleHead, Vector thsaleGoods, Vector thsalePayment)
	{
		CustomerDef cust = new CustomerDef();

		if (!Agog_VipCaller.getDefault().queryVip(cust, thsaleHead.hykh,false))
			return;

		if (cust.code == null || cust.code.trim().equals(""))
		{
			new MessageBox("原会员卡[" + thsaleHead.hykh + "]信息查询失败!");
			return;
		}

		memberGrantFinish(cust);
		saleEvent.setVIPInfo(getVipInfoLabel());
	}

}
