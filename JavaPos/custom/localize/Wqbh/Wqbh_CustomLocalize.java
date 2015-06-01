package custom.localize.Wqbh;

import java.io.BufferedReader;
import java.io.IOException;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Logic.CouponQueryInfoBS;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.LoginBS;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Logic.SaleTicketListBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bcrm.Bcrm_CustomLocalize;
//万达百货（万千百货）
public class Wqbh_CustomLocalize extends Bcrm_CustomLocalize
{
	public String getAssemblyVersion()
	{
		return "17338 build 2015.03.04";
	}

	protected boolean checkAllowUseBank(int seqno)
	{
		// 全部注册码都允许使用金卡工程
		return true;
	}

	public CreatePayment createCreatePayment()
	{
		return new custom.localize.Wqbh.Wqbh_CreatePayment();
	}

	public SaleBS createSaleBS()
	{
		return new custom.localize.Wqbh.Wqbh_SaleBS();
	}

	public SaleBillMode createSaleBillMode()
	{

		BufferedReader br = CommonMethod.readFileGBK(GlobalVar.ConfigPath + "//WqbhKP.ini");
		try {
			if(br != null){
				if(br.readLine() != null)
				{
					return new custom.localize.Wqbh.Wqbh_SaleBillMode_ZG();
				}
				else
				{
					return new custom.localize.Wqbh.Wqbh_SaleBillMode();
				}
			}
		} catch (IOException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
		return new custom.localize.Wqbh.Wqbh_SaleBillMode();
	}

	public AccessLocalDB createAccessLocalDB()
	{
		return new custom.localize.Wqbh.Wqbh_AccessLocalDB();
	}

	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new custom.localize.Wqbh.Wqbh_HykInfoQueryBS();
	}

	public AccessBaseDB createAccessBaseDB()
	{
		return new custom.localize.Wqbh.Wqbh_AccessBaseDB();
	}

	public NetService createNetService()
	{
		return new custom.localize.Wqbh.Wqbh_NetService();
	}

	public CouponQueryInfoBS createCouponQueryInfoBS()
	{
		return new custom.localize.Wqbh.Wqbh_CouponQueryInfoBS();
	}

	public AccessDayDB createAccessDayDB()
	{
		return new custom.localize.Wqbh.Wqbh_AccessDayDB();
	}

	public SaleTicketListBS createSaleTicketListBS()
	{
		return new custom.localize.Wqbh.Wqbh_SaleTicketListBS();
	}

	public DisplaySaleTicketBS createDisplaySaleTicketBS()
	{
		return new custom.localize.Wqbh.Wqbh_DisplaySaleTicketBS();
	}

	public TaskExecute createTaskExecute()
	{
		return new custom.localize.Wqbh.Wqbh_TaskExecute();
	}
	
	public DataService createDataService()
	{
		return new custom.localize.Wqbh.Wqbh_DataService();
	}

	public MenuFuncBS createMenuFuncBS()
	{
		return new custom.localize.Wqbh.Wqbh_MenuFuncBS();
	}
	
	public MzkInfoQueryBS createMzkInfoQueryBS()
	{
		return new custom.localize.Wqbh.Wqbh_MzkInfoQuerBS();
	}
	
	public LoginBS createLoginBS()
	{
		return new custom.localize.Wqbh.Wqbh_LoginBS();
	}
	
}
