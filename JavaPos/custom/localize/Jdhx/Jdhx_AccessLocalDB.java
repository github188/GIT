package custom.localize.Jdhx;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bstd.Bstd_AccessLocalDB;

public class Jdhx_AccessLocalDB extends Bstd_AccessLocalDB
{
	
	public void paraInitDefault()
	{
		super.paraInitDefault();

		GlobalInfo.sysPara.isWater = "Y";
	}
    public void paraConvertByCode(String code, String value)
    {
        super.paraConvertByCode(code, value);

        try
        {
			if (code.equals("HN") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isWater = value.trim();
				return;
			}
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
