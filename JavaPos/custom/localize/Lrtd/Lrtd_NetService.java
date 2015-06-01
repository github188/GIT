package custom.localize.Lrtd;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

import custom.localize.Bstd.Bstd_NetService;

public class Lrtd_NetService extends Bstd_NetService
{
	/**
	 * <configure CmdCode="131">
	 * <CmdType>HttpCmd</CmdType>
	 * <CmdMemo>JFXF</CmdMemo>
	 * <CmdTran>1</CmdTran>
	 * <StartTrans>true</StartTrans>
	 * <Cmd_01_Mode>MemoryCourse</Cmd_01_Mode>
	 * <Sql_01_Type>*</Sql_01_Type>
	 * <Tran_01_Sql>{call
	 * java_findruleshop(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?
	 * ,?,)}</Tran_01_Sql>
	 * <Tran_01_ParaName>mktcode,code,gz,uid,rule,catid,ppcode,rqsj,cardno,
	 * cardtype</Tran_01_ParaName>
	 * <Tran_01_ParaType>s,s,s,s,s,s,s,s,s,s</Tran_01_ParaType>
	 * <Tran_01_ColName>recode,remsg,isjf,couponrule,memo,str1,str2,str3,num1,
	 * num2</Tran_01_ColName>
	 * <Tran_01_ColType>i,s,c,s,s,s,s,s,f,f</Tran_01_ColType>
	 * </configure>
	 */

	public boolean getGoodsIsJFXF(SaleHeadDef salehead, GoodsDef goods, SpareInfoDef info, Http http)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode, goods.code, goods.gz, goods.uid, "0", goods.catid, goods.ppcode, salehead.rqsj, " ", " " };
		String[] args = { "mktcode", "code", "gz", "uid", "rule", "catid", "ppcode", "rqsj", "cardno", "cardtype" };
		try
		{
			head = new CmdHead(131);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			// 不显示错误信息
			result = HttpCall(http, line, "查询本商品积分消费规则失败");

			if (result == 0)
			{
				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[] { "isjf", "couponrule", "memo", "str1", "str2", "str3", "num1", "num2" });

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);
					info.char3 = (row[0].charAt(0));
					goods.str4 = row[1];
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

	public boolean sendHykJf(SaleHeadDef saleHead, Http http)
	{
		if (!GlobalInfo.isOnline) { return false; }

		StringBuffer line = new StringBuffer();
		int result = -1;

		CmdHead head = null;
		String[] values = { saleHead.mkt, saleHead.syjh, saleHead.syyh, String.valueOf(saleHead.fphm), saleHead.hykh, String.valueOf(saleHead.bcjf), String.valueOf(saleHead.ljjf), saleHead.str5,
		        // 增加以下两个字段，兼容以前版本
		String.valueOf(saleHead.ysje), // R5,百货均需发送本笔应收金额
		(SellType.ISSALE(saleHead.djlb) ? "0" : "3") }; // 标识增-加减积分

		String[] args = { "mktcode", "syjh", "syyh", "fphm", "hykh", "bcjf", "ljjf", "str5", "ysje", "djlb" };

		try
		{
			saleHead.ljjf = 0;

			head = new CmdHead(CmdDef.SENDHYKJF);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(http, line, Language.apply("发送会员积分至会员服务器同步失败!"));

			if (result == 0)
			{
				Vector vi = new XmlParse(line.toString()).parseMeth(0, new String[] { "ljjf", "memo", "realjf" });

				if (vi.size() > 0)
				{
					String[] row1 = (String[]) vi.elementAt(0);

					if (Double.parseDouble(row1[0]) > 0)
						saleHead.ljjf = Double.parseDouble(row1[0]);

					if (row1.length > 2 && row1[2] != null && !row1[2].trim().equals(""))
						saleHead.num10 = Convert.toDouble(row1[2].trim());
				}

				return true;
			}
			else
			{

				return false;
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			PosLog.getLog(getClass()).error(ex);
			return false;
		}
	}
}
