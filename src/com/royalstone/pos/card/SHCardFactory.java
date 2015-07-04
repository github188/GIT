package com.royalstone.pos.card;

/**
 * 工厂类负责生成实现了IShoppingCard接口的类

 * @author liangxinbiao
 */
public class SHCardFactory {

	public static IShoppingCard createInstance() {
		return new SHCard();
	}
}
