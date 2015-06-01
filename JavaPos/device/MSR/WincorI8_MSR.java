package device.MSR;


import java.util.Vector;

import com.efuture.javaPos.Device.Interface.Interface_MSR;
import com.efuture.javaPos.Global.Language;

public class WincorI8_MSR implements Interface_MSR
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
		boolean exit = false;
		int i=0;
		
		// 先找到磁道标记
		for(i=0;i<trackbuffer.length();i++) if (trackbuffer.charAt(i) == 'c') break;
		if (i >= trackbuffer.length()) return false;
		i++;
		
		while(!exit)
		{
			if (i >= trackbuffer.length()) break;
			
			switch(trackbuffer.charAt(i))
			{
				case '1':
					i = dumpTrack(trackbuffer,track1,i);
					break;
				case '2':
					i = dumpTrack(trackbuffer,track2,i);
					break;
				case '3':
					i = dumpTrack(trackbuffer,track3,i);
					break;
				default:
					exit = true;
					break;
			}
			
			//
			i++;
		}
		
		return true;
	}
	
	private int dumpTrack(StringBuffer trackbuffer, StringBuffer track,int index)
	{
		int i = index + 1;

		if (i >= trackbuffer.length()) return i;
		
		switch(trackbuffer.charAt(i))
		{
			case '0':
				i++;
				while(trackbuffer.charAt(i) != '?' && trackbuffer.charAt(i) != '/' && trackbuffer.charAt(i) != '\r' && trackbuffer.charAt(i) != '\n')
				{
					track.append(trackbuffer.charAt(i));
					i++;
				}
				i += 2;
				break;
			case '1':
			case '2':
				i += 2;
				break;
			default:
				break;
		}
		
		return i;
	}

	public Vector getPara() {
		return null;
	}

	public String getDiscription() 
	{
		return Language.apply("WincorI8款机键盘口刷卡槽");
	}
	
	
}
