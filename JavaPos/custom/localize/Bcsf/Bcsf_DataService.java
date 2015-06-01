package custom.localize.Bcsf;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.SaleHeadDef;

import custom.localize.Bstd.Bstd_DataService;

public class Bcsf_DataService extends Bstd_DataService
{
	public boolean sendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Sqldb sql)
	{
		if (super.sendSaleData(saleHead, saleGoods, salePayment, sql))
		{
			if (SellType.ISCARD(saleHead.djlb))
				return true;

			if (GlobalInfo.sysPara.isenableyhps == 'Y')
			{
				String[] row = new String[2];
				if (((Bcsf_NetService) NetService.getDefault()).getSaleStamp(row))
				{
					saleHead.num1 = Convert.toDouble(row[0]);
					saleHead.num2 = Convert.toDouble(row[1]);

					if (saleHead.num1 == 0 && saleHead.num2 == 0)
						return true;

					StringBuffer info = new StringBuffer();

					info.append(Language.apply("正常印花数: ") + Convert.appendStringSize("", String.valueOf(saleHead.num1), 1, 16, 16, 0) + "\n");
					info.append(Language.apply("赠送印花数: ") + Convert.appendStringSize("", String.valueOf(saleHead.num2), 1, 16, 16, 0) + "\n");
					info.append(Language.apply("合      计: ") + Convert.appendStringSize("", String.valueOf(saleHead.num1 + saleHead.num2), 1, 16, 16, 0) + "\n");

					new MessageBox(info.toString());
				}
			}
			return true;
		}
		return false;
	}
}
