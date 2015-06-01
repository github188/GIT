package custom.localize.Wqbh;

import java.sql.ResultSet;
import java.util.Vector;
import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Wqbh.Wqbh_SaleBillMode.DQTaxDef;


public class Wqbh_AccessDayDB extends AccessDayDB
{
	public static boolean writeTaxLog(String[] faxref ,Object obj)
	{
    	try
    	{
            // 按表的字段确定对象的数据,表结构不存在的数据不保存
            String[] ref = GlobalInfo.dayDB.getTableColumns("FAXLOG");
            if (ref == null || ref.length <= 0) ref = faxref;
            ref[13] = "net_bz";
    		String line = CommonMethod.getInsertSql("FAXLOG", ref);
			
			if (!GlobalInfo.dayDB.setSql(line))
            {
                return false;
            }
			
			if (!GlobalInfo.dayDB.setObjectToParam(obj,ref))
            {
                return false;
            }
			
			if (!GlobalInfo.dayDB.executeSql())
            {
                return false;
            }
			
			//记录发送任务
            //AccessLocalDB.getDefault().writeTask(StatusType.TASK_SENDBANKLOG,TaskExecute.getKeyTextByBalanceDate());

    		return true;
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    		return false;
    	}
    }
	
	public static DQTaxDef getTaxLog(String syjh, long fphm)
    {
		Wqbh_SaleBillMode ws = new  Wqbh_SaleBillMode();
		DQTaxDef dqinfo  = ws. new DQTaxDef();
        ResultSet rs = null;  
        
    	try
    	{
    		rs = GlobalInfo.dayDB.selectData("select * from FAXLOG where fphm = '" + fphm + "' and syjh = '" + syjh + "'");
            if (rs == null)
            {
                return null;
            }
            
           if(rs.next())
            {
                if (!GlobalInfo.dayDB.getResultSetToObject(dqinfo,dqinfo.ref))
                {
                    return null;
                }

            }
            
            return dqinfo;
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    		return null;
    	}
        finally
        {
            GlobalInfo.dayDB.resultSetClose();
        }
    }
	
	public static SaleHeadDef getSaleHead(String syjh, long fphm)
    {
		SaleHeadDef shead  =  new SaleHeadDef();
        ResultSet rs = null;  
        
    	try
    	{
    		rs = GlobalInfo.dayDB.selectData("select * from SALEHEAD where fphm = '" + fphm + "' and syjh = '" + syjh + "'");
            if (rs == null)
            {
                return null;
            }
            
           if(rs.next())
            {
                if (!GlobalInfo.dayDB.getResultSetToObject(shead,SaleHeadDef.ref))
                {
                    return null;
                }

            }
            
            return shead;
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    		return null;
    	}
        finally
        {
            GlobalInfo.dayDB.resultSetClose();
        }
    }

	 public static Vector getSaleDetail( String code)
	    {
	        ResultSet rs = null;
	        
	        try
	        {

	            //syjh = '" + ConfigClass.CashRegisterCode + "' and 
	            if ((rs = GlobalInfo.dayDB.selectData("select * from SALEGOODS where fphm = " + code + " order by rowno")) != null)
	            {
	                Vector salegoods = new Vector();
	                while (rs.next())
	                {
	                    SaleGoodsDef sg = new SaleGoodsDef();

	                    if (!GlobalInfo.dayDB.getResultSetToObject(sg))
	                    {
	                        return null;
	                    }

	                    salegoods.add(sg);
	                    
	                }
	                
	                return salegoods;
	            }
	            else
	            {
	                return null;
	            }
	        }
	        catch (Exception ex)
	        {
	            ex.printStackTrace();

	            return null;
	        }
	        finally
	        {
	        	GlobalInfo.dayDB.resultSetClose();
	        }
	    }
	 
	 public static Vector getPayDetail(String code)
	    {
	        ResultSet rs = null;
	        
	        try
	        {
	           if ((rs = GlobalInfo.dayDB.selectData("select * from SALEPAY where fphm = " + code +" Order By (case when rowno > 0 then 1 else -1 end) Desc,ABS(rowno) Asc")) != null)
	            {
	            	Vector salepay = new Vector();
	                while (rs.next())
	                {
	                	SalePayDef sp = new SalePayDef();
	                	
	                	if (!GlobalInfo.dayDB.getResultSetToObject(sp))
	                    {
	                        return null;
	                    }

	                	salepay.add(sp);
	                	
	                }

	                return salepay;
	            }
	            else
	            {
	                return null;
	            }
	        }
	        catch (Exception ex)
	        {
	            ex.printStackTrace();

	            return null;
	        }
	        finally
	        {
	        	GlobalInfo.dayDB.resultSetClose();
	        }
	    }
}
