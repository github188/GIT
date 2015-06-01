package custom.localize.Sdyz;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.OperUserDef;

public class Sdyz_DataService extends com.efuture.javaPos.Global.DataService
{
	public String analyzeGoodsBarcode(String code)
    {
		if (code.charAt(0) == '2' && code.length() == 8)
			return code.substring(1,7);
		else if (code.length() == 13)
			return code.substring(0,12);
		else
			return code;
    }
	
    public boolean getCustomer(CustomerDef cust, String track)
    {
        if (GlobalInfo.isOnline)
        {
            if (!NetService.getDefault().getCustomer(cust,track))
            {
                return false;
            }
        }
        else
        {
        	new MessageBox("顾客卡必须联网使用!", null, false);
        	
            return false;
        }
        
        return true;
    }
    
    public boolean getOperUser(OperUserDef staff, String id)
    {
    	if (!super.getOperUser(staff,id)) return false;

    	//永远具有红冲权限
    	StringBuffer s = new StringBuffer(staff.priv);
    	s.setCharAt(0, 'Y');
    	staff.priv = s.toString();
        
        return true;
    }
}
