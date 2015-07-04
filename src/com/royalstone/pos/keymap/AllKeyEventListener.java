package com.royalstone.pos.keymap;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.royalstone.pos.gui.test.KeyboardTest;

/**
 * 对不可编程键盘实行键盘映射(如 A键 映射成 B键)
 * 它实际上是个键盘钩子,用来捕捉底层的所有键盘事件,
 * 然后根据配置将原来的键盘输入换成相对应的键值
 * @author liangxinbiao
 */
public class AllKeyEventListener {

	/**
	 * @param file 配置文件名
	 */
	public AllKeyEventListener(String file) {

		try {
			TheListener listener = new TheListener(file);
			Toolkit.getDefaultToolkit().addAWTEventListener(
				listener,
				AWTEvent.KEY_EVENT_MASK);
		} catch (Exception ex) {
			ex.printStackTrace();

		}
	}

	/**
	 * 键盘监听器 
	 * @author liangxinbiao
	 */
	private class TheListener implements AWTEventListener {

		WinKeyMap keyMap;

		public TheListener(String file) throws Exception {
			keyMap = new WinKeyMap(file);
		}

		public void eventDispatched(AWTEvent event) {
			if ((event instanceof KeyEvent)
				&& !(event instanceof TheKeyEvent)) {
				KeyEvent keyEvent = (KeyEvent) event;
				ArrayList maps = keyMap.getMap(keyEvent.getKeyCode());
				if (maps != null) {
					for (int i = 0; i < maps.size(); i++, i++) {
						keyEvent.consume();
						EventQueue queue =
							Toolkit.getDefaultToolkit().getSystemEventQueue();

						queue.postEvent(
							new TheKeyEvent(
								keyEvent.getComponent(),
								keyEvent.getID(),
								keyEvent.getWhen(),
								keyEvent.getModifiers(),
								((Integer) maps.get(i)).intValue(),
								((String) maps.get(i + 1)).charAt(0)));
					}
				}

			}
		}
	}

	/**
	 * 代表映射后的键盘事件 
	 * @author liangxinbiao
	 */
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

	/**
	 * 主方法,测试用
	 * @param args
	 */
	public static void main(String[] args) {
		AllKeyEventListener allKeyEventListener =
			new AllKeyEventListener("winkeymap.xml");
		KeyboardTest keyboardTest = new KeyboardTest();
		keyboardTest.show();
	}
}
