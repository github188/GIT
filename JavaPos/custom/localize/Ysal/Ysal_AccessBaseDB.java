package custom.localize.Ysal;

import java.sql.ResultSet;

import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.OperUserDef;

import custom.localize.Bcrm.Bcrm_AccessBaseDB;

public class Ysal_AccessBaseDB extends Bcrm_AccessBaseDB
{
	public int getGoodsDef(GoodsDef goodsDef, int searchFlag, String code, String gz, String proTime, String yhsj, String djlb)
	{
		int flag;
		int result = -1;

		// 先按条码查找
		if (GlobalInfo.sysPara.forcebybarcode == 'Y' && code.length() > 0)
		{
			flag = 1;
			result = getGoodsDef(goodsDef, searchFlag, code, gz, proTime, yhsj, flag, djlb);
		}
		else if (code.length() >= 8)
		{
			flag = 1;
			result = getGoodsDef(goodsDef, searchFlag, code, gz, proTime, yhsj, flag, djlb);
		}
		else
		{
			// 允许代码销售,先按代码查
			flag = 0;
			if (GlobalInfo.sysPara.codesale == 'Y')
			{
				result = getGoodsDef(goodsDef, searchFlag, code, gz, proTime, yhsj, flag, djlb);
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
			result = getGoodsDef(goodsDef, searchFlag, code, gz, proTime, yhsj, flag, djlb);
		}
		if(result < 0){
			return result;
		}
		OperUserDef user = new  OperUserDef();
		Ysal_SaleBillMode ys = new Ysal_SaleBillMode();
		if(getOperUser(GlobalInfo.posLogin.gh,user)){
			//本地查询人员信息成功 则使用本地人员信息资料判断是否通收
			if (!user.ists.equals("Y") && ys.getMktCode().equals("0002"))
			{
				if (!findOperRangpriv(goodsDef.str1,goodsDef.gz))
					result = -1;				
			}
		}else{
			if (!GlobalInfo.posLogin.ists.equals("Y") && ys.getMktCode().equals("0002"))
			{
				if (!findOperRangpriv(goodsDef.str1,goodsDef.gz))
					result = -1;				
			}
		}
		return result;
	}
	
	//OPERRANGPRIV
    public boolean findOperRangpriv(String gys, String gz)    
    {
        try
        {
	        ResultSet rs = GlobalInfo.baseDB.selectData("select * from OPERRANGPRIV where gh = '" + GlobalInfo.posLogin.gh + "' AND SUPPLIER = '" + gys + "' AND GZ = '"+gz+"' AND MKT = '"+GlobalInfo.sysPara.mktcode +"'"  );
	    	if (rs != null && rs.next())
	    	{
	    		return true;
	    	}
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	
        	return false;
        }
        
        return false;
    }
}
