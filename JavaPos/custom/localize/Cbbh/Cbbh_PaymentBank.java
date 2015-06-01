package custom.localize.Cbbh;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.BankLogDef;

public class Cbbh_PaymentBank extends PaymentBank {
	
	public void accountPay(boolean ret, BankLogDef bld, PaymentBankFunc pbf)
	{
		if (!ret)
		{
			// 交易失败，放弃付款对象
			salepay = null;
		}
		else
		{
			// 交易成功，记录交易数据到付款对象，batch必须不为空,标记付款已记账
			salepay.payno = bld.cardno;
			salepay.batch = String.valueOf(bld.trace);
			salepay.str1 = salepay.batch;
			if(bld.memo.length()<=0)
			{
				salepay.idno = salepay.batch;
			}
			else
			{
				salepay.idno = bld.authno+bld.memo;//参考号+行号
			}
			salepay.ybje = bld.je;
			salepay.je = ManipulatePrecision.doubleConvert(salepay.ybje * ((paymode != null) ? paymode.hl : 1));
			salepay.kye = bld.kye;
			salepay.memo = bld.memo;
			salepay.num6 = bld.ylzk;
			// 替换付款明细名称为银行名称
			if (bld.bankinfo != null)
			{
				String bankinfo = bld.bankinfo.trim();
				if (bankinfo.length() > 2 && pbf != null && pbf.getReplaceBankNameMode())
				{
					int p = bankinfo.indexOf("-");
					if (p > 0)
						salepay.payname = bankinfo.substring(p + 1);
					else
						salepay.payname = bankinfo;
				}
			}
		}

		// 更新付款断点数据，标记为已付款状态,否则在记账以后如果掉电,断点读入的还是未记账状态
		if (this.saleBS != null)
			this.saleBS.writeBrokenData();
	}
	
	public boolean cancelPay()
	{
		if(salepay.paycode.equals(Cbbh_SaleBS.dzkcode)||salepay.paycode.equals(Cbbh_SaleBS.lbkcode)||salepay.paycode.equals(Cbbh_SaleBS.ppkcode))
			return true;
		else
			return super.cancelPay();
	}
	
	protected boolean cancelPayBack()
	{
		if(salepay.paycode.equals(Cbbh_SaleBS.dzkcode)||salepay.paycode.equals(Cbbh_SaleBS.lbkcode)||salepay.paycode.equals(Cbbh_SaleBS.ppkcode))
			return true;
		else
			return super.cancelPayBack();
	}
}
