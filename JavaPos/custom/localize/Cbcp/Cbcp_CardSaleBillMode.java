package custom.localize.Cbcp;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.CardSaleBillMode;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Cbcp_CardSaleBillMode extends CardSaleBillMode {

	public String getItemDataString(PrintTemplateItem item, int index)
    {
        String line = null;
        SalePayDef pay = null;
        PayModeDef mode = null;
        
        try
        {
        	 line = extendCase(item, index);
        	 
        	 if (line == null)
             {
        		 switch (Integer.parseInt(item.code))
                 {
        		 	case CSBM_payye:
        		 		pay = (SalePayDef) originalsalepay.elementAt(index);
                        mode = DataService.getDefault().searchPayMode(pay.paycode);
		                if (isExistPaycode(mode.code))
		                {
		                	
		                	if(GlobalInfo.sysPara.isUnityMzkSrv == 'N')
		            		{
			                	if(SellType.ISBACK(salehead.djlb))
			                	{
			                		if(pay.str1.trim().getBytes().length==16)//新世纪卡卡余额需要加上退的金额
				                	{
			                			line = ManipulatePrecision.doubleToString(pay.kye+pay.ybje);
				                	}
			                		else
				                	{
				                		line = ManipulatePrecision.doubleToString(pay.kye);
				                	}
			                		
			                	}
			                	else
			                	{
			                		if(pay.str1.trim().getBytes().length==16)//新世纪卡卡余额需要减去消费金额
				                	{
				                		line = ManipulatePrecision.doubleToString(pay.kye-pay.ybje);	 
				                	}
				                	else
				                	{
				                		line = ManipulatePrecision.doubleToString(pay.kye);
				                	}
			                	}
		            		}
		                	else
		                	{
		                		line = ManipulatePrecision.doubleToString(pay.kye);
		                	}
		                	
		                }
                        break;
        		 	case CSBM_payYye:
        		 		
        		 		if(GlobalInfo.sysPara.isUnityMzkSrv == 'N')
	            		{
        		 		
	        		 		if(SellType.ISBACK(salehead.djlb))
		                	{
		                		if(pay.str1.trim().getBytes().length==16)
			                	{
		                			line = ManipulatePrecision.doubleToString(pay.kye);
			                	}
		                		else
			                	{
		                			//重百提货卡需要-退的金额，显示原金额。
			                		line = ManipulatePrecision.doubleToString(pay.kye-pay.ybje);
			                	}
		                		
		                	}
		                	else
		                	{
		                		if(pay.str1.trim().getBytes().length==16)
			                	{
			                		line = ManipulatePrecision.doubleToString(pay.kye);	 
			                	}
			                	else
			                	{
			                		//重百提货卡需要+消费的金额，显示原金额。
			                		line = ManipulatePrecision.doubleToString(pay.kye+pay.ybje);
			                	
			                	}
		                	}
	            		}
        		 		else
	                	{
	                		line = ManipulatePrecision.doubleToString(pay.kye+pay.ybje);
	                	}
                        break;
                        
        		 	default:
        		 		line = super.getItemDataString(item, index);
                 }
             }
        	 
        	 return line;
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        	return null;
        }
    }
	
}
