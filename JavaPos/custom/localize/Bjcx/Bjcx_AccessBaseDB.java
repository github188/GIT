package custom.localize.Bjcx;

import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Struct.CustomerDef;

import custom.localize.Bcrm.Bcrm_AccessBaseDB;

public class Bjcx_AccessBaseDB extends Bcrm_AccessBaseDB
{

	public boolean getCustomer(CustomerDef cust, String track)
    {
		try
		{
			//城乡脱网时，从2轨里截取=号前面的数据为卡号
			track = track.trim();
			int index = track.indexOf("=");
			if (index > 0)
			{
				track = Convert.newSubString(track, 0, index);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		
		}
		return super.getCustomer(cust, track);
    }
}
