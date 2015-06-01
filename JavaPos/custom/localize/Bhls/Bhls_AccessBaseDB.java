package custom.localize.Bhls;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.javaPos.Global.AccessBaseDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.PublicMethod;
import com.efuture.javaPos.Struct.GoodsAmountDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;

public class Bhls_AccessBaseDB extends AccessBaseDB
{
	public void transferPopInfoToGoodsInfo(GoodsDef finalGoods,GoodsPopDef popDef)
	{
		super.transferPopInfoToGoodsInfo(finalGoods, popDef);
		
	    // 促销单的yhspace记录有促销单是否允许折上折标志，
		// 再用商品上的pophyjzkl字段标记yhspace,用于计算是判断
		if (popDef != null)
	    finalGoods.pophyjzkl = popDef.yhspace;
	}
	
    public boolean findAmountDef(GoodsAmountDef pl,String code,String gz,String uid, double sl)
    {
		 String date =ManipulateDateTime.getCurrentDate();
		 
		 //先查找是否有商品折扣率定义
    	 String command = "SELECT (case when MAX(PLLSJ) is null then 0 else MAX(PLLSJ) end) from GOODSAMOUNT where "+"  code = '"+code+"' AND "
    	 		+"UID = '"+uid +"' AND PLSL ="+ 0 +" AND KSRQ <= '"+date +"' AND JSRQ >= '"+date+"'";
    	 
         Object obj = GlobalInfo.baseDB.selectOneData(command);
         int pllsj = 0;
         if (obj == null || Convert.toInt(obj) == 0)
         {
        	 //再查找是否有柜组折扣率定义
        	 command = "SELECT (case when MAX(PLLSJ) is null then 0 else MAX(PLLSJ) end) from GOODSAMOUNT where gz = '"+gz+"' AND "
 	 				+"UID = '"+uid +"' AND PLSL = "+ 1;
        	 obj = GlobalInfo.baseDB.selectOneData(command);
        	 if (obj == null)
        		 return false;
         }
         
         pllsj = Convert.toInt(obj);
         
         if (pllsj == 0) return false;
         
         command = "SELECT  code,gz,uid,pllsj,plhyj,plsl,ksrq,jsrq,memo from GOODSAMOUNT where pllsj = " + pllsj;
         
         ResultSet rs = null;
         
         try
         {
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
        		
         }
         catch(Exception er)
         {
        	 er.printStackTrace();
        	 return false;
         }
         finally
         {
        	GlobalInfo.baseDB.resultSetClose(); 
         }
    }

    //查找优惠规则促销
    public boolean findPopRule(GoodsPopDef popDef,String code,String gz,String uid,String rulecode,String catid,String ppcode,String time, String custType, String custNo)
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
    		
    		if (rule1 == null || rule2 == null || rule3 == null ) return false;
    		
    		ManipulateDateTime mdt = new ManipulateDateTime();
    		
    		String sqlstr = "SELECT CASE WHEN MAX(seqno) IS NULL THEN 0 ELSE MAX(seqno) END FROM GOODSPOP " +
    		"WHERE ksrq <= '" + mdt.getDateBySlash() + "' AND jsrq >= '" + mdt.getDateBySlash() + "' AND " +
    		"kssj <= '" + vtime[1].trim().substring(0,5) + "' AND jssj >= '" + vtime[1].trim().substring(0,5) + "' AND " +
    		"(rule = '"+ rule1 + "' OR rule = '" + rule2 + "' OR rule = '" + rule3 + "' ) AND " +
    		"(" +
    		"(code = '"+ code + "' AND (gz = '" + gz + "' OR gz = '0') AND type = '1' AND " +
    		"(CASE WHEN LTRIM(uid) IS NULL THEN '00' ELSE LTRIM(uid) END = CASE WHEN LTRIM('"+ uid +"') IS NULL THEN '" + 00 + "' ELSE LTRIM('"+ uid +"') END)" +
    		"  AND sl <> 0 )" +
    		" OR ((code = '"+ gz + "' OR code = '0') AND type = '2')"+
    		" OR ((code = '"+ gz + "' OR code = '0') AND ppcode = '"+ ppcode +"' AND type = '4')"+
    		" OR (code = '" + catid + "' AND type = '3')"+
    		" OR (code = '"+ catid + "' AND ppcode = '"+ ppcode +"' AND type = '5')" +
    		" OR (code = '"+ ppcode +"' AND type = '5 ')" +
    		")"; 
    		
    		
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
            
            sqlstr = "SELECT seqno,djbh,type,rule,mode,code,gz,uid,catid,ppcode,sl,yhspace,ksrq,jsrq,kssj,jssj,poplsj,pophyj,poppfj" +
            		",poplsjzkl,pophyjzkl,poppfjzkl,poplsjzkfd,pophyjzkfd,poppfjzkfd,memo,str1,str2,num1,num2 FROM GOODSPOP" +
            		" WHERE seqno = "+ seqno ;
            
            rs = GlobalInfo.baseDB.selectData(sqlstr);
            
            if (rs == null) return false;
    		
    		boolean ret = false;
    		while (rs.next())
    		{
    			if (!GlobalInfo.baseDB.getResultSetToObject(popDef))
                {
                    return false;
                }
    			
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
    
    public boolean findPopRuleNew(GoodsPopDef popDef,String code,String gz,String uid,String rulecode,String catid,String ppcode,String time)
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
    		
    		String sqlstr = "SELECT CASE WHEN MAX(seqno) IS NULL THEN 0 ELSE MAX(seqno) END FROM GOODSPOP " +
    		",(select memo from GOODSPOP where code = '" + rulecode + "' and rule = 'DMJ') t2 " +
    		"WHERE ksrq <= '" + mdt.getDateBySlash() + "' AND jsrq >= '" + mdt.getDateBySlash() + "' AND " +
    		"kssj <= '" + vtime[1].trim().substring(0,5) + "' AND jssj >= '" + vtime[1].trim().substring(0,5) + "' AND " +
    		"(rule = 'NMJ' ) AND " +
    		"(" +
    		"(code = '"+ code + "' AND (gz = '" + gz + "' OR gz = '0') AND type = '1' AND " +
    		"(CASE WHEN LTRIM(uid) IS NULL THEN '00' ELSE LTRIM(uid) END = CASE WHEN LTRIM('"+ uid +"') IS NULL THEN '" + 00 + "' ELSE LTRIM('"+ uid +"') END)" +
    		"  AND sl <> 0 )" +
    		" OR ((code = '"+ gz + "' OR code = '0') AND type = '2')"+
    		" OR ((code = '"+ gz + "' OR code = '0') AND ppcode = '"+ ppcode +"' AND type = '4')"+
    		" OR (code = '" + catid + "' AND type = '3')"+
    		" OR (code = '"+ catid + "' AND ppcode = '"+ ppcode +"' AND type = '5')" +
    		" OR (code = '"+ ppcode +"' AND type = '6')" +
    		")" + 
    		" and goodspop.memo = t2.memo"; 
    		
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
            
            sqlstr = "SELECT seqno,djbh,type,rule,mode,code,t2.gz gz,uid,t2.catid catid,t2.ppcode ppcode,sl,yhspace,ksrq,jsrq,kssj,jssj,t2.poplsj poplsj, t2.pophyj pophyj,poppfj,t2.poplsjzkl poplsjzkl,pophyjzkl,poppfjzkl,poplsjzkfd,pophyjzkfd,poppfjzkfd,goodspop.memo memo,str1,t2.str2 str2,num1,num2 FROM GOODSPOP" +
            		",(select memo,gz,catid,ppcode,pophyj,poplsjzkl,poplsj,str2 from GOODSPOP where code = '" + rulecode + "' and rule = 'DMJ') t2 " +
            		" WHERE seqno = "+ seqno +
            		" and goodspop.memo = t2.memo";
            
            rs = GlobalInfo.baseDB.selectData(sqlstr);
            
            if (rs == null) return false;
    		
    		boolean ret = false;
    		while (rs.next())
    		{
    			if (!GlobalInfo.baseDB.getResultSetToObject(popDef))
                {
                    return false;
                }
    			
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
    
    public boolean findGiftRuleNew(GoodsPopDef popDef,String code,String gz,String uid,String rulecode,String catid,String ppcode,String time,String cardno,String cardtype)
    {
    	ResultSet rs = null;
    	
    	try
    	{
    		PublicMethod.timeStart(Language.apply("正在查询本地赠品促销,请等待......"));
    		
    		if (time == null || time.trim().equals("")) return false;
    		String vtime[] = time.split(" ");
    		if (vtime.length < 2) return false;
    		if (vtime[1].length() < 5) return false;
    			    		
    		ManipulateDateTime mdt = new ManipulateDateTime();
    		
    		// 增加会员组判断
    		String customercond = "";
    		if (cardno == null || cardtype == null || cardno.trim().equals("") || cardtype.trim().equals(""))
    		{
    			customercond = " AND (str1 = 'ALL' OR str1 = '@2') ";
    		}
    		else
    		{
    			customercond = " AND (str1 = 'ALL' OR str1 = '@1' OR str1 = '" + cardtype +" ') ";
    		}
    		
    		String sqlstr = "SELECT CASE WHEN MAX(seqno) IS NULL THEN 0 ELSE MAX(seqno) END FROM GOODSPOP " +
    		"WHERE ksrq <= '" + mdt.getDateBySlash() + "' AND jsrq >= '" + mdt.getDateBySlash() + "' AND " +
    		"kssj <= '" + vtime[1].trim().substring(0,5) + "' AND jssj >= '" + vtime[1].trim().substring(0,5) + "' AND " +
    		"(rule = 'RMS') AND pophyj = " + rulecode + customercond + 
    		" AND ((code = '"+ code + "' AND (gz = '" + gz + "' OR gz = '0') AND type = '1' AND " +
    		" (CASE WHEN LTRIM(uid) IS NULL THEN '00' ELSE LTRIM(uid) END = CASE WHEN LTRIM('"+ uid +"') IS NULL THEN '" + 00 + "' ELSE LTRIM('"+ uid +"') END)" +
    		" AND sl <> 0 )" +
    		" OR ((code = '"+ gz + "' OR code = '0') AND type = '2')"+
    		" OR ((code = '"+ gz + "' OR code = '0') AND ppcode = '"+ ppcode +"' AND type = '4')"+
    		" OR (code = '" + catid + "' AND type = '3')"+
    		" OR (code = '"+ catid + "' AND ppcode = '"+ ppcode +"' AND type = '5')" +
    		" OR (code = '"+ ppcode +"' AND type = '5 ')" +
    		")"; 
    		
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
            
            sqlstr = "SELECT seqno,djbh,type,rule,mode,code,gz,uid,catid,ppcode,sl,yhspace,ksrq,jsrq,kssj,jssj,poplsj,pophyj,poppfj" +
            		",poplsjzkl,pophyjzkl,poppfjzkl,poplsjzkfd,pophyjzkfd,poppfjzkfd,memo,str1,str2,num1,num2 FROM GOODSPOP" +
            		" WHERE seqno = "+ seqno ;
            
            rs = GlobalInfo.baseDB.selectData(sqlstr);
            
            if (rs == null) return false;
    		
    		boolean ret = false;
    		while (rs.next())
    		{
    			if (!GlobalInfo.baseDB.getResultSetToObject(popDef))
                {
                    return false;
                }
    			
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
    		
    		PublicMethod.timeEnd(Language.apply("查询本地赠品促销耗时: "));
    	}
    }
    
    //寻找编码商品赠品促销
    public boolean findRulePopGift(Vector giftGoods,String djbh)
    {
    	ResultSet rs = null;
    	
    	try
    	{
    		String sqlstr = "SELECT seqno,djbh,type,rule,mode,code,gz,uid,catid,ppcode,sl,yhspace,ksrq,jsrq,kssj,jssj,poplsj,pophyj,poppfj" +
    		",poplsjzkl,pophyjzkl,poppfjzkl,poplsjzkfd,pophyjzkfd,poppfjzkfd,memo,str1,str2,num1,num2 FROM GOODSPOP " +
    		" WHERE djbh = '"+ djbh +"' and rule = 'RJG'";
    		
    		rs = GlobalInfo.baseDB.selectData(sqlstr);
            
            if (rs == null) return false;
    		
    		boolean ret = false;
    		while (rs.next())
    		{
    			GoodsPopDef gpd = new GoodsPopDef();
    			
    			if (!GlobalInfo.baseDB.getResultSetToObject(gpd))
                {
                    return false;
                }
    			
    			giftGoods.add(gpd);
    			
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
