package custom.localize.Syss;

import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bhls.Bhls_CustomLocalize;

public class Syss_CustomLocalize extends Bhls_CustomLocalize {

    public String getAssemblyVersion()
    {
    	return "1.2.0 bulid 2009.07.17";
    }
    
	public SaleBillMode createSaleBillMode()
	{
		return new custom.localize.Syss.Syss_SaleBillMode();
	}
	
    public CreatePayment createCreatePayment()
    {
		return new custom.localize.Syss.Syss_CreatePayment();
    }
    
    protected boolean checkAllowUseBank(int seqno)
    {
    	// 全部注册码都允许使用金卡工程   
    	return true;
    }
    
    public AccessLocalDB createAccessLocalDB()
    {
    	return new custom.localize.Syss.Syss_AccessLocalDB();
    }
}
