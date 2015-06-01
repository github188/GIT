package device.RdPlugins;

import com.efuture.javaPos.Device.Interface.Interface_RdPlugins;

public class AbacusCRM_RdPlugin implements Interface_RdPlugins
{
	static
	{
		System.loadLibrary("AbacusCRM");
	}

	private String errcode;
	private String errmsg;
	private String retXml;

	public native int load();

	public native String[] CrmInvoke(int funCode, String inputXmlData);

	public native String getHardDiskID();

	public native int release();

	private void clearMsg()
	{
		errcode = "";
		errmsg = "";
	}

	public boolean loadPlugins()
	{
		return load() == 0 ? true : false;
	}

	public boolean exec(int code, String param)
	{
		clearMsg();

		try
		{
			/*String hardinfo = getHardDiskID().trim();
			System.out.println(hardinfo);*/
			
			String[] retinfo = CrmInvoke(code, param);

			if (retinfo == null)
			{
				errcode = "XX";
				errmsg = "功能模块调用失败";
				return false;
			}

			if (retinfo.length > 0 && retinfo[0] != null)
			{
				errcode = retinfo[0];
				if (errcode.equals("0"))
				{
					if (retinfo.length > 1 && retinfo[1] != null)
						retXml = retinfo[1].trim();

					if (retinfo.length > 2 && retinfo[2] != null)
						errmsg = retinfo[2].trim();

					return true;
				}
				else
				{
					if (retinfo.length > 2 && retinfo[2] != null)
						errmsg = retinfo[2];
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return false;
	}

	public boolean exec(String param)
	{
		return false;
	}

	public String getErrorCode()
	{
		return errcode;
	}

	public String getErrorMsg()
	{
		if (errmsg == null)
			errmsg = "未知错误";

		return errmsg;
	}

	public boolean releasePlugins()
	{
		return release() == 0 ? true : false;
	}

	public Object getObject()
	{
		return this.retXml;
	}
}
