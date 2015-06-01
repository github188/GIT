package custom.localize.Wjjy;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bhls.Bhls_CustomLocalize;


public class Wjjy_CustomLocalize extends Bhls_CustomLocalize
{
    public String getAssemblyVersion()
    {
        return "1.1.1 bulid 2008.10.09";
    }

    public CreatePayment createCreatePayment()
    {
        return new Wjjy_CreatePayment();
    }

    public DataService createDataService()
    {
        return new Wjjy_DataService();
    }

    public SaleBS createSaleBS()
    {
        return new Wjjy_SaleBS();
    }

    public HykInfoQueryBS createHykInfoQueryBS()
    {
        return new Wjjy_HykInfoQueryBS();
    }

    public NetService createNetService()
    {
        return new Wjjy_NetService();
    }

    public SaleBillMode createSaleBillMode()
    {
        return new Wjjy_SaleBillMode();
    }
}
