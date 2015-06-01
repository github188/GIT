package custom.localize.Bjys;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Struct.OperUserDef;

public class Bjys_AppendBusinessPerBS 
{
	public Bjys_AppendBusinessPerBS()
	{
		
	}
	
	public boolean findYyyh(String yyyh)
	{
		OperUserDef staff = null;
		
		try
		{
			if (yyyh == null || yyyh.trim().length() <= 0)
			{
				return true;
			}
			
			staff = new OperUserDef();
			
			if (DataService.getDefault().getOperUser(staff,yyyh))
            {
            	if (staff.type != '2') 
            	{
                    new MessageBox("该工号不是营业员!", null, false);
                    

                    return false;
            	}
            	
            	//检查工号过期
                String expireDate = staff.maxdate + " 0:0:0";
                ManipulateDateTime mdt = new ManipulateDateTime();
                if (mdt.getDisDateTime(mdt.getDateBySlash() + " 0:0:0", expireDate) < 0)
                {
                    new MessageBox("该工号已过期!", null, false);
                    
                    return false;
                }
                
            	return true;
            }
			else
			{
				return false;
			}
		}
		catch (Exception ex)
		{
			new MessageBox("查找营业员工号时发生异常:" + ex.getMessage());
			ex.printStackTrace();
			return false;
		}
	}
}
