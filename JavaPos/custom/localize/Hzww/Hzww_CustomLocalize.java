package custom.localize.Hzww;

import com.efuture.javaPos.Logic.SaleBS;

import custom.localize.Bstd.Bstd_CustomLocalize;

/*
 * 杭州午苇
 */
public class Hzww_CustomLocalize extends Bstd_CustomLocalize 
{

	// 客户化版本号
	public String getAssemblyVersion()
	{
		return "1.0.2 build 2014.10.23";
	}

	public SaleBS createSaleBS()
	{
		return new Hzww_SaleBS();
	}

}
