package custom.localize.Bhls;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Bhls_PaymentMzk extends PaymentMzk
{
	public Bhls_PaymentMzk()
	{
		super();
	}
	
	public Bhls_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		super(mode,sale);
	}
	
	public Bhls_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		super(pay,head);
	}

	public String getDisplayCardno()
	{
		if (this.mzkreq.track2.indexOf("=") > -1)
		{
			return super.getDisplayCardno();
		}
		
		if (this.mzkreq.track2.length() > 18)
			return this.mzkreq.track2.substring(0, 17)+"8";
		else
			return super.getDisplayCardno();
	}

    protected boolean saveFindMzkResultToSalePay()
    {
    	if (!super.saveFindMzkResultToSalePay()) return false;
		
		//卡类型,面值金额,批次,收银员号,小票类型
    	try
    	{
    		salepay.idno = mzkret.func.trim().substring(1,2) + "," + ManipulatePrecision.doubleToString(mzkret.money , 0, 1) + "," + ManipulatePrecision.doubleToString(mzkret.value2 , 0, 1)+ "," + saleBS.saleHead.syyh + ","+ saleBS.saleHead.djlb;
    		return true;
    	}
    	catch(Exception er)
    	{
    		new MessageBox(Language.apply("面值卡返回数据不合法!"));
    		return false;
    	}
    }
}
