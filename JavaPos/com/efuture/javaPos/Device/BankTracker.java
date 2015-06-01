package com.efuture.javaPos.Device;

import org.eclipse.swt.widgets.Display;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Interface.Interface_BankTracker;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;

public class BankTracker
{
	public static BankTracker deviceBankTracker = null;
	private Interface_BankTracker tracker = null;
	private boolean connect;
	private boolean enable;

	public static String isAutoMSR = "";
	public static int interval = 50;
	public static boolean initFlag = false;
	public static String trackerConfig = "";

	public static BankTracker getDefault()
	{
		if (BankTracker.deviceBankTracker == null)
		{
			BankTracker.deviceBankTracker = new BankTracker(ConfigClass.BankTracker1);
		}

		return BankTracker.deviceBankTracker;
	}

	public String getTracker()
	{
		return tracker.getTracker();
	}

	public BankTracker()
	{

	}

	public BankTracker(String name)
	{
		try
		{
			if ((name != null) && (name.trim().length() > 0))
			{
				Class cl = Class.forName(name);

				tracker = (Interface_BankTracker) cl.newInstance();
			}
			else
			{
				deviceBankTracker = new BankTracker();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			PosLog.getLog(getClass()).debug(ex);
			tracker = null;

			//new MessageBox("[" + name + "]\n\n设备不存在");
			new MessageBox(Language.apply("[{0}]\n\n设备不存在", new Object[]{name}));
		}
	}

	public boolean isValid()
	{
		if (tracker == null)
			return false;
		return true;
	}

	public void setEnable(boolean enable)
	{
		this.enable = enable;
	}

	public boolean open()
	{
		if (tracker == null)
			return false;

		connect = tracker.open();
		return connect;
	}

	public boolean close()
	{
		if (tracker == null) { return false; }

		return tracker.close();
	}

	public static void autoMSR()
	{
		if (DeviceName.deviceICCard != null && DeviceName.deviceICCard.indexOf("|") != -1)
			autoIC();
		else if (DeviceName.deviceBankTracker != null && DeviceName.deviceBankTracker.indexOf("|") != -1)
			autoBank();
	}

	private static void autoIC()
	{
		try
		{
			if (!initFlag)
			{
				if (DeviceName.deviceICCard.length() > 0 && DeviceName.deviceICCard.indexOf(",") > 0)
				{
					String[] para = DeviceName.deviceICCard.split("\\|");
					if (para != null && para.length > 0)
					{
						String[] timeInterval = para[0].split(",");
						{
							if (timeInterval != null && timeInterval.length > 0)
								isAutoMSR = timeInterval[0].trim();
							if (timeInterval != null && timeInterval.length > 1)
								interval = Integer.parseInt(timeInterval[1].trim());
							initFlag = true;
						}
					}

				}
			}

			if (isAutoMSR.equals("Y") && initFlag)
			{
				Display.getDefault().syncExec(new Runnable()
				{
					public void run()
					{
						try
						{
							Thread.sleep(interval);
							NewKeyListener.sendKey(GlobalVar.ICInput);
						}
						catch (Exception ex)
						{

						}
					}
				});
			}
		}
		catch (Exception ex)
		{
			initFlag = false;
			ex.printStackTrace();
		}

	}

	private static void autoBank()
	{
		try
		{
			if (!initFlag)
			{
				if (DeviceName.deviceBankTracker.length() > 0 && DeviceName.deviceBankTracker.indexOf(",") > 0)
				{
					String[] para = DeviceName.deviceICCard.split("\\|");
					if (para != null && para.length > 0)
					{
						String[] timeInterval = para[0].split(",");
						{
							if (timeInterval != null && timeInterval.length > 0)
								isAutoMSR = timeInterval[0].trim();
							if (timeInterval != null && timeInterval.length > 1)
								interval = Integer.parseInt(timeInterval[1].trim());
							initFlag = true;
						}
					}

				}
			}

			if (isAutoMSR.equals("Y") && initFlag)
			{
				Display.getDefault().syncExec(new Runnable()
				{
					public void run()
					{
						try
						{
							Thread.sleep(interval);
							NewKeyListener.sendKey(GlobalVar.BankTracker);
						}
						catch (Exception ex)
						{

						}
					}
				});
			}
		}
		catch (Exception ex)
		{
			initFlag = false;
			ex.printStackTrace();
		}

	}
}
