package com.royalstone.pos.services;

import jpos.JposException;
import jpos.config.JposEntry;

/**
 * Service implementation of a pos cash drawer. Creates a
 * virtual cash drawer using JFC.
 *
 *
 * @author  Quentin Olson
 * @see
 */
public class POSCashDrawer
	extends RSBaseServiceAdapter
	implements jpos.loader.JposServiceInstance, jpos.services.CashDrawerService14 {

	private static boolean drawerOpen = false;

	private String driverName;
	private String portName;
	private String baudRate;
	private String byteSize;
	private String parity;
	private String stopBits;
	private String value1;
	private String value2;
	private String value3;
	private String value4;
	private String value5;

	public POSCashDrawer() {

		super("POSCashDrawer");

	}

	public void init(JposEntry entry) {
		driverName = (String) entry.getPropertyValue("driverName");
		portName = (String) entry.getPropertyValue("portName");
		baudRate = (String) entry.getPropertyValue("baudRate");
		byteSize = (String) entry.getPropertyValue("byteSize");
		parity = (String) entry.getPropertyValue("parity");
		stopBits = (String) entry.getPropertyValue("stopBits");
		value1 = (String) entry.getPropertyValue("value1");
		value2 = (String) entry.getPropertyValue("value2");
		value3 = (String) entry.getPropertyValue("value3");
		value4 = (String) entry.getPropertyValue("value4");
		value5 = (String) entry.getPropertyValue("value5");

		RSCashDrawer.driverInit(driverName);
		RSCashDrawer.paramInit(
			portName,
			baudRate,
			byteSize,
			parity,
			stopBits,
			value1,
			value2,
			value3,
			value4,
			value5);
	}

	// CashDrawerSevice12

	// Capabilities
	public boolean getCapStatus() throws JposException {
		return true;
	}

	// Properties
	public boolean getDrawerOpened() throws JposException {
		if (RSCashDrawer.drawerChk() == 1) {
			return true;
		}
		return false;
	}

	// Methods
	public void openDrawer() throws JposException {
		RSCashDrawer.drawerOpen();
	}

	public void waitForDrawerClose(
		int beepTimeout,
		int beepFrequency,
		int beepDuration,
		int beepDelay)
		throws JposException {
	}

	// CashDrawerSevice13

	public int getCapPowerReporting() throws JposException {
		return 0;
	}

	// Properties
	public int getPowerNotify() throws JposException {
		return 0;
	}

	public void setPowerNotify(int powerNotify) throws JposException {
		return;
	}

	public int getPowerState() throws JposException {
		return 0;
	}

	public void open(String logicalName, jpos.services.EventCallbacks cb)
		throws jpos.JposException {
		super.open(logicalName, cb);
		RSCashDrawer.drawerInit();
	}

	public void release() throws jpos.JposException {
		super.release();
		RSCashDrawer.drawerClose();
	}

	/* £¨·Ç Javadoc£©
	 * @see jpos.loader.JposServiceInstance#deleteInstance()
	 */
	public void deleteInstance() throws JposException {

	}

}
