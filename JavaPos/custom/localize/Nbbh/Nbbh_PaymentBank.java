package custom.localize.Nbbh;

import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Nbbh_PaymentBank extends PaymentBank
{

	public Nbbh_PaymentBank()
	{		
		super();
	}
	
	public Nbbh_PaymentBank(PayModeDef mode,SaleBS sale)
	{
		super(mode,sale);
	}
	
	public Nbbh_PaymentBank(SalePayDef pay,SaleHeadDef head)
	{
		super(pay,head);
	}
	
	public void accountPay(boolean ret, BankLogDef bld, PaymentBankFunc pbf)
	{
		String memo="";
		if(salepay!=null) memo=salepay.memo;
		super.accountPay(ret, bld, pbf);
		if(ret && saleBS!=null && SellType.ISSALE(saleBS.saletype))
		{
			if(bld!=null && Convert.toInt(bld.type)==Nbbh_PaymentBank.XYKXF)
			{
				salepay.memo = salepay.str6;
			}
			else
			{
				salepay.memo=memo;
				salepay.str6 = salepay.memo;
			}
		}
		
		
	}
}
