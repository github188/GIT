package custom.localize.Zjzd;

import com.efuture.javaPos.Struct.PayModeDef;

import custom.localize.Bcrm.Bcrm_CreatePayment;


public class Zjzd_CreatePayment extends Bcrm_CreatePayment {
    public boolean allowQuickInputMoney(PayModeDef pay) {
        if ((pay.type == '4') && (pay.isbank == 'N')) {
            return true;
        }

        return super.allowQuickInputMoney(pay);
    }
}
