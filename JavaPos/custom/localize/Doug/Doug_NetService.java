package custom.localize.Doug;

import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bhls.Bhls_NetService;

public class Doug_NetService extends Bhls_NetService
{

    public boolean findFwqRange(String code,String gz,String uid)
    {
        if (!GlobalInfo.isOnline)
        {
            return false;
        }

        String[] values = { code, gz, uid };
        String[] arg = { "code", "gz", "spec"};
        CmdHead aa = null;
        StringBuffer line = new StringBuffer();
        int result = -1;

        try
        {
            aa = new CmdHead(CmdDef.FINDFWQRANGE);
            line.append(aa.headToString() + Transition.SimpleXML(values, arg));

            result = HttpCall(line, "");

            if (result == 0)
            {
            	return true;
            }
        }
        catch (Exception er)
        {
            er.printStackTrace();
        }

        return false;    	
    }

}
