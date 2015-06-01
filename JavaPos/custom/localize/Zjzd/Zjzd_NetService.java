package custom.localize.Zjzd;

import java.util.Vector;

import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bcrm.Bcrm_NetService;


public class Zjzd_NetService extends Bcrm_NetService {
    public int sendExtendSaleData(SaleHeadDef saleHead, Vector saleGoods,
        Vector salePayment, Vector retValue) {
        if (saleHead.bc == '#') {
            return sendSaleData(saleHead, saleGoods, salePayment, retValue,
                null, 38);
        }

        return sendSaleData(saleHead, saleGoods, salePayment, retValue,
            getMemCardHttp(45), 45);
    }
}
