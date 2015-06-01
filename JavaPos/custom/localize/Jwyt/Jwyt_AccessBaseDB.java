package custom.localize.Jwyt;

import java.sql.ResultSet;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.PublicMethod;
import com.efuture.javaPos.Struct.CustomerTypeDef;
import com.efuture.javaPos.Struct.GoodsPopDef;

import custom.localize.Bstd.Bstd_AccessBaseDB;

public class Jwyt_AccessBaseDB extends Bstd_AccessBaseDB
{

	public boolean findPopRuleCRM(GoodsPopDef popDef, String code, String gz, String uid, String rulecode, String catid, String ppcode, String time, String cardno, String cardtype)
	{

		ResultSet rs = null;

		try
		{
			PublicMethod.timeStart("正在查询本地规则促销,请等待......");

			if (time == null || time.trim().equals(""))
				return false;

			String vtime[] = time.split(" ");

			if (vtime.length < 2)
				return false;

			if (vtime[1].length() < 5)
				return false;

			ManipulateDateTime mdt = new ManipulateDateTime();

			// 增加会员组判断
			String appendLine = "";
			// 没有刷卡
			if (cardno == null || cardtype == null)
			{
				appendLine = "AND (str1 = 'ALL' OR str1 = '@2') ";
			}
			else
			{
				String selHyk = "select text from MemoInfo where code = '" + cardno + "' AND type = 'HYFZ'";
				Object obj = GlobalInfo.baseDB.selectOneData(selHyk);
				if (obj == null)
				{
					appendLine = "AND (str1 = 'ALL' OR str1 = '@1' OR str1 = '" + cardtype + "') ";
				}
				else
				{
					String zh = "#" + String.valueOf(obj);
					appendLine = "AND (str1 = 'ALL' OR str1 = '@1' OR str1 = '" + zh + "' OR str1 = '" + cardtype + "') ";
				}
			}

			String sqlstr = "SELECT CASE WHEN MAX(seqno) IS NULL THEN 0 ELSE MAX(seqno) END FROM crmrulepop " + "WHERE ksrq <= '" + mdt.getDateBySlash() + "' AND jsrq >= '" + mdt.getDateBySlash() + "' AND " + "kssj <= '" + vtime[1].trim().substring(0, 5) + "' AND jssj >= '" + vtime[1].trim().substring(0, 5) + "' AND " + "(mode = 'NMJ' )" + appendLine + " AND ((code = '" + code + "' AND (gz = '" + gz + "' OR gz = 'ALL') AND type = '1' AND " + "(CASE WHEN LTRIM(uid) IS NULL THEN '00' ELSE LTRIM(uid) END = CASE WHEN LTRIM('" + uid + "') IS NULL THEN '" + 00 + "' ELSE LTRIM('" + uid + "') END)" + "  AND sl <> 0 ) OR" + "((gz = '" + gz + "' OR gz = 'ALL') AND " + "(ppcode ='" + ppcode + "' OR ppcode = 'ALL') AND " + "(catid = '" + catid + "' OR catid = 'ALL') AND " + "(rule = '" + rulecode + "' OR rule = '0' ) AND type = '2'))";

			rs = GlobalInfo.baseDB.selectData(sqlstr);

			int seqno = 0;
			String memo = "";
			boolean ret = false;
			int yhspace = 0;
			while (rs.next())
			{
				seqno = rs.getInt(1);
				ret = true;
			}

			if (!ret || seqno == 0)
				return false;

			String sqlstr1 = "SELECT CASE WHEN memo IS NULL THEN '' ELSE memo END,CASE WHEN YHSPACE IS NULL THEN 0 ELSE YHSPACE END FROM crmrulepop where seqno = " + seqno;

			rs = GlobalInfo.baseDB.selectData(sqlstr1);

			while (rs.next())
			{
				memo = rs.getString(1) == null ? "" : rs.getString(1).toString();
				yhspace = rs.getInt(2) == 0 ? 0 : rs.getInt(2);
				ret = true;
			}

			if (memo.indexOf(",") > 0)
				memo = memo.substring(0, memo.indexOf(","));

			boolean mj = false;
			if (yhspace != 0)
			{
				String str1 = "";
				if (GlobalInfo.sysPara.iscrmtjprice == 'Y')
					str1 = Convert.increaseInt(yhspace, 5).substring(0, 4);
				else
					str1 = Convert.increaseInt(yhspace, 4);

				if (str1.charAt(1) == '1' || str1.charAt(1) == '2')
					mj = true;
			}

			if (mj)
			{
				sqlstr = "SELECT seqno,djbh,type,rule,mode,code,gz,uid,t2.catid catid,t2.ppcode ppcode,t2.sl sl,yhspace,ksrq,jsrq,kssj,jssj,t2.poplsj poplsj,t2.pophyj pophyj,poppfj" + ",poplsjzkl,pophyjzkl,poppfjzkl,poplsjzkfd,pophyjzkfd,poppfjzkfd,memo,str1,t2.str2 str2,num1,num2 FROM crmrulepop" + ",(select catid,ppcode,sl,poplsj,pophyj,str2 from crmrulepop where djbh = '" + memo + "' and mode = 'DMJ') t2 " + " WHERE seqno = " + seqno;
			}
			else
			{
				sqlstr = "SELECT seqno,djbh,type,rule,mode,code,gz,uid,catid,ppcode,sl,yhspace,ksrq,jsrq,kssj,jssj,poplsj,pophyj,poppfj" + ",poplsjzkl,pophyjzkl,poppfjzkl,poplsjzkfd,pophyjzkfd,poppfjzkfd,memo,str1,str2,num1,num2 FROM crmrulepop" + " WHERE seqno = " + seqno;
			}
			rs = GlobalInfo.baseDB.selectData(sqlstr);

			if (rs == null)
				return false;

			ret = false;
			while (rs.next())
			{
				if (!GlobalInfo.baseDB.getResultSetToObject(popDef)) { return false; }

				if (mj)
					popDef.gz = "1";
				// 在本地数据库里str2 存储的str3的内容，str2永远为空
				popDef.str3 = popDef.str2;
				popDef.ksrq = String.valueOf(popDef.num2);
				popDef.str2 = "";
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

			PublicMethod.timeEnd("查询本地规则促销耗时: ");
		}

	}

	public boolean findHYZK(GoodsPopDef popDef, String code, String custtype, String gz, String catid, String ppcode, String specialInfo)
	{
		ResultSet rs = null;

		try
		{
			// 查找类型
			rs = GlobalInfo.localDB.selectData("select * from CUSTOMERTYPE where code = '" + custtype + "'");

			if (rs == null) { return false; }

			if (rs.next())
			{
				CustomerTypeDef type1 = new CustomerTypeDef();

				if (!GlobalInfo.localDB.getResultSetToObject(type1)) { return false; }

				// 取卡类型的默认折扣率，默认为折上折
				popDef.pophyj = type1.value1;
				popDef.num2 = 1;

			}

			GlobalInfo.localDB.resultSetClose();

			PublicMethod.timeStart("正在查询VIP折扣率,请等待......");
			String date = new ManipulateDateTime().getDateBySlash();
			String time = new ManipulateDateTime().getTime();
			time = time.substring(0, time.lastIndexOf(":"));

			String sqlstr = "select CASE WHEN seqno IS NULL THEN 0 ELSE seqno END from CRMVIPZK " + "where " + "(" + "   (type = '1' AND code = '" + code + "' and (gz = '" + gz + "' or gz = '0' or gz = '')) " + "OR (type = '2' AND ppcode = '" + ppcode + "' and (memo = '0' or memo = '" + specialInfo + "'))" + "OR (type = '3' AND (gz = '" + gz + "'or gz = '0')AND  (memo = '0' or memo = '" + specialInfo + "'))" + "OR ((type = '4' OR type = '5' OR type = '6') AND catid = substr('" + catid + "',1,length(catid)) AND (memo = '0' OR memo = '" + specialInfo + "' or memo = ''))" + ")" + "AND (mode = '0' OR mode = '" + custtype + "') AND ( (str1 = '2' and ksrq <= '" + date + "' and jsrq >= '" + date + "' and kssj <= '" + time + "' and jssj >= '" + time + "' ) ) order by type asc,seqno desc";

			Object obj = GlobalInfo.baseDB.selectOneData(sqlstr);

			int seqno = 0;
			if (obj != null)
			{
				seqno = Integer.parseInt(String.valueOf(obj));
				if (seqno == 0)
					return true;
			}
			else
			{
				sqlstr = "select CASE WHEN seqno IS NULL THEN 0 ELSE seqno END from CRMVIPZK " + "where " + "(" + "   (type = '1' AND code = '" + code + "' and (gz = '" + gz + "' or gz = '0' or gz ='')) " + "OR (type = '2' AND ppcode = '" + ppcode + "' and (memo = '0' or memo = '" + specialInfo + "'))" + "OR (type = '3' AND (gz = '" + gz + "' or gz = '0') AND  (memo = '0' or memo = '" + specialInfo + "'))" + "OR ((type = '4' OR type = '5' OR type = '6') AND catid = substr('" + catid + "',1,length(catid)) AND (memo = '0' OR memo = '" + specialInfo + "' or memo = ''))" + ")" + "AND (mode = '0' OR mode = '" + custtype + "') AND (str1 = '1' ) order by type asc,seqno desc";

				obj = GlobalInfo.baseDB.selectOneData(sqlstr);

				if (obj != null)
				{
					seqno = Integer.parseInt(String.valueOf(obj));
					if (seqno == 0)
						return true;
				}
			}

			if (obj == null)
				return true;

			sqlstr = "SELECT seqno,djbh,type,rule,mode,code,gz,uid,catid,ppcode,sl,yhspace,ksrq,jsrq,kssj,jssj,poplsj,pophyj,poppfj" + ",poplsjzkl,pophyjzkl,poppfjzkl,poplsjzkfd,pophyjzkfd,poppfjzkfd,memo,str1,str2,num1,num2 FROM CRMVIPZK" + " WHERE seqno = " + seqno;

			rs = GlobalInfo.baseDB.selectData(sqlstr);

			if (rs == null)
				return true;

			boolean ret = false;
			while (rs.next())
			{
				if (!GlobalInfo.baseDB.getResultSetToObject(popDef)) { return true; }

				ret = true;
			}

			return ret;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return true;
		}
		finally
		{
			GlobalInfo.baseDB.resultSetClose();
			GlobalInfo.localDB.resultSetClose();

			PublicMethod.timeEnd("查询本地VIP折扣耗时: ");
		}
	}
}
