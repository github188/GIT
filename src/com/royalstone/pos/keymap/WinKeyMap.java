package com.royalstone.pos.keymap;

import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * 封装键盘各键之间的映射关系,它从键盘映射文件生成
 * @author liangxinbiao
 */

public class WinKeyMap {

	private Element root;
	private HashMap map = new HashMap();

	/**
	 * 根据键盘映射文件生成它的内存中的表示
	 * @param filename 键盘映射文件生成
	 * @throws FileNotFoundException
	 * @throws JDOMException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public WinKeyMap(String filename)
		throws
			FileNotFoundException,
			JDOMException,
			SecurityException,
			NoSuchFieldException,
			IllegalArgumentException,
			IllegalAccessException {

		Document doc = (new SAXBuilder()).build(new FileInputStream(filename));
		root = doc.getRootElement();

		List keys = root.getChildren("key");
		Iterator iter = keys.iterator();
		while (iter.hasNext()) {
			Element key = (Element) iter.next();
			String srcKeyValue = key.getAttributeValue("name");
			String mapKeyValue = key.getTextTrim();
			String mapchar=key.getAttributeValue("mapchar");
			Field srcField = KeyEvent.class.getField(srcKeyValue);
			if (mapKeyValue != null && mapKeyValue.length() > 0) {
				String[] strmaps = mapKeyValue.split("\\Q|\\E");
				String[] mapchars=mapchar.split("\\Q|\\E");
				ArrayList mapList = new ArrayList();
				System.out.println("mapKeyValue="+mapKeyValue); 
				for (int i = 0; i < strmaps.length; i++) {
					System.out.println(strmaps[i]);
					Field mapField = KeyEvent.class.getField(strmaps[i]);
					mapList.add(new Integer(mapField.getInt(mapField)));
					mapList.add(mapchars[i]);
				}
				map.put(new Integer(srcField.getInt(srcField)), mapList);
			}

		}

	}

	/**
	 * 取得某一个键值的对应的键值列表
	 * @param keycode 
	 * @return
	 */
	public ArrayList getMap(int keycode) {
		ArrayList result = (ArrayList) map.get(new Integer(keycode));
		return result;
	}

	public static void main(String[] args) {
	}
}
