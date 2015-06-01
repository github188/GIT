package custom.localize.Bhcm;

import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bcrm.Bcrm_DataService;

public class Bhcm_DataService extends Bcrm_DataService
{
    public void updateSendSaleData(SaleHeadDef saleHead, String memo, double value, Sqldb sql)
    {
        super.updateSendSaleData(saleHead, memo, value, sql);

        if ((memo != null) && memo.trim().equals(""))
        {
            return;
        }

        if (sql != null)
        {
            return;
        }

        saleHead.str3 = memo;

        String line = "update SALEHEAD set STR3 = '" + saleHead.str3 + "' where syjh = '" + ConfigClass.CashRegisterCode + "' and  fphm = " +
                      saleHead.fphm;

        GlobalInfo.dayDB.executeSql(line);
    }
}
