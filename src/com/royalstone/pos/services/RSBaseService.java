/*
 * Filename: RSBaseService.java
 * Created on 2004-1-28
 * @author wangcr
 */
package com.royalstone.pos.services;

import jpos.JposConst;
import jpos.JposException;
import jpos.events.*;
import jpos.services.EventCallbacks;

/**
 * Type name: RSBaseService
 */
public class RSBaseService implements JposConst {


	protected RSBaseService(String s)
	{
		this(s, false);
	}

	protected RSBaseService(String s, boolean flag)
	{
		checkHealthText = "";
		claimed = false;
		deviceEnabled = false;
		freezeEvents = false;
		physicalDeviceDescription = "";
		physicalDeviceName = "";
		state = 1;
		dataEventEnabled = false;
		dataCount = 0;
		autoDisable = false;
		outputID = 0;
		capPowerReporting = 0;
		powerNotify = 0;
		powerState = 2000;
		callbacks = null;
		withEventQueue = false;
		Queue = null;
		withEventQueue = flag;
	}

	protected void checkEvents()
	{
		if(Queue == null)
			return;
		synchronized(Queue)
		{
			Queue.notify();
		}
	}

	protected void clearAllEvents()
	{
		if(Queue == null)
			return;
		synchronized(Queue.eventObjects)
		{
			Queue.eventObjects.removeAllElements();
			Queue.eventEvents.removeAllElements();
		}
	}

	protected void clearInputEvents()
	{
		if(Queue == null)
			return;
		synchronized(Queue.eventObjects)
		{
			int i = Queue.eventEvents.getSize();
			for(int j = 0; j < i;)
			{
				JposEvent jposevent = (JposEvent)Queue.eventEvents.peekElement(j);
				boolean flag = false;
				if(jposevent instanceof DataEvent)
					flag = true;
				if(jposevent instanceof ErrorEvent)
				{
					ErrorEvent errorevent = (ErrorEvent)jposevent;
					if(errorevent.getErrorLocus() == 2 || errorevent.getErrorLocus() == 3)
						flag = true;
				}
				if(flag)
				{
					Queue.eventEvents.removeElement(jposevent);
					Object obj = Queue.eventObjects.peekElement(j);
					Queue.eventObjects.removeElement(obj);
					i--;
				} else
				{
					j++;
				}
			}

		}
	}

	protected void clearOutputEvents()
	{
		if(Queue == null)
			return;
		synchronized(Queue.eventObjects)
		{
			int i = Queue.eventEvents.getSize();
			for(int j = 0; j < i;)
			{
				JposEvent jposevent = (JposEvent)Queue.eventEvents.peekElement(j);
				boolean flag = false;
				if(jposevent instanceof OutputCompleteEvent)
					flag = true;
				if(jposevent instanceof ErrorEvent)
				{
					ErrorEvent errorevent = (ErrorEvent)jposevent;
					if(errorevent.getErrorLocus() == 1)
						flag = true;
				}
				if(flag)
				{
					Queue.eventEvents.removeElement(jposevent);
					Object obj = Queue.eventObjects.peekElement(j);
					Queue.eventObjects.removeElement(obj);
					i--;
				} else
				{
					j++;
				}
			}

		}
	}

	static int compareStringPattern(byte abyte0[], byte abyte1[], int i, int j)
	{
		int k = 0;
		int l = 0;
		do
		{
			if(k >= j)
				return l < abyte0.length ? 0 : 1;
			if(l >= abyte0.length)
				return 0;
			byte byte0 = abyte0[l];
			if(byte0 == 42)
				return 1;
			byte byte1 = abyte1[i + k];
			if(byte0 == 46)
				l++;
			else
			if(byte0 == 91)
			{
				boolean flag = false;
				boolean flag1 = true;
				int i1 = -1;
				for(int j1 = l; j1 < abyte0.length; j1++)
				{
					if(abyte0[j1] != 93)
						continue;
					i1 = j1;
					break;
				}

				if(i1 < 0)
					return -l;
				if((byte0 = abyte0[l + 1]) == 94)
				{
					flag1 = false;
					l++;
				}
				for(; l < i1; l++)
				{
					byte0 = abyte0[l];
					byte byte2;
					if((byte2 = abyte0[l + 1]) == 45)
					{
						l += 2;
						byte byte3 = abyte0[l];
						if(byte1 < byte0 || byte1 > byte3)
							continue;
						flag = true;
						break;
					}
					if(byte0 != byte1)
						continue;
					flag = true;
					break;
				}

				l = i1;
				if(flag1 && flag || !flag1 && !flag)
					l++;
				else
					return 0;
			} else
			if(byte0 == byte1)
				l++;
			else
				return 0;
			k++;
		} while(true);
	}

	protected boolean eventQueueIsFull()
	{
		if(Queue == null)
			return true;
		boolean flag = false;
		synchronized(Queue.eventObjects)
		{
			boolean flag1 = Queue.eventObjects.isFull();
			return flag1;
		}
	}

	public boolean getAutoDisable()
		throws JposException
	{
		return returnGetBooleanProperties("getAutoDisable()", autoDisable);
	}

	public int getCapPowerReporting()
		throws JposException
	{
		return returnGetIntProperties("getCapPowerReporting()", capPowerReporting);
	}

	public String getCheckHealthText()
		throws JposException
	{
		return returnGetStringProperties("getCheckHealthText()", checkHealthText);
	}

	public boolean getClaimed()
		throws JposException
	{
		return returnGetBooleanProperties("getClaimed()", claimed);
	}

	public int getDataCount()
		throws JposException
	{
		return returnGetIntProperties("getDataCount()", dataCount);
	}

	public boolean getDataEventEnabled()
		throws JposException
	{
		return returnGetBooleanProperties("getDataEventEnabled()", dataEventEnabled);
	}

	public boolean getDeviceEnabled()
		throws JposException
	{
		return returnGetBooleanProperties("getDeviceEnabled()", deviceEnabled);
	}

	protected String getDeviceServiceDescription()
		throws JposException
	{
		String s = "Wincor Nixdorf JavaPOS Base Device Service,";
		int i = "$Revision: 1.1 $".indexOf(' ');
		int j = "$Revision: 1.1 $".lastIndexOf(' ');
		if(i >= 0 && j > i)
			s = s + "version 1.5." + "$Revision: 1.1 $".substring(i, j).trim();
		i = "$Modtime:: 18.12.:1 16:53 $".indexOf(' ');
		j = "$Modtime:: 18.12.:1 16:53 $".lastIndexOf(' ');
		if(i >= 0 && j > i)
		{
			String s1 = "$Modtime:: 18.12.:1 16:53 $".substring(i, j).trim();
			i = s1.indexOf(".:");
			if(i >= 0)
				s1 = s1.substring(0, i) + ".0" + s1.substring(i + 2);
			s = s + " from " + s1;
		}
		return s;
	}

	public boolean getFreezeEvents()
		throws JposException
	{
		return returnGetBooleanProperties("getFreezeEvents()", freezeEvents);
	}

	public int getOutputID()
		throws JposException
	{
		return returnGetIntProperties("getOutputID()", outputID);
	}

	public String getPhysicalDeviceDescription()
		throws JposException
	{
		return returnGetStringProperties("getPhysicalDeviceDescription()", physicalDeviceDescription);
	}

	public String getPhysicalDeviceName()
		throws JposException
	{
		return returnGetStringProperties("getPhysicalDeviceName()", physicalDeviceName);
	}

	public int getPowerNotify()
		throws JposException
	{
		return returnGetIntProperties("getPowerNotify()", powerNotify);
	}

	public int getPowerState()
		throws JposException
	{
		return returnGetIntProperties("getPowerState()", powerState);
	}

	public int getState()
		throws JposException
	{
		return returnGetIntProperties("getState()", state);
	}

	protected void postDataEvent(Object obj, DataEvent dataevent)
	{
	}

	protected void postErrorEvent(Object obj, ErrorEvent errorevent)
	{
	}

	protected void postOutputCompleteEvent(Object obj, OutputCompleteEvent outputcompleteevent)
	{
	}

	protected void postStatusUpdateEvent(Object obj, StatusUpdateEvent statusupdateevent)
	{
	}

	protected boolean preDataEvent(Object obj, DataEvent dataevent)
	{
		return true;
	}

	protected boolean preErrorEvent(Object obj, ErrorEvent errorevent)
	{
		return true;
	}

	protected boolean preOutputCompleteEvent(Object obj, OutputCompleteEvent outputcompleteevent)
	{
		return true;
	}

	protected boolean preStatusUpdateEvent(Object obj, StatusUpdateEvent statusupdateevent)
	{
		return true;
	}

	protected boolean putEvent(JposEvent jposevent, Object obj)
	{
		if(Queue == null)
			return false;
		boolean flag = false;
		synchronized(Queue.eventObjects)
		{
			Queue.eventObjects.putElement(obj);
			flag = Queue.eventEvents.putElement(jposevent);

		}
		checkEvents();
		return flag;
	}

	protected boolean returnGetBooleanProperties(String s, boolean flag)
		throws JposException
	{
		return flag;
	}

	protected byte[] returnGetByteArrayProperties(String s, byte abyte0[], int i)
		throws JposException
	{
		byte abyte1[] = new byte[i];
		if(i > 0)
			System.arraycopy(abyte0, 0, abyte1, 0, i);
		return abyte1;
	}

	protected byte[] returnGetByteArrayProperties(String s, byte abyte0[], int i, int j)
		throws JposException
	{
		byte abyte1[] = new byte[i];
		if(i > 0)
			if(j == 0)
			{
				System.arraycopy(abyte0, 0, abyte1, 0, i);
			} else
			{
				for(int k = 0; k < i; k++)
					abyte1[k] = (byte)(abyte0[k] + j);

			}
		return abyte1;
	}

	protected int returnGetIntProperties(String s, int i)
		throws JposException
	{

		return i;
	}

	protected String returnGetStringProperties(String s, String s1)
		throws JposException
	{

		return s1;
	}

	protected void startEventThread()
	{
		startEventThread(null);
	}

	protected void startEventThread(String s)
	{
		if(!withEventQueue)
			return;
		Queue = new RSBaseServiceEventQueue(this);
		if(s != null)
			Queue.setName(s);
		Queue.threadShouldFinish = false;
		Queue.start();
	}

	protected void startEventThread(String s, int i)
	{
		if(!withEventQueue)
			return;
		Queue = new RSBaseServiceEventQueue(this, i);
		if(s != null)
			Queue.setName(s);
		Queue.threadShouldFinish = false;
		Queue.start();
	}

	protected void stopEventThread()
	{
		if(!withEventQueue)
			return;
		if(Queue != null)
		{
			Queue.threadShouldFinish = true;
			synchronized(Queue)
			{
				Queue.notify();
			}
			try
			{
				Queue.join();
			}
			catch(InterruptedException _ex) { }
			Queue = null;
		}
	}

	protected void traceAndThrowExceptionFromDCAL(JposException jposexception, String s)
		throws JposException
	{
		traceAndThrowJposException(new JposException(jposexception.getErrorCode(), jposexception.getErrorCodeExtended(), "DCAL error:" + s + ":" + jposexception.getMessage()));
	}

	protected void traceAndThrowJposException(JposException jposexception)
		throws JposException
	{

		throw jposexception;
	}

	protected static String transformFromByteArray(byte abyte0[])
	{
		if(abyte0 == null)
			return "<null>";
		else
			return transformFromByteArray(abyte0, 0, abyte0.length);
	}

	protected static String transformFromByteArray(byte abyte0[], int i, int j)
	{
		StringBuffer stringbuffer = new StringBuffer();
		if(abyte0 == null)
			return "<null>";
		for(int k = i; k < i + j; k++)
			if(abyte0[k] > 32)
			{
				stringbuffer.append((char)abyte0[k]);
			} else
			{
				stringbuffer.append("\\x");
				String s = Integer.toHexString(abyte0[k]);
				if(s.length() == 1)
					stringbuffer.append("0");
				stringbuffer.append(s);
			}

		return stringbuffer.toString();
	}

	protected static byte[] transformToByteArray(String s)
	{
		byte abyte0[] = new byte[s.length()];
		int i = 0;
		for(int j = 0; j < s.length() && i < abyte0.length; j++)
			if(s.charAt(j) != '\\')
				abyte0[i++] = (byte)s.charAt(j);
			else
			if(j + 3 < abyte0.length && s.charAt(j + 1) == 'x')
			{
				char c = s.charAt(j + 2);
				char c1 = s.charAt(j + 3);
				j += 3;
				int k = 0;
				if(c <= '9' && c >= '0')
					k = c - 48;
				else
				if(c <= 'f' && c >= 'a')
					k = (c + 10) - 97;
				else
				if(c <= 'F' && c >= 'A')
					k = (c + 10) - 65;
				k *= 16;
				if(c1 <= '9' && c1 >= '0')
					k += c1 - 48;
				else
				if(c1 <= 'f' && c1 >= 'a')
					k += (c1 + 10) - 97;
				else
				if(c1 <= 'F' && c1 >= 'A')
					k += (c1 + 10) - 65;
				abyte0[i++] = (byte)k;
			}

		byte abyte1[] = new byte[i];
		System.arraycopy(abyte0, 0, abyte1, 0, i);
		return abyte1;
	}

	protected String checkHealthText;
	protected boolean claimed;
	protected boolean deviceEnabled;
	protected boolean freezeEvents;
	protected String physicalDeviceDescription;
	protected String physicalDeviceName;
	protected int state;
	protected boolean dataEventEnabled;
	protected int dataCount;
	protected boolean autoDisable;
	protected int outputID;
	protected int capPowerReporting;
	protected int powerNotify;
	protected int powerState;
	protected EventCallbacks callbacks;
	private boolean withEventQueue;
	private RSBaseServiceEventQueue Queue;


}
