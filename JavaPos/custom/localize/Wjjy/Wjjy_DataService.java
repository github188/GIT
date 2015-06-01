package custom.localize.Wjjy;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

import custom.localize.Bhls.Bhls_DataService;

public class Wjjy_DataService extends Bhls_DataService {
    // 面值卡交易
    public boolean sendMzkSale(MzkRequestDef req,MzkResultDef ret)
    {
        if (GlobalInfo.isOnline)
        {
        	if (req.paycode.equals("0508"))
        	{
        		return NetService.getDefault().sendMzkSale(NetService.getDefault().getMemCardHttp(CmdDef.SENDMZK), req, ret);
        	}
        	else
        	{
        		return NetService.getDefault().sendMzkSale(req,ret);
        	}
        }
        else
        {
        	new MessageBox("面值卡必须联网使用!");
        }
        
        return false;
    }
    
    public boolean getBackSellLimit(String oldsyjh,String oldfphm,String cardno,String memo,SpareInfoDef info)
    {
    	if (GlobalInfo.isOnline)
        {
    		return ((Wjjy_NetService)NetService.getDefault()).getBackSellLimit(oldsyjh, oldfphm, cardno, memo, info);
        }
        else
        {
        	info.num1 = 99999999;
        	return true;
        }
        
    }
    
    public boolean getCustomer(CustomerDef cust, String track)
    {
        if (GlobalInfo.isOnline)
        {
            if (!NetService.getDefault().getCustomer(cust,track))
            {
                return false;
            }
        }
        else
        {
        	// 配置了独立的会员卡服务器，则会员卡必须联网使用
        	if (GlobalInfo.sysPara.memcardsvrurl != null && GlobalInfo.sysPara.memcardsvrurl.trim().length() > 0)
        	{
        		if (track.indexOf("=") != -1)
        		{
        			cust.code = track.substring(0,track.indexOf("="));
        		}
        		else
        		{
        			cust.code = track;
        		}
        		
        		cust.name   = "脱网使用";
        		cust.status = "Y";
        		cust.track  = track;
        		cust.zkl    = 1;
        		return true;
        	}
        	
            if (!AccessBaseDB.getDefault().getCustomer(cust,track))
            {
            	new MessageBox("无此顾客卡信息!", null, false);
            	
                return false;
            }
        }
        
        return true;
    }
}
