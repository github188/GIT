/*
 * Created on 2005-5-17
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.royalstone.pos.hardware.config;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.royalstone.pos.util.PosConfig;

/**
 * @author yaopoqing
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class HardWareConfigure {
	private PosConfig posconfig = null;

	Document doc = null;

	Element eRoot = null;

	List childList = null;

	Element printerElement = null;

	Element scannerElement = null;

	Element cashElement = null;

	Element displayElement = null;

	public HardWareConfigure(PosConfig posconfig) {
		this.posconfig = posconfig;
	}

	public void initHardWareConfig() {
         String jposHeader="<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
         		"\n<!DOCTYPE JposEntries PUBLIC \"-//JavaPOS//DTD//EN\"" +
         		"\n\"jpos/res/jcl.dtd\">" +
         		"\n<JposEntries>\n";
		 String printerStr=this.configPrinter();
         String cashStr=this.configCashDrawer();
         String displayStr=this.configCustDisplay();
         String scannerStr=this.configScanner();
         String jposTrail="</JposEntries>";
         String outStr=jposHeader+printerStr+cashStr+displayStr+scannerStr+jposTrail;
         FileOutputStream fos=null;
         try {
			fos=new FileOutputStream("jpos.xml");
			 fos.write(outStr.getBytes("UTF-8"));
			 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(fos!=null)
				try {
					fos.close();
				} catch (IOException e1) {
				}
		}
	}

	private String configPrinter()  {
		String header="<JposEntry logicalName=\"POSPrinter\">";
		String fyClass = posconfig.getString("PRN_FYCLASS");
		if (fyClass.equals(""))
			//fyClass = "com.royalstone.pos.services.factories.RSServiceInstanceFactory";
		   return this.defaulPrinter();
		String serClass = posconfig.getString("PRN_SERCLASS");
		if (serClass.equals(""))
			serClass = "com.royalstone.pos.services.POSPrinter";
	    String portName=posconfig.getString("PRN_PORTNAME");
		if(portName.equals(""))
			portName="COM1";
		String baudRate=posconfig.getString("PRN_BAUDRATE");
		if(baudRate.equals(""))
			baudRate="9600";
		String byteSize=posconfig.getString("PRN_BYTESIZE");
		if(byteSize.equals(""))
			byteSize="8";
		String stopBits=posconfig.getString("PRN_STOPBITS");
		if(stopBits.equals(""))
			stopBits="0";
		String parity=posconfig.getString("PRN_PARITY");
		if(parity.equals(""))
			parity="0";
		String driverName=posconfig.getString("PRN_DRIVERNAME");
		if(driverName.equals(""))
			driverName="drv/RSPrinter41_TM_U210";
		
		
		StringBuffer bodyBf=new StringBuffer();
		
		bodyBf.append(header);
		bodyBf.append("\n<creation factoryClass=\""+fyClass+"\" serviceClass=\""+serClass+"\"/>\n");
		bodyBf.append("<vendor name=\"Royalstone software Inc.\" url=\"http://www.royalstone.cn\"/>\n"+
				 "<jpos category=\"Printer\" version=\"1.4\"/>\n"+
				 "<product description=\"Printer\" name=\"Royalstone software Inc. Services for JavaPOS(TM) Standard\" url=\"http://www.royalstone.cn\"/>\n");
        bodyBf.append("<prop name=\"portName\" value=\""+portName+"\"/>\n");
		bodyBf.append("<prop name=\"baudRate\" value=\""+baudRate+"\"/>\n");
		bodyBf.append("<prop name=\"byteSize\" value=\""+byteSize+"\"/>\n");
		bodyBf.append("<prop name=\"stopBits\" value=\""+stopBits+"\"/>\n");
		bodyBf.append("<prop name=\"parity\" value=\""+parity+"\"/>\n");
		bodyBf.append("<prop name=\"driverName\" value=\""+driverName+"\"/>\n");
		bodyBf.append("</JposEntry>\n");
		
		return bodyBf.toString();		

	}
   private String defaulPrinter(){
   	String defaulPrinter="<JposEntry logicalName=\"POSPrinter\">\n"+
		"<creation factoryClass=\"com.royalstone.pos.services.factories.JournalPrinterServiceInstanceFactory\" serviceClass=\"com.royalstone.pos.services.JournalPrinter\"/>\n"+
		"<vendor name=\"royalstone\" url=\"royalstone\"/>\n"+
		"<jpos category=\"POSPrinter\" version=\"1.2\"/>\n"+
		"<product description=\"pos41\" name=\"pos41\" url=\"www.royalstone.com.cn\"/>\n"+
		"<prop name=\"vendor.Title\" type=\"String\" value=\"Journal Printer\"/>\n"+
		"<prop name=\"vendor.Columns\" type=\"String\" value=\"20\"/>\n"+
		"<prop name=\"vendor.Rows\" type=\"String\" value=\"10\"/>\n"+
		"<prop name=\"vendor.font\" type=\"String\" value=\"Courier\"/>\n"+
		"<prop name=\"vendor.pointsize\" type=\"String\" value=\"9\"/>\n"+
		"<prop name=\"vendor.Width\" type=\"String\" value=\"200\"/>\n"+
		"<prop name=\"vendor.Height\" type=\"String\" value=\"200\"/>\n"+
		"<prop name=\"vendor.Xoffset\" type=\"String\" value=\"800\"/>\n"+
		"<prop name=\"vendor.Yoffset\" type=\"String\" value=\"200\"/>\n"+
	    "</JposEntry>\n";
   	return defaulPrinter;
   }
	private String configCashDrawer() {
		String header="<JposEntry logicalName=\"POSCashDrawer\">";
		String fyClass = posconfig.getString("CASH_FYCLASS");
		if (fyClass.equals(""))
			fyClass = "com.royalstone.pos.services.factories.RSServiceInstanceFactory";
		
		String serClass = posconfig.getString("CASH_SERCLASS");
		if (serClass.equals(""))
			serClass = "com.royalstone.pos.services.POSCashDrawer";
	    String portName=posconfig.getString("CASH_PORTNAME");
		if(portName.equals(""))
			portName="COM1";
		String baudRate=posconfig.getString("CASH_BAUDRATE");
		if(baudRate.equals(""))
			baudRate="9600";
		String byteSize=posconfig.getString("CASH_BYTESIZE");
		if(byteSize.equals(""))
			byteSize="8";
		String stopBits=posconfig.getString("CASH_STOPBITS");
		if(stopBits.equals(""))
			stopBits="0";
		String parity=posconfig.getString("CASH_PARITY");
		if(parity.equals(""))
			parity="0";
		String driverName=posconfig.getString("CASH_DRIVERNAME");
		if(driverName.equals(""))
			driverName="drv/RSCashDrawer_TM_U210";
		
		
		StringBuffer bodyBf=new StringBuffer();
		
		bodyBf.append(header);
		bodyBf.append("\n<creation factoryClass=\""+fyClass+"\" serviceClass=\""+serClass+"\"/>\n");
		bodyBf.append("<vendor name=\"Royalstone software Inc.\" url=\"http://www.royalstone.cn\"/>\n"+
				 "<jpos category=\"CashDrawer\" version=\"1.4\"/>\n"+
				 "<product description=\"CashDrawer\" name=\"Royalstone software Inc. Services for JavaPOS(TM) Standard\" url=\"http://www.royalstone.cn\"/>\n");
        bodyBf.append("<prop name=\"portName\" value=\""+portName+"\"/>\n");
		bodyBf.append("<prop name=\"baudRate\" value=\""+baudRate+"\"/>\n");
		bodyBf.append("<prop name=\"byteSize\" value=\""+byteSize+"\"/>\n");
		bodyBf.append("<prop name=\"stopBits\" value=\""+stopBits+"\"/>\n");
		bodyBf.append("<prop name=\"parity\" value=\""+parity+"\"/>\n");
		bodyBf.append("<prop name=\"driverName\" value=\""+driverName+"\"/>\n");
		bodyBf.append("</JposEntry>\n");
		
		return bodyBf.toString();

	}

	private String configCustDisplay()  {
		String header="<JposEntry logicalName=\"POSCustDisplay\">";
		String fyClass = posconfig.getString("DISP_FYCLASS");
		if (fyClass.equals(""))
			fyClass = "com.royalstone.pos.services.factories.RSServiceInstanceFactory";
		
		String serClass = posconfig.getString("DISP_SERCLASS");
		if (serClass.equals(""))
			serClass = "com.royalstone.pos.services.CustDisplay";
	    String portName=posconfig.getString("DISP_PORTNAME");
		if(portName.equals(""))
			portName="COM1";
		String baudRate=posconfig.getString("DISP_BAUDRATE");
		if(baudRate.equals(""))
			baudRate="9600";
		String byteSize=posconfig.getString("DISP_BYTESIZE");
		if(byteSize.equals(""))
			byteSize="8";
		String stopBits=posconfig.getString("DISP_STOPBITS");
		if(stopBits.equals(""))
			stopBits="0";
		String parity=posconfig.getString("DISP_PARITY");
		if(parity.equals(""))
			parity="0";
		String driverName=posconfig.getString("DISP_DRIVERNAME");
		if(driverName.equals(""))
			driverName="drv/RSVFD41_BA63G";
		
		
		StringBuffer bodyBf=new StringBuffer();
		
		bodyBf.append(header);
		bodyBf.append("\n<creation factoryClass=\""+fyClass+"\" serviceClass=\""+serClass+"\"/>\n");
		bodyBf.append("<vendor name=\"Royalstone software Inc.\" url=\"http://www.royalstone.cn\"/>\n"+
				 "<jpos category=\"LineDisplay\" version=\"1.4\"/>\n"+
				 "<product description=\"LineDisplay\" name=\"Royalstone software Inc. Services for JavaPOS(TM) Standard\" url=\"http://www.royalstone.cn\"/>\n");
        bodyBf.append("<prop name=\"portName\" value=\""+portName+"\"/>\n");
		bodyBf.append("<prop name=\"baudRate\" value=\""+baudRate+"\"/>\n");
		bodyBf.append("<prop name=\"byteSize\" value=\""+byteSize+"\"/>\n");
		bodyBf.append("<prop name=\"stopBits\" value=\""+stopBits+"\"/>\n");
		bodyBf.append("<prop name=\"parity\" value=\""+parity+"\"/>\n");
		bodyBf.append("<prop name=\"driverName\" value=\""+driverName+"\"/>\n");
		bodyBf.append("</JposEntry>\n");
		
		return bodyBf.toString();
	}

	private String configScanner() {
                return "\n";
	}



}