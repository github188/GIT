package custom.localize.Htsc;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.CustInfoDef;
import com.efuture.javaPos.Struct.CustNoDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Cmls.Cmls_NetService;

public class Htsc_NetService extends Cmls_NetService {
	public boolean validSaleNo(Vector ret, String djlb, String saleNo) {
		if (!GlobalInfo.isOnline) {
			return false;
		}

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode, djlb, saleNo };
		String[] args = { "mkt", "djlb", "saleno" };

		try {
			head = new CmdHead(CmdDef.HTSC_VALIDSALENO);
			line.append(head.headToString()
					+ Transition.SimpleXML(values, args));

			// 不显示错误信息
			result = HttpCall(getMemCardHttp(CmdDef.HTSC_VALIDSALENO), line, "");

			if (result == 0) {
				Vector v = new XmlParse(line.toString()).parseMeth(0,
						new String[] { "gz", "je", "djbs", "gdid", "yyy",
								"custno", "name", "phone", "tel", "addr" });

				if (v.size() > 0) {
					String[] row = (String[]) v.elementAt(0);
					for (int i = 0; i < row.length; i++) {
						ret.add(row[i]);
					}
					// ret.add(row[0]);
					// ret.add(row[1]);
					// ret.add(row[2]);
					return true;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return false;
	}

	public boolean getBackSaleInfo(String djlb, String saleNo, SaleHeadDef shd,
			Vector saleDetailList, Vector payDetail) {
		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode, djlb, saleNo };
		String[] args = { "mkt", "djlb", "saleno" };

		try {
			// 查询退货小票头
			head = new CmdHead(CmdDef.GETBACKSALEHEAD);
			line.append(head.headToString()
					+ Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0) {
				new MessageBox(Language.apply("退货小票头查询失败!"));
				return false;
			}

			Vector v = new XmlParse(line.toString()).parseMeth(0,
					SaleHeadDef.ref);

			if (v.size() < 1) {
				new MessageBox(Language.apply("没有查询到退货小票头,退货小票不存在或已确认!"));
				return false;
			}

			String[] row = (String[]) v.elementAt(0);

			if (!Transition.ConvertToObject(shd, row)) {
				shd = null;
				new MessageBox(Language.apply("退货小票头转换失败!"));
				return false;
			}

			line.delete(0, line.length());
			v.clear();
			row = null;
			result = -1;

			// 查询退货小票明细
			head = new CmdHead(CmdDef.GETBACKSALEDETAIL);
			line.append(head.headToString()
					+ Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0) {
				new MessageBox(Language.apply("退货小票明细查询失败!"));
				return false;
			}

			v = new XmlParse(line.toString()).parseMeth(0, SaleGoodsDef.ref);

			if (v.size() < 1) {
				new MessageBox(Language.apply("没有查询到退货小票明细,退货小票不存在或已确认!"));
				return false;
			}

			for (int i = 0; i < v.size(); i++) {
				row = (String[]) v.elementAt(i);

				SaleGoodsDef sgd = new SaleGoodsDef();

				if (Transition.ConvertToObject(sgd, row)) {
					saleDetailList.add(sgd);
				} else {
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
			line.append(head.headToString()
					+ Transition.SimpleXML(values, args));
			result = HttpCall(line, "");

			if (result != 0) {
				new MessageBox(Language.apply("付款明细查询失败!"));
				return false;
			}

			v = new XmlParse(line.toString()).parseMeth(0, SalePayDef.ref);

			if (v.size() < 1) {
				new MessageBox(Language.apply("没有查询到付款小票明细,退货小票不存在或已确认!"));
				return false;
			}

			for (int i = 0; i < v.size(); i++) {
				row = (String[]) v.elementAt(i);
				SalePayDef spd = new SalePayDef();

				if (Transition.ConvertToObject(spd, row)) {
					payDetail.add(spd);
				} else {
					payDetail.clear();
					payDetail = null;
					return false;
				}
			}

			return true;
		} catch (Exception ex) {
			shd = null;

			if (saleDetailList != null) {
				saleDetailList.clear();
				saleDetailList = null;
			}
			ex.printStackTrace();
			return false;
		} finally {
			head = null;
			line = null;
		}
	}

	// 查询客户号
	public String selectCustNo() {
		if (!GlobalInfo.isOnline) {
			return null;
		}

		CmdHead aa = null;
		StringBuffer line = new StringBuffer();
		int result = -1;

		try {
			aa = new CmdHead(CmdDef.HTSC_GETSALENO);
			line.append(aa.headToString() + Transition.buildEmptyXML());
			result = HttpCall(line, "");

			if (result == 0) {
				Vector v = new XmlParse(line.toString()).parseMeth(0,
						CustNoDef.ref);
				String[] CustNoDef = (String[]) v.elementAt(0);

				return CustNoDef[0];
			} else {
				new MessageBox("没有获取到顾客代码");
				return null;
			}
		} catch (Exception er) {
			er.printStackTrace();
			return null;
		} finally {
			aa = null;
			line = null;
		}
	}

	// 新增或修改客户信息
	public boolean addOrModUserInfo(String vcustno, String vname,
			String vphone, String vtel, String vaddr) {
		if (!GlobalInfo.isOnline) {
			return false;
		}

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { vcustno, vname, vphone, vtel, vaddr,
				GlobalInfo.sysPara.mktcode };
		String[] args = { "vcustno", "vname", "vphone", "vtel", "vaddr", "vmkt" };

		try {
			// 拼接传递XML
			head = new CmdHead(CmdDef.HTSC_UPCUSTINFO);
			line.append(head.headToString()
					+ Transition.SimpleXML(values, args));

			// 访问posserver查询数据
			result = HttpCall(line, "");

			if (result == 0) {
				// 获得结果集
				Vector v = new XmlParse(line.toString()).parseMeth(0,
						new String[] { "rcustno" });

				if (v.size() > 0) {
					return true;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return false;
	}

	// 查询顾客信息
	public Vector getCustList(String vphone, String vtel, String vname) {
		if (!GlobalInfo.isOnline) {
			return null;
		}

		StringBuffer line = new StringBuffer();
		int result = -1;
		CmdHead head = null;
		String[] values = { vphone, vtel, vname };
		String[] args = { "vphone", "vtel", "vname" };

		try {
			head = new CmdHead(CmdDef.HTSC_SELECTCUSTINFO);
			line.append(head.headToString()
					+ Transition.SimpleXML(values, args));

			result = HttpCall(line, "");

			if (result == 0) {
				Vector v = new XmlParse(line.toString()).parseMeth(0,
						CustInfoDef.ref);

				return v;
			} else {
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();

			return null;
		}
	}

	// 发送销售小票
	public int sendSaleData(SaleHeadDef saleHead, Vector saleGoods,
			Vector salePayment, Vector retValue, Http http, int commandCode) {
		SaleGoodsDef saleGoodsDef = null;
		SalePayDef salePayDef = null;

		// 收银机组
		saleHead.str3 = GlobalInfo.tempDef.str3; // 顾客代码
		saleHead.str4 = GlobalInfo.tempDef.str4; // 特价键
		saleHead.str5 = GlobalInfo.tempDef.str5; // 销售时间

		System.out.println(saleHead.str3 + saleHead.str4 + saleHead.str5);
		if (!GlobalInfo.isOnline) {
			return -1;
		}

		try {
			CmdHead aa = null;
			int result = -1;
			aa = new CmdHead(commandCode);

			// 单头打XML
			String line = Transition.ItemDetail(saleHead, SaleHeadDef.ref,
					new String[][] { new String[] { "jygs",
							GlobalInfo.sysPara.jygs } });
			line = Transition.closeTable(line, "SaleHeadDef", 1);

			// 小票明细
			String line1 = "";

			for (int i = 0; i < saleGoods.size(); i++) {
				saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
				line1 += Transition.ItemDetail(saleGoodsDef, SaleGoodsDef.ref);
			}

			line1 = Transition.closeTable(line1, "saleGoodsDef",
					saleGoods.size());

			// 付款明细
			String line2 = "";

			for (int i = 0; i < salePayment.size(); i++) {
				salePayDef = (SalePayDef) salePayment.elementAt(i);

				line2 += Transition.ItemDetail(salePayDef, SalePayDef.ref);
			}

			line2 = Transition.closeTable(line2, "salePayDef",
					salePayment.size());

			// 合并
			line = Transition.getHeadXML(line + line1 + line2);

			StringBuffer line3 = new StringBuffer();
			line3.append(aa.headToString() + line);

			if (http == null) {
				result = HttpCall(line3, Language.apply("上传小票失败!"));
			} else {
				result = HttpCall(http, line3, Language.apply("上传小票失败!"));
			}
			// 返回应答数据
			if (result == 0 && retValue != null
					&& line3.toString().trim().length() > 0) {
				// 找第4个命令sendok过程的返回
				Vector v = new XmlParse(line3.toString()).parseMeth(3,
						new String[] { "memo", "value" });

				if (v.size() > 0) {
					String[] row = (String[]) v.elementAt(0);

					retValue.add(row[0]);
					retValue.add(row[1]);
				}
			}

			//
			return result;
		} catch (Exception er) {
			er.printStackTrace();

			return -1;
		} finally {
			saleGoodsDef = null;
			salePayDef = null;
			GlobalInfo.tempDef.str3 = ""; // 顾客代码
			GlobalInfo.tempDef.str4 = "N"; // 特价键
			GlobalInfo.tempDef.str5 = ""; // 销售时间
		}
	}

	public boolean findPopRuleCRM(GoodsPopDef popDef, String code, String gz, String uid, String rulecode, String catid, String ppcode, String time, String cardno, String cardtype, String isfjk, String grouplist, String djlb, Http http, int cmdcode,String ssdh)
	{
		if (!GlobalInfo.isOnline) { return false; }

		if (cardno == null)
		{
			cardno = " ";
		}

		if (cardtype == null)
		{
			cardtype = " ";
		}

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = {
							GlobalInfo.sysPara.mktcode,
							GlobalInfo.sysPara.jygs,
							code,
							gz,
							uid,
							rulecode,
							catid,
							ppcode,
							time,
							cardno,
							cardtype,
							isfjk,
							grouplist,
							djlb,
							ssdh};
		String[] args = {
							"mktcode",
							"jygs",
							"code",
							"gz",
							"uid",
							"rule",
							"catid",
							"ppcode",
							"rqsj",
							"cardno",
							"cardtype",
							"isfjk",
							"grouplist",
							"djlb",
							"ssdh"};

		try
		{
			head = new CmdHead(cmdcode);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			//不显示错误信息
			result = HttpCall(http, line, "");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, GoodsPopDef.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(popDef, row)) { return true; }
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return false;
	}
}
