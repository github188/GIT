package custom.localize.Cbcp;

import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.HykInfoQueryBS;

public class Cbcp_HykInfoQueryBS extends HykInfoQueryBS
{

	public String readMemberCard(boolean ispay)
	{
		char flag = GlobalInfo.sysPara.findcustfjk;
		try
		{
			GlobalInfo.sysPara.findcustfjk = 'Y';
			return super.readMemberCard(ispay);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		finally
		{
			GlobalInfo.sysPara.findcustfjk = flag;
		}		
	}
}
