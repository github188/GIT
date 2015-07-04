/*
 * 创建日期 2004-6-5
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package com.royalstone.pos.hardware;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.royalstone.pos.common.Sale;
import com.royalstone.pos.util.Value;

import jpos.JposException;
import jpos.LineDisplay;

/**
 * @author liangxinbiao
 *
 * 更改所生成类型注释的模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
public class POSCustDisplay implements ICustDisplay {

	private LineDisplay control;
	private boolean isOpen = false;
	private static POSCustDisplay instance;
	private ICustDisplay custDisplay;

	private POSCustDisplay(LineDisplay c, String devicename) {
		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream("CustDisplay.properties"));
			String className = prop.getProperty("className");

			if (className != null && !className.equals("")) {
				Class clazz = Class.forName(className);
				custDisplay = (ICustDisplay) clazz.newInstance();

				control = c;
				control.open(devicename);
				control.claim(1000);
				control.setDeviceEnabled(true);

				custDisplay.init(control);
				isOpen = true;
			}

		} catch (JposException e) {
			e.printStackTrace();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		}
	}

	public LineDisplay getControl() {
		return control;
	}

	public static POSCustDisplay getInstance() {
		if (instance == null) {
			instance =
				new POSCustDisplay(new jpos.LineDisplay(), "POSCustDisplay");
		}
		return instance;
	}

	/* （非 Javadoc）
	 * @see com.royalstone.pos.hardware.ICustDisplay#init(jpos.LineDisplay)
	 */
	public void init(LineDisplay c) {
	}

	/* （非 Javadoc）
	 * @see com.royalstone.pos.hardware.ICustDisplay#printText(java.lang.String)
	 */
	public void printText(String value) {
		if (isOpen) {
			custDisplay.printText(value);
		}
	}

	/* （非 Javadoc）
	 * @see com.royalstone.pos.hardware.ICustDisplay#clear()
	 */
	public void clear() {
		if (isOpen) {
			custDisplay.clear();
		}
	}

	/* （非 Javadoc）
	 * @see com.royalstone.pos.hardware.ICustDisplay#welcome()
	 */
	public void welcome() {
		if (isOpen) {
			custDisplay.welcome();
		}

	}

	/* （非 Javadoc）
	 * @see com.royalstone.pos.hardware.ICustDisplay#printSubTotal(java.lang.String)
	 */
	public void printSubTotal(String value) {
		if (isOpen) {
			custDisplay.printSubTotal(value);
		}
	}

	/* （非 Javadoc）
	 * @see com.royalstone.pos.hardware.ICustDisplay#printTotal(java.lang.String)
	 */
	public void printTotal(String value) {
		if (isOpen) {
			custDisplay.printTotal(value);
		}
	}

	/* （非 Javadoc）
	 * @see com.royalstone.pos.hardware.ICustDisplay#printReturn(java.lang.String)
	 */
	public void printReturn(String value) {
		if (isOpen) {
			custDisplay.printReturn(value);
		}
	}

	/* （非 Javadoc）
	 * @see com.royalstone.pos.hardware.ICustDisplay#printGoods(java.lang.String)
	 */
	public void printGoods(Sale s) {
		if (isOpen) {
			custDisplay.printGoods(s);
		}
	}

	/* （非 Javadoc）
	 * @see com.royalstone.pos.hardware.ICustDisplay#printAmtPr(java.lang.String)
	 */
	public void printAmtPr(String value) {
		if (isOpen) {
			custDisplay.printAmtPr(value);
		}

	}

	/* （非 Javadoc）
	 * @see com.royalstone.pos.hardware.ICustDisplay#printPayment(java.lang.String)
	 */
	public void printPayment(String value) {
		if (isOpen) {
			custDisplay.printPayment(value);
		}
	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.hardware.ICustDisplay#printSubTotal(com.royalstone.pos.util.Value)
	 */
	public void printSubTotal(Value value) {
		printSubTotal( value.toString() );		
	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.hardware.ICustDisplay#printTotal(com.royalstone.pos.util.Value)
	 */
	public void printTotal(Value value) {
		printTotal( value.toString() );
		
	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.hardware.ICustDisplay#printReturn(com.royalstone.pos.util.Value)
	 */
	public void printReturn(Value value) {
		printReturn( value.toString() );
		
	}

	/* (non-Javadoc)
	 * @see com.royalstone.pos.hardware.ICustDisplay#printPayment(com.royalstone.pos.util.Value)
	 */
	public void printPayment(Value value) {
		printPayment( value.toString() );
		
	}

}
