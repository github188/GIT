package custom.localize.Doug;

import java.sql.ResultSet;

import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bhls.Bhls_AccessBaseDB;

public class Doug_AccessBaseDB extends Bhls_AccessBaseDB
{

	public boolean findFwqRange(String code,String gz,String uid)
	{
    	ResultSet rs = null;
    	
    	try
    	{
    		rs = GlobalInfo.baseDB.selectData("select code from goodspop where code = '"+ code +"' and (gz = '"+ gz + "' or gz = '0') and uid = '" + uid +"' and rule = 'QFW'");
    		
    		if (rs != null && rs.next())
    		{
    			return true;
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
    	
		return false;
	}

}
