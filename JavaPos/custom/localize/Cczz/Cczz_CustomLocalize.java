package custom.localize.Cczz;

import java.io.BufferedReader;
import java.io.IOException;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.TaskExecute;
import com.efuture.javaPos.Logic.BankLogQueryBS;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bcrm.Bcrm_CustomLocalize;

//长春卓展
public class Cczz_CustomLocalize extends Bcrm_CustomLocalize
{
    public String getAssemblyVersion()
    {
    	return "18288 build 2015.04.14";
    }
    
    public SaleBS createSaleBS()
    {
		return new custom.localize.Cczz.Cczz_SaleBS();
    }
    
    public TaskExecute createTaskExecute()
    {
    	return new Cczz_TaskExecute();
    }
    
	public SaleBillMode createSaleBillMode()
	{
		BufferedReader br = CommonMethod.readFileGBK("CczzKP.ini");
		try {
			if(br != null){
				if(br.readLine() != null)
				{
					return new custom.localize.Cczz.Cczz_SaleBillMode();
				}
				else
				{
					return new custom.localize.Cczz.Cczz_SaleBillMode_old();
				}
			}
		} catch (IOException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
		return new custom.localize.Cczz.Cczz_SaleBillMode_old();
	}
	
	public DataService createDataService()
    {
		return new custom.localize.Cczz.Cczz_DataService();
    }
	
    public AccessDayDB createAccessDayDB()
    {
    	return new Cczz_AccessDayDB();
    }
    
    public AccessLocalDB createAccessLocalDB()
    {
    	return new Cczz_AccessLocalDB();
    }
    
	public HykInfoQueryBS createHykInfoQueryBS()
	{
		return new Cczz_HykInfoQueryBS();
	}
	
	public MzkInfoQueryBS createMzkInfoQueryBS()
	{
		return new Cczz_MzkInfoQueryBS();
	}	
	
	public DisplaySaleTicketBS createDisplaySaleTicketBS()
	{
		return new Cczz_DisplaySaleTicketBS();
	}
	
    public CreatePayment createCreatePayment()
    {
    	return new Cczz_CreatePayment();
    }
    
	public MenuFuncBS createMenuFuncBS()
	{
		return new Cczz_MenuFuncBS();
	} 
	
    public SellType createSellType()
    {
    	return new Cczz_SellType();
    }
    
	public BankLogQueryBS createBankCardQueryBS()
	{
		return new Cczz_BankLogQueryBS();
	}
	
    public NetService createNetService()
    {
    	return new Cczz_NetService();
    }
}
