package device.RdPlugins;

import com.efuture.javaPos.Device.Interface.Interface_RdPlugins;

public class WuHanTong_RdPlugin implements Interface_RdPlugins
{
	static
	{
		System.loadLibrary("WuHanTongPos");
		/*
		 * if (PathFile.fileExist("WuHanTongPos.dll")) {
		 * System.loadLibrary("WuHanTongPos"); enable = true; }
		 */
	}

	private String errcode;
	private String errmsg;

	public native int load();

	public native String[] execute(String cmd);

	public native int release();

	// private static WuHanTong_RdPlugin card;

	private void clear()
	{
		errcode = "";
		errmsg = "";
	}


	public boolean loadPlugins()
	{
		return load() == 0 ? true : false;
	}

	public boolean releasePlugins()
	{
		return release() == 0 ? true : false;
	}

	public boolean exec(String para)
	{
		clear();

		String[] retinfo = execute(para);

		if (retinfo == null)
		{
			errcode = "XX";
			errmsg = "功能模块调用失败";
		}

		if (retinfo.length > 0 && retinfo[0] != null)
			errcode = retinfo[0];
		if (retinfo.length > 1 && retinfo[1] != null)
			errmsg = retinfo[1];

		return true;
	}

	public String getErrorCode()
	{
		return errcode;
	}

	public String getErrorMsg()
	{
		return errmsg;
	}

	public boolean exec(int code, String param)
	{
		return false;
	}

	public Object getObject()
	{
		return null;
	}

}
