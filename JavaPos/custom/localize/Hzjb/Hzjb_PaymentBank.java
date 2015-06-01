package custom.localize.Hzjb;

import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Hzjb_PaymentBank extends PaymentBank {
	public Hzjb_PaymentBank()
	{
		super();
	}
	
	public Hzjb_PaymentBank(PayModeDef mode,SaleBS sale)
	{
		super(mode,sale);
	}
	
	public Hzjb_PaymentBank(SalePayDef pay,SaleHeadDef head)
	{
		super(pay,head);
	}
	
	public String getCancelPayHint()
	{
		return  "撤销原参考号为 " + salepay.str1 + " 的银联交易吗？";
	}
	
	public void accountPay(boolean ret,PaymentBankFunc pbf)
	{
		if (!ret)
		{
			// 交易失败，放弃付款对象
			salepay = null;
		}
		else
		{
			// 交易成功，记录交易数据到付款对象，batch必须不为空,标记付款已记账
			salepay.payno = pbf.getBankLog().cardno;
			salepay.batch = String.valueOf(pbf.getBankLog().trace);
			salepay.str1  = salepay.batch;
			salepay.idno  = salepay.batch;
			salepay.ybje  = pbf.getBankLog().je;
			salepay.je    = pbf.getBankLog().je;
			
			// 替换付款明细名称为银行名称
			String bankinfo = pbf.getBankLog().bankinfo.trim();
			
			if (bankinfo.length() > 4)
			{
				salepay.bankno = bankinfo.substring(0,4);
			}
			
			if (bankinfo.length() > 0 && pbf.getReplaceBankNameMode())
			{
				if (bankinfo.length() > 4) salepay.payname = bankinfo.substring(3);
				else                       salepay.payname = bankinfo;
			}
			
			salepay.idno = salepay.bankno;
		}
	}
}
