package custom.localize.Wqbh;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bcrm.Bcrm_AccessLocalDB;

public class Wqbh_AccessLocalDB extends Bcrm_AccessLocalDB
{
	public void paraInitDefault()
	{
		super.paraInitDefault();
		GlobalInfo.sysPara.isDel="Y";
		GlobalInfo.sysPara.EBillandSgd="N";
		GlobalInfo.sysPara.isPrintDHY = "Y";
		GlobalInfo.sysPara.isAutoCheckIn = "Y";
		GlobalInfo.sysPara.isNEWDHY="N";
		GlobalInfo.sysPara.WHstoreId="";
	}
	public void paraConvertByCode(String code, String value)
	{
		try
		{
			if (code.equals("13")) {

			return; }

			if (code.equals("11"))
			{
				GlobalInfo.sysPara.mktname = value.trim();
				return;
			}
			if (code.equals("ID") && CommonMethod.noEmpty(value))
			{
				GlobalInfo.sysPara.isDel = value.trim();
				return;
			}	
			if (code.equals("W1") && CommonMethod.noEmpty(value))
			{
				String[] row = value.split(",");
				if(row.length >= 1) GlobalInfo.sysPara.EBillandSgd = row[0].trim();
				if(row.length >= 2) GlobalInfo.sysPara.isPrintDHY = row[1].trim();
				if(row.length >= 3) GlobalInfo.sysPara.isAutoCheckIn = row[2].trim();
				if(row.length >= 4) GlobalInfo.sysPara.isNEWDHY = row[3].trim();
				return;
			}
			if (code.equals("W7") && CommonMethod.noEmpty(value)){
				GlobalInfo.sysPara.WHstoreId = value.trim();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		super.paraConvertByCode(code, value);

	}
}
