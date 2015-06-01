package custom.localize.Zxft;

import com.efuture.javaPos.Logic.SaleBS;

import custom.localize.Cmls.Cmls_CustomLocalize;
//中信富泰项目
public class Zxft_CustomLocalize extends Cmls_CustomLocalize
{
    public String getAssemblyVersion()
    {
    	return "12971 build 2012.12.12";
    }
    
    public SaleBS createSaleBS(){
    	return new custom.localize.Zxft.Zxft_SaleBS();
    }

}
