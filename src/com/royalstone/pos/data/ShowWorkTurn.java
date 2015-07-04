package com.royalstone.pos.data;

/**
 * @author liangxinbiao
 */
public class ShowWorkTurn {

	public static void main(String[] args) {
		PosTurnList posTurnList=PosTurnList.getInstance();
		ShopClock shopClock=ShopClock.getInstance();
		System.out.println(posTurnList);
		System.out.println(shopClock);
	}
}
