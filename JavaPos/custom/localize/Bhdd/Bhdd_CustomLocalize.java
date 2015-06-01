package custom.localize.Bhdd;

import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bhls.Bhls_CustomLocalize;


public class Bhdd_CustomLocalize extends Bhls_CustomLocalize
{
    public String getAssemblyVersion()
    {
        return "1.0.1 bulid 2009.07.14";
    }

    public SaleBillMode createSaleBillMode()
    {
        return new custom.localize.Bhdd.Bhdd_SaleBillMode();
    }    

    public CreatePayment createCreatePayment()
    {
        return new custom.localize.Bhdd.Bhdd_CreatePayment();
    }
}
