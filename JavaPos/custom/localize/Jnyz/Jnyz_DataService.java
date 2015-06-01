package custom.localize.Jnyz;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Cmls.Cmls_DataService;


public class Jnyz_DataService extends Cmls_DataService
{	
	public int doRefundExtendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Vector retValue)
	{
		int ret = NetService.getDefault().sendSaleData(saleHead, saleGoods, salePayment, retValue, NetService.getDefault().getMemCardHttp(CmdDef.SENDCRMSELL), CmdDef.SENDCRMSELL);
		if (ret != 2 && ret != 0)
		{
			return ret;
		}
		
		if (GlobalInfo.sysPara.searchPosAndCUST.equals("Y"))
		{
			ret = NetService.getDefault().sendSaleData(saleHead, saleGoods, salePayment, retValue, NetService.getDefault().getMemCardHttp(CmdDef.SENDSELL), CmdDef.SENDSELL);
		}
		
		return ret;
	}
	
	public boolean getReceipt(String ysyjh,String yfphm,SaleHeadDef salehead)
	{
		boolean ret = false;
		if (GlobalInfo.isOnline)
		{
			ret = ((Jnyz_NetService)NetService.getDefault()).getReceipt(ysyjh,yfphm,salehead);
		}
		else
		{
			new MessageBox("必须联网使用！");
		}
		
		return ret;
	}
	
	//获取网上小票
	public boolean getReceipt(String ysyjh,String yfphm,SaleHeadDef salehead,Vector salegoods,Vector salepay)
	{
		boolean ret = false;
		if (GlobalInfo.isOnline)
		{
			ret = ((Jnyz_NetService)NetService.getDefault()).getReceipt(ysyjh,yfphm,salehead,salegoods,salepay);
		}
		else
		{
			new MessageBox("必须联网使用！");
		}
		
		return ret;
	}
	
	//获取支付宝单据金额
	public double getZfbJe(String syjh,String fphm)
	{
		double resultJe = 0;
		
		if (GlobalInfo.isOnline)
		{
			resultJe = ((Jnyz_NetService)NetService.getDefault()).getZfbJe(syjh,fphm);
		}
		else
		{
			new MessageBox("必须联网使用！");
		}
		return resultJe;
	}
	
}
