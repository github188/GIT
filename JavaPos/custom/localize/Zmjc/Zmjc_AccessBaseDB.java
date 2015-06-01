package custom.localize.Zmjc;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.PublicMethod;

import custom.localize.Bcrm.Bcrm_AccessBaseDB;

public class Zmjc_AccessBaseDB extends Bcrm_AccessBaseDB
{
	
	
	/**
	 * 获取国籍信息
	 * @param flights 获取的国籍信息(里面装载的是NationalityDef类)
	 * @param fnumber 国籍查询条件
	 * @param isLike 是否模糊查询
	 * @return
	 */
	public boolean getNationality(Vector nationality, String nNumber, boolean isLike)
	{		
		ResultSet rs = null;
		String sql = "";
		try
		{
			PublicMethod.timeStart(Language.apply("正在读取本地国籍信息,请等待......"));
			
			nationality.removeAllElements();
			
			sql = "SELECT PCRCODE, PCRCNAME, PCRENAME FROM poscoderegion " ;
			if(nNumber != null && nNumber.length() > 0)
			{				
				if (isLike)
				{
//					sql += "WHERE upper(nnumber) LIKE '%" + nnumber.toUpperCase() + "%' ";
					sql += "WHERE UPPER(PCRENAME) LIKE '%" + nNumber.toUpperCase() + "%' ";
				}
				else
				{
//					sql += "WHERE upper(nnumber) = '" + nnumber.toUpperCase() + "' ";
					sql += "WHERE UPPER(PCRENAME) = '" + nNumber.toUpperCase() + "' ";
				}
			}	
			
            sql += " ORDER BY PCRENAME ";
			
			rs = GlobalInfo.baseDB.selectData(sql);
			if (rs == null) { return false; }
			int i=1;
			while (rs.next())
			{
				NationalityDef nDef = new NationalityDef();
				i++;
				if (GlobalInfo.baseDB.getResultSetToObject(nDef)) 
				{
					nationality.add(nDef);					
				}
			} 
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
			//
			PublicMethod.timeEnd(Language.apply("读取本地国籍信息耗时: "));
		}
	}


	//获取打包商品明细
	public boolean getGoodsPackList(Vector goodsPackList, String goodsCode, String gz, double lsj)
	{
		ResultSet rs = null;
    	
    	try
    	{
        	PublicMethod.timeStart(Language.apply("正在查询本地打包商品明细,请等待......"));
    		
    		String sqlstr = "SELECT * from PACKGOODSDETAIL WHERE PACKCODE = '" + goodsCode + "'";
    		GlobalInfo.baseDB.setSql(sqlstr);
    		
        	rs = GlobalInfo.baseDB.selectData();
    		String[] row;
        	while(rs.next())
        	{
        		row = new String[10];
        		row[0] = rs.getString("code");
        		row[1] = rs.getString("barcode");
        		row[2] = rs.getString("jg");
        		row[3] = rs.getString("sl");
        		row[4] = rs.getString("sjje");
        		row[5] = rs.getString("zkfd");
        		row[6] = rs.getString("str1");
        		row[7] = rs.getString("str2");
        		row[8] = rs.getString("num1");
        		row[9] = rs.getString("num2");
        		goodsPackList.add(row);
        	}
        	GlobalInfo.baseDB.resultSetClose();
        	
    		// 参与的分组规则
    		return true;
    	}
    	catch (Exception ex)
    	{
    		goodsPackList.removeAllElements();
    		ex.printStackTrace();
    		return false;
    	}
    	finally
    	{
    		GlobalInfo.baseDB.resultSetClose();
    		
    		PublicMethod.timeEnd(Language.apply("查询本地促销分组耗时: "));
    	}
		
	}
}
