package com.efuture.javaPos.Device;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Interface.Interface_ElectronicScale;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.Language;

public class ElectronicScale
{
	public static ElectronicScale deviceEScale = null;

	private Interface_ElectronicScale EScale = null;

	private boolean connect = false;

	private boolean enable = false;

	public static ElectronicScale getDefault()
	{
		if (ElectronicScale.deviceEScale == null)
		{
			ElectronicScale.deviceEScale = new ElectronicScale(ConfigClass.ElectronicScale1);
		}

		return ElectronicScale.deviceEScale;
	}

	public ElectronicScale()
	{

	}

	public ElectronicScale(String name)
	{
		try
		{
			if ((name != null) && (name.trim().length() > 0))
			{
				Class cl = Class.forName(name);

				EScale = (Interface_ElectronicScale) cl.newInstance();
			}
			else
			{
				deviceEScale = new ElectronicScale();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			PosLog.getLog(getClass()).debug(ex);
			EScale = null;

			//new MessageBox("[" + name + "]\n\n电子秤设备对象不存在");
			new MessageBox(Language.apply("[{0}]\n\n电子秤设备对象不存在", new Object[]{name}));
		}
	}

	public boolean open()
	{
		if (EScale == null) { return false; }

		connect = EScale.open();

		return connect;
	}

	public void setEnable(boolean enable)
	{
		if (connect)
		{
			if (this.enable != enable)
			{
				EScale.setEnable(enable);
			}

			this.enable = enable;
		}
	}

	public void close()
	{
		if (connect)
		{
			setEnable(false);

			if (EScale == null)
				return;
			EScale.close();
			
		}
	}

	public boolean setPrice(double lsj)
	{
		if (enable && EScale != null)
			return EScale.sendData4(String.valueOf(lsj));
		return false;
	}

	public boolean setIgnorePeer()
	{
		if (enable && EScale != null)
			return EScale.sendData1("");
		return false;
	}

	public boolean startPolling()
	{
		if (enable && EScale != null)
			return EScale.sendData2("");
		return false;
	}

	public boolean run()
	{
		if (enable && EScale != null)
			return EScale.recvData3();
		return false;
	}

	public double getWeight()
	{
		if (enable && EScale != null)
			return EScale.getWeight();
		return 0;
	}

	public boolean getData()
	{
		if (enable && EScale != null)
			return EScale.recvData2();
		return false;
	}

	public boolean isValid()
	{
		if (EScale == null)
			return false;
		else
			return true;
	}

	public boolean stopPolling()
	{
		if (enable && EScale != null)
			return EScale.sendData3(null);

		return false;
	}

}
