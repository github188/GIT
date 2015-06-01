package custom.localize.Lrtd;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.MutiSelectBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bstd.Bstd_CustomLocalize;

//丽日超市
public class Lrtd_CustomLocalize extends Bstd_CustomLocalize
{
	public String getAssemblyVersion()
	{
		//主版本号 . 子版本号 [ 修正版本号 [. 编译版本号 ]]
		return "18138 build 2015.01.14";
	}
	
	
	public SaleBS createSaleBS()
	{
		return new Lrtd_SaleBS();
	}
	
	public NetService createNetService()
	{
		return new Lrtd_NetService();
	}
	
	public MutiSelectBS createMutiSelectBS()
	{
		return new Lrtd_MutiSelectBS();
	}
	public DataService createDataService()
	{
		return new Lrtd_DataService();
	}
	public AccessLocalDB createAccessLocalDB()
    {
		return new custom.localize.Lrtd.Lrtd_AccessLocalDB();
    }
    public SaleBillMode createSaleBillMode()
    {
    	return new custom.localize.Lrtd.Lrtd_SaleBillMode();
    }
}
