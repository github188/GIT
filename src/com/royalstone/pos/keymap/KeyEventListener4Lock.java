/*
 * Created on 2004-8-30
 */
package com.royalstone.pos.keymap;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;

import com.royalstone.pos.gui.test.KeyboardTest;

/**
 * @author Huangxuean
 *为自动锁机功能设定的监听器类
 *自动监听键盘和鼠标事件
 */
public class KeyEventListener4Lock{
	private long millseconds = System.currentTimeMillis();
	public KeyEventListener4Lock(){
		try {
			TheListener listener = new TheListener();
			Toolkit.getDefaultToolkit().addAWTEventListener(
				listener,
				AWTEvent.KEY_EVENT_MASK|AWTEvent.MOUSE_EVENT_MASK);
		} catch (Exception ex) {
			ex.printStackTrace();

		}	
	}	
	
	/**
	 * @return 当前时间，以毫秒记
	 * */	
	public long getMillSeconds(){
		return millseconds;
	}
	
	private class TheListener implements AWTEventListener {
		public void eventDispatched(AWTEvent event) {
			millseconds = System.currentTimeMillis();
		}
	}
		
	private class TheKeyEvent extends KeyEvent {
		public TheKeyEvent(
			Component source,
			int id,
			long when,
			int modifiers,
			int keyCode,
			char keyChar) {
			super(source, id, when, modifiers, keyCode, keyChar);
		}
	}
	
	public static void main(String[] args) {
		KeyEventListener4Lock kel =
			new KeyEventListener4Lock();
		KeyboardTest keyboardTest = new KeyboardTest();
		keyboardTest.show();
	}
}
