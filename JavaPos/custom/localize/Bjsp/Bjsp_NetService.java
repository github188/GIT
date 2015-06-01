package custom.localize.Bjsp;

import java.util.Vector;

import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Cmls.Cmls_NetService;

public class Bjsp_NetService extends Cmls_NetService
{
	 public boolean createWebServerConn()
	 {
		 /**
    	try
    	{
    		if (GlobalInfo.axis == null)
    		{
    			GlobalInfo.axis = new Bjsp_AxisWebService();
    			
    			((Bjsp_AxisWebService)GlobalInfo.axis).createWebServerConn();
    		}
    		
    		return true;
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    		return false;
    	}
    	*/
		 return super.createWebServerConn();
    }
	 
	public int sendSaleData(SaleHeadDef saleHead, Vector saleGoods,Vector salePayment,Vector retValue,Http http,int commandCode)
	{
		for (int i = 0;i < saleGoods.size();i++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef)saleGoods.elementAt(i);
			
			if (sgd.batch != null && !sgd.batch.equals("")) continue;
			
			sgd.batch = sgd.fph;
		}
		
		return super.sendSaleData(saleHead, saleGoods, salePayment, retValue, http, commandCode);
	}
}
