package custom.localize.Ysal;


import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Cmls.Cmls_NetService;

public class Ysal_NetService extends Cmls_NetService
{
//	 获得商品
	public int getGoodsDef(GoodsDef goodsDef, String searchFlag, String code, String gz, String scsj, String yhsj, String djlb)
	{
		if (!GlobalInfo.isOnline) { return -1; }

		CmdHead cmdHead = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try
		{
			cmdHead = new CmdHead(CmdDef.FINDGOODS);

			String[] value = { ConfigClass.CashRegisterCode, searchFlag, code, gz, scsj, yhsj, djlb ,GlobalInfo.posLogin.gh};
			String[] arg = { "syjh", "searchflag", "code", "gz", "scsj", "yhsj", "djlb" ,"syyh"};
			line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));

			result = HttpCall(line, "未找到此商品");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, GoodsDef.ref);

				if (v.size() > 0)
				{
					String[] lines = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(goodsDef, lines)) { return 0; }
				}

				return -1;
			}
			else
			{
				return result;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			cmdHead = null;
			line = null;
		}

		return -1;
	}
	
	public boolean getBackSaleInfo(String syjh, String fphm, SaleHeadDef shd, Vector saleDetailList, Vector payDetail)
	{
		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { syjh, fphm ,GlobalInfo.posLogin.gh};
		String[] args = { "syjh", "code" ,"syyh"};

		try
		{
			// 查询退货小票头
			head = new CmdHead(CmdDef.GETBACKSALEHEAD);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox("退货小票头查询失败!");
				return false;
			}

			Vector v = new XmlParse(line.toString()).parseMeth(0, SaleHeadDef.ref);

			if (v.size() < 1)
			{
				new MessageBox("没有查询到退货小票头,退货小票不存在或已确认!");
				return false;
			}

			String[] row = (String[]) v.elementAt(0);

			if (!Transition.ConvertToObject(shd, row))
			{
				shd = null;
				new MessageBox("退货小票头转换失败!");
				return false;
			}

			line.delete(0, line.length());
			v.clear();
			row = null;
			result = -1;

			// 查询退货小票明细
			head = new CmdHead(CmdDef.GETBACKSALEDETAIL);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox("退货小票明细查询失败!");
				return false;
			}

			v = new XmlParse(line.toString()).parseMeth(0, SaleGoodsDef.ref);

			if (v.size() < 1)
			{
				new MessageBox("没有查询到退货小票明细,退货小票不存在或已确认!");
				return false;
			}

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);

				SaleGoodsDef sgd = new SaleGoodsDef();

				if (Transition.ConvertToObject(sgd, row))
				{
					saleDetailList.add(sgd);
				}
				else
				{
					saleDetailList.clear();
					saleDetailList = null;
					return false;
				}
			}

			line.delete(0, line.length());
			v.clear();
			row = null;
			result = -1;

			// 查询小票付款明细
			head = new CmdHead(CmdDef.GETBACKPAYSALEDETAIL);
			line.append(head.headToString() + Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0)
			{
				new MessageBox("付款明细查询失败!");
				return false;
			}

			v = new XmlParse(line.toString()).parseMeth(0, SalePayDef.ref);

			if (v.size() < 1)
			{
				new MessageBox("没有查询到付款小票明细,退货小票不存在或已确认!");
				return false;
			}

			for (int i = 0; i < v.size(); i++)
			{
				row = (String[]) v.elementAt(i);
				SalePayDef spd = new SalePayDef();

				if (Transition.ConvertToObject(spd, row))
				{
					payDetail.add(spd);
				}
				else
				{
					payDetail.clear();
					payDetail = null;
					return false;
				}
			}

			return true;
		}
		catch (Exception ex)
		{
			shd = null;

			if (saleDetailList != null)
			{
				saleDetailList.clear();
				saleDetailList = null;
			}
			ex.printStackTrace();
			return false;
		}
		finally
		{
			head = null;
			line = null;
		}
	}
}
