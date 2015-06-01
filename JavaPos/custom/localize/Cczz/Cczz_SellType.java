package custom.localize.Cczz;

import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.SaleHeadDef;

public class Cczz_SellType extends SellType {
	public String typeExchange(String type, char hhFlag,SaleHeadDef salehead)
    {
            if (type.equals(RETAIL_SALE))
            {
            	if (hhFlag == 'Y')
            	{
            		return "换货销售";
            	}
            	
            	if (salehead != null && salehead.num2 == 1)
            	{
            		return "预售销售";
            	}
            	
            	return "零售销售";
            }
            else if (type.equals( RETAIL_SALE_HC))
            {
            	if (hhFlag == 'Y')
            	{
            		return "冲换换销";
            	}
            	
            	if (salehead != null && salehead.num2 == 1)
            	{
            		return "红冲预售";
            	}
            	
            	return "红冲销售";
            }
            else if (type.equals( RETAIL_BACK))
            {
            	if (hhFlag == 'Y')
            	{
            		return "换货退货";
            	}
            	
            	if (salehead != null && salehead.num2 == 1)
            	{
            		return "预售退货";
            	}
         
            	return "零售退货";       
            }
            else if ( type.equals (RETAIL_BACK_HC))
            {
            	if (hhFlag == 'Y')
            	{
            		return "冲换换退";
            	}
            	
            	if (salehead != null && salehead.num2 == 1)
            	{
            		return "红冲预退";
            	}
            	
            	return "红冲退货";   
            }
            else
            {
            	return super.typeExchange(type, hhFlag,salehead);
            }
        
    }
    
    //判断是否高亮
    public boolean COMMONBUSINESS(String djlb, char hhflag,SaleHeadDef salehead)
    {
    	if (salehead != null && salehead.num2 == 1)
    	{
    		return false;
    	}
    	
    	return super.COMMONBUSINESS(djlb, hhflag, salehead);
    }
}
