package custom.localize.Sdyz;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;

public class Sdyz_CustomLocalize extends CustomLocalize
{
    public String getAssemblyVersion()
    {
    	return "1.0.2 bulid 2007.06.22";
    }	
    
    public DataService createDataService()
    {
		return new custom.localize.Sdyz.Sdyz_DataService();
    }
    
    public NetService createNetService()
    {
		return new custom.localize.Sdyz.Sdyz_NetService();
    }
    
    public AccessLocalDB createAccessLocalDB()
    {
		return new custom.localize.Sdyz.Sdyz_AccessLocalDB();
    }    
    
    public AccessBaseDB createAccessBaseDB()
    {
		return new custom.localize.Sdyz.Sdyz_AccessBaseDB();
    }
        
    public SaleBS createSaleBS()
    {
		return new custom.localize.Sdyz.Sdyz_SaleBS();
    }     
    
    public HykInfoQueryBS createHykInfoQueryBS()
    {
		return new custom.localize.Sdyz.Sdyz_HykInfoQueryBS();
    }      
    
    public CreatePayment createCreatePayment()
    {
		return new custom.localize.Sdyz.Sdyz_CreatePayment();
    }     
}

