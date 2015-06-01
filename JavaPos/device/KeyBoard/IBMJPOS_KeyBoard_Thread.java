package device.KeyBoard;


import jpos.JposException;

import com.efuture.commonKit.MessageBox;

public class IBMJPOS_KeyBoard_Thread extends Thread
{
	private String logicname;
	jpos.POSKeyboard keyBoard;
	
	public IBMJPOS_KeyBoard_Thread(jpos.POSKeyboard keyboard, String logicname)
	{
		this.logicname = logicname;
		this.keyBoard = keyboard;
	}

	public void run()
	{
		int retry_times = 0;
		
		try
		{
			while(true)
			{
				sleep(5000);
				
				if (retry_times >= 120)
				{
					throw new Exception("Too many retry for keyboard");
				}
				
				try
				{
					int state = keyBoard.getState();
					if (state == jpos.POSKeyboard.JPOS_S_CLOSED)
					{
						keyBoard.open(logicname);
					}
					boolean claimed = keyBoard.getClaimed();
					if (!claimed)
					{
						keyBoard.claim(1000);
					}
					boolean enabled = keyBoard.getDeviceEnabled();
					if (!enabled)
					{
						keyBoard.setDeviceEnabled(true);
					}
					boolean dataEnable= keyBoard.getDataEventEnabled();
					if (!dataEnable)
					{
						keyBoard.setDataEventEnabled(true);
					}
					
					retry_times = 0;
				}
				catch(JposException e)
				{
					retry_times++;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			new MessageBox("打开JPOS键盘异常:\n" + e.getMessage());
		}
	}
}