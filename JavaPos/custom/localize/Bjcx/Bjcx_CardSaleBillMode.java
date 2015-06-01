package custom.localize.Bjcx;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.CardSaleBillMode;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Bjcx_CardSaleBillMode extends CardSaleBillMode
{

	protected final static int CSBM_printType = 107;//电子钱包存入标识
	private String printType = null;
	
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
        			 //电子钱包标识
        		 	case CSBM_printType:
        		 		line = null;//this.printType;暂未用
        		 		
                        break;
                        
        		 	case CSBM_cardcode:        
        		 		pay = (SalePayDef) originalsalepay.elementAt(index);
                        mode = DataService.getDefault().searchPayMode(pay.paycode);
                        
		                if (isExistPaycode(mode.code))
		                {
		                	String flag="";
		                	if (pay.paycode.equals("0111"))
		                	{
		                		if (pay.ybje > 0)
		                		{
		                			flag = "（消费）";
		                		}
		                		else if (pay.ybje < 0)
		                		{
		                			flag = "（存入）";
		                		}
		                		
		                	}
		                	
		                	line = pay.payno + flag;
		                	
		                	num = num + 1;
		                }
		                
		                break;
		                
		                
        		 	case CSBM_salemoney:
        		 		pay = (SalePayDef) originalsalepay.elementAt(index);
                        mode = DataService.getDefault().searchPayMode(pay.paycode);
                        
		                if (isExistPaycode(mode.code))
		                {
		                	double ybje = pay.ybje * SellType.SELLSIGN(salehead.djlb);
			                if (pay.paycode.equals("0111"))
		                	{
		                		if (pay.ybje > 0)
		                		{
		                			ybje = 1*ybje;
		                		}
		                		else if (pay.ybje < 0)
		                		{
		                			ybje = -1 * ybje;
		                		}
		                		
		                	}
			                
        		 			line =  ManipulatePrecision.doubleToString(ybje);
        		 			
        		 			if (pay.hl == 0)
		                    {
		                        pay.hl = 1;
		                    }
		
		                    hj += ybje * pay.hl; //(pay.ybje * pay.hl * SellType.SELLSIGN(salehead.djlb));
		                }
		                
		                break;
		                
		                
        		 	default:
        		 		return super.getItemDataString(item, index);
        		 		
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


	public void setPrintType(String printType)
	{
		this.printType = printType;
	}
}
