package custom.localize.Jdhx;

import java.util.Vector;

import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Jdhx_AccessDayDB extends AccessDayDB
{
	public boolean writeSale(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{
		boolean done = false;
		boolean issendfee = true;

		try
		{
			if (SellType.isJF(saleHead.djlb))
			{
				for (int i = 0; i < salePayment.size(); i++)
				{
					SalePayDef spd = (SalePayDef) salePayment.get(i);
					PayModeDef pmd = DataService.getDefault().searchPayMode(spd.paycode);
					if (pmd.type == '3' || pmd.type == '4')
					{
						issendfee = false;
						break;
					}
				}

				// 现金支票类若进行水费扣款失败,则直接返回小票失败!
				if (issendfee)
				{
					if (SellType.ISBACK(saleHead.djlb))
					{
						if (!Jdhx_WaterFee.getDefault().execute(saleHead, "03", saleGoods, salePayment))
							return false;
					}
					else
					{
						if (!Jdhx_WaterFee.getDefault().execute(saleHead, "02", saleGoods, salePayment))
							return false;
					}
				}
			}

			done = super.writeSale(saleHead, saleGoods, salePayment);

			if (SellType.isJF(saleHead.djlb))
			{
				if (!done)
					return false;

				// 若有储值卡银行卡类支付，则强制小票成功
				if (!issendfee)
				{
					if (SellType.ISBACK(saleHead.djlb))
					{
						if (!Jdhx_WaterFee.getDefault().execute(saleHead, "03", saleGoods, salePayment))
							updateWaterFeeFlag(saleHead.fphm, "N");
						else
							updateWaterFeeFlag(saleHead.fphm, "Y");
					}
					else
					{
						if (!Jdhx_WaterFee.getDefault().execute(saleHead, "02", saleGoods, salePayment))
							updateWaterFeeFlag(saleHead.fphm, "N");
						else
							updateWaterFeeFlag(saleHead.fphm, "Y");
					}
				}
				else
				{
					updateWaterFeeFlag(saleHead.fphm, "Y");
				}
			}

			return done;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean updateWaterFeeFlag(long fphm, String bz)
	{
		String line = "";

		try
		{
			line = "update SALEHEAD set str5 = '" + bz + "' where syjh = '" + ConfigClass.CashRegisterCode + "' and  fphm = " + fphm;
			if (GlobalInfo.dayDB.executeSql(line))
				return true;

			return false;
		}
		catch (Exception er)
		{
			er.printStackTrace();
			return false;
		}
	}
}
