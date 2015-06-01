package custom.localize.Jwyt;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Logic.GoodsInfoQueryBS;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Payment.CreatePayment;
import custom.localize.Bstd.Bstd_CustomLocalize;

/**
 * 
 *  北京吴裕泰
 *
 */
public class Jwyt_CustomLocalize extends Bstd_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "1.1.18 bulid 2014.1.28";
	}

	public LoadSysInfo createLoadSysInfo()
	{
		return new Jwyt_LoadSysInfo();
	}

	public SaleBS createSaleBS()
	{
		return new Jwyt_SaleBS();
	}

	public AccessBaseDB createAccessBaseDB()
	{
		return new Jwyt_AccessBaseDB();
	}

	public TaskExecute createTaskExecute()
	{
		return new Jwyt_TaskExecute();
	}

	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new Jwyt_HykInfoQueryBS();
	}

	public MzkInfoQueryBS createMzkInfoQueryBS()
	{
		return new Jwyt_MzkInfoQueryBS();
	}

	public CreatePayment createCreatePayment()
	{
		return new Jwyt_CreatePayment();
	}

	public DataService createDataService()
	{
		return new Jwyt_DataService();
	}

	public NetService createNetService()
	{
		return new Jwyt_NetService();
	}

	public GoodsInfoQueryBS createGoodsInfoQueryBS()
	{
		return new Jwyt_GoodsInfoQueryBS();
	}

	public AccessLocalDB createAccessLocalDB()
	{
		return new Jwyt_AccessLocalDB();
	}
}
