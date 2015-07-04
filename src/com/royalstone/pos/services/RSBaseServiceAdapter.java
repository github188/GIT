package com.royalstone.pos.services;

import java.util.Vector;

import jpos.config.JposEntry;

/**
 * @author  liangxinbiao
 * @see
 */
public class RSBaseServiceAdapter
	implements jpos.services.BaseService, jpos.JposConst {

	// Event management

	static private int EventCount;
	static private Vector event_list;

	// Jpos Properties

	private boolean AutoDisable;
	private int CapPowerReporting;
	private String CheckHealthText;
	private boolean Claimed;
	private int DataCount;
	private boolean DataEventEnabled;
	private boolean DeviceEnabled;
	private boolean FreezeEvents;
	private int OutputID;
	private int PowerNotify;
	private int PowerState;
	private int State;
	private String DeviceControlDescription;
	private int DeviceControlVersion = 1002000; //default
	private String DeviceServiceDescription;
	private int DeviceServiceVersion = 1002000; //default
	private String PhysicalDeviceDescription;
	private String PhysicalDeviceName;

	private jpos.services.EventCallbacks callbacks;

	private static String name;

	/**
	 * Constructor creates the device panel based on name.
	 */
	public RSBaseServiceAdapter(String device_name) {

		super();
		name = device_name;
		event_list = new Vector();

	}

	public void init(JposEntry entry) {
	}

	/** See JPOS */
	public void fireEvent(jpos.events.DataEvent ev) {
		if (callbacks != null)
			callbacks.fireDataEvent(ev);
		else
			System.out.println("No callbacks bound to Service!");
	}

	/** See JPOS */
	public void fireEvent(jpos.events.DirectIOEvent ev) {
		callbacks.fireDirectIOEvent(ev);
	}

	// Jpos Implementation

	/** See JPOS */
	public String getCheckHealthText() throws jpos.JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new jpos.JposException(JPOS_S_ERROR));
		}
		return CheckHealthText;
	}

	/** See JPOS */
	public void setCheckHealthText(String s) throws jpos.JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new jpos.JposException(JPOS_S_ERROR));
		}
		CheckHealthText = s;
	}

	/** See JPOS */
	public boolean getClaimed() throws jpos.JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new jpos.JposException(JPOS_S_ERROR));
		}
		return Claimed;
	}

	/** See JPOS */
	public void setClaimed(boolean b) throws jpos.JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new jpos.JposException(JPOS_S_ERROR));
		}
		Claimed = b;
	}

	/** See JPOS */
	public boolean getDeviceEnabled() throws jpos.JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new jpos.JposException(JPOS_S_ERROR));
		}
		return DeviceEnabled;
	}

	/** See JPOS */
	public void setDeviceEnabled(boolean b) throws jpos.JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new jpos.JposException(JPOS_S_ERROR));
		}
		DeviceEnabled = b;
	}

	/** See JPOS */
	public String getDeviceServiceDescription() throws jpos.JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new jpos.JposException(JPOS_S_ERROR));
		}
		return DeviceServiceDescription;
	}

	/** See JPOS */
	public void setDeviceServiceDescription(String s)
		throws jpos.JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new jpos.JposException(JPOS_S_ERROR));
		}
		DeviceServiceDescription = s;
	}

	/** See JPOS */
	public int getDeviceServiceVersion() throws jpos.JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new jpos.JposException(JPOS_S_ERROR));
		}
		return DeviceServiceVersion;
	}

	/** See JPOS */
	public void setDeviceServiceVersion(int v) throws jpos.JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new jpos.JposException(JPOS_S_ERROR));
		}
		DeviceServiceVersion = v;
	}

	/** See JPOS */
	public boolean getFreezeEvents() throws jpos.JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new jpos.JposException(JPOS_S_ERROR));
		}
		if (getState() == JPOS_S_ERROR) {
			throw (new jpos.JposException(JPOS_S_ERROR));
		}
		return FreezeEvents;
	}

	/** See JPOS */
	public void setFreezeEvents(boolean freezeEvents)
		throws jpos.JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new jpos.JposException(JPOS_S_ERROR));
		}
		FreezeEvents = freezeEvents;
	}

	/** See JPOS */
	public String getPhysicalDeviceDescription() throws jpos.JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new jpos.JposException(JPOS_S_ERROR));
		}
		return PhysicalDeviceDescription;
	}

	/** See JPOS */
	public void setPhysicalDeviceDescription(String s)
		throws jpos.JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new jpos.JposException(JPOS_S_ERROR));
		}
		PhysicalDeviceDescription = s;
	}

	/** See JPOS */
	public String getPhysicalDeviceName() throws jpos.JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new jpos.JposException(JPOS_S_ERROR));
		}
		return PhysicalDeviceName;
	}

	/** See JPOS */
	public void setPhysicalDeviceName(String s) throws jpos.JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new jpos.JposException(JPOS_S_ERROR));
		}
		PhysicalDeviceName = s;
	}

	/** See JPOS */
	public int getState() throws jpos.JposException {
		if (State == JPOS_S_ERROR) { // oops
			throw (new jpos.JposException(JPOS_S_ERROR));
		}
		return State;
	}

	/** See JPOS */
	public void setState(int s) throws jpos.JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new jpos.JposException(JPOS_S_ERROR));
		}
		State = s;
	}

	// Methods supported by all device services

	/** See JPOS */
	public void claim(int timeout) throws jpos.JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new jpos.JposException(JPOS_S_ERROR));
		}
	}

	/** See JPOS */
	public void close() throws jpos.JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new jpos.JposException(JPOS_S_ERROR));
		}
	}

	/** See JPOS */
	public void checkHealth(int level) throws jpos.JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new jpos.JposException(JPOS_S_ERROR));
		}
	}

	/** See JPOS */
	public void directIO(int command, int[] data, Object object)
		throws jpos.JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new jpos.JposException(JPOS_S_ERROR));
		}
	}

	public void open(String logicalName, jpos.services.EventCallbacks cb)
		throws jpos.JposException {
		/** See JPOS */

		callbacks = cb;

		if (getState() == JPOS_S_ERROR) {
			throw (new jpos.JposException(JPOS_S_ERROR));
		}
	}

	/** See JPOS */
	public void release() throws jpos.JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new jpos.JposException(JPOS_S_ERROR));
		}
	}
}
