package device.MSR;


import java.util.Vector;

import com.efuture.javaPos.Device.Interface.Interface_MSR;
import com.efuture.javaPos.Global.Language;

public class IBM4614KeyBoard_MSR implements Interface_MSR
{
	public void close()
	{
	}

	public boolean open()
	{
		return true;
	}

	public void setEnable(boolean enable)
	{
	}

	public boolean parseTrack(StringBuffer trackbuffer, StringBuffer track1, StringBuffer track2, StringBuffer track3)
	{
/*		
		// 发出的回车键不触发keyReleased事件,因此主动发出一次
		String trackstr = trackbuffer.toString();
        if (trackstr.endsWith("?") || trackstr.endsWith("/"))
		{
            Display.getDefault().syncExec(new Runnable()
            {
                public void run()
                {
					Control control = Display.getCurrent().getFocusControl();
					Event event = new Event();
					event.widget  = control;
					event.keyCode = 13;
					event.doit = true;
					//event.type = SWT.KeyDown;
					//Display.getCurrent().post(event);
					event.type = SWT.KeyUp;
					Display.getCurrent().post(event);
                }
            });
		}
*/		
		return false;
	}
	
	public Vector getPara() 
	{
		return null;
	}

	public String getDiscription() 
	{
		return Language.apply("IBM4614键盘口刷卡槽");
	}
}
