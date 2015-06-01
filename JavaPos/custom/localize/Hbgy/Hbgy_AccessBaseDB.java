package custom.localize.Hbgy;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.PublicMethod;
import com.efuture.javaPos.Struct.CmPopGoodsDef;
import com.efuture.javaPos.Struct.CmPopRuleDef;
import com.efuture.javaPos.Struct.CmPopRuleLadderDef;
import com.efuture.javaPos.Struct.CmPopTitleDef;
import com.efuture.javaPos.Struct.GoodsDef;

import custom.localize.Bstd.Bstd_AccessBaseDB;

public class Hbgy_AccessBaseDB extends Bstd_AccessBaseDB
{
	public Vector findCMPOPGoods(String rqsj, GoodsDef goods, String cardno, String cardtype)
	{
		ResultSet rs = null;

		try
		{
			PublicMethod.timeStart("正在查询本地促销信息,请等待......");

			// 解析日期、时间、星期
			if (rqsj == null || rqsj.trim().equals(""))
				return null;
			String vtime[] = rqsj.split(" ");
			if (vtime.length < 2)
				return null;
			if (vtime[1].length() < 5)
				return null;
			ManipulateDateTime mdt = new ManipulateDateTime();

			// 商品条件
			String code = goods.code;
			String gz = goods.gz;
			String uid = goods.uid;
			String ppcode = goods.ppcode;
			String catid = goods.catid;
			String barcode = goods.barcode;

			// 后单压前单模式
			String overmode = "";

			// new MessageBox("HD:" + GlobalInfo.sysPara.isbackoverpre);
			if (GlobalInfo.sysPara.isbackoverpre == 'Y')
				overmode = " AND ruletype = '" + goods.managemode + "'";

			// 会员条件
			String hykline = "(custlist is null OR custlist = '' OR custlist = '%' OR custlist like '%FULL%' OR ";
			if (cardno == null || cardtype == null)
			{
				hykline += "custlist like '%NALL%')";
			}
			else
			{
				String selHyk = "select text from MemoInfo where code = '" + cardno + "' AND type = 'HYFZ'";
				Object obj = GlobalInfo.baseDB.selectOneData(selHyk);
				if (obj == null)
				{
					hykline += "custlist like '%HALL%' OR custlist like '%" + cardtype + "%')";
				}
				else
				{
					String hygrp = "#" + String.valueOf(obj);
					hykline += "custlist like '%HALL%' OR custlist like '%" + cardtype + "%' OR custlist '%" + hygrp + "%')";
				}
			}

			// 商品参与范围
			StringBuffer sqlbuf = new StringBuffer();
			sqlbuf.append("SELECT * from CMPOPGOODS ");
			sqlbuf.append("WHERE ksrq <= '" + mdt.getDateBySlash() + "' AND jsrq >= '" + mdt.getDateBySlash() + "' AND ");
			sqlbuf.append("kssj <= '" + vtime[1].trim().substring(0, 5) + "' AND jssj >= '" + vtime[1].trim().substring(0, 5) + "' AND ");
			sqlbuf.append("(weeklist = '%' OR weeklist is null OR weeklist = '' OR weeklist LIKE '%" + mdt.getDateWeek() + "%') AND " + hykline);
			sqlbuf.append("AND joinmode = ? AND (");
			sqlbuf.append("(codemode = '0') OR ");
			sqlbuf.append("(codemode = '1' AND (codeid = ? AND (codegz = ? OR codegz = '%' OR codegz is null OR codegz = '') AND (codeuid = ? OR codeuid = '%' OR codeuid is null OR codeuid = ''))) OR ");
			sqlbuf.append("(codemode = '2' AND (codeid = ? OR codeid = '%')) OR ");
			sqlbuf.append("(codemode = '3' AND (codeid = ? OR codeid = '%')) OR ");
			sqlbuf.append("(codemode = '4' AND (codeid = ? OR codeid = '%')) OR ");
			sqlbuf.append("(codemode = '5' AND (codeid = ? AND codegz = ? )) OR ");
			sqlbuf.append("(codemode = '6' AND (codeid = ? AND codegz = ? )) OR ");
			sqlbuf.append("(codemode = '7' AND (codeid = ? AND codegz = ? )) OR ");
			sqlbuf.append("(codemode = '8' AND (codeid = ? AND codegz = ? AND codeuid = ?)) OR ");
			sqlbuf.append("(codemode = '9' AND (codeid = ? AND (codegz = ? OR codegz = '%' OR codegz is null OR codegz = '')))");
			sqlbuf.append(") " + overmode + " ORDER BY dqid,ruletype,cmpopseqno");
			String sqlstr = sqlbuf.toString();
			GlobalInfo.baseDB.setSql(sqlstr);

			//
			Vector popvec = new Vector();

			// 先查找商品参与范围的所有规则
			GlobalInfo.baseDB.paramSetChar(1, 'Y');
			GlobalInfo.baseDB.paramSetString(2, code);
			GlobalInfo.baseDB.paramSetString(3, gz);
			GlobalInfo.baseDB.paramSetString(4, uid);

			GlobalInfo.baseDB.paramSetString(5, gz);
			GlobalInfo.baseDB.paramSetString(6, ppcode);
			GlobalInfo.baseDB.paramSetString(7, catid);

			GlobalInfo.baseDB.paramSetString(8, gz);
			GlobalInfo.baseDB.paramSetString(9, ppcode);

			GlobalInfo.baseDB.paramSetString(10, gz);
			GlobalInfo.baseDB.paramSetString(11, catid);

			GlobalInfo.baseDB.paramSetString(12, ppcode);
			GlobalInfo.baseDB.paramSetString(13, catid);

			GlobalInfo.baseDB.paramSetString(14, gz);
			GlobalInfo.baseDB.paramSetString(15, ppcode);
			GlobalInfo.baseDB.paramSetString(16, catid);

			GlobalInfo.baseDB.paramSetString(17, barcode);
			GlobalInfo.baseDB.paramSetString(18, gz);

			rs = GlobalInfo.baseDB.selectData();
			while (rs.next())
			{
				CmPopGoodsDef cmpop = new CmPopGoodsDef();
				if (!GlobalInfo.baseDB.getResultSetToObject(cmpop)) { return null; }
				popvec.add(cmpop);
			}
			GlobalInfo.baseDB.resultSetClose();

			// 去掉商品不参与范围的所有规则
			GlobalInfo.baseDB.setSql(sqlstr);
			GlobalInfo.baseDB.paramSetChar(1, 'N');
			GlobalInfo.baseDB.paramSetString(2, code);
			GlobalInfo.baseDB.paramSetString(3, gz);
			GlobalInfo.baseDB.paramSetString(4, uid);

			GlobalInfo.baseDB.paramSetString(5, gz);
			GlobalInfo.baseDB.paramSetString(6, ppcode);
			GlobalInfo.baseDB.paramSetString(7, catid);

			GlobalInfo.baseDB.paramSetString(8, gz);
			GlobalInfo.baseDB.paramSetString(9, ppcode);

			GlobalInfo.baseDB.paramSetString(10, gz);
			GlobalInfo.baseDB.paramSetString(11, catid);

			GlobalInfo.baseDB.paramSetString(12, ppcode);
			GlobalInfo.baseDB.paramSetString(13, catid);

			GlobalInfo.baseDB.paramSetString(14, gz);
			GlobalInfo.baseDB.paramSetString(15, ppcode);
			GlobalInfo.baseDB.paramSetString(16, catid);

			GlobalInfo.baseDB.paramSetString(17, barcode);
			GlobalInfo.baseDB.paramSetString(18, gz);

			rs = GlobalInfo.baseDB.selectData();
			while (rs.next())
			{
				String dqid = rs.getString("dqid");
				String ruleid = rs.getString("ruleid");
				for (int i = 0; i < popvec.size(); i++)
				{
					CmPopGoodsDef pop = (CmPopGoodsDef) popvec.elementAt(i);
					if (pop.dqid.equals(dqid) && pop.ruleid.equals(ruleid))
					{
						popvec.remove(i);
						i--;
					}
				}
			}
			GlobalInfo.baseDB.resultSetClose();

			// 去掉同档期同规则类型中序号较小的 （即超市的后单压死前单,只保留同档期，同类型中序号最大的一条）
			CmPopGoodsDef lastpop = null;
			for (int i = 0; i < popvec.size(); i++)
			{
				CmPopGoodsDef pop = (CmPopGoodsDef) popvec.elementAt(i);
				// 档期ID相等 且 （促销类型相同或规则ID相同） 且 当前促销序号大于前一个序号时将前一个促销删除
				if (lastpop != null && pop.dqid.equals(lastpop.dqid) && ((pop.ruletype != null && pop.ruletype.trim().equals(lastpop.ruletype.trim())) || pop.ruleid.equals(lastpop.ruleid)) && pop.cmpopseqno > lastpop.cmpopseqno)
				{
					popvec.remove(lastpop);
					i--;
				}
				lastpop = pop;
			}

			// 去掉不参与门店的所有规则
			for (int i = 0; i < popvec.size(); i++)
			{
				CmPopGoodsDef pop = (CmPopGoodsDef) popvec.elementAt(i);

				// 未设置门店则默认是都参加
				sqlstr = "select count(*) from CMPOPMKTLIST where dqid = '" + pop.dqid + "'";
				Object obj = GlobalInfo.baseDB.selectOneData(sqlstr);
				if (obj == null || Integer.parseInt(obj.toString()) <= 0)
					continue;

				// 若用当前dqid，mkt查出来的参与门店为空，则表示当前门店不参与该dqid，删之
				sqlstr = "select count(*) from CMPOPMKTLIST where dqid = '" + pop.dqid + "' AND " + "(mkt = '" + GlobalInfo.sysPara.mktcode.toUpperCase() + "' OR mkt = 'ALL') AND joinmode = 'Y'";
				obj = GlobalInfo.baseDB.selectOneData(sqlstr);
				if (obj == null || Integer.parseInt(obj.toString()) <= 0)
				{
					popvec.remove(i);
					i--;
					continue;
				}

				// 若用当前dqid，mkt查出来的记录为非空，则表示当前门店不参与该条dqid,删之
				sqlstr = "select count(*) from CMPOPMKTLIST where dqid = '" + pop.dqid + "' AND " + "(mkt = '" + GlobalInfo.sysPara.mktcode.toUpperCase() + "' OR mkt = 'ALL') AND joinmode = 'N'";
				obj = GlobalInfo.baseDB.selectOneData(sqlstr);
				if (obj != null && Integer.parseInt(obj.toString()) > 0)
				{
					popvec.remove(i);
					i--;
					continue;
				}
			}

			// 取得相应的档期、规则、阶梯等信息 （档期，规则必须）
			for (int i = 0; i < popvec.size(); i++)
			{
				CmPopGoodsDef pop = (CmPopGoodsDef) popvec.elementAt(i);

				// 根据之前找到的dqid查找档期详细信息
				sqlstr = "select * from CMPOPTITLE where dqid = '" + pop.dqid + "'";
				rs = GlobalInfo.baseDB.selectData(sqlstr);
				boolean findok = false;
				while (rs.next())
				{
					pop.dqinfo = new CmPopTitleDef();
					if (GlobalInfo.baseDB.getResultSetToObject(pop.dqinfo))
						findok = true;
					break;
				}
				GlobalInfo.baseDB.resultSetClose();
				// 若未查找到与当前dqid相关联的档期信息，则将该条dq删掉
				if (!findok)
				{
					popvec.remove(i);
					i--;
					continue;
				}

				// 根据dqid,ruleid查找规则详细信息
				sqlstr = "select * from CMPOPRULE where dqid = '" + pop.dqid + "' AND ruleid = '" + pop.ruleid + "'";
				rs = GlobalInfo.baseDB.selectData(sqlstr);
				findok = false;
				while (rs.next())
				{
					pop.ruleinfo = new CmPopRuleDef();
					if (GlobalInfo.baseDB.getResultSetToObject(pop.ruleinfo))
						findok = true;
					break;
				}
				GlobalInfo.baseDB.resultSetClose();

				// 若未查找到与当前dqid,ruleid相关联的档期信息，则将该条dq删掉
				if (!findok)
				{
					popvec.remove(i);
					i--;
					continue;
				}

				// 根据dqid,ruleid查找规则阶梯详细信息(规则阶梯不是必须的)
				sqlstr = "select * from CMPOPRULELADDER where dqid = '" + pop.dqid + "' AND ruleid = '" + pop.ruleid + "' order by ladderpri desc,popje desc";
				rs = GlobalInfo.baseDB.selectData(sqlstr);
				while (rs.next())
				{
					if (pop.ruleladder == null)
						pop.ruleladder = new Vector();
					CmPopRuleLadderDef poprl = new CmPopRuleLadderDef();
					if (GlobalInfo.baseDB.getResultSetToObject(poprl))
					{
						pop.ruleladder.add(poprl);
					}
				}
				GlobalInfo.baseDB.resultSetClose();
			}

			// 返回查找出的参与的活动规则
			// 每个商品对应一个popvec
			return popvec;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		finally
		{
			GlobalInfo.baseDB.resultSetClose();

			PublicMethod.timeEnd("查询本地促销规则耗时: ");
		}

	}
}
