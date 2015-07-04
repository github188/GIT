package com.royalstone.pos.hardware;

import com.royalstone.pos.common.PosContext;

import jpos.CashDrawer;
import jpos.JposException;

/**
 * 钱箱属于硬件层（在JavaPos标准的Control上的层)
 * @author liangxinbiao
 */
public class POSCashDrawer {

	public static final int OPEN = 0;
	public static final int CLOSE = 1;
	public static final int UNKNOWN = 2;

	private static POSCashDrawer instance;

	private CashDrawer control;
	private boolean isOpen = false;

	/**
	 * 私有构造方法，只允许从getInstance()方法里取得实例
	 * @param c
	 * @param devicename
	 */
	private POSCashDrawer(CashDrawer c, String devicename) {
		try {
			control = c;
			control.open(devicename);
			control.claim(1000);
			control.setDeviceEnabled(true);
			isOpen = true;
		} catch (JposException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 取得JavaPos标准的Control层的钱箱对象
	 * @return
	 */
	public CashDrawer getControl() {
		return control;
	}

	/**
	 * 开钱箱 
	 */
	public void open() {
		try {
			if (isOpen && !PosContext.getInstance().isTraining())
				control.openDrawer();
		} catch (JposException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 判断钱箱开合状态
	 * @return
	 */
	public int isOpen() {
		if (isOpen) {
			try {
				boolean result=control.getDrawerOpened();
				if(result){
					return OPEN;
				}else{
					return CLOSE;
				}
			} catch (JposException ex) {
				ex.printStackTrace();
			}
		}
		return UNKNOWN;
	}

	/**
	 * 以单实例的方式取得钱箱对象
	 * @return
	 */
	public static POSCashDrawer getInstance() {
		if (instance == null) {
			instance =
				new POSCashDrawer(new jpos.CashDrawer(), "POSCashDrawer");
		}
		return instance;
	}

}
