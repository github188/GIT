package custom.localize.Bszm;

import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bstd.Bstd_AccessLocalDB;

public class Bszm_AccessLocalDB extends Bstd_AccessLocalDB
{
	// 专卖客户化参数
	public void paraConvertByCode(String code, String value)
	{

		try
		{
			super.paraConvertByCode(code, value);

			if (code.equals("Z1"))
			{
				GlobalInfo.sysPara.iscfginputarea = value.charAt(0);
				return;
			}
			
			if (code.equals("Z2"))
			{
				GlobalInfo.sysPara.iscodeupper = value.charAt(0);
			}

			if (code.equals("Z3"))
			{
				GlobalInfo.sysPara.whenprintbill = value.charAt(0);
			}
			
			if (code.equals("Z4"))
			{
				GlobalInfo.sysPara.backpayctrl = value.charAt(0);
			}
			
			if (code.equals("Z5"))
			{
				GlobalInfo.sysPara.jfPayCodeList = (value==null ? "":value.replace(" ", ""));
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	// 参数初始值
	public void paraInitDefault()
	{
		try
		{
			super.paraInitDefault();
			
			GlobalInfo.sysPara.iscfginputarea = 'N';
			GlobalInfo.sysPara.iscodeupper  = 'N';
			GlobalInfo.sysPara.whenprintbill = 'Y';
			GlobalInfo.sysPara.backpayctrl = 'N';
			GlobalInfo.sysPara.jfPayCodeList = "";//01,02
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
