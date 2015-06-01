package custom.localize.Bcrm;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.LoadSysInfo;


public class Bcrm_LoadSysInfo extends LoadSysInfo
{
    public boolean checkLicenceByExtend(String strseq, String key)
    {
    	String mktcode = GlobalInfo.sysPara.mktcode;
    	
        // 分解经营公司
        if (GlobalInfo.sysPara.mktcode != null)
        {
            if (GlobalInfo.sysPara.mktcode.split(",").length >= 2)
            {
                mktcode = GlobalInfo.sysPara.mktcode.substring(GlobalInfo.sysPara.mktcode.indexOf(",") + 1);
            }
        }

        String code = ManipulatePrecision.getRegisterCode(GlobalInfo.sysPara.mktname, mktcode, strseq, key);

        if (code.equals(ConfigClass.CDKey))
        {
            return true;
        }
        else
        {
        	return false;
        }
    }
}
