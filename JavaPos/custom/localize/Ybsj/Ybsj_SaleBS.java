package custom.localize.Ybsj;

import com.efuture.javaPos.Struct.CustomerDef;

import custom.localize.Bstd.Bstd_SaleBS;

public class Ybsj_SaleBS extends Bstd_SaleBS
{
	public boolean memberGrantFinish(CustomerDef cust)
	{
		if(super.memberGrantFinish(cust))
		{
			//保留会员返回的member_id
			this.saleHead.memo = cust.track;
			return true;
		}
		return false;
	}
}
