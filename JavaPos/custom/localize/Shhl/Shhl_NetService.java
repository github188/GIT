package custom.localize.Shhl;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bstd.Bstd_NetService;

public class Shhl_NetService extends Bstd_NetService
{
	public boolean findStampGoods(String cutpricecode, String[] row)
	{
		if (!GlobalInfo.isOnline)
			return false;

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { cutpricecode };
		String[] args = { "cutpricecode" };

		try
		{
			head = new CmdHead(CmdDef.GETSTAMPGOODS);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			// 不显示错误信息
			result = HttpCall(line, "");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "goodscode", "goodsprice", "goodsdjbh" });

				if (v.size() > 0)
				{
					String[] tmp = (String[]) v.elementAt(0);

					if (tmp.length > 1)
					{
						row[0] = tmp[0];
						row[1] = tmp[1];
						row[2] = tmp[2];
					}
					return true;
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return false;

	}

	// 查找商品的保质期
	public boolean findGoodsSelfLife(String code, String date, String[] row)
	{
		if (!GlobalInfo.isOnline)
			return false;

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode,code, date };
		String[] args = { "mkt","code", "date" };

		try
		{
			head = new CmdHead(CmdDef.HHDL_GETGOODSSHELFLIFE);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			// 不显示错误信息
			result = HttpCall(line, "");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "goodsprice" });

				if (v.size() > 0)
				{
					String[] tmp = (String[]) v.elementAt(0);

					if (tmp.length > 0)
						row[0] = tmp[0];

					return true;
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return false;

	}
	
//	 发送销售小票
	public int sendSaleData(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment, Vector retValue, Http http, int commandCode)
	{
		SaleGoodsDef saleGoodsDef = null;
		SalePayDef salePayDef = null;

		if (!GlobalInfo.isOnline) { return -1; }

		try
		{
			CmdHead aa = null;
			int result = -1;
			aa = new CmdHead(commandCode);

			// 单头打XML
			String line = Transition.ItemDetail(saleHead, SaleHeadDef.ref, new String[][] { new String[] { "jygs", GlobalInfo.sysPara.jygs } });
			line = Transition.closeTable(line, "SaleHeadDef", 1);

			// 小票明细
			String line1 = "";

			for (int i = 0; i < saleGoods.size(); i++)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
				line1 += Transition.ItemDetail(saleGoodsDef, SaleGoodsDef.ref);
			}

			line1 = Transition.closeTable(line1, "saleGoodsDef", saleGoods.size());

			// 付款明细
			String line2 = "";

			for (int i = 0; i < salePayment.size(); i++)
			{
				salePayDef = (SalePayDef) salePayment.elementAt(i);

				line2 += Transition.ItemDetail(salePayDef, SalePayDef.ref);
			}

			line2 = Transition.closeTable(line2, "salePayDef", salePayment.size());

			// 合并
			line = Transition.getHeadXML(line + line1 + line2);

			StringBuffer line3 = new StringBuffer();
			line3.append(aa.headToString() + line);
			PosLog.getLog(this.getClass()).info("[小票号："+saleHead.fphm+"]调用&"+commandCode+"&号命令返回信息:*】"+line3);

			if (http == null)
			{
				result = HttpCall(line3, Language.apply("上传小票失败!"));
			}
			else
			{
				result = HttpCall(http, line3, Language.apply("上传小票失败!"));
			}
			PosLog.getLog(this.getClass()).info("[小票号："+saleHead.fphm+"]调用&"+commandCode+"&号命令返回信息:*】"+result);

			// 返回应答数据
			if (result == 0 && retValue != null && line3.toString().trim().length() > 0)
			{
				// 找第4个命令sendok过程的返回
				Vector v = new XmlParse(line3.toString()).parseMeth(3, new String[] { "memo", "value" });

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					retValue.add(row[0]);
					retValue.add(row[1]);
				}
			}

			//
			return result;
		}
		catch (Exception er)
		{
			er.printStackTrace();

			return -1;
		}
		finally
		{
			saleGoodsDef = null;
			salePayDef = null;
		}
	}
}
