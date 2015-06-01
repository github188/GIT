package custom.localize.Tcrc;

import java.util.Vector;

import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.GlobalInfo;
import custom.localize.Bhls.Bhls_NetService;

public class Tcrc_NetService extends Bhls_NetService
{
	public String[] FINDGRANT(String gh,String gz)
	{
	   	 if (!GlobalInfo.isOnline)
         {
             return null;
         }

    	 CmdHead head = null;
         StringBuffer line = new StringBuffer();
         int result = -1;
         String[] values = {gh,gz};
         String[] args = { "gh","gz"};
         try
         {
        	 head = new CmdHead(CmdDef.GETONECOMMONVALUES);
        	 line.append(head.headToString() + Transition.SimpleXML(values, args));
        	 
             result = HttpCall(getMemCardHttp(CmdDef.GETONECOMMONVALUES),line, "");
             
             if (result == 0)
 			{
 				Vector v = new XmlParse(line.toString()).parseMeth(0,new String[] {"riszk"});
            	
 				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);
					return row;// [0];
				}
 			}
 		}
 		catch (Exception er)
 		{
 			er.printStackTrace();
 		}
		return null;

	}
}
