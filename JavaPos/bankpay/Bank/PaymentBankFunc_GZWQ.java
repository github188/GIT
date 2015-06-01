package bankpay.Bank;

import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

public class PaymentBankFunc_GZWQ extends PaymentBankFunc
{
	public boolean checkDate(Text date)
	{
		if (date.getText().length() != 4)
		{
			new MessageBox("日期的输入格式必须为:\n0203(MMDD)");
			return false;
		}
		return true;
	}
}
