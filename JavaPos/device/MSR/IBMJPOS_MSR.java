package device.MSR;

import java.util.Vector;

import jpos.JposException;
import jpos.MSR;
import jpos.MSRConst;
import jpos.events.DataEvent;
import jpos.events.DataListener;
import jpos.events.ErrorEvent;
import jpos.events.ErrorListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_MSR;
import com.efuture.javaPos.Global.Language;

public class IBMJPOS_MSR implements Interface_MSR, DataListener, ErrorListener
{
	private MSR msr;

	public boolean parseTrack(StringBuffer trackbuffer, StringBuffer track1, StringBuffer track2, StringBuffer track3)
	{
		return false;
	}

	public boolean open()
	{
		if (DeviceName.deviceMSR.length() <= 0)
		{
			return false;
		}

		msr = new MSR();

		try
		{
			msr.open(DeviceName.deviceMSR);

			return true;
		}
		catch (JposException e)
		{
			e.printStackTrace();
			new MessageBox(Language.apply("打开JPOS刷卡槽异常:\n") + e.getMessage());
		}

		return false;
	}

	public void close()
	{
		try
		{
			if (msr != null) msr.close();
		}
		catch (JposException e)
		{
			e.printStackTrace();
		}
	}

	public void setEnable(boolean enable)
	{
		try
		{
			if (enable)
			{
				if (!msr.getClaimed())
				{
					msr.claim(1000);
					//msr.setErrorReportingType(MSRConst.MSR_ERT_CARD);
					msr.setErrorReportingType(MSRConst.MSR_ERT_TRACK);
					msr.setDeviceEnabled(true);
					msr.setDataEventEnabled(true);
					msr.setDecodeData(true);
					msr.setParseDecodeData(true);
					msr.addDataListener(this);
					msr.addErrorListener(this);
				}
			}
			else
			{
				if (msr.getClaimed())
				{
					msr.removeDataListener(this);
					msr.removeErrorListener(this);
					msr.setDeviceEnabled(false);
					msr.setDataEventEnabled(false);
					msr.setDecodeData(false);
					msr.setParseDecodeData(false);
					msr.release();
				}
			}
		}
		catch (JposException e)
		{
			e.printStackTrace();
		}
	}

	public void dataOccurred(DataEvent arg0)
	{
		try
		{
			com.efuture.javaPos.Device.MSR.MSRTrack1 = "";
			com.efuture.javaPos.Device.MSR.MSRTrack2 = "";
			com.efuture.javaPos.Device.MSR.MSRTrack3 = "";

			com.efuture.javaPos.Device.MSR.MSRTrack1 = ManipulateStr.getDelCharInStr(new String(msr.getTrack1Data()), (char) 0x1B, 2);
			com.efuture.javaPos.Device.MSR.MSRTrack2 = ManipulateStr.getDelCharInStr(new String(msr.getTrack2Data()), (char) 0x1B, 2);
			com.efuture.javaPos.Device.MSR.MSRTrack3 = ManipulateStr.getDelCharInStr(new String(msr.getTrack3Data()), (char) 0x1B, 2);

			Display.getDefault().syncExec(new Runnable()
			{
				public void run()
				{
					if (Display.getCurrent() != null)
					{
						Control control = Display.getCurrent().getFocusControl();

						if (control.getClass().getName().equals("org.eclipse.swt.widgets.Text") && (control.getData() != null)
								&& ((String) control.getData()).equals("MSRINPUT"))
						{
							((Text) control).setText("***********************************");

							// 标记有刷卡数据
							com.efuture.javaPos.Device.MSR.havaMSR = true;

							// 发送按键通知键盘侦听
							Event event = new Event();

							event.widget = control;
							event.keyCode = 13;
							event.doit = true;
							event.data = "MSR";

							event.type = SWT.KeyDown;

							Display.getCurrent().post(event);

							event.type = SWT.KeyUp;

							Display.getCurrent().post(event);
						}
					}
				}
			});
			msr.setDataEventEnabled(true);
		}
		catch (Exception je)
		{
			je.printStackTrace();
		}
	}

	public void errorOccurred(ErrorEvent arg0)
	{
		// 将卡号解析出来作为文本输入		
		try
		{
			com.efuture.javaPos.Device.MSR.MSRTrack1 = "";
			com.efuture.javaPos.Device.MSR.MSRTrack2 = "";
			com.efuture.javaPos.Device.MSR.MSRTrack3 = "";

			com.efuture.javaPos.Device.MSR.MSRTrack1 = ManipulateStr.getDelCharInStr(new String(msr.getTrack1Data()), (char) 0x1B, 2);
			com.efuture.javaPos.Device.MSR.MSRTrack2 = ManipulateStr.getDelCharInStr(new String(msr.getTrack2Data()), (char) 0x1B, 2);
			com.efuture.javaPos.Device.MSR.MSRTrack3 = ManipulateStr.getDelCharInStr(new String(msr.getTrack3Data()), (char) 0x1B, 2);

			if ((com.efuture.javaPos.Device.MSR.MSRTrack2 == null) || (com.efuture.javaPos.Device.MSR.MSRTrack2.length() <= 0))
			{
				return;
			}

			Display.getDefault().syncExec(new Runnable()
			{
				public void run()
				{
					if (Display.getCurrent() != null)
					{
						Control control = Display.getCurrent().getFocusControl();

						if (control.getClass().getName().equals("org.eclipse.swt.widgets.Text") && (control.getData() != null)
								&& ((String) control.getData()).equals("MSRINPUT"))
						{
							((Text) control).setText("***********************************");

							// 标记有刷卡数据
							com.efuture.javaPos.Device.MSR.havaMSR = true;

							// 发送按键通知键盘侦听
							Event event = new Event();

							event.widget = control;
							event.keyCode = 13;
							event.doit = true;
							event.data = "MSR";

							event.type = SWT.KeyDown;

							Display.getCurrent().post(event);

							event.type = SWT.KeyUp;

							Display.getCurrent().post(event);
						}
					}
				}
			});
			msr.setDataEventEnabled(true);
		}
		catch (Exception je)
		{
			je.printStackTrace();
		}
	}

	public Vector getPara()
	{
		Vector v = new Vector();
		v.add(new String[] { Language.apply("JPOS逻辑名"), "MSR1" });
		return v;
	}

	public String getDiscription()
	{
		return Language.apply("IBM的JPOS驱动方式刷卡槽");
	}
}
