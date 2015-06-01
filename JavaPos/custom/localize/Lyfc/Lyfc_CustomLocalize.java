package custom.localize.Lyfc;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Logic.SaleBS;

import custom.localize.Bstd.Bstd_CustomLocalize;

/**
 * 
 * 洛阳丰采
 *
 */
public class Lyfc_CustomLocalize extends Bstd_CustomLocalize
{
	public String getAssemblyVersion()
	{
		// 主版本号 . 子版本号 [ 修正版本号 [. 编译版本号 ]]
		return "1.0.4 build 2014.11.05";
	}

	public SaleBS createSaleBS()
	{
		return new Lyfc_SaleBS();
	}

	public NetService createNetService()
	{
		return new Lyfc_NetService();
	}
}
