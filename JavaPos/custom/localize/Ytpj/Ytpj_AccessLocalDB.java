package custom.localize.Ytpj;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.GlobalInfo;
import custom.localize.Bstd.Bstd_AccessLocalDB;

public class Ytpj_AccessLocalDB extends Bstd_AccessLocalDB
{
	public void paraInitDefault()
	{
		super.paraInitDefault();
		GlobalInfo.sysPara.isPrintDHY = "Y";
		GlobalInfo.sysPara.isAutoCheckIn = "Y";
	}
	public void paraConvertByCode(String code, String value)
	{
		try
		{
			if (code.equals("W1") && CommonMethod.noEmpty(value))
			{
				String[] row = value.split(",");
				if(row.length >= 1) GlobalInfo.sysPara.isPrintDHY = row[0].trim();
				if(row.length >= 2) GlobalInfo.sysPara.isAutoCheckIn = row[1].trim();
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
