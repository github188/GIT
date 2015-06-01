package custom.localize.Ybsj;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bstd.Bstd_AccessLocalDB;

public class Ybsj_AccessLocalDB extends Bstd_AccessLocalDB
{
    public void paraConvertByCode(String code, String value)
    {
        super.paraConvertByCode(code, value);

        try
        {
        	if (code.equals("WJ") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.cyCrmUrl = value.trim();
				return;
			}
        	
            if (code.equals("WK") && CommonMethod.noEmpty(value))
            {
                GlobalInfo.sysPara.cyCrmUsrPwd = value.trim();
                return;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
