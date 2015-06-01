package device.RdPlugins;

import com.efuture.javaPos.Device.Interface.Interface_RdPlugins;

public class WuWeiScale_RdPlugin implements Interface_RdPlugins
{
	static
	{
		System.loadLibrary("escale");
		//new MessageBox("escale.dll load ok");
	}

	public native int load();

	public native String[] getweight();

	public native int sendtare();

	public native int sendzero();

	public native int release();

	private String weight = "0.00";

	public boolean loadPlugins()
	{
		return load() == 0 ? true : false;
	}

	public boolean releasePlugins()
	{
		return release() == 0 ? true : false;
	}

	public boolean exec(String param)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean exec(int code, String param)
	{
		switch (code)
		{
			//清零
			case 0:
				if (sendzero() == 240)
					return true;

			//去皮
			case 1:
				if (sendtare() == 240)
					return true;

			//获取重量
			case 2:
				weight = "0.00";
				
				String[] ret = getweight();
				if (ret == null || ret.length < 2)
					return false;

				if (!ret[0].equals("240"))
					return false;

				weight = ret[1];
				return true;

		}

		return false;
	}

	public String getErrorCode()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getErrorMsg()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Object getObject()
	{
		return weight;
	}
}
