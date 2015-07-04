/*
 *POS Version 4 Product
 *融通系统集成有限公司
 *@author HuangXuean
 *@version 1.0
 */
package com.royalstone.pos.gui.test;

import com.royalstone.pos.gui.Loadometer;

/**
 * @author Huangxuean
 * 
 * 
 */
public class LoadometerTest {

	public static void main(String[] args) {
		Loadometer loadometer=new Loadometer(System.out);
		loadometer.show();
		loadometer.setStep(0);
		loadometer.setAmount("124");
	}
}
