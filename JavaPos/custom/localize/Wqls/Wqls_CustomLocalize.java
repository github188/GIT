package custom.localize.Wqls;

import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bhls.Bhls_CustomLocalize;

// 老万千百货
public class Wqls_CustomLocalize extends Bhls_CustomLocalize 
{
    public String getAssemblyVersion()
    {
    	return "2.2.5 bulid 2009.08.26";
    }
    
    protected boolean checkAllowUseBank(int seqno)
    {
    	// 全部注册码都允许使用金卡工程
    	return true;
    }
    
    public CreatePayment createCreatePayment()
    {
		return new custom.localize.Wqls.Wqls_CreatePayment();
    }
    
    public SaleBS createSaleBS()
    {
		return new custom.localize.Wqls.Wqls_SaleBS();
    }
    
	public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Wqls.Wqls_SaleBillMode();
	}
	
    public AccessLocalDB createAccessLocalDB()
    {
    	return new custom.localize.Wqls.Wqls_AccessLocalDB();
    }
    
    public HykInfoQueryBS createHykInfoQueryBS()
    {
    	return new custom.localize.Wqls.Wqls_HykInfoQueryBS();
    }
    
    public AccessBaseDB createAccessBaseDB()
    {
    	return new custom.localize.Wqls.Wqls_AccessBaseDB();
    }
}
