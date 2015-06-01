package custom.localize.Cczz;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Struct.CustomerDef;


public class Cczz_HykInfoQueryBS extends HykInfoQueryBS
{
    public CustomerDef findMemberCard(String track2)
    {
        String track = null;

        if (track2.indexOf("=") >= 0)
        {
            track = track2.substring(0, track2.indexOf("="));

            if (track.length() >= 16)
            {
                track = track2.substring(0, 16);
            }
            else
            {
                track = track2;
            }
        }
        else if (track2.length() > 16 && track2.indexOf("!") != 0)
        {
            track = track2.substring(0, 16);
        }
        else
        {
            track = track2;
        }

        CustomerDef cus = super.findMemberCard(track);

        if ((cus != null) && !GlobalInfo.isOnline)
        {
            new MessageBox("【脱网】下无法计算VIP的积分信息，请到服务台查询");
        }

        return cus;
    }
}
