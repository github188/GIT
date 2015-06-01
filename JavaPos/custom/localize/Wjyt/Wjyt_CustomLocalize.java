package custom.localize.Wjyt;

import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import custom.localize.Bstd.Bstd_CustomLocalize;

public class Wjyt_CustomLocalize extends Bstd_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "1.0.7 bulid 2013.01.17";
	}

	public SaleBS createSaleBS()
	{
		return new Wjyt_SaleBS();
	}
	
	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new Wjyt_HykInfoQueryBS();
	}
}
