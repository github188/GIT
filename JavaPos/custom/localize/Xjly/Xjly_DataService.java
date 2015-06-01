package custom.localize.Xjly;


import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Cmls.Cmls_DataService;

public class Xjly_DataService extends Cmls_DataService
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
