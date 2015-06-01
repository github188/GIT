package custom.localize.Cqhq;

import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;

import custom.localize.Bhcm.Bhcm_CustomLocalize;
import custom.localize.Bhcm.Bhcm_SaleBS;
import custom.localize.Cmls.Cmls_CustomLocalize;

//重庆环球，客户化读卡
public class Cqhq_CustomLocalize  extends Cmls_CustomLocalize
{
    public String getAssemblyVersion()
    {
    	return "16441 build 2013.12.18";
    }
	
	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new custom.localize.Cqhq.Cqhq_HykInfoQueryBS();
	}
	
}
