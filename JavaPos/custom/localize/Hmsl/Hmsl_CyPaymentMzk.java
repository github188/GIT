package custom.localize.Hmsl;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;

// 邯郸美食林调用长益储值卡接口  for yebk 2015-01-15
public class Hmsl_CyPaymentMzk extends PaymentMzk
{
	
	// 面值卡查询
	public boolean getMzkInfo(MzkRequestDef req, MzkResultDef ret)
	{
		return sendMzkSale(req, ret);
	}
	
	// 面值卡交易
	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{
		if (GlobalInfo.isOnline)
		{
             //'01'-消费,'02'-消费冲正,'03'-退货,'04'-退货冲正,'05'-查询,'06'-冻结
			if (req.type.equals("05"))
			{
				return Hmsl_CyDateService.getDefault().queryMzkInfo(req, ret);				
			}
			else if (req.type.equals("01"))
			{
				return Hmsl_CyDateService.getDefault().mzkSale(req, ret);				
			}
			else if (req.type.equals("02"))
			{
				return Hmsl_CyDateService.getDefault().flushes(req, ret);
			}
			else
			{
				String typeName = null;
                if (req.type.equals("03"))
					typeName = Language.apply("退货");
				else if (req.type.equals("04"))
					typeName = Language.apply("退货冲正");
				else
					typeName = Language.apply("未知");
				new MessageBox("长益储值卡不支持" + typeName +"交易");
				return false;
			}
		}
		else
		{
			new MessageBox(Language.apply("面值卡必须联网使用!"));
		}

		return false;
	}
	
	// 第三方储值卡必须即时扣款，否则可能导致其他不必要的问题
	public boolean realAccountPay()
	{
		//先检查是否有未冲正的记录
		if (!sendAccountCz())
		{
			new MessageBox("冲正交易记录失败，无法继续交易。请联系信息部!!!");
			return false;
		}
		
		// 付款即时记账
		if (mzkAccount(true))
		{
			deleteMzkCz();
		
			return true;
		}
		else
		{
			// 长益储值卡接口要求交易失败，即进行冲正
			sendAccountCz();
			
			return false;
		}
	}
	
	public boolean collectAccountPay()
	{
		// 已记账,直接返回
		return true;
	}
}
