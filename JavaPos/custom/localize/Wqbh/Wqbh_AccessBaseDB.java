package custom.localize.Wqbh;

import java.sql.ResultSet;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.GoodsPopDef;

import custom.localize.Bcrm.Bcrm_AccessBaseDB;

public class Wqbh_AccessBaseDB extends Bcrm_AccessBaseDB
{
	public boolean findPopRule(GoodsPopDef popDef, String code, String gz, String uid, String rulecode, String catid, String ppcode, String time, String custType, String custNo)
	{

		ResultSet rs = null;
		String rule1 = null;
		String rule2 = null;
		String rule3 = null;

		try
		{
			if (time == null || time.trim().equals("")) return false;

			String vtime[] = time.split(" ");

			if (vtime.length < 2) return false;

			if (vtime[1].length() < 5) return false;

			if (rulecode.trim().equals("R"))
			{
				rule1 = "RMJ";
				rule2 = "RMS";
				rule3 = "RMF";
			}
			else if (rulecode.trim().equals("MJ"))
			{
				rule1 = "RMJ";
				rule2 = "RMJ";
				rule3 = "RMJ";
			}
			else if (rulecode.trim().equals("MS"))
			{
				rule1 = "RMS";
				rule2 = "RMS";
				rule3 = "RMF";
			}

			if (rule1 == null || rule2 == null || rule3 == null) return false;

			ManipulateDateTime mdt = new ManipulateDateTime();

			// 增加会员组判断
			String appendLine = "";

			// 没有刷卡的情况
			if ("".endsWith(custType.trim()))
			{
				appendLine = "AND (str1 = '0000' or str1 = '9999')";
			}
			// 刷了卡的情况
			// 所有非会员 str1 = '0000'
			// 所有人 str1 = '9999'
			// 所有会员 str1 = '9998'
			else
			{
				appendLine = "AND (str1 = '9998' or str1 = '9999' or str1 = '" + custType + "')";
			}

			String sqlstr = "SELECT CASE WHEN MAX(seqno) IS NULL THEN 0 ELSE MAX(seqno) END FROM GOODSPOP " + "WHERE ksrq <= '"
					+ mdt.getDateBySlash() + "' AND jsrq >= '" + mdt.getDateBySlash() + "' AND " + "kssj <= '" + vtime[1].trim().substring(0, 5)
					+ "' AND jssj >= '" + vtime[1].trim().substring(0, 5) + "' AND " + "(rule = '" + rule1 + "' OR rule = '" + rule2
					+ "' OR rule = '" + rule3 + "' ) AND " + "(" + "(code = '" + code + "' AND (gz = '" + gz + "' OR gz = '0') AND type = '1' AND "
					+ "(CASE WHEN LTRIM(uid) IS NULL THEN '00' ELSE LTRIM(uid) END = CASE WHEN LTRIM('" + uid + "') IS NULL THEN '" + 00
					+ "' ELSE LTRIM('" + uid + "') END)" + "  AND sl <> 0 )" + appendLine + " OR ((code = '" + gz
					+ "' OR code = '0') AND type = '2')" + " OR ((code = '" + gz + "' OR code = '0') AND ppcode = '" + ppcode + "' AND type = '4')"
					+ " OR (code = '" + catid + "' AND type = '3')" + " OR (code = '" + catid + "' AND ppcode = '" + ppcode + "' AND type = '5')"
					+ " OR (code = '" + ppcode + "' AND type = '5 ')" + ")";

			Object obj = GlobalInfo.baseDB.selectOneData(sqlstr);

			int seqno = 0;
			if (obj == null)
			{
				return false;
			}
			else
			{
				seqno = Integer.parseInt(String.valueOf(obj));
				if (seqno == 0) return false;
			}

			sqlstr = "SELECT seqno,djbh,type,rule,mode,code,gz,uid,catid,ppcode,sl,yhspace,ksrq,jsrq,kssj,jssj,poplsj,pophyj,poppfj"
					+ ",poplsjzkl,pophyjzkl,poppfjzkl,poplsjzkfd,pophyjzkfd,poppfjzkfd,memo,str1,str2,num1,num2 FROM GOODSPOP" + " WHERE seqno = "
					+ seqno;

			rs = GlobalInfo.baseDB.selectData(sqlstr);

			if (rs == null) return false;

			boolean ret = false;
			while (rs.next())
			{
				if (!GlobalInfo.baseDB.getResultSetToObject(popDef)) { return false; }

				ret = true;
			}

			return ret;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			GlobalInfo.baseDB.resultSetClose();
		}

	}
}
