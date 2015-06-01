package com.efuture.javaPos.Global;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.TimeDate;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.CustomerTypeDef;
import com.efuture.javaPos.Struct.CustomerVipZklDef;
import com.efuture.javaPos.Struct.GoodsAmountDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsFrameDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.GoodsUnitsDef;
import com.efuture.javaPos.Struct.ManaFrameDef;
import com.efuture.javaPos.Struct.OperRoleDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayRuleDef;
import com.efuture.javaPos.Struct.SyjGrangeDef;
import com.efuture.javaPos.Struct.ContentItemForTouchScrn;

//用来访问本地数据库用
public class AccessBaseDB
{
	public static AccessBaseDB currentAccessBaseDB = null;
	
    public static AccessBaseDB getDefault()
    {
        if (AccessBaseDB.currentAccessBaseDB == null)
        {
        	AccessBaseDB.currentAccessBaseDB = CustomLocalize.getDefault().createAccessBaseDB();
        }

        return AccessBaseDB.currentAccessBaseDB;
    }

    public String nvl(String a,String b)
    {
    	return "(case when LTRIM("+a+") is null then "+b+" else LTRIM("+a+") end)";
    }
    public void createPreparedSql()
    {
	    try
	    {
	    	//促销类型
	    	//1-商品
	    	//2-柜组
	    	//3-品类
	    	//4-柜组、品牌
	    	//5-品类、品牌
	    	//6-品牌
	    	GlobalInfo.psGoodsCode = GlobalInfo.baseDB.getConnection().prepareStatement("select * from GOODS where CODE = ?",
	    			ResultSet.TYPE_FORWARD_ONLY,
	    			ResultSet.CONCUR_READ_ONLY);
	    	
	    	GlobalInfo.psGoodsBarCode = GlobalInfo.baseDB.getConnection().prepareStatement("select * from GOODS where BARCODE = ?", 
	    			ResultSet.TYPE_FORWARD_ONLY,
	    			ResultSet.CONCUR_READ_ONLY);
	    	
            StringBuffer command = new StringBuffer();
            command.append("SELECT (case when MAX(SEQNO) is null then 0 else MAX(SEQNO) end) from GOODSPOP where ");
            command.append("KSRQ <= ? AND jsrq >= ? AND kssj <= ? AND jssj >= ? AND (LTRIM(rule) = '1' OR LTRIM(rule) is null OR LTRIM(rule) = '') AND "); 
            command.append("(code = ? AND (gz = ? OR gz = '0') AND ");
            command.append("(case when LTRIM(uid) is null then '00' else LTRIM(uid) end) = (case when LTRIM(?) is null then '00' else LTRIM(?) end)");
            command.append(" AND ");
            command.append("sl <> 0 AND (type = '1' OR (type = '7' AND ? >= yhspace)) OR "); 
            command.append("(code = ? AND type = '2') OR ");
            command.append("(code = ? AND ppcode = ? AND type = '4') OR ");
            command.append("(code = ? AND type ='3') OR ");
            command.append("(code = ? AND ppcode = ? AND type = '5') OR "); 
            command.append("(code = ? AND type = '6' )) AND ");
            command.append("(str1 is null or str1 = '0' or str1 = '' or str1 like '%");
            command.append(new ManipulateDateTime().getDateWeek()+"%')");
	    	GlobalInfo.psGoodsPop = GlobalInfo.baseDB.getConnection().prepareStatement(command.toString(), 
	    			ResultSet.TYPE_FORWARD_ONLY,
	    			ResultSet.CONCUR_READ_ONLY);
	    			
	    	GlobalInfo.psManaframe = GlobalInfo.localDB.getConnection().prepareStatement("select count(*) from MANAFRAME where gz = ? and iscs = ?", 
	    			ResultSet.TYPE_FORWARD_ONLY,
	    			ResultSet.CONCUR_READ_ONLY);
	    }
	    catch(Exception ex)
	    {
	    	ex.printStackTrace();
	    	
	    	GlobalInfo.psGoodsCode = null;
	    	GlobalInfo.psGoodsBarCode = null;
	    	GlobalInfo.psGoodsPop = null;
	    	GlobalInfo.psManaframe = null;
	    }
    }
    
    public int getGoodsDef(GoodsDef goodsDef, int searchFlag, String code,
                           String gz, String proTime, String yhsj, String djlb)
    {
        int flag;
        int result = -1;
        
        // 先按条码查找
        if (GlobalInfo.sysPara.forcebybarcode == 'Y' && code.length()>0)
        {
        	flag = 1;
        	result = getGoodsDef(goodsDef, searchFlag, code, gz, proTime, yhsj,flag, djlb);
        }
        else if (code.length() >= 8)
        {
        	flag = 1;
        	result = getGoodsDef(goodsDef, searchFlag, code, gz, proTime, yhsj,flag, djlb);
        }
        else
        {
            // 允许代码销售,先按代码查
            flag = 0;
            if (GlobalInfo.sysPara.codesale == 'Y')
            {
            	result = getGoodsDef(goodsDef, searchFlag, code, gz, proTime, yhsj,flag, djlb);
            }
            else
            {
            	result = -1;
            }
        }
        
        // 没找到商品，条码代码交换,继续查询
        if (result < 0)
        {
	        if (flag == 1) flag = 0;
	        else flag = 1;
	        result = getGoodsDef(goodsDef, searchFlag, code, gz, proTime, yhsj,flag, djlb);
        }
        
        return result;
    }
    
    public int getGoodsDef(GoodsDef goodsDef, int searchFlag, String barcode,String gz, String proTime, String yhsj,int flag, String djlb)    
    {
        int result = -1;

        int moregz;
        int moreret;
        ResultSet rs = null;

        GoodsDef finalGoods = null;
        //GoodsDef tempGoods = new GoodsDef();
        
        // 开始搜索本地商品信息
        try
        {
        	//
        	PublicMethod.timeStart(Language.apply("正在查询本地商品库,请等待......"));
        	
/*			不要删除，备用方式
            String command;
            if (flag == 0)
            	command = "select * from GOODS where CODE = '" + barcode + "'";
            else
            	command = "select * from GOODS where BARCODE = '" + barcode + "'";
        	
            rs = GlobalInfo.baseDB.selectData(command);
*/

        	if (flag == 0)
        	{
        		if (GlobalInfo.psGoodsCode == null) return -1;
        		GlobalInfo.baseDB.setSql(GlobalInfo.psGoodsCode);
        	}
        	else
        	{
        		if (GlobalInfo.psGoodsBarCode == null) return -1;
        		GlobalInfo.baseDB.setSql(GlobalInfo.psGoodsBarCode);
        	}
    		GlobalInfo.baseDB.paramSetString(1, barcode);
        	rs = GlobalInfo.baseDB.selectData();
        	
        	////////
            if (rs == null)
            {
                return -1;
            }

            moregz = moreret = 0;
            Vector grouprange = null;
            while (rs.next())
            {
                GoodsDef tempGoods = new GoodsDef();
                
                if (!GlobalInfo.baseDB.getResultSetToObject(tempGoods,GoodsDef.refLocal))
                {
                    continue;
                }
                
                // 找代码,如果是多原印码、多包装、子商品继续找下一个
                if ((flag == 0) && ((tempGoods.type == 'A') || (tempGoods.type == 'B') || (tempGoods.type == 'C')))
                {
                    continue;
                }

                // 启用商品例外价模式,在例外价表GoodsException查找经营配置和柜组
                if (GlobalInfo.sysPara.useGoodsFrameMode == 'Y')
                {
                	// 先赋值当前商品资料
                    finalGoods = tempGoods;
                    
                    // 再操作商品经营配置和例外价，没有定义经营配置，默认经营配置GZ为当前门店号
                	GlobalInfo.baseDB.resultSetClose();
                	GoodsFrameDef geDef = new GoodsFrameDef();
                	result = getGoodsFrame(geDef,searchFlag,tempGoods.barcode,gz);
                	if (result == 0)
                	{
                		// 例外经营配置和例外价
                		finalGoods.gz = geDef.gz;
                		finalGoods.lsj = geDef.lsj;
                		finalGoods.hyj = geDef.hyj;
                		finalGoods.hyjzkfd = geDef.hyjzkfd;
                		finalGoods.pfj = geDef.pfj;
                		finalGoods.pfjzkfd = geDef.pfjzkfd;
                		finalGoods.xxtax = geDef.xxtax;
                		finalGoods.xjjg = geDef.xjjg;
                		finalGoods.jgjd = geDef.jgjd;
                		finalGoods.minplsl = geDef.minplsl;	
                		finalGoods.issqkzk = geDef.issqkzk;
                		finalGoods.isvipzk = geDef.isvipzk;
                		finalGoods.iszs = geDef.iszs;
                		finalGoods.maxzkl = geDef.maxzkl;
                		finalGoods.ischgjg = geDef.ischgjg;
                		finalGoods.maxzke = geDef.maxzke;
                		finalGoods.memo = geDef.memo;				
                		finalGoods.str1 = geDef.str1;
                		finalGoods.str2 = geDef.str2;
                		finalGoods.str3 = geDef.str3;
                		finalGoods.num1 = geDef.num1;				
                		finalGoods.num2 = geDef.num2;				
                		finalGoods.num3 = geDef.num3;
                	}
                	if (result == -1 && GlobalInfo.sysPara.allowGoodsFrameSale == 'Y') 
                	{
            			finalGoods.gz = GlobalInfo.sysPara.mktcode;
            			result = 0;
                	}
                	break;
                }
                
                // 检查收银机是否串柜销售
                if ((searchFlag < 4) && (GlobalInfo.syjDef.ists != 'Y') && !AccessLocalDB.getDefault().checkSyjGrange(tempGoods.gz))
                {
                    if (result == -1)
                    {
                        result = 2; //如果一个也没找到才设置为收银机串柜
                    }
                }
                else
                {
                    // 柜台销售，通过营业员的柜组控制;或者找赠品
                    if ((searchFlag == 2) || (searchFlag == 4))
                    {
                        //商品柜组和营业员的柜组相同，查找成功
                        if (tempGoods.gz.equals(gz) || gz.equals("%"))
                        {
                            finalGoods = tempGoods;
                            result     = 0;

                            break;
                        }
                        else
                        {
                            result = 3;
                        }
                    }
                    else //超市销售，查找编码相同的记录;柜台销售，不通过营业员的柜组控制
                    {	
                        int valid = 0;

                        if (searchFlag == 1)
                        {
                        	if (AccessLocalDB.getDefault().checkManaframe(tempGoods.gz,'Y'))
                        	{
                        		valid = 1;
                        	}
                        }
                        else if (searchFlag == 3)
                        {
                        	if (AccessLocalDB.getDefault().checkManaframe(tempGoods.gz,'N'))
                        	{
                        		valid = 1;
                        	}                
                        }

                        if ((moreret < 1) || (valid > 0))
                        {
                            moregz++; //已经找到的柜组个数

                            if (valid > 0)
                            {
                                if (moreret < 1)
                                {
                                    moregz = 1;
                                }

                                moreret++; //有效柜组的个数
                            }

                            finalGoods = tempGoods;
                            result     = 0;
                            
                            // 如果本地查询商品不检查多柜，则不用检查多个柜组
                            if (GlobalInfo.sysPara.localNotCheckMultiGz.equals("Y"))
                            {
                            	if (grouprange == null)
                            	{
                            		grouprange = new Vector();
                            		AccessLocalDB.getDefault().readSyjGrange(grouprange);
                            		String strgz = "";
                            		for (int i = 0;i < grouprange.size();i++)
                            		{
                            			strgz = strgz + ((SyjGrangeDef)grouprange.get(i)).gz.toString() + ";";
                            		}
                            	}
                            	
                            	SyjGrangeDef sgd = null;
                            	for (int i = 0;i < grouprange.size();i++)
                            	{
                            		sgd = (SyjGrangeDef)grouprange.get(i);
                            		
                            		if (sgd.gz.equals(finalGoods.gz))
                            		{
                            			break;
                            		}
                            	}
                            	
                            	if (sgd != null && sgd.gz.equals(finalGoods.gz))
                            	{
                            		break;
                            	}
                            }
                            else
                            {
	                            if ((moregz > 1) || (moreret > 1)) //存在多个柜组，要求输入商品柜组
	                            {
	                            	result = 4;
	                                break;
	                            }
                            }
                        }
                    }
                }
                
                if (tempGoods.attr01 != null && tempGoods.attr01.trim().length() > 0)
                {
                	String[] types = tempGoods.attr01.trim().split(",");
                	for (int i = 0; i < types.length; i++)
                	{
                		if (types[i].equals(djlb))
                		{
                			result = 10;
                			break;
                		}
                	}
                }
            }
            
            if (result == 0)
            { 
            	//
            	GlobalInfo.baseDB.resultSetClose();
        	
            	// 查找优惠
                GoodsPopDef popDef = getPromotion(finalGoods.code,
                                                  finalGoods.gz,
                                                  finalGoods.catid,
                                                  finalGoods.ppcode,
                                                  finalGoods.uid, proTime, yhsj);
            	
                // 将优惠单信息赋值到商品
                transferPopInfoToGoodsInfo(finalGoods,popDef);

                //
                if (!PublicMethod.transferInfo(finalGoods, goodsDef, "ref","ref"))
                {
                    result = -1;
                }
                
                // 脱网商品允许销红
                goodsDef.isxh = 'Y';
            }
        }
        catch (Exception er)
        {
            er.printStackTrace();

            result = -1;
        }
        finally
        {
        	//
            GlobalInfo.baseDB.resultSetClose();

            //
            PublicMethod.timeEnd(Language.apply("查询本地商品库耗时: "));            
        }
                
        return result;
    }

    public int getGoodsFrame(GoodsFrameDef finalge, int searchFlag, String barcode,String gz)    
    {
        int result = -1;
        int moregz = 0;
        int moreret = 0;

        try
        {
	        ResultSet rs = GlobalInfo.baseDB.selectData("select * from GoodsFrame where barcode = '"+barcode+"'");
	    	while(rs != null && rs.next())
	    	{
	    		GoodsFrameDef tempge = new GoodsFrameDef();
	            if (!GlobalInfo.baseDB.getResultSetToObject(tempge,GoodsFrameDef.refLocal))
	            {
	                continue;
	            }
	            
                // 检查是否本门店的经营配置
                if (tempge.mkt != null && !tempge.mkt.trim().equals("") && !tempge.mkt.trim().equals(GlobalInfo.sysPara.mktcode))
                {
                    continue;
                }
                
	            // 检查收银机是否串柜销售
	            if ((searchFlag < 4) && (GlobalInfo.syjDef.ists != 'Y') && !AccessLocalDB.getDefault().checkSyjGrange(tempge.gz))
	            {
	                if (result == -1)
	                {
	                    result = 2; //如果一个也没找到才设置为收银机串柜
	                }
	            }
	            else
	            {
	                // 柜台销售，通过营业员的柜组控制;或者找赠品
	                if ((searchFlag == 2) || (searchFlag == 4))
	                {
	                    //商品柜组和营业员的柜组相同，查找成功
	                    if (tempge.gz.equals(gz) || gz.equals("%"))
	                    {
	                    	PublicMethod.transferInfo(tempge,finalge,"ref","ref");
	                        result  = 0;
	
	                        break;
	                    }
	                    else
	                    {
	                        result = 3;
	                    }
	                }
	                else //超市销售，查找编码相同的记录;柜台销售，不通过营业员的柜组控制
	                {
	                    int valid = 0;
	
	                    if (searchFlag == 1)
	                    {
	                    	if (AccessLocalDB.getDefault().checkManaframe(tempge.gz,'Y'))
	                    	{
	                    		valid = 1;
	                    	}
	                    }
	                    else if (searchFlag == 3)
	                    {
	                    	if (AccessLocalDB.getDefault().checkManaframe(tempge.gz,'N'))
	                    	{
	                    		valid = 1;
	                    	}                
	                    }
	
	                    if ((moreret < 1) || (valid > 0))
	                    {
	                        moregz++; //已经找到的柜组个数
	
	                        if (valid > 0)
	                        {
	                            if (moreret < 1)
	                            {
	                                moregz = 1;
	                            }
	
	                            moreret++; //有效柜组的个数
	                        }
	
	                        if ((moregz > 1) || (moreret > 1)) //存在多个柜组，要求输入商品柜组
	                        {
	                            result = 4;
	
	                            if (moreret > 1)
	                            {
	                                break;
	                            }
	                        }
	                        else //查找成功，继续查找判断是否存在多柜组
	                        {
	                        	PublicMethod.transferInfo(tempge,finalge,"ref","ref");
	                            result  = 0;
	                        }
	                    }
	                }
	            }
	    	}
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	
        	result = -1;
        }
        
        return result;
    }
    
    public void transferPopInfoToGoodsInfo(GoodsDef finalGoods,GoodsPopDef popDef)
    {
        if (popDef != null)
        {
            finalGoods.isxh    = 'Y';
            finalGoods.kcsl    = 0;
            finalGoods.popdjbh = popDef.djbh;
            finalGoods.poptype = popDef.type;
            finalGoods.poplsj  = popDef.poplsj;
            finalGoods.pophyj  = popDef.pophyj;
            finalGoods.poppfj  = popDef.poppfj;

            finalGoods.poplsjzkl  = popDef.poplsjzkl;
            finalGoods.pophyjzkl  = popDef.pophyjzkl;
            finalGoods.poppfjzkl  = popDef.poppfjzkl;
            finalGoods.poplsjzkfd = popDef.poplsjzkfd;
            finalGoods.pophyjzkfd = popDef.pophyjzkfd;
            finalGoods.poppfjzkfd = popDef.poppfjzkfd;
            
            
        }
        else
        {
            finalGoods.isxh    = 'Y';
            finalGoods.kcsl    = 0;
            finalGoods.poptype = '0';
            finalGoods.popdjbh = "";
        }
    }

    public GoodsPopDef getPromotion(String code, String gz, String catid,
                                    String ppcode, String uid, String scsj,
                                    String yhsj)
    {
        if (yhsj.length() <= 0)
        {
            return null;
        }

        ResultSet rs = null;

        TimeDate timeObj = new TimeDate();
        timeObj.fullTime = yhsj;
        timeObj.split();

        String tsj = timeObj.hh + ":" + timeObj.min; //24:12
        String tdate = timeObj.cc + timeObj.yy + "/" + timeObj.mm + "/" +
                       timeObj.dd;
        int dis = 0;

        //if (scsj.length() > 0)
        if (scsj.length() >= 8)
        {
            long timeDis = new ManipulateDateTime().getDisDateTimeByMS(scsj,
                                                                       yhsj);
            dis = Integer.parseInt(String.valueOf(timeDis));
        }

        try
        {
/*        	
            String command = "SELECT (case when MAX(SEQNO) is null then 0 else MAX(SEQNO) end) from GOODSPOP " +
            "where    KSRQ <= '" + tdate + "'" + " AND jsrq >= '" +
            tdate + "'" + " AND kssj <= '" + tsj + "'" +
            " AND jssj >= '" + tsj + "'" + " AND" + " (" + "(" +
            "code = '" + code + "' AND (gz = '" + gz +
            "' OR gz = '0')" +
            "AND (case when LTRIM(uid) is null then '00' else LTRIM(uid) end) = (case when LTRIM('" +
            uid + "') is null then '00' else LTRIM('" + uid +
            "') end)" + "AND sl <> 0" +
            "AND (type = '1' OR (type = '7' AND " + dis +
            " >= yhspace))" + ")" + "OR (code = '" + gz +
            "' AND type = '2')" + "OR (code = '" + gz +
            "' AND ppcode = '" + ppcode + "' AND type = '4')" +
            "OR (code = '" + catid + "' AND type ='3')" +
            "OR (code = '" + catid + "' AND ppcode =  '" + ppcode +
            "' AND type = '5')" + "OR (code =  '" + ppcode +
            "' AND type = '6')" + " )";

            rs = GlobalInfo.baseDB.selectData(command);
*/
        	if (GlobalInfo.psGoodsPop == null) return null;
        	
        	GlobalInfo.baseDB.setSql(GlobalInfo.psGoodsPop);
    		GlobalInfo.baseDB.paramSetString(1, tdate);
    		GlobalInfo.baseDB.paramSetString(2, tdate);
    		GlobalInfo.baseDB.paramSetString(3, tsj);
    		GlobalInfo.baseDB.paramSetString(4, tsj);
    		GlobalInfo.baseDB.paramSetString(5, code);
    		GlobalInfo.baseDB.paramSetString(6, gz);
    		GlobalInfo.baseDB.paramSetString(7, uid);
    		GlobalInfo.baseDB.paramSetString(8, uid);
    		GlobalInfo.baseDB.paramSetInt(9, dis);
    		GlobalInfo.baseDB.paramSetString(10, gz);
    		GlobalInfo.baseDB.paramSetString(11, gz);
    		GlobalInfo.baseDB.paramSetString(12, ppcode);
    		GlobalInfo.baseDB.paramSetString(13, catid);
    		GlobalInfo.baseDB.paramSetString(14, catid);
    		GlobalInfo.baseDB.paramSetString(15, ppcode);
    		GlobalInfo.baseDB.paramSetString(16, ppcode);
        	rs = GlobalInfo.baseDB.selectData();
        	
            if (rs == null)
            {
                return null;
            }

            if (rs.next())
            {
                long seqNo = rs.getLong(1);

                if (seqNo == 0)
                {
                    return null;
                }
                else
                {
                    GlobalInfo.baseDB.resultSetClose();
                    
                    rs = GlobalInfo.baseDB.selectData("SELECT * FROM GOODSPOP where SEQNO = " + seqNo);

                    if (rs.next())
                    {
                        GoodsPopDef popDef = new GoodsPopDef();

                        if (!GlobalInfo.baseDB.getResultSetToObject(popDef))
                        {
                            return null;
                        }

                        return popDef;
                    }
                }
            }

            return null;
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
         
	public boolean doWhtBlackList(String cardno)
	{
		try
		{
			ResultSet rs = null;
			PublicMethod.timeStart(Language.apply("正在核对黑名单,请等待......"));

			rs = GlobalInfo.baseDB.selectData("select * from MEMOINFO where code = '" + cardno + "'" + "and type='WHT'");

			if (rs == null )
			{
				return false;
			}
			
			if (rs.next())
			{
				if (!rs.getString("CODE").trim().equals(cardno))
					return false;
			}
			else
			{
				return false;
			}
			
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
    public boolean getOperUser(String id, OperUserDef user)
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

    public void setOperUserPass(String password)
    {
        try
        {
            GlobalInfo.baseDB.setSql("update OPERUSER set PASSWD=? where GH=?");

            GlobalInfo.baseDB.paramSetString(1,
                                             ManipulatePrecision.getEncrypt(password));
            GlobalInfo.baseDB.paramSetString(2, GlobalInfo.posLogin.gh);

            GlobalInfo.baseDB.executeSql();
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
        	PublicMethod.timeStart(Language.apply("正在查询本地顾客库,请等待......"));
        	
        	if (GlobalInfo.sysPara.custDisconnetNoPeriod == 'Y' && !GlobalInfo.isOnline)
        	{
        		cust.code 	= track;
        		cust.type   = "XX";
        		cust.status = "Y";
        		cust.track  = track;
        		cust.name	= Language.apply("脱机会员");
        		cust.ishy   = 'N';
        		cust.iszk   = 'N';
        		cust.isjf   = 'N';
        		cust.func	= "N";
        		cust.zkl    = 1;
        		
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
	    	            if (!rs.next())
	    	            {
	    	            	return false;
	    	            }
	                }
	            }
            }
            
            if (!GlobalInfo.baseDB.getResultSetToObject(cust))
            {
                return false;
            }

            //
            GlobalInfo.baseDB.resultSetClose();

            // 查找类型
            rs = GlobalInfo.localDB.selectData("select * from CUSTOMERTYPE where code = '" +
                                               cust.type + "'");

            if (rs == null)
            {
                return false;
            }

            if (rs.next())
            {
                CustomerTypeDef type = new CustomerTypeDef();

                if (!GlobalInfo.localDB.getResultSetToObject(type))
                {
                    return false;
                }

                cust.ishy   = type.ishy;
                cust.iszk   = type.iszk;
                cust.isjf   = type.isjf;
                cust.func   = type.func;
                cust.zkl    = type.zkl;
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
            if (cust.zkl <= 0) cust.zkl = 1;

            GlobalInfo.localDB.resultSetClose();

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
            GlobalInfo.localDB.resultSetClose();
            
            //
            PublicMethod.timeEnd(Language.apply("查询本地顾客库耗时: ")); 
        }
    }

    public boolean getGoodsMutiUnit(Vector unit, String code)
    {
        ResultSet rs = null;

        try
        {
            rs = GlobalInfo.baseDB.selectData("select * from GOODSUNITS where code = '" +
                                              code + "'");

            if (rs == null)
            {
                return false;
            }

            while (rs.next())
            {
                GoodsUnitsDef goodsUnit = new GoodsUnitsDef();

                if (!GlobalInfo.baseDB.getResultSetToObject(goodsUnit))
                {
                    return false;
                }

                unit.add(goodsUnit);
            }

            return true;
        }
        catch (Exception er)
        {
            er.printStackTrace();

            return false;
        }
        finally
        {
            GlobalInfo.baseDB.resultSetClose();
        }
    }

    public boolean getSubGoodsDef(Vector subGoods, String fxm, String gz,
                                  char type)
    {
        ResultSet rs = null;

        try
        {
            rs = GlobalInfo.baseDB.selectData("select barcode,name from goods where FXM= '" + fxm + "' and gz= '" + gz + "' and TYPE= " + type + "");

            if (rs == null)
            {
                return false;
            }

            while (rs.next())
            {
                String[] row = new String[2];
                row[0] = rs.getString("barcode");
                row[1] = rs.getString("name");
                subGoods.add(row);
            }

            return true;
        }
        catch (Exception er)
        {
            er.printStackTrace();

            return false;
        }
        finally
        {
            GlobalInfo.baseDB.resultSetClose();
        }
    }
    
    public long getGoodsOrCategoryMaxCount(boolean searchflag,long cateid,int level)
	{
		ResultSet rs = null;
		long count = 0;
		String sql =null;
		
		//false 查找商品类别
		if (searchflag)
			sql =  "select count(*) from goods where catid=" + cateid ;
		else
			sql = "select count(*) from goodscate where PARENTCATEID=" + cateid + " and catelevelid=" + level;

		try
		{
			rs = GlobalInfo.baseDB.selectData(sql);

			if (rs != null && rs.next())
				count = rs.getLong(1);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return count;
	}
    
    public boolean getGoodsOrCategoryPages(Vector listgoods,boolean searhflag,long startPos, long endPos,long cateid,int level)
	{
		ResultSet rs = null;
		String sql =null;
		
		if (searhflag)
			sql = "select * from goods where catid=" + cateid + (ConfigClass.LocalDBType.equalsIgnoreCase("SQLite") == true ? (" limit " + startPos + "," + endPos) : "") ;		
		else
			sql= "select * from goodscate  where parentcateid=" + cateid  +" and catelevelid="+level+ (ConfigClass.LocalDBType.equalsIgnoreCase("SQLite") == true ? (" limit " + startPos + "," + endPos) : "");
		
		try
		{
			rs = GlobalInfo.baseDB.selectData(sql);

			if (rs == null)
				return false;

			boolean ret = false;
			int n = 0;
			while (rs.next())
			{
				ContentItemForTouchScrn item = new ContentItemForTouchScrn();

				if (!GlobalInfo.baseDB.getResultSetToObject(item,(searhflag ? ContentItemForTouchScrn.refGoods :ContentItemForTouchScrn.refCate)))
					return false; 
			
				listgoods.setElementAt(item, n);
				// listgoods.add(item);
				ret = true;

				n++;
				if (n >= endPos - startPos)
					break;
			}
			if (n==0)
				ret =false;
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
    
    
    
    /*
     * 新的商品模糊查询函数
     * 修改说明:修改查询SQL
     * wangyong add by 2010.5.28
     */
    public boolean getGoodsList(ArrayList listgoods,char codetype,String Code,String sql)
    {
    	ResultSet rs = null;
    	
    	try
    	{
    		//old code bak
        	//rs = GlobalInfo.baseDB.selectData("select * from goods where ('"+ Code +"'= barcode and '"+ codetype + "'= '"+0+"') or ('"+ Code +"'= code and '"+codetype+"' = '"+1+"') or ('"+Code+"'=gz and '"+codetype+"'='"+2+"')");
        	    		
        	//new code
        	rs = GlobalInfo.baseDB.selectData("select * from goods " + sql + (ConfigClass.LocalDBType.equalsIgnoreCase("SQLite") == true ? " limit 100" : ""));
    		    		   		
    		if (rs == null) return false;
    		
    		boolean ret = false;
    		int n = 0;
    		while (rs.next())
    		{
    			GoodsDef goods = new GoodsDef();
    			
    			if (!GlobalInfo.baseDB.getResultSetToObject(goods))
                {
                    return false;
                }
    			
    			listgoods.add(goods);
    			ret = true;
    			
    			// 商品查询列表只显示100个集合，避免数据过大
    			n++;
    			if (n >= 100) break;
    		}
    		
    		return ret;
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
    
    public boolean findAmountDef(GoodsAmountDef pl,String code,String gz,String uid, double sl)
    {
		 String date =ManipulateDateTime.getCurrentDate(); 
    	 String command = "SELECT (case when MAX(PLSL) is null then 0 else MAX(PLSL) end) from GOODSAMOUNT where gz = '"+gz+"' AND code = '"+code+"' AND "
    	 		+"UID = '"+uid +"' AND PLSL <="+ sl +" AND KSRQ <= '"+date +"' AND JSRQ >= '"+date+"'";
    	 
         Object obj = GlobalInfo.baseDB.selectOneData(command);
         double plsl = 0;
         if (obj == null)
         {
             return false;
         }
         else
         {
              plsl = Double.parseDouble(String.valueOf(obj));
              if (plsl == 0) return false;
         }
         
         command = "SELECT  code,gz,uid,pllsj,plhyj,plsl,ksrq,jsrq from GOODSAMOUNT where gz = '"+gz+"' AND code = '"+code+"' AND "
	 		+"UID = '"+uid +"' AND PLSL ="+ plsl +" AND KSRQ <= '"+date +"' AND JSRQ >= '"+date+"'";
         
         ResultSet rs = null;
         
         try{
        	 rs = GlobalInfo.baseDB.selectData(command);
        	 
        	 if (rs == null ) return false;
        	 
        	 if (rs.next())
        	 {
        		 if (GlobalInfo.baseDB.getResultSetToObject(pl))
        		 {
        			 return true;
        		 }
        	 }
        	 
        	 return false;
        		
         }catch(Exception er)
         {
        	 er.printStackTrace();
        	 return false;
         }
         finally
         {
        	GlobalInfo.baseDB.resultSetClose(); 
         }
    }
    
    public void getBatchList(ArrayList batchList,String code,String gz,String uid)
    {
    	ResultSet rs = null;
    	
    	try
    	{
    		rs = GlobalInfo.baseDB.selectData("select code,gz,uid,pllsj,plhyj,plsl,ksrq,jsrq from goodsamount where '"+ code +"' = code and '"+ gz + "' = gz and '"+ uid+"' = uid");
    		
    		if (rs == null) return ;
    		
    		while (rs.next())
    		{
    			GoodsAmountDef gad = new GoodsAmountDef();
    			
    			if (!GlobalInfo.baseDB.getResultSetToObject(gad))
                {
                    return ;
                }
    			
    			batchList.add(gad);
    		}
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	finally
    	{
    		GlobalInfo.baseDB.resultSetClose();
    	}
    }
    
    public void getYhList(ArrayList yhList,String code,String gz,String catid,String ppcode,String specinfo)
    {
    	ResultSet rs = null;
    	
    	try
    	{
    		rs = GlobalInfo.baseDB.selectData("select * from goodspop where (code ='"+ code +"' and (gz = '" + gz +"' or gz = '"+ 0 + "'" +
    				") and case when ltrim(uid) is null then '" + 00 +"' else ltrim(uid) end = case when ltrim('"+ specinfo +"') is null then '" + 00 + "' else ltrim('"+ specinfo +"') end and sl <> 0 and (type = '"+ 1 +"' or type = '"+7+"')) " +
    				"or (code = '" + gz + "' and type = '"+ 2 +"') or (code = '" + gz + "' and code = '"+ppcode+"' and type = '" + 4 +"') " +
    				"or (code = '"+catid+"' and type = '" + 3 + "') or (code = '"+ catid +"' and code = '" + ppcode +"' and type = '"+ 5+ "') or (code = '"+ppcode+"' and type = '" + 6 +"') order by seqno");
    		
    		if (rs == null) return ;
    		
    		while (rs.next())
    		{
    			GoodsPopDef gpd = new GoodsPopDef();
    			
    			if (!GlobalInfo.baseDB.getResultSetToObject(gpd))
                {
                    return ;
                }
    			
    			yhList.add(gpd);
    		}
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	finally
    	{
    		GlobalInfo.baseDB.resultSetClose();
    	}
    }
    
	public boolean findVIPZKL(CustomerVipZklDef zklDef, String custcode, String custtype, GoodsDef gd)
	{	
    	ResultSet rs = null;
    	
    	try
    	{
        	PublicMethod.timeStart(Language.apply("正在查询本地VIP折扣,请等待......"));
    		
        	// 解析日期、时间、星期
    		ManipulateDateTime mdt = new ManipulateDateTime();
    		String vtime = mdt.getTime().substring(0,5);
    		
    		// 商品条件
    		String code = gd.code;
    		String gz = gd.gz;
    		String uid = gd.uid;
    		String ppcode = gd.ppcode;
    		String catid = gd.catid;
    		String barcode = gd.barcode;
    		
    		// 取得会员类对应商品的折扣率定义
    		StringBuffer sqlstr = new StringBuffer();
    		sqlstr.append("SELECT SEQNO FROM CUSTOMERZKL ");
    		sqlstr.append("WHERE ksrq <= '" + mdt.getDateBySlash() + "' AND jsrq >= '" + mdt.getDateBySlash() + "' AND ");
    		sqlstr.append("kssj <= '" + vtime + "' AND jssj >= '" + vtime + "' AND ");
    		sqlstr.append("(weeklist LIKE '" + mdt.getDateWeek() + "%' OR weeklist = '%' OR weeklist is null OR weeklist = '') AND (");
    		sqlstr.append("(codemode = '0') OR ");
    		sqlstr.append("(codemode = '1' AND (codeid = ? AND (codegz = ? OR codegz = '%' OR codegz is null OR codegz = '') AND (codeuid = ? OR codeuid = '%' OR codeuid is null OR codeuid = ''))) OR "); 
    		sqlstr.append("(codemode = '2' AND (codeid = ? OR codeid = '%')) OR ");
    		sqlstr.append("(codemode = '3' AND (codeid = ? OR codeid = '%')) OR ");
    		sqlstr.append("(codemode = '4' AND (codeid = ? OR codeid = '%')) OR ");
    		sqlstr.append("(codemode = '5' AND (codeid = ? AND codegz = ? )) OR ");
    		sqlstr.append("(codemode = '6' AND (codeid = ? AND codegz = ? )) OR ");
    		sqlstr.append("(codemode = '7' AND (codeid = ? AND codegz = ? )) OR ");
    		sqlstr.append("(codemode = '8' AND (codeid = ? AND codegz = ? AND codeuid = ?)) OR ");
    		sqlstr.append("(codemode = '9' AND (codeid = ? AND (codegz = ? OR codegz = '%' OR codegz is null OR codegz = '')))");
    		sqlstr.append(") AND ");
    		sqlstr.append("(custtype = '" + custtype + "' OR custtype = 'ALL' OR custtype = 'HALL') ORDER BY SEQNO DESC");
    		GlobalInfo.baseDB.setSql(sqlstr.toString());

    		// 先查找商品参与范围的所有规则
    		GlobalInfo.baseDB.paramSetString(1, code);
    		GlobalInfo.baseDB.paramSetString(2, gz);
    		GlobalInfo.baseDB.paramSetString(3, uid);
    		
    		GlobalInfo.baseDB.paramSetString(4, gz);
    		GlobalInfo.baseDB.paramSetString(5, ppcode);
    		GlobalInfo.baseDB.paramSetString(6, catid);
    		
    		GlobalInfo.baseDB.paramSetString(7, gz);
    		GlobalInfo.baseDB.paramSetString(8, ppcode);
    		
    		GlobalInfo.baseDB.paramSetString(9, gz);
    		GlobalInfo.baseDB.paramSetString(10, catid);
    		
    		GlobalInfo.baseDB.paramSetString(11, ppcode);
    		GlobalInfo.baseDB.paramSetString(12, catid);
    		
    		GlobalInfo.baseDB.paramSetString(13, gz);    		
    		GlobalInfo.baseDB.paramSetString(14, ppcode);
    		GlobalInfo.baseDB.paramSetString(15, catid);
    		
    		GlobalInfo.baseDB.paramSetString(16, barcode);
    		GlobalInfo.baseDB.paramSetString(17, gz);
    		
    		// 查询序号最大的
        	boolean ret = false;
    		rs = GlobalInfo.baseDB.selectData();
    		while (rs != null && rs.next())
    		{
    			if (!GlobalInfo.baseDB.getResultSetToObject(zklDef))
                {
                    return false;
                }
    			
    			// 规则号为空或者没有门店范围表，则认为所有门店适用,否则检查规则的门店范围是否符合
    			if (zklDef.ruleid == null || zklDef.ruleid.equals("") || !GlobalInfo.baseDB.isTableExist("CMRULEMFLIST"))
    			{
	    			ret = true;
	    			break;
    			}
    			else
    			{
    				// 符合门店范围
    				if (mathMarketRuleRange("CUSTZKL",zklDef.ruleid))
    				{
    	    			ret = true;
    	    			break;
    				}
    			}
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
    		
    		PublicMethod.timeEnd(Language.apply("查询本地VIP折扣耗时: "));
    	}
    }
	
	public boolean mathMarketRuleRange(String rulemode,String ruleid)
	{
		ManaFrameDef curmf = null;
		
		// 用sqldb的st对象,ps对象返回的ResultSet对象被外部调用循环占用
		try
		{
			ResultSet rs = GlobalInfo.localDB.selectData("select * from MANAFRAME where gz = '" + GlobalInfo.getPhysicsMarket()+"'",true);
			if (rs != null)
			{
				if (rs.next())
				{
					curmf = new ManaFrameDef();
					if (!GlobalInfo.localDB.getResultSetToObject(curmf, rs))
					{
						curmf = null;
					}
				}
				GlobalInfo.localDB.resultSetClose(rs);
			}
			if (curmf == null && GlobalInfo.baseDB.isTableExist("MANAFRAME"))
			{
				rs = GlobalInfo.baseDB.selectData("select * from MANAFRAME where gz = '" + GlobalInfo.getPhysicsMarket()+"'",true);
				if (rs != null)
				{
					if (rs.next())
					{
						curmf = new ManaFrameDef();
						if (!GlobalInfo.baseDB.getResultSetToObject(curmf, rs))
						{
							curmf = null;
						}
					}
					GlobalInfo.baseDB.resultSetClose(rs);
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select count(*) from CMRULEMFLIST where MFRULEID = '"+ruleid+"' AND MFRULETYPE = '"+rulemode+"' AND (");
		sqlstr.append("(mfmode = '0' AND mfcode = '"+GlobalInfo.getPhysicsMarket()+"')");
		if (curmf != null)
		{
			sqlstr.append(" OR (mfmode = '%' AND ");
			sqlstr.append("(mfattr01 = '%' or mfattr01 = '"+curmf.attr01+") AND ");
			sqlstr.append("(mfattr02 = '%' or mfattr02 = '"+curmf.attr02+") AND ");
			sqlstr.append("(mfattr03 = '%' or mfattr03 = '"+curmf.attr03+") AND ");
			sqlstr.append("(mfattr04 = '%' or mfattr04 = '"+curmf.attr04+") AND ");
			sqlstr.append("(mfattr05 = '%' or mfattr05 = '"+curmf.attr05+") AND ");
			sqlstr.append("(mfattr06 = '%' or mfattr06 = '"+curmf.attr06+") AND ");
			sqlstr.append("(mfattr07 = '%' or mfattr07 = '"+curmf.attr07+") AND ");
			sqlstr.append("(mfattr08 = '%' or mfattr08 = '"+curmf.attr08+"))");
		}
		sqlstr.append(")");
		
		// 门店参与且没有设置不参与范围
		Object obj = GlobalInfo.baseDB.selectOneData(sqlstr + " AND JOINMODE = 'Y'");
		if (obj != null && Convert.toInt(obj) >= 1)
		{
			obj = GlobalInfo.baseDB.selectOneData(sqlstr + " AND JOINMODE = 'N'");
			if (obj != null && Convert.toInt(obj) >= 1) return false;
			else return true;
		}
		else return false;
	}
	
	public Vector getGoodsPayRule(GoodsDef gd)
	{	
    	ResultSet rs = null;
    	
    	try
    	{
        	PublicMethod.timeStart(Language.apply("正在查询本地收款规则,请等待......"));

        	// 解析日期、时间、星期
    		ManipulateDateTime mdt = new ManipulateDateTime();
    		String vtime = mdt.getTime().substring(0,5);
    		
    		// 商品条件
    		String code = gd.code;
    		String gz = gd.gz;
    		String uid = gd.uid;
    		String ppcode = gd.ppcode;
    		String catid = gd.catid;
    		String barcode = gd.barcode;
    		
    		// 取商品可收的付款方式列表
    		StringBuffer sqlstr = new StringBuffer();
    		sqlstr.append("SELECT paycode,payflag,CASE WHEN MAX(seqno) IS NULL THEN 0 ELSE MAX(seqno) END seqno FROM PAYRULE ");
    		sqlstr.append("WHERE ksrq <= '" + mdt.getDateBySlash() + "' AND jsrq >= '" + mdt.getDateBySlash() + "' AND ");
    		sqlstr.append("kssj <= '" + vtime + "' AND jssj >= '" + vtime + "' AND ");
    		sqlstr.append("(weeklist LIKE '" + mdt.getDateWeek() + "%' OR weeklist = '%' OR weeklist is null OR weeklist = '') AND (");
    		sqlstr.append("(codemode = '0') OR ");
    		sqlstr.append("(codemode = '1' AND (codeid = ? AND (codegz = ? OR codegz = '%' OR codegz is null OR codegz = '') AND (codeuid = ? OR codeuid = '%' OR codeuid is null OR codeuid = ''))) OR "); 
    		sqlstr.append("(codemode = '2' AND (codeid = ? OR codeid = '%')) OR ");
    		sqlstr.append("(codemode = '3' AND (codeid = ? OR codeid = '%')) OR ");
    		sqlstr.append("(codemode = '4' AND (codeid = ? OR codeid = '%')) OR ");
    		sqlstr.append("(codemode = '5' AND (codeid = ? AND codegz = ? )) OR ");
    		sqlstr.append("(codemode = '6' AND (codeid = ? AND codegz = ? )) OR ");
    		sqlstr.append("(codemode = '7' AND (codeid = ? AND codegz = ? )) OR ");
    		sqlstr.append("(codemode = '8' AND (codeid = ? AND codegz = ? AND codeuid = ?)) OR ");
    		sqlstr.append("(codemode = '9' AND (codeid = ? AND (codegz = ? OR codegz = '%' OR codegz is null OR codegz = '')))"); 
    		sqlstr.append(") GROUP BY paycode,payflag");
    		GlobalInfo.baseDB.setSql(sqlstr.toString());

    		// 查询结果集
    		Vector payrule = new Vector();
    		
    		// 先查找商品参与范围的所有规则
    		GlobalInfo.baseDB.paramSetString(1, code);
    		GlobalInfo.baseDB.paramSetString(2, gz);
    		GlobalInfo.baseDB.paramSetString(3, uid);
    		
    		GlobalInfo.baseDB.paramSetString(4, gz);
    		GlobalInfo.baseDB.paramSetString(5, ppcode);
    		GlobalInfo.baseDB.paramSetString(6, catid);
    		
    		GlobalInfo.baseDB.paramSetString(7, gz);
    		GlobalInfo.baseDB.paramSetString(8, ppcode);
    		
    		GlobalInfo.baseDB.paramSetString(9, gz);
    		GlobalInfo.baseDB.paramSetString(10, catid);
    		
    		GlobalInfo.baseDB.paramSetString(11, ppcode);
    		GlobalInfo.baseDB.paramSetString(12, catid);
    		
    		GlobalInfo.baseDB.paramSetString(13, gz);    		
    		GlobalInfo.baseDB.paramSetString(14, ppcode);
    		GlobalInfo.baseDB.paramSetString(15, catid);
    		
    		GlobalInfo.baseDB.paramSetString(16, barcode);
    		GlobalInfo.baseDB.paramSetString(17, gz);
        	rs = GlobalInfo.baseDB.selectData();
        	while(rs.next())
        	{
        		PayRuleDef pr = new PayRuleDef();
	    		if (!GlobalInfo.baseDB.getResultSetToObject(pr))
	            {
	                return null;
	            }
    			payrule.add(pr);
        	}
        	GlobalInfo.baseDB.resultSetClose();
        	
        	// 具体规则
        	for (int i=0;i<payrule.size();i++)
        	{
        		PayRuleDef pr = (PayRuleDef)payrule.elementAt(i);
        		
        		// 获取规则信息
	            rs = GlobalInfo.baseDB.selectData("SELECT * from PAYRULE WHERE SEQNO = " + pr.seqno);
	            while(rs.next())
	            {
		    		if (!GlobalInfo.baseDB.getResultSetToObject(pr))
		            {
		                return null;
		            }
	            }
	            GlobalInfo.baseDB.resultSetClose();
        	}

    		return payrule;
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    		return null;
    	}
    	finally
    	{
    		GlobalInfo.baseDB.resultSetClose();
    		
    		PublicMethod.timeEnd(Language.apply("查询本地收款规则耗时: "));
    	}
    }
}
