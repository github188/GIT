package custom.localize.Bjcx;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Cmls.Cmls_DataService;

public class Bjcx_DataService extends Cmls_DataService {
	

    
	// 获取私有参数
	public boolean getNetSysPara()
	{
		if (GlobalInfo.isOnline)
        {
            if (!NetService.getDefault().getSysPara()) return false;
        }

        // 读取CRM参数信息
		if (GlobalInfo.isOnline)
		{
			// 读取POS参数但不做paraFinish处理,以避免重复处理
			if (!AccessLocalDB.getDefault().readSysPara(false)) return false;
			/*
			if (NetService.getDefault().getMemCardHttp(CmdDef.GETCRMPARA) != GlobalInfo.localHttp)
			{
				if (!NetService.getDefault().getSysPara(null,false,CmdDef.GETCRMPARA)) return false;
			}*/
			
			if (!NetService.getDefault().getSysPara(NetService.getDefault().getMemCardHttp(CmdDef.GETCRMPARA),false,CmdDef.GETCRMPARA)) return false;
		}
        
        return AccessLocalDB.getDefault().readSysPara();
	}
	
	 public boolean getBackSaleInfo(String syjh, String fphm, SaleHeadDef shd, Vector saleDetailList, Vector payDetail)
	    {
	        try
	        {
	        	if (super.getBackSaleInfo(syjh, fphm, shd, saleDetailList, payDetail))
	        	{
	        		if (shd.fphm == -1)
	        		{
	        			new MessageBox("已开增值税发票，不允许退货!");
	        			return false;
	        		}
	        		return true;
	        	}
	        	else
	        	{
	        		return false;
	        	
	        	}
	            
	        }
	        catch (Exception ex)
	        {
	            ex.printStackTrace();

	            return false;
	        }
	    }
	
	public boolean getCustomer(CustomerDef cust, String track)
    {
		boolean blnRet =false;
		
		try
		{
			blnRet = super.getCustomer(cust, track);
			if (blnRet && GlobalInfo.isOnline)
			{
				boolean isGroupMemberCard = false;//是否为即机关卡
				if (cust.func.trim().length()>=3 && cust.func.trim().charAt(2) == 'Y')
		        {
					isGroupMemberCard = true;						
		        }
				if (isGroupMemberCard)
				{
					//机关卡只能在团购单据类型下使用
					if (!SellType.isGroupbuy(GlobalInfo.saleform.sale.saleBS.saletype))
					{
						blnRet = false;
						new MessageBox("操作失败，机关卡不在此处使用！");
						return blnRet;
					}
				}
				else
				{
					//非机关卡不能在团购单据类型下使用
					if (SellType.isGroupbuy(GlobalInfo.saleform.sale.saleBS.saletype))
					{
						blnRet = false;
						new MessageBox("操作失败，非机关卡不在此处使用！");
						return blnRet;
					}
				}
				
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		
		}
		
		return blnRet;
		
    }

}
