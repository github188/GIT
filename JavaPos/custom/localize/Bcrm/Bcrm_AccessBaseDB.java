package custom.localize.Bcrm;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.PublicMethod;
import com.efuture.javaPos.Struct.CmPopGiftsDef;
import com.efuture.javaPos.Struct.CmPopGoodsDef;
import com.efuture.javaPos.Struct.CmPopRuleDef;
import com.efuture.javaPos.Struct.CmPopRuleLadderDef;
import com.efuture.javaPos.Struct.CmPopTitleDef;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.CustomerTypeDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;

import custom.localize.Bhls.Bhls_AccessBaseDB;

public class Bcrm_AccessBaseDB extends Bhls_AccessBaseDB
{
	public boolean findHYZK(GoodsPopDef popDef, String code, String custtype, String gz, String catid, String ppcode, String specialInfo)
	{	
    	ResultSet rs = null;
    	
    	try
    	{
    		//查找类型
            rs = GlobalInfo.localDB.selectData("select * from CUSTOMERTYPE where code = '" +
            		custtype + "'");

            if (rs == null)
            {
                return false;
            }

            if (rs.next())
            {
                CustomerTypeDef type1 = new CustomerTypeDef();

                if (!GlobalInfo.localDB.getResultSetToObject(type1))
                {
                    return false;
                }

                // 取卡类型的默认折扣率，默认为折上折
                popDef.pophyj = type1.value1;
                popDef.num2 = 1;
                
            }
            
            GlobalInfo.localDB.resultSetClose();
            
        	PublicMethod.timeStart(Language.apply("正在查询VIP折扣率,请等待......"));
        	String date = new ManipulateDateTime().getDateBySlash();
    		String time = new ManipulateDateTime().getTime();
    		time = time.substring(0, time.lastIndexOf(":"));
    		
        	String sqlstr  = "select CASE WHEN seqno IS NULL THEN 0 ELSE seqno END from CRMVIPZK " +
							"where " +
							"(" +
									"   (type = '1' AND code = '"+code+"' and (gz = '"+gz+"' or gz = '0' or gz = '')) " +
									"OR (type = '2' AND ppcode = '"+ppcode+"' and (memo = '0' or memo = '"+specialInfo+"'))" +
									"OR (type = '3' AND (gz = '"+gz+"'or gz = '0')AND  (memo = '0' or memo = '"+specialInfo+"'))" +
									"OR ((type = '4' OR type = '5' OR type = '6') AND catid = substr('"+catid+"',1,length(catid)) AND (memo = '0' OR memo = '"+specialInfo+"' or memo = ''))" +
							")" +
							"AND (mode = '0' OR mode = '"+custtype+"') AND ( (str1 = '2' and ksrq <= '"+date+"' and jsrq >= '"+date+"' and kssj <= '"+time+"' and jssj >= '"+time+"' ) ) order by type asc,seqno desc";
        	
    		Object obj = GlobalInfo.baseDB.selectOneData(sqlstr);
    		
            int seqno = 0;
            if (obj != null)
            {
            	seqno = Integer.parseInt(String.valueOf(obj));
            	if (seqno == 0) return true;
            }
            else
            {
            	sqlstr  = "select CASE WHEN seqno IS NULL THEN 0 ELSE seqno END from CRMVIPZK " +
				"where " +
				"(" +
						"   (type = '1' AND code = '"+code+"' and (gz = '"+gz+"' or gz = '0' or gz ='')) " +
						"OR (type = '2' AND ppcode = '"+ppcode+"' and (memo = '0' or memo = '"+specialInfo+"'))" +
						"OR (type = '3' AND (gz = '"+gz+"' or gz = '0') AND  (memo = '0' or memo = '"+specialInfo+"'))" +
						"OR ((type = '4' OR type = '5' OR type = '6') AND catid = substr('"+catid+"',1,length(catid)) AND (memo = '0' OR memo = '"+specialInfo+"' or memo = ''))" +
				")" +
				"AND (mode = '0' OR mode = '"+custtype+"') AND (str1 = '1' ) order by type asc,seqno desc";

            	obj = GlobalInfo.baseDB.selectOneData(sqlstr);
            	
                if (obj != null)
                {
                	seqno = Integer.parseInt(String.valueOf(obj));
                	if (seqno == 0) return true;
                }
            }
            
            if (obj == null) return true;
            
            sqlstr = "SELECT seqno,djbh,type,rule,mode,code,gz,uid,catid,ppcode,sl,yhspace,ksrq,jsrq,kssj,jssj,poplsj,pophyj,poppfj" +
            		",poplsjzkl,pophyjzkl,poppfjzkl,poplsjzkfd,pophyjzkfd,poppfjzkfd,memo,str1,str2,num1,num2 FROM CRMVIPZK" +
            		" WHERE seqno = "+ seqno;
            
            rs = GlobalInfo.baseDB.selectData(sqlstr);
            
            if (rs == null) return true;
    		
    		boolean ret = false;
    		while (rs.next())
    		{
    			if (!GlobalInfo.baseDB.getResultSetToObject(popDef))
                {
                    return true;
                }
    			
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
            
    		PublicMethod.timeEnd(Language.apply("查询本地VIP折扣耗时: "));
    	}
    }
	
	
	public boolean getCustomer(CustomerDef cust, String track)
	{
		// 联名卡和老卡不包含“=”
		// 新卡包含“=”需要效验磁道号是否正确
		
		ResultSet rs = null;
        
        try
        {
        	PublicMethod.timeStart(Language.apply("正在查询本地顾客库,请等待......"));
        	
            rs = GlobalInfo.baseDB.selectData("select * from CUSTOMER where track = '" + track + "'");
        	
            if (rs == null)
            {
            	return false;
            }

            if (rs.next())
            {
                if (!GlobalInfo.baseDB.getResultSetToObject(cust))
                {
                    return false;
                }
                
                //
                GlobalInfo.baseDB.resultSetClose();
                
                /**
                // 查询到卡信息，效验磁道号是否正确,老卡无需效验
                if (type)
                {
                	if (!track.equals(checkCardTrack(cust,track1)))
                	{
                		new MessageBox("磁道号效验错误，请去卡中心检查写卡是否正确");
                		return false;
                	}
                }*/
                
                // 查找类型
                rs = GlobalInfo.localDB.selectData("select * from CUSTOMERTYPE where code = '" +
                                                   cust.type + "'");

                if (rs == null)
                {
                    return false;
                }

                if (rs.next())
                {
                    CustomerTypeDef type1 = new CustomerTypeDef();

                    if (!GlobalInfo.localDB.getResultSetToObject(type1))
                    {
                        return false;
                    }

                    cust.ishy   = type1.ishy;
                    cust.iszk   = type1.iszk;
                    cust.isjf   = type1.isjf;
                    cust.func   = type1.func;
                    cust.zkl    = type1.zkl;
                    cust.value1 = type1.value1;
                    cust.value2 = type1.value2;
                    cust.value3 = type1.value3;
                    cust.value4 = type1.value4;
                    cust.value5 = type1.value5;
                    cust.valstr1 = type1.valstr1;
                    cust.valstr2 = type1.valstr2;
                    cust.valstr3 = type1.valstr3;
                    cust.valnum1 = type1.valnum1;
                    cust.valnum2 = type1.valnum2;
                    cust.valnum3 = type1.valnum3;
                }
                if (cust.zkl <= 0) cust.zkl = 1;

                GlobalInfo.localDB.resultSetClose();
                
                //在CRM中为零钞转存余额，拖网查会员时，这个值为默认折扣率，所以去掉 
                cust.value1 = 0 ;
                
                return true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return false;
        }
        finally
        {
            GlobalInfo.baseDB.resultSetClose();
            GlobalInfo.localDB.resultSetClose();
            
            //
            PublicMethod.timeEnd(Language.apply("查询本地顾客库耗时: ")); 
        }

        return false;
	}
	
	public String checkCardTrack(CustomerDef cust,String track1) {
		String pwd = cust.maxdate;
		String track2 = track1 + "="+pwd;
		int num = 0;
		for (int i = 0; i <  track2.length(); i++)
		{
			if (track2.charAt(i) >= '0' && track2.charAt(i) <= '9')
			{
				num += Convert.toInt(String.valueOf(track2.charAt(i)));
			}
		}
		String t = String.valueOf(num)+String.valueOf((int)ManipulatePrecision.doubleConvert(num/track2.length()));
		
		if (t.length() >= 2)
		{
			track2 += t.substring(0,2);
		}
		else
		{
			track2 += Convert.increaseChar(t, '0',2);
		}
		
		return track2;
	}


	public boolean findPopRuleCRM(GoodsPopDef popDef,String code,String gz,String uid,String rulecode,String catid,String ppcode,String time,String cardno,String cardtype)
    {
    	ResultSet rs = null;
    	
    	try
    	{
        	PublicMethod.timeStart(Language.apply("正在查询本地规则促销,请等待......"));
        	
    		if (time == null || time.trim().equals("")) return false;
    		
    		String vtime[] = time.split(" ");
    		
    		if (vtime.length < 2) return false;
    		
    		if (vtime[1].length() < 5) return false;
    		
    		ManipulateDateTime mdt = new ManipulateDateTime();
    		
    		//增加会员组判断
    		String appendLine = "";
    		//没有刷卡
    		if (cardno == null || cardtype == null)
    		{
    			appendLine = "AND (str1 = 'ALL' OR str1 = '@2') ";
    		}
    		else
    		{
    			String selHyk = "select text from MemoInfo where code = '"+cardno+"' AND type = 'HYFZ'";
    			Object obj = GlobalInfo.baseDB.selectOneData(selHyk);
    			if (obj == null)
    			{
    				appendLine = "AND (str1 = 'ALL' OR str1 = '@1' OR str1 = '"+cardtype+"') ";
    			}
    			else
    			{
    				String zh = "#"+String.valueOf(obj);
    				appendLine = "AND (str1 = 'ALL' OR str1 = '@1' OR str1 = '"+ zh +"' OR str1 = '"+cardtype+"') ";
    			}
    		}
    		
    		String sqlstr = "SELECT CASE WHEN MAX(seqno) IS NULL THEN 0 ELSE MAX(seqno) END FROM crmrulepop " +
    		"WHERE ksrq <= '" + mdt.getDateBySlash() + "' AND jsrq >= '" + mdt.getDateBySlash() + "' AND " +
    		"kssj <= '" + vtime[1].trim().substring(0,5) + "' AND jssj >= '" + vtime[1].trim().substring(0,5) + "' AND " +
    		"(mode = 'NMJ' )" 
    		+ appendLine +
    		" AND ((code = '"+ code + "' AND (gz = '" + gz + "' OR gz = 'ALL') AND type = '1' AND " +
    		"(CASE WHEN LTRIM(uid) IS NULL THEN '00' ELSE LTRIM(uid) END = CASE WHEN LTRIM('"+ uid +"') IS NULL THEN '" + 00 + "' ELSE LTRIM('"+ uid +"') END)" +
    		"  AND sl <> 0 ) OR" +
    		"((gz = '"+ gz + "' OR gz = 'ALL') AND " +
    		 "(ppcode ='"+ppcode+"' OR ppcode = 'ALL') AND " +
    		 "(catid = '"+catid+"' OR catid = 'ALL') AND " +
    		 "(rule = '"+rulecode+"' OR rule = '0' ) AND type = '2'))";
    		
    		rs = GlobalInfo.baseDB.selectData(sqlstr);
    		
            int seqno = 0;
            String memo = "";
    		boolean ret = false;
    		int yhspace= 0;
    		while (rs.next())
    		{
    			seqno = rs.getInt(1);
    			ret = true;
    		}
    		
    		if (!ret || seqno == 0) return false;
    		
    		String sqlstr1 = "SELECT CASE WHEN memo IS NULL THEN '' ELSE memo END,CASE WHEN YHSPACE IS NULL THEN 0 ELSE YHSPACE END FROM crmrulepop where seqno = "+seqno;
    		
    		rs = GlobalInfo.baseDB.selectData(sqlstr1);
    		
    		while (rs.next())
    		{
    			memo  = rs.getString(1) == null ? "" : rs.getString(1).toString();
    			yhspace = rs.getInt(2) == 0 ? 0 : rs.getInt(2);
    			ret = true;
    		}
    		
    		if ( memo.indexOf(",") > 0 ) memo = memo.substring(0,memo.indexOf(","));
    		
    		boolean mj = false;
    		if (yhspace != 0)
    		{
    			String str1 = "";
    			 if(String.valueOf(yhspace).charAt(0) == '9')
    			 {
    				 str1 = String.valueOf(yhspace).substring(1);
    			 }
    			 else if (GlobalInfo.sysPara.iscrmtjprice == 'Y')
            		str1 = Convert.increaseInt(yhspace, 5).substring(0,4);
            	else
            		str1 = Convert.increaseInt(yhspace, 4);
            	
            	if (str1.charAt(1) == '1' || str1.charAt(1) == '2') mj = true;
    		}
            
    		if (mj)
    		{
	            sqlstr = "SELECT seqno,djbh,type,rule,mode,code,gz,uid,t2.catid catid,t2.ppcode ppcode,t2.sl sl,yhspace,ksrq,jsrq,kssj,jssj,t2.poplsj poplsj,t2.pophyj pophyj,poppfj" +
	            		",poplsjzkl,pophyjzkl,poppfjzkl,poplsjzkfd,pophyjzkfd,poppfjzkfd,memo,str1,t2.str2 str2,num1,num2,t2.str5 str5 FROM crmrulepop" +
	            		",(select catid,ppcode,sl,poplsj,pophyj,str2,str5 from crmrulepop where djbh = '" + memo + "' and mode = 'DMJ') t2 " +
	            		" WHERE seqno = "+ seqno;
    		}
    		else
    		{
    			sqlstr = "SELECT seqno,djbh,type,rule,mode,code,gz,uid,catid,ppcode,sl,yhspace,ksrq,jsrq,kssj,jssj,poplsj,pophyj,poppfj" +
        				",poplsjzkl,pophyjzkl,poppfjzkl,poplsjzkfd,pophyjzkfd,poppfjzkfd,memo,str1,str2,num1,num2,str5 FROM crmrulepop" +
        				" WHERE seqno = "+ seqno;
    		}
            rs = GlobalInfo.baseDB.selectData(sqlstr);
            
            if (rs == null) return false;
    		
    		ret = false;
    		while (rs.next())
    		{
    			if (!GlobalInfo.baseDB.getResultSetToObject(popDef))
                {
                    return false;
                }
    			
    			if (mj) popDef.gz = "1";
    			//在本地数据库里str2 存储的str3的内容，str2永远为空
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
    		
    		PublicMethod.timeEnd(Language.apply("查询本地规则促销耗时: "));
    	}
    }
	
	// CMPOP促销 start
    public Vector findCMPOPGoods(String rqsj,GoodsDef goods,String cardno,String cardtype)
    {
    	ResultSet rs = null;
    	
    	try
    	{
        	PublicMethod.timeStart(Language.apply("正在查询本地促销信息,请等待......"));

        	// 解析日期、时间、星期
    		if (rqsj == null || rqsj.trim().equals("")) return null;
    		String vtime[] = rqsj.split(" ");
    		if (vtime.length < 2) return null;
    		if (vtime[1].length() < 5) return null;
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
    		if (GlobalInfo.sysPara.isbackoverpre == 'Y')
    		{
    			overmode = " AND ruletype = '" + goods.managemode + "'";
    		}
    		
    		// 会员条件
			String hykline = "(custlist is null OR custlist = '' OR custlist = '%' OR custlist like '%FULL%' OR "; 
    		if (cardno == null || cardtype == null)
    		{
    			hykline += "custlist like '%NALL%')";
    		}
    		else
    		{
    			String selHyk = "select text from MemoInfo where code = '"+cardno+"' AND type = 'HYFZ'";
    			Object obj = GlobalInfo.baseDB.selectOneData(selHyk);
    			if (obj == null)
    			{
    				hykline += "custlist like '%HALL%' OR custlist like '%" + cardtype + "%')";
    			}
    			else
    			{
    				String hygrp = "#"+String.valueOf(obj);
    				hykline += "custlist like '%HALL%' OR custlist like '%" + cardtype + "%' OR custlist '%" + hygrp + "%')";
    			}
    		}
    		
    		// 商品参与范围
    		StringBuffer sqlbuf = new StringBuffer();
    		sqlbuf.append("SELECT * from CMPOPGOODS ");
    		sqlbuf.append("WHERE ksrq <= '" + mdt.getDateBySlash() + "' AND jsrq >= '" + mdt.getDateBySlash() + "' AND ");
    		sqlbuf.append("kssj <= '" + vtime[1].trim().substring(0,5) + "' AND jssj >= '" + vtime[1].trim().substring(0,5) + "' AND ");
    		sqlbuf.append("(weeklist = '%' OR weeklist is null OR weeklist = '' OR weeklist LIKE '%" + mdt.getDateWeek() + "%') AND " + hykline);
    		sqlbuf.append("AND joinmode = ? AND (");
    		sqlbuf.append("(codemode = '0') OR ");
    		sqlbuf.append("(codemode = '1' AND (codeid = ? AND (codegz = ? OR codegz = '%' OR codegz is null OR codegz = '') AND (codeuid = ? OR codeuid = '%' OR codeuid is null OR codeuid = ''))) OR "); 
    		sqlbuf.append("(codemode = '2' AND (codeid = ? OR codeid = '%')) OR ");
    		sqlbuf.append("(codemode = '3' AND (codeid = ? OR codeid = '%')) OR ");
    		sqlbuf.append("(codemode = '4' AND (codeid = ? OR codeid = '%')) OR ");
    		sqlbuf.append("(codemode = '5' AND ((codeid = ? OR codeid = '%') AND (codegz = ? OR codegz = '%'))) OR ");
    		sqlbuf.append("(codemode = '6' AND ((codeid = ?  OR codeid = '%')  AND (codegz = ? OR codegz = '%'))) OR ");
    		sqlbuf.append("(codemode = '7' AND ((codeid = ? OR codeid = '%') AND (codegz = ? OR codegz = '%'))) OR ");
    		sqlbuf.append("(codemode = '8' AND ((codeid = ? OR codeid = '%') AND (codegz = ? OR codegz = '%') AND (codeuid = ? OR codeuid = '%'))) OR ");
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
        	while(rs.next())
        	{
        		CmPopGoodsDef cmpop = new CmPopGoodsDef();
    			if (!GlobalInfo.baseDB.getResultSetToObject(cmpop))
                {
                    return null;
                }
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
        	while(rs.next())
        	{
        		String dqid = rs.getString("dqid");
        		String ruleid = rs.getString("ruleid");
        		for (int i=0;i<popvec.size();i++)
        		{
        			CmPopGoodsDef pop = (CmPopGoodsDef)popvec.elementAt(i);
        			if (pop.dqid.equals(dqid) && pop.ruleid.equals(ruleid))
        			{
        				popvec.remove(i);
        				i--;
        			}
        		}
        	}
        	GlobalInfo.baseDB.resultSetClose();
        	
        	// 去掉同档期同规则类型中序号较小的
        	CmPopGoodsDef lastpop = null;
        	for (int i=0;i<popvec.size();i++)
        	{
        		CmPopGoodsDef pop = (CmPopGoodsDef)popvec.elementAt(i);
        		if (lastpop != null && pop.dqid.equals(lastpop.dqid) && 
        			((pop.ruletype != null && pop.ruletype.trim().equals(lastpop.ruletype.trim())) || pop.ruleid.equals(lastpop.ruleid)) && 
        			pop.cmpopseqno > lastpop.cmpopseqno)
        		{
        			popvec.remove(lastpop);
        			i--;
        		}
        		lastpop = pop;
        	}
        	
        	// 去掉不参与门店的所有规则
    		for (int i=0;i<popvec.size();i++)
    		{
    			CmPopGoodsDef pop = (CmPopGoodsDef)popvec.elementAt(i);
    		
    			// 未设置门店则默认是都参加
    			sqlstr = "select count(*) from CMPOPMKTLIST where dqid = '" + pop.dqid + "'";
    			Object obj = GlobalInfo.baseDB.selectOneData(sqlstr);
    			if (obj == null || Integer.parseInt(obj.toString()) <= 0) continue;
    			
    			sqlstr = "select count(*) from CMPOPMKTLIST where dqid = '" + pop.dqid + "' AND " +
    			"(mkt = '" + GlobalInfo.sysPara.jygs.trim() + GlobalInfo.sysPara.mktcode + "' OR mkt = 'ALL') AND joinmode = 'Y'";
    			obj = GlobalInfo.baseDB.selectOneData(sqlstr);
    			if (obj == null || Integer.parseInt(obj.toString()) <= 0)
    			{
    				popvec.remove(i);
    				i--;
    				continue;
    			}
    			
    			sqlstr = "select count(*) from CMPOPMKTLIST where dqid = '" + pop.dqid + "' AND " +
    			"(mkt = '" + GlobalInfo.sysPara.jygs.trim() + GlobalInfo.sysPara.mktcode + "' OR mkt = 'ALL') AND joinmode = 'N'";
    			obj = GlobalInfo.baseDB.selectOneData(sqlstr);
    			if (obj != null && Integer.parseInt(obj.toString()) > 0)
    			{
    				popvec.remove(i);
    				i--;
    				continue;
    			}
    		}
    		
    		// 取得相应的档期、规则、阶梯等信息
    		for (int i=0;i<popvec.size();i++)
    		{
    			CmPopGoodsDef pop = (CmPopGoodsDef)popvec.elementAt(i);        	
        		
    			// 档期
    			sqlstr = "select * from CMPOPTITLE where dqid = '" + pop.dqid + "'" ;
    			rs = GlobalInfo.baseDB.selectData(sqlstr);
    			boolean findok = false; 
    			while(rs.next())
    			{
        			pop.dqinfo = new CmPopTitleDef();
        			if (GlobalInfo.baseDB.getResultSetToObject(pop.dqinfo)) findok = true; 
        			break;
    			}
				GlobalInfo.baseDB.resultSetClose();
    			if (!findok)
    			{
    				popvec.remove(i);
    				i--;
    				continue;
    			}
    			
    			// 规则
    			sqlstr = "select * from CMPOPRULE where dqid = '" + pop.dqid + "' AND ruleid = '" + pop.ruleid + "'";
    			rs = GlobalInfo.baseDB.selectData(sqlstr);
    			findok = false;
    			while (rs.next())
    			{
        			pop.ruleinfo = new CmPopRuleDef();
        			if (GlobalInfo.baseDB.getResultSetToObject(pop.ruleinfo)) findok = true; 
        			break;
    			}
				GlobalInfo.baseDB.resultSetClose();
    			if (!findok)
    			{
    				popvec.remove(i);
    				i--;
    				continue;
    			}
    			
    			// 规则阶梯
    			sqlstr = "select * from CMPOPRULELADDER where dqid = '" + pop.dqid + "' AND ruleid = '" + pop.ruleid + "' order by ladderpri desc,popje desc";
    			rs = GlobalInfo.baseDB.selectData(sqlstr);
    			while (rs.next())
    			{
        			if (pop.ruleladder == null) pop.ruleladder = new Vector();
        			CmPopRuleLadderDef poprl = new CmPopRuleLadderDef();
        			if (GlobalInfo.baseDB.getResultSetToObject(poprl))
        			{
        				pop.ruleladder.add(poprl);
        			}
    			}
    			GlobalInfo.baseDB.resultSetClose();
    		}
    		
    		// 参与的活动规则
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
    		
    		PublicMethod.timeEnd(Language.apply("查询本地促销规则耗时: "));
    	}
    }
    
    public Vector findCMPOPGroup(String dqid,String ruleid,int group)
    {
    	ResultSet rs = null;
    	
    	try
    	{
        	PublicMethod.timeStart(Language.apply("正在查询本地促销分组,请等待......"));
    		
    		// 商品参与范围
    		String sqlstr = "SELECT * from CMPOPGOODS WHERE dqid = '" + dqid + "' AND ruleid = '" + ruleid + "'";
    		if (group >= 0) sqlstr += " AND goodsgroup = " + group + " AND joinmode = 'Y' order by goodsgrouprow";
    		else sqlstr += " AND joinmode = 'Y' order by goodsgroup,goodsgrouprow";
    		GlobalInfo.baseDB.setSql(sqlstr);

    		// 先查找所有分组的参与商品范围
    		Vector popvec = new Vector();
        	rs = GlobalInfo.baseDB.selectData();
        	while(rs.next())
        	{
        		CmPopGoodsDef cmpop = new CmPopGoodsDef();
    			if (!GlobalInfo.baseDB.getResultSetToObject(cmpop))
                {
                    return null;
                }
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
    		
    		PublicMethod.timeEnd(Language.apply("查询本地促销分组耗时: "));
    	}
    }
    
    public Vector findCMPOPGift(String dqid,String ruleid,String ladderid)
    {
    	ResultSet rs = null;
    	
    	try
    	{
        	PublicMethod.timeStart(Language.apply("正在查询本地促销赠品,请等待......"));
    		
        	//
    		Vector giftvec = new Vector();

    		// 先按对应阶梯找,若没有对应阶梯的赠品,则查找所有阶梯对应的赠品
    		do 
    		{
	    		// 查找赠品结果
	    		String sqlstr = "SELECT * from CMPOPGIFTS where " +
	    		"dqid = '" + dqid + "' AND ruleid = '" + ruleid + "' AND ladderid = '" + ladderid + "' order by giftgroup,giftgrouprow";
	    		GlobalInfo.baseDB.setSql(sqlstr);
	
	    		// 档期规则对应的赠品清单
	        	rs = GlobalInfo.baseDB.selectData();
	        	while(rs.next())
	        	{
	        		CmPopGiftsDef cmgift = new CmPopGiftsDef();
	    			if (!GlobalInfo.baseDB.getResultSetToObject(cmgift))
	                {
	                    return null;
	                }
	    			giftvec.add(cmgift);
	        	}
	        	GlobalInfo.baseDB.resultSetClose();
	        	
	        	// 找到对应阶梯的赠品则跳出循环
	        	if (giftvec.size() > 0) break;
	        	else
	        	{
	        		if (ladderid.equals("%")) break;
	        		else ladderid = "%";
	        	}
    		} while(true);
    		
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
    		
    		PublicMethod.timeEnd(Language.apply("查询本地促销赠品耗时: "));
    	}
    }
    // CMPOP促销 end 
}
