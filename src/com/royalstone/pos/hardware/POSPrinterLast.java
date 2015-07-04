/*
 * 创建日期 2005-8-17
 */
package com.royalstone.pos.hardware;

import java.util.ArrayList;

/**
 * @author liangxinbiao
 *
 */
public class POSPrinterLast implements IPrinter {

	private ArrayList commandList = new ArrayList();
	private ArrayList paramList = new ArrayList();

	private POSPrinter posPrinter;

	public POSPrinterLast() {

		posPrinter = POSPrinter.getInstance();

	}

	public void println(String value) {
		commandList.add("P");
		paramList.add(value);
	}

	public void feed(int line) {
		commandList.add("F");
		paramList.add(new Integer(line));
	}

	public void cut() {
		
		for (int i = 0; i < commandList.size(); i++) {

			String command = (String) commandList.get(i);

			if (command.equals("P")) {
				posPrinter.println((String) paramList.get(i));
			} else if (command.equals("F")) {
				posPrinter.feed(((Integer) paramList.get(i)).intValue());
			}

		}

		commandList.clear();
		paramList.clear();
		posPrinter.cut();

	}

}
