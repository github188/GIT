package bankpay.Payment;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Szzy_PaymentBank extends PaymentBank
{
	public SalePayDef inputPay(String money)
	{
		try
		{
			//new MessageBox("aaaaaaaaaaaaaaaa");
			// 如果允许单独进行银联消费则先检查是否有单独的银联消费交易要分配
			if (GlobalInfo.sysPara.allowbankselfsale == 'Y')
			{
				getAllotSalePay();
				if (salepay != null)
					return salepay;
			}

			// 打开金卡输入窗口
			CreatePayment.getDefault().getPaymentBankForm().open(this, PaymentBank.XYKXF);

			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;
	}
	
	public void accountPay(boolean ret, BankLogDef bld, PaymentBankFunc pbf)
	{
		
		if (!ret)
		{
			// 交易失败，放弃付款对象
			salepay = null;
		}
		else
		{
	
			if (bld.bankinfo.trim().equals("0302"))
			{
				//paymode = DataService.getDefault().searchPayMode("0302");
				salepay.paycode = "0302";
				salepay.payname = paymode.name;
			}
			else if (bld.bankinfo.trim().equals("0303"))
			{
				//paymode = DataService.getDefault().searchPayMode("0303");
				salepay.paycode = "0303";
				salepay.payname = paymode.name;
			}
			
			
			// 交易成功，记录交易数据到付款对象，batch必须不为空,标记付款已记账
			salepay.payno = bld.cardno;
			salepay.batch = String.valueOf(bld.trace);
			salepay.str1 = salepay.batch;
			salepay.idno = salepay.batch;
			salepay.ybje = bld.je;
			salepay.je = ManipulatePrecision.doubleConvert(salepay.ybje * ((paymode != null) ? paymode.hl : 1));
			salepay.kye = bld.kye;
			salepay.memo = bld.memo;
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
}
