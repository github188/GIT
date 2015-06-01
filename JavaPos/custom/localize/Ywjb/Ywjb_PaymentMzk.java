package custom.localize.Ywjb;

import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;


public class Ywjb_PaymentMzk extends PaymentMzk
{
    public Ywjb_PaymentMzk()
    {
        super();
    }

    public Ywjb_PaymentMzk(PayModeDef mode, SaleBS sale)
    {
        super(mode, sale);
    }

    public Ywjb_PaymentMzk(SalePayDef pay, SaleHeadDef head)
    {
        super(pay, head);
    }

    public String getDisplayCardno()
    {
        if (mzkret.cardno.length() >= 8)
        {
            return mzkret.cardno.substring(0, 8);
        }
        else
        {
            return mzkret.cardno;
        }
    }

    protected void saveAccountMzkResultToSalePay()
    {
        super.saveAccountMzkResultToSalePay();

        //判断是否回收,str5设置Y,打印时检查
        if (SellType.ISSALE(mzkreq.invdjlb) && PaymentMzk.recycleStatus && (salepay.kye < mzkret.value3))
        {
            salepay.str5 = "Y";
            salepay.kye  = 0;
        }
    }

    protected boolean saveFindMzkResultToSalePay()
    {
        if (!super.saveFindMzkResultToSalePay()) return false;

        //储值卡序列号
        salepay.str1 = mzkret.memo;
        
        return true;
    }
}
