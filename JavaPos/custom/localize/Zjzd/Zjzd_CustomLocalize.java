package custom.localize.Zjzd;

import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;

import custom.localize.Bcrm.Bcrm_CustomLocalize;


public class Zjzd_CustomLocalize extends Bcrm_CustomLocalize {
    public String getAssemblyVersion() {
        return "1.0.0 bulid 2010.09.20";
    }

    public SaleBS createSaleBS() {
        return new Zjzd_SaleBS();
    }

    public NetService createNetService() {
        return new Zjzd_NetService();
    }

    public SaleBillMode createSaleBillMode() {
        return new Zjzd_SaleBillMode();
    }

    public CreatePayment createCreatePayment() {
        return new Zjzd_CreatePayment();
    }

    public AccessBaseDB createAccessBaseDB() {
        return new custom.localize.Zjzd.Zjzd_AccessBaseDB();
    }
}
