package custom.localize.Wjjy;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bhls.Bhls_PaymentMzk;


public class Wjjy_PaymentMzk extends Bhls_PaymentMzk
{
    public Wjjy_PaymentMzk()
    {
        super();
    }

    public Wjjy_PaymentMzk(PayModeDef mode, SaleBS sale)
    {
        super(mode, sale);
    }

    public Wjjy_PaymentMzk(SalePayDef pay, SaleHeadDef head)
    {
        super(pay, head);
    }

    public String getDisplayCardno()
    {
        return mzkret.cardno;
    }

    public void setRequestDataBySalePay()
    {
        // 根据salepay生成交易请求包
        super.setRequestDataBySalePay();

        try
        {
            mzkreq.passwd = mzkreq.track2.substring(mzkreq.track2.length() - 5);
        }
        catch (Exception er)
        {
            new MessageBox(er.getMessage());
        }
    }

    public boolean checkMzkMoneyValid()
    {
    	if (paymode.code.equals("0508"))
		{
			for (int i = 0;i< saleBS.salePayment.size();i++)
			{
				SalePayDef paydef = (SalePayDef) saleBS.salePayment.elementAt(i);
				if (paydef.paycode.equals("0508"))
				{
					new MessageBox(paydef.payname+"只允许一次性付款\n请删除原"+paydef.payname+"付款方式后再次付款");
					return false;
				}
			}
		}
    	
    	return super.checkMzkMoneyValid();
    }
    
    public boolean findMzk(String track1, String track2, String track3)
    {
        String pass = "";

        if (!paymode.code.equals("0508"))
        {
            StringBuffer passwd = new StringBuffer();

            if (!getPasswdBeforeFindMzk(passwd))
            {
                return false;
            }

            pass = passwd.toString();
        }

        //
        setRequestDataByFind(track1, track2, track3);

        mzkreq.passwd = pass;

        return sendMzkSale(mzkreq, mzkret);
    }
}
