package custom.localize.Dxyc;

import java.util.Vector;

import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.JfSaleRuleDef;

import custom.localize.Bstd.Bstd_NetService;


public class Dxyc_NetService extends Bstd_NetService
{
//	 查找商品是否存在换购规则
    public boolean getJfExchangeGoods(JfSaleRuleDef jsrd,String code,String cuscode,String type)
    {
    	if (!GlobalInfo.isOnline) return false;
    	
    	CmdHead cmdHead = null;
        StringBuffer line = new StringBuffer();
        int result = -1;
        
        try
        {
        	cmdHead = new CmdHead(CmdDef.GETGOODSEXCHANGE);
        	
        	String[] value = 
            {
        		GlobalInfo.sysPara.mktcode,GlobalInfo.sysPara.jygs,cuscode,type,code
            };
        	
        	String[] arg = 
        	{
        		"mktcode","jygs","track","type","code"
        	};
        	
        	line.append(cmdHead.headToString() + Transition.SimpleXML(value, arg));
        	
        	
        	result = HttpCall(getMemCardHttp(CmdDef.GETGOODSEXCHANGE), line, "");
        	
        	if (result == 0)
            {
        		Vector v = new XmlParse(line.toString()).parseMeth(0,JfSaleRuleDef.ref);
        		
        		if (v.size() > 0)
                {
        			String[] lines = (String[]) v.elementAt(0);
        			
        			if (Transition.ConvertToObject(jsrd, lines))
                    {
                        return true;
                    }
                }
            }
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        }
        finally
        {
        	cmdHead = null;
            line    = null;
        }
        
    	return false;
    }
}
