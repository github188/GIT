package custom.localize.Lydf;

import java.util.Vector;

import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Lydf_AccessDayDB extends AccessDayDB
{
	public boolean writeSale(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{
		boolean ret = super.writeSale(saleHead, saleGoods, salePayment);

		if (!ret)
			return false;

		ProgressBox pb = new ProgressBox();

		try
		{
			Lydf_TaxInfo taxinfo = new Lydf_TaxInfo();
			saleHead.str3 = "";
			saleHead.str4 = "";

			if (SellType.ISSALE(saleHead.djlb))
			{
				taxinfo.saletype = saleHead.djlb;
				taxinfo.oprtype = "0"; // 开票

				pb.setText("正在发送税控开票数据，请稍等...");

				Vector taxSalePay = new Vector();
				for (int i = 0; i < salePayment.size(); i++)
				{
					SalePayDef tmpSalePay = (SalePayDef) ((SalePayDef) salePayment.get(i)).clone();
					taxSalePay.add(tmpSalePay);
				}
				
				if (Lydf_Taxer.getDefault().execute(1, saleHead, saleGoods, taxSalePay, taxinfo))
				{
					saleHead.salefphm = taxinfo.PH;
					saleHead.str3 = taxinfo.toString();

					((Lydf_AccessDayDB) AccessDayDB.getDefault()).updateTaxInfo(1, saleHead);
					((Lydf_NetService) NetService.getDefault()).sendSaleTax(saleHead, taxinfo);
				}
				else
				{
					Lydf_Util.writeTaxFile(saleHead, saleGoods, salePayment, taxinfo);

					saleHead.str4 = "本发票未被税务机关采集，不能作为报销凭证!";
					taxinfo.memo = saleHead.str4;
					((Lydf_AccessDayDB) AccessDayDB.getDefault()).updateTaxInfo(3, saleHead);
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (pb != null)
				pb.close();
			pb = null;
		}
		return ret;
	}

	public boolean updateTaxInfo(int cmdcode, SaleHeadDef salehead)
	{
		String line = "";
		try
		{
			switch (cmdcode)
			{
				case 1:
					line = "update salehead set salefphm = '" + salehead.salefphm + "', str3 = '" + salehead.str3 + "' where syjh = '" + salehead.syjh + "' and fphm=" + salehead.fphm;
					break;
				case 3:
					line = "update salehead set str3 = '" + salehead.str3 + "' where syjh = '" + salehead.syjh + "' and fphm=" + salehead.fphm;
					break;
				case 4:
					line = "update salehead set str4 = '" + salehead.str4 + "' where syjh = '" + salehead.syjh + "' and fphm=" + salehead.fphm;
					break;
			}

			if (GlobalInfo.dayDB.executeSql(line))
				return true;

			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
}
