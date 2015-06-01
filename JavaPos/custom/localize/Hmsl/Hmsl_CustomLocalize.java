package custom.localize.Hmsl;

import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Payment.CreatePayment;

import custom.localize.Bstd.Bstd_CustomLocalize;

/*
 * 邯单美食林
 */
public class Hmsl_CustomLocalize extends Bstd_CustomLocalize
{
	public String getAssemblyVersion()
	{
		// 主版本号 . 子版本号 [ 修正版本号 [. 编译版本号 ]]
		return "18174 build 2015.02.02";
	}

	public DataService createDataService()
	{
		return new Hmsl_DataService();
	}

	public CreatePayment createCreatePayment()
	{
		return new Hmsl_CreatePayment();
	}
	
	public AccessLocalDB createAccessLocalDB()
	{
		return new Hmsl_AccessLocalDB();
	}
	
	public LoadSysInfo createLoadSysInfo()
	{
		return new Hmsl_LoadSysInfo();
	}
	
}
