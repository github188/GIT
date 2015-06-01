package custom.localize.Nbbh;

import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

public class Nbbh_PaymentMzk extends PaymentMzk
{

	public double calcPayRuleMaxMoney()
	{
		if (GlobalInfo.sysPara.havePayRule == 'Y' || GlobalInfo.sysPara.havePayRule == 'A')
		{
			return super.calcPayRuleMaxMoney();
		}
		else
		{
			//mzkret.str2="0,1";//"2010202003";
			String[] goodsList = null;;//mzkret.str2;
			if(mzkret.str2!=null && mzkret.str2.length()>0) goodsList = mzkret.str2.split(",");
			if(goodsList==null || goodsList.length!=saleBS.saleGoods.size())
			{
				return 0;//未找到匹配的规则或规则不正确
			}
			
			double hjje = 0;
			try
			{
				for (int i = 0; i < saleBS.saleGoods.size(); i++)
				{
					SaleGoodsDef sg = (SaleGoodsDef) saleBS.saleGoods.elementAt(i);
					SpareInfoDef spinfo = (SpareInfoDef) saleBS.goodsSpare.elementAt(i);
					boolean isallow = false;
					double ftje = 0;
					if (goodsList[i].trim().equalsIgnoreCase("0"))
					{
						if (spinfo == null)
							continue;

						if (spinfo.payft != null)
						{
							for (int j = 0; j < spinfo.payft.size(); j++)
							{
								String[] s = (String[]) spinfo.payft.elementAt(j);
								ftje += Convert.toDouble(s[3]);
							}
						}
						isallow = true;

					}
					if (isallow || goodsAllowApportionPay(i))
					{
						double maxfdje = sg.hjje - saleBS.getZZK(sg) - ftje;

						hjje += maxfdje;
					}
				}


				if (hjje > 0)
				{
					// 标记控制柜组
					isControlGz = true;
				}
			}
			catch (Exception er)
			{
				er.printStackTrace();
			}

			return hjje;
		}
	}

	public void setRequestDataByFind(String track1, String track2, String track3)
	{
		super.setRequestDataByFind(track1, track2, track3);
		
		if (saleBS==null || saleBS.saleGoods==null) return;
		
		String memo="";
		for (int i = 0; i < saleBS.saleGoods.size(); i++)
		{
			SaleGoodsDef sg = (SaleGoodsDef) saleBS.saleGoods.elementAt(i);
			if(sg==null) continue;
			//Code|Gz|Catid|Pp,Code|Gz|Catid|Pp
			if(i>0) memo = memo + ",";
			memo = memo + sg.code + "|" + sg.gz + "|" + sg.catid + "|" + sg.ppcode;			
		}
		mzkreq.memo = memo;
	}
}
