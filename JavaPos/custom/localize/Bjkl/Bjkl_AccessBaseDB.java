package custom.localize.Bjkl;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.TimeDate;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.PublicMethod;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.OperRoleDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SuperMarketPopRuleDef;

import custom.localize.Bstd.Bstd_AccessBaseDB;

public class Bjkl_AccessBaseDB extends Bstd_AccessBaseDB
{

	//得到授权人员信息
    public boolean getGrantOperUser(String id, OperUserDef user)
    {
        ResultSet rs = null;
        
        try
        {
        	//
        	PublicMethod.timeStart(Language.apply("正在查询本地人员库,请等待......"));
        	
            rs = GlobalInfo.baseDB.selectData("select * from OPERUSER where gh = '" +
                                              id + "'");

            if (rs == null)
            {
                return false;
            }

            if (rs.next())
            {
                if (!GlobalInfo.baseDB.getResultSetToObject(user))
                {
                    return false;
                }

                //
                GlobalInfo.baseDB.resultSetClose();

                // 查找角色
                rs = GlobalInfo.localDB.selectData("select * from OPERROLE where code = '" +
                                                   user.role + "'");

                if (rs == null)
                {
                    return false;
                }

                if (rs.next())
                {
                    OperRoleDef role = new OperRoleDef();

                    if (!GlobalInfo.localDB.getResultSetToObject(role))
                    {
                        return false;
                    }

                    user.operrange = role.operrange;
                    user.isgrant   = role.isgrant;
                    user.privth    = role.privth;
                    user.privqx    = role.privqx;
                    user.privdy    = role.privdy;
                    user.privgj    = role.privgj;
                    user.priv      = role.priv;
                    user.dpzkl     = role.dpzkl;
                    user.zpzkl     = role.zpzkl;
                    user.thxe      = role.thxe;
                    user.privje1   = role.privje1;
                    user.privje2   = role.privje2;
                    user.privje3   = role.privje3;
                    user.privje4   = role.privje4;
                    user.privje5   = role.privje5;
                    user.grantgz   = role.grantgz;
                    user.funcmenu  = role.funcmenu;
                }

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
            PublicMethod.timeEnd(Language.apply("查询本地人员库耗时: "));                        
        }

        return false;
    }
	// 查找超市促销规则
	public boolean findSuperMarketPopRule(Vector ruleReqList, Vector rulePopList, SuperMarketPopRuleDef popRule)
	{
		ResultSet rs = null;

		try
		{
			PublicMethod.timeStart("正在查询本地促销信息,请等待......");

			// 商品参与范围
			String sqlstr = "select " +
					"ppiseq,ppibillno,ppimode,ppibarcode,ppimfid," +
					"ppicatid,ppippcode,ppispec,ppistartdate,ppienddate," +
					"ppistarttime,ppiendtime,ppizkfd,ppispace,ppinewsj," +
					"ppinewhyj,ppinewrate,ppinewhyrate,ppinewpfj,ppinewpfrate," +
					"ppihyzkfd,ppipfzkfd,pphdjlb," + nvl("ppimaxnum", "0") +",ppipresentcode,"+
					"ppititle," + nvl("ppimaxnum", "0") +",ppipresentcode,ppipresentunit," +nvl("ppipresentjs", "0") + "," +
					nvl("ppipresentsl", "0") + "," + nvl("ppipresentxl", "0") + "," + nvl("ppipresentjg", "0") +",pphjc,pphstr1," +
					"pphstr2,pphstr3,pphstr4,pphstr5,pphstr6,pphiszsz,pphistjn,pphisptgz" +
					" from goodspopinfo where ppibillno='" + popRule.djbh + "'";
	
			System.out.println("查询详单:  "+sqlstr);
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
				ruleDef.yhspace =Convert.toDouble(rs.getString(14));
				ruleDef.yhlsj = Convert.toDouble(rs.getString(15));
				ruleDef.yhhyj = Convert.toDouble(rs.getString(16));
				ruleDef.yhzkl = Convert.toDouble(rs.getString(17));
				ruleDef.yhhyzkl = Convert.toDouble(rs.getString(18));
				ruleDef.yhpfj = Convert.toDouble(rs.getString(19));
				ruleDef.yhpfzkl = Convert.toDouble(rs.getString(20));
				ruleDef.yhhyzkfd = Convert.toDouble(rs.getString(21));
				ruleDef.yhpfzkfd = Convert.toDouble(rs.getString(22));
				ruleDef.yhdjlb= rs.getString(23).charAt(0) ;
				ruleDef.yhplsl= Convert.toDouble(rs.getString(24));
				ruleDef.presentcode= rs.getString(25);
				ruleDef.title= rs.getString(26);
				ruleDef.maxnum=Convert.toDouble(rs.getString(27));
				ruleDef.presentcode1= rs.getString(28);
				ruleDef.presentunit= rs.getString(29);
				ruleDef.presentjs= Convert.toDouble(rs.getString(30));
				ruleDef.presentsl= Convert.toDouble(rs.getString(31));
				ruleDef.presentxl= Convert.toDouble(rs.getString(32));
				ruleDef.presentjg= Convert.toDouble(rs.getString(33));
				ruleDef.jc= Convert.toLong(rs.getString(34));
				ruleDef.ppistr1= rs.getString(35);
				ruleDef.ppistr2= rs.getString(36);
				ruleDef.ppistr3= rs.getString(37);
				ruleDef.ppistr4= rs.getString(38);
				ruleDef.ppistr5= rs.getString(39);
				ruleDef.ppistr6= rs.getString(40);
				ruleDef.iszsz= rs.getString(41);
				ruleDef.istjn= rs.getString(42);
				ruleDef.isptgz=rs.getString(43);
			
				
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

	//得到通查条件
	private String getCommCond(String tab,String vcode,String vgz, String vpp,String vcatid,String vspec,double vscsjjg)
	{
		return "("+tab+".PPIBARCODE = '"+vcode + "' AND " +
				"("+tab+".PPIMFID ='"+ vgz+"' OR "+tab+".PPIMFID = '0') AND "+
                 tab+".PPISPEC  ='"+vspec+ "' AND "+tab+".PPIMAXNUM <> 0 AND "+
				"("+tab+".PPIMODE = '1' OR ("+tab+".PPIMODE = '7' AND "+ vscsjjg+ " >= "+tab+".PPISPACE))) OR "+
				"("+tab+".PPIBARCODE ='"+ vgz+"' AND "+tab+".PPIMODE = '2') OR "+
				"("+tab+".PPIBARCODE = '"+vgz+"' AND "+tab+".PPIPPCODE ='"+ vpp +"'AND "+
				tab+".PPIMODE = '4') OR ('"+ vcatid +"' like "+tab+".PPIBARCODE||'%' AND "+
				tab+".PPIMODE = '3') OR ('"+ vcatid +"' like "+tab+".PPIBARCODE||'%' AND "+
				tab+".PPIPPCODE ='"+ vpp+"' AND "+tab+".PPIMODE = '5') OR "+
				"("+tab+".PPIBARCODE = '"+vpp+"' AND "+tab+".PPIMODE = '6')";
	}
	
	
	private boolean getRuleDef (ResultSet rs,SuperMarketPopRuleDef ruleDef)
	{
		try
		{
			if(rs !=null)
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
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			GlobalInfo.baseDB.resultSetClose();
		}
	}
	public boolean findSuperMarketPopBillNo(SuperMarketPopRuleDef ruleDef, String code, String gz, String catid, String pp, String uid, String scsj, String yhsj, String custno)
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
			
			String retfield ="SELECT PPISEQ,PPIMODE,PPIBILLNO,PPIBARCODE,PPIMFID,PPICATID," +
					"PPIPPCODE,PPISPEC,PPINEWSJ,PPINEWHYJ,PPINEWRATE,PPINEWHYRATE,PPIZKFD," +
					"PPISTARTDATE,PPIENDDATE,PPISTARTTIME,PPIENDTIME,PPISPACE,PPINEWPFJ," + 
					"PPINEWPFRATE,PPIHYZKFD,PPIPFZKFD,PPIMAXNUM FROM GOODSPOPINFO A WHERE "; 
			
			String where =" A.PPISTARTDATE <='"+
							today +"' AND A.PPIENDDATE   >='"+
							today+"' AND A.PPISTARTTIME <='"+
							sj+"' AND A.PPIENDTIME   >='"+sj +"' ";
			
			String exist = " Exists (Select 'x' From GOODSPOPINFOTIME C  Where "+
								"C.PPIBILLNO = A.PPIBILLNO And C.PPIKSRQ  <='" + 
								today + "' And C.PPIJSRQ  >='" +
								today + "' And C.PPIKSSJ <='"+ sj+
								"' And C.PPIJSSJ >= '"+sj + "') ";
			
			
			boolean isvip = false;
			if(custno !=null && custno.length()>0)
				isvip = true;
			
			double billamount =0.0;
			if(Convert.toDouble(gz)>0)
				billamount = Convert.toDouble(gz);
				
			StringBuffer sb =new StringBuffer();
			
			if(isvip)
			{
				if(code.equalsIgnoreCase("ALL"))
				{
					sb.append(retfield).append(where);
					sb.append("AND A.PPHISPTGZ  = '1' And A.PPIPRESENTSL =0 AND A.PPIHYZKFD =0 AND ");
					sb.append(exist);
					sb.append(" AND A.PPHDJLB = '8' AND A.PPIBARCODE = '"+code+"' AND A.PPIMODE = '8' ");
					sb.append(" order by PPISEQ DESC limit 1 ");
					
					System.out.println("查询明细1： " + sb.toString());
					GlobalInfo.baseDB.setSql(sb.toString());
			
					if(getRuleDef(GlobalInfo.baseDB.selectData(),ruleDef))
						return true;
				}
				else
				{
					sb.append(retfield).append(where);
					sb.append("AND A.PPHISPTGZ  = '1' And A.PPIPRESENTSL =0 AND A.PPIHYZKFD =0 AND ");
					sb.append(exist);
					sb.append(" AND A.PPHDJLB = '8' AND ( ").append(getCommCond("A",code,gz,pp,catid,uid,sjjg)).append(" )");
					sb.append(" AND Not Exists (Select 'x' From GOODSPOPINFO B Where B.PPIBILLNO = A.PPIBILLNO And B.PPHDJLB = '8' AND (");
					sb.append(getCommCond("B",code,gz,pp,catid,uid,sjjg)).append(")");
					sb.append(" AND B.PPIPRESENTSL = 1 And B.PPHISTJN <>'1')");
					sb.append(" order by PPISEQ DESC limit 1 ");
					
					System.out.println("查询明细2： " + sb.toString());
			
					GlobalInfo.baseDB.setSql(sb.toString());
					ResultSet rs = GlobalInfo.baseDB.selectData();
					
					if(rs.next())
						if(getRuleDef(rs,ruleDef))
							return true;
				}
				
				if(sb.toString().length()>0)
					sb.delete(0, sb.toString().length() - 1);
				
				if(code.equalsIgnoreCase("ALL"))
				{
					sb.append(retfield).append(where);
					sb.append("AND A.PPHISPTGZ  = '0' And A.PPIPRESENTSL =0 AND A.PPIHYZKFD =0 AND ");
					sb.append(exist);
					sb.append(" AND A.PPHDJLB = '8' AND A.PPIBARCODE = '"+code+"' AND A.PPIMODE = '8' ");
					sb.append(" order by PPISEQ DESC limit 1 ");
					
					System.out.println("查询明细3： " + sb.toString());
					GlobalInfo.baseDB.setSql(sb.toString());
					
					ResultSet rs = GlobalInfo.baseDB.selectData();
					if(rs.next())
					{
						if(getRuleDef(rs,ruleDef))
							return true;
						
						return false;
					}
				}
				else
				{
					sb.append(retfield).append(where);
					sb.append("AND A.PPHISPTGZ  = '0' And A.PPIPRESENTSL =0 AND A.PPIHYZKFD =0 AND ");
					sb.append(exist);
					sb.append(" AND A.PPHDJLB = '8' AND ( ").append(getCommCond("A",code,gz,pp,catid,uid,sjjg)).append(" )");
					sb.append(" AND Not Exists (Select 'x' From GOODSPOPINFO B Where B.PPIBILLNO = A.PPIBILLNO And B.PPHDJLB = '8' AND(");
					sb.append(getCommCond("B",code,gz,pp,catid,uid,sjjg)).append(")");
					sb.append(" AND B.PPIPRESENTSL = 1 And B.PPHISTJN <>'1')");
					sb.append(" order by PPISEQ DESC limit 1 ");
					
					System.out.println("查询明细4： " + sb.toString());
			
					GlobalInfo.baseDB.setSql(sb.toString());
					
					ResultSet rs = GlobalInfo.baseDB.selectData();
					if(rs.next())
					{
						if(getRuleDef(rs,ruleDef))
							return true;
						
						return false;
					}
				}
			}
			if(sb.toString().length()>0)
				sb.delete(0, sb.toString().length() - 1);
			
			if(code.equalsIgnoreCase("ALL"))
			{
				sb.append(retfield).append(where);
				sb.append("AND A.PPHISPTGZ  = '1' And A.PPIPRESENTSL =0 AND A.PPIHYZKFD =1 AND ");
				sb.append(exist);
				sb.append(" AND A.PPHDJLB = '8' AND A.PPIBARCODE = '"+code+"'  AND A.PPIMODE = '8' ");
				sb.append(" order by PPISEQ DESC limit 1 ");
				
				System.out.println("查询明细5： " + sb.toString());
				GlobalInfo.baseDB.setSql(sb.toString());
				
				ResultSet rs = GlobalInfo.baseDB.selectData();
				if(rs.next())
				{
					if(getRuleDef(rs,ruleDef))
						return true;
					
					return false;
				}
			}
			else
			{
				sb.append(retfield).append(where);
				sb.append("AND A.PPHISPTGZ  = '1' And A.PPIPRESENTSL =0 AND A.PPIHYZKFD =1 AND ");
				sb.append(exist);
				sb.append(" AND A.PPHDJLB = '8' AND ( ").append(getCommCond("A",code,gz,pp,catid,uid,sjjg)).append(" )");
				sb.append(" AND Not Exists (Select 'x' From GOODSPOPINFO B Where B.PPIBILLNO = A.PPIBILLNO And B.PPHDJLB = '8' AND(");
				sb.append(getCommCond("B",code,gz,pp,catid,uid,sjjg)).append(")");
				sb.append(" AND B.PPIPRESENTSL = 1 And B.PPHISTJN <>'1')");
				sb.append(" order by PPISEQ DESC limit 1 ");
				
				System.out.println("查询明细6： " + sb.toString());
		
				GlobalInfo.baseDB.setSql(sb.toString());
				
				ResultSet rs = GlobalInfo.baseDB.selectData();
				if(rs.next())
				{
					if(getRuleDef(rs,ruleDef))
						return true;
					
					return false;
				}
			}
			if(sb.toString().length()>0)
				sb.delete(0, sb.toString().length() - 1);
			
			if(code.equalsIgnoreCase("ALL"))
			{
				sb.append(retfield).append(where);
				sb.append("AND A.PPHISPTGZ  = '0' And A.PPIPRESENTSL =0 AND A.PPIHYZKFD =1 AND ");
				sb.append(exist);
				sb.append(" AND A.PPHDJLB = '8' AND A.PPIBARCODE = '"+code+"'  AND A.PPIMODE = '8' ");
				sb.append(" order by PPISEQ DESC limit 1 ");
				
				System.out.println("查询明细7： " + sb.toString());
				GlobalInfo.baseDB.setSql(sb.toString());
		
				ResultSet rs = GlobalInfo.baseDB.selectData();
				if(rs.next())
				{
					if(getRuleDef(rs,ruleDef))
						return true;
					
					return false;
				}
			}
			else
			{
				sb.append(retfield).append(where);
				sb.append("AND A.PPHISPTGZ  = '0' And A.PPIPRESENTSL =0 AND A.PPIHYZKFD =1 AND ");
				sb.append(exist);
				sb.append(" AND A.PPHDJLB = '8' AND ( ").append(getCommCond("A",code,gz,pp,catid,uid,sjjg)).append(" )");
				sb.append(" AND Not Exists (Select 'x' From GOODSPOPINFO B Where B.PPIBILLNO = A.PPIBILLNO And B.PPHDJLB = '8' AND(");
				sb.append(getCommCond("B",code,gz,pp,catid,uid,sjjg)).append(")");
				sb.append(" AND B.PPIPRESENTSL = 1 And B.PPHISTJN <>'1')");
				sb.append(" order by PPISEQ DESC limit 1 ");
				
				System.out.println("查询明细8： " + sb.toString());
		
				GlobalInfo.baseDB.setSql(sb.toString());
				
				ResultSet rs = GlobalInfo.baseDB.selectData();
				if(rs.next())
				{
					if(getRuleDef(rs,ruleDef))
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

	public boolean findSuperMarketPopBillN1o(SuperMarketPopRuleDef ruleDef, String vcode, String vgz, String vdzxl, String vpp, String vspec, String vscsj, String vyhsj, String vcustno)
	{
		int ret;
		double vseqno;
		double vscsjjg;
		String vsj;
		double vsale;
		char vhyzx;

		ResultSet rs = null;

		if (vcustno == null || vcustno.length() == 0)
		{
			vhyzx = '1';
		}
		else
		{
			vhyzx = '0';
		}
		if (vyhsj == null || vyhsj.equals(""))
		{
			ret = 0;
			return false;
		}

		// LET vsj=TO_CHAR(TO_DATE(vyhsj,'%Y/%m/%d %H:%M:%S'),'%H:%M');
		vsj = vyhsj.substring(vyhsj.indexOf(" ")).trim();

		if (vscsj.trim() == null || vscsj.trim().equals(""))
		{
			vscsjjg = 0;
		}
		else
		{
			ManipulateDateTime a = new ManipulateDateTime();
			vscsjjg = a.getDisDateTimeByMS(vscsj, vyhsj);// 日期相减;
		}

		StringBuffer sqlstr = new StringBuffer();

		// 如果是会员
		if (vhyzx == '0')
		{
			if (vcode.equals("ALL"))
			{
				if (Convert.isNumber(vgz))
				{
					vsale = Convert.toDouble(vgz);
				}
				else
				{
					vsale = 0;
				}
				System.out.println("会员排外 ALL");
				// 查找正在生效的不大于应付款的金额最大的，序号最大的那条优惠原则
				sqlstr.append("SELECT").append(nvl("PPISEQ", "0"));
				sqlstr.append("FROM ( SELECT ").append(nvl("MAX(PPISEQ)", "0")).append("PPISEQ");
				sqlstr.append("       FROM GOODSPOPINFO A ");
				sqlstr.append("       WHERE A.PPISTARTDATE <= date('now') AND ");
				sqlstr.append("             A.PPISTARTTIME >=  date('now') AND ");
				sqlstr.append("             A.PPISTARTTIME <= '" + vsj + "' AND ");
				sqlstr.append("             A.PPIENDTIME   >= '" + vsj + "'  AND ");
				sqlstr.append("             A.PPHISPTGZ    = '1' AND ");
				sqlstr.append("             (").append(nvl("A.PPIPRESENTSL", "0")).append("= '0')");
				sqlstr.append("             AND ");
				sqlstr.append(nvl("A.PPIHYZKFD", "1")).append(" ='0' AND ");
				sqlstr.append("             Exists (Select 'x' ");
				sqlstr.append("                     From GOODSPOPINFOTIME C ");
				sqlstr.append("                     Where  C.PPIBILLNO = A.PPIBILLNO AND ");
				sqlstr.append("                            C.PPIKSRQ  <=  date('now') AND ");// strftime('%Y/%m/%d
				                                                                             // %H:%M:%S',Sysdate)
				sqlstr.append("                            C.PPIJSRQ  >= date('now') AND ");
				sqlstr.append("                            C.PPIKSSJ <= '" + vsj + "' AND ");
				sqlstr.append("                            C.PPIJSSJ >= '" + vsj + "'");
				sqlstr.append("                     ) AND ");
				sqlstr.append("             A.PPHDJLB = '8' AND ");
				sqlstr.append("             A.PPIBARCODE = '" + vcode + "' AND ");
				sqlstr.append("             A.PPIMODE = '8' ");
				sqlstr.append("        ORDER BY A.PPINEWSJ DESC, A.PPISEQ DESC");
				sqlstr.append("     )");
				// sqlstr.append("  WHERE ROWNUM = 1");

			}
			else
			{
				System.out.println("会员排外 ");
				sqlstr.append("SELECT ").append(nvl("MAX(PPISEQ)", "0"));
				sqlstr.append("FROM GOODSPOPINFO A");
				sqlstr.append("WHERE A.PPISTARTDATE <= date('now') AND ");
				sqlstr.append("      A.PPIENDDATE   >= date('now') AND ");
				sqlstr.append("      A.PPISTARTTIME <= '" + vsj + "' AND ");
				sqlstr.append("      A.PPIENDTIME   >= '" + vsj + "' AND ");
				sqlstr.append("      A.PPHISPTGZ    = '1' AND ");
				sqlstr.append("      (").append(nvl("A.PPIPRESENTSL", "0")).append("= '0' ) ");
				sqlstr.append("      AND ");
				sqlstr.append(nvl("A.PPIHYZKFD", "1")).append(" ='0' AND ");
				sqlstr.append("      Exists (Select 'x'");
				sqlstr.append("              From GOODSPOPINFOTIME C");
				sqlstr.append("              Where  C.PPIBILLNO = A.PPIBILLNO AND ");
				sqlstr.append("                     C.PPIKSRQ  <= date('now') AND ");
				sqlstr.append("                     C.PPIJSRQ  >= date('now') AND ");
				sqlstr.append("                     C.PPIKSSJ <= '" + vsj + "' AND ");
				sqlstr.append("                     C.PPIJSSJ >= '" + vsj + "'");
				sqlstr.append("             ) AND ");
				sqlstr.append("      A.PPHDJLB = '8' AND ");
				sqlstr.append("    (");
				sqlstr.append("      (  A.PPIBARCODE = '" + vcode + "' AND  (A.PPIMFID = '" + vgz + "' OR A.PPIMFID = '0') AND ");
				sqlstr.append(nvl("LTRIM(A.PPISPEC)", "00")).append("=").append(nvl("LTRIM(''" + vspec + "'')", "00")).append("AND A.PPIMAXNUM <> 0 AND ");
				sqlstr.append("( A.PPIMODE = '1' OR (A.PPIMODE = '7' AND  '" + vscsjjg + "' >= A.PPISPACE))");
				sqlstr.append(") OR");
				sqlstr.append("(A.PPIBARCODE =" + vgz + " AND  A.PPIMODE = '2') OR ");
				sqlstr.append("  (A.PPIBARCODE = '" + vgz + "' AND  D A.PPIPPCODE = '" + vpp + "' AND A.PPIMODE = '4') OR");
				sqlstr.append("('" + vdzxl + "'  like A.PPIBARCODE||'%' AND A.PPIMODE = '3') OR");
				sqlstr.append("('" + vdzxl + "'  like A.PPIBARCODE||'%' AND A.PPIPPCODE = " + vpp + " AND A.PPIMODE = '5') OR");
				sqlstr.append("(A.PPIBARCODE = " + vpp + " AND A.PPIMODE = '6')");
				sqlstr.append(")  AND ");
				sqlstr.append("Not Exists (Select 'x' From GOODSPOPINFO B Where B.PPIBILLNO = A.PPIBILLNO  AND ");
				sqlstr.append("B.PPHDJLB = '8'  AND ");
				sqlstr.append("(");
				sqlstr.append("(  B.PPIBARCODE = '" + vcode + "'  AND  (B.PPIMFID = '" + vgz + "' OR B.PPIMFID = '0')  AND ");
				sqlstr.append("" + nvl("LTRIM(PPISPEC)", "00") + " = " + nvl("LTRIM('" + vspec + "')", "00") + "  AND  B.PPIMAXNUM <> 0  AND ");
				sqlstr.append("( PPIMODE = '1' OR (PPIMODE = '7'  AND  '" + vscsjjg + "' >= PPISPACE))");
				sqlstr.append(") OR");
				sqlstr.append("(B.PPIBARCODE = '" + vgz + "'  AND  B.PPIMODE = '2') OR ");
				sqlstr.append("(B.PPIBARCODE = '" + vgz + "' AND B.PPIPPCODE = '" + vpp + "' AND B.PPIMODE = '4') OR");
				sqlstr.append("('" + vdzxl + "'  like B.PPIBARCODE||'%' AND B.PPIMODE = '3') OR");
				sqlstr.append("('" + vdzxl + "'  like B.PPIBARCODE||'%' AND B.PPIPPCODE = '" + vpp + "' AND B.PPIMODE = '5') OR");
				sqlstr.append("(B.PPIBARCODE = '" + vpp + "' AND B.PPIMODE = '6')");
				sqlstr.append(")  AND ");
				sqlstr.append("nvl(b.PPIPRESENTSL,'0') = '1' And B.PPHISTJN <>'1'");
				sqlstr.append(")");
			}
			System.out.println(sqlstr.toString());
			Object obj = GlobalInfo.baseDB.selectOneData(sqlstr.toString());
			sqlstr.delete(0, sqlstr.toString().trim().length());
			int plsl = 0;
			if (obj == null)
			{
				return false;
			}
			else
			{
				plsl = Convert.toInt((String.valueOf(obj)));
				if (plsl == 0)
					return false;
			}

			if (plsl > 0)
			{
				String sql = "SELECT PPIBILLNO FROM GOODSPOPINFO WHERE PPISEQ = " + plsl;
				obj = GlobalInfo.baseDB.selectOneData(sql);
				ret = 1;
				ruleDef.djbh = obj.toString();
				if ((ret == 1) && (obj != null))
				{
					sql = "SELECT COUNT(*) FROM GOODSPOPINFO WHERE PPIBILLNO = " + obj.toString();
					obj = GlobalInfo.baseDB.selectOneData(sql);
					ruleDef.yhspace = Convert.toDouble(obj.toString());
				}
				return true;
			}

			String sql = "";
			if (vcode.equals("ALL"))
			{
				if (Convert.isNumber(vgz))
				{
					vsale = Convert.toDouble(vgz);
				}
				else
				{
					vsale = 0;
				}

				System.out.println("会员 ALL");
				sql = "SELECT /*+ RULE*/" + nvl("PPISEQ", "0") + "FROM ( SELECT  PPISEQ" + "          FROM GOODSPOPINFO X " + "         WHERE PPISTARTDATE <= TRUNC(SYSDATE)  AND " + "                PPIENDDATE   >=  TRUNC(SYSDATE) AND" + "                PPISTARTTIME <= '" + vsj + "' AND" + "                PPIENDTIME   >= '" + vsj + "'  And" + "               " + nvl("PPHISPTGZ", "0") + "    = '0' And" + "              (" + nvl("PPIPRESENTSL", "0") + " = '0'" + "               )" + "               And" + "               " + nvl("PPIHYZKFD", "1") + " ='0' And" + "               Exists (Select 'x'" + "                        From GOODSPOPINFOTIME A" + "                        Where  A.PPIBILLNO = X.PPIBILLNO And" + "                               A.PPIKSRQ  <= TRUNC(SYSDATE) And" + "                               A.PPIJSRQ  >= TRUNC(Sysdate) And" + "                               A.PPIKSSJ <= '" + vsj + "' And" + "                               A.PPIJSSJ >= " + vsj + "                        ) And" + "             PPHDJLB = '8' AND" + "             PPIBARCODE = '" + vcode + "' AND" + "             PPIMODE = '8' "/*
																																																																																																																																																																																																																																																																																										 * AND
																																																																																																																																																																																																																																																																																										 * PPINEWSJ
																																																																																																																																																																																																																																																																																										 * <=
																																																																																																																																																																																																																																																																																										 * VSALE
																																																																																																																																																																																																																																																																																										 */
				        + "       ORDER BY PPINEWSJ DESC, PPISEQ DESC" + "    )";
				// +"WHERE ROWNUM = 1";

			}
			else
			{
				System.out.println("会员");
				sqlstr.append("SELECT /*+ RULE*/ " + nvl("PPISEQ", "0"));
				sqlstr.append("INTO vseqno");
				sqlstr.append("FROM GOODSPOPINFO A");
				sqlstr.append("WHERE A.PPISTARTDATE <= date('now')  AND ");
				sqlstr.append("A.PPIENDDATE   >=  date('now')  AND ");
				sqlstr.append("A.PPISTARTTIME <= '" + vsj + "'  AND ");
				sqlstr.append("A.PPIENDTIME   >= '" + vsj + "'  AND ");
				sqlstr.append("" + nvl("A.PPHISPTGZ", "0") + "    = '0'  AND ");
				sqlstr.append("(" + nvl("A.PPIPRESENTSL", "0") + " = '0' ");

				sqlstr.append(")");
				sqlstr.append(" AND ");
				sqlstr.append("" + nvl("A.PPIHYZKFD", "1") + " ='0'  AND ");
				sqlstr.append("Exists (Select 'x'");
				sqlstr.append("From GOODSPOPINFOTIME C");
				sqlstr.append("Where  C.PPIBILLNO = A.PPIBILLNO  AND ");
				sqlstr.append("C.PPIKSRQ  <= date('now')  AND ");
				sqlstr.append("C.PPIJSRQ  >= date('now')  AND ");
				sqlstr.append("C.PPIKSSJ <= '" + vsj + "'  AND ");
				sqlstr.append("C.PPIJSSJ >= '" + vsj + "'");
				sqlstr.append(")  AND ");
				sqlstr.append("A.PPHDJLB = '8'  AND ");
				sqlstr.append("(");
				sqlstr.append("(  A.PPIBARCODE = '" + vcode + "'  AND  (A.PPIMFID = '" + vgz + "' OR A.PPIMFID = '0')  AND ");
				sqlstr.append("" + nvl("LTRIM(PPISPEC)", "00") + " = " + nvl("LTRIM('" + vspec + "')", "00") + " AND  A.PPIMAXNUM <> 0  AND ");
				sqlstr.append("( A.PPIMODE = '1' OR (A.PPIMODE = '7'  AND  '" + vscsjjg + "' >= A.PPISPACE))");
				sqlstr.append(") OR");
				sqlstr.append("(A.PPIBARCODE = '" + vgz + "'  AND  A.PPIMODE = '2') OR");
				sqlstr.append("(A.PPIBARCODE = '" + vgz + "'  AND  A.PPIPPCODE = '" + vpp + "'  AND  A.PPIMODE = '4') OR");
				sqlstr.append("('" + vdzxl + "'  like A.PPIBARCODE||'%'  AND  A.PPIMODE = '3') OR");
				sqlstr.append("('" + vdzxl + "'  like A.PPIBARCODE||'%'  AND  A.PPIPPCODE = '" + vpp + "'  AND  A.PPIMODE = '5') OR");
				sqlstr.append("(A.PPIBARCODE = '" + vpp + "'  AND  A.PPIMODE = '6')");
				sqlstr.append(")  AND ");
				sqlstr.append("Not Exists (Select 'x' From GOODSPOPINFO B Where B.PPIBILLNO = A.PPIBILLNO  AND ");
				sqlstr.append("B.PPHDJLB = '8'  AND ");
				sqlstr.append("(");
				sqlstr.append("(  B.PPIBARCODE = '" + vcode + "'  AND  (B.PPIMFID = '" + vgz + "' OR B.PPIMFID = '0')  AND ");
				sqlstr.append("" + nvl("LTRIM(PPISPEC)", "00") + " = " + nvl("LTRIM('" + vspec + "')", "00") + "AND  B.PPIMAXNUM <> 0  AND ");
				sqlstr.append("( PPIMODE = '1' OR (PPIMODE = '7'  AND  '" + vscsjjg + "' >= PPISPACE))");
				sqlstr.append(") OR");
				sqlstr.append("(B.PPIBARCODE = '" + vgz + "'  AND  B.PPIMODE = '2') OR");
				sqlstr.append("(B.PPIBARCODE = '" + vgz + "'  AND  B.PPIPPCODE = '" + vpp + "'  AND  B.PPIMODE = '4') OR");
				sqlstr.append("('" + vdzxl + "'  like B.PPIBARCODE||'%'  AND  B.PPIMODE = '3') OR");
				sqlstr.append("('" + vdzxl + "'  like B.PPIBARCODE||'%'  AND  B.PPIPPCODE = '" + vpp + "'  AND  B.PPIMODE = '5') OR");
				sqlstr.append("(B.PPIBARCODE = '" + vpp + "'  AND  B.PPIMODE = '6')");
				sqlstr.append(")  AND ");
				sqlstr.append("" + nvl("b.PPIPRESENTSL", "0") + " = '1'  AND  B.PPHISTJN <>'1'");
				sqlstr.append(");");
				sql = sqlstr.toString();
			}
			System.out.println(sql);
			obj = GlobalInfo.baseDB.selectOneData(sql);
			sqlstr.delete(0, sqlstr.toString().trim().length());
			plsl = 0;
			if (obj == null)
			{
				return false;
			}
			else
			{
				plsl = Convert.toInt((String.valueOf(obj)));
				if (plsl == 0)
					return false;
			}
			if (plsl > 0)
			{
				sql = "SELECT PPIBILLNO FROM GOODSPOPINFO WHERE PPISEQ = " + plsl;
				obj = GlobalInfo.baseDB.selectOneData(sql);
				ret = 1;
				ruleDef.djbh = obj.toString();
				if ((ret == 1) && (obj != null))
				{
					sql = "SELECT COUNT(*) FROM GOODSPOPINFO WHERE PPIBILLNO = " + obj.toString();
					obj = GlobalInfo.baseDB.selectOneData(sql);
					ruleDef.yhspace = Convert.toDouble(obj.toString());
				}
				return true;
			}
		}

		// 非会员部分
		// ***********************非会员部分********************
		// 在这里优先找排它规则数据
		if (vcode == "ALL")
		{
			if (Convert.isNumber(vgz))
			{
				vsale = Convert.toDouble(vgz);
			}
			else
			{
				vsale = 0;
			}
			System.out.println("非会员排外 ALL");
			sqlstr.append("SELECT   " + nvl("PPISEQ", "0"));
			sqlstr.append("FROM ( SELECT " + nvl("max(PPISEQ)", "0") + " PPISEQ ");
			sqlstr.append("FROM GOODSPOPINFO X ");
			sqlstr.append("WHERE PPISTARTDATE <= date('now') AND ");
			sqlstr.append("PPIENDDATE   >=  date('now') AND ");
			sqlstr.append("PPISTARTTIME <= '" + vsj + "' AND ");
			sqlstr.append("PPIENDTIME   >= '" + vsj + "'  And ");
			sqlstr.append("" + nvl("PPIHYZKFD", "1.0") + " = '1.0' And  ");
			sqlstr.append("PPHISPTGZ = '1.0' And  ");
			sqlstr.append("(" + nvl("PPIPRESENTSL", "0.0") + " = '0.0' ");
			sqlstr.append(")");
			sqlstr.append("And ");
			sqlstr.append("Exists (Select 'x' ");
			sqlstr.append("From GOODSPOPINFOTIME A ");
			sqlstr.append("Where  A.PPIBILLNO = X.PPIBILLNO And ");
			sqlstr.append("A.PPIKSRQ  <= date('now') And ");
			sqlstr.append("A.PPIJSRQ  >= date('now') And ");
			sqlstr.append("A.PPIKSSJ <= '" + vsj + "' And ");
			sqlstr.append("A.PPIJSSJ >= '" + vsj + "' )");
			sqlstr.append(" And ");
			sqlstr.append("PPHDJLB = '8' AND ");
			sqlstr.append("PPIBARCODE = '" + vcode + "' AND ");
			sqlstr.append("PPIMODE = '8'  ");
			sqlstr.append("ORDER BY PPINEWSJ DESC, PPISEQ DESC ");
			sqlstr.append(")");
			// sqlstr.append("WHERE ROWNUM = 1;");

		}
		else
		{
			System.out.println("非会员排外 ");
			sqlstr.append("SELECT /*+ RULE*/ " + nvl("PPISEQ", "0"));
			sqlstr.append("FROM GOODSPOPINFO A ");
			sqlstr.append("WHERE A.PPISTARTDATE <= date('now') AND ");
			sqlstr.append("A.PPIENDDATE   >=	date('now') AND ");
			sqlstr.append("A.PPISTARTTIME <= '" + vsj + "' AND ");
			sqlstr.append("A.PPIENDTIME   >= '" + vsj + "' And ");
			sqlstr.append("" + nvl("A.PPIHYZKFD", "1.0") + " = '1.0' And     ");
			sqlstr.append("A.PPHISPTGZ   = '1.0' And ");
			sqlstr.append("(" + nvl("A.PPIPRESENTSL", "0.0") + " = '0.0' ");

			sqlstr.append(")");
			sqlstr.append("And ");
			sqlstr.append("Exists (Select 'x' ");
			sqlstr.append("From GOODSPOPINFOTIME C ");
			sqlstr.append("Where  C.PPIBILLNO = A.PPIBILLNO And ");
			sqlstr.append("C.PPIKSRQ  <= date('now') And ");
			sqlstr.append("C.PPIJSRQ   >= date('now')  AND ");
			sqlstr.append("C.PPIKSSJ <= '" + vsj + "'  AND ");
			sqlstr.append("C.PPIJSSJ >= '" + vsj + "'");
			sqlstr.append(")  AND ");
			sqlstr.append("A.PPHDJLB = '8'  AND ");
			sqlstr.append("(");
			sqlstr.append("(  A.PPIBARCODE = '" + vcode + "'  AND  (A.PPIMFID = '" + vgz + "' OR A.PPIMFID = '0')  AND ");
			sqlstr.append("" + nvl("LTRIM(PPISPEC)", "00") + " = " + nvl("LTRIM('" + vspec + "')", "00") + " AND  A.PPIMAXNUM <> 0  AND ");
			sqlstr.append("( A.PPIMODE = '1' OR (A.PPIMODE = '7'  AND  '" + vscsjjg + "' >= A.PPISPACE))");
			sqlstr.append(") OR");
			sqlstr.append("(A.PPIBARCODE = '" + vgz + "'  AND  A.PPIMODE = '2') OR");
			sqlstr.append("(A.PPIBARCODE = '" + vgz + "'  AND  A.PPIPPCODE = '" + vpp + "'  AND  A.PPIMODE = '4') OR");
			sqlstr.append("('" + vdzxl + "'  like A.PPIBARCODE||'%'  AND  A.PPIMODE = '3') OR");
			sqlstr.append("('" + vdzxl + "'  like A.PPIBARCODE||'%'  AND  A.PPIPPCODE = '" + vpp + "'  AND  A.PPIMODE = '5') OR");
			sqlstr.append("(A.PPIBARCODE = '" + vpp + "' AND A.PPIMODE = '6')");
			sqlstr.append(")  AND ");
			sqlstr.append("Not Exists (Select 'x' From GOODSPOPINFO B Where B.PPIBILLNO = A.PPIBILLNO  AND ");
			sqlstr.append("B.PPHDJLB = '8'  AND ");
			sqlstr.append("(");
			sqlstr.append("(  B.PPIBARCODE = '" + vcode + "'  AND  (B.PPIMFID = '" + vgz + "' OR B.PPIMFID = '0')  AND ");
			sqlstr.append("" + nvl("LTRIM(PPISPEC)", "00") + " = " + nvl("LTRIM(" + vspec + ")", "00") + " AND  B.PPIMAXNUM <> 0  AND ");
			sqlstr.append("( PPIMODE = '1' OR (PPIMODE = '7'  AND  '" + vscsjjg + "' >= PPISPACE))");
			sqlstr.append(") OR");
			sqlstr.append("(B.PPIBARCODE = '" + vgz + "'  AND  B.PPIMODE = '2') OR");
			sqlstr.append("(B.PPIBARCODE = '" + vgz + "'  AND  B.PPIPPCODE = '" + vpp + "'  AND  B.PPIMODE = '4') OR");
			sqlstr.append("('" + vdzxl + "'  like B.PPIBARCODE||'%'  AND  B.PPIMODE = '3') OR");
			sqlstr.append("('" + vdzxl + "'  like B.PPIBARCODE||'%'  AND  B.PPIPPCODE = '" + vpp + "'  AND  B.PPIMODE = '5') OR");
			sqlstr.append("(B.PPIBARCODE = '" + vpp + "'  AND  B.PPIMODE = '6')");
			sqlstr.append(")  AND ");
			sqlstr.append("" + nvl("b.PPIPRESENTSL", "0") + " = '1'  AND  B.PPHISTJN <>'1'");
			sqlstr.append(")");
		}

		String sql = null;
		System.out.println(sqlstr.toString());
		Object obj = GlobalInfo.baseDB.selectOneData(sqlstr.toString());
		sqlstr.delete(0, sqlstr.toString().trim().length());
		int plsl = 0;
		if (obj != null)
		{
			plsl = Convert.toInt((String.valueOf(obj)));
			// if (plsl == 0) return false;
		}
		if (plsl > 0)
		{
			sql = "SELECT PPIBILLNO FROM GOODSPOPINFO WHERE PPISEQ = " + plsl;
			obj = GlobalInfo.baseDB.selectOneData(sql);
			ret = 1;
			ruleDef.djbh = obj.toString();
			if ((ret == 1) && (obj != null))
			{
				sql = "SELECT COUNT(*) FROM GOODSPOPINFO WHERE PPIBILLNO = " + obj.toString();
				obj = GlobalInfo.baseDB.selectOneData(sql);
				ruleDef.yhspace = Convert.toDouble(obj.toString());
			}
			return true;
		}

		if (vcode.equals("ALL"))
		{
			if (Convert.isNumber(vgz))
			{
				vsale = Convert.toDouble(vgz);
			}
			else
			{
				vsale = 0;
			}
			System.out.println("非会员 ALL");
			sqlstr.append("SELECT /*+ RULE*/ " + nvl("PPISEQ", "0"));
			sqlstr.append("FROM ( SELECT " + nvl("max(PPISEQ)", "0") + " PPISEQ ");
			sqlstr.append("FROM GOODSPOPINFO X ");
			sqlstr.append("WHERE PPISTARTDATE <= date('now')   AND  ");
			sqlstr.append("PPIENDDATE   >=	date('now')  AND ");
			sqlstr.append("PPISTARTTIME <= '" + vsj + "'  AND ");
			sqlstr.append("PPIENDTIME   >= '" + vsj + "'	 AND ");

			sqlstr.append("" + nvl("X.PPIHYZKFD", "1.0") + " = '1.0' And     ");
			sqlstr.append("" + nvl("PPHISPTGZ", "0") + "= '0' And  ");
			sqlstr.append("(" + nvl("PPIPRESENTSL", "0.0") + " = '0.0' ");
			sqlstr.append(")");
			sqlstr.append(" AND ");
			sqlstr.append("Exists (Select 'x'");
			sqlstr.append("From GOODSPOPINFOTIME A ");
			sqlstr.append("Where  A.PPIBILLNO = X.PPIBILLNO  AND ");
			sqlstr.append("A.PPIKSRQ  <= date('now')  AND ");
			sqlstr.append("A.PPIJSRQ  >= date('now')  AND ");
			sqlstr.append("A.PPIKSSJ <= '" + vsj + "'  AND ");
			sqlstr.append("A.PPIJSSJ >= '" + vsj + "'");
			sqlstr.append(")  AND ");
			sqlstr.append(" PPHDJLB = '8'  AND ");
			sqlstr.append("PPIBARCODE = '" + vcode + "'  AND ");
			sqlstr.append("PPIMODE = '8' ");
			sqlstr.append("ORDER BY PPINEWSJ DESC, PPISEQ DESC");
			sqlstr.append(")");
			// sqlstr.append(" WHERE ROWNUM = 1");

		}
		else
		{
			System.out.println("非会员");
			sqlstr.append("SELECT /*+ RULE*/ " + nvl("PPISEQ", "0"));
			sqlstr.append("FROM GOODSPOPINFO A ");
			sqlstr.append("WHERE A.PPISTARTDATE <= date('now')  AND ");
			sqlstr.append("A.PPIENDDATE   >=	date('now')  AND ");
			sqlstr.append("A.PPISTARTTIME <= '" + vsj + "'  AND ");
			sqlstr.append("A.PPIENDTIME   >= '" + vsj + "'  AND ");
			sqlstr.append("" + nvl("A.PPIHYZKFD", "1.0") + " = '1.0' And     ");
			sqlstr.append("" + nvl("PPHISPTGZ", "0") + "= '0' And  ");
			sqlstr.append("(" + nvl("PPIPRESENTSL", "0.0") + " = '0.0' ");

			sqlstr.append(")");
			sqlstr.append(" AND ");
			sqlstr.append("Exists (Select 'x' ");
			sqlstr.append("From GOODSPOPINFOTIME C ");
			sqlstr.append("Where  C.PPIBILLNO = A.PPIBILLNO   AND  ");
			sqlstr.append("C.PPIKSRQ  <= date('now')  AND ");
			sqlstr.append("C.PPIJSRQ  >= date('now')  AND ");
			sqlstr.append("C.PPIKSSJ <= '" + vsj + "'  AND ");
			sqlstr.append("C.PPIJSSJ >= '" + vsj + "'");
			sqlstr.append(") And ");
			sqlstr.append("A.PPHDJLB = '8' AND");
			sqlstr.append("(");
			sqlstr.append("(  A.PPIBARCODE = '" + vcode + "'  AND  (A.PPIMFID = '" + vgz + "' OR A.PPIMFID = '0')  AND ");
			sqlstr.append("" + nvl("LTRIM(PPISPEC)", "00") + " = " + nvl("LTRIM('" + vspec + "')", "00") + "  AND  A.PPIMAXNUM <> 0  AND ");
			sqlstr.append("( A.PPIMODE = '1' OR (A.PPIMODE = '7'  AND  '" + vscsjjg + "' >= A.PPISPACE))");
			sqlstr.append(") OR");
			sqlstr.append("(A.PPIBARCODE = '" + vgz + "'  AND  A.PPIMODE = '2') OR");
			sqlstr.append("(A.PPIBARCODE = '" + vgz + "'  AND  A.PPIPPCODE = '" + vpp + "'  AND  A.PPIMODE = '4') OR");
			sqlstr.append("('" + vdzxl + "'  like A.PPIBARCODE||'%'  AND  A.PPIMODE = '3') OR");
			sqlstr.append("('" + vdzxl + "'  like A.PPIBARCODE||'%'  AND  A.PPIPPCODE = '" + vpp + "'  AND  A.PPIMODE = '5') OR");
			sqlstr.append("(A.PPIBARCODE = '" + vpp + "'  AND  A.PPIMODE = '6')");
			sqlstr.append(")  AND ");
			sqlstr.append("Not Exists (Select 'x' From GOODSPOPINFO B Where B.PPIBILLNO = A.PPIBILLNO  AND ");
			sqlstr.append("B.PPHDJLB = '8'  AND ");
			sqlstr.append("(");
			sqlstr.append("(  B.PPIBARCODE = '" + vcode + "'  AND  (B.PPIMFID = '" + vgz + "' OR B.PPIMFID = '0')  AND ");
			sqlstr.append("" + nvl("LTRIM(PPISPEC)", "00") + " = " + nvl("LTRIM('" + vspec + "')", "00") + "  AND  B.PPIMAXNUM <> 0  AND ");
			sqlstr.append("( PPIMODE = '1' OR (PPIMODE = '7'  AND  '" + vscsjjg + "' >= PPISPACE))");
			sqlstr.append(") OR");
			sqlstr.append("(B.PPIBARCODE = '" + vgz + "'  AND  B.PPIMODE = '2') OR");
			sqlstr.append("(B.PPIBARCODE = '" + vgz + "'  AND  B.PPIPPCODE = '" + vpp + "'  AND  B.PPIMODE = '4') OR");
			sqlstr.append("('" + vdzxl + "'  like B.PPIBARCODE||'%'  AND  B.PPIMODE = '3') OR");
			sqlstr.append("('" + vdzxl + "'  like B.PPIBARCODE||'%'  AND  B.PPIPPCODE = '" + vpp + "'  AND  B.PPIMODE = '5') OR");
			sqlstr.append("(B.PPIBARCODE = '" + vpp + "'  AND  B.PPIMODE = '6')");
			sqlstr.append(")  AND ");
			sqlstr.append("" + nvl("b.PPIPRESENTSL", "0") + " = '1'  AND  B.PPHISTJN <>'1'");
			sqlstr.append(")");
		}
		System.out.println(sqlstr.toString());
		obj = GlobalInfo.baseDB.selectOneData(sqlstr.toString());
		sqlstr.delete(0, sqlstr.toString().trim().length());
		if (obj == null)
		{
			return false;
		}
		else
		{
			plsl = Convert.toInt((String.valueOf(obj)));
			if (plsl == 0)
				return false;
		}
		if (plsl > 0)
		{
			sql = "SELECT PPIBILLNO FROM GOODSPOPINFO WHERE PPISEQ = " + plsl;
			obj = GlobalInfo.baseDB.selectOneData(sql);
			ret = 1;
			ruleDef.djbh = obj.toString();
			if ((ret == 1) && (obj != null))
			{
				sql = "SELECT COUNT(*) FROM GOODSPOPINFO WHERE PPIBILLNO = '" + obj.toString() + "'";
				obj = GlobalInfo.baseDB.selectOneData(sql);
				ruleDef.yhspace = Convert.toDouble(obj.toString());
			}
			return true;
		}
		return false;
	}

	public GoodsPopDef getPromotion(String code, String gz, String catid, String ppcode, String uid, String scsj, String yhsj)
	{
		if (yhsj == null || yhsj.length() <= 0)
			return null;

		ResultSet rs = null;

		double sjjg = 0; // 时间间隔
		String today = new ManipulateDateTime().getDateBySign();

		TimeDate timeObj = new TimeDate();
		timeObj.fullTime = yhsj;
		timeObj.split();
		String sj = timeObj.hh + ":" + timeObj.min;
		GoodsPopDef goods1 = null, goods2 = null;

		if (scsj.length() >= 8)
		{
			long timeDis = new ManipulateDateTime().getDisDateTimeByMS(scsj, yhsj);
			sjjg = timeDis / 1000; // 将时间之差转换成秒
		}

		try
		{
			// 交叉重叠的档期内先找单品促销最低价的
			String cond = "SELECT PPISEQ,PPIMODE,PPIBILLNO,PPIBARCODE,PPIMFID,PPICATID," + "PPIPPCODE,PPISPEC,PPINEWSJ,PPINEWHYJ,PPINEWRATE,PPINEWHYRATE,PPIZKFD," + "PPISTARTDATE,PPIENDDATE,PPISTARTTIME,PPIENDTIME,PPISPACE,PPINEWPFJ," + "PPINEWPFRATE,PPIHYZKFD,PPIPFZKFD,PPIMAXNUM FROM GOODSPOPINFO ";
			StringBuffer sb = new StringBuffer();

			sb.append(cond);
			sb.append("WHERE PPISTARTDATE <= '" + today + "' AND PPIENDDATE   >='" + today + "' And PPISTARTTIME <= '" + sj + "' AND PPIENDTIME   >= '" + sj + "' And ");
			sb.append("Exists (Select 'x' From GOODSPOPINFOTIME A Where  A.PPIBILLNO = PPIBILLNO And A.PPIKSRQ  <='" + today + "' And A.PPIJSRQ  >= '" + today + "' And ");
			sb.append("A.PPIKSSJ <='" + sj + "' And A.PPIJSSJ >='" + sj + "') And PPHDJLB ='1' AND");
			sb.append("(PPIBARCODE = " + code + " AND (PPIMFID ='" + gz + "' OR PPIMFID = '0') AND");
			sb.append(nvl("PPISPEC", "00") + "=" + nvl("'" + uid + "'", "00") + " AND PPIMAXNUM <> 0 AND (PPIMODE = '1' OR (PPIMODE = '7' AND " + sjjg + ">= PPISPACE)) AND");
			sb.append(" PPINEWSJ > 0)");
			sb.append(" order by PPINEWSJ limit 1 ");

			System.out.println("查询明细1： " + sb.toString());
			GlobalInfo.baseDB.setSql(sb.toString());

			rs = GlobalInfo.baseDB.selectData();

			if (rs.next())
			{
				goods1 = new GoodsPopDef();
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

			}

			GlobalInfo.baseDB.resultSetClose();

			sb.delete(0, sb.toString().length() - 1);

			// 查找最大序号商品
			sb.append(cond);
			sb.append("WHERE PPISTARTDATE <= '" + today + "' AND PPIENDDATE   >='" + today + "' And PPISTARTTIME <= '" + sj + "' AND PPIENDTIME   >= '" + sj + "' And ");
			sb.append("Exists (Select 'x' From GOODSPOPINFOTIME A Where  A.PPIBILLNO = PPIBILLNO And A.PPIKSRQ  <='" + today + "' And A.PPIJSRQ  >= '" + today + "' And ");
			sb.append("A.PPIKSSJ <='" + sj + "' And A.PPIJSSJ >='" + sj + "') And ");
			sb.append("PPHDJLB ='1' AND ");
			sb.append("((PPIBARCODE = " + code + " AND (PPIMFID ='" + gz + "' OR PPIMFID = '0') AND ");
			sb.append("PPISPEC = '00'  AND PPIMAXNUM <> 0 AND ");
			sb.append("(PPIMODE = '1' OR (PPIMODE = '7' AND " + sjjg + ">= PPISPACE))) OR ");
			sb.append("(PPIBARCODE = '" + gz + "' AND PPIMODE = '2') OR ");
			sb.append("(PPIBARCODE = '" + gz + "' AND PPIPPCODE = '" + ppcode + "' AND PPIMODE = '4') OR ");
			sb.append("(PPIBARCODE = '" + catid + "' AND PPIMODE = '3') OR ");
			sb.append("(PPIBARCODE = '" + catid + "' AND PPIPPCODE = '" + ppcode + "' AND PPIMODE = '5') OR ");
			sb.append("(PPIBARCODE = '" + ppcode + "'  AND PPIMODE = '6')) ");
			sb.append("order by PPISEQ DESC limit 1");

			System.out.println("查询明细2： " + sb.toString());
			GlobalInfo.baseDB.setSql(sb.toString());

			rs = GlobalInfo.baseDB.selectData();

			if (rs.next())
			{
				goods2 = new GoodsPopDef();
				goods2.seqno = Convert.toLong(rs.getString(1));
				goods2.type = rs.getString(2).charAt(0);
				goods2.djbh = rs.getString(3);
				// goods2.code = rs.getString(4);
				// goods2.gz = rs.getString(5);
				// goods2.catid = rs.getString(6);
				// goods2.ppcode = rs.getString(7);
				// goods2.uid = rs.getString(8);
				goods2.poplsj = Convert.toDouble(rs.getString(9));
				goods2.pophyj = Convert.toDouble(rs.getString(10));
				goods2.poplsjzkl = Convert.toDouble(rs.getString(11));
				goods2.pophyjzkl = Convert.toDouble(rs.getString(12));
				goods2.poplsjzkfd = Convert.toDouble(rs.getString(13));
				goods2.ksrq = rs.getString(14);
				goods2.jsrq = rs.getString(15);
				goods2.kssj = rs.getString(16);
				goods2.jssj = rs.getString(17);
				goods2.yhspace = Convert.toInt(rs.getString(18));
				goods2.poppfj = Convert.toDouble(rs.getString(19));
				goods2.poppfjzkl = Convert.toDouble(rs.getString(20));
				goods2.pophyjzkfd = Convert.toDouble(rs.getString(21));
				goods2.poppfjzkfd = Convert.toDouble(rs.getString(22));

			}

			if (goods1 == null)
				return goods2;

			if (goods2 != null)
				if (goods1.type == goods2.type)
					return goods1;

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
