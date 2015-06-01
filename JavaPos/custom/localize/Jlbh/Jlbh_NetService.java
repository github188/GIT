package custom.localize.Jlbh;

import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Cmls.Cmls_NetService;

public class Jlbh_NetService extends Cmls_NetService
{
	public boolean sendJKLCard(String mktcode, String syjh, long fphm, String syyh,String TRACEID,String SETTDATE,String TRANSTIME,String RMONEY,String memo1,String memo2)
	{
		if (!GlobalInfo.isOnline)
        {
            return false;
        }

        CmdHead head = null;
        StringBuffer line = new StringBuffer();
        int result = -1;
        String[] values = 
                          {
                              GlobalInfo.sysPara.jygs,mktcode,syjh,String.valueOf(fphm),syyh,TRACEID,SETTDATE, TRANSTIME, RMONEY,memo1
                          };
        String[] args = 
                        {
                            "jygs","mktcode", "syjh","fphm", "syyh", "TRACEID", "SETTDATE","TRANSTIME","RMONEY","memo1"
                        };

        try
        {
            head = new CmdHead(CmdDef.SENDJKLCARD);
            line.append(head.headToString() +
                        Transition.SimpleXML(values, args));

            //不显示错误信息
            result = HttpCall(GlobalInfo.localHttp, line, "储值卡确认交易失败");

            if (result == 0)
            {
               return true;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return false;
	}
	
	
}
