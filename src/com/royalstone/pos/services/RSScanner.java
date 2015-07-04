/*
 * Filename: RSScanner.java
 * Created on 2004-1-18
 * @author wangcr
 */
package com.royalstone.pos.services;

import jpos.JposConst;
import jpos.JposException;
import jpos.ScannerConst;
import jpos.services.EventCallbacks;
import jpos.services.ScannerService17;
import jpos.events.*;

import javax.comm.*;
import java.io.*;
import java.util.*;

public class RSScanner
	extends RSBaseService
	implements ScannerService17, JposConst, ScannerConst, SerialPortEventListener {

	private InputStream inCOM;
	private OutputStream outCOM;
	private SerialPort COMs;
	private String portNameCOM;
	private OSServiceConfiguration OSSC;
	private int baudrate;
	private int bits;
	private int stopbits;
	private int parity;
	private int flowControl;
	private int flagSetRTS;
	private int flagSetDTR;
	private int flagDSRControl;
	private boolean isShared;
	private boolean isAsyncThread;
	int sharedFlag;
	private boolean restoreError;
	private String usesName;
	private int openRef;
	private int claimRef;
	private int enableRef;
	private static Vector allSimpleCommDevices = new Vector();

	protected static final int SCAN_MAX_BYTES = 100;
	private static final byte SCAN_HEADER_STX = 2;
	private static final byte SCAN_TRAILER_ETX = 3;
	private static final byte SCAN_TRAILER_LF = 10;
	private static final byte SCAN_TRAILER_CR = 13;
	private static final byte ZEROBYTEARRAY[] = new byte[0];
	protected boolean decodeData;
	protected byte scanData[];
	protected byte scanDataLabel[];
	protected int scanDataType;
	protected boolean opened;
	protected boolean queuingMode;
	protected boolean dsrControl;
	protected boolean scannerModeB;
	protected byte controlStrings[][];
	private static final int CTL_ENABLE = 0;
	private static final int CTL_DISABLE = 1;
	private static final int CTL_RESET = 2;
	protected String errorText;
	protected String errorTextextended;
	protected int errorCode;
	protected OSServiceConfiguration serviceConfiguration;
	protected byte internalScanDataBuffer[];
	private int internalScanDataBufferLength;
	private int internalScanDataTrailerStartIdx;
	private int internalScanDataTrailerStartLen;
	private String logicalname;
	byte headerStrings[][] = { { 0x02 }
	};
//	byte trailerStrings[][] = { { 3 }, {
//			13, 10 }, {
//			13 }, {
//			10 }
//	};
	
	byte trailerStrings[][] = { { -0x7D }
	};

	public RSScanner(String s) {
		this(s, true);
	}

	protected RSScanner(String s, boolean flag) {
		super(s, flag);

		//--
		portNameCOM = "";
		OSSC = null;
		flagSetRTS = 2;
		flagSetDTR = 2;
		flagDSRControl = 2;
		isShared = false;
		isAsyncThread = false;
		COMs = null;
		inCOM = null;
		outCOM = null;
		restoreError = false;
		sharedFlag = 0;
		for (StringTokenizer stringtokenizer = new StringTokenizer(s, ",");
			stringtokenizer.hasMoreTokens();
			) {
			String s1 = stringtokenizer.nextToken().trim();
			if (s1.equalsIgnoreCase("shared"))
				isShared = true;
			else if (s1.equalsIgnoreCase("asyncRead"))
				isAsyncThread = true;
		}

		//--
		scanData = ZEROBYTEARRAY;
		scanDataLabel = ZEROBYTEARRAY;
		initializeMembers();
	}

	public void deleteInstance() throws JposException {
		// keep empty

	}

	/* 
	 * @see jpos.services.ScannerService13#getCapPowerReporting()
	 */
	//	public int getCapPowerReporting() throws JposException {
	//		
	//		return 0;
	//	}

	/* 
	 * @see jpos.services.ScannerService13#getPowerNotify()
	 */
	//	public int getPowerNotify() throws JposException {
	//		
	//		return 0;
	//	}

	/* 
	 * @see jpos.services.ScannerService13#setPowerNotify(int)
	 */
	public void setPowerNotify(int powerNotify) throws JposException {
		if (super.deviceEnabled)
			traceAndThrowJposException(
				new JposException(106, "device is enabled"));
		if (super.capPowerReporting == 0 && powerNotify != 0)
			traceAndThrowJposException(
				new JposException(106, "illegal PowerNotify"));
		super.powerNotify = powerNotify;

	}

	/* 
	 * @see jpos.services.ScannerService13#getPowerState()
	 */
	//	public int getPowerState() throws JposException {
	//		
	//		return 0;
	//	}

	/* 
	 * @see jpos.services.ScannerService12#getAutoDisable()
	 */
	//	public boolean getAutoDisable() throws JposException {
	//		
	//		return false;
	//	}

	/* 
	 * @see jpos.services.ScannerService12#setAutoDisable(boolean)
	 */
	public void setAutoDisable(boolean autoDisable) throws JposException {
		super.autoDisable = autoDisable;

	}

	/* 
	 * @see jpos.services.ScannerService12#getDataCount()
	 */
	//	public int getDataCount() throws JposException {
	//		
	//		return 0;
	//	}

	/* 
	 * @see jpos.services.ScannerService12#getDataEventEnabled()
	 */
	//	public boolean getDataEventEnabled() throws JposException {
	//		
	//		return false;
	//	}

	/* 
	 * @see jpos.services.ScannerService12#setDataEventEnabled(boolean)
	 */
	public void setDataEventEnabled(boolean flag) throws JposException {
		if (super.dataEventEnabled == flag)
			return;
		boolean flag1 = !queuingMode && super.deviceEnabled;
		if (flag) {
			if (flag1)
				try {

					if (!isEnabled()) {
						_enable();
						if (isControlStringSet(0))
							_write(
								controlStrings[0],
								0,
								controlStrings[0].length,
								1000);
					}
				} catch (JposException jposexception) {
					traceAndThrowExceptionFromDCAL(
						jposexception,
						"can't set DataEventEnabled to true");
				}
			super.dataEventEnabled = flag;
			checkEvents();
		} else {
			if (flag1) {
				if (isEnabled()) {
					if (isControlStringSet(1))
						_write(
							controlStrings[1],
							0,
							controlStrings[1].length,
							1000);
					_disable();
				}
			}
			super.dataEventEnabled = flag;
		}

	}

	/* 
	 * @see jpos.services.ScannerService12#getDecodeData()
	 */
	public boolean getDecodeData() throws JposException {

		return returnGetBooleanProperties("getDecodeData()", decodeData);
	}

	/* 
	 * @see jpos.services.ScannerService12#setDecodeData(boolean)
	 */
	public void setDecodeData(boolean flag) throws JposException {

		decodeData = flag;

	}

	/* 
	 * @see jpos.services.ScannerService12#getScanData()
	 */
	public byte[] getScanData() throws JposException {
		return returnGetByteArrayProperties(
			"getScanData()",
			scanData,
			scanData.length);
	}

	/* 
	 * @see jpos.services.ScannerService12#getScanDataLabel()
	 */
	public byte[] getScanDataLabel() throws JposException {
		byte abyte0[] = scanDataLabel;
		if (!decodeData)
			abyte0 = ZEROBYTEARRAY;
		return returnGetByteArrayProperties(
			"getScanDataLabel()",
			abyte0,
			abyte0.length);
	}

	/* 
	 * @see jpos.services.ScannerService12#getScanDataType()
	 */
	public int getScanDataType() throws JposException {
		int i = scanDataType;
		if (!decodeData)
			i = 0;
		return returnGetIntProperties("getScanDataType()", i);
	}

	/* 
	 * @see jpos.services.ScannerService12#clearInput()
	 */
	public void clearInput() throws JposException {
		try {
			_flush(1000);
		} catch (JposException jposexception) {
			traceAndThrowExceptionFromDCAL(jposexception, "can't flush");
		}
		super.dataCount = 0;
		scanData = ZEROBYTEARRAY;
		scanDataLabel = ZEROBYTEARRAY;
		scanDataType = 0;
		clearInputEvents();
		internalScanDataBufferLength = 0;
		internalScanDataTrailerStartIdx = -1;
		internalScanDataTrailerStartLen = 0;
		if (isControlStringSet(2))
			if (!isEnabled())
				try {
					_enable();
					_write(
						controlStrings[2],
						0,
						controlStrings[2].length,
						1000);
					_disable();
				} catch (JposException jposexception1) {
					traceAndThrowExceptionFromDCAL(
						jposexception1,
						"clearInput: can't enable/write/disable dcal");
				} else
				try {
					_write(
						controlStrings[2],
						0,
						controlStrings[2].length,
						1000);
				} catch (JposException jposexception2) {
					traceAndThrowExceptionFromDCAL(
						jposexception2,
						"clearInput: can't write dcal");
				}
		if (super.deviceEnabled && super.dataEventEnabled && !isEnabled())
			try {
				_enable();
			} catch (JposException jposexception3) {
				traceAndThrowExceptionFromDCAL(
					jposexception3,
					"clearInput: can't enable dcal");
			}

	}

	/* 
	 * @see jpos.services.BaseService#getCheckHealthText()
	 */
	//	public String getCheckHealthText() throws JposException {
	//		
	//		return null;
	//	}

	/* 
	 * @see jpos.services.BaseService#getClaimed()
	 */
	//	public boolean getClaimed() throws JposException {
	//		
	//		return false;
	//	}

	/* 
	 * @see jpos.services.BaseService#getDeviceEnabled()
	 */
	//	public boolean getDeviceEnabled() throws JposException {
	//		
	//		return false;
	//	}

	/* 
	 * @see jpos.services.BaseService#setDeviceEnabled(boolean)
	 */
	public void setDeviceEnabled(boolean flag) throws JposException {
		if (super.state == 1)
			traceAndThrowJposException(
				new JposException(101, "setDeviceEnabled: device closed"));
		if (!super.claimed)
			traceAndThrowJposException(
				new JposException(103, "setDeviceEnabled: device not claimed"));
		if (super.deviceEnabled == flag)
			return;
		boolean flag1 = queuingMode || super.dataEventEnabled;
		if (flag) {
			if (flag1)
				try {
					if (!isEnabled()) {
						_enable();
						if (isControlStringSet(0))
							_write(
								controlStrings[0],
								0,
								controlStrings[0].length,
								1000);
						if (scannerModeB) {
							_enable();
							_disable();
							_enable();
						}
					}
					super.state = 2;
				} catch (JposException jposexception) {
					try {
						if (isEnabled()) {
							if (isControlStringSet(1))
								_write(
									controlStrings[1],
									0,
									controlStrings[1].length,
									1000);
							_disable();
						}
					} catch (JposException _ex) {
					}
					traceAndThrowExceptionFromDCAL(
						jposexception,
						"can't call dcal.enable() method");
				}
			super.deviceEnabled = flag;
			if (super.powerNotify == 1) {
				super.powerState = 2001;
				putEvent(
					new StatusUpdateEvent(
						super.callbacks.getEventSource(),
						2001),
					null);
			} else {
				super.powerState = 2000;
			}
			checkEvents();
		} else {
			if (flag1) {
				if (isEnabled()) {
					if (isControlStringSet(1))
						_write(
							controlStrings[1],
							0,
							controlStrings[1].length,
							1000);
					_disable();
				}
			}
			super.deviceEnabled = flag;
			super.powerState = 2000;
		}

	}

	/* 
	 * @see jpos.services.BaseService#getDeviceServiceDescription()
	 */
	public String getDeviceServiceDescription() throws JposException {

		String s = "Royalstone software Inc. JavaPOS Scanner Device Service,";
		int i = "$Revision: 1.1 $".indexOf(' ');
		int j = "$Revision: 1.1 $".lastIndexOf(' ');
		if (i >= 0 && j > i)
			s = s + "version 1.5." + "$Revision: 1.1 $".substring(i, j).trim();
		i = "$Modtime:: 18.06.02 18:59 $".indexOf(' ');
		j = "$Modtime:: 18.06.02 18:59 $".lastIndexOf(' ');
		if (i >= 0 && j > i) {
			String s1 = "$Modtime:: 18.06.02 18:59 $".substring(i, j).trim();
			i = s1.indexOf(".:");
			if (i >= 0)
				s1 = s1.substring(0, i) + ".0" + s1.substring(i + 2);
			s = s + " from " + s1;
		}

		return s;
	}

	/* 
	 * @see jpos.services.BaseService#getDeviceServiceVersion()
	 */
	public int getDeviceServiceVersion() throws JposException {
		int i = 1007000;

		return i;
	}

	/* 
	 * @see jpos.services.BaseService#getFreezeEvents()
	 */
	//	public boolean getFreezeEvents() throws JposException {
	//		
	//		return false;
	//	}

	/* 
	 * @see jpos.services.BaseService#setFreezeEvents(boolean)
	 */
	public void setFreezeEvents(boolean flag) throws JposException {
		super.freezeEvents = flag;
		if (!flag)
			checkEvents();

	}

	/* 
	 * @see jpos.services.BaseService#getPhysicalDeviceDescription()
	 */
	//	public String getPhysicalDeviceDescription() throws JposException {
	//		
	//		return null;
	//	}

	/* 
	 * @see jpos.services.BaseService#getPhysicalDeviceName()
	 */
	//	public String getPhysicalDeviceName() throws JposException {
	//		
	//		return null;
	//	}

	/* 
	 * @see jpos.services.BaseService#getState()
	 */
	//	public int getState() throws JposException {
	//		
	//		return 0;
	//	}

	/* 
	 * @see jpos.services.BaseService#claim(int)
	 */
	public void claim(int i) throws JposException {
		if (i < 0 && i != -1)
			traceAndThrowJposException(
				new JposException(106, "illegal parameter"));
		if (super.claimed) {
			// device already claimed
			return;
		}
		super.claimed = false;
		try {
			_claim(i);
		} catch (JposException jposexception) {
			traceAndThrowExceptionFromDCAL(jposexception, "can't claim");
		}
		clearAllEvents();
		if (dsrControl) {
			try {
				_enable();
			} catch (JposException jposexception1) {
				try {
					_release();
				} catch (JposException _ex) {
				}
				traceAndThrowExceptionFromDCAL(
					jposexception1,
					"claim: device not connected");
			}
			_disable();
		}
		super.claimed = true;
		startEventThread("RSScanner-EventThread", 50);
	}

	/* 
	 * @see jpos.services.BaseService#close()
	 */
	public void close() throws JposException {
		if (super.deviceEnabled)
			setDeviceEnabled(false);
		if (super.claimed)
			release();
		//		_removeEventListener(this);
		_close();

		initializeMembers();
	}

	/* 
	 * @see jpos.services.BaseService#checkHealth(int)
	 */
	public void checkHealth(int level) throws JposException {
		if (!super.claimed)
			traceAndThrowJposException(
				new JposException(103, "device not claimed"));
		if (!super.deviceEnabled)
			traceAndThrowJposException(
				new JposException(105, "device not enabled"));
		switch (level) {
			case 1 : // '\001'
				super.checkHealthText = "internal test for Scanner: successful";
				break;

			case 2 : // '\002'
			case 3 : // '\003'
				super.checkHealthText =
					"this health check level for WNScanner is not supported";
				traceAndThrowJposException(
					new JposException(106, "level not supported"));
				// fall through

			default :
				super.checkHealthText = "unknown level for health check";
				traceAndThrowJposException(
					new JposException(106, "unknown level"));
				break;
		}
	}

	/* 
	 * @see jpos.services.BaseService#directIO(int, int[], java.lang.Object)
	 */
	public void directIO(int command, int[] data, Object object)
		throws JposException {
		throw new JposException(106, "special commands not defined");

	}

	/* 
	 * @see jpos.services.BaseService#open(java.lang.String, jpos.services.EventCallbacks)
	 */
	public void open(String s, EventCallbacks eventcallbacks)
		throws JposException {
		//--
		usesName = s;
		if (usesName != null) {
			try {
				OSSC = new OSServiceConfiguration(usesName);
			} catch (Exception _ex) {
				throw new JposException(
					104,
					"configuration entry 'uses' found but reference entry '"
						+ usesName
						+ "' does not exist.");
			}
		} else {
			throw new JposException(
				104,
				"device logical name does not provide.");
		}
		portNameCOM = OSSC.getValue("port");
		String s2 = OSSC.getValue("baudrate");
		String s3 = OSSC.getValue("stopbits");
		String s4 = OSSC.getValue("parity");
		String s5 = OSSC.getValue("bits");
		String s6 = OSSC.getValue("protocol");
		String s7 = OSSC.getValue("dsrControl");
		String s8 = OSSC.getValue("setRTS");
		String s9 = OSSC.getValue("setDTR");
		if (portNameCOM == null)
			throw new JposException(
				104,
				"StdSimpleCommDevice: port entry in port definition "
					+ usesName
					+ " not found.");
		if (s2 == null)
			throw new JposException(
				104,
				"StdSimpleCommDevice: baudrate entry in port definition "
					+ usesName
					+ " not found.");
		baudrate = (new Integer(s2.trim())).intValue();
		if (s5 == null)
			throw new JposException(
				104,
				"StdSimpleCommDevice: bits entry in port definition "
					+ usesName
					+ " not found.");
		bits = (new Integer(s5.trim())).intValue();
		if (bits == 5)
			bits = 5;
		else if (bits == 6)
			bits = 6;
		else if (bits == 7)
			bits = 7;
		else if (bits == 8)
			bits = 8;
		else
			throw new JposException(
				104,
				"StdSimpleCommDevice: bits entry in port definition "
					+ usesName
					+ " is illegal.");
		if (s3 == null)
			throw new JposException(
				104,
				"StdSimpleCommDevice: stopbits entry in port definition "
					+ usesName
					+ " not found.");
		s3 = s3.trim();
		if (s3.equalsIgnoreCase("1"))
			stopbits = 1;
		else if (s3.equalsIgnoreCase("2"))
			stopbits = 2;
		else if (s3.equalsIgnoreCase("1,5"))
			stopbits = 3;
		else
			throw new JposException(
				104,
				"StdSimpleCommDevice: stopbits entry in port definition "
					+ usesName
					+ " is illegal.");
		if (s4 == null)
			throw new JposException(
				104,
				"StdSimpleCommDevice: parity entry in port definition "
					+ usesName
					+ " not found.");
		s4 = s4.trim();
		if (s4.equalsIgnoreCase("none"))
			parity = 0;
		else if (s4.equalsIgnoreCase("even"))
			parity = 2;
		else if (s4.equalsIgnoreCase("odd"))
			parity = 1;
		else
			throw new JposException(
				104,
				"StdSimpleCommDevice: parity entry in port definition "
					+ usesName
					+ " is illegal.");
		if (s6 == null)
			throw new JposException(
				104,
				"StdSimpleCommDevice: protocol entry in port definition "
					+ usesName
					+ " not found.");
		s6 = s6.trim();
		if (s6.equalsIgnoreCase("none"))
			flowControl = 0;
		else if (s6.equalsIgnoreCase("cts"))
			flowControl = 3;
		else if (s6.equalsIgnoreCase("ctsin"))
			flowControl = 1;
		else if (s6.equalsIgnoreCase("ctsout"))
			flowControl = 2;
		else if (s6.equalsIgnoreCase("xon"))
			flowControl = 12;
		else if (s6.equalsIgnoreCase("xonin"))
			flowControl = 4;
		else if (s6.equalsIgnoreCase("xonout"))
			flowControl = 8;
		else
			throw new JposException(
				104,
				"StdSimpleCommDevice: protocol entry in port definition "
					+ usesName
					+ " is illegal.");
		if (s7 != null) {
			s7 = s7.trim();
			if (s7.equalsIgnoreCase("false"))
				flagDSRControl = 0;
			else if (s7.equalsIgnoreCase("true"))
				flagDSRControl = 1;
			else if (s7.equalsIgnoreCase("notused"))
				flagDSRControl = 2;
			else
				throw new JposException(
					104,
					"StdSimpleCommDevice: dsrControl entry in port definition "
						+ usesName
						+ " is illegal.");
		}
		if (s8 != null) {
			s8 = s8.trim();
			if (s8.equalsIgnoreCase("false"))
				flagSetRTS = 0;
			else if (s8.equalsIgnoreCase("true"))
				flagSetRTS = 1;
			else if (s8.equalsIgnoreCase("notused"))
				flagSetRTS = 2;
			else
				throw new JposException(
					104,
					"StdSimpleCommDevice: setRTS entry in port definition "
						+ usesName
						+ " is illegal.");
		}
		if (s9 != null) {
			s9 = s9.trim();
			if (s9.equalsIgnoreCase("false"))
				flagSetDTR = 0;
			else if (s9.equalsIgnoreCase("true"))
				flagSetDTR = 1;
			else if (s9.equalsIgnoreCase("notused"))
				flagSetDTR = 2;
			else
				throw new JposException(
					104,
					"StdSimpleCommDevice: setDTR entry in port definition "
						+ usesName
						+ " is illegal.");
		}
		addRef();

		//--
		logicalname = s;
		super.callbacks = eventcallbacks;

		try {
			serviceConfiguration = new OSServiceConfiguration(logicalname);
		} catch (Exception _ex) {

			JposException jposexception =
				new JposException(106, "can't create object");
			traceAndThrowJposException(jposexception);
		}

		String s1 = getConfigurationStrings(s);
		if (s1 != null) {
			JposException jposexception2 = new JposException(106, s1);
			traceAndThrowJposException(jposexception2);
		}
		opened = true;
		super.state = 2;

	}

	/* 
	 * @see jpos.services.BaseService#release()
	 */
	public void release() throws JposException {
		try {
			if (isEnabled()) {
				if (isControlStringSet(1))
					_write(
						controlStrings[1],
						0,
						controlStrings[1].length,
						1000);
				_disable();
			}
			_release();
		} catch (JposException jposexception) {
			traceAndThrowExceptionFromDCAL(jposexception, "can't release");
		}
		super.deviceEnabled = false;
		super.claimed = false;
		stopEventThread();
	}

	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
			case SerialPortEvent.BI :
			case SerialPortEvent.OE :
			case SerialPortEvent.FE :
			case SerialPortEvent.PE :
			case SerialPortEvent.CD :
			case SerialPortEvent.CTS :
			case SerialPortEvent.RI :
			case SerialPortEvent.OUTPUT_BUFFER_EMPTY :
				break;
			case SerialPortEvent.DSR :
				if (flagSetDTR == 2 || flagDSRControl == 2)
					break;
				char c = '\u07D1';
				boolean flag = COMs.isDSR();
				if (flagDSRControl == 1
					&& !flag
					|| flagDSRControl == 0
					&& flag)
					c = '\u07D4';
				if (flagDSRControl == 1
					&& flag
					|| flagDSRControl == 0
					&& !flag)
					c = '\u07D1';
				statusUpdateOccurred(c);
				break;
			case SerialPortEvent.DATA_AVAILABLE :
				int i = 0;
				int j = 50;
				byte abyte0[] = new byte[j];
				do try {

						int k = inCOM.available();

						if (k == 0)
							break;

						k = inCOM.read(abyte0, i, j);

						j -= k;
						i += k;
						if (j <= 0)
							break;
					} catch (IOException ioexception) {
						errorOccurred(
							111,
							0,
							"error: cannot read from physical port "
								+ portNameCOM
								+ "(asynchron): "
								+ ioexception.getMessage());

					} while (true);
//				System.out.println("CC=" + i);
//				printBytes(abyte0);
//				System.out.println("DD=" + abyte0);
				if (i > 0)
					inputAvailable(abyte0, i);
				break;
		}
	}

	public void printBytes(byte[] code) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < code.length; i++) {
			buffer.append("0x");
			int v = code[i] & 0xFF;
			if (v < 16)
				buffer.append("0");
			buffer.append(Integer.toHexString(v).toUpperCase());
			buffer.append(",");
		}
		System.out.println(buffer.toString());
	}

	//----------------------------------------------
	//----------- Business helper method -----------
	//----------------------------------------------
	public void inputAvailable(byte abyte0[], int i) {

		if(!super.deviceEnabled)
			return;
		boolean flag = false;
		for(int j = 0; j < i; j++)
		{
			if(internalScanDataBufferLength > 99)
			{
				ErrorEvent errorevent = new ErrorEvent(super.callbacks.getEventSource(), 111, 1, 2, 12);
				putEvent(errorevent, null);
				if(eventQueueIsFull())
				{
					if(isEnabled())
						try
						{
							if(isControlStringSet(1))
								_write(controlStrings[1], 0, controlStrings[1].length, 1000);
							_disable();
						}
						catch(JposException _ex)
						{
						}
				}
				internalScanDataBufferLength = 0;
				internalScanDataTrailerStartIdx = -1;
				internalScanDataTrailerStartLen = 0;
				break;
			}
			byte byte0 = abyte0[j];
			if(abyte0[j] != 0x02 && abyte0[j] != -0x7D)byte0 = (byte)((abyte0[j] & 0x0F) | 0x30);
			internalScanDataBuffer[internalScanDataBufferLength++] = byte0;
			boolean flag1 = false;
			//flag1 = true;
			if(internalScanDataTrailerStartIdx < 0)
			{
				for(int k = 0; k < trailerStrings.length; k++)
				{
//					byte[] showbytes=new byte[1];
//					showbytes[0]=trailerStrings[k][0];
//					
//					System.out.println("AAAA="+(new String(showbytes)+"AAAA"));
//					printBytes(showbytes);
//					
//					showbytes[0]=byte0;
//					System.out.println("BBBB="+(new String(showbytes)+"BBBB"));
//					printBytes(showbytes);

					if(trailerStrings[k][0] != byte0)
						continue;
					internalScanDataTrailerStartIdx = internalScanDataBufferLength - 1;
					if(trailerStrings[k].length == 1)
					{
						flag1 = true;
						internalScanDataTrailerStartLen = 1;
					}
					break;
				}

			} else
			{
				int k1 = internalScanDataBufferLength - 1 - internalScanDataTrailerStartIdx;
				for(int l = 0; l < trailerStrings.length; l++)
				{
					if(trailerStrings[l].length <= k1 || trailerStrings[l][k1] != byte0)
						continue;
					if(trailerStrings[l].length == k1 + 1)
					{
						flag1 = true;
						internalScanDataTrailerStartLen = k1 + 1;
					}
					break;
				}

			}
			if(flag1)
			{
				int l1 = 0;
				for(int i1 = 0; i1 < headerStrings.length; i1++)
					if(headerStrings[i1].length == 1){

//						byte[] showbytes=new byte[1];
//						showbytes[0]=headerStrings[i1][0];
//					
//						System.out.println("CCCC="+(new String(showbytes)+"CCCC"));
//						printBytes(showbytes);
//					
//						showbytes[0]=internalScanDataBuffer[0];
//						System.out.println("DDDD="+(new String(showbytes)+"DDDD"));
//						printBytes(showbytes);

						if(headerStrings[i1][0] == internalScanDataBuffer[0])
						{
							l1 = 1;
						} else
						{
							for(int j1 = 0; j1 < headerStrings[i1].length; j1++)
							{
								if(headerStrings[i1][j1] != internalScanDataBuffer[j1])
									break;
								if(j1 != headerStrings[i1].length - 1)
									continue;
								l1 = headerStrings[i1].length;
								break;
							}

						}
					}

				if(l1 > 0)
				{
					internalScanDataBufferLength -= l1;
					internalScanDataTrailerStartIdx -= l1;
					System.arraycopy(internalScanDataBuffer, l1, internalScanDataBuffer,0, internalScanDataBufferLength);
					
//					System.out.println("EEEE=");
//					printBytes(internalScanDataBuffer);
				}
				internalScanDataBufferLength = internalScanDataTrailerStartIdx;
				internalScanDataBuffer[internalScanDataBufferLength] = 0;
//				byte abyte1[] = new byte[internalScanDataBufferLength];
//				System.arraycopy(internalScanDataBuffer, 0, abyte1, 0, internalScanDataBufferLength);
				scanData = new byte[internalScanDataBufferLength];
				System.arraycopy(internalScanDataBuffer, 0, scanData, 0, internalScanDataBufferLength);

				boolean flag2 = (!queuingMode || super.autoDisable) && !scannerModeB;
				if(flag2)
				{
					try
					{
						if(isEnabled())
						{
							if(isControlStringSet(1))
								_write(controlStrings[1], 0, controlStrings[1].length, 1000);
							_disable();
						}
					}
					catch(JposException _ex)
					{
					}
				}
				
				super.dataCount++;
				putEvent(new DataEvent(super.callbacks.getEventSource(), 0), null);
				
				if(eventQueueIsFull())
				{
					if(isEnabled())
						try
						{
							if(isControlStringSet(1))
								_write(controlStrings[1], 0, controlStrings[1].length, 1000);
							_disable();
						}
						catch(JposException _ex)
						{
						}
				}
				internalScanDataBufferLength = 0;
				internalScanDataTrailerStartIdx = -1;
				internalScanDataTrailerStartLen = 0;
			}
		}


	}

	public void statusUpdateOccurred(int i) {

		if (super.powerNotify == 1) {
			if (i == 2001)
				super.powerState = 2001;
			else
				super.powerState = 2004;
			putEvent(
				new StatusUpdateEvent(super.callbacks.getEventSource(), i),
				null);
		}

	}

	public void errorOccurred(int i, int j, String s) {
		ErrorEvent errorevent = null;
		if (i == 114)
			errorevent =
				new ErrorEvent(super.callbacks.getEventSource(), 114, 0, 2, 12);
		else
			errorevent =
				new ErrorEvent(super.callbacks.getEventSource(), i, j, 2, 12);
		putEvent(errorevent, null);
	}

	protected String getConfigurationStrings(String s) {
		String s1 = null;
//		if (logicalname.startsWith("ROYALSTONE_")
//			|| logicalname.startsWith("SYMBOL_")) {
			super.physicalDeviceDescription =
				"Scanner LS, logicalName="
					+ s
					+ ", connected at "
					+ serviceConfiguration.getValue("productDescription");
			super.physicalDeviceName = "Symbol Scanner LS4004 et others";
//		} else {
//			s1 =
//				"cannot query ROYALSTONE_ or SYMBOL_ property value (open name)";
//			return s1;
//		}
		String s2 = serviceConfiguration.getValue("queuingMode");
		if (s2 != null) {
			if (s2.equalsIgnoreCase("on"))
				queuingMode = true;
			else if (s2.equalsIgnoreCase("off")) {
				queuingMode = false;
			} else {
				s1 = "illegal queuingMode value '" + s2 + "'";
				return s1;
			}
		} else {
			queuingMode = false;
		}
		s2 = serviceConfiguration.getValue("scannerMode");
		if (s2 != null) {
			if (s2.equalsIgnoreCase("B"))
				scannerModeB = true;
			else if (s2.equalsIgnoreCase("A")) {
				scannerModeB = false;
			} else {
				s1 = "illegal scannerMode value '" + s2 + "'";
				return s1;
			}
		} else {
			scannerModeB = false;
		}
		s2 = serviceConfiguration.getValue("controlSequence");
		controlStrings = getControlStrings(s2, null);
		s2 = serviceConfiguration.getValue("headerSequence");
		headerStrings = getControlStrings(s2, headerStrings);
		s2 = serviceConfiguration.getValue("trailerSequence");
		trailerStrings = getControlStrings(s2, trailerStrings);

		s2 = serviceConfiguration.getValue("dsrControl");
		if (s2 != null) {
			if (s2.equalsIgnoreCase("true"))
				dsrControl = true;
			else if (s2.equalsIgnoreCase("false"))
				dsrControl = true;
			else if (s2.equalsIgnoreCase("notused")) {
				dsrControl = false;
			} else {
				s1 = "illegal dsrControl value '" + s2 + "'";

				return s1;
			}
		} else {

			dsrControl = false;
		}
		if (dsrControl)
			super.capPowerReporting = 1;

		return s1;
	}

	private static byte[][] getControlStrings(String s, byte abyte0[][]) {
		if (s == null)
			return abyte0;
		StringTokenizer stringtokenizer = new StringTokenizer(s, ",");
		int i;
		for (i = 0; stringtokenizer.hasMoreTokens(); i++)
			stringtokenizer.nextToken();

		byte abyte1[][] = new byte[i][];
		stringtokenizer = new StringTokenizer(s, ",");
		for (int j = 0; stringtokenizer.hasMoreTokens(); j++) {
			String s1 = stringtokenizer.nextToken().trim();
			abyte1[j] = RSBaseService.transformToByteArray(s1);
		}

		return abyte1;
	}

	protected void initializeMembers() {
		super.autoDisable = false;
		super.capPowerReporting = 0;
		super.checkHealthText = "[Error]";
		super.claimed = false;
		super.dataCount = 0;
		super.dataEventEnabled = false;
		super.deviceEnabled = false;
		super.freezeEvents = false;
		super.powerNotify = 0;
		super.powerState = 2000;
		super.state = 1;
		super.physicalDeviceDescription = "";
		super.physicalDeviceName = "[Error]";
		decodeData = false;
		scanData = ZEROBYTEARRAY;
		scanDataLabel = ZEROBYTEARRAY;
		scanDataType = 0;
		opened = false;
		queuingMode = false;
		dsrControl = false;
		internalScanDataBuffer = new byte[100];
		internalScanDataBufferLength = 0;
		internalScanDataTrailerStartIdx = -1;
		internalScanDataTrailerStartLen = 0;
		scannerModeB = false;
		controlStrings = null;
		errorText = "";
		errorTextextended = "";
		errorCode = 0;
		super.callbacks = null;
		System.gc();
	}

	boolean isControlStringSet(int i) {
		if (controlStrings == null)
			return false;
		if (controlStrings.length <= i)
			return false;
		return controlStrings[i].length != 0;
	}

	public boolean isClaimed() {
		return super.claimed;
	}

	public boolean isEnabled() {
		return super.deviceEnabled;
	}

	public boolean isOpened() {
		return opened;
	}
	//----------------------------------------------
	//----------- Operation helper method ----------
	//----------------------------------------------	
	public void _claim(int i) throws JposException {

		if (!isOpened())
			throw new JposException(
				101,
				"StdSimpleCommDevice: for port " + portNameCOM + "is closed.");
		if (!isShared && isClaimed())
			return;
		if (!isClaimed()) {

			Enumeration enumeration = CommPortIdentifier.getPortIdentifiers();

			CommPortIdentifier commportidentifier = null;
			boolean flag = false;
			System.out.println(
				"commHasMoreElement=" + enumeration.hasMoreElements());
			while (enumeration.hasMoreElements()) {

				commportidentifier =
					(CommPortIdentifier) enumeration.nextElement();
				System.out.println("commName=" + commportidentifier.getName());

				if (commportidentifier.getPortType() == 1
					&& commportidentifier.getName().equals(portNameCOM)) {
					flag = true;
					break;
				}
			}
			if (!flag)
				throw new JposException(
					106,
					0,
					" error: cannot find physically port '"
						+ portNameCOM
						+ "' in standard comm api");
			try {
				if (i == 0)
					i = 1;

				COMs =
					(SerialPort) commportidentifier.open(
						"StdSimpleCommDevice-" + portNameCOM,
						i);

				COMs.addEventListener(this);
				COMs.notifyOnDataAvailable(true);
				COMs.notifyOnFramingError(true);
				COMs.notifyOnOutputEmpty(true);
				COMs.notifyOnOverrunError(true);
				COMs.notifyOnParityError(true);
				if (flagSetDTR != 2 && flagDSRControl != 2)
					COMs.notifyOnDSR(true);
			} catch (TooManyListenersException toomanylistenersexception) {
				throw new JposException(
					106,
					0,
					" error: cannot open port logically, '"
						+ portNameCOM
						+ "': too many listeners in use"
						+ toomanylistenersexception.getMessage());
			} catch (PortInUseException portinuseexception) {
				throw new JposException(
					112,
					0,
					" error: cannot open physically port, '"
						+ portNameCOM
						+ "': port is in use"
						+ portinuseexception.getMessage());
			}
			try {

				inCOM = COMs.getInputStream();

			} catch (IOException ioexception) {
				COMs = null;
				throw new JposException(
					112,
					0,
					" error: cannot open physically port, '"
						+ portNameCOM
						+ "': input stream not available"
						+ ioexception.getMessage());
			}
			try {

				outCOM = COMs.getOutputStream();

			} catch (IOException ioexception1) {
				try {

					inCOM.close();

				} catch (IOException _ex) {
				}

				COMs.close();

				COMs = null;
				throw new JposException(
					112,
					0,
					" error: cannot open physically port, '"
						+ portNameCOM
						+ "': output stream not available"
						+ ioexception1.getMessage());
			}
			try {

				COMs.setSerialPortParams(baudrate, bits, stopbits, parity);

				COMs.setFlowControlMode(flowControl);

				byte byte0 = 50;

				COMs.enableReceiveTimeout(50);

			} catch (UnsupportedCommOperationException unsupportedcommoperationexception) {
				sharedFlag = 0;
				if (COMs != null) {
					try {
						inCOM.close();
					} catch (IOException _ex) {
					}
					try {
						outCOM.close();
					} catch (IOException _ex) {
					}
					COMs = null;
				}
				throw new JposException(
					106,
					0,
					" error: access of port '"
						+ portNameCOM
						+ "': cannot set port parameter: "
						+ unsupportedcommoperationexception.getMessage());
			}
		}
		claimRef++;
	}

	public void _enable() throws JposException {

		checkOpenClaimEnable(false);
		if (!isShared && isEnabled())
			return;
		if (enableRef == 0)
			checkSetDTR(false);
		if (flagDSRControl == 0 || flagDSRControl == 1)
			try {
				boolean flag = COMs.isDSR();

				if (flagDSRControl == 1
					&& !flag
					|| flagDSRControl == 0
					&& flag)
					throw new JposException(108, "DSR is not enabled.");
			} catch (Exception exception) {
				restoreError = true;
				sharedFlag = 0;
				throw new JposException(
					107,
					0,
					" enable(): cannot get DSR line at port+ '"
						+ portNameCOM
						+ "':"
						+ exception.getMessage());
			}
		if (enableRef == 0)
			try {
				checkSetRTS(false);
			} catch (JposException _ex) {
				try {
					checkSetDTR(true);
				} catch (JposException _ex2) {
				}
			}
		enableRef++;
	}

	public void _disable() throws JposException {

		checkOpenClaimEnable(false);
		if (!isShared && !isEnabled())
			return;
		if (enableRef == 0)
			return;
		if (enableRef == 1) {
			checkSetRTS(true);
			try {
				checkSetDTR(true);
			} catch (JposException _ex) {
				try {
					checkSetRTS(false);
				} catch (JposException _ex2) {
				}
			}
		}
		if (enableRef > 0)
			enableRef--;
	}

	public void _release() throws JposException {

		if (!isOpened())
			throw new JposException(
				101,
				"StdSimpleCommDevice: for port " + usesName + " is closed.");
		if (!isClaimed())
			throw new JposException(
				106,
				"StdSimpleCommDevice: for port "
					+ usesName
					+ " is not claimed.");
		if (!isShared && isEnabled())
			try {
				_disable();
			} catch (JposException _ex) {
			}
		if (claimRef == 1)
			try {

				if (COMs != null) {
					try {

						inCOM.close();

					} catch (IOException _ex) {
					}
					try {

						outCOM.close();

					} catch (IOException _ex) {
					}
					COMs.removeEventListener();

					COMs.close();

					COMs = null;
				}
			} catch (Exception exception) {
				throw new JposException(
					106,
					0,
					" error: cannot close physically port + '"
						+ portNameCOM
						+ "': "
						+ exception.getMessage());
			}
		if (claimRef > 0)
			claimRef--;
	}

	private void checkOpenClaimEnable(boolean flag) throws JposException {
		if (openRef == 0)
			throw new JposException(
				101,
				"StdSimpleCommDevice: for port " + portNameCOM + " is closed.");
		if (claimRef == 0)
			throw new JposException(
				103,
				"StdSimpleCommDevice: for port "
					+ portNameCOM
					+ " is not claimed.");
		if (flag && enableRef == 0)
			throw new JposException(
				105,
				"StdSimpleCommDevice: for port "
					+ portNameCOM
					+ " is not enabled.");
		else
			return;
	}

	void checkSetDTR(boolean flag) throws JposException {
		if (flagSetDTR != 0 && flagSetDTR != 1)
			return;
		boolean flag1;
		if (flagSetDTR == 1) {
			if (!flag)
				flag1 = true;
			else
				flag1 = false;
		} else if (!flag)
			flag1 = false;
		else
			flag1 = true;

		COMs.setDTR(flag1);
	}

	void checkSetRTS(boolean flag) throws JposException {
		if (flagSetRTS != 0 && flagSetRTS != 1)
			return;
		boolean flag1;
		if (flagSetRTS == 1) {
			if (!flag)
				flag1 = true;
			else
				flag1 = false;
		} else if (!flag)
			flag1 = false;
		else
			flag1 = true;

		COMs.setRTS(flag1);
	}

	public boolean _write(byte abyte0[], int i, int j, int k)
		throws JposException {
		boolean flag = false;
		checkOpenClaimEnable(true);
		int i1 = k;
		try {
			if (restoreError) {

				clearError();
			}
			if (flagDSRControl == 0 || flagDSRControl == 1) {
				boolean flag1 = COMs.isDSR();

				if (flagDSRControl == 1
					&& !flag1
					|| flagDSRControl == 0
					&& flag1)
					throw new JposException(108, "DSR is not enabled.");
			}
			int l = j;

			outCOM.write(abyte0, i, j);

			if (l != j)
				return false;
		} catch (IOException ioexception) {
			restoreError = true;
			sharedFlag = 0;
			throw new JposException(
				112,
				0,
				" error: cannot write to physically port '"
					+ portNameCOM
					+ "': "
					+ ioexception.getMessage());
		}
		return true;
	}

	public int _writeRead(
		byte abyte0[],
		int i,
		int j,
		byte abyte1[],
		int k,
		int l,
		int i1)
		throws JposException {
		boolean flag = false;
		_write(abyte0, i, j, 0);
		return _read(abyte1, k, l, i1);
	}

	public int _read(byte abyte0[], int i, int j, int k) throws JposException {
		boolean flag = false;
		int j1 = j;
		checkOpenClaimEnable(true);
		if (restoreError)
			clearError();
		try {
			if (flagDSRControl == 0 || flagDSRControl == 1) {
				boolean flag1 = COMs.isDSR();
				if (flagDSRControl == 1
					&& !flag1
					|| flagDSRControl == 0
					&& flag1)
					throw new JposException(108, "DSR is not enabled.");
			}
			if (k <= 0) {

				COMs.disableReceiveTimeout();

			} else {

				COMs.enableReceiveTimeout(k);

			}
		} catch (UnsupportedCommOperationException unsupportedcommoperationexception) {
			sharedFlag = 0;
			restoreError = true;
			throw new JposException(
				111,
				0,
				" error: cannot set read timeout for physically port '"
					+ portNameCOM
					+ "' (unsupported): "
					+ unsupportedcommoperationexception.getMessage());
		}
		int k1 = 200;
		while (k > 0 && j1 > 0) {
			int l;
			try {

				l = inCOM.available();

			} catch (IOException ioexception) {
				sharedFlag = 0;
				restoreError = true;
				throw new JposException(
					111,
					0,
					" error: cannot read from physically port '"
						+ portNameCOM
						+ "' (IO exception on available): "
						+ ioexception.getMessage());
			}
			if (l > 0) {
				int i1;
				try {

					i1 = inCOM.read(abyte0, i, j1);

				} catch (IOException ioexception1) {
					throw new JposException(
						111,
						0,
						" error: cannot read on physically port '"
							+ portNameCOM
							+ "' (IO exception on read): "
							+ ioexception1.getMessage());
				}
				i += i1;
				j1 -= i1;
			} else {
				try {
					Thread.sleep(k1);
				} catch (InterruptedException interruptedexception) {
					throw new JposException(
						111,
						0,
						" error: cannot read on physically port '"
							+ portNameCOM
							+ "' (interrupted): "
							+ interruptedexception.getMessage());
				}
				k -= k1;
			}
		}
		return j1;
	}

	private void clearError() {
		if (!restoreError)
			return;
		if (COMs != null)
			COMs.sendBreak(1000);
		restoreError = false;
	}

	public void _close() throws JposException {

		if (!isOpened())
			throw new JposException(
				101,
				"StdSimpleCommDevice: for port " + portNameCOM + "is closed.");
		if (!isShared && isEnabled())
			try {
				_disable();
			} catch (JposException _ex) {
			}
		if (!isShared && isClaimed())
			try {
				_release();
			} catch (JposException _ex) {
			}
		if (openRef == 1 && COMs != null) {

			try {

				inCOM.close();

			} catch (IOException _ex) {
			}
			try {

				outCOM.close();

			} catch (IOException _ex) {
			}
			COMs.removeEventListener();
			COMs = null;
		}
		removeRef();
	}

	//	public void _addEventListener(DCALEventListener dcaleventlistener)
	//		throws JposException
	//	{
	//		boolean flag = false;
	//		synchronized(DCALListeners)
	//		{
	//			if(!isShared)
	//			{
	//				if(DCALListeners[0] != null)
	//					throw new JposException(104, "StdSimpleCommDevice: more than one listeners for port" + portNameCOM + " added.");
	//				DCALListeners[0] = dcaleventlistener;
	//			} else
	//			{
	//				throw new JposException(104, "StdSimpleCommDevice: in shared mode: wrong listener class transferred for port" + portNameCOM + ".");
	//			}
	//		}
	//	}				
	//	
	//	public void _removeEventListener(DCALEventListener dcaleventlistener)
	//	{
	//		synchronized(DCALListeners)
	//		{
	//			int i;
	//			for(i = 0; i < DCALListeners.length; i++)
	//				if(DCALListeners[i] == dcaleventlistener)
	//					break;
	//
	//			if(i == DCALListeners.length)
	//				return;
	//			DCALListeners[i] = null;
	//		}
	//	}

	void addRef() {
		if (openRef == 0)
			allSimpleCommDevices.addElement(this);
		openRef++;
	}

	private void removeRef() {
		openRef--;
		if (openRef == 0)
			allSimpleCommDevices.removeElement(this);
	}

	public void _flush(int i) throws JposException {
		boolean flag = false;

		boolean flag1 = false;
		if (openRef == 0)
			throw new JposException(
				101,
				"StdSimpleCommDevice: flush() for port "
					+ portNameCOM
					+ " is closed.");
		if (claimRef == 0)
			throw new JposException(
				103,
				"StdSimpleCommDevice: flush()  for port "
					+ portNameCOM
					+ " is not claimed.");
		clearError();
		if (!isEnabled()) {
			flag = true;

			try {
				_enable();
			} catch (JposException _ex) {
			}
		}
		int k = i + 100;
		do {
			try {
				int j = inCOM.available();
				if (j <= 0) {
					if ((k -= 100) < 0)
						break;
					try {
						Thread.sleep(100L);
					} catch (InterruptedException _ex) {
					}
				}
				for (int l = 0; l < j; l++)
					inCOM.read();

				continue;
			} catch (IOException _ex) {
			}
			break;
		} while (true);
		if (flag)
			try {
				_disable();
			} catch (JposException _ex) {
			}
	}

}
