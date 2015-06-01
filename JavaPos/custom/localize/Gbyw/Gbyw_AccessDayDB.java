package custom.localize.Gbyw;

import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Struct.SalePayDef;

public class Gbyw_AccessDayDB extends AccessDayDB
{
	public boolean isBuckleMoney(SalePayDef spd)
	{
		// 老系统 je < 0 表示扣回的付款方式
		// 新系统由于零钞转存0111付款方式金额也要记负,但不是扣回
		// 新系统 flag = '3' 标记扣回付款，金额记负数
		if ((spd.je < 0 && !Gbyw_PaymentCoin.isPaymentLczc(spd)) || spd.flag == '3')
			return true;

		return false;
	}
}
