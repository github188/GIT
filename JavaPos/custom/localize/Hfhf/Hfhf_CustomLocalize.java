package custom.localize.Hfhf;

import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;

import custom.localize.Bstd.Bstd_CustomLocalize;

/*
 * 合肥红府
 */
public class Hfhf_CustomLocalize extends Bstd_CustomLocalize
{
	public String getAssemblyVersion()
	{
		// 主版本号 . 子版本号 [ 修正版本号 [. 编译版本号 ]]
		return "1.0.12 build 2014.08.20";
	}

	public SaleBS createSaleBS()
	{
		return new Hfhf_SaleBS();
	}

	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new Hfhf_HykInfoQueryBS();
	}

	public CreatePayment createCreatePayment()
	{
		return new Hfhf_CreatePayment();
	}

	public DataService createDataService()
	{
		return new Hfhf_DataService();
	}

	public TaskExecute createTaskExecute()
	{
		return new Hfhf_TaskExecute();
	}

	public AccessLocalDB createAccessLocalDB()
	{
		return new Hfhf_AccessLocalDB();
	}

	public AccessDayDB createAccessDayDB()
	{
		return new Hfhf_AccessDayDB();
	}
}
