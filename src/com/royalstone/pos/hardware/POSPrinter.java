package com.royalstone.pos.hardware;

import jpos.JposException;
import jpos.POSPrinterConst;

/**
 * 小票打印机属于硬件层（在JavaPos标准的Control上的层)
 * @author liangxinbiao
 */
public class POSPrinter implements IPrinter {

	private jpos.POSPrinter control;
	private boolean isOpen=false;
	private boolean isEnable=true;
	
	private static POSPrinter instance;
	
	/**
	 * 私有构造方法，只允许从getInstance()方法里取得实例
	 * @param c
	 * @param devicename
	 */
	private POSPrinter(jpos.POSPrinter c, String devicename) {
		try {
			control = c;
			control.open(devicename);
			control.claim(1000);
			control.setDeviceEnabled(true);
			isOpen=true;
		} catch (JposException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 取得JavaPos标准的Control层的POS打印机对象
	 * @return
	 */
	public jpos.POSPrinter getControl() {
		return control;
	}

	/**
	 * @see com.royalstone.pos.hardware.IPrinter#println(java.lang.String)
	 */
	public void println(String value) {
		try {
			if(isOpen && isEnable)control.printNormal(POSPrinterConst.PTR_S_JOURNAL, value + "\n");
		} catch (JposException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @see com.royalstone.pos.hardware.IPrinter#feed(int)
	 */
	public void feed(int line) {
		try {
			if(isOpen && isEnable){
				for (int i = 0; i < line; i++) {
					control.printNormal(POSPrinterConst.PTR_S_JOURNAL, "\n");
				}
			}
		} catch (JposException ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * @see com.royalstone.pos.hardware.IPrinter#cut()
	 */	
	public void cut() {	    
		try {
			if(isOpen && isEnable){
				control.cutPaper(0);
			}
		}
		catch (JposException ex) {
			ex.printStackTrace();
		}	
	}
	
	public void setEnable(boolean value){
		this.isEnable=value;
	}
	
	public boolean isEnable(){
		return this.isEnable;
	}
	
	public boolean isOpen(){
		return this.isOpen;
	}
	
	/**
	 * 以单实例的方式取得小票打印机
	 * @return
	 */
	public static POSPrinter getInstance() {
		if (instance == null) {
			instance = new POSPrinter(new jpos.POSPrinter(), "POSPrinter");
		}
		return instance;
	}


}
