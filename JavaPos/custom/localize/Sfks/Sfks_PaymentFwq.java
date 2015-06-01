package custom.localize.Sfks;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Sfks_PaymentFwq extends Payment
{
	public Sfks_PaymentFwq()
	{
		super();
	}
	
	public Sfks_PaymentFwq(PayModeDef mode,SaleBS sale)
	{
		super(mode,sale);
	}
	
	public Sfks_PaymentFwq(SalePayDef pay,SaleHeadDef head)
	{
		super(pay,head);
	}
	
	protected boolean checkMoneyValid(String money,double ye)
	{	
		Sfks_DataService dataservice = (Sfks_DataService)DataService.getDefault();
		
		// 检查商品是否在允许范围内
		SaleGoodsDef goods = null;
		for (int i=0;i<saleBS.saleGoods.size();i++)
		{
			goods = (SaleGoodsDef)saleBS.saleGoods.elementAt(i);
			
			if (!dataservice.findFwqRange(goods.code, goods.gz, goods.uid))
			{
				new MessageBox("该交易中第"+(i+1)+"行商品‘" + goods.name + "’\n\n不在收券范围内,不能付款");
				return false;
			}
		}

		return super.checkMoneyValid(money, ye);
	}
}
