package custom.localize.Bstd;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.TimeDate;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.PublicMethod;
import com.efuture.javaPos.Struct.CmPopGiftsDef;
import com.efuture.javaPos.Struct.CmPopGoodsDef;
import com.efuture.javaPos.Struct.CmPopRuleDef;
import com.efuture.javaPos.Struct.CmPopRuleLadderDef;
import com.efuture.javaPos.Struct.CmPopTitleDef;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.CustomerTypeDef;
import com.efuture.javaPos.Struct.GoodsBarcodeDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.SuperMarketPopRuleDef;

public class Bstd_AccessBaseDB extends AccessBaseDB
{
	// 获得一条码对多商品的条码列表
	public void findGoodsBarcodeList(String code, ArrayList codelist)
	{
		ResultSet rs = null;

		try
		{
			rs = GlobalInfo.baseDB.selectData("select barcode,gdbarcode,gdname,gdbzhl from GoodsBarcode where barcode = '" + code + "'");

			if (rs == null)
				return;

			while (rs.next())
			{
				GoodsBarcodeDef ccd = new GoodsBarcodeDef();

				if (!GlobalInfo.baseDB.getResultSetToObject(ccd)) { return; }

				codelist.add(ccd);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			GlobalInfo.baseDB.resultSetClose();
		}
	}

	public boolean getCustomer(CustomerDef cust, String track)
	{
		ResultSet rs = null;

		try
		{
			PublicMethod.timeStart("正在查询本地顾客库,请等待......");

			if (GlobalInfo.sysPara.custDisconnetNoPeriod == 'Y' && !GlobalInfo.isOnline)
			{
				cust.code = track;
				cust.type = "XX";
				cust.status = "Y";
				cust.track = track;
				cust.name = "脱机会员";
				cust.ishy = 'N';
				cust.iszk = 'N';
				cust.isjf = 'N';
				cust.func = "N";
				cust.zkl = 1;

				return true;
			}

			rs = GlobalInfo.baseDB.selectData("select * from CUSTOMER where track = '" + track + "'");
			if (rs == null)
			{
				return false;
			}
			else
			{
				if (!rs.next())
				{
					GlobalInfo.baseDB.resultSetClose();

					rs = GlobalInfo.baseDB.selectData("select * from CUSTOMER where code = '" + track + "'");
					if (rs == null)
					{
						return false;
					}
					else
					{
						if (!rs.next()) { return false; }
					}
				}
			}

			if (!GlobalInfo.baseDB.getResultSetToObject(cust)) { return false; }

			//
			GlobalInfo.baseDB.resultSetClose();

			// 查找类型
			rs = GlobalInfo.baseDB.selectData("select * from CUSTOMERTYPE where code = '" + cust.type + "'");

			if (rs == null) { return false; }

			if (rs.next())
			{
				CustomerTypeDef type = new CustomerTypeDef();

				if (!GlobalInfo.baseDB.getResultSetToObject(type)) { return false; }

				cust.ishy = type.ishy;
				cust.iszk = type.iszk;
				cust.isjf = type.isjf;
				cust.func = type.func;
				cust.zkl = type.zkl;
				cust.value1 = type.value1;
				cust.value2 = type.value2;
				cust.value3 = type.value3;
				cust.value4 = type.value4;
				cust.value5 = type.value5;
				cust.valstr1 = type.valstr1;
				cust.valstr2 = type.valstr2;
				cust.valstr3 = type.valstr3;
				cust.valnum1 = type.valnum1;
				cust.valnum2 = type.valnum2;
				cust.valnum3 = type.valnum3;
			}
			if (cust.zkl <= 0)
				cust.zkl = 1;

			GlobalInfo.baseDB.resultSetClose();

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
		finally
		{
			GlobalInfo.baseDB.resultSetClose();

			//
			PublicMethod.timeEnd("查询本地顾客库耗时: ");
		}
	}

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
			if (cardno == null || cardno.trim().equals("") || cardtype == null || cardtype.trim().equals(""))
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
				sqlstr = "select count(*) from CMPOPMKTLIST where dqid = '" + pop.dqid + "' AND " + "(mkt = '" + GlobalInfo.sysPara.mktcode + "' OR mkt = 'ALL') AND joinmode = 'Y'";
				obj = GlobalInfo.baseDB.selectOneData(sqlstr);
				if (obj == null || Integer.parseInt(obj.toString()) <= 0)
				{
					popvec.remove(i);
					i--;
					continue;
				}

				// 若用当前dqid，mkt查出来的记录为非空，则表示当前门店不参与该条dqid,删之
				sqlstr = "select count(*) from CMPOPMKTLIST where dqid = '" + pop.dqid + "' AND " + "(mkt = '" + GlobalInfo.sysPara.mktcode + "' OR mkt = 'ALL') AND joinmode = 'N'";
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

	public Vector findCMPOPGroup(String dqid, String ruleid, int group)
	{
		ResultSet rs = null;

		try
		{
			PublicMethod.timeStart("正在查询本地促销分组,请等待......");

			// 商品参与范围
			String sqlstr = "SELECT * from CMPOPGOODS WHERE dqid = '" + dqid + "' AND ruleid = '" + ruleid + "'";
			if (group >= 0)
				sqlstr += " AND goodsgroup = " + group + " AND joinmode = 'Y' order by goodsgrouprow";
			else
				sqlstr += " AND joinmode = 'Y' order by goodsgroup,goodsgrouprow";
			GlobalInfo.baseDB.setSql(sqlstr);

			// 先查找所有分组的参与商品范围
			Vector popvec = new Vector();
			rs = GlobalInfo.baseDB.selectData();
			while (rs.next())
			{
				CmPopGoodsDef cmpop = new CmPopGoodsDef();
				if (!GlobalInfo.baseDB.getResultSetToObject(cmpop)) { return null; }
				popvec.add(cmpop);
			}
			GlobalInfo.baseDB.resultSetClose();

			// 参与的分组规则
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

			PublicMethod.timeEnd("查询本地促销分组耗时: ");
		}
	}

	public Vector findCMPOPGift(String dqid, String ruleid, String ladderid)
	{
		ResultSet rs = null;

		try
		{
			PublicMethod.timeStart("正在查询本地促销赠品,请等待......");

			//
			Vector giftvec = new Vector();

			// 先按对应阶梯找,若没有对应阶梯的赠品,则查找所有阶梯对应的赠品
			do
			{
				// 查找赠品结果
				String sqlstr = "SELECT * from CMPOPGIFTS where " + "dqid = '" + dqid + "' AND ruleid = '" + ruleid + "' AND ladderid = '" + ladderid + "' order by giftgroup,giftgrouprow";
				GlobalInfo.baseDB.setSql(sqlstr);

				// 档期规则对应的赠品清单
				rs = GlobalInfo.baseDB.selectData();
				while (rs.next())
				{
					CmPopGiftsDef cmgift = new CmPopGiftsDef();
					if (!GlobalInfo.baseDB.getResultSetToObject(cmgift)) { return null; }
					giftvec.add(cmgift);
				}
				GlobalInfo.baseDB.resultSetClose();

				// 找到对应阶梯的赠品则跳出循环
				if (giftvec.size() > 0)
					break;
				else
				{
					if (ladderid.equals("%"))
						break;
					else
						ladderid = "%";
				}
			}
			while (true);

			// 所有赠品
			return giftvec;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		finally
		{
			GlobalInfo.baseDB.resultSetClose();

			PublicMethod.timeEnd("查询本地促销赠品耗时: ");
		}
	}

	public Vector getYhList(String rqsj, GoodsDef goods, String cardno, String cardtype)
	{
		return findCMPOPGoods(rqsj, goods, cardno, cardtype);
	}

	// 查找超市促销规则
	public boolean findU51PopRule(Vector ruleReqList, Vector rulePopList, SuperMarketPopRuleDef popRule)
	{
		ResultSet rs = null;

		try
		{
			PublicMethod.timeStart("正在查询本地促销信息,请等待......");

			// 商品参与范围
			String sqlstr = "select " + "ppiseq,ppibillno,ppimode,ppibarcode,ppimfid," + "ppicatid,ppippcode,ppispec,ppistartdate,ppienddate," + "ppistarttime,ppiendtime,ppizkfd,ppispace,ppinewsj," + "ppinewhyj,ppinewrate,ppinewhyrate,ppinewpfj,ppinewpfrate," + "ppihyzkfd,ppipfzkfd,pphdjlb," + nvl("ppimaxnum", "0") + ",ppipresentcode," + "ppititle," + nvl("ppimaxnum", "0") + ",ppipresentcode,ppipresentunit," + nvl("ppipresentjs", "0") + "," + nvl("ppipresentsl", "0") + "," + nvl("ppipresentxl", "0") + "," + nvl("ppipresentjg", "0") + ",pphjc,pphstr1," + "pphstr2,pphstr3,pphstr4,pphstr5,pphstr6,pphiszsz,pphistjn,pphisptgz" + " from goodspopinfo where ppibillno='" + popRule.djbh + "'";

			System.out.println("查询详单:  " + sqlstr);
			GlobalInfo.baseDB.setSql(sqlstr);

			// 先查找所有分组的参与商品范围

			rs = GlobalInfo.baseDB.selectData();
			// SuperMarketPopRuleDef retRule = new SuperMarketPopRuleDef();
			while (rs.next())
			{
				SuperMarketPopRuleDef ruleDef = new SuperMarketPopRuleDef();

				ruleDef.seqno = Convert.toLong(rs.getString(1));
				ruleDef.djbh = rs.getString(2);
				ruleDef.type = rs.getString(3).charAt(0);
				ruleDef.code = rs.getString(4);
				ruleDef.gz = rs.getString(5);
				ruleDef.dzxl = rs.getString(6);
				ruleDef.pp = rs.getString(7);
				ruleDef.spec = rs.getString(8);
				ruleDef.ksrq = rs.getString(9);
				ruleDef.jsrq = rs.getString(10);
				ruleDef.kssj = rs.getString(11);
				ruleDef.jssj = rs.getString(12);
				ruleDef.zkfd = Convert.toDouble(rs.getString(13));
				ruleDef.yhspace = Convert.toDouble(rs.getString(14));
				ruleDef.yhlsj = Convert.toDouble(rs.getString(15));
				ruleDef.yhhyj = Convert.toDouble(rs.getString(16));
				ruleDef.yhzkl = Convert.toDouble(rs.getString(17));
				ruleDef.yhhyzkl = Convert.toDouble(rs.getString(18));
				ruleDef.yhpfj = Convert.toDouble(rs.getString(19));
				ruleDef.yhpfzkl = Convert.toDouble(rs.getString(20));
				ruleDef.yhhyzkfd = Convert.toDouble(rs.getString(21));
				ruleDef.yhpfzkfd = Convert.toDouble(rs.getString(22));
				ruleDef.yhdjlb = rs.getString(23).charAt(0);
				ruleDef.yhplsl = Convert.toDouble(rs.getString(24));
				ruleDef.presentcode = rs.getString(25);
				ruleDef.title = rs.getString(26);
				ruleDef.maxnum = Convert.toDouble(rs.getString(27));
				ruleDef.presentcode1 = rs.getString(28);
				ruleDef.presentunit = rs.getString(29);
				ruleDef.presentjs = Convert.toDouble(rs.getString(30));
				ruleDef.presentsl = Convert.toDouble(rs.getString(31));
				ruleDef.presentxl = Convert.toDouble(rs.getString(32));
				ruleDef.presentjg = Convert.toDouble(rs.getString(33));
				ruleDef.jc = Convert.toLong(rs.getString(34));
				ruleDef.ppistr1 = rs.getString(35);
				ruleDef.ppistr2 = rs.getString(36);
				ruleDef.ppistr3 = rs.getString(37);
				ruleDef.ppistr4 = rs.getString(38);
				ruleDef.ppistr5 = rs.getString(39);
				ruleDef.ppistr6 = rs.getString(40);
				ruleDef.iszsz = rs.getString(41);
				ruleDef.istjn = rs.getString(42);
				ruleDef.isptgz = rs.getString(43);

				if (ruleDef.yhdjlb == '8')
				{
					// 规则条件
					ruleReqList.add(ruleDef);
				}
				else
				{
					// 规则结果
					rulePopList.add(ruleDef);
				}
			}
			GlobalInfo.baseDB.resultSetClose();

			// 参与的分组规则
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			GlobalInfo.baseDB.resultSetClose();

			PublicMethod.timeEnd("查询本地促销信息耗时: ");
		}
	}

	// 得到通查条件
	private String getCommCond(String tab, String vcode, String vgz, String vpp, String vcatid, String vspec, double vscsjjg)
	{
		return "(" + tab + ".PPIBARCODE = '" + vcode + "' AND " + "(" + tab + ".PPIMFID ='" + vgz + "' OR " + tab + ".PPIMFID = '0') AND " + tab + ".PPISPEC  ='" + vspec + "' AND " + tab + ".PPIMAXNUM <> 0 AND " + "(" + tab + ".PPIMODE = '1' OR (" + tab + ".PPIMODE = '7' AND " + vscsjjg + " >= " + tab + ".PPISPACE))) OR " + "(" + tab + ".PPIBARCODE ='" + vgz + "' AND " + tab + ".PPIMODE = '2') OR " + "(" + tab + ".PPIBARCODE = '" + vgz + "' AND " + tab + ".PPIPPCODE ='" + vpp + "'AND " + tab + ".PPIMODE = '4') OR ('" + vcatid + "' like " + tab + ".PPIBARCODE||'%' AND " + tab + ".PPIMODE = '3') OR ('" + vcatid + "' like " + tab + ".PPIBARCODE||'%' AND " + tab + ".PPIPPCODE ='" + vpp + "' AND " + tab + ".PPIMODE = '5') OR " + "(" + tab + ".PPIBARCODE = '" + vpp + "' AND " + tab + ".PPIMODE = '6')";
	}

	private boolean getRuleDef(ResultSet rs, SuperMarketPopRuleDef ruleDef)
	{
		try
		{
			if (rs != null)
			{
				ruleDef.seqno = Convert.toLong(rs.getString(1));
				ruleDef.type = rs.getString(2).charAt(0);
				ruleDef.djbh = rs.getString(3);
				ruleDef.code = rs.getString(4);
				ruleDef.gz = rs.getString(5);
				ruleDef.dzxl = rs.getString(6);
				ruleDef.pp = rs.getString(7);
				ruleDef.spec = rs.getString(8);
				ruleDef.yhlsj = Convert.toDouble(rs.getString(9));
				ruleDef.yhhyj = Convert.toDouble(rs.getString(10));
				ruleDef.yhzkl = Convert.toDouble(rs.getString(11));
				ruleDef.yhhyzkl = Convert.toDouble(rs.getString(12));
				ruleDef.zkfd = Convert.toDouble(rs.getString(13));
				ruleDef.ksrq = rs.getString(14);
				ruleDef.jsrq = rs.getString(15);
				ruleDef.kssj = rs.getString(16);
				ruleDef.jssj = rs.getString(17);
				ruleDef.yhspace = Convert.toInt(rs.getString(18));
				ruleDef.yhpfj = Convert.toDouble(rs.getString(19));
				ruleDef.yhpfzkl = Convert.toDouble(rs.getString(20));
				ruleDef.yhhyzkfd = Convert.toDouble(rs.getString(21));
				ruleDef.yhpfzkfd = Convert.toDouble(rs.getString(22));

				return true;
			}
			return false;
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

	public boolean findU51PopBillNo(SuperMarketPopRuleDef ruleDef, String code, String gz, String catid, String pp, String uid, String scsj, String yhsj, String custno)
	{
		if (yhsj == null || yhsj.length() <= 0)
			return false;

		try
		{
			double sjjg = 0; // 时间间隔
			String today = new ManipulateDateTime().getDateBySign();

			TimeDate timeObj = new TimeDate();
			timeObj.fullTime = yhsj;
			timeObj.split();
			String sj = timeObj.hh + ":" + timeObj.min;

			if (scsj.length() >= 5)
			{
				long timeDis = new ManipulateDateTime().getDisDateTimeByMS(scsj, yhsj);
				sjjg = timeDis / 1000; // 将时间之差转换成秒
			}

			String retfield = "SELECT PPISEQ,PPIMODE,PPIBILLNO,PPIBARCODE,PPIMFID,PPICATID," + "PPIPPCODE,PPISPEC,PPINEWSJ,PPINEWHYJ,PPINEWRATE,PPINEWHYRATE,PPIZKFD," + "PPISTARTDATE,PPIENDDATE,PPISTARTTIME,PPIENDTIME,PPISPACE,PPINEWPFJ," + "PPINEWPFRATE,PPIHYZKFD,PPIPFZKFD,PPIMAXNUM FROM GOODSPOPINFO A WHERE ";

			String where = " A.PPISTARTDATE <='" + today + "' AND A.PPIENDDATE   >='" + today + "' AND A.PPISTARTTIME <='" + sj + "' AND A.PPIENDTIME   >='" + sj + "' ";

			String exist = " Exists (Select 'x' From GOODSPOPINFOTIME C  Where " + "C.PPIBILLNO = A.PPIBILLNO And C.PPIKSRQ  <='" + today + "' And C.PPIJSRQ  >='" + today + "' And C.PPIKSSJ <='" + sj + "' And C.PPIJSSJ >= '" + sj + "') ";

			boolean isvip = false;
			if (custno != null && custno.length() > 0)
				isvip = true;

			double billamount = 0.0;
			if (Convert.toDouble(gz) > 0)
				billamount = Convert.toDouble(gz);

			StringBuffer sb = new StringBuffer();

			if (isvip)
			{
				if (code.equalsIgnoreCase("ALL"))
				{
					sb.append(retfield).append(where);
					sb.append("AND A.PPHISPTGZ  = '1' And A.PPIPRESENTSL =0 AND A.PPIHYZKFD =0 AND ");
					sb.append(exist);
					sb.append(" AND A.PPHDJLB = '8' AND A.PPIBARCODE = '" + code + "' AND A.PPIMODE = '8' ");
					sb.append(" order by PPISEQ DESC limit 1 ");

					System.out.println("查询明细1： " + sb.toString());
					GlobalInfo.baseDB.setSql(sb.toString());

					if (getRuleDef(GlobalInfo.baseDB.selectData(), ruleDef))
						return true;
				}
				else
				{
					sb.append(retfield).append(where);
					sb.append("AND A.PPHISPTGZ  = '1' And A.PPIPRESENTSL =0 AND A.PPIHYZKFD =0 AND ");
					sb.append(exist);
					sb.append(" AND A.PPHDJLB = '8' AND ( ").append(getCommCond("A", code, gz, pp, catid, uid, sjjg)).append(" )");
					sb.append(" AND Not Exists (Select 'x' From GOODSPOPINFO B Where B.PPIBILLNO = A.PPIBILLNO And B.PPHDJLB = '8' AND (");
					sb.append(getCommCond("B", code, gz, pp, catid, uid, sjjg)).append(")");
					sb.append(" AND B.PPIPRESENTSL = 1 And B.PPHISTJN <>'1')");
					sb.append(" order by PPISEQ DESC limit 1 ");

					System.out.println("查询明细2： " + sb.toString());

					GlobalInfo.baseDB.setSql(sb.toString());
					ResultSet rs = GlobalInfo.baseDB.selectData();

					if (rs.next())
						if (getRuleDef(rs, ruleDef))
							return true;
				}

				if (sb.toString().length() > 0)
					sb.delete(0, sb.toString().length() - 1);

				if (code.equalsIgnoreCase("ALL"))
				{
					sb.append(retfield).append(where);
					sb.append("AND A.PPHISPTGZ  = '0' And A.PPIPRESENTSL =0 AND A.PPIHYZKFD =0 AND ");
					sb.append(exist);
					sb.append(" AND A.PPHDJLB = '8' AND A.PPIBARCODE = '" + code + "' AND A.PPIMODE = '8' ");
					sb.append(" order by PPISEQ DESC limit 1 ");

					System.out.println("查询明细3： " + sb.toString());
					GlobalInfo.baseDB.setSql(sb.toString());

					ResultSet rs = GlobalInfo.baseDB.selectData();
					if (rs.next())
					{
						if (getRuleDef(rs, ruleDef))
							return true;

						return false;
					}
				}
				else
				{
					sb.append(retfield).append(where);
					sb.append("AND A.PPHISPTGZ  = '0' And A.PPIPRESENTSL =0 AND A.PPIHYZKFD =0 AND ");
					sb.append(exist);
					sb.append(" AND A.PPHDJLB = '8' AND ( ").append(getCommCond("A", code, gz, pp, catid, uid, sjjg)).append(" )");
					sb.append(" AND Not Exists (Select 'x' From GOODSPOPINFO B Where B.PPIBILLNO = A.PPIBILLNO And B.PPHDJLB = '8' AND(");
					sb.append(getCommCond("B", code, gz, pp, catid, uid, sjjg)).append(")");
					sb.append(" AND B.PPIPRESENTSL = 1 And B.PPHISTJN <>'1')");
					sb.append(" order by PPISEQ DESC limit 1 ");

					System.out.println("查询明细4： " + sb.toString());

					GlobalInfo.baseDB.setSql(sb.toString());

					ResultSet rs = GlobalInfo.baseDB.selectData();
					if (rs.next())
					{
						if (getRuleDef(rs, ruleDef))
							return true;

						return false;
					}
				}
			}
			if (sb.toString().length() > 0)
				sb.delete(0, sb.toString().length() - 1);

			if (code.equalsIgnoreCase("ALL"))
			{
				sb.append(retfield).append(where);
				sb.append("AND A.PPHISPTGZ  = '1' And A.PPIPRESENTSL =0 AND A.PPIHYZKFD =1 AND ");
				sb.append(exist);
				sb.append(" AND A.PPHDJLB = '8' AND A.PPIBARCODE = '" + code + "'  AND A.PPIMODE = '8' ");
				sb.append(" order by PPISEQ DESC limit 1 ");

				System.out.println("查询明细5： " + sb.toString());
				GlobalInfo.baseDB.setSql(sb.toString());

				ResultSet rs = GlobalInfo.baseDB.selectData();
				if (rs.next())
				{
					if (getRuleDef(rs, ruleDef))
						return true;

					return false;
				}
			}
			else
			{
				sb.append(retfield).append(where);
				sb.append("AND A.PPHISPTGZ  = '1' And A.PPIPRESENTSL =0 AND A.PPIHYZKFD =1 AND ");
				sb.append(exist);
				sb.append(" AND A.PPHDJLB = '8' AND ( ").append(getCommCond("A", code, gz, pp, catid, uid, sjjg)).append(" )");
				sb.append(" AND Not Exists (Select 'x' From GOODSPOPINFO B Where B.PPIBILLNO = A.PPIBILLNO And B.PPHDJLB = '8' AND(");
				sb.append(getCommCond("B", code, gz, pp, catid, uid, sjjg)).append(")");
				sb.append(" AND B.PPIPRESENTSL = 1 And B.PPHISTJN <>'1')");
				sb.append(" order by PPISEQ DESC limit 1 ");

				System.out.println("查询明细6： " + sb.toString());

				GlobalInfo.baseDB.setSql(sb.toString());

				ResultSet rs = GlobalInfo.baseDB.selectData();
				if (rs.next())
				{
					if (getRuleDef(rs, ruleDef))
						return true;

					return false;
				}
			}
			if (sb.toString().length() > 0)
				sb.delete(0, sb.toString().length() - 1);

			if (code.equalsIgnoreCase("ALL"))
			{
				sb.append(retfield).append(where);
				sb.append("AND A.PPHISPTGZ  = '0' And A.PPIPRESENTSL =0 AND A.PPIHYZKFD =1 AND ");
				sb.append(exist);
				sb.append(" AND A.PPHDJLB = '8' AND A.PPIBARCODE = '" + code + "'  AND A.PPIMODE = '8' ");
				sb.append(" order by PPISEQ DESC limit 1 ");

				System.out.println("查询明细7： " + sb.toString());
				GlobalInfo.baseDB.setSql(sb.toString());

				ResultSet rs = GlobalInfo.baseDB.selectData();
				if (rs.next())
				{
					if (getRuleDef(rs, ruleDef))
						return true;

					return false;
				}
			}
			else
			{
				sb.append(retfield).append(where);
				sb.append("AND A.PPHISPTGZ  = '0' And A.PPIPRESENTSL =0 AND A.PPIHYZKFD =1 AND ");
				sb.append(exist);
				sb.append(" AND A.PPHDJLB = '8' AND ( ").append(getCommCond("A", code, gz, pp, catid, uid, sjjg)).append(" )");
				sb.append(" AND Not Exists (Select 'x' From GOODSPOPINFO B Where B.PPIBILLNO = A.PPIBILLNO And B.PPHDJLB = '8' AND(");
				sb.append(getCommCond("B", code, gz, pp, catid, uid, sjjg)).append(")");
				sb.append(" AND B.PPIPRESENTSL = 1 And B.PPHISTJN <>'1')");
				sb.append(" order by PPISEQ DESC limit 1 ");

				System.out.println("查询明细8： " + sb.toString());

				GlobalInfo.baseDB.setSql(sb.toString());

				ResultSet rs = GlobalInfo.baseDB.selectData();
				if (rs.next())
				{
					if (getRuleDef(rs, ruleDef))
						return true;

					return false;
				}
			}

			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public GoodsPopDef getPromotion(String code, String gz, String catid, String ppcode, String uid, String scsj, String yhsj)
	{
		if (GlobalInfo.sysPara.isSuperMarketPop != 'Y')
			return super.getPromotion(code, gz, catid, ppcode, uid, scsj, yhsj);

		if (yhsj == null || yhsj.length() <= 0)
			return null;

		ResultSet rs = null;

		double sjjg = 0; // 时间间隔
		String today = new ManipulateDateTime().getDateBySign();

		TimeDate timeObj = new TimeDate();
		timeObj.fullTime = yhsj;
		timeObj.split();
		String sj = timeObj.hh + ":" + timeObj.min;
		GoodsPopDef goods1 = new GoodsPopDef();

		if (scsj.length() >= 8)
		{
			long timeDis = new ManipulateDateTime().getDisDateTimeByMS(scsj, yhsj);
			sjjg = timeDis / 1000; // 将时间之差转换成秒
		}

		try
		{
			// 交叉重叠的档期内先找单品促销最低价的
			StringBuffer sb = new StringBuffer();

			String condseq = "SELECT MAX(Ppiseq) FROM GOODSPOPINFO ";
			// 查找最大序号商品
			sb.append(condseq);
			sb.append("WHERE PPISTARTDATE <= '" + today + "' AND PPIENDDATE   >='" + today + "' And PPISTARTTIME <= '" + sj + "' AND PPIENDTIME   >= '" + sj + "' And ");
			sb.append("PPIMFID = '" + GlobalInfo.sysPara.mktcode + "' AND ");
			sb.append("Ppihyzkfd = '1' AND " + "Pphdjlb = '1' AND "); // 找单品促销
			sb.append("Exists (Select 'x' From GOODSPOPINFOTIME A Where  A.PPIBILLNO = PPIBILLNO And A.PPIKSRQ  <='" + today + "' And A.PPIJSRQ  >= '" + today + "' And ");
			sb.append("A.PPIKSSJ <='" + sj + "' And A.PPIJSSJ >='" + sj + "') And ");
			sb.append("PPHDJLB ='1' AND ");
			sb.append("((PPIBARCODE = '" + code + "' AND (PPIMFID ='" + gz + "' OR PPIMFID = '0') AND ");
			sb.append("PPISPEC = '00'  AND PPIMAXNUM <> 0 AND ");
			sb.append("(PPIMODE = '1' OR (PPIMODE = '7' AND " + sjjg + ">= PPISPACE))) OR ");
			sb.append("(PPIBARCODE = '" + gz + "' AND PPIMODE = '2') OR ");
			sb.append("(PPIBARCODE = '" + gz + "' AND PPIPPCODE = '" + ppcode + "' AND PPIMODE = '4') OR ");
			sb.append("(PPIBARCODE = '" + catid + "' AND PPIMODE = '3') OR ");
			sb.append("(PPIBARCODE = '" + catid + "' AND PPIPPCODE = '" + ppcode + "' AND PPIMODE = '5') OR ");
			sb.append("(PPIBARCODE = '" + ppcode + "'  AND PPIMODE = '6')) ");

			System.out.println("查询明细1： " + sb.toString());
			// 找到最大序号
			String seqno1 = (String) GlobalInfo.baseDB.selectOneData(sb.toString());

			if (seqno1 != null && Convert.toLong(seqno1) > 0)
			{
				sb.delete(0, sb.toString().length());
				sb.append("SELECT PPISEQ,PPIMODE,PPIBILLNO,PPIBARCODE,PPIMFID,PPICATID," + "PPIPPCODE,PPISPEC,PPINEWSJ,PPINEWHYJ,PPINEWRATE,PPINEWHYRATE,PPIZKFD," + "PPISTARTDATE,PPIENDDATE,PPISTARTTIME,PPIENDTIME,PPISPACE,PPINEWPFJ," + "PPINEWPFRATE,PPIHYZKFD,PPIPFZKFD,PPIMAXNUM FROM GOODSPOPINFO WHERE Ppiseq = " + Convert.toLong(seqno1));
				rs = GlobalInfo.baseDB.selectData(sb.toString());

				if (rs.next())
				{
					goods1.seqno = Convert.toLong(rs.getString(1));
					goods1.type = rs.getString(2).charAt(0);
					goods1.djbh = rs.getString(3);
					// goods1.code = rs.getString(4);
					// goods1.gz = rs.getString(5);
					// goods1.catid = rs.getString(6);
					// goods1.ppcode = rs.getString(7);
					// goods1.uid = rs.getString(8);
					goods1.poplsj = Convert.toDouble(rs.getString(9));
					goods1.pophyj = Convert.toDouble(rs.getString(10));
					goods1.poplsjzkl = Convert.toDouble(rs.getString(11));
					goods1.pophyjzkl = Convert.toDouble(rs.getString(12));
					goods1.poplsjzkfd = Convert.toDouble(rs.getString(13));
					goods1.ksrq = rs.getString(14);
					goods1.jsrq = rs.getString(15);
					goods1.kssj = rs.getString(16);
					goods1.jssj = rs.getString(17);
					goods1.yhspace = Convert.toInt(rs.getString(18));
					goods1.poppfj = Convert.toDouble(rs.getString(19));
					goods1.poppfjzkl = Convert.toDouble(rs.getString(20));
					goods1.pophyjzkfd = Convert.toDouble(rs.getString(21));
					goods1.poppfjzkfd = Convert.toDouble(rs.getString(22));
					goods1.num3 = Convert.toDouble(rs.getString(23));
				}
			}

			GlobalInfo.baseDB.resultSetClose();
			// 找会员促销
			sb.delete(0, sb.toString().length());

			condseq = "SELECT MAX(Ppiseq) FROM GOODSPOPINFO ";
			// 查找最大序号商品
			sb.append(condseq);
			sb.append("WHERE PPISTARTDATE <= '" + today + "' AND PPIENDDATE   >='" + today + "' And PPISTARTTIME <= '" + sj + "' AND PPIENDTIME   >= '" + sj + "' And ");
			sb.append("PPIMFID = '" + GlobalInfo.sysPara.mktcode + "' AND ");
			sb.append("Ppihyzkfd = '1' AND " + "Pphdjlb = 'H' AND "); // 找会员促销
			sb.append("Exists (Select 'x' From GOODSPOPINFOTIME A Where  A.PPIBILLNO = PPIBILLNO And A.PPIKSRQ  <='" + today + "' And A.PPIJSRQ  >= '" + today + "' And ");
			sb.append("A.PPIKSSJ <='" + sj + "' And A.PPIJSSJ >='" + sj + "') And ");
			sb.append("PPHDJLB ='1' AND ");
			sb.append("((PPIBARCODE = '" + code + "' AND (PPIMFID ='" + gz + "' OR PPIMFID = '0') AND ");
			sb.append("PPISPEC = '00'  AND PPIMAXNUM <> 0 AND ");
			sb.append("(PPIMODE = '1' OR (PPIMODE = '7' AND " + sjjg + ">= PPISPACE))) OR ");
			sb.append("(PPIBARCODE = '" + gz + "' AND PPIMODE = '2') OR ");
			sb.append("(PPIBARCODE = '" + gz + "' AND PPIPPCODE = '" + ppcode + "' AND PPIMODE = '4') OR ");
			sb.append("(PPIBARCODE = '" + catid + "' AND PPIMODE = '3') OR ");
			sb.append("(PPIBARCODE = '" + catid + "' AND PPIPPCODE = '" + ppcode + "' AND PPIMODE = '5') OR ");
			sb.append("(PPIBARCODE = '" + ppcode + "'  AND PPIMODE = '6')) ");

			System.out.println("查询明细2： " + sb.toString());

			// 找到最大序号
			String seqno2 = (String) GlobalInfo.baseDB.selectOneData(sb.toString());

			if (seqno2 == null || Convert.toLong(seqno2) <= 0)
				return goods1;

			if (seqno1 != null && Convert.toLong(seqno1) > 0)
			{
				sb.delete(0, sb.toString().length());
				sb.append("SELECT pibillno, Ppinewhyj, Ppinewhyrate,Ppizkfd WHERE Ppiseq = " + Convert.toLong(seqno2));

				rs = GlobalInfo.baseDB.selectData(sb.toString());

				if (rs.next())
				{
					goods1.str5 = rs.getString(1);
					goods1.num5 = Convert.toDouble(rs.getString(2));
					goods1.num6 = Convert.toDouble(rs.getString(3));
					goods1.num7 = Convert.toDouble(rs.getString(4));
				}
			}
			else
			{

				sb.delete(0, sb.toString().length());
				sb.append("SELECT Ppimode pibillno, Ppinewsj,Ppinewhyj, Ppinewhyrate,Ppizkfd WHERE Ppiseq = " + Convert.toLong(seqno2));

				// GlobalInfo.baseDB.setSql(sb.toString());

				rs = GlobalInfo.baseDB.selectData(sb.toString());

				if (rs.next())
				{
					goods1.type = rs.getString(1).charAt(0);
					goods1.str5 = rs.getString(2);
					goods1.poplsj = Convert.toDouble(rs.getString(3));
					goods1.num5 = Convert.toDouble(rs.getString(4));
					goods1.num6 = Convert.toDouble(rs.getString(4));
					goods1.num7 = Convert.toDouble(rs.getString(5));
				}

			}
			GlobalInfo.baseDB.resultSetClose();
			return goods1;
		}
		catch (Exception er)
		{
			er.printStackTrace();

			return null;
		}
		finally
		{
			GlobalInfo.baseDB.resultSetClose();
		}

	}

}
