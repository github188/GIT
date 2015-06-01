package custom.localize.Syss;

import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bhls.Bhls_AccessLocalDB;


public class Syss_AccessLocalDB extends Bhls_AccessLocalDB
{
    public void paraConvertByCode(String code, String value)
    {
        try
        {
            if (code.equals("13"))
            {
                return;
            }

            if (code.equals("11"))
            {
                GlobalInfo.sysPara.mktname = value.trim();

                return;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        super.paraConvertByCode(code, value);
    }
}
