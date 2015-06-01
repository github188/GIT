package device.ICCard;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_ICCard;

public class ICCARD implements Interface_ICCard
{
	static
	{
		System.loadLibrary("ICCARD");
	}

	int deviceNo = 0;

	public native int open(String path, int com, long band);

	public native String searchCard(int deviceID, int section);

	public native int close_reader(int deviceID);

	public boolean close()
	{
		/**
		int i = close_reader(deviceNo);
		
		if (i == 0) { return true; }

		return false;
		*/
		return true;
	}

	public String getDiscription()
	{
		return "解百项目IC卡设备";
	}

	public Vector getPara()
	{
		Vector v = new Vector();
		v.add(new String[]{"端口号","0","1","2","3","4","5","6","7","8","9"});	
		v.add(new String[]{"波特率","9600","110","300","600","1200","2400","4800","19200"});
    	
		return v;
	}

	public boolean open()
	{
		String[] arg = DeviceName.deviceICCard.split(",");
		String path = "";
		int com = 0;
		long band = 0;

		if (arg.length > 0)
		{
			path = arg[0];

			if (arg.length > 1)
			{
				com = Convert.toInt(arg[1]);
			}

			if (arg.length > 2)
			{
				band = Convert.toLong(arg[2]);
			}
		}
		
		deviceNo = open(path, com, band);

		if (deviceNo >= 0) { return true; }

		return false;
	}

	public String findCard()
	{

		String line = searchCard(deviceNo, 0);
		
		return line;
	}

	public String updateCardMoney(String cardno, String operator, double ye)
	{
		return "error:该设备不支持本功能";
	}
}
