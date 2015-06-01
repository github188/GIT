package bankpay.Bank;

import com.efuture.commonKit.Convert;

/**
 * 中免（农商行-银石）接口
 * 2014.5.15
 *
 */
public class NjysZMNSH_PaymentBankFunc extends NjysZMGD_PaymentBankFunc
{

	public String getbankfunc()
	{
		return "C:\\hngmc\\";
	}
	
	public String getRequestTHInfo()
	{
		/*原流水号	ASCII	6	取消及退货交易传入，其他交易传入空格
		原交易时间	ASCII 	4	退货交易传入，其他交易传入空格
		原交易时间	ASCII	6	退货交易传入，其他交易传入空格
		原交易授权号	ASCII	6	退货交易传入，其他交易传入空格
		原交易信息	ASCII	24	退货交易传入，其他交易传入空格。不足左补空格*/
		
		return Convert.increaseChar("", ' ', (6+4+6+6+24));
	}
}
