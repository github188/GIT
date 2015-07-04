package com.royalstone.pos.services;

import jpos.JposException;
import jpos.config.JposEntry;

/**
 *
 * @author  Quentin Olson
 * @see
 */
public class CustDisplay
	extends RSBaseServiceAdapter
	implements jpos.loader.JposServiceInstance, jpos.services.LineDisplayService14 {
	
	int current_row;
	int current_col;
	int rows;
	int cols;

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
		
		RSVFD.driverInit(driverName);
		RSVFD.paramInit(
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

	public CustDisplay() {

		super("OperDisplay");

		current_row = 0;
		current_col = 0;

	}

	public void setText(String s) {
		//
	}

	// 1.2
	public int getCapBlink() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return 0;
	}

	public boolean getCapBrightness() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return true;
	}

	public int getCapCharacterSet() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return 0;
	}

	public boolean getCapDescriptors() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return true;
	}

	public boolean getCapHMarquee() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return true;
	}

	public boolean getCapICharWait() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return true;
	}

	public boolean getCapVMarquee() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return true;
	}

	// Properties
	public int getCharacterSet() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return 0;
	}

	public void setCharacterSet(int characterSet) throws JposException {
	}

	public String getCharacterSetList() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return "";
	}

	public int getColumns() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return cols;
	}

	public int getCurrentWindow() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return 0;
	}

	public void setCurrentWindow(int currentWindow) throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
	}

	public int getCursorColumn() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return current_col;
	}

	public void setCursorColumn(int cursorColumn) throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		current_col = cursorColumn; // check bounds!!!
	}

	public int getCursorRow() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return current_row;
	}

	public void setCursorRow(int cursorRow) throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		current_row = cursorRow; // check bounds!!!
	}

	public boolean getCursorUpdate() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return true;
	}

	public void setCursorUpdate(boolean cursorUpdate) throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
	}

	public int getDeviceBrightness() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return 0;
	}

	public void setDeviceBrightness(int deviceBrightness)
		throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
	}

	public int getDeviceColumns() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return cols;
	}

	public int getDeviceDescriptors() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return 0;
	}

	public int getDeviceRows() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return rows;
	}

	public int getDeviceWindows() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return 0;
	}

	public int getInterCharacterWait() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return 0;
	}

	public void setInterCharacterWait(int interCharacterWait)
		throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
	}

	public int getMarqueeFormat() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return 0;
	}

	public void setMarqueeFormat(int marqueeFormat) throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
	}

	public int getMarqueeRepeatWait() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return 0;
	}

	public void setMarqueeRepeatWait(int marqueeRepeatWait)
		throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
	}

	public int getMarqueeType() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return 0;
	}

	public void setMarqueeType(int marqueeType) throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
	}

	public int getMarqueeUnitWait() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return 0;
	}

	public void setMarqueeUnitWait(int marqueeUnitWait) throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
	}

	public int getRows() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return rows;
	}

	// Methods
	public void clearDescriptors() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
	}

	public void clearText() throws JposException {
        if (getState() == JPOS_S_ERROR) {
            throw (new JposException(JPOS_S_ERROR));
        }
        RSVFD.vfdClear();
	}

	public void createWindow(
		int viewportRow,
		int viewportColumn,
		int viewportHeight,
		int viewportWidth,
		int windowHeight,
		int windowWidth)
		throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
	}

	public void destroyWindow() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
	}

	public void displayText(String data, int attribute) throws JposException {

		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}

		try {
			RSVFD.vfdPrint(data.getBytes("GB2312"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void displayTextAt(int row, int column, String data, int attribute)
		throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		try{
		if(attribute==1)
			RSVFD.vfdWelcome("wel".getBytes());
		if(attribute==2)
			RSVFD.vfdPrintReturn(data.getBytes());
		if(attribute==3)
			RSVFD.vfdPrintTotal(data.getBytes());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void refreshWindow(int window) throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
	}

	public void scrollText(int direction, int units) throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
	}

	public void setDescriptor(int descriptor, int attribute)
		throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
	}

	// 1.3 Capabilities
	public int getCapPowerReporting() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return 0;
	}

	// Properties
	public int getPowerNotify() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return 0;
	}

	public void setPowerNotify(int powerNotify) throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
	}

	public int getPowerState() throws JposException {
		if (getState() == JPOS_S_ERROR) {
			throw (new JposException(JPOS_S_ERROR));
		}
		return 0;
	}

	public void open(String logicalName, jpos.services.EventCallbacks cb)
		throws jpos.JposException {
		super.open(logicalName, cb);
		RSVFD.vfdInit(null);
	}
	/* £¨·Ç Javadoc£©
	 * @see jpos.loader.JposServiceInstance#deleteInstance()
	 */

	public void deleteInstance() throws JposException {
	}
}
